/*
 * Utilities.java
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
package edu.clemson.rsrg.nProver.utilities;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.nProver.GeneralPurposeProver;
import edu.clemson.rsrg.nProver.utilities.treewakers.ExpLabeler;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
import java.util.Map;

/**
 * <p>
 * This class contains a bunch of utilities methods used by the {@link GeneralPurposeProver} and all of its associated
 * {@link TreeWalkerVisitor TreeWalkerVisitors}.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Nicodemus Msafiri J. M.
 *
 * @version 1.0
 */
public class Utilities {

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Given a {@link VerificationCondition}, convert all relevant expression to a numeric label.
     * </p>
     *
     * @param condition
     *            Current verification condition.
     *
     * @return A mapping for all the expression in this verification condition.
     */
    public static Map<String, Integer> convertExpToLabel(VerificationCondition condition) {
        Sequent sequent = condition.getSequent();
        ExpLabeler labeler = new ExpLabeler();

        // Visit antecedents
        for (Exp exp : sequent.getAntecedents()) {
            TreeWalker.visit(labeler, exp);
        }

        // Visit consequents
        for (Exp exp : sequent.getConcequents()) {
            TreeWalker.visit(labeler, exp);
        }

        return labeler.getExpLabels();
    }

}