package it.robertolaricchia.android_prefetching_lib.graph;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;
import it.robertolaricchia.android_prefetching_lib.room.PrefetchingDatabase;
import it.robertolaricchia.android_prefetching_lib.room.dao.GraphEdgeDao;


public class ActivityGraph {

    List<ActivityNode> nodeList;
    ActivityNode current = null;

    public ActivityGraph() {
        nodeList = new LinkedList<>();
    }

    /**
     * For a given Activity, Create an ActivityNode object,  and add it to the Node list. For this node, create
     * all of its successor objects in the Room database AND also statically in the ActivityNode object.
     * @param activityName The activity 
     */
    public void initNodes(String activityName) {
        Log.w("ACT_GRAPH", "initNodes() fired for node: " + activityName);
        ActivityNode temp = new ActivityNode(activityName);

        // Verify if the current activity node already exists in the activity graph
        if (nodeList.contains(temp)) {
            temp = nodeList.get(nodeList.lastIndexOf(temp));
        } else {
            nodeList.add(temp);
        }

        // Get all edges (destinations) for a given activity (source)
        List<GraphEdgeDao.GraphEdge> edges = PrefetchingDatabase.getInstance().graphEdgeDao().getEdgesForActivity(activityName);
        Log.w("ACT_GRAPH", "Edges size: " + edges.size());


        // Verify if all edges (destinations) for this activity already exists in the
        // database AND the static hashmap.  Also, check if they exist in the Activity graph.
        // If not,  add any missing edges.
        for (GraphEdgeDao.GraphEdge edge : edges) {

            if (edge != null && edge.actName != null) {
                ActivityNode temp2 = new ActivityNode(edge.actName);

                if (nodeList.contains(temp2)) {
                    Log.w("ACT_GRAPH", "contains temp2");
                    temp2 = nodeList.get(nodeList.lastIndexOf(temp2));
                } else {
                    Log.w("ACT_GRAPH", "does not contain temp2");
                    nodeList.add(temp2);
                }

                //  Add the Source-Successor relationship to both the database and the temp node itself
                temp.initSuccessor(temp2);
                Log.w("ACT_GRAPH", "adding successors: " + temp.activityName + " -> " + temp2.activityName);
            }
        }


    }

    /**
     * Will update the transitions taking place between the current {@linkplain ActivityNode} and the
     * successor ActivityNode.  This implies updating any missing ActivityNodes both statically and at the
     * database level.
     * @param activityName Current activity to which the application is transitioning to
     * @return True IFF the transition taking place is from (current node-> Successor node).
     */
    public boolean updateNodes(String activityName) {
        boolean shouldPrefetch = false;
        ActivityNode temp = new ActivityNode(activityName);
        // verify if this activity has already been added to the graph
        if (nodeList.contains(temp)) {
            temp = nodeList.get(nodeList.lastIndexOf(temp));
        } else {
            nodeList.add(temp);
        }
        if (current!=null) {
            shouldPrefetch = current.addSuccessor(temp);
        }
        current = temp;
        return shouldPrefetch;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ActivityGraph beginning\n\n");
        for (ActivityNode node: nodeList) {
            builder.append(node.toString());
        }
        builder.append("ActivityGraph end\n\n");
        return builder.toString();
    }

    public ActivityNode getCurrent() {
        return current;
    }

    /**
     * Fetches an Activity Node by name
     * @param activityName
     * @return
     */
    public ActivityNode getByName(String activityName) {
        ActivityNode node = new ActivityNode(activityName);
        if (nodeList.contains(node)) {
            return nodeList.get( nodeList.indexOf(node) );
        }
        throw new RuntimeException("Unable to find node: "+activityName);
    }
}
