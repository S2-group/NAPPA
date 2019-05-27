package it.robertolaricchia.android_prefetching_lib.graph;

import android.app.Activity;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.robertolaricchia.android_prefetching_lib.room.PrefetchingDatabase;
import it.robertolaricchia.android_prefetching_lib.room.dao.GraphEdgeDao;
import it.robertolaricchia.android_prefetching_lib.room.data.LARData;


public class ActivityGraph {

    List<ActivityNode> nodeList;
    ActivityNode current = null;
    private static ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(1);
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
        //link analysis ranking (LAR)
        LARData LAR = PrefetchingDatabase.getInstance().activityDao().getLAR(activityName);
        Log.d("LARData",activityName+" Pagerank: "+LAR.PR+" Authority: "+LAR.authority+" Hub: "+LAR.hub);

        // Verify if the current activity node already exists in the activity graph
        if (nodeList.contains(temp)) {
            temp = nodeList.get(nodeList.lastIndexOf(temp));
        } else {
            temp.pageRank = LAR.PR;
            temp.authority = LAR.authority;
            temp.hub = LAR.hub;
            Log.d("LARDataElse",activityName+" Pagerank: "+LAR.PR+" Authority: "+LAR.authority+" Hub: "+LAR.hub);
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
        float dump = 0.85f;
        float initialPageRank = 0.25f;
        float initialAuthority = 1f;
        float initialHub = 1f;
        ActivityNode temp = new ActivityNode(activityName);
        // verify if this activity has already been added to the graph
        final String tempActivityName = temp.activityName;
        if (nodeList.contains(temp)) {
            temp = nodeList.get(nodeList.lastIndexOf(temp));
        } else {
            temp.pageRank=initialPageRank;
            temp.authority=initialAuthority;
            temp.hub=initialHub;
            nodeList.add(temp);
            poolExecutor.schedule(() -> {
                PrefetchingDatabase.getInstance().activityDao().insertLAR(new LARData(tempActivityName,initialPageRank,initialAuthority,initialHub));
            }, 0, TimeUnit.SECONDS);
            Log.d("LARDataInit",activityName+" Pagerank: "+temp.pageRank+" Authority: "+temp.authority+" Hub: "+temp.hub);
        }
        if (current!=null) {
            shouldPrefetch = current.addSuccessor(temp);
            ///////PageRank Update
            float tempPR = 0;
            int index = nodeList.lastIndexOf(temp);
            for (ActivityNode ancestor : temp.ancestors.keySet()) {
                tempPR += ancestor.pageRank / ancestor.successors.size();
            }
            tempPR = (1 - dump) / nodeList.size() + dump * tempPR;
            temp.pageRank = tempPR;
            nodeList.set(index, temp);
            ////////////////
            // HITS Algorithm https://en.wikipedia.org/wiki/HITS_algorithm
            //authority update
            /*float sumAuthority = 0, sumHub = 0;
            for (ActivityNode node: nodeList){
                float tempAuthority = 0;
                Log.d("LARDataAntonioTestN",node.activityName);
                for (ActivityNode ancestor : node.ancestors.keySet()) {
                    tempAuthority += ancestor.hub;
                    Log.d("LARDataAntonioTest",ancestor.activityName+" hub "+ancestor.hub);
                }
                node.authority = tempAuthority;
                Log.d("LARDataAntonioTest","authority ->"+node.authority+"");
                sumAuthority += tempAuthority*tempAuthority;
            }
            sumAuthority = (float)Math.sqrt((double)(sumAuthority));
            for (ActivityNode node: nodeList){
                node.authority /= sumAuthority;
                for (ActivityNode node1: nodeList)if (node1.successors.containsKey(node)) node1.successors.put(node, node1.successors.get(node));
            }
            //hub update
            for (ActivityNode node: nodeList){
                float tempHub = 0;
                Log.d("LARDataAntonioTestN2",node.activityName);
                for (ActivityNode successor : node.successors.keySet()) {
                    tempHub += successor.authority;
                    Log.d("LARDataAntonioTest2",successor.activityName+" authority "+successor.authority);
                }
                node.hub = tempHub;
                Log.d("LARDataAntonioTest2","hub ->"+node.hub+"");
                sumHub += tempHub*tempHub;
            }
            sumHub = (float)Math.sqrt((double)(sumHub));
            for (ActivityNode node: nodeList){
                node.hub /= sumHub;
                for (ActivityNode node1: nodeList)if (node1.ancestors.containsKey(node)) node1.ancestors.put(node, node1.ancestors.get(node));
                index = nodeList.lastIndexOf(node);
                nodeList.set(index,node);
                poolExecutor.schedule(() -> {
                    PrefetchingDatabase.getInstance().activityDao().updateLAR(new LARData(node.activityName, node.pageRank,node.authority,node.hub));
                }, 0, TimeUnit.SECONDS);
                Log.d("LARDataUpdateNull",node.activityName+" Pagerank: "+node.pageRank+" Authority: "+node.authority+" Hub: "+node.hub);
            }*/
            ////////////////ANTONIO FIX
            float sumAuthority = 0, sumHub = 0;
            //hub update
            for (ActivityNode node: nodeList){
                float tempHub = 0;
                Log.d("LARDataAntonioTestN2","im looking for HUB value of "+node.activityName +" which has as outcoming links: ");
                for (ActivityNode successor : node.successors.keySet()) {
                    tempHub += successor.authority;
                    Log.d("LARDataAntonioTest2",successor.activityName+" with AUTHORITY value"+successor.authority);
                }
                node.hub = tempHub;
                Log.d("LARDataAntonioTest2","so the not normalized HUB value is "+node.hub+"");
                sumHub += tempHub*tempHub;
            }
            sumHub = (float)Math.sqrt((double)(sumHub));
            for (ActivityNode node: nodeList){
                node.hub /= sumHub;
                for (ActivityNode node1: nodeList)if (node1.ancestors.containsKey(node)) node1.ancestors.put(node, node1.ancestors.get(node));
            }
            //authority update
            for (ActivityNode node: nodeList){
                float tempAuthority = 0;
                Log.d("LARDataAntonioTestN","im looking for AUTHORITY value of "+node.activityName+" which has as incoming links: ");
                for (ActivityNode ancestor : node.ancestors.keySet()) {
                    tempAuthority += ancestor.hub;
                    Log.d("LARDataAntonioTest",ancestor.activityName+" with HUB value"+ancestor.hub);
                }
                node.authority = tempAuthority;
                Log.d("LARDataAntonioTest","so the not normalized AUTHORITY value is "+node.authority+"");
                sumAuthority += tempAuthority*tempAuthority;
            }
            sumAuthority = (float)Math.sqrt((double)(sumAuthority));
            for (ActivityNode node: nodeList){
                node.authority /= sumAuthority;
                for (ActivityNode node1: nodeList)if (node1.successors.containsKey(node)) node1.successors.put(node, node1.successors.get(node));
                index = nodeList.lastIndexOf(node);
                nodeList.set(index,node);
                poolExecutor.schedule(() -> {
                    PrefetchingDatabase.getInstance().activityDao().updateLAR(new LARData(node.activityName, node.pageRank,node.authority,node.hub));
                }, 0, TimeUnit.SECONDS);
                Log.d("LARDataUpdateNull",node.activityName+" Pagerank: "+node.pageRank+" Normalized authority: "+node.authority+"  Normalized Hub: "+node.hub);
            }
            //////////////////////////

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
