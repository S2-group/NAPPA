package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.commonmodels.Effect;

/**
 * Representation of https://pokeapi.co/docs/v2#abilityeffectchange
 */
public class AbilityEffectChange {
    List<Effect> effect_entries;
    NamedAPIResource version_group;

    @NonNull
    @Override
    public String toString() {
        return "AbilityEffectChange{" +
                "effectEntries=" + effect_entries +
                ", versionGroup=" + version_group +
                '}';
    }

    public List<Effect> getEffectEntries() {
        return effect_entries;
    }

    public NamedAPIResource getVersionGroup() {
        return version_group;
    }
}
