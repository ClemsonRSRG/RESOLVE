/**
 * VariableReplacingVisitor.java
 * ---------------------------------
 * Copyright (c) 2016
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

/**
 * TODO: Fix this class
 */
public class VariableReplacingVisitor extends TypeVisitor {

    private final Map<String, MTType> mySubstitutions;

    public VariableReplacingVisitor(Map<String, String> substitutions,
            TypeGraph g) {
        mySubstitutions = convertToMTNamedMap(substitutions, g);
    }

    public VariableReplacingVisitor(Map<String, MTType> substitutions) {
        mySubstitutions = new HashMap<>(substitutions);
    }

    public MTType getFinalExpression() {
        return null;
    }

    private static Map<String, MTType> convertToMTNamedMap(
            Map<String, String> original, TypeGraph g) {

        Map<String, MTType> result = new HashMap<String, MTType>();

        for (Map.Entry<String, String> entry : original.entrySet()) {
            result.put(entry.getKey(), new MTNamed(g, entry.getValue()));
        }

        return result;
    }

}