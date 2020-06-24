package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;
import nl.vu.cs.s2group.nappa.util.NappaUtil;

/**
 * This strategy greedily determine which activity successors can benefit the most from
 * prefetching. This is not limited to immediate successors but can consider successors
 * at a higher depth in the ENG, provided that the "added value" of prefetching these
 * high-depth successors is sufficient.
 * <br/><br/>
 *
 * <p> The node selection process introduces the notion of a "Weight" factor.
 * The algorithm recursively traverses the set of all successors in a depth first way
 * up to the point where the base case does not hold. With every recursive
 * iteration of the algorithm, the weight value is defined as the score that has been
 * calculated in the previous iteration. With every iteration of the recursive step,
 * the weight value decays further.
 * <br/><br/>
 *
 * <p> Considering the recursive step, it can be seen that as the graph depth increases,
 * then the scores corresponding to all subsequent nodes decreases. To limit the number
 * of node candidates generated, a threshold value is inserted.
 */
public class GreedyPrefetchingStrategyOnVisitFrequency implements PrefetchingStrategy {
    private static final String LOG_TAG = GreedyPrefetchingStrategyOnVisitFrequency.class.getSimpleName();

    private float threshold;
    private HashMap<Long, String> reversedHashMap = new HashMap<>();

    public GreedyPrefetchingStrategyOnVisitFrequency(float threshold) {
        this.threshold = threshold;
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {

        Map<String, Long> activityMap = PrefetchingLib.activityMap;
        for (String key : activityMap.keySet()){
            reversedHashMap.put(activityMap.get(key), key);
        }

        List<ActivityNode> probableNodes = getMostProbableNodes(node, 1, new LinkedList<>());

        return NappaUtil.getUrlsFromCandidateNodes(node, probableNodes);
    }

    /**
     * Will calculate the probabilities of access for each individual successor for a {@code node}.
     * Will then recursively calculate the probability for the successor of each successor:
     *
     * node->successorA->...->successorN
     *
     * @param node Current activity to be considered for prefetching
     * @param initialProbability The probability calculated for a predcessor, used as a weight in order to calculate
     *                           the probabilities of each successor
     * @param probableNodes List containing all the probable nodes, corresponding to those nodes that
     *                      have a probability exceeding the prescribed threshold.
     * @return The set of probable nodes {@code List<ActivityNode>} with respect to the initial activity {@code node}
     */
    private List<ActivityNode> getMostProbableNodes(ActivityNode node, float initialProbability, List<ActivityNode> probableNodes) {
        // Fetch the current state of the session aggregate
        List<SessionDao.SessionAggregate> sessionAggregate = node.getSessionAggregateList();
        HashMap<Long, Integer> successorCountMap = new HashMap<>();

        int total = 0;
        for (SessionDao.SessionAggregate succ : sessionAggregate) {
            // Add the total number of transitions between nodes for a given source and all destinations
            total += succ.countSource2Dest;
            // For all successors, track the number of transitions
            successorCountMap.put(succ.idActDest, succ.countSource2Dest.intValue());
        }

        // For each destination calculate the probability of Access
        for (Long succ : successorCountMap.keySet()) {
            // Individual successor divided by total accesses
            float prob = initialProbability * ((float) successorCountMap.get(succ)/total);
            ActivityNode node1 = PrefetchingLib.getActivityGraph().getByName(reversedHashMap.get(succ));

            if (prob >= threshold) {
                // If not yet added, add this current node to the probable nodes and calculate the
                //     next probability using this nodes probability as an initial probability.
                //BUG -- 2 contains(succ) is wrong
                if (!probableNodes.contains(node1)) {
                    probableNodes.add(node1);
                    // Compute the probable nodes using this successor as the current activity
                    // NOTE TO SELF: The further this calculation recurses, the lower the probabilities become.
                    getMostProbableNodes(node1, prob, probableNodes);
                }

            }
            Log.d(LOG_TAG, "Computed probability: " + prob + " for " + node1.activityName);
        }

        return probableNodes;
    }
}
