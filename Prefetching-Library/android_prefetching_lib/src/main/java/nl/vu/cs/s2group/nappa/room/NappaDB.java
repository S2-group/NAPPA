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

// TODO Refactor the Room classes organization
//  The current structure is messy and falling apart. The Dao/Entity/View model could be
//  replaced in favor of the domain/feature model as done for the `activityVisitTime` feature.
//  This organization allows to keep together classes that are related to each other, making
//  maintenance simpler.
//  While we are at it, consider renaming:
//  * The class `SessionData` to `ActivityVisitFrequency` or simply `VisitFrequency`;
//  * The entity classes `MyEntityData` to just `MyEntity`;
//  Other considerations:
//  * Enforcing required data by either making entities attributes type primitive or annotating object types with @NonNull;
//  * Enforcing foreign keys at the database level --> currently only enforced in the application levels. Adding invalid FK is currently allowed;
//  * Extracting model class `UrlCandidateToUrlParameter` from `UrlCandidateDao`;
//  * Extracting model class `SessionAggregate` from `SessionDao`;
//  * Using DatabaseView to simplify some queries;
//  * Extracting `LAR` operations from `ActivityTableDao` --> LAR refers to PageRank, HITS and Salsa scores, but I am not sure what the acronym means;
//  * Resolve warnings listed when building the library;
//  The model classes extraction is motivated to simplify code reading. It is a lot better to read
//  `List<SessionAggregate>` than `List<SessionDao.SessionAggregate>` .
//  There are probably other improvements and considerations to take into account, but this is the
//  preliminary list after working in the NAPPA library for a few weeks.
//  If not familiar with the database, I recommend using an app with NAPPA enabled for a while
//  to create data and then open the database in a SQL tool (e.g., DB Browser for SQLite).
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
