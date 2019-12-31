/*
 * ModuleEntry.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.entry;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;

/**
 * <p>
 * This abstract class serves as the parent class of all module entries in the
 * symbol table.
 * </p>
 *
 * @version 2.0
 */
public abstract class ModuleEntry extends SymbolTableEntry {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * An helper constructor that allow us to store the name, and the module
     * that defined this entry
     * for any objects created from a class that inherits from
     * {@code ModuleEntry}.
     * </p>
     *
     * @param name Name associated with this entry.
     * @param definingElement The {@link ModuleDec} that created this entry.
     */
    protected ModuleEntry(String name, ModuleDec definingElement) {
        super(name, definingElement, ModuleIdentifier.GLOBAL);
    }

}
