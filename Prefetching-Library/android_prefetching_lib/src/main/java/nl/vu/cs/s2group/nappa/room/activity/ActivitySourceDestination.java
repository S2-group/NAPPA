package nl.vu.cs.s2group.nappa.room.activity;

import androidx.room.DatabaseView;

/**
 * This view maps all links recorded between activities
 */
@DatabaseView(viewName = "nappa_view_activity_source_destination",
        value = "SELECT DISTINCT " +
                "	nappa_activity_a.activity_name AS sourceActivityName, " +
                "	nappa_activity_b.activity_name AS destinationActivityName, " +
                "	id_activity_source AS sourceActivityID, " +
                "	id_activity_destination AS destinationActivityID " +
                "FROM nappa_session_data " +
                "	INNER JOIN " +
                "		nappa_activity AS nappa_activity_a " +
                "		ON id_activity_source = nappa_activity_a.id " +
                "	INNER JOIN " +
                "		nappa_activity AS nappa_activity_b " +
                "		ON id_activity_destination = nappa_activity_b.id ")
public class ActivitySourceDestination {
    public String sourceActivityName;
    public String destinationActivityName;

    public int sourceActivityID;
    public int destinationActivityID;
}
