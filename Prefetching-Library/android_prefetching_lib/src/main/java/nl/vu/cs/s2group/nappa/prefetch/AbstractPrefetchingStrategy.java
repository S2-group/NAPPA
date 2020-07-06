package nl.vu.cs.s2group.nappa.prefetch;

import nl.vu.cs.s2group.nappa.util.NappaConfigMap;

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
public abstract class AbstractPrefetchingStrategy implements PrefetchingStrategy {
    public static final float DEFAULT_SCORE_LOWER_THRESHOLD = 0.6f;
    public static final int DEFAULT_LAST_N_SESSIONS = 5;
    public static final int DEFAULT_MAX_URL_TO_PREFETCH = 2;
    public static final boolean DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS = true;

    protected final int maxNumberOfUrlToPrefetch;
    protected final int lastNSessions;
    protected final float scoreLowerThreshold;
    protected final boolean useAllSessionsAsScoreForLastNSessions;

    public AbstractPrefetchingStrategy() {
        maxNumberOfUrlToPrefetch = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.MAX_URL_TO_PREFETCH,
                DEFAULT_MAX_URL_TO_PREFETCH);

        useAllSessionsAsScoreForLastNSessions = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS,
                DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS);

        lastNSessions = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.LAST_N_SESSIONS,
                DEFAULT_LAST_N_SESSIONS);

        scoreLowerThreshold = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.LOWER_THRESHOLD_SCORE,
                DEFAULT_SCORE_LOWER_THRESHOLD);
    }

    @Override
    public boolean needVisitTime() {
        return false;
    }
}
