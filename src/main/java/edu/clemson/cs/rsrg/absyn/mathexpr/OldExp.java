/**
 * OldExp.java
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

import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the "old" mathematical expression
 * intermediate objects that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * <p>An "old" expression is an expression that refers to the
 * incoming value of the expression.</p>
 *
 * @version 2.0
 */
public class OldExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The actual declared mathematical expression represented in the code.</p> */
    private final Exp myOrigExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an inner expression from the passed in {@link Exp}
     * class.</p>
     *
     * @param l A {@link Location} representation object.
     * @param exp An {@link Exp} that represents the actual expression.
     */
    public OldExp(Location l, Exp exp) {
        super(l);
        myOrigExp = exp;

        if (exp.getMathType() != null) {
            setMathType(exp.getMathType());
        }

        if (exp.getMathTypeValue() != null) {
            setMathTypeValue(exp.getMathTypeValue());
        }
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
        sb.append("OldExp\n");

        if (myOrigExp != null) {
            sb.append(myOrigExp.asString(indentSize + innerIndentSize,
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
        if (myOrigExp != null) {
            found = myOrigExp.containsExp(exp);
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
        if (myOrigExp != null) {
            if (IsOldExp) {
                found = myOrigExp.containsVar(varName, false);
            }
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link OldExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof OldExp) {
            OldExp eAsOldExp = (OldExp) o;
            result = myLoc.equals(eAsOldExp.myLoc);

            if (result) {
                result = myOrigExp.equals(eAsOldExp.myOrigExp);
            }
        }

        return result;
    }

    /**
     * <p>Returns a deep copy of this old expression's actual expression.</p>
     *
     * @return The assignment {@link Exp} object.
     */
    public Exp getExp() {
        return myOrigExp.clone();
    }

    /**
     * <p>This method returns the list of subexpressions.</p>
     *
     * @return A list containing {@link Exp} type objects.
     */
    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        list.add(myOrigExp.clone());

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
        return myOrigExp.clone();
    }

    /**
     *  <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*
    @Override
    public void setSubExpression(int index, Exp e) {
        exp = e;
    }*/

    /**
     * <p>This method sets the mathematical type associated
     * with this object.</p>
     *
     * @param mathType The {@link MTType} type object.
     */
    @Override
    public void setMathType(MTType mathType) {
        super.setMathType(mathType);
        myOrigExp.setMathType(mathType);
    }

    /**
     * <p>This method sets the mathematical type value associated
     * with this object.</p>
     *
     * @param mathTypeValue The {@link MTType} type object.
     */
    @Override
    public void setMathTypeValue(MTType mathTypeValue) {
        super.setMathTypeValue(mathTypeValue);
        myOrigExp.setMathTypeValue(mathTypeValue);
    }

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
        if (myOrigExp != null) {
            sb.append("#" + myOrigExp.toString());
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
        Location newLoc = new Location(myLoc);
        Exp newOrigExp = null;
        if (myOrigExp != null) {
            newOrigExp = myOrigExp.clone();
        }

        return new OldExp(newLoc, newOrigExp);
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
        return new OldExp(new Location(myLoc), substitute(myOrigExp,
                substitutions));
    }

}