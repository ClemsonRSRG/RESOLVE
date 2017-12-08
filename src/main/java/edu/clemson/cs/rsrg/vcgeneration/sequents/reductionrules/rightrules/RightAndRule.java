/*
 * RightAndRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.rightrules;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.InfixExp;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.AbstractReductionRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.ReductionRuleApplication;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class contains the logic for applying the {@code right and}
 * rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class RightAndRule extends AbstractReductionRuleApplication
        implements
            ReductionRuleApplication {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code right and}
     * rule.</p>
     *
     * @param originalSequent The original {@link Sequent} that contains
     *                        the expression to be reduced.
     * @param originalExp The {@link Exp} to be reduced.
     */
    public RightAndRule(Sequent originalSequent, Exp originalExp) {
        super(originalSequent, originalExp);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Sequent Reduction Rule}.</p>
     *
     * @return A list of {@link Sequent Sequents} that resulted
     * from applying the rule.
     */
    @Override
    public final List<Sequent> applyRule() {
        if (myOriginalExp instanceof InfixExp) {
            InfixExp originalExpAsInfixExp = (InfixExp) myOriginalExp;
            List<Exp> newConsequents1 = new ArrayList<>();
            List<Exp> newConsequents2 = new ArrayList<>();
            for (Exp exp : myOriginalSequent.getConcequents()) {
                if (exp.equals(originalExpAsInfixExp)) {
                    // Add the left and right into the different antecedent lists
                    if (originalExpAsInfixExp.getOperatorAsString().equals("and")) {
                        newConsequents1.add(originalExpAsInfixExp.getLeft().clone());
                        newConsequents2.add(originalExpAsInfixExp.getRight().clone());
                    }
                    // This must be an error!
                    else {
                        unexpectedExp();
                    }
                }
                // Don't do anything to the other expressions.
                else {
                    newConsequents1.add(exp.clone());
                    newConsequents2.add(exp.clone());
                }
            }

            // Construct new sequents
            // YS: resultingSequent2 clones the location, because this is a new VC that
            // we are trying to prove. Simply proving resultingSequent1 is not good enough.
            Sequent resultingSequent1 = new Sequent(myOriginalSequent.getLocation(),
                    myOriginalSequent.getAntecedents(), newConsequents1);
            myResultingSequents.add(resultingSequent1);
            Sequent resultingSequent2 = new Sequent(myOriginalSequent.getLocation().clone(),
                    myOriginalSequent.getAntecedents(), newConsequents2);
            myResultingSequents.add(resultingSequent2);
        }
        // This must be an error!
        else {
            unexpectedExp();
        }

        return myResultingSequents;
    }

    /**
     * <p>This method returns a description associated with
     * the {@code Sequent Reduction Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Right And Rule";
    }

}