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

    public static List<DefaultApiModel> parseLsitToDefaultApiModel(List<?> wrapperList, String method) {
        List<DefaultApiModel> types = new ArrayList<>();
        for (Object wrapper : wrapperList) {
            DefaultApiModel model = parseObjectToDefaultApiModel(wrapper, method);
            if (model != null) types.add(model);
        }

        return types;
    }

    @Nullable
    public static DefaultApiModel parseObjectToDefaultApiModel(@NonNull Object wrapper, String method) {
        try {
            return (DefaultApiModel) wrapper.getClass().getMethod(method).invoke(wrapper);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
