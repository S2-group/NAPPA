package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.prefetchurl.ParameteredUrl;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;

@Deprecated
public class PrefetchStrategyImpl8 implements PrefetchStrategy {
    private float threshold;
    private HashMap<Long, String> reversedHashMap = new HashMap<>();

    public  PrefetchStrategyImpl8(float threshold) {
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
            listUrlToPrefetch.addAll(computeCandidateUrl2(probableNodes.get(i), node));
            Log.e("PREFSTRAT8","SELECTED --> " + probableNodes.get(i).activityName + " index: " + probableNodes.get(i).prob);

        }

        //return computeCandidateUrl(node);
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

            //prob *= node1.pageRank;

            //if (prob >= threshold) {
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

            //}
            //Log.e("PREFSTRAT8", "Computed probability: " + node1.prob + " for " + node1.activityName+ " s - "+threshold
                    //+" pr: " +node1.pageRank);
        }
        return probableNodes;
    }
    private List<String> computeCandidateUrl2(ActivityNode toBeChecked, ActivityNode node) {
        node.parameteredUrlMap.keySet();

        List<String> candidates = new LinkedList<>();


        Map<String, String> extrasMap = PrefetchingLib.getExtrasMap().get(PrefetchingLib.getActivityIdFromName(node.activityName));

        for (ParameteredUrl parameteredUrl : toBeChecked.parameteredUrlList) {
            
            if ((null != extrasMap) && extrasMap.keySet().containsAll(parameteredUrl.getParamKeys())) {
                candidates.add(
                        parameteredUrl.fillParams(extrasMap)
                );
            }
        }
        

        for (String candidate: candidates) {
            Log.e("PREFSTRAT8", candidate + " for: " + toBeChecked.activityName);
        }

        return candidates;
    }
}
