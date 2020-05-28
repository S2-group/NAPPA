package nl.vu.cs.s2group.nappa.room;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents all data corresponding to an http request
 */
@Entity(tableName = "pf_url")
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
