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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class contains logic for reducing a {@link Sequent}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SequentReduction {

    // ===========================================================
    // Member Fields
    // ===========================================================

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
        myOriginalSequent = sequent;
        myResultingSequents = new ArrayList<>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

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

        return myOriginalSequent.equals(that.myOriginalSequent)
                && myResultingSequents.equals(that.myResultingSequents);
    }

    /**
     * <p>This method returns the {@code sequents} that
     * resulted from applying the reduction rules.</p>
     *
     * @return A list of {@link Sequent Sequents}.
     */
    public final List<Sequent> getResultingSequents() {
        return myResultingSequents;
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myOriginalSequent.hashCode();
        result = 31 * result + myResultingSequents.hashCode();
        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

}