/*
 * MathTypeTheoremDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.mathdecl;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.InfixExp;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the mathematical type theorem declarations
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class MathTypeTheoremDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The list of universally quantified variable declarations</p> */
    private final List<MathVarDec> myUniversalVars;

    /** <p>The assertion that applies to all variables in this type theorem</p> */
    private final Exp myAssertion;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a mathematical type theorem.</p>
     *
     * @param name Name of the type theorem.
     * @param universalVars List of universally quantified variables for this type theorem.
     * @param assertion The expression that relates all variables in this type theorem.
     */
    public MathTypeTheoremDec(PosSymbol name, List<MathVarDec> universalVars,
            Exp assertion) {
        super(name.getLocation(), name);
        myUniversalVars = universalVars;
        myAssertion = assertion;
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
        sb.append("Type Theorem ");
        sb.append(myName.asString(0, innerIndentInc));
        sb.append(":\n");

        Iterator<MathVarDec> it = myUniversalVars.iterator();
        while (it.hasNext()) {
            printSpace(indentSize + innerIndentInc, sb);
            sb.append("For all ");
            sb.append(it.next().asString(0, innerIndentInc));

            if (it.hasNext()) {
                sb.append(",\n");
            }
        }
        sb.append("\n");

        sb.append(myAssertion.asString(indentSize + innerIndentInc
                + innerIndentInc, innerIndentInc));
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

        MathTypeTheoremDec that = (MathTypeTheoremDec) o;

        if (!myUniversalVars.equals(that.myUniversalVars))
            return false;
        return myAssertion.equals(that.myAssertion);
    }

    /**
     * <p>This method returns all universally quantified math variables in
     * this type theorem.</p>
     *
     * @return A list of {@link MathVarDec} representation objects.
     */
    public final List<MathVarDec> getUniversalVars() {
        return myUniversalVars;
    }

    /**
     * <p>This method returns the assertion that relates the variables
     * this type theorem.</p>
     *
     * @return An {@link Exp} representation object.
     */
    public final Exp getAssertion() {
        return myAssertion;
    }

    /**
     * <p>This method checks to see if the assertion we have is a binding
     * condition.</p>
     *
     * @return {@code true} if the assertion is an {@code implies}, {@code false} otherwise.
     */
    public final boolean hasBindingCondition() {
        return myAssertion instanceof InfixExp
                && ((InfixExp) myAssertion).getOperatorAsString().equals(
                        "implies");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myUniversalVars.hashCode();
        result = 31 * result + myAssertion.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final MathTypeTheoremDec copy() {
        List<MathVarDec> newVars = new ArrayList<>();
        for (MathVarDec varDec : myUniversalVars) {
            newVars.add((MathVarDec) varDec.clone());
        }

        return new MathTypeTheoremDec(myName.clone(), newVars, myAssertion.clone());
    }
}