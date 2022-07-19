/*
 * Plantation.java
 * ---------------------------------
 * Copyright (c) 2022
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
 * This class is for every plantation that is created. Each plantation is designated by its root label and a
 * {@link CongruenceClass} they belong to
 * </p>
 *
 * @author Nicodemus Msafiri J. M.
 *
 * @version v1.0
 */

public class Plantation {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private Integer treeNodeLabel;
    private int firstPlantationCluster;
    private int plantationTag;
    private int nextCCPlantation;
    private int nextVrtyPlantation;
    private int prvVrtyPlantation;

    // ===========================================================
    // Constructors
    // ===========================================================

    // what goes to plantation tag should be the index in the plantation array, it can act as plantation designator
    public Plantation(Integer treeNodeLabel, int firstPlantationCluster, int plantationTag, int nextCCPlantation,
            int nextVrtyPlantation, int prvVrtyPlantation) {
        this.treeNodeLabel = treeNodeLabel;
        this.firstPlantationCluster = firstPlantationCluster;
        this.plantationTag = plantationTag;
        this.nextCCPlantation = nextCCPlantation;
        this.nextVrtyPlantation = nextVrtyPlantation;
        this.prvVrtyPlantation = prvVrtyPlantation;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Integer getTreeNodeLabel() {
        return treeNodeLabel;
    }

    public int getFirstPlantationCluster() {
        return firstPlantationCluster;
    }

    public void setFirstPlantationCluster(int firstPlantationCluster) {
        this.firstPlantationCluster = firstPlantationCluster;
    }

    public int getPlantationTag() {
        return plantationTag;
    }

    public int getNextCCPlantation() {
        return nextCCPlantation;
    }

    public void setNextCCPlantation(int nextCCPlantation) {
        this.nextCCPlantation = nextCCPlantation;
    }

    public int getNextVrtyPlantation() {
        return nextVrtyPlantation;
    }

    public void setNextVrtyPlantation(int nextVrtyPlantation) {
        this.nextVrtyPlantation = nextVrtyPlantation;
    }

    public void setPrvVrtyPlantation(int prvVrtyPlantation) {
        this.prvVrtyPlantation = prvVrtyPlantation;
    }

    public int getPrvVrtyPlantation() {
        return prvVrtyPlantation;
    }

    /**
     * <p>
     * This method returns the plantation in string format.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String toString() {
        return "|" + "treeNodeLabel=" + treeNodeLabel + "||firstStandCluster=" + firstPlantationCluster + "||standTag="
                + plantationTag + "||nextCCStand=" + nextCCPlantation + "||nextVrtyStand=" + nextVrtyPlantation
                + "||prvVrtyStand=" + prvVrtyPlantation + "|";
    }
}