/**
 * TypeGraph.java
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
package edu.clemson.cs.rsrg.typeandpopulate.typereasoning;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTProper;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import java.util.Map;

/**
 * <p>Represents a directed graph of types, where edges between types
 * indicate a possible coercion that the type checker can perform.</p>
 *
 * @version 2.0
 */
public class TypeGraph {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>A set of non-thread-safe resources to be used during general type
     * reasoning. This really doesn't belong here, but anything that's reasoning
     * about types should already have access to a type graph, and only one type
     * graph is created per thread, so this is a convenient place to put it.</p>
     */
    public final PerThreadReasoningResources threadResources =
            new PerThreadReasoningResources();

    // ===========================================================
    // Global Mathematical Types
    // ===========================================================

    public final MTType ENTITY = new MTProper(this, "Entity");
    public final MTProper CLS = new MTProper(this, null, true, "MType");
    public final MTProper BOOLEAN = new MTProper(this, CLS, false, "B");
    public final MTProper EMPTY_SET =
            new MTProper(this, CLS, false, "Empty_Set");
    public final MTProper VOID = new MTProper(this, CLS, false, "Void");
    public final MTType ELEMENT = new MTProper(this, "Element");

    public MTFunction POWERTYPE;
    public MTFunction POWERCLASS;
    public MTFunction UNION;
    public MTFunction INTERSECT;
    public MTFunction FUNCTION;
    public MTFunction CROSS;
    public MTFunction AND;
    public MTFunction NOT;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void addRelationship(Exp bindingExpression, MTType destination,
            Exp bindingCondition, Scope environment) {}

    public boolean isSubtype(MTType subtype, MTType supertype) {
        return false;
    }

    public boolean isKnownToBeIn(Exp value, MTType expected) {
        return false;
    }

    public boolean isKnownToBeIn(MTType value, MTType expected) {
        return false;
    }

    public static MTType getCopyWithVariablesSubstituted(MTType original,
            Map<String, MTType> substitutions) {
        return null;
    }

    public static <T extends Exp> T getCopyWithVariablesSubstituted(T original,
            Map<String, MTType> substitutions) {
        return null;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Helper Constructs
    // ===========================================================

}