package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

// TODO Consider the possibility of merging ActivityVisitTime and SessionData
//  The initial idea of the Entity ActivityVisitTime was to store the time a user spent in a given activity. This was introduced in PR S2-group/NAPPA#62 for storing the time and updated in PR S2-group/NAPPA#67 for adjustments needed for the prefetching strategy GreedyPrefetchingStrategyOnVisitFrequencyAndTime. It worked well and it had different responsibilities from the Entity SessionData.
//  However, with PR S2-group/NAPPA#86 for prefetching strategy TFPRPrefetchingStrategy, we had to add a new information to this entity, namely, from which activity we navigated from. With this new addition, both Entities are almost identical, differing only on the specific information stored (frequency VS time) and the design choice on when and how often the information is inserted.
//  The information is stored in the Entity SessionData when navigating to a activity from another activity (skips the first activity from launching the app) and stores one row per session and source/destination activity. When this navigation happens again in the same session, the row is updated instead of inserting a new row.
//  In the Entity ActivityVisitTime, however, the information is stored when leaving an activity, either to navigate to another activity or closing the app. Furthermore, for this entity, it was opted to inserting a new row is inserted whenever new information is obtained instead of updating an existing row. While this is not used at the moment, this design allows to having measures such as average and medium since we don't store an aggregate visit time.
//  With this in mind, we could either maintain both Entities, or merge them in a single Entity. In this case, it must be discussed which design decisions will be kept and which will be discarded. When merging both Entities, only the Entity and Dao should be merged. The aggregated DatabaseView and classes should be kept separated as they will serve to different purposes.
//  This issue might be related to issue S2-group/NAPPA#82.

/**
 * Represents the database table that register the time a user spends visiting activities
 */
@Entity(tableName = "pf_activity_visit_time",
        indices = {
                @Index("activity_name"),
                @Index("from_activity"),
                @Index("id_session"),
        })
public class ActivityVisitTime {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "activity_name")
    public String activityName;

    /**
     * The previous activity can be null for the first activity accessed when launching the app
     */
    @ColumnInfo(name = "from_activity")
    public String fromActivity;

    @ColumnInfo(name = "id_session")
    public long sessionId;

    /**
     * The date this activity was accessed
     */
    @NonNull
    public Date timestamp;

    /**
     * The visit duration in millisecond
     */
    public long duration;

    public ActivityVisitTime(@NonNull String activityName,
                             String fromActivity,
                             long sessionId,
                             @NonNull Date timestamp,
                             long duration) {
        this.activityName = activityName;
        this.fromActivity = fromActivity;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.duration = duration;
    }
}
