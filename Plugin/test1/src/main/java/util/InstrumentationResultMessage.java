package util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiStatement;
import org.jetbrains.annotations.NotNull;

public class InstrumentationResultMessage {
    private final StringBuilder builder;

    /**
     * Count of statements instrumented in this run
     */
    private int instrumentationCount;

    /**
     * Count of statements that can be instrumented in this run
     */
    private int possibleInstrumentationCount;

    /**
     * Count of statements that were instrumented in previous runs
     */
    private int alreadyInstrumentedCount;

    public InstrumentationResultMessage() {
        builder = new StringBuilder();
        instrumentationCount = 0;
        possibleInstrumentationCount = 0;
        alreadyInstrumentedCount = 0;
    }

    /**
     * Returns the text of the result message.
     *
     * @return the result message
     */
    public String getMessage() {
        addInstrumentationOverview();
        return builder.toString();
    }

    /**
     * Increment by 1 the count of statement instrumented in this run
     *
     * @return A instance of this object
     */
    public InstrumentationResultMessage incrementInstrumentationCount() {
        instrumentationCount++;
        return this;
    }

    /**
     * Increment by 1 the count of statement that can be instrumented
     *
     * @return A instance of this object
     */
    public InstrumentationResultMessage incrementPossibleInstrumentationCount() {
        possibleInstrumentationCount++;
        return this;
    }

    /**
     * Increment by 1 the count of statement that were already instrumented
     *
     * @return A instance of this object
     */
    public InstrumentationResultMessage incrementAlreadyInstrumentedCount() {
        alreadyInstrumentedCount++;
        return this;
    }

    /**
     * Insert a overview of the instrumentation in the begging of the result message
     */
    private void addInstrumentationOverview() {
        StringBuilder message = new StringBuilder();

        message.append(possibleInstrumentationCount)
                .append(" statements can be instrumented.")
                .append("\n")
                .append(instrumentationCount)
                .append(" statements were instrumented in this run.")
                .append("\n")
                .append(alreadyInstrumentedCount)
                .append(" statements were already instrumented.")
                .append("\n\n");

        builder.insert(0, message);
    }

    /**
     * Append the class or interface qualified name to the result message
     *
     * @param psiClass A Java class or interface.
     * @return A instance of this object
     */
    public InstrumentationResultMessage appendPsiClass(@NotNull PsiClass psiClass) {
        builder.append("Class: ").append(psiClass.getQualifiedName()).append("\n");
        return this;
    }

    /**
     * Append the statement text to the result message
     *
     * @param psiStatement A Java statement.
     * @return A instance of this object
     */
    public InstrumentationResultMessage appendPsiStatement(@NotNull PsiStatement psiStatement) {
        builder.append("Statement: ").append(psiStatement.getText()).append("\n");
        return this;
    }

    /**
     * Append a block separator to the result message
     *
     * @return A instance of this object
     */
    public InstrumentationResultMessage appendNewBlock() {
        builder.append("\n");
        return this;
    }
}
