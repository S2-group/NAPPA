package nl.vu.cs.s2group.nappa.prefetch;

import nl.vu.cs.s2group.nappa.util.NappaConfigMap;

/**
 * This class defines common configuration shared among the prefetching strategies.
 * <p>
 * Configurations shared between all strategies are:
 *
 * <ul>
 *     <li> {@link PrefetchingStrategyConfigKeys#MAX_URL_TO_PREFETCH} </li>
 *     <li> {@link PrefetchingStrategyConfigKeys#LAST_N_SESSIONS} </li>
 *     <li> {@link PrefetchingStrategyConfigKeys#LOWER_THRESHOLD_SCORE} </li>
 * </ul>
 * <p>
 * Configurations shared between some strategies are:
 *
 * <ul>
 *     <li> {@link PrefetchingStrategyConfigKeys#USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS} </li>
 *     <li> {@link PrefetchingStrategyConfigKeys#NUMBER_OF_ITERATIONS} </li>
 * </ul>
 */
public abstract class AbstractPrefetchingStrategy implements PrefetchingStrategy {
    public static final float DEFAULT_SCORE_LOWER_THRESHOLD = 0.6f;
    public static final float DEFAULT_DAMPING_FACTOR = 0.85f;
    public static final int DEFAULT_LAST_N_SESSIONS = 5;
    public static final int DEFAULT_MAX_URL_TO_PREFETCH = 2;
    public static final int DEFAULT_NUMBER_OF_ITERATIONS = 10;
    public static final boolean DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS = true;

    protected final int maxNumberOfUrlToPrefetch;
    protected final int lastNSessions;
    protected final int numberOfIterations;
    protected final float scoreLowerThreshold;
    protected final float dampingFactor;
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

        numberOfIterations = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.NUMBER_OF_ITERATIONS,
                DEFAULT_NUMBER_OF_ITERATIONS);

        dampingFactor = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.PAGE_RANK_DAMPING_FACTOR,
                DEFAULT_DAMPING_FACTOR);
    }

    @Override
    public boolean needVisitTime() {
        return false;
    }

    @Override
    public boolean needSuccessorsVisitTime() {
        return false;
    }
}
