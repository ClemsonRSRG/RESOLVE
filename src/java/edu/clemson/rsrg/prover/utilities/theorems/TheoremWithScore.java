/*
 * TheoremWithScore.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.utilities.theorems;

/**
 * <p>
 * This class represents a <em>Theorem</em> with a score attached to it.
 * </p>
 *
 * @author Mike Kabbani
 *
 * @version 2.0
 */
public class TheoremWithScore implements Comparable<TheoremWithScore> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A <em>Theorem</em>
     * </p>
     */
    private final Theorem myTheorem;

    /**
     * <p>
     * An integer score ranking the theorem.
     * </p>
     */
    private Integer myScore;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This objects stores a <em>Theorem</em> and assigns it with a score of 1.
     * </p>
     *
     * @param t
     *            A theorem.
     */
    public TheoremWithScore(Theorem t) {
        myTheorem = t;
        myScore = 1;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method implements the method in {@link Comparable}.
     * </p>
     *
     * @param o
     *            Object to be compared.
     *
     * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object.
     */
    @Override
    public final int compareTo(TheoremWithScore o) {
        return myScore - o.myScore;
    }

    /**
     * <p>
     * This method must be implemented by all inherited classes to override the default equals method implementation.
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

        TheoremWithScore that = (TheoremWithScore) o;

        if (!myTheorem.equals(that.myTheorem))
            return false;
        return myScore.equals(that.myScore);
    }

    /**
     * <p>
     * This method returns the inner representation of a <em>Theorem</em>.
     * </p>
     *
     * @return A theorem.
     */
    public final Theorem getTheorem() {
        return myTheorem;
    }

    /**
     * <p>
     * This method returns the current score assigned to the <em>Theorem</em>.
     * </p>
     *
     * @return An integer score.
     */
    public final Integer getTheoremScore() {
        return myScore;
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
        int result = myTheorem.hashCode();
        result = 31 * result + myScore.hashCode();
        return result;
    }

    /**
     * <p>
     * This method returns the current expression in string format.
     * </p>
     *
     * @return Current {@link TheoremWithScore} as a string.
     */
    @Override
    public final String toString() {
        return "TheoremWithScore{" + "myTheorem=" + myTheorem + ", myScore=" + myScore + '}';
    }

    /**
     * <p>
     * This method updates the <em>Theorem</em>'s score.
     * </p>
     *
     * @param score
     *            An updated value for the integer score.
     */
    public final void updateTheoremScore(int score) {
        myScore = score;
    }

}
