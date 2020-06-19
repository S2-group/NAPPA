package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

public class APIResourceUtil {
    private APIResourceUtil() {
        throw new IllegalStateException("APIResourceUtil is an utility class and should be instantiated!");
    }

    public static List<NamedAPIResource> parseListToNamedAPOResourceList(List<?> wrapperList, String method) {
        List<NamedAPIResource> types = new ArrayList<>();
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
            e.printStackTrace();
            return null;
        }
    }
}
