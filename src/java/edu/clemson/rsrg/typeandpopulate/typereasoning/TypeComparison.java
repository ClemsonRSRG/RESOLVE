/*
 * TypeComparison.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.typereasoning;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;

/**
 * <p>
 * This is an interface for all types of type comparisons that we might be comparing during the type and reasoning phase
 * of the compiler.
 * </p>
 *
 * @version 2.0
 */
public interface TypeComparison<V extends Exp, T extends MTType> {

    /**
     * <p>
     * Takes an instance of {@link Exp} and use the {@link MTType} found in the expression and compare it with the
     * expected {@link MTType}.
     * </p>
     *
     * @param foundValue
     *            The expression to be compared.
     * @param foundType
     *            The type for the expression.
     * @param expectedType
     *            The expected type for the expression.
     *
     * @return {@code true} if {@code foundType = expectedType}, {@code false} otherwise.
     */
    boolean compare(V foundValue, T foundType, T expectedType);

    /**
     * <p>
     * This method returns a string description for each type comparison.
     * </p>
     *
     * @return A string.
     */
    String description();

}
