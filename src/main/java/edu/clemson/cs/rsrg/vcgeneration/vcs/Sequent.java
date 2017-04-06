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
public class Sequent implements Cloneable {

    // ===========================================================
    // Member Fields
    // ===========================================================

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
     * @param antecedents The antecedents for this sequent.
     * @param consequents The consequents for this sequent.
     */
    public Sequent(Set<Exp> antecedents, Set<Exp> consequents) {
        myAntecedents = new LinkedHashSet<>(antecedents);
        myConcequents = new LinkedHashSet<>(consequents);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default {@code clone} method implementation.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final Sequent clone() {
        return new Sequent(new LinkedHashSet<>(myAntecedents), new LinkedHashSet<>(myConcequents));
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

        return myAntecedents.equals(sequent.myAntecedents)
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
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myAntecedents.hashCode();
        result = 31 * result + myConcequents.hashCode();
        return result;
    }

    /**
     * <p>This method returns the sequent in string format.</p>
     *
     * @return A string.
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        Iterator<Exp> antecedentIt = myAntecedents.iterator();
        while (antecedentIt.hasNext()) {
            Exp nextExp = antecedentIt.next();
            sb.append(nextExp.asString(0, 0));

            if (antecedentIt.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append("⊢");

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
        sb.append(")");

        return sb.toString();
    }

}