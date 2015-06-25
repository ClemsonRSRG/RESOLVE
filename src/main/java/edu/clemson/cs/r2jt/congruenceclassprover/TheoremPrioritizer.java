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

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created by nabilkabbani on 12/10/14.
 */
public class TheoremPrioritizer {

    protected PriorityQueue<TheoremWithScore> m_pQueue;
    private Map<String, Integer> m_vc_symbols;
    private Map<String, Integer> m_theoremAppliedCount;

    public TheoremPrioritizer(List<TheoremCongruenceClosureImpl> theoremList,
            Map<String, Integer> vcSymbols, int threshold,
            Map<String, Integer> appliedCount) {
        m_pQueue = new PriorityQueue<TheoremWithScore>(theoremList.size());
        m_vc_symbols = vcSymbols;
        m_theoremAppliedCount = appliedCount;

        for (TheoremCongruenceClosureImpl t : theoremList) {
            TheoremWithScore tws = new TheoremWithScore(t);
            //int score = calculateScore(t.getFunctionNames());
            int score = Integer.MAX_VALUE;
            if(!shouldExclude(t.getLiteralsInMatchingPart()) &&
                    !shouldExclude(t.getFunctionNames())) {
                score = calculateScore(t.getNonQuantifiedSymbols());
            }
            if (m_theoremAppliedCount.containsKey(t.m_theoremString)) {
                score += m_theoremAppliedCount.get(t.m_theoremString);
            }
            //if (score <= threshold) {
            tws.m_score = score;
            m_pQueue.add(tws);
            //}

        }

    }

    public boolean shouldExclude(Set<String> vcMustContainThese){
        for (String s: vcMustContainThese){
            if(!m_vc_symbols.containsKey(s)){
                return true;
            }
        }
        return false;
    }
    public int calculateScore(Set<String> theorem_symbols) {
        int score = 0;
        int not_contained_penalty = m_vc_symbols.keySet().size();
        for (String s : theorem_symbols) {
            if (m_vc_symbols.containsKey(s)) {
               score  += m_vc_symbols.get(s);
            }
            else
                score += not_contained_penalty;
        }
        return score/theorem_symbols.size();
    }

    public TheoremCongruenceClosureImpl poll() {
        return m_pQueue.poll().m_theorem;
    }

}
