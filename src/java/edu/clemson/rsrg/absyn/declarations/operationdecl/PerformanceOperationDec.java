/*
 * PerformanceOperationDec.java
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
package edu.clemson.rsrg.absyn.declarations.operationdecl;

import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ModuleParameter;

/**
 * <p>
 * This is the class for all the performance operation declaration objects that the compiler builds using the ANTLR4 AST
 * nodes.
 * </p>
 *
 * @version 2.0
 */
public class PerformanceOperationDec extends Dec implements ModuleParameter {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The wrapped operation declaration
     * </p>
     */
    private final OperationDec myWrappedOpDec;

    /**
     * <p>
     * The duration expression
     * </p>
     */
    private final AssertionClause myDuration;

    /**
     * <p>
     * The manip_disp expression
     * </p>
     */
    private final AssertionClause myManipDisp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a performance operation declaration.
     * </p>
     *
     * @param opDec
     *            An {@link OperationDec} containing all the operation related items.
     * @param duration
     *            A {@link AssertionClause} representing the performance operation's duration clause.
     * @param manip_disp
     *            A {@link AssertionClause} representing the performance operation's manipulation displacement clause.
     */
    public PerformanceOperationDec(OperationDec opDec, AssertionClause duration, AssertionClause manip_disp) {
        super(opDec.getLocation(), opDec.getName());
        myWrappedOpDec = opDec;
        myDuration = duration;
        myManipDisp = manip_disp;
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

        // operation declaration
        sb.append(myWrappedOpDec.asString(indentSize, innerIndentInc));
        sb.append("\n");

        // duration clause
        if (myDuration != null) {
            sb.append(myDuration.asString(indentSize + innerIndentInc, innerIndentInc));
            sb.append("\n");
        }

        // manip_disp clause
        if (myManipDisp != null) {
            sb.append(myManipDisp.asString(indentSize + innerIndentInc, innerIndentInc));
            sb.append("\n");
        }

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

        PerformanceOperationDec that = (PerformanceOperationDec) o;

        if (!myWrappedOpDec.equals(that.myWrappedOpDec))
            return false;
        if (myDuration != null ? !myDuration.equals(that.myDuration) : that.myDuration != null)
            return false;
        return myManipDisp != null ? myManipDisp.equals(that.myManipDisp) : that.myManipDisp == null;
    }

    /**
     * <p>
     * This method returns the duration clause for this performance operation declaration.
     * </p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getDuration() {
        return myDuration;
    }

    /**
     * <p>
     * This method returns the manipulation displacement clause for this performance operation declaration.
     * </p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getManipDisp() {
        return myManipDisp;
    }

    /**
     * <p>
     * This method returns the wrapped operation declaration for this performance operation declaration.
     * </p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final OperationDec getWrappedOpDec() {
        return myWrappedOpDec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myWrappedOpDec.hashCode();
        result = 31 * result + (myDuration != null ? myDuration.hashCode() : 0);
        result = 31 * result + (myManipDisp != null ? myManipDisp.hashCode() : 0);
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final PerformanceOperationDec copy() {
        AssertionClause newDuration = null;
        if (myDuration != null) {
            newDuration = myDuration.clone();
        }

        AssertionClause newManipDisp = null;
        if (myManipDisp != null) {
            newManipDisp = myManipDisp.clone();
        }

        return new PerformanceOperationDec((OperationDec) myWrappedOpDec.clone(), newDuration, newManipDisp);
    }

}
