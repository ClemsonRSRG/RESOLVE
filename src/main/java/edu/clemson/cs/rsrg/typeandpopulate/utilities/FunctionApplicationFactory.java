/*
 * FunctionApplicationFactory.java
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
package edu.clemson.cs.rsrg.typeandpopulate.utilities;

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.List;

/**
 * <p>
 * This is an interface for building a function application.
 * </p>
 *
 * @version 2.0
 */
public interface FunctionApplicationFactory {

    /**
     * <p>
     * This method returns a {@link MTType} resulting from a function
     * application.
     * </p>
     *
     * @param g The current type graph.
     * @param f The function to be applied.
     * @param calledAsName The name for this function application type.
     * @param arguments List of arguments for applying the function.
     *
     * @return A function application {@link MTType}.
     */
    MTType buildFunctionApplication(TypeGraph g, MTFunction f,
            String calledAsName, List<MTType> arguments);

}
