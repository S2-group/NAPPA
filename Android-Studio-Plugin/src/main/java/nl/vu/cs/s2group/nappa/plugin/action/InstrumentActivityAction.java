package nl.vu.cs.s2group.nappa.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.LinkedList;
import java.util.List;

/**
 *  This class pertains to the parsing of the android manifest files for Activities
 *  and also setting up the Pre-fetching library package imports for usage on the application/ Project.
 *  Furthermore injects the Prefetch.init code to the project in order to initialize the prefetching
 *  library
 */
public class InstrumentActivityAction extends AnAction {

    private String cat  = "okfile";
    private List<String> javaActivityNameList = new LinkedList<>();
    private Project project;

    //PrefetchingLib.init(this);



    /**
     * This detects the activitis from the manifest file and also initializes the prefetching library.
     *
     * @param listpsi: A list containing references to all the AndroidManifest Files found within a project
     */
    private void populateActivityNameListFromManifestAndInitLibrary(PsiFile[] listpsi) {
        javaActivityNameList.clear();

        // Iterate through all Manifest Files detected within the project
        for (PsiFile file1 : listpsi) {
            cat += "\n"+file1.getVirtualFile().getPath()+"\n";
            PsiElement psiElement = file1.findElementAt(0);

            // Treat the Android.Xml file as an XML structure
            XmlFile xmlFile = (XmlFile) file1;
            try {

                // Navigate tags until you reach the Activity Tags according to the following hierarchy
                //  (Manifest ) -> (application) -> activity
                String package_name = xmlFile.getRootTag().getAttribute("package").getValue();

                XmlTag applicationTag = xmlFile.getRootTag().findFirstSubTag("application");

                XmlTag[] activityTags = applicationTag.findSubTags("activity");

                // Iterate through each activity tag
                for (XmlTag tag : activityTags) {
                    // Add the activity name as defined in the manifest
                    cat += "\n"+tag.getAttribute("android:name").getValue();
                    try {
                        // Fetch the java resource file corresponding to the activity name
                        String[] names = tag.getAttribute("android:name").getValue().split("\\.");
                        String name = names[names.length - 1] + ".java";
                        javaActivityNameList.add(name);

                        // Append the related filename
                        cat += "    " + name;

                        //To determine whether this activity is the main activity and the launcher activity
                        boolean isMain = false;
                        boolean isLauncher = false;

                        // Find the intent filter tag in order to be able to DETERMINE whether
                        //      this activity is the main activity AND/OR the launcher activity
                        XmlTag[] intentFilterTags = tag.findSubTags("intent-filter");

                        for (XmlTag intentFilterTag : intentFilterTags) {
                            // Find the relevant Action and category subtags
                            XmlTag[] actionTags = intentFilterTag.findSubTags("action");
                            XmlTag[] categoryTags = intentFilterTag.findSubTags("category");


                            // Update the boolean for a given activity as MAIN activity and LAUNCHER
                            //       activity if this is found to be present for a given activity.
                            //       There can only be one of each
                            for (XmlTag actionTag : actionTags) {
                                if (actionTag.getAttribute("android:name")!=null &&
                                        actionTag.getAttribute("android:name").getValue().compareTo("android.intent.action.MAIN") == 0) {
                                    isMain = true;
                                    break;
                                }
                            }
                            for (XmlTag categoryTag: categoryTags) {
                                if (categoryTag.getAttribute("android:name")!=null &&
                                        categoryTag.getAttribute("android:name").getValue().compareTo("android.intent.category.LAUNCHER") == 0) {
                                    isLauncher = true;
                                    break;
                                }
                            }



                        }

                        cat += name + "\n";
                        cat += "isMain? " + isMain + "\n";
                        cat += "isLauncher? " + isLauncher + "\n";

                        // Consider only the Main activity
                        if (isMain && isLauncher) {
                            // Open the Java File corresponding to the main activity
                            PsiFile[] listActJava = FilenameIndex.getFilesByName(project, name, GlobalSearchScope.projectScope(project));
                            for (PsiFile psiFile : listActJava) {
                                // Open the activity's Java file and extract the set of all the classes within it
                                if (psiFile instanceof PsiJavaFile) {

                                    PsiJavaFile javaFile = (PsiJavaFile) psiFile;

                                    // FIXME:  How do we know that the class the first class defined in the Java file is effectively the class we are looking for?
                                    //   There can only be one public class per java file, given that public classes must have the same name as
                                    //   the resource file.  But there may be more private classes within
                                    PsiClass psiClass = javaFile.getClasses()[0];

                                    // Find the onCreate Callback method
                                    PsiMethod[] psiMethods = psiClass.findMethodsByName("onCreate", false);
                                    if (psiMethods.length > 0) {
                                        try {

                                            /** IMPORTATN:  This is the Element which contatins the statement to connect the
                                             *     Android application's Main activity to the NAPPA Prefetching Library.
                                             *     Essentially, we add a statement which initializes Nappa at the very beginning
                                             *     of the application launch
                                             * */
                                            final PsiElement psiElementToAdd = PsiElementFactory.SERVICE.getInstance(project).createStatementFromText(
                                                    "PrefetchingLib.init(this);", psiClass);

                                            // Get the statements of the onCreate callback
                                            PsiStatement[] psiStatements = psiMethods[0].getBody().getStatements();
                                            PsiElement onCreateElement = null;

                                            boolean addStatement = true;

                                            /**
                                             *  This loop determines whether a reference to the prefetch lib will be
                                             *  added to the main activity or not.
                                             */
                                            for (PsiStatement statement : psiStatements) {
                                                // Find the Super OnCreate Statement and store it as a
                                                //      PsiElement to be used later in order to inject the PreFetch Lib initializer
                                                //      If super.onCreate constructor is never found,  this reference is not used and the
                                                //      injected code is added at the start of the onCreate method
                                                if (statement.getText().startsWith("super.onCreate")) {
                                                    onCreateElement = statement;
                                                }
                                                // Check if the statement to be injected (the injection to
                                                //      link to the prefetch lib) already exists in the
                                                //      project.  ALSO print all the statements within
                                                //      this class
                                                if (statement.isEquivalentTo(psiElementToAdd)) {
                                                    addStatement = false;
                                                    cat += "FOUND IN FIRST ELSE\n";
                                                    //break;
                                                } else if (statement.textMatches(psiElementToAdd)) {
                                                    addStatement = false;
                                                    cat += "FOUND IN SECOND ELSE\n";
                                                    //break;
                                                } else {
                                                    cat += "\n"+statement.getText()+"\n";
                                                }
                                            }

                                            // Keep track of the reference (element) to the super.oncreate
                                            //      statement
                                            final PsiElement onCreateElementFinal = onCreateElement;

                                            /**
                                             * This is where the reference to the prefetching lib is added, assuming
                                             * that the previous loop determines it is necessary to do so.
                                             */
                                            if (addStatement) {
                                                cat += "\nADDING INIT PREFETCHING LIB IN " + name + "\n\n";

                                                // Inject Prefetch Lib to the project
                                                WriteCommandAction.runWriteCommandAction(project, () -> {
                                                    // Super.oncreate constructor found / not found scenarios
                                                    if (onCreateElementFinal != null) {
                                                        psiMethods[0].getBody().addAfter(psiElementToAdd, onCreateElementFinal);
                                                    } else {
                                                        psiMethods[0].getBody().add(psiElementToAdd);
                                                    }
                                                });
                                            } else {
                                                cat += "\nINIT PREFETCHING LIB NOT ADDED in "+name+"\n\n";
                                            }
                                        } catch (Exception e) {
                                            cat += "\n"+e.getMessage()+"\n\n";
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                        }

                    } catch (Exception e2) {
                        cat += "    "+e2.toString();
                    }

                }
            } catch (Exception e1) {
                e1.printStackTrace();
                cat += "  error in tag reading\n";
            }
        }

    }

    private void instrumentActivityFiles() {
        // Iterate through all the java resource files corresponding to each activity in the Android
        //      App.  Usin the name of the files collected in the previous function
        for (String javaActivityName: javaActivityNameList) {
            // Fetch the file in PSI structure
            PsiFile[] listActJava = FilenameIndex.getFilesByName(project, javaActivityName, GlobalSearchScope.projectScope(project));

            /**
             * IMPORTANT: This iterates through every activity's corresponding resouce file in order
             * to instrument each activity to make use of the Prefetching lib.
             */
            for (PsiFile actJava: listActJava) {
                if (actJava instanceof PsiJavaFile) {
                    PsiJavaFile javaFile = (PsiJavaFile) actJava;
                    // FIXME: Assumption of first defined class being the public class
                    PsiClass psiClass = javaFile.getClasses()[0];

                    /** IMPORTATN:  This is the Element which contains the statement to import
                     *      the prefetching lib's package
                     * */
                    final PsiElement importToAdd = PsiElementFactory.SERVICE.getInstance(project).createImportStatementOnDemand(
                            "nl.vu.cs.s2group.nappa");

                    // Fetch all the import statements for this java file
                    PsiImportList importList = javaFile.getImportList();
                    PsiImportStatement[] importStatements = importList.getImportStatements();

                    boolean imported = false;

                    // Check to see that the prefetch lib import has already taken place, to
                    //      avoid duplicates
                    for (PsiImportStatement importStatement : importStatements) {
                        if (importStatement.getText().compareTo("import nl.vu.cs.s2group.nappa*;") == 0) {
                            imported = true;
                            break;
                        }
                    }

                    cat += "\nIMPORT FOUND FOR CLASS " + javaActivityName + ": " + imported + "\n\n";

                    // If the prefetcht lib import has not been found in the import list, add it now
                    if (!imported) {
                        WriteCommandAction.runWriteCommandAction(project, () -> {
                            importList.add(importToAdd);
                        });
                    }

                    // write the full name of the class (inclulding full package description).
                    cat += "   "+psiClass.getQualifiedName()+"   ";
                    // Fetch onResume callbackmethod
                    PsiMethod[] psiMethods = psiClass.findMethodsByName("onResume", false);

                    // Determine whether onResume was found or not
                    cat += "   osResume()  ";
                    cat += psiMethods.length > 0 ? "found\n" : "not found\n";


                    /**
                     * IMPORTANT: This is where the prefetching lib adds the PrefetchingLib transition to
                     *  the next activity.  This is done by instrumenting the onResume Callback.  If the
                     *  callback is missing, then the callback is added to the activity.
                     */
                    if (psiMethods.length > 0) {

                        try {
                            // Determine if On Resume is writable
                            cat += psiMethods[0].getBody().isWritable()? "writable" : "not writable";
                            cat += "\n";

                            // THis is the statement added to instrument intent transitions
                            final PsiElement psiElementToAdd = PsiElementFactory.SERVICE.getInstance(project).createStatementFromText(
                                    //        "System.out.println(\"ciao amico\");", psiClass);
                                    "PrefetchingLib.setCurrentActivity(this);", psiClass);


                            PsiStatement[] psiStatements = psiMethods[0].getBody().getStatements();
                            boolean addStatement = true;

                            for (PsiStatement statement : psiStatements) {
                                if (statement.isEquivalentTo(psiElementToAdd)) {
                                    addStatement = false;
                                    break;
                                } else if (statement.textMatches(psiElementToAdd)) {
                                    addStatement = false;
                                    break;
                                } else {
                                    cat += "\n"+statement.getText()+"\n\n";
                                }
                            }

                            if (addStatement) {
                                WriteCommandAction.runWriteCommandAction(project, () -> {
                                    psiMethods[0].getBody().add(
                                            //PsiElementFactory.SERVICE.getInstance(project).createExpressionFromText("System.out.println(\"ciao amico\");", psiClass)
                                            //PsiElementFactory.SERVICE.getInstance(project).createStatementFromText("System.out.println(\"ciao amico\");", psiClass)
                                            psiElementToAdd
                                    );
                                });
                            } else {
                                cat += "\nNOT ADDED in "+javaActivityName+"\n\n";
                            }

                        } catch (Exception e3) {
                            cat += e3.toString()+"\n";
                        }

                    } else { //NO onResume METHOD FOUND
                        WriteCommandAction.runWriteCommandAction(project, () -> {
                            psiClass.add(
                                    PsiElementFactory.SERVICE.getInstance(project).createMethodFromText("@Override\n" +
                                            "protected void onResume(){\n" +
                                            "super.onResume();\n" +
                                            "PrefetchingLib.setCurrentActivity(this);\n" +
                                            "}", psiClass)
                            );
                        });
                    }

                }
            }
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Open the specific project
        project = e.getProject();

        // OPEN the Android Manifest File(s), and process each one of them in order to instrument them (project level)
        PsiFile[] listpsi = FilenameIndex.getFilesByName(project, "AndroidManifest.xml", GlobalSearchScope.projectScope(project));

        /**** Activity Instrumentation begins here ***/

        populateActivityNameListFromManifestAndInitLibrary(listpsi);
        instrumentActivityFiles();

        // This should print "Hello" + "okFile" + <<File Path>> + <<Extension Dialogue>>
        //    The actual output from the plug in is generated from
        //    populateActivityNameListFromManifestAndInitLibrary(listpsi);
        Messages.showMessageDialog("Hello\t"+cat, "World", Messages.getInformationIcon());

    }
}
