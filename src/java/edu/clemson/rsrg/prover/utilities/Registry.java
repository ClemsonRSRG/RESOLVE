/*
 * Registry.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.utilities;

import edu.clemson.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.*;

/**
 * <p>
 * This class serves as a registry for the symbols we have encountered during the automated proving process.
 * </p>
 *
 * @author Mike Khabbani
 *
 * @version 2.0
 */
public class Registry {

    // ===========================================================
    // Usage
    // ===========================================================

    /**
     * <p>
     * This defines the various usage types for a symbol.
     * </p>
     *
     * @version 2.0
     */
    public enum Usage {
        LITERAL, FORALL, SINGULAR_VARIABLE, CREATED, HASARGS_SINGULAR, HASARGS_FORALL
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A map that caches the isSubtype results.
     * </p>
     */
    private final Map<String, Boolean> myCachedIsSubtype;

    /**
     * <p>
     * A set of operators that are commutative.
     * </p>
     */
    private final Set<String> myCommutativeOperators;

    /**
     * <p>
     * A set of symbol names that are universally quantified.
     * </p>
     */
    private final Set<String> myForAlls;

    /**
     * <p>
     * A set of names that come from some lambda expression.
     * </p>
     */
    private final Set<String> myLambdaNames;

    /**
     * <p>
     * A set that keeps track of dotted symbols.
     * </p>
     */
    private final Set<String> myPartTypes;

    /**
     * <p>
     * A map from part type index to parent array.
     * </p>
     */
    private final Map<Integer, ArrayList<Integer>> myPartTypeParentArray;

    /**
     * <p>
     * A map from symbol name to usage type.
     * </p>
     */
    private final Map<String, Usage> mySymbolToUsage;

    /**
     * <p>
     * A map from symbol name to type.
     * </p>
     */
    private final Map<String, MTType> myTypeDictionary;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>
     * A stack containing indices that have been set to unused.
     * </p>
     */
    private final Stack<Integer> myUnusedIndices;

    // -----------------------------------------------------------
    // Public fields
    // -----------------------------------------------------------

    /**
     * <p>
     * A list of symbol names.
     * </p>
     */
    public final ArrayList<String> myIndexToSymbol;

    /**
     * <p>
     * A list of mathematical types.
     * </p>
     */
    public final ArrayList<MTType> myIndexToType;

    /**
     * <p>
     * A tree map from symbol names to their associated index.
     * </p>
     */
    public final TreeMap<String, Integer> mySymbolToIndex;

    /**
     * <p>
     * A map from a mathematical type to the set of associated operators.
     * </p>
     */
    public final Map<MTType, TreeSet<String>> myTypeToSetOfOperators;

    /**
     * <p>
     * A list of indices referring to a parent array.
     * </p>
     */
    public final ArrayList<Integer> mySymbolIndexParentArray;

    // -----------------------------------------------------------
    // MakeSymbol-related
    // -----------------------------------------------------------

    /**
     * <p>
     * Regex for regular symbols.
     * </p>
     */
    private final String myCCFormat = "¢c%03d";

    /**
     * <p>
     * Regex for variable symbols.
     * </p>
     */
    private final String myCVFormat = "¢v%03d";

    /**
     * <p>
     * Counter for keeping track of number of symbols created.
     * </p>
     */
    private int myUniqueCounter = 0;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a registry table containing the various different pieces of information on symbols we have
     * encountered.
     * </p>
     *
     * @param g
     *            The current type graph.
     */
    public Registry(TypeGraph g) {
        mySymbolToIndex = new TreeMap<>();
        myTypeToSetOfOperators = new HashMap<>();
        myIndexToSymbol = new ArrayList<>();
        myIndexToType = new ArrayList<>();
        mySymbolIndexParentArray = new ArrayList<>();
        myUnusedIndices = new Stack<>();
        mySymbolToUsage = new HashMap<>(2048, .5f); // entries won't change
        myForAlls = new HashSet<>();
        myTypeGraph = g;
        myTypeDictionary = new TreeMap<>();

        addSymbol("=B", new MTFunction(g, g.BOOLEAN, g.ENTITY, g.ENTITY), Usage.LITERAL); // = as a
                                                                                          // predicate
                                                                                          // function,
                                                                                          // not as an
                                                                                          // assertion
        addSymbol("true", g.BOOLEAN, Usage.LITERAL);
        addSymbol("false", g.BOOLEAN, Usage.LITERAL);

        assert (getIndexForSymbol("=B") == 0);

        myLambdaNames = new HashSet<>();
        myPartTypes = new HashSet<>();
        myPartTypeParentArray = new HashMap<>();

        // could look for these in theorems instead
        myCommutativeOperators = new HashSet<>();
        myCommutativeOperators.add("+N");
        myCommutativeOperators.add("+Z");
        myCommutativeOperators.add("=B");
        myCommutativeOperators.add("andB");
        myCommutativeOperators.add("orB");

        myCachedIsSubtype = new HashMap<>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method adds the symbol to the registry if it is new, otherwise it returns the integer representation for
     * that symbol.
     * </p>
     *
     * @param symbolName
     *            Symbol name
     * @param symbolType
     *            Symbol's mathematical type.
     * @param usage
     *            Symbol's usage type.
     *
     * @return An integer representing the symbol.
     */
    public final int addSymbol(String symbolName, MTType symbolType, Usage usage) {
        symbolName = symbolName.replaceAll("\\p{Cc}", "");
        if (symbolName.contains("lambda")) {
            myLambdaNames.add(symbolName);
        }

        assert symbolName.length() != 0 : "blank symbol error in addSymbol";

        if (isSymbolInTable(symbolName)) {
            return getIndexForSymbol(symbolName);
        }

        if (symbolName.contains(".")) {
            myPartTypes.add(symbolName);
        }

        if (myTypeToSetOfOperators.containsKey(symbolType)) {
            myTypeToSetOfOperators.get(symbolType).add(symbolName);
        } else {
            TreeSet<String> t = new TreeSet<>();
            t.add(symbolName);
            assert symbolType != null : symbolName + " has null type";
            if (symbolType != null) {
                myTypeToSetOfOperators.put(symbolType, t);
                myTypeDictionary.put(symbolType.toString().replace("'", ""), symbolType);
            }
        }

        mySymbolToUsage.put(symbolName, usage);

        if (usage.equals(Usage.FORALL) || usage.equals(Usage.HASARGS_FORALL)) {
            myForAlls.add(symbolName);
        }

        int incomingsize = mySymbolToIndex.size();
        mySymbolToIndex.put(symbolName, mySymbolToIndex.size());
        myIndexToSymbol.add(symbolName);
        myIndexToType.add(symbolType);
        mySymbolIndexParentArray.add(incomingsize);

        assert mySymbolToIndex.size() == myIndexToSymbol.size();
        assert incomingsize < mySymbolToIndex.size();

        return mySymbolToIndex.size() - 1;
    }

    /**
     * <p>
     * This method uses the passed in integer index and attempts to compress the symbol indices.
     * </p>
     *
     * @param index
     *            Integer index to be compressed.
     *
     * @return The compressed integer index.
     */
    public final int findAndCompress(int index) {
        // early return for parent
        if (mySymbolIndexParentArray.get(index) == index)
            return index;

        Stack<Integer> needToUpdate = new Stack<>();

        assert index < mySymbolIndexParentArray.size() : "findAndCompress error";

        int parent = mySymbolIndexParentArray.get(index);
        while (parent != index) {
            needToUpdate.push(index);
            index = parent;
            parent = mySymbolIndexParentArray.get(index);
        }

        while (!needToUpdate.isEmpty()) {
            mySymbolIndexParentArray.set(needToUpdate.pop(), index);
        }

        return index;
    }

    /**
     * <p>
     * This method gets the set of children symbol names.
     * </p>
     *
     * <p>
     * <em>Note:</em> Use sparingly, call with a parent symbol. Assumes parent array is compressed.
     * </p>
     *
     * @param parent
     *            A parent symbol name.
     *
     * @return A set of children symbol names.
     */
    public final Set<String> getChildren(String parent) {
        int pInt = getIndexForSymbol(parent);
        HashSet<Integer> ch = new HashSet<>();
        for (int i = 0; i < mySymbolIndexParentArray.size(); ++i) {
            if (i == pInt)
                continue;
            if (mySymbolIndexParentArray.get(i) == pInt) {
                ch.add(i);
            }
        }

        HashSet<String> rSet = new HashSet<>();
        for (Integer i : ch) {
            rSet.add(myIndexToSymbol.get(i));
        }

        return rSet;
    }

    /**
     * <p>
     * This method returns all the symbols that are universally bounded.
     * </p>
     *
     * @return A set of symbols names.
     */
    public final Set<String> getForAlls() {
        return myForAlls;
    }

    /**
     * <p>
     * This method returns the integer index that represents this symbol.
     * </p>
     *
     * @param symbol
     *            The symbol name we are searching.
     *
     * @return The associated integer index.
     */
    public final int getIndexForSymbol(String symbol) {
        assert mySymbolToIndex.get(symbol) != null : symbol + " not found" + mySymbolToIndex.toString();

        if (!mySymbolToIndex.containsKey(symbol)) {
            return -1;
        }

        int r = mySymbolToIndex.get(symbol);

        return findAndCompress(r);
    }

    /**
     * <p>
     * This method returns all the parent symbols that have the same type.
     * </p>
     *
     * @param t
     *            A mathematical type.
     *
     * @return A set of symbols that have type {@code t}.
     */
    public final Set<String> getParentsByType(MTType t) {
        Set<String> rSet = getSetMatchingType(t);
        Set<String> fSet = new HashSet<>();
        for (String s : rSet) {
            int id = getIndexForSymbol(s);
            if (mySymbolIndexParentArray.get(id) == id) {
                fSet.add(s);
            }
        }

        return fSet;
    }

    /**
     * <p>
     * This method returns all the root symbol for {@code sym}.
     * </p>
     *
     * @param sym
     *            The symbol name we are searching.
     *
     * @return Root symbol name.
     */
    public final String getRootSymbolForSymbol(String sym) {
        if (mySymbolToIndex.containsKey(sym)) {
            return getSymbolForIndex(getIndexForSymbol(sym));
        } else {
            return "";
        }
    }

    /**
     * <p>
     * This method returns the symbol located at the specified index.
     * </p>
     *
     * @param index
     *            An index referring to a symbol in our registry.
     *
     * @return The associated symbol.
     */
    public final String getSymbolForIndex(int index) {
        assert index >= 0 : "invalid index: " + index + " in Registry.getSymbolForIndex";

        String rS = myIndexToSymbol.get(findAndCompress(index));
        assert rS.length() != 0 : "Blank symbol error";

        return rS;
    }

    /**
     * <p>
     * This method returns the mathematical type of the symbol at the specified index.
     * </p>
     *
     * @param index
     *            An index referring to a symbol in our registry.
     *
     * @return A {@link MTType}.
     */
    public final MTType getTypeByIndex(int index) {
        return myIndexToType.get(findAndCompress(index));
    }

    /**
     * <p>
     * The type graph containing all the type relationships.
     * </p>
     *
     * @return The type graph for the compiler.
     */
    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    /**
     * <p>
     * This method returns the usage type for the specified symbol.
     * </p>
     *
     * @param symbol
     *            The symbol name we are searching.
     *
     * @return The usage type.
     */
    public final Usage getUsage(String symbol) {
        return mySymbolToUsage.get(symbol);
    }

    /**
     * <p>
     * This method checks to see if the operation is commutative.
     * </p>
     *
     * @param opNum
     *            An index referring to an operation.
     *
     * @return {@code true} if it is commutative, {@code false} otherwise.
     */
    public final boolean isCommutative(int opNum) {
        String root = getSymbolForIndex(opNum);

        return isCommutative(root);
    }

    /**
     * <p>
     * This method checks if {@code a} is a subtype of {@code b} and caches the result for future queries.
     * </p>
     *
     * @param a
     *            Mathematical type A.
     * @param b
     *            Mathematical type B.
     *
     * @return {@code true} if it is a subtype, {@code false} otherwise.
     */
    public final boolean isSubtype(MTType a, MTType b) {
        String catKey = a.toString() + "," + b.toString();

        // Check our cached results
        if (myCachedIsSubtype.containsKey(catKey)) {
            return myCachedIsSubtype.get(catKey);
        } else {
            // Determine if it is subtype and add it to our cache
            boolean is = a.isSubtypeOf(b);
            myCachedIsSubtype.put(catKey, is);

            return is;
        }
    }

    /**
     * <p>
     * This method converts a mathematical type to a symbol.
     * </p>
     *
     * @param symbolType
     *            A mathematical type.
     * @param isVariable
     *            A flag that indicates if this is a variable.
     *
     * @return The index associated with this new symbol.
     */
    public final int makeSymbol(MTType symbolType, boolean isVariable) {
        String symbolName;
        if (isVariable) {
            symbolName = String.format(myCVFormat, myUniqueCounter++);
        } else {
            symbolName = String.format(myCCFormat, myUniqueCounter++);
        }

        return addSymbol(symbolName, symbolType, Usage.CREATED);
    }

    /**
     * <p>
     * This method substitutes the indices of A and B.
     * </p>
     *
     * @param opIndexA
     *            index that becomes parent of B
     * @param opIndexB
     *            index to be replaced by opIndexA
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
            myIndexToType.set(opIndexA, bType);
        }

        if (a_us.equals(Usage.LITERAL) || b_us.equals(Usage.LITERAL)) {
            mySymbolToUsage.put(aS, Usage.LITERAL);
        } else if (a_us.equals(Usage.CREATED) || b_us.equals(Usage.CREATED)) {
            mySymbolToUsage.put(aS, Usage.CREATED);
        }

        if (myPartTypes.contains(bS)) {
            myPartTypes.add(aS);
        }

        myUnusedIndices.push(opIndexB);
        mySymbolIndexParentArray.set(opIndexB, opIndexA);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for retrieving all symbols that are associated with {@code t} or any of its subtypes.
     * </p>
     *
     * @param t
     *            A mathematical type.
     *
     * @return A set of symbol names.
     */
    private Set<String> getSetMatchingType(MTType t) {
        assert t != null : "request for null type";
        Set<String> rSet = new HashSet<>();
        Set<MTType> allTypesInSet = myTypeToSetOfOperators.keySet();

        assert !myTypeToSetOfOperators.isEmpty() : "empty m_typeToSetOfOperator.keySet()";
        assert allTypesInSet != null : "null set in Registry.getSetMatchingType";

        // if there are subtypes of t, return those too
        for (MTType m : allTypesInSet) {
            assert m != null : "null entry in allTypesInSet";
            if (isSubtype(m, t)) {
                rSet.addAll(myTypeToSetOfOperators.get(m));
            }
        }

        if (myTypeToSetOfOperators.get(t) != null) {
            rSet.addAll(myTypeToSetOfOperators.get(t));
        }

        return rSet;
    }

    /**
     * <p>
     * An helper method that checks if an symbol is a commutative operator.
     * </p>
     *
     * @param op
     *            The symbol name we are searching.
     *
     * @return {@code true} if {@code op} is an commutative operator, {@code false} otherwise.
     */
    private boolean isCommutative(String op) {
        return myCommutativeOperators.contains(op);
    }

    /**
     * <p>
     * An helper method that checks to see if a symbol is in our registry table.
     * </p>
     *
     * @param symbol
     *            The symbol name we are searching.
     *
     * @return {@code true} if it is in our registry table, {@code false} otherwise.
     */
    private boolean isSymbolInTable(String symbol) {
        return mySymbolToIndex.containsKey(symbol);
    }

}
