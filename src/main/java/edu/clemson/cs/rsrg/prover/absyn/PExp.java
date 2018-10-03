/*
 * PExp.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.prover.absyn;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.prover.absyn.expressions.PSymbol;
import edu.clemson.cs.rsrg.prover.absyn.treewalkers.PExpVisitor;
import edu.clemson.cs.rsrg.prover.exception.BindingException;
import edu.clemson.cs.rsrg.prover.immutableadts.ImmutableList;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.*;

/**
 * <p>This class represents the root of the prover abstract syntax tree (AST) hierarchy.</p>
 *
 * <p>{@code PExp} is the root of the prover abstract syntax tree
 * hierarchy. Unlike {@link Exp Exp}s, {@code PExp}s are immutable and
 * exist without the complications introduced by control structures.
 * {@code PExp}s exist to represent <em>only</em> mathematical expressions.</p>
 *
 * @author Hampton Smith
 * @author Mike Kabbani
 * @version 2.0
 */
public abstract class PExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>An instance of a binding exception.</p> */
    protected final static BindingException BINDING_EXCEPTION =
            new BindingException();

    /** <p>The expression's cached symbol names.</p> */
    private Set<String> myCachedSymbolNames = null;

    /** <p>The expression's cached function applications.</p> */
    private List<PExp> myCachedFunctionApplications = null;

    /** <p>The expression's cached quantified variable symbols.</p> */
    private Set<PSymbol> myCachedQuantifiedVariables = null;

    /** <p>The expression's mathematical type.</p> */
    protected final MTType myMathType;

    /** <p>The expression's mathematical type value.</p> */
    protected final MTType myMathTypeValue;

    /** <p>The expression's structure hash.</p> */
    public final int structureHash;

    /** <p>The expression's value hash.</p> */
    public final int valueHash;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the calculated
     * hash values and mathematical type information for
     * objects created from a class that inherits from
     * {@code PExp}.</p>
     *
     * @param hashes An helper object that contains the structure hash and
     *               value hash.
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     */
    protected PExp(HashDuple hashes, MTType type, MTType typeValue) {
        this(hashes.structureHash, hashes.valueHash, type, typeValue);
    }

    /**
     * <p>An helper constructor that allow us to store the calculated
     * hash values and mathematical type information for
     * objects created from a class that inherits from
     * {@code PExp}.</p>
     *
     * @param structureHash The expression's structure hash
     * @param valueHash The expression's value hash.
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     */
    protected PExp(int structureHash, int valueHash, MTType type,
            MTType typeValue) {
        myMathType = type;
        myMathTypeValue = typeValue;

        this.structureHash = structureHash;
        this.valueHash = valueHash;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method is the {@code accept()} method in a visitor pattern
     * for invoking an instance of {@link PExpVisitor}.</p>
     *
     * @param v A visitor for {@link PExp PExps}.
     */
    public abstract void accept(PExpVisitor v);

    /**
     * <p>This method must be implemented by all inherited classes
     * to override the default equals method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * <p>This method gets the mathematical type associated
     * with this expression.</p>
     *
     * @return A {@link MTType} type object.
     */
    public final MTType getMathType() {
        return myMathType;
    }

    /**
     * <p>This method gets the mathematical type value associated
     * with this expression.</p>
     *
     * @return A {@link MTType} type object.
     */
    public final MTType getMathTypeValue() {
        return myMathTypeValue;
    }

    /**
     * <p>This method returns the list of sub-expressions.</p>
     *
     * @return An immutable list containing {@link PExp} expressions.
     */
    public abstract ImmutableList<PExp> getSubExpressions();

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        return valueHash;
    }

    /**
     * <p>This method checks to see if this expression is obviously
     * equivalent to {@code true}.</p>
     *
     * @return {@code true} if it is obviously equivalent to mathematical
     * {@code true} expression, {@code false} otherwise.
     */
    public abstract boolean isObviouslyTrue();

    /**
     * <p>This method checks to see if this expression represents
     * a variable.</p>
     *
     * @return {@code true} if it is a variable expression,
     * {@code false} otherwise.
     */
    public abstract boolean isVariable();

    /**
     * <p>This method returns a DEEP COPY of this expression, with all instances of
     * {@link PExp PExps} that occur as keys in {@code substitutions}
     * replaced with their corresponding values.</p>
     *
     * @param substitutions A mapping from {@link PExp PExps} that should be
     *                      substituted out to the {@link PExp PExps} that should
     *                      replace them.
     *
     * @return A new {@link PExp} that is a deep copy of the original with
     *         the provided substitutions made.
     */
    public abstract PExp substitute(Map<PExp, PExp> substitutions);

    /**
     * <p>This method attempts to replace an argument at the specified
     * index.</p>
     *
     * @param index Index to an argument.
     * @param e The {@link PExp} to replace the one in our argument list.
     *
     * @return A new {@link PExp} with the expression at the specified index
     * replaced with {@code e}.
     */
    public abstract PExp withSubExpressionReplaced(int index, PExp e);

    /**
     * <p>This method returns a new expression with the mathematical type
     * replaced.</p>
     *
     * @param t A new mathematical type.
     *
     * @return A new {@link PExp} with {@code t} as its mathematical type.
     */
    public abstract PExp withTypeReplaced(MTType t);

    /**
     * <p>This method returns a new expression with the mathematical type
     * value replaced.</p>
     *
     * @param t A new mathematical type value.
     *
     * @return A new {@link PExp} with {@code t} as its mathematical type value.
     */
    public abstract PExp withTypeValueReplaced(MTType t);

    // ===========================================================
    // Protected Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>An helper construct for storing the structure hash and
     * value hash for a {@code PExp}.</p>
     */
    protected static class HashDuple {

        /** <p>The structure hash.</p> */
        public int structureHash;

        /** <p>The value hash.</p> */
        public int valueHash;

        /**
         * <p>This creates a duple with the two hash values.</p>
         *
         * @param structureHash A structure hash.
         * @param valueHash A value hash.
         */
        public HashDuple(int structureHash, int valueHash) {
            this.structureHash = structureHash;
            this.valueHash = valueHash;
        }
    }

}