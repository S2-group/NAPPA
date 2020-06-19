package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

public class AbilityPokemon {
    boolean is_hidden;
    int slot;
    NamedAPIResource pokemon;

    @NonNull
    @Override
    public String toString() {
        return "AbilityPokemon{" +
                "isHidden=" + is_hidden +
                ", slot=" + slot +
                ", pokemon=" + pokemon +
                '}';
    }

    public boolean isHidden() {
        return is_hidden;
    }

    public int getSlot() {
        return slot;
    }

    public NamedAPIResource getPokemon() {
        return pokemon;
    }
}
