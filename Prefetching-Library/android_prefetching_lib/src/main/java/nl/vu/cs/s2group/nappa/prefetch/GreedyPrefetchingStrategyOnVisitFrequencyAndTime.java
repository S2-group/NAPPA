package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;

public class GreedyPrefetchingStrategyOnVisitFrequencyAndTime extends AbstractPrefetchingStrategy implements PrefetchingStrategy {
    private static final String LOG_TAG = GreedyPrefetchingStrategyOnVisitFrequencyAndTime.class.getSimpleName();

    public GreedyPrefetchingStrategyOnVisitFrequencyAndTime(Map<PrefetchingStrategyConfigKeys, Object> config) {
        super(config);
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {
        return null;
    }
}
