package nl.vu.cs.s2group.nappa.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import nl.vu.cs.s2group.nappa.plugin.util.InstrumentResultMessage;
import nl.vu.cs.s2group.nappa.plugin.util.InstrumentUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This class pertains to the parsing of the android manifest files for Activities
 * and also setting up the Pre-fetching library package imports for usage on the application/ Project.
 * Furthermore injects the Prefetch.init code to the project in order to initialize the prefetching
 * library
 */
public class InstrumentActivityAction extends AnAction {
    private Project project;
    private InstrumentResultMessage resultMessage;

    /**
     * This Action is responsible for initializing the Prefetching Library in the main launcher
     * {@link android.app.Activity} and to inject navigation probes in all {@link android.app.Activity}.
     * <br/><br/>
     *
     * @param e {@inheritDoc}
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project = e.getProject();
        resultMessage = new InstrumentResultMessage();

        try {
            getAllJavaFilesWithAnActivity().forEach((activityName, isMainLauncherActivity) -> {
                PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, activityName + ".java", GlobalSearchScope.projectScope(project));
                for (PsiFile psiFile : psiFiles) {
                    resultMessage.incrementPossibleInstrumentationCount();
                    PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                    InstrumentUtil.addLibraryImport(project, psiJavaFile);
                    injectNavigationProbes(psiJavaFile);
                    if (Boolean.TRUE.equals(isMainLauncherActivity)) {
                        resultMessage.incrementPossibleInstrumentationCount();
                        addLibraryInitializationStatement(psiJavaFile);
                    }
                }
            });
            resultMessage.showResultDialog(project, "Navigation Probes Instrumentation Result");
        } catch (Exception exception) {
            resultMessage.showErrorDialog(project, exception, "Failed to Instrument Navigation Probes");
        }
    }

    /**
     * This method finds the {@code onResume()} method implemented in an {@link android.app.Activity} and
     * insert an instrumented text to add the navigation probes.There are three instrumentation cases for
     * injecting the navigation probes.
     * <br/><br/>git che
     *
     * <p> Case 1. The {@link android.app.Activity} don't have the method {@code onResume()}. In this case,
     * the method is injected containing the super constructor and the navigation probe. The injected code
     * is as follows:
     *
     * <pre>{@code
     * @Override
     * protected void onResume() {
     *     super.onResume();
     *     PrefetchingLib.setCurrentActivity(this);
     * }
     * }</pre>
     *
     * <p> Case 2. The {@link android.app.Activity} has a method {@code onResume()} with existing source
     * code. In this case, the navigation probe is inserted at the top of the method {@code onResume()},
     * after invoking the super constructor, if it present, or before the first statement in the method.
     * The injected code is as follows:
     *
     * <pre>{@code PrefetchingLib.setCurrentActivity(this);}</pre>
     *
     * <p> Case 3. The {@link android.app.Activity} has an empty method {@code onResume()}. In this case,
     * the super constructor is injected together with the navigation probe. The injected code is as follows:
     *
     * <pre>{@code
     * super.onResume();
     * PrefetchingLib.setCurrentActivity(this);
     * }</pre>
     *
     * @param javaFile The Java file containing the an {@link android.app.Activity}
     */
    private void injectNavigationProbes(@NotNull PsiJavaFile javaFile) {
        String instrumentedText = "PrefetchingLib.setCurrentActivity(this);";
        PsiClass[] psiClasses = javaFile.getClasses();
        for (PsiClass psiClass : psiClasses) {
            // There is only one initialization per app
            if (psiClass.getText().contains(instrumentedText)) {
                resultMessage.incrementAlreadyInstrumentedCount();
                break;
            }

            // The library must be initialized only in the file main class
            if (!InstrumentUtil.isMainPublicClass(psiClass)) continue;

            // There are three cases to inject a navigation probe
            PsiMethod[] psiMethods = psiClass.findMethodsByName("onResume", false);
            // Case 1. There is no method "onResume"
            if (psiMethods.length == 0) injectNavigationProbesWithoutOnResumeMethod(psiClass, instrumentedText);
            else {
                PsiCodeBlock psiBody = psiMethods[0].getBody();
                // Case 2. There is a method "onResume" and it an empty body
                // Only interfaces and abstracts methods don't have a body.
                // The method "onResume" will always have a body.
                // noinspection ConstantConditions
                if (psiBody.getStatements().length == 0)
                    injectNavigationProbesWithEmptyOnResumeMethod(psiClass, psiBody, instrumentedText);
                    // Case 3. There is a method "onResume" and it has a non-empty body
                else
                    injectNavigationProbesWithNonEmptyOnResumeMethod(psiClass, psiBody, instrumentedText);

                resultMessage.incrementInstrumentationCount()
                        .appendPsiClass(psiClass)
                        .appendPsiMethod(psiMethods[0])
                        .appendNewBlock();
            }

        }
    }

    /**
     * Inject the navigation probe to the method {@code onResume} with empty body to the class
     *
     * @param psiClass         Represents a Java class.
     * @param psiBody          Represents the body of the method {@code onResume} found in the class
     * @param instrumentedText Represents the source code to inject
     */
    private void injectNavigationProbesWithEmptyOnResumeMethod(PsiClass psiClass, PsiCodeBlock psiBody, String instrumentedText) {
        PsiElement instrumentedElement = PsiElementFactory
                .getInstance(project)
                .createStatementFromText("" +
                        "super.onResume();\n" +
                        instrumentedText, psiClass);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            psiBody.add(instrumentedElement);
        });
    }

    /**
     * Inject the navigation probe to the method {@code onResume} containing existing code to the class
     *
     * @param psiClass         Represents a Java class.
     * @param psiBody          Represents the body of the method {@code onResume} found in the class
     * @param instrumentedText Represents the source code to inject
     */
    private void injectNavigationProbesWithNonEmptyOnResumeMethod(PsiClass psiClass, @NotNull PsiCodeBlock psiBody, String instrumentedText) {
        // If there is a super constructor invocation, is must be in the first line of the method
        PsiStatement firstStatement = psiBody.getStatements()[0];
        boolean isSuperOnResume = firstStatement.getText().contains("super.onResume(");

        PsiElement instrumentedElement = PsiElementFactory
                .getInstance(project)
                .createStatementFromText(instrumentedText, psiClass);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (isSuperOnResume) psiBody.addAfter(instrumentedElement, firstStatement);
            else psiBody.addBefore(instrumentedElement, firstStatement);
        });
    }

    /**
     * Inject a {@code onResume} method with the navigation probes to the class
     *
     * @param psiClass         Represents a Java class.
     * @param instrumentedText Represents the source code to inject
     */
    private void injectNavigationProbesWithoutOnResumeMethod(PsiClass psiClass, String instrumentedText) {
        PsiMethod instrumentedElement = PsiElementFactory
                .getInstance(project)
                .createMethodFromText("" +
                        "@Override\n" +
                        "protected void onResume() {\n" +
                        "super.onResume();\n" +
                        instrumentedText + "\n" +
                        "}", psiClass);

        resultMessage.incrementInstrumentationCount()
                .appendPsiClass(psiClass)
                .appendOverridePsiMethod(instrumentedElement)
                .appendNewBlock();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            psiClass.add(instrumentedElement);
        });
    }


    /**
     * This method finds the {@code onCreate()} method implemented in the main launcher
     * {@link android.app.Activity} and insert an instrumented text containing the Prefetching Library
     * initialization with the default Greedy Prefetching Strategy
     * <br/><br/>
     *
     * <p> The initialization is inserted at the top of the {@code onCreate()} method, after
     * invoking the super constructor, if present, or before the first statement in the method.
     * <br/><br/>
     *
     * <p> The following source code is instrumented:
     *
     * <pre>{@code Prefetch.init(this, PrefetchingStrategy.STRATEGY_GREEDY);}</pre>
     *
     * @param javaFile The Java file containing the main launcher {@link android.app.Activity}
     */
    private void addLibraryInitializationStatement(@NotNull PsiJavaFile javaFile) {
        String instrumentedText = "PrefetchingLib.init(this, PrefetchingStrategy.STRATEGY_GREEDY);";
        PsiClass[] psiClasses = javaFile.getClasses();

        for (PsiClass psiClass : psiClasses) {
            // There is only one initialization per app
            if (psiClass.getText().contains(instrumentedText)) {
                resultMessage.incrementAlreadyInstrumentedCount();
                break;
            }

            // The library must be initialized only in the file main class
            if (!InstrumentUtil.isMainPublicClass(psiClass)) continue;

            // There should be exactly a single method named "onCreate" and it should not be empty
            PsiMethod[] psiMethods = psiClass.findMethodsByName("onCreate", false);
            if (psiMethods.length == 0) break;
            PsiCodeBlock psiBody = psiMethods[0].getBody();
            if (psiBody == null) break;

            // If there is a super constructor invocation, is must be in the first line of the method
            PsiStatement firstStatement = psiBody.getStatements()[0];
            boolean isSuperOnCreate = firstStatement.getText().contains("super.onCreate(");

            // This is the Element which contains the statement to connect the
            // Android application's Main activity to the NAPPA Prefetching Library.
            // Essentially, we add a statement which initializes Nappa at the very beginning
            // of the application launch
            PsiElement instrumentedElement = PsiElementFactory
                    .getInstance(project)
                    .createStatementFromText(instrumentedText, psiClass);

            resultMessage.incrementInstrumentationCount()
                    .appendPsiClass(psiClass)
                    .appendPsiMethod(psiMethods[0])
                    .appendNewBlock();

            WriteCommandAction.runWriteCommandAction(project, () -> {
                if (isSuperOnCreate) psiBody.addAfter(instrumentedElement, firstStatement);
                else psiBody.addBefore(instrumentedElement, firstStatement);
            });
        }
    }

    /**
     * Identify all Java classes that are child of the class {@link android.app.Activity} by scanning
     * all AndroidManifest files within a project. This method also identifies the main launcher activity.
     *
     * @return A map of java file names to a flag indicating if this file contains the main launcher activity
     */
    @NotNull
    private Map<String, Boolean> getAllJavaFilesWithAnActivity() {
        Map<String, Boolean> javaFiles = new HashMap<>();
        PsiFile[] androidManifestFiles = FilenameIndex.getFilesByName(project, "AndroidManifest.xml", GlobalSearchScope.projectScope(project));

        // Navigate tags until you reach the Activity Tags according to the following hierarchy
        //  Manifest -> application -> activity
        for (PsiFile psiFile : androidManifestFiles) {
            XmlFile androidManifestFile = (XmlFile) psiFile;
            XmlTag rootTag = androidManifestFile.getRootTag();

            if (rootTag == null) continue;
            XmlTag applicationTag = rootTag.findFirstSubTag("application");

            if (applicationTag == null) continue;
            XmlTag[] activityTags = applicationTag.findSubTags("activity");

            for (XmlTag activityTag : activityTags) {
                XmlAttribute tagAndroidName = activityTag.getAttribute("android:name");
                if (tagAndroidName == null) continue;
                String activityName = tagAndroidName.getValue();
                if (activityName == null) continue;

                // Fetch the java resource file corresponding to the activity name
                activityName = activityName.substring(activityName.lastIndexOf(".") + 1);
                boolean isMainLauncherActivity = activityTag.getText().contains("android.intent.action.MAIN") &&
                        activityTag.getText().contains("android.intent.category.LAUNCHER");
                javaFiles.put(activityName, isMainLauncherActivity);
            }
        }
        return javaFiles;
    }
}
