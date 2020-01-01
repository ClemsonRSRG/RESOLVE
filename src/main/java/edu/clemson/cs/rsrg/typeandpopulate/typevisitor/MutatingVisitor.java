/*
 * MutatingVisitor.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.typevisitor;

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p>
 * This is the abstract base class for mutating bounded variables.
 * </p>
 *
 * @version 2.0
 */
abstract class MutatingVisitor extends BoundVariableVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The last type visited with the appropriate replacements done by this
     * visitor.
     * </p>
     */
    private MTType myClosingType;

    /**
     * <p>
     * The resulting mathematical type.
     * </p>
     */
    protected MTType myFinalExpression;

    /**
     * <p>
     * This stores the root mathematical type.
     * </p>
     */
    private MTType myRoot;

    /**
     * <p>
     * A list containing a map of changes for each level of children nodes.
     * </p>
     */
    private final LinkedList<Map<Integer, MTType>> myChangesAtLevel =
            new LinkedList<>();

    /**
     * <p>
     * A list of indices created during the visit.
     * </p>
     */
    private final LinkedList<Integer> myIndices = new LinkedList<>();

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method adds additional logic before we visit a {@link MTType} by
     * storing all the
     * {@link MTType MTypes} in {@code t}.
     * </p>
     *
     * @param t A math type.
     */
    @Override
    public final void beginMTType(MTType t) {
        if (myRoot == null) {
            myRoot = t;
            myFinalExpression = myRoot;
        }

        myIndices.push(0); // We start at the zeroth child
        myChangesAtLevel.push(new HashMap<Integer, MTType>());

        mutateBeginMTType(t);
    }

    /**
     * <p>
     * This method adds additional logic after we visit the children of a
     * {@link MTType} by replacing
     * the closing type.
     * </p>
     *
     * @param t A math type.
     */
    @Override
    public final void endChildren(MTType t) {
        myClosingType = t;

        Map<Integer, MTType> changes = myChangesAtLevel.peek();
        if (!changes.isEmpty()) {
            myClosingType = t.withComponentsReplaced(changes);
            replaceWith(myClosingType);
        }

        mutateEndChildren(t);
    }

    /**
     * <p>
     * This method adds additional logic after we visit a {@link MTType} by
     * removing all the items
     * created during the visit.
     * </p>
     *
     * @param t A math type.
     */
    @Override
    public final void endMTType(MTType t) {
        mutateEndMTType(t);

        // We're not visiting any more children at this level (because the
        // level just ended!)
        myIndices.pop();
        myChangesAtLevel.pop();

        // If I'm the root, there's no chance I have any siblings
        if (t != myRoot) {
            // Increment to the next potential child index
            int i = myIndices.pop();

            myIndices.push(i + 1);
        }
    }

    /**
     * <p>
     * This method returns the final mathematical type after all the changes are
     * done.
     * </p>
     *
     * @return The resulting final {@link MTType}.
     */
    public final MTType getFinalExpression() {
        return myFinalExpression;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This method checks to see if we are at the root node.
     * </p>
     *
     * @return {@code true} if we are at the root node, {@code false} otherwise.
     */
    protected final boolean atRoot() {
        return (myIndices.size() == 1);
    }

    /**
     * <p>
     * This method returns the current closing mathematical type with the
     * appropriate replacements.
     * </p>
     *
     * @return The current closing {@link MTType}.
     */
    protected final MTType getTransformedVersion() {
        return myClosingType;
    }

    /**
     * <p>
     * This method adds additional logic to mutate {@code t} before we visit it.
     * </p>
     *
     * @param t A math type.
     */
    protected void mutateBeginMTType(MTType t) {}

    /**
     * <p>
     * This method adds additional logic to mutate {@code t}'s children before
     * we visit them.
     * </p>
     *
     * @param t A math type.
     */
    protected void mutateEndChildren(MTType t) {}

    /**
     * <p>
     * This method adds additional logic to mutate a {@code t} after we visit
     * it.
     * </p>
     *
     * @param t A math type.
     */
    protected void mutateEndMTType(MTType t) {}

    /**
     * <p>
     * This method uses {@code replacement} and appropriately replaces it.
     * </p>
     *
     * @param replacement The replacing {@link MTType}.
     */
    protected final void replaceWith(MTType replacement) {
        if (myIndices.size() == 1) {
            // We're the root
            myFinalExpression = replacement;
        }
        else {
            myChangesAtLevel.get(1).put(myIndices.get(1), replacement);
        }
    }

}
