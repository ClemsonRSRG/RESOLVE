/*
 * SequentReduction.java
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

import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import java.util.*;

/**
 * <p>This class contains logic for reducing a {@link Sequent}.</p>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Sequent_calculus">Sequent Calculus</a>
 * for the history and explanation of each of the reduction rules.
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SequentReduction {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>A map that stores all the details associated with
     * a particular {@link Location}.</p>
     */
    private final Map<Location, String> myLocationDetails;

    /** <p>The incoming {@link Sequent} we are trying to reduce.</p> */
    private final Sequent myOriginalSequent;

    /** <p>A resulting list containing the reduced {@link Sequent Sequent(s)}.</p> */
    private final List<Sequent> myResultingSequents;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that helps reduce a
     * {@link Sequent}.</p>
     *
     * @param sequent A {@link Sequent} to be reduced.
     */
    public SequentReduction(Sequent sequent) {
        myLocationDetails = new HashMap<>();
        myOriginalSequent = sequent;
        myResultingSequents = new ArrayList<>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the various different sequent reduction
     * rules until we can't reduce any further.</p>
     */
    public final void applyReduction() {
        Deque<Sequent> sequentsToBeReduced = new LinkedList<>();
        List<Sequent> reducedSequents = new ArrayList<>();

        // Add the original sequent to the sequentsToBeReduced
        // and begin reducing it!
        sequentsToBeReduced.add(myOriginalSequent);
        while (!sequentsToBeReduced.isEmpty()) {
            Sequent seq = sequentsToBeReduced.getFirst();

            // Check to see if have a sequent with atomic formulas.
            // If we do, then we are done reducing the sequent!
            if (seq.consistOfAtomicFormulas()) {
                reducedSequents.add(seq);
            }
            // Otherwise, apply the left and/or right reduction
            // rules to reduce it!
            else {
                // Try to apply the left reduction rules
                ReductionResult leftResult = applyLeftReductionRules(seq);

                // If we didn't do any reductions on the left, try applying
                // the right reduction rules.
                if (!leftResult.doneReduction) {
                    ReductionResult rightResult = applyRightReductionRules(seq);

                    // If we didn't do any reductions on the right either, then
                    // we have an error. Either one of the reduction rules is wrong,
                    // or we have incorrectly detected this as a sequent that didn't
                    // contain atomic formulas.
                    if (!rightResult.doneReduction) {
                        throw new MiscErrorException("Error encountered during reduction. Sequent: " + seq
                                + " is contains atomic formulas or one of the reduction rules is wrong!",
                                new IllegalStateException());
                    }
                    else {
                        Deque<Sequent> newSequentsToBeReduced = rightResult.resultingSequents;
                        newSequentsToBeReduced.addAll(sequentsToBeReduced);
                        sequentsToBeReduced = newSequentsToBeReduced;
                    }
                }
                // If we did, then add it back to sequentsToBeReduced for potentially
                // more reductions.
                else {
                    Deque<Sequent> newSequentsToBeReduced = leftResult.resultingSequents;
                    newSequentsToBeReduced.addAll(sequentsToBeReduced);
                    sequentsToBeReduced = newSequentsToBeReduced;
                }
            }
        }

        myResultingSequents.addAll(reducedSequents);
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

        SequentReduction that = (SequentReduction) o;

        return myLocationDetails.equals(that.myLocationDetails)
                && myOriginalSequent.equals(that.myOriginalSequent)
                && myResultingSequents.equals(that.myResultingSequents);
    }

    /**
     * <p>This method returns a map containing details about
     * a {@link Location} object that was generated during the proof
     * application process.</p>
     *
     * @return A map from {@link Location} to location detail strings.
     */
    public final Map<Location, String> getNewLocationString() {
        return myLocationDetails;
    }

    /**
     * <p>This method returns the {@code sequents} that
     * resulted from applying the reduction rules.</p>
     *
     * @return A list of {@link Sequent Sequents}.
     */
    public final List<Sequent> getResultingSequents() {
        if (myResultingSequents.isEmpty()) {
            throw new MiscErrorException(
                    "Did you forget to call applyReduction?",
                    new IllegalAccessError());
        }

        return myResultingSequents;
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myLocationDetails.hashCode();
        result = 31 * result + myOriginalSequent.hashCode();
        result = 31 * result + myResultingSequents.hashCode();
        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Left Rules
    // -----------------------------------------------------------

    /**
     * <p>This method attempts to apply the left reduction rules
     * to {@code sequent}.</p>
     *
     * @param sequent The sequent to be reduced.
     *
     * @return The reduction results.
     */
    private ReductionResult applyLeftReductionRules(Sequent sequent) {
        boolean appliedReduction = false;
        Deque<Sequent> resultingSeq = new LinkedList<>();

        return new ReductionResult(appliedReduction, resultingSeq);
    }

    // -----------------------------------------------------------
    // Right Rules
    // -----------------------------------------------------------

    /**
     * <p>This method attempts to apply the right reduction rules
     * to {@code sequent}.</p>
     *
     * @param sequent The sequent to be reduced.
     *
     * @return The reduction results.
     */
    private ReductionResult applyRightReductionRules(Sequent sequent) {
        boolean appliedReduction = false;
        Deque<Sequent> resultingSeq = new LinkedList<>();

        return new ReductionResult(appliedReduction, resultingSeq);
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>An helper class that stores the results from applying the
     * different reduction rules.</p>
     */
    private class ReductionResult {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>A boolean flag that indicates whether or not we did a reduction.</p> */
        final boolean doneReduction;

        /** <p>The {@link Sequent Sequents} that resulted from applying the reduction rules.</p> */
        final Deque<Sequent> resultingSequents;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This creates an object that is used to store the reduction
         * results.</p>
         *
         * @param result {@code true} if we did some kind of reduction,
         *               {@code false} otherwise.
         * @param sequents The {@link Sequent Sequents} that resulted from
         *                 applying the reduction rules.
         */
        ReductionResult(boolean result, Deque<Sequent> sequents) {
            doneReduction = result;
            resultingSequents = sequents;
        }

    }

}