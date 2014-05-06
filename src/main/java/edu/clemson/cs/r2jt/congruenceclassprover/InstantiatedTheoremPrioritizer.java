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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author mike
 */
public class InstantiatedTheoremPrioritizer {

    protected PriorityQueue<PExpWithScore> m_pQueue;

    public InstantiatedTheoremPrioritizer(
            List<InsertExpWithJustification> theoremList,
            HashMap<String, Integer> vcSymbolCount) {
        m_pQueue = new PriorityQueue<PExpWithScore>(theoremList.size());
        for (InsertExpWithJustification p : theoremList) {
            m_pQueue.add(new PExpWithScore(p.m_PExp, vcSymbolCount,
                    p.m_Justification));
        }
    }

    public PExp poll() {
        return m_pQueue.poll().m_theorem;
    }

    protected class PExpWithScore implements Comparable<PExpWithScore> {

        protected PExp m_theorem;
        protected String m_theoremDefinitionString;
        protected Integer m_score = 0;
        protected HashMap<String, Integer> m_symbol_count;

        public PExpWithScore(PExp theorem,
                HashMap<String, Integer> vcSymbolCount, String justification) {
            m_theorem = theorem;
            m_theoremDefinitionString = justification;
            ArrayList<String> symList = getListOfSymbolsInPExp(theorem);
            // todo get collection of symbols in theorem
            for (String s : symList) {
                if (s.equals("and") || s.equals("=") || s.equals("implies"))
                    continue;
                if (vcSymbolCount.containsKey(s)) {
                    m_score += vcSymbolCount.get(s);
                }
            }

        }

        private ArrayList<String> getListOfSymbolsInPExp(PExp p) {
            ArrayList<String> sList = new ArrayList<String>();
            sList.add(p.getTopLevelOperation());
            PExpSubexpressionIterator pit = p.getSubExpressionIterator();
            while (pit.hasNext()) {
                sList.addAll(getListOfSymbolsInPExp(pit.next()));
            }
            return sList;
        }

        @Override
        public String toString() {
            return m_theoremDefinitionString + "\n" + "\t[" + m_score + "]"
                    + " " + m_theorem.toString() + "\n";
        }

        @Override
        public int compareTo(PExpWithScore o) {
            return o.m_score - m_score;
        }
    }
}
