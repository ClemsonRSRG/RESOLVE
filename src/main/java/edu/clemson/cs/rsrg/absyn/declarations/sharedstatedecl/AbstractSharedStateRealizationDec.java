/*
 * AbstractSharedStateRealizationDec.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is the abstract base class for both the shared state realization and
 * facility shared state realization objects that the compiler builds
 * using the ANTLR4 AST nodes.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public abstract class AbstractSharedStateRealizationDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The list of programming variables for the new shared state.</p> */
    protected final List<VarDec> myStateVars;

    /** <p>The convention clause for the new shared state.</p> */
    protected final AssertionClause myConvention;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the name
     * and convention for any objects created from a class
     * that inherits from {@code AbstractSharedStateRealizationDec}.</p>
     *
     * @param name Name of the new shared state.
     * @param stateVarDecs The list of {@link VarDec}s that are in the new shared state.
     * @param convention Shared state convention.
     */
    protected AbstractSharedStateRealizationDec(PosSymbol name,
            List<VarDec> stateVarDecs, AssertionClause convention) {
        super(name.getLocation(), name);
        myStateVars = stateVarDecs;
        myConvention = convention;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        AbstractSharedStateRealizationDec that =
                (AbstractSharedStateRealizationDec) o;

        if (!myStateVars.equals(that.myStateVars))
            return false;
        return myConvention.equals(that.myConvention);
    }

    /**
     * <p>Returns the convention for this shared state.</p>
     *
     * @return The type convention in {@link AssertionClause} format.
     */
    public final AssertionClause getConvention() {
        return myConvention;
    }

    /**
     * <p>Returns the variables for this shared state.</p>
     *
     * @return The list of {@link VarDec} representations.
     */
    public final List<VarDec> getStateVars() {
        return myStateVars;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myStateVars.hashCode();
        result = 31 * result + myConvention.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by concrete subclasses of {@link AbstractSharedStateRealizationDec}
     * to manufacture a copy of themselves.</p>
     *
     * @return A new {@link AbstractSharedStateRealizationDec} that is a
     * deep copy of the original.
     */
    protected AbstractSharedStateRealizationDec copy() {
        throw new MiscErrorException("Shouldn't be calling copy()!  Type: "
                + this.getClass(), new CloneNotSupportedException());
    }

    /**
     * <p>An helper method to copy all the programming variables
     * in this shared state.</p>
     *
     * @return A deep copy of the list of {@link VarDec}s.
     */
    protected final List<VarDec> copyStateVars() {
        List<VarDec> newStateVars = new ArrayList<>();
        for (VarDec varDec : myStateVars) {
            newStateVars.add((VarDec) varDec.clone());
        }

        return newStateVars;
    }
}