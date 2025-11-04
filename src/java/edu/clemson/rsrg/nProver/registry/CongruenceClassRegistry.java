/*
 * CongruenceClassRegistry.java
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

import java.util.*;

/**
 * <p>
 * Congruence class registry, a key component in the nProver. It stores the sequent VC and performs all the
 * manipulations necessary to prove the VC's correctness.
 * </p>
 *
 * @author Nicodemus Msafiri J. M.
 *
 * @version v1.0
 *
 * @param <T1>
 *            type of tree node label selected
 * @param <T2>
 *            tree category tag
 * @param <T3>
 *            default tree category attribute
 * @param <T4>
 */

public class CongruenceClassRegistry<T1, T2, T3, T4> {

    /**
     * <p>
     * Maximum capacity set for congruece class designators
     * </p>
     */
    private int ccDesignatorCapacity;
    /**
     * <p>
     * Maximum capacity set for cluster designators
     * </p>
     */
    private int cClusterDesignatorCapacity;

    /**
     * <p>
     * Maximum capacity set for the arguments
     * </p>
     */
    private int argumentListCapacity;

    /**
     * <p>
     * Maximum capacity set for the registry root labels
     * </p>
     */
    private int rootLabelCapacity;

    /**
     * <p>
     * Maximum capacity set for the registry labels
     * </p>
     */
    private int topLabelCapacity;

    /**
     * <p>
     * Top congruence class designator representing the most recent used integer value to designate a class
     * </p>
     */
    private int topCongruenceClassDesignator;

    /**
     * <p>
     * Top congruence cluster designator representing the most recent used integer value to designate a class
     * </p>
     */
    private int topCongruenceClusterDesignator;

    /**
     * <p>
     * This array keeps a list of congruence classes containing at least one tree with a root node specified by an index
     * of this array.
     * </p>
     */
    private VarietyList[] varietyArray;

    /**
     * <p>
     * This array keeps a list of congruence clusters containing the same root node.
     * </p>
     */
    private Stand[] standArray;

    /**
     * <p>
     * This array keeps all created congruence clusters in the registry for the target sequent VC to be verified.
     * </p>
     */
    private CongruenceCluster[] clusterArray;

    /**
     * <p>
     * This array keeps all created congruence classes in the registry for the target sequent VC to be verified.
     * </p>
     */
    private CongruenceClass[] congruenceClassArray;

    /**
     * <p>
     * This array keeps all the arguments for the created clusters effectively. Arguments used in more than one cluster
     * are created only once.
     * </p>
     */
    private ClusterArgument[] clusterArgumentArray;

    /**
     * <p>
     * This Queue store the arguments appended by the client before creating a cluster.
     * </p>
     */
    private Queue<Integer> clusterArgumentString;

    /**
     * <p>
     * This Queue keeps the list of all classes that are to be merged as a consequence of two current classes
     * collapsing.
     * </p>
     */
    private Queue<Integer> classMergeList;

    /**
     * <p>
     * This Set keeps the reflexive operators that appears on the succedent of the sequent VC to be proved. The client
     * add respective integers for the operators before the operator is registered.
     * </p>
     */
    private Set<Integer> succedentReflexiveOperatorsSet;

    /**
     * <p>
     * This is stand designator used as an index in stand array and stand tag as the cluster is created
     * </p>
     */
    private int indexForStandArray;

    /**
     * <p>
     * The most recent used index in the {@link ClusterArgument}
     * </p>
     */
    private int topArgStrArrIndex;

    /**
     * <p>
     * Starting index for the arguments, first two indices are reserved.
     * </p>
     */
    private static final int START_ARG_INDEX = 2;

    /**
     * <p>
     * This boolean flag indicates whether the sequent VC is proved or not.
     * </p>
     */
    private boolean isProved;

    /**
     * <p>
     * This boolean flag indicates whether the sequent VC contains the reflexive operators in the succedent.
     * </p>
     */
    private boolean succedentReflexiveOperatorTest;

    /**
     * <p>
     * The constructor for the registry that stores the target sequent VC in classes that contain clusters organized in
     * varieties and stands.
     * </p>
     *
     * @param ccDesignatorCapacity
     *            The maximum capacity provided for congruence class designators
     * @param cClusterDesignatorCapacity
     *            The maximum capacity provided for congruence cluster designators
     * @param argumentListCapacity
     *            The maximum capacity provided for arguments
     * @param rootLabelCapacity
     *            The maximum capacity provided for root labels.
     */
    public CongruenceClassRegistry(int ccDesignatorCapacity, int cClusterDesignatorCapacity, int argumentListCapacity,
            int rootLabelCapacity) {
        this.ccDesignatorCapacity = ccDesignatorCapacity;
        this.cClusterDesignatorCapacity = cClusterDesignatorCapacity;
        this.argumentListCapacity = argumentListCapacity;
        this.rootLabelCapacity = rootLabelCapacity;

        topCongruenceClassDesignator = 0;
        topCongruenceClusterDesignator = 0;
        indexForStandArray = 0;
        topLabelCapacity = 0;
        topArgStrArrIndex = START_ARG_INDEX;
        isProved = false;
        succedentReflexiveOperatorTest = false;

        varietyArray = new VarietyList[rootLabelCapacity];
        standArray = new Stand[rootLabelCapacity];
        clusterArray = new CongruenceCluster[cClusterDesignatorCapacity];
        congruenceClassArray = new CongruenceClass[ccDesignatorCapacity];
        clusterArgumentArray = new ClusterArgument[100000];
        clusterArgumentString = new ArrayDeque<>();
        classMergeList = new ArrayDeque<>();
        succedentReflexiveOperatorsSet = new HashSet<>();

        // start the index 0 with {0,0,0,0,0,0} by creating a cluster object,
        // with 0 index to argument list then update later
        CongruenceCluster cCluster = new CongruenceCluster(0, 0, 0, 0, 0, 0, 0, 0);

        // put the created cluster into the cluster array
        clusterArray[0] = cCluster;

        // start the index 0 with {0,0,0,0,0,0} by creating an argument array
        ClusterArgument cArgument = new ClusterArgument(0, 0, 0, 0, 0);
        clusterArgumentArray[0] = cArgument;

        // start the index 0 with {0,0,0,0,0,0} by creating a stand
        Stand stand = new Stand(0, 0, 0, 0, 0, 0);

        // put the initial created stand into the array
        standArray[0] = stand;

        // start the index 0 with {0,0,0,0,0} with {0,0,0} attribute in the congruence class array.
        BitSet attribute = new BitSet();
        attribute.clear();
        CongruenceClass cClass = new CongruenceClass(0, 0, 0, 0);
        cClass.setClassAttribute(attribute);
        congruenceClassArray[0] = cClass;

    }

    /****************************************************************************************************************
     * PUBLIC METHODS
     ***************************************************************************************************************/

    /**
     * <p>
     * The operation registers a new singleton class with one cluster, both are assigned a new designator and accessor
     * to the class is returned
     * </p>
     *
     * @param treeNodeLabel
     *            An integer value to represent the tree node being registered.
     *
     * @return integer value representing accessor for the class created.
     */
    public int registerCluster(Integer treeNodeLabel) {
        int nextWithSimilarArgString = 0;
        int nextStandCluster = 0;
        int prevStandCluster = 0;
        int nextCCStand = 0;
        int nextVrtyStand = 0;
        int prvVrtyStand = 0;

        // special Bingo check for reflexive operators in the succedent before we continue normally if the VC is not
        // proved
        if (succedentReflexiveOperatorsSet.contains(treeNodeLabel)) {
            Queue<Integer> tempArgList = new ArrayDeque<>();
            Integer tempClassDesignator;
            int iter = clusterArgumentString.size();

            // create a copy as it will be needed if the VC is not proved and normal registration of reflexive operator
            // is resumed.
            while (iter > 0) {
                tempClassDesignator = clusterArgumentString.remove();
                tempArgList.add(tempClassDesignator);
                clusterArgumentString.add(tempClassDesignator);
                iter--;
            }

            // use an internal procedure to do what are congruent is doing, and call that inside are congruent operation
            // are congruent is meant for the client outside
            if (areClassesCongruent(tempArgList.remove(), tempArgList.remove())) {
                isProved = true;
            } else {
                // for efficiency, this will tell the registry there is a reflexive operator in the succedent and the
                // special test should be activated otherwise don't waste any resources
                succedentReflexiveOperatorTest = true;
            }
        }

        if (isProved == false) {

            topCongruenceClassDesignator++;
            topCongruenceClusterDesignator++;
            indexForStandArray++;
            topLabelCapacity++;

            // this is the last position in the argument string array in terms of depth from the empty arg string
            int lastArgStringPos = 0;
            // create a stand class object
            Stand stand = new Stand(treeNodeLabel, indexForStandArray, indexForStandArray, nextCCStand, nextVrtyStand,
                    prvVrtyStand);
            // put the created stand into the stand array
            standArray[indexForStandArray] = stand;

            // create a congruence class object
            // indexInArgArray is the Arg string occurrence position, an index for the created arg string for this
            // cluster
            CongruenceClass cClass = new CongruenceClass(indexForStandArray, topCongruenceClassDesignator,
                    lastArgStringPos, topCongruenceClassDesignator);

            // put the created class into the congruence class array
            congruenceClassArray[topCongruenceClassDesignator] = cClass;

            // create a cluster object, with 0 index to argument list then update later once tags are included
            CongruenceCluster cCluster = new CongruenceCluster(treeNodeLabel, 0, topCongruenceClassDesignator,
                    topCongruenceClusterDesignator, nextStandCluster, prevStandCluster, topCongruenceClusterDesignator,
                    nextWithSimilarArgString);

            // put the created cluster into the cluster array
            clusterArray[topCongruenceClusterDesignator] = cCluster;

            // get the index created after putting the argument string for this cluster
            int indexInArgArray = createClusterArgumentArray(treeNodeLabel, clusterArgumentString);

            // set the index to argument array,
            clusterArray[topCongruenceClusterDesignator].setIndexToArgumentList(indexInArgArray);

            // update variety list array
            addInVarietyListArray(treeNodeLabel, indexForStandArray, indexForStandArray);

            return topCongruenceClassDesignator;
        } else {
            return 0;
        }
    }

    /**
     * <p>
     * This operatipon checks if the cluster to be registered already exists in the registry. It involves checking into
     * the cluster argument array if the argument exists
     * </p>
     *
     * @param treeNodeLabel
     *            The root node for the cluster to be checked
     *
     * @return {@code true} if the cluster exists in the registry, otherwise, it returns false.
     */
    public boolean checkIfRegistered(Integer treeNodeLabel) { /* Is_Already_Reg_Clstr */
        int classDesignator = 0;
        int nextClusterArgIndex = 0;
        int count = 0;
        int argStringLengh = argListLength(clusterArgumentString);
        int currentClusterArgIndex = 1;
        Queue<Integer> tempQueue = new ArrayDeque<>();
        // if argStringLength is 0, it is variable or constant
        if (argStringLengh == 0) {
            if (clusterArgumentArray[currentClusterArgIndex] == null) {
                // there is nothing in the argument string yet, just return false
                return false;
            } else {
                // The condition checks if the label and the argument is the same as one to be registered
                if (clusterArray[clusterArgumentArray[currentClusterArgIndex].getClusterNumber()]
                        .getTreeNodeLabel() == treeNodeLabel
                        && clusterArray[clusterArgumentArray[currentClusterArgIndex].getClusterNumber()]
                                .getIndexToArgList() == currentClusterArgIndex) {
                    return true;
                }
                // The while loop checks clusters in cluster array with one argument by following a pointer next with
                // same argument filed until we find one or we get to the end.
                int currentClusterIndex = clusterArray[clusterArgumentArray[currentClusterArgIndex].getClusterNumber()]
                        .getNextWithSameArg();
                while (clusterArray[currentClusterIndex].getNextWithSameArg() != 0) {
                    if (clusterArray[currentClusterIndex].getTreeNodeLabel() == treeNodeLabel
                            && clusterArray[currentClusterIndex].getIndexToArgList() == currentClusterArgIndex) {
                        return true;
                    }
                    currentClusterIndex = clusterArray[currentClusterIndex].getNextWithSameArg();
                    if (currentClusterIndex == 0) {
                        // if it is 0 there is nothing more we can do, it is not there.
                        return false;
                    }
                }

                if (clusterArray[currentClusterIndex].getNextWithSameArg() == 0) {
                    if (clusterArray[currentClusterIndex].getTreeNodeLabel() == treeNodeLabel
                            && clusterArray[currentClusterIndex].getIndexToArgList() == currentClusterArgIndex) {
                        return true;
                    }
                }

            }
            return false;
        }
        // The while loop checks for clusters with arguments, and assumed they are already appended in the argument list
        while (clusterArgumentString.size() > 0) {
            classDesignator = removeFirstArgDesignator();
            tempQueue.add(classDesignator);
            classDesignator = getTheUltimateDominantClass(congruenceClassArray[classDesignator].getDominantCClass());

            nextClusterArgIndex = clusterArgumentArray[currentClusterArgIndex].getNextClusterArg();
            if (nextClusterArgIndex == 0) {
                // This if statement is entered if there is no next level after the argument being checked.
                // Just restore the argument list and return false.
                while (clusterArgumentString.size() > 0) {
                    tempQueue.add(clusterArgumentString.remove());
                }
                while (tempQueue.size() > 0) {
                    appendToClusterArgList(tempQueue.remove());
                }
                return false;
            } else {
                if (getTheUltimateDominantClass(
                        congruenceClassArray[clusterArgumentArray[nextClusterArgIndex].getCcNumber()]
                                .getDominantCClass()) == classDesignator) {
                    count++;
                    currentClusterArgIndex = nextClusterArgIndex;
                } else {
                    // it didn't have even the first class for the first argument, just return false and exit
                    if (clusterArgumentArray[nextClusterArgIndex].getAlternativeArg() == 0) {

                        while (clusterArgumentString.size() > 0) {
                            tempQueue.add(clusterArgumentString.remove());
                        }
                        // restore the cluster argument list
                        while (tempQueue.size() > 0) {
                            appendToClusterArgList(tempQueue.remove());
                        }
                        return false;
                    }
                    // check the alternative args if we can find it
                    while (clusterArgumentArray[nextClusterArgIndex].getAlternativeArg() != 0) {

                        if (getTheUltimateDominantClass(
                                clusterArgumentArray[clusterArgumentArray[nextClusterArgIndex].getAlternativeArg()]
                                        .getCcNumber()) == classDesignator) {
                            // argument found, increase the count and exit
                            count++;
                            currentClusterArgIndex = clusterArgumentArray[nextClusterArgIndex].getAlternativeArg();
                            break;
                        } else {
                            // argument not found, check the next one
                            nextClusterArgIndex = clusterArgumentArray[nextClusterArgIndex].getAlternativeArg();
                        }
                    }
                }

            }
            // check if we found all arguments
            if (argStringLengh == count) {
                // now check if the cluster exists by checking the label and the arg string
                if (clusterArray[clusterArgumentArray[currentClusterArgIndex].getClusterNumber()]
                        .getTreeNodeLabel() == treeNodeLabel
                        && clusterArray[clusterArgumentArray[currentClusterArgIndex].getClusterNumber()]
                                .getIndexToArgList() == currentClusterArgIndex) {
                    // restore the cluster argument list
                    while (tempQueue.size() > 0) {
                        appendToClusterArgList(tempQueue.remove());
                    }
                    return true;
                }
                int clusterNumber = clusterArray[clusterArgumentArray[currentClusterArgIndex].getClusterNumber()]
                        .getNextWithSameArg();
                while (clusterNumber != 0) {
                    if (clusterArray[clusterNumber].getTreeNodeLabel() == treeNodeLabel
                            && clusterArray[clusterNumber].getIndexToArgList() == currentClusterArgIndex) {
                        // restore the cluster argument list
                        while (tempQueue.size() > 0) {
                            appendToClusterArgList(tempQueue.remove());
                        }
                        return true;
                    }
                    clusterNumber = clusterArray[clusterNumber].getNextWithSameArg();
                }
            }
        }

        // restore the cluster argument list
        while (tempQueue.size() > 0) {
            appendToClusterArgList(tempQueue.remove());
        }
        return false;
    }

    /**
     * <p>
     * This operation is used to get the accessor for the class with the cluster we are about to register.
     * </p>
     *
     * @param treeNodeLabel
     *            A label for the cluster to be registered
     *
     * @return an accessor for the class containing the cluster to be created.
     */
    public int getAccessorFor(Integer treeNodeLabel) {/* Get_Accr_for */
        int currentIndexInArgumentString = 1;
        int currentIndexInClusterArray = clusterArgumentArray[currentIndexInArgumentString].getClusterNumber();
        if (clusterArgumentString.size() == 0) {
            // Assuming everything in the list of clusters with same arguments will point to
            // currentIndexInArgumentString, walking the list we should eventually find one with the tree node label
            // we are looking for
            while (clusterArray[currentIndexInClusterArray].getTreeNodeLabel() != treeNodeLabel) {
                currentIndexInClusterArray = clusterArray[currentIndexInClusterArray].getNextWithSameArg();
            }
            // return the class designator and it should be the dominant one
            return congruenceClassArray[clusterArray[currentIndexInClusterArray].getIndexToCongruenceClass()]
                    .getDominantCClass();

        } else {
            // The approach assumes the way the args were put in the structure is maintained all throughout
            int finalCountNeeded = clusterArgumentString.size();
            int currentClassDesignator = 0;
            int nextClusterArgument = 1;
            int countArgumentsFound = 0;

            // The operation is called when we know the cluster exists. Therefore, we should find everything in the
            // argument string no need to keep the count.
            while (clusterArgumentString.size() > 0) {
                currentClassDesignator = removeFirstArgDesignator();
                currentClassDesignator = congruenceClassArray[currentClassDesignator].getDominantCClass();
                nextClusterArgument = clusterArgumentArray[nextClusterArgument].getNextClusterArg();

                if (congruenceClassArray[clusterArgumentArray[nextClusterArgument].getCcNumber()]
                        .getDominantCClass() == currentClassDesignator) {
                    countArgumentsFound++;
                } else {
                    while (clusterArgumentArray[nextClusterArgument].getAlternativeArg() != 0) {
                        if (getTheUltimateDominantClass(
                                clusterArgumentArray[clusterArgumentArray[nextClusterArgument].getAlternativeArg()]
                                        .getCcNumber()) == currentClassDesignator) {
                            countArgumentsFound++;
                            nextClusterArgument = clusterArgumentArray[nextClusterArgument].getAlternativeArg();
                            break;
                        } else {
                            nextClusterArgument = clusterArgumentArray[nextClusterArgument].getAlternativeArg();
                        }
                    }
                }
            }

            if (finalCountNeeded == countArgumentsFound) {
                return congruenceClassArray[clusterArray[clusterArgumentArray[nextClusterArgument].getClusterNumber()]
                        .getIndexToCongruenceClass()].getDominantCClass();
            } else {
                // we should not get here if everything is set up correctly
                // but just for safety return 0
                return 0;
            }
        }

    }

    /**
     * <p>
     * This operation allows us to move from one congruence class accessor to another in the variety.
     * </p>
     *
     * @param treeNodeLabel
     *            The tree node label a class we are looking for should contain in its clusters
     * @param currentCCAccessor
     *            The current class accessor
     *
     * @return the next congruence class accessor after {@param currentCCAccessor}.
     */
    public int advanceCClassAccessor(Integer treeNodeLabel, int currentCCAccessor) { // Advance_CC_Accr_for
        int currentStandForTreeNodeLabel = 0;
        int nextStandInNextClassAccessor = 0;

        currentStandForTreeNodeLabel = varietyArray[treeNodeLabel].getFirstStand();

        int congruenceClassForCluster = clusterArray[standArray[currentStandForTreeNodeLabel].getFirstStandCluster()]
                .getIndexToCongruenceClass();
        int dominantCongruenceClassForCluster = congruenceClassArray[congruenceClassForCluster].getDominantCClass();

        while (currentStandForTreeNodeLabel != 0) {
            if (congruenceClassForCluster == currentCCAccessor
                    || dominantCongruenceClassForCluster == currentCCAccessor) {
                nextStandInNextClassAccessor = standArray[currentStandForTreeNodeLabel].getNextVrtyStand();
                congruenceClassForCluster = clusterArray[standArray[nextStandInNextClassAccessor]
                        .getFirstStandCluster()].getIndexToCongruenceClass();
                dominantCongruenceClassForCluster = congruenceClassArray[congruenceClassForCluster].getDominantCClass();
                return dominantCongruenceClassForCluster;
            } else {
                currentStandForTreeNodeLabel = standArray[currentStandForTreeNodeLabel].getNextVrtyStand();
                if (currentStandForTreeNodeLabel != 0) {
                    congruenceClassForCluster = clusterArray[standArray[currentStandForTreeNodeLabel]
                            .getFirstStandCluster()].getIndexToCongruenceClass();
                    dominantCongruenceClassForCluster = congruenceClassArray[congruenceClassForCluster]
                            .getDominantCClass();
                }
            }
        }
        // this is just defensive, we will never get here as the operation is always called when we have next accessor
        return 0;
    }

    /**
     * <p>
     * This operation checks if the classes are exhausted in the veriety.
     * </p>
     *
     * @param treeNodeLabel
     *            The designator for the variety we are checking.
     * @param currentCCAccessor
     *            Current congruence class accessor.
     *
     * @return {@code true} if we have exhausted all the classes in the variety designated by {@param treeNodeLabel}.
     */
    public boolean isVarietyMaximal(Integer treeNodeLabel, int currentCCAccessor) { /* Is_Vrty_Maximal_for */
        int currentStandForTreeNodeLabel = 0;
        int nextStandInNextClassAccessor = 0;

        currentStandForTreeNodeLabel = varietyArray[treeNodeLabel].getFirstStand();

        int congruenceClassForCluster = clusterArray[standArray[currentStandForTreeNodeLabel].getFirstStandCluster()]
                .getIndexToCongruenceClass();
        int dominantCongruenceClassForCluster = congruenceClassArray[congruenceClassForCluster].getDominantCClass();

        while (currentStandForTreeNodeLabel != 0) {
            if (congruenceClassForCluster == currentCCAccessor
                    || dominantCongruenceClassForCluster == currentCCAccessor) {
                nextStandInNextClassAccessor = standArray[currentStandForTreeNodeLabel].getNextVrtyStand();
                if (nextStandInNextClassAccessor == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                currentStandForTreeNodeLabel = standArray[currentStandForTreeNodeLabel].getNextVrtyStand();
                if (currentStandForTreeNodeLabel != 0) {
                    congruenceClassForCluster = clusterArray[standArray[currentStandForTreeNodeLabel]
                            .getFirstStandCluster()].getIndexToCongruenceClass();
                    dominantCongruenceClassForCluster = congruenceClassArray[congruenceClassForCluster]
                            .getDominantCClass();
                }
            }
        }
        // this is just defensive, we hope it will be existing and decision will be made earlier
        return true;
    }

    /**
     * <p>
     * This operation checks if the supplied tree node label is among the root node labels in the registry.
     * </p>
     *
     * @param treeNodeLabel
     *            Tree node lable to be checked.
     *
     * @return {@code true} if the {@param treeNodeLabel} is within the nodes in the registry, returns {@code false}
     *         otherwise.
     */
    public boolean isRegistryLabel(Integer treeNodeLabel) { /* Is_Rgry_Lab */
        if (varietyArray[treeNodeLabel] != null) {
            // each node at some point is considered a root node label
            return true;
        }
        return false;
    }

    /**
     * <p>
     * This operation return the remaining capacity for the congruence class designators in the registry.
     * </p>
     *
     * @return the difference between the maximum class capacity and the current used count of class designators.
     */
    public int remainingCCDesignatorCap() {
        return ccDesignatorCapacity - topCongruenceClassDesignator;
    }

    /**
     * <p>
     * This operation return the remaining capacity for the cluster designators in the registry.
     * </p>
     *
     * @return the difference between the maximum cluster capacity and the current used count of cluster designators.
     */
    public int remainingCClusterDesignatorCap() {
        return cClusterDesignatorCapacity - topCongruenceClusterDesignator;
    }

    /**
     * <p>
     * This operation return the remaining label capacity for the labels in the registry.
     * </p>
     *
     * @return the difference between the maximum label capacity and the current used count on the label.
     */
    public int remainingLabelCap() {
        return rootLabelCapacity - topLabelCapacity;
    }

    /**
     * <p>
     * This operation allows us to move from one cluster to another inside a stand.
     * </p>
     *
     * @param treeNodeLabel
     *            The root node label for the stand.
     * @param currentClusterAccessor
     *            The current cluster designator.
     *
     * @return the next cluster accessor after {@param currentClusterAccessor}.
     */
    public int advanceClusterAccessor(Integer treeNodeLabel, int currentClusterAccessor) {
        int dominantCluster = currentClusterAccessor;
        while (clusterArray[dominantCluster].getDominantCluster() != currentClusterAccessor) {
            dominantCluster = clusterArray[dominantCluster].getDominantCluster();
        }
        return clusterArray[dominantCluster].getNextStandCluster();
    }

    /**
     * <p>
     * This operation checks if the cluster designators are exhausted inside the stand.
     * </p>
     *
     * @param treeNodeLabel
     *            A tree node label that designates the stand being checked
     * @param currentClusterAccessor
     *            A current cluster accessor in the stand
     *
     * @return {@code true} if we have exhausted all the clusters in the stand, otherwise it returns {@code false}
     */
    public boolean isStandMaximal(Integer treeNodeLabel, int currentClusterAccessor) {
        int dominantCluster = currentClusterAccessor;
        while (clusterArray[dominantCluster].getDominantCluster() != currentClusterAccessor) {
            dominantCluster = clusterArray[dominantCluster].getDominantCluster();
        }
        if (clusterArray[dominantCluster].getNextStandCluster() == 0) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * This operation checks to see if provided congruence class accessor is minimal.
     * </p>
     *
     * @param treeNodeLabel
     *            A tree node label designating the variety where class designated by {@param cClassAccessor} resides
     * @param cClassAccessor
     *            A congruence class accessor to check if it is minimal
     *
     * @return {@code true} iff the {@param cClassAccessor} is minimal, otherwise, return {@code false}
     */
    public boolean isMinimalVCCDesignator(Integer treeNodeLabel, int cClassAccessor) {
        if (congruenceClassArray[cClassAccessor].getDominantCClass() == cClassAccessor
                && varietyArray[treeNodeLabel] != null) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * This operation checks if the stand designator is minimal
     * </p>
     *
     * @param treeNodeLabel
     *            The tree node label for the clusters in the stand
     * @param cClassAccessor
     *            The accessor for the class the stand is in
     * @param clusterAccessor
     *            The accessor for the cluster in the stand
     *
     * @return {@code true} if the stand designator is minimal, otherwise, it returns {@code false}
     */
    public boolean isMinimalStandClusterDesignator(Integer treeNodeLabel, int cClassAccessor, int clusterAccessor) {
        if (clusterArray[clusterAccessor].getDominantCluster() == clusterAccessor
                && isMinimalVCCDesignator(treeNodeLabel, cClassAccessor)) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * This operation removes the first argument class designator from the registry's argument list
     * </p>
     *
     * @return the accessor for the first argument in the registry's argument list
     */
    public int removeFirstArgDesignator() {
        return clusterArgumentString.remove();
    }

    /**
     * <p>
     * This operation gets the length of an argument string in the registry
     * </p>
     *
     * @param clusterArgumentString
     *            The argument string
     *
     * @return the length of an argument string in the registry
     */
    public int argListLength(Queue<Integer> clusterArgumentString) {
        return clusterArgumentString.size();
    }

    /**
     * <p>
     * The operation appends the argument passed from the client to the argument list in the registry
     * </p>
     *
     * @param cClassDesignator
     *            The congruence class accessor for the argument.
     */
    public void appendToClusterArgList(int cClassDesignator) {
        clusterArgumentString.add(cClassDesignator);
    }

    /**
     * <p>
     * This operation merges two congruence class designated by the accessor passed in the parameters.
     * </p>
     *
     * @param firstCCAccessor
     *            The first congruence class accessor
     * @param secondCCAccessor
     *            The second congruence class accessor
     */
    public void makeCongruent(int firstCCAccessor, int secondCCAccessor) {
        // The approach takes assumes firstCCAccessor is smaller than secondCCAccessor always.
        // The example used is for first accessor being 3 and second accessor being 7
        // The variables are named with 3 and 7 to simplify tracing
        int indexToArgString_3_L1 = 0;
        int indexToArgString_7_L2 = 0;
        int indexToArgString_3_L2 = 0;
        int indexTOArgString_7_L1 = 0;
        int level_2 = 2;
        int level_1 = 1;
        BitSet bitSet = new BitSet();

        // make sure they are dominant classes
        firstCCAccessor = getTheUltimateDominantClass(congruenceClassArray[firstCCAccessor].getDominantCClass());
        secondCCAccessor = getTheUltimateDominantClass(congruenceClassArray[secondCCAccessor].getDominantCClass());

        // addFirst and removeFirst uses FIFO
        classMergeList.add(firstCCAccessor);
        classMergeList.add(secondCCAccessor);

        // if no classes are added during merging process it will only execute once. Otherwise, the while loop continue
        // until the class merge list is exhausted
        while (classMergeList.size() != 0) {
            firstCCAccessor = classMergeList.remove();
            secondCCAccessor = classMergeList.remove();
            bitSet.clear();
            bitSet = mergeAttribute(firstCCAccessor, secondCCAccessor);
            // in either case we will have 4 values to help in narrowing the search
            if (firstCCAccessor < secondCCAccessor) {
                indexToArgString_3_L1 = congruenceClassArray[firstCCAccessor].getIndexInClusterArgArrayFromASOP(1);
                indexToArgString_7_L2 = congruenceClassArray[secondCCAccessor].getIndexInClusterArgArrayFromASOP(2);
                indexToArgString_3_L2 = congruenceClassArray[firstCCAccessor].getIndexInClusterArgArrayFromASOP(2);
                indexTOArgString_7_L1 = congruenceClassArray[secondCCAccessor].getIndexInClusterArgArrayFromASOP(1);
            } else {
                indexToArgString_3_L1 = congruenceClassArray[secondCCAccessor].getIndexInClusterArgArrayFromASOP(1);
                indexToArgString_7_L2 = congruenceClassArray[firstCCAccessor].getIndexInClusterArgArrayFromASOP(2);
                indexToArgString_3_L2 = congruenceClassArray[secondCCAccessor].getIndexInClusterArgArrayFromASOP(2);
                indexTOArgString_7_L1 = congruenceClassArray[firstCCAccessor].getIndexInClusterArgArrayFromASOP(1);
            }
            // check if it is proved
            if (isSequentVCProvedByAttributes(bitSet)) {
                isProved = true;
            } else if (succedentReflexiveOperatorTest) {
                if (indexToArgString_7_L2 != 0 && indexToArgString_3_L1 != 0) {
                    if (firstCCAccessor < secondCCAccessor) {
                        if (reflexivityBingoTest(firstCCAccessor, secondCCAccessor, level_2)) {
                            isProved = true;
                        }
                    } else {
                        if (reflexivityBingoTest(secondCCAccessor, firstCCAccessor, level_2)) {
                            isProved = true;
                        }
                    }
                } else if (indexTOArgString_7_L1 != 0 && indexToArgString_3_L2 != 0) {
                    if (firstCCAccessor < secondCCAccessor) {
                        if (reflexivityBingoTest(firstCCAccessor, secondCCAccessor, level_1)) {
                            isProved = true;
                        }
                    } else {
                        if (reflexivityBingoTest(secondCCAccessor, firstCCAccessor, level_1)) {
                            isProved = true;
                        }
                    }

                }
            }
            // continue with the merge process
            if (!isProved) {
                mergeClasses(firstCCAccessor, secondCCAccessor);
            }
        }
    }

    /**
     * <p>
     * This operation checks if the two provided congruence classes are already congruent
     * </p>
     *
     * @param firstAccessor
     *            The first congruence class accessor
     * @param secondAccessor
     *            The second congruence class accessor
     *
     * @return {@code true} iff the two classes are congruent. Otherwise, it returns {@code false}
     */
    public boolean areCongruent(int firstAccessor, int secondAccessor) {
        return areClassesCongruent(firstAccessor, secondAccessor);
    }

    /**
     * <p>
     * This operation checks to see if the target sequent VC is proved
     * </p>
     *
     * @return {@code true} iff the sequent VC is proved. Otherwise, it returns {@code false}
     */
    public boolean checkIfProved() {
        return isProved;
    }

    /**
     * <p>
     * This operation updates the congruence class attributes
     * </p>
     *
     * @param classAccessor
     *            The accessor for the class to be updated
     * @param attributeIn
     *            The attribute to be attached to the class
     */
    public void updateClassAttributes(int classAccessor, BitSet attributeIn) {
        BitSet attributeAt = new BitSet();
        attributeAt = congruenceClassArray[classAccessor].getAttribute();
        attributeAt.or(attributeIn);
        congruenceClassArray[classAccessor].setClassAttribute(attributeAt);
        if (isSequentVCProvedByAttributes(attributeAt)) {
            isProved = true;
        }
    }

    /**
     * <p>
     * This operation add the reflexive operators found in the succedent of the target sequent VC to the registry's
     * reflexive set of operators
     * </p>
     *
     * @param treeNodeLabel
     *            The label for the reflexive operator
     */
    public void addOperatorToSuccedentReflexiveOperatorSet(Integer treeNodeLabel) {
        succedentReflexiveOperatorsSet.add(treeNodeLabel);
    }

    /*************************************
     * PRIVATE METHODS
     ****************************************************************************/

    /**
     * <p>
     * The operation that gets the ultimate dominant class designator for a given class
     * </p>
     *
     * @param cClassDesingator
     *            is a current class designator.
     *
     * @return int value for the ultimate class designator for the provided designator.
     */
    private int getTheUltimateDominantClass(int cClassDesingator) {
        while (congruenceClassArray[cClassDesingator].getDominantCClass() != cClassDesingator) {
            cClassDesingator = congruenceClassArray[cClassDesingator].getDominantCClass();
        }
        return cClassDesingator;
    }

    /**
     * <p>
     * This operation checks to see of two classes are congruent
     * </p>
     *
     * @param firstAccessor
     *            is an accessor for the first class provided by the client.
     * @param secondAccessor
     *            is an accessor for the second class provided by the client
     *
     * @return {@code true} iff the two provided classes are congruent. Otherwise, return {@code false}.
     */
    private boolean areClassesCongruent(int firstAccessor, int secondAccessor) {
        if (firstAccessor == secondAccessor) {
            return true;
        } else {// one is the dominant of each other, or both have the same dominant class
            int dominantFirstClass = getTheUltimateDominantClass(firstAccessor);

            int dominantSecondClass = getTheUltimateDominantClass(secondAccessor);

            if (dominantFirstClass == dominantSecondClass) {
                return true;
            }

        }
        return false;
    }

    /**
     * <p>
     * This is a sub-procedure for the reflexivity Bingo test {@code reflexivityBingoTest}
     * </p>
     *
     * @param indexToArgInSecondLevel
     *            index to the argument string representing first occurrence of the class in second level.
     * @param classAccessorInFirstLevel
     *            accessor for the class in the first level that the second class should match.
     *
     * @return {@code true} iff the previous class from {@param indexToArgInSecondLevel} is
     *         {@param classAccessorInFirstLevel}, and the root node of the cluster is in the set of reflexive
     *         operators. Otherwise return {@code false}.
     */
    private boolean subReflexiveBingoTest(int indexToArgInSecondLevel, int classAccessorInFirstLevel) {

        int currentCluster;
        BitSet multiplier = new BitSet();
        BitSet classAttributes = new BitSet();
        // this check is from the second level looking the second argument upwards
        // moving from one 7 to another 7 in the same level to see if we hit the right 7 that has been changed to a 3
        // and previous is 3
        while (clusterArgumentArray[indexToArgInSecondLevel]
                .getNxtIndexWithSameCCNumberInLevel() != indexToArgInSecondLevel) {

            if (clusterArgumentArray[clusterArgumentArray[indexToArgInSecondLevel].getPrevClusterArg()]
                    .getCcNumber() == classAccessorInFirstLevel) {
                currentCluster = clusterArgumentArray[indexToArgInSecondLevel].getClusterNumber(); // first class
                while (clusterArray[currentCluster].getNextWithSameArg() != currentCluster) {
                    // check the cluster if it has the operator we are looking for
                    classAttributes = congruenceClassArray[clusterArray[currentCluster].getIndexToCongruenceClass()]
                            .getAttribute();
                    multiplier.set(2);
                    classAttributes.and(multiplier);
                    if (succedentReflexiveOperatorsSet.contains(clusterArray[currentCluster].getTreeNodeLabel())
                            && classAttributes.cardinality() == 1) {
                        // we may need the classes to be dominant
                        return true;
                    }
                    // get to the next cluster that uses the same argument
                    currentCluster = clusterArray[currentCluster].getNextWithSameArg();
                }
            }
            // get the next 7 in the lavel 2, it might have been another seven preceded with 3
            indexToArgInSecondLevel = clusterArgumentArray[indexToArgInSecondLevel]
                    .getNxtIndexWithSameCCNumberInLevel();
            // currentCluster = clusterArgumentArray[indexToArgString_7].getClusterNumber();
        }
        return false;
    }

    /**
     * <p>
     * This is one way to check if the sequent VC is proved. It is called when there is an existence of the reflexive
     * operator in the succedent.
     * </p>
     *
     * @param firstClassAccessor
     *            is accessor for the first class, {@param firstClassAccessor} is considered smaller than
     *            {@param secondClassAccessor}.
     * @param secondClassAccessor
     *            is accessor for the second class, {@param secondClassAccessor} is considered greater than
     *            {@param firstClassAccessor}.
     * @param level
     *            is the level in the argument string array for the second class.
     *
     * @return {@code true} iff the sequent VC is proved, otherwise it returns {@code false}
     */
    private boolean reflexivityBingoTest(int firstClassAccessor, int secondClassAccessor, int level) {

        if (level == 2) {
            int indexToArgString_7 = congruenceClassArray[secondClassAccessor].getIndexInClusterArgArrayFromASOP(2);
            return subReflexiveBingoTest(indexToArgString_7, firstClassAccessor);

        } else if (level == 1) {

            int indexToArgString_3 = congruenceClassArray[firstClassAccessor].getIndexInClusterArgArrayFromASOP(2);
            return subReflexiveBingoTest(indexToArgString_3, secondClassAccessor);

        }
        return false;
    }

    /**
     * <p>
     * The operation merges two attributes for two merged classes into one. The attribute on the class with larger
     * accessor is merged into the attribute on the class with smaller accessor
     * </p>
     *
     * @param firstAccessor
     *            is accessor for the first class, {@param firstAccessor} is considered smaller than
     *            {@param secondAccessor}.
     * @param secondAccessor
     *            is accessor for the second class, {@param secondAccessor} is considered greater than
     *            {@param firstAccessor}.
     *
     * @return BitSet resulting from merging the two individual bit sets for the first class and second class.
     */
    private BitSet mergeAttribute(int firstAccessor, int secondAccessor) {
        // just as example, first accessor = 3, second accessor = 7
        BitSet bitSet_03 = congruenceClassArray[firstAccessor].getAttribute();
        BitSet bitSet_07 = congruenceClassArray[secondAccessor].getAttribute();
        bitSet_03.or(bitSet_07); // OR the two bit sets and the result will be in bitSet_03.
        return bitSet_03;
    }

    /**
     * <p>
     * This operation checks whether the sequent VC is proved or not using the BitSet on the classes
     * </p>
     *
     * @param bitSetIn
     *            is a bitset provided to be checked if it proves the sequent VC.
     *
     * @return {@code true} iff the {@param bitSeIn} has all the bits set, which proves the sequent VC. Otherwise, the
     *         operation returns {@code false}.
     */
    private boolean isSequentVCProvedByAttributes(BitSet bitSetIn) {
        // the number of 1's is 3 when all three bits are set.
        if (bitSetIn.cardinality() == 3) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * The operation adds a new stand to the variety list designated by {@param treeNodeLabel}. It starts a new list
     * when the list is not existing or join to the existing list. The stands are kept in ascending order according to
     * their indices in stand array.
     * </p>
     *
     * @param treeNodeLabel
     *            a designator for the variety
     * @param newStand
     *            a new stand to be included into the variety list for the {@param treeNodeLabel}
     * @param standTag
     *            a stand tag for the {@param newStand}
     */
    private void addInVarietyListArray(Integer treeNodeLabel, int newStand, int standTag) {
        int currentStandInVarietyList = 0;
        if (varietyArray[treeNodeLabel] == null) {
            VarietyList varietyList = new VarietyList(newStand, standTag);
            varietyArray[treeNodeLabel] = varietyList;
        } else {
            currentStandInVarietyList = varietyArray[treeNodeLabel].getFirstStand();
            if (newStand < currentStandInVarietyList) {
                // put it at the front
                // set the next stand on the new stand
                standArray[newStand].setNextVrtyStand(currentStandInVarietyList);
                standArray[currentStandInVarietyList].setPrvVrtyStand(newStand);
                // update the fist stand in the variety list in array
                varietyArray[treeNodeLabel].setFirstStand(newStand);
            } else {
                // put it at the end of the list or somewhere suitable according to the order
                while (newStand > currentStandInVarietyList
                        && standArray[currentStandInVarietyList].getNextVrtyStand() != 0) {
                    currentStandInVarietyList = standArray[currentStandInVarietyList].getNextVrtyStand();
                }
                standArray[currentStandInVarietyList].setNextVrtyStand(newStand);
                standArray[newStand].setPrvVrtyStand(currentStandInVarietyList);
            }

        }
    }

    /**
     * <p>
     * The operation merges the two classes in the registry accessed by {@param firstCCAccessor} and
     * {@param secondCCAccessor} the class with large accessor is merged to the one with smaller accessor. The new class
     * containing the content of the two classes is designated with the smaller designator, and the dominant class for
     * the larger designator is updated to the smaller designator
     * </p>
     *
     * @param firstCCAccessor
     *            is an accessor for the first class provided by the client.
     * @param secondCCAccessor
     *            is accessor for the second class provided by the client.
     */
    private void mergeClasses(int firstCCAccessor, int secondCCAccessor) {
        // update dominant class and perform path compression upward the chain
        if (firstCCAccessor < secondCCAccessor) {
            // change the dominant class for the larger class designator between the two merge classes
            updateDominantClass(firstCCAccessor, secondCCAccessor);
            // update the stand either by joining their clusters or moving the stand to the smaller class
            updateStandInSmallerClass(firstCCAccessor, secondCCAccessor);
        } else {
            // change the dominant class for the larger class designator
            updateDominantClass(secondCCAccessor, firstCCAccessor);
            // update the stand either by joining their clusters or moving the stand to the smaller class
            updateStandInSmallerClass(secondCCAccessor, firstCCAccessor);
        }
        int level = 0;
        // take the second accessor and find where we should start looking in the arg string, get the level and index in
        // FASOP
        if (firstCCAccessor < secondCCAccessor) {
            level = congruenceClassArray[secondCCAccessor].getLastArgStringPosition();
        } else {
            level = congruenceClassArray[firstCCAccessor].getLastArgStringPosition();
        }
        while (level != 0) {
            // the order matter for updateClusterArgumentAfterMerging operation
            if (firstCCAccessor < secondCCAccessor) {
                if (!isProved) {
                    // update only when it is not proved
                    updateClusterArgumentAfterMerging(firstCCAccessor, secondCCAccessor, level);
                } else {
                    break;
                }
            } else {
                if (!isProved) {
                    updateClusterArgumentAfterMerging(secondCCAccessor, firstCCAccessor, level);
                } else {
                    break;
                }
            }
            level--;
        }

    }

    /**
     * <p>
     * The operation updates the dominant class designator for the {@param secondCCAccessor} to point to the
     * {@param firstCCAccessor} ultimate designator. The attributes are also updated, where {@param firstCCAccessor}
     * will now have an attribute resulting from merging the two individual attributes
     * </p>
     *
     * @param firstCCAccessor
     *            is accessor for the first class, {@param firstCCAccessor} is considered smaller than
     *            {@param secondCCAccessor}.
     * @param secondCCAccessor
     *            is accessor for the second class, {@param secondCCAccessor} is considered greater than
     *            {@param firstCCAccessor}.
     */
    private void updateDominantClass(int firstCCAccessor, int secondCCAccessor) {
        if (congruenceClassArray[firstCCAccessor].getDominantCClass() == firstCCAccessor) {
            // it is its own dominant class so no compression
            congruenceClassArray[secondCCAccessor].setDominantCClass(firstCCAccessor);
            // update the attribute at class level, which depends on the smaller firstCCAccessor
            BitSet bitSet_03 = mergeAttribute(firstCCAccessor, secondCCAccessor);
            // update the bit set for class 3, as now it contains new stuff from class 7
            congruenceClassArray[firstCCAccessor].setClassAttribute(bitSet_03);

        } else {
            // it is not, compression needed
            int currentDominantClass = firstCCAccessor;
            // go up the chain as far as possible, I did not consider going down the chain
            currentDominantClass = getTheUltimateDominantClass(currentDominantClass);
            congruenceClassArray[secondCCAccessor].setDominantCClass(currentDominantClass);

            // update the attribute too at class level
            BitSet bitSet_03 = mergeAttribute(currentDominantClass, secondCCAccessor);
            // update the bit set for currentDominantClass, it is now containing new stuff from class 7
            congruenceClassArray[currentDominantClass].setClassAttribute(bitSet_03);
        }
    }

    /**
     * <p>
     * The operation removes the class in the variety list after the stand that stood in that class for a root label is
     * merged to another class
     * </p>
     *
     * @param treeNodeLabel
     *            the tree node label designating the stand
     * @param standDesignatorToRemove
     *            index to the stand that is to be removed
     */
    private void removeClassFromVarietyList(Integer treeNodeLabel, int standDesignatorToRemove) {
        int currentStandInList = varietyArray[treeNodeLabel].getFirstStand();
        int previousStandInList = 0;
        int nextStandInList = 0;
        // it is the first one in the variety array list, now it has to be removed
        if (currentStandInList == standDesignatorToRemove) {
            // get rid of the first one and make the second one in the variety list the first one
            varietyArray[treeNodeLabel]
                    .setFirstStand(standArray[varietyArray[treeNodeLabel].getFirstStand()].getNextVrtyStand());
            // make the previous pointer 0
            standArray[standArray[varietyArray[treeNodeLabel].getFirstStand()].getNextVrtyStand()].setPrvVrtyStand(0);
        } else {
            // it is not the first one in the variety array list, just remove it
            // this assumes stand designator to remove must be in the variety list. If that is the case just
            // remove it by re-allocating the pointers
            previousStandInList = standArray[standDesignatorToRemove].getPrvVrtyStand();
            nextStandInList = standArray[standDesignatorToRemove].getNextVrtyStand();

            standArray[previousStandInList].setNextVrtyStand(nextStandInList);
            if (nextStandInList != 0) {
                // note we have P0 as the initial stand in
                standArray[nextStandInList].setPrvVrtyStand(previousStandInList);
            }
        }
    }

    /**
     * <p>
     * The operation update the stands in the smaller class after the two classes are merged
     * </p>
     *
     * @param firstCCAccessor
     *            the smaller class designator
     * @param secondCCAccessor
     *            the larger class designator
     */
    private void updateStandInSmallerClass(int firstCCAccessor, int secondCCAccessor) {
        int currentFirstAccessor = firstCCAccessor;
        int currentSecondAccessor = secondCCAccessor;

        int standDesignator_1 = congruenceClassArray[currentFirstAccessor].getFirstStand();
        int standDesignator_2 = congruenceClassArray[currentSecondAccessor].getFirstStand();

        Integer treeNodeLabel_2, treeNodeLabel_1;
        // tree node label 2 is the one for the stand being moved
        int nextStandDesignator_1, nextStandDesignator_2;
        while (standDesignator_2 != 0) {

            treeNodeLabel_2 = standArray[standDesignator_2].getTreeNodeLabel();
            // get the tree node label for this stand
            treeNodeLabel_1 = standArray[standDesignator_1].getTreeNodeLabel();

            // things may be changed and re-arranged, keep this record and use it later
            nextStandDesignator_1 = standArray[standDesignator_1].getNextCCStand();
            nextStandDesignator_2 = standArray[standDesignator_2].getNextCCStand();

            // compare the tree node labels and do what is necessary
            if (treeNodeLabel_2.equals(treeNodeLabel_1)) {
                // the tree nodes for the stands are the same, join the clusters
                joinClustersOnSameRootNodeStand(standDesignator_1, standDesignator_2);
                // update the variety list in the variety array
                removeClassFromVarietyList(treeNodeLabel_2, standDesignator_2);
                standDesignator_2 = nextStandDesignator_2;
                standDesignator_1 = nextStandDesignator_1;

            } else if (treeNodeLabel_1 < treeNodeLabel_2) {
                // this means we still need to check if the next in list one is still smaller than treeNodeLabel_2
                // this is the case we can still find equality
                joinStandFrom2ndListToFirstList(standDesignator_1, standDesignator_2);
                // start from the stand added to the first list
                standDesignator_1 = standDesignator_2;
                // start from the next one on the second list
                standDesignator_2 = nextStandDesignator_2;

            } else {
                // this case, treeNodeLabel_1 > treeNodeLabel_2 so it is definitely not on the list,
                // just merge the stands.
                // This condition will only happen once, as the rest will be greater than what we just added
                // from the second list.
                joinStandFrom2ndListToFirstList(standDesignator_1, standDesignator_2);
                // start from the stand added to the first list
                standDesignator_1 = standDesignator_2;

                // update the 1st stand in the first class, this is assuming the idea that this part will only be
                // executed once.
                congruenceClassArray[firstCCAccessor].setFirstStand(standDesignator_2);
                // start from the next one on the second list
                standDesignator_2 = nextStandDesignator_2;
            }

        }
    }

    /**
     * <p>
     * The operation move a stand from the larger class to the smaller class where it will be merged to the new list
     * according to their root node label. In this case, the operation covers the merger when the root nodes are either
     * greater than or less than than each other
     * </p>
     *
     * @param standDesignator_1
     *            index to the first stand in the first class
     * @param standDesignator_2
     *            index to the second stand in the second class
     */
    private void joinStandFrom2ndListToFirstList(int standDesignator_1, int standDesignator_2) {
        Integer treeNodeLabel_1 = standArray[standDesignator_1].getTreeNodeLabel();
        Integer treeNodeLabel_2 = standArray[standDesignator_2].getTreeNodeLabel();

        if (treeNodeLabel_1 < treeNodeLabel_2) {
            standJoinCase_01(standDesignator_1, standDesignator_2);
        } else {
            standJoinCase_02(standDesignator_1, standDesignator_2);
        }
    }

    /**
     * <p>
     * The operation join clusters in stands that have the same root node label by moving the clusters from the larger
     * stand to the smaller stand
     * </p>
     *
     * @param standDesignator_1
     *            index to the smaller stand
     * @param standDesignator_2
     *            index to the larger stand
     */
    private void joinClustersOnSameRootNodeStand(int standDesignator_1, int standDesignator_2) {
        int currentClusterDesignator_1 = standArray[standDesignator_1].getFirstStandCluster();
        int currentClusterDesignator_2 = standArray[standDesignator_2].getFirstStandCluster();

        int reserveCurrentStandCluster_1 = currentClusterDesignator_1;
        int reserveCurrentStandCluster_2 = currentClusterDesignator_2;
        int tempCurrentClusterDesignator_2 = 0;

        // update all clusters in the stand to belong to the new class by changing their class field
        // we are using the dominant class, and we do this before the merging of clusters
        int dominantClassDesignator = getTheUltimateDominantClass(
                congruenceClassArray[clusterArray[reserveCurrentStandCluster_1].getIndexToCongruenceClass()]
                        .getDominantCClass());
        // This condition should work if the cluster at index 0 has all 0's. This is done in line 66
        while (clusterArray[reserveCurrentStandCluster_2].getNextStandCluster() != reserveCurrentStandCluster_2) {
            clusterArray[reserveCurrentStandCluster_2].setIndexToCongruenceClass(dominantClassDesignator);
            reserveCurrentStandCluster_2 = clusterArray[reserveCurrentStandCluster_2].getNextStandCluster();
        }

        // This condition will work if the cluster array at index 0 is initialized with zeroes.
        while (clusterArray[currentClusterDesignator_2].getNextStandCluster() != currentClusterDesignator_2) {
            // get all clusters in the larger stand
            if (currentClusterDesignator_1 < currentClusterDesignator_2) {
                tempCurrentClusterDesignator_2 = currentClusterDesignator_2;

                // get what next on list_2
                currentClusterDesignator_2 = clusterJoinCase_01(currentClusterDesignator_1, currentClusterDesignator_2);

                // start from where we added the cluster in list_1
                currentClusterDesignator_1 = tempCurrentClusterDesignator_2;
            } else {
                tempCurrentClusterDesignator_2 = currentClusterDesignator_2;
                // get what is next from list_2
                currentClusterDesignator_2 = clusterJoinCase_02(currentClusterDesignator_1, currentClusterDesignator_2);
                // start from where we added the cluster in list_1
                currentClusterDesignator_1 = tempCurrentClusterDesignator_2;

                // under the assumption this will only be executed once and that will now be our fist cluster in the
                // list_1
                standArray[standDesignator_1].setFirstStandCluster(currentClusterDesignator_1);
            }
        }
    }

    /**
     * <p>
     * This operation mergers two clusters one for the smaller stand and one from the larger stand, in this case, the
     * cluster on the smaller stand is less than the cluster on the larger stand
     * </p>
     *
     * @param currentClusterDesignator_1
     *            is the class designator for the first cluster in smaller stand
     * @param currentClusterDesignator_2
     *            is the class designator for the second cluster in a larger stand
     *
     * @return a cluster designator for the cluster just moved to the smaller stand
     */
    private int clusterJoinCase_01(int currentClusterDesignator_1, int currentClusterDesignator_2) {
        int next_1, next_2, prev_2;

        while (currentClusterDesignator_1 < currentClusterDesignator_2
                && clusterArray[currentClusterDesignator_1].getNextStandCluster() != 0
                && clusterArray[currentClusterDesignator_1].getNextStandCluster() < currentClusterDesignator_2) {
            currentClusterDesignator_1 = clusterArray[currentClusterDesignator_1].getNextStandCluster();
        }

        // keep records of all pointers
        next_1 = clusterArray[currentClusterDesignator_1].getNextStandCluster();
        next_2 = clusterArray[currentClusterDesignator_2].getNextStandCluster();
        prev_2 = clusterArray[currentClusterDesignator_2].getPreviousStandCluster();

        // completely restore everything, every prev and next to something that can be worked on from scratch, I guess
        // it will solve the problem in a more general way
        clusterArray[currentClusterDesignator_1].setNextStandCluster(currentClusterDesignator_2);

        clusterArray[currentClusterDesignator_2].setPreviousStandCluster(currentClusterDesignator_1);
        clusterArray[currentClusterDesignator_2].setNextStandCluster(next_1);
        clusterArray[next_1].setPreviousStandCluster(currentClusterDesignator_2);

        clusterArray[next_2].setPreviousStandCluster(prev_2);

        // update the class designator field in the cluster to pointer to the new class they belong
        clusterArray[currentClusterDesignator_2]
                .setIndexToCongruenceClass(clusterArray[currentClusterDesignator_1].getIndexToCongruenceClass());
        // return where to start on list_2
        return next_2;
    }

    /**
     * <p>
     * The operation join two stands where the tree node designating the first one is less than the tree node
     * designating the second one
     * </p>
     *
     * @param standDesignator_1
     *            index to stand array for the first stand
     * @param standDesignator_2
     *            index to stand orray for the second stand
     */
    private void standJoinCase_01(int standDesignator_1, int standDesignator_2) {
        int next_1, currentStandCluster_2, currentStandCluster_1, dominantClassDesignator;

        // sort them using tree node labels
        Integer treeNodeLabel_1, treeNodeLabel_2;
        treeNodeLabel_1 = standArray[standDesignator_1].getTreeNodeLabel();
        treeNodeLabel_2 = standArray[standDesignator_2].getTreeNodeLabel();

        while (treeNodeLabel_1 < treeNodeLabel_2 && standArray[standDesignator_1].getNextCCStand() != 0
                && standArray[standArray[standDesignator_1].getNextCCStand()].getTreeNodeLabel() < treeNodeLabel_2) {
            standDesignator_1 = standArray[standDesignator_1].getNextCCStand();
            treeNodeLabel_1 = standArray[standDesignator_1].getTreeNodeLabel();
        }
        // the tree nodes for the stands are the same, join the clusters
        if (treeNodeLabel_2.equals(treeNodeLabel_1)) {
            joinClustersOnSameRootNodeStand(standDesignator_1, standDesignator_2);
        } else {
            // keep records of all pointers
            next_1 = standArray[standDesignator_1].getNextCCStand();

            standArray[standDesignator_1].setNextCCStand(standDesignator_2);
            standArray[standDesignator_2].setNextCCStand(next_1);

            // update all clusters in the stand to belong to the new class by changing their class field
            currentStandCluster_2 = standArray[standDesignator_2].getFirstStandCluster();
            currentStandCluster_1 = standArray[standDesignator_1].getFirstStandCluster();
            // we are using the dominant class
            dominantClassDesignator = getTheUltimateDominantClass(
                    congruenceClassArray[clusterArray[currentStandCluster_1].getIndexToCongruenceClass()]
                            .getDominantCClass());
            // This condition should work if the cluster at index 0 has all 0's. This is done in line 66
            while (clusterArray[currentStandCluster_2].getNextStandCluster() != currentStandCluster_2) {
                clusterArray[currentStandCluster_2].setIndexToCongruenceClass(dominantClassDesignator);
                currentStandCluster_2 = clusterArray[currentStandCluster_2].getNextStandCluster();
            }
        }
    }

    /**
     * <p>
     * This operation mergers two clusters one for the smaller stand and one from the larger stand, in this case, the
     * cluster on the smaller stand is greater than the cluster on the larger stand
     * </p>
     *
     * @param currentClusterDesignator_1
     *            is the class designator for the first cluster in smaller stand
     * @param currentClusterDesignator_2
     *            is the class designator for the second cluster in a larger stand
     *
     * @return a cluster designator for the cluster moved to the smaller stand
     */
    private int clusterJoinCase_02(int currentClusterDesignator_1, int currentClusterDesignator_2) {
        int next_2, prev_2;

        // this loop will possibly not be executed, think more if so delete it
        while (currentClusterDesignator_1 > currentClusterDesignator_2
                && clusterArray[currentClusterDesignator_1].getPreviousStandCluster() != 0) {
            currentClusterDesignator_1 = clusterArray[currentClusterDesignator_1].getPreviousStandCluster();
        }

        // keep records of pointers
        next_2 = clusterArray[currentClusterDesignator_2].getNextStandCluster();
        prev_2 = clusterArray[currentClusterDesignator_2].getPreviousStandCluster();

        clusterArray[currentClusterDesignator_1].setPreviousStandCluster(currentClusterDesignator_2);
        clusterArray[currentClusterDesignator_2].setNextStandCluster(currentClusterDesignator_1);

        clusterArray[next_2].setPreviousStandCluster(prev_2);

        // update the class designator field in the cluster to pointer to the new class they belong
        clusterArray[currentClusterDesignator_2]
                .setIndexToCongruenceClass(clusterArray[currentClusterDesignator_1].getIndexToCongruenceClass());

        return next_2;
    }

    /**
     * <p>
     * The operation join two stands where the tree node designating the first one is greater than the tree node
     * designating the second one
     * </p>
     *
     * @param standDesignator_1
     *            index to stand array for the first stand
     * @param standDesignator_2
     *            index to stand orray for the second stand
     */
    private void standJoinCase_02(int standDesignator_1, int standDesignator_2) {
        int currentStandCluster_2, currentStandCluster_1, dominantClassDesignator;

        standArray[standDesignator_2].setNextCCStand(standDesignator_1);

        // update all clusters in the stand to belong to the new class by changing their class field
        currentStandCluster_2 = standArray[standDesignator_2].getFirstStandCluster();
        currentStandCluster_1 = standArray[standDesignator_1].getFirstStandCluster();
        // assign the dominant class
        dominantClassDesignator = getTheUltimateDominantClass(
                congruenceClassArray[clusterArray[currentStandCluster_1].getIndexToCongruenceClass()]
                        .getDominantCClass());
        while (clusterArray[currentStandCluster_2].getNextStandCluster() != 0) {
            clusterArray[currentStandCluster_2].setIndexToCongruenceClass(dominantClassDesignator);
            currentStandCluster_2 = clusterArray[currentStandCluster_2].getNextStandCluster();
        }
        // the final update when next stand cluster is 0
        clusterArray[currentStandCluster_2].setIndexToCongruenceClass(dominantClassDesignator);

    }

    /**
     * <p>
     * The operation updates the cluster argument array after merging two classes, after merging we may need to update
     * FASOP, rearrange arguments ond merge clusters
     * </p>
     *
     * @param firstAccessor
     *            congruence class accessor for the smaller class e.g., 3
     * @param secondAccessor
     *            congruence class accessor for the larger class e.g., 7
     * @param level
     *            the level in the cluster argument string
     */
    private void updateClusterArgumentAfterMerging(int firstAccessor, int secondAccessor, int level) {

        int indexToArgString = congruenceClassArray[secondAccessor].getIndexInClusterArgArrayFromASOP(level);
        int indexToArgString_3 = congruenceClassArray[firstAccessor].getIndexInClusterArgArrayFromASOP(level);
        int tempIndexToArgString;
        if (congruenceClassArray[firstAccessor].getIndexInClusterArgArrayFromASOP(level) == 0) { // 3 is not in the
                                                                                                 // level at all
            // there are more than one argument string of 7 in the level enter the while loop, or pass forward
            while (clusterArgumentArray[indexToArgString].getNxtIndexWithSameCCNumberInLevel() != indexToArgString) {
                clusterArgumentArray[indexToArgString].setCcNumber(firstAccessor);

                updateClassFASOP(firstAccessor, level, indexToArgString);
                reArrangeArguments(indexToArgString);
                indexToArgString = clusterArgumentArray[indexToArgString].getNxtIndexWithSameCCNumberInLevel();
            }

            // there is no other argument string of 7 in the level and non is existing for 3 in the level or its the
            // final 7 after the list
            // first change the class number in the argument record from 7 to 3
            clusterArgumentArray[indexToArgString].setCcNumber(firstAccessor);
            // now we should go to the FASOP for class 3 and update it as now 3 exists in the level
            updateClassFASOP(firstAccessor, level, indexToArgString);
            reArrangeArguments(indexToArgString);

        } else {
            // 3 is existing in the level and can be anywhere, take this by looking at each father and its children
            while (indexToArgString != 0) {
                // no two 7s will be under same father
                tempIndexToArgString = clusterArgumentArray[indexToArgString].getNxtIndexWithSameCCNumberInLevel();
                // 3 is together with considered 7 under the same father
                boolean checkIfUnderSameParent = areClassesUnderSameParentInArgArray(indexToArgString, firstAccessor,
                        level);
                if (checkIfUnderSameParent) {// 3 and 7 have the same parent
                    if (clusterArgumentArray[indexToArgString].getNextClusterArg() == 0) {// but 3 has no children
                        // first change the class number in the argument record from 7 to 3
                        clusterArgumentArray[indexToArgString].setCcNumber(firstAccessor);

                        // now we should go to the FASOP for class 3 and update it as now we have two 3s existing under
                        // same father
                        updateClassFASOP(firstAccessor, level, indexToArgString);

                        // make 7 which is now 3 dormant by skipping it
                        int nextIndexToFollow = clusterArgumentArray[clusterArgumentArray[indexToArgString]
                                .getPrevClusterArg()].getNextClusterArg();
                        int prevIndexToFollow = 0;
                        // The if statement around while loop is a change after debugging
                        if (nextIndexToFollow != 0) {
                            // if it is zero it means it is the only argument, and alternative argument is 0
                            while (nextIndexToFollow != indexToArgString) {
                                prevIndexToFollow = nextIndexToFollow;
                                nextIndexToFollow = clusterArgumentArray[nextIndexToFollow].getAlternativeArg();
                            }
                        }
                        if (prevIndexToFollow == 0) {
                            // it is the first one in the children make the second child first
                            clusterArgumentArray[clusterArgumentArray[indexToArgString].getPrevClusterArg()]
                                    .setNextClusterArg(clusterArgumentArray[indexToArgString].getAlternativeArg());
                        } else {
                            // it is in between children, just deal with next alternative arguments
                            clusterArgumentArray[prevIndexToFollow]
                                    .setAlternativeArg(clusterArgumentArray[indexToArgString].getAlternativeArg());
                        }
                        // update the clusters by merging the two lists
                        mergeClusters(indexToArgString, indexToArgString_3);

                    } else {
                        // move what is under 7 to what is under 3
                        mergeSuffixTo(indexToArgString, indexToArgString_3);

                        // update the clusters by merging the two lists
                        mergeClusters(indexToArgString, indexToArgString_3);
                    }

                } else {
                    // 3 is not under the same father for the considered 7
                    // first change the class number in the argument record from 7 to 3
                    clusterArgumentArray[indexToArgString].setCcNumber(firstAccessor);
                    // now we should go to the FASOP for class 3 and update it as now 3 exists in the level
                    updateClassFASOP(firstAccessor, level, indexToArgString);
                    reArrangeArguments(indexToArgString);
                }
                // updated after debugging to the next line after commented one
                indexToArgString = tempIndexToArgString;
            }
        }
    }

    /**
     * <p>
     * The operation updates the cluster argument string by merging the children of arguments with classes that got
     * merged
     * </p>
     *
     * @param indexToArgString
     *            index to cluster argument array with a larger class e.g., 7
     * @param indexToArgString_3
     *            index to cluster argument array with a smaller class e.g., 3
     */
    private void mergeSuffixTo(int indexToArgString, int indexToArgString_3) {

        int currentArgumentToMove = clusterArgumentArray[indexToArgString].getNextClusterArg();
        int nextArgumentToMove = clusterArgumentArray[currentArgumentToMove].getAlternativeArg();
        int currentLargestArgumentInList_3 = clusterArgumentArray[indexToArgString_3].getNextClusterArg();
        int previousLargestArgumentInList_3 = 0;
        int nextLargestArgumentInList_3 = 0;

        // int nextLargestArgumentInList_3 = clusterArgumentArray[currentLargestArgumentInList_3].getAlternativeArg();

        int classDesignator_7 = clusterArgumentArray[currentArgumentToMove].getCcNumber();
        int classDesignator_3 = clusterArgumentArray[currentLargestArgumentInList_3].getCcNumber();

        // the condition works if the cluster argument string at position 0, has all zeroes. Done in line 70
        while (clusterArgumentArray[currentArgumentToMove].getAlternativeArg() != currentArgumentToMove) {

            // case_01, moving argument under 7 that doesn't exist under 3
            if (classDesignator_7 > classDesignator_3) {
                // reallocate pointers in 3
                clusterArgumentArray[indexToArgString_3].setNextClusterArg(currentArgumentToMove);
                clusterArgumentArray[currentArgumentToMove].setPrevClusterArg(indexToArgString_3);
                clusterArgumentArray[currentArgumentToMove].setAlternativeArg(currentLargestArgumentInList_3);

                // reset what to deal with on the next iteration
                currentLargestArgumentInList_3 = currentArgumentToMove;

            } else if (classDesignator_7 < classDesignator_3) {
                // int previousLargestArgumentInList_3 = 0;
                nextLargestArgumentInList_3 = currentLargestArgumentInList_3;
                while (classDesignator_7 < classDesignator_3) {// it has to go into this loop at least once
                                                               // previousLargestArgumentInList_3 can't be 0
                    previousLargestArgumentInList_3 = nextLargestArgumentInList_3;
                    nextLargestArgumentInList_3 = clusterArgumentArray[nextLargestArgumentInList_3].getAlternativeArg();
                    classDesignator_3 = clusterArgumentArray[nextLargestArgumentInList_3].getCcNumber();
                }
                if (classDesignator_3 == classDesignator_7) {
                    // cut to the chase, these two will be under same parent, second one adopted
                    // call merge suffix again on the two new arguments
                    mergeSuffixTo(currentArgumentToMove, nextLargestArgumentInList_3);

                    // update the clusters by merging the two lists
                    mergeClusters(currentArgumentToMove, nextLargestArgumentInList_3);

                    // get the next one under 3
                    currentLargestArgumentInList_3 = clusterArgumentArray[currentLargestArgumentInList_3]
                            .getAlternativeArg();
                } else {
                    // update things on 3 side
                    clusterArgumentArray[previousLargestArgumentInList_3].setAlternativeArg(currentArgumentToMove);
                    clusterArgumentArray[currentArgumentToMove].setPrevClusterArg(indexToArgString_3);
                    clusterArgumentArray[currentArgumentToMove].setAlternativeArg(nextLargestArgumentInList_3);

                    // reset what to deal with on the next iteration
                    currentLargestArgumentInList_3 = currentArgumentToMove;

                }
            } else {
                // classDesignator_7 == classDesignator_3
                mergeSuffixTo(currentArgumentToMove, currentLargestArgumentInList_3);

                // update the clusters by merging the two lists
                mergeClusters(currentArgumentToMove, currentLargestArgumentInList_3);

                // get the next one under 3
                currentLargestArgumentInList_3 = clusterArgumentArray[currentLargestArgumentInList_3]
                        .getAlternativeArg();
            }
            // reallocate pointers in 7
            clusterArgumentArray[indexToArgString].setNextClusterArg(nextArgumentToMove);

            // reset what to deal with on the next iteration
            // currentLargestArgumentInList_3 = currentArgumentToMove;
            currentArgumentToMove = clusterArgumentArray[indexToArgString].getNextClusterArg();
            nextArgumentToMove = clusterArgumentArray[currentArgumentToMove].getAlternativeArg();

            // might be common for all cases move down
            classDesignator_7 = clusterArgumentArray[currentArgumentToMove].getCcNumber();
            classDesignator_3 = clusterArgumentArray[currentLargestArgumentInList_3].getCcNumber();
        }
        // forget about 7 which is now 3 and after all its children are moved to the 3
        int indexToParentOfArg_7 = clusterArgumentArray[indexToArgString].getPrevClusterArg();
        int nextAfter7 = clusterArgumentArray[indexToArgString].getAlternativeArg();
        clusterArgumentArray[indexToParentOfArg_7].setNextClusterArg(nextAfter7);

    }

    /**
     * <p>
     * The operation merges to two clusters that used to have different arguments and now they have the same argument
     * after the larger class turning to the smaller class e.g., 7 ---> 3
     * </p>
     *
     * @param indexToArgString
     *            index to argument string for the larger class e.g., 7
     * @param indexToArgString_3
     *            index to argument string for the smaller class e.g., 3
     */
    private void mergeClusters(int indexToArgString, int indexToArgString_3) {

        int indexInClusterArray_7 = clusterArgumentArray[indexToArgString].getClusterNumber();
        int indexInClusterArray_3 = clusterArgumentArray[indexToArgString_3].getClusterNumber();

        int nextIndexInClusterArray_7 = 0;
        int nextIndexInClusterArray_3 = 0;
        Integer label_7 = clusterArray[indexInClusterArray_7].getTreeNodeLabel();
        Integer label_3 = clusterArray[indexInClusterArray_3].getTreeNodeLabel();

        while (indexInClusterArray_7 != 0) {
            // move until the next with same argument string is 0
            if (label_7 > label_3) {
                // the label for the 7 list is greater than the 3 list

                nextIndexInClusterArray_7 = clusterArray[indexInClusterArray_7].getNextWithSameArg();
                clusterArray[indexInClusterArray_7].setNextWithSameArg(indexInClusterArray_3);
                indexInClusterArray_3 = indexInClusterArray_7;
                indexInClusterArray_7 = nextIndexInClusterArray_7;
                label_7 = clusterArray[indexInClusterArray_7].getTreeNodeLabel();
                label_3 = clusterArray[indexInClusterArray_3].getTreeNodeLabel();
            } else if (label_7 < label_3) {
                // the label for the 7 list is smaller than the 7 list

                if (clusterArray[clusterArray[indexInClusterArray_3].getNextWithSameArg()].getTreeNodeLabel()
                        .equals(clusterArray[indexInClusterArray_7].getTreeNodeLabel())) {
                    indexInClusterArray_3 = clusterArray[indexInClusterArray_3].getNextWithSameArg();
                    label_3 = clusterArray[indexInClusterArray_3].getTreeNodeLabel();
                } else {
                    nextIndexInClusterArray_7 = clusterArray[indexInClusterArray_7].getNextWithSameArg();
                    while (clusterArray[clusterArray[indexInClusterArray_3].getNextWithSameArg()]
                            .getTreeNodeLabel() > clusterArray[indexInClusterArray_7].getTreeNodeLabel()) {
                        indexInClusterArray_3 = clusterArray[indexInClusterArray_3].getNextWithSameArg();
                    }
                    nextIndexInClusterArray_3 = clusterArray[indexInClusterArray_3].getNextWithSameArg();
                    clusterArray[indexInClusterArray_3].setNextWithSameArg(indexInClusterArray_7);
                    clusterArray[indexInClusterArray_7].setNextWithSameArg(nextIndexInClusterArray_3);
                    indexInClusterArray_3 = indexInClusterArray_7;
                    indexInClusterArray_7 = nextIndexInClusterArray_7;
                    label_7 = clusterArray[indexInClusterArray_7].getTreeNodeLabel();
                    label_3 = clusterArray[indexInClusterArray_3].getTreeNodeLabel();
                }
            } else if (label_3.equals(label_7)) {
                int dominantClass_3 = getTheUltimateDominantClass(
                        congruenceClassArray[clusterArray[indexInClusterArray_3].getIndexToCongruenceClass()]
                                .getDominantCClass());
                int dominantClass_7 = getTheUltimateDominantClass(
                        congruenceClassArray[clusterArray[indexInClusterArray_7].getIndexToCongruenceClass()]
                                .getDominantCClass());

                // gone back to the dominant class
                if (dominantClass_7 == dominantClass_3) {
                    // TODO: put unused cluster to the re-use list
                } else {
                    classMergeList.add(dominantClass_3);
                    classMergeList.add(dominantClass_7);
                }
                // change the dominant class of 7 to 3, which happens in both cases above
                clusterArray[indexInClusterArray_7]
                        .setDominantCluster(clusterArray[indexInClusterArray_3].getDominantCluster());
                // get to the next one with same argument on list 7
                indexInClusterArray_7 = clusterArray[indexInClusterArray_7].getNextWithSameArg();
                indexInClusterArray_3 = clusterArray[indexInClusterArray_3].getNextWithSameArg();
                // get the new tree node labels
                label_7 = clusterArray[indexInClusterArray_7].getTreeNodeLabel();
                label_3 = clusterArray[indexInClusterArray_3].getTreeNodeLabel();
            }

        }

    }

    /**
     * <p>
     * The operation rearrange children under a parent and keep them in order, children are ordered in descending order
     * </p>
     *
     * @param argIndexWithChangedClass
     *            index to the argument string of the class that got changed e.g., 7 ---> 3
     */
    private void reArrangeArguments(int argIndexWithChangedClass) {

        // It is the only child under a parent, or the last one of the children,
        // No rearrangement needed
        if (clusterArgumentArray[argIndexWithChangedClass].getAlternativeArg() == 0) {
        } else {
            // changed class has siblings under the parent, some shifting will happen in different cases below
            if (clusterArgumentArray[clusterArgumentArray[argIndexWithChangedClass].getPrevClusterArg()]
                    .getNextClusterArg() == argIndexWithChangedClass) {
                // make the parent point to the next after cluster record that we have changed to a lower class
                clusterArgumentArray[clusterArgumentArray[argIndexWithChangedClass].getPrevClusterArg()]
                        .setNextClusterArg(clusterArgumentArray[argIndexWithChangedClass].getAlternativeArg());

                // move it to the right place
                int previousIndexToArgString = argIndexWithChangedClass;
                int nextIndexToArgString = clusterArgumentArray[argIndexWithChangedClass].getAlternativeArg();
                while (clusterArgumentArray[argIndexWithChangedClass]
                        .getCcNumber() < clusterArgumentArray[nextIndexToArgString].getCcNumber()) {
                    previousIndexToArgString = nextIndexToArgString;
                    nextIndexToArgString = clusterArgumentArray[nextIndexToArgString].getAlternativeArg();
                }
                // set our changed argument record to have an alternative argument less than it
                clusterArgumentArray[argIndexWithChangedClass].setAlternativeArg(nextIndexToArgString);

                // the previous one should now point to our changed argument record
                clusterArgumentArray[previousIndexToArgString].setAlternativeArg(argIndexWithChangedClass);

            } else {
                // it is not the first child
                int currentIndexToArgString = clusterArgumentArray[clusterArgumentArray[argIndexWithChangedClass]
                        .getPrevClusterArg()].getNextClusterArg();
                int nextIndexToArgString = clusterArgumentArray[currentIndexToArgString].getAlternativeArg();
                int previousIndexToArgString = currentIndexToArgString;

                // walk the list of children until we get to the changed child
                while (nextIndexToArgString != argIndexWithChangedClass) {
                    previousIndexToArgString = nextIndexToArgString;
                    nextIndexToArgString = clusterArgumentArray[nextIndexToArgString].getAlternativeArg();
                }

                // connect the two left and right children before we remove the middle one to its rightful place
                clusterArgumentArray[previousIndexToArgString]
                        .setAlternativeArg(clusterArgumentArray[argIndexWithChangedClass].getAlternativeArg());

                previousIndexToArgString = argIndexWithChangedClass;
                nextIndexToArgString = clusterArgumentArray[argIndexWithChangedClass].getAlternativeArg();

                while (clusterArgumentArray[argIndexWithChangedClass]
                        .getCcNumber() < clusterArgumentArray[nextIndexToArgString].getCcNumber()) {
                    previousIndexToArgString = nextIndexToArgString;
                    nextIndexToArgString = clusterArgumentArray[nextIndexToArgString].getAlternativeArg();
                }
                // set our changed argument record to have an alternative argument less than it
                clusterArgumentArray[argIndexWithChangedClass].setAlternativeArg(nextIndexToArgString);

                // the previous one should now point to our changed argument record
                clusterArgumentArray[previousIndexToArgString].setAlternativeArg(argIndexWithChangedClass);
            }
        }
    }

    /**
     * <p>
     * The operation checks if the two classes on the argument list are under the same parent
     * </p>
     *
     * @param indexToString
     *            index to the argument string with larger class e.g., 7
     * @param firstAccessor
     *            smaller class accessor e.g., 3
     * @param level
     *            the argument string level
     *
     * @return the operation returns true when a parent for the argument at
     */
    private boolean areClassesUnderSameParentInArgArray(int indexToString, int firstAccessor, int level) {
        int parent_07 = clusterArgumentArray[indexToString].getPrevClusterArg();
        int indexToFollow = congruenceClassArray[firstAccessor].getIndexInClusterArgArrayFromASOP(level);
        int parent_03 = clusterArgumentArray[indexToFollow].getPrevClusterArg();

        if (parent_07 == parent_03) {
            return true;
        } else if (parent_07 > parent_03) {
            return false;
        } else {
            while (parent_07 < parent_03) {
                indexToFollow = clusterArgumentArray[indexToFollow].getNxtIndexWithSameCCNumberInLevel();
                parent_03 = clusterArgumentArray[indexToFollow].getPrevClusterArg();
            }
            if (parent_07 == parent_03) {
                return true;
            }
            return false;
        }

    }

    /**
     * <p>
     * The operation create a cluster argument as an entry to the cluster argument array
     * </p>
     *
     * @param label
     *            a label to the respective cluster with arguments in {@param clusterArgumentString}
     * @param clusterArgumentString
     *            an argument string holding the arguments for the cluster with {@param label}
     *
     * @return an integer value representing an index to the argument array for the argument created
     */
    private int createClusterArgumentArray(Integer label, Queue<Integer> clusterArgumentString) {
        // initial index for the cluster argument array (CAA)
        int index = 1;
        int precedingIndex = 0;
        int indexInClusterArray = 0;
        int clusterNumber = 0;
        boolean existed = false;
        boolean alternativeExists = true;
        boolean precedingIndexUsed = false;

        // for ASOP
        int level = 0;

        if (argListLength(clusterArgumentString) == 0) {
            // for constants and variables no arg of empty string has being created
            if (clusterArgumentArray[index] == null) {
                ClusterArgument clusterArgument = new ClusterArgument(0, 0, 0, 1, 0);
                clusterArgumentArray[index] = clusterArgument;
            } else { // empty arg string but already exists
                updateNextWithSameArgument(label, index, topCongruenceClusterDesignator);
            }
            return index;
        } else {
            // arg string is not empty
            while (argListLength(clusterArgumentString) > 0) {
                // remove the far right one first (FIFO)
                int lastCCDesignator = removeFirstArgDesignator();
                if (clusterArgumentArray[index].getNextClusterArg() == 0
                        && clusterArgumentArray[index].getCcNumber() != lastCCDesignator) {
                    if (argListLength(clusterArgumentString) == 0)
                        clusterNumber = topCongruenceClusterDesignator;
                    ClusterArgument clusterArgument = new ClusterArgument(0, index, lastCCDesignator, clusterNumber, 0);
                    clusterArgumentArray[topArgStrArrIndex] = clusterArgument;
                    // old one to the new one
                    clusterArgumentArray[index].setNextClusterArg(topArgStrArrIndex);
                    // index now to the newly created argument array
                    index = topArgStrArrIndex;

                    // update ASOP for the class lastCCDesignator used here
                    updateClassFASOP(lastCCDesignator, ++level, index);

                    topArgStrArrIndex++; // move to the next available
                } else if (clusterArgumentArray[index].getNextClusterArg() == 0
                        && clusterArgumentArray[index].getCcNumber() == lastCCDesignator) {
                    // we don't create a new arg string in the array
                    // But we have to update the cluster if it is the last argument in the current cluster arg string
                    if (argListLength(clusterArgumentString) == 0) {
                        // it was created but it is not active yet
                        if (clusterArgumentArray[index].getClusterNumber() == 0) {
                            // set the cluster number to the one created already
                            clusterArgumentArray[index].setClusterNumber(topCongruenceClusterDesignator);
                        } else {
                            // it is an active string, and we should update previous cluster with similar arg
                            updateNextWithSameArgument(label, index, topCongruenceClusterDesignator);
                        }
                    }
                } else if (clusterArgumentArray[index].getNextClusterArg() != 0
                        && clusterArgumentArray[index].getCcNumber() != lastCCDesignator) {
                    // change index to the next one
                    index = clusterArgumentArray[index].getNextClusterArg();
                    if (clusterArgumentArray[index].getCcNumber() == lastCCDesignator) {
                        if (argListLength(clusterArgumentString) == 0) {
                            if (clusterArgumentArray[index].getClusterNumber() == 0) {
                                // set the cluster number to the one that will be created next
                                clusterArgumentArray[index].setClusterNumber(topCongruenceClusterDesignator);
                            } else {
                                // it is an active string, and we should update previous cluster with similar arg
                                updateNextWithSameArgument(label, index, topCongruenceClusterDesignator);

                            }

                            return index;
                        }
                        // even though we didn't change anything in the argument string, increment the level
                        level++;
                    } else {
                        if (clusterArgumentArray[index].getAlternativeArg() == 0
                                && clusterArgumentArray[index].getCcNumber() < lastCCDesignator) {
                            if (argListLength(clusterArgumentString) == 0)
                                // if it is the last arg string we are creating
                                clusterNumber = topCongruenceClusterDesignator;
                            ClusterArgument clusterArgument = new ClusterArgument(0, index - 1, lastCCDesignator,
                                    clusterNumber, index);
                            clusterArgumentArray[topArgStrArrIndex] = clusterArgument;
                            clusterArgumentArray[index - 1].setNextClusterArg(topArgStrArrIndex);
                            index = topArgStrArrIndex;

                            // update ASOP for the class lastCCDesignator used here
                            updateClassFASOP(lastCCDesignator, ++level, index);

                            topArgStrArrIndex++;
                        }
                        if (clusterArgumentArray[index].getAlternativeArg() == 0
                                && clusterArgumentArray[index].getCcNumber() > lastCCDesignator) {
                            if (argListLength(clusterArgumentString) == 0)
                                // if it is the last arg string we are creating
                                clusterNumber = topCongruenceClusterDesignator;
                            ClusterArgument clusterArgument = new ClusterArgument(0, index - 1, lastCCDesignator,
                                    clusterNumber, 0);
                            // seems to do the right thing, but I have to check if it doesn't break anything else
                            clusterArgumentArray[index].setAlternativeArg(topArgStrArrIndex);
                            clusterArgumentArray[topArgStrArrIndex] = clusterArgument;
                            index = topArgStrArrIndex;

                            // update ASOP for the class lastCCDesignator used here
                            updateClassFASOP(lastCCDesignator, ++level, index);

                            topArgStrArrIndex++;
                        }
                        while (alternativeExists) {
                            if (clusterArgumentArray[index].getAlternativeArg() != 0) {
                                if (clusterArgumentArray[index].getCcNumber() == lastCCDesignator) {
                                    existed = true;
                                    break;
                                } else {
                                    index = clusterArgumentArray[index].getAlternativeArg();
                                }
                            } else {
                                if (clusterArgumentArray[index].getCcNumber() == lastCCDesignator) {
                                    existed = true;
                                    break;
                                } else {
                                    alternativeExists = false;
                                }
                            }
                        }

                        if (!existed) {
                            index = clusterArgumentArray[clusterArgumentArray[index].getPrevClusterArg()]
                                    .getNextClusterArg();

                            while (clusterArgumentArray[index].getCcNumber() > lastCCDesignator) {
                                precedingIndex = index;
                                precedingIndexUsed = true;
                                index = clusterArgumentArray[index].getAlternativeArg();

                            }

                            if (clusterArgumentArray[index].getCcNumber() < lastCCDesignator && precedingIndexUsed) {
                                if (argListLength(clusterArgumentString) == 0)
                                    // if it is the last arg string we are creating
                                    clusterNumber = topCongruenceClusterDesignator;
                                ClusterArgument clusterArgument = new ClusterArgument(0,
                                        clusterArgumentArray[index].getPrevClusterArg(), lastCCDesignator,
                                        clusterNumber, index);
                                clusterArgumentArray[topArgStrArrIndex] = clusterArgument;
                                clusterArgumentArray[precedingIndex].setAlternativeArg(topArgStrArrIndex);
                                index = topArgStrArrIndex;

                                // update ASOP for the class lastCCDesignator used here
                                updateClassFASOP(lastCCDesignator, ++level, index);

                                topArgStrArrIndex++;
                            }
                            if (clusterArgumentArray[index].getCcNumber() < lastCCDesignator && !precedingIndexUsed) {
                                if (argListLength(clusterArgumentString) == 0)
                                    // if it is the last arg string we are creating
                                    clusterNumber = topCongruenceClusterDesignator;
                                ClusterArgument clusterArgument = new ClusterArgument(0,
                                        clusterArgumentArray[index].getPrevClusterArg(), lastCCDesignator,
                                        clusterNumber, index);
                                clusterArgumentArray[topArgStrArrIndex] = clusterArgument;
                                clusterArgumentArray[clusterArgumentArray[index].getPrevClusterArg()]
                                        .setNextClusterArg(topArgStrArrIndex);
                                clusterArgumentArray[topArgStrArrIndex].setAlternativeArg(index);
                                index = topArgStrArrIndex;

                                // update ASOP for the class lastCCDesignator used here
                                updateClassFASOP(lastCCDesignator, ++level, index);
                                topArgStrArrIndex++;
                            }

                        }

                    }

                }
            }

        }
        return index;
    }

    /**
     * <p>
     * The operation updates the clusters that ends up being with the same argument after merging the classes and put
     * them in the list, the list is kept in order
     * </p>
     *
     * @param lab
     *            label for the cluster we want it to join the list of clusters with same arguments
     * @param index
     *            index to the first cluster in the list of clusters with same arguments
     * @param topCongruenceClusterDesignator
     *            the top congruence cluster designator
     */
    private void updateNextWithSameArgument(Integer lab, int index, int topCongruenceClusterDesignator) {
        // get the tree node label of the first cluster
        Integer label_1 = clusterArray[clusterArgumentArray[index].getClusterNumber()].getTreeNodeLabel();
        Integer label_2 = lab;
        int prevIndexInClusterArray = 0;
        int nextIndexInClusterArray = clusterArgumentArray[index].getClusterNumber();
        ;
        if (label_1 < label_2) {
            // it is greater than the first one, make it the first one and change the argument string position
            clusterArray[topCongruenceClusterDesignator]
                    .setNextWithSameArg(clusterArgumentArray[index].getClusterNumber());
            clusterArgumentArray[index].setClusterNumber(topCongruenceClusterDesignator);
        } else {
            // it is less than the first argument in the list, find the right position to insert it
            while (label_1 > label_2) {
                prevIndexInClusterArray = nextIndexInClusterArray;
                nextIndexInClusterArray = clusterArray[nextIndexInClusterArray].getNextWithSameArg();
                label_1 = clusterArray[nextIndexInClusterArray].getTreeNodeLabel();

            }
            clusterArray[prevIndexInClusterArray].setNextWithSameArg(topCongruenceClusterDesignator);
            clusterArray[topCongruenceClusterDesignator].setNextWithSameArg(nextIndexInClusterArray);
        }
    }

    /**
     * <p>
     * The operation updates the FASOP for the classes created as they are used as arguments in the clusters, their
     * occurrences in the cluster argument array are recorded in the levels for the FASOP array
     * </p>
     *
     * @param ccDesignator
     *            the designator to the congruence class holding the FASOP
     * @param level
     *            level in FASOP being updated
     * @param indexInArgumentString
     *            the index in the cluster argument array where the class designated by {@param ccDesignator}
     */
    private void updateClassFASOP(int ccDesignator, int level, int indexInArgumentString) {
        // for the class given go check if for the level provided index
        // to the arg array is 0, which means no this class is at that level
        if (congruenceClassArray[ccDesignator].getIndexInClusterArgArrayFromASOP(level) == 0) {
            // just add an index to the arg array one to that level
            congruenceClassArray[ccDesignator].addToArgStringOccPos(indexInArgumentString, level);
        } else {
            // there is a class at that level and it has more to fix this as we have to keep the list in order of
            // their parents
            int prevIndexToFollow = 0;
            boolean specialCase = true;
            int nextIndexToFollow = congruenceClassArray[ccDesignator].getIndexInClusterArgArrayFromASOP(level);

            if (clusterArgumentArray[indexInArgumentString]
                    .getPrevClusterArg() == clusterArgumentArray[nextIndexToFollow].getPrevClusterArg()) {
                // do nothing. last argument string position for 3 can stay the same
            } else {

                // we have to make sure the parent for the nextIndexToFollow when it is 0 exist, and it is 0, otherwise
                // this will fail, set the argument string at index 0, having 0's all over
                while (clusterArgumentArray[indexInArgumentString]
                        .getPrevClusterArg() < clusterArgumentArray[nextIndexToFollow].getPrevClusterArg()) {
                    prevIndexToFollow = nextIndexToFollow;
                    nextIndexToFollow = clusterArgumentArray[nextIndexToFollow].getNxtIndexWithSameCCNumberInLevel();
                    specialCase = false;
                }
                if (specialCase) {
                    // when we have the new having the biggest father, have to update the FASOP
                    clusterArgumentArray[indexInArgumentString].setNexIndexWithSameCCInSameLevel(nextIndexToFollow);
                    congruenceClassArray[ccDesignator].addToArgStringOccPos(indexInArgumentString, level);
                } else {
                    clusterArgumentArray[prevIndexToFollow].setNexIndexWithSameCCInSameLevel(indexInArgumentString);
                    clusterArgumentArray[indexInArgumentString].setNexIndexWithSameCCInSameLevel(nextIndexToFollow);
                }
            }
        }

        // this should update last argument string position in a class to get us to the lowest level during searching.
        if (congruenceClassArray[ccDesignator].getIndexInClusterArgArrayFromASOP(level + 1) == 0) {
            congruenceClassArray[ccDesignator].setLastArgStringPosition(level);
        } else {
            // it is not the last position leave the current one
        }
    }

    public boolean isClassDesignator(int label) {
        return label <= topCongruenceClassDesignator;
    }

    public void displayCongruence(List<String> symbolMapping, int classIndex) {
        StringBuilder sb = new StringBuilder();

        CongruenceClass congruenceClass = congruenceClassArray[classIndex];
        if (congruenceClass.getClassTag() != congruenceClass.getDominantCClass()) {
            congruenceClass = congruenceClassArray[congruenceClass.getDominantCClass()];
        }
        Stand stand = standArray[congruenceClass.getFirstStand()];

        sb.append("CC" + classIndex + " -> ");

        while (stand.getStandTag() != 0) {
            CongruenceCluster congruenceCluster = clusterArray[stand.getFirstStandCluster()];

            do {
                displayCluster(symbolMapping, congruenceCluster, sb);
                if (congruenceCluster.getNextStandCluster() != 0
                        && congruenceCluster.getIndexToTag() != congruenceCluster.getNextStandCluster()) {
                    sb.append(" | ");
                }

                congruenceCluster = clusterArray[congruenceCluster.getNextStandCluster()];
            } while (congruenceCluster.getIndexToTag() != 0
                    && congruenceCluster.getIndexToTag() != congruenceCluster.getNextStandCluster());

            if (stand.getNextCCStand() != 0) {
                sb.append(" | ");
            }
            stand = standArray[stand.getNextCCStand()];
        }

        System.out.println(sb);
    }

    private void displayCluster(List<String> symbolMapping, CongruenceCluster cluster, StringBuilder sb) {
        String operator = symbolMapping.get(cluster.getTreeNodeLabel());
        ClusterArgument argument = clusterArgumentArray[cluster.getIndexToArgList()];

        sb.append(operator + " ");

        while (argument.getPrevClusterArg() != 0) {
            sb.append("CC" + argument.getCcNumber());

            argument = clusterArgumentArray[argument.getPrevClusterArg()];
            if (argument.getPrevClusterArg() != 0)
                sb.append(", ");
        }
    }

    // public methods to help me visualize the arrays for testing: TO BE DELETED
    public ClusterArgument[] getClusterArgArray() {
        return clusterArgumentArray;
    }

    public CongruenceCluster[] getClusterArray() {
        return clusterArray;
    }

    public Stand[] getStandArray() {
        return standArray;
    }

    public CongruenceClass[] getCongruenceClassArray() {
        return congruenceClassArray;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ClusterArguments: ");
        sb.append(Arrays.toString(clusterArgumentArray));
        sb.append("\nClusters: ");
        sb.append(Arrays.toString(clusterArray));
        sb.append("\nStands: ");
        sb.append(Arrays.toString(standArray));
        sb.append("\nCongruenceClasses: ");
        sb.append(Arrays.toString(congruenceClassArray));
        sb.append("\n");
        return sb.toString();
    }
}
