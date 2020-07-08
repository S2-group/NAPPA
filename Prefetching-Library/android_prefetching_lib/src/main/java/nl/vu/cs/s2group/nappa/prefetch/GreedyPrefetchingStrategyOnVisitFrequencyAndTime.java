package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.util.NappaConfigMap;
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
public class GreedyPrefetchingStrategyOnVisitFrequencyAndTime extends AbstractPrefetchingStrategy {
    private static final String LOG_TAG = GreedyPrefetchingStrategyOnVisitFrequencyAndTime.class.getSimpleName();

    private static final float DEFAULT_WEIGHT_FREQUENCY_SCORE = 0.5f;
    private static final float DEFAULT_WEIGHT_TIME_SCORE = 0.5f;

    protected final float weightFrequencyScore;
    protected final float weightTimeScore;

    @Override
    public boolean needVisitTime() {
        return true;
    }

    public GreedyPrefetchingStrategyOnVisitFrequencyAndTime() {
        super();

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
    public List<String> getTopNUrlToPrefetchForNode(@NonNull ActivityNode node, Integer maxNumber) {
        long startTime = new Date().getTime();

        List<String> urls = getTopNUrlToPrefetchForNode(node, 1, new ArrayList<>());
        logStrategyExecutionDuration(node, startTime);

        return urls;
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
        // Fetches the data to start the calculations - Aggregate visit time and frequency
        float totalAggregateTime = NappaUtil.getSuccessorsAggregateVisitTime(node);
        int totalAggregateFrequency = NappaUtil.getSuccessorsTotalAggregateVisitFrequency(node, lastNSessions);
        Map<String, Integer> successorsAggregateFrequencyMap = NappaUtil.mapSuccessorsAggregateVisitFrequency(node, lastNSessions);
        // Temporary variables to find the best successor
        ActivityNode bestSuccessor = null;
        float bestSuccessorScore = 0;

        // Loop all successors and saves the successor with the best score. In case of drawn, the
        //  first current best successor is picked
        for (ActivityNode successor : node.successors.keySet()) {
            Integer successorFrequency = successorsAggregateFrequencyMap.get(successor.activityName);
            if (successorFrequency == null)
                throw new NoSuchElementException("Unable to obtain the successor frequency count!");
            float successorTime = successor.getAggregateVisitTime().totalDuration;

            float successorTimeScore = totalAggregateTime == 0 ? 0 : (successorTime / totalAggregateTime * weightTimeScore);
            float successorFrequencyScore = totalAggregateFrequency == 0 ? 0 : ((float) successorFrequency / totalAggregateFrequency * weightFrequencyScore);

            float successorScore = parentScore * (successorTimeScore + successorFrequencyScore);

            if (bestSuccessor == null || successorScore > bestSuccessorScore) {
                bestSuccessor = successor;
                bestSuccessorScore = successorScore;
            }
        }

        // Verifies if this node has any successor. If it has, verifies if the successor with the best score has a score high enough
        if (bestSuccessor == null || bestSuccessorScore < scoreLowerThreshold) return urlList;

        // Fetches the URLs from the bestSuccessor and the remaining URL budget
        int remainingUrlBudget = maxNumberOfUrlToPrefetch - urlList.size();
        List<String> bestSuccessorUrls = NappaUtil.getUrlsFromCandidateNode(node, bestSuccessor, remainingUrlBudget);

        Log.d(LOG_TAG, node.activityName +
                " best successor is " + bestSuccessor.activityName +
                " with score " + bestSuccessorScore +
                " containing the URLS " + bestSuccessorUrls);

        // Add the remaining URLs to the list of URLs to prefetch
        urlList.addAll(bestSuccessorUrls);

        // Verifies if there is any URL budget left
        if (urlList.size() >= maxNumberOfUrlToPrefetch) return urlList;
        return getTopNUrlToPrefetchForNode(bestSuccessor, bestSuccessorScore, urlList);
    }
}
