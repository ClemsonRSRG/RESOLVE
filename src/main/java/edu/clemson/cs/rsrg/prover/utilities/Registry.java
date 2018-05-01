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

}