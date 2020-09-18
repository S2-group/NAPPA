package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /*
     * The Extras and Navigation monitors triggers the strategy at similar time and might conflict
     * with each other. This map is a quick fix to ensure that each triggered prefetcher modifies
     * only its data. This will add a few milliseconds to the total duration, but will keep the run
     * consistent.
     */
    private Map<Integer, List<String>> visitedNodes;
    private Map<Integer, List<String>> selectedUrls;
    private int executionNumber = 0;

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

        visitedNodes = new HashMap<>();
        selectedUrls = new HashMap<>();
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(@NonNull ActivityNode node, Integer maxNumber) {
        long startTime = System.currentTimeMillis();

        executionNumber++;
        int key = executionNumber;
        Log.d(LOG_TAG, String.format("(#%d) Starting execution %d for node '%s'.", key, key, node.activityName));

        visitedNodes.put(key, new ArrayList<>());
        selectedUrls.put(key, new ArrayList<>());

        getTopNUrlToPrefetchForNode(node, 1, key);
        List<String> urls = selectedUrls.get(key);

        selectedUrls.remove(key);
        visitedNodes.remove(key);

        logStrategyExecutionDuration(node, startTime, key);
        //noinspection ConstantConditions
        return urls;
    }

    /**
     * Auxiliary method to recursively find the best successor. This method works in three stages.
     * <p>
     * The first stage is to find the successor of the current node with the best score.
     * If no successor is found or if the score of the best successor is insufficient
     * (i.e., the calculated score is lower than the lower bound threshold), then the recursion
     * stops.
     * <p>
     * If the score is higher, then the second stage starts. In this stage the method verifies
     * if there is sufficient budget (i.e., number of URLs) to add all URLs of the best successor.
     * In the case there isn't, only a subset of the URLs of the best successor is added to the URL
     * list.
     * <p>
     * In the third stage the method verifies whether to continue the recursion or return the
     * current URL list. If the URL budget is completely filled, then the URL list is returned,
     * otherwise, the recursion continues by invoking this method with the best successor object
     * and its score.
     *
     * @param node        The current node in the recursion. Either the node that started the
     *                    recursion or of of its descendant
     * @param parentScore The score of the parent node.
     * @param key         An ID representing the visited nodes and selected URLs entries for this
     *                    execution
     */
    private void getTopNUrlToPrefetchForNode(@NonNull ActivityNode node, float parentScore, int key) {
        // Verifies if this node was already been visited -- recursion found a loop
        if (isNodeVisited(key, node.activityName)) return;
        addVisitedNode(key, node.activityName);

        if (node.successors.isEmpty()) return;

        // Temporary variables to find the best successor
        ActivityNode bestSuccessor = null;
        float bestSuccessorScore = 0;

        /*
         * If the current node has only one successor, then the successor score will be always equal
         * to the parent score. This means we don't need to run the calculations and fetch the
         * aggregate spent time and visit frequency. To ensure that the scores get lower and lower
         * as we traverse deeper in the graph, we take only 90% of the parent score for the next
         * recursion. This percentage was arbitrarily selected.
         */
        if (node.successors.size() == 1) {
            bestSuccessor = node.successors.keySet().iterator().next();
            bestSuccessorScore = parentScore * 0.9f;
        } else {
            // Fetches the data to start the calculations
            // Represents the total aggregate time spent visiting all successors from current node
            float totalAggregateTime = NappaUtil.getSuccessorsAggregateVisitTime(node);

            // Represents the total aggregate visit frequency from all successors from current node
            int totalAggregateFrequency = NappaUtil.getSuccessorsTotalAggregateVisitFrequency(node, lastNSessions);

            // Represents a map with <activity name, visit frequency> for each successor
            Map<String, Integer> successorsAggregateFrequencyMap = NappaUtil.mapSuccessorsAggregateVisitFrequency(node, lastNSessions);

            /*
             * Loop all successors and saves the successor with the best score. In case of drawn,
             * the current best successor is picked
             */
            for (ActivityNode successor : node.successors.keySet()) {
                float successorTime = successor.getAggregateVisitTime().totalDuration;
                Integer successorFrequency = successorsAggregateFrequencyMap.get(successor.activityName);
                if (successorFrequency == null) {
                    Log.w(LOG_TAG, String.format("(#%d) Unknown visit frequency count for node '%s'. ", key, successor.activityName));
                    successorFrequency = 0;
                }

                float successorTimeScore = totalAggregateTime == 0 ? 0 : (successorTime / totalAggregateTime) * weightTimeScore;
                float successorFrequencyScore = totalAggregateFrequency == 0 ? 0 : ((float) successorFrequency / totalAggregateFrequency) * weightFrequencyScore;

                float successorScore = parentScore * (successorTimeScore + successorFrequencyScore);

                if (bestSuccessor == null || successorScore > bestSuccessorScore) {
                    bestSuccessor = successor;
                    bestSuccessorScore = successorScore;
                }
            }
        }

        // Verifies if this node has any successor. If it has, verifies if the successor with the best score has a score high enough
        if (bestSuccessor == null || bestSuccessorScore < scoreLowerThreshold) return;

        // Fetches the URLs from the bestSuccessor and the remaining URL budget
        List<String> urls = selectedUrls.get(key);
        //noinspection ConstantConditions
        int remainingUrlBudget = maxNumberOfUrlToPrefetch - urls.size();
        List<String> bestSuccessorUrls = NappaUtil.getUrlsFromCandidateNode(node, bestSuccessor, remainingUrlBudget);

        Log.d(LOG_TAG, String.format("(#%d) The best successor for activity '%s' is node '%s' with a score of %f and the following %d URLS: %s",
                key,
                node.activityName,
                bestSuccessor.activityName,
                bestSuccessorScore,
                bestSuccessorUrls.size(),
                bestSuccessorUrls
        ));

        // Add the remaining URLs to the list of URLs to prefetch
        urls.addAll(bestSuccessorUrls);
        selectedUrls.put(key, urls);

        // Verifies if there is any URL budget left
        if (urls.size() >= maxNumberOfUrlToPrefetch) return;
        getTopNUrlToPrefetchForNode(bestSuccessor, bestSuccessorScore, key);
    }

    private void addVisitedNode(int key, String nodeName) {
        List<String> list = visitedNodes.get(key);
        //noinspection ConstantConditions
        list.add(nodeName);
        visitedNodes.put(key, list);
    }

    private boolean isNodeVisited(int key, String nodeName) {
        //noinspection ConstantConditions
        return visitedNodes.get(key).contains(nodeName);
    }
}
