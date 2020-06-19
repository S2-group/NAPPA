package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.commonmodels;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#generationgameindex
 */
public class GenerationGameIndex {
    int game_index;
    NamedAPIResource generation;

    @NonNull
    @Override
    public String toString() {
        return "GenerationGameIndex{" +
                "game_index=" + game_index +
                ", generation=" + generation +
                '}';
    }

    public int getGameIndex() {
        return game_index;
    }

    public NamedAPIResource getGeneration() {
        return generation;
    }
}
