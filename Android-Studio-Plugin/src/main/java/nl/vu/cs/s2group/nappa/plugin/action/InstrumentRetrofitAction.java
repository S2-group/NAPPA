package nl.vu.cs.s2group.nappa.plugin.action;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Will check the existence of Retrofit Variables in this projects AND instruments Retrofit
 * clients. Overall, the hierarchy inside a project that is covered by this instrumenter is as follows:
 * <p>
 * File {@link PsiFile}
 * |--->Class {@link PsiClass}
 * |-------|--->Method {@link PsiMethod}
 * |-------|----|------> Statement {@link PsiStatement}
 * <p>
 * The plugin considers the following Retrofit Builder Scenario:
 * <p>
 * Retrofit builder = new Retrofit.Builder()
 * .BuilderMethod(...)
 * ...
 * .build();
 */

public class InstrumentRetrofitAction extends AnAction {
    Project project;
    PsiMethod signature;
    StringBuilder displayMessage = new StringBuilder();



    /**
     * Will check the existence of Retrofit Variables in this project AND instruments Retrofit
     * clients to make use of okHttp as a client, and finally injects an interceptor to hook
     * the retrofit client to The prefetching Library..
     *
     * @param event
     */
    @Override
    public void actionPerformed(AnActionEvent event) {
        project = event.getProject();
        boolean retrofitFound = false;
        String[] fileNames = FilenameIndex.getAllFilenames(project);
        // Generate a list of all the files included in the Project
        List<PsiFile> psiFiles = new LinkedList<>();

        List<PsiStatement> retrofitDeclarations = new LinkedList<PsiStatement>();

        displayMessage.append("Greetings\n")
                    .append("We will now instrument all retrofit Instances to make use of an NAPPA enabled OkHttp client\n\n");

        // Fetch files from the project directory by searching by name
        for (String fileName : fileNames) {
            psiFiles.addAll(Arrays.asList(FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project))));
        }

        // Iterate all statements inside all project files declaring a new Retrofit Client
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

                                // Find the retrofit builder
                                if (statement.getText().contains("Retrofit.Builder")) {
                                    retrofitFound = true;
                                    displayMessage.append("\n Retrofit Definition found in file: ").append(psiFile.getName())
                                                  .append("\nClass:").append(psiClass.getName())
                                                  .append("\nMethod:").append(psiMethod.getName());

                                    // If the current builder instance contains a client specified by the user
                                    if (statement.getText().contains(".client")) {

                                        displayMessage.append("\n\n IMPORTANT:")
                                                      .append("\n-----------------\n")
                                                      .append("\n Retrofit Client definition identified with an OkHttp Client Instance. ")
                                                      .append("\n Statement:")
                                                      .append(statement.getText())
                                                      .append("\nPlease run \"Instrument OkHttp\" from the plugin if you have not done so yet.\n\n");



                                    }
                                    // If the user is using a default client
                                    else {
                                        displayMessage.append("\n INSTRUMENTING:")
                                                      .append("\n Original Statement:\n")
                                                      .append(statement.getText());

                                        final PsiElement clientBuilderElement = PsiElementFactory.SERVICE.getInstance(project)
                                                .createExpressionFromText(
                                                        "new Retrofit.Builder().client(Nappa.getOkHttp())", psiClass);

                                        // Considering the expression: new Retrofit.Builder().anotherExpression()...  Iterate recursively until
                                        //    the expression reaches the new Retrofit.Builder() substring
                                        statement.accept(new JavaRecursiveElementVisitor() {
                                            @Override
                                            public void visitElement(PsiElement element) {

                                                // The  "new Retrofit.Builder()" is of PsiNewExpression  type
                                                if (element instanceof PsiNewExpression) {


                                                    // Inject the instrumented okHttpClient to the Retrofit Client
                                                    WriteCommandAction.runWriteCommandAction(project, () -> {
                                                        element.replace(clientBuilderElement);


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

                            }

                        } catch (NullPointerException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
        if(!retrofitFound)
            displayMessage.append("\n\nNO INSTANCE OF RETROFIT FOUND:")
                          .append("\n\nInstrumentation process did not change anything.");

        Messages.showMessageDialog(displayMessage.toString(), "Retrofit Client Instrumentation", Messages.getInformationIcon());
    }

}

