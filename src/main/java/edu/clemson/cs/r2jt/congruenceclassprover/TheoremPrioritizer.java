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
    private Registry m_vcReg;


    public TheoremPrioritizer(List<TheoremCongruenceClosureImpl> theoremList,
            Map<String, Integer> vcSymbols, int threshold,
            Map<String, Integer> appliedCount, Registry vcReg) {
        m_pQueue = new PriorityQueue<TheoremWithScore>(theoremList.size());
        m_vc_symbols = vcSymbols;
        m_theoremAppliedCount = appliedCount;
        m_vcReg = vcReg;
        for (TheoremCongruenceClosureImpl t : theoremList) {
            TheoremWithScore tws = new TheoremWithScore(t);
            //int score = calculateScore(t.getFunctionNames());
            int score = Integer.MAX_VALUE;
            if(t.m_allowNewSymbols) {
                if (!shouldExclude(t.getLiteralsInMatchingPart())) {
                    score = calculateScoreMinimum(t.getNonQuantifiedSymbols(), 0);
                }
            }
            else{
                if(!shouldExclude(t.getLiteralsInMatchingPart()) &&
                        !shouldExclude(t.getFunctionNames())){
                    score = calculateScoreMinimum(t.getNonQuantifiedSymbols(),m_vc_symbols.keySet().size());
                }

            }


            if (m_theoremAppliedCount.containsKey(t.m_theoremString)
                    && score < Integer.MAX_VALUE) {
                score += m_theoremAppliedCount.get(t.m_theoremString);
            }
            //if (score <= threshold) {
            tws.m_score = score;
            m_pQueue.add(tws);
            //}
        }

    }

    public boolean shouldExclude(Set<String> vcMustContainThese) {
        for (String s : vcMustContainThese) {
            String c = m_vcReg.getRootSymbolForSymbol(s);
            if (!m_vc_symbols.containsKey(c)
                    && !m_vcReg.m_symbolToIndex.containsKey(c)) {
                return true;
            }
        }
        return false;
    }

//  minimum of symbol scores in both vc and theorem
   public int calculateScoreMinimum(Set<String> theorem_symbols, int not_contained_penalty) {
        int score = not_contained_penalty;
       int number_not_contained = 0;
        for (String s : theorem_symbols) {
            String c = m_vcReg.getRootSymbolForSymbol(s);
            if (m_vc_symbols.containsKey(c)) {
                int c_score = m_vc_symbols.get(c);
                if (c_score < score)
                    score = c_score;
            }
            else number_not_contained++;
        }
        return score + number_not_contained;
    }

    // average of symbol scores
 public int calculateScoreAverage(Set<String> theorem_symbols, int not_contained_penalty) {
     float score = 0;
     for (String s : theorem_symbols) {
         String c = m_vcReg.getRootSymbolForSymbol(s);
         if (m_vc_symbols.containsKey(c)) {
             score += m_vc_symbols.get(c);
         }
         else score += not_contained_penalty;
     }

     return (int)(score/(float)theorem_symbols.size());
 }

    public TheoremCongruenceClosureImpl poll() {
        return m_pQueue.poll().m_theorem;
    }

}
