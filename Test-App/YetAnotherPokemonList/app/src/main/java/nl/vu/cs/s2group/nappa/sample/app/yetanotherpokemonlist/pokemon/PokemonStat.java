package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemonstat
 */
public class PokemonStat {
    NamedAPIResource stat;
    int effort;
    int baseStat;

    @NonNull
    @Override
    public String toString() {
        return "PokemonStat{" +
                "stat=" + stat +
                ", effort=" + effort +
                ", base_stat=" + baseStat +
                '}';
    }

    public NamedAPIResource getStat() {
        return stat;
    }

    public int getEffort() {
        return effort;
    }

    public int getBaseStat() {
        return baseStat;
    }
}
