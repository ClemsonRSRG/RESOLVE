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
    protected Registry m_vcReg;

    public InstantiatedTheoremPrioritizer(
            List<InsertExpWithJustification> theoremList, Registry vcReg) {
        m_pQueue = new PriorityQueue<PExpWithScore>(theoremList.size());
        m_vcReg = vcReg;
        for (InsertExpWithJustification p : theoremList) {
            PExpWithScore pes = new PExpWithScore(p.m_PExp, p.m_Justification);
            pes.m_score = calculateScore(pes.m_theorem_symbols, p.m_symCnt);
            //if (pes.m_score < threshold)
            m_pQueue.add(pes);
        }
    }

    public int calculateScore(Set<String> theorem_symbols, int symCnt) {

        float max = m_vcReg.m_indexToSymbol.size();
        float score = 0f;
        float age = 0f;
        float sSz = theorem_symbols.size();
        assert sSz <= (float) symCnt;
        float diff = 1f - (sSz / (float) symCnt);
        //diff = diff > 0 ? diff : 0;
        for (String s : theorem_symbols) {

            String rS = m_vcReg.getRootSymbolForSymbol(s);
            if (m_vcReg.m_symbolToIndex.containsKey(rS)) {
                // Age
                age += m_vcReg.getIndexForSymbol(s);
            }
            else {
                score += max;
            }
        }

        float avgAge = age / sSz;
        // these range from [0,1], lower is better
        float scaledAvgAge = avgAge / max;

        scaledAvgAge += .01;
        diff += .01;
        int r = (int) ((80f * scaledAvgAge) + (20f * diff));
        return r;
    }

    public PExp poll() {
        return m_pQueue.poll().m_theorem;
    }

}
