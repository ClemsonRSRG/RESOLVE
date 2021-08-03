/*
 * NormalizedAtomicExpression.java
 * ---------------------------------
 * Copyright (c) 2021
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
public class NormalizedAtomicExpression {

    private final int[] m_expression;
    private int m_classConstant;
    private int arity; // number of arguments
    private final Registry m_registry;
    private Map<String, Integer> m_opMmap;
    private Set<Integer> m_opIdSet;
    private Map<String, Integer> m_argMmap;

    public NormalizedAtomicExpression(Registry registry, int[] intArray) {
        m_registry = registry;
        arity = intArray.length - 1;
        if (!m_registry.isCommutative(intArray[0])) {
            m_expression = intArray;
        }
        else {
            int[] ord = new int[arity];
            for (int i = 1; i < intArray.length; ++i) {
                ord[i - 1] = intArray[i];
            }
            Arrays.sort(ord);
            int[] ne = new int[intArray.length];
            ne[0] = intArray[0];
            for (int i = 0; i < ord.length; ++i) {
                ne[i + 1] = ord[i];
            }
            m_expression = ne;
        }
        m_classConstant = -1;
    }

    protected int getArity() {
        return arity;
    }

    protected Registry getRegistry() {
        return m_registry;
    }

    protected Set<Integer> getOpIds() {
        if (m_opIdSet != null) {
            return m_opIdSet;
        }
        m_opIdSet = new HashSet<Integer>();
        for (int i = 0; i < m_expression.length; ++i) {
            m_opIdSet.add(m_expression[i]);
        }
        int r = readRoot();
        m_opIdSet.add(r);
        return m_opIdSet;
    }

    protected Map<String, Integer>
            getOperatorsAsStrings(boolean justArguments) {
        if (justArguments && m_argMmap != null) {
            return new HashMap<String, Integer>(m_argMmap);
        }
        if (!justArguments && m_opMmap != null) {
            return new HashMap<String, Integer>(m_opMmap);
        }
        m_argMmap = new HashMap<String, Integer>();
        for (int i = 1; i < m_expression.length; ++i) {
            String curOp = readSymbol(i);
            if (m_argMmap.containsKey(curOp)) {
                m_argMmap.put(curOp, m_argMmap.get(curOp) + 1);
            }
            else
                m_argMmap.put(curOp, 1);
        }
        m_opMmap = new HashMap<String, Integer>(m_argMmap);
        String fSym = readSymbol(0);
        String rSym = m_registry.getSymbolForIndex(readRoot());
        if (m_opMmap.containsKey(fSym)) {
            m_opMmap.put(fSym, m_opMmap.get(fSym) + 1);
        }
        else {
            m_opMmap.put(fSym, 1);
        }
        if (m_opMmap.containsKey(rSym)) {
            m_opMmap.put(rSym, m_opMmap.get(rSym) + 1);
        }
        else {
            m_opMmap.put(rSym, 1);
        }
        if (justArguments) {
            return new HashMap<String, Integer>(m_argMmap);
        }
        else {
            return new HashMap<String, Integer>(m_opMmap);
        }

    }

    public int readPosition(int position) {
        return m_expression[position];
    }

    public String readSymbol(int position) {
        return m_registry.getSymbolForIndex(m_expression[position]);
    }

    // return array contains 'n' if sint is an arg used at position 'n', 0 denotes operator, -1
    // denotes cong class
    public int[] getPositionsFor(int sint) {
        int[] rArray = new int[arity + 2];
        int count = 0;
        for (int i = 0; i <= arity; ++i) {
            if (m_expression[i] == sint) {
                rArray[count++] = i;
            }
        }
        if (readRoot() == sint)
            rArray[count++] = -1;

        return Arrays.copyOf(rArray, count);
    }

    // -1 meaning wildcard.
    public int[] rootedLiterals(Map<String, String> overMap, Registry vc_Reg) {
        int[] rArray = new int[m_expression.length + 1];
        for (int i = 0; i <= m_expression.length; ++i) {
            int expI = (i < m_expression.length) ? m_expression[i]
                    : m_classConstant;
            String k = m_registry.getSymbolForIndex(expI);
            String v = (overMap.containsKey(k)) ? overMap.get(k) : k;
            if (v.equals("")) {
                rArray[i] = -1;
            }
            else if (!vc_Reg.m_symbolToIndex.containsKey(v)) {
                return null;
            }
            else {
                rArray[i] = vc_Reg.getIndexForSymbol(v);
            }
        }
        return rArray;
    }

    // "" meaning mapped.
    public String[] unMappedWildcards(Map<String, String> overMap) {
        String[] rArray = new String[m_expression.length + 1];
        for (int i = 0; i < m_expression.length; ++i) {
            String ks = m_registry.getSymbolForIndex(m_expression[i]);
            if (overMap.containsKey(ks) && overMap.get(ks).equals("")) {
                rArray[i] = ks;
            }
            else
                rArray[i] = "";
        }
        String ks = m_registry.getSymbolForIndex(m_classConstant);
        if (overMap.containsKey(ks) && overMap.get(ks).equals("")) {
            rArray[m_expression.length] = ks;
        }
        else
            rArray[m_expression.length] = "";
        return rArray;
    }

    public NormalizedAtomicExpression replaceOperator(int orig, int repl) {
        if (orig == repl)
            return this;
        if (!getOpIds().contains(orig)) {
            return this;
        }
        int[] na = new int[m_expression.length];
        boolean changed = false;
        for (int i = 0; i < m_expression.length; ++i) {
            if (m_expression[i] == orig) {
                na[i] = repl;
                changed = true;
            }
            else
                na[i] = m_expression[i];
        }
        if (readRoot() == orig) {
            writeToRoot(repl);
        }
        NormalizedAtomicExpression rNa = this;
        if (changed) {
            rNa = new NormalizedAtomicExpression(m_registry, na);
            rNa.writeToRoot(readRoot());
        }
        assert (changed == (rNa != this));
        assert changed == (rNa.hashCode() != hashCode());
        return rNa;
    }

    protected void writeToRoot(int root) {
        m_opMmap = null;
        m_opIdSet = null;
        m_classConstant = root;
    }

    protected int readRoot() {
        return m_classConstant;
    }

    public NormalizedAtomicExpression rootOps() {
        int[] roots = new int[arity + 1];
        for (int i = 0; i < roots.length; ++i) {
            roots[i] = m_registry.findAndCompress(m_expression[i]);
        }
        NormalizedAtomicExpression rn =
                new NormalizedAtomicExpression(m_registry, roots);
        return rn;
    }

    public boolean hasVarOps() {
        boolean isVar = false;
        for (int i = 0; i < m_expression.length; ++i) {
            String s = readSymbol(i);
            if (s.startsWith("¢v")) {
                isVar = true;
                break;
            }
            Registry.Usage us = m_registry.getUsage(s);
            if (us == Registry.Usage.FORALL
                    || us == Registry.Usage.HASARGS_FORALL) {
                isVar = true;
                break;
            }
        }
        return isVar;
    }

    public String toString() {
        String r;
        String funcSymbol = readSymbol(0);

        String args = "";

        for (int i = 1; i < m_expression.length; ++i) {
            args += readSymbol(i) + ",";
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
        return Arrays.hashCode(m_expression);
    }

    public boolean equals(Object o) {
        if (o instanceof NormalizedAtomicExpression) {
            NormalizedAtomicExpression other = (NormalizedAtomicExpression) o;
            if (Arrays.equals(m_expression, other.m_expression)
                    && other.m_registry == m_registry)
                return true;
        }
        return false;
    }

    public int numberOfQuants() {
        int c = 0;
        for (String k : getOperatorsAsStrings(false).keySet()) {
            if (m_registry.getUsage(k).equals(Registry.Usage.FORALL)
                    || m_registry.getUsage(k)
                            .equals(Registry.Usage.HASARGS_FORALL)
                    || m_registry.getUsage(k).equals(Registry.Usage.CREATED)) {
                c++;
            }
        }
        return c;
    }

    public static class numQuantsComparator
            implements
                Comparator<NormalizedAtomicExpression> {

        public int compare(NormalizedAtomicExpression nae1,
                NormalizedAtomicExpression nae2) {
            return nae1.numberOfQuants() - nae2.numberOfQuants();
        }
    }
}
