package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.Nappa;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;
import nl.vu.cs.s2group.nappa.util.NappaUtil;

/**
 * This strategy utilizes the PageRank link analysis algorithm to determine which
 * nodes to select. This strategy only considers the direct successors of the
 * current node.
 */
@Deprecated
public class PageRankPrefetchingStrategy implements PrefetchingStrategy {
    private static final String LOG_TAG = PageRankPrefetchingStrategy.class.getSimpleName();

    private HashMap<Long, String> reversedHashMap = new HashMap<>();
    float threshold;
    public PageRankPrefetchingStrategy(float threshold) {this.threshold=threshold;}

    @Override
    public boolean needVisitTime() {
        return false;
    }

    @Override
    public boolean needSuccessorsVisitTime() {
        return false;
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {

        Map<String, Long> activityMap = Nappa.activityMap;
        for (String key : activityMap.keySet()){
            reversedHashMap.put(activityMap.get(key), key);
        }

        List<ActivityNode> probableNodes = getMostProbableNodes(node, new LinkedList<>());
        for (ActivityNode node1 : probableNodes) {
            for (int i=probableNodes.lastIndexOf(node1)+1;i<probableNodes.size();i++) {
                ActivityNode node2 = probableNodes.get(i);
                if(node1.pageRank<node2.pageRank){
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
            Log.d(LOG_TAG,"SELECTED --> " + probableNodes.get(i).activityName+ " index: " + probableNodes.get(i).pageRank);
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
            successorCountMap.put(succ.idActDest, Nappa.getActivityGraph().getByName(succ.actName).pageRank);
        }

        for (Long succ : successorCountMap.keySet()) {

            ActivityNode node1 = Nappa.getActivityGraph().getByName(reversedHashMap.get(succ));

                if (!probableNodes.contains(node1)) {
                    probableNodes.add(node1);
                    getMostProbableNodes(node1, probableNodes);
                }

        }

        return probableNodes;
    }

}

