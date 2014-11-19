/**
 * InstantiatedTheoremPrioritizer.java
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

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PExpSubexpressionIterator;
import edu.clemson.cs.r2jt.proving.absyn.PLambda;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author mike
 */
public class InstantiatedTheoremPrioritizer {

    protected PriorityQueue<PExpWithScore> m_pQueue;

    public InstantiatedTheoremPrioritizer(
            List<InsertExpWithJustification> theoremList,
            Map<String, Integer> vcSymbols, int threshold) {
        m_pQueue = new PriorityQueue<PExpWithScore>(theoremList.size());
        for (InsertExpWithJustification p : theoremList) {
            PExpWithScore pes =
                    new PExpWithScore(p.m_PExp, vcSymbols, p.m_Justification);
            if (pes.m_score < threshold)
                m_pQueue.add(pes);
        }
    }

    public PExp poll() {
        return m_pQueue.poll().m_theorem;
    }

    protected class PExpWithScore implements Comparable<PExpWithScore> {

        protected PExp m_theorem;
        protected String m_theoremDefinitionString;
        protected Integer m_score = 1;
        protected HashMap<String, Integer> m_symbol_count;

        public PExpWithScore(PExp theorem, Map<String, Integer> vcSymbols,
                String justification) {
            m_theorem = theorem;
            m_theoremDefinitionString = justification;
            HashSet<String> thSet = getSetOfSymbolsInPExp(theorem);
            for (String s : thSet) {
                if (vcSymbols.containsKey(s)) {
                    m_score *= vcSymbols.get(s);
                }
                else {
                    m_score *= vcSymbols.keySet().size();
                }
            }

        }

        private HashSet<String> getSetOfSymbolsInPExp(PExp p) {

            HashSet<String> rSet = new HashSet<String>();
            if (p.getClass().getSimpleName().equals("PLambda")) {
                return rSet;
            }
            if (!p.isLiteral()) {
                rSet.add(p.getTopLevelOperation());
            }
            PExpSubexpressionIterator pit = p.getSubExpressionIterator();
            while (pit.hasNext()) {
                rSet.addAll(getSetOfSymbolsInPExp(pit.next()));
            }
            rSet.remove("=");

            return rSet;
        }

        @Override
        public String toString() {
            return m_theoremDefinitionString + "\n" + "\t[" + m_score + "]"
                    + " " + m_theorem.toString() + "\n";
        }

        @Override
        public int compareTo(PExpWithScore o) {
            return m_score - o.m_score;
        }
    }
}
