/*
 * OldExp.java
 * ---------------------------------
 * Copyright (c) 2023
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
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.*;

/**
 * <p>
 * This is the class for all the "old" mathematical expression objects that the compiler builds using the ANTLR4 AST
 * nodes.
 * </p>
 *
 * <p>
 * An "old" expression is an expression that refers to the incoming value of the expression.
 * </p>
 *
 * @version 2.0
 */
public class OldExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The actual declared mathematical expression represented in the code.
     * </p>
     */
    private final Exp myOrigExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs an inner expression from the passed in {@link Exp} class.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param exp
     *            An {@link Exp} that represents the actual expression.
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
        return myOrigExp.containsExp(exp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;
        if (IsOldExp) {
            found = myOrigExp.containsVar(varName, false);
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
     * <p>
     * Returns this old expression's actual expression.
     * </p>
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
     * <p>
     * This method sets the mathematical type associated with this object.
     * </p>
     *
     * @param mathType
     *            The {@link MTType} type object.
     */
    @Override
    public final void setMathType(MTType mathType) {
        super.setMathType(mathType);
        myOrigExp.setMathType(mathType);
    }

    /**
     * <p>
     * This method sets the mathematical type value associated with this object.
     * </p>
     *
     * @param mathTypeValue
     *            The {@link MTType} type object.
     */
    @Override
    public final void setMathTypeValue(MTType mathTypeValue) {
        super.setMathTypeValue(mathTypeValue);
        myOrigExp.setMathTypeValue(mathTypeValue);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        return new OldExp(cloneLocation(), myOrigExp.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        // Attempt to retrieve a substitution key
        Exp substitutionKey = null;
        OldExp oldExpWithFuncNameOnly = null;
        FunctionExp innerFunctionExp = null;
        Iterator<Exp> mapKeysIt = substitutions.keySet().iterator();
        while (mapKeysIt.hasNext() && substitutionKey == null) {
            Exp nextKey = mapKeysIt.next();

            // We found a key that is equivalent
            if (nextKey.equivalent(this)) {
                substitutionKey = nextKey;
            }
            // YS: It is possible that "myOrigExp" is either a FunctionExp or a
            // DotExp with a FunctionExp as it's last segment. In this case,
            // if we happen to have a replacement key that matches all the parts
            // up to the name, then we need to apply the substitution.
            else {
                if (myOrigExp instanceof DotExp) {
                    DotExp myOrigExpAsDotExp = (DotExp) myOrigExp;
                    List<Exp> segments = myOrigExpAsDotExp.getSegments();

                    // Check to see if the last segment is a FunctionExp
                    Exp lastSegment = segments.get(segments.size() - 1);
                    if (lastSegment instanceof FunctionExp) {
                        // Construct a new OldExp to compare
                        FunctionExp lastSegmentAsFunctionExp = (FunctionExp) lastSegment;

                        // Construct a new DotExp to compare
                        List<Exp> newSegments = new ArrayList<>(segments.size());
                        for (int i = 0; i < segments.size() - 1; i++) {
                            newSegments.add(segments.get(i).clone());
                        }
                        newSegments.add(lastSegmentAsFunctionExp.getName());

                        OldExp newOldExp = new OldExp(myLoc,
                                new DotExp(myOrigExpAsDotExp.getLocation().clone(), newSegments));
                        if (nextKey.equivalent(newOldExp)) {
                            substitutionKey = nextKey;
                            oldExpWithFuncNameOnly = newOldExp;

                            // Store the function for future use
                            innerFunctionExp = lastSegmentAsFunctionExp;
                        }
                    }
                } else if (myOrigExp instanceof FunctionExp) {
                    // Construct a new OldExp to compare
                    FunctionExp myOrigExpAsFunctionExp = (FunctionExp) myOrigExp;
                    OldExp newOldExp = new OldExp(myLoc, myOrigExpAsFunctionExp.getName());
                    if (nextKey.equivalent(newOldExp)) {
                        substitutionKey = nextKey;
                        oldExpWithFuncNameOnly = newOldExp;

                        // Store the function for future use
                        innerFunctionExp = myOrigExpAsFunctionExp;
                    }
                }
            }
        }

        // YS: If we don't have a substitution key, then simply make a copy
        if (substitutionKey == null) {
            return new OldExp(cloneLocation(), myOrigExp.clone());
        } else {
            Exp replacementExp = substitutions.get(substitutionKey);

            // YS: If we only matched the function name, then we need to do something special.
            if (oldExpWithFuncNameOnly != null) {
                return substituteFunctionExp(innerFunctionExp, replacementExp, substitutions);
            }
            // YS: Else, simply return "replacementExp"
            else {
                return replacementExp.clone();
            }
        }
    }
}
