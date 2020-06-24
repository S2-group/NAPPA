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

    public List<ActivityNode> getMostProbableNodes(ActivityNode node, float parentScore, List<ActivityNode> candidateNodes) {
//        instead of returning a list of nodes, it might be more useful to pass a list of URLs
//        OR we define the array of URLs as this class property?
//        add live data observer to the select query
//
//        BEFORE THE METHOD
//        mantain frequency and time data in memory
//        load data from last N sessions when initializing the library
//        simple access this data here -- > will surely improve reading speed
//
//        IN THIS METHOD
//        obtain the current node URLs
//        if the number of URLs found is smaller than the maximum allowed, prepare to visit the next
//        child
//
//        normalize the frequency and time by dividing the current value by the sum
//        the child score will be the previous score multiplied by the 0.5 time + 0.5 frequency
//        Unless the current node has a single child, the child score will be lower than the current
//        allow to pass the weight as confiuration
//
//        find the child with the highest probability
//        if this child score is higher than the lower bound thrshold, then visit this child


        return candidateNodes;
    }
}
