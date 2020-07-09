package nl.vu.cs.s2group.nappa.room;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// TODO Observed a record containing id_activity = null in the database
//  Upon analyzing the database data after running tests, it was observed a record
//  in the table `nappa_url` containing a null `id_activity`.
//  The id_activity should always be present, otherwise the record will become a
//  orphan record since this Entity is always filtered based on the activity ID.
//  This Entity records are created in the CustomInterceptor class. Try to replicate
//  this occurrence and investigate why a null ID was recorded.
//  Seems like the new object is created with null ID. Investigate why that is the case
//  and if this needs to be changed

/**
 * Represents all data corresponding to an http request
 */
@Entity(tableName = "nappa_url")
public class RequestData {

    @PrimaryKey(autoGenerate = true) public Long id;
    @ColumnInfo(name = "id_activity") public Long idActivity;
    @ColumnInfo(name = "url") String url;
    @ColumnInfo(name = "mime_type") public String mimeType;
    @ColumnInfo(name = "size") public Long size;
    @ColumnInfo(name = "time_issued") public Long timeIssued;


    public RequestData(Long id, Long idActivity, String url, String mimeType, Long size, Long timeIssued) {
        this.id = id;
        this.idActivity = idActivity;
        this.url = url;
        this.mimeType = mimeType;
        this.size = size;
        this.timeIssued = timeIssued;
    }
}
