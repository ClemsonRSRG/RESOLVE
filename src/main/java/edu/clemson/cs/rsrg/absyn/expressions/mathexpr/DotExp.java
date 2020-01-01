/*
 * DotExp.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This is the class for all the mathematical dotted expression objects that the
 * compiler builds
 * using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class DotExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The expression's collection of inner expressions.
     * </p>
     */
    private final List<Exp> mySegmentExps;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a dotted expression to keep track of all the inner
     * expressions.
     * </p>
     *
     * @param l A {@link Location} representation object.
     * @param segments A list of {@link Exp} object.
     */
    public DotExp(Location l, List<Exp> segments) {
        super(l);
        mySegmentExps = segments;
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

        Iterator<Exp> it = mySegmentExps.iterator();
        while (it.hasNext()) {
            sb.append(it.next().asString(0, innerIndentInc));

            if (it.hasNext()) {
                sb.append(".");
            }
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = false;
        if (mySegmentExps != null) {
            Iterator<Exp> i = mySegmentExps.iterator();
            while (i.hasNext() && !found) {
                Exp temp = i.next();
                if (temp != null) {
                    if (temp.containsExp(exp)) {
                        found = true;
                    }
                }
            }
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;
        if (mySegmentExps != null) {
            Iterator<Exp> i = mySegmentExps.iterator();
            while (i.hasNext() && !found) {
                Exp temp = i.next();
                if (temp != null) {
                    if (temp.containsVar(varName, IsOldExp)) {
                        found = true;
                    }
                }
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

        DotExp dotExp = (DotExp) o;

        return mySegmentExps.equals(dotExp.mySegmentExps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean result = (e instanceof DotExp);

        if (result) {
            DotExp eAsDotExp = (DotExp) e;

            if (mySegmentExps != null && eAsDotExp.mySegmentExps != null) {
                Iterator<Exp> thisSegmentExps = mySegmentExps.iterator();
                Iterator<Exp> eSegmentExps = eAsDotExp.mySegmentExps.iterator();
                while (result && thisSegmentExps.hasNext()
                        && eSegmentExps.hasNext()) {

                    result &= thisSegmentExps.next()
                            .equivalent(eSegmentExps.next());
                }

                // Both had better have run out at the same time
                result &= (!thisSegmentExps.hasNext())
                        && (!eSegmentExps.hasNext());
            }
        }

        return result;
    }

    /**
     * <p>
     * This method returns all the inner expressions.
     * </p>
     *
     * @return A list containing all the segmented {@link Exp}s.
     */
    public final List<Exp> getSegments() {
        return mySegmentExps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        return copyExps();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + mySegmentExps.hashCode();
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
        return new DotExp(cloneLocation(), copyExps());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        List<Exp> newSegments = new ArrayList<>(mySegmentExps.size());

        // Make sure we have segments to replace
        if (!mySegmentExps.isEmpty()) {
            // YS: This substitution should be thought of as longest common
            // prefix string substitution. If our DotExp happens to be: "S.Contents",
            // we don't want to apply a substitution from "Contents" -> "Apple".
            // However, if there is a substitution from "S" to "T", we do want to apply
            // that substitution. Note that at any point if we detect it is a receptacle
            // of some form, we don't apply any substitutions.
            Exp substitutionKey = null;
            FunctionExp innerFunctionExp = null;
            int lastGoodIndex = -1;
            boolean matchedFunctionName = false;
            for (int i = 0; i < mySegmentExps.size()
                    && !matchedFunctionName; i++) {
                Exp e = mySegmentExps.get(i);

                // YS: We form a new DotExp up to ith position and compare it to see
                // if there is a key that matches this new expression. If yes,
                // it means it is part of something that can be substituted.
                Exp toCompareExp;
                Exp toCompareFunctionNameExp = null;
                if (i == 0) {
                    // Special handling for FunctionExp
                    if (e instanceof FunctionExp) {
                        toCompareFunctionNameExp = ((FunctionExp) e).getName();
                    }

                    toCompareExp = e;
                }
                else {
                    // Special handling for FunctionExp
                    if (e instanceof FunctionExp) {
                        List<Exp> functionSegmentList =
                                new ArrayList<>(mySegmentExps.subList(0, i));
                        functionSegmentList.add(((FunctionExp) e).getName());
                        toCompareFunctionNameExp =
                                new DotExp(myLoc.clone(), functionSegmentList);
                    }

                    List<Exp> newSubSegmentList =
                            new ArrayList<>(mySegmentExps.subList(0, i + 1));
                    toCompareExp = new DotExp(myLoc.clone(), newSubSegmentList);
                }

                // YS: Check to see if we have a key that is equivalent to "toCompareExp".
                for (Exp keyExp : substitutions.keySet()) {
                    // We found a key that is equivalent
                    if (keyExp.equivalent(toCompareExp)) {
                        substitutionKey = keyExp;
                        lastGoodIndex = i;
                    }
                    else {
                        // Special handling for FunctionExp
                        if (e instanceof FunctionExp) {
                            if (keyExp.equivalent(toCompareFunctionNameExp)) {
                                substitutionKey = keyExp;
                                lastGoodIndex = i;
                                matchedFunctionName = true;
                                innerFunctionExp = (FunctionExp) e;
                            }
                        }
                    }
                }
            }

            // YS: If we don't have a substitution key, then simply make a copy
            if (substitutionKey == null) {
                newSegments = copyExps();

                return new DotExp(cloneLocation(), newSegments);
            }
            else {
                Exp replacementExp = substitutions.get(substitutionKey);

                // YS: If we only matched the function name, then we need to do something special.
                if (innerFunctionExp != null) {
                    return substituteFunctionExp(innerFunctionExp,
                            replacementExp, substitutions);
                }
                // YS: Make a copy of "replacementExp" if it is a VCVarExp
                // and it matches the whole expression.
                else if (replacementExp instanceof VCVarExp
                        && (lastGoodIndex + 1 == mySegmentExps.size())) {
                    return replacementExp.clone();
                }
                // YS: Else create a new DotExp with the proper replacements.
                else {
                    // Case #1: "replacementExp" is a VarExp
                    if (replacementExp instanceof VarExp) {
                        newSegments.add(replacementExp.clone());
                    }
                    // Case #2: "replacementExp" is a DotExp
                    else if (replacementExp instanceof DotExp) {
                        DotExp replacementExpAsDotExp = (DotExp) replacementExp;
                        List<Exp> segments =
                                replacementExpAsDotExp.getSegments();

                        // Copy the segments
                        for (Exp segment : segments) {
                            newSegments.add(segment.clone());
                        }
                    }
                    // Case #3: "replacementExp" is an OldExp
                    else if (replacementExp instanceof OldExp) {
                        newSegments.add(replacementExp.clone());
                    }
                    // Case #4: "replacementExp" is a VCVarExp
                    else if (replacementExp instanceof VCVarExp) {
                        newSegments.add(replacementExp.clone());
                    }
                    // Everything else is an error!
                    else {
                        throw new SourceErrorException(
                                "Cannot substitute: " + this.toString()
                                        + " with: " + replacementExp.toString(),
                                replacementExp.getLocation());
                    }

                    // Copy the rest of the segments if any
                    for (int i = lastGoodIndex + 1; i < mySegmentExps
                            .size(); i++) {
                        newSegments.add(mySegmentExps.get(i).clone());
                    }

                    return new DotExp(cloneLocation(), newSegments);
                }
            }
        }
        // YS: Nothing to replace, simply return an empty DotExp.
        else {
            return new DotExp(cloneLocation(), newSegments);
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the
     * segment expressions.
     * </p>
     *
     * @return A list containing {@link Exp}s.
     */
    private List<Exp> copyExps() {
        List<Exp> copyJoiningExps = new ArrayList<>();
        for (Exp exp : mySegmentExps) {
            copyJoiningExps.add(exp.clone());
        }

        return copyJoiningExps;
    }
}
