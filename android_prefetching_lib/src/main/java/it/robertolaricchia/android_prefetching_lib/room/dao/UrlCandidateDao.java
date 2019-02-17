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

    @Query("SELECT puc.id, id_activity as idActivity, count, url_order as urlOrder, type, url_piece as urlPiece " +
            "from pf_url_candidate as puc " +
            "LEFT JOIN pf_url_candidate_part as pucp ON puc.id = pucp.id_url_candidate " +
            "WHERE id_activity = :idAct")
    LiveData<List<UrlCandidateToUrlParameter>> getCandidatePartsListLiveDataForActivity(Long idAct);

    class UrlCandidateToUrlParameter {
        public Long id;
        public Long idActivity;
        public Integer count;
        public Integer urlOrder;
        public Integer type;
        public String urlPiece;

        public static List<ParameteredUrl> getParameteredUrlList(List<UrlCandidateToUrlParameter> parameterList) {
            ArrayMap<Long, ParameteredUrl> parameteredUrlHashMap = new ArrayMap<>();

            Log.e("PARAM", "COUNT: " + parameterList.size());

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
            return new LinkedList<>(parameteredUrlHashMap.values());
        }
    }

}
