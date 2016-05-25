/**
 * SetCollectionExp.java
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
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.*;

/**
 * <p>This is the class for all the mathematical set (as a collection)
 * expression objects that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class SetCollectionExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The list of member expressions in this set collection.</p> */
    private final Set<MathExp> myMembers;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a mathematical set collection expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param vars A set of {@link MathExp}s where each one is a member
     *             in this set.
     */
    public SetCollectionExp(Location l, Set<MathExp> vars) {
        super(l);
        myMembers = vars;
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

        sb.append("{");
        if (myMembers != null) {
            if (myMembers.isEmpty()) {
                sb.append("");
            }
            else {
                Iterator<MathExp> i = myMembers.iterator();
                while (i.hasNext()) {
                    MathExp m = i.next();
                    sb.append(m.asString(indentSize, innerIndentInc));

                    if (i.hasNext()) {
                        sb.append(", ");
                    }
                }
            }
        }
        sb.append("}");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = false;

        Iterator<MathExp> i = myMembers.iterator();
        while (i.hasNext() && !found) {
            MathExp m = i.next();

            if (m != null) {
                found = m.containsExp(exp);
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

        Iterator<MathExp> i = myMembers.iterator();
        while (i.hasNext() && !found) {
            MathExp m = i.next();

            if (m != null) {
                found = m.containsVar(varName, IsOldExp);
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

        SetCollectionExp that = (SetCollectionExp) o;

        return myMembers.equals(that.myMembers);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean result = (e instanceof SetCollectionExp);

        if (result) {
            SetCollectionExp eAsSetCollectionExp = (SetCollectionExp) e;

            if (myMembers != null && eAsSetCollectionExp.myMembers != null) {
                Iterator<MathExp> thisMemberExps = myMembers.iterator();
                Iterator<MathExp> eMemberExps =
                        eAsSetCollectionExp.myMembers.iterator();
                while (result && thisMemberExps.hasNext()
                        && eMemberExps.hasNext()) {
                    result &=
                            thisMemberExps.next()
                                    .equivalent(eMemberExps.next());
                }

                //Both had better have run out at the same time
                result &=
                        (!thisMemberExps.hasNext()) && (!eMemberExps.hasNext());
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> subExpList = new ArrayList<>();
        for (MathExp m : myMembers) {
            subExpList.add(m);
        }

        return subExpList;
    }

    /**
     * <p>This method returns all the
     * variable expressions in this set.</p>
     *
     * @return A set containing all the {@link MathExp}s.
     */
    public final Set<MathExp> getVars() {
        return myMembers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myMembers.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link SetCollectionExp} from applying the remember rule.
     */
    @Override
    public final SetCollectionExp remember() {
        Set<MathExp> newVarExps = new HashSet<>();
        for (MathExp m : myMembers) {
            newVarExps.add((MathExp) m.remember());
        }

        return new SetCollectionExp(new Location(myLoc), newVarExps);
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

        sb.append("{");
        if (myMembers != null) {
            Iterator<MathExp> i = myMembers.iterator();

            while (i.hasNext()) {
                sb.append(i.next().toString());

                if (i.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        sb.append("}");

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
        return new SetCollectionExp(new Location(myLoc), copyExps());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        Set<MathExp> newMembers = new HashSet<>();
        for (MathExp m : myMembers) {
            newMembers.add((MathExp) substitute(m, substitutions));
        }

        return new SetCollectionExp(new Location(myLoc), newMembers);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the variable expressions.</p>
     *
     * @return A list containing {@link MathExp}s.
     */
    private Set<MathExp> copyExps() {
        Set<MathExp> copyMathExps = new HashSet<>();
        for (MathExp v : myMembers) {
            copyMathExps.add(v.clone());
        }

        return copyMathExps;
    }
}