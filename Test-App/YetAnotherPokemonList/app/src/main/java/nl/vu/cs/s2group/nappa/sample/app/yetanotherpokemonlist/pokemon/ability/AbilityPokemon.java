package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

public class AbilityPokemon {
    boolean isHidden;
    int slot;
    NamedAPIResource pokemon;

    @NonNull
    @Override
    public String toString() {
        return "AbilityPokemon{" +
                "isHidden=" + isHidden +
                ", slot=" + slot +
                ", pokemon=" + pokemon +
                '}';
    }

    public boolean isHidden() {
        return isHidden;
    }

    public int getSlot() {
        return slot;
    }

    public NamedAPIResource getPokemon() {
        return pokemon;
    }
}
