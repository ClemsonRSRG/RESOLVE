/*
 * SharedStateDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.items.mathitems.SpecInitFinalItem;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This is the class for all the shared state declaration objects that the
 * compiler builds using the
 * ANTLR4 AST nodes.
 * </p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SharedStateDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The list of abstract state variables for the new shared state.
     * </p>
     */
    private final List<MathVarDec> myAbstractStateVars;

    /**
     * <p>
     * The constraint clause for the new shared state.
     * </p>
     */
    private final AssertionClause myConstraint;

    /**
     * <p>
     * The initialization block for the new shared state.
     * </p>
     */
    private final SpecInitFinalItem myStateInitItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a shared state declaration.
     * </p>
     *
     * @param name Name of the new shared state.
     * @param abstractStateVars List of abstract state variables.
     * @param constraint Shared state constraint.
     * @param initItem Initialization information for verification.
     */
    public SharedStateDec(PosSymbol name, List<MathVarDec> abstractStateVars,
            AssertionClause constraint, SpecInitFinalItem initItem) {
        super(name.getLocation(), name);
        myAbstractStateVars = abstractStateVars;
        myConstraint = constraint;
        myStateInitItem = initItem;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append(myName.asString(0, innerIndentInc));
        sb.append("\n");

        // List of abstract state vars
        for (MathVarDec varDec : myAbstractStateVars) {
            printSpace(indentSize + innerIndentInc, sb);
            sb.append("Abstract_Var ");
            sb.append(varDec.asString(0, innerIndentInc));
            sb.append(";\n");
        }

        // Constraint
        sb.append(myConstraint.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append("\n");

        // Init/Final
        sb.append(myStateInitItem.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append("\n");

        printSpace(indentSize, sb);
        sb.append("end ");
        sb.append(myName.asString(0, innerIndentInc));
        sb.append(";");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        SharedStateDec that = (SharedStateDec) o;

        if (!myAbstractStateVars.equals(that.myAbstractStateVars))
            return false;
        if (!myConstraint.equals(that.myConstraint))
            return false;
        return myStateInitItem.equals(that.myStateInitItem);
    }

    /**
     * <p>
     * Returns the type constraint for this shared state.
     * </p>
     *
     * @return The shared state constraint in {@link AssertionClause} format.
     */
    public final AssertionClause getConstraint() {
        return myConstraint;
    }

    /**
     * <p>
     * Returns the initialization block for this shared state.
     * </p>
     *
     * @return All relevant verification for initialization in
     *         {@link SpecInitFinalItem} format.
     */
    public final SpecInitFinalItem getInitialization() {
        return myStateInitItem;
    }

    /**
     * <p>
     * Returns the list of abstract state variables for this shared state.
     * </p>
     *
     * @return List of {@link MathVarDec}s.
     */
    public final List<MathVarDec> getAbstractStateVars() {
        return myAbstractStateVars;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myAbstractStateVars.hashCode();
        result = 31 * result + myConstraint.hashCode();
        result = 31 * result + myStateInitItem.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final SharedStateDec copy() {
        List<MathVarDec> newAbstractStateVars = new ArrayList<>();
        for (MathVarDec varDec : myAbstractStateVars) {
            newAbstractStateVars.add((MathVarDec) varDec.clone());
        }

        return new SharedStateDec(myName.clone(), newAbstractStateVars,
                myConstraint.clone(), myStateInitItem.clone());
    }
}
