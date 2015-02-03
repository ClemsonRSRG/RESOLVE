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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// todo: create array impl and compare.
/**
 * Created by mike on 4/4/2014.
 */
public class NormalizedAtomicExpressionMapImpl
        implements
            Comparable<NormalizedAtomicExpressionMapImpl> {

    private static final int m_maxPositions = 6;
    private final Map<Integer, Integer> m_expression;

    public NormalizedAtomicExpressionMapImpl() {
        m_expression = new TreeMap<Integer, Integer>();
    }

    /**
     *
     * @param operator
     * @return bit pattern of positions. 0 indicates the operator is not used
     */
    public int readOperator(int operator) {
        if (m_expression.containsKey(operator)) {
            return m_expression.get(operator);
        }
        return 0;
    }

    protected Set<Integer> getKeys() {
        return m_expression.keySet();
    }

    protected Set<String> getArgumentsAsStrings(Registry reg) {
        HashSet<String> rSet = new HashSet<String>();
        for (int i = 1; i < m_maxPositions; ++i) {
            int index = readPosition(i);
            if (index < 0)
                break;
            String op = reg.getSymbolForIndex(index);
            rSet.add(op);
        }
        return rSet;
    }

    /**
     *
     * @param position
     * @return integer representation of operator at position or -1 if none
     */
    public int readPosition(int position) {
        if (position >= m_maxPositions) {
            return -1; // needed for construction of str arrays
        }
        position = 1 << position;
        Set<Map.Entry<Integer, Integer>> entries = m_expression.entrySet();
        // hangs here with some lambda formulae
        for (Map.Entry<Integer, Integer> e : entries) {
            if ((e.getValue() & position) != 0) {
                return e.getKey();
            }
            /* todo: possible use for map: multiple operators in a single position.
             for use in binding quantified variables.
             */
        }
        return -1;
    }

    /**
     * @param operator integer value of operator
     * @param position 0 denotes first position.
     */
    public void writeOnto(int operator, int position) {
        position = 1 << position;
        Integer curValue = m_expression.get(operator);
        if (curValue != null) {
            position = position | curValue;
        }
        m_expression.put(operator, position);
    }

    public boolean replaceOperator(int orig, int repl) {
        int origPositions = -1;
        if (m_expression.containsKey(orig)) {
            origPositions = m_expression.get(orig);
        }
        int replPositions = 0;
        if (m_expression.containsKey(repl)) {
            replPositions = m_expression.get(repl);
        }
        if (origPositions > 0) {
            m_expression.remove(orig);

            m_expression.put(repl, origPositions | replPositions);
            return true;
        }
        return false;
    }

    /**
     *
     * @param root
     */
    protected void writeToRoot(int root) {
        int position = 1 << m_maxPositions;
        m_expression.put(root, position);
    }

    protected int readRoot() {
        int position = 1 << m_maxPositions;
        Set<Map.Entry<Integer, Integer>> entries = m_expression.entrySet();
        for (Map.Entry<Integer, Integer> e : entries) {
            if ((e.getValue() & position) != 0) {
                return e.getKey();
            }
            /* todo: possible use for map: multiple operators in a single position.
             for use in binding quantified variables.
             */
        }
        return -1;
    }

    // compare left sides of 2 expressions.  If this returns 0, you must compare right hand sides afterwards.
    @Override
    public int compareTo(NormalizedAtomicExpressionMapImpl o) {
        for (int i = 0; i < m_maxPositions; ++i) {
            int cmp = readPosition(i) - o.readPosition(i);
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    public NormalizedAtomicExpressionMapImpl clear() {
        m_expression.clear();
        return this;
    }

    public int numOperators() {
        return m_expression.keySet().size();
    }

    public NormalizedAtomicExpressionMapImpl translateFromRegParam1ToRegParam2(
            Registry source, Registry destination,
            HashMap<String, String> mapping) {

        NormalizedAtomicExpressionMapImpl translated =
                new NormalizedAtomicExpressionMapImpl();
        Set<Integer> keys = m_expression.keySet();
        for (Integer k : keys) {
            String sourceName = source.getSymbolForIndex(k);
            String destName = "";
            switch (source.getUsage(sourceName)) {
            case LITERAL:
            case HASARGS_SINGULAR:// literals and func names should be in both
                if (destination.isSymbolInTable(sourceName)) {
                    destName = sourceName;
                }
                else {
                    return translated.clear();
                }
                break;
            case HASARGS_FORALL:
            case FORALL:
                if (mapping.containsKey(sourceName)) {
                    destName = mapping.get(sourceName);
                }
                break;
            }
            if (!destName.equals("")) {
                int trKey = destination.getIndexForSymbol(destName);
                int positions = m_expression.get(k);
                translated.m_expression.put(trKey, positions);
            }

        }
        return translated;
    }

    public NormalizedAtomicExpressionMapImpl incrementLastKnown() {
        NormalizedAtomicExpressionMapImpl incremented =
                new NormalizedAtomicExpressionMapImpl();
        int pos = 0;
        int op = readPosition(pos);
        if (op < 0) { // function operator unknown, upper bound is end of list
            incremented.writeOnto(Integer.MAX_VALUE, 0);
            incremented.writeToRoot(Integer.MAX_VALUE);
            return incremented;
        }
        int opPrev = op;
        while (op >= 0) {
            incremented.writeOnto(opPrev, pos);
            pos++;
            opPrev = op;
            op = readPosition(pos);
        }
        incremented.m_expression.remove(opPrev);
        incremented.writeOnto(++opPrev, pos - 1);
        return incremented;
    }

    public String toHumanReadableString(Registry registry) {
        if (m_expression.isEmpty()) {
            return "empty expression";
        }
        String r;
        String funcSymbol = registry.getSymbolForIndex(readPosition(0));
        String args = "";
        int cur;
        int i = 1;
        while ((cur = readPosition(i)) >= 0) {
            args += registry.getSymbolForIndex(cur);
            args += ",";
            i++;
        }
        if (args.length() != 0) {
            args = args.substring(0, args.length() - 1);
        }
        args = "(" + args + ")";
        r = funcSymbol + args;
        // if there is a root
        int root = readRoot();
        if (root >= 0) {
            r += "=" + registry.getSymbolForIndex(root);
        }

        return r;
    }

    @Override
    public String toString() {
        return m_expression.toString();
    }
}
