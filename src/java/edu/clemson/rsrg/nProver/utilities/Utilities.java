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
import edu.clemson.rsrg.nProver.registry.CongruenceClassRegistry;
import edu.clemson.rsrg.nProver.utilities.treewakers.*;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
import java.util.LinkedHashMap;
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

        // NM: 0, 1 are spared for succedent = (0), <=(1) and can expand with
        // with more reflexive operators
        CongruenceClassRegistry<Integer, String, String, String> registry = new CongruenceClassRegistry<>(1000, 1000,
                1000, 1000);
        Map<String, Integer> expLabels = new LinkedHashMap<>();

        // Visit antecedents
        RegisterAntecedent regAntecedent = new RegisterAntecedent(registry, expLabels, 2);
        for (Exp exp : sequent.getAntecedents()) {
            TreeWalker.visit(regAntecedent, exp);
        }

        // Visit consequents
        RegisterAntecedent regConsequent = new RegisterAntecedent(regAntecedent.getRegistry(),
                regAntecedent.getExpLabels(), regAntecedent.getNextLabel()); // YS: change this to the correct
                                                                             // constructor.
        for (Exp exp : sequent.getConcequents()) {
            TreeWalker.visit(regConsequent, exp);
        }

        return regConsequent.getExpLabels();
    }

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
    public static boolean proveTargetVC(VerificationCondition condition) {
        Sequent sequent = condition.getSequent();
        RegisterSequent registration = new RegisterSequent();

        // Visit antecedents
        for (Exp exp : sequent.getAntecedents()) {
            TreeWalker.visit(registration, exp);
        }
        registration.setAntecedentExp(false);

        // Visit consequents
        for (Exp exp : sequent.getConcequents()) {
            TreeWalker.visit(registration, exp);
        }

        return true;
    }
}