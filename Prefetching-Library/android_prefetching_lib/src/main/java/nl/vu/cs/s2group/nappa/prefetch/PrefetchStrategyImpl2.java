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

public class PrefetchStrategyImpl2 implements PrefetchStrategy {

    private DiffMatchPatch dmp = new DiffMatchPatch();

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {
        /*Log.w("PrefStratImpl", "Started for node: "+node.activityName);
        SessionDao.SessionAggregate best = null;
        List<SessionDao.SessionAggregate> sessionAggregateList = node.getSessionAggregateList();
        if (sessionAggregateList!=null) {
            for (SessionDao.SessionAggregate aggregate : sessionAggregateList) {
                Log.w("PrefStratImpl", "Evaluating successor: " + aggregate.actName);
                if (best == null) {
                    best = aggregate;
                } else if (aggregate.countSource2Dest > best.countSource2Dest) {
                    Log.w("PrefStratImpl",
                            "choosing "+aggregate.actName+": "+aggregate.countSource2Dest+" against " +
                                    best.actName+": "+best.countSource2Dest);
                    best = aggregate;
                }
            }
            if (best != null) {
                Log.w("PrefStratImpl", "Chosen successor: " + best.actName);
                List<AggregateUrlDao.AggregateURL> list = PrefetchingDatabase.getInstance().urlDao().getAggregateForIdActivity(best.idActDest, maxNumber);
                LinkedList<String> toBeReturned = new LinkedList<String>();
                for (AggregateUrlDao.AggregateURL elem : list) {
                    toBeReturned.add(elem.getUrl());
                }
                return toBeReturned;
            } else {
                Log.w("PrefStratImpl", "Null successor");
            }
        } else {
            Log.w("PrefStratImpl", "SessionAggregateList is null");
        }*/
        //new Thread(() -> {
            List<ActivityExtraData> extraDataList = node.getListActivityExtraLiveData().getValue();
            if (extraDataList != null) {
                /*for (ActivityExtraData data : extraDataList) {
                    Log.w("PREFSTRAT2", "actId: " + data.idActivity + "\t" + data.key + ": " + data.value);

                    LinkedList<DiffMatchPatch.Diff> list = dmp.diffMain(data.value,
                            "http://api.openweathermap.org/data/2.5/weather?q=Kabul&appid=75f4ddb403cdbac1df21fa8a10c21ce9");
                    dmp.diffCleanupEfficiency(list);

                    for (DiffMatchPatch.Diff diff : list) {
                        Log.w("PREFSTRAT2", diff.toString());
                    }

                    Log.w("PREFSTRAT2", "----------------");
                }*/
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
                                //System.out.println(diff);

                                if (diff.operation == DiffMatchPatch.Operation.EQUAL) {
                                    temp.addParameter(count, ParameteredUrl.TYPES.STATIC, diff.text);
                                    count++;
                                } else if (diff.operation == DiffMatchPatch.Operation.INSERT){
                                    temp.addParameter(count, ParameteredUrl.TYPES.PARAMETER, "");
                                    count++;
                                }

                            }

                            parameteredUrls.add(temp);

                            //System.out.println("__________________________________");
                        }
                    }







                    List<String> candidates = new LinkedList<>();

                    Log.w("PREFSTRAT2", PrefetchingLib.getExtrasMap().toString());
                    Log.w("PREFSTRAT2",  PrefetchingLib.getActivityIdFromName(node.activityName).toString());

                    for (ParameteredUrl url : parameteredUrls) {
                        //for (ActivityExtraData activityExtraData: extraDataList) {
                            List<ParameteredUrl.UrlParameter> parameterList = url.getUrlParameterList();
                            StringBuilder sb = new StringBuilder();
                            for (ParameteredUrl.UrlParameter parameter : parameterList) {
                                if (parameter.type == ParameteredUrl.TYPES.STATIC)
                                    //System.out.println(parameter.urlPiece);
                                    sb.append(parameter.urlPiece);
                                else
                                    sb.append(PrefetchingLib.getExtrasMap().get(
                                            //2L
                                            PrefetchingLib.getActivityIdFromName(node.activityName)
                                    ));
                                    //System.out.println("PARAM");
                            }
                            candidates.add(sb.toString());
                            //System.out.println("-----------------------");
                        //}
                    }



                    for (String candidate: candidates) {
                        Log.e("PREFSTRAT2", candidate);
                    }

                    return candidates;






                }
            } else {
                Log.e("PREFSTRAT2", "NO EXTRADATA");
            }
        //}).start();



        return Arrays.asList(new String[]{});
    }
}
