package test;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import org.jetbrains.annotations.Contract;
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
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        resultMessage = new InstrumentationResultMessage();
        String[] fileFilter = new String[]{"import okhttp3"};
        String[] classFilter = new String[]{"OkHttpClient"};

        List<PsiFile> psiFiles = InstrumentationUtil.getAllJavaFilesInProjectAsPsi(project);

        InstrumentationUtil.scanPsiFileStatement(psiFiles, fileFilter, classFilter, this::processPsiStatement);

        Messages.showMessageDialog(resultMessage.getMessage(), "OkHttp Instrumentation Result ", Messages.getInformationIcon());
    }

    /**
     * Scan a statement to search for a instance of OkHttpClient to instrument.
     * This method is used as {@link java.util.function.Consumer} callback for the method
     * {@link util.InstrumentationUtil#scanPsiFileStatement}
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
     * @param psiStatement A potential Java statement to instrument
     */
    private void processPsiStatement(@NotNull PsiStatement psiStatement) {
        PsiCodeBlock psiBody = (PsiCodeBlock) psiStatement.getParent();
        PsiMethod psiMethod = (PsiMethod) psiBody.getParent();
        PsiClass psiClass = (PsiClass) psiMethod.getParent();

        psiStatement.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                String test = element.getText();
                if (element.getText().contains("PrefetchingLib.getOkHttp(")) {
                    resultMessage.incrementAlreadyInstrumentedCount();
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

                if (psiBody.getText().contains(instrumentedLine)) {
                    resultMessage.incrementAlreadyInstrumentedCount();
                    return;
                }

                PsiElement instrumentedElement = PsiElementFactory
                        .getInstance(project)
                        .createStatementFromText(instrumentedLine, psiClass);

                Runnable writeCommand = makeWriteCommand(statementType, instrumentedElement, element, psiBody);
                WriteCommandAction.runWriteCommandAction(project, writeCommand);

                resultMessage.incrementInstrumentationCount()
                        .appendPsiClass(psiClass)
                        .appendPsiMethod(psiMethod)
                        .appendNewBlock();
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
        if (statementType == STATEMENT_TYPE_RETURN) {
            //noinspection ConstantConditions
            expression = new String[]{
                    "return",
                    ((PsiReturnStatement) element).getReturnValue().getText()
            };
        } else {
            expression = element.getText().split("=");
        }

        if (expression.length != 2) return null;

        String instrumentedLine = expression[0] + " = PrefetchingLib.getOkHttp(" + expression[1].replace(";", "") + ")";
        instrumentedLine = instrumentedLine + (element.getText().contains(";") ? ";" : "");
        return instrumentedLine;
    }

    /**
     * Generate a {@link Runnable} instance to write in the original source code.
     * This should be passed as parameter to the method {@link WriteCommandAction#runWriteCommandAction} .
     *
     * @param statementType       An ID identifying the processed PsiElement class type
     * @param instrumentedElement A Psi element containing the instrumented code
     * @param originalElement     A Psi element containing the code to be instrumented
     * @param psiMethodBody       A method body used as reference to locate the original element in the Psi tree.
     * @return A {@link Runnable} instance for instrumentation
     */
    @Contract(pure = true)
    private @NotNull Runnable makeWriteCommand(int statementType, PsiElement instrumentedElement, PsiElement originalElement, PsiCodeBlock psiMethodBody) {
        switch (statementType) {
            case STATEMENT_TYPE_ASSIGNMENT:
            case STATEMENT_TYPE_DECLARATION:
                return () -> {
                    originalElement.replace(instrumentedElement);
                };
            default:
                return () -> {
                };
        }
    }
}
