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
package edu.clemson.cs.r2jt.init2;

import edu.clemson.cs.r2jt.absynnew.ModuleAST;
import edu.clemson.cs.r2jt.errors.ErrorHandler2;
import edu.clemson.cs.r2jt.init2.file.ResolveFile;
import edu.clemson.cs.r2jt.misc.FlagDependencyException;
import edu.clemson.cs.r2jt.misc.FlagManager;
import edu.clemson.cs.r2jt.rewriteprover.ProverListener;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.io.File;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * TODO: Description for this class
 */
public class CompileEnvironment {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private File myCompileDir = null;
    private CompileReport myCompileReport;
    private final Map<ModuleIdentifier, AbstractMap.SimpleEntry<ModuleAST, ResolveFile>> myCompilingModules;
    private final Map<ModuleIdentifier, File> myExternalRealizFiles;
    private boolean myDebugOff = false;
    private final ErrorHandler2 myErrorHandler;
    private boolean myGenPVCs = false;
    private final Stack<ModuleIdentifier> myIncompleteModules;
    private ProverListener myListener = null;
    private String myOutputFileName = null;
    private ScopeRepository mySymbolTable = null;
    private ResolveFile myTargetFile = null;
    private TypeGraph myTypeGraph = null;
    private Map<String, ResolveFile> myUserFileMap;

    // ===========================================================
    // Objects
    // ===========================================================

    public final FlagManager flags;

    // ===========================================================
    // Constructors
    // ===========================================================

    public CompileEnvironment(String[] args) throws FlagDependencyException {
        flags = new FlagManager(args);
        myCompilingModules =
                new HashMap<ModuleIdentifier, AbstractMap.SimpleEntry<ModuleAST, ResolveFile>>();
        myErrorHandler = new ErrorHandler2(this);
        myExternalRealizFiles = new HashMap<ModuleIdentifier, File>();
        myIncompleteModules = new Stack<ModuleIdentifier>();
        myUserFileMap = new HashMap<String, ResolveFile>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Constructs a record containing the module id, the file, and the module
     * dec, and places it in the module environment. Also places the module into
     * a stack that indicates compilation has begun on this module but has not
     * completed.</p>
     *
     * @param file The original source file.
     * @param moduleAST The ANTLR4 module AST.
     */
    public void constructRecord(ResolveFile file, ModuleAST moduleAST) {
        ModuleIdentifier mid = new ModuleIdentifier(moduleAST);
        assert !myCompilingModules.containsKey(mid) : "We already compiled a module with this ID!";
        myCompilingModules.put(mid,
                new AbstractMap.SimpleEntry<ModuleAST, ResolveFile>(moduleAST,
                        file));
        myIncompleteModules.push(mid);

        // Print out debugging message
        if (!myDebugOff) {
            myErrorHandler.message("Construct record: " + mid.toString()); //DEBUG
        }
    }

    /**
     * Returns true if the specified module is present in the compilation
     * environment, has an associated file and a valid module dec.
     */
    public boolean containsID(ModuleIdentifier id) {
        return myCompilingModules.containsKey(id);
    }

    /**
     * Returns the file associated with the specified id.
     */
    public ResolveFile getFile(ModuleIdentifier id) {
        return myCompilingModules.get(id).getValue();
    }

    /**
     * Returns the <code>ModuleAST</code> associated with the specified id.
     */
    public ModuleAST getModuleAST(ModuleIdentifier id) {
        return myCompilingModules.get(id).getKey();
    }

    public void addExternalRealizFile(ModuleIdentifier id, File file) {
        myExternalRealizFiles.put(id, file);
    }

    public boolean isExternalRealizFile(ModuleIdentifier id) {
        return myExternalRealizFiles.containsKey(id);
    }

    /**
     * Returns true iff we should suppress debug output.
     */
    public boolean debugOff() {
        return myDebugOff;
    }

    public CompileReport getCompileReport() {
        return myCompileReport;
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

    public ResolveFile getUserFileFromMap(String key) {
        return myUserFileMap.get(key);
    }

    public boolean isMetaFile(String key) {
        return myUserFileMap.containsKey(key);
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

    /**
     * Used to set a map of user files when used with the web interface
     */
    public void setFileMap(Map<String, ResolveFile> fMap) {
        myUserFileMap = fMap;
    }

    /** Name the output file. */
    public void setOutputFileName(String outputFile) {
        myOutputFileName = outputFile;
    }

    public void setPerformanceFlag() {
        myGenPVCs = true;
    }

    public void setProverListener(ProverListener listener) {
        myListener = listener;
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

    /** Sets the workspace directory to the specified directory. */
    public void setWorkspaceDir(File dir) {
        myCompileDir = dir;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

}