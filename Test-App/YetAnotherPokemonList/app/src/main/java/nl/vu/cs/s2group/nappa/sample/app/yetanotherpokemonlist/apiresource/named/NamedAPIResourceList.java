package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of https://pokeapi.co/docs/v2#named
 */
public class NamedAPIResourceList {
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
    List<NamedAPIResource> results;

    public NamedAPIResourceList() {
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

    public List<NamedAPIResource> getResults() {
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
