/*
 * Stand.java
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

/**
 * <p>
 * This class is for every stand that is created. Each stand is designated by its root label and a
 * {@link CongruenceClass} they belong to
 * </p>
 *
 * @author Nicodemus Msafiri J. M.
 *
 * @version v1.0
 */

public class Stand {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private Integer treeNodeLabel;
    private int firstStandCluster;
    private int standTag;
    private int nextCCStand;
    private int nextVrtyStand;
    private int prvVrtyStand;

    // ===========================================================
    // Constructors
    // ===========================================================

    // what goes to stand tag should be the index in the stand array, it can act as stand designator
    public Stand(Integer treeNodeLabel, int firstStandCluster, int standTag, int nextCCStand, int nextVrtyStand,
            int prvVrtyStand) {
        this.treeNodeLabel = treeNodeLabel;
        this.firstStandCluster = firstStandCluster;
        this.standTag = standTag;
        this.nextCCStand = nextCCStand;
        this.nextVrtyStand = nextVrtyStand;
        this.prvVrtyStand = prvVrtyStand;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Integer getTreeNodeLabel() {
        return treeNodeLabel;
    }

    public int getFirstStandCluster() {
        return firstStandCluster;
    }

    public void setFirstStandCluster(int firstStandCluster) {
        this.firstStandCluster = firstStandCluster;
    }

    public int getStandTag() {
        return standTag;
    }

    public int getNextCCStand() {
        return nextCCStand;
    }

    public void setNextCCStand(int nextCCStand) {
        this.nextCCStand = nextCCStand;
    }

    public int getNextVrtyStand() {
        return nextVrtyStand;
    }

    public void setNextVrtyStand(int nextVrtyStand) {
        this.nextVrtyStand = nextVrtyStand;
    }

    public void setPrvVrtyStand(int prvVrtyStand) {
        this.prvVrtyStand = prvVrtyStand;
    }

    public int getPrvVrtyStand() {
        return prvVrtyStand;
    }

    /**
     * <p>
     * This method returns the stand in string format.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String toString() {
        return "|" + "treeNodeLabel=" + treeNodeLabel + "||firstStandCluster=" + firstStandCluster + "||standTag="
                + standTag + "||nextCCStand=" + nextCCStand + "||nextVrtyStand=" + nextVrtyStand + "||prvVrtyStand="
                + prvVrtyStand + "|";
    }
}