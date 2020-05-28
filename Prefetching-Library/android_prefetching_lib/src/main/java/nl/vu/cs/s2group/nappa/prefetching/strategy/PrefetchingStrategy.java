package nl.vu.cs.s2group.nappa.prefetching.strategy;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;

/**
 * Basic interface used implementing a strategy to identify which URLs the user is most likely to
 * request in the immediate future
 */
public interface PrefetchingStrategy {
    int STRATEGY_GREEDY = 10;

    /**
     * @param node      Represents the current {@link android.app.Activity} to where the user navigated to
     * @param maxNumber Limits the number of requests to prefetch
     * @return A list of URLs with high potential to be requested by the user in the immediate future
     */
    @NonNull
    List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber);

}
