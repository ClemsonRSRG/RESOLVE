/*
 * OperationProcedureDec.java
 * ---------------------------------
 * Copyright (c) 2021
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
import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.rsrg.absyn.statements.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This is the class for all the local operation procedure declaration objects that the compiler builds using the ANTLR4
 * AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class OperationProcedureDec extends Dec {

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
     * The decreasing expression
     * </p>
     */
    private final AssertionClause myDecreasing;

    /**
     * <p>
     * The list of facility declarations
     * </p>
     */
    private final List<FacilityDec> myFacilityDecs;

    /**
     * <p>
     * The list of variable declarations
     * </p>
     */
    private final List<VarDec> myVariableDecs;

    /**
     * <p>
     * The list of statements
     * </p>
     */
    private final List<Statement> myStatements;

    /**
     * <p>
     * Boolean indicating if this is a recursive procedure or not.
     * </p>
     */
    private final boolean myRecursiveFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a local recursive operation procedure declaration.
     * </p>
     *
     * @param opDec
     *            An {@link OperationDec} containing all the operation related items.
     * @param decreasing
     *            A {@link AssertionClause} representing the procedure's decreasing clause.
     * @param facDecs
     *            A list of {@link FacilityDec} representing the procedure's facility declarations.
     * @param varDecs
     *            A list of {@link VarDec} representing the procedure's variables.
     * @param statements
     *            As list of {@link Statement} representing the procedure's statements.
     * @param recursiveFlag
     *            A boolean indicating if this procedure is recursive or not.
     */
    public OperationProcedureDec(OperationDec opDec, AssertionClause decreasing, List<FacilityDec> facDecs,
            List<VarDec> varDecs, List<Statement> statements, boolean recursiveFlag) {
        super(opDec.getLocation(), opDec.getName());
        myDecreasing = decreasing;
        myFacilityDecs = facDecs;
        myRecursiveFlag = recursiveFlag;
        myStatements = statements;
        myVariableDecs = varDecs;
        myWrappedOpDec = opDec;
    }

    /**
     * <p>
     * This constructs a local operation procedure declaration.
     * </p>
     *
     * @param opDec
     *            An {@link OperationDec} containing all the operation related items.
     * @param facDecs
     *            A list of {@link FacilityDec} representing the procedure's facility declarations.
     * @param varDecs
     *            A list of {@link VarDec} representing the procedure's variables.
     * @param statements
     *            As list of {@link Statement} representing the procedure's statements.
     */
    public OperationProcedureDec(OperationDec opDec, List<FacilityDec> facDecs, List<VarDec> varDecs,
            List<Statement> statements) {
        this(opDec, null, facDecs, varDecs, statements, false);
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

        printSpace(indentSize, sb);
        if (myRecursiveFlag) {
            sb.append("Recursive ");
        }
        sb.append("Procedure\n");

        // decreasing clause
        if (myDecreasing != null) {
            sb.append(myDecreasing.asString(indentSize + innerIndentInc, innerIndentInc));
            sb.append("\n");
        }

        // facility declarations
        for (FacilityDec facilityDec : myFacilityDecs) {
            sb.append(facilityDec.asString(indentSize + innerIndentInc, innerIndentInc));
            sb.append("\n");
        }

        // variable declarations
        for (VarDec varDec : myVariableDecs) {
            printSpace(indentSize + innerIndentInc, sb);
            sb.append("Var ");
            sb.append(varDec.asString(0, innerIndentInc));
            sb.append(";\n");
        }

        // statements
        sb.append("\n");
        for (Statement statement : myStatements) {
            sb.append(statement.asString(indentSize + innerIndentInc, innerIndentInc));
            sb.append("\n");
        }

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

        OperationProcedureDec that = (OperationProcedureDec) o;

        if (myRecursiveFlag != that.myRecursiveFlag)
            return false;
        if (!myWrappedOpDec.equals(that.myWrappedOpDec))
            return false;
        if (myDecreasing != null ? !myDecreasing.equals(that.myDecreasing) : that.myDecreasing != null)
            return false;
        if (!myFacilityDecs.equals(that.myFacilityDecs))
            return false;
        if (!myVariableDecs.equals(that.myVariableDecs))
            return false;
        return myStatements.equals(that.myStatements);
    }

    /**
     * <p>
     * This method returns the decreasing clause for this operation procedure declaration.
     * </p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getDecreasing() {
        return myDecreasing;
    }

    /**
     * <p>
     * This method returns the list of facility declarations for this operation procedure declaration.
     * </p>
     *
     * @return A list of {@link FacilityDec} representation objects.
     */
    public final List<FacilityDec> getFacilities() {
        return myFacilityDecs;
    }

    /**
     * /**
     * <p>
     * This method returns whether or not this is an recursive procedure.
     * </p>
     *
     * @return {@code true} if it is recursive, {@code false} otherwise.
     */
    public final boolean getRecursive() {
        return myRecursiveFlag;
    }

    /**
     * <p>
     * This method returns the list of statements in for this operation procedure declaration.
     * </p>
     *
     * @return The list of {@link Statement} representation objects.
     */
    public final List<Statement> getStatements() {
        return myStatements;
    }

    /**
     * <p>
     * This method returns the list of variables for this operation procedure declaration.
     * </p>
     *
     * @return A list of {@link VarDec} representation objects.
     */
    public final List<VarDec> getVariables() {
        return myVariableDecs;
    }

    /**
     * <p>
     * This method returns the wrapped operation declaration for this operation procedure declaration.
     * </p>
     *
     * @return The {@link OperationDec} representation object.
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
        result = 31 * result + (myDecreasing != null ? myDecreasing.hashCode() : 0);
        result = 31 * result + myFacilityDecs.hashCode();
        result = 31 * result + myVariableDecs.hashCode();
        result = 31 * result + myStatements.hashCode();
        result = 31 * result + (myRecursiveFlag ? 1 : 0);
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final OperationProcedureDec copy() {
        AssertionClause newDecreasing = null;
        if (myDecreasing != null) {
            newDecreasing = myDecreasing.clone();
        }

        List<FacilityDec> newFacilityDecs = new ArrayList<>();
        for (FacilityDec f : myFacilityDecs) {
            newFacilityDecs.add((FacilityDec) f.clone());
        }

        List<VarDec> newVarDecs = new ArrayList<>();
        for (VarDec v : myVariableDecs) {
            newVarDecs.add((VarDec) v.clone());
        }

        List<Statement> newStatements = new ArrayList<>();
        for (Statement s : myStatements) {
            newStatements.add(s.clone());
        }

        return new OperationProcedureDec((OperationDec) myWrappedOpDec.clone(), newDecreasing, newFacilityDecs,
                newVarDecs, newStatements, myRecursiveFlag);
    }
}
