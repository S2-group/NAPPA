package nl.vu.cs.s2group.nappa.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import android.util.ArrayMap;
import android.util.Log;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import nl.vu.cs.s2group.nappa.prefetching.url.ParameteredUrl;
import nl.vu.cs.s2group.nappa.db.data.UrlCandidate;
import nl.vu.cs.s2group.nappa.db.data.UrlCandidateParts;

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
            "from pf_url_candidate as puc " +
            "LEFT JOIN pf_url_candidate_part as pucp ON puc.id = pucp.id_url_candidate " +
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
            "from pf_url_candidate as puc " +
            "LEFT JOIN pf_url_candidate_part as pucp ON puc.id = pucp.id_url_candidate " +
            "WHERE id_activity = :idAct")
    LiveData<List<UrlCandidateToUrlParameter>> getCandidatePartsListLiveDataForActivity(Long idAct);


    /**
     * This class represents an individual URL Candidate
     */
    class UrlCandidateToUrlParameter {
        public Long id;             // Id of the UrkCandudate
        public Long idActivity;     // Id of theActivity to which the URL Belongs to
        public Integer count;
        public Integer urlOrder;
        public Integer type;
        public String urlPiece;

        /**
         * Process all URL "Chunks" of type {@linkplain UrlCandidateToUrlParameter } stored in
         * {@code parameterList}, and an array of {@linkplain ParameteredUrl}s
         *
         *
         * @param parameterList A list of URL parameters
         * @return A list of parametered URLS
         */
        @NotNull
        @Contract("_ -> new")
        public static List<ParameteredUrl> getParameteredUrlList(@NotNull List<UrlCandidateToUrlParameter> parameterList) {
            ArrayMap<Long, ParameteredUrl> parameteredUrlHashMap = new ArrayMap<>();

            Log.d(LOG_TAG, "PARAM " + "COUNT: " + parameterList.size());

            // For each parameter in the parameter list,  build a parameteredUrl object and store it
            //  in the hashmap
            for (UrlCandidateToUrlParameter parameter : parameterList) {

                ParameteredUrl parameteredUrl = parameteredUrlHashMap.get(parameter.id);
                if (parameteredUrl == null) {
                    parameteredUrl = new ParameteredUrl();
                    parameteredUrlHashMap.put(parameter.id, parameteredUrl);
                }

                Log.d(LOG_TAG, "PARAM " + "ID_ACTIVITY: " + parameter.idActivity);

                Log.d(LOG_TAG, "PARAM " + parameter.urlPiece);
                Log.d(LOG_TAG, "PARAM " + parameter.type + "");
                Log.d(LOG_TAG, "PARAM " + parameter.urlOrder + "");
                parameteredUrl.addParameter(parameter.urlOrder,
                        ParameteredUrl.getTYPESFromId(parameter.type),
                        parameter.urlPiece);

            }
            // Return an Iterable list of all the values stored in the hashmap
            return new LinkedList<>(parameteredUrlHashMap.values());
        }
    }

}
