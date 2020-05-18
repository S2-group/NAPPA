package test;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import util.InstrumentationUtil;
import util.InstrumentationResultMessage;

import java.util.List;

public class OkHttpInstrumentationAction extends AnAction {
    private Project project;
    private InstrumentationResultMessage resultMessage;

    /**
     * Checks the existence of okHttp variables in this project AND Instruments to get OkHttp
     *
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        resultMessage = new InstrumentationResultMessage();
        String[] fileFilter = new String[]{
                "import okhttp3"
        };

        List<PsiFile> psiFiles = InstrumentationUtil.getAllJavaFilesInProjectAsPsi(project);

        InstrumentationUtil.scanPsiFileStatement(psiFiles, fileFilter, this::processPsiStatement);

        Messages.showMessageDialog(resultMessage.getMessage(), "OkHttp Instrumentation Result ", Messages.getInformationIcon());
    }

    private void processPsiStatement(@NotNull PsiStatement psiStatement) {
        if (!psiStatement.getText().contains("new OkHttpClient")) return;

        resultMessage.incrementPossibleInstrumentationCount();

        psiStatement.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (!element.getText().contains("new OkHttpClient")) return;
                String txt = element.getText();
                if (element instanceof PsiAssignmentExpression) {
                    processOkHttpStatement(psiStatement, (PsiAssignmentExpression) element);
                } else if (element instanceof PsiVariable) {
                    processOkHttpStatement(psiStatement, (PsiVariable) element);
                } else super.visitElement(element);
            }
        });
    }

    /**
     * Process the OkHttp instrumentation for cases where an existent OkHttp variable already exists and is assigned a
     * new instance of an OkHttp object. (e.g., {@code client = new OkHttpClient();})
     *
     * @param psiStatement         A statement containing a OkHttp assigment
     * @param assignmentExpression The assignment expression
     */
    private void processOkHttpStatement(PsiStatement psiStatement, @NotNull PsiAssignmentExpression assignmentExpression) {
        if (assignmentExpression.getLExpression().getType() == null) {
            return;
        }

        if (assignmentExpression.getLExpression().getType().getCanonicalText().compareTo("okhttp3.OkHttpClient") == 0) {
            PsiCodeBlock psiBody = (PsiCodeBlock) psiStatement.getParent();
            PsiClass psiClass = (PsiClass) psiBody.getParent().getParent();
            String varName = assignmentExpression.getLExpression().getText();

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
                    .appendPsiStatement(psiStatement)
                    .appendNewBlock();
        }
    }

    private void processOkHttpStatement(PsiStatement psiStatement, @NotNull PsiVariable variableExpression) {
        if (variableExpression.getType().getCanonicalText().compareTo("okhttp3.OkHttpClient") == 0) {
            PsiCodeBlock psiBody = (PsiCodeBlock) psiStatement.getParent();
            PsiClass psiClass = (PsiClass) psiBody.getParent().getParent();
            String varName = variableExpression.getName();

            PsiElement elementInstrumented = PsiElementFactory
                    .getInstance(project)
                    .createStatementFromText(
                            "OkHttpClient " + varName + " = PrefetchingLib.getOkHttp();",
                            psiClass);

            if (psiBody.getText().contains(elementInstrumented.getText())) {
                resultMessage.incrementAlreadyInstrumentedCount();
                return;
            }

            WriteCommandAction.runWriteCommandAction(project, () -> {
                variableExpression.replace(elementInstrumented);
            });

            resultMessage.incrementInstrumentationCount()
                    .appendPsiClass(psiClass)
                    .appendPsiStatement(psiStatement)
                    .appendNewBlock();
        }
    }
}
