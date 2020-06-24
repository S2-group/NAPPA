package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;

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
        scoreLowerThreshold = data != null ? Float.parseFloat(data.toString()) : 0.6f;
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {
        return null;
    }
}
