/*
 * Metrics.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.output;

/**
 * <p>
 * The {@code Metrics} class contains aggregate data about a full proof attempt.
 * </p>
 *
 * @author Hampton Smith
 * @author Mike Kabbani
 *
 * @version 2.0
 */
public class Metrics {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Time in milliseconds spent on proving this {@code VC}.
     * </p>
     */
    private final long myProofDuration;

    /**
     * <p>
     * Maximum time in milliseconds that the prover attempts to prove this {@code VC}.
     * </p>
     */
    private final long myTimeout;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs an object for keeping track of the aggregate data about proving a {@code VC}.
     * </p>
     *
     * @param duration
     *            Total time spend proving this {@code VC}.
     * @param timeout
     *            The specified timeout before giving up.
     */
    public Metrics(long duration, long timeout) {
        myProofDuration = duration;
        myTimeout = timeout;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

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

        Metrics metrics = (Metrics) o;

        if (myProofDuration != metrics.myProofDuration)
            return false;
        return myTimeout == metrics.myTimeout;
    }

    /**
     * <p>
     * This method returns the timeout in milliseconds.
     * </p>
     *
     * @return A number.
     */
    public final long getTimeout() {
        return myTimeout;
    }

    /**
     * <p>
     * This method returns the time spent proving this {@code VC}.
     * </p>
     *
     * @return A number.
     */
    public final long getProofDuration() {
        return myProofDuration;
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
        int result = (int) (myProofDuration ^ (myProofDuration >>> 32));
        result = 31 * result + (int) (myTimeout ^ (myTimeout >>> 32));
        return result;
    }

}
