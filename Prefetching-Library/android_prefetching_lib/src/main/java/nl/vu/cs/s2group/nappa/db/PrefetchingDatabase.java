package nl.vu.cs.s2group.nappa.db;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import org.jetbrains.annotations.Contract;

import nl.vu.cs.s2group.nappa.db.room.graph.ActivityDao;
import nl.vu.cs.s2group.nappa.db.room.graph.Activity;
import nl.vu.cs.s2group.nappa.db.room.graph.ActivityExtra;
import nl.vu.cs.s2group.nappa.db.room.graph.ActivityExtraDao;
import nl.vu.cs.s2group.nappa.db.room.graph.EdgeDao;
import nl.vu.cs.s2group.nappa.db.room.session.SessionContent;
import nl.vu.cs.s2group.nappa.db.room.session.SessionDao;
import nl.vu.cs.s2group.nappa.db.dao.UrlCandidateDao;
import nl.vu.cs.s2group.nappa.db.data.LARData;
import nl.vu.cs.s2group.nappa.db.room.session.Session;
import nl.vu.cs.s2group.nappa.db.data.UrlCandidate;
import nl.vu.cs.s2group.nappa.db.data.UrlCandidateParts;

@Database(entities = {
        RequestData.class, Activity.class, Session.class, SessionContent.class, ActivityExtra.class,
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

    @Contract(pure = true)
    public static PrefetchingDatabase getInstance() {
        return instance;
    }

    public abstract AggregateUrlDao urlDao();
    public abstract ActivityDao activityDao();
    public abstract SessionDao sessionDao();
    public abstract EdgeDao graphEdgeDao();
    public abstract ActivityExtraDao activityExtraDao();
    public abstract UrlCandidateDao urlCandidateDao();
}
