package nl.vu.cs.s2group.nappa.handler.activity.extra;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.room.data.ActivityExtraData;

/**
 * Defines a handler to fetch in the database a object containing the aggregate
 * {@link ActivityExtraData} for the provided node. After fetching the data,
 * this handler will register the fetched LiveData object in the provided node.
 */
public class FetchIntentExtraRunnable implements Runnable {
    private static final String LOG_TAG = FetchIntentExtraRunnable.class.getSimpleName();

    ActivityNode activity;

    public FetchIntentExtraRunnable(ActivityNode activity) {
        this.activity = activity;
    }

    @Override
    public void run() {

    }
}
