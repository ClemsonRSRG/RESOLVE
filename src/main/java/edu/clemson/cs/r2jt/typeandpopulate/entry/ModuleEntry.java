/*
 * ModuleEntry.java
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
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;

public abstract class ModuleEntry extends SymbolTableEntry {

    public ModuleEntry(String name, ResolveConceptualElement definingElement) {
        super(name, definingElement, ModuleIdentifier.GLOBAL);
    }
}
