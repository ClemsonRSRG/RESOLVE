/**
 * CompileEnvironment2.java
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
package edu.clemson.cs.r2jt.init2.model;

import edu.clemson.cs.r2jt.errors.ErrorHandler2;
import edu.clemson.cs.r2jt.misc.FlagDependencyException;
import edu.clemson.cs.r2jt.misc.FlagManager;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

import java.io.File;

/**
 * TODO: Description for this class
 */
public class CompileEnvironment2 {

    // ===========================================================
    // Member Fields
    // ===========================================================

    public final FlagManager flags;

    private File myCompileMainDir = null;
    private CompileReport2 myCompileReport;
    private boolean myDebugOff = false;
    private final ErrorHandler2 myErrorHandler;
    private boolean myGenPVCs = false;
    private String myOutputFileName = null;
    private ScopeRepository mySymbolTable = null;
    private TypeGraph myTypeGraph = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public CompileEnvironment2(String[] args) throws FlagDependencyException {
        flags = new FlagManager(args);
        myErrorHandler = new ErrorHandler2(this);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public CompileReport2 getCompileReport() {
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

    public File getMainDir() {
        return myCompileMainDir;
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

    public void setCompileReport(CompileReport2 cr) {
        myCompileReport = cr;
    }

    /**
     * Indicates that debug output should be turned off to the maximum amount
     * this is possible.
     */
    public void setDebugOff() {
        myDebugOff = true;
    }

    /** Sets the main directory to the specified directory. */
    public void setMainDir(File mainDir) {
        myCompileMainDir = mainDir;
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

    public void setTypeGraph(TypeGraph t) {
        myTypeGraph = t;
    }

}