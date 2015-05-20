/**
 * CompileEnvironment.java
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
package edu.clemson.cs.r2jt.init2.misc;

import edu.clemson.cs.r2jt.errors.ErrorHandler2;
import edu.clemson.cs.r2jt.init2.file.ResolveFile;
import edu.clemson.cs.r2jt.misc.FlagDependencyException;
import edu.clemson.cs.r2jt.misc.FlagManager;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

import java.io.File;

/**
 * TODO: Description for this class
 */
public class CompileEnvironment {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private File myCompileDir = null;
    private CompileReport myCompileReport;
    private boolean myDebugOff = false;
    private final ErrorHandler2 myErrorHandler;
    private boolean myGenPVCs = false;
    private String myOutputFileName = null;
    private ScopeRepository mySymbolTable = null;
    private ResolveFile myTargetFile = null;
    private TypeGraph myTypeGraph = null;

    // ===========================================================
    // Objects
    // ===========================================================

    public final FlagManager flags;

    // ===========================================================
    // Constructors
    // ===========================================================

    public CompileEnvironment(String[] args) throws FlagDependencyException {
        flags = new FlagManager(args);
        myErrorHandler = new ErrorHandler2(this);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public CompileReport getCompileReport() {
        return myCompileReport;
    }

    /**
     * Returns true iff we should suppress debug output.
     */
    public boolean debugOff() {
        return myDebugOff;
    }

    public ErrorHandler2 getErrorHandler() {
        return myErrorHandler;
    }

    public File getWorkspaceDir() {
        return myCompileDir;
    }

    public String getOutputFilename() {
        return myOutputFileName;
    }

    public boolean getPerformanceFlag() {
        return myGenPVCs;
    }

    public String[] getRemainingArgs() {
        return flags.getRemainingArgs();
    }

    public ScopeRepository getSymbolTable() {
        return mySymbolTable;
    }

    public TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    public void setCompileReport(CompileReport cr) {
        myCompileReport = cr;
    }

    /**
     * Indicates that debug output should be turned off to the maximum amount
     * this is possible.
     */
    public void setDebugOff() {
        myDebugOff = true;
    }

    /** Sets the workspace directory to the specified directory. */
    public void setWorkspaceDir(File dir) {
        myCompileDir = dir;
    }

    /** Name the output file. */
    public void setOutputFileName(String outputFile) {
        myOutputFileName = outputFile;
    }

    public void setPerformanceFlag() {
        myGenPVCs = true;
    }

    public void setSymbolTable(ScopeRepository table) {
        if (table == null) {
            throw new IllegalArgumentException(
                    "Symbol table may not be set to null!");
        }

        if (mySymbolTable != null) {
            throw new IllegalStateException(
                    "Symbol table may only be set once!");
        }

        mySymbolTable = table;
    }

    public void setTargetFile(ResolveFile f) {
        myTargetFile = f;
    }

    public void setTypeGraph(TypeGraph t) {
        myTypeGraph = t;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

}