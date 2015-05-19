/**
 * ErrorHandler2.java
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
package edu.clemson.cs.r2jt.errors;

import edu.clemson.cs.r2jt.init2.ResolveCompiler;
import edu.clemson.cs.r2jt.init2.misc.CompileEnvironment;

/**
 * TODO: Description for this class
 */
public class ErrorHandler2 {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final CompileEnvironment myInstanceEnvironment;
    private final boolean myIsWebOutput;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ErrorHandler2(CompileEnvironment e) {
        myInstanceEnvironment = e;
        myIsWebOutput = e.flags.isFlagSet(ResolveCompiler.FLAG_WEB);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

}