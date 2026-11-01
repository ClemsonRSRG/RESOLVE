package edu.clemson.rsrg.nProver.utilities.theorems;

import java.util.*;

public class coveringDispenser {
    HashMap<Integer, Set<Integer>> blockerSet = new HashMap<>();
    Set<Integer> sourceSet =  new HashSet<>();
    int blocker = -1;
    Set<Integer> sources = new HashSet<>();

    public void peremptoryAddSourceDesignator(int source) {
        sourceSet.add(source);
    }

    public void insertBlockerDesignator(int blocker) {
        this.blocker = blocker;
    }

    public int insertSourceDesignator(int source) {
        if(blocker == -1 || !sourceSet.contains(source)) {
            return -1;
        }
        sources.add(source);
        return 0;
    }

    public void addNextBlocker(int blocker) {
        blockerSet.put(blocker, sourceSet);
        this.blocker = blocker;
        this.sourceSet = new HashSet<>();
    }

    public Set<Set<Integer>> formCoveringSet() {
        return coverSetHelper(blockerSet.keySet()).coveredSet;
    }

    private Cover coverSetHelper(Set<Integer> blockers) {
        Cover cover = new Cover();

        for(Integer b: blockers) {
            Set<Integer> blockerSubset = new HashSet<>(blockers);
            blockerSubset.remove(b);

            Cover subcover = coverSetHelper(blockerSubset);
            if(subcover.covered) {
                cover.coveredSet.addAll(subcover.coveredSet);
                cover.coveredSet.add(blockers);
                cover.covered = true;
            } else {
                cover.uncoveredSources = new HashSet<>(subcover.uncoveredSources);
                cover.uncoveredSources.removeAll(blockerSet.get(b));
                if(cover.uncoveredSources.isEmpty()) {
                    cover.coveredSet.add(blockers);
                    cover.covered = true;
                }
            }
        }

        return cover;
    }

    static class Cover {
        private boolean covered = false;
        private HashSet<Integer> uncoveredSources;
        private Set<Set<Integer>> coveredSet;
    }
}