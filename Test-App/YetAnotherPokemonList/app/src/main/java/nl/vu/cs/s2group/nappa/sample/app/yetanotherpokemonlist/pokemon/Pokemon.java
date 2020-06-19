package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability.PokemonAbility;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.type.PokemonType;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemon
 */
public class Pokemon {
    String name;
    List<PokemonType> types;
    List<PokemonAbility> abilities;

    @NonNull
    @Override
    public String toString() {
        return "Pokemon{" +
                "name='" + name + '\'' +
                ", types=" + types +
                ", abilities=" + abilities +
                '}';
    }
}
