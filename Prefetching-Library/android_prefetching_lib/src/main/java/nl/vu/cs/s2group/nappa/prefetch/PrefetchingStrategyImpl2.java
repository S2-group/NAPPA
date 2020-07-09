package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;
import android.util.Log;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.prefetchurl.ParameteredUrl;
import nl.vu.cs.s2group.nappa.room.AggregateUrlDao;
import nl.vu.cs.s2group.nappa.room.PrefetchingDatabase;
import nl.vu.cs.s2group.nappa.room.data.ActivityExtraData;

// TODO: Read the strategy, provide a JavaDoc with basic description and update class name
@Deprecated
public class PrefetchingStrategyImpl2 implements PrefetchingStrategy {
    private static final String LOG_TAG = PrefetchingStrategyImpl2.class.getSimpleName();

    private DiffMatchPatch dmp = new DiffMatchPatch();

    @Override
    public boolean needVisitTime() {
        return false;
    }

    @Override
    public boolean needSuccessorsVisitTime() {
        return false;
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {
            List<ActivityExtraData> extraDataList = node.getListActivityExtraLiveData().getValue();
            if (extraDataList != null) {
                Map<ActivityNode, Integer> successors = node.getSuccessors();
                List<ParameteredUrl> parameteredUrls = new LinkedList<>();

                for (ActivityNode successor : successors.keySet()) {
                    List<AggregateUrlDao.AggregateURL> list = PrefetchingDatabase.getInstance()
                            .urlDao()
                            .getAggregateForIdActivity(PrefetchingLib.getActivityIdFromName(successor.activityName), 10);
                    for (AggregateUrlDao.AggregateURL aggregateURL : list) {
                        List<AggregateUrlDao.AggregateURL> newlist = new LinkedList<>(list);

                        newlist.remove(newlist.indexOf(aggregateURL));

                        for (AggregateUrlDao.AggregateURL aggregateURL1 : newlist) {
                            LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(aggregateURL.getUrl(), aggregateURL1.getUrl());
                            dmp.diffCleanupEfficiency(diffs);

                            ParameteredUrl temp = new ParameteredUrl();
                            int count = 0;

                            for (DiffMatchPatch.Diff diff : diffs) {
                                if (diff.operation == DiffMatchPatch.Operation.EQUAL) {
                                    temp.addParameter(count, ParameteredUrl.TYPES.STATIC, diff.text);
                                    count++;
                                } else if (diff.operation == DiffMatchPatch.Operation.INSERT){
                                    temp.addParameter(count, ParameteredUrl.TYPES.PARAMETER, "");
                                    count++;
                                }
                            }
                            parameteredUrls.add(temp);
                        }
                    }
                    List<String> candidates = new LinkedList<>();

                    Log.d(LOG_TAG, PrefetchingLib.getExtrasMap().toString());
                    Log.d(LOG_TAG,  PrefetchingLib.getActivityIdFromName(node.activityName).toString());

                    for (ParameteredUrl url : parameteredUrls) {
                            List<ParameteredUrl.UrlParameter> parameterList = url.getUrlParameterList();
                            StringBuilder sb = new StringBuilder();
                            for (ParameteredUrl.UrlParameter parameter : parameterList) {
                                if (parameter.type == ParameteredUrl.TYPES.STATIC)
                                    sb.append(parameter.urlPiece);
                                else
                                    sb.append(PrefetchingLib.getExtrasMap().get(
                                            //2L
                                            PrefetchingLib.getActivityIdFromName(node.activityName)
                                    ));
                            }
                            candidates.add(sb.toString());
                    }

                    for (String candidate: candidates) {
                        Log.d(LOG_TAG, candidate);
                    }

                    return candidates;
                }
            } else {
                Log.d(LOG_TAG, "NO EXTRADATA");
            }
        return Arrays.asList(new String[]{});
    }
}
