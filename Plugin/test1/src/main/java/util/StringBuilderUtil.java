package util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiStatement;
import org.jetbrains.annotations.NotNull;

public class StringBuilderUtil {

    /**
     * Insert how many expressions were instrumented at the begging of a result message
     *
     * @param builder         Contains the message to display as result from a instrumentation
     * @param instrumentCount The total number of expressions found for instrumentation
     * @return The original message prefixed by the number of expressions instrumented
     */
    public static @NotNull StringBuilder addInstrumentCount(@NotNull StringBuilder builder, int instrumentCount) {
        String message;
        if (instrumentCount == 0) message = "No new expressions found for instrumentation.\n";
        else message = "Found " + instrumentCount + " expressions to instrument.\n\n";
        return builder.insert(0, message);
    }

    /**
     * Append information to identify the class to the original message
     *
     * @param builder Contains the message to display as result from a instrumentation
     * @param psiClass A Java class or interface.
     * @return The original message appended with the class qualified name
     */
    public static @NotNull StringBuilder appendPsiClass(@NotNull StringBuilder builder, @NotNull PsiClass psiClass) {
        return builder.append("Class: ").append(psiClass.getQualifiedName()).append("\n");
    }

    /**
     * Append information to identify the statement to the original message
     *
     * @param builder Contains the message to display as result from a instrumentation
     * @param psiStatement A Java statement.
     * @return The original message appended with the statement
     */
    public static @NotNull StringBuilder appendPsiStatement(@NotNull StringBuilder builder, @NotNull PsiStatement psiStatement) {
        return builder.append("Statement: ").append(psiStatement.getText()).append("\n");
    }
}
