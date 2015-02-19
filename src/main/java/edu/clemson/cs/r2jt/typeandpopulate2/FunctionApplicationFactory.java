/**
 * FunctionApplicationFactory.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

import java.util.List;

public interface FunctionApplicationFactory {

    public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
            String calledAsName, List<MTType> arguments);
}
