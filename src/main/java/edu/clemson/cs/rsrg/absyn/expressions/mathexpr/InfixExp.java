/*
 * InfixExp.java
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
 * <p>This is the class for all the mathematical infix expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class InfixExp extends AbstractFunctionExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression on the left hand side.</p> */
    protected final Exp myLeftHandSide;

    /** <p>The expression's operation.</p> */
    protected final PosSymbol myOperationName;

    /** <p>The expression on the right hand side.</p> */
    protected final Exp myRightHandSide;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an infix expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param left A {@link Exp} representing the left hand side.
     * @param opQual A {@link PosSymbol} representing the operator's qualifier.
     * @param opName A {@link PosSymbol} representing the operator.
     * @param right A {@link Exp} representing the right hand side.
     */
    public InfixExp(Location l, Exp left, PosSymbol opQual, PosSymbol opName,
            Exp right) {
        super(l, opQual);
        myLeftHandSide = left;
        myOperationName = opName;
        myRightHandSide = right;
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
        sb.append("(");
        sb.append(myLeftHandSide.asString(0, indentSize + innerIndentInc));

        sb.append(" ");
        if (myQualifier != null) {
            sb.append(myQualifier.asString(0, innerIndentInc));
            sb.append("::");
        }
        sb.append(myOperationName.asString(0, innerIndentInc));
        sb.append(" ");

        sb.append(myRightHandSide.asString(0, indentSize + innerIndentInc));
        sb.append(")");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Exp compareWithAssumptions(Exp exp) {
        Exp retExp;
        if (this.equals(exp)) {
            retExp = VarExp.getTrueVarExp(myLoc, myMathType.getTypeGraph());
        }
        else if (myOperationName.getName().equals("and")) {
            Exp newLeftSide = myLeftHandSide.compareWithAssumptions(exp);
            Exp newRightSide = myRightHandSide.compareWithAssumptions(exp);

            PosSymbol newOpQualifier = null;
            if (myQualifier != null) {
                newOpQualifier = myQualifier.clone();
            }

            retExp =
                    new InfixExp(cloneLocation(), newLeftSide, newOpQualifier,
                            myOperationName.clone(), newRightSide);
        }
        else {
            retExp = this.clone();
        }

        return retExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = myLeftHandSide.containsExp(exp);
        if (!found) {
            found = myRightHandSide.containsExp(exp);
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = myLeftHandSide.containsVar(varName, IsOldExp);
        if (!found) {
            found = myRightHandSide.containsVar(varName, IsOldExp);
        }

        return found;
    }

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

        InfixExp infixExp = (InfixExp) o;

        if (!myLeftHandSide.equals(infixExp.myLeftHandSide))
            return false;
        if (!myOperationName.equals(infixExp.myOperationName))
            return false;
        return myRightHandSide.equals(infixExp.myRightHandSide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equivalent(Exp e) {
        boolean retval = e instanceof InfixExp;
        if (retval) {
            InfixExp eAsInfix = (InfixExp) e;
            retval =
                    posSymbolEquivalent(myOperationName,
                            eAsInfix.myOperationName)
                            && myLeftHandSide
                                    .equivalent(eAsInfix.myLeftHandSide)
                            && myRightHandSide
                                    .equivalent(eAsInfix.myRightHandSide);
        }

        return retval;
    }

    /**
     * <p>This method returns the left hand side expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getLeft() {
        return myLeftHandSide;
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
     * <p>This method returns the right hand side expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getRight() {
        return myRightHandSide;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> subExps = new ArrayList<>();
        subExps.add(myLeftHandSide.clone());
        subExps.add(myRightHandSide.clone());

        return subExps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myLeftHandSide.hashCode();
        result = 31 * result + myOperationName.hashCode();
        result = 31 * result + myRightHandSide.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected Exp copy() {
        PosSymbol newOpQualifier = null;
        if (myQualifier != null) {
            newOpQualifier = myQualifier.clone();
        }

        return new InfixExp(cloneLocation(), myLeftHandSide, newOpQualifier,
                myOperationName.clone(), myRightHandSide.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        PosSymbol newOpQualifier = null;
        if (myQualifier != null) {
            newOpQualifier = myQualifier.clone();
        }

        return new InfixExp(cloneLocation(), substitute(myLeftHandSide,
                substitutions), newOpQualifier, myOperationName.clone(),
                substitute(myRightHandSide, substitutions));
    }

}