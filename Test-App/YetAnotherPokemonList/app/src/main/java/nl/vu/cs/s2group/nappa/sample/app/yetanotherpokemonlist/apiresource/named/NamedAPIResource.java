package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named;

import androidx.annotation.NonNull;

/**
 * Representation of https://pokeapi.co/docs/v2#namedapiresource
 */
public class NamedAPIResource {
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
