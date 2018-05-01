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

}