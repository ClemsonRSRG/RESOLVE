/**
 * NormalizedAtomicExpressionMapImpl.java
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
    private Map<Integer, Integer> m_expression;

    public NormalizedAtomicExpressionMapImpl() {
        m_expression = new TreeMap<Integer, Integer>();
    }

    /**
     *
     * @param operator
     * @return bit pattern of positions. 0 indicates the operator is not used
     */
    public int readOperator(int operator) {
        if (m_expression.containsKey(operator))
            return m_expression.get(operator);
        return 0;
    }

    public int readPosition(int position) {
        position = 1 << position;
        Set<Map.Entry<Integer, Integer>> entries = m_expression.entrySet();
        for (Map.Entry<Integer, Integer> e : entries) {
            if ((e.getValue() & position) != 0)
                return e.getKey();
            /* todo: possible use for map: multiple operators in a single position.
             for use in binding quantified variables.
             */
        }
        return -1;
    }

    /**
     * @param operator integer value of operator
     * @param position 0 denotes first position.
     * @return post operation value of position;
     */
    public void writeOnto(int operator, int position) {
        int porig = position;
        position = 1 << position;
        Integer curValue = m_expression.get(operator);
        if (curValue != null) {
            position = position | curValue;
        }
        m_expression.put(operator, position);
        return;
    }

    public boolean replaceOperator(int orig, int repl) {
        int origPositions = readOperator(orig);
        if (origPositions > 0) {
            m_expression.remove(orig);
            m_expression.put(repl, origPositions);
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
        return readPosition(m_maxPositions);
    }

    // compare left sides of 2 expressions.  If this returns 0, you must compare right hand sides afterwards.
    @Override
    public int compareTo(NormalizedAtomicExpressionMapImpl o) {
        for (int i = 0; i < m_maxPositions; ++i) {
            int cmp = readPosition(i) - o.readPosition(i);
            if (cmp != 0)
                return cmp;
        }
        return 0;
    }

    public String toHumanReadableString(Registry registry) {
        String r = "";
        int cur;
        int i = 0;
        while (((cur = readPosition(i)) >= 0) && i < m_maxPositions) {
            if (i == 1)
                r += "(";
            r += registry.getSymbolForIndex(cur);
            if (i != 0)
                r += ",";
            i++;
        }
        r = r.substring(0, r.length() - 1);
        r += ")=" + registry.getSymbolForIndex(readRoot());

        return r;
    }

    public String toString() {
        return m_expression.toString();
    }
}
