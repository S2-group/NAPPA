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
 * <code>PrefetchingLib.init(PrefetchingStrategyType.STRATEGY_ID)</code>
 * <br/><br/>
 *
 * <p> See {@link PrefetchingStrategyType} for the available values of the parameter
 * {@code STRATEGY_ID}
 */
public interface PrefetchingStrategy {
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
     * Verifies if the time spent visiting an activity is used for calculating the
     * probabilities in {@link #getTopNUrlToPrefetchForNode(ActivityNode, Integer)}.
     * Namely, the data from {@link ActivityNode#getAggregateVisitTime()}.
     *
     * @return {@code True} if the data is used, {@code False} otherwise.
     */
    boolean needVisitTime();

    /**
     * Verifies if the time spent visiting a successor activity when navigated from the current
     * activity is used for calculating the probabilities in
     * {@link #getTopNUrlToPrefetchForNode(ActivityNode, Integer)}.
     * Namely, the data from {@link ActivityNode#getSuccessorsVisitTimeList()}.
     *
     * @return {@code True} if the data is used, {@code False} otherwise.
     */
    boolean needSuccessorsVisitTime();

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
    @Contract("_-> new")
    static PrefetchingStrategy getStrategy(@NotNull PrefetchingStrategyType strategyId) {
        switch (strategyId) {
            case STRATEGY_MOST_VISITED_SUCCESSOR:
                return new MostVisitedSuccessorPrefetchingStrategy();
            case STRATEGY_2:
                return new PrefetchingStrategyImpl2();
            case STRATEGY_GREEDY_VISIT_FREQUENCY:
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
            case STRATEGY_GREEDY_VISIT_FREQUENCY_AND_TIME:
                return new GreedyPrefetchingStrategyOnVisitFrequencyAndTime();
            case STRATEGY_TFPR:
                return new TfprPrefetchingStrategy();
            default:
                return new GreedyPrefetchingStrategyOnVisitFrequency(0.6f);
        }
    }
}
