package test;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.InstrumentationUtil;
import util.InstrumentationResultMessage;

import java.util.Arrays;
import java.util.List;

public class OkHttpInstrumentationAction extends AnAction {
    private static final int STATEMENT_TYPE_DECLARATION = 0;
    private static final int STATEMENT_TYPE_ASSIGNMENT = 1;

    private Project project;
    private InstrumentationResultMessage resultMessage;
    private String[] psiStatementFilter;

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
        psiStatementFilter = new String[]{
                "new OkHttpClient",
                ".build()"
        };

        List<PsiFile> psiFiles = InstrumentationUtil.getAllJavaFilesInProjectAsPsi(project);

        InstrumentationUtil.scanPsiFileStatement(psiFiles, fileFilter, this::processPsiStatement);

        Messages.showMessageDialog(resultMessage.getMessage(), "OkHttp Instrumentation Result ", Messages.getInformationIcon());
    }

    /**
     * Scan a statement to search for a instance of OkHttpClient to instrument.
     * This method is used as {@link java.util.function.Consumer} callback for the method
     * {@link util.InstrumentationUtil#scanPsiFileStatement}
     *
     * @param psiStatement A potential Java statement to instrument
     */
    private void processPsiStatement(@NotNull PsiStatement psiStatement) {
        if (Arrays.stream(psiStatementFilter).noneMatch(psiStatement.getText()::contains)) return;

        if (psiStatement.getText().contains("new OkHttpClient")) {
            resultMessage.incrementPossibleInstrumentationCount();
        }

        psiStatement.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (Arrays.stream(psiStatementFilter).noneMatch(psiStatement.getText()::contains)) return;

                int statementType = -1;

                if (element instanceof PsiAssignmentExpression) {
                    statementType = STATEMENT_TYPE_ASSIGNMENT;
                } else if (element instanceof PsiVariable) {
                    statementType = STATEMENT_TYPE_DECLARATION;
                } else super.visitElement(element);

                if (statementType == -1) return;
                if (!hasTypeOkHttp(statementType, element)) return;

                PsiCodeBlock psiBody = (PsiCodeBlock) psiStatement.getParent();
                PsiMethod psiMethod = (PsiMethod) psiBody.getParent();
                PsiClass psiClass = (PsiClass) psiMethod.getParent();

                String instrumentedLine = makeInstrumentationLine(statementType, element, element.getText().contains(".build()"));

                if (psiBody.getText().contains(instrumentedLine)) {
                    resultMessage.incrementAlreadyInstrumentedCount();
                    return;
                }

                PsiElement instrumentedElement = PsiElementFactory
                        .getInstance(project)
                        .createStatementFromText(instrumentedLine, psiClass);

                Runnable writeCommand = makeWriteCommand(statementType, psiBody, instrumentedElement, element);
                WriteCommandAction.runWriteCommandAction(project, writeCommand);

                resultMessage.incrementInstrumentationCount()
                        .appendPsiClass(psiClass)
                        .appendPsiMethod(psiMethod)
                        .appendNewBlock();

//                if (element instanceof PsiAssignmentExpression) {
//                    processOkHttpStatement(psiStatement, (PsiAssignmentExpression) element);
//                } else if (element instanceof PsiVariable) {
//                    processOkHttpStatement(psiStatement, (PsiVariable) element);
//                } else super.visitElement(element);
            }
        });
    }

    private boolean hasTypeOkHttp(int statementType, PsiElement element) {
        switch (statementType) {
            case STATEMENT_TYPE_ASSIGNMENT:
                PsiAssignmentExpression assignmentExpression = (PsiAssignmentExpression) element;
                return assignmentExpression.getType() != null && assignmentExpression.getType().getCanonicalText().equals("okhttp3.OkHttpClient");
            case STATEMENT_TYPE_DECLARATION:
                return ((PsiVariable) element).getType().getCanonicalText().equals("okhttp3.OkHttpClient");
            default:
                return false;
        }
    }

    private @NotNull String makeInstrumentationLine(int statementType, PsiElement element, boolean isBuilder) {
        String parameter;
        switch (statementType) {
            case STATEMENT_TYPE_ASSIGNMENT:
                PsiAssignmentExpression assignmentExpression = (PsiAssignmentExpression) element;
                String okHttpVariableName = assignmentExpression.getLExpression().getText();
                parameter = isBuilder ? assignmentExpression.getRExpression().getText() : okHttpVariableName;
                return okHttpVariableName + " = PrefetchingLib.getOkHttp(" + parameter + ");";
            case STATEMENT_TYPE_DECLARATION:
                PsiVariable variableExpression = (PsiVariable) element;
                String leftExpression = variableExpression.getText().substring(0, variableExpression.getText().indexOf("=") + 1);
                parameter = isBuilder ? "BUILDER" : "";
                return leftExpression + " PrefetchingLib.getOkHttp(" + parameter + ");";
            default:
                return "";
        }
    }

    @Contract(pure = true)
    private @NotNull Runnable makeWriteCommand(int statementType, PsiCodeBlock psiMethodBody, PsiElement instrumentedElement, PsiElement originalStatement) {
        switch (statementType) {
            case STATEMENT_TYPE_ASSIGNMENT:
                return () -> {
                    psiMethodBody.addAfter(instrumentedElement, originalStatement);
                };
            case STATEMENT_TYPE_DECLARATION:
                return () -> {
                    psiMethodBody.replace(instrumentedElement);
                };
            default:
                return () -> {
                };
        }
    }

    /**
     * Process the OkHttp instrumentation for cases where an existent OkHttp variable already exists and is assigned a
     * new instance of an OkHttp object. (e.g., {@code client = new OkHttpClient();})
     *
     * @param psiStatement         A statement containing a OkHttp assigment
     * @param assignmentExpression A PsiElement containing a variable assignment
     */
    private void processOkHttpStatement(PsiStatement psiStatement, @NotNull PsiAssignmentExpression assignmentExpression) {
        if (assignmentExpression.getType() != null && assignmentExpression.getType().getCanonicalText().equals("okhttp3.OkHttpClient")) {
            PsiCodeBlock psiBody = (PsiCodeBlock) psiStatement.getParent();
            PsiMethod psiMethod = (PsiMethod) psiBody.getParent();
            PsiClass psiClass = (PsiClass) psiMethod.getParent();
            String varName = assignmentExpression.getLExpression().getText();
            String instrumentedLine = varName + " = PrefetchingLib.getOkHttp(";

            PsiElement elementInstrumented = PsiElementFactory
                    .getInstance(project)
                    .createStatementFromText(
                            varName + " = PrefetchingLib.getOkHttp(" + varName + ");",
                            psiClass);

            if (psiBody.getText().contains(elementInstrumented.getText())) {
                resultMessage.incrementAlreadyInstrumentedCount();
                return;
            }

            WriteCommandAction.runWriteCommandAction(project, () -> {
                psiBody.addAfter(elementInstrumented, psiStatement);
            });

            resultMessage.incrementInstrumentationCount()
                    .appendPsiClass(psiClass)
                    .appendPsiMethod(psiMethod)
                    .appendNewBlock();
        }
    }

    /**
     * Process the OkHttp instrumentation for cases where a OkHttp variable is declared and assigned a new instance of
     * an OkHttp object. (e.g., {@code OkHttpClient client = new OkHttpClient();})
     *
     * @param psiStatement       A statement containing a OkHttp assigment
     * @param variableExpression A PsiElement containing a variable declaration
     */
    private void processOkHttpStatement(PsiStatement psiStatement, @NotNull PsiVariable variableExpression) {
        if (variableExpression.getType().getCanonicalText().equals("okhttp3.OkHttpClient")) {
            PsiCodeBlock psiBody = (PsiCodeBlock) psiStatement.getParent();
            PsiMethod psiMethod = (PsiMethod) psiBody.getParent();
            PsiClass psiClass = (PsiClass) psiMethod.getParent();
            String instrumentedLine = variableExpression.getText().substring(0, variableExpression.getText().indexOf("=") + 1)
                    + " PrefetchingLib.getOkHttp();";

            PsiElement elementInstrumented = PsiElementFactory
                    .getInstance(project)
                    .createStatementFromText(instrumentedLine, psiClass);

            if (psiBody.getText().contains(elementInstrumented.getText())) {
                resultMessage.incrementAlreadyInstrumentedCount();
                return;
            }

            WriteCommandAction.runWriteCommandAction(project, () -> {
                variableExpression.replace(elementInstrumented);
            });

            resultMessage.incrementInstrumentationCount()
                    .appendPsiClass(psiClass)
                    .appendPsiMethod(psiMethod)
                    .appendNewBlock();
        }
    }
}
