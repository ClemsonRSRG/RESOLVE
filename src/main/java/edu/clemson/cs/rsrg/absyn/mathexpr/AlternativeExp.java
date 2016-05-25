/**
 * AlternativeExp.java
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
package edu.clemson.cs.rsrg.absyn.mathexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical alternative expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class AlternativeExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The alternatives member.</p> */
    private final List<AltItemExp> myAlternatives;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an alternative expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param alternatives A list of {@link AltItemExp} expressions.
     */
    public AlternativeExp(Location l, List<AltItemExp> alternatives) {
        super(l);
        myAlternatives = alternatives;

        boolean foundOtherwise = false;
        for (AltItemExp e : alternatives) {
            foundOtherwise = foundOtherwise || (e.getTest() == null);
        }
        if (!foundOtherwise) {
            throw new MiscErrorException("Must have otherwise.",
                    new IllegalArgumentException());
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

        if (myAlternatives != null) {
            for (AltItemExp exp : myAlternatives) {
                if (exp != null) {
                    sb.append(exp.asString(indentSize + innerIndentInc,
                            innerIndentInc));
                }
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
        if (myAlternatives != null) {
            Iterator<AltItemExp> i = myAlternatives.iterator();
            while (i.hasNext() && !found) {
                AltItemExp temp = i.next();
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
        Iterator<AltItemExp> i = myAlternatives.iterator();
        while (i.hasNext() && !found) {
            AltItemExp temp = i.next();
            if (temp != null) {
                if (temp.containsVar(varName, IsOldExp)) {
                    found = true;
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

        AlternativeExp that = (AlternativeExp) o;

        return myAlternatives.equals(that.myAlternatives);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean result = e instanceof AlternativeExp;

        if (result) {
            AlternativeExp eAsAlternativeExp = (AlternativeExp) e;

            Iterator<AltItemExp> thisAltItems = myAlternatives.iterator();
            Iterator<AltItemExp> eAltItems =
                    eAsAlternativeExp.myAlternatives.iterator();

            while (result && thisAltItems.hasNext() && eAltItems.hasNext()) {
                result &= thisAltItems.next().equivalent(eAltItems.next());
            }

            //Both had better have run out at the same time
            result &= (!thisAltItems.hasNext()) && (!eAltItems.hasNext());
        }

        return result;
    }

    /**
     * <p>This method returns the list of alternative expressions.</p>
     *
     * @return A list containing {@link AltItemExp}s.
     */
    public final List<AltItemExp> getAlternatives() {
        return myAlternatives;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> subExpList = new ArrayList<>();
        for (AltItemExp exp : myAlternatives) {
            subExpList.add(exp.clone());
        }

        return subExpList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myAlternatives.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link AlternativeExp} from applying the remember rule.
     */
    @Override
    public final AlternativeExp remember() {
        List<AltItemExp> itemsCopy = new ArrayList<>();
        for (AltItemExp item : myAlternatives) {
            itemsCopy.add(item.remember());
        }

        return new AlternativeExp(new Location(myLoc), itemsCopy);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{{");
        for (AltItemExp exp : myAlternatives) {
            sb.append(exp.toString());
            sb.append("\n");
        }
        sb.append("}}");

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
        return new AlternativeExp(new Location(myLoc), copyAltItemList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        List<AltItemExp> newAlternatives = new ArrayList<>();
        for (Exp e : myAlternatives) {
            newAlternatives.add((AltItemExp) substitute(e, substitutions));
        }

        return new AlternativeExp(myLoc, newAlternatives);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list of alternative expressions.</p>
     *
     * @return A list containing {@link AltItemExp}s.
     */
    private List<AltItemExp> copyAltItemList() {
        List<AltItemExp> copyAlternatives = new ArrayList<>();
        for (AltItemExp exp : myAlternatives) {
            copyAlternatives.add((AltItemExp) exp.clone());
        }

        return copyAlternatives;
    }
}