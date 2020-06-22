package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

public class PokeAPIUtil {
    private static final String LOG_TAG = PokeAPIUtil.class.getSimpleName();

    private PokeAPIUtil() {
        throw new IllegalStateException("APIResourceUtil is an utility class and should not be instantiated!");
    }

    public static List<NamedAPIResource> parseListToNamedAPOResourceList(List<?> wrapperList, String method) {
        List<NamedAPIResource> types = new ArrayList<>();
        if (wrapperList == null) return types;
        for (Object wrapper : wrapperList) {
            NamedAPIResource model = parseObjectToNamedAPIResource(wrapper, method);
            if (model != null) types.add(model);
        }

        return types;
    }

    @Nullable
    public static NamedAPIResource parseObjectToNamedAPIResource(@NonNull Object wrapper, String method) {
        try {
            return (NamedAPIResource) wrapper.getClass().getMethod(method).invoke(wrapper);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Log.e(LOG_TAG, "Failed to parse object to Named API Resource", e);
            return null;
        }
    }

    public static <T> List<T> filterListByLanguage(List<T> list) {
        return filterListByLanguage(list, "en");
    }

    @Nullable
    public static <T> List<T> filterListByLanguage(List<T> list, String language) {
        List<T> filteredList = new ArrayList<>();
        if (list == null) return filteredList;
        try {
            for (T obj : list) {
                NamedAPIResource namedAPIResource = (NamedAPIResource) obj.getClass().getMethod("getLanguage").invoke(obj);
                Objects.requireNonNull(namedAPIResource);
                if (namedAPIResource.getName().equals(language)) {
                    filteredList.add(obj);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Log.e(LOG_TAG, "Failed to find object with language", e);
            return null;
        }
        return filteredList;
    }
}
