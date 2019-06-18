package nl.vu.cs.s2group.graph;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.vu.cs.s2group.PrefetchingLib;
import nl.vu.cs.s2group.room.PrefetchingDatabase;
import nl.vu.cs.s2group.room.dao.GraphEdgeDao;
import nl.vu.cs.s2group.room.data.LARData;


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
        Log.d("LARDataInitFetchDB",activityName+" Pagerank: "+LAR.PR+" HITS-Authority: "+LAR.authority+" HITS-Hub: "+LAR.hub+" SALSA-Authority: "+LAR.authorityS+" SALSA-Hub: "+LAR.hubS);

        // Verify if the current activity node already exists in the activity graph
        if (nodeList.contains(temp)) {
            temp = nodeList.get(nodeList.lastIndexOf(temp));
            Log.d("LARDataInit","already in nodeList");
        } else {
            temp.pageRank = LAR.PR;
            temp.authority = LAR.authority;
            temp.hub = LAR.hub;
            temp.authorityS = LAR.authorityS;
            temp.hubS = LAR.hubS;
            Log.d("LARDataInit","node "+ temp.activityName+" added to nodeList");
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
                LAR = PrefetchingDatabase.getInstance().activityDao().getLAR(edge.actName);
                Log.d("LARDataInitFetchDB",activityName+" Pagerank: "+LAR.PR+" HITS-Authority: "+LAR.authority+" HITS-Hub: "+LAR.hub+" SALSA-Authority: "+LAR.authorityS+" SALSA-Hub: "+LAR.hubS+" loaded following an edge");

                if (nodeList.contains(temp2)) {
                    Log.w("ACT_GRAPH", "contains temp2");
                    Log.d("LARDataInit","already in nodeList from edge");
                    temp2 = nodeList.get(nodeList.lastIndexOf(temp2));
                } else {
                    Log.w("ACT_GRAPH", "does not contain temp2");
                    temp2.pageRank = LAR.PR;
                    temp2.authority = LAR.authority;
                    temp2.hub = LAR.hub;
                    temp2.authorityS = LAR.authorityS;
                    temp2.hubS = LAR.hubS;
                    Log.d("LARDataInit","node "+ temp2.activityName+" added to nodeList from edge");
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
        float initialAuthorityS = 1f;
        float initialHubS = 1f;
        ActivityNode temp = new ActivityNode(activityName);
        // verify if this activity has already been added to the graph
        final String tempActivityName = temp.activityName;
        if (nodeList.contains(temp)) {
            temp = nodeList.get(nodeList.lastIndexOf(temp));
        } else {
            temp.pageRank=initialPageRank;
            temp.authority=initialAuthority;
            temp.hub=initialHub;
            temp.authorityS=initialAuthorityS;
            temp.hubS=initialHubS;
            nodeList.add(temp);
            poolExecutor.schedule(() -> {
                PrefetchingDatabase.getInstance().activityDao().insertLAR(new LARData(tempActivityName,initialPageRank,initialAuthority,initialHub,initialAuthorityS,initialHubS));
            }, 0, TimeUnit.SECONDS);
            Log.d("LARDataUpdate","node "+temp.activityName+" added to nodelist");
            Log.d("LARDataUpdate"," Pagerank: "+temp.pageRank+" HITS-Authority: "+temp.authority+" HITS-Hub: "+temp.hub+" SALSA-Authority: "+temp.authorityS+" SALSA-Hub: "+temp.hubS);
        }
        if (current!=null) {
            shouldPrefetch = current.addSuccessor(temp);
            nodeList.set(nodeList.lastIndexOf(temp),temp);
            //updates
            updateLAR(activityName);
            temp = nodeList.get(nodeList.lastIndexOf(temp));
            Log.d("LARDataCalculatedUpdate",temp.activityName+" Pagerank: "+temp.pageRank+" HITS-Authority: "+temp.authority+" HITS-Hub: "+temp.hub+" SALSA-Authority: "+temp.authorityS+" SALSA-Hub: "+temp.hubS);
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

    public void updateLAR(String activityName){
        switch(PrefetchingLib.prefetchStrategyNum){
            case 3:
                break;
            case 4:
                break;
            case 5:
                updatePR(activityName);
                break;
            case 6:
                updateHITS();
                break;
            case 7:
                updateSALSA();
                break;
            case 8:
                updatePR(activityName);
                break;
            case 9:
                updateHITS();
                break;
            default:
        }
    }
    public void updatePR(String activityName){
        ///////PageRank Update
        ActivityNode temp = new ActivityNode(activityName);
        temp = nodeList.get(nodeList.lastIndexOf(temp));
        float dump = 0.85f;
        float tempPR = 0;
        int index = nodeList.lastIndexOf(temp);
        for (ActivityNode ancestor : temp.ancestors.keySet()) {
            tempPR += ancestor.pageRank / ancestor.successors.size();
        }
        tempPR = (1 - dump) / nodeList.size() + dump * tempPR;
        temp.pageRank = tempPR;
        nodeList.set(index, temp);
        final ActivityNode temp_= temp;
        poolExecutor.schedule(() -> {
            PrefetchingDatabase.getInstance().activityDao().updateLAR(new LARData(temp_.activityName, temp_.pageRank,temp_.authority,temp_.hub,temp_.authorityS,temp_.hubS));
        }, 0, TimeUnit.SECONDS);
    }

    public void updateHITS(){
        ////////////////
        // HITS Algorithm https://en.wikipedia.org/wiki/HITS_algorithm
        //authority update
        float sumAuthority = 0, sumHub = 0;
        for (ActivityNode node: nodeList){
            float tempAuthority = 0;
            for (ActivityNode ancestor : node.ancestors.keySet()) {
                tempAuthority += ancestor.hub;
            }
            node.authority = tempAuthority;
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
            for (ActivityNode successor : node.successors.keySet()) {
                tempHub += successor.authority;
            }
            node.hub = tempHub;
            sumHub += tempHub*tempHub;
        }
        sumHub = (float)Math.sqrt((double)(sumHub));
        for (ActivityNode node: nodeList){
            node.hub /= sumHub;
            for (ActivityNode node1: nodeList)if (node1.ancestors.containsKey(node)) node1.ancestors.put(node, node1.ancestors.get(node));
            int index = nodeList.lastIndexOf(node);
            nodeList.set(index,node);
            poolExecutor.schedule(() -> {
                PrefetchingDatabase.getInstance().activityDao().updateLAR(new LARData(node.activityName, node.pageRank,node.authority,node.hub,node.authorityS,node.hubS));
            }, 0, TimeUnit.SECONDS);
        }
    }

    public void updateSALSA(){
        ///////////////////////////////////SALSA ALGORITHM http://snap.stanford.edu/class/cs224w-readings/najork05salsa.pdf
        float sumAuthorityS = 1, sumHubS = 1;
        float tempAuthorityS = 0, tempHubS=0;
        /////////AuthorityS update
        //check the nodes with in-degree > 0
        //with out-degree > 0
        /*for (ActivityNode node: nodeList){
            if(node.ancestors.keySet().size()!=0) sumAuthorityS+=node.authorityS;
            if(node.successors.keySet().size()!=0) sumHubS+=node.hubS;
        }*/
        //IF in-degree > 0 tempAuthority=1/norm-1-of(all node with in-degree>0) ELSE tempAuthority=0
        //check it also in the successors' structure of my ancestors
        for (ActivityNode node: nodeList){
            if(node.ancestors.size()!=0) node.authorityS=1/sumAuthorityS;
            else node.authorityS=0;
            for (ActivityNode ancestor: node.ancestors.keySet()){
                for (ActivityNode successorOfAncestor: ancestor.successors.keySet()){
                    if(node.activityName.equals(successorOfAncestor.activityName))successorOfAncestor.authorityS=node.authorityS;
                }
            }
        }
        // IF in-degree > 0 Authority=sumOf(tempAuthority of all successors of all ancestors divided by its in-degree, divided by the out-degree of its ancestor) ELSE Authority=0
        for (ActivityNode node: nodeList){
            tempAuthorityS = 0;
                for (ActivityNode ancestor: node.ancestors.keySet()) {
                    for (ActivityNode successorOfAncestor : ancestor.successors.keySet()) {
                        if(successorOfAncestor.ancestors.size()>0)tempAuthorityS += successorOfAncestor.authorityS / (successorOfAncestor.ancestors.size() * ancestor.successors.size());
                    }
                }
                node.authorityS=tempAuthorityS;
                //update changes in the successors' structure of my ancestors
                for (ActivityNode ancestor: node.ancestors.keySet()){
                    for (ActivityNode successorOfAncestor: ancestor.successors.keySet()){
                        if(node.activityName.equals(successorOfAncestor.activityName))successorOfAncestor.authorityS=node.authorityS;
                    }
                }
        }

        ///////////////hubS update

        //IF out-degree > 0 tempHub=1/norm-1-of(all node with out-degree>0) ELSE tempHub=0
        //check it also in the ancestors' structure of my successors
        for (ActivityNode node: nodeList){
            if(node.successors.size()!=0) node.hubS=1/sumHubS;
            else node.hubS=0;
            for (ActivityNode successor: node.successors.keySet()){
                for (ActivityNode ancestorOfSuccessor: successor.ancestors.keySet()){
                    if(node.activityName.equals(ancestorOfSuccessor.activityName))ancestorOfSuccessor.hubS=node.hubS;
                }
            }
        }
        for (ActivityNode node: nodeList){
            tempHubS = 0;
                for (ActivityNode successor: node.successors.keySet()) {
                    for (ActivityNode ancestorOfSuccessor : successor.ancestors.keySet()) {
                        if(ancestorOfSuccessor.successors.size()>0) tempHubS += ancestorOfSuccessor.hubS / (ancestorOfSuccessor.successors.size() * successor.ancestors.size());
                    }
                }
                node.hubS=tempHubS;
                for (ActivityNode successor: node.successors.keySet()){
                    for (ActivityNode ancestorOfSuccessor : successor.ancestors.keySet()){
                        if(node.activityName.equals(ancestorOfSuccessor.activityName))ancestorOfSuccessor.hubS=node.hubS;
                    }
                }
        }
        for (ActivityNode node: nodeList){
            poolExecutor.schedule(() -> {
                PrefetchingDatabase.getInstance().activityDao().updateLAR(new LARData(node.activityName, node.pageRank,node.authority,node.hub,node.authorityS,node.hubS));
            }, 0, TimeUnit.SECONDS);
            /*for(ActivityNode node2: nodeList) {
                for (ActivityNode ancestor : node2.ancestors.keySet()) {
                    if(ancestor.activityName.equals(node.activityName)) {ancestor.authorityS=node.authorityS;
                    ancestor.hubS=node.hubS;}
                }
                for (ActivityNode successor : node2.successors.keySet()) {
                    if(successor.activityName.equals(node.activityName)){ successor.authorityS=node.authorityS;
                    successor.hubS=node.hubS;}
                }
            }*/
        }
    }
}
