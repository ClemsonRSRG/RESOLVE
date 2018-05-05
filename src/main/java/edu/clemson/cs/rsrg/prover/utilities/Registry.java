/*
 * Registry.java
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
package edu.clemson.cs.rsrg.prover.utilities;

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.*;

/**
 * <p>This class serves as a registry for the symbols we have encountered
 * during the automated proving process.</p>
 *
 * @author Mike Khabbani
 * @version 2.0
 */
public class Registry {

    // ===========================================================
    // Usage
    // ===========================================================

    /**
     * <p>This defines the various usage types for a symbol.</p>
     *
     * @version 2.0
     */
    public enum Usage {
        LITERAL, FORALL, SINGULAR_VARIABLE, CREATED, HASARGS_SINGULAR,
        HASARGS_FORALL
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    public Registry(TypeGraph g) {
        m_symbolToIndex = new TreeMap<String, Integer>();
        m_typeToSetOfOperators = new HashMap<MTType, TreeSet<String>>();
        m_indexToSymbol = new ArrayList<String>();
        m_indexToType = new ArrayList<MTType>();
        m_symbolIndexParentArray = new ArrayList<Integer>();
        m_unusedIndices = new Stack<Integer>();
        m_symbolToUsage = new HashMap<String, Usage>(2048, .5f); // entries won't change
        m_foralls = new HashSet<String>();
        m_typeGraph = g;
        m_typeDictionary = new TreeMap<String, MTType>();
        addSymbol("=B", new MTFunction(g, g.BOOLEAN, g.ENTITY, g.ENTITY),
                Usage.LITERAL); // = as a predicate function, not as an assertion
        addSymbol("true", g.BOOLEAN, Usage.LITERAL);
        addSymbol("false", g.BOOLEAN, Usage.LITERAL);
        assert (getIndexForSymbol("=B") == 0);
        m_appliedTheoremDependencyGraph = new HashMap<String, Set<Integer>>();
        m_lambda_names = new HashSet<String>();
        m_partTypes = new HashSet<String>();
        m_partTypeParentArray = new HashMap<Integer, ArrayList<Integer>>();

        // could look for these in theorems instead
        m_commutative_operators = new HashSet<String>();
        m_commutative_operators.add("+N");
        m_commutative_operators.add("+Z");
        m_commutative_operators.add("=B");
        m_commutative_operators.add("andB");
        m_commutative_operators.add("orB");
        m_cached_isSubtype = new HashMap<String, Boolean>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method adds the symbol to the registry if it is new,
     * otherwise it returns the integer representation for that symbol.</p>
     *
     * @param symbolName Symbol name
     * @param symbolType Symbol's mathematical type.
     * @param usage Symbol's usage type.
     *
     * @return An integer representing the symbol.
     */
    public final int addSymbol(String symbolName, MTType symbolType, Usage usage) {
        symbolName = symbolName.replaceAll("\\p{Cc}", "");
        if (symbolName.contains("lambda"))
            m_lambda_names.add(symbolName);
        assert symbolName.length() != 0 : "blank symbol error in addSymbol";
        if (isSymbolInTable(symbolName)) {
            return getIndexForSymbol(symbolName);
        }
        if (symbolName.contains(".")) {
            m_partTypes.add(symbolName);
        }

        if (m_typeToSetOfOperators.containsKey(symbolType)) {
            m_typeToSetOfOperators.get(symbolType).add(symbolName);
        }
        else {
            TreeSet<String> t = new TreeSet<>();
            t.add(symbolName);
            assert symbolType != null : symbolName + " has null type";
            if (symbolType != null) {
                m_typeToSetOfOperators.put(symbolType, t);
                m_typeDictionary.put(symbolType.toString().replace("'", ""),
                        symbolType);
            }
        }

        m_symbolToUsage.put(symbolName, usage);
        if (usage.equals(Usage.FORALL) || usage.equals(Usage.HASARGS_FORALL)) {
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

    /**
     * <p>This method uses the passed in integer index and attempts
     * to compress the symbol indices.</p>
     *
     * @param index Integer index to be compressed.
     *
     * @return The compressed integer index.
     */
    public final int findAndCompress(int index) {
        // early return for parent
        if (m_symbolIndexParentArray.get(index) == index)
            return index;

        Stack<Integer> needToUpdate = new Stack<>();

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

    //

    /**
     * <p>This method gets the set of children symbol names.</p>
     *
     * <p><em>Note:</em> Use sparingly, call with a parent symbol.
     * Assumes parent array is compressed.</p>
     *
     * @param parent A parent symbol name.
     *
     * @return A set of children symbol names.
     */
    public final Set<String> getChildren(String parent) {
        int pInt = getIndexForSymbol(parent);
        HashSet<Integer> ch = new HashSet<>();
        for (int i = 0; i < m_symbolIndexParentArray.size(); ++i) {
            if (i == pInt)
                continue;
            if (m_symbolIndexParentArray.get(i) == pInt) {
                ch.add(i);
            }
        }

        HashSet<String> rSet = new HashSet<>();
        for (Integer i : ch) {
            rSet.add(m_indexToSymbol.get(i));
        }

        return rSet;
    }

    /**
     * <p>This method returns all the symbols that are
     * universally bounded.</p>
     *
     * @return A set of symbols names.
     */
    public final Set<String> getForAlls() {
        return m_foralls;
    }

    /**
     * <p>This method returns the integer index that represents
     * this symbol.</p>
     *
     * @param symbol The symbol name we are searching.
     *
     * @return The associated integer index.
     */
    public final int getIndexForSymbol(String symbol) {
        assert m_symbolToIndex.get(symbol) != null : symbol + " not found"
                + m_symbolToIndex.toString();

        if (!m_symbolToIndex.containsKey(symbol)) {
            return -1;
        }

        int r = m_symbolToIndex.get(symbol);

        return findAndCompress(r);
    }

    /**
     * <p>This method returns all the parent symbols that
     * have the same type.</p>
     *
     * @param t A mathematical type.
     *
     * @return A set of symbols that have type {@code t}.
     */
    public final Set<String> getParentsByType(MTType t) {
        Set<String> rSet = getSetMatchingType(t);
        Set<String> fSet = new HashSet<>();
        for (String s : rSet) {
            int id = getIndexForSymbol(s);
            if (m_symbolIndexParentArray.get(id) == id) {
                fSet.add(s);
            }
        }

        return fSet;
    }

    /**
     * <p>This method returns all the root symbol for {@code sym}.</p>
     *
     * @param sym The symbol name we are searching.
     *
     * @return Root symbol name.
     */
    public final String getRootSymbolForSymbol(String sym) {
        if (m_symbolToIndex.containsKey(sym)) {
            return getSymbolForIndex(getIndexForSymbol(sym));
        }
        else {
            return "";
        }
    }

    /**
     * <p>This method returns the symbol located at the
     * specified index.</p>
     *
     * @param index An index referring to a symbol in our registry.
     *
     * @return The associated symbol.
     */
    public final String getSymbolForIndex(int index) {
        assert index >= 0 : "invalid index: " + index
                + " in Registry.getSymbolForIndex";

        String rS = m_indexToSymbol.get(findAndCompress(index));
        assert rS.length() != 0 : "Blank symbol error";

        return rS;
    }

    /**
     * <p>This method returns the mathematical type of the
     * symbol at the specified index.</p>
     *
     * @param index An index referring to a symbol in our registry.
     *
     * @return A {@link MTType}.
     */
    public final MTType getTypeByIndex(int index) {
        return m_indexToType.get(findAndCompress(index));
    }

    /**
     * <p>This method returns the usage type for the specified
     * symbol.</p>
     *
     * @param symbol The symbol name we are searching.
     *
     * @return The usage type.
     */
    public final Usage getUsage(String symbol) {
        return m_symbolToUsage.get(symbol);
    }

    /**
     * <p>This method checks to see if the operation is
     * commutative.</p>
     *
     * @param opNum An index referring to an operation.
     *
     * @return {@code true} if it is commutative,
     * {@code false} otherwise.
     */
    public final boolean isCommutative(int opNum) {
        String root = getSymbolForIndex(opNum);

        return isCommutative(root);
    }

    /**
     * <p>This method checks if {@code a} is a subtype of
     * {@code b} and caches the result for future queries.</p>
     *
     * @param a Mathematical type A.
     * @param b Mathematical type B.
     *
     * @return {@code true} if it is a subtype, {@code false} otherwise.
     */
    public final boolean isSubtype(MTType a, MTType b) {
        String catKey = a.toString() + "," + b.toString();

        // Check our cached results
        if (m_cached_isSubtype.containsKey(catKey)) {
            return m_cached_isSubtype.get(catKey);
        }
        else {
            // Determine if it is subtype and add it to our cache
            boolean is = a.isSubtypeOf(b);
            m_cached_isSubtype.put(catKey, is);

            return is;
        }
    }

    /**
     * <p>This method converts a mathematical type to a symbol.</p>
     *
     * @param symbolType A mathematical type.
     * @param isVariable A flag that indicates if this is a variable.
     *
     * @return The index associated with this new symbol.
     */
    public final int makeSymbol(MTType symbolType, boolean isVariable) {
        String symbolName;
        if (isVariable) {
            symbolName = String.format(m_cvFormat, m_uniqueCounter++);
        }
        else {
            symbolName = String.format(m_ccFormat, m_uniqueCounter++);
        }

        return addSymbol(symbolName, symbolType, Usage.CREATED);
    }

    /**
     * <p>This method substitutes the indices of A and B.</p>
     *
     * @param opIndexA index that becomes parent of B
     * @param opIndexB index to be replaced by opIndexA
     */
    public final void substitute(int opIndexA, int opIndexB) {
        MTType aType = getTypeByIndex(opIndexA);
        MTType bType = getTypeByIndex(opIndexB);

        // set usage to most restricted: i.e literal over created over forall
        // this is because the earliest now becomes the parent
        String aS = getSymbolForIndex(opIndexA);
        String bS = getSymbolForIndex(opIndexB);
        Usage a_us = getUsage(aS);
        Usage b_us = getUsage(bS);
        if (!a_us.equals(Usage.FORALL) && isSubtype(bType, aType)) {
            m_indexToType.set(opIndexA, bType);
        }

        if (a_us.equals(Usage.LITERAL) || b_us.equals(Usage.LITERAL)) {
            m_symbolToUsage.put(aS, Usage.LITERAL);
        }
        else if (a_us.equals(Usage.CREATED) || b_us.equals(Usage.CREATED)) {
            m_symbolToUsage.put(aS, Usage.CREATED);
        }

        if (m_partTypes.contains(bS)) {
            m_partTypes.add(aS);
        }

        m_unusedIndices.push(opIndexB);
        m_symbolIndexParentArray.set(opIndexB, opIndexA);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method for retrieving all symbols
     * that are associated with {@code t} or any of its subtypes.</p>
     *
     * @param t A mathematical type.
     *
     * @return A set of symbol names.
     */
    private Set<String> getSetMatchingType(MTType t) {
        assert t != null : "request for null type";
        Set<String> rSet = new HashSet<>();
        Set<MTType> allTypesInSet = m_typeToSetOfOperators.keySet();

        assert !m_typeToSetOfOperators.isEmpty() : "empty m_typeToSetOfOperator.keySet()";
        assert allTypesInSet != null : "null set in Registry.getSetMatchingType";

        // if there are subtypes of t, return those too
        for (MTType m : allTypesInSet) {
            assert m != null : "null entry in allTypesInSet";
            if (isSubtype(m, t)) {
                rSet.addAll(m_typeToSetOfOperators.get(m));
            }
        }

        if (m_typeToSetOfOperators.get(t) != null) {
            rSet.addAll(m_typeToSetOfOperators.get(t));
        }

        return rSet;
    }

    /**
     * <p>An helper method that checks if an symbol
     * is a commutative operator.</p>
     *
     * @param op The symbol name we are searching.
     *
     * @return {@code true} if {@code op} is an commutative
     * operator, {@code false} otherwise.
     */
    private boolean isCommutative(String op) {
        return m_commutative_operators.contains(op);
    }

    /**
     * <p>An helper method that checks to see if a symbol is in
     * our registry table.</p>
     *
     * @param symbol The symbol name we are searching.
     *
     * @return {@code true} if it is in our registry table,
     * {@code false} otherwise.
     */
    private boolean isSymbolInTable(String symbol) {
        return m_symbolToIndex.containsKey(symbol);
    }

}