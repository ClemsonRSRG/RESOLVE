/*
 * PExpWithScore.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.utilities;

import edu.clemson.rsrg.prover.absyn.PExp;
import java.util.Map;

/**
 * <p>
 * This class allows us to create a comparable theorem with a score.
 * </p>
 *
 * @author Mike Kabbani
 *
 * @version 2.0
 */
public class PExpWithScore implements Comparable<PExpWithScore> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A theorem expression
     * </p>
     */
    private final PExp myTheorem;

    /**
     * <p>
     * The theorem's definition.
     * </p>
     */
    private final String myTheoremDefinitionString;

    /**
     * <p>
     * A comparison score.
     * </p>
     */
    private final Integer myScore;

    /**
     * <p>
     * A map of symbols in this theorem.
     * </p>
     */
    private final Map<String, String> myBMap;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a new expression that includes a score.
     * </p>
     *
     * @param theorem
     *            A theorem represented as a {@link PExp}.
     * @param bMap
     *            A map of symbols in this theorem.
     * @param justification
     *            The theorem definition expressed as a string.
     */
    public PExpWithScore(PExp theorem, Map<String, String> bMap, String justification) {
        myBMap = bMap;
        myScore = 1;
        myTheorem = theorem;
        myTheoremDefinitionString = justification;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Compares <code>this<code> and <code>o</code>.
     * </p>
     *
     * @param o
     *            Another {@link PExpWithScore}.
     *
     * @return Comparison results expressed as an integer.
     */
    @Override
    public final int compareTo(PExpWithScore o) {
        return myScore - o.myScore;
    }

    /**
     * <p>
     * This method returns a theorem expression.
     * </p>
     *
     * @return A {@link PExp}.
     */
    public final PExp getTheorem() {
        return myTheorem;
    }

    /**
     * <p>
     * This method returns the theorem definition.
     * </p>
     *
     * @return A string.
     */
    public final String getTheoremDefinitionString() {
        return myTheoremDefinitionString;
    }

    /**
     * <p>
     * This method returns this expression in string format.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String toString() {
        return myTheoremDefinitionString + "\n" + "\t[" + myScore + "]" + " " + myTheorem.toString() + "\t" + myBMap
                + "\n";
    }
}
