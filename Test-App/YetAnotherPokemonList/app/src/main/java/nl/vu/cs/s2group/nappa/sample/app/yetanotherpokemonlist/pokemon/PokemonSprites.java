package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

/**
 * Representation of https://pokeapi.co/docs/v2#pokemonsprites
 */
public class PokemonSprites {
    String frontDefault;
    String frontShiny;
    String frontFemale;
    String frontShinyFemale;
    String backDefault;
    String backShiny;
    String backFemale;
    String backShinyFemale;

    @NonNull
    @Override
    public String toString() {
        return "PokemonSprites{" +
                "front_default='" + frontDefault + '\'' +
                ", front_shiny='" + frontShiny + '\'' +
                ", front_female='" + frontFemale + '\'' +
                ", front_shiny_female='" + frontShinyFemale + '\'' +
                ", back_default='" + backDefault + '\'' +
                ", back_shiny='" + backShiny + '\'' +
                ", back_female='" + backFemale + '\'' +
                ", back_shiny_female='" + backShinyFemale + '\'' +
                '}';
    }

    public String getFrontShiny() {
        return frontShiny;
    }

    public String getFrontFemale() {
        return frontFemale;
    }

    public String getFrontShinyFemale() {
        return frontShinyFemale;
    }

    public String getBackDefault() {
        return backDefault;
    }

    public String getBackShiny() {
        return backShiny;
    }

    public String getBackFemale() {
        return backFemale;
    }

    public String getBackShinyFemale() {
        return backShinyFemale;
    }

    public String getFrontDefault() {
        return frontDefault;
    }
}
