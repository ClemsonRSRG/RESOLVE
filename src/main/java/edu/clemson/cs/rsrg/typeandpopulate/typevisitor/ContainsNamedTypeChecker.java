/**
 * ContainsNamedTypeChecker.java
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
package edu.clemson.cs.rsrg.typeandpopulate.typevisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Fix this class
 */
public class ContainsNamedTypeChecker extends TypeVisitor {

    private final Set<String> myNames = new HashSet<String>();

    private boolean myResult = false;

    /**
     * <p>Result in <code>true</code> if one of the given names appears in the
     * checked type.  The set will not be changed, but it will be read from
     * so it must not change while checking runs.</p>
     * @param names
     */
    public ContainsNamedTypeChecker(Set<String> names) {
        myNames.addAll(names);
    }

    /**
     * <p>Results in <code>true</cdoe> if the given name appears in the checked
     * type.</p>
     * @param name
     */
    public ContainsNamedTypeChecker(String name) {
        myNames.add(name);
    }

    public boolean getResult() {
        return myResult;
    }

}