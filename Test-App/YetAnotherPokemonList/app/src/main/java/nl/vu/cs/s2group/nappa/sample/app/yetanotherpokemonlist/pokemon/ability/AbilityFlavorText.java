package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#abilityflavortext
 */
public class AbilityFlavorText {
    String flavor_text;
    NamedAPIResource language;
    NamedAPIResource version_group;

    @NonNull
    @Override
    public String toString() {
        return "AbilityFlavorText{" +
                "flavorText='" + flavor_text + '\'' +
                ", language=" + language +
                ", versionGroup=" + version_group +
                '}';
    }

    public String getFlavorText() {
        return flavor_text;
    }

    public NamedAPIResource getLanguage() {
        return language;
    }

    public NamedAPIResource getVersionGroup() {
        return version_group;
    }
}
