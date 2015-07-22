/**
 * CompileEnvironment.java
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
package edu.clemson.cs.r2jt.init2;

import edu.clemson.cs.r2jt.absynnew.ModuleAST;
import edu.clemson.cs.r2jt.errors.ErrorHandler2;
import edu.clemson.cs.r2jt.init2.file.ResolveFile;
import edu.clemson.cs.r2jt.init2.file.Utilities;
import edu.clemson.cs.r2jt.misc.FlagDependencyException;
import edu.clemson.cs.r2jt.misc.FlagManager;
import edu.clemson.cs.r2jt.rewriteprover.ProverListener;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.ScopeRepository;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.io.File;
import java.util.*;

/**
 * <p>This class stores all necessary objects and flags needed during
 * the compilation environment.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 1.0
 */
public class CompileEnvironment {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>This contains the absolute path to the RESOLVE workspace directory.</p>
     */
    private File myCompileDir = null;

    /**
     * <p>This object contains information about the current compilation for the
     * WebIDE/WebAPI.</p>
     */
    private CompileReport myCompileReport;

    /**
     * <p>This contains all modules we have currently seen. This includes both complete
     * and incomplete modules. A module is complete when we are done processing it. An
     * incomplete module usually means that we are still processing it's import.</p>
     */
    private final Map<ModuleIdentifier, AbstractMap.SimpleEntry<ModuleAST, ResolveFile>> myCompilingModules;

    /**
     * <p>This map stores all externally realizations for a particular concept.
     * The <code>Archiver</code> should be the only one that cares about these files.</p>
     */
    private final Map<ModuleIdentifier, File> myExternalRealizFiles;

    /**
     * <p>A flag to see if we want to show debugging information or not.</p>
     */
    private boolean myDebugOff = false;

    /**
     * <p>This is the error handler for the RESOLVE compiler.</p>
     */
    private final ErrorHandler2 myErrorHandler;

    /**
     * <p>This flag indicates whether or not we want to generate
     * performance VCs.</p>
     */
    private boolean myGenPVCs = false;

    /**
     * <p>This list stores all the incomplete modules.</p>
     */
    private final List<ModuleIdentifier> myIncompleteModules;

    /**
     * <p>This listener object provides instant feedback to the
     * interested party as soon as the prover is done processing a VC.</p>
     */
    private ProverListener myListener = null;

    /**
     * <p>This string stores the desired output file name provided
     * by the user.</p>
     */
    private String myOutputFileName = null;

    /**
     * <p>The symbol table for the compiler.</p>
     */
    private ScopeRepository mySymbolTable = null;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private TypeGraph myTypeGraph = null;

    /**
     * <p>This stores all user created files from the WebIDE/WebAPI.</p>
     */
    private Map<String, ResolveFile> myUserFileMap;

    // ===========================================================
    // Objects
    // ===========================================================

    /**
     * <p>This object contains all the flag objects that have been
     * created by the different modules.</p>
     */
    public final FlagManager flags;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Instantiates a compilation environment to store all
     * necessary modules, files and flags.</p>
     *
     * @param args The specified compiler arguments array.
     *
     * @throws FlagDependencyException
     */
    public CompileEnvironment(String[] args, String compilerVersion)
            throws FlagDependencyException {
        flags = new FlagManager(args);
        myCompileReport = new CompileReport();
        myCompilingModules =
                new HashMap<ModuleIdentifier, AbstractMap.SimpleEntry<ModuleAST, ResolveFile>>();
        myErrorHandler = new ErrorHandler2(this);
        myExternalRealizFiles = new HashMap<ModuleIdentifier, File>();
        myIncompleteModules = new LinkedList<ModuleIdentifier>();
        myUserFileMap = new HashMap<String, ResolveFile>();

        if (!flags.isFlagSet(ResolveCompiler.FLAG_NO_DEBUG)) {
            synchronized (System.out) {
                // Print Compiler Messages
                System.out.println("RESOLVE Compiler/Verifier - " + compilerVersion
                        + " Version.");
                System.out.println("\tUse -help flag for options.");
                System.out.println();
            }
        }

        if (flags.isFlagSet(ResolveCompiler.FLAG_WORKSPACE_DIR)) {
            myCompileDir =
                    Utilities.getWorkspaceDir(flags.getFlagArgument(
                            ResolveCompiler.FLAG_WORKSPACE_DIR, "Path"));
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Remove the module associated with the <code>ModuleIdentifier</code>
     * from our incomplete module stack. This indicates the completion of
     * this module.</p>
     *
     * @param mid Completed module's identifier.
     */
    public void completeRecord(ModuleIdentifier mid) {
        assert myCompilingModules.containsKey(mid) : "We haven't seen a module with this ID yet!";
        assert myIncompleteModules.contains(mid) : "We already completed compilation for a module with this ID!";
        myIncompleteModules.remove(mid);

        // Print out debugging message
        if (!myDebugOff) {
            myErrorHandler.message("Complete record: " + mid.toString()); //DEBUG
        }
    }

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
        myIncompleteModules.add(mid);

        // Print out debugging message
        if (!myDebugOff) {
            myErrorHandler.message("Construct record: " + mid.toString()); //DEBUG
        }
    }

    /**
     * <p>Returns true if the specified module is present in the compilation
     * environment, has an associated file and a valid module dec.</p>
     *
     * @param id The ID for the <code>ResolveFile</code> we want to search for.
     */
    public boolean containsID(ModuleIdentifier id) {
        return myCompilingModules.containsKey(id);
    }

    /**
     * <p>Returns the file associated with the specified id.</p>
     *
     * @param id The ID for the <code>ResolveFile</code> we want to search for.
     */
    public ResolveFile getFile(ModuleIdentifier id) {
        return myCompilingModules.get(id).getValue();
    }

    /**
     * <pReturns the <code>ModuleAST</code> associated with the specified id.></p>
     *
     * @param id The ID for the <code>ResolveFile</code> we want to search for.
     */
    public ModuleAST getModuleAST(ModuleIdentifier id) {
        return myCompilingModules.get(id).getKey();
    }

    /**
     * <p>Adds this file as an externally realized file.</p>
     *
     * @param id The ID for the <code>ResolveFile</code> that we want to set as externally realized.
     * @param file The externally realized file.
     */
    public void addExternalRealizFile(ModuleIdentifier id, File file) {
        myExternalRealizFiles.put(id, file);
    }

    /**
     * <p>This checks to see if the module associated with this id is an externally
     * realized file.</p>
     *
     * @param id The ID for the <code>File</code> we want to search for.
     *
     * @return True if it is externally realized. False otherwise.
     */
    public boolean isExternalRealizFile(ModuleIdentifier id) {
        return myExternalRealizFiles.containsKey(id);
    }

    /**
     * <p>Checks to see if we want debugging output or not.</p>
     *
     * @return Returns true iff we should suppress debug output.
     */
    public boolean debugOff() {
        return myDebugOff;
    }

    /**
     * <p>Returns the report object that contains all the compilation
     * results needed by the WebIDE/WebAPI.</p>
     *
     * @return A report object.
     */
    public CompileReport getCompileReport() {
        return myCompileReport;
    }

    /**
     * <p>Returns the compiler's error handler object.</p>
     *
     * @return Error handler object.
     */
    public ErrorHandler2 getErrorHandler() {
        return myErrorHandler;
    }

    /**
     * <p>Returns a pointer to the current
     * RESOLVE workspace directory.</p>
     *
     * @return A <code>File</code> object
     */
    public File getWorkspaceDir() {
        return myCompileDir;
    }

    /**
     * <p>Returns the output name that the user have specified.</p>
     *
     * @return A <code>String</code> name for the output file.
     */
    public String getOutputFilename() {
        return myOutputFileName;
    }

    /**
     * <p>Checks to see if the user have requested the VC Generator
     * to generate performance VCs.</p>
     *
     * @return True if the user wants performance VCs, false otherwise.
     */
    public boolean getPerformanceFlag() {
        return myGenPVCs;
    }

    /**
     * <p>Returns the remaining arguments not handled by the
     * compile environment.</p>
     *
     * @return All the remaining arguments that the caller needs to handle.
     */
    public String[] getRemainingArgs() {
        return flags.getRemainingArgs();
    }

    /**
     * <p>The symbol table containing all symbol information.</p>
     *
     * @return The symbol table for the compiler.
     */
    public ScopeRepository getSymbolTable() {
        return mySymbolTable;
    }

    /**
     * <p>The type graph containing all the type relationships.</p>
     *
     * @return The type graph for the compiler.
     */
    public TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    /**
     * <p>Returns <code>ResolveFile</code> for the specified string
     * object. Notice that the pre-condition for this method is that
     * the key exist in the map.</p>
     *
     * @param key Name of the file.
     *
     * @return The <code>ResolveFile</code> object for the specified key.
     */
    public ResolveFile getUserFileFromMap(String key) {
        return myUserFileMap.get(key);
    }

    /**
     * <p>This checks to see if the file is a user created file from the
     * WebIDE/WebAPI.</p>
     *
     * @param key Name of the file.
     *
     * @return True if it is a user created file from the WebIDE/WebAPI.
     * False otherwise.
     */
    public boolean isMetaFile(String key) {
        return myUserFileMap.containsKey(key);
    }

    /**
     * <p>Indicates that debug output should be turned off to the maximum amount
     * this is possible.</p>
     */
    public void setDebugOff() {
        myDebugOff = true;
    }

    /**
     * <p>Used to set a map of user files when invoking the compiler from
     * the WebIDE/WebAPI.</p>
     *
     * @param fMap The map of user created files.
     */
    public void setFileMap(Map<String, ResolveFile> fMap) {
        myUserFileMap = fMap;
    }

    /**
     * <p>Sets the name of the output file.</p>
     *
     * @param outputFile Name of the output file specified by the user.
     */
    public void setOutputFileName(String outputFile) {
        myOutputFileName = outputFile;
    }

    /**
     * <p>Sets the flag to generate performance VCs.</p>
     */
    public void setPerformanceFlag() {
        myGenPVCs = true;
    }

    /**
     * <p>Adds a listerner for the prover.</p>
     *
     * @param listener The listener object that is going to communicate
     *                 results from/to.
     */
    public void setProverListener(ProverListener listener) {
        myListener = listener;
    }

    /**
     * <p>Sets this table as our new symbol table.</p>
     *
     * @param table The newly created and blank symbol table.
     */
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

    /**
     * <p>Sets a new type graph to indicate relationship between types.</p>
     *
     * @param t The newly created type graph.
     */
    public void setTypeGraph(TypeGraph t) {
        myTypeGraph = t;
    }

}