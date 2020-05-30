package nl.vu.cs.s2group.nappa.graph;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import nl.vu.cs.s2group.nappa.db.NappaDB;
import nl.vu.cs.s2group.nappa.db.room.graph.Edge;
import nl.vu.cs.s2group.nappa.db.room.graph.LAR;
import nl.vu.cs.s2group.nappa.ranking.algorithm.LARRanking;


public class ActivityGraph {
    private final static String LOG_TAG = ActivityGraph.class.getSimpleName();

    private List<ActivityNode> nodeList;
    private ActivityNode current = null;

    public ActivityGraph() {
        nodeList = new LinkedList<>();
    }

    /**
     * For a given Activity, Create an ActivityNode object,  and add it to the Node list. For this node, create
     * all of its successor objects in the Room database AND also statically in the ActivityNode object.
     * @param activityName The activity 
     */
    public void initNodes(String activityName) {
        Log.d(LOG_TAG, "ACT_GRAPH" + "initNodes() fired for node: " + activityName);
        ActivityNode temp = new ActivityNode(activityName);
        //link analysis ranking (lar)
        LAR lar = NappaDB.getInstance().larDao().getLAR(activityName);
        Log.d(LOG_TAG, "LARDataInitFetchDB" + activityName + " Pagerank: " + lar.PR + " HITS-Authority: " + lar.authority + " HITS-Hub: " + lar.hub + " SALSA-Authority: " + lar.authorityS + " SALSA-Hub: " + lar.hubS);

        // Verify if the current activity node already exists in the activity graph
        if (nodeList.contains(temp)) {
            temp = nodeList.get(nodeList.lastIndexOf(temp));
            Log.d(LOG_TAG, "LARDataInit" + "already in nodeList");
        } else {
            temp.lar = lar;
            Log.d(LOG_TAG, "LARDataInit" + "node " + temp.activityName + " added to nodeList");
            nodeList.add(temp);
        }


        // Get all edges (destinations) for a given activity (source)
        List<Edge> edges = NappaDB.getInstance().graphEdgeDao().getEdgesForActivity(activityName);
        Log.d(LOG_TAG, "ACT_GRAPH" + "Edges size: " + edges.size());


        // Verify if all edges (destinations) for this activity already exists in the
        // database AND the static hashmap.  Also, check if they exist in the Activity graph.
        // If not,  add any missing edges.
        for (Edge edge : edges) {

            if (edge != null && edge.actName != null) {
                ActivityNode temp2 = new ActivityNode(edge.actName);
                lar = NappaDB.getInstance().larDao().getLAR(edge.actName);
                Log.d(LOG_TAG, "LARDataInitFetchDB" + activityName + " Pagerank: " + lar.PR + " HITS-Authority: " + lar.authority + " HITS-Hub: " + lar.hub + " SALSA-Authority: " + lar.authorityS + " SALSA-Hub: " + lar.hubS + " loaded following an edge");

                if (nodeList.contains(temp2)) {
                    Log.d(LOG_TAG, "ACT_GRAPH" + "contains temp2");
                    Log.d(LOG_TAG, "LARDataInit" + "already in nodeList from edge");
                    temp2 = nodeList.get(nodeList.lastIndexOf(temp2));
                } else {
                    Log.d(LOG_TAG, "ACT_GRAPH" + "does not contain temp2");
                    temp2.lar = lar;
                    Log.d(LOG_TAG, "LARDataInit" + "node " + temp2.activityName + " added to nodeList from edge");
                    nodeList.add(temp2);
                }

                //  Add the Source-Successor relationship to both the database and the temp node itself
                temp.initSuccessor(temp2);
                Log.d(LOG_TAG, "ACT_GRAPH" + "adding successors: " + temp.activityName + " -> " + temp2.activityName);
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
        final String tempActivityName = temp.activityName;
        if (nodeList.contains(temp)) {
            temp = nodeList.get(nodeList.lastIndexOf(temp));
        } else {
            temp.lar = LARRanking.initLAR(activityName);
            nodeList.add(temp);
            Log.d(LOG_TAG, "node " + temp.activityName + " added to nodelist");
        }
        if (current!=null) {
            shouldPrefetch = current.addSuccessor(temp);
            nodeList.set(nodeList.lastIndexOf(temp),temp);
            //updates
            LARRanking.updateLAR(nodeList, activityName);
            temp = nodeList.get(nodeList.lastIndexOf(temp));
        }
        current = temp;
        return shouldPrefetch;
    }

    @NotNull
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
     * @param activityName Name of the activity to be fetched
     * @return {@linkplain ActivityNode} Corresponding to the activity name requested
     */
    public ActivityNode getByName(String activityName) {
        ActivityNode node = new ActivityNode(activityName);
        if (nodeList.contains(node)) {
            return nodeList.get( nodeList.indexOf(node) );
        }
        throw new RuntimeException("Unable to find node: "+activityName);
    }
}
