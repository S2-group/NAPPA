package test;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import util.InstrumentationUtil;

import java.util.List;

public class OkHttpInstrumentationAction extends AnAction {
    Project project;
    PsiMethod signature;
    String cat = "Buongiorno\n";

    /**
     * Checks the existence of okHttp variables in this project AND Instruments to get OkHttp
     *
     * @param e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        cat = "";
        String[] fileFilter = new String[]{
                "import okhttp3"
        };
        List<PsiFile> psiFiles = InstrumentationUtil.getAllJavaFilesInProjectAsPsi(project);

        InstrumentationUtil.scanPsiFileStatement(psiFiles, fileFilter, this::processPsiStatement);

        Messages.showMessageDialog(cat, "Hello", Messages.getInformationIcon());
    }

    private void processPsiStatement(@NotNull PsiStatement statement) {
        if (!statement.getText().contains("new OkHttpClient")) {
            return;
        }

        if (statement instanceof PsiExpressionStatement) {
            for (PsiElement element : statement.getChildren()) {
                if (element instanceof PsiAssignmentExpression) {
                    PsiAssignmentExpression assignmentExpression = ((PsiAssignmentExpression) element);
                    if (assignmentExpression.getLExpression().getType() == null) {
                        return;
                    }
                    if (assignmentExpression.getLExpression().getType().getCanonicalText().compareTo("okhttp3.OkHttpClient") == 0) {
                        PsiCodeBlock psiBody = (PsiCodeBlock) statement.getParent();
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
                            psiBody.addAfter(elementInstrumented, statement);
                        });
                    }
                }
            }
        }
    }
}
