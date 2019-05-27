package it.robertolaricchia.android_prefetching_lib.room;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import it.robertolaricchia.android_prefetching_lib.room.dao.ActivityExtraDao;
import it.robertolaricchia.android_prefetching_lib.room.dao.GraphEdgeDao;
import it.robertolaricchia.android_prefetching_lib.room.dao.SessionDao;
import it.robertolaricchia.android_prefetching_lib.room.dao.UrlCandidateDao;
import it.robertolaricchia.android_prefetching_lib.room.data.ActivityExtraData;
import it.robertolaricchia.android_prefetching_lib.room.data.LARData;
import it.robertolaricchia.android_prefetching_lib.room.data.Session;
import it.robertolaricchia.android_prefetching_lib.room.data.SessionData;
import it.robertolaricchia.android_prefetching_lib.room.data.UrlCandidate;
import it.robertolaricchia.android_prefetching_lib.room.data.UrlCandidateParts;

@Database(entities = {
        RequestData.class, ActivityData.class, Session.class, SessionData.class, ActivityExtraData.class,
        UrlCandidate.class, UrlCandidateParts.class, LARData.class},
        version = 10)
public abstract class PrefetchingDatabase extends RoomDatabase {

    private static PrefetchingDatabase instance = null;
    PrefetchingDatabase(){}

    public static PrefetchingDatabase getInstance(Context context) {
        if (instance == null)
            synchronized (PrefetchingDatabase.class) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        PrefetchingDatabase.class, "pf_db")
                        //TODO remove and provide migrations in production
                        .fallbackToDestructiveMigration()
                        .build();
            }
        return instance;
    }

    public static PrefetchingDatabase getInstance() {
        return instance;
    }

    public abstract AggregateUrlDao urlDao();
    public abstract ActivityTableDao activityDao();
    public abstract SessionDao sessionDao();
    public abstract GraphEdgeDao graphEdgeDao();
    public abstract ActivityExtraDao activityExtraDao();
    public abstract UrlCandidateDao urlCandidateDao();
}
