package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;

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
public abstract class AbstractPrefetchingStrategy implements PrefetchingStrategy {
    public static final float DEFAULT_SCORE_LOWER_THRESHOLD = 0.6f;
    public static final int DEFAULT_LAST_N_SESSIONS = 5;
    public static final int DEFAULT_MAX_URL_TO_PREFETCH = 2;
    public static final boolean DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS = true;

    private Map<PrefetchingStrategyConfigKeys, Object> config;

    protected final int maxNumberOfUrlToPrefetch;
    protected final int lastNSessions;
    protected final float scoreLowerThreshold;
    protected final boolean useAllSessionsAsScoreForLastNSessions;

    public AbstractPrefetchingStrategy(@NonNull Map<PrefetchingStrategyConfigKeys, Object> config) {
        this.config = config;

        maxNumberOfUrlToPrefetch = getConfig(
                PrefetchingStrategyConfigKeys.MAX_URL_TO_PREFETCH,
                DEFAULT_MAX_URL_TO_PREFETCH);

        useAllSessionsAsScoreForLastNSessions = getConfig(
                PrefetchingStrategyConfigKeys.USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS,
                DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS);

        lastNSessions = getConfig(
                PrefetchingStrategyConfigKeys.LAST_N_SESSIONS,
                DEFAULT_LAST_N_SESSIONS);

        scoreLowerThreshold = getConfig(
                PrefetchingStrategyConfigKeys.LOWER_THRESHOLD_SCORE,
                DEFAULT_SCORE_LOWER_THRESHOLD);
    }

    protected int getConfig(PrefetchingStrategyConfigKeys key, int defaultValue) {
        Object value = config.get(key);
        return value == null ? defaultValue : Integer.parseInt(value.toString());
    }

    protected boolean getConfig(PrefetchingStrategyConfigKeys key, boolean defaultValue) {
        Object value = config.get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.toString());
    }

    protected float getConfig(PrefetchingStrategyConfigKeys key, float defaultValue) {
        Object value = config.get(key);
        return value == null ? defaultValue : Float.parseFloat(value.toString());
    }

    @Override
    public boolean needVisitTime() {
        return false;
    }
}
