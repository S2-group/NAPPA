package nl.vu.cs.s2group.prefetch;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.PrefetchingLib;
import nl.vu.cs.s2group.graph.ActivityNode;
import nl.vu.cs.s2group.prefetchurl.ParameteredUrl;
import nl.vu.cs.s2group.room.dao.SessionDao;

public class PrefetchStrategyImpl5 implements PrefetchStrategy {

    private HashMap<Long, String> reversedHashMap = new HashMap<>();
    float threshold;
    public  PrefetchStrategyImpl5(float threshold) {this.threshold=threshold;}



    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {

        Map<String, Long> activityMap = PrefetchingLib.activityMap;
        for (String key : activityMap.keySet()){
            reversedHashMap.put(activityMap.get(key), key);
        }

        List<ActivityNode> probableNodes = getMostProbableNodes(node, new LinkedList<>());
        //for (ActivityNode node1 : probableNodes) Log.d("PREFSTRAT5xxxxxxxx",node1.activityName+" - "+node1.pageRank);
        for (ActivityNode node1 : probableNodes) {
            for (int i=probableNodes.lastIndexOf(node1)+1;i<probableNodes.size();i++) {
                ActivityNode node2 = probableNodes.get(i);
                //Log.d("PREFSTRAT5NODE",node1.activityName+" --> "+node2.activityName);
                if(node1.pageRank<node2.pageRank){
                    //Log.d("PREFSTRAT5NODE","SCAMBIO");
                    ActivityNode temp=node1;
                    probableNodes.set(probableNodes.lastIndexOf(node1),node2);
                    probableNodes.set(probableNodes.lastIndexOf(node2),temp);
                    node1=node2;
                    //for (ActivityNode noder : probableNodes) Log.d("PREFSTRAT5vvvvv",noder.activityName+" - "+noder.pageRank);
                }
            }
        }
        List<String> listUrlToPrefetch = new LinkedList<>();
        maxNumber = (int) (threshold*probableNodes.size() +1);

        for (int i=0; i<maxNumber; i++) {
            listUrlToPrefetch.addAll(computeCandidateUrl2(probableNodes.get(i), node));
            Log.e("PREFSTRAT5","SELECTED --> " + probableNodes.get(i).activityName+ " index: " + probableNodes.get(i).pageRank);
        }
        /*float n = 0.25f/(float)reversedHashMap.keySet().size();
        for (int i=0; i<probableNodes.size(); i++) {
            if(probableNodes.get(i).pageRank>n) {
                listUrlToPrefetch.addAll(computeCandidateUrl2(probableNodes.get(i), node));
                Log.e("PREFSTRAT5", "SELECTED --> " + probableNodes.get(i).activityName + " index: " + probableNodes.get(i).pageRank);
            }
        }*/

        return listUrlToPrefetch;
    }

    /**
     * Will calculate the probabilities of access for each individual successor for a {@code node}.
     * Will then recursively calculate the probability for the successor of each successor:
     *
     * node->successorA->...->successorN
     *
     * @param node Current activity to be considered for prefetching
     *
     * @param probableNodes List containing all the probable nodes, corresponding to those nodes that
     *                      have a probability exceeding the prescribed threshold.
     * @return The set of probable nodes {@code List<ActivityNode>} with respect to the initial activity {@code node}
     */
    private List<ActivityNode> getMostProbableNodes(ActivityNode node, List<ActivityNode> probableNodes) {
        // Fetch the current state of the session aggregate
        List<SessionDao.SessionAggregate> sessionAggregate = node.getSessionAggregateList();
        HashMap<Long, Float> successorCountMap = new HashMap<>();

        int total = 0;
        for (SessionDao.SessionAggregate succ : sessionAggregate) {
            successorCountMap.put(succ.idActDest, PrefetchingLib.getActivityGraph().getByName(succ.actName).pageRank);
        }

        for (Long succ : successorCountMap.keySet()) {

            ActivityNode node1 = PrefetchingLib.getActivityGraph().getByName(reversedHashMap.get(succ));

                if (!probableNodes.contains(node1)) {
                    probableNodes.add(node1);
                    getMostProbableNodes(node1, probableNodes);
                }

        }

        return probableNodes;
    }

    private List<String> computeCandidateUrl2(ActivityNode toBeChecked, ActivityNode node) {
        node.parameteredUrlMap.keySet();

        List<String> candidates = new LinkedList<>();

        Map<String, String> extrasMap = PrefetchingLib.getExtrasMap().get(PrefetchingLib.getActivityIdFromName(node.activityName));
        for (ParameteredUrl parameteredUrl : toBeChecked.parameteredUrlList) {
            if ((null != extrasMap) && !extrasMap.isEmpty() && extrasMap.keySet().containsAll(parameteredUrl.getParamKeys())) {
                candidates.add(
                        parameteredUrl.fillParams(extrasMap)
                );
            }

        }
        for (String candidate: candidates) {
            Log.e("PREFSTRAT5", candidate + " url for: " + toBeChecked.activityName);
        }
        return candidates;
    }
}

