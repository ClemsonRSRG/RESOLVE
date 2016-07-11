/**
 * ProgramVariableDotExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.programexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the programming dotted expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ProgramVariableDotExp extends ProgramVariableExp {

    /// ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's collection of inner expressions.</p> */
    private final List<ProgramVariableExp> mySegmentExps;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming variable dotted expression to keep track
     * of all the inner expressions.</p>
     *
     * @param l A {@link Location} representation object.
     * @param segments A list of {@link Exp} object.
     */
    public ProgramVariableDotExp(Location l, List<ProgramVariableExp> segments) {
        super(l, null);
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

        Iterator<ProgramVariableExp> it = mySegmentExps.iterator();
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
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        ProgramVariableDotExp that = (ProgramVariableDotExp) o;

        return mySegmentExps.equals(that.mySegmentExps);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = e instanceof ProgramVariableDotExp;

        if (retval) {
            ProgramVariableDotExp eAsProgramVariableDotExp =
                    (ProgramVariableDotExp) e;

            retval =
                    posSymbolEquivalent(getQualifier(),
                            eAsProgramVariableDotExp.getQualifier());

            if (mySegmentExps != null
                    && eAsProgramVariableDotExp.mySegmentExps != null) {
                Iterator<ProgramVariableExp> thisSegmentExps =
                        mySegmentExps.iterator();
                Iterator<ProgramVariableExp> eSegmentExps =
                        eAsProgramVariableDotExp.mySegmentExps.iterator();

                while (retval && thisSegmentExps.hasNext()
                        && eSegmentExps.hasNext()) {
                    retval &=
                            thisSegmentExps.next().equivalent(
                                    eSegmentExps.next());
                }

                //Both had better have run out at the same time
                retval &=
                        (!thisSegmentExps.hasNext())
                                && (!eSegmentExps.hasNext());
            }
        }

        return retval;
    }

    /**
     * <p>This method returns all the inner expressions.</p>
     *
     * @return A list containing all the segmented {@link ProgramVariableExp}s.
     */
    public final List<ProgramVariableExp> getSegments() {
        return mySegmentExps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> progExpAsExps = new ArrayList<>();
        for (ProgramVariableExp exp : mySegmentExps) {
            progExpAsExps.add(exp);
        }

        return progExpAsExps;
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
        return new ProgramVariableDotExp(cloneLocation(), copyExps());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        List<ProgramVariableExp> newSegments = new ArrayList<>();
        for (ProgramVariableExp e : mySegmentExps) {
            newSegments.add((ProgramVariableExp) substitute(e, substitutions));
        }

        return new ProgramVariableDotExp(cloneLocation(), newSegments);
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
    private List<ProgramVariableExp> copyExps() {
        List<ProgramVariableExp> copyJoiningExps = new ArrayList<>();
        for (ProgramVariableExp exp : mySegmentExps) {
            copyJoiningExps.add((ProgramVariableExp) exp.clone());
        }

        return copyJoiningExps;
    }
}