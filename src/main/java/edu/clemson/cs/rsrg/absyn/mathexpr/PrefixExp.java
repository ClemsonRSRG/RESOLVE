/**
 * PrefixExp.java
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
 * <p>This is the abstract base class for all the mathematical prefix expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
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
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     * @param opName A {@link PosSymbol} representing the operator.
     * @param argument A {@link Exp} representing the right hand side.
     */
    public PrefixExp(Location l, PosSymbol qual, PosSymbol opName, Exp argument) {
        super(l, qual);
        myOperationName = opName;
        myArgument = argument;
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
        sb.append("PrefixExp\n");

        if (myOperationName != null) {
            sb.append(myOperationName.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        if (myArgument != null) {
            sb.append(myArgument.asString(indentSize + innerIndentSize,
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
        return myArgument.containsExp(exp);
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
        return myArgument.containsVar(varName, IsOldExp);
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link PrefixExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof PrefixExp) {
            PrefixExp eAsPrefixExp = (PrefixExp) o;
            result = myLoc.equals(eAsPrefixExp.myLoc);

            if (result) {
                result =
                        myOperationName.equals(eAsPrefixExp.myOperationName)
                                && myArgument.equals(eAsPrefixExp.myArgument);
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
     * <p>This method returns a deep copy of the argument expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getArgument() {
        return myArgument.clone();
    }

    /**
     * <p>This method returns a deep copy of the operator name.</p>
     *
     * @return A {link PosSymbol} object containing the operator.
     */
    @Override
    public PosSymbol getOperatorAsPosSymbol() {
        return myOperationName.clone();
    }

    /**
     * <p>This method returns a deep copy of the operator name.</p>
     *
     * @return The operator as a string.
     */
    @Override
    public String getOperatorAsString() {
        return myOperationName.toString();
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
        subExps.add(myArgument.clone());

        return subExps;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link PrefixExp} from applying the remember rule.
     */
    @Override
    public Exp remember() {
        Exp newArgument = ((MathExp) myArgument).remember();

        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new PrefixExp(new Location(myLoc), qualifier, myOperationName
                .clone(), newArgument);
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*public void setSubExpression(int index, Exp e) {
        myArgument = e;
    }*/

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public Exp simplify() {
        Exp newArgument;
        if (myArgument instanceof EqualsExp) {
            EqualsExp equalsExp = (EqualsExp) myArgument;

            EqualsExp.Operator newOperator;
            if (equalsExp.getOperator() == EqualsExp.Operator.EQUAL) {
                newOperator = EqualsExp.Operator.NOT_EQUAL;
            }
            else {
                newOperator = EqualsExp.Operator.EQUAL;
            }

            PosSymbol qualifier = equalsExp.getQualifier();
            if (qualifier != null) {
                qualifier = qualifier.clone();
            }

            newArgument =
                    new EqualsExp(new Location(equalsExp.getLocation()),
                            qualifier, equalsExp.getLeft(), newOperator,
                            equalsExp.getRight());
        }
        else {
            newArgument = this.clone();
        }

        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new PrefixExp(new Location(myLoc), qualifier, myOperationName
                .clone(), newArgument);
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

        if (myOperationName != null) {
            sb.append(getOperatorAsString());
        }

        if (myArgument != null) {
            sb.append("( ");
            sb.append(myArgument.toString());
            sb.append(" )");
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

        return new PrefixExp(new Location(myLoc), qualifier, myOperationName
                .clone(), myArgument.clone());
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

        return new PrefixExp(new Location(myLoc), qualifier, myOperationName
                .clone(), substitute(myArgument, substitutions));
    }

}