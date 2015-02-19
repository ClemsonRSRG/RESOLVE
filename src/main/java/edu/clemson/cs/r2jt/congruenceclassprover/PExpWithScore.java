/**
 * PExpWithScore.java
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

import java.util.HashMap;
import java.util.HashSet;
import edu.clemson.cs.r2jt.rewriteprover.absyn.*;

public class PExpWithScore implements Comparable<PExpWithScore> {

    protected PExp m_theorem;
    protected String m_theoremDefinitionString;
    protected Integer m_score = 1;
    protected HashMap<String, Integer> m_symbol_count;
    protected HashSet<String> m_theorem_symbols;

    public PExpWithScore(PExp theorem, String justification) {
        m_theorem = theorem;
        m_theoremDefinitionString = justification;
        m_theorem_symbols = getSetOfSymbolsInPExp(m_theorem);
    }

    public void updateTheoremSymbols(Registry reg) {
        HashSet<String> newSet = new HashSet<String>();
        if (m_theorem_symbols != null) {
            for (String sym : m_theorem_symbols) {
                if (reg.isSymbolInTable(sym))
                    newSet.add(reg.getRootSymbolForSymbol(sym));
                else
                    newSet.add(sym);
            }
            m_theorem_symbols = newSet;
        }
    }

    private HashSet<String> getSetOfSymbolsInPExp(PExp p) {

        HashSet<String> rSet = new HashSet<String>();
        if (!p.getClass().getSimpleName().equals("PSymbol")) {
            return rSet;
        }
        PSymbol asPSymbol = (PSymbol) p;
        if (!asPSymbol.isFunction()) {
            rSet.add(asPSymbol.toString());
        }
        PExpSubexpressionIterator pit = asPSymbol.getSubExpressionIterator();
        while (pit.hasNext()) {
            rSet.addAll(getSetOfSymbolsInPExp(pit.next()));
        }

        return rSet;
    }

    @Override
    public String toString() {
        return m_theoremDefinitionString + "\n" + "\t[" + m_score + "]" + " "
                + m_theorem.toString() + "\n";
    }

    @Override
    public int compareTo(PExpWithScore o) {
        return m_score - o.m_score;
    }
}