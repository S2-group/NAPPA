package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrap the a response of the PokeAPI
 */
public abstract class ApiResponseWrapper {
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

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public abstract List<?> getResults();
}
