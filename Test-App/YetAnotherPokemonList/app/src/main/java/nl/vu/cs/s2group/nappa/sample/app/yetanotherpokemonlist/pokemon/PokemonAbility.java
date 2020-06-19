package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemonability
 */
public class PokemonAbility {
    int slot;
    boolean isHidden;
    NamedAPIResource ability;

    @NonNull
    @Override
    public String toString() {
        return "AbilitiesResponseWrapper{" +
                "slot=" + slot +
                ", is_hidden=" + isHidden +
                ", ability=" + ability +
                '}';
    }

    public int getSlot() {
        return slot;
    }

    public NamedAPIResource getAbility() {
        return ability;
    }

    public boolean isHidden() {
        return isHidden;
    }
}
