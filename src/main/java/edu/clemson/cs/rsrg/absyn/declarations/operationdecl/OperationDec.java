/*
 * OperationDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.operationdecl;

import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameter;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the operation declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class OperationDec extends Dec implements ModuleParameter {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The list of parameter variable declarations</p> */
    private final List<ParameterVarDec> myParameters;

    /** <p>The type model for the return value.</p> */
    private final Ty myReturnTy;

    /** <p>The affects clause.</p> */
    private final AffectsClause myAffects;

    /** <p>The requires expression</p> */
    private final AssertionClause myRequires;

    /** <p>The ensures expression</p> */
    private final AssertionClause myEnsures;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an operation declaration.</p>
     *
     * @param name A {@link PosSymbol} representing the operation's
     *             name.
     * @param parameters A list of {@link ParameterVarDec} representing the operation's
     *                   parameter variables.
     * @param returnTy A {@link Ty} representing the operation's
     *                 return value type.
     * @param affects A {@link AffectsClause} representing the operation's
     *                affects clause.
     * @param requires A {@link AssertionClause} representing the operation's
     *                 requires clause.
     * @param ensures A {@link AssertionClause} representing the operation's
     *                ensures clause.
     */
    public OperationDec(PosSymbol name, List<ParameterVarDec> parameters,
            Ty returnTy, AffectsClause affects, AssertionClause requires,
            AssertionClause ensures) {
        super(name.getLocation(), name);
        myAffects = affects;
        myEnsures = ensures;
        myParameters = parameters;
        myRequires = requires;
        myReturnTy = returnTy;
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
        sb.append("Operation ");
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
            sb.append("\n");
        }

        // requires clause
        sb.append(myRequires.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append("\n");

        // ensures clause
        sb.append(myEnsures.asString(indentSize + innerIndentInc,
                innerIndentInc));

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

        OperationDec that = (OperationDec) o;

        if (!myParameters.equals(that.myParameters))
            return false;
        if (myReturnTy != null ? !myReturnTy.equals(that.myReturnTy)
                : that.myReturnTy != null)
            return false;
        if (myAffects != null ? !myAffects.equals(that.myAffects)
                : that.myAffects != null)
            return false;
        if (!myRequires.equals(that.myRequires))
            return false;
        return myEnsures.equals(that.myEnsures);
    }

    /**
     * <p>This method returns the affects clause
     * for this operation declaration.</p>
     *
     * @return The {@link AffectsClause} representation object.
     */
    public final AffectsClause getAffectedVars() {
        return myAffects;
    }

    /**
     * <p>This method returns the ensures clause
     * for this operation declaration.</p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getEnsures() {
        return myEnsures;
    }

    /**
     * <p>This method returns the parameter variable declarations
     * for this operation declaration.</p>
     *
     * @return A list of {@link ParameterVarDec} representation objects.
     */
    public final List<ParameterVarDec> getParameters() {
        return myParameters;
    }

    /**
     * <p>This method returns the requires clause
     * for this operation declaration.</p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getRequires() {
        return myRequires;
    }

    /**
     * <p>This method returns the raw return type
     * for this operation declaration.</p>
     *
     * @return The {@link Ty} representation object.
     */
    public final Ty getReturnTy() {
        return myReturnTy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myParameters.hashCode();
        result = 31 * result + (myReturnTy != null ? myReturnTy.hashCode() : 0);
        result = 31 * result + (myAffects != null ? myAffects.hashCode() : 0);
        result = 31 * result + myRequires.hashCode();
        result = 31 * result + myEnsures.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final OperationDec copy() {
        AffectsClause newAffects = null;
        if (myAffects != null) {
            newAffects = myAffects.clone();
        }

        List<ParameterVarDec> newParameters = new ArrayList<>();
        for (ParameterVarDec varDec : myParameters) {
            newParameters.add((ParameterVarDec) varDec.clone());
        }

        Ty newReturnTy = null;
        if (myReturnTy != null) {
            newReturnTy = myReturnTy.clone();
        }

        return new OperationDec(myName.clone(), newParameters, newReturnTy, newAffects, myRequires.clone(), myEnsures.clone());
    }
}