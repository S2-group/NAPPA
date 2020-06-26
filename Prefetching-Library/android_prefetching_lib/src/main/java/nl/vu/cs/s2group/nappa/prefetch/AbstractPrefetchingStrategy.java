package nl.vu.cs.s2group.nappa.prefetch;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * This class defines common configuration shared among all prefetching strategies.
 * The accepted configurations are:
 *
 * <ul>
 *     <li> {@link PrefetchingStrategyConfigKeys#MAX_URL_TO_PREFETCH} </li>
 *     <li> {@link PrefetchingStrategyConfigKeys#LAST_N_SESSIONS} </li>
 *     <li> {@link PrefetchingStrategyConfigKeys#LOWER_THRESHOLD_SCORE} </li>
 *     <li> {@link PrefetchingStrategyConfigKeys#USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS} </li>
 * </ul>
 */
public abstract class AbstractPrefetchingStrategy {
    public static final float DEFAULT_SCORE_LOWER_THRESHOLD = 0.6f;
    public static final int DEFAULT_LAST_N_SESSIONS = 5;
    public static final int DEFAULT_MAX_URL_TO_PREFETCH = 2;
    public static final boolean DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS = true;

    protected final int maxNumberOfUrlToPrefetch;
    protected final int lastNSessions;
    protected final float scoreLowerThreshold;
    protected final boolean useAllSessionsAsScoreForLastNSessions;

    public AbstractPrefetchingStrategy(@NotNull Map<PrefetchingStrategyConfigKeys, Object> config) {
        Object data;

        data = config.get(PrefetchingStrategyConfigKeys.MAX_URL_TO_PREFETCH);
        maxNumberOfUrlToPrefetch = data != null ? Integer.parseInt(data.toString()) : DEFAULT_MAX_URL_TO_PREFETCH;

        data = config.get(PrefetchingStrategyConfigKeys.USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS);
        useAllSessionsAsScoreForLastNSessions = data != null ? Boolean.getBoolean(data.toString()) : DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS;

        data = config.get(PrefetchingStrategyConfigKeys.LAST_N_SESSIONS);
        lastNSessions = data != null ? Integer.parseInt(data.toString()) : DEFAULT_LAST_N_SESSIONS;

        data = config.get(PrefetchingStrategyConfigKeys.LOWER_THRESHOLD_SCORE);
        scoreLowerThreshold = data != null ? Float.parseFloat(data.toString()) : DEFAULT_SCORE_LOWER_THRESHOLD;
    }
}
