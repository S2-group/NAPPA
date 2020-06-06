package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.prefetchurl.ParameteredUrl;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;

public class PrefetchStrategyImpl4 implements PrefetchStrategy {
    private final static String LOG_TAG = PrefetchStrategyImpl4.class.getSimpleName();

    private float threshold;
    private HashMap<Long, String> reversedHashMap = new HashMap<>();
    private static ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(1);
    public  PrefetchStrategyImpl4(float threshold) {
        this.threshold = threshold;
    }
    public static int lastN = 2;


    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {

        Map<String, Long> activityMap = PrefetchingLib.activityMap;
        for (String key : activityMap.keySet()){
            reversedHashMap.put(activityMap.get(key), key);
        }
        List<ActivityNode> probableNodes = getMostProbableNodes(node);
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
            Log.d(LOG_TAG,"SELECTED --> " + probableNodes.get(i).activityName + " index: " + probableNodes.get(i).prob);

        }
        return listUrlToPrefetch;
    }

    /**
     * Will calculate the total probabilities of access for each individual successor for a {@code node}.
     * Will then recursively calculate the probability for the successor of each successor and will sum it if a successor is already been accessed from another path:
     *
     * node->successorA->...->successorN
     * if(successorN is known) -> sum probabilities (indipendent events)
     *
     * @param node Current activity to be considered for prefetching
     * total Total number of hits in the sessions taken in consideration
     * successorCountMap List containing all the probable nodes, corresponding to those nodes that
     *                      have a probability exceeding the prescribed threshold.
     * @return The set of probable nodes {@code List<ActivityNode>} with respect to the initial activity {@code node}
     */

    private List<ActivityNode> getMostProbableNodes(ActivityNode node) {
        HashMap<Long, Integer> successorCountMap = new HashMap<>();
        List<ActivityNode> probableNodes = new LinkedList<>();
        successorCountMap = zeroContextNodes(node,successorCountMap);
        int total = 0;
        for(Long candidate : successorCountMap.keySet()) {
            total+=successorCountMap.get(candidate);
            Log.d(LOG_TAG,"actName :"+reversedHashMap.get(candidate)+" hit: "+successorCountMap.get(candidate));
        }
        //////////////////////////// Will calculate the probability to access a node by partial match based on a 0-order markov-model
        //////////////////////////// https://pdfs.semanticscholar.org/f9dc/bf7b0c900335932d9a651b9c21d8a59c3679.pdf

        for (Long succ : successorCountMap.keySet()) {
            float prob = 0;
            if(total>0) prob= (float)successorCountMap.get(succ)/total; // * succ.pageRank or others
            ActivityNode node1 = PrefetchingLib.getActivityGraph().getByName(reversedHashMap.get(succ));
            node1.prob=prob;
            probableNodes.add(node1);
            Log.d(LOG_TAG, "Computed probability: " + prob + " for " + node1.activityName);
        }
        return probableNodes;
    }

    private HashMap<Long, Integer> zeroContextNodes(ActivityNode node, HashMap<Long, Integer> successorCountMap){
        List<SessionDao.SessionAggregate> sessionAggregate = node.getSessionAggregateList(lastN);
        for (SessionDao.SessionAggregate succ : sessionAggregate) {
            successorCountMap = zeroContextNodes(PrefetchingLib.getActivityGraph().getByName(reversedHashMap.get(succ.idActDest)),successorCountMap);
            if(successorCountMap.containsKey(succ.idActDest)){
                successorCountMap.put(succ.idActDest, succ.countSource2Dest.intValue()+successorCountMap.get(succ.idActDest));
            }
            else {successorCountMap.put(succ.idActDest, succ.countSource2Dest.intValue()); }//Log.d("PREFSTRAT4 insert count",succ.countSource2Dest+"");}
        }
        return  successorCountMap;
    }

    private List<String> computeCandidateUrl2(ActivityNode toBeChecked, ActivityNode node) {
        node.parameteredUrlMap.keySet();

        List<String> candidates = new LinkedList<>();


        Map<String, String> extrasMap = PrefetchingLib.getExtrasMap().get(PrefetchingLib.getActivityIdFromName(node.activityName));

        for (ParameteredUrl parameteredUrl : toBeChecked.parameteredUrlList) {
            if (extrasMap.keySet().containsAll(parameteredUrl.getParamKeys())) {
                candidates.add(
                        parameteredUrl.fillParams(extrasMap)
                );
            }
        }

        for (String candidate: candidates) {
            Log.d(LOG_TAG, candidate + " for: " + toBeChecked.activityName);
        }

        return candidates;
    }
}
