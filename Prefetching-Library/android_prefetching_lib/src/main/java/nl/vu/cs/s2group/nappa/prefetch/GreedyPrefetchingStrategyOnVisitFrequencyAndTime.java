package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.util.NappaUtil;

/**
 * This strategy employs a Greedy approach using the time a user spends in the activities
 * and the how frequent the user access the activities to decide which nodes to select.
 * This strategy runs recursively on the children of the current most probable node.
 * Only a single child is select per recursion.
 * <p>
 * This strategy accepts the following configurations:
 * <ul>
 *     <li>{@link PrefetchingStrategyConfigKeys#WEIGHT_FREQUENCY_SCORE}</li>
 *     <li>{@link PrefetchingStrategyConfigKeys#WEIGHT_TIME_SCORE}</li>
 * </ul>
 */
public class GreedyPrefetchingStrategyOnVisitFrequencyAndTime extends AbstractPrefetchingStrategy implements PrefetchingStrategy {
    private static final String LOG_TAG = GreedyPrefetchingStrategyOnVisitFrequencyAndTime.class.getSimpleName();

    private static final float DEFAULT_WEIGHT_FREQUENCY_SCORE = 0.5f;
    private static final float DEFAULT_WEIGHT_TIME_SCORE = 0.5f;

    protected final float weightFrequencyScore;
    protected final float weightTimeScore;

    public GreedyPrefetchingStrategyOnVisitFrequencyAndTime(Map<PrefetchingStrategyConfigKeys, Object> config) {
        super(config);
        Object data;

        data = config.get(PrefetchingStrategyConfigKeys.WEIGHT_FREQUENCY_SCORE);
        weightFrequencyScore = data != null ? Float.parseFloat(data.toString()) : DEFAULT_WEIGHT_FREQUENCY_SCORE;

        data = config.get(PrefetchingStrategyConfigKeys.WEIGHT_TIME_SCORE);
        weightTimeScore = data != null ? Float.parseFloat(data.toString()) : DEFAULT_WEIGHT_TIME_SCORE;

        if ((weightFrequencyScore + weightTimeScore) != 1.0)
            throw new IllegalArgumentException("The sum of the time and frequency weight must be 1!");
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(@NonNull ActivityNode node, Integer maxNumber) {
        Log.d(LOG_TAG, node.activityName + " searching best successors");
        return getTopNUrlToPrefetchForNode(node, 1, new ArrayList<>());
    }

    /**
     * Auxiliary method to recursively find the best successor. This method works in three stages.
     * <p>
     * The first stage is to find the successor of the current node with the best score.
     * If no successor is found or if the score of the best successor is insufficient
     * (i.e., the calculated score is lower than the lower bound threshold), then the recursion
     * stops and the current list of URLs is returned.
     * <p>
     * If the score is higher, then the second stage starts. In this stage the method verifies
     * if there is sufficient budget (i.e., number of URLs) to add all URLs of the best successor.
     * In the case there isn't, only a subset of the URLs of the best successor is added to the URL
     * list.
     * <p>
     * In the third stage the method verifies whether to continue the recursion or return the
     * current URL list. If the URL budget is completely filled, then the URL list is returned,
     * otherwise, the recursion continues by invoking this method with the best successor object,
     * score and current URL list
     *
     * @param node        The current node in the recursion. Either the node that started the
     *                    recursion or of of its descendant
     * @param parentScore The score of the parent node.
     * @param urlList     The list of URLs to prefetch
     * @return The {@code urlList} after completing all recursions
     */
    private List<String> getTopNUrlToPrefetchForNode(@NonNull ActivityNode node, float parentScore, List<String> urlList) {
        // TODO Fix these items to complete the strategy implementation:
        //  Use approach A (i.e., last N actual sessions)
        //  Update the visit time to accept empty rows (in the observer?)
        //  Address division by zero in this method

        // Fetches the data to start the calculations - Aggregate visit time and frequency
        float totalAggregateTime = NappaUtil.getSuccessorsAggregateVisitTime(node);
        int totalAggregateFrequency = NappaUtil.getSuccessorsAggregateVisitFrequency(node, lastNSessions);

        // Temporary variables to find the best successor
        ActivityNode bestSuccessor = null;
        float bestSuccessorScore = 0;

        // Loop all successors and saves the successor with the best score. In case of drawn, the
        //  first current best successor is picked
        for (ActivityNode successor : node.successors.keySet()) {
            int successorFrequency = NappaUtil.getSuccessorAggregateVisitFrequency(successor, lastNSessions);
            float successorTime = successor.getAggregateVisitTime().totalDuration;

            float successorTimeScore = totalAggregateTime == 0 ? 0 : (successorTime / totalAggregateTime * weightTimeScore);
            float successorFrequencyScore = totalAggregateFrequency == 0 ? 0 : ((float) successorFrequency / totalAggregateFrequency * weightFrequencyScore);

            float successorScore = parentScore * (successorTimeScore + successorFrequencyScore);

            if (bestSuccessor == null || successorScore > bestSuccessorScore) {
                bestSuccessor = successor;
                bestSuccessorScore = successorScore;
            }
        }

        // Verifies if a best successor wa found and if its score is sufficient
        if (bestSuccessor == null || bestSuccessorScore < scoreLowerThreshold) return urlList;

        // Fetches the URLs from the bestSuccessor and the remaining URL budget
        List<String> bestSuccessorUrls = NappaUtil.getUrlsFromCandidateNode(node, bestSuccessor);
        int remainingUrlBudget = maxNumberOfUrlToPrefetch - urlList.size();

        Log.d(LOG_TAG, node.activityName +
                " best successor is " + bestSuccessor.activityName +
                " with score " + bestSuccessorScore +
                " containing the URLS " + bestSuccessorUrls);

        // Verifies if the URL list of the best successor fits the budget.
        // If not, take only the first N URLs until using all budget
        if (bestSuccessorUrls.size() > remainingUrlBudget) {
            bestSuccessorUrls = bestSuccessorUrls.subList(0, remainingUrlBudget);
        }

        // Add the remaining URLs to the list of URLs to prefetch
        urlList.addAll(bestSuccessorUrls);

        // Verifies if there is any URL budget left
        if (urlList.size() >= maxNumberOfUrlToPrefetch) return urlList;
        return getTopNUrlToPrefetchForNode(bestSuccessor, bestSuccessorScore, bestSuccessorUrls);
    }
}
