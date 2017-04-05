/*
 * DotExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical dotted expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class DotExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's collection of inner expressions.</p> */
    private final List<Exp> mySegmentExps;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a dotted expression to keep track
     * of all the inner expressions.</p>
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

                    result &=
                            thisSegmentExps.next().equivalent(
                                    eSegmentExps.next());
                }

                //Both had better have run out at the same time
                result &=
                        (!thisSegmentExps.hasNext())
                                && (!eSegmentExps.hasNext());
            }
        }

        return result;
    }

    /**
     * <p>This method returns all the inner expressions.</p>
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

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link DotExp} from applying the remember rule.
     */
    @Override
    public final DotExp remember() {
        List<Exp> newSegmentExps = new ArrayList<>();
        for (Exp e : mySegmentExps) {
            Exp copyExp;
            if (e instanceof MathExp){
                copyExp = ((MathExp) e).remember();
            }
            else {
                throw new MiscErrorException("We encountered an expression of the type " +
                        e.getClass().getName(),
                        new InvalidClassException(""));
            }

            newSegmentExps.add(copyExp);
        }

        return new DotExp(cloneLocation(), newSegmentExps);
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
        return new DotExp(cloneLocation(), copyExps());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        List<Exp> newSegments = new ArrayList<>();
        for (Exp e : mySegmentExps) {
            newSegments.add(substitute(e, substitutions));
        }

        return new DotExp(cloneLocation(), newSegments);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the segment expressions.</p>
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