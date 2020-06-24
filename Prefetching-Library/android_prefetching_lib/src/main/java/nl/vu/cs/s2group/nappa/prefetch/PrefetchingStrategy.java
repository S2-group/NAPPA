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
 *     <li> {@link #STRATEGY_MOST_VISITED_SUCCESSOR}</li>
 *     <li> {@link #STRATEGY_2}</li>
 *     <li> {@link #STRATEGY_GREEDY}</li>
 *     <li> {@link #STRATEGY_PPM} (Prediction by Partial Match)</li>
 *     <li> {@link #STRATEGY_PAGERANK}</li>
 *     <li> {@link #STRATEGY_HITS} (Hyperlink-Induced Topic Search)</li>
 *     <li> {@link #STRATEGY_SALSA} (Stochastic Approach for Link-Structure Analysis)</li>
 *     <li> {@link #STRATEGY_GREEDY_WITH_PAGERANK_SCORES}</li>
 *     <li> {@link #STRATEGY_PPM_WITH_HITS_SCORES}</li>
 * </ul>
 *
 */
public interface PrefetchingStrategy {
    /**
     * ID for strategy implemented at {@link MostVisitedSuccessorPrefetchingStrategy}
     */
    @Deprecated
    int STRATEGY_MOST_VISITED_SUCCESSOR = 1;

    /**
     * ID for strategy implemented at {@link PrefetchingStrategyImpl2}
     */
    @Deprecated
    int STRATEGY_2 = 2;

    /**
     * ID for strategy implemented at {@link GreedyPrefetchingStrategyOnVisitFrequency}
     */
    int STRATEGY_GREEDY = 3;

    /**
     * ID for strategy implemented at {@link PPMPrefetchingStrategy}
     */
    @Deprecated
    int STRATEGY_PPM = 4;

    /**
     * ID for strategy implemented at {@link PageRankPrefetchingStrategy}
     */
    @Deprecated
    int STRATEGY_PAGERANK = 5;

    /**
     * ID for strategy implemented at {@link HITSPrefetchingStrategy}
     */
    @Deprecated
    int STRATEGY_HITS = 6;

    /**
     * ID for strategy implemented at {@link SALSAPrefetchingStrategy}
     */
    @Deprecated
    int STRATEGY_SALSA = 7;

    /**
     * ID for strategy implemented at {@link GreedyWithPageRankScoresPrefetchingStrategy}
     */
    @Deprecated
    int STRATEGY_GREEDY_WITH_PAGERANK_SCORES = 8;

    /**
     * ID for strategy implemented at {@link PPMWithHITSScoresPrefetchingStrategy}
     */
    @Deprecated
    int STRATEGY_PPM_WITH_HITS_SCORES = 9;

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
     * {@link GreedyPrefetchingStrategyOnVisitFrequency}
     *
     * @param strategyId The identification number of the prefetching strategy.
     * @return A implemented prefetching strategy.
     */
    @SuppressWarnings("DuplicateBranchesInSwitch")
    @NotNull
    @Contract("_ -> new")
    static PrefetchingStrategy getStrategy(int strategyId) {
        switch (strategyId) {
            case STRATEGY_MOST_VISITED_SUCCESSOR:
                return new MostVisitedSuccessorPrefetchingStrategy();
            case STRATEGY_2:
                return new PrefetchingStrategyImpl2();
            case STRATEGY_GREEDY:
                return new GreedyPrefetchingStrategyOnVisitFrequency(0.6f);
            case STRATEGY_PPM:
                return new PPMPrefetchingStrategy(0.6f);
            case STRATEGY_PAGERANK:
                return new PageRankPrefetchingStrategy(0.6f);
            case STRATEGY_HITS:
                return new HITSPrefetchingStrategy(0.6f);
            case STRATEGY_SALSA:
                return new SALSAPrefetchingStrategy(0.6f);
            case STRATEGY_GREEDY_WITH_PAGERANK_SCORES:
                return new GreedyWithPageRankScoresPrefetchingStrategy(0.6f);
            case STRATEGY_PPM_WITH_HITS_SCORES:
                return new PPMWithHITSScoresPrefetchingStrategy(0.6f);
            default:
                return new GreedyPrefetchingStrategyOnVisitFrequency(0.6f);
        }
    }
}
