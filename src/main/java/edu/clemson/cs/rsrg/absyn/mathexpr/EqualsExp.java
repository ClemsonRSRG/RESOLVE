/**
 * EqualsExp.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.mathexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical equality/inequality expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class EqualsExp extends AbstractFunctionExp {

    // ===========================================================
    // Operators
    // ===========================================================

    public enum Operator {
        EQUAL {

            @Override
            public String toString() {
                return "=";
            }
        },
        NOT_EQUAL {

            @Override
            public String toString() {
                return "/=";
            }
        }
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression on the left hand side.</p> */
    private final Exp myLeftHandSide;

    /** <p>The expression's operation.</p> */
    private final Operator myOperator;

    /** <p>The expression on the right hand side.</p> */
    private final Exp myRightHandSide;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an equality/inequality expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     * @param left A {@link Exp} representing the left hand side.
     * @param operator A {@link Operator} representing the operator.
     * @param right A {@link Exp} representing the right hand side.
     */
    public EqualsExp(Location l, PosSymbol qual, Exp left, Operator operator,
            Exp right) {
        super(l, qual);
        myLeftHandSide = left;
        myOperator = operator;
        myRightHandSide = right;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("EqualsExp\n");

        if (myLeftHandSide != null) {
            sb.append(myLeftHandSide.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        printSpace(indentSize + innerIndentSize, sb);
        sb.append(myOperator.name());
        sb.append("\n");

        if (myRightHandSide != null) {
            sb.append(myRightHandSide.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method attempts to find the provided expression in our
     * subexpressions.</p>
     *
     * @param exp The expression we wish to locate.
     *
     * @return True if there is an instance of <code>exp</code>
     * within this object's subexpressions. False otherwise.
     */
    @Override
    public boolean containsExp(Exp exp) {
        boolean found = myLeftHandSide.containsExp(exp);
        if (!found) {
            found = myRightHandSide.containsExp(exp);
        }

        return found;
    }

    /**
     *  <p>This method attempts to find an expression with the given name in our
     * subexpressions.</p>
     *
     * @param varName Expression name.
     * @param IsOldExp Flag to indicate if the given name is of the form
     *                 "#[varName]"
     *
     * @return True if there is a {@link Exp} within this object's
     * subexpressions that matches <code>varName</code>. False otherwise.
     */
    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = myLeftHandSide.containsVar(varName, IsOldExp);
        if (!found) {
            found = myRightHandSide.containsVar(varName, IsOldExp);
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link EqualsExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof EqualsExp) {
            EqualsExp eAsEqualsExp = (EqualsExp) o;
            result = myLoc.equals(eAsEqualsExp.myLoc);

            if (result) {
                result =
                        (myOperator == eAsEqualsExp.myOperator)
                                && myLeftHandSide
                                        .equals(eAsEqualsExp.myLeftHandSide)
                                && myRightHandSide
                                        .equals(eAsEqualsExp.myRightHandSide);
            }
        }

        return result;
    }

    /**
     * <p>Shallow compare is too weak for many things, and equals() is too
     * strict. This method returns <code>true</code> <strong>iff</code> this
     * expression and the provided expression, <code>e</code>, are equivalent
     * with respect to structure and all function and variable names.</p>
     *
     * @param e The expression to compare this one to.
     *
     * @return True <strong>iff</strong> this expression and the provided
     *         expression are equivalent with respect to structure and all
     *         function and variable names.
     */
    @Override
    public boolean equivalent(Exp e) {
        boolean retval = e instanceof EqualsExp;
        if (retval) {
            EqualsExp eAsEquals = (EqualsExp) e;
            retval =
                    (myOperator == eAsEquals.myOperator)
                            && (myLeftHandSide
                                    .equivalent(eAsEquals.myLeftHandSide))
                            && (myRightHandSide
                                    .equivalent(eAsEquals.myRightHandSide));
        }

        return retval;
    }

    /**
     * <p>This method returns a deep copy of the left hand side expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getLeft() {
        return myLeftHandSide.clone();
    }

    /**
     * <p>This method returns the operator.</p>
     *
     * @return A {link Operator} object containing the operator.
     */
    public Operator getOperator() {
        return myOperator;
    }

    /**
     * <p>This method returns a deep copy of the operator name.</p>
     *
     * @return A {link PosSymbol} object containing the operator.
     */
    @Override
    public PosSymbol getOperatorAsPosSymbol() {
        return new PosSymbol(new Location(myLoc), getOperatorAsString());
    }

    /**
     * <p>This method returns a deep copy of the operator name.</p>
     *
     * @return The operator as a string.
     */
    @Override
    public String getOperatorAsString() {
        return myOperator.toString();
    }

    /**
     * <p>This method returns a deep copy of the right hand side expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getRight() {
        return myRightHandSide.clone();
    }

    /**
     * <p>This method returns a deep copy of the list of
     * subexpressions.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> subExps = new ArrayList<>();
        subExps.add(myLeftHandSide.clone());
        subExps.add(myRightHandSide.clone());

        return subExps;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link EqualsExp} from applying the remember rule.
     */
    @Override
    public EqualsExp remember() {
        Exp newLeft = ((MathExp) myLeftHandSide).remember();
        Exp newRight = ((MathExp) myRightHandSide).remember();

        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new EqualsExp(new Location(myLoc), qualifier, newLeft,
                myOperator, newRight);
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*public void setSubExpression(int index, Exp e) {
        switch (index) {
            case 0:
                myLeftHandSide = e;
                break;
            case 1:
                myRightHandSide = e;
                break;
        }
    }*/

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public MathExp simplify() {
        Exp simplified;
        if (myLeftHandSide.equivalent(myRightHandSide)) {
            simplified =
                    MathExp.getTrueVarExp(myLoc, myMathType.getTypeGraph());
        }
        else {
            simplified = this.clone();
        }

        return (MathExp) simplified;
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (myQualifier != null) {
            sb.append(myQualifier.toString());
            sb.append("::");
        }

        if (myLeftHandSide != null) {
            sb.append("(");
            sb.append(myLeftHandSide.toString());
            sb.append(" ");
        }

        sb.append(myOperator.toString());
        sb.append(" ");

        if (myRightHandSide != null) {
            sb.append(myRightHandSide.toString());
            sb.append(")");
        }

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by this concrete subclass of {@link Exp} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link Exp} that is a deep copy of the original.
     */
    @Override
    protected Exp copy() {
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new EqualsExp(new Location(myLoc), qualifier, myLeftHandSide
                .clone(), myOperator, myRightHandSide.clone());
    }

    /**
     * <p>Implemented by this concrete subclass of {@link Exp} to manufacture
     * a copy of themselves where all subexpressions have been appropriately
     * substituted. This class is assuming that <code>this</code>
     * does not match any key in <code>substitutions</code> and thus need only
     * concern itself with performing substitutions in its children.</p>
     *
     * @param substitutions A mapping from {@link Exp}s that should be
     *                      substituted out to the {@link Exp} that should
     *                      replace them.
     *
     * @return A new {@link Exp} that is a deep copy of the original with
     *         the provided substitutions made.
     */
    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new EqualsExp(new Location(myLoc), qualifier, substitute(
                myLeftHandSide, substitutions), myOperator, substitute(
                myRightHandSide, substitutions));
    }

}