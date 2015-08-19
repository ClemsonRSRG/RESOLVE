/**
 * ModuleDec.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn;

import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

import java.util.List;

/**
 * <p>This is the abstract base class for all the module declaration type
 * intermediate objects that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public abstract class ModuleDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The current module's import objects.</p> */
    protected final List<UsesItem> myUsesItems;

    /** <p>The current module's declaration objects.</p> */
    protected final List<Dec> myDecs;

    // ===========================================================
    // Constructor
    // ===========================================================

    protected ModuleDec(Location l, PosSymbol name, List<UsesItem> usesItems,
            List<Dec> decs) {
        super(l, name);
        myUsesItems = usesItems;
        myDecs = decs;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method gets all the object declarations associated
     * with this module.</p>
     *
     * @return A list of {link Dec} objects.
     */
    public List<Dec> getDecList() {
        return myDecs;
    }

    /**
     * <p>This method gets all the import objects associated
     * with this module.</p>
     *
     * @return A list of {link UsesItem} objects.
     */
    public List<UsesItem> getUsesItems() {
        return myUsesItems;
    }

}