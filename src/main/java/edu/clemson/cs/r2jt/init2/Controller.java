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

import edu.clemson.cs.r2jt.init2.file.ResolveFile;
import edu.clemson.cs.r2jt.init2.misc.CompileEnvironment;
import edu.clemson.cs.r2jt.init2.misc.CompileReport;
import edu.clemson.cs.r2jt.errors.ErrorHandler2;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;

/**
 * A manager for the target file of a compilation.
 */
public class Controller {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final CompileEnvironment myCompileEnvironment;
    private final CompileReport myCompileReport;
    private final ErrorHandler2 myErrorHandler;
    private final MathSymbolTableBuilder mySymbolTable;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Controller(CompileEnvironment e) {
        myCompileEnvironment = e;
        myCompileReport = e.getCompileReport();
        myErrorHandler = e.getErrorHandler();
        mySymbolTable = (MathSymbolTableBuilder) e.getSymbolTable();
    }

    /**
     * Compiles a target file. A target file is one that is specified on the
     * command line of the compiler as opposed to one that is being compiled
     * because it was imported by another file.
     */
    public void compileTargetFile(ResolveFile file) {
        // Set this as our target file in the compile environment
        myCompileEnvironment.setTargetFile(file);
    }

}