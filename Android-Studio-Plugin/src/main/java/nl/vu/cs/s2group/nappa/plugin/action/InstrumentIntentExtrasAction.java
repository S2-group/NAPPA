package nl.vu.cs.s2group.nappa.plugin.action;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.JavaElementType;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import nl.vu.cs.s2group.nappa.plugin.util.InstrumentResultMessage;
import nl.vu.cs.s2group.nappa.plugin.util.InstrumentUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        // All variations of the method startActivity in the Android API
        // https://developer.android.com/reference/android/app/Activity#startActivities(android.content.Intent[],%20android.os.Bundle)
        String[] identifierFilter = new String[]{
                "startActivity",
                "startActivityForResult",
                "startActivityFromChild",
                "startActivityFromFragment",
                "startActivityIfNeeded",
        };
        resultMessage.incrementProcessedStatementsCount();
        rootPsiElement.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                // This verification is done here to reduce the number of recursive calls
                if (!element.getText().contains("startActivity")) return;

                // Verifies if it is a identifier of a startActivity method
                if (!(element instanceof PsiIdentifier) || Arrays.stream(identifierFilter).noneMatch(element.getText()::equals)) {
                    super.visitElement(element);
                    return;
                }

                resultMessage.incrementPossibleInstrumentationCount();

                PsiClass psiClass = PsiTreeUtil.getParentOfType(rootPsiElement, PsiClass.class);
                PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
                PsiExpressionList parameterList = PsiTreeUtil.getChildOfType(methodCall, PsiExpressionList.class);
                PsiElement intentParameter = findElementSentAsIntentParameter((PsiIdentifier) element, methodCall);
                boolean isMethodInInlineLambdaFunction = methodCall.getParent() instanceof PsiLambdaExpression;
                String instrumentedText = "PrefetchingLib.notifyExtras(INTENT.getExtras());";

                PsiStatement referenceStatement = PsiTreeUtil.getParentOfType(methodCall, PsiStatement.class);
                if (referenceStatement == null || intentParameter == null) return;

                // Verifies if this element is already instrumented
                PsiStatement previousStatement = PsiTreeUtil.getPrevSiblingOfType(referenceStatement, PsiStatement.class);
                if (previousStatement != null && previousStatement.getText().contains("PrefetchingLib")) {
                    resultMessage.incrementAlreadyInstrumentedCount();
                    return;
                }

                String str = "---\n" +
                        "Class: " + psiClass.getName() + "\n" +
                        "Element: " + element.getText() + "\n" +
                        "Full method " + methodCall.getText() + "\n" +
                        "Parameter list " + parameterList.getText() + "\n" +
                        "Intent parameter " + (intentParameter != null ? intentParameter : "not found") + "\n" +
                        "Is lambda inline: " + isMethodInInlineLambdaFunction + "\n";
                System.out.print(str);

                if (intentParameter instanceof PsiReferenceExpression)
                    injectExtraProbeForVariableReference(psiClass, referenceStatement, methodCall, (PsiReferenceExpression) intentParameter, instrumentedText);
                else
                    injectExtraProbeForMethodCallOrNewExpression(psiClass, referenceStatement, methodCall, intentParameter, instrumentedText);

                System.out.print("\n");
            }
        });
    }

    /**
     * Scan the parameter list and return the {@link PsiElement} representing the object sent as the
     * {@link android.content.Intent Intent} parameter for the method {@code startActivity} and its variants
     *
     * @param methodIdentifier     Represent the identifier of the method {@code startActivity}
     * @param methodCallExpression Represents the list of parameter send to the method {@code startActivity}
     * @return The {@link PsiElement} object representing the parameter {@link android.content.Intent Intent}
     * or {@code null} otherwise
     */
    @Nullable
    private PsiElement findElementSentAsIntentParameter(@NotNull PsiIdentifier methodIdentifier, PsiMethodCallExpression methodCallExpression) {
        String[] identifierFilter = new String[]{
                "startActivityFromChild",
                "startActivityFromFragment",
        };
        // The startActivity methods above receives the Intent in the second parameter
        int parameterPosition = Arrays.asList(identifierFilter).contains(methodIdentifier.getText()) ? 2 : 1;
        int currentParameterPosition = 0;
        PsiExpressionList parameterList = PsiTreeUtil.getChildOfType(methodCallExpression, PsiExpressionList.class);

        if (parameterList == null) return null;

        for (PsiElement child : parameterList.getChildren()) {
            if (child instanceof PsiReferenceExpression || child instanceof PsiMethodCallExpression || child instanceof PsiNewExpression) {
                currentParameterPosition++;
                if (currentParameterPosition == parameterPosition) return child;
            }

        }
        return null;
    }

    /**
     * Instrument the simplest case when the method {@code startActivity} receives a existing
     * {@link android.content.Intent Intent} object. The target source code and resulting instrumentation
     * are the follow"
     *
     * <pre>{@code
     * // Target
     * Intent myIntent = ...
     * startActivity(myIntent);
     *
     * // Result
     * Intent myIntent = ...
     * PrefetchingLib.notifyExtras(myIntent.getExtras());
     * startActivity(myIntent);
     * }</pre>
     *
     * @param psiClass           Represents a Java class
     * @param referenceStatement Represents the {@link PsiElement} used as reference to inject a new {@link PsiElement}
     * @param methodCall         Represents the method {@code startActivity}
     * @param intentParameter    Represent the object send as the parameter {@link android.content.Intent Intent} in
     *                           the method {@code startActivity}
     * @param instrumentedText   Represents the template source code to inject
     */
    private void injectExtraProbeForVariableReference(PsiClass psiClass,
                                                      PsiElement referenceStatement,
                                                      @NotNull PsiMethodCallExpression methodCall,
                                                      @NotNull PsiReferenceExpression intentParameter,
                                                      @NotNull String instrumentedText) {
        PsiElement instrumentedElement = PsiElementFactory
                .getInstance(project)
                .createStatementFromText(instrumentedText.replace("INTENT", intentParameter.getText()), psiClass);

        resultMessage.incrementInstrumentationCount();

        // Verifies if we are instrumenting a inline lambda function
        if (methodCall.getParent() instanceof PsiLambdaExpression) {
            injectExtraProbesForInlineLambdaFunction(methodCall, new PsiElement[]{instrumentedElement});
            return;
        }

        System.out.print("instrument: " + instrumentedElement.getText());
        // Inject the instrumented notifier of extra changes
        WriteCommandAction.runWriteCommandAction(project, () -> {
            referenceStatement.getParent().addBefore(instrumentedElement, referenceStatement);
        });
    }

    /**
     * Instrument the simplest case when the method {@code startActivity} receives a new
     * {@link android.content.Intent Intent} object, either via a instantiation with keyword {@code new}
     * or a method call. The target source code and resulting instrumentation are the follow:
     * <br/><br/>
     *
     * <p> Case 1. Instantiation
     *
     * <pre>{@code
     * // Target
     * startActivity(new Intent(...));
     *
     * // Result
     * Intent intent = new Intent(...);
     * PrefetchingLib.notifyExtras(intent.getExtras());
     * startActivity(intent)
     * }</pre>
     *
     * <p> Case 1. Method call
     *
     * <pre>{@code
     * // Target
     * startActivity(Intent.createChooser(...));
     *
     * // Result
     * Intent intent = Intent.createChooser(...);
     * PrefetchingLib.notifyExtras(intent.getExtras());
     * startActivity(intent)
     * }</pre>
     *
     * @param psiClass           Represents a Java class
     * @param referenceStatement Represents the {@link PsiElement} used as reference to inject a new {@link PsiElement}
     * @param methodCall         Represents the method {@code startActivity}
     * @param intentParameter    Represent the object send as the parameter {@link android.content.Intent Intent} in
     *                           the method {@code startActivity}
     * @param instrumentedText   Represents the template source code to inject
     */
    private void injectExtraProbeForMethodCallOrNewExpression(PsiClass psiClass,
                                                              PsiElement referenceStatement,
                                                              @NotNull PsiMethodCallExpression methodCall,
                                                              @NotNull PsiElement intentParameter,
                                                              @NotNull String instrumentedText) {
        String instrumentedElementIntentText = "Intent intent = " + intentParameter.getText() + ";";
        String instrumentedElementMethodCallText = methodCall.getText().replace(intentParameter.getText(), "intent");

        PsiElement instrumentedElementIntent = PsiElementFactory
                .getInstance(project)
                .createStatementFromText(instrumentedElementIntentText, psiClass);
        PsiElement instrumentedElementLibrary = PsiElementFactory
                .getInstance(project)
                .createStatementFromText(instrumentedText.replace("INTENT", "intent"), psiClass);
        PsiElement instrumentedElementMethodCall = PsiElementFactory
                .getInstance(project)
                .createStatementFromText(instrumentedElementMethodCallText, psiClass);

        resultMessage.incrementInstrumentationCount();

        if (methodCall.getParent() instanceof PsiLambdaExpression) {
            injectExtraProbesForInlineLambdaFunction(methodCall, new PsiElement[]{
                    instrumentedElementIntent,
                    instrumentedElementLibrary,
                    instrumentedElementMethodCall,
            });
            return;
        }

        System.out.print("instrument: " + instrumentedElementIntent.getText() + "\n" + instrumentedElementLibrary.getText() + "\n" + instrumentedElementMethodCall.getText() + "\n");
        // Inject the instrumented notifier of extra changes
        WriteCommandAction.runWriteCommandAction(project, () -> {
            referenceStatement.getParent().addBefore(instrumentedElementIntent, referenceStatement);
            referenceStatement.getParent().addBefore(instrumentedElementLibrary, referenceStatement);
            methodCall.replace(instrumentedElementMethodCall);
        });
    }

    private void injectExtraProbesForInlineLambdaFunction(PsiMethodCallExpression methodCall, PsiElement[] elementsToInject) {
        PsiElement newCodeBlock = PsiElementFactory
                .getInstance(project)
                .createCodeBlock();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (PsiElement psiElement : elementsToInject) {
                newCodeBlock.add(psiElement);
            }
            methodCall.getParent().add(newCodeBlock);
            methodCall.delete();
        });
    }

//    private void processStartActivityInstrumentationAsMethod(PsiElement rootPsiElement, PsiElement element) {
//        String instrumentedText = "PrefetchingLib.notifyExtras(INTENT_NAME.getExtras());\n";
//
//        // Represents the parameters passed to the startActivity Function, surrounded by parenthesis
//        PsiExpressionList parameterList = PsiTreeUtil.findChildOfType(element, PsiExpressionList.class);
//        // Get the Identifier of the intent
//        PsiReferenceExpression intentNameHandle = PsiTreeUtil.findChildOfType(parameterList, PsiReferenceExpression.class);
//        PsiStatement referenceStatement = PsiTreeUtil.getParentOfType(element, PsiStatement.class);
//
//        if (intentNameHandle == null || referenceStatement == null) return;
//
//        resultMessage.incrementPossibleInstrumentationCount();
//
//        instrumentedText = instrumentedText.replace("INTENT_NAME", intentNameHandle.getText());
//
//        // Verifies if this element is already instrumented
//        PsiStatement previousStatement = PsiTreeUtil.getPrevSiblingOfType(referenceStatement, PsiStatement.class);
//        if (previousStatement != null && previousStatement.getText().equals(instrumentedText)) {
//            resultMessage.incrementAlreadyInstrumentedCount();
//            return;
//        }
//
//        PsiClass psiClass = (PsiClass) InstrumentUtil.getAncestorPsiElementFromElement(rootPsiElement, PsiClass.class);
//        PsiMethod psiMethod = (PsiMethod) InstrumentUtil.getAncestorPsiElementFromElement(rootPsiElement, PsiMethod.class);
//
//        if (element.getParent() instanceof PsiLambdaExpression)
//            injectExtraProbeOnSingleLineLambdaFunction(psiClass, element, instrumentedText);
//        else {
//            injectExtraProbesOnSimpleStatement(psiClass, element, instrumentedText);
//        }
//
//        //noinspection ConstantConditions -- Since we loop through classes, it is certain that there is a parent Java class
//        resultMessage.incrementInstrumentationCount()
//                .appendPsiClass(psiClass)
//                .appendPsiMethod(psiMethod)
//                .appendNewBlock();
//    }

//    private void processStartActivityInstrumentationAsIdentifies(PsiElement rootPsiElement, PsiElement element) {
//        // Find the Handle to the PARENT which encapsulates the entire method call
//        // "view.context.startActivity(...)"
//        PsiMethodCallExpression fullMethod = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
//        if (fullMethod == null) return;
//        processStartActivityInstrumentationAsMethod(rootPsiElement, fullMethod);
//    }
//
//    private void injectExtraProbeOnSingleLineLambdaFunction(PsiClass psiClass,
//                                                            PsiElement startActivityElement,
//                                                            String instrumentedText) {
//        PsiElement newCodeBlock = PsiElementFactory
//                .getInstance(project)
//                .createCodeBlock();
//        PsiElement instrumentedElement = PsiElementFactory
//                .getInstance(project)
//                .createStatementFromText(instrumentedText, psiClass);
//        WriteCommandAction.runWriteCommandAction(project, () -> {
//            newCodeBlock.add(instrumentedElement);
//            newCodeBlock.add(startActivityElement.copy());
//            startActivityElement.getParent().add(newCodeBlock);
//            startActivityElement.delete();
//        });
//    }
//
//    private void injectExtraProbesOnSimpleStatement(PsiClass psiClass, PsiElement element, String instrumentedText) {
//        PsiElement instrumentedElement = PsiElementFactory
//                .getInstance(project)
//                .createStatementFromText(instrumentedText, psiClass);
//
//        // Inject the instrumented notifier of extra changes
//        WriteCommandAction.runWriteCommandAction(project, () -> {
//            element.getParent().addBefore(instrumentedElement, referenceStatement);
//        });
//    }
}

