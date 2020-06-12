package nl.vu.cs.s2group.nappa.plugin.action;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import nl.vu.cs.s2group.nappa.plugin.util.InstrumentResultMessage;
import nl.vu.cs.s2group.nappa.plugin.util.InstrumentUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
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
    private static final int HAS_NO_INLINE_IF = 0;
    private static final int HAS_INLINE_THEN_BRANCH = 1;
    private static final int HAS_INLINE_ELSE_BRANCH = 2;

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
        // Defines all variations of the method startActivity in the Android API
        String[] identifierFilter = new String[]{
                // https://developer.android.com/reference/android/app/Activity#startActivity(android.content.Intent)
                "startActivity",

                // https://developer.android.com/reference/android/app/Activity#startActivityForResult(android.content.Intent,%20int)
                "startActivityForResult",

                // https://developer.android.com/reference/android/app/Activity#startActivityFromChild(android.app.Activity,%20android.content.Intent,%20int)
                // This method was deprecated in API level 30.
                "startActivityFromChild",

                // https://developer.android.com/reference/android/app/Activity#startActivityFromFragment(android.app.Fragment,%20android.content.Intent,%20int,%20android.os.Bundle)
                // This method was deprecated in API level 28.
                "startActivityFromFragment",

                // https://developer.android.com/reference/android/app/Activity#startActivityIfNeeded(android.content.Intent,%20int,%20android.os.Bundle)
                "startActivityIfNeeded",
        };

        rootPsiElement.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                resultMessage.incrementProcessedElementsCount();

                // This verification is done here to reduce the number of recursive calls
                if (!element.getText().contains("startActivity")) return;

                // Verifies if it is a identifier of a startActivity method
                if (!(element instanceof PsiIdentifier) || Arrays.stream(identifierFilter).noneMatch(element.getText()::equals)) {
                    super.visitElement(element);
                    return;
                }

                // Verifies if this identifier refers to a method call
                PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
                if (methodCall == null) return;

                resultMessage.incrementPossibleInstrumentationCount();

                // Verifies if the startActivity method call is declared inside an inline lambda function
                // or in a IF statement with an inline THEN or ELSe branches
                int hasInlineIfStatement = isProcessingInlineIf(methodCall);
                boolean hasInlineLambdaFunction = methodCall.getParent() instanceof PsiLambdaExpression;
                boolean requiresToEncapsulateInCodeBlock = hasInlineLambdaFunction || hasInlineIfStatement != HAS_NO_INLINE_IF;

                // Verifies if the processed element is in an inline THEN branch of a IF statement and if
                // the IF has a ELSE branch. If this is the case, the the ELSE branch is processed first.
                // In the current implementation, if the THEN branch is processed before the ELSE branch,
                // then the ELSE branch is skipped.
                if (hasInlineIfStatement == HAS_INLINE_THEN_BRANCH) {
                    PsiIfStatement ifStatement = PsiTreeUtil.getParentOfType(methodCall, PsiIfStatement.class, false, PsiCodeBlock.class);
                    if (ifStatement != null && ifStatement.getElseBranch() != null)
                        super.visitElement(ifStatement.getElseBranch());
                }

                // Fetches the Intent object sent as parameter in the method startActivity and the statement
                // element used as reference to instrument non-inline occurrences
                PsiElement intentParameter = findElementSentAsIntentParameter((PsiIdentifier) element, methodCall);
                PsiStatement referenceStatement = PsiTreeUtil.getParentOfType(methodCall, PsiStatement.class);
                if (referenceStatement == null || intentParameter == null) return;

                // Verifies if this element is already instrumented. The requiresToEncapsulateInCodeBlock flag
                // is considered in the verification since any inline block instrumented by this action will
                // always be replaced with a code block. Thus, if a inline statement is found, the method has
                // not been instrumented yet. Furthermore, the previous statement of a inline block might contain
                // a instrumented statement referent to another startActivity method.
                PsiStatement previousStatement = PsiTreeUtil.getPrevSiblingOfType(referenceStatement, PsiStatement.class);
                if (previousStatement != null && !requiresToEncapsulateInCodeBlock && previousStatement.getText().contains("PrefetchingLib")) {
                    resultMessage.incrementAlreadyInstrumentedCount();
                    return;
                }

                PsiClass psiClass = PsiTreeUtil.getParentOfType(rootPsiElement, PsiClass.class);
                //noinspection ConstantConditions --> To arrive here we looped through Java clasees
                InstrumentUtil.addLibraryImport(project, psiClass);

                String instrumentedText = "PrefetchingLib.notifyExtras(INTENT.getExtras());";
                if (intentParameter instanceof PsiReferenceExpression)
                    injectExtraProbeForVariableReference(psiClass,
                            referenceStatement,
                            methodCall,
                            (PsiReferenceExpression) intentParameter,
                            instrumentedText,
                            requiresToEncapsulateInCodeBlock);
                else
                    injectExtraProbeForMethodCallOrNewExpression(psiClass,
                            referenceStatement,
                            methodCall,
                            intentParameter,
                            instrumentedText,
                            requiresToEncapsulateInCodeBlock);

                PsiMethod psiMethod = PsiTreeUtil.getParentOfType(rootPsiElement, PsiMethod.class);
                //noinspection ConstantConditions --> To arrive here we looped through Java methods
                resultMessage.incrementInstrumentationCount()
                        .appendPsiClass(psiClass)
                        .appendPsiMethod(psiMethod)
                        .appendNewBlock();
            }
        });
    }

    /**
     * Verifies if the method call is declared in an inline THEN/ELSE branch of an IF statement
     *
     * @param methodCall The {@code startActivity} method
     * @return {@code True} if the {@code startActivity} method is declared either in the THEN or ELSE branch
     * of an IF statement and the branch is an inline branch
     */
    private int isProcessingInlineIf(PsiMethodCallExpression methodCall) {
        // Verifies if the methodCall is inside an IF statement
        PsiIfStatement ifStatement = PsiTreeUtil.getParentOfType(methodCall, PsiIfStatement.class, false, PsiCodeBlock.class);
        if (ifStatement == null) return HAS_NO_INLINE_IF;

        // Verifies if there is an THEN branch -- It should always have, but just in case...
        PsiStatement thenBranch = ifStatement.getThenBranch();
        if (thenBranch == null) return HAS_NO_INLINE_IF;

        // Verifies if the THEN branch is inline and if it contains the methodCall
        PsiMethodCallExpression methodCallInThenBranch = PsiTreeUtil.getChildOfType(ifStatement.getThenBranch(), PsiMethodCallExpression.class);
        if (methodCallInThenBranch != null && methodCallInThenBranch.equals(methodCall)) return HAS_INLINE_THEN_BRANCH;

        // Verifies if there is an ELSE branch
        PsiStatement elseBranch = ifStatement.getElseBranch();
        if (elseBranch == null) return HAS_NO_INLINE_IF;

        // Verifies if the ELSE branch is inline and if it contains the methodCall
        PsiMethodCallExpression methodCallInElseBranch = PsiTreeUtil.getChildOfType(ifStatement.getElseBranch(), PsiMethodCallExpression.class);
        if (methodCallInElseBranch != null && methodCallInElseBranch.equals(methodCall)) return HAS_INLINE_ELSE_BRANCH;

        return HAS_NO_INLINE_IF;
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
        // Verifies in which position the method receives a Intent parameter
        String[] identifierFilter = new String[]{
                "startActivityFromChild",
                "startActivityFromFragment",
        };
        int parameterPosition = Arrays.asList(identifierFilter).contains(methodIdentifier.getText()) ? 2 : 1;
        int currentParameterPosition = 0;

        // Fetch the list of parameters
        PsiExpressionList parameterList = PsiTreeUtil.getChildOfType(methodCallExpression, PsiExpressionList.class);
        if (parameterList == null) return null;

        // Loop through the method parameters list
        for (PsiElement child : parameterList.getChildren()) {
            if (!(child instanceof PsiJavaToken || child instanceof PsiWhiteSpace)) {
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
                                                      @NotNull String instrumentedText,
                                                      boolean requiresToEncapsulateInCodeBlock) {
        // Construct the element to inject
        PsiElement instrumentedElement = PsiElementFactory
                .getInstance(project)
                .createStatementFromText(instrumentedText.replace("INTENT", intentParameter.getText()), psiClass);

        // Verifies if we are instrumenting a inline statement
        if (requiresToEncapsulateInCodeBlock) {
            injectExtraProbesForInlineLambdaFunction(methodCall, new PsiElement[]{
                    instrumentedElement,
                    PsiElementFactory
                            .getInstance(project)
                            .createStatementFromText(methodCall.getText() + ";", psiClass),
            });
            return;
        }

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
                                                              @NotNull String instrumentedText,
                                                              boolean requiresToEncapsulateInCodeBlock) {
        // Construct the source code text to inject
        String variableName = InstrumentUtil.getUniqueVariableName(methodCall, "intent");
        String intentDeclarationText = "Intent " + variableName + " = " + intentParameter.getText() + ";";
        String methodCallText = methodCall.getText().replace(intentParameter.getText(), variableName);
        methodCallText = methodCallText.replace("\n", "").replaceAll(" {2}", " ");

        // Construct the elements to inject -- The declaration of an Intent object and the call to the Prefetch Library
        PsiElement instrumentedElementIntent = PsiElementFactory
                .getInstance(project)
                .createStatementFromText(intentDeclarationText, psiClass);
        PsiElement instrumentedElementLibrary = PsiElementFactory
                .getInstance(project)
                .createStatementFromText(instrumentedText.replace("INTENT", variableName), psiClass);

        // Verifies if we are instrumenting a inline statement
        if (requiresToEncapsulateInCodeBlock) {
            injectExtraProbesForInlineLambdaFunction(methodCall, new PsiElement[]{
                    instrumentedElementIntent,
                    instrumentedElementLibrary,
                    PsiElementFactory
                            .getInstance(project)
                            .createStatementFromText(methodCallText + ";", psiClass),
            });
            return;
        }

        // Construct the elements to inject -- The call to the method startActivity
        PsiElement instrumentedElementMethodCall = PsiElementFactory
                .getInstance(project)
                .createStatementFromText(methodCallText, psiClass);

        // Inject the instrumented notifier of extra changes and the new Intent object
        WriteCommandAction.runWriteCommandAction(project, () -> {
            referenceStatement.getParent().addBefore(instrumentedElementIntent, referenceStatement);
            referenceStatement.getParent().addBefore(instrumentedElementLibrary, referenceStatement);
            methodCall.replace(instrumentedElementMethodCall);
        });
    }

    /**
     * This method provides an extension to the methods {@link #injectExtraProbeForMethodCallOrNewExpression}
     * and {@link #injectExtraProbeForVariableReference} for cases where the startActivity method to instrument
     * is declared within an inline statement (e.g. lambda function, inline THEN/ELSE branches in IFs statements).
     * It replaces the {@code methodCall} element with a new {@link PsiCodeBlock} containing all elements
     * in the list {@code elementsToInject}
     * <br/><br/>
     *
     * <p> Case 1. Lambda functions
     *
     * <pre>{@code
     * // Target
     * someMethod((someParams) -> startActivity(intent));
     * someMethod2((someParams2) -> startActivity(createsNewIntent()));
     *
     * // Result
     * someMethod((someParams) -> {
     *     PrefetchingLib.notifyExtras(intent.getExtras());
     *     startActivity(intent);
     * });
     * someMethod2((someParams2) -> {
     *     Intent intent1 = createsNewIntent();
     *     PrefetchingLib.notifyExtras(intent1.getExtras());
     *     startActivity(intent1);
     * });
     * }</pre>
     *
     * <p> Case 1. IF statements
     *
     * <pre>{@code
     * // Target
     * if(condition) startActivity(new Intent(....));
     * else startActivity(new Intent(....);
     *
     * // Result
     * if(condition) {
     *     Intent intent1 = new Intent(....);
     *     PrefetchingLib.notifyExtras(intent1.getExtras());
     *     startActivity(intent1);
     * } else {
     *     Intent intent = new Intent(....);
     *     PrefetchingLib.notifyExtras(intent.getExtras());
     *     startActivity(intent);
     * }
     * }</pre>
     *
     * @param methodCall       Represents the startActivity method to instrument
     * @param elementsToInject Represents the list of {@link PsiElement} to inject in this instrumentation
     */
    private void injectExtraProbesForInlineLambdaFunction(PsiMethodCallExpression methodCall, PsiElement[] elementsToInject) {
        // Fetches the ancestor with possible inline statement
        PsiLambdaExpression lambdaExpression = PsiTreeUtil.getParentOfType(methodCall, PsiLambdaExpression.class, false, PsiCodeBlock.class);
        PsiIfStatement ifStatement = PsiTreeUtil.getParentOfType(methodCall, PsiIfStatement.class, false, PsiCodeBlock.class);

        // Construct a empty code block to inject
        PsiElement newCodeBlock = PsiElementFactory
                .getInstance(project)
                .createCodeBlock();

        // Inject the instrumented statements within a code block
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (PsiElement psiElement : elementsToInject) {
                newCodeBlock.add(psiElement);
            }

            // Verifies what type of inline statement is being instrumented
            if (lambdaExpression != null) methodCall.replace(newCodeBlock);
            else if (ifStatement != null) methodCall.getParent().replace(newCodeBlock);
        });
    }
}

