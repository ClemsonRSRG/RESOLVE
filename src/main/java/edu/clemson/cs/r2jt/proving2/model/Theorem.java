/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

        result.add(new ReplaceTheoremInConsequentWithTrue(this));

        if (myAssertion instanceof PSymbol) {
            PSymbol assertionAsPS = (PSymbol) myAssertion;
            if (assertionAsPS.name.equals("implies")) {
                PExp left = assertionAsPS.arguments.get(0);
                PExp right = assertionAsPS.arguments.get(1);

                result.add(new ExpandAntecedentByImplication(this, left
                        .splitIntoConjuncts(), right));
                result.add(new StrengthenConsequent(this, left
                        .splitIntoConjuncts(), right.splitIntoConjuncts()));
            }
            else if (assertionAsPS.name.equals("=")) {
                PExp left = assertionAsPS.arguments.get(0);
                PExp right = assertionAsPS.arguments.get(1);

                result
                        .add(new ExpandAntecedentBySubstitution(this, left,
                                right));
                result
                        .add(new ExpandAntecedentBySubstitution(this, right,
                                left));

                result
                        .add(new SubstituteInPlaceInConsequent(this, left,
                                right));
                result
                        .add(new SubstituteInPlaceInConsequent(this, right,
                                left));
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
