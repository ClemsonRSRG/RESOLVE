/**
 * IterativeExp.java
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
package edu.clemson.cs.rsrg.absyn.mathexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.absyn.variables.MathVarDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical iterative expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class IterativeExp extends MathExp {

    // ===========================================================
    // Operators
    // ===========================================================

    public enum Operator {
        SUM {

            @Override
            public String toString() {
                return "SUM";
            }

        },
        PRODUCT {

            @Override
            public String toString() {
                return "PRODUCT";
            }

        },
        CONCATENATION {

            @Override
            public String toString() {
                return "CONCATENATION";
            }

        },
        UNION {

            @Override
            public String toString() {
                return "UNION";
            }

        },
        INTERSECTION {

            @Override
            public String toString() {
                return "INTERSECTION";
            }

        };

        /**
         * <p>This method returns a deep copy of the operator name.</p>
         *
         * @param l A {@link Location} representation object.
         *
         * @return A {link PosSymbol} object containing the operator.
         */
        public PosSymbol getOperatorAsPosSymbol(Location l) {
            return new PosSymbol(new Location(l), toString());
        }
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's operation.</p> */
    private final Operator myOperator;

    /** <p>The mathematical variable in this iterative expression.</p> */
    private final MathVarDec myVar;

    /** <p>The iterative expression's where part.</p> */
    private final Exp myWhereExp;

    /** <p>The iterative expression's body.</p> */
    private final Exp myBodyExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a iterative expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param operator A {@link Operator} representing the operator.
     * @param var A {@link MathVarDec} representing the expression's variable.
     * @param where A {@link Exp} representing the where clause.
     * @param body A {@link Exp} representing the body of the expression.
     */
    public IterativeExp(Location l, Operator operator, MathVarDec var,
            Exp where, Exp body) {
        super(l);
        myOperator = operator;
        myVar = var;
        myWhereExp = where;
        myBodyExp = body;
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
        sb.append("IterativeExp\n");

        printSpace(indentSize + innerIndentSize, sb);
        sb.append(myOperator);
        sb.append(" ");

        sb
                .append(myVar.asString(indentSize + innerIndentSize,
                        innerIndentSize));

        if (myWhereExp != null) {
            sb.append(myWhereExp.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        sb.append(myBodyExp.asString(indentSize + innerIndentSize,
                innerIndentSize));

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
    public final boolean containsExp(Exp exp) {
        boolean found = myWhereExp.containsExp(exp);
        if (!found) {
            found = myBodyExp.containsExp(exp);
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
        boolean found = myWhereExp.containsVar(varName, IsOldExp);
        if (!found) {
            found = myBodyExp.containsVar(varName, IsOldExp);
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link IterativeExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof IterativeExp) {
            IterativeExp eAsIterativeExp = (IterativeExp) o;
            result = myLoc.equals(eAsIterativeExp.myLoc);

            if (result) {
                result =
                        myOperator.getOperatorAsPosSymbol(myLoc).equals(
                                eAsIterativeExp.myOperator
                                        .getOperatorAsPosSymbol(myLoc));
                result &= myVar.equals(eAsIterativeExp.myVar);

                if (result) {
                    result = myWhereExp.equals(eAsIterativeExp.myWhereExp);

                    if (result) {
                        result = myBodyExp.equals(eAsIterativeExp.myBodyExp);
                    }
                }
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
        boolean retval = e instanceof IterativeExp;
        if (retval) {
            IterativeExp eAsIterativeExp = (IterativeExp) e;
            retval =
                    myOperator.getOperatorAsPosSymbol(myLoc).equals(
                            eAsIterativeExp.myOperator
                                    .getOperatorAsPosSymbol(myLoc));
            retval &=
                    myVar.getName().equals(eAsIterativeExp.myVar.getName())
                            && myVar.getTy().equals(
                                    eAsIterativeExp.myVar.getTy());
            retval &= myWhereExp.equivalent(eAsIterativeExp.myWhereExp);
            retval &= myBodyExp.equivalent(eAsIterativeExp.myBodyExp);
        }

        return retval;
    }

    /**
     * <p>This method returns a deep copy of the body expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getBody() {
        return myBodyExp.clone();
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
     * <p>This method returns a deep copy of the list of
     * subexpressions.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        list.add(myWhereExp.clone());
        list.add(myBodyExp.clone());

        return list;
    }

    /**
     * <p>This method returns a deep copy of the variable.</p>
     *
     * @return The {@link MathVarDec} representation object.
     */
    public MathVarDec getVar() {
        return myVar.clone();
    }

    /**
     * <p>This method returns a deep copy of the where expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getWhere() {
        return myWhereExp.clone();
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link IterativeExp} from applying the remember rule.
     */
    @Override
    public IterativeExp remember() {
        Exp newWhere = ((MathExp) myWhereExp).remember();
        Exp newBody = ((MathExp) myBodyExp).remember();

        return new IterativeExp(new Location(myLoc), myOperator, myVar.clone(),
                newWhere, newBody);
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
                where = e;
                break;
            case 1:
                body = e;
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
        sb.append(myOperator.toString());
        sb.append(" ");
        sb.append(myVar.toString());

        if (myWhereExp != null) {
            sb.append(" where ");
            sb.append(myWhereExp.toString());
            sb.append(", ");
        }

        sb.append(myBodyExp.toString());

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
        Exp newWhere = null;
        if (myWhereExp != null) {
            newWhere = myWhereExp.clone();
        }

        return new IterativeExp(new Location(myLoc), myOperator, myVar.clone(),
                newWhere, myBodyExp.clone());
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
        return new IterativeExp(new Location(myLoc), myOperator, myVar.clone(),
                substitute(myWhereExp, substitutions), substitute(myBodyExp,
                        substitutions));
    }

}