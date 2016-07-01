/**
 * BindingVisitor.java
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
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Fix this class
 */
public class BindingVisitor extends TypeVisitor {

    private boolean myMatchSoFarFlag = true;

    private Map<String, MTType> myBindings = new HashMap<>();

    private final TypeGraph myTypeGraph;

    public BindingVisitor(TypeGraph g, Map<String, MTType> concreteContext) {
        //super(concreteContext);
        myTypeGraph = g;
    }

    // TODO: This doesn't belong here
    public final boolean visit(MTType t1, MTType t2) {
        return false;
    }

    public boolean binds() {
        return myMatchSoFarFlag;
    }

    public Map<String, MTType> getBindings() {
        return myBindings;
    }
}