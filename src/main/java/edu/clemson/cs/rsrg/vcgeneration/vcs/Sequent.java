/*
 * Sequent.java
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
package edu.clemson.cs.rsrg.vcgeneration.vcs;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.BasicCapabilities;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.vcgeneration.treewalkers.AtomicFormulaChecker;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>This class stores antecedent and consequent expressions for a given
 * conditional assertion.</p>
 *
 * <p>All antecedents are joined by the {@code and} operator and the consequents
 * are joined by the {@code or} operator. This means that if all the conditions
 * in the antecedent are true, then one of the conditions in the consequent
 * must be true.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class Sequent implements BasicCapabilities, Cloneable {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The location for this {@code Sequent}.</p> */
    private Location myLocation;

    /** <p>Set of all antecedent conditions</p> */
    private final Set<Exp> myAntecedents;

    /** <p>Set of all consequent conditions</p> */
    private final Set<Exp> myConcequents;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that represents each of verification
     * conditions that must be verified.</p>
     *
     * @param loc The location that created this sequent.
     * @param antecedents The antecedents for this sequent.
     * @param consequents The consequents for this sequent.
     */
    public Sequent(Location loc, Set<Exp> antecedents, Set<Exp> consequents) {
        myLocation = loc;
        myAntecedents = new LinkedHashSet<>(antecedents);
        myConcequents = new LinkedHashSet<>(consequents);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the instantiated object.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentInc The additional indentation increment
     *                       for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indentSize; ++i) {
            sb.append(" ");
        }

        sb.append("{");
        Iterator<Exp> antecedentIt = myAntecedents.iterator();
        while (antecedentIt.hasNext()) {
            Exp nextExp = antecedentIt.next();
            sb.append(nextExp.asString(0, 0));

            if (antecedentIt.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("}");

        sb.append(" |- ");

        sb.append("{");
        if (myConcequents.isEmpty()) {
            sb.append("true");
        }
        else {
            Iterator<Exp> consequentIt = myConcequents.iterator();
            while (consequentIt.hasNext()) {
                Exp nextExp = consequentIt.next();
                sb.append(nextExp.asString(0, 0));

                if (consequentIt.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        sb.append("}");

        return sb.toString();
    }

    /**
     * <p>This method overrides the default {@code clone} method implementation.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final Sequent clone() {
        return new Sequent(myLocation.clone(), new LinkedHashSet<>(myAntecedents),
                new LinkedHashSet<>(myConcequents));
    }

    /**
     * <p>This method checks to see if this is a {@code Sequent} that
     * only contains atomic formulas.</p>
     *
     * @return {@code true} if it is, {@code false} otherwise.
     */
    public final boolean consistOfAtomicFormulas() {
        boolean retval = true;

        // First check our antecedents
        Iterator<Exp> antecedentIt = myAntecedents.iterator();
        while (antecedentIt.hasNext() && retval) {
            AtomicFormulaChecker checker = new AtomicFormulaChecker();
            TreeWalker.visit(checker, antecedentIt.next());
            retval = checker.getIsAtomicFormula();
        }

        // Then check our consequents
        Iterator<Exp> consequentIt = myConcequents.iterator();
        while (consequentIt.hasNext() && retval) {
            AtomicFormulaChecker checker = new AtomicFormulaChecker();
            TreeWalker.visit(checker, consequentIt.next());
            retval = checker.getIsAtomicFormula();
        }

        return retval;
    }

    /**
     * <p>This method overrides the default {@code equals} method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Sequent sequent = (Sequent) o;

        return (myLocation != null ? myLocation.equals(sequent.myLocation)
                : sequent.myLocation == null)
                && myAntecedents.equals(sequent.myAntecedents)
                && myConcequents.equals(sequent.myConcequents);
    }

    /**
     * <p>This method returns the antecedent in this sequent.</p>
     *
     * @return A set of {@link Exp} representing the antecedent.
     */
    public final Set<Exp> getAntecedents() {
        return myAntecedents;
    }

    /**
     * <p>This method returns the consequents in this sequent.</p>
     *
     * @return A set of {@link Exp} representing the consequent.
     */
    public final Set<Exp> getConcequents() {
        return myConcequents;
    }

    /**
     * <p>This method returns the location that created this
     * sequent.</p>
     *
     * @return A {@link Location}.
     */
    public final Location getLocation() {
        return myLocation;
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myLocation != null ? myLocation.hashCode() : 0;
        result = 31 * result + myAntecedents.hashCode();
        result = 31 * result + myConcequents.hashCode();
        return result;
    }

    /**
     * <p>This method stores a new location for this sequent.</p>
     *
     * @param loc A {@link Location}.
     */
    public final void setLocation(Location loc) {
        myLocation = loc;
    }

    /**
     * <p>This method returns the sequent in string format.</p>
     *
     * @return A string.
     */
    @Override
    public final String toString() {
        return asString(0, 4);
    }

}