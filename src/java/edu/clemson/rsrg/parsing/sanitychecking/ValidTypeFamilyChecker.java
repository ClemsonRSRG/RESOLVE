/*
 * ValidTypeFamilyChecker.java
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
package edu.clemson.rsrg.parsing.sanitychecking;

import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;

/**
 * <p>
 * This is a sanity checker for making sure the {@link TypeFamilyDec} has valid {@code initialization} and
 * {@code finalization ensures} clauses.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class ValidTypeFamilyChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The type family declaration we are checking.
     * </p>
     */
    private final TypeFamilyDec myTypeFamilyDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates a sanity checker for checking the {@code initialization} and {@code finalization} assertion clauses are
     * valid.
     * </p>
     *
     * @param typeFamilyDec
     *            The encountered type family declaration.
     */
    public ValidTypeFamilyChecker(TypeFamilyDec typeFamilyDec) {
        myTypeFamilyDec = typeFamilyDec;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Checks to see if the {@link TypeFamilyDec TypeFamilyDec's} {@code initialization} and {@code finalization} are
     * valid.
     * </p>
     *
     * @throws SourceErrorException
     *             This is thrown when we encounter an invalid {@code initialization} or {@code finalization} assertion
     *             clause.
     */
    public final void hasValidAssertionClauses() {
        // Exemplar and #Exemplar
        String exemplarAsString = myTypeFamilyDec.getExemplar().getName();
        String oldExemplar = "#" + exemplarAsString;

        // Check initialization ensures clause
        AssertionClause initEnsuresClause = myTypeFamilyDec.getInitialization().getEnsures();
        if (initEnsuresClause.getAssertionExp().containsVar(exemplarAsString, true)) {
            throw new SourceErrorException(
                    "Initialization ensures clause cannot be expressed using '" + oldExemplar + "'.",
                    initEnsuresClause.getLocation());
        }

        // Check any which_entails clauses inside initialization ensures clause
        if (initEnsuresClause.getWhichEntailsExp() != null
                && initEnsuresClause.getWhichEntailsExp().containsVar(exemplarAsString, true)) {
            throw new SourceErrorException(
                    "Initialization ensures clause cannot contain an 'which_entails' clause that uses '" + oldExemplar
                            + "'.",
                    initEnsuresClause.getWhichEntailsExp().getLocation());
        }

        // Check finalization ensures clause
        AssertionClause finalEnsuresClause = myTypeFamilyDec.getFinalization().getEnsures();
        if (finalEnsuresClause.getAssertionExp().containsVar(exemplarAsString, false)) {
            throw new SourceErrorException(
                    "Finalization ensures clause cannot be expressed using '" + exemplarAsString + "'.",
                    finalEnsuresClause.getLocation());
        }

        // Check any which_entails clauses inside finalization ensures clause
        if (finalEnsuresClause.getWhichEntailsExp() != null
                && finalEnsuresClause.getWhichEntailsExp().containsVar(exemplarAsString, false)) {
            throw new SourceErrorException(
                    "Finalization ensures clause cannot contain an 'which_entails' clause that uses '"
                            + exemplarAsString + "'.",
                    finalEnsuresClause.getWhichEntailsExp().getLocation());
        }
    }

}
