package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;

public class GreedyPrefetchingStrategyOnVisitFrequencyAndTime implements PrefetchingStrategy {
    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {
        return null;
    }
}
