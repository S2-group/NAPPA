package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.util.NappaConfigMap;

// TODO Unordered list of tasks to complete issue #52
//  * Decide which fields to use in the inner class TFPRNode
//  * Implement subgraph initialization
//  * Implement the page rank calculation
//  * Do we need to keep the previous score or the current score only
//  * Verify if we need the inner class
//  * Decide how many iterations to run
//  * Add a configurable parameter for the number of runs
//  * Decide on which nodes to use for the subgraph --> all successors from current node and all nodes that points to the successors
//  * Add a LiveData list in ActivityNode with the successors aggregate time
//  * Test the Handler idea to initialize this LiveData object
//  * Create utility method to get the total time given the LiveData list

/**
 * This strategy employs a Link Analysis approach implementing a PageRank-based algorithm
 * using the time a user spends in the activities and how frequent the user access the
 * activities to decide which nodes to select.
 * <p>
 * Only a subgraph of the ENG is considered in the calculations. The score calculations are
 * performed at runtime and are not persisted in the database.
 * <p>
 * Strategy inspired on the paper Personalized PageRank for Web Page Prediction Based
 * on Access Time-Length and Frequency from 2007.
 * <p>
 * This strategy accepts the following configurations:
 * <ul>
 *     <li>{@link PrefetchingStrategyConfigKeys#WEIGHT_FREQUENCY_SCORE}</li>
 *     <li>{@link PrefetchingStrategyConfigKeys#WEIGHT_TIME_SCORE}</li>
 * </ul>
 *
 * @see <a href="https://dl.acm.org/doi/10.1109/WI.2007.145">Personalized PageRank paper</a>
 */
public class TFPRPrefetchingStrategy extends AbstractPrefetchingStrategy {
    private static final String LOG_TAG = TFPRPrefetchingStrategy.class.getSimpleName();

    private static final float DEFAULT_WEIGHT_FREQUENCY_SCORE = 0.5f;
    private static final float DEFAULT_WEIGHT_TIME_SCORE = 0.5f;

    protected final float weightFrequencyScore;
    protected final float weightTimeScore;

    @Override
    public boolean needVisitTime() {
        return true;
    }

    public TFPRPrefetchingStrategy(@NonNull Map<PrefetchingStrategyConfigKeys, Object> config) {
        super(config);

        weightFrequencyScore = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.WEIGHT_FREQUENCY_SCORE,
                DEFAULT_WEIGHT_FREQUENCY_SCORE);

        weightTimeScore = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.WEIGHT_TIME_SCORE,
                DEFAULT_WEIGHT_TIME_SCORE);

        if ((weightFrequencyScore + weightTimeScore) != 1.0)
            throw new IllegalArgumentException("The sum of the time and frequency weight must be 1!");
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(@NotNull ActivityNode node, Integer maxNumber) {
        long startTime = new Date().getTime();
        List<String> urlsToPrefetch = new ArrayList<>();

        Log.d(LOG_TAG, node.activityName + " found successors in " + (new Date().getTime() - startTime) + " ms");
        return urlsToPrefetch;
    }

    private List<TFPRNode> runPageRank(List<TFPRNode> graph) {
        return new ArrayList<>();
    }

    private List<TFPRNode> getSubgraph(ActivityNode node) {
        return new ArrayList<>();
    }

    private class TFPRNode {
        ActivityNode node;
        float tfprScore;

    }
}
