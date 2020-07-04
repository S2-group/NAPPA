package nl.vu.cs.s2group.nappa.prefetch;

/**
 * <ul>
 *     <li> {@link #STRATEGY_MOST_VISITED_SUCCESSOR}</li>
 *     <li> {@link #STRATEGY_2}</li>
 *     <li> {@link #STRATEGY_GREEDY_VISIT_FREQUENCY}</li>
 *     <li> {@link #STRATEGY_PPM} (Prediction by Partial Match)</li>
 *     <li> {@link #STRATEGY_PAGERANK}</li>
 *     <li> {@link #STRATEGY_HITS} (Hyperlink-Induced Topic Search)</li>
 *     <li> {@link #STRATEGY_SALSA} (Stochastic Approach for Link-Structure Analysis)</li>
 *     <li> {@link #STRATEGY_GREEDY_WITH_PAGERANK_SCORES}</li>
 *     <li> {@link #STRATEGY_PPM_WITH_HITS_SCORES}</li>
 * </ul>
 */
public enum PrefetchingStrategyType {
    /**
     * ID for strategy implemented at {@link MostVisitedSuccessorPrefetchingStrategy}
     */
    STRATEGY_MOST_VISITED_SUCCESSOR,

    /**
     * ID for strategy implemented at {@link PrefetchingStrategyImpl2}
     */
    STRATEGY_2,

    /**
     * ID for strategy implemented at {@link GreedyPrefetchingStrategyOnVisitFrequency}
     */
    STRATEGY_GREEDY_VISIT_FREQUENCY,

    /**
     * ID for strategy implemented at {@link GreedyPrefetchingStrategyOnVisitFrequencyAndTime}.
     * This strategy select the top N most probable nodes using a Greedy approach and using the
     * user visit time and frequency per node as measurements of weight
     */
    STRATEGY_GREEDY_VISIT_FREQUENCY_AND_TIME,

    /**
     * ID for strategy implemented at {@link PPMPrefetchingStrategy}
     */
    STRATEGY_PPM,

    /**
     * ID for strategy implemented at {@link PageRankPrefetchingStrategy}
     */
    STRATEGY_PAGERANK,

    /**
     * ID for strategy implemented at {@link TFPRPrefetchingStrategy}
     */
    STRATEGY_TFPR,

    /**
     * ID for strategy implemented at {@link HITSPrefetchingStrategy}
     */
    STRATEGY_HITS,

    /**
     * ID for strategy implemented at {@link SALSAPrefetchingStrategy}
     */
    STRATEGY_SALSA,

    /**
     * ID for strategy implemented at {@link GreedyWithPageRankScoresPrefetchingStrategy}
     */
    STRATEGY_GREEDY_WITH_PAGERANK_SCORES,

    /**
     * ID for strategy implemented at {@link PPMWithHITSScoresPrefetchingStrategy}
     */
    STRATEGY_PPM_WITH_HITS_SCORES,
}
