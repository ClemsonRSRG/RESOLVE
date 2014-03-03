/**
 * BindingCondition.java
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
package edu.clemson.cs.r2jt.typereasoning;

import edu.clemson.cs.r2jt.absyn.*;

public class BindingCondition {

    private Exp myConditionExp;

    public BindingCondition(Exp condition) {
        myConditionExp = condition;
    }

    @Override
    public String toString() {
        return myConditionExp.toString();
    }
}
