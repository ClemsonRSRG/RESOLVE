/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
