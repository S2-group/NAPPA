package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.commonmodels.Name;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.commonmodels.VerboseEffect;

/**
 * Representation of https://pokeapi.co/docs/v2#ability
 */
public class Ability {
    int id;
    String name;
    boolean isMainSeries;
    NamedAPIResource generation;
    List<Name> names;
    List<VerboseEffect> effectEntries;
    List<AbilityEffectChange> effectChanges;
    List<AbilityFlavorText> flavorTextEntries;
    List<AbilityPokemon> pokemon;

    @NonNull
    @Override
    public String toString() {
        return "Ability{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isMainSeries=" + isMainSeries +
                ", generation=" + generation +
                ", names=" + names +
                ", effectEntries=" + effectEntries +
                ", effectChanges=" + effectChanges +
                ", flavorTextEntries=" + flavorTextEntries +
                ", pokemon=" + pokemon +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isMainSeries() {
        return isMainSeries;
    }

    public NamedAPIResource getGeneration() {
        return generation;
    }

    public List<Name> getNames() {
        return names;
    }

    public List<VerboseEffect> getEffectEntries() {
        return effectEntries;
    }

    public List<AbilityEffectChange> getEffectChanges() {
        return effectChanges;
    }

    public List<AbilityFlavorText> getFlavorTextEntries() {
        return flavorTextEntries;
    }

    public List<AbilityPokemon> getPokemon() {
        return pokemon;
    }
}
