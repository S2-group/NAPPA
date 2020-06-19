package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability.AbilitiesResponseWrapper;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.type.TypesResponseWrapper;

public class Pokemon {
    String name;
    List<TypesResponseWrapper> types;
    List<AbilitiesResponseWrapper> abilities;

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
