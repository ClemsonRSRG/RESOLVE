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
package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.Dec;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.InfixExp;
import edu.clemson.cs.r2jt.absyn.MathAssertionDec;
import edu.clemson.cs.r2jt.absyn.MathModuleDec;
import edu.clemson.cs.r2jt.absyn.VarExp;

public class TheoremToVCsConverter implements Iterable<VerificationCondition> {

    private List<VerificationCondition> myVCs =
            new LinkedList<VerificationCondition>();

    public TheoremToVCsConverter(MathModuleDec m) {
        List<Dec> declarations = m.getDecs();

        for (Dec d : declarations) {
            if (d instanceof MathAssertionDec) {
                MathAssertionDec dAsMathAssertionDec = (MathAssertionDec) d;

                if (dAsMathAssertionDec.getKind() != MathAssertionDec.AXIOM) {
                    Exp assertion = dAsMathAssertionDec.getAssertion();
                    assertion = Utilities.applyQuantification(assertion);

                    VerificationCondition finalVC =
                            asVerificationCondition(dAsMathAssertionDec
                                    .getName().getName(), assertion);

                    myVCs.add(finalVC);
                }
            }
        }
    }

    private static VerificationCondition asVerificationCondition(String name,
            Exp e) {

        Exp antecedent, consequent;

        if (e instanceof InfixExp
                && ((InfixExp) e).getOpName().getName().equals("implies")) {

            InfixExp eAsInfixExp = (InfixExp) e;
            antecedent = eAsInfixExp.getLeft();
            consequent = eAsInfixExp.getRight();
        }
        else {
            antecedent = Exp.getTrueVarExp(e.getMathType().getTypeGraph());
            consequent = e;
        }

        antecedent = eliminateUniversalQuantifiers(antecedent);
        consequent = eliminateUniversalQuantifiers(consequent);

        return new VerificationCondition(antecedent, consequent, name);
    }

    private static Exp eliminateUniversalQuantifiers(Exp e) {
        Exp copy = Exp.copy(e);

        eliminateUniversalQuantifiersInPlace(copy);

        return copy;
    }

    private static void eliminateUniversalQuantifiersInPlace(Exp e) {
        if (e instanceof VarExp) {
            VarExp eAsVarExp = (VarExp) e;
            if (eAsVarExp.getQuantification() == VarExp.FORALL) {
                eAsVarExp.setQuantification(VarExp.NONE);
            }
        }
        else {
            for (Exp subexp : e.getSubExpressions()) {
                eliminateUniversalQuantifiersInPlace(subexp);
            }
        }
    }

    @Override
    public Iterator<VerificationCondition> iterator() {
        return myVCs.iterator();
    }

}
