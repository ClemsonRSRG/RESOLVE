/**
 * OutfixExp.java
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
 * <p>This is the abstract base class for all the mathematical outfix expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class OutfixExp extends AbstractFunctionExp {

    // ===========================================================
    // Operators
    // ===========================================================

    public enum Operator {
        ANGLE {

            @Override
            public String getLeftDelimiterString() {
                return "<";
            }

            @Override
            public String getRightDelimiterString() {
                return ">";
            }

            @Override
            public String toString() {
                return "ANGLE";
            }

        },
        DBL_ANGLE {

            @Override
            public String getLeftDelimiterString() {
                return "<<";
            }

            @Override
            public String getRightDelimiterString() {
                return ">>";
            }

            @Override
            public String toString() {
                return "DBL_ANGLE";
            }

        },
        SQUARE {

            @Override
            public String getLeftDelimiterString() {
                return "[";
            }

            @Override
            public String getRightDelimiterString() {
                return "]";
            }

            @Override
            public String toString() {
                return "SQUARE";
            }

        },
        DBL_SQUARE {

            @Override
            public String getLeftDelimiterString() {
                return "[[";
            }

            @Override
            public String getRightDelimiterString() {
                return "]]";
            }

            @Override
            public String toString() {
                return "DBL_SQUARE";
            }

        },
        BAR {

            @Override
            public String getLeftDelimiterString() {
                return "|";
            }

            @Override
            public String getRightDelimiterString() {
                return "|";
            }

            @Override
            public String toString() {
                return "BAR";
            }

        },
        DBL_BAR {

            @Override
            public String getLeftDelimiterString() {
                return "||";
            }

            @Override
            public String getRightDelimiterString() {
                return "||";
            }

            @Override
            public String toString() {
                return "DBL_BAR";
            }

        };

        /**
         * <p>Returns the left delimiter as a string.</p>
         *
         * @return A string.
         */
        public abstract String getLeftDelimiterString();

        /**
         * <p>Returns the right delimiter as a string.</p>
         *
         * @return A string.
         */
        public abstract String getRightDelimiterString();
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's operator.</p> */
    private final Operator myOperator;

    /** <p>The argument expression.</p> */
    private final Exp myArgument;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an equality/inequality expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     * @param operator A {@link Operator} representing the operator.
     * @param argument A {@link Exp} representing the right hand side.
     */
    public OutfixExp(Location l, PosSymbol qual, Operator operator, Exp argument) {
        super(l, qual);
        myOperator = operator;
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
        sb.append("OutfixExp\n");

        printSpace(indentSize + innerIndentSize, sb);
        sb.append(myOperator.toString());
        sb.append("\n");

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
     * for the {@link OutfixExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof OutfixExp) {
            OutfixExp eAsOutfixExp = (OutfixExp) o;
            result = myLoc.equals(eAsOutfixExp.myLoc);

            if (result) {
                result =
                        myOperator.equals(eAsOutfixExp.myOperator)
                                && myArgument.equals(eAsOutfixExp.myArgument);
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
        boolean retval = e instanceof OutfixExp;

        if (retval) {
            OutfixExp eAsOutfix = (OutfixExp) e;
            retval =
                    (myOperator == eAsOutfix.myOperator)
                            && equivalent(myArgument, eAsOutfix.myArgument);
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
        return new PosSymbol(new Location(myLoc), getOperatorAsString());
    }

    /**
     * <p>This method returns a deep copy of the operator name.</p>
     *
     * @return The operator as a string.
     */
    @Override
    public String getOperatorAsString() {
        return myOperator.getLeftDelimiterString() + "_"
                + myOperator.getRightDelimiterString();
    }

    /**
     * <p>This method method returns a deep copy of the list of
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
     * @return The resulting {@link OutfixExp} from applying the remember rule.
     */
    @Override
    public Exp remember() {
        Exp newArgument = ((MathExp) myArgument).remember();

        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new OutfixExp(new Location(myLoc), qualifier, myOperator,
                newArgument);
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
    public MathExp simplify() {
        return this.clone();
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

        sb.append(myOperator.getLeftDelimiterString());
        sb.append(" ");

        if (myArgument != null) {
            sb.append(myArgument.toString());
        }

        sb.append(" ");
        sb.append(myOperator.getRightDelimiterString());

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

        return new OutfixExp(new Location(myLoc), qualifier, myOperator,
                myArgument.clone());
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

        return new OutfixExp(new Location(myLoc), qualifier, myOperator,
                substitute(myArgument, substitutions));
    }

}