/*
 * NormalizedAtomicExpression.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.prover.utilities.expressions;

import edu.clemson.cs.rsrg.prover.utilities.Registry;
import java.util.*;

/**
 * <p>This class represents an normalized atomic expression.</p>
 *
 * @author Mike Khabbani
 * @version 2.0
 */
public class NormalizedAtomicExpression {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final int[] m_expression;
    private int m_classConstant;
    private int arity; // number of arguments
    private final Registry m_registry;
    private Map<String, Integer> m_opMmap;
    private Set<Integer> m_opIdSet;
    private Map<String, Integer> m_argMmap;

    // ===========================================================
    // Constructors
    // ===========================================================

    public NormalizedAtomicExpression(Registry registry, int[] intArray) {
        m_registry = registry;
        arity = intArray.length - 1;
        if (!m_registry.isCommutative(intArray[0])) {
            m_expression = intArray;
        }
        else {
            int[] ord = new int[arity];
            System.arraycopy(intArray, 1, ord, 0, intArray.length - 1);

            Arrays.sort(ord);

            int[] ne = new int[intArray.length];
            ne[0] = intArray[0];
            System.arraycopy(ord, 0, ne, 1, ord.length);

            m_expression = ne;
        }

        m_classConstant = -1;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    @Override
    public final boolean equals(Object o) {
        if (o instanceof NormalizedAtomicExpression) {
            NormalizedAtomicExpression other = (NormalizedAtomicExpression) o;

            return Arrays.equals(m_expression, other.m_expression)
                    && other.m_registry == m_registry;
        }

        return false;
    }

    public final Map<String, Integer> getOperatorsAsStrings(boolean justArguments) {
        if (justArguments && m_argMmap != null) {
            return new HashMap<>(m_argMmap);
        }
        if (!justArguments && m_opMmap != null) {
            return new HashMap<>(m_opMmap);
        }

        m_argMmap = new HashMap<>();

        for (int i = 1; i < m_expression.length; ++i) {
            String curOp = readSymbol(i);
            if (m_argMmap.containsKey(curOp)) {
                m_argMmap.put(curOp, m_argMmap.get(curOp) + 1);
            }
            else
                m_argMmap.put(curOp, 1);
        }

        m_opMmap = new HashMap<>(m_argMmap);
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
            return new HashMap<>(m_argMmap);
        }
        else {
            return new HashMap<>(m_opMmap);
        }
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(m_expression);
    }

    public final int readRoot() {
        return m_classConstant;
    }

    @Override
    public final String toString() {
        String r;
        String funcSymbol = readSymbol(0);

        StringBuilder argsBuilder = new StringBuilder();
        for (int i = 1; i < m_expression.length; ++i) {
            argsBuilder.append(readSymbol(i)).append(",");
        }
        String args = argsBuilder.toString();

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

    // ===========================================================
    // Package-Private Methods
    // ===========================================================

    final int getArity() {
        return arity;
    }

    final Set<Integer> getOpIds() {
        if (m_opIdSet != null) {
            return m_opIdSet;
        }

        m_opIdSet = new HashSet<>();

        for (int aM_expression : m_expression) {
            m_opIdSet.add(aM_expression);
        }

        int r = readRoot();
        m_opIdSet.add(r);

        return m_opIdSet;
    }

    // return array contains 'n' if sint is an arg used at position 'n', 0 denotes operator, -1 denotes cong class
    final int[] getPositionsFor(int sint) {
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

    final Registry getRegistry() {
        return m_registry;
    }

    final boolean hasVarOps() {
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

    final int readPosition(int position) {
        return m_expression[position];
    }

    final String readSymbol(int position) {
        return m_registry.getSymbolForIndex(m_expression[position]);
    }

    final NormalizedAtomicExpression replaceOperator(int orig, int repl) {
        if (orig == repl) {
            return this;
        }

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
            else {
                na[i] = m_expression[i];
            }
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

    // -1 meaning wildcard.
    final int[] rootedLiterals(Map<String, String> overMap, Registry vc_Reg) {
        int[] rArray = new int[m_expression.length + 1];
        for (int i = 0; i <= m_expression.length; ++i) {
            int expI =
                    (i < m_expression.length) ? m_expression[i]
                            : m_classConstant;
            String k = m_registry.getSymbolForIndex(expI);
            String v = (overMap.containsKey(k)) ? overMap.get(k) : k;

            if (v.equals("")) {
                rArray[i] = -1;
            }
            else if (!vc_Reg.mySymbolToIndex.containsKey(v)) {
                return null;
            }
            else {
                rArray[i] = vc_Reg.getIndexForSymbol(v);
            }
        }

        return rArray;
    }

    final NormalizedAtomicExpression rootOps() {
        int[] roots = new int[arity + 1];
        for (int i = 0; i < roots.length; ++i) {
            roots[i] = m_registry.findAndCompress(m_expression[i]);
        }

        return new NormalizedAtomicExpression(m_registry, roots);
    }

    // "" meaning mapped.
    final String[] unMappedWildcards(Map<String, String> overMap) {
        String[] rArray = new String[m_expression.length + 1];
        for (int i = 0; i < m_expression.length; ++i) {
            String ks = m_registry.getSymbolForIndex(m_expression[i]);
            if (overMap.containsKey(ks) && overMap.get(ks).equals("")) {
                rArray[i] = ks;
            }
            else {
                rArray[i] = "";
            }
        }

        String ks = m_registry.getSymbolForIndex(m_classConstant);
        if (overMap.containsKey(ks) && overMap.get(ks).equals("")) {
            rArray[m_expression.length] = ks;
        }
        else {
            rArray[m_expression.length] = "";
        }

        return rArray;
    }

    final void writeToRoot(int root) {
        m_opMmap = null;
        m_opIdSet = null;
        m_classConstant = root;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private int numberOfQuants() {
        int c = 0;
        for (String k : getOperatorsAsStrings(false).keySet()) {
            if (m_registry.getUsage(k).equals(Registry.Usage.FORALL)
                    || m_registry.getUsage(k).equals(
                            Registry.Usage.HASARGS_FORALL)
                    || m_registry.getUsage(k).equals(Registry.Usage.CREATED)) {
                c++;
            }
        }

        return c;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>An helper class that compares the number of quantifiers in
     * two {@link NormalizedAtomicExpression NormalizedAtomicExpressions}.</p>
     */
    public static class numQuantsComparator
            implements
                Comparator<NormalizedAtomicExpression> {

        /**
         * <p>Compares {@code nae1} and {@code nae2}.</p>
         *
         * @param nae1 A normalized atomic expression.
         * @param nae2 Another normalized atomic expression
         *
         * @return Comparison results expressed as an integer.
         */
        @Override
        public final int compare(NormalizedAtomicExpression nae1,
                NormalizedAtomicExpression nae2) {
            return nae1.numberOfQuants() - nae2.numberOfQuants();
        }
    }

}