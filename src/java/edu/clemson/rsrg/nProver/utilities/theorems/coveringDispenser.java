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

    public void addNextBlocker() {
        blockerSet.put(blocker, sources);
        this.blocker = -1;
        this.sources = new HashSet<>();
    }

    public Set<Set<Integer>> formCoveringSet() {
        return coverSetHelper(blockerSet.keySet()).coveredSets;
    }

    private Cover coverSetHelper(Set<Integer> blockers) {
        Cover cover = new Cover();

        for(Integer b: blockers) {
            Set<Integer> blockerSubset = new HashSet<>(blockers);
            blockerSubset.remove(b);

            if(blockerSubset.isEmpty()) {
                cover.coveredSources.addAll(blockerSet.get(b));
                return cover;
            }

            Cover subcover = coverSetHelper(blockerSubset);
            if(cover.covered){
                cover.coveredSets.addAll(subcover.coveredSets);
            } else {
                if(subcover.covered) {
                    cover.covered = true;
                    cover.coveredSets.addAll(subcover.coveredSets);
                } else {
                    cover.coveredSources.addAll(subcover.coveredSources);
                    if(cover.coveredSources.size() == sourceSet.size()) {
                        cover.covered = true;
                    }
                }
            }
        }

        if(cover.covered)
            cover.coveredSets.add(blockers);

        return cover;
    }

    static class Cover {
        private boolean covered = false;
        private final Set<Integer> coveredSources = new HashSet<>();
        private final Set<Set<Integer>> coveredSets =  new HashSet<>();
    }
}