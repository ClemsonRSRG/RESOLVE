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
 * TODO: Fix this class
 */
public class MTFunction extends MTType {

    // ===========================================================
    // Constructors
    // ===========================================================

    public MTFunction(TypeGraph g, MTType range, List<MTType> paramTypes) {
        super(g);
    }

    @Override
    public void accept(TypeVisitor v) {

    }

    @Override
    public void acceptOpen(TypeVisitor v) {

    }

    @Override
    public void acceptClose(TypeVisitor v) {

    }

    @Override
    public Map<String, MTType> bindTo(MTType o, Map<String, MTType> context) {
        return null;
    }

    @Override
    public List<MTType> getComponentTypes() {
        return null;
    }

    @Override
    public MTType getType() {
        return null;
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        return null;
    }

    /**
     * <p>This is just a template method to <em>force</em> all concrete
     * subclasses of <code>MTType</code> to implement <code>hashCode()</code>,
     * as the type resolution algorithm depends on it being implemented
     * sensibly.</p>
     *
     * @return A hashcode consistent with <code>equals()</code> and thus
     * alpha-equivalency.
     */
    @Override
    protected int getHashCode() {
        return 0;
    }

}