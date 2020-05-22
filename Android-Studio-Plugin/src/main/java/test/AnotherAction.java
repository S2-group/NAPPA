package test;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Instruments for notification of extras updates by identifying calls to .putExtra(key, val)
 */
public class AnotherAction extends AnAction {

    Project project;
    PsiMethod signature;
    String cat = "Buongiorno\n";

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();

        //TODO add import package for prefetching lib

        //signature = PsiElementFactory.SERVICE.getInstance(project).createMethodFromText("", null);



        String[] fileNames = FilenameIndex.getAllFilenames(project);
        List<PsiFile> psiFiles = new LinkedList<>();

        for (String fileName : fileNames) {
            psiFiles.addAll(Arrays.asList(FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project))));
        }

        // Iterate through all files (as arrays) following the next hierarchy
        //  File []--> JavaFile[]-->Classes[]-->Methods[]-->Statements[]
        for (PsiFile psiFile : psiFiles) {
            if (psiFile instanceof PsiJavaFile) {
                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                PsiClass[] psiClasses = javaFile.getClasses();

                for (PsiClass psiClass : psiClasses) {
                    PsiMethod[] psiMethods = psiClass.getMethods();
                    for (PsiMethod psiMethod : psiMethods) {
                        try {
                            PsiStatement[] psiStatements = psiMethod.getBody().getStatements();
                            for (PsiStatement statement : psiStatements) {
                                // Find statements regarding Intent Extras, and collect the arguments from the putExtras method calls.
                                if (statement.getText().contains(".putExtra(")) {
                                    cat += statement.getClass().getCanonicalName()+"\n";
                                    cat += psiClass.getName() + "::" + psiMethod.getName() + " -> " + statement.getText() + "\n";

                                    if (statement instanceof PsiMethodCallExpression) {  // Statement is Method Call
                                        PsiMethodCallExpression callExpression = (PsiMethodCallExpression) statement;
                                        PsiExpression[] psiExpressions = callExpression.getArgumentList().getExpressions();
                                        for (PsiExpression psiExpression : psiExpressions) {
                                            cat += "Param: " + psiExpression.getText() + " -> " + psiExpression.getType().getCanonicalText() + "\n";
                                        }
                                    } else {                                             // Statement is Expression
                                        cat += statement.getClass().getCanonicalName()+"\n";
                                        if (statement instanceof PsiExpressionStatement) {
                                            PsiExpressionStatement expressionStatement = (PsiExpressionStatement) statement;

                                            // First get the expression from the statement
                                            PsiExpression expression = expressionStatement.getExpression();

                                            List<PsiElement> elementsToCkeck = new LinkedList<>();
                                            List<PsiMethodCallExpression> expressionList = new LinkedList<>(); // Contains all methodCallExpressions
                                            List<PsiMethodCallExpression> useful = new LinkedList<>();

                                            int len = 0;
                                            // FIXME: What is meant by this constant 20?
                                            // Break down any methodCallExpressions into its argumentss.  These arguments can either be
                                            //  more methodCallExpressions or NOT. Produce a list of expressions that can be checked for the
                                            //  existence of the putExtra method call
                                            while (len < 20 && expression != null && expression instanceof PsiMethodCallExpression) {
                                                PsiMethodCallExpression callExpression = (PsiMethodCallExpression) expression;
                                                // If the expression is a method call, extract all the expressions within the arguments
                                                PsiExpression[] psiExpressions = callExpression.getArgumentList().getExpressions();

                                                for (PsiExpression expression1 : psiExpressions) {
                                                    if (expression1 instanceof PsiMethodCallExpression) {
                                                        // Expand the expression list with any arguments which are method calls
                                                        expressionList.add((PsiMethodCallExpression)expression1);
                                                    } else {
                                                        // Make a list of element to check later
                                                        elementsToCkeck.add(expression1);
                                                    }
                                                }
                                                try {
                                                    // Remove any already analyzed method calls
                                                    expression = ((LinkedList<PsiMethodCallExpression>) expressionList).pop();
                                                } catch (Exception e3) {
                                                    expression = null;
                                                } finally {
                                                    len++;
                                                }
                                            }

                                            // Check which children nodes are useful
                                            for (PsiElement element : elementsToCkeck) {
                                                cat += "TO CHECK : " + element.getText();
                                                if (element.getText().contains(".putExtra(")) {
                                                    for (PsiElement child : element.getChildren()) {
                                                        getAllChildern(child, useful);
                                                    }
                                                }
                                            }

                                            /**
                                             * Importatn:  For all useful children nodes, insert an extras
                                             * notifier, which will update the Enriched Navigation Graph in the
                                             * Prefetching Lib
                                             */
                                            for (PsiMethodCallExpression callExpression : useful) {
                                                final String var1 = callExpression.getArgumentList().getExpressions()[0].getText();
                                                final String var2 = callExpression.getArgumentList().getExpressions()[1].getText();
                                                final PsiElement element = PsiElementFactory.SERVICE.getInstance(project).createStatementFromText(
                                                        "PrefetchingLib.notifyExtra("+var1+", "+var2+");", psiClass);
                                                if (!callExpression.getParent().getNextSibling().textMatches(element)) {
                                                    cat += "\nNEXT SIBLING -> "+callExpression.getParent().getNextSibling().getText()+"\n";
                                                    WriteCommandAction.runWriteCommandAction(project, () -> {

                                                        psiMethod.getBody().addAfter(
                                                                element, callExpression.getParent()
                                                        );

                                                    });
                                                }

                                            }


                                            /*
                                            if (expression instanceof PsiMethodCallExpression) {
                                                PsiMethodCallExpression callExpression = (PsiMethodCallExpression) expression;
                                                PsiExpression[] psiExpressions = callExpression.getArgumentList().getExpressions();
                                                for (PsiExpression expression1 : psiExpressions) {
                                                    //cat += "EXPRESSION " + expression1.getText() + " -> " + expression1.getType().getCanonicalText() + "\n";

                                                }
                                            }
                                            */

                                            /*
                                            PsiElement[] elements = expressionStatement.getChildren();

                                            for (PsiElement element : elements) {
                                                //cat += element.getText()+" -> "+element.getClass().getCanonicalName()+"\n";
                                            }
                                            */
                                        }
                                    }
                                }
                            }
                        } catch (NullPointerException e1) {
                            //cat += psiClass.getName() + "::" + psiMethod.getName() + " -> " + e1.getMessage() + "\n";
                            e1.printStackTrace();
                        }
                    }
                }

                //javaFile.getClasses()[0].getMethods()[0].getBody().getStatements()[0].getText().contains(".putExtra");
            }
        }

        Messages.showMessageDialog(cat, "Hello", Messages.getInformationIcon());


    }

    /**
     * Generates a list of Useful nodes
     * @param element A psiElmenet to extract all the children nodes from
     * @param list A list of useful PsiMethodCallExpressions to be populated
     */
    private void getAllChildern(PsiElement element, List<PsiMethodCallExpression> list) {
        PsiElement[] children = element.getChildren();
        for (PsiElement child : children) {
            if (child instanceof PsiMethodCallExpression) {
                PsiMethodCallExpression callExpression = (PsiMethodCallExpression) child;

                // Get all arguments for the method call
                PsiExpression[] expressions = ((PsiMethodCallExpression) child).getArgumentList().getExpressions();
                // Put extra should have two arguments,
                if (callExpression.getArgumentList().getExpressions().length == 2) {
                    cat += child.getText() + "\n";
                    boolean useful = true;
                    for (PsiExpression expression : expressions) {
                        // Type must be a string
                        useful &= expression.getType().getCanonicalText().compareTo("java.lang.String") == 0;
                        // Text must contain putExtra
                        useful &= expression.getText().contains(".putExtra(");
                        try {
                            cat += expression.getText() + "  ->  " + expression.getType().getCanonicalText() + "\n\n";
                        } catch (Exception e) {
                            useful = false;
                        }
                    }
                    if (useful) {
                        cat += "USEFUL!!!\n";
                        list.add(callExpression);
                    }
                }

            }
            getAllChildern(child, list);
        }
    }
}
