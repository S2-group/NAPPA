package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.type;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemontype
 */
public class PokemonType {
    int slot;
    NamedAPIResource type;

    @NonNull
    @Override
    public String toString() {
        return "TypeResponseWrapper{" +
                "slot=" + slot +
                ", type=" + type +
                '}';
    }

    public int getSlot() {
        return slot;
    }

    public NamedAPIResource getType() {
        return type;
    }
}
