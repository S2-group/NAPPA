package nl.vu.cs.s2group.room.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.annotation.NonNull;

@Entity(tableName = "pf_session_data", primaryKeys = {"id_session", "id_activity_source", "id_activity_destination"})
public class SessionData {

    @NonNull @ColumnInfo(name = "id_session") public Long idSession;
    @NonNull @ColumnInfo(name = "id_activity_source") public Long idActivitySource;
    @NonNull @ColumnInfo(name = "id_activity_destination") public Long idActivityDestination;
    @ColumnInfo(name = "count_source_destination") public Long countSourceDestination;

    /**
     * Setter for the session data
     * @param idSession Id of the session in which this transition is taking place
     * @param idActivitySource Id of the source activity
     * @param idActivityDestination Id of the destination activity
     * @param countSourceDestination The number of instances this sourc-destination transition has occurred
     */
    public SessionData(@NonNull Long idSession, @NonNull Long idActivitySource, @NonNull Long idActivityDestination, Long countSourceDestination) {
        this.idSession = idSession;
        this.idActivitySource = idActivitySource;
        this.idActivityDestination = idActivityDestination;
        this.countSourceDestination = countSourceDestination;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SessionData:\t");
        sb.append("idSession:").append(idSession)
                .append("\nidActSource: ").append(idActivitySource)
                .append("\nidActDest: ").append(idActivityDestination)
                .append("\ntransitionCount: ").append(countSourceDestination)
                .append("\n\n");
        return sb.toString();
    }
}
