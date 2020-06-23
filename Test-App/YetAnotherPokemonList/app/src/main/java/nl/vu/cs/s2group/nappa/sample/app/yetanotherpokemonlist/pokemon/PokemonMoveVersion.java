package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemonmoveversion
 */
public class PokemonMoveVersion {
    NamedAPIResource moveLearnMethod;
    NamedAPIResource versionGroup;
    int levelLearnedAt;

    @NonNull
    @Override
    public String toString() {
        return "PokemonMoveVersion{" +
                "move_learn_method=" + moveLearnMethod +
                ", version_group=" + versionGroup +
                ", level_learned_at=" + levelLearnedAt +
                '}';
    }

    public NamedAPIResource getMoveLearnMethod() {
        return moveLearnMethod;
    }

    public NamedAPIResource getVersionGroup() {
        return versionGroup;
    }

    public int getLevelLearnedAt() {
        return levelLearnedAt;
    }
}
