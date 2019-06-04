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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Will Instrument the startActivity(Intent) method in order to notify NAPPA of ALL extras that have been added for
 * a given activity.
 *
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
 *         intent.putExtra(EXTRA_MESSAGE, message);
 *         prefetchingLib.notifyExtras(intent.getAllExtras)
 *         startActivity(intent);
 *
 */

public class ExtrasInstrumentationAction extends AnAction {
    Project project;
    PsiMethod signature;
    StringBuilder displayMessage = new StringBuilder();



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
                            PsiStatement[] psiStatements = psiMethod.getBody().getStatements();
                            for (PsiStatement statement : psiStatements) {

                                // Find the startActivity method call
                                if (statement.getText().contains("startActivity")) {


                                    activityTransitionFound = true;
                                    displayMessage.append("\n startActivity method call found in file: ").append(psiFile.getName())
                                                  .append("\nClass:").append(psiClass.getName())
                                                  .append("\nMethod:").append(psiMethod.getName());

                                        displayMessage.append("\n INSTRUMENTING:")
                                                      .append("\n Original Statement:\n")
                                                      .append(statement.getText());



                                    // Considering the expression: startActivity(intentName) .... Iterate recursively until
                                    statement.accept(new JavaRecursiveElementVisitor() {
                                            @Override
                                            public void visitElement(PsiElement element) {
                                                // The  "startActivity(intent)" is of PsiMethodCallExpression type
                                                if (element instanceof PsiMethodCallExpression) {
                                                    String intentName;
                                                    // Represents the parameters passed to the startActivityFunction
                                                    PsiElement parameterList = PsiTreeUtil.firstChild(element).getNextSibling();
                                                    // Get teh Identifier of the intent
                                                    intentName = PsiTreeUtil.findChildOfType(parameterList, PsiIdentifier.class)
                                                                    .getText();

                                                    final PsiElement clientBuilderElement = PsiElementFactory.SERVICE.getInstance(project)
                                                            .createExpressionFromText("PrefetchingLib.notifyExtras("+ intentName + ");"
                                                                    , psiClass);


                                                    // Inject the instrumented notifier of extra changes
                                                    WriteCommandAction.runWriteCommandAction(project, () -> {
                                                        element.getParent().addBefore(clientBuilderElement, element);


                                                    });
                                                }
                                                // Basecase: Only visit elements if the builder has not yet been encountered
                                                else{
                                                    super.visitElement(element);
                                                }
                                            }
                                        });

                                        displayMessage.append("\n New Statement:\n")
                                                      .append(statement.getText())
                                                      .append("\n\n").toString();


                                }

                            }

                        } catch (NullPointerException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
        if(!activityTransitionFound)
            displayMessage.append("\n\nNO ACTIVITY TRANSITION FOUND:")
                          .append("\n\nInstrumentation process did not change anything.");

        Messages.showMessageDialog(displayMessage.toString(), "Intent Extras Instrumentation", Messages.getInformationIcon());
    }

}

