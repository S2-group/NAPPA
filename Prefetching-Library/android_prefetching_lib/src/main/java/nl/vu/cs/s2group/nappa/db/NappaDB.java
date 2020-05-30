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
import nl.vu.cs.s2group.nappa.db.room.http.RequestData;
import nl.vu.cs.s2group.nappa.db.room.session.SessionContent;
import nl.vu.cs.s2group.nappa.db.room.session.SessionDao;
import nl.vu.cs.s2group.nappa.db.room.http.AggregateUrlDao;
import nl.vu.cs.s2group.nappa.db.room.http.UrlCandidateDao;
import nl.vu.cs.s2group.nappa.db.data.LARData;
import nl.vu.cs.s2group.nappa.db.room.session.Session;
import nl.vu.cs.s2group.nappa.db.room.http.UrlCandidate;
import nl.vu.cs.s2group.nappa.db.room.http.UrlCandidateParts;

@Database(
        entities = {
                RequestData.class,
                Activity.class,
                Session.class,
                SessionContent.class,
                ActivityExtra.class,
                UrlCandidate.class,
                UrlCandidateParts.class,
                LARData.class
        },
        version = 10)
public abstract class NappaDB extends RoomDatabase {

    private static NappaDB instance = null;
    NappaDB(){}

    public static NappaDB getInstance(Context context) {
        if (instance == null)
            synchronized (NappaDB.class) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        NappaDB.class, "nappa_db")
                        //TODO remove and provide migrations in production
                        .fallbackToDestructiveMigration()
                        .build();
            }
        return instance;
    }

    @Contract(pure = true)
    public static NappaDB getInstance() {
        return instance;
    }

    public abstract AggregateUrlDao urlDao();
    public abstract ActivityDao activityDao();
    public abstract SessionDao sessionDao();
    public abstract EdgeDao graphEdgeDao();
    public abstract ActivityExtraDao activityExtraDao();
    public abstract UrlCandidateDao urlCandidateDao();
}
