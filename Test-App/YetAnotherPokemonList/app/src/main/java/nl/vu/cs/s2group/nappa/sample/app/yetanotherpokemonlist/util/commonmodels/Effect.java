package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.commonmodels;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#effect
 */
public class Effect {
    String effect;
    NamedAPIResource language;

    @NonNull
    @Override
    public String toString() {
        return "Effect{" +
                "effect='" + effect + '\'' +
                ", language=" + language +
                '}';
    }

    public String getEffect() {
        return effect;
    }

    public NamedAPIResource getLanguage() {
        return language;
    }
}
