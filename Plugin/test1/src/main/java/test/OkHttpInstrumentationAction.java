package test;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import util.InstrumentationUtil;
import util.StringBuilderUtil;

import java.util.List;

public class OkHttpInstrumentationAction extends AnAction {
    private Project project;
    private StringBuilder displayMessage;
    private int instrumentCount;

    /**
     * Checks the existence of okHttp variables in this project AND Instruments to get OkHttp
     *
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        displayMessage = new StringBuilder();
        instrumentCount = 0;
        String[] fileFilter = new String[]{
                "import okhttp3"
        };

        List<PsiFile> psiFiles = InstrumentationUtil.getAllJavaFilesInProjectAsPsi(project);

        InstrumentationUtil.scanPsiFileStatement(psiFiles, fileFilter, this::processPsiStatement);

        displayMessage = StringBuilderUtil.addInstrumentCount(displayMessage, instrumentCount);

        Messages.showMessageDialog(displayMessage.toString(), "OkHttp Instrumentation Result ", Messages.getInformationIcon());
    }

    private void processPsiStatement(@NotNull PsiStatement psiStatement) {
        if (!psiStatement.getText().contains("new OkHttpClient")) {
            return;
        }

        if (psiStatement instanceof PsiExpressionStatement) {
            for (PsiElement element : psiStatement.getChildren()) {
                if (element instanceof PsiAssignmentExpression) {
                    PsiAssignmentExpression assignmentExpression = ((PsiAssignmentExpression) element);
                    if (assignmentExpression.getLExpression().getType() == null) {
                        return;
                    }
                    if (assignmentExpression.getLExpression().getType().getCanonicalText().compareTo("okhttp3.OkHttpClient") == 0) {
                        PsiCodeBlock psiBody = (PsiCodeBlock) psiStatement.getParent();
                        PsiMethod psiMethod = (PsiMethod) psiBody.getParent();
                        PsiClass psiClass = (PsiClass) psiMethod.getParent();
                        String varName = assignmentExpression.getLExpression().getText();

                        final PsiElement elementInstrumented = PsiElementFactory
                                .getInstance(project)
                                .createStatementFromText(
                                        varName + " = PrefetchingLib.getOkHttp(" + varName + ");",
                                        psiClass);

                        if (psiBody.getText().contains(elementInstrumented.getText())) {
                            return;
                        }

                        WriteCommandAction.runWriteCommandAction(project, () -> {
                            psiBody.addAfter(elementInstrumented, psiStatement);
                        });
                        instrumentCount++;
                        displayMessage = StringBuilderUtil.appendPsiClass(displayMessage, psiClass);
                        displayMessage = StringBuilderUtil.appendPsiStatement(displayMessage, psiStatement);
                    }
                }
            }
        }
    }
}
