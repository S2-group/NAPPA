package it.robertolaricchia.android_prefetching_lib.graph;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;
import it.robertolaricchia.android_prefetching_lib.prefetchurl.ParameteredUrl;
import it.robertolaricchia.android_prefetching_lib.room.dao.ActivityExtraDao;
import it.robertolaricchia.android_prefetching_lib.room.dao.SessionDao;
import it.robertolaricchia.android_prefetching_lib.room.dao.UrlCandidateDao;
import it.robertolaricchia.android_prefetching_lib.room.data.ActivityExtraData;
import it.robertolaricchia.android_prefetching_lib.room.data.UrlCandidate;
import it.robertolaricchia.android_prefetching_lib.room.data.UrlCandidateParts;

public class ActivityNode {

    public String activityName;

    public Map<ActivityNode, Integer> successors = new ConcurrentHashMap<>();
    public Map<ActivityNode, Integer> ancestors = new ConcurrentHashMap<>();
    private LiveData<List<SessionDao.SessionAggregate>> listSessionAggregateLiveData;
    public Map<String, ParameteredUrl> parameteredUrlMap = new HashMap<>();
    public List<ParameteredUrl> parameteredUrlList = new LinkedList<>();
    public LiveData<List<UrlCandidateDao.UrlCandidateToUrlParameter>>  urlCandidateDbLiveData;

    private LiveData<List<ActivityExtraData>> listActivityExtraLiveData;

    public ActivityNode(String activityName) {
        this.activityName = activityName;

        PrefetchingLib.registerActivity(activityName);
    }

    public Map<ActivityNode, Integer> getSuccessors() {
        return successors;
    }

    public boolean shouldSetSessionAggregateLiveData() {
        return listSessionAggregateLiveData == null;
    }

    public boolean shouldSetActivityExtraLiveData() {
        return listActivityExtraLiveData == null;
    }

    public boolean shouldSetUrlCandidateDbLiveDataLiveData() {
        return urlCandidateDbLiveData == null;
    }

    public List<SessionDao.SessionAggregate> getSessionAggregateList() {
        return listSessionAggregateLiveData.getValue();
    }

    public LiveData<List<ActivityExtraData>> getListActivityExtraLiveData() {
        return listActivityExtraLiveData;
    }

    public void setUrlCandidateDbLiveData(LiveData<List<UrlCandidateDao.UrlCandidateToUrlParameter>> urlCandidateDbLiveData) {
        this.urlCandidateDbLiveData = urlCandidateDbLiveData;
        this.urlCandidateDbLiveData.observeForever(parameterList -> {
            if (parameterList!=null)
                this.parameteredUrlList = UrlCandidateDao.UrlCandidateToUrlParameter.getParameteredUrlList(parameterList);
        });
    }

    public void setListSessionAggregateLiveData(LiveData<List<SessionDao.SessionAggregate>> listSessionAggregateLiveData) {
        this.listSessionAggregateLiveData = listSessionAggregateLiveData;
        this.listSessionAggregateLiveData.observeForever((list) -> {
            Log.d("UPDATE SESSION", "source = "+activityName);
            for (SessionDao.SessionAggregate listElem : list) {
                Log.d("UPDATE SESSION", "dest: "+listElem.actName + ", count: " + listElem.countSource2Dest);
            }
        });
    }

    public void setListActivityExtraLiveData(LiveData<List<ActivityExtraData>> listActivityExtraLiveData) {
        this.listActivityExtraLiveData = listActivityExtraLiveData;
        //TODO update here
        this.listActivityExtraLiveData.observeForever((list) -> {
            Log.d("PREFSTRAT2", activityName);
            for (ActivityExtraData listElem : list) {
                Log.d("PREFSTRAT2", "actid: "+listElem.idActivity+ ", key: " + listElem.key);
            }
        });
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActivityNode
                && this.activityName != null
                && ((ActivityNode) obj).activityName != null
                && this.activityName.compareTo(((ActivityNode) obj).activityName) == 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("--------------------------\nNode: "+activityName+"\n");

        builder.append("\no~o~o~o~o~o~o~o~o~o~o~o~o~o~o~o~o~\n");
        builder.append("Successors:\n");
        for (ActivityNode successor: successors.keySet()) {
            builder.append("\t"+successor.activityName+" - hit: "+successors.get(successor)).append("\n");
        }
        builder.append("\no~o~o~o~o~o~o~o~o~o~o~o~o~o~o~o~o~\n");
        builder.append("Ancestors:\n");
        for (ActivityNode ancestor: ancestors.keySet()) {
            builder.append("\t"+ancestor.activityName+" - hit: "+ancestors.get(ancestor)).append("\n");
        }

        builder.append("\n\n--------------------------\n");
        return builder.toString();
    }

    public void initSuccessor(ActivityNode activityNode) {
        //TODO fix here
        successors.put(activityNode, 0);
        activityNode.ancestors.put(this, 0);
        PrefetchingLib.addSessionData(activityName, activityNode.activityName, 0L);
    }

    public boolean addSuccessor(ActivityNode activityNode) {
        if (activityNode != this) {
            if (!successors.containsKey(activityNode) && !ancestors.containsKey(activityNode)) {
                successors.put(activityNode, 1);
                activityNode.ancestors.put(this, 1);
                //CREATE NEW SESSIONDATA - THIS IS THE FIRST TIME
                Log.w("ACTNODE", "CREATING, NOT IN DB");
                PrefetchingLib.addSessionData(activityName, activityNode.activityName, 1L);
                return true;
            } else if (successors.containsKey(activityNode) /*&& !ancestors.containsKey(activityNode)*/){
                successors.put(activityNode, successors.get(activityNode) + 1);
                //UPDATE SESSIONDATA - THE SESSIONDATA ALREADY EXISTS
                /*if ( successors.get(activityNode) > 0 ) {
                    PrefetchingLib.updateSessionData(activityName, activityNode.activityName, successors.get(activityNode).longValue());
                    Log.w("ACTNODE", "CREATING, NOT IN DB");
                } else {
                    Log.w("ACTNODE", "UPDATING AFTER LOADING FROM DB");
                    PrefetchingLib.addSessionData(activityName, activityNode.activityName, 1L);
                }*/
                Log.w("ACTNODE", "UPDATING AFTER LOADING FROM DB");
                PrefetchingLib.updateSessionData(activityName, activityNode.activityName, successors.get(activityNode).longValue());
                return true;
            } else if (/*!successors.containsKey(activityNode) &&*/ ancestors.containsKey(activityNode)) {
                //DO NOTHING
                //activityNode.ancestors.put(this, ancestors.get(activityNode) + 1);
                //ancestors.put(activityNode, ancestors.get(activityNode) + 1);
                return false;
            }
        }
        return false;
    }

    public static List<ActivityNode> getAllParents(ActivityNode node, List<ActivityNode> parents) {
        for (ActivityNode parent : node.ancestors.keySet()) {
            if (!parents.contains(parent)) {
                parents.add(parent);
                getAllParents(parent, parents);
            }
        }
        return parents;
    }

    public static List<ActivityNode> getAllSuccessors(ActivityNode node, List<ActivityNode> successors) {
        for (ActivityNode successor : node.successors.keySet()) {
            if (!successors.contains(successor)) {
                successors.add(successor);
                getAllParents(successor, successors);
            }
        }
        return successors;
    }
}
