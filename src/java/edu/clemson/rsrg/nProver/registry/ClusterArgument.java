/*
 * ClusterArgument.java
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
 * This class is for each cluster argument that is stored in the cluster argument array
 * </p>
 *
 * @author Nicodemus Msafiri J. M.
 *
 * @version v1.0
 */
public class ClusterArgument {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private int nextClusterArg;
    private int prevClusterArg;
    private int ccNumber; // these are congruence class designators
    private int nexIndexWithSameCCInSameLevel; // points to the same congruence class in the in the same level of
                                               // argument string.
    private int clusterNumber; // the first cluster of the chained clusters with same argument
    private int alternativeArg;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ClusterArgument(int nextClusterArg, int prevClusterArg, int ccNumber, int clusterNumber,
            int alternativeArg) {
        this.nextClusterArg = nextClusterArg;
        this.prevClusterArg = prevClusterArg;
        this.ccNumber = ccNumber;
        nexIndexWithSameCCInSameLevel = 0;
        this.clusterNumber = clusterNumber;
        this.alternativeArg = alternativeArg;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public int getNextClusterArg() {
        return nextClusterArg;
    }

    public void setNextClusterArg(int nextClusterArg) {
        this.nextClusterArg = nextClusterArg;
    }

    public int getPrevClusterArg() {
        return prevClusterArg;
    }

    public void setPrevClusterArg(int prevClusterArg) {
        this.prevClusterArg = prevClusterArg;
    }

    public int getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(int ccNumber) {
        this.ccNumber = ccNumber;
    }

    public int getNxtIndexWithSameCCNumberInLevel() {
        return nexIndexWithSameCCInSameLevel;
    }

    public void setNexIndexWithSameCCInSameLevel(int nexIndexWithSameCCInSameLevel) {
        this.nexIndexWithSameCCInSameLevel = nexIndexWithSameCCInSameLevel;
    }

    public int getClusterNumber() {
        return clusterNumber;
    }

    public void setClusterNumber(int clusterNumber) {
        this.clusterNumber = clusterNumber;
    }

    public int getAlternativeArg() {
        return alternativeArg;
    }

    public void setAlternativeArg(int alternativeArg) {
        this.alternativeArg = alternativeArg;
    }

    /**
     * <p>
     * This method returns the cluster argument in string format.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String toString() {
        return "|" + "nextClusterArg=" + nextClusterArg + "||prevClusterArg=" + prevClusterArg + "||ccNumber="
                + ccNumber + "||clusterNumber=" + clusterNumber + "||nexIndexWithSameCCInSameLevel="
                + nexIndexWithSameCCInSameLevel + "||alternativeArg=" + alternativeArg + "|";
    }
}