package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;

/**
 * The <tt>PrefetchStrategy</tt> interface provides the API to access and initialize
 * the implemented prefetching strategies. The strategy is selected upon initializing the
 * library API via:
 * <br/><br/>
 *
 * <code>PrefetchingLib.init(STRATEGY_ID)</code>
 * <br/><br/>
 *
 * <p>The available values of the {@code STRATEGY_ID} parameter are in the following list:
 *
 * <ul>
 *     <li> {@link #STRATEGY_1}</li>
 *     <li> {@link #STRATEGY_2}</li>
 *     <li> {@link #STRATEGY_GREEDY}</li>
 *     <li> {@link #STRATEGY_4}</li>
 *     <li> {@link #STRATEGY_5}</li>
 *     <li> {@link #STRATEGY_6}</li>
 *     <li> {@link #STRATEGY_7}</li>
 *     <li> {@link #STRATEGY_8}</li>
 *     <li> {@link #STRATEGY_9}</li>
 * </ul>
 *
 */
public interface PrefetchStrategy {
    /**
     * ID for strategy implemented at {@link PrefetchStrategyImpl}
     */
    @Deprecated
    int STRATEGY_1 = 1;

    /**
     * ID for strategy implemented at {@link PrefetchStrategyImpl2}
     */
    @Deprecated
    int STRATEGY_2 = 2;

    /**
     * ID for strategy implemented at {@link GreedyPrefetchStrategy}
     */
    int STRATEGY_GREEDY = 3;

    /**
     * ID for strategy implemented at {@link PrefetchStrategyImpl4}
     */
    @Deprecated
    int STRATEGY_4 = 4;

    /**
     * ID for strategy implemented at {@link PrefetchStrategyImpl5}
     */
    @Deprecated
    int STRATEGY_5 = 5;

    /**
     * ID for strategy implemented at {@link PrefetchStrategyImpl6}
     */
    @Deprecated
    int STRATEGY_6 = 6;

    /**
     * ID for strategy implemented at {@link PrefetchStrategyImpl7}
     */
    @Deprecated
    int STRATEGY_7 = 7;

    /**
     * ID for strategy implemented at {@link PrefetchStrategyImpl8}
     */
    @Deprecated
    int STRATEGY_8 = 8;

    /**
     * ID for strategy implemented at {@link PrefetchStrategyImpl9}
     */
    @Deprecated
    int STRATEGY_9 = 9;

    /**
     * Obtain the most likely {@link android.app.Activity} the user is likely to navigate to
     *
     * @param node      Represents the current {@link android.app.Activity} to where the user navigated to
     * @param maxNumber Limits the number of requests to prefetch
     * @return A list of URLs with high potential to be requested by the user in the immediate future
     */
    @NonNull
    List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber);

    /**
     * Instantiate the prefetching strategy corresponding to the provided ID.
     * If the ID is unknown, instantiate the default Greedy-based strategy implemented by
     * {@link GreedyPrefetchStrategy}
     *
     * @param strategyId The identification number of the prefetching strategy.
     * @return A implemented prefetching strategy.
     */
    @SuppressWarnings("DuplicateBranchesInSwitch")
    @NotNull
    @Contract("_ -> new")
    static PrefetchStrategy getStrategy(int strategyId) {
        switch (strategyId) {
            case STRATEGY_1:
                return new PrefetchStrategyImpl();
            case STRATEGY_2:
                return new PrefetchStrategyImpl2();
            case STRATEGY_GREEDY:
                return new GreedyPrefetchStrategy(0.6f);
            case STRATEGY_4:
                return new PrefetchStrategyImpl4(0.6f);
            case STRATEGY_5:
                return new PrefetchStrategyImpl5(0.6f);
            case STRATEGY_6:
                return new PrefetchStrategyImpl6(0.6f);
            case STRATEGY_7:
                return new PrefetchStrategyImpl7(0.6f);
            case STRATEGY_8:
                return new PrefetchStrategyImpl8(0.6f);
            case STRATEGY_9:
                return new PrefetchStrategyImpl9(0.6f);
            default:
                return new GreedyPrefetchStrategy(0.6f);
        }
    }
}
