package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.util.NappaUtil;

// TODO Unordered list of tasks to complete issue #52
//  * Implement the page rank calculation
//  * Decide how many iterations to run
//  * Add a configurable parameter for the number of runs
//  * Verify if handler for successor visit time is working

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
 * </ul>
 *
 * @see <a href="https://dl.acm.org/doi/10.1109/WI.2007.145">Personalized PageRank paper</a>
 */
public class TfprPrefetchingStrategy extends AbstractPrefetchingStrategy {
    private static final String LOG_TAG = TfprPrefetchingStrategy.class.getSimpleName();

    public TfprPrefetchingStrategy() {
        super();
    }

    @Override
    public boolean needVisitTime() {
        return true;
    }

    @Override
    public boolean needSuccessorsVisitTime() {
        return true;
    }

    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(@NotNull ActivityNode node, Integer maxNumber) {
        long startTime = new Date().getTime();

        // Prepare the graph
        TfprGraph graph = makeSubgraph(node);
        calculateVisitTimeScores(graph);

        // Run page rank
        runTfprAlgorithm(graph);

        // Select all nodes with score above the threshold
        List<ActivityNode> selectedNodes = getSuccessorListSortByTfprScore(graph, node);

        // Select all URLs that fits the budget
        List<String> selectedUrls = getUrls(node, selectedNodes);

        Log.d(LOG_TAG, node.activityName + " found successors in " + (new Date().getTime() - startTime) + " ms");
        return selectedUrls;
    }

    @NotNull
    private List<String> getUrls(ActivityNode currentNode, @NotNull List<ActivityNode> nodes) {
        List<String> urls = new ArrayList<>();

        for (ActivityNode node : nodes) {
            int remainingUrlBudget = maxNumberOfUrlToPrefetch - urls.size();
            List<String> nodUrls = NappaUtil.getUrlsFromCandidateNode(currentNode, node, remainingUrlBudget);
            urls.addAll(nodUrls);

            Log.d(LOG_TAG, "Selected node " + node.getActivitySimpleName() +
                    " containing the URLS " + nodUrls);
            if (urls.size() >= maxNumberOfUrlToPrefetch) break;
        }

        return urls;
    }

    /**
     * Take the successors of the current node, sort by their TFPR score and return an array
     * with all the successors that have a TFPR score higher than the lower threshold score.
     *
     * @param graph       The subgraph after running the TFPR algorithm
     * @param currentNode The {@link android.app.Activity} the user navigated to
     * @return An array of successors sorted by TFPR score
     */
    @NotNull
    private List<ActivityNode> getSuccessorListSortByTfprScore(@NotNull TfprGraph graph, @NotNull ActivityNode currentNode) {
        //noinspection ConstantConditions We do not add null values to the map
        List<TfprNode> sortedNodes = graph.graph.get(currentNode.activityName).successors;
        sortedNodes.sort(Comparator.comparing(node -> node.tfprScore));

        return Arrays.asList(sortedNodes
                .stream()
                .filter(node -> node.tfprScore >= scoreLowerThreshold)
                .map(successor -> successor.node)
                .toArray(ActivityNode[]::new));
    }

    /**
     * Run the TFPR algorithm to calculate the TFPR score
     *
     * @param graph A subgraph with parents/successors linked and visit time weights
     *              calculated
     */
    private void runTfprAlgorithm(TfprGraph graph) {
        for (int i = 0; i < numberOfIterations; i++) {

        }
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

    /**
     * Take an instance of {@link TfprGraph} with liked parents and successors and run the
     * calculations to obtain the visit time weight required to run the TFPR algorithm.
     *
     * @param tfprGraph The subgraph to run the TFPR algorithm
     */
    private void calculateVisitTimeScores(@NotNull TfprGraph tfprGraph) {
        for (TfprNode tfprNode : tfprGraph.graph.values()) {
            // Obtain t(u)
            tfprNode.aggregateVisitTime = tfprNode.node.getAggregateVisitTime().totalDuration;

            // Obtain partial SUM(t(w)) | w e G
            tfprGraph.aggregateVisitTime += tfprNode.aggregateVisitTime;

            // Obtain SUM(t(v, w)) | w e F_v
            tfprNode.totalAggregateVisitTimeFromSuccessors = NappaUtil.getSuccessorsAggregateVisitTimeOriginatedFromNode(tfprNode.node);

            // Obtain t(v, u) for all pairs of v -> u where v is the current node
            tfprNode.aggregateVisitTimeFromSuccessors = NappaUtil.getSuccessorsAggregateVisitTimeOriginatedFromNodeMap(tfprNode.node);
        }

    }

    private static class TfprGraph {
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
            return "TfprGraph{\n" +
                    graph.values()
                            .stream()
                            .map(TfprNode::toString)
                            .collect(Collectors.joining("\n")) +
                    '}';
        }
    }

    private static class TfprNode {
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
                    node.getActivitySimpleName() + " : " +
                    "TFPR = " + tfprScore +
                    '}';
        }
    }
}
