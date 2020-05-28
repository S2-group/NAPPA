package nl.vu.cs.s2group.nappa.prefetching.strategy;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;

/**
 * Basic interface used implementing a strategy to identify which URLs the user is most likely to
 * request in the immediate future
 */
public interface PrefetchingStrategy {
    @Deprecated
    int STRATEGY_1 = 1;
    @Deprecated
    int STRATEGY_2 = 2;
    int STRATEGY_GREEDY = 3;
    @Deprecated
    int STRATEGY_4 = 4;
    @Deprecated
    int STRATEGY_5 = 5;
    @Deprecated
    int STRATEGY_6 = 6;
    @Deprecated
    int STRATEGY_7 = 7;
    @Deprecated
    int STRATEGY_8 = 8;
    @Deprecated
    int STRATEGY_9 = 9;

    /**
     * @param node      Represents the current {@link android.app.Activity} to where the user navigated to
     * @param maxNumber Limits the number of requests to prefetch
     * @return A list of URLs with high potential to be requested by the user in the immediate future
     */
    @NonNull
    List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber);

    /**
     * Instantiate the prefetching strategy corresponding to the provided ID.
     * If the ID is unknown, instantiate the default Greedy-based strategy implemented by
     * {@link GreedyPrefetchingStrategy}
     *
     * @param strategyId The identification number of the prefetching strategy.
     * @return A implemented prefetching strategy.
     */
    @NotNull
    @Contract("_ -> new")
    static PrefetchingStrategy getStrategy(int strategyId) {
        switch (strategyId) {
            case STRATEGY_1:
                return new PrefetchingStrategyImpl();
            case STRATEGY_2:
                return new PrefetchingStrategyImpl2();
            case STRATEGY_4:
                return new PrefetchingStrategyImpl4(0.6f);
            case STRATEGY_5:
                return new PrefetchingStrategyImpl5(0.6f);
            case STRATEGY_6:
                return new PrefetchingStrategyImpl6(0.6f);
            case STRATEGY_7:
                return new PrefetchingStrategyImpl7(0.6f);
            case STRATEGY_8:
                return new PrefetchingStrategyImpl8(0.6f);
            case STRATEGY_9:
                return new PrefetchingStrategyImpl9(0.6f);
            case PrefetchingStrategy.STRATEGY_GREEDY:
            default:
                return new GreedyPrefetchingStrategy();
        }
    }
}
