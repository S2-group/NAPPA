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
 * This strategy is based in the {@link GreedyPrefetchingStrategy}, where the score
 * calculation is changed to use the PageRank score used in {@link PageRankPrefetchingStrategy}.
 * This strategy only considers the direct successors of the current node.
 */
@Deprecated
public class GreedyWithPageRankScoresPrefetchingStrategy implements PrefetchingStrategy {
    private static final String LOG_TAG = GreedyWithPageRankScoresPrefetchingStrategy.class.getSimpleName();
    private float threshold;
    private HashMap<Long, String> reversedHashMap = new HashMap<>();

    public GreedyWithPageRankScoresPrefetchingStrategy(float threshold) {
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

        for (ActivityNode node1 : probableNodes) {
            for (int i=probableNodes.lastIndexOf(node1)+1;i<probableNodes.size();i++) {
                ActivityNode node2 = probableNodes.get(i);
                if(node1.prob<node2.prob){
                    ActivityNode temp=node1;
                    probableNodes.set(probableNodes.lastIndexOf(node1),node2);
                    probableNodes.set(probableNodes.lastIndexOf(node2),temp);
                    node1=node2;
                }
            }
        }
        List<String> listUrlToPrefetch = new LinkedList<>();
        maxNumber = (int) (threshold*probableNodes.size() +1);

        for (int i=0; i<maxNumber; i++) {
            listUrlToPrefetch.addAll(NappaUtil.getUrlsFromCandidateNode(node, probableNodes.get(i)));
            Log.d(LOG_TAG,"SELECTED --> " + probableNodes.get(i).activityName + " index: " + probableNodes.get(i).prob);

        }

        return listUrlToPrefetch;
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
            float prob = initialProbability * ((float) successorCountMap.get(succ)/total * PrefetchingLib.getActivityGraph().getByName(reversedHashMap.get(succ)).pageRank);
            ActivityNode node1 = PrefetchingLib.getActivityGraph().getByName(reversedHashMap.get(succ));

                // If not yet added, add this current node to the probable nodes and calculate the
                //     next probability using this nodes probability as an initial probability.
                if (!probableNodes.contains(node1)) {
                    node1.prob=prob;
                    probableNodes.add(node1);
                    // Compute the probable nodes using this successor as the current activity
                    // NOTE TO SELF: The further this calculation recurses, the lower the probabilities become.
                    getMostProbableNodes(node1, prob, probableNodes);
                }else if(prob>node1.prob){
                    node1.prob=prob;
                }
        }
        return probableNodes;
    }
}
