package nl.vu.cs.s2group.nappa.plugin.action;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.JavaElementType;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import nl.vu.cs.s2group.nappa.plugin.util.InstrumentResultMessage;
import nl.vu.cs.s2group.nappa.plugin.util.InstrumentUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

public class InstrumentIntentExtrasAction extends AnAction {
    Project project;
    private InstrumentResultMessage resultMessage;
    StringBuilder displayMessage = new StringBuilder();

    /**
     * Will find the location of the startActivity(...) method, and from there it will
     * prepend a call to prefetchingLib.notifyExtras(intent.getAllExtras).
     *
     * @param event {@inheritDoc}
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        project = event.getProject();
        resultMessage = new InstrumentResultMessage();
        String[] fileFilter = new String[]{"android.content.Intent"};
        String[] classFilter = new String[]{"Intent"};

        try {
            List<PsiFile> psiFiles = InstrumentUtil.getAllJavaFilesInProjectAsPsi(project);
            InstrumentUtil.runScanOnJavaFile(psiFiles, fileFilter, classFilter, this::processPsiStatement);
            resultMessage.showResultDialog(project, "Intent Extras Instrumentation Result");
        } catch (Exception exception) {
            resultMessage.showErrorDialog(project, exception, "Failed to Instrument Intent Extras");
        }

//        boolean activityTransitionFound = false;
//        String[] fileNames = FilenameIndex.getAllFilenames(project);
//        // Generate a list of all the files included in the Project
//        List<PsiFile> psiFiles = new LinkedList<>();
//
//        List<PsiStatement> retrofitDeclarations = new LinkedList<PsiStatement>();
//
//        displayMessage.append("Greetings\n")
//                .append("We will now instrument all activity transitions due to calling the method startActivity(intentInstance).\n")
//                .append("We will inject a call to prefetchingLib.notifyExtras(...) to capture all the extras that are \n")
//                .append("being sent to the next activity.");
//
//        // Fetch files from the project directory by searching by name
//        for (String fileName : fileNames) {
//            psiFiles.addAll(Arrays.asList(FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project))));
//        }
//
//        // Iterate all statements inside all project files which contain an activity transition startActivity(...)
//        for (PsiFile psiFile : psiFiles) {
//
//            if (psiFile instanceof PsiJavaFile) {
//                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
//                PsiClass[] psiClasses = javaFile.getClasses();
//
//                for (PsiClass psiClass : psiClasses) {
//                    PsiMethod[] psiMethods = psiClass.getMethods();
//                    for (PsiMethod psiMethod : psiMethods) {
//                        try {
//                            // A PsiMethod's Body cannot be retrieved if it belongs to a compiled class
//                            if (psiMethod.getBody() != null) {
//
//                                PsiStatement[] psiStatements = psiMethod.getBody().getStatements();
//                                for (PsiStatement statement : psiStatements) {
//                                    String statementText = statement.getText();
//
//                                    // Find the startActivity method call
//                                    if (statementText.contains("startActivity") && !statementText.contains("PrefetchingLib.notifyExtras")) {
//
//                                        activityTransitionFound = true;
//                                        displayMessage.append("\n startActivity method call found in file: ").append(psiFile.getName())
//                                                .append("\nClass:").append(psiClass.getName())
//                                                .append("\nMethod:").append(psiMethod.getName());
//
//                                        displayMessage.append("\n INSTRUMENTING:")
//                                                .append("\n Original Statement:\n")
//                                                .append(statement.getText());
//
//                                        final PsiElement clientBuilderElement;
//                                        final PsiElement target;
//
//                                        statement.accept(new JavaRecursiveElementVisitor() {
//                                            @Override
//                                            public void visitElement(PsiElement element) {
//
//                                                try {
//                                                    String elementText = element.getText();
//                                                    if (!elementText.contains("startActivityForResult") && !elementText.contains("PrefetchingLib.notifyExtras")) {
//                                                        // Considering the method call expression: startActivity(intentName)
//                                                        if (element instanceof PsiMethodCallExpression && element.getText().startsWith("startActivity")) {
//                                                            String intentName = "";
//                                                            // Represents the parameters passed to the startActivity Function, surrounded by parenthesis
//                                                            PsiElement parameterList = PsiTreeUtil.findChildOfType(element, PsiExpressionList.class);
//                                                            // Get teh Identifier of the intent
//                                                            PsiElement intentNameHandle = PsiTreeUtil.findChildOfType(parameterList, PsiReferenceExpression.class);
//
//                                                            if (intentNameHandle != null) {
//                                                                intentName = intentNameHandle.getText();
//                                                                final PsiElement clientBuilderElement = PsiElementFactory.SERVICE.getInstance(project)
//                                                                        .createStatementFromText("PrefetchingLib.notifyExtras(" + intentName + ".getExtras());\n\n"
//                                                                                , psiClass);
//
//
//                                                                // Inject the instrumented notifier of extra changes
//                                                                WriteCommandAction.runWriteCommandAction(project, () -> {
//                                                                    element.getParent().addBefore(clientBuilderElement, element);
//
//
//                                                                });
//                                                            } else {
//                                                                String error = "\n\n*************Could not Parse Element: *********" +
//                                                                        "\nElement:" + element.getText() +
//                                                                        "\n**********************************************\n\n";
//
//                                                                System.out.println(error);
//
//                                                            }
//
//
//                                                        }
//                                                        // Considering the expression: view.context.startActivity(intentName)
//                                                        else if (element instanceof PsiIdentifier && element.getText().startsWith("startActivity")) {
//                                                            String intentName = "";
//                                                            // Find the Handle to the PARENT which encapsulates the entire method call
//                                                            // "view.context.startActivity(...)"
//                                                            PsiElement fullMethod = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
//                                                            // Get a handle to the parameters list "(intentName)" of "view.getContext().startActivity(intentName)"
//                                                            PsiElement parameterList = PsiTreeUtil.getNextSiblingOfType(fullMethod.getFirstChild(), PsiExpressionList.class);
//                                                            // Get a handle to the teh Identifier of the intent
//                                                            PsiElement intentNameHandle = PsiTreeUtil.findChildOfType(parameterList, PsiReferenceExpression.class);
//
//                                                            if (intentNameHandle != null) {
//                                                                // Fetch the intent Name and create the injectable statement
//                                                                intentName = intentNameHandle.getText();
//                                                                final PsiElement clientBuilderElement = PsiElementFactory.SERVICE.getInstance(project)
//                                                                        .createStatementFromText("PrefetchingLib.notifyExtras(" + intentName + ".getExtras());\n\n"
//                                                                                , psiClass);
//
//
//                                                                // Inject the intent probe before the startActivity Method
//                                                                WriteCommandAction.runWriteCommandAction(project, () -> {
//                                                                    fullMethod.addBefore(clientBuilderElement, fullMethod);
//
//
//                                                                });
//                                                            } else {
//                                                                String error = "\n\n*************Could not Parse Element: *********" +
//                                                                        "\nElement:" + element.getText() +
//                                                                        "\n**********************************************\n\n";
//
//                                                                System.out.println(error);
//
//                                                            }
//
//
//                                                        } else {
//                                                            super.visitElement(element);
//                                                        }
//
//                                                    }
//
//
//                                                } catch (NullPointerException e) {
//                                                    e.printStackTrace();
//
//                                                    displayMessage.append("NullPointerException:\n")
//                                                            .append("Element:\n\n");
//
//                                                    if (element != null) {
//                                                        displayMessage.append(element.getText());
//                                                    }
//
//
//                                                }
//
//                                            }
//                                        });
//
//                                        displayMessage.append("\n New Statement:\n")
//                                                .append(statement.getText())
//                                                .append("\n\n");
//
//
//                                    }
//
//                                }
//
//                            }
//
//                        } catch (NullPointerException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//        if (!activityTransitionFound)
//            displayMessage.append("\n\nNO ACTIVITY TRANSITION FOUND:")
//                    .append("\n\nInstrumentation process did not change anything.");
//
//        Messages.showMessageDialog(displayMessage.toString(), "Intent Extras Instrumentation", Messages.getInformationIcon());
    }

    private void processPsiStatement(@NotNull PsiElement rootPsiElement) {
        resultMessage.incrementProcessedStatementsCount();
        rootPsiElement.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                // Verifies if the element contain the method to instrument.
                // This verification is done here to reduce the number of recursive calls
                if (!element.getText().contains("startActivity")) return;

                boolean isStartActivity = element.getText().startsWith("startActivity");
                String rootPsiElementText = rootPsiElement.getText();
                String elementText = element.getText();

                // Verifies if this element is invoking a method or if it is invoked by its children
                // and if the invoked method is the `startActivity` or `startActivityForResult`
                if (isStartActivity && element instanceof PsiMethodCallExpression)
                    processStartActivityInstrumentationAsMethod(rootPsiElement, element);
                else if (isStartActivity && element instanceof PsiIdentifier)
                    processStartActivityInstrumentationAsIdentifies(rootPsiElement, element);
                else super.visitElement(element);
            }
        });
    }

    private void processStartActivityInstrumentationAsMethod(PsiElement rootPsiElement, PsiElement element) {
        String instrumentLine = "PrefetchingLib.notifyExtras(INTENT_NAME.getExtras());\n";

        // Represents the parameters passed to the startActivity Function, surrounded by parenthesis
        PsiExpressionList parameterList = PsiTreeUtil.findChildOfType(element, PsiExpressionList.class);
        // Get the Identifier of the intent
        PsiReferenceExpression intentNameHandle = PsiTreeUtil.findChildOfType(parameterList, PsiReferenceExpression.class);
        PsiStatement referenceStatement = PsiTreeUtil.getParentOfType(element, PsiStatement.class);

        if (intentNameHandle == null || referenceStatement == null) return;

        resultMessage.incrementPossibleInstrumentationCount();

        instrumentLine = instrumentLine.replace("INTENT_NAME", intentNameHandle.getText());

        // Verifies if this element is already instrumented
        PsiStatement previousStatement = PsiTreeUtil.getPrevSiblingOfType(referenceStatement, PsiStatement.class);
        if (previousStatement != null && previousStatement.getText().equals(instrumentLine)) {
            resultMessage.incrementAlreadyInstrumentedCount();
            return;
        }

        PsiClass psiClass = (PsiClass) InstrumentUtil.getAncestorPsiElementFromElement(rootPsiElement, PsiClass.class);
        PsiMethod psiMethod = (PsiMethod) InstrumentUtil.getAncestorPsiElementFromElement(rootPsiElement, PsiMethod.class);

        PsiElement instrumentedElement = PsiElementFactory
                .getInstance(project)
                .createStatementFromText(instrumentLine, psiClass);

        //noinspection ConstantConditions -- Since we loop through classes, it is certain that there is a parent Java class
        resultMessage.incrementInstrumentationCount()
                .appendPsiClass(psiClass)
                .appendPsiMethod(psiMethod)
                .appendNewBlock();

        // Inject the instrumented notifier of extra changes
        WriteCommandAction.runWriteCommandAction(project, () -> {
            element.getParent().addBefore(instrumentedElement, referenceStatement);
        });
    }

    private void processStartActivityInstrumentationAsIdentifies(PsiElement rootPsiElement, PsiElement element) {
        // Find the Handle to the PARENT which encapsulates the entire method call
        // "view.context.startActivity(...)"
        PsiMethodCallExpression fullMethod = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        if (fullMethod == null) return;
        processStartActivityInstrumentationAsMethod(rootPsiElement, fullMethod);
    }
}

