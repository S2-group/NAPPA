package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi.DefaultApiModel;

public class AbilitiesResponseWrapper {
    int slot;
    boolean is_hidden;
    DefaultApiModel ability;

    @NonNull
    @Override
    public String toString() {
        return "AbilitiesResponseWrapper{" +
                "slot=" + slot +
                ", is_hidden=" + is_hidden +
                ", ability=" + ability +
                '}';
    }

    public int getSlot() {
        return slot;
    }

    public DefaultApiModel getAbility() {
        return ability;
    }

    public boolean isHidden() {
        return is_hidden;
    }
}
