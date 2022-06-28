/*
 * SymmetricBoundVariableVisitor.java
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
package edu.clemson.rsrg.typeandpopulate.typevisitor;

import edu.clemson.rsrg.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.rsrg.typeandpopulate.query.UniversalVariableQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.FinalizedScope;
import java.util.*;

/**
 * <p>
 * This is the abstract base class for symmetrically visiting bounded variables.
 * </p>
 *
 * @version 2.0
 */
abstract class SymmetricBoundVariableVisitor extends SymmetricVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A scope for the first set of bounded variables.
     * </p>
     */
    private Deque<Map<String, MTType>> myBoundVariables1 = new LinkedList<>();

    /**
     * <p>
     * A scope for the second set of bounded variables.
     * </p>
     */
    private Deque<Map<String, MTType>> myBoundVariables2 = new LinkedList<>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a symmetric visitor with no initial bounded variables.
     * </p>
     */
    protected SymmetricBoundVariableVisitor() {
    }

    /**
     * <p>
     * This constructs a symmetric visitor using a {@link FinalizedScope}.
     * </p>
     *
     * @param context1
     *            A finalized scope.
     */
    protected SymmetricBoundVariableVisitor(FinalizedScope context1) {
        Map<String, MTType> topLevel = new HashMap<>();

        List<MathSymbolEntry> quantifiedVariables = context1.query(UniversalVariableQuery.INSTANCE);
        for (MathSymbolEntry entry : quantifiedVariables) {
            topLevel.put(entry.getName(), entry.getType());
        }

        myBoundVariables1.push(topLevel);
    }

    /**
     * <p>
     * This constructs a symmetric visitor with initial bounded variables for the first scope.
     * </p>
     *
     * @param context1
     *            Bounded variables map.
     */
    protected SymmetricBoundVariableVisitor(Map<String, MTType> context1) {
        myBoundVariables1.push(new HashMap<>(context1));
    }

    /**
     * <p>
     * This constructs a symmetric visitor with initial bounded variables for both scopes.
     * </p>
     *
     * @param context1
     *            Bounded variables map for the first scope.
     * @param context2
     *            Bounded variables map for the second scope.
     */
    protected SymmetricBoundVariableVisitor(Map<String, MTType> context1, Map<String, MTType> context2) {
        this(context1);
        myBoundVariables2.push(new HashMap<>(context2));
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method adds additional logic before we visit two {@link MTBigUnion} by storing the quantified variables into
     * separate scopes.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return The result from calling {@link #boundBeginMTBigUnion(MTBigUnion, MTBigUnion)}.
     */
    @Override
    public final boolean beginMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        myBoundVariables1.push(t1.getQuantifiedVariables());
        myBoundVariables2.push(t2.getQuantifiedVariables());

        return boundBeginMTBigUnion(t1, t2);
    }

    /**
     * <p>
     * This method adds additional logic after we visit two {@link MTBigUnion} by removing the quantified variables from
     * our scopes.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return The result from calling {@link #boundEndMTBigUnion(MTBigUnion, MTBigUnion)}.
     */
    @Override
    public final boolean endMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        boolean result = boundEndMTBigUnion(t1, t2);
        myBoundVariables1.pop();
        myBoundVariables2.pop();

        return result;
    }

    /**
     * <p>
     * This method resets this symmetric visitor.
     * </p>
     */
    public void reset() {
        myBoundVariables1.clear();
        myBoundVariables2.clear();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This method adds additional logic to bound {@code t1} and {@code t2} before we visit it.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return {@code true} if {@code t1} and {@code t2} bind, {@code false} otherwise.
     */
    protected boolean boundBeginMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        return true;
    }

    /**
     * <p>
     * This method adds additional logic to bound {@code t1} and {@code t2} after we visit it.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return {@code true} if {@code t1} and {@code t2} bind, {@code false} otherwise.
     */
    protected boolean boundEndMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        return true;
    }

    /**
     * <p>
     * This method returns the mathematical type used to bind the given variable name from my first scope of bounded
     * variables.
     * </p>
     *
     * @param name
     *            A variable name.
     *
     * @return The {@link MTType} type used for binding.
     */
    protected final MTType getInnermostBinding1(String name) {
        return getInnermostBinding(myBoundVariables1, name);
    }

    /**
     * <p>
     * This method returns the mathematical type used to bind the given variable name from my second scope of bounded
     * variables.
     * </p>
     *
     * @param name
     *            A variable name.
     *
     * @return The {@link MTType} type used for binding.
     */
    protected final MTType getInnermostBinding2(String name) {
        return getInnermostBinding(myBoundVariables2, name);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This method returns mathematical type for the inner most binding for the given variable name from the provided
     * scope.
     * </p>
     *
     * @param name
     *            A variable name.
     *
     * @return The {@link MTType} representation object.
     *
     * @throws NoSuchElementException
     *             We did not locate a {@link MTType} with that name.
     */
    private static MTType getInnermostBinding(Deque<Map<String, MTType>> scopes, String name)
            throws NoSuchElementException {
        MTType result = null;

        Iterator<Map<String, MTType>> scopesIter = scopes.iterator();
        while (result == null && scopesIter.hasNext()) {
            result = scopesIter.next().get(name);
        }

        if (result == null) {
            throw new NoSuchElementException(name);
        }

        return result;
    }

}
