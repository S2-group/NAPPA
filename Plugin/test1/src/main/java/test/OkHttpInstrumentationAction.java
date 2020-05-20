package test;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.InstrumentationResultMessage;
import util.InstrumentationUtil;

import java.util.List;

public class OkHttpInstrumentationAction extends AnAction {
    private static final int STATEMENT_TYPE_DECLARATION = 0;
    private static final int STATEMENT_TYPE_ASSIGNMENT = 1;
    private static final int STATEMENT_TYPE_RETURN = 2;

    private Project project;
    private InstrumentationResultMessage resultMessage;

    /**
     * Checks the existence of okHttp variables in this project AND Instruments to get OkHttp
     *
     * @param e {@inheritDoc}
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project = e.getProject();
        resultMessage = new InstrumentationResultMessage();
        String[] fileFilter = new String[]{"import okhttp3"};
        String[] classFilter = new String[]{"OkHttpClient"};

        List<PsiFile> psiFiles = InstrumentationUtil.getAllJavaFilesInProjectAsPsi(project);

        InstrumentationUtil.runScanOnJavaFile(psiFiles, fileFilter, classFilter, this::processPsiStatement);

        Messages.showMessageDialog(resultMessage.getMessage(), "OkHttp Instrumentation Result ", Messages.getInformationIcon());
    }

    /**
     * Scan a statement to search for a instance of OkHttpClient to instrument.
     * This method is used as {@link java.util.function.Consumer} callback for the method
     * {@link util.InstrumentationUtil#runScanOnJavaFile}
     * <br/><br/>
     *
     * <p>The following occurrences should be instrumented </p>
     *
     * <pre>
     * {@code
     * OkHttpClient client = new OkHttpClient()
     * OkHttpClient client = okHttpClientBuilder.build()
     * client = new OkHttpClient()
     * client = okHttpClientBuilder.build()
     * }
     * </pre>
     *
     * @param rootPsiElement A potential Java statement to instrument
     */
    private void processPsiStatement(@NotNull PsiElement rootPsiElement) {
        rootPsiElement.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element.getText().contains("PrefetchingLib.getOkHttp(")) {
                    resultMessage.incrementPossibleInstrumentationCount().incrementAlreadyInstrumentedCount();
                    return;
                }

                int statementType = -1;

                if (element instanceof PsiAssignmentExpression) statementType = STATEMENT_TYPE_ASSIGNMENT;
                else if (element instanceof PsiVariable) statementType = STATEMENT_TYPE_DECLARATION;
                else if (element instanceof PsiReturnStatement) statementType = STATEMENT_TYPE_RETURN;
                else super.visitElement(element);

                if (statementType == -1) return;
                if (!hasTypeOkHttp(statementType, element)) return;

                resultMessage.incrementPossibleInstrumentationCount();

                String instrumentedLine = makeInstrumentationLine(statementType, element);

                if (instrumentedLine == null) {
                    resultMessage.incrementUnneededInstrumentationCount();
                    return;
                }

                PsiCodeBlock psiBody = (PsiCodeBlock) InstrumentationUtil.getAncestorPsiElementFromElement(rootPsiElement, PsiCodeBlock.class);

                if (psiBody != null && psiBody.getText().contains(instrumentedLine)) {
                    resultMessage.incrementAlreadyInstrumentedCount();
                    return;
                }

                PsiClass psiClass = (PsiClass) InstrumentationUtil.getAncestorPsiElementFromElement(rootPsiElement, PsiClass.class);

                PsiMethod psiMethod = (PsiMethod) InstrumentationUtil.getAncestorPsiElementFromElement(rootPsiElement, PsiMethod.class);

                PsiElement instrumentedElement = PsiElementFactory
                        .getInstance(project)
                        .createStatementFromText(instrumentedLine, psiClass);

                WriteCommandAction.runWriteCommandAction(project, () -> {
                    element.replace(instrumentedElement);
                });

                //noinspection ConstantConditions -- Since we loop through classes, it is certain that there is a parent Java class
                InstrumentationUtil.addLibraryImport(project, psiClass);

                resultMessage.incrementInstrumentationCount().appendPsiClass(psiClass);

                if (psiMethod != null) resultMessage.appendPsiMethod(psiMethod);
                else if (element instanceof PsiField) resultMessage.appendPsiField((PsiField) element);
                else resultMessage.appendPsiClassInitializer();

                resultMessage.appendNewBlock();
            }
        });
    }

    /**
     * Verifies if the {@code element} contains an assignment or declaration of a {@code OkHttpClient}
     *
     * @param statementType An ID identifying the processed PsiElement class type
     * @param element       A Psi element potentially contain the code to be instrumented
     * @return {@code True} if the {@code element} has th type {@code OkHttpClient}, {@code False} otherwise
     */
    private boolean hasTypeOkHttp(int statementType, PsiElement element) {
        String okHttpClientType = "okhttp3.OkHttpClient";
        try {
            switch (statementType) {
                case STATEMENT_TYPE_ASSIGNMENT:
                    PsiAssignmentExpression assignmentExpression = (PsiAssignmentExpression) element;
                    return assignmentExpression.getType() != null &&
                            assignmentExpression.getType().getCanonicalText().equals(okHttpClientType);

                case STATEMENT_TYPE_DECLARATION:
                    return ((PsiVariable) element).getType().getCanonicalText().equals(okHttpClientType);

                case STATEMENT_TYPE_RETURN:
                    PsiReturnStatement returnStatement = (PsiReturnStatement) element;
                    return returnStatement.getReturnValue() != null &&
                            returnStatement.getReturnValue().getType() != null &&
                            returnStatement.getReturnValue().getType().getCanonicalText().equals(okHttpClientType);

                default:
                    return false;
            }
        } catch (PsiInvalidElementAccessException e) {
            return false;
        }
    }

    /**
     * Verifies if the element can be instrumented.
     * If so, generates a instrumented source-code line using the prefetch library
     *
     * @param statementType An ID identifying the processed PsiElement class type
     * @param element       A Psi element containing the code to be instrumented
     * @return The new source-code line if instrumentation is possible, {@code null} otherwise
     */
    private @Nullable String makeInstrumentationLine(int statementType, @NotNull PsiElement element) {
        boolean isBuilder = element.getText().contains(".build()");
        boolean isDefaultOkHttpConstructor = element.getText().contains("new OkHttpClient()");

        if (!isBuilder && !isDefaultOkHttpConstructor) return null;

        String[] expression;
        String delimiter;
        if (statementType == STATEMENT_TYPE_RETURN) {
            delimiter = " ";
            //noinspection ConstantConditions
            expression = new String[]{
                    "return",
                    ((PsiReturnStatement) element).getReturnValue().getText()
            };
        } else {
            delimiter = " = ";
            expression = element.getText().split("=");
        }

        if (expression.length != 2) return null;

        expression[1] = " PrefetchingLib.getOkHttp(" + expression[1] + ")";
        expression[1] = expression[1].replace(";)", ")");
        expression[1] = expression[1] + (element.getText().contains(";") ? ";" : "");

        return String.join(delimiter, expression);
    }
}
