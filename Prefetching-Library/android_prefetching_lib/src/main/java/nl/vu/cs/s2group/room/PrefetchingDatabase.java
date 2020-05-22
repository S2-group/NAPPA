package nl.vu.cs.s2group.room;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import nl.vu.cs.s2group.room.dao.ActivityExtraDao;
import nl.vu.cs.s2group.room.dao.GraphEdgeDao;
import nl.vu.cs.s2group.room.dao.SessionDao;
import nl.vu.cs.s2group.room.dao.UrlCandidateDao;
import nl.vu.cs.s2group.room.data.ActivityExtraData;
import nl.vu.cs.s2group.room.data.LARData;
import nl.vu.cs.s2group.room.data.Session;
import nl.vu.cs.s2group.room.data.SessionData;
import nl.vu.cs.s2group.room.data.UrlCandidate;
import nl.vu.cs.s2group.room.data.UrlCandidateParts;

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
