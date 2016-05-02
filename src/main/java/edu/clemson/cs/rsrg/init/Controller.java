/**
 * Controller.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.init;

import edu.clemson.cs.r2jt.absynnew.ImportCollectionAST;
import edu.clemson.cs.r2jt.absynnew.ModuleAST;
import edu.clemson.cs.rsrg.errorhandling.AntlrErrorListener;
import edu.clemson.cs.rsrg.errorhandling.ErrorHandler;
import edu.clemson.cs.rsrg.errorhandling.exception.*;
import edu.clemson.cs.rsrg.init.file.FileLocator;
import edu.clemson.cs.rsrg.init.file.ModuleType;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.init.file.Utilities;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.parsing.*;
import edu.clemson.cs.rsrg.parsing.data.ResolveToken;
import edu.clemson.cs.rsrg.parsing.data.ResolveTokenFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * <p>A manager for the target file of a compilation.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 1.0
 */
public class Controller {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The current job's compilation environment
     * that stores all necessary objects and flags.</p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /**
     * <p>This is the error handler for the RESOLVE compiler.</p>
     */
    private final ErrorHandler myErrorHandler;

    /**
     * <p>This is the error listener for all ANTLR4 related objects.</p>
     */
    private final AntlrErrorListener myAntlrErrorListener;

    /**
     * <p>The symbol table for the compiler.</p>
     */
    private final MathSymbolTableBuilder mySymbolTable;

    // ===========================================================
    // Objects
    // ===========================================================

    /**
     * <p>This is a list of all externally realized file extensions
     * accepted by the RESOLVE compiler. When adding a new type of
     * extension, simply add the extension name into the list.</p>
     */
    public static final List<String> NON_NATIVE_EXT =
            Collections.unmodifiableList(Arrays.asList("java", "c", "h"));

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This object takes care of all dependency imports, population,
     * semantic analysis, translation, VC generation and/or verification.</p>
     *
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     */
    public Controller(CompileEnvironment compileEnvironment) {
        myCompileEnvironment = compileEnvironment;
        myErrorHandler = compileEnvironment.getErrorHandler();
        myAntlrErrorListener = new AntlrErrorListener(myErrorHandler);
        mySymbolTable =
                (MathSymbolTableBuilder) compileEnvironment.getSymbolTable();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Compiles a target file. A target file is one that is specified on the
     * command line of the compiler as opposed to one that is being compiled
     * because it was imported by another file.</p>
     *
     * @param file The compiling RESOLVE file.
     */
    public void compileTargetFile(ResolveFile file) {
        try {
            // Use ANTLR4 to build the AST
            ModuleAST targetModule = createModuleAST(file);

            // Add this file to our compile environment
            /*myCompileEnvironment.constructRecord(file, targetModule);

            // Create a dependencies graph and search for import
            // dependencies.
            DefaultDirectedGraph<ModuleIdentifier, DefaultEdge> g =
                    new DefaultDirectedGraph<ModuleIdentifier, DefaultEdge>(
                            DefaultEdge.class);
            g.addVertex(new ModuleIdentifier(targetModule));
            findDependencies(g, targetModule);

            // Begin analyzing the file
            AnalysisPipeline analysisPipe =
                    new AnalysisPipeline(myCompileEnvironment, mySymbolTable);
            for (ModuleIdentifier m : getCompileOrder(g)) {
                analysisPipe.process(m);

                // Complete compilation for this module
                myCompileEnvironment.completeRecord(m);
            }*/
        }
        catch (Throwable e) {
            Throwable cause = e;
            while (cause != null && !(cause instanceof CompilerException)) {
                cause = cause.getCause();
            }

            if (cause == null) {
                // TODO: Check to see if ever get here. All exceptions should extend the CompilerException class.
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new MiscErrorException("Unknown Exception", e);
            }
            else {
                CompilerException see = (CompilerException) cause;
                myErrorHandler.error(see.getOffendingToken(), e.getMessage());
            }
        }
        finally {
            // Stop error logging
            ErrorHandler errorHandler = myCompileEnvironment.getErrorHandler();
            if (!errorHandler.hasStopped()) {
                errorHandler.stopLogging();
            }
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>For concept/enhancement realizations, the user can supply
     * Non-RESOLVE type files. This method locates all externally
     * supplied files and add them to the compile environment for
     * future use.</p>
     *
     * @param m The compiling module.
     *
     * @throws MiscErrorException
     */
    private void addFilesForExternalImports(ModuleAST m) {
        Set<Token> externals =
                m.getImports().getImportsOfType(
                        ImportCollectionAST.ImportType.EXTERNAL);

        for (Token externalImport : externals) {
            try {
                FileLocator l =
                        new FileLocator(externalImport.getText(),
                                NON_NATIVE_EXT);
                File workspaceDir = myCompileEnvironment.getWorkspaceDir();
                Files.walkFileTree(workspaceDir.toPath(), l);
                myCompileEnvironment.addExternalRealizFile(
                        new ModuleIdentifier(externalImport), l.getFile());
            }
            catch (IOException ioe) {
                throw new MiscErrorException(ioe.getMessage(), ioe.getCause());
            }
        }
    }

    /**
     * <p>This method uses the <code>ResolveFile</code> provided
     * to construct a parser and create an ANTLR4 module AST.</p>
     *
     * @param file The RESOLVE file that we are going to compile.
     *
     * @return The ANTLR4 Module AST.
     *
     * @throws MiscErrorException
     */
    private ModuleAST createModuleAST(ResolveFile file) {
        ANTLRInputStream input = file.getInputStream();
        if (input == null) {
            throw new MiscErrorException("ANTLRInputStream null",
                    new IllegalArgumentException());
        }

        ResolveLexer lexer = new ResolveLexer(input);
        ResolveTokenFactory factory = new ResolveTokenFactory(input);
        lexer.setTokenFactory(factory);

        TokenStream tokenStream = new CommonTokenStream(lexer);
        ResolveParser parser = new ResolveParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(myAntlrErrorListener);
        parser.setTokenFactory(factory);
        ParserRuleContext context = parser.module();
        //return TreeUtil.createASTNodeFrom(start);
        return null;
    }

    /**
     * <p>A recursive method to find all the import dependencies
     * needed by the specified module.</p>
     *
     * @param g The compilation's file dependency graph.
     * @param root Current compiling module.
     *
     * @throws CircularDependencyException
     * @throws ImportException
     */
    private void findDependencies(DefaultDirectedGraph g, ModuleAST root) {
        for (Token importRequest : root.getImports().getImportsExcluding(
                ImportCollectionAST.ImportType.EXTERNAL)) {
            ResolveFile file = findResolveFile(importRequest.getText());
            ModuleIdentifier id = new ModuleIdentifier(importRequest);
            ModuleIdentifier rootId = new ModuleIdentifier(root);
            ModuleAST module;

            // Search for the file in our processed modules
            if (myCompileEnvironment.containsID(id)) {
                module = createModuleAST(file);
                myCompileEnvironment.constructRecord(file, module);
            }
            else {
                module = myCompileEnvironment.getModuleAST(id);
            }

            // Import error
            if (root.getImports().inCategory(
                    ImportCollectionAST.ImportType.IMPLICIT, importRequest)) {
                if (!module.appropriateForImport()) {
                    throw new ImportException("Invalid import "
                            + module.getName() + "; Cannot import module of "
                            + "type: " + module.getClass());
                }
            }

            // Check for circular dependency
            if (pathExists(g, id, rootId)) {
                throw new CircularDependencyException(
                        "Circular dependency detected.");
            }

            // Add new edge to our graph indicating the relationship between
            // the two files.
            Graphs.addEdgeWithVertices(g, rootId, id);

            // Now check this new module for dependencies
            findDependencies(g, module);
        }

        addFilesForExternalImports(root);
    }

    /**
     * <p>This method attempts to locate a file with the
     * specified name.</p>
     *
     * @param baseName The name of the file including the extension
     *
     * @return A <code>ResolveFile</code> object that is used by the compiler.
     *
     * @throws MiscErrorException
     */
    private ResolveFile findResolveFile(String baseName) {
        // First check to see if this is a user created
        // file from the WebIDE/WebAPI.
        ResolveFile file;
        if (myCompileEnvironment.isMetaFile(baseName)) {
            file = myCompileEnvironment.getUserFileFromMap(baseName);
        }
        // If not, use the file locator to locate our file
        else {
            try {
                FileLocator l =
                        new FileLocator(baseName, ModuleType.getAllExtensions());
                File workspaceDir = myCompileEnvironment.getWorkspaceDir();
                Files.walkFileTree(workspaceDir.toPath(), l);
                ModuleType extType = Utilities.getModuleType(baseName);
                file =
                        Utilities.convertToResolveFile(l.getFile(), extType,
                                workspaceDir.getAbsolutePath());
            }
            catch (IOException ioe) {
                throw new MiscErrorException(ioe.getMessage(), ioe.getCause());
            }
        }

        return file;
    }

    /**
     * <p>This method returns the order that our modules
     * need to be compiled.</p>
     *
     * @param g The compilation's file dependency graph.
     *
     * @return An ordered list of <code>ModuleIdentifiers</code>.
     */
    private List<ModuleIdentifier> getCompileOrder(DefaultDirectedGraph g) {
        List<ModuleIdentifier> result = new ArrayList<ModuleIdentifier>();

        EdgeReversedGraph<ModuleIdentifier, DefaultEdge> reversed =
                new EdgeReversedGraph<ModuleIdentifier, DefaultEdge>(g);

        TopologicalOrderIterator<ModuleIdentifier, DefaultEdge> dependencies =
                new TopologicalOrderIterator<ModuleIdentifier, DefaultEdge>(
                        reversed);
        while (dependencies.hasNext()) {
            result.add(dependencies.next());
        }
        return result;
    }

    /**
     * <p>This method is used to check for circular dependencies when
     * importing modules using our file dependencies graph.</p>
     *
     * @param g The compilation's file dependency graph.
     * @param src The source file module.
     * @param dest The destination file module.
     *
     * @return True if there is a cycle, false otherwise.
     */
    private boolean pathExists(DefaultDirectedGraph g, ModuleIdentifier src,
            ModuleIdentifier dest) {
        //If src doesn't exist in g, then there is obviously no path from
        //src -> ... -> dest
        if (!g.containsVertex(src)) {
            return false;
        }
        GraphIterator<ModuleIdentifier, DefaultEdge> iterator =
                new DepthFirstIterator<ModuleIdentifier, DefaultEdge>(g, src);

        while (iterator.hasNext()) {
            ModuleIdentifier next = iterator.next();
            //we've reached dest from src -- a path exists.
            if (next.equals(dest)) {
                return true;
            }
        }
        return false;
    }

}