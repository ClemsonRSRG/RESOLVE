/**
 * BindingCollection.java
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

import java.util.HashMap;

public class BindingCollection {

    private HashMap<String, String> myBindings = new HashMap<String, String>();
    private int varCounter = 0;

    public void addBinding(String var1, String var2) {
        myBindings.put(var1, var2);
    }

    public String getBinding(String var) {
        String boundVar = myBindings.get(var);
        if (boundVar == null) {
            boundVar = var;
        }
        return boundVar;
    }
}
