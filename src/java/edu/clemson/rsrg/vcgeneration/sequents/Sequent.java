/*
 * Sequent.java
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
package edu.clemson.rsrg.vcgeneration.sequents;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.parsing.data.BasicCapabilities;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.rsrg.vcgeneration.utilities.treewalkers.AtomicFormulaChecker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This class stores {@code antecedent} and {@code consequent} expressions for a given conditional assertion.
 * </p>
 *
 * <p>
 * All {@code antecedents} are joined by the {@code and} operator and the {@code consequents} are joined by the
 * {@code or} operator. This means that if all the conditions in the {@code antecedent} are {@code true}, then one of
 * the conditions in the {@code consequent} must be {@code true}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class Sequent implements BasicCapabilities, Cloneable {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The location for this {@code sequent}.
     * </p>
     */
    private final Location myLocation;

    /**
     * <p>
     * List of all {@code antecedent} conditions
     * </p>
     */
    private final List<Exp> myAntecedents;

    /**
     * <p>
     * List of all {@code consequent} conditions
     * </p>
     */
    private final List<Exp> myConcequents;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that represents each of verification conditions that must be verified.
     * </p>
     *
     * @param loc
     *            The location that created this {@code sequent}.
     * @param antecedents
     *            The {@code antecedents} for this {@code sequent}.
     * @param consequents
     *            The {@code consequents} for this {@code sequent}.
     */
    public Sequent(Location loc, List<Exp> antecedents, List<Exp> consequents) {
        myLocation = loc;
        myAntecedents = new ArrayList<>(antecedents);
        myConcequents = new ArrayList<>(consequents);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method creates a special indented text version of the instantiated object.
     * </p>
     *
     * @param indentSize
     *            The base indentation to the first line of the text.
     * @param innerIndentInc
     *            The additional indentation increment for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentSize; ++i) {
            sb.append(" ");
        }

        sb.append(Utilities.expListAsString(myAntecedents));
        sb.append(" |- ");
        sb.append(Utilities.expListAsString(myConcequents));

        return sb.toString();
    }

    /**
     * <p>
     * This method overrides the default {@code clone} method implementation.
     * </p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final Sequent clone() {
        return new Sequent(myLocation.clone(), new ArrayList<>(myAntecedents), new ArrayList<>(myConcequents));
    }

    /**
     * <p>
     * This method checks to see if this is a {@code sequent} that only contains atomic formulas.
     * </p>
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
     * <p>
     * This method overrides the default {@code equals} method implementation.
     * </p>
     *
     * @param o
     *            Object to be compared.
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

        return myLocation.equals(sequent.myLocation) && myAntecedents.equals(sequent.myAntecedents)
                && myConcequents.equals(sequent.myConcequents);
    }

    /**
     * <p>
     * This method returns the {@code antecedent} in this {@code sequent}.
     * </p>
     *
     * @return A list of {@link Exp} representing the {@code antecedent}.
     */
    public final List<Exp> getAntecedents() {
        return myAntecedents;
    }

    /**
     * <p>
     * This method returns the {@code consequents} in this {@code sequent}.
     * </p>
     *
     * @return A list of {@link Exp} representing the {@code consequent}.
     */
    public final List<Exp> getConcequents() {
        return myConcequents;
    }

    /**
     * <p>
     * This method returns the location that created this {@code sequent}.
     * </p>
     *
     * @return A {@link Location}.
     */
    public final Location getLocation() {
        return myLocation;
    }

    /**
     * <p>
     * This method overrides the default {@code hashCode} method implementation.
     * </p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myLocation.hashCode();
        result = 31 * result + myAntecedents.hashCode();
        result = 31 * result + myConcequents.hashCode();
        return result;
    }

    /**
     * <p>
     * This method returns the sequent in string format.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String toString() {
        return asString(0, 4);
    }

}
