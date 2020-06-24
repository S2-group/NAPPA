package nl.vu.cs.s2group.nappa.prefetch;

public enum PrefetchingStrategyConfigKeys {
    /**
     * Maps an {@link Integer} representing the maximum number of URLs that will be prefetched
     * in a run of {@link PrefetchingStrategy#getTopNUrlToPrefetchForNode}.
     */
    MAX_URL_TO_PREFETCH,

    /**
     * Maps a {@link Float} representing the lower threshold used when deciding weather to
     * select a node as candidate or not.
     */
    SCORE_LOWER_THRESHOLD,
}
