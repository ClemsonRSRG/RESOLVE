/*
 * ValidSharedStateChecker.java
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
package edu.clemson.rsrg.parsing.sanitychecking;

import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import java.util.List;

/**
 * <p>
 * This is a sanity checker for making sure the {@link SharedStateDec} has valid a {@code initialization ensures}
 * clause.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class ValidSharedStateChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The shared state declaration we are checking.
     * </p>
     */
    private final SharedStateDec mySharedStateDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates a sanity checker for checking the {@code initialization} assertion clause is valid.
     * </p>
     *
     * @param sharedStateDec
     *            The encountered shared state declaration.
     */
    public ValidSharedStateChecker(SharedStateDec sharedStateDec) {
        mySharedStateDec = sharedStateDec;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Checks to see if the {@link SharedStateDec SharedStateDec's} {@code initialization} is valid.
     * </p>
     *
     * @throws SourceErrorException
     *             This is thrown when we encounter an invalid {@code initialization} assertion clause.
     */
    public final void hasValidAssertionClauses() {
        // Loop through our list of math variable declarations
        List<MathVarDec> mathVarDecList = mySharedStateDec.getAbstractStateVars();
        AssertionClause initEnsuresClause = mySharedStateDec.getInitialization().getEnsures();
        for (MathVarDec mathVarDec : mathVarDecList) {
            // #mathVardec
            String mathVarDecAsString = mathVarDec.getName().getName();
            String oldMathVarDec = "#" + mathVarDecAsString;

            // Check initialization ensures clause
            if (initEnsuresClause.getAssertionExp().containsVar(mathVarDecAsString, true)) {
                throw new SourceErrorException(
                        "Initialization ensures clause cannot be expressed using '" + oldMathVarDec + "'.",
                        initEnsuresClause.getLocation());
            }

            // Check any which_entails clauses inside initialization ensures clause
            if (initEnsuresClause.getWhichEntailsExp() != null
                    && initEnsuresClause.getWhichEntailsExp().containsVar(mathVarDecAsString, true)) {
                throw new SourceErrorException(
                        "Initialization ensures clause cannot contain an 'which_entails' clause that uses '"
                                + oldMathVarDec + "'.",
                        initEnsuresClause.getWhichEntailsExp().getLocation());
            }
        }
    }

}
