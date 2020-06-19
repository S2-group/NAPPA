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
    PokemonSprites sprites;
    boolean is_default;
    int height;
    int weight;
    int base_experience;

    @NonNull
    @Override
    public String toString() {
        return "Pokemon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", types=" + types +
                ", abilities=" + abilities +
                ", stats=" + stats +
                ", sprites=" + sprites +
                ", is_default=" + is_default +
                ", height=" + height +
                ", weight=" + weight +
                ", base_experience=" + base_experience +
                '}';
    }

    public PokemonSprites getSprites() {
        return sprites;
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

    public boolean isIs_default() {
        return is_default;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public int getBase_experience() {
        return base_experience;
    }

    public List<PokemonStat> getStats() {
        return stats;
    }

    public int getId() {
        return id;
    }
}
