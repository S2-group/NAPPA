package nl.vu.cs.s2group.nappa.db.room.http;

import android.util.ArrayMap;
import android.util.Log;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import nl.vu.cs.s2group.nappa.prefetching.url.ParameteredUrl;

/**
 * This class represents an individual URL Candidate
 */
public class UrlCandidateToUrlParameter {
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

        Log.d(UrlCandidateDao.LOG_TAG, "PARAM " + "COUNT: " + parameterList.size());

        // For each parameter in the parameter list,  build a parameteredUrl object and store it
        //  in the hashmap
        for (UrlCandidateToUrlParameter parameter : parameterList) {

            ParameteredUrl parameteredUrl = parameteredUrlHashMap.get(parameter.id);
            if (parameteredUrl == null) {
                parameteredUrl = new ParameteredUrl();
                parameteredUrlHashMap.put(parameter.id, parameteredUrl);
            }

            Log.d(UrlCandidateDao.LOG_TAG, "PARAM " + "ID_ACTIVITY: " + parameter.idActivity);

            Log.d(UrlCandidateDao.LOG_TAG, "PARAM " + parameter.urlPiece);
            Log.d(UrlCandidateDao.LOG_TAG, "PARAM " + parameter.type + "");
            Log.d(UrlCandidateDao.LOG_TAG, "PARAM " + parameter.urlOrder + "");
            parameteredUrl.addParameter(parameter.urlOrder,
                    ParameteredUrl.getTYPESFromId(parameter.type),
                    parameter.urlPiece);

        }
        // Return an Iterable list of all the values stored in the hashmap
        return new LinkedList<>(parameteredUrlHashMap.values());
    }
}
