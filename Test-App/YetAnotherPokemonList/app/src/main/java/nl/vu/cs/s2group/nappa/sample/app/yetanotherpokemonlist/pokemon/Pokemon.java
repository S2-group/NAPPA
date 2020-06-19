package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemon
 */
public class Pokemon {
    int id;
    String name;
    List<PokemonType> types;
    List<PokemonAbility> abilities;
    List<PokemonStat> stats;
    List<PokemonMove> moves;
    List<PokemonHeldItem> heldItems;
    PokemonSprites sprites;
    boolean isDefault;
    int height;
    int weight;
    int baseExperience;

    @NonNull
    @Override
    public String toString() {
        return "Pokemon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", types=" + types +
                ", abilities=" + abilities +
                ", stats=" + stats +
                ", moves=" + moves +
                ", heldItems=" + heldItems +
                ", sprites=" + sprites +
                ", isDefault=" + isDefault +
                ", height=" + height +
                ", weight=" + weight +
                ", base_experience=" + baseExperience +
                '}';
    }

    public List<PokemonHeldItem> getHeldItems() {
        return heldItems;
    }

    public PokemonSprites getSprites() {
        return sprites;
    }

    public List<PokemonMove> getMoves() {
        return moves;
    }

    public String getName() {
        return name;
    }

    public List<PokemonType> getTypes() {
        return types;
    }

    public List<PokemonAbility> getAbilities() {
        return abilities;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public int getBaseExperience() {
        return baseExperience;
    }

    public List<PokemonStat> getStats() {
        return stats;
    }

    public int getId() {
        return id;
    }
}
