/*
 * VariableReplacingVisitor.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.typevisitor;

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTNamed;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * <p>This class visits the named types to see if any of the
 * given names needs to be replaced.</p>
 *
 * @version 2.0
 */
public class VariableReplacingVisitor extends MutatingVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>A map of substituting types.</p> */
    private final Map<String, MTType> mySubstitutions;

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This constructs a visitor used to replace all variable
     * that are provided in the map.</p>
     *
     * @param substitutions A map of substitution types as strings.
     * @param g The current type graph.
     */
    public VariableReplacingVisitor(Map<String, String> substitutions,
            TypeGraph g) {
        mySubstitutions = convertToMTNamedMap(substitutions, g);
    }

    /**
     * <p>This constructs a visitor used to replace all variable
     * that are provided in the map.</p>
     *
     * @param substitutions A map of substituting types.
     */
    public VariableReplacingVisitor(Map<String, MTType> substitutions) {
        mySubstitutions = new HashMap<>(substitutions);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTNamed} by replacing it with the {@link MTType}
     * with the changes provided by the map.</p>
     *
     * @param t A math type.
     */
    @Override
    public final void endMTNamed(MTNamed t) {
        if (mySubstitutions.containsKey(t.getName())) {
            try {
                getInnermostBinding(t.getName());
                //This is bound to some inner scope
            }
            catch (NoSuchElementException e) {
                replaceWith(mySubstitutions.get(t.getName()));
            }
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This method returns a map of substituting types.</p>
     *
     * @param original A map of substitution types as strings.
     * @param g The current type graph.
     *
     * @return A map from {@link String} to {@link MTType}.
     */
    private static Map<String, MTType> convertToMTNamedMap(Map<String, String> original, TypeGraph g) {
        Map<String, MTType> result = new HashMap<>();

        for (Map.Entry<String, String> entry : original.entrySet()) {
            result.put(entry.getKey(), new MTNamed(g, entry.getValue()));
        }

        return result;
    }
}