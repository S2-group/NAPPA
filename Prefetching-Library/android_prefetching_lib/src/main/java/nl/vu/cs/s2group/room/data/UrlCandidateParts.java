package nl.vu.cs.s2group.room.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents an individual part of a URL candidate, which can either be a statically defined piece of an URL,
 * or a dynamically defined parameter.
 * <br><br>
 * <B>IF</B> {@code UrlCandidateParts.type == STATIC}, then the {@code UrlCandidateParts.urlPiece} will represent
 * a statically defined fragment of a full URL.
 * <br>
 * <B>ELSE IF</B>  {@code UrlCandidateParts.type == PARAMETER}, then this implies that a fragment of a full URL has also
 * been defined as an EXTRA through intent transitions.  Therefore the {@code UrlCandidateParts.urlPiece} will
 * reference the key of an {@link ActivityExtraData} object
 */
@Entity(tableName = "pf_url_candidate_part")
public class UrlCandidateParts {

    @PrimaryKey(autoGenerate = true) public Long id;
    @ColumnInfo(name = "id_url_candidate") public Long idUrlCandidate;
    @ColumnInfo(name = "url_order") public Integer order;
    @ColumnInfo(name = "type") public Integer type;   // Type represents STATIC or PARAMETER
    @ColumnInfo(name = "url_piece") public String urlPiece;     // If type is STATIC, it represents a statically defined
                                                                //   URL piece.   If type is PARAMETER, then it
                                                                //   represents the key of an ActivityExtraData entity in the database



    public UrlCandidateParts(Long idUrlCandidate, Integer order, Integer type, String urlPiece) {
        this.idUrlCandidate = idUrlCandidate;
        this.order = order;
        this.type = type;
        this.urlPiece = urlPiece;
    }
}
