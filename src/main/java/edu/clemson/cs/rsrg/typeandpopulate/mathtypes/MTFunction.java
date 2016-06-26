/**
 * MTFunction.java
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
package edu.clemson.cs.rsrg.typeandpopulate.mathtypes;

import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.TypeVisitor;
import java.util.List;
import java.util.Map;

/**
 * TODO:
 */
public class MTFunction extends MTType {

    public MTFunction(TypeGraph g, MTType range, List<MTType> paramTypes) {

    }

    @Override
    public void accept(TypeVisitor v) {

    }

    @Override
    public Map<String, MTType> bindTo(MTType o, Map<String, MTType> context) {
        return null;
    }

    @Override
    public MTType getType() {
        return null;
    }

    @Override
    public TypeGraph getTypeGraph() {
        return null;
    }

    @Override
    public MTType getCopyWithVariablesSubstituted(
            Map<String, MTType> substitutions) {
        return null;
    }
}