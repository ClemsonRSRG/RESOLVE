/**
 * IntegerExp.java
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
 * <p>This is the class for all the mathematical integer expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class IntegerExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The qualifier for this mathematical integer</p> */
    private PosSymbol myQualifier;

    /** <p>The integer representing this mathematical integer</p> */
    private final Integer myInteger;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a mathematical integer expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qualifier A {@link PosSymbol} representation object.
     * @param i A {@link Integer} expression.
     */
    public IntegerExp(Location l, PosSymbol qualifier, int i) {
        super(l);
        myQualifier = qualifier;
        myInteger = i;
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
        sb.append("IntegerExp\n");

        if (myQualifier != null) {
            sb.append(myQualifier.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append("::");
        }

        printSpace(indentSize + innerIndentSize, sb);
        sb.append(myInteger);
        sb.append("\n");

        return sb.toString();
    }

    /**
     * <p>This method attempts to find the provided expression in our
     * subexpressions. The result of this calling this method should
     * always be false, because we can not contain an expression.</p>
     *
     * @param exp The expression we wish to locate.
     *
     * @return False.
     */
    @Override
    public boolean containsExp(Exp exp) {
        return false;
    }

    /**
     * <p>This method attempts to find an expression with the given name in our
     * subexpressions. The result of this calling this method should
     * always be false, because we can not contain an expression.</p>
     *
     * @param varName Expression name.
     * @param IsOldExp Flag to indicate if the given name is of the form
     *                 "#[varName]"
     *
     * @return False.
     */
    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link IntegerExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof IntegerExp) {
            IntegerExp eAsIntegerExp = (IntegerExp) o;
            result = myLoc.equals(eAsIntegerExp.myLoc);

            if (result) {
                result = myInteger.equals(eAsIntegerExp.myInteger);
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
        boolean retval = e instanceof IntegerExp;
        if (retval) {
            IntegerExp eAsIntegerExp = (IntegerExp) e;
            retval = myInteger.equals(eAsIntegerExp.myInteger);
        }

        return retval;
    }

    /**
     * <p>This method returns a deep copy of the qualifier name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public PosSymbol getQualifier() {
        return myQualifier.clone();
    }

    /**
     * <p>This method method returns a deep copy of the list of
     * subexpressions. The result of this calling this method should
     * always be an empty list, because we can not contain an expression.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        return new ArrayList<>();
    }

    /**
     * <p>This method returns a deep copy of the integer value.</p>
     *
     * @return The {@link Integer} value.
     */
    public int getValue() {
        return new Integer(myInteger);
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link IntegerExp} from applying the remember rule.
     */
    @Override
    public IntegerExp remember() {
        return (IntegerExp) this.clone();
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    //public void setSubExpression(int index, Exp e) {}

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
            sb.append(myQualifier);
            sb.append("::");
        }
        sb.append(myInteger);

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
    public Exp copy() {
        return new IntegerExp(new Location(myLoc), myQualifier.clone(),
                myInteger);
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
        return new IntegerExp(new Location(myLoc), myQualifier.clone(),
                myInteger);
    }

}