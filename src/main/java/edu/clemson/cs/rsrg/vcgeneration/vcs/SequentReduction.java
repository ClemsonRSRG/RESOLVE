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

            // Apply the left reduction rules

            // Apply the right reduction rules

            // Check to see if we have a sequent with atomic formulas
            // on both sides. If yes, we are done reducing the sequent.
            // Otherwise, keep reducing it!
            /*if (seq.consistOfAtomicFormulas()) {
                reducedSequents.add(seq);
            }
            else {
                sequentsToBeReduced.addFirst(seq);
            }*/
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

}