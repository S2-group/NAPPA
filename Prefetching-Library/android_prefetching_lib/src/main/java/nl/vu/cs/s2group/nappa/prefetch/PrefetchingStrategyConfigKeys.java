package nl.vu.cs.s2group.nappa.prefetch;

import android.content.Context;

import java.util.Map;

/**
 * This enumerate contains all available configurations that can be passed as parameter to
 * the NAPPA initializer method at
 * {@link nl.vu.cs.s2group.nappa.PrefetchingLib#init(Context, PrefetchingStrategyType, Map)}.
 * There are global configurations shared among strategies and configurations specific to one
 * or more strategies. The shared configurations can ve verified at {@link AbstractPrefetchingStrategy}
 * and the specific configuration sin the strategy implementation class
 */
public enum PrefetchingStrategyConfigKeys {
    /**
     * Maps an {@link Integer} representing the maximum number of URLs that will be prefetched
     * in a run of {@link PrefetchingStrategy#getTopNUrlToPrefetchForNode}.
     * <p>
     * The default value is {@link AbstractPrefetchingStrategy#DEFAULT_MAX_URL_TO_PREFETCH}.
     */
    MAX_URL_TO_PREFETCH,

    /**
     * Maps a {@link Float} representing the lower threshold used when deciding weather to
     * select a node as candidate or not.
     * <p>
     * The default value is {@link AbstractPrefetchingStrategy#DEFAULT_SCORE_LOWER_THRESHOLD}.
     */
    LOWER_THRESHOLD_SCORE,

    /**
     * Maps a {@link Integer} representing the number of sessions to fetch data used to calculate
     * the probability of navigating the a given node. This number includes the current session.
     * To fetch data from all sessions, set this parameter to -1
     * This parameter limits the search for the following data:
     * <ul>
     *     <li> Aggregate visit time</li>
     *     <li> Aggregate visit frequency</li>
     * </ul>
     * <p>
     * The default value is {@link AbstractPrefetchingStrategy#DEFAULT_LAST_N_SESSIONS}.
     */
    LAST_N_SESSIONS,

    /**
     * Maps a {@link Float} representing the weight to give for the aggregate visit time
     * score of a given node when calculating the probability of the user navigating to
     * this node.
     * <p>
     * This configuration is available only to strategies that uses more than one parameter
     * as measure of weight. The sum of all weights used by the strategy must be 1.
     * <p>
     * The default value is an equivalent fraction between all weights considered.
     * (e.e., if another measure of weight is used, then the default value is 0.5, if two
     * more measures of weight are used, then de default value is 0.333, etc.)
     */
    WEIGHT_TIME_SCORE,

    /**
     * Maps a {@link Float} representing the weight to give for the aggregate visit frequency
     * score of a given node when calculating the probability of the user navigating to
     * this node.
     * <p>
     * This configuration is available only to strategies that uses more than one parameter
     * as measure of weight. The sum of all weights used by the strategy must be 1.
     * <p>
     * The default value is an equivalent fraction between all weights considered.
     * (e.e., if another measure of weight is used, then the default value is 0.5, if two
     * more measures of weight are used, then de default value is 0.333, etc.)
     */
    WEIGHT_FREQUENCY_SCORE,
}
