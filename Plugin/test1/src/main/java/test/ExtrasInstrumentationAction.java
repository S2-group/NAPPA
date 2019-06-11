package test;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiExpressionListImpl;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.io.Console;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Will Instrument the startActivity(Intent) method in order to notify NAPPA of ALL extras that have been added for
 * a given activity.
 * <p>
 * NOTE: This action relies on the intent.getExtras() method, which will return NULL if there are NO extras added to the
 * intent.  NAPPA will ignore this instrumentation if the extras bundle is NULL.
 *
 * <p>
 * File {@link PsiFile}
 * |--->Class {@link PsiClass}
 * |-------|--->Method {@link PsiMethod}
 * |-------|----|------> Statement {@link PsiStatement}
 * <p>
 * The plugin considers the following Activity Transition Scenario:
 * <p>
 * intent.putExtra(EXTRA_MESSAGE, message);
 * prefetchingLib.notifyExtras(intent.getAllExtras)
 * startActivity(intent);
 */

public class ExtrasInstrumentationAction extends AnAction {
    Project project;
    PsiMethod signature;
    StringBuilder displayMessage = new StringBuilder();
    StringBuilder logger = new StringBuilder();

    /**
     * Will find the location of the startActivity(...) method, and from there it will
     * perpend a call to prefetchingLib.notifyExtras(intent.getAllExtras).
     *
     * @param event
     */
    @Override
    public void actionPerformed(AnActionEvent event) {

        project = event.getProject();
        boolean activityTransitionFound = false;
        String[] fileNames = FilenameIndex.getAllFilenames(project);
        // Generate a list of all the files included in the Project
        List<PsiFile> psiFiles = new LinkedList<>();

        List<PsiStatement> retrofitDeclarations = new LinkedList<PsiStatement>();

        displayMessage.append("Greetings\n")
                .append("We will now instrument all activity transitions due to calling the method startActivity(intentInstance).\n")
                .append("We will inject a call to prefetchingLib.notifyExtras(...) to capture all the extras that are \n")
                .append("being sent to the next activity.");

        // Fetch files from the project directory by searching by name
        for (String fileName : fileNames) {
            psiFiles.addAll(Arrays.asList(FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project))));
        }

        // Iterate all statements inside all project files which contain an activity transition startActivity(...)
        for (PsiFile psiFile : psiFiles) {

            if (psiFile instanceof PsiJavaFile) {
                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                PsiClass[] psiClasses = javaFile.getClasses();

                for (PsiClass psiClass : psiClasses) {
                    PsiMethod[] psiMethods = psiClass.getMethods();
                    for (PsiMethod psiMethod : psiMethods) {
                        try {
                            // A PsiMethod's Body cannot be retrieved if it belongs to a compiled class
                            if (psiMethod.getBody() != null) {

                                PsiStatement[] psiStatements = psiMethod.getBody().getStatements();
                                for (PsiStatement statement : psiStatements) {
                                    String statementText = statement.getText();

                                    // Find the startActivity method call
                                    if (statementText.contains("startActivity") && !statementText.contains("PrefetchingLib.notifyExtras")) {

                                        activityTransitionFound = true;
                                        displayMessage.append("\n startActivity method call found in file: ").append(psiFile.getName())
                                                .append("\nClass:").append(psiClass.getName())
                                                .append("\nMethod:").append(psiMethod.getName());

                                        displayMessage.append("\n INSTRUMENTING:")
                                                .append("\n Original Statement:\n")
                                                .append(statement.getText());

                                        final PsiElement clientBuilderElement;
                                        final PsiElement target;

                                        statement.accept(new JavaRecursiveElementVisitor() {
                                            @Override
                                            public void visitElement(PsiElement element) {

                                                super.visitElement(element);
                                                try{
                                                    String elementText = element.getText();
                                                    if(!elementText.contains("startActivityForResult") && !elementText.contains("PrefetchingLib.notifyExtras"))
                                                    {
                                                        // Considering the method call expression: startActivity(intentName)
                                                        if (element instanceof PsiMethodCallExpression && element.getText().startsWith("startActivity")) {
                                                            String intentName = "";
                                                            // Represents the parameters passed to the startActivity Function, surrounded by parenthesis
                                                            PsiElement parameterList = PsiTreeUtil.findChildOfType(element, PsiExpressionList.class);
                                                            // Get teh Identifier of the intent
                                                            PsiElement intentNameHandle = PsiTreeUtil.findChildOfType(parameterList, PsiReferenceExpression.class);

                                                            if (intentNameHandle != null) {
                                                                intentName = intentNameHandle.getText();
                                                                final PsiElement clientBuilderElement = PsiElementFactory.SERVICE.getInstance(project)
                                                                        .createStatementFromText("PrefetchingLib.notifyExtras(" + intentName + ".getExtras());\n\n"
                                                                                , psiClass);



                                                                // Inject the instrumented notifier of extra changes
                                                                WriteCommandAction.runWriteCommandAction(project, () -> {
                                                                    element.getParent().addBefore(clientBuilderElement, element);


                                                                });
                                                            }else{
                                                                String error = "\n\n*************Could not Parse Element: *********" +
                                                                        "\nElement:" + element.getText() +
                                                                        "\n**********************************************\n\n";

                                                                System.out.println(error);

                                                            }


                                                        }
                                                        // Considering the expression: view.context.startActivity(intentName)
                                                        else if (element instanceof PsiIdentifier && element.getText().startsWith("startActivity")) {
                                                            String intentName = "";
                                                            // Find the Handle to the PARENT which encapsulates the entire method call
                                                            // "view.context.startActivity(...)"
                                                            PsiElement fullMethod = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
                                                            // Get a handle to the parameters list "(intentName)" of "view.getContext().startActivity(intentName)"
                                                            PsiElement parameterList = PsiTreeUtil.getNextSiblingOfType(fullMethod.getFirstChild(), PsiExpressionList.class);
                                                            // Get a handle to the teh Identifier of the intent
                                                            PsiElement intentNameHandle = PsiTreeUtil.findChildOfType(parameterList, PsiReferenceExpression.class);

                                                            if (intentNameHandle != null) {
                                                                // Fetch the intent Name and create the injectable statement
                                                                intentName = intentNameHandle.getText();
                                                                final PsiElement clientBuilderElement = PsiElementFactory.SERVICE.getInstance(project)
                                                                        .createStatementFromText("PrefetchingLib.notifyExtras(" + intentName + ".getExtras());\n\n"
                                                                                , psiClass);


                                                                // Inject the intent probe before the startActivity Method
                                                                WriteCommandAction.runWriteCommandAction(project, () -> {
                                                                    fullMethod.addBefore(clientBuilderElement, fullMethod);


                                                                });
                                                            } else {
                                                                String error = "\n\n*************Could not Parse Element: *********" +
                                                                        "\nElement:" + element.getText() +
                                                                        "\n**********************************************\n\n";

                                                                System.out.println(error);

                                                            }


                                                        }

                                                    }


                                                }catch(NullPointerException e)
                                                {
                                                    e.printStackTrace();

                                                    displayMessage.append("NullPointerException:\n")
                                                                  .append("Element:\n\n");

                                                    if(element!=null)
                                                    {
                                                        displayMessage.append(element.getText());
                                                    }


                                                }

                                            }
                                        });

                                        displayMessage.append("\n New Statement:\n")
                                                .append(statement.getText())
                                                .append("\n\n").toString();


                                    }

                                }

                            }

                        } catch (NullPointerException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
        if (!activityTransitionFound)
            displayMessage.append("\n\nNO ACTIVITY TRANSITION FOUND:")
                    .append("\n\nInstrumentation process did not change anything.");

        Messages.showMessageDialog(displayMessage.toString(), "Intent Extras Instrumentation", Messages.getInformationIcon());
    }

}

