package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http;

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
    Object[] results;
}
