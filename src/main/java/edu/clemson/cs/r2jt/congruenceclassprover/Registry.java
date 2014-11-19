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

import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by mike on 4/3/2014.
 */
public class Registry {

    public final String m_ccFormat = "¢c%03d";
    public final String m_cvFormat = "¢v%03d";
    public TreeMap<String, Integer> m_symbolToIndex;
    public Map<MTType, TreeSet<String>> m_typeToSetOfOperators;
    public ArrayList<String> m_indexToSymbol;
    public ArrayList<MTType> m_indexToType;
    public ArrayList<Integer> m_symbolIndexParentArray;
    public Stack<Integer> m_unusedIndices;
    private int m_uniqueCounter = 0;
    protected TypeGraph m_typeGraph;

    public static enum Usage {

        LITERAL, FORALL, SINGULAR_VARIABLE, CREATED, HASARGS_SINGULAR,
        HASARGS_FORALL
    };

    private final Map<String, Usage> m_symbolToUsage;
    private final Set<String> m_foralls;
    protected Map<String, MTType> m_typeDictionary;

    public Registry(TypeGraph g) {
        m_symbolToIndex = new TreeMap<String, Integer>();
        m_typeToSetOfOperators = new HashMap<MTType, TreeSet<String>>();
        m_indexToSymbol = new ArrayList<String>();
        m_indexToType = new ArrayList<MTType>();
        m_symbolIndexParentArray = new ArrayList<Integer>();
        m_unusedIndices = new Stack<Integer>();
        m_symbolToUsage = new HashMap<String, Usage>(); // entries won't change
        m_foralls = new HashSet<String>();
        m_typeGraph = g;
        m_typeDictionary = new TreeMap<String, MTType>();
        addSymbol("=", g.BOOLEAN, Usage.LITERAL); // = as a predicate function, not as an assertion
        addSymbol("true", g.BOOLEAN, Usage.LITERAL);
        assert (getIndexForSymbol("=") == 0);

    }

    public Usage getUsage(String symbol) {
        return m_symbolToUsage.get(symbol);
    }

    public Set<String> getSetMatchingType(MTType t) {
        Set<String> rSet = new HashSet<String>();
        Set<MTType> allTypesInSet = m_typeToSetOfOperators.keySet();
        assert !m_typeToSetOfOperators.isEmpty() : "empty m_typeToSetOfOperator.keySet()";
        assert allTypesInSet != null : "null set in Registry.getSetMatchingType";
        // if there are subtypes of t, return those too
        for (MTType m : allTypesInSet) {
            assert m != null : "null entry in allTypesInSet";
            if (m.isSubtypeOf(t)) {
                rSet.addAll(m_typeToSetOfOperators.get(m));
            }
        }
        rSet.addAll(m_typeToSetOfOperators.get(t));
        return rSet;
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
        assert index < m_symbolIndexParentArray.size() : "findAndCompress error";
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
        if (r < 0) {
            System.err.println(symbol + " has no current index");
        }
        return findAndCompress(r);
    }

    public Set<String> getForAlls() {
        return m_foralls;
    }

    public int makeSymbol(MTType symbolType, boolean isVariable) {
        String symbolName = "";
        if (isVariable)
            symbolName = String.format(m_cvFormat, m_uniqueCounter++);
        else
            symbolName = String.format(m_ccFormat, m_uniqueCounter++);
        return addSymbol(symbolName, symbolType, Usage.CREATED);
    }

    // if symbol is new, it adds it, otherwise, it returns current int rep
    public int addSymbol(String symbolName, MTType symbolType, Usage usage) {
        // temporary until type system is fixed
        if (symbolName.equals("Integer"))
            symbolName = "Z";
        if (isSymbolInTable(symbolName)) {
            return getIndexForSymbol(symbolName);
        }
        if (symbolName.equals("Az")) {
            symbolType =
                    new MTFunction(m_typeGraph, m_typeGraph.Z, m_typeGraph.Z);
        }
        if (m_typeToSetOfOperators.containsKey(symbolType)) {
            m_typeToSetOfOperators.get(symbolType).add(symbolName);
        }
        else {
            TreeSet<String> t = new TreeSet<String>();
            t.add(symbolName);
            assert symbolType != null : symbolName + " has null type";
            if (symbolType != null) {
                m_typeToSetOfOperators.put(symbolType, t);
                m_typeDictionary.put(symbolType.toString().replace("'", ""),
                        symbolType);
            }
        }

        m_symbolToUsage.put(symbolName, usage);
        if (usage.equals(Usage.FORALL)) {
            m_foralls.add(symbolName);
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

    public void flushUnusedSymbols() {}

    public Set<String> getFunctionNames() {
        HashSet<String> rSet = new HashSet<String>();
        for (Entry<String, Usage> e : m_symbolToUsage.entrySet()) {
            if (e.getValue().equals(Usage.HASARGS_SINGULAR)) {
                rSet.add(e.getKey());
            }
        }
        return rSet;
    }
}
