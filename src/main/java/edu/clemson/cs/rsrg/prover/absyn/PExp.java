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
import edu.clemson.cs.rsrg.prover.exception.BindingException;
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

    /** <p>The object's mathematical type.</p> */
    protected final MTType myMathType;

    /** <p>The object's mathematical type value.</p> */
    protected final MTType myMathTypeValue;

    /** <p>The expression's structure hash.</p> */
    protected final int myStructureHash;

    /** <p>The expression's value hash.</p> */
    protected final int myValueHash;

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
        myStructureHash = structureHash;
        myValueHash = valueHash;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

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