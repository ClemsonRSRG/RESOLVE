/**
 * NormalizedAtomicExpressionMapImpl.java
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

import java.util.*;

// todo: create array impl and compare.
/**
 * Created by mike on 4/4/2014.
 */
public class NormalizedAtomicExpressionMapImpl {

    private final Map<Integer, Integer> m_expression; // opId -> positionBitCode
    private int arity = 0; // number of arguments
    private final Registry m_registry;
    private static HashSet<TreeMap<Integer,Integer>> m_mapPool;

    public NormalizedAtomicExpressionMapImpl(Registry reg) {
        m_expression = new TreeMap<Integer, Integer>();
        m_registry = reg;
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

    public int popcount(int x) {
        int count;
        for (count = 0; x > 0; count++)
            x &= x - 1;
        return count;
    }

    // Returns a multiset of all ops, including the root
    protected Map<String, Integer> getOperatorsAsStrings() {
        HashMap<String, Integer> rMap = new HashMap<String, Integer>();
        for (Integer k : m_expression.keySet()) {
            rMap.put(m_registry.getSymbolForIndex(k), popcount(m_expression.get(k)));
        }
        String root = m_registry.getSymbolForIndex(readRoot());
        if(rMap.containsKey(root))
            rMap.put(root,rMap.get(root)+1);
        else
            rMap.put(root,1);
        return rMap;
    }

    // Returns a multiset of arguments
    protected Map<String, Integer> getArgumentsAsStrings() {
        HashMap<String, Integer> rMap = new HashMap<String, Integer>();
        for (int i = 1; i <= arity; ++i) {
            int index = readPosition(i);
            if (index < 0)
                break;
            String op = m_registry.getSymbolForIndex(index);
            int count;
            if (rMap.containsKey(op)) {
                count = rMap.get(op) + 1;
            }
            else
                count = 1;
            rMap.put(op, count);
        }
        return rMap;
    }

    /**
     *
     * @param position
     * @return integer representation of operator at position or -1 if none
     */
    public int readPosition(int position) {
        position = 1 << position;
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

    // returns a key if it used everywhere in positionBitCode
    public int readPositionBitcode(int positionBitCode) {
        Set<Map.Entry<Integer, Integer>> entries = m_expression.entrySet();
        for (Map.Entry<Integer, Integer> e : entries) {
            if ((e.getValue() & positionBitCode) == positionBitCode) {
                return e.getKey();
            }
            /* todo: possible use for map: multiple operators in a single position.
             for use in binding quantified variables.
             */
        }
        return -1;
    }

    // just overwrites entry
    public void overwriteEntry(int operator, int positionBitCode) {
        m_expression.put(operator, positionBitCode);
    }

    /**
     * @param operator integer value of operator
     * @param position 0 denotes first position.
     */
    public void writeOnto(int operator, int position) {
        if (position > arity)
            arity = position;
        position = 1 << position;
        Integer curValue = m_expression.get(operator);
        if (curValue != null) {
            position = position | curValue;
        }
        m_expression.put(operator, position);
    }

    public boolean replaceOperator(int orig, int repl) {
        int origPositions = -1;
        if(readRoot()==orig){
            writeToRoot(repl);
        }
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

    public NormalizedAtomicExpressionMapImpl withOrderedArguments() {

        Integer[] ord = new Integer[arity];
        for (int i = 0; i < arity; ++i) {
            ord[i] = readPosition(i + 1);
        }
        Arrays.sort(ord);

        NormalizedAtomicExpressionMapImpl rExpr =
                new NormalizedAtomicExpressionMapImpl(m_registry);
        rExpr.writeOnto(readPosition(0), 0);
        for (int i = 0; i < arity; ++i) {
            rExpr.writeOnto(ord[i], i + 1);
        }
        int root = readRoot();
        if (root >= 0) {
            rExpr.writeToRoot(root);
        }
        return rExpr;
    }

    /**
     *
     * @param root
     */
    protected void writeToRoot(int root) {
        m_registry.m_exprRootMap.put(this,root);
    }

    protected int readRoot() {
        if(m_registry.m_exprRootMap.containsKey(this))
            return m_registry.m_exprRootMap.get(this);
        return -2;
    }

    public NormalizedAtomicExpressionMapImpl clear() {
        m_expression.clear();
        return this;
    }

    public int numOperators() {
        return arity;
    }

    // Currently doesn't work if there are blanks in the equation (such as the fun symbol)
    public String toHumanReadableString() {
        if (m_expression.isEmpty()) {
            return "empty expression";
        }
        String r;
        String funcSymbol = m_registry.getSymbolForIndex(readPosition(0));

        String args = "";
        int cur;
        int i = 1;
        while ((cur = readPosition(i)) >= 0) {
            args += m_registry.getSymbolForIndex(cur);
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
            r += "=" + m_registry.getSymbolForIndex(root);
        }

        return r;
    }

    @Override
    public int hashCode() {
        return m_expression.hashCode();
    }

    public boolean equals(Object o){
        if(o instanceof NormalizedAtomicExpressionMapImpl){
            NormalizedAtomicExpressionMapImpl other = (NormalizedAtomicExpressionMapImpl)o;
            if(other.m_expression.equals(m_expression) && other.m_registry==m_registry) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return m_expression.toString();
    }
}
