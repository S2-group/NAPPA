package nl.vu.cs.s2group.nappa.handler.session;

import nl.vu.cs.s2group.nappa.room.data.SessionData;

/**
 * Defines a handler to fetch in the database a object containing the {@link SessionData}
 * for the provided node. After fetching the data, this handler will register the LiveData
 * object for the provide node.
 */
public class FetchSessionDataHandler {
    private static final String LOG_TAG = FetchSessionDataHandler.class.getSimpleName();

    private FetchSessionDataHandler() {
        throw new IllegalStateException(LOG_TAG + " is a handler class and should not be instantiated!");
    }
}
