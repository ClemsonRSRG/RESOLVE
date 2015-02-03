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
package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.absyn.DotExp;
import edu.clemson.cs.r2jt.absyn.EqualsExp;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.VarExp;
import edu.clemson.cs.r2jt.collections.List;

public class VerificationCondition {

    private String myName;
    private Conjuncts myAntecedents, myConsequents;

    public VerificationCondition(Exp antecedent, Exp consequent, String name) {
        myAntecedents = new Conjuncts(antecedent);
        myConsequents = new Conjuncts(consequent);
        myName = name;
    }

    public VerificationCondition(Exp antecedent, Exp consequent) {
        this(antecedent, consequent, null);
    }

    public VerificationCondition(List<Exp> antecedents, List<Exp> consequents,
            String name) {
        myAntecedents = new Conjuncts(antecedents);
        myConsequents = new Conjuncts(consequents);
        myName = name;
    }

    public VerificationCondition(List<Exp> antecedents, List<Exp> consequents) {
        this(antecedents, consequents, null);
    }

    public int getNumConsequents() {
        return myConsequents.size();
    }

    public int getNumAntecedents() {
        return myAntecedents.size();
    }

    public VerificationCondition copy() {
        List<Exp> newAntecedents = new List<Exp>();
        List<Exp> newConsequents = new List<Exp>();

        for (Exp a : myAntecedents) {
            newAntecedents.add(Exp.copy(a));
        }

        for (Exp c : myConsequents) {
            newConsequents.add(Exp.copy(c));
        }

        return new VerificationCondition(newAntecedents, newConsequents, myName);
    }

    public void setAntecedents(Conjuncts antecedents) {
        myAntecedents = antecedents;
    }

    public void setConsequents(Conjuncts consequents) {
        myConsequents = consequents;
    }

    public Conjuncts getAntecedents() {
        return myAntecedents;
    }

    public Conjuncts getConsequents() {
        return myConsequents;
    }

    public String getName() {
        return myName;
    }

    public void simplify() {
        myAntecedents.eliminateObviousConjunctsInPlace();
        myConsequents.eliminateObviousConjunctsInPlace();

        myAntecedents.eliminateRedundantConjuncts();
        myConsequents.eliminateRedundantConjuncts();

        for (Exp e : myAntecedents) {
            myConsequents.eliminateEquivalentConjunctsInPlace(e);
        }
    }

    public boolean equals(Object o) {
        boolean retval = o instanceof VerificationCondition;

        if (retval) {
            VerificationCondition otherVC = (VerificationCondition) o;
            retval =
                    (otherVC.getAntecedents().equivalent(myAntecedents))
                            && (otherVC.getConsequents()
                                    .equivalent(myConsequents));
        }

        return retval;
    }

    public String toString() {
        return myAntecedents + "===============================>\n"
                + myConsequents;
    }

    /**
     * <p>Eliminates expansions in <code>assumedExps</code> by substituting
     * their equivalent expression throughout <code>assumedExps</code> and
     * <code>toConfirmExps</code>.  This is done in place.  For more discussion 
     * on expansions, see <code>retrieveExpansion()</code>.</p>
     * 
     * @param assumedExps A list of expressions from which expansions should
     *                    be eliminated and which should be the target of
     *                    subsitutions by those expansions.
     * @param toConfirmExps A list of expressions which should be the target of
     *                      any substitutions.
     */
    public void propagateExpansionsInPlace() {

        EqualsExp curExpansion = retrieveExpansion();

        MatchReplace curMatcher;
        while (curExpansion != null) {
            curMatcher =
                    new DirectReplace(curExpansion.getLeft(), curExpansion
                            .getRight());

            MatchApplicator.applyAllInPlace(myAntecedents, curMatcher);
            MatchApplicator.applyAllInPlace(myConsequents, curMatcher);

            curExpansion = retrieveExpansion();
        }
    }

    /**
     * <p>Finds, removes, and returns the first expression in 
     * <code>assumedExps</code> that represents an expansion.  An expansion is
     * an equality of the form <code>X = &lt;expression&gt;</code> or
     * <code>&lt;expression&gt; = X</code>, where X is a variable name. If there
     * is no such element in <code>assumedExps</code>, returns 
     * <code>null</code>.</p>
     * 
     * @param assumedExps The list of expressions to search.
     * @return The first expansion in the list, or <code>null</code> if there is
     *         no expansion in the list.
     */
    private EqualsExp retrieveExpansion() {
        Iterator<Exp> iter = myAntecedents.iterator();

        EqualsExp retval = null;
        Exp curExp;
        while (iter.hasNext() && retval == null) {
            curExp = iter.next();
            if (curExp instanceof EqualsExp) {
                EqualsExp expAsEquals = (EqualsExp) curExp;

                if (expAsEquals.getOperator() == EqualsExp.EQUAL) {
                    if (expAsEquals.getLeft() instanceof VarExp
                            || expAsEquals.getLeft() instanceof DotExp) {
                        retval = expAsEquals;
                        iter.remove();
                    }
                    else if (expAsEquals.getRight() instanceof VarExp
                            || expAsEquals.getRight() instanceof DotExp) {
                        retval = (EqualsExp) Exp.clone(expAsEquals);
                        Exp swapSpace = retval.getRight();
                        retval.setRight(retval.getLeft());
                        retval.setLeft(swapSpace);

                        iter.remove();
                    }
                }
            }
        }

        return retval;
    }
}
