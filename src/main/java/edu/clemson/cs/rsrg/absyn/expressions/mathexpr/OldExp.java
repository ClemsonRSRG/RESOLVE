/**
 * OldExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the "old" mathematical expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
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
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();

        printSpace(indentSize, sb);
        sb.append("#");
        sb.append(myOrigExp.asString(0, innerIndentInc));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = false;
        if (myOrigExp != null) {
            found = myOrigExp.containsExp(exp);
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;
        if (myOrigExp != null) {
            if (IsOldExp) {
                found = myOrigExp.containsVar(varName, false);
            }
        }

        return found;
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

        OldExp oldExp = (OldExp) o;

        return myOrigExp.equals(oldExp.myOrigExp);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = (e instanceof OldExp);
        if (retval) {
            OldExp eAsOldExp = (OldExp) e;
            retval = myOrigExp.equivalent(eAsOldExp.myOrigExp);
        }

        return retval;
    }

    /**
     * <p>Returns this old expression's actual expression.</p>
     *
     * @return The {@link Exp} that we are applying the "old" operator to.
     */
    public final Exp getExp() {
        return myOrigExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        list.add(myOrigExp);

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myOrigExp.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link Exp} from applying the remember rule.
     */
    @Override
    public final Exp remember() {
        return myOrigExp.clone();
    }

    /**
     * <p>This method sets the mathematical type associated
     * with this object.</p>
     *
     * @param mathType The {@link MTType} type object.
     */
    @Override
    public final void setMathType(MTType mathType) {
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
    public final void setMathTypeValue(MTType mathTypeValue) {
        super.setMathTypeValue(mathTypeValue);
        myOrigExp.setMathTypeValue(mathTypeValue);
    }

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link Exp} from applying the simplification step.
     */
    @Override
    public final Exp simplify() {
        return this.clone();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        Location newLoc = new Location(myLoc);
        Exp newOrigExp = null;
        if (myOrigExp != null) {
            newOrigExp = myOrigExp.clone();
        }

        return new OldExp(newLoc, newOrigExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new OldExp(new Location(myLoc), substitute(myOrigExp,
                substitutions));
    }

}