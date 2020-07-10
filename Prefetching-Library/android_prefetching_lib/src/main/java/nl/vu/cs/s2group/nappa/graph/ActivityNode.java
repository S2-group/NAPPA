package nl.vu.cs.s2group.nappa.graph;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import nl.vu.cs.s2group.nappa.Nappa;
import nl.vu.cs.s2group.nappa.prefetchurl.ParameteredUrl;
import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.room.activity.visittime.AggregateVisitTimeByActivity;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;
import nl.vu.cs.s2group.nappa.room.dao.UrlCandidateDao;
import nl.vu.cs.s2group.nappa.room.data.ActivityExtraData;
import nl.vu.cs.s2group.nappa.room.data.SessionData;

public class ActivityNode {
    private static final String LOG_TAG = ActivityNode.class.getSimpleName();

    public String activityName;
    /**
     * This property was added in PR #99 with the intent of replacing {@link
     * #activityName}. Across the Nappa implementation, the activity ID is
     * frequently needed, but it is usually accessed via the map {@link
     * Nappa#activityMap}. Currently, this property is only used in the method
     * {@link #getActivityId()}
     */
    private ActivityData activityData;

    // TODO Verify possibility of simplifying successor/ancestors structure
    //  Both `successors` and `ancestors` are defined as a map of `ActivityNode --> visited count`
    //  for the current session. However, the value of the map doesn't seems to be used, as the
    //  LiveData object `listSessionAggregateLiveData` provides the aggregate from the past sessions
    //  and is used in the strategies. In this case, we can likely simply this map to a list
    public Map<ActivityNode, Integer> successors = new ConcurrentHashMap<>();
    public Map<ActivityNode, Integer> ancestors = new ConcurrentHashMap<>();
    private LiveData<List<SessionDao.SessionAggregate>> listSessionAggregateLiveData;
    public Map<String, ParameteredUrl> parameteredUrlMap = new HashMap<>();
    public List<ParameteredUrl> parameteredUrlList = new LinkedList<>();            // A list of all parametered URLs within the activity
    public LiveData<List<UrlCandidateDao.UrlCandidateToUrlParameter>> urlCandidateDbLiveData;
    private LiveData<List<ActivityExtraData>> listActivityExtraLiveData;
    public float pageRank, authority, hub, authorityS, hubS, prob;
    LiveData<AggregateVisitTimeByActivity> aggregateVisitTimeLiveData;
    AggregateVisitTimeByActivity aggregateVisitTime;
    LiveData<List<AggregateVisitTimeByActivity>> successorVisitTimeLiveData;
    List<AggregateVisitTimeByActivity> successorVisitTimeList;

    /**
     * Initializes the current activity node by creating an object of the activity, and also
     * by initializing the current activity in the the Prefetchinglib's static hashmap of activities
     * and the Room database.
     * <p>
     * NOTE: Persistence inthe prefetching lib is only performed if the database does not already
     * contain the activityName
     *
     * @param activityName
     */
    public ActivityNode(String activityName) {
        this.activityName = activityName;
        // Register activity to the prefetching LIB
        Nappa.registerActivity(activityName);
    }

    public void setActivityData(ActivityData activityData) {
        this.activityData = activityData;
    }

    /**
     * Return the same as invoking {@code MyActivity.class.getSimpleName()}. Currently, only
     * the activity canonical name is store, however, we can easily obtain the activity simple
     * name from the canonical name.
     *
     * @return The activity class simple name.
     */
    public String getActivitySimpleName() {
        String[] activityNamespace = activityName.split("\\.");
        return activityNamespace[activityNamespace.length - 1];
    }

    public AggregateVisitTimeByActivity getAggregateVisitTime() {
        return aggregateVisitTime;
    }

    /**
     * Verifies whether the {@link LiveData} object for the aggregate visit time is instantiated
     * or not
     *
     * @return {@code False} if the object is instantiated or {@code True} otherwise
     */
    public boolean shouldSetAggregateVisitTime() {
        return aggregateVisitTimeLiveData == null;
    }

    /**
     * Verifies whether the {@link LiveData} object for the successors aggregate visit time
     * is instantiated or not
     *
     * @return {@code False} if the object is instantiated or {@code True} otherwise
     */
    public boolean shouldSetSuccessorVisitTime() {
        return successorVisitTimeLiveData == null;
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
        if (listSessionAggregateLiveData == null || listSessionAggregateLiveData.getValue() == null)
            return new ArrayList<>();
        return listSessionAggregateLiveData.getValue();
    }

    public LiveData<List<ActivityExtraData>> getListActivityExtraLiveData() {
        return listActivityExtraLiveData;
    }

    public List<AggregateVisitTimeByActivity> getSuccessorsVisitTimeList() {
        if (successorVisitTimeList == null) return new ArrayList<>();
        return successorVisitTimeList;
    }

    /**
     * Set the {@link LiveData} object containing the aggregate visit time per activity
     * and attach an observer to it to update the actual aggregate visit time object when there
     * are changes in the database
     *
     * @param aggregateVisitTimeLiveData A valid instance of the {@link LiveData} object
     */
    public void setAggregateVisitTimeLiveData(LiveData<AggregateVisitTimeByActivity> aggregateVisitTimeLiveData) {
        this.aggregateVisitTimeLiveData = aggregateVisitTimeLiveData;

        this.aggregateVisitTimeLiveData.observeForever((newAggregateVisitTime) -> {
            if (newAggregateVisitTime == null || newAggregateVisitTime.activityName == null) {
                Log.d(LOG_TAG, "Observer - visit time - " +
                        getActivitySimpleName() +
                        " - Was not accessed in the last N sessions");
                aggregateVisitTime = new AggregateVisitTimeByActivity();
                aggregateVisitTime.totalDuration = 0;
                aggregateVisitTime.activityName = activityName;
                return;
            }

            if (newAggregateVisitTime.equals(aggregateVisitTime)) return;

            aggregateVisitTime = newAggregateVisitTime;
            Log.d(LOG_TAG, "Observer - visit time - " +
                    getActivitySimpleName() +
                    " - New aggregate visit time found is " +
                    newAggregateVisitTime.totalDuration + " ms");
        });
    }

    /**
     * Set the {@link LiveData} object containing the successors aggregate visit time per
     * activity and attach an observer to it to update the successors visit time list when
     * there are changes in the database
     *
     * @param successorVisitTimeLiveData A valid instance of the {@link LiveData} object.
     */
    public void setSuccessorsAggregateVisitTimeLiveData(LiveData<List<AggregateVisitTimeByActivity>> successorVisitTimeLiveData) {
        this.successorVisitTimeLiveData = successorVisitTimeLiveData;
        this.successorVisitTimeLiveData.observeForever((newSuccessorVisitTime) -> {
            if (newSuccessorVisitTime == null || newSuccessorVisitTime.size() == 0) return;
            successorVisitTimeList = newSuccessorVisitTime;
            Log.d(LOG_TAG, "Observer - successor visit time - " +
                    getActivitySimpleName() +
                    " - Updating the visit time from the successors list:\n" +
                    successorVisitTimeList.toString());
        });
    }

    /**
     * Statically instantiates a List of UrlCandidateToUrlParameters Object, which compose a full URL
     * along with its parameters.  An observer updates the activity's list of parametered URLS whenever
     * there is an update to the list of {@linkplain UrlCandidateDao.UrlCandidateToUrlParameter}
     *
     * @param urlCandidateDbLiveData
     */
    public void setUrlCandidateDbLiveData(LiveData<List<UrlCandidateDao.UrlCandidateToUrlParameter>> urlCandidateDbLiveData) {
        this.urlCandidateDbLiveData = urlCandidateDbLiveData;

        // This observable updates an activity's list of parametered URLS whenever there is a change in
        // the list of URL Candidates in the database
        this.urlCandidateDbLiveData.observeForever(parameterList -> {
            // From the UPDATED set of candidate candidates, build a list containing the parameters for all URLS
            if (parameterList == null) return;
            this.parameteredUrlList = UrlCandidateDao.UrlCandidateToUrlParameter.getParameteredUrlList(parameterList);
            Log.d(LOG_TAG, "Observer - URL candidate " +
                    getActivitySimpleName() +
                    parameterList.toString());
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
            if (list == null) return;
            StringBuilder message = new StringBuilder();

            message.append("Observer - session data - source")
                    .append(getActivitySimpleName())
                    .append("\n")
                    .append("destinations:\n");

            for (SessionDao.SessionAggregate listElem : list) {
                message.append("\t")
                        .append(listElem.actName)
                        .append("( count ")
                        .append(listElem.countSource2Dest)
                        .append(")\n");
            }

            Log.d(LOG_TAG, message.toString());
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
        this.listActivityExtraLiveData.observeForever((list) -> {
            if (list == null) return;
            StringBuilder message = new StringBuilder();

            message.append("Observer - intent extra - ")
                    .append(getActivitySimpleName())
                    .append("\n")
                    .append("Extras:\n");

            for (ActivityExtraData listElem : list) {
                message.append("\tactivity ID ")
                        .append(listElem.idActivity)
                        .append("( key ")
                        .append(listElem.key)
                        .append(")\n");
            }

            Log.d(LOG_TAG, message.toString());
        });
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActivityNode
                && this.activityName != null
                && ((ActivityNode) obj).activityName != null
                && this.activityName.compareTo(((ActivityNode) obj).activityName) == 0;
    }

    // TODO Method toString contains lot of noise
    //  It would simplify the visualization if the printed string was simpler and compact.
    //  LAR scores are required on specific strategies. There is no need to print them on all
    //  strategies
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("--------------------------\nNode: " + activityName + "\nPageRank :" + pageRank + "\nHITS-Authority :" + authority + "\nHITS-Hub :" + hub + "\nSALSA-Authority :" + authorityS + "\nSALSA-Hub :" + hubS);

        builder.append("\no~o~o~o~o~o~o~o~o~o~o~o~o~o~o~o~o~\n");
        builder.append("Successors:\n");
        for (ActivityNode successor : successors.keySet()) {
            builder.append("\t" + successor.activityName + " - hit: " + successors.get(successor)).append("\n");
        }
        builder.append("\no~o~o~o~o~o~o~o~o~o~o~o~o~o~o~o~o~\n");
        builder.append("Ancestors:\n");
        for (ActivityNode ancestor : ancestors.keySet()) {
            builder.append("\t" + ancestor.activityName + " - hit: " + ancestors.get(ancestor)).append("\n");
        }

        builder.append("\n\n--------------------------\n");
        return builder.toString();
    }

    /**
     * For the current ActivityNode, store a successor activity (activityNode).  Also, for the current
     * session, add the current source-Destination relationship to the database
     *
     * @param activityNode A Successor node which is connected to the Current Activity object
     */
    public void initSuccessor(ActivityNode activityNode) {
        //TODO fix here
        successors.put(activityNode, 0);
        activityNode.ancestors.put(this, 0);
        // Store the relation between source-destination in the database with a count of 0
        Nappa.addSessionData(activityName, activityNode.activityName, 0L);
    }

    /**
     * Updates the {@linkplain ActivityNode} transitions in the database and also determines whether prefetching
     * should take place or not. If the current node is not yet part of the activity graph, this is also added to the graph.
     * If it already exitsis, it will increment the transition count.  All updates are reflected in the database entity
     * {@link SessionData}.
     * Overall, prefetching takes place IFF moving from the current node to as successor AND NOT
     * from current node to an ancestor.
     *
     * @param activityNode the current node to which the application is transitioning to
     * @return TRUE if prefetching should take place, implying the application is moving from a given node -> successor
     * FALSE if otherwise
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
                Log.d(LOG_TAG, "ACTNODE " + "CREATING, NOT IN DB");
                Nappa.addSessionData(activityName, activityNode.activityName, 1L);
                return true;
            }
            // CASE 2: Activity has already been registered as a successor, thus update the number
            //  of transitions towards this activity
            //  RETURN: TRUE (Prefetch)
            else if (successors.containsKey(activityNode) /*&& !ancestors.containsKey(activityNode)*/) {
                successors.put(activityNode, successors.get(activityNode) + 1);
                //UPDATE SESSIONDATA - THE SESSIONDATA ALREADY EXISTS
                Log.d(LOG_TAG, "ACTNODE " + "UPDATING AFTER LOADING FROM DB");
                Nappa.updateSessionData(activityName, activityNode.activityName, successors.get(activityNode).longValue());
                return true;
            }
            // CASE 3: Activity is moving from successor to ancestor, thus do not prefetch
            //  RETURN: False
            else if (/*!successors.containsKey(activityNode) &&*/ ancestors.containsKey(activityNode)) {
                //DO NOTHING
                return false;
            }
        }
        return false;
    }

    /**
     * Recursively traverse through the list of parents until all parents are identified
     *
     * @param node    The node for which the parents will be identified
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

    /**
     * Fetches the ID of this activity node. If {@link #activityData} exists, takes the
     * ID defined on this object. Otherwise, fetches the ID from the {@link Nappa#activityMap}.
     *
     * @return The activity ID
     * @throws NoSuchElementException if there is no mapping of this activity
     */
    public long getActivityId() {
        if (activityData != null) return activityData.id;

        Long activityId = Nappa.getActivityIdFromName(activityName);
        if (activityId == null)
            throw new NoSuchElementException("Unknown ID for activity " + activityName);

        return activityId;
    }
}
