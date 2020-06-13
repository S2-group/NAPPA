package nl.vu.cs.s2group.nappa.util;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.prefetchurl.ParameteredUrl;

public class NappaUtil {
    private NappaUtil() {
        throw new IllegalStateException("NappaUtil is a utility class and should be instantiated!");
    }

    @NotNull
    public static List<String> getUrlsFromCandidateNodes(ActivityNode visitedNode, @NotNull List<ActivityNode> candidateNodes) {
        List<String> candidateUrls = new LinkedList<>();

        for (ActivityNode candidateNode : candidateNodes) {
            candidateUrls.addAll(getUrlsFromCandidateNode(visitedNode, candidateNode));
        }

        return candidateUrls;
    }

    @NotNull
    public static List<String> getUrlsFromCandidateNode(@NotNull ActivityNode visitedNode, @NotNull ActivityNode candidateNode) {
        List<String> candidateUrls = new LinkedList<>();
        long activityId = PrefetchingLib.getActivityIdFromName(visitedNode.activityName);
        Map<String, String> extrasMap = PrefetchingLib.getExtrasMap().get(activityId);

        if (extrasMap == null || extrasMap.isEmpty()) return candidateUrls;

        for (ParameteredUrl parameteredUrl : candidateNode.parameteredUrlList) {
            if (extrasMap.keySet().containsAll(parameteredUrl.getParamKeys())) {
                String urlWithExtras = parameteredUrl.fillParams(extrasMap);
                candidateUrls.add(urlWithExtras);
            }
        }

        return candidateUrls;
    }
}
