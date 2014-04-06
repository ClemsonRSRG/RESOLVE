/**
 * Registry.java
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

import edu.clemson.cs.r2jt.typeandpopulate.MTType;

import java.util.*;

/**
 * Created by mike on 4/3/2014.
 */
public class Registry {

    private final String m_ccFormat = "¢%03d";
    public TreeMap<String, Integer> m_symbolToIndex;
    public Map<MTType, TreeSet<String>> m_typeToSetOfOperators;
    public Vector<String> m_indexToSymbol;
    public Vector<MTType> m_indexToType;
    public Vector<Integer> m_symbolIndexParentArray;
    public Stack<Integer> m_unusedIndices;
    private int m_uniqueCounter = 0;

    public Registry() {
        m_symbolToIndex = new TreeMap<String, Integer>();
        m_typeToSetOfOperators = new HashMap<MTType, TreeSet<String>>();
        m_indexToSymbol = new Vector<String>();
        m_indexToType = new Vector<MTType>();
        m_symbolIndexParentArray = new Vector<Integer>();
        m_unusedIndices = new Stack<Integer>();
        addSymbol("true", null);
    }

    public Set<String> getSetMatchingType(MTType t) {
        Set<String> rSet = new HashSet<String>();
        Set<MTType> allTypesInSet = m_typeToSetOfOperators.keySet();
        // if there are subtypes of t, return those too
        for (MTType m : allTypesInSet) {
            if (m.isSubtypeOf(t))
                rSet.addAll(m_typeToSetOfOperators.get(m));
        }
        return m_typeToSetOfOperators.get(t);
    }

    /**
     *
     * @param opIndexA index that becomes parent of B
     * @param opIndexB index to be replaced by opIndexA
     */
    public void substitute(int opIndexA, int opIndexB) {
        m_unusedIndices.push(opIndexB);
        m_symbolIndexParentArray.set(opIndexB, opIndexA);
    }

    protected int findAndCompress(int index) {
        Stack<Integer> needToUpdate = new Stack<Integer>();
        int parent = m_symbolIndexParentArray.get(index);
        while (parent != index) {
            needToUpdate.push(index);
            index = parent;
            parent = m_symbolIndexParentArray.get(index);
        }
        while (!needToUpdate.isEmpty()) {
            m_symbolIndexParentArray.set(needToUpdate.pop(), index);
        }

        return index;
    }

    public String getSymbolForIndex(int index) {
        return m_indexToSymbol.get(findAndCompress(index));
    }

    public MTType getTypeByIndex(int index) {
        return m_indexToType.get(findAndCompress(index));
    }

    public boolean isSymbolInTable(String symbol) {
        return m_symbolToIndex.containsKey(symbol);
    }

    public int getIndexForSymbol(String symbol) {
        assert m_symbolToIndex.get(symbol) != null : symbol + " not found"
                + m_symbolToIndex.toString();
        int r = m_symbolToIndex.get(symbol);
        if (r < 0)
            System.err.println(symbol + " has no current index");
        return findAndCompress(r);
    }

    public int makeSymbol(MTType symbolType) {
        String symbolName = String.format(m_ccFormat, m_uniqueCounter++);
        return addSymbol(symbolName, symbolType);
    }

    // if symbol is new, it adds it, otherwise, it returns current int rep
    public int addSymbol(String symbolName, MTType symbolType) {
        if (isSymbolInTable(symbolName))
            return getIndexForSymbol(symbolName);

        if (m_typeToSetOfOperators.containsKey(symbolType))
            m_typeToSetOfOperators.get(symbolType).add(symbolName);
        else {
            TreeSet<String> t = new TreeSet<String>();
            t.add(symbolName);
            m_typeToSetOfOperators.put(symbolType, t);
        }

        int incomingsize = m_symbolToIndex.size();
        m_symbolToIndex.put(symbolName, m_symbolToIndex.size());
        m_indexToSymbol.add(symbolName);
        m_indexToType.add(symbolType);
        m_symbolIndexParentArray.add(incomingsize);
        assert m_symbolToIndex.size() == m_indexToSymbol.size();
        assert incomingsize < m_symbolToIndex.size();
        return m_symbolToIndex.size() - 1;
    }

    public void flushUnusedSymbols() {
    // probably have to shift vectors
    // maybe need to renumber things

    }
}
