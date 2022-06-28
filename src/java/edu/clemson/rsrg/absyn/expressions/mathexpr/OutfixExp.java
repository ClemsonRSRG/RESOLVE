/*
 * OutfixExp.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn.expressions.mathexpr;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This is the abstract base class for all the mathematical outfix expression objects that the compiler builds using the
 * ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class OutfixExp extends AbstractFunctionExp {

    // ===========================================================
    // Operators
    // ===========================================================

    /**
     * <p>
     * This defines the various outfix operators.
     * </p>
     *
     * @version 2.0
     */
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
         * <p>
         * Returns the left delimiter as a string.
         * </p>
         *
         * @return A string.
         */
        public abstract String getLeftDelimiterString();

        /**
         * <p>
         * Returns the right delimiter as a string.
         * </p>
         *
         * @return A string.
         */
        public abstract String getRightDelimiterString();
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The expression's operator.
     * </p>
     */
    private final Operator myOperator;

    /**
     * <p>
     * The argument expression.
     * </p>
     */
    private final Exp myArgument;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs an equality/inequality expression.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param operator
     *            A {@link Operator} representing the operator.
     * @param argument
     *            A {@link Exp} representing the right hand side.
     */
    public OutfixExp(Location l, Operator operator, Exp argument) {
        super(l, null);
        myOperator = operator;
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
        printSpace(indentSize, sb);

        sb.append(myOperator.getLeftDelimiterString());
        sb.append(myArgument.asString(0, innerIndentInc));
        sb.append(myOperator.getRightDelimiterString());

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

        OutfixExp outfixExp = (OutfixExp) o;

        if (myOperator != outfixExp.myOperator)
            return false;
        return myArgument.equals(outfixExp.myArgument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = e instanceof OutfixExp;

        if (retval) {
            OutfixExp eAsOutfix = (OutfixExp) e;
            retval = (myOperator == eAsOutfix.myOperator) && equivalent(myArgument, eAsOutfix.myArgument);
        }

        return retval;
    }

    /**
     * <p>
     * This method returns the argument expression.
     * </p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getArgument() {
        return myArgument;
    }

    /**
     * <p>
     * This method returns the operator.
     * </p>
     *
     * @return A {@link Operator} object containing the operator.
     */
    public final Operator getOperator() {
        return myOperator;
    }

    /**
     * <p>
     * This method returns a deep copy of the operator name.
     * </p>
     *
     * @return A {@link PosSymbol} object containing the operator.
     */
    @Override
    public final PosSymbol getOperatorAsPosSymbol() {
        return new PosSymbol(cloneLocation(), getOperatorAsString());
    }

    /**
     * <p>
     * This method returns operator name in string format.
     * </p>
     *
     * @return The operator as a string.
     */
    @Override
    public final String getOperatorAsString() {
        return myOperator.getLeftDelimiterString() + "_" + myOperator.getRightDelimiterString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> subExps = new ArrayList<>();
        subExps.add(myArgument.clone());

        return subExps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myOperator.hashCode();
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
        return new OutfixExp(cloneLocation(), myOperator, myArgument.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new OutfixExp(cloneLocation(), myOperator, substitute(myArgument, substitutions));
    }

}
