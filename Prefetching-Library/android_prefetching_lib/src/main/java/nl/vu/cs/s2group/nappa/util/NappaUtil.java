package nl.vu.cs.s2group.nappa.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.prefetchurl.ParameteredUrl;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;

/**
 * This class containing common utility methods for the NAPPA Prefetching Library
 */
public class NappaUtil {

    private NappaUtil() {
        throw new IllegalStateException("NappaUtil is a utility class and should not be instantiated!");
    }

    /**
     * Parses the list of candidate nodes and verifies if the URLs requested in a candidate node can
     * be requested with the Extras captured in the visited node
     *
     * @param visitedNode    Represents the node which the user is currently visiting
     * @param candidateNodes Represents a list containing all nodes with the potential to be visited
     *                       in the near future
     * @return All URLs requested by all candidate nodes that can be requested with the information
     * present in the currently visited node
     */
    @NotNull
    public static List<String> getUrlsFromCandidateNodes(ActivityNode visitedNode, @NotNull List<ActivityNode> candidateNodes) {
        List<String> candidateUrls = new LinkedList<>();

        for (ActivityNode candidateNode : candidateNodes) {
            candidateUrls.addAll(getUrlsFromCandidateNode(visitedNode, candidateNode));
        }

        return candidateUrls;
    }

    /**
     * Verifies if the URLs requested in the candidate node can be requested with the Extras captured
     * in the visited node
     *
     * @param visitedNode   Represents the node which the user is currently visiting
     * @param candidateNode Represents a node with the potential to be visited in the near future
     * @return All URLs requested in the candidate node that can be requested with the information
     * present in the currently visited node
     */
    @NotNull
    public static List<String> getUrlsFromCandidateNode(@NotNull ActivityNode visitedNode, @NotNull ActivityNode candidateNode) {
        List<String> candidateUrls = new LinkedList<>();
        long activityId = PrefetchingLib.getActivityIdFromName(visitedNode.activityName);
        Map<String, String> extrasMap = PrefetchingLib.getExtrasMap().get(activityId);

        // Verifies if the current activity contain any registered extras in the current session
        if (extrasMap == null || extrasMap.isEmpty()) return candidateUrls;

        for (ParameteredUrl parameteredUrl : candidateNode.parameteredUrlList) {
            // Verifies if the extras required by the candidate URL are known
            if (extrasMap.keySet().containsAll(parameteredUrl.getParamKeys())) {
                String urlWithExtras = parameteredUrl.fillParams(extrasMap);
                candidateUrls.add(urlWithExtras);
            }
        }

        return candidateUrls;
    }

    /**
     * Calculate the total aggregate sum of the visit time of all subsequent nodes
     *
     * @param activityNode The current node
     * @return Return the total aggregate visit time
     */
    public static float getSuccessorsAggregateVisitTime(@NotNull ActivityNode activityNode) {
        float total = 0;
        for (ActivityNode node : activityNode.successors.keySet()) {
            total += node.getAggregateVisitTime().totalDuration;
        }
        return total;
    }

    /**
     * Calculate the total aggregate sum of the visit frequency of all subsequent nodes
     *
     * @param activityNode The current node
     * @return Return the total aggregate visit frequency
     */
    public static int getSuccessorsTotalAggregateVisitFrequency(@NotNull ActivityNode activityNode, int lastNSessions) {
        int total = 0;
        List<SessionDao.SessionAggregate> frequencyList = lastNSessions == -1 ? activityNode.getSessionAggregateList() : activityNode.getSessionAggregateList(1);

        for (SessionDao.SessionAggregate sessionAggregate : frequencyList) {
            total += sessionAggregate.countSource2Dest;
        }

        return total;
    }

    /**
     * Maps the successors aggregate visit frequency count using the activity name as key
     * and the aggregate visit frequency as value
     *
     * @param activityNode The current node
     * @return Return the mapped aggregate visit frequency count for this node successors
     */
    @NotNull
    public static Map<String, Integer> mapSuccessorsAggregateVisitFrequency(@NotNull ActivityNode activityNode, int lastNSessions) {
        List<SessionDao.SessionAggregate> frequencyList = lastNSessions == -1 ? activityNode.getSessionAggregateList() : activityNode.getSessionAggregateList(1);
        Map<String, Integer> frequencyMap = new HashMap<>(frequencyList.size());

        for (SessionDao.SessionAggregate sessionAggregate : frequencyList) {
            frequencyMap.put(sessionAggregate.actName, sessionAggregate.countSource2Dest.intValue());
        }

        return frequencyMap;
    }
}
