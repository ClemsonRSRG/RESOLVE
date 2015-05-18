/**
 * Controller.java
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
package edu.clemson.cs.r2jt.init2;

import edu.clemson.cs.r2jt.init2.misc.CompileEnvironment;
import edu.clemson.cs.r2jt.init2.misc.CompileReport;
import edu.clemson.cs.r2jt.errors.ErrorHandler2;

/**
 * A manager for the target file of a compilation.
 */
public class Controller {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final CompileReport myCompileReport;
    private final ErrorHandler2 myErrorHandler;
    private final CompileEnvironment myInstanceEnvironment;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Controller(CompileEnvironment e) {
        myCompileReport = e.getCompileReport();
        myErrorHandler = e.getErrorHandler();
        myInstanceEnvironment = e;
    }

}