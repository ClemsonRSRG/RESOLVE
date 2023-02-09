/*
 * SyntacticSubtypeChecker.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.typevisitor;

import edu.clemson.rsrg.typeandpopulate.exception.TypeMismatchException;
import edu.clemson.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.rsrg.typeandpopulate.symboltables.FinalizedScope;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * <p>
 * A <em>syntactic subtype</em> refers to a type that can be demonstrated as a (non-strict) subtype of some other type
 * using only syntactic information. Specifically, without recourse to type theorems. The syntactic subtype relationship
 * thus completely captures all hard-coded type relationship information.
 * </p>
 *
 * <p>
 * This class implements a check for a syntactic subtype relationship as a symmetric visitor. To check if
 * <code>t1</code> is a syntactic subtype of <code>t2</code>, use code like this:
 * </p>
 *
 * <pre>
 * SyntacticSubtypeChecker checker = new SyntacticSubtypeChecker(typeGraph);
 * try {
 *     checker.visit(t1, t2);
 *     // Stuff to do if t1 is a syntactic subtype of t2
 * } catch (IllegalArgumentException e) {
 *     TypeMismatchException mismatch = (TypeMismatchException) e.getCause();
 *     // Stuff to do if t1 is NOT a syntactic subtype of t2
 * }
 * </pre>
 *
 * <p>
 * As shown, the <code>visit()</code> method of a <code>SyntacticSubtypeChecker</code> will exit normally if two types
 * have a syntactic subtype relationship, or throw an <code>IllegalArgumentException</code> if they do not. The
 * <code>IllegalArgumentException</code> will wrap a <code>TypeMismatchException</code> describing the specific problem.
 * </p>
 *
 * <p>
 * As the checker descends the type trees in parallel, it keeps a record of actual component types from <code>t1</code>
 * that were matched with some quantified type-variable from <code>t2</code>. After a successful check, this record can
 * be accessed via <code>getBindings()</code>.
 * </p>
 *
 * <p>
 * At this time, the following syntactic relationships are recognized as forming a syntactic subtype relationship:
 * </p>
 *
 * <ul>
 * <li>Any type is a syntactic subtype of both <strong>MType</strong> and <strong>Entity</strong> (which is a superset
 * of <strong>MType</strong>).</li>
 * <li><strong>Empty_Set</strong> is a subtype of all types, including itself.</li>
 * <li>Any type is a syntactic subtype of itself or a type that is <em>alpha equivalent</em> to itself.</li>
 * <li><code>t1</code> is a syntactic subtype of <code>t2</code> if <code>BigUnion{unique_var_name_1 : MType,
 *     ... unique_var_name_n : MType}{t1}</code> (for some {@code n > 0}, where each <code>unique_var_name</code> does
 * not appear in <code>t1</code>) is a syntactic subtype of <code>t2</code>.</li>
 * <li>
 *
 * <pre>
 * BigUnion{t1 : (T1 : Power(MType)),
 *          t2 : (T2 : Power(MType)),
 *                ...
 *          tn : (Tn : Power(MType))}
 *         {t_type_valued_expression}
 * </pre>
 *
 * Is a syntactic subtype of:
 *
 * <pre>
 * BigUnion{r1 : (R1 : Power(MType)),
 *          r2 : (R2 : Power(MType)),
 *                ...
 *          rk : (rk : Power(MType))}
 *         {r_type_valued_expression}
 * </pre>
 *
 * If <code>n &lt; k</code> and there is some valuation of a (non-strict) subset of the <code>r</code>s and some
 * restriction (to syntactic subtypes) of the <code>R</code>s not associated with <code>r</code>s in the valuation
 * subset such that <code>r_type_valued_expression</code> becomes alpha-equivalent to
 * <code>t_type_valued_expression</code>.</li>
 * </ul>
 *
 * <p>
 * <strong>Note:</strong> Currently we do not deal correctly with types where the same quantified variable appears
 * multiple times in the template, e.g., "<code>BigUnion{t : MType}{t union t}</code>". This is coded defensively and
 * will throw a <code>RuntimeException</code> if it occurs.
 * </p>
 *
 * @version 2.0
 */
public class SyntacticSubtypeChecker extends SymmetricBoundVariableVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Exception to be thrown when there is a mismatch.
     * </p>
     */
    private static final IllegalArgumentException MISMATCH = new IllegalArgumentException(
            new TypeMismatchException(""));

    /**
     * <p>
     * A map of current bindings.
     * </p>
     */
    private Map<String, MTType> myBindings = new HashMap<>();

    /**
     * <p>
     * The current type graph object in use.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a visitor with the type graph.
     * </p>
     *
     * @param g
     *            The current type graph.
     */
    public SyntacticSubtypeChecker(TypeGraph g) {
        myTypeGraph = g;
    }

    /**
     * <p>
     * This constructs a visitor with the type graph and a finalized scope.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param context1
     *            A finalized scope.
     */
    public SyntacticSubtypeChecker(TypeGraph g, FinalizedScope context1) {
        super(context1);
        myTypeGraph = g;
    }

    /**
     * <p>
     * This constructs a visitor with the type graph and a bounded variable map.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param context1
     *            Bounded variables map.
     */
    public SyntacticSubtypeChecker(TypeGraph g, Map<String, MTType> context1) {
        super(context1);
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method adds additional logic before we visit two {@link MTFunctionApplication} by attempting to checking if
     * <code>t1's</code> function name is equal to <code>t2's</code> name.
     * </p>
     *
     * <p>
     * If it is not the same, then we have a mismatch error.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return This method always returns true if it is not a mismatch type.
     */
    @Override
    public final boolean beginMTFunctionApplication(MTFunctionApplication t1, MTFunctionApplication t2) {
        if (!t1.getName().equals(t2.getName())) {
            throw MISMATCH;
        }

        return true;
    }

    /**
     * <p>
     * This method adds additional logic before we visit two {@link MTType} by attempting to checking if <code>t1</code>
     * is a subtype of <code>t2</code>.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return The subtype check result.
     */
    @Override
    public final boolean beginMTType(MTType t1, MTType t2) {
        // Alpha-equivalent types are definitely syntactic subtypes. No need
        // to descend
        return !t1.equals(t2);
    }

    /**
     * <p>
     * This method adds additional logic before we visit two {@link MTNamed} by attempting to checking if
     * <code>t1</code> is a subtype of <code>t2</code>.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return The subtype check result.
     */
    @Override
    public final boolean beginMTNamed(MTNamed t1, MTNamed t2) {
        if (!t1.getName().equals(t2.getName())) {
            if (getInnermostBinding2(t2.getName()).equals(myTypeGraph.CLS)) {
                bind(t2.getName(), t1);
            } else {
                MTType t1DeclaredType = t1;
                MTType t2DeclaredType = t2;
                try {
                    t1DeclaredType = getInnermostBinding1(t1.getName());
                } catch (NoSuchElementException nsee) {

                }

                try {
                    t2DeclaredType = getInnermostBinding2(t2.getName());
                } catch (NoSuchElementException nsee) {

                }

                if (t1DeclaredType == t1 && t2DeclaredType == t2) {
                    // We have no information on these named types, but they don't
                    // share a name, so...
                    throw MISMATCH;
                }

                if (!haveAxiomaticSubtypeRelationship(t1DeclaredType, t2DeclaredType)) {
                    // This is fine if the declared type of t1 is a syntactic subtype
                    // of the declared type of t2
                    visit(t1DeclaredType, t2DeclaredType);
                }
            }
        }

        return true; // Keep searching siblings
    }

    /**
     * <p>
     * This method adds additional logic before we visit two {@link MTSetRestriction}.
     * </p>
     *
     * <p>
     * Currently this feature is not implemented and will always throw an exception.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return This method always throws a <code>Mismatch</code> exception.
     */
    @Override
    public final boolean beginMTSetRestriction(MTSetRestriction t1, MTSetRestriction t2) {
        // TODO:
        // For the moment, there's no obvious way to do this. We'll just say no
        // set restriction can be a syntactic subtype of any other.
        throw MISMATCH;
    }

    /**
     * <p>
     * This method adds additional logic before we visit two {@link MTProper} by checking if <code>t1</code> can be
     * established to be a subtype of <code>t2</code>.
     * </p>
     *
     * <p>
     * If we cannot establish a relationship, then we have a mismatch error.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return This method always returns true if it is not a mismatch type.
     */
    @Override
    public final boolean beginMTProper(MTProper t1, MTProper t2) {
        if (!(t1 == t2 || haveAxiomaticSubtypeRelationship(t1, t2))) {
            throw MISMATCH;
        }

        return true;
    }

    /**
     * <p>
     * This method returns the current type bindings map.
     * </p>
     *
     * @return A map of type bindings.
     */
    public final Map<String, MTType> getBindings() {
        return myBindings;
    }

    /**
     * <p>
     * This method provides logic for handling type mismatches.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return The updated result from attempting to handle the mismatch types.
     */
    @Override
    public final boolean mismatch(MTType t1, MTType t2) {
        // Note it's possible that t1 and t2 could both be MTBigUnion, even
        // though we're in mismatch() because they could have a different number
        // of quantified subtypes.
        if (t2 instanceof MTBigUnion && !(t1 instanceof MTBigUnion)) {
            // This may be ok, since we can wrap any expression in a trivial
            // big union
            MTBigUnion t2AsMTBigUnion = (MTBigUnion) t2;
            int quantifiedVariableCount = t2AsMTBigUnion.getQuantifiedVariables().size();

            t1 = new MTBigUnion(t1.getTypeGraph(), quantifiedVariableCount, t1);

            visit(t1, t2);
        } else if (t2 instanceof MTNamed && getInnermostBinding2(((MTNamed) t2).getName()).equals(myTypeGraph.CLS)) {

            bind(((MTNamed) t2).getName(), t1);
        } else if (haveAxiomaticSubtypeRelationship(t1, t2)) {
            // We're a syntactic subtype, so we don't need to do anything
        } else {
            // Otherwise, there's no way to continue, so we bomb
            throw MISMATCH;
        }

        return true; // Keep searching siblings
    }

    /**
     * <p>
     * Resets a checker so that it is prepared to check a new pair of types.
     * </p>
     */
    @Override
    public final void reset() {
        super.reset();
        myBindings.clear();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This method adds a new binding to our bindings map.
     * </p>
     *
     * @param name
     *            A variable name.
     * @param type
     *            A math type.
     */
    private void bind(String name, MTType type) {
        if (myBindings.containsKey(name)) {
            throw new RuntimeException("Duplicate quantified variable name: " + name);
        }

        myBindings.put(name, type);
    }

    /**
     * <p>
     * This method checks to see if there is already pre-established subtype relationship from our math universe.
     * </p>
     *
     * @param subtype
     *            A math subtype.
     * @param supertype
     *            A math supertype.
     *
     * @return {@code true} if it is an axiomatic subtype, {@code false} otherwise.
     */
    private boolean haveAxiomaticSubtypeRelationship(MTType subtype, MTType supertype) {
        // Respectively, here: EMPTY_SET is a subtype of everything, everything
        // is a subtype of CLS, and everything is a subtype of ENTITY.

        return subtype.equals(myTypeGraph.EMPTY_SET) || supertype.equals(myTypeGraph.CLS)
                || supertype.equals(myTypeGraph.ENTITY);
    }

}
