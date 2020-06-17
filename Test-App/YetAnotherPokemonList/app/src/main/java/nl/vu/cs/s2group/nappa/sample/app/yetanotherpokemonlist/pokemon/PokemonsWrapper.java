package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import java.util.ArrayList;
import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http.ApiResponseWrapper;

public class PokemonsWrapper extends ApiResponseWrapper {
    /**
     * An array containing the entries for the current pagination
     */
    protected List<Pokemon> results;

    public PokemonsWrapper() {
        results = new ArrayList<>();
    }

    public List<Pokemon> getResults() {
        return results;
    }
}
