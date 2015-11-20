/**
 * InstantiatedTheoremPrioritizer.java
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

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import java.util.*;

/**
 *
 * @author mike
 */
public class InstantiatedTheoremPrioritizer {

    protected PriorityQueue<PExpWithScore> m_pQueue;
    private Map<String, Integer> m_vc_symbols;
    protected Registry m_vcReg;

    public InstantiatedTheoremPrioritizer(
            List<InsertExpWithJustification> theoremList,
            Map<String, Integer> vcSymbols, int threshold, Registry vcReg) {
        m_pQueue = new PriorityQueue<PExpWithScore>(theoremList.size());
        m_vc_symbols = vcSymbols;
        m_vcReg = vcReg;
        for (InsertExpWithJustification p : theoremList) {
            PExpWithScore pes = new PExpWithScore(p.m_PExp, p.m_Justification);
            pes.m_score = calculateScore(pes.m_theorem_symbols);
            //if (pes.m_score < threshold)
            m_pQueue.add(pes);
        }
    }

    public int calculateScore(Set<String> theorem_symbols) {
        int max = m_vcReg.m_indexToSymbol.size();
        int score = 0;
        if (m_vc_symbols.isEmpty()) {
            for (String s : theorem_symbols) {
                if (m_vcReg.m_symbolToIndex.containsKey(s)) {
                    score += m_vcReg.getIndexForSymbol(s);
                }
                else
                    score += max;
            }
        }
        for (String s : theorem_symbols) {
            String rS = m_vcReg.getRootSymbolForSymbol(s);
            if (m_vc_symbols.containsKey(rS)) {
                int i_score =
                        m_vc_symbols.get(rS);
                score += i_score;
            }
            else {
                score += max;
            }
        }
        return score / theorem_symbols.size();
    }

    public PExp poll() {
        return m_pQueue.poll().m_theorem;
    }

}
