package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.util.NappaConfigMap;
import nl.vu.cs.s2group.nappa.util.NappaUtil;

// TODO Unordered list of tasks to complete issue #52
//  * Decide which fields to use in the inner class TFPRNode
//  * Implement subgraph initialization
//  * Implement the page rank calculation
//  * Do we need to keep the previous score or the current score only
//  * Verify if we need the inner class
//  * Decide how many iterations to run
//  * Add a configurable parameter for the number of runs
//  * Decide on which nodes to use for the subgraph --> all successors from current node and all nodes that points to the successors
//  * Add a LiveData list in ActivityNode with the successors aggregate time
//  * Test the Handler idea to initialize this LiveData object
//  * Create utility method to get the total time given the LiveData list

/**
 * This strategy employs a Link Analysis approach implementing a PageRank-based algorithm
 * using the time a user spends in the activities and how frequent the user access the
 * activities to decide which nodes to select.
 * <p>
 * Only a subgraph of the ENG is considered in the calculations. The score calculations are
 * performed at runtime and are not persisted in the database.
 * <p>
 * Strategy inspired on the paper Personalized PageRank for Web Page Prediction Based
 * on Access Time-Length and Frequency from 2007.
 * <p>
 * This strategy accepts the following configurations:
 * <ul>
 *     <li>{@link PrefetchingStrategyConfigKeys#WEIGHT_FREQUENCY_SCORE}</li>
 *     <li>{@link PrefetchingStrategyConfigKeys#WEIGHT_TIME_SCORE}</li>
 * </ul>
 *
 * @see <a href="https://dl.acm.org/doi/10.1109/WI.2007.145">Personalized PageRank paper</a>
 */
public class TfprPrefetchingStrategy extends AbstractPrefetchingStrategy {
    private static final String LOG_TAG = TfprPrefetchingStrategy.class.getSimpleName();

    private static final float DEFAULT_WEIGHT_FREQUENCY_SCORE = 0.5f;
    private static final float DEFAULT_WEIGHT_TIME_SCORE = 0.5f;

    protected final float weightFrequencyScore;
    protected final float weightTimeScore;

    @Override
    public boolean needVisitTime() {
        return true;
    }

    public TfprPrefetchingStrategy() {
        super();

        weightFrequencyScore = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.WEIGHT_FREQUENCY_SCORE,
                DEFAULT_WEIGHT_FREQUENCY_SCORE);

        weightTimeScore = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.WEIGHT_TIME_SCORE,
                DEFAULT_WEIGHT_TIME_SCORE);

        if ((weightFrequencyScore + weightTimeScore) != 1.0)
            throw new IllegalArgumentException("The sum of the time and frequency weight must be 1!");
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(@NotNull ActivityNode node, Integer maxNumber) {
        long startTime = new Date().getTime();
        TfprGraph graph = makeSubgraph(node);

        Log.d(LOG_TAG, node.activityName + " found successors in " + (new Date().getTime() - startTime) + " ms");
        return new ArrayList<>();
    }

    private List<TfprNode> runPageRank(List<TfprNode> graph) {
        return new ArrayList<>();
    }

    /**
     * Create an instance of {@link TfprGraph} with a subgraph G of all nodes to consider
     * in this calculation. The subgraph includes the following nodes:
     *
     * <ul>
     *     <li> The current node </li>
     *     <li> All successors of the current node </li>
     *     <li> All parents of all successors of the current node </li>
     * </ul>
     * <p>
     * All the {@link TfprNode} created here contains the links to their respective {@link
     * ActivityNode} object and their parents and successors present in the subgraph.
     *
     * @param currentNode Represents the {@link android.app.Activity} the user navigated to
     * @return A instance of {@link TfprGraph} without the aggregate visit time weights.
     */
    @NotNull
    private TfprGraph makeSubgraph(@NotNull ActivityNode currentNode) {
        TfprGraph tfprGraph = new TfprGraph();

        tfprGraph.dampingFactor = 0.85f;

        // Creates a TFPR node for the current node
        TfprNode currentNodeTfprNode = new TfprNode(currentNode);
        tfprGraph.graph.put(currentNode.activityName, currentNodeTfprNode);

        // Create TFPR nodes for all successors of the current node and add parent-successor links
        for (ActivityNode successor : currentNode.successors.keySet()) {
            TfprNode successorTfprNode = getOrCreateTfprNode(tfprGraph, successor);
            linkNodesAsSuccessorParent(successorTfprNode, currentNodeTfprNode);

            // Create TFPR nodes for all the parents of this successor and add parent-successor links
            for (ActivityNode successorParent : successor.ancestors.keySet()) {
                TfprNode successorParentTfprNode = getOrCreateTfprNode(tfprGraph, successorParent);
                linkNodesAsSuccessorParent(successorTfprNode, successorParentTfprNode);
            }
        }

        return tfprGraph;
    }

    /**
     * Link the nodes {@code successor} and {@code parent} if they are not linked yet
     *
     * @param successor The successor node.
     * @param parent    The parent node.
     */
    private void linkNodesAsSuccessorParent(@NotNull TfprNode successor, @NotNull TfprNode parent) {
        if (!parent.successors.contains(successor)) parent.successors.add(successor);
        if (!successor.parents.contains(parent)) successor.parents.add(parent);
    }

    /**
     * Verifies if the provided node exists in the graph. If so, return it, otherwise
     * create a new node and add it to the graph
     *
     * @param tfprGraph The graph to add a new {@link TfprNode} if it doesn't contain
     *                  the provided node
     * @param node      The node used as reference to get an existent or create a new
     *                  {@link TfprNode}
     * @return Either an existent or a new node
     */
    private TfprNode getOrCreateTfprNode(@NotNull TfprGraph tfprGraph, @NotNull ActivityNode node) {
        if (tfprGraph.graph.containsKey(node.activityName))
            return tfprGraph.graph.get(node.activityName);

        TfprNode newNode = new TfprNode(node);
        tfprGraph.graph.put(node.activityName, newNode);

        return newNode;
    }

    private TfprGraph calculateVisitTimeScores(TfprGraph tfprGraph) {
        for (TfprNode tfprNode : tfprGraph.graph.values()) {
            // Obtain t(u)
            tfprNode.aggregateVisitTime = tfprNode.node.getAggregateVisitTime().totalDuration;

            // Obtain partial SUM(t(w)) | w e G
            tfprGraph.aggregateVisitTime += tfprNode.aggregateVisitTime;

            // Obtain SUM(t(v, w)) | w e F_v
            tfprNode.totalAggregateVisitTimeFromSuccessors = NappaUtil.getSuccessorsAggregateVisitTimeOriginatedFromNode(tfprNode.node);

            // Obtain all
            for (TfprNode successor : tfprNode.successors) {
                successor.aggregateVisitTimeFromSuccessors.put(
                        successor.node.activityName,
                        NappaUtil.getSuccessorsAggregateVisitTimeOriginatedFromNode(tfprNode.node, successor.node));
            }
        }

        return tfprGraph;
    }

    private class TfprGraph {
        /**
         * Represents G, the subgraph used to compute the TFPR score. This subgraph is
         * stored as a hash map that maps the name of the activity to its TFPR node.
         */
        Map<String, TfprNode> graph;

        /**
         * Represents SUM(t(w)) | w e G, the total time spent on all pages of the tree.
         */
        long aggregateVisitTime;

        /**
         * Represents alpha
         */
        float dampingFactor;

        public TfprGraph() {
            this.graph = new HashMap<>();
        }

        @NonNull
        @Override
        public String toString() {
            return "TfprGraph{" +
                    graph.values()
                            .stream()
                            .map(TfprNode::toString)
                            .collect(Collectors.joining("\n")) +
                    '}';
        }
    }

    private class TfprNode {
        /**
         * Represents B_u, the set of pages that link to page u.
         */
        List<TfprNode> parents;

        /**
         * Represents F_v, the set of pages that page v links to.
         * From the point of view of each parent
         */
        List<TfprNode> successors;

        /**
         * A reference to the default node representation. Needed to obtain the URLs
         */
        ActivityNode node;

        /**
         * Represents Tvu, the  sum  of  the  time–lengths  spent  on  visiting  page  u
         * when  page  v  and  u  were  visited consecutively. if more occurrences of the
         * path v→u  occurs and the user stay on page u for a longer  time, the  value
         * of t(v, u) will be larger, thus t(v,u) covers both information of access
         * time-length and access frequency of a page u.
         */
        Map<String, Long> aggregateVisitTimeFromSuccessors;

        /**
         * Represents TFPR(u)
         */
        float tfprScore;

        /**
         * Represent t(u), the total time spent on page u.
         */
        long aggregateVisitTime;

        /**
         * Represents SUM(t(v, w)) | w e F_v, the total time spent on all pages when accessed from
         * a page v.
         */
        long totalAggregateVisitTimeFromSuccessors;

        public TfprNode(ActivityNode node) {
            parents = new ArrayList<>();
            successors = new ArrayList<>();
            this.node = node;
        }

        @NonNull
        @Override
        public String toString() {
            return "TfprNode{" +
                    node.activityName + " : " +
                    "TFPR = " + tfprScore +
                    '}';
        }
    }
}
