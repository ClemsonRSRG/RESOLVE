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
import edu.clemson.cs.r2jt.vcgeneration.vcs.VerificationCondition;
import sun.plugin.dom.exception.NoModificationAllowedException;

import java.util.*;

/**
 *
 * @author mike
 */
public class InstantiatedTheoremPrioritizer {

    protected PriorityQueue<PExpWithScore> m_pQueue;
    protected VerificationConditionCongruenceClosureImpl m_vc;
    protected Registry m_vcReg;

    public InstantiatedTheoremPrioritizer(VerificationConditionCongruenceClosureImpl vc) {
        m_pQueue = new PriorityQueue<PExpWithScore>(4096);
        m_vc = vc;
        m_vcReg = vc.getRegistry();

    }

    public void add(Set<InsertExpWithJustification> theoremSet){
        for (InsertExpWithJustification p : theoremSet) {
            PExpWithScore pes = new PExpWithScore(p.m_PExp, p.m_Justification);
            pes.m_score = calculateScore(pes.m_theorem_symbols, p.m_symCnt);
            //if (pes.m_score < threshold)
            m_pQueue.add(pes);
        }
    }
    private int goalArg(String s){
        int max = 3;
        int si  = m_vcReg.getIndexForSymbol(s);
        if(si<0 || m_vcReg.getUsage(s).equals(Registry.Usage.HASARGS_SINGULAR)) return max;
        String sc = m_vcReg.getRootSymbolForSymbol(s);
        for(String g: m_vc.m_goal){
            if(g.equals("false")) continue;
            int gi = m_vcReg.getIndexForSymbol(g);
            if(si==gi) return 0;
            /*String gc = m_vcReg.getSymbolForIndex(gi);
            for(NormalizedAtomicExpression ng : m_vc.getConjunct().m_useMap.get(gi)){
                if(ng.readRoot()!= gi) continue;
                if(ng.getOperatorsAsStrings(true).containsKey(sc))
                    return 1;
            }*/
        }
        return max;
    }
    public int calculateScore(Set<String> theorem_symbols, int symCnt) {

        float max = m_vc.getRegistry().m_indexToSymbol.size();
        float score = 0f;
        float age = 0f;
        float sSz = theorem_symbols.size();
        assert sSz <= (float) symCnt;
        float diff = 1f - (sSz / (float) symCnt);
        int minGr = Integer.MAX_VALUE;
        //diff = diff > 0 ? diff : 0;
        for (String s : theorem_symbols) {

            String rS = m_vcReg.getRootSymbolForSymbol(s);
            //int gr = goalArg(rS);
            //if(gr < minGr) minGr = gr;
            if (m_vcReg.m_symbolToIndex.containsKey(rS)) {
                // Age
                age += m_vcReg.getIndexForSymbol(s);
            }
            else {
                score += max;
            }
        }
        //if(minGr < 2) return minGr;
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
