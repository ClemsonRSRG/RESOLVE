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
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.Antecedent;
import edu.clemson.cs.r2jt.proving2.Consequent;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mike on 4/3/2014.
 */
public class VerificationConditionCongruenceClosureImpl {

    private final Registry m_registry;
    public final String m_name;
    private final Antecedent m_antecedent;
    private final Consequent m_consequent;
    private final ConjunctionOfNormalizedAtomicExpressions m_conjunction;
    private final List<List<String>> m_goal; // every item in each sublist is equivalent iff proved.  Disjunctions in consequent are split into seperate vc's before we see them here.

    public static enum STATUS {
        FALSE_ASSUMPTION, STILL_EVALUATING, PROVED, UNPROVABLE
    }

    public List<PExp> forAllQuantifiedPExps; // trap constraints, can create Theorems externally from this

    // currently support only unchained equalities, so each sublist is size 2.
    public VerificationConditionCongruenceClosureImpl(TypeGraph g, VC vc) {
        m_name = vc.getName();
        m_antecedent = vc.getAntecedent();
        m_consequent = vc.getConsequent();
        m_registry = new Registry(g);
        m_conjunction =
                new ConjunctionOfNormalizedAtomicExpressions(m_registry);
        m_goal = new ArrayList<List<String>>();
        forAllQuantifiedPExps = new ArrayList<PExp>();
        addPExp(m_antecedent.iterator(), true);
        addPExp(m_consequent.iterator(), false);
    }

    protected ConjunctionOfNormalizedAtomicExpressions getConjunct() {
        return m_conjunction;
    }

    public Registry getRegistry() {
        return m_registry;
    }

    protected Set<String> getFunctionNames() {
        return m_registry.getFunctionNames();
    }

    protected Map<String, Integer> getGoalSymbols() {
        HashSet<String> goalSymbolSet = new HashSet<String>();
        for (List<String> agoalList : m_goal) {
            for (String agoal : agoalList) {
                // true is the root of many expressions
                if (agoal.equals("true"))
                    continue;
                goalSymbolSet.add(agoal);
            }
        }
        return m_conjunction.getSymbolProximity(goalSymbolSet);
    }

    public STATUS isProved() {
        if (m_conjunction.m_evaluates_to_false)
            return STATUS.FALSE_ASSUMPTION;
        for (List<String> g : m_goal) {
            // check each goal has same root
            if (!g.get(0).equals(g.get(1))) // diff symbols, same root?
            {
                if (m_registry.getIndexForSymbol(g.get(0)) != m_registry
                        .getIndexForSymbol(g.get(1))) // can avoid this check by updating goal on merges
                {
                    String g0 =
                            m_registry.getSymbolForIndex(m_registry
                                    .getIndexForSymbol(g.get(0)));
                    String g1 =
                            m_registry.getSymbolForIndex(m_registry
                                    .getIndexForSymbol(g.get(1)));

                    //if((g0.equals("true") && g1.equals("false")) || (g0.equals("false") && g1.equals("true"))) return STATUS.UNPROVABLE;
                    return STATUS.STILL_EVALUATING; // not proved yet
                }
            }
        }
        return STATUS.PROVED;
    }

    private void addPExp(Iterator<PExp> pit, boolean inAntecedent) {
        while (pit.hasNext()) {
            PExp curr = pit.next();
            if (!curr.getQuantifiedVariables().isEmpty()
                    && !curr.getSymbolNames().contains("lambda")) {
                forAllQuantifiedPExps.add(curr);
            }
            else if (curr.isEquality()) { // f(x,y) = z and g(a,b) = c ; then z is replaced by c
                PExp lhs = curr.getSubExpressions().get(0);
                PExp rhs = curr.getSubExpressions().get(1);
                int lhsIndex = (m_conjunction.addFormula(lhs));
                int rhsIndex = (m_conjunction.addFormula(rhs));
                if (inAntecedent) {
                    m_conjunction.mergeOperators(lhsIndex, rhsIndex);
                }
                else {
                    addGoal(m_registry.getSymbolForIndex(lhsIndex), m_registry
                            .getSymbolForIndex(rhsIndex));
                }
            }
            else { // P becomes P = true or P(x...) becomes P(x ...) = z and z is replaced by true
                int intRepForExp = m_conjunction.addFormula(curr);
                if (inAntecedent) {
                    m_conjunction.mergeOperators(m_registry
                            .getIndexForSymbol("true"), intRepForExp);
                }
                else {
                    addGoal(m_registry.getSymbolForIndex(intRepForExp), "true");
                }
            }

        }

    }

    private void addGoal(String a, String b) {
        List<String> newGoal = new ArrayList<String>(2);
        newGoal.add(a);
        newGoal.add(b);
        m_goal.add(newGoal);
    }

    @Override
    public String toString() {
        String r = m_name + "\n" + m_conjunction;
        r += "----------------------------------\n";
        for (List<String> gl : m_goal) {
            String ro0 =
                    m_registry.getSymbolForIndex(m_registry
                            .getIndexForSymbol(gl.get(0)));
            String ro1 =
                    m_registry.getSymbolForIndex(m_registry
                            .getIndexForSymbol(gl.get(1)));
            r += ro0 + "=" + ro1 + "\n";
        }

        return r;
    }

}
