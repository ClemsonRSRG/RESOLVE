/**
 * Import.java
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
package edu.clemson.cs.r2jt.init;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.ModuleID;

/**
 * <p>An <code>Import</code> defines a module that needs to be imported because
 * it is referenced from another module that we are trying to compile.</p>
 * 
 * <p><code>Import</code>s can be <em>related</em> or <em>unrelated</em>.
 * <ul>
 * <li>Related imports are imports based on a common concept module with the file
 * we're trying to compile.  For instance, if we include Enhancement.en because
 * the file we're compiling realizes it, this is a related file--they both 
 * center around the same base concept that Enhancement.en enhances.</li>
 * <li>Unrelated imports are based on modules that may not derive from the same
 * concept.  These mostly appear in "uses" clauses.</li>
 * </ul>
 * </p>
 */
public class Import {

    // ==========================================================
    // Variables 
    // ==========================================================

    private Location loc;

    private ModuleID id;

    // ==========================================================
    // Constructors
    // ==========================================================

    public Import(Location loc, ModuleID id) {
        this.loc = loc;
        this.id = id;
    }

    // ==========================================================
    // Accessor Methods
    // ==========================================================

    public Location getLocation() {
        return loc;
    }

    public ModuleID getModuleID() {
        return id;
    }
}
