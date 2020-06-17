package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi;

import androidx.annotation.NonNull;

public class ApiDefaultModel implements ApiModel {
    String name;
    String url;

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

}
