package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemonmove
 */
public class PokemonMove {
    NamedAPIResource move;
    List<PokemonMoveVersion> versionGroupDetails;

    @NonNull
    @Override
    public String toString() {
        return "PokemonMove{" +
                "move=" + move +
                ", version_group_details=" + versionGroupDetails +
                '}';
    }

    public NamedAPIResource getMove() {
        return move;
    }

    public List<PokemonMoveVersion> getVersionGroupDetails() {
        return versionGroupDetails;
    }
}
