package nl.vu.cs.s2group.nappa.prefetch;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class AbstractPrefetchingStrategy {
    protected int maxNumberOfUrlToPrefetch;

    public AbstractPrefetchingStrategy(@NotNull Map<PrefetchingStrategyConfigKeys, Object> config) {
        Object data;

        data = config.get(PrefetchingStrategyConfigKeys.MAX_URL_TO_PREFETCH);
        maxNumberOfUrlToPrefetch = data != null ? Integer.parseInt(data.toString()) : 2;
    }
}
