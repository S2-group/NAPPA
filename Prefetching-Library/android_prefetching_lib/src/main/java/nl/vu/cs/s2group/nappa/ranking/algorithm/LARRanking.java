package nl.vu.cs.s2group.nappa.ranking.algorithm;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.db.NappaDB;
import nl.vu.cs.s2group.nappa.db.room.graph.LAR;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.prefetching.strategy.PrefetchingStrategy;

/**
 * This class update scores used by prefetching strategies 5 to 9
 * This code was originally in {@link nl.vu.cs.s2group.nappa.graph.ActivityGraph} and was extracted to this class
 * I don't know what LAR is supposed to mean =/
 * In the future, these strategies and LAR classes are likely to be removed
 */
@Deprecated
public class LARRanking implements RakingAlgorithm {
    private final static String LOG_TAG = LARRanking.class.getSimpleName();
    private final static ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(1);

    @Deprecated
    public static LAR initLAR(String activityName) {
        float initialPageRank = 0.25f;
        float initialAuthority = 1f;
        float initialHub = 1f;
        float initialAuthorityS = 1f;
        float initialHubS = 1f;
        LAR lar = new LAR(activityName, initialPageRank, initialAuthority, initialHub, initialAuthorityS, initialHubS);

        poolExecutor.schedule(() -> {
            NappaDB.getInstance().larDao().insertLAR(lar);
        }, 0, TimeUnit.SECONDS);

        Log.d(LOG_TAG, "LARDataUpdate" + lar.toString());


        return lar;
    }

    @Deprecated
    public static void updateLAR(List<ActivityNode> nodeList, String activityName) {
        switch (PrefetchingLib.prefetchStrategyNum) {
            case PrefetchingStrategy.STRATEGY_5:
            case PrefetchingStrategy.STRATEGY_8:
                ActivityNode node = new ActivityNode(activityName);
                node = nodeList.get(nodeList.lastIndexOf(node));
                updatePR(node, nodeList.size());
                break;

            case PrefetchingStrategy.STRATEGY_6:
            case PrefetchingStrategy.STRATEGY_9:
                updateHITS(nodeList);
                break;

            case PrefetchingStrategy.STRATEGY_7:
                updateSALSA(nodeList);
                break;
        }
    }

    /**
     * Updates PageRank score of a node
     */
    private static void updatePR(@NotNull ActivityNode node, int listSize) {
        float dump = 0.85f;
        float nodePR = 0;
        for (ActivityNode ancestor : node.ancestors.keySet()) {
            nodePR += ancestor.lar.PR / ancestor.successors.size();
        }
        nodePR = (1 - dump) / listSize + dump * nodePR;
        node.lar.PR = nodePR;
        Log.d(LOG_TAG, "LARDataCalculatedUpdate " + node.lar.toString());

        poolExecutor.schedule(() -> {
            NappaDB.getInstance().larDao().updateLAR(node.lar);
        }, 0, TimeUnit.SECONDS);
    }


    /**
     * Updates HITS score of a node
     */
    private static void updateHITS(@NotNull List<ActivityNode> nodeList) {
        float sumAuthority = 0, sumHub = 0;
        for (ActivityNode node : nodeList) {
            float tempAuthority = 0;
            for (ActivityNode ancestor : node.ancestors.keySet()) {
                tempAuthority += ancestor.lar.hub;
            }
            node.lar.authority = tempAuthority;
            sumAuthority += tempAuthority * tempAuthority;
        }
        sumAuthority = (float) Math.sqrt((double) (sumAuthority));
        for (ActivityNode node : nodeList) {
            node.lar.authority /= sumAuthority;
            for (ActivityNode node1 : nodeList)
                if (node1.successors.containsKey(node))
                    node1.successors.put(node, node1.successors.get(node));
        }
        //hub update
        for (ActivityNode node : nodeList) {
            float tempHub = 0;
            for (ActivityNode successor : node.successors.keySet()) {
                tempHub += successor.lar.authority;
            }
            node.lar.hub = tempHub;
            sumHub += tempHub * tempHub;
        }
        sumHub = (float) Math.sqrt((double) (sumHub));
        for (ActivityNode node : nodeList) {
            node.lar.hub /= sumHub;
            for (ActivityNode node1 : nodeList)
                if (node1.ancestors.containsKey(node))
                    node1.ancestors.put(node, node1.ancestors.get(node));
            int index = nodeList.lastIndexOf(node);
            nodeList.set(index, node);
            Log.d(LOG_TAG, "LARDataCalculatedUpdate " + node.lar.toString());

            poolExecutor.schedule(() -> {
                NappaDB.getInstance().larDao().updateLAR(node.lar);
            }, 0, TimeUnit.SECONDS);
        }
    }

    /**
     * Update SALSA score
     */
    private static void updateSALSA(@NotNull List<ActivityNode> nodeList) {
        float sumAuthorityS = 1, sumHubS = 1;
        float tempAuthorityS = 0, tempHubS = 0;
        //AuthorityS update

        //IF in-degree > 0 tempAuthority=1/norm-1-of(all node with in-degree>0) ELSE tempAuthority=0
        //check it also in the successors' structure of my ancestors
        for (ActivityNode node : nodeList) {
            if (node.ancestors.size() != 0) node.lar.authorityS = 1 / sumAuthorityS;
            else node.lar.authorityS = 0;
            for (ActivityNode ancestor : node.ancestors.keySet()) {
                for (ActivityNode successorOfAncestor : ancestor.successors.keySet()) {
                    if (node.activityName.equals(successorOfAncestor.activityName))
                        successorOfAncestor.lar.authorityS = node.lar.authorityS;
                }
            }
        }
        // IF in-degree > 0 Authority=sumOf(tempAuthority of all successors of all ancestors divided by its in-degree, divided by the out-degree of its ancestor) ELSE Authority=0
        for (ActivityNode node : nodeList) {
            tempAuthorityS = 0;
            for (ActivityNode ancestor : node.ancestors.keySet()) {
                for (ActivityNode successorOfAncestor : ancestor.successors.keySet()) {
                    if (successorOfAncestor.ancestors.size() > 0)
                        tempAuthorityS += successorOfAncestor.lar.authorityS / (successorOfAncestor.ancestors.size() * ancestor.successors.size());
                }
            }
            node.lar.authorityS = tempAuthorityS;
            //update changes in the successors' structure of my ancestors
            for (ActivityNode ancestor : node.ancestors.keySet()) {
                for (ActivityNode successorOfAncestor : ancestor.successors.keySet()) {
                    if (node.activityName.equals(successorOfAncestor.activityName))
                        successorOfAncestor.lar.authorityS = node.lar.authorityS;
                }
            }
        }

        //hubS update

        //IF out-degree > 0 tempHub=1/norm-1-of(all node with out-degree>0) ELSE tempHub=0
        //check it also in the ancestors' structure of my successors
        for (ActivityNode node : nodeList) {
            if (node.successors.size() != 0) node.lar.hubS = 1 / sumHubS;
            else node.lar.hubS = 0;
            for (ActivityNode successor : node.successors.keySet()) {
                for (ActivityNode ancestorOfSuccessor : successor.ancestors.keySet()) {
                    if (node.activityName.equals(ancestorOfSuccessor.activityName))
                        ancestorOfSuccessor.lar.hubS = node.lar.hubS;
                }
            }
        }
        for (ActivityNode node : nodeList) {
            tempHubS = 0;
            for (ActivityNode successor : node.successors.keySet()) {
                for (ActivityNode ancestorOfSuccessor : successor.ancestors.keySet()) {
                    if (ancestorOfSuccessor.successors.size() > 0)
                        tempHubS += ancestorOfSuccessor.lar.hubS / (ancestorOfSuccessor.successors.size() * successor.ancestors.size());
                }
            }
            node.lar.hubS = tempHubS;
            for (ActivityNode successor : node.successors.keySet()) {
                for (ActivityNode ancestorOfSuccessor : successor.ancestors.keySet()) {
                    if (node.activityName.equals(ancestorOfSuccessor.activityName))
                        ancestorOfSuccessor.lar.hubS = node.lar.hubS;
                }
            }
        }
        for (ActivityNode node : nodeList) {
            Log.d(LOG_TAG, "LARDataCalculatedUpdate " + node.lar.toString());
            poolExecutor.schedule(() -> {
                NappaDB.getInstance().larDao().updateLAR(node.lar);
            }, 0, TimeUnit.SECONDS);
        }
    }
}
