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
    boolean is_main_series;
    NamedAPIResource generation;
    List<Name> names;
    List<VerboseEffect> effect_entries;
    List<AbilityEffectChange> effect_changes;
    List<AbilityFlavorText> flavor_text_entries;
    List<AbilityPokemon> pokemon;

    @NonNull
    @Override
    public String toString() {
        return "Ability{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isMainSeries=" + is_main_series +
                ", generation=" + generation +
                ", names=" + names +
                ", effectEntries=" + effect_entries +
                ", effectChanges=" + effect_changes +
                ", flavorTextEntries=" + flavor_text_entries +
                ", pokemon=" + pokemon +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isIsMainSeries() {
        return is_main_series;
    }

    public NamedAPIResource getGeneration() {
        return generation;
    }

    public List<Name> getNames() {
        return names;
    }

    public List<VerboseEffect> getEffectEntries() {
        return effect_entries;
    }

    public List<AbilityEffectChange> getEffectChanges() {
        return effect_changes;
    }

    public List<AbilityFlavorText> getFlavorTextEntries() {
        return flavor_text_entries;
    }

    public List<AbilityPokemon> getPokemon() {
        return pokemon;
    }
}
