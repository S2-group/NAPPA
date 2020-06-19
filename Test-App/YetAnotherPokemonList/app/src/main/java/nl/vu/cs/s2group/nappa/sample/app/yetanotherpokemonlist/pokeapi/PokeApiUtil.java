package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PokeApiUtil {
    private PokeApiUtil() {
        throw new IllegalStateException("PokeApiUtil is an utility class and should be instantiated!");
    }

    public static List<NamedAPIResource> parseLsitToDefaultApiModel(List<?> wrapperList, String method) {
        List<NamedAPIResource> types = new ArrayList<>();
        for (Object wrapper : wrapperList) {
            NamedAPIResource model = parseObjectToDefaultApiModel(wrapper, method);
            if (model != null) types.add(model);
        }

        return types;
    }

    @Nullable
    public static NamedAPIResource parseObjectToDefaultApiModel(@NonNull Object wrapper, String method) {
        try {
            return (NamedAPIResource) wrapper.getClass().getMethod(method).invoke(wrapper);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
