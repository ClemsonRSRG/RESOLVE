/**
 * BindingVisitor.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.typevisitor;

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.FinalizedScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class attempts to bind the concrete expression <code>t1</code> against
 * the template expression <code>t2</code>.</p>
 *
 * @version 2.0
 */
public class BindingVisitor extends SymmetricBoundVariableVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The current type graph object in use.</p> */
    private final TypeGraph myTypeGraph;

    /** <p>A map of current bindings.</p> */
    private Map<String, MTType> myBindings = new HashMap<>();

    /** <p>The result from the current binding.</p> */
    private boolean myMatchSoFarFlag = true;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a visitor with the type graph.</p>
     *
     * @param g The current type graph.
     */
    public BindingVisitor(TypeGraph g) {
        myTypeGraph = g;
    }

    /**
     * <p>This constructs a visitor with the type graph and a
     * finalized scope.</p>
     *
     * @param g The current type graph.
     * @param concreteContext A finalized scope.
     */
    public BindingVisitor(TypeGraph g, FinalizedScope concreteContext) {
        super(concreteContext);
        myTypeGraph = g;
    }

    /**
     * <p>This constructs a visitor with the type graph and a bounded
     * variable map.</p>
     *
     * @param g The current type graph.
     * @param concreteContext Bounded variables map.
     */
    public BindingVisitor(TypeGraph g, Map<String, MTType> concreteContext) {
        super(concreteContext);
        myTypeGraph = g;
    }

    /**
     * <p>This constructs a visitor with the type graph, a bounded variable map
     * and a template variable map.</p>
     *
     * @param g The current type graph.
     * @param concreteContext Bounded variables map.
     * @param templateContext Template variables map.
     */
    public BindingVisitor(TypeGraph g, Map<String, MTType> concreteContext, Map<String, MTType> templateContext) {
        super(concreteContext, templateContext);
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTNamed} by attempting to bind <code>t1</code>
     * to <code>t2</code>.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The binding result.
     */
    @Override
    public final boolean beginMTNamed(MTNamed t1, MTNamed t2) {
        MTType t1DeclaredType = getInnermostBinding1(t1.getName());
        MTType t2DeclaredType = getInnermostBinding2(t2.getName());

        if (myBindings.containsKey(t2.getName())) {
            t1DeclaredType = myBindings.get(t2.getName());
        }

        //Fine if the declared type of t1 restricts the declared type of t2
        myMatchSoFarFlag &=
                myTypeGraph.isSubtype(t1DeclaredType, t2DeclaredType);

        if (!myBindings.containsKey(t2.getName()) && myMatchSoFarFlag) {
            myBindings.put(t2.getName(), t1);
        }

        //No need to keep searching if we've already found we don't bind
        return myMatchSoFarFlag;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTProper} by checking if <code>t1</code>
     * is equal to <code>t2</code>.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The result from the check.
     */
    @Override
    public final boolean beginMTProper(MTProper t1, MTProper t2) {
        myMatchSoFarFlag &= t1.equals(t2);

        //No need to keep searching if we've already found we don't bind
        return myMatchSoFarFlag;
    }

    /**
     * <p>This method returns the current final binding result
     * between two {@link MTType MTTypes}.</p>
     *
     * @return {@code true} if we have successfully bound <code>t1</code>
     * to <code>t2</code>, {@code false} otherwise.
     */
    public final boolean binds() {
        return myMatchSoFarFlag;
    }

    /**
     * <p>This method provides logic for handling type mismatches.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The updated result from attempting to handle the mismatch
     * types.
     */
    @Override
    public final boolean mismatch(MTType t1, MTType t2) {
        //This is fine if t1 names a type of which t2 is a supertype
        if (t2 instanceof MTNamed) {
            String t2Name = ((MTNamed) t2).getName();
            MTType t2DeclaredType = getInnermostBinding2(t2Name);

            if (myBindings.containsKey(((MTNamed) t2).getName())) {
                t1 = myBindings.get(((MTNamed) t2).getName());
            }

            myMatchSoFarFlag &= myTypeGraph.isSubtype(t1, t2DeclaredType);

            if (!myBindings.containsKey(((MTNamed) t2).getName()) && myMatchSoFarFlag) {
                myBindings.put(t2Name, t1);
            }
        }
        else if (t1 instanceof MTBigUnion) {
            //So long as the inner expression binds, this is ok
            myMatchSoFarFlag = visit(((MTBigUnion) t1).getExpression(), t2);
        }
        else if (t2 instanceof MTBigUnion) {
            //So long as the inner expression binds, this is ok
            myMatchSoFarFlag = visit(t1, ((MTBigUnion) t2).getExpression());
        }
        else {
            myMatchSoFarFlag = false;
        }

        //No need to keep searching if we've already found we don't bind
        return myMatchSoFarFlag;
    }

    /**
     * <p>This method returns the current type bindings map.</p>
     *
     * @return A map of type bindings.
     */
    public final Map<String, MTType> getBindings() {
        return myBindings;
    }

}