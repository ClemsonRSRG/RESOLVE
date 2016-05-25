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
package edu.clemson.cs.rsrg.absyn.programexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
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
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     * @param segments A list of {@link Exp} object.
     */
    public ProgramVariableDotExp(Location l, PosSymbol qual,
            List<ProgramVariableExp> segments) {
        super(l, qual);
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

        if (getQualifier() != null) {
            sb.append(getQualifier().asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("::");
        }

        if (mySegmentExps != null) {
            for (ProgramVariableExp e : mySegmentExps) {
                sb.append(e.asString(indentSize + innerIndentInc,
                        innerIndentInc));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();

        if (getQualifier() != null) {
            sb.append(getQualifier().toString());
            sb.append("::");
        }

        if (mySegmentExps != null) {
            Iterator<ProgramVariableExp> i = mySegmentExps.iterator();

            while (i.hasNext()) {
                sb.append(i.next().toString());
                if (i.hasNext()) {
                    sb.append(".");
                }
            }
        }

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        PosSymbol newQualifier = null;
        if (getQualifier() != null) {
            newQualifier = getQualifier().clone();
        }

        return new ProgramVariableDotExp(new Location(myLoc), newQualifier,
                copyExps());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        PosSymbol newQualifier = null;
        if (getQualifier() != null) {
            newQualifier = getQualifier().clone();
        }

        List<ProgramVariableExp> newSegments = new ArrayList<>();
        for (ProgramVariableExp e : mySegmentExps) {
            newSegments.add((ProgramVariableExp) substitute(e, substitutions));
        }

        return new ProgramVariableDotExp(new Location(myLoc), newQualifier, newSegments);
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