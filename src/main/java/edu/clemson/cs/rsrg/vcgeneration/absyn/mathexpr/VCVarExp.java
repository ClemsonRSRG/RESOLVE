/**
 * VCVarExp.java
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
package edu.clemson.cs.rsrg.vcgeneration.absyn.mathexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.absyn.mathexpr.MathExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class serves as a wrapper for an {@link MathExp}.
 * When the {@link VCGenerator} creates new variable expresssions,
 * it wraps the original expression inside this class.</p>
 *
 * @version 2.0
 */
public class VCVarExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The actual declared mathematical expression represented in the code.</p> */
    private final Exp myOrigExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a wrapper expression for the original
     * mathematical expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param exp An {@link Exp} that represents the actual expression.
     */
    public VCVarExp(Location l, Exp exp) {
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
        sb.append("VCVarExp\n");

        if (myOrigExp != null) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append("?");
            sb.append(myOrigExp.asString(0, innerIndentSize));
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
     * for the {@link VCVarExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof VCVarExp) {
            VCVarExp eAsVarExp = (VCVarExp) o;
            result = myLoc.equals(eAsVarExp.myLoc);

            if (result) {
                result = myOrigExp.equals(eAsVarExp.myOrigExp);
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
        boolean retval = false;
        if (e instanceof VCVarExp) {
            VCVarExp eAsVarExp = (VCVarExp) e;
            retval = myOrigExp.equivalent(eAsVarExp.myOrigExp);
        }

        return retval;
    }

    /**
     * <p>Returns a deep copy of this VC variable expression's actual expression.</p>
     *
     * @return The {@link Exp} that we prepended the "?" to.
     */
    public Exp getExp() {
        return myOrigExp.clone();
    }

    /**
     * <p>This method returns the list of subexpressions.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
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
     * @return The resulting {@link VCVarExp} from applying the remember rule.
     */
    @Override
    public VCVarExp remember() {
        return (VCVarExp) this.clone();
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
        if (myOrigExp != null) {
            sb.append("?");
            sb.append(myOrigExp.toString());
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
        return new VCVarExp(new Location(myLoc), myOrigExp.clone());
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
        return new VCVarExp(new Location(myLoc), substitute(myOrigExp,
                substitutions));
    }

}