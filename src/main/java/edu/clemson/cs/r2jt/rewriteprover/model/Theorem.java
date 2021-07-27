/*
 * Theorem.java
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
package edu.clemson.cs.r2jt.rewriteprover.model;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.rewriteprover.justifications.Justification;
import edu.clemson.cs.r2jt.rewriteprover.transformations.ExpandAntecedentByImplication;
import edu.clemson.cs.r2jt.rewriteprover.transformations.ExpandAntecedentBySubstitution;
import edu.clemson.cs.r2jt.rewriteprover.transformations.ReplaceTheoremInConsequentWithTrue;
import edu.clemson.cs.r2jt.rewriteprover.transformations.StrengthenConsequent;
import edu.clemson.cs.r2jt.rewriteprover.transformations.SubstituteInPlaceInConsequent;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import edu.clemson.cs.r2jt.misc.Utils.Mapping;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class Theorem implements Conjunct {

    public static final Mapping<Theorem, PExp> UNWRAPPER =
            new TheoremUnwrapper();

    /**
     * <p>
     * Guaranteed not to have a top-level and (otherwise this would be two
     * theorems.)
     * </p>
     */
    private PExp myAssertion;
    private final Justification myJustification;

    public Theorem(PExp assertion, Justification justification) {
        myAssertion = assertion;
        myJustification = justification;
    }

    public PExp getAssertion() {
        return myAssertion;
    }

    public Justification getJustification() {
        return myJustification;
    }

    public List<Transformation> getTransformations() {
        List<Transformation> result = new LinkedList<Transformation>();

        result.add(new ReplaceTheoremInConsequentWithTrue(this));

        if (myAssertion instanceof PSymbol) {
            PSymbol assertionAsPS = (PSymbol) myAssertion;
            if (assertionAsPS.name.equals("implies")) {
                PExp left = assertionAsPS.arguments.get(0);
                PExp right = assertionAsPS.arguments.get(1);

                result.add(new ExpandAntecedentByImplication(this,
                        left.splitIntoConjuncts(), right));
                result.add(new StrengthenConsequent(this,
                        left.splitIntoConjuncts(), right.splitIntoConjuncts()));
            }
            else if (assertionAsPS.name.equals("=")) {
                PExp left = assertionAsPS.arguments.get(0);
                PExp right = assertionAsPS.arguments.get(1);

                result.add(
                        new ExpandAntecedentBySubstitution(this, left, right));
                result.add(
                        new ExpandAntecedentBySubstitution(this, right, left));

                result.add(
                        new SubstituteInPlaceInConsequent(this, left, right));
                result.add(
                        new SubstituteInPlaceInConsequent(this, right, left));
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "" + myAssertion;
    }

    @Override
    public Site toSite(PerVCProverModel m) {
        return new Site(m, this, Collections.EMPTY_LIST, myAssertion);
    }

    @Override
    public PExp getExpression() {
        return myAssertion;
    }

    @Override
    public void setExpression(PExp newValue) {
        myAssertion = newValue;
    }

    @Override
    public boolean editable() {
        return false;
    }

    @Override
    public boolean libraryTheorem() {
        return true;
    }

    private static class TheoremUnwrapper implements Mapping<Theorem, PExp> {

        @Override
        public PExp map(Theorem input) {
            return input.myAssertion;
        }
    }
}
