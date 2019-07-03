package nl.vu.cs.s2group.prefetch;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.graph.ActivityNode;

public interface PrefetchStrategy {

    @NonNull List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber);

}
