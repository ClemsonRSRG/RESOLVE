/**
 * TypeComparison.java
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
package edu.clemson.cs.r2jt.typereasoning2;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;

public interface TypeComparison<V extends ExprAST, T extends MTType> {

    public boolean compare(V foundValue, T foundType, T expectedType);

    public String description();
}
