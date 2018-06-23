/*
 * CompileEnvironment.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.init;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.init.file.ResolveFileBasicInfo;
import edu.clemson.cs.rsrg.init.output.FileOutputListener;
import edu.clemson.cs.rsrg.init.output.OutputListener;
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.statushandling.WriterStatusHandler;
import edu.clemson.cs.rsrg.statushandling.exception.FlagDependencyException;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.init.flag.FlagManager;
import edu.clemson.cs.rsrg.misc.Utilities;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeRepository;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

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
    private File myCompileDir;

    /**
     * <p>This contains all modules we have currently seen. This includes both complete
     * and incomplete modules. A module is complete when we are done processing it. An
     * incomplete module usually means that we are still processing it's import.</p>
     */
    private final Map<ModuleIdentifier, AbstractMap.SimpleEntry<ModuleDec, ResolveFile>> myCompilingModules;

    /**
     * <p>This map stores all externally realizations for a particular concept.
     * The {@code Archiver} should be the only one that cares about these files.</p>
     */
    private final Map<ModuleIdentifier, File> myExternalRealizFiles;

    /**
     * <p>This is the default status handler for the RESOLVE compiler.</p>
     */
    private final StatusHandler myStatusHandler;

    /**
     * <p>This list stores all the incomplete modules.</p>
     */
    private final List<ModuleIdentifier> myIncompleteModules;

    /**
     * <p>This list stores listener objects that provides instant feedback to the
     * interested party when we are done with a compilation activity.</p>
     */
    private final List<OutputListener> myOutputListeners;

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
    private Map<ResolveFileBasicInfo, ResolveFile> myUserFileMap;

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
     * @param compilerVersion The current compiler version.
     * @param statusHandler A status handler to display debug or error messages.
     *
     * @throws FlagDependencyException There was some kind of dependency error
     * with the user specified flags.
     * @throws IOException There was an error creating the specified error log file.
     */
    public CompileEnvironment(String[] args, String compilerVersion,
            StatusHandler statusHandler)
            throws FlagDependencyException,
                IOException {
        flags = new FlagManager(args);
        myCompilingModules =
                new LinkedHashMap<>();
        myExternalRealizFiles = new LinkedHashMap<>();
        myIncompleteModules = new LinkedList<>();
        myOutputListeners = new LinkedList<>();
        myUserFileMap = new LinkedHashMap<>();

        // Check for custom workspace path
        String path = null;
        if (flags.isFlagSet(ResolveCompiler.FLAG_WORKSPACE_DIR)) {
            path =
                    flags.getFlagArgument(ResolveCompiler.FLAG_WORKSPACE_DIR,
                            "Path");
        }
        myCompileDir = Utilities.getWorkspaceDir(path);

        // Check for file error output flag
        if (flags.isFlagSet(ResolveCompiler.FLAG_DEBUG_FILE_OUT)) {
            Date date = new Date();
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            Path infoFilePath = Paths.get(myCompileDir.getAbsolutePath(),
                    "Log-" + dateFormat.format(date) + ".log");
            Path errorFilePath = Paths.get(myCompileDir.getAbsolutePath(),
                    "Debug-Log-" + dateFormat.format(date) + ".log");
            Charset charset = Charset.forName("UTF-8");

            statusHandler =
                    new WriterStatusHandler(Files.newBufferedWriter(infoFilePath, charset, CREATE, APPEND),
                            Files.newBufferedWriter(errorFilePath, charset, CREATE, APPEND));
        }
        myStatusHandler = statusHandler;

        // Add a default file listener if we didn't specify no file output
        if (!flags.isFlagSet(ResolveCompiler.FLAG_NO_FILE_OUTPUT)) {
            myOutputListeners.add(new FileOutputListener(myStatusHandler));
        }

        // Debugging information
        if (flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
            synchronized (System.out) {
                // Print Compiler Messages
                myStatusHandler.info(null, "RESOLVE Compiler/Verifier - "
                        + compilerVersion + " Version.");
                myStatusHandler.info(null, "\tUse -help flag for options.\n");
            }
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Adds this file as an externally realized file.</p>
     *
     * @param id The ID for the {@link ResolveFile} that we want to set as externally realized.
     * @param file The externally realized file.
     */
    public final void addExternalRealizFile(ModuleIdentifier id, File file) {
        myExternalRealizFiles.put(id, file);
    }

    /**
     * <p>Adds a new listener object.</p>
     *
     * @param listener A new {@link OutputListener} object.
     */
    public final void addOutputListener(OutputListener listener) {
        myOutputListeners.add(listener);
    }

    /**
     * <p>Remove the module associated with the {@link ModuleIdentifier}
     * from our incomplete module stack. This indicates the completion of
     * this module.</p>
     *
     * @param mid Completed module's identifier.
     */
    public final void completeRecord(ModuleIdentifier mid) {
        assert myCompilingModules.containsKey(mid) : "We haven't seen a module with this ID yet!";
        assert myIncompleteModules.contains(mid) : "We already completed compilation for a module with this ID!";
        myIncompleteModules.remove(mid);
    }

    /**
     * <p>Constructs a record containing the module id, the file, and the module
     * dec, and places it in the module environment. Also places the module into
     * a stack that indicates compilation has begun on this module but has not
     * completed.</p>
     *
     * @param file The original source file.
     * @param moduleDec The module representation declaration.
     */
    public final void constructRecord(ResolveFile file, ModuleDec moduleDec) {
        ModuleIdentifier mid = new ModuleIdentifier(moduleDec);
        assert !myCompilingModules.containsKey(mid) : "We already compiled a module with this ID!";
        myCompilingModules.put(mid,
                new AbstractMap.SimpleEntry<>(moduleDec,
                        file));
        myIncompleteModules.add(mid);
    }

    /**
     * <p>Returns true if the specified module is present in the compilation
     * environment, has an associated file and a valid module dec.</p>
     *
     * @param id The ID for the {@link ResolveFile} we want to search for.
     *
     * @return {@code true} if we have compiled this {@link ModuleIdentifier},
     * {@code false} otherwise.
     */
    public final boolean containsID(ModuleIdentifier id) {
        return myCompilingModules.containsKey(id);
    }

    /**
     * <p>Returns the file associated with the specified id.</p>
     *
     * @param id The ID for the {@link ResolveFile} we want to search for.
     *
     * @return The {@link ResolveFile} associated with the {@code id}.
     */
    public final ResolveFile getFile(ModuleIdentifier id) {
        return myCompilingModules.get(id).getValue();
    }

    /**
     * <p>Returns the {@link ModuleDec} associated with the specified id.</p>
     *
     * @param id The ID for the {@link ResolveFile} we want to search for.
     *
     * @return The {@link ModuleDec} associated with the {@code id}.
     */
    public final ModuleDec getModuleAST(ModuleIdentifier id) {
        return myCompilingModules.get(id).getKey();
    }

    /**
     * <p>Returns the remaining arguments not handled by the
     * compile environment.</p>
     *
     * @return All the remaining arguments that the caller needs to handle.
     */
    public final String[] getRemainingArgs() {
        return flags.getRemainingArgs();
    }

    /**
     * <p>Returns all the output listeners that are interested
     * in the compilation results.</p>
     *
     * @return A list of {@link OutputListener OutputListeners}.
     */
    public final List<OutputListener> getOutputListeners() {
        return myOutputListeners;
    }

    /**
     * <p>Returns the compiler's status handler object.</p>
     *
     * @return A {@link StatusHandler} object.
     */
    public final StatusHandler getStatusHandler() {
        return myStatusHandler;
    }

    /**
     * <p>The symbol table containing all symbol information.</p>
     *
     * @return The symbol table for the compiler.
     */
    public final ScopeRepository getSymbolTable() {
        return mySymbolTable;
    }

    /**
     * <p>The type graph containing all the type relationships.</p>
     *
     * @return The type graph for the compiler.
     */
    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    /**
     * <p>Returns {@link ResolveFile} for the specified string
     * object. Notice that the pre-condition for this method is that
     * the key exist in the map.</p>
     *
     * @param fileBasicInfo The name of the file including any known parent directory.
     *
     * @return The {@link ResolveFile} object for the specified key.
     */
    public final ResolveFile getUserFileFromMap(
            ResolveFileBasicInfo fileBasicInfo) {
        return myUserFileMap.get(fileBasicInfo);
    }

    /**
     * <p>Returns a pointer to the current
     * RESOLVE workspace directory.</p>
     *
     * @return A {@link File} object
     */
    public final File getWorkspaceDir() {
        return myCompileDir;
    }

    /**
     * <p>This checks to see if the module associated with this id is an externally
     * realized file.</p>
     *
     * @param id The ID for the {@link ResolveFile} we want to search for.
     *
     * @return {@code true} if it is externally realized, {@code false} otherwise.
     */
    public final boolean isExternalRealizFile(ModuleIdentifier id) {
        return myExternalRealizFiles.containsKey(id);
    }

    /**
     * <p>This checks to see if the module associated with this id has been
     * compiled or not.</p>
     *
     * @param id The ID for the {@link ResolveFile} we want to search for.
     *
     * @return {@code true} if is incomplete, {@code false} otherwise.
     */
    public final boolean isCompleteModule(ModuleIdentifier id) {
        return containsID(id) && !myIncompleteModules.contains(id);
    }

    /**
     * <p>This checks to see if the file is a user created file from the
     * WebIDE/WebAPI.</p>
     *
     * @param fileBasicInfo The name of the file including any known parent directory.
     *
     * @return {@code true} if it is a user created file from the WebIDE/WebAPI,
     * {@code false} otherwise.
     */
    public final boolean isMetaFile(ResolveFileBasicInfo fileBasicInfo) {
        return myUserFileMap.containsKey(fileBasicInfo);
    }

    /**
     * <p>Used to set a map of user files when invoking the compiler from
     * the WebIDE/WebAPI.</p>
     *
     * @param fMap The map of user created files.
     */
    public final void setFileMap(Map<ResolveFileBasicInfo, ResolveFile> fMap) {
        myUserFileMap = fMap;
    }

    /**
     * <p>Sets this table as our new symbol table.</p>
     *
     * @param table The newly created and blank symbol table.
     */
    public final void setSymbolTable(ScopeRepository table) {
        if (table == null) {
            throw new MiscErrorException(
                    "Symbol table may not be set to null!",
                    new IllegalArgumentException());
        }

        if (mySymbolTable != null) {
            throw new MiscErrorException("Symbol table may only be set once!",
                    new IllegalStateException());
        }

        mySymbolTable = table;
    }

    /**
     * <p>Sets a new type graph to indicate relationship between types.</p>
     *
     * @param t The newly created type graph.
     */
    public final void setTypeGraph(TypeGraph t) {
        myTypeGraph = t;
    }

}