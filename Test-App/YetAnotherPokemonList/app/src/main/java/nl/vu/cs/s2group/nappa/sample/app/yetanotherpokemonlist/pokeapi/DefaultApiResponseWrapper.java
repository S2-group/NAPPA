package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrap the a response of the PokeAPI
 */
public class DefaultApiResponseWrapper {
    /**
     * The total number of entries in this API
     */
    int count;

    /**
     * The URL link for the next pagination
     */
    String next;

    /**
     * The URL link for the previous pagination
     */
    String previous;

    /**
     * An array containing the entries for the current pagination
     */
    List<DefaultApiModel> results;

    public DefaultApiResponseWrapper() {
        results = new ArrayList<>();
    }

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<DefaultApiModel> getResults() {
        return results;
    }

    @NonNull
    @Override
    public String toString() {
        return "ApiResponseWrapper{" +
                "count=" + count +
                ", next='" + next + '\'' +
                ", previous='" + previous + '\'' +
                "results=" + results.toString() +
                '}';
    }
}
