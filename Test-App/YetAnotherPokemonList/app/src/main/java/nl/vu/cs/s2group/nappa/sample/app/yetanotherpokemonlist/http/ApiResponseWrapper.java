package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrap the a response of the PokeAPI
 */
public class ApiResponseWrapper {
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
    List<?> results;

    public ApiResponseWrapper() {
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

    public List<?> getResults() {
        return results;
    }
}
