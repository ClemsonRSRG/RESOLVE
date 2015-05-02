/**
 * VerificationConditionCongruenceClosureImpl.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.misc.Flag;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.Antecedent;
import edu.clemson.cs.r2jt.rewriteprover.Consequent;
import edu.clemson.cs.r2jt.rewriteprover.VC;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTProper;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.ArrayList;
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
    private final TypeGraph m_typegraph;
    public final String m_name;
    private final Antecedent m_antecedent;
    private final Consequent m_consequent;
    private final ConjunctionOfNormalizedAtomicExpressions m_conjunction;
    protected final List<String> m_goal;

    public static enum STATUS {
        FALSE_ASSUMPTION, STILL_EVALUATING, PROVED, UNPROVABLE
    }

    public List<PExp> forAllQuantifiedPExps; // trap constraints, can create Theorems externally from this

    // currently support only unchained equalities, so each sublist is size 2.
    public VerificationConditionCongruenceClosureImpl(TypeGraph g, VC vc) {
        m_typegraph = g;
        m_name = vc.getName();
        m_antecedent = vc.getAntecedent();
        m_consequent = vc.getConsequent();
        m_registry = new Registry(g);
        m_conjunction =
                new ConjunctionOfNormalizedAtomicExpressions(m_registry);
        m_goal = new ArrayList<String>();
        forAllQuantifiedPExps = new ArrayList<PExp>();
        addPExp(m_antecedent.iterator(), true);
        addPExp(m_consequent.iterator(), false);
        naturalNumsFix();
    }

    protected void naturalNumsFix() {
        if (m_registry.m_typeDictionary.containsKey("N")) {
            MTType natType = m_registry.m_typeDictionary.get("N");
            Set<String> natSymbols = m_registry.getSetMatchingType(natType);

            for (String s : natSymbols) {
                m_conjunction.natToZ(s);
            }
        }
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
        for (String goal : m_goal) {
            // true is the root of many expressions
            if (goal.equals("true"))
                continue;
            goalSymbolSet.add(goal);

        }
        Map<String, Integer> rMap =
                m_conjunction.getSymbolProximity(goalSymbolSet);
        /*HashMap<String,Integer> rMap = new HashMap<String, Integer>();
        for(String s : tmpMap.keySet()){
            if(!m_registry.getUsage(s).equals(Registry.Usage.HASARGS_SINGULAR)){
                rMap.put(s,tmpMap.get(s));
            }
        }*/

        // remove function names
        return rMap;
    }

    public STATUS isProved() {
        if (m_conjunction.m_evaluates_to_false)
            return STATUS.FALSE_ASSUMPTION; // this doesn't mean P->Q = False, it just means P = false
        String goal1 = m_goal.get(0);
        String goal2 = m_goal.get(1);
        // check each goal has same root
        if (!goal1.equals(goal2)) // diff symbols, same root?
        {
            if (m_registry.getIndexForSymbol(goal1) != m_registry
                    .getIndexForSymbol(goal2)) // can avoid this check by updating goal on merges
            {
                return STATUS.STILL_EVALUATING; // not proved yet
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
        m_goal.add(a);
        m_goal.add(b);
    }

    @Override
    public String toString() {
        String r = m_name + "\n" + m_conjunction;
        r += "----------------------------------\n";

        String ro0 =
                m_registry.getSymbolForIndex(m_registry
                        .getIndexForSymbol(m_goal.get(0)));
        String ro1 =
                m_registry.getSymbolForIndex(m_registry
                        .getIndexForSymbol(m_goal.get(1)));
        r += ro0 + "=" + ro1 + "\n";

        return r;
    }

}
