package nl.vu.cs.s2group.nappa.db.room.http;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UrlCandidateDao {
    String LOG_TAG = UrlCandidateDao.class.getSimpleName();

    @Insert
    Long insertUrlCandidate(UrlCandidate urlCandidate);

    @Insert
    void insertUrlCandidatePart(UrlCandidateParts urlCandidateParts);

    @Insert
    void insertUrlCandidateParts(List<UrlCandidateParts> urlCandidateParts);

    @Update
    void updateUrlCandidate(UrlCandidate urlCandidate);

    @Query("SELECT puc.id, count, url_order as urlOrder, type, url_piece as urlPiece " +
            "from nappa_url_candidate as puc " +
            "LEFT JOIN nappa_url_candidate_part as pucp ON puc.id = pucp.id_url_candidate " +
            "WHERE id_activity = :idAct")
    List<UrlCandidateToUrlParameter> getCandidatePartsListForActivity(Long idAct);

    /**
     * Fetch for an activity "idAct" all url canditates, composed of URLS and also url pieces.  Will
     * return the results as an object {@linkplain UrlCandidateToUrlParameter}.
     * @param idAct The activity for which all urlCandidates and their corresponidng UrlCandidateParts
     *              will be fetchedf for
     * @return For and Activity A,  and its urlCandidates C,  where c element of C contains candidate parts P,
     *         then return a list of all candidate parts P for all urlCandidates C
     */
    @Query("SELECT puc.id, id_activity as idActivity, count, url_order as urlOrder, type, url_piece as urlPiece " +
            "from nappa_url_candidate as puc " +
            "LEFT JOIN nappa_url_candidate_part as pucp ON puc.id = pucp.id_url_candidate " +
            "WHERE id_activity = :idAct")
    LiveData<List<UrlCandidateToUrlParameter>> getCandidatePartsListLiveDataForActivity(Long idAct);


}
