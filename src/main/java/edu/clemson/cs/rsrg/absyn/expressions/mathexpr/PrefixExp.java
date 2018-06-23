/*
 * PrefixExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the abstract base class for all the mathematical prefix expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class PrefixExp extends AbstractFunctionExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's operation.</p> */
    private final PosSymbol myOperationName;

    /** <p>The argument expression.</p> */
    private final Exp myArgument;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an infix expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param opQual A {@link PosSymbol} representing the operator's qualifier.
     * @param opName A {@link PosSymbol} representing the operator.
     * @param argument A {@link Exp} representing the right hand side.
     */
    public PrefixExp(Location l, PosSymbol opQual, PosSymbol opName,
            Exp argument) {
        super(l, opQual);
        myOperationName = opName;
        myArgument = argument;
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

        sb.append(myOperationName.asString(indentSize, innerIndentInc));
        sb.append("(");
        sb.append(myArgument.asString(0, innerIndentInc));
        sb.append(")");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        return myArgument.containsExp(exp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        return myArgument.containsVar(varName, IsOldExp);
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

        PrefixExp prefixExp = (PrefixExp) o;

        if (!myOperationName.equals(prefixExp.myOperationName))
            return false;
        return myArgument.equals(prefixExp.myArgument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = (e instanceof PrefixExp);

        if (retval) {
            PrefixExp eAsPrefixExp = (PrefixExp) e;
            retval =
                    Exp.posSymbolEquivalent(myOperationName,
                            eAsPrefixExp.myOperationName)
                            && Exp.equivalent(myArgument,
                                    eAsPrefixExp.myArgument);
        }

        return retval;
    }

    /**
     * <p>This method returns the argument expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getArgument() {
        return myArgument;
    }

    /**
     * <p>This method returns the operator name.</p>
     *
     * @return A {@link PosSymbol} object containing the operator.
     */
    @Override
    public final PosSymbol getOperatorAsPosSymbol() {
        return myOperationName;
    }

    /**
     * <p>This method returns the operator name in string format.</p>
     *
     * @return The operator as a string.
     */
    @Override
    public final String getOperatorAsString() {
        return myOperationName.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> subExps = new ArrayList<>();
        subExps.add(myArgument);

        return subExps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myOperationName.hashCode();
        result = 31 * result + myArgument.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new PrefixExp(cloneLocation(), qualifier, myOperationName
                .clone(), myArgument.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new PrefixExp(cloneLocation(), qualifier, myOperationName
                .clone(), substitute(myArgument, substitutions));
    }

}