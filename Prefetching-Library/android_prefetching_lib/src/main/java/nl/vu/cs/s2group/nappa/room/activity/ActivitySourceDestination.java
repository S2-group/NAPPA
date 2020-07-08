package nl.vu.cs.s2group.nappa.room.activity;

import androidx.room.DatabaseView;

/**
 * This view maps all links recorded between activities
 */
@DatabaseView(viewName = "pf_view_activity_source_destination",
        value = "SELECT DISTINCT " +
                "	pf_activity_a.activity_name AS sourceActivityName, " +
                "	pf_activity_b.activity_name AS destinationActivityName, " +
                "	id_activity_source AS sourceActivityID, " +
                "	id_activity_destination AS destinationActivityID " +
                "FROM pf_session_data " +
                "	INNER JOIN " +
                "		pf_activity AS pf_activity_a " +
                "		ON id_activity_source = pf_activity_a.id " +
                "	INNER JOIN " +
                "		pf_activity AS pf_activity_b " +
                "		ON id_activity_destination = pf_activity_b.id ")
public class ActivitySourceDestination {
    public String sourceActivityName;
    public String destinationActivityName;

    public int sourceActivityID;
    public int destinationActivityID;
}
