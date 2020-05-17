package util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * An class containing common utility methods to simplify the instrumentation actions
 */
public final class InstrumentationUtil {

    /**
     * Scan the project directory for all source files to search for all Java source files
     *
     * @param project An object representing an IntelliJ project.
     *
     * @return A list of all Java source files in the project
     */
    public static @NotNull List<PsiFile> getAllJavaFilesInProjectAsPsi(Project project) {
        List<PsiFile> psiFiles = new LinkedList<>();
        String[] fileNames = FilenameIndex.getAllFilenames(project);

        fileNames = Arrays.stream(fileNames)
                .filter(fileName -> fileName.contains(".java"))
                .toArray(String[]::new);

        for (String fileName : fileNames) {
            psiFiles.addAll(Arrays.asList(FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project))));
        }

        return psiFiles;
    }
}
