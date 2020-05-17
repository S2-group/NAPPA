package test;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
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

        List<PsiFile> psiFiles = InstrumentationUtil.getAllJavaFilesInProjectAsPsi(project);

        for (PsiFile psiFile : psiFiles) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            PsiClass[] psiClasses = javaFile.getClasses();

            for (PsiClass psiClass : psiClasses) {
                PsiMethod[] psiMethods = psiClass.getMethods();
                for (PsiMethod psiMethod : psiMethods) {
                    try {
                        PsiStatement[] psiStatements = psiMethod.getBody().getStatements();
                        for (PsiStatement statement : psiStatements) {
                            // Check for the construction of OkHttp objects
                            if (statement.getText().contains("new OkHttpClient()") ||
                                    statement.getText().contains("new OkHttpClient.Builder")) {

                                cat += statement.getClass().getCanonicalName() + "\n";
                                cat += psiClass.getName() + "::" + psiMethod.getName() + " -> " + statement.getText() + "\n";

                                // Check if this reference to the OkHttpClient is an assignment or an
                                // Usage
                                if (statement instanceof PsiExpressionStatement) {
                                    cat += "PsiExpressionStatement  " + statement.getText() + "\n";
                                    // Print the canonical type of the okhttp statement
                                    cat += ((PsiExpressionStatement) statement).getExpression().getType().getCanonicalText() + "\n";

                                    // For all children of this statement
                                    for (PsiElement element : statement.getChildren()) {
                                        cat += element.getText() + "\n";
                                        // Verify if this Expression is an assignment
                                        if (element instanceof PsiAssignmentExpression) {
                                            cat += "\nAssignment!\n\n";
                                            PsiAssignmentExpression assignmentExpression = ((PsiAssignmentExpression) element);
                                            // Check if the left expression has a type of OkHttpClient
                                            if (((PsiAssignmentExpression) element).getLExpression().getType().getCanonicalText().compareTo("okhttp3.OkHttpClient") == 0) {

                                                /**
                                                 * IMPORTANT:  This statement instruments the application to fetch the okHttpClient within the
                                                 * Prefetching lib
                                                 */
                                                String varName = assignmentExpression.getLExpression().getText();
                                                final PsiElement elementX = PsiElementFactory.getInstance(project).createStatementFromText(
                                                        varName + " = PrefetchingLib.getOkHttp(" + varName + ");", psiClass);

                                                // FIXME: Multiple Inserts to this method call whenever the user makes use of this action
                                                if (!assignmentExpression.getParent().getNextSibling().textMatches(elementX)) {
                                                    WriteCommandAction.runWriteCommandAction(project, () -> {
                                                        psiMethod.getBody().addAfter(
                                                                elementX, statement
                                                        );
                                                    });
                                                }
                                            }
                                            cat += "Type: " + ((PsiAssignmentExpression) element).getLExpression().getType().getCanonicalText() + "\n";
                                            cat += "Variable name:  " + ((PsiAssignmentExpression) element).getLExpression().getText() + "\n";
                                        } else {
                                            cat += "Not an assignment";
                                        }
                                    }
                                }


                            }
                        }
                    } catch (NullPointerException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        Messages.showMessageDialog(cat, "Hello", Messages.getInformationIcon());
    }
}
