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

    /** <p>A map from argument name to its integer representation.</p> */
    private Map<String, Integer> myArgMap;

    /** <p>Number of arguments</p> */
    private int myArity;

    /** <p>Integer index representing the root symbol.</p> */
    private int myClassConstant;

    /** <p>Index for each normalized atomic expression.</p> */
    private final int[] myExpression;

    /** <p>A set of operation ids.</p> */
    private Set<Integer> myOpIdSet;

    /** <p>A map from operation name to its integer representation.</p> */
    private Map<String, Integer> myOpMap;

    /** <p>Registry for symbols that we have encountered so far.</p> */
    private final Registry myRegistry;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a normalized atomic expression.</p>
     *
     * @param registry A registry of symbols encountered so far.
     * @param intArray An integer array representing each of the symbols
     *                 in this expression.
     */
    public NormalizedAtomicExpression(Registry registry, int[] intArray) {
        myRegistry = registry;
        myArity = intArray.length - 1;

        if (!myRegistry.isCommutative(intArray[0])) {
            myExpression = intArray;
        }
        else {
            int[] ord = new int[myArity];
            System.arraycopy(intArray, 1, ord, 0, intArray.length - 1);

            Arrays.sort(ord);

            int[] ne = new int[intArray.length];
            ne[0] = intArray[0];
            System.arraycopy(ord, 0, ne, 1, ord.length);

            myExpression = ne;
        }

        myClassConstant = -1;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    @Override
    public final boolean equals(Object o) {
        if (o instanceof NormalizedAtomicExpression) {
            NormalizedAtomicExpression other = (NormalizedAtomicExpression) o;

            return Arrays.equals(myExpression, other.myExpression)
                    && other.myRegistry == myRegistry;
        }

        return false;
    }

    public final Map<String, Integer> getOperatorsAsStrings(boolean justArguments) {
        if (justArguments && myArgMap != null) {
            return new HashMap<>(myArgMap);
        }
        if (!justArguments && myOpMap != null) {
            return new HashMap<>(myOpMap);
        }

        myArgMap = new HashMap<>();

        for (int i = 1; i < myExpression.length; ++i) {
            String curOp = readSymbol(i);
            if (myArgMap.containsKey(curOp)) {
                myArgMap.put(curOp, myArgMap.get(curOp) + 1);
            }
            else
                myArgMap.put(curOp, 1);
        }

        myOpMap = new HashMap<>(myArgMap);
        String fSym = readSymbol(0);
        String rSym = myRegistry.getSymbolForIndex(readRoot());
        if (myOpMap.containsKey(fSym)) {
            myOpMap.put(fSym, myOpMap.get(fSym) + 1);
        }
        else {
            myOpMap.put(fSym, 1);
        }

        if (myOpMap.containsKey(rSym)) {
            myOpMap.put(rSym, myOpMap.get(rSym) + 1);
        }
        else {
            myOpMap.put(rSym, 1);
        }

        if (justArguments) {
            return new HashMap<>(myArgMap);
        }
        else {
            return new HashMap<>(myOpMap);
        }
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(myExpression);
    }

    public final int readRoot() {
        return myClassConstant;
    }

    @Override
    public final String toString() {
        String r;
        String funcSymbol = readSymbol(0);

        StringBuilder argsBuilder = new StringBuilder();
        for (int i = 1; i < myExpression.length; ++i) {
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
            r += "=" + myRegistry.getSymbolForIndex(root);
        }

        return r;
    }

    // ===========================================================
    // Package-Private Methods
    // ===========================================================

    final int getArity() {
        return myArity;
    }

    final Set<Integer> getOpIds() {
        if (myOpIdSet != null) {
            return myOpIdSet;
        }

        myOpIdSet = new HashSet<>();

        for (int aM_expression : myExpression) {
            myOpIdSet.add(aM_expression);
        }

        int r = readRoot();
        myOpIdSet.add(r);

        return myOpIdSet;
    }

    // return array contains 'n' if sint is an arg used at position 'n', 0 denotes operator, -1 denotes cong class
    final int[] getPositionsFor(int sint) {
        int[] rArray = new int[myArity + 2];
        int count = 0;
        for (int i = 0; i <= myArity; ++i) {
            if (myExpression[i] == sint) {
                rArray[count++] = i;
            }
        }
        if (readRoot() == sint)
            rArray[count++] = -1;

        return Arrays.copyOf(rArray, count);
    }

    final Registry getRegistry() {
        return myRegistry;
    }

    final boolean hasVarOps() {
        boolean isVar = false;
        for (int i = 0; i < myExpression.length; ++i) {
            String s = readSymbol(i);
            if (s.startsWith("¢v")) {
                isVar = true;
                break;
            }

            Registry.Usage us = myRegistry.getUsage(s);

            if (us == Registry.Usage.FORALL
                    || us == Registry.Usage.HASARGS_FORALL) {
                isVar = true;
                break;
            }
        }

        return isVar;
    }

    final int readPosition(int position) {
        return myExpression[position];
    }

    final String readSymbol(int position) {
        return myRegistry.getSymbolForIndex(myExpression[position]);
    }

    final NormalizedAtomicExpression replaceOperator(int orig, int repl) {
        if (orig == repl) {
            return this;
        }

        if (!getOpIds().contains(orig)) {
            return this;
        }

        int[] na = new int[myExpression.length];
        boolean changed = false;
        for (int i = 0; i < myExpression.length; ++i) {
            if (myExpression[i] == orig) {
                na[i] = repl;
                changed = true;
            }
            else {
                na[i] = myExpression[i];
            }
        }

        if (readRoot() == orig) {
            writeToRoot(repl);
        }

        NormalizedAtomicExpression rNa = this;
        if (changed) {
            rNa = new NormalizedAtomicExpression(myRegistry, na);
            rNa.writeToRoot(readRoot());
        }

        assert (changed == (rNa != this));
        assert changed == (rNa.hashCode() != hashCode());

        return rNa;
    }

    // -1 meaning wildcard.
    final int[] rootedLiterals(Map<String, String> overMap, Registry vc_Reg) {
        int[] rArray = new int[myExpression.length + 1];
        for (int i = 0; i <= myExpression.length; ++i) {
            int expI =
                    (i < myExpression.length) ? myExpression[i]
                            : myClassConstant;
            String k = myRegistry.getSymbolForIndex(expI);
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
        int[] roots = new int[myArity + 1];
        for (int i = 0; i < roots.length; ++i) {
            roots[i] = myRegistry.findAndCompress(myExpression[i]);
        }

        return new NormalizedAtomicExpression(myRegistry, roots);
    }

    // "" meaning mapped.
    final String[] unMappedWildcards(Map<String, String> overMap) {
        String[] rArray = new String[myExpression.length + 1];
        for (int i = 0; i < myExpression.length; ++i) {
            String ks = myRegistry.getSymbolForIndex(myExpression[i]);
            if (overMap.containsKey(ks) && overMap.get(ks).equals("")) {
                rArray[i] = ks;
            }
            else {
                rArray[i] = "";
            }
        }

        String ks = myRegistry.getSymbolForIndex(myClassConstant);
        if (overMap.containsKey(ks) && overMap.get(ks).equals("")) {
            rArray[myExpression.length] = ks;
        }
        else {
            rArray[myExpression.length] = "";
        }

        return rArray;
    }

    final void writeToRoot(int root) {
        myOpMap = null;
        myOpIdSet = null;
        myClassConstant = root;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private int numberOfQuants() {
        int c = 0;
        for (String k : getOperatorsAsStrings(false).keySet()) {
            if (myRegistry.getUsage(k).equals(Registry.Usage.FORALL)
                    || myRegistry.getUsage(k).equals(
                            Registry.Usage.HASARGS_FORALL)
                    || myRegistry.getUsage(k).equals(Registry.Usage.CREATED)) {
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