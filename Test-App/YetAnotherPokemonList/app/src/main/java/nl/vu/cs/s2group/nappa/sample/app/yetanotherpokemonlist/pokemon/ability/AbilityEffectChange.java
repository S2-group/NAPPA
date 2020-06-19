package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.commonmodels.Effect;

/**
 * Representation of https://pokeapi.co/docs/v2#abilityeffectchange
 */
public class AbilityEffectChange {
    List<Effect> effectEntries;
    NamedAPIResource versionGroup;

    @NonNull
    @Override
    public String toString() {
        return "AbilityEffectChange{" +
                "effectEntries=" + effectEntries +
                ", versionGroup=" + versionGroup +
                '}';
    }

    public List<Effect> getEffectEntries() {
        return effectEntries;
    }

    public NamedAPIResource getVersionGroup() {
        return versionGroup;
    }
}
