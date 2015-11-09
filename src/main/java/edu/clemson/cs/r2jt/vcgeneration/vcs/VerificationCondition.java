/**
 * VerificationCondition.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.vcgeneration.vcs;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.rewriteprover.Conjuncts;

public class VerificationCondition {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /** <p>Section ID for this VC</p> **/
    private String myName;

    /** <p>List of Consequents (Goals) and
     * Antecedents (Givens) </p>
     */
    private Conjuncts myAntecedents, myConsequents;

    // ===========================================================
    // Constructors
    // ===========================================================

    public VerificationCondition(Exp antecedent, Exp consequent, String name) {
        myAntecedents = new Conjuncts(antecedent);
        myConsequents = new Conjuncts(consequent);
        myName = name;
    }

    public VerificationCondition(Exp antecedent, Exp consequent) {
        this(antecedent, consequent, null);
    }

    public VerificationCondition(List<Exp> antecedents, List<Exp> consequents,
            String name) {
        myAntecedents = new Conjuncts(antecedents);
        myConsequents = new Conjuncts(consequents);
        myName = name;
    }

    public VerificationCondition(List<Exp> antecedents, List<Exp> consequents) {
        this(antecedents, consequents, null);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>The deep copy method for this object.</p>
     *
     * @return Copy of the original object.
     */
    public VerificationCondition copy() {
        List<Exp> newAntecedents = new List<Exp>();
        List<Exp> newConsequents = new List<Exp>();

        // Copy all antecedents
        for (Exp a : myAntecedents) {
            newAntecedents.add(Exp.copy(a));
        }

        // Copy all consequents
        for (Exp c : myConsequents) {
            newConsequents.add(Exp.copy(c));
        }

        return new VerificationCondition(newAntecedents, newConsequents, myName);
    }

    /**
     * <p>Equality test for this object.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if equal, false otherwise.
     */
    public boolean equals(Object o) {
        boolean retval = o instanceof VerificationCondition;

        if (retval) {
            VerificationCondition otherVC = (VerificationCondition) o;
            retval =
                    (otherVC.getAntecedents().equivalent(myAntecedents))
                            && (otherVC.getConsequents()
                                    .equivalent(myConsequents));
        }

        return retval;
    }

    /**
     * <p>Returns the current list of givens.</p>
     *
     * @return The givens in <code>Conjuncts</code> form.
     */
    public Conjuncts getAntecedents() {
        return myAntecedents;
    }

    /**
     * <p>Returns the current goal.</p>
     *
     * @return The goals in <code>Conjuncts</code> form.
     */
    public Conjuncts getConsequents() {
        return myConsequents;
    }

    /**
     * <p>Returns the section ID assigned by the
     * VC Generator.</p>
     *
     * @return <code>String</code> form of the ID.
     */
    public String getName() {
        return myName;
    }

    /**
     * <p>This will give us the number of givens for
     * a particular VC.</p>
     *
     * @return Number of <code>Antecedents</code>
     */
    public int getNumAntecedents() {
        return myAntecedents.size();
    }

    /**
     * <p>This will give us the number of goals for
     * a particular VC.</p>
     *
     * @return Number of <code>Concequents</code>
     */
    public int getNumConsequents() {
        return myConsequents.size();
    }

    /**
     * <p>Updates the list of antecedents.</p>
     *
     * @param antecedents New givens for this VC.
     */
    public void setAntecedents(Conjuncts antecedents) {
        myAntecedents = antecedents;
    }

    /**
     * <p>Updates the list of consequents.</p>
     *
     * @param consequents New goal for this VC.
     */
    public void setConsequents(Conjuncts consequents) {
        myConsequents = consequents;
    }

    /**
     * <p>Simplify the VCs</p>
     */
    public void simplify() {
        myAntecedents.eliminateObviousConjunctsInPlace();
        myConsequents.eliminateObviousConjunctsInPlace();

        myAntecedents.eliminateRedundantConjuncts();
        myConsequents.eliminateRedundantConjuncts();

        for (Exp e : myAntecedents) {
            myConsequents.eliminateEquivalentConjunctsInPlace(e);
        }
    }

    /**
     * <p>Convert this object into a readable format.</p>
     *
     * @return String object that can be printed.
     */
    public String toString() {
        return myAntecedents + "===============================>\n"
                + myConsequents;
    }
}