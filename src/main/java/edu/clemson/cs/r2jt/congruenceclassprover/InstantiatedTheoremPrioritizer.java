/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
