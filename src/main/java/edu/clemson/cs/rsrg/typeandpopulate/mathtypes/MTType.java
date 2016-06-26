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
 * <p>This abstract class serves as the parent class of all
 * mathematical types that can .</p>
 *
 * @version 2.0
 */
public abstract class MTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The current type graph object in use.</p> */
    protected final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the type graph
     * of any objects created from a class that inherits from
     * {@code MTType}.</p>
     *
     * @param g The current type graph.
     */
    protected MTType(TypeGraph g) {
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public abstract void accept(TypeVisitor v);

    public abstract Map<String, MTType> bindTo(MTType o,
            Map<String, MTType> context);

    public abstract MTType getType();

    public abstract TypeGraph getTypeGraph();

    public abstract MTType getCopyWithVariablesSubstituted(
            Map<String, MTType> substitutions);

}