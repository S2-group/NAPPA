package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemonstat
 */
public class PokemonStat {
    NamedAPIResource stat;
    int effort;
    int base_stat;

    @NonNull
    @Override
    public String toString() {
        return "PokemonStat{" +
                "stat=" + stat +
                ", effort=" + effort +
                ", base_stat=" + base_stat +
                '}';
    }

    public NamedAPIResource getStat() {
        return stat;
    }

    public int getEffort() {
        return effort;
    }

    public int getBase_stat() {
        return base_stat;
    }
}
