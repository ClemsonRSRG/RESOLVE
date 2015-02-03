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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.RemoveAntecedent;
import edu.clemson.cs.r2jt.proving2.transformations.SubstituteInPlaceInAntecedent;
import edu.clemson.cs.r2jt.proving2.transformations.SubstituteInPlaceInConsequent;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class VariablePropagator implements Automator {

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        Iterator<LocalTheorem> localTheorems =
                model.getLocalTheoremList().iterator();

        PExp variable = null;
        PExp expansion = null;
        int antecedentIndex = -1;
        PExp curTheoremAssertion;
        LocalTheorem curTheorem = null;
        while (variable == null && localTheorems.hasNext()) {
            curTheorem = localTheorems.next();
            curTheoremAssertion = curTheorem.getAssertion();
            antecedentIndex++;

            if (curTheoremAssertion instanceof PSymbol) {
                PSymbol curTheoremSymbol = (PSymbol) curTheoremAssertion;

                if (curTheoremSymbol.name.equals("=")) {
                    PExp left = curTheoremSymbol.arguments.get(0);
                    PExp right = curTheoremSymbol.arguments.get(1);

                    variable = getSingleVariable(left);
                    if (variable == null) {
                        variable = getSingleVariable(right);

                        if (variable != null) {
                            expansion = left;
                        }
                    }
                    else {
                        expansion = right;
                    }
                }
            }
        }

        if (variable == null) {
            stack.pop();
        }
        else {
            List<Automator> steps = new LinkedList<Automator>();

            if (!variable.equals(expansion)) {
                steps.add(new ApplyAll(new SubstituteInPlaceInAntecedent(
                        curTheorem, variable, expansion)));
                steps.add(new ApplyAll(new SubstituteInPlaceInConsequent(
                        curTheorem, variable, expansion)));
            }
            steps.add(new ApplyN(new RemoveAntecedent(model, curTheorem), 1));
            stack.push(new PushSequence(steps));
        }
    }

    /**
     * <p>Takes an expression and returns the same expression if it represents
     * a single, unquantified variable.  Otherwise it returns null.</p>
     * @param e
     * @return 
     */
    private PExp getSingleVariable(PExp e) {
        PExp result = null;

        if (e instanceof PSymbol) {
            PSymbol eAsPSymbol = (PSymbol) e;

            if (eAsPSymbol.arguments.size() == 0
                    && eAsPSymbol.quantification
                            .equals(PSymbol.Quantification.NONE)
                    && !eAsPSymbol.isLiteral()) {
                result = eAsPSymbol;
            }
        }

        return result;
    }
}
