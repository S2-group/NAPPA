package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.type;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi.DefaultApiModel;

public class TypesResponseWrapper {
    int slot;
    DefaultApiModel type;

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

    public DefaultApiModel getType() {
        return type;
    }
}
