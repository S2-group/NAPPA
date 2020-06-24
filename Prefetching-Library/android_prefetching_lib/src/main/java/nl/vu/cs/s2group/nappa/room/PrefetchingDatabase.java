package nl.vu.cs.s2group.nappa.room;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import nl.vu.cs.s2group.nappa.room.converter.DateConverters;
import nl.vu.cs.s2group.nappa.room.dao.ActivityExtraDao;
import nl.vu.cs.s2group.nappa.room.dao.ActivityVisitTimeDao;
import nl.vu.cs.s2group.nappa.room.dao.GraphEdgeDao;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;
import nl.vu.cs.s2group.nappa.room.dao.UrlCandidateDao;
import nl.vu.cs.s2group.nappa.room.data.ActivityExtraData;
import nl.vu.cs.s2group.nappa.room.data.ActivityVisitTime;
import nl.vu.cs.s2group.nappa.room.data.LARData;
import nl.vu.cs.s2group.nappa.room.data.Session;
import nl.vu.cs.s2group.nappa.room.data.SessionData;
import nl.vu.cs.s2group.nappa.room.data.UrlCandidate;
import nl.vu.cs.s2group.nappa.room.data.UrlCandidateParts;

@Database(
        entities = {
                RequestData.class,
                ActivityData.class,
                Session.class,
                SessionData.class,
                ActivityExtraData.class,
                UrlCandidate.class,
                UrlCandidateParts.class,
                LARData.class,
                ActivityVisitTime.class,
        },
        version = 12)
@TypeConverters({DateConverters.class})
public abstract class PrefetchingDatabase extends RoomDatabase {

    private static PrefetchingDatabase instance = null;

    PrefetchingDatabase() {
    }

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

    public abstract ActivityVisitTimeDao activityVisitTimeDao();
}
