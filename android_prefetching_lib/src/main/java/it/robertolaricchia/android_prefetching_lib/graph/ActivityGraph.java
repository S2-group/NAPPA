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

    public void initNodes(String activityName) {
        Log.w("ACT_GRAPH", "initNodes() fired for node: " + activityName);
        ActivityNode temp = new ActivityNode(activityName);
        if (nodeList.contains(temp)) {
            temp = nodeList.get(nodeList.lastIndexOf(temp));
        } else {
            nodeList.add(temp);
        }
        List<GraphEdgeDao.GraphEdge> edges = PrefetchingDatabase.getInstance().graphEdgeDao().getEdgesForActivity(activityName);
        Log.w("ACT_GRAPH", "Edges size: " + edges.size());
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
                temp.initSuccessor(temp2);
                Log.w("ACT_GRAPH", "adding successors: " + temp.activityName + " -> " + temp2.activityName);
            }
        }


    }

    public boolean updateNodes(String activityName) {
        boolean shouldPrefetch = false;
        ActivityNode temp = new ActivityNode(activityName);
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

    public ActivityNode getByName(String activityName) {
        ActivityNode node = new ActivityNode(activityName);
        if (nodeList.contains(node)) {
            return nodeList.get( nodeList.indexOf(node) );
        }
        throw new RuntimeException("Unable to find node: "+activityName);
    }
}
