/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.model;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.justifications.Justification;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentByImplication;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentBySubstitution;
import edu.clemson.cs.r2jt.proving2.transformations.ReplaceTheoremInConsequentWithTrue;
import edu.clemson.cs.r2jt.proving2.transformations.StrengthenConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.SubstituteInPlaceInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.utilities.Mapping;
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
     * <p>Guaranteed not to have a top-level and (otherwise this would be two
     * theorems.)</p>
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

        result.add(new ReplaceTheoremInConsequentWithTrue(myAssertion));

        if (myAssertion instanceof PSymbol) {
            PSymbol assertionAsPS = (PSymbol) myAssertion;
            if (assertionAsPS.name.equals("implies")) {
                PExp left = assertionAsPS.arguments.get(0);
                PExp right = assertionAsPS.arguments.get(1);

                result.add(new ExpandAntecedentByImplication(left
                        .splitIntoConjuncts(), right));
                result.add(new StrengthenConsequent(left.splitIntoConjuncts(),
                        right.splitIntoConjuncts()));
            }
            else if (assertionAsPS.name.equals("=")) {
                PExp left = assertionAsPS.arguments.get(0);
                PExp right = assertionAsPS.arguments.get(1);

                result
                        .add(new ExpandAntecedentBySubstitution(left, right,
                                this));
                result
                        .add(new ExpandAntecedentBySubstitution(right, left,
                                this));

                result.add(new SubstituteInPlaceInConsequent(left, right));
                result.add(new SubstituteInPlaceInConsequent(right, left));
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

    private static class TheoremUnwrapper implements Mapping<Theorem, PExp> {

        @Override
        public PExp map(Theorem input) {
            return input.myAssertion;
        }
    }
}
