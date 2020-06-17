package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ApiDefaultResponseWrapper extends ApiResponseWrapper {
    /**
     * An array containing the entries for the current pagination
     */
    protected List<ApiModel> results;

    public ApiDefaultResponseWrapper() {
        results = new ArrayList<>();
    }

    public List<ApiModel> getResults() {
        return results;
    }

    @NonNull
    @Override
    public String toString() {
        return "ApiDefaultResponseWrapper{" +
                "results=" + results +
                "} " + super.toString();
    }
}
