package it.robertolaricchia.android_prefetching_lib.graph;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;
import it.robertolaricchia.android_prefetching_lib.prefetchurl.ParameteredUrl;
import it.robertolaricchia.android_prefetching_lib.room.dao.SessionDao;
import it.robertolaricchia.android_prefetching_lib.room.dao.UrlCandidateDao;
import it.robertolaricchia.android_prefetching_lib.room.data.ActivityExtraData;

public class ActivityNode {

    public String activityName;

    public Map<ActivityNode, Integer> successors = new ConcurrentHashMap<>();
    public Map<ActivityNode, Integer> ancestors = new ConcurrentHashMap<>();
    private LiveData<List<SessionDao.SessionAggregate>> listSessionAggregateLiveData;
    private LiveData<List<SessionDao.SessionAggregate>> listLastNSessionAggregateLiveData;
    public Map<String, ParameteredUrl> parameteredUrlMap = new HashMap<>();
    public List<ParameteredUrl> parameteredUrlList = new LinkedList<>();            // A list of all parametered URLs within the activity
    public LiveData<List<UrlCandidateDao.UrlCandidateToUrlParameter>>  urlCandidateDbLiveData;
    private LiveData<List<ActivityExtraData>> listActivityExtraLiveData;
    public float pageRank,authority,hub,authorityS,hubS,prob;
    /**
     * Initializes the current activity node by creating an object of the activity, and also
     * by initializing the current activity in the the Prefetchinglib's static hashmap of activities
     * and the Room database.
     *
     * NOTE: Persistence inthe prefetching lib is only performed if the database does not already
     * contain the activityName
     *
     * @param activityName
     */
    public ActivityNode(String activityName) {
        this.activityName = activityName;
        // Register activity to the prefetching LIB
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

    public List<SessionDao.SessionAggregate> getSessionAggregateList(int LastN) {
        return listLastNSessionAggregateLiveData.getValue();

    }

    public LiveData<List<ActivityExtraData>> getListActivityExtraLiveData() {
        return listActivityExtraLiveData;
    }

    /**
     * Statically instantiates a List of UrlCandidateToUrlParameters Object, which compose a full URL
     * along with its parameters.  An observer updates the activity's list of parametered URLS whenever
     * there is an update to the list of {@linkplain it.robertolaricchia.android_prefetching_lib.room.dao.UrlCandidateDao.UrlCandidateToUrlParameter}
     * @param urlCandidateDbLiveData
     */
    public void setUrlCandidateDbLiveData(LiveData<List<UrlCandidateDao.UrlCandidateToUrlParameter>> urlCandidateDbLiveData) {
        this.urlCandidateDbLiveData = urlCandidateDbLiveData;

        // This observable updates an activity's list of parametered URLS whenever there is a change in
        // the list of URL Candidates in the database
        this.urlCandidateDbLiveData.observeForever(parameterList -> {
           // From the UPDATED set of candidate candidates, build a list containing the parameters for all URLS
            if (parameterList!=null)
                this.parameteredUrlList = UrlCandidateDao.UrlCandidateToUrlParameter.getParameteredUrlList(parameterList);
        });
    }

    /**
     * Statically instantiates the aggregated count of all ( source->destination ) transitions AND
     * sets an observer for all changes to this count in the database using LiveData as a subject,
     * in order to ensure consistency
     *
     * @param listSessionAggregateLiveData The Source for which the count of (source -> dest) visits
     *                                     is being performed
     */
    public void setListSessionAggregateLiveData(LiveData<List<SessionDao.SessionAggregate>> listSessionAggregateLiveData) {
        this.listSessionAggregateLiveData = listSessionAggregateLiveData;
        this.listSessionAggregateLiveData.observeForever((list) -> {
            Log.d("UPDATE SESSION", "source = "+activityName);
            for (SessionDao.SessionAggregate listElem : list) {
                Log.d("UPDATE SESSION", "dest: "+listElem.actName + ", count: " + listElem.countSource2Dest);
            }
        });
    }

    public void setLastNListSessionAggregateLiveData(LiveData<List<SessionDao.SessionAggregate>> listLastNSessionAggregateLiveData) {
        this.listLastNSessionAggregateLiveData = listLastNSessionAggregateLiveData;
        this.listLastNSessionAggregateLiveData.observeForever((list) -> {
            Log.d("UPDATED LAST N SESSION ", "source = "+activityName);
            for (SessionDao.SessionAggregate listElem : list) {
                Log.d("UPDATED  LAST N SESSION", "dest: "+listElem.actName + ", count: " + listElem.countSource2Dest);
            }
        });
    }

    /**
     * Statically instantiates all extra data (key-value pairs) for a given activity  AND sets an observer
     * for all changes to the extras table in the database by using LiveData as a subject, in order
     * to ensure consistency
     *
     * @param listActivityExtraLiveData The activity from which all extras will be recorded
     */
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
        StringBuilder builder = new StringBuilder("--------------------------\nNode: "+activityName+"\nPageRank :"+pageRank+"\nHITS-Authority :"+authority+"\nHITS-Hub :"+hub+"\nSALSA-Authority :"+authorityS+"\nSALSA-Hub :"+hubS);

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

    /**
     * For the current ActivityNode, store a successor activity (activityNode).  Also, for the current
     * session, add the current source-Destination relationship to the database
     * @param activityNode A Successor node which is connected to the Current Activity object
     */
    public void initSuccessor(ActivityNode activityNode) {
        //TODO fix here
        successors.put(activityNode, 0);
        activityNode.ancestors.put(this, 0);
        // Store the relation between source-destination in the database with a count of 0
        PrefetchingLib.addSessionData(activityName, activityNode.activityName, 0L);
    }

    /**
     * Updates the {@linkplain ActivityNode} transitions in the database and also determines whether prefetching
     * should take place or not. If the current node is not yet part of the activity graph, this is also added to the graph.
     * If it already exitsis, it will increment the transition count.  All updates are reflected in the database entity
     * {@link it.robertolaricchia.android_prefetching_lib.room.data.SessionData}.
     * Overall, prefetching takes place IFF moving from the current node to as successor AND NOT
     * from current node to an ancestor.
     *
     * @param activityNode the current node to which the application is transitioning to
     * @return TRUE if prefetching should take place, implying the application is moving from a given node -> successor
     *         FALSE if otherwise
     */
    public boolean addSuccessor(ActivityNode activityNode) {
        // Successor cannot be itself
        if (activityNode != this) {
            // CASE 1:  Activity has not been registered yet, as determined by Activity is
            //  not being a successor to itself AND activity is not an ancestor to itself,
            //  RETURN:  TRUE (Prefetch)
            if (!successors.containsKey(activityNode) && !ancestors.containsKey(activityNode)) {
                successors.put(activityNode, 1);
                activityNode.ancestors.put(this, 1);
                //CREATE NEW SESSIONDATA - THIS IS THE FIRST TIME
                Log.w("ACTNODE", "CREATING, NOT IN DB");
                PrefetchingLib.addSessionData(activityName, activityNode.activityName, 1L);
                return true;
            }
            // CASE 2: Activity has already been registered as a successor, thus update the number
            //  of transitions towards this activity
            //  RETURN: TRUE (Prefetch)
            else if (successors.containsKey(activityNode) /*&& !ancestors.containsKey(activityNode)*/){
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
            }
            // CASE 3: Activity is moving from successor to ancestor, thus do not prefetch
            //  RETURN: False
            else if (/*!successors.containsKey(activityNode) &&*/ ancestors.containsKey(activityNode)) {
                //DO NOTHING
                //activityNode.ancestors.put(this, ancestors.get(activityNode) + 1);
                //ancestors.put(activityNode, ancestors.get(activityNode) + 1);
                return false;
            }
        }
        return false;
    }

    /**
     * Recursively traverse through the list of parents until all parents are identified
     * @param node The node for which the parents will be identified
     * @param parents The list of parents to be returned
     * @return a {@code List<ActivityNode>} containing all the parents of the requested {@code node}
     */
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
