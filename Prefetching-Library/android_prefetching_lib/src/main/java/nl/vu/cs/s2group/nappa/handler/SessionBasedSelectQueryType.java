package nl.vu.cs.s2group.nappa.handler;

/**
 * This enumerate provide flags to determine which select query should be executed.
 */
public enum SessionBasedSelectQueryType {
    /**
     * Select data from all sessions.
     */
    ALL_SESSIONS,

    /**
     * Select the last N sessions registered in the Entity
     * {@link nl.vu.cs.s2group.nappa.room.data.Session Session}.
     */
    LAST_N_SESSIONS_FROM_ENTITY_SESSION,

    /**
     * Select the last N sessions registered in the Entity being queried.
     */
    LAST_N_SESSIONS_FROM_QUERIED_ENTITY,
}
