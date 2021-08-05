/*
 * TypeRelationshipPredicate.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.typereasoning.relationships;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.Map;

/**
 * <p>
 * This is an interface for all type relationship predicates that we might be able to establish using our type reasoning
 * system.
 * </p>
 *
 * @version 2.0
 */
public interface TypeRelationshipPredicate {

    /**
     * <p>
     * Given two types, we attempt to statically demonstrate if their type relationship can be established statically.
     * </p>
     *
     * @param canonical1
     *            The first {@link MTType} object.
     * @param canonical2
     *            The second {@link MTType} object.
     * @param typeBindings
     *            Map of established type bindings.
     * @param expressionBindings
     *            Map of established expression bindings.
     *
     * @return {@code true} if it can be demonstrated statically, {@code false} otherwise.
     */
    boolean canBeDemonstratedStatically(MTType canonical1, MTType canonical2, Map<String, MTType> typeBindings,
            Map<String, Exp> expressionBindings);

    /**
     * <p>
     * Given a map of substitutions, we attempt to replace any unbound variables in types.
     * </p>
     *
     * @param substitutions
     *            Map of substitutions for unbound variables.
     *
     * @return A {@code TypeRelationshipPredicate} after the substitution.
     */
    TypeRelationshipPredicate replaceUnboundVariablesInTypes(Map<String, String> substitutions);

}
