/**
 * MTType.java
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
import java.util.Map;

/**
 * TODO:
 */
public abstract class MTType {

    public abstract void accept(TypeVisitor v);

    public abstract Map<String, MTType> bindTo(MTType o,
            Map<String, MTType> context);

    public abstract MTType getType();

    public abstract TypeGraph getTypeGraph();

    public abstract MTType getCopyWithVariablesSubstituted(
            Map<String, MTType> substitutions);

}