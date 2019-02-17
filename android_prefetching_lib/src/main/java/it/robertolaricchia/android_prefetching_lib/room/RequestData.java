package it.robertolaricchia.android_prefetching_lib.room;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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
