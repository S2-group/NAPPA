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
 * Utilizes the Stochastic Approach for Link-Structure Analysis (SALSA) ranking
 * algorithm to determine which nodes to select. This strategy only considers the
 * direct successors of the current node.
 * <br/><br/>
 *
 * <p> The SALSA algorithm is defined in the paper
 * <a href="https://dl.acm.org/doi/10.1145/382979.383041">
 * SALSA: the stochastic approach for link-structure analysis</a>
 */
@Deprecated
public class SALSAPrefetchingStrategy implements PrefetchingStrategy {
    private static final String LOG_TAG = SALSAPrefetchingStrategy.class.getSimpleName();
    private HashMap<Long, String> reversedHashMap = new HashMap<>();
    private float threshold;

    public SALSAPrefetchingStrategy(float threshold) {this.threshold = threshold;}

    @Override
    public boolean needVisitTime() {
        return false;
    }


    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {

        Map<String, Long> activityMap = PrefetchingLib.activityMap;
        for (String key : activityMap.keySet()){
            reversedHashMap.put(activityMap.get(key), key);
        }

        List<ActivityNode> probableNodes = getMostProbableNodes(node, new LinkedList<>());
        for (ActivityNode node1 : probableNodes) {
            for (int i=probableNodes.lastIndexOf(node1)+1;i<probableNodes.size();i++) {
                ActivityNode node2 = probableNodes.get(i);
                if(node1.authorityS<node2.authorityS){
                    ActivityNode temp=node1;
                    probableNodes.set(probableNodes.lastIndexOf(node1),node2);
                    probableNodes.set(probableNodes.lastIndexOf(node2),temp);
                    node1=node2;
                }else if(node1.authorityS==node2.authorityS&&node1.hubS<node2.hubS){
                        ActivityNode temp=node1;
                        probableNodes.set(probableNodes.lastIndexOf(node1),node2);
                        probableNodes.set(probableNodes.lastIndexOf(node2),temp);
                        node1=node2;
                }
            }
        }
        List<String> listUrlToPrefetch = new LinkedList<>();
        maxNumber = (int) (threshold*probableNodes.size() + 1);

        for (int i=0; i<maxNumber; i++) {
            listUrlToPrefetch.addAll(NappaUtil.getUrlsFromCandidateNode(node, probableNodes.get(i)));
            Log.d(LOG_TAG,"SELECTED --> " + probableNodes.get(i).activityName + " index: " + probableNodes.get(i).authorityS);

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
            successorCountMap.put(succ.idActDest, PrefetchingLib.getActivityGraph().getByName(succ.actName).authorityS);
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
}
