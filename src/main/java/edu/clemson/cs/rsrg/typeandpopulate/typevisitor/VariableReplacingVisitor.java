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

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.Map;

/**
 * TODO:
 */
public class VariableReplacingVisitor extends TypeVisitor {

    public VariableReplacingVisitor(Map<String, String> substitutions,
            TypeGraph g) {}

    public MTType getFinalExpression() {
        return null;
    }

}