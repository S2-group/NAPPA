package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

public class Pokemon {
    String name;

    @NonNull
    @Override
    public String toString() {
        return "Pokemon{" +
                "name='" + name + '\'' +
                '}';
    }
}
