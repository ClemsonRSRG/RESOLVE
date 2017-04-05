/*
 * VCVarExp.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.absyn.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.MathExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class serves as a wrapper for an {@link MathExp}.
 * When the {@link VCGenerator} creates new variable expressions,
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
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("?");
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

        VCVarExp vcVarExp = (VCVarExp) o;

        return myOrigExp.equals(vcVarExp.myOrigExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = false;
        if (e instanceof VCVarExp) {
            VCVarExp eAsVarExp = (VCVarExp) e;
            retval = myOrigExp.equivalent(eAsVarExp.myOrigExp);
        }

        return retval;
    }

    /**
     * <p>Returns this VC variable expression's actual expression.</p>
     *
     * @return The {@link Exp} that we prepended the "?" to.
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
     * @return The resulting {@link VCVarExp} from applying the remember rule.
     */
    @Override
    public final VCVarExp remember() {
        return (VCVarExp) this.clone();
    }

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public final MathExp simplify() {
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
        return new VCVarExp(cloneLocation(), myOrigExp.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new VCVarExp(cloneLocation(), substitute(myOrigExp,
                substitutions));
    }

}