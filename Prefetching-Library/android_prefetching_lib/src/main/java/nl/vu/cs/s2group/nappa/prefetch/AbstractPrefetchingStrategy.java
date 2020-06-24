package nl.vu.cs.s2group.nappa.prefetch;

import java.util.Map;

public abstract class AbstractPrefetchingStrategy {
    protected int maxNumberOfUrlToPrefetch;

    public AbstractPrefetchingStrategy(Map<PrefetchingStrategyConfigKeys, Object> config) {
        if (config.containsKey(PrefetchingStrategyConfigKeys.MAX_URL_TO_PREFETCH)) {

        }
    }
}
