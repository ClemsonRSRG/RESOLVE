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
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Consequent;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.EliminateTrueConjunctInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.ReplaceSymmetricEqualityWithTrueInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.ReplaceTheoremInConsequentWithTrue;
import java.util.Deque;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>The <code>Simplify</code> automator turns consequents that appear as
 * givens and symmetric equalities into "true" and eliminates true conjuncts 
 * until no more work can be done.</p>
 */
public class Simplify implements Automator {

    public static final Simplify INSTANCE = new Simplify();

    private Simplify() {

    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        //Turn local theorems in the consequent into true
        boolean replacedTheorem = simplifyTheorem(model);

        if (!replacedTheorem) {
            //Turn symmetric equalities into true
            Iterator<Application> symmetricEqualities =
                    ReplaceSymmetricEqualityWithTrueInConsequent.INSTANCE
                            .getApplications(model);

            if (symmetricEqualities.hasNext()) {
                symmetricEqualities.next().apply(model);
            }
            else {
                //Eliminate true conjuncts
                Iterator<Application> trueConjuncts =
                        EliminateTrueConjunctInConsequent.INSTANCE
                                .getApplications(model);

                if (trueConjuncts.hasNext()) {
                    trueConjuncts.next().apply(model);
                }
                else {
                    stack.pop();
                }
            }
        }
    }

    private boolean simplifyTheorem(PerVCProverModel model) {
        //As an optimization we first make sure there's something to find...
        Set<PExp> localTheoremSet = model.getLocalTheoremSet();
        Iterator<Consequent> consequents = model.getConsequentList().iterator();
        boolean foundMatch = false;
        Consequent c = null;
        while (consequents.hasNext() && !foundMatch) {
            c = consequents.next();

            foundMatch = localTheoremSet.contains(c.getExpression());
        }

        if (foundMatch) {
            //Now we actually go find it
            LocalTheorem t = null;
            Iterator<LocalTheorem> theorems =
                    model.getLocalTheoremList().iterator();

            LocalTheorem curTheorem;
            while (t == null && theorems.hasNext()) {
                curTheorem = theorems.next();

                if (curTheorem.getAssertion().equals(c.getExpression())) {
                    t = curTheorem;
                }
            }

            if (t == null) {
                throw new RuntimeException("uhhh...?");
            }

            new ReplaceTheoremInConsequentWithTrue(t).getApplications(model)
                    .next().apply(model);
        }

        return foundMatch;
    }
}
