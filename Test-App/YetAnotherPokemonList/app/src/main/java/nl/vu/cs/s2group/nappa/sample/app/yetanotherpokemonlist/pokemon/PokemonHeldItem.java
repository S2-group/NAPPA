package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemonhelditem
 */
public class PokemonHeldItem {
    NamedAPIResource item;
    List<PokemonHeldItemVersion> versionDetails;

    @NonNull
    @Override
    public String toString() {
        return "PokemonHeldItem{" +
                "item=" + item +
                ", versionDetails=" + versionDetails +
                '}';
    }

    public NamedAPIResource getItem() {
        return item;
    }

    public List<PokemonHeldItemVersion> getVersionDetails() {
        return versionDetails;
    }
}
