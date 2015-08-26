/**
 * UnaryMinusExp.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical unary minus expression
 * intermediate objects that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class UnaryMinusExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The mathematical expression that is being applied "unary minus".</p> */
    private final Exp myInnerArgumentExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a unary minus expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param exp An {@link Exp} that represents the actual expression.
     */
    public UnaryMinusExp(Location l, Exp exp) {
        super(l);
        myInnerArgumentExp = exp;
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
        sb.append("UnaryMinusExp\n");

        if (myInnerArgumentExp != null) {
            sb.append(myInnerArgumentExp.asString(indentSize + innerIndentSize,
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
        boolean found = false;
        if (myInnerArgumentExp != null) {
            found = myInnerArgumentExp.containsExp(exp);
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
        boolean found = false;
        if (myInnerArgumentExp != null) {
            found = myInnerArgumentExp.containsVar(varName, IsOldExp);
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link UnaryMinusExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof UnaryMinusExp) {
            UnaryMinusExp eAsUnaryMinusExp = (UnaryMinusExp) o;
            result = myLoc.equals(eAsUnaryMinusExp.myLoc);

            if (result) {
                result =
                        myInnerArgumentExp
                                .equals(eAsUnaryMinusExp.myInnerArgumentExp);
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
        boolean retval = (e instanceof UnaryMinusExp);
        if (retval) {
            UnaryMinusExp eAsUnaryMinusExp = (UnaryMinusExp) e;
            retval =
                    myInnerArgumentExp
                            .equivalent(eAsUnaryMinusExp.myInnerArgumentExp);
        }

        return retval;
    }

    /**
     * <p>Returns a deep copy of this expression's inner argument expression.</p>
     *
     * @return The assignment {@link Exp} object.
     */
    public Exp getArgument() {
        return myInnerArgumentExp.clone();
    }

    /**
     * <p>This method returns the list of subexpressions.</p>
     *
     * @return A list containing {@link Exp} type objects.
     */
    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        list.add(myInnerArgumentExp.clone());

        return list;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link Exp} from applying the remember rule.
     */
    @Override
    public Exp remember() {
        return this.clone();
    }

    /**
     *  <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*public void setSubExpression(int index, Exp e) {
        myInnerArgumentExp = e;
    }*/

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link Exp} from applying the simplification step.
     */
    @Override
    public Exp simplify() {
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
        if (myInnerArgumentExp != null) {
            sb.append("-(");
            sb.append(myInnerArgumentExp.toString());
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
        Exp newArgumentExp = null;
        if (myInnerArgumentExp != null) {
            newArgumentExp = myInnerArgumentExp.clone();
        }

        return new UnaryMinusExp(new Location(myLoc), newArgumentExp);
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
    public Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new UnaryMinusExp(new Location(myLoc), substitute(
                myInnerArgumentExp, substitutions));
    }

}