package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.annotation.NonNull;

/**
 * Wrap the entries for the {@code /pokemon} API
 */
public class Pokemon {
    /**
     * The name of the pokemon
     */
    String name;

    /**
     * The URL link containing information of the pokemon
     */
    String url;

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
