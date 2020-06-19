package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemonsprites
 */
public class PokemonSprites {
    String front_default;
    String front_shiny;
    String front_female;
    String front_shiny_female;
    String back_default;
    String back_shiny;
    String back_female;
    String back_shiny_female;

    @NonNull
    @Override
    public String toString() {
        return "PokemonSprites{" +
                "front_default='" + front_default + '\'' +
                ", front_shiny='" + front_shiny + '\'' +
                ", front_female='" + front_female + '\'' +
                ", front_shiny_female='" + front_shiny_female + '\'' +
                ", back_default='" + back_default + '\'' +
                ", back_shiny='" + back_shiny + '\'' +
                ", back_female='" + back_female + '\'' +
                ", back_shiny_female='" + back_shiny_female + '\'' +
                '}';
    }

    public String getFront_shiny() {
        return front_shiny;
    }

    public String getFront_female() {
        return front_female;
    }

    public String getFront_shiny_female() {
        return front_shiny_female;
    }

    public String getBack_default() {
        return back_default;
    }

    public String getBack_shiny() {
        return back_shiny;
    }

    public String getBack_female() {
        return back_female;
    }

    public String getBack_shiny_female() {
        return back_shiny_female;
    }

    public String getFront_default() {
        return front_default;
    }
}
