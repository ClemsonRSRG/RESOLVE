/**
 * TheoremPrioritizer.java
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
            int score = calculateScore(t.getFunctionNames());
            if (m_theoremAppliedCount.containsKey(t.m_theoremString)) {
                score += m_theoremAppliedCount.get(t.m_theoremString);
            }
            //if (score <= threshold) {
            tws.m_score = score;
            m_pQueue.add(tws);
            //}

        }

    }

    public int calculateScore(Set<String> theorem_symbols) {
        int count_of_functions_not_in_vc = 0;
        int size = m_vc_symbols.keySet().size();
        int score = 0;
        for (String s : theorem_symbols) {

            if (m_vc_symbols.containsKey(s)) {
                int c_score = m_vc_symbols.get(s);
                // c_score: the number of steps it takes to reach the goal symbol from s.
                if (c_score < score)
                    score = c_score;
            }
            else
                count_of_functions_not_in_vc++;
        }
        //if(score > count) score /=count;
        return score + (count_of_functions_not_in_vc * 2000);
    }

    public TheoremCongruenceClosureImpl poll() {
        return m_pQueue.poll().m_theorem;
    }

}
