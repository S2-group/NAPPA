package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemonhelditemversion
 */
public class PokemonHeldItemVersion {
    NamedAPIResource version;
    int rarity;

    @NonNull
    @Override
    public String toString() {
        return "PokemonHeldItemVersion{" +
                "version=" + version +
                ", rarity=" + rarity +
                '}';
    }

    public NamedAPIResource getVersion() {
        return version;
    }

    public int getRarity() {
        return rarity;
    }
}
