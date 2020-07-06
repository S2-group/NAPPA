package nl.vu.cs.s2group.nappa.util;

import java.util.Map;

import nl.vu.cs.s2group.nappa.handler.SessionBasedSelectQueryType;
import nl.vu.cs.s2group.nappa.prefetch.AbstractPrefetchingStrategy;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategyConfigKeys;

/**
 * Centralize a single and shared instance of the user defined NAPPA configuration map.
 */
public class NappaConfigMap {
    private static Map<PrefetchingStrategyConfigKeys, Object> config;

    private NappaConfigMap() {
        throw new IllegalStateException("NappaConfig is a utility class and should not be instantiated!");
    }

    /**
     * Initialize the configuration map. Should be invoked only once.
     *
     * @param config A configuration map defined by the user.
     * @throws RuntimeException If the configuration map was already previously initialized.
     */
    public static void init(Map<PrefetchingStrategyConfigKeys, Object> config) {
        if (NappaConfigMap.config != null)
            throw new RuntimeException("The NAPPA configuration should be initialized only once.");
        NappaConfigMap.config = config;
    }

    /**
     * Defines a new configuration from the |key, value| pair. There shouldn't be a configuration
     * with the provided key.
     *
     * @param key   The configuration key
     * @param value The configuration value
     * @throws IllegalArgumentException If the configuration key was already previously initialized.
     * @throws RuntimeException         If the configuration map is not initialized
     */
    public static void put(PrefetchingStrategyConfigKeys key, Object value) {
        if (config == null)
            throw new RuntimeException("The NAPPA configuration is not initialized.");
        if (config.containsKey(key))
            throw new IllegalArgumentException("There is already an configuration defined by the key " + key + ".");
        config.put(key, value);
    }

    public static int get(PrefetchingStrategyConfigKeys key, int defaultValue) {
        Object value = config.get(key);
        return value == null ? defaultValue : Integer.parseInt(value.toString());
    }

    public static boolean get(PrefetchingStrategyConfigKeys key, boolean defaultValue) {
        Object value = config.get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.toString());
    }

    public static float get(PrefetchingStrategyConfigKeys key, float defaultValue) {
        Object value = config.get(key);
        return value == null ? defaultValue : Float.parseFloat(value.toString());
    }

    /**
     * Some entities specify select queries based a certain number of sessions to take
     * and which entity to use as a source to identify which are these sessions. This
     * getter provides a flag based on the existent configurations.
     *
     * @return A flag defining the which select query to run.
     */
    public static SessionBasedSelectQueryType getSessionBasedSelectQueryType() {
        int lastNSessions = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.LAST_N_SESSIONS,
                AbstractPrefetchingStrategy.DEFAULT_LAST_N_SESSIONS);
        boolean useSessionEntity = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS,
                AbstractPrefetchingStrategy.DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS);

        if (lastNSessions == -1) return SessionBasedSelectQueryType.ALL_SESSIONS;
        else if (useSessionEntity)
            return SessionBasedSelectQueryType.LAST_N_SESSIONS_FROM_ENTITY_SESSION;
        else return SessionBasedSelectQueryType.LAST_N_SESSIONS_FROM_QUERIED_ENTITY;
    }
}
