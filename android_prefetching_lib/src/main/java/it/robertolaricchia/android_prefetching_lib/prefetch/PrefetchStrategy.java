package it.robertolaricchia.android_prefetching_lib.prefetch;

import android.support.annotation.NonNull;

import java.util.List;

import it.robertolaricchia.android_prefetching_lib.graph.ActivityNode;

public interface PrefetchStrategy {

    @NonNull List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber);

}
