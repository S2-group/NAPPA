package it.robertolaricchia.android_prefetching_lib.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.util.ArrayMap;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import it.robertolaricchia.android_prefetching_lib.prefetchurl.ParameteredUrl;
import it.robertolaricchia.android_prefetching_lib.room.data.UrlCandidate;
import it.robertolaricchia.android_prefetching_lib.room.data.UrlCandidateParts;

@Dao
public interface UrlCandidateDao {

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
     * @param idAct
     * @return
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
        public Long id;
        public Long idActivity;     // Activity to which the URL Belongs to
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
        public static List<ParameteredUrl> getParameteredUrlList(List<UrlCandidateToUrlParameter> parameterList) {
            ArrayMap<Long, ParameteredUrl> parameteredUrlHashMap = new ArrayMap<>();

            Log.e("PARAM", "COUNT: " + parameterList.size());

            // For each parameter in the parameter list,  build a parameteredUrl object and store it
            //  in the hashmap
            for (UrlCandidateToUrlParameter parameter : parameterList) {

                ParameteredUrl parameteredUrl = parameteredUrlHashMap.get(parameter.id);
                if (parameteredUrl == null) {
                    parameteredUrl = new ParameteredUrl();
                    parameteredUrlHashMap.put(parameter.id, parameteredUrl);
                }

                Log.e("PARAM", "ID_ACTIVITY: " + parameter.idActivity);

                /*try {*/
                    Log.e("PARAM", parameter.urlPiece);
                    Log.e("PARAM", parameter.type+"");
                    Log.e("PARAM", parameter.urlOrder+"");
                    parameteredUrl.addParameter(parameter.urlOrder,
                            ParameteredUrl.getTYPESFromId(parameter.type),
                            parameter.urlPiece);
                /*} catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
            // Return an Iterable list of all the values stored in the hashmap
            return new LinkedList<>(parameteredUrlHashMap.values());
        }
    }

}
