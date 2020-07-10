package nl.vu.cs.s2group.nappa.room;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import nl.vu.cs.s2group.nappa.room.activity.ActivitySourceDestination;
import nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTime;
import nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTimeDao;
import nl.vu.cs.s2group.nappa.room.activity.visittime.AggregateVisitTimeBySession;
import nl.vu.cs.s2group.nappa.room.activity.visittime.SuccessorsAggregateVisitTimeBySession;
import nl.vu.cs.s2group.nappa.room.converter.DateConverters;
import nl.vu.cs.s2group.nappa.room.dao.ActivityExtraDao;
import nl.vu.cs.s2group.nappa.room.dao.GraphEdgeDao;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;
import nl.vu.cs.s2group.nappa.room.dao.UrlCandidateDao;
import nl.vu.cs.s2group.nappa.room.data.ActivityExtraData;
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
        views = {
                AggregateVisitTimeBySession.class,
                SuccessorsAggregateVisitTimeBySession.class,
                ActivitySourceDestination.class,
        },
        version = 1)
@TypeConverters({DateConverters.class})
public abstract class NappaDB extends RoomDatabase {

    private static NappaDB instance = null;

    NappaDB() {
    }

    public static void init(Context context) {
        if (instance == null)
            synchronized (NappaDB.class) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        NappaDB.class, "nappa.db")
                        //TODO remove and provide migrations in production
                        .fallbackToDestructiveMigration()
                        .build();
            }
    }

    public static NappaDB getInstance() {
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
