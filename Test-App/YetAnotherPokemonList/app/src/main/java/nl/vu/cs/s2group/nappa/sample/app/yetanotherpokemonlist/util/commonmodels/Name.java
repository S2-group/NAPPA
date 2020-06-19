package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.commonmodels;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#name
 */
public class Name {
    String name;
    NamedAPIResource language;

    @NonNull
    @Override
    public String toString() {
        return "Name{" +
                "name='" + name + '\'' +
                ", language=" + language +
                '}';
    }

    public String getName() {
        return name;
    }

    public NamedAPIResource getLanguage() {
        return language;
    }
}
