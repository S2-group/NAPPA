package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#abilityflavortext
 */
public class AbilityFlavorText {
    String flavorText;
    NamedAPIResource language;
    NamedAPIResource versionGroup;

    @NonNull
    @Override
    public String toString() {
        return "AbilityFlavorText{" +
                "flavorText='" + flavorText + '\'' +
                ", language=" + language +
                ", versionGroup=" + versionGroup +
                '}';
    }

    public String getFlavorText() {
        return flavorText;
    }

    public NamedAPIResource getLanguage() {
        return language;
    }

    public NamedAPIResource getVersionGroup() {
        return versionGroup;
    }
}
