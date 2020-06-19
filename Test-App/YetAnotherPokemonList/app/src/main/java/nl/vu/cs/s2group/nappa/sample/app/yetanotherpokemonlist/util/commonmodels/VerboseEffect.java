package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.commonmodels;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#verboseeffect
 */
public class VerboseEffect {
    String effect;
    String shortEffect;
    NamedAPIResource language;

    @NonNull
    @Override
    public String toString() {
        return "VerboseEffect{" +
                "effect='" + effect + '\'' +
                ", shortEffect='" + shortEffect + '\'' +
                ", language=" + language +
                '}';
    }

    public String getEffect() {
        return effect;
    }

    public String getShortEffect() {
        return shortEffect;
    }

    public NamedAPIResource getLanguage() {
        return language;
    }
}
