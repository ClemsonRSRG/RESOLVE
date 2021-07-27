/*
 * NoSuchModuleException.java
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
package edu.clemson.cs.r2jt.typeandpopulate;

@SuppressWarnings("serial")
public class NoSuchModuleException extends SymbolTableException {

    public final ModuleIdentifier sourceModule;
    public final ModuleIdentifier requestedModule;

    public NoSuchModuleException(ModuleIdentifier source,
            ModuleIdentifier requested) {

        sourceModule = source;
        requestedModule = requested;
    }
}
