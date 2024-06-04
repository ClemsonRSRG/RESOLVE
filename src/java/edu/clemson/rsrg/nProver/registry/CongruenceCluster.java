/*
 * CongruenceCluster.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.nProver.registry;

import edu.clemson.rsrg.nProver.registry.CongruenceClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * This class is for every cluster that is created and belonging to one of the {@link CongruenceClass} all clusters are
 * also belong to one of the {@link Plantation}
 * </p>
 *
 * @author Nicodemus Msafiri J. M.
 *
 * @version v1.0
 */
public class CongruenceCluster {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private Integer treeNodeLabel;
    private int indexToArgumentList;
    private int indexToCongruenceClass;
    private int tag;
    private int nextPlantationCluster;
    private int previousPlantationCluster;
    private int dominantCluster;
    private int nextWithSameArg;

    // ===========================================================
    // Constructors
    // ===========================================================

    public CongruenceCluster(Integer treeNodeLabel, int toArgList, int toCC, int toClusterTag,
            int nextPlantationCluster, int previousPlantationCluster, int dominantCluster, int nextWithSameArg) {
        this.treeNodeLabel = treeNodeLabel;
        indexToArgumentList = toArgList;
        indexToCongruenceClass = toCC;
        tag = toClusterTag; // to a structure that keeps the tags
        this.nextPlantationCluster = nextPlantationCluster; // next cluster in the plantation in same class, having same
                                                            // root node label
        this.previousPlantationCluster = previousPlantationCluster; // previous cluster
        this.dominantCluster = dominantCluster; // will point to itself initially, and gets the smaller dominant when it
                                                // is merged to other cluster
        this.nextWithSameArg = nextWithSameArg; // points to the next cluster with same arguments
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Integer getTreeNodeLabel() {
        return treeNodeLabel;
    }

    public int getIndexToArgList() {
        return indexToArgumentList;
    }

    public void setIndexToArgumentList(int newIndex) {
        indexToArgumentList = newIndex;
    }

    public int getIndexToCongruenceClass() {
        return indexToCongruenceClass;
    }

    public void setIndexToCongruenceClass(int newIndex) {
        indexToCongruenceClass = newIndex;
    }

    public int getIndexToTag() {
        return tag;
    }

    public void setIndexToTag(int newIndex) {
        tag = newIndex;
    }

    public int getNextPlantationCluster() {
        return nextPlantationCluster;
    }

    public void setNextPlantationCluster(int newIndex) {
        nextPlantationCluster = newIndex;
    }

    public int getPreviousPlantationCluster() {
        return previousPlantationCluster;
    }

    public void setPreviousPlantationCluster(int newIndex) {
        previousPlantationCluster = newIndex;
    }

    public int getDominantCluster() {
        return dominantCluster;
    }

    public void setDominantCluster(int newIndex) {
        dominantCluster = newIndex;
    }

    public int getNextWithSameArg() {
        return nextWithSameArg;
    }

    public void setNextWithSameArg(int newIndex) {
        nextWithSameArg = newIndex;
    }

    /**
     * <p>
     * This method returns the congruence cluster in string format.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String toString() {
        return "|" + "treeNodeLabel=" + treeNodeLabel + "||indexToArgumentList=" + indexToArgumentList
                + "||indexToCongruenceClass=" + indexToCongruenceClass + "||nextStandCluster=" + nextPlantationCluster
                + "||previousStandCluster=" + previousPlantationCluster + "||dominantCluster=" + dominantCluster
                + "||nextWithSameArg=" + nextWithSameArg + "|";
    }
}