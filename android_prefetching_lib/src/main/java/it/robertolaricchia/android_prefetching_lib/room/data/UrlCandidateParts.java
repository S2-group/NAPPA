package it.robertolaricchia.android_prefetching_lib.room.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "pf_url_candidate_part")
public class UrlCandidateParts {

    @PrimaryKey(autoGenerate = true) public Long id;
    @ColumnInfo(name = "id_url_candidate") public Long idUrlCandidate;
    @ColumnInfo(name = "url_order") public Integer order;
    @ColumnInfo(name = "type") public Integer type;   // Type represents STATIC or PARAMETER
    @ColumnInfo(name = "url_piece") public String urlPiece;


    public UrlCandidateParts(Long idUrlCandidate, Integer order, Integer type, String urlPiece) {
        this.idUrlCandidate = idUrlCandidate;
        this.order = order;
        this.type = type;
        this.urlPiece = urlPiece;
    }
}
