/**
 * ProcedureDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.operationdecl;

import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the procedure declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ProcedureDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The list of parameter variable declarations</p> */
    private final List<ParameterVarDec> myParameters;

    /** <p>The type model for the return value.</p> */
    private final Ty myReturnTy;

    /** <p>The affects clause.</p> */
    private final AffectsClause myAffects;

    /** <p>The decreasing expression</p> */
    private final AssertionClause myDecreasing;

    /** <p>The list of facility declarations</p> */
    private final List<FacilityDec> myFacilityDecs;

    /** <p>The list of variable declarations</p> */
    private final List<VarDec> myVariableDecs;

    /** <p>The list of statements</p> */
    private final List<Statement> myStatements;

    /** <p>Boolean indicating if this is a recursive procedure or not.</p> */
    private final boolean myRecursiveFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a recursive procedure declaration.</p>
     *
     * @param name A {@link PosSymbol} representing the procedure's
     *             name.
     * @param parameters A list of {@link ParameterVarDec} representing the procedure's
     *                   parameter variables.
     * @param returnTy A {@link Ty} representing the procedure's
     *                 return value type.
     * @param affects A {@link AffectsClause} representing the procedure's
     *                affects clause.
     * @param decreasing A {@link AssertionClause} representing the procedure's
     *                   decreasing clause.
     * @param facDecs A list of {@link FacilityDec} representing the procedure's
     *                facility declarations.
     * @param varDecs A list of {@link VarDec} representing the procedure's
     *                variables.
     * @param statements As list of {@link Statement} representing the procedure's
     *                   statements.
     * @param recursiveFlag A boolean indicating if this procedure is recursive or not.
     */
    public ProcedureDec(PosSymbol name, List<ParameterVarDec> parameters,
            Ty returnTy, AffectsClause affects, AssertionClause decreasing,
            List<FacilityDec> facDecs, List<VarDec> varDecs,
            List<Statement> statements, boolean recursiveFlag) {
        super(name.getLocation(), name);
        myAffects = affects;
        myDecreasing = decreasing;
        myFacilityDecs = facDecs;
        myParameters = parameters;
        myRecursiveFlag = recursiveFlag;
        myReturnTy = returnTy;
        myStatements = statements;
        myVariableDecs = varDecs;
    }

    /**
     * <p>This constructs a procedure declaration.</p>
     *
     * @param name A {@link PosSymbol} representing the procedure's
     *             name.
     * @param parameters A list of {@link ParameterVarDec} representing the procedure's
     *                   parameter variables.
     * @param returnTy A {@link Ty} representing the procedure's
     *                 return value type.
     * @param affects A {@link AffectsClause} representing the procedure's
     *                affects clause.
     * @param facDecs A list of {@link FacilityDec} representing the procedure's
     *                facility declarations.
     * @param varDecs A list of {@link VarDec} representing the procedure's
     *                variables.
     * @param statements As list of {@link Statement} representing the procedure's
     *                   statements.
     */
    public ProcedureDec(PosSymbol name, List<ParameterVarDec> parameters,
            Ty returnTy, AffectsClause affects, List<FacilityDec> facDecs,
            List<VarDec> varDecs, List<Statement> statements) {
        this(name, parameters, returnTy, affects, null, facDecs, varDecs,
                statements, false);
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
        if (myRecursiveFlag) {
            sb.append("Recursive ");
        }
        sb.append("Procedure ");
        sb.append(myName.asString(0, innerIndentInc));

        // parameters
        sb.append("( ");
        Iterator<ParameterVarDec> paraIt = myParameters.iterator();
        while (paraIt.hasNext()) {
            sb.append(paraIt.next().asString(0, innerIndentInc));

            if (paraIt.hasNext()) {
                sb.append("; ");
            }
        }
        sb.append(" )");

        // return value
        if (myReturnTy != null) {
            sb.append(" : ");
            sb.append(myReturnTy.asString(0, innerIndentInc));
        }

        sb.append(";\n");

        // affects clause
        if (myAffects != null) {
            sb.append(myAffects.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        // decreasing clause
        if (myDecreasing != null) {
            sb.append(myDecreasing.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        // facility declarations
        sb.append("\n");
        for (FacilityDec facilityDec : myFacilityDecs) {
            sb.append(facilityDec.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n");
        }

        // variable declarations
        sb.append("\n");
        for (VarDec varDec : myVariableDecs) {
            sb.append(varDec.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n");
        }

        // statements
        sb.append("\n");
        for (Statement statement : myStatements) {
            sb.append(statement.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n");
        }

        printSpace(indentSize, sb);
        sb.append("end\n");

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

        ProcedureDec that = (ProcedureDec) o;

        if (myRecursiveFlag != that.myRecursiveFlag)
            return false;
        if (!myParameters.equals(that.myParameters))
            return false;
        if (myReturnTy != null ? !myReturnTy.equals(that.myReturnTy)
                : that.myReturnTy != null)
            return false;
        if (myAffects != null ? !myAffects.equals(that.myAffects)
                : that.myAffects != null)
            return false;
        if (myDecreasing != null ? !myDecreasing.equals(that.myDecreasing)
                : that.myDecreasing != null)
            return false;
        if (!myFacilityDecs.equals(that.myFacilityDecs))
            return false;
        if (!myVariableDecs.equals(that.myVariableDecs))
            return false;
        return myStatements.equals(that.myStatements);

    }

    /**
     * <p>This method returns the affects clause
     * for this procedure declaration.</p>
     *
     * @return The {@link AffectsClause} representation object.
     */
    public final AffectsClause getAffectedVars() {
        return myAffects;
    }

    /**
     * <p>This method returns the decreasing clause
     * for this procedure declaration.</p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getDecreasing() {
        return myDecreasing;
    }

    /**
     * <p>This method returns the list of facility declarations
     * for this procedure declaration.</p>
     *
     * @return A list of {@link FacilityDec} representation objects.
     */
    public final List<FacilityDec> getFacilities() {
        return myFacilityDecs;
    }

    /**
     * <p>This method returns the parameter variable declarations
     * for this procedure declaration.</p>
     *
     * @return A list of {@link ParameterVarDec} representation objects.
     */
    public final List<ParameterVarDec> getParameters() {
        return myParameters;
    }

    /**
     * <p>This method returns whether or not this is an recursive procedure.</p>
     *
     * @return {@code true} if it is recursive, {@code false} otherwise.
     */
    public final boolean getRecursive() {
        return myRecursiveFlag;
    }

    /**
     * <p>This method returns the raw return type
     * for this procedure declaration.</p>
     *
     * @return The {@link Ty} representation object.
     */
    public final Ty getReturnTy() {
        return myReturnTy;
    }

    /**
     * <p>This method returns the list of statements in
     * for this procedure declaration.</p>
     *
     * @return The list of {@link Statement} representation objects.
     */
    public final List<Statement> getStatements() {
        return myStatements;
    }

    /**
     * <p>This method returns the list of variables
     * for this procedure declaration.</p>
     *
     * @return A list of {@link VarDec} representation objects.
     */
    public final List<VarDec> getVariables() {
        return myVariableDecs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myParameters.hashCode();
        result = 31 * result + (myReturnTy != null ? myReturnTy.hashCode() : 0);
        result = 31 * result + (myAffects != null ? myAffects.hashCode() : 0);
        result =
                31 * result
                        + (myDecreasing != null ? myDecreasing.hashCode() : 0);
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
    protected final ProcedureDec copy() {
        AffectsClause newAffects = null;
        if (myAffects != null) {
            newAffects = myAffects.clone();
        }

        AssertionClause newDecreasing = null;
        if (myDecreasing != null) {
            newDecreasing = myDecreasing.clone();
        }

        List<ParameterVarDec> newParameters = new ArrayList<>();
        for (ParameterVarDec varDec : myParameters) {
            newParameters.add((ParameterVarDec) varDec.clone());
        }

        Ty newReturnTy = null;
        if (myReturnTy != null) {
            newReturnTy = myReturnTy.clone();
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

        return new ProcedureDec(myName.clone(), newParameters, newReturnTy,
                newAffects, newDecreasing, newFacilityDecs, newVarDecs,
                newStatements, myRecursiveFlag);
    }
}