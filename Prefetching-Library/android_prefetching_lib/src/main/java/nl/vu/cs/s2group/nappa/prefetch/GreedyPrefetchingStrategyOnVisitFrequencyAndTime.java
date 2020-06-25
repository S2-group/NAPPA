package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.util.NappaUtil;

/**
 * This strategy employs a Greedy approach using the time a user spends in the activities
 * and the how frequent the user access the activities to decide which nodes to select.
 * This strategy runs recursively on the children of the current  most probable node.
 */
public class GreedyPrefetchingStrategyOnVisitFrequencyAndTime extends AbstractPrefetchingStrategy implements PrefetchingStrategy {
    private static final String LOG_TAG = GreedyPrefetchingStrategyOnVisitFrequencyAndTime.class.getSimpleName();
    protected final float scoreLowerThreshold;

    public GreedyPrefetchingStrategyOnVisitFrequencyAndTime(Map<PrefetchingStrategyConfigKeys, Object> config) {
        super(config);
        Object data;

        data = config.get(PrefetchingStrategyConfigKeys.SCORE_LOWER_THRESHOLD);
        scoreLowerThreshold = data != null ? Float.parseFloat(data.toString()) : DEFAULT_SCORE_LOWER_THRESHOLD;
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(@NotNull ActivityNode node, Integer maxNumber) {
        Log.d(LOG_TAG, node.activityName + " searching best successors");
        return getTopNUrlToPrefetchForNode(node, 1, new ArrayList<>());
    }

    private List<String> getTopNUrlToPrefetchForNode(@NonNull ActivityNode node, float parentScore, List<String> urlList) {
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
            float successorScore = parentScore * ((successorFrequency / (float) totalAggregateFrequency * 0.5f) + (successorTime / totalAggregateTime * 0.5f));
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
//        IN THIS METHOD
//        Unless the current node has a single child, the child score will be lower than the current
//        allow to pass the weight as confiuration
    }
}
