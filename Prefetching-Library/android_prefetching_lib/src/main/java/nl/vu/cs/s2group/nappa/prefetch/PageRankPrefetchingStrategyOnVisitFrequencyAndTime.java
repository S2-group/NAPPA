package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;

/**
 * This strategy employs a Link Analysis approach implementing a PageRank-based algorithm
 * using the time a user spends in the activities and how frequent the user access the
 * activities to decide which nodes to select.
 * <p>
 * Only a subgraph of the ENG is considered in the calculations. The score calculations are
 * performed at runtime and are not persisted in the database.
 * <p>
 * This strategy accepts the following configurations:
 * <ul>
 *     <li>{@link PrefetchingStrategyConfigKeys#WEIGHT_FREQUENCY_SCORE}</li>
 *     <li>{@link PrefetchingStrategyConfigKeys#WEIGHT_TIME_SCORE}</li>
 * </ul>
 */
public class PageRankPrefetchingStrategyOnVisitFrequencyAndTime extends AbstractPrefetchingStrategy {
    public PageRankPrefetchingStrategyOnVisitFrequencyAndTime(@NotNull Map<PrefetchingStrategyConfigKeys, Object> config) {
        super(config);
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {
        return new ArrayList<>();
    }
}
