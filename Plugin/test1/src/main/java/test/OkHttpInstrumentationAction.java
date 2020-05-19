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
                if (element.getText().contains("PrefetchingLib.getOkHttp(")) {
                    resultMessage.incrementAlreadyInstrumentedCount();
                    return;
                }

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
            }
        });
    }

    private boolean hasTypeOkHttp(int statementType, PsiElement element) {
        try {
            switch (statementType) {
                case STATEMENT_TYPE_ASSIGNMENT:
                    PsiAssignmentExpression assignmentExpression = (PsiAssignmentExpression) element;
                    return assignmentExpression.getType() != null && assignmentExpression.getType().getCanonicalText().equals("okhttp3.OkHttpClient");
                case STATEMENT_TYPE_DECLARATION:
                    return ((PsiVariable) element).getType().getCanonicalText().equals("okhttp3.OkHttpClient");
                default:
                    return false;
            }
        } catch (PsiInvalidElementAccessException e) {
            return false;
        }
    }

    private @NotNull String makeInstrumentationLine(int statementType, PsiElement element, boolean isBuilder) {
        String parameter;
        switch (statementType) {
            case STATEMENT_TYPE_ASSIGNMENT:
                PsiAssignmentExpression assignmentExpression = (PsiAssignmentExpression) element;
                String variableName = assignmentExpression.getLExpression().getText();
                parameter = isBuilder ? assignmentExpression.getRExpression().getText() : "new OkHttpClient()";
                return variableName + " = PrefetchingLib.getOkHttp(" + parameter + ");";
            case STATEMENT_TYPE_DECLARATION:
                PsiVariable variableExpression = (PsiVariable) element;
                String leftExpression = variableExpression.getText().substring(0, variableExpression.getText().indexOf("=") + 1);
                String text = element.getText();
                parameter = isBuilder ? text.substring(text.indexOf("=") + 1, text.indexOf(";")) : "new OkHttpClient()";
                return leftExpression + " PrefetchingLib.getOkHttp(" + parameter + ");";
            default:
                return "";
        }
    }

    @Contract(pure = true)
    private @NotNull Runnable makeWriteCommand(int statementType, PsiCodeBlock psiMethodBody, PsiElement instrumentedElement, PsiElement originalElement) {
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
