/**
 * TheoremPrioritizer.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.absyn.StringExp;
import edu.clemson.cs.r2jt.vcgeneration.vcs.VerificationCondition;

import java.util.*;

/**
 * Created by nabilkabbani on 12/10/14.
 */
public class TheoremPrioritizer {

    protected PriorityQueue<TheoremWithScore> m_pQueue;
    private Map<String, Integer> m_theoremAppliedCount;
    private Registry m_vcReg;
    private VerificationConditionCongruenceClosureImpl m_vc;
    private Map<String, Integer> m_nonQuantMap;
    private Set<TheoremCongruenceClosureImpl> m_smallEndEquations;

    public TheoremPrioritizer(List<TheoremCongruenceClosureImpl> theoremList,
            Map<String, Integer> appliedCount,
            VerificationConditionCongruenceClosureImpl vc,
            Set<String> nonQuantifiedTheoremSymbols,
            Set<TheoremCongruenceClosureImpl> smallEndEquations) {
        m_pQueue = new PriorityQueue<TheoremWithScore>(theoremList.size());
        m_theoremAppliedCount = appliedCount;
        m_vcReg = vc.getRegistry();
        m_vc = vc;
        m_nonQuantMap = new HashMap<String, Integer>();
        m_smallEndEquations = smallEndEquations;
        int count = 0;
        for (String s : m_vcReg.m_indexToSymbol) {
            if (nonQuantifiedTheoremSymbols.contains(s)) {
                m_nonQuantMap.put(s, count++);
            }
        }
        for (TheoremCongruenceClosureImpl t : theoremList) {
            TheoremWithScore tws = new TheoremWithScore(t);
            //int score = calculateScore(t.getFunctionNames());
            int score;
            //if (!shouldExclude(t.getFunctionNames())) {
            if (!shouldExclude(t.getNonQuantifiedSymbols())) {
                score =
                        calculateScoreMinimum(t.getNonQuantifiedSymbols(),
                                m_vcReg.m_symbolToIndex.keySet().size());
                if (m_theoremAppliedCount.containsKey(t.m_name)) {
                    score += m_theoremAppliedCount.get(t.m_name);
                }
                if (m_smallEndEquations.contains(t)) {
                    score += 1;
                }
                tws.m_score = score;
                m_pQueue.add(tws);
            }
        }
    }

    public boolean shouldExclude(Set<String> vcMustContainThese) {
        for (String s : vcMustContainThese) {
            String c = m_vcReg.getRootSymbolForSymbol(s);
            if (!m_vcReg.m_symbolToIndex.containsKey(c)) {
                return true;
            }
        }
        return false;
    }

    //  minimum of symbol scores in both vc and theorem
    public int calculateScoreMinimum(Set<String> theorem_symbols,
            int not_contained_penalty) {
        if (theorem_symbols.isEmpty())
            return 0;
        int score = not_contained_penalty;
        int number_not_contained = 1;
        for (String s : theorem_symbols) {
            if (m_nonQuantMap.containsKey(s)) {
                int c_score = goalArg(s);
                if (c_score < 0)
                    c_score = m_nonQuantMap.get(s);
                if (c_score < score)
                    score = c_score;
            }
            else
                number_not_contained++;
        }
        return (score + 1) * number_not_contained;
    }

    private int goalArg(String s) {
        int si = m_vcReg.getIndexForSymbol(s);
        String sc = m_vcReg.getRootSymbolForSymbol(s);
        for (String g : m_vc.m_goal) {
            if (g.equals("false"))
                continue;
            int gi = m_vcReg.getIndexForSymbol(g);
            if (si == gi)
                return 0;
            for (NormalizedAtomicExpression ng : m_vc.getConjunct().getUses(gi)) {
                if (ng.readRoot() != gi)
                    continue;
                if (ng.getOperatorsAsStrings(true).containsKey(sc))
                    return 1;
            }
        }
        return -1;
    }

    public TheoremCongruenceClosureImpl poll() {
        return m_pQueue.poll().m_theorem;
    }

}
