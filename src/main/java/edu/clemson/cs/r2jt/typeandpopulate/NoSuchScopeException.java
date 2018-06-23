/*
 * NoSuchScopeException.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;

@SuppressWarnings("serial")
public class NoSuchScopeException extends RuntimeException {

    public final ResolveConceptualElement requestedScope;

    public NoSuchScopeException(ResolveConceptualElement e) {
        requestedScope = e;
    }
}
