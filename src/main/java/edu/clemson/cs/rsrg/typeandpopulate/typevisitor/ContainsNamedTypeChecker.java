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

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTNamed;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>This class visits the named types to see if any of the
 * given names is present.</p>
 *
 * @version 2.0
 */
public class ContainsNamedTypeChecker extends BoundVariableVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>A set of names.</p> */
    private final Set<String> myNames = new HashSet<>();

    /** <p>This is used to store the final result of the visit.</p> */
    private boolean myResult = false;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Result in <code>true</code> if one of the given names appears in the
     * checked type. The set will not be changed, but it will be read from
     * so it must not change while checking runs.</p>
     *
     * @param names A set of names.
     */
    public ContainsNamedTypeChecker(Set<String> names) {
        myNames.addAll(names);
    }

    /**
     * <p>Results in <code>true</code> if the given name appears in the checked
     * type.</p>
     *
     * @param name A name.
     */
    public ContainsNamedTypeChecker(String name) {
        myNames.add(name);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTNamed} by checking if it appears in our set
     * of <code>names</code>.</p>
     *
     * @param named A math type.
     */
    @Override
    public final void endMTNamed(MTNamed named) {
        try {
            getInnermostBinding(named.getName());
        }
        catch (NoSuchElementException nsee) {
            myResult = myResult || myNames.contains(named.getName());
        }
    }

    /**
     * <p>This method returns the final result after we are
     * done visiting all the type nodes.</p>
     *
     * @return {@code true} if the the given name appears in the checked
     * type, {@code false} otherwise.
     */
    public final boolean getResult() {
        return myResult;
    }

}