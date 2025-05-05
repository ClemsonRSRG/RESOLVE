/*
 * VarietyList.java
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
 * This class is a start of the variety list starting from the first stand designated by the tree node label. Each
 * variety list created will be stored in the variety array
 * </p>
 *
 * @author Nicodemus Msafiri J. M.
 *
 * @version v1.0
 */
public class VarietyList {

    private int firstStand;
    private int varietyTagIndex;

    public VarietyList(int firstStand, int indexToVarietyTag) {
        this.firstStand = firstStand;
        varietyTagIndex = indexToVarietyTag;
    }

    public int getFirstStand() {
        return firstStand;
    }

    public int getVarietyTagIndex() {
        return varietyTagIndex;
    }

    public void setFirstStand(int newFirstStand) {
        firstStand = newFirstStand;
    }
}
