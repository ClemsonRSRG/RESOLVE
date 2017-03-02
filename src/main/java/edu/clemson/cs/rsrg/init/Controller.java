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

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.cs.rsrg.init.pipeline.AnalysisPipeline;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.statushandling.StdErrHandler;
import edu.clemson.cs.rsrg.statushandling.AntlrErrorListener;
import edu.clemson.cs.rsrg.statushandling.exception.*;
import edu.clemson.cs.rsrg.init.file.FileLocator;
import edu.clemson.cs.rsrg.init.file.ModuleType;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.init.pipeline.ASTOutputPipeline;
import edu.clemson.cs.rsrg.misc.Utilities;
import edu.clemson.cs.rsrg.parsing.ResolveLexer;
import edu.clemson.cs.rsrg.parsing.ResolveParser;
import edu.clemson.cs.rsrg.parsing.TreeBuildingListener;
import edu.clemson.cs.rsrg.parsing.data.ResolveTokenFactory;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
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
     * <p>This is the status handler for the RESOLVE compiler.</p>
     */
    private final StatusHandler myStatusHandler;

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
        myStatusHandler = compileEnvironment.getStatusHandler();
        myAntlrErrorListener = new AntlrErrorListener(myStatusHandler);
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
    public final void compileTargetFile(ResolveFile file) {
        try {
            // Use ANTLR4 to build the AST
            ModuleDec targetModule = createModuleAST(file);

            // Add this file to our compile environment
            myCompileEnvironment.constructRecord(file, targetModule);
            if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                myStatusHandler.info(null, "Begin Compiling: " + targetModule.getName().getName());
            }

            // Create a dependencies graph and search for import
            // dependencies.
            DefaultDirectedGraph<ModuleIdentifier, DefaultEdge> g =
                    new DefaultDirectedGraph<>(
                            DefaultEdge.class);
            g.addVertex(new ModuleIdentifier(targetModule));
            findDependencies(g, targetModule);

            // Begin analyzing the file
            AnalysisPipeline analysisPipe =
                    new AnalysisPipeline(myCompileEnvironment, mySymbolTable);
            for (ModuleIdentifier m : getCompileOrder(g)) {
                // Print the entire ModuleDec
                if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_PRINT_MODULE) &&
                        m.equals(new ModuleIdentifier(targetModule))) {
                    ModuleDec dec = myCompileEnvironment.getModuleAST(m);

                    StringBuffer sb = new StringBuffer();
                    sb.append("\n---------------Print Module---------------\n\n");
                    sb.append(dec.asString(0, 4));
                    sb.append("\n---------------Print Module---------------\n");
                    myStatusHandler.info(null, sb.toString());
                }

                // Output AST to Graphviz dot file. (Only for argument files)
                if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_EXPORT_AST) &&
                        m.equals(new ModuleIdentifier(targetModule))) {
                    ASTOutputPipeline astOutputPipe =
                            new ASTOutputPipeline(myCompileEnvironment, mySymbolTable);
                    astOutputPipe.process(m);
                }

                // Type and populate symbol table
                analysisPipe.process(m);

                // Complete compilation for this module
                myCompileEnvironment.completeRecord(m);
                if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                    myStatusHandler.info(null, "Done Compiling: " + m.toString());
                }
            }
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
                myStatusHandler.error(see.getErrorLocation(), e.getMessage());
                if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG_STACK_TRACE)
                        && myStatusHandler instanceof StdErrHandler) {
                   e.printStackTrace();
                }
                myStatusHandler.stopLogging();
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
     * @param importItem A filename that we have labeled as externally import
     *
     * @throws MiscErrorException We caught some kind of {@link IOException}.
     */
    private void addFileAsExternalImport(PosSymbol importItem) {
        try {
            FileLocator l =
                    new FileLocator(importItem.getName(), NON_NATIVE_EXT);
            File workspaceDir = myCompileEnvironment.getWorkspaceDir();
            Files.walkFileTree(workspaceDir.toPath(), l);

            // Only attempt to add
            List<File> foundFiles = l.getFiles();
            if (foundFiles.size() == 1) {
                myCompileEnvironment
                        .addExternalRealizFile(new ModuleIdentifier(importItem
                                .getName()), l.getFile());

                // Print out debugging message
                if (myCompileEnvironment.flags
                        .isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                    myStatusHandler.info(null, "Skipping External Import: "
                            + importItem.getName());
                }
            }
            else if (foundFiles.size() > 1) {
                throw new ImportException(
                        "Found more than one external import with the name "
                                + importItem.getName() + ";");
            }
        }
        catch (IOException ioe) {
            throw new MiscErrorException(ioe.getMessage(), ioe.getCause());
        }
    }

    /**
     * <p>This method uses the {@link ResolveFile} provided
     * to construct a parser and create an ANTLR4 module AST.</p>
     *
     * @param file The RESOLVE file that we are going to compile.
     *
     * @return The inner representation for a module. See {@link ModuleDec}.
     *
     * @throws MiscErrorException Some how we couldn't instantiate an {@link ANTLRInputStream}.
     * @throws SourceErrorException There are errors in the source file.
     */
    private ModuleDec createModuleAST(ResolveFile file) {
        ANTLRInputStream input = file.getInputStream();
        if (input == null) {
            throw new MiscErrorException("ANTLRInputStream null",
                    new IllegalArgumentException());
        }

        // Create a RESOLVE language lexer
        ResolveLexer lexer = new ResolveLexer(input);
        ResolveTokenFactory factory = new ResolveTokenFactory(file);
        lexer.setTokenFactory(factory);

        // Create a RESOLVE language parser
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ResolveParser parser = new ResolveParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(myAntlrErrorListener);
        parser.setTokenFactory(factory);

        // Two-Stage Parsing
        // Reason: We might not need the full power of LL.
        // The solution proposed by the ANTLR folks (found here:
        // https://github.com/antlr/antlr4/blob/master/doc/faq/general.md)
        // is to use SLL prediction mode first and switch to LL if it fails.
        ParserRuleContext rootModuleCtx;
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        try {
            rootModuleCtx = parser.module();
        }
        catch (Exception ex) {
            tokens.reset();
            parser.reset();
            parser.getInterpreter().setPredictionMode(PredictionMode.LL);
            rootModuleCtx = parser.module();
        }

        // Check for any parsing errors
        int numParserErrors = parser.getNumberOfSyntaxErrors();
        if (numParserErrors != 0) {
            throw new MiscErrorException("Found " + numParserErrors
                    + " errors while parsing " + file.toString(),
                    new IllegalStateException());
        }

        // Build the intermediate representation
        TreeBuildingListener v =
                new TreeBuildingListener(file, myCompileEnvironment
                        .getTypeGraph());
        ParseTreeWalker.DEFAULT.walk(v, rootModuleCtx);

        return v.getModule();
    }

    /**
     * <p>A recursive method to find all the import dependencies
     * needed by the specified module.</p>
     *
     * @param g The compilation's file dependency graph.
     * @param root Current compiling module.
     *
     * @throws CircularDependencyException Some of the source files form a
     * circular dependency.
     * @throws ImportException Incorrect import type.
     * @throws SourceErrorException There are errors in the source file.
     */
    private void findDependencies(DefaultDirectedGraph g, ModuleDec root) {
        Map<PosSymbol, Boolean> allImports = root.getModuleDependencies();
        for (PosSymbol importRequest : allImports.keySet()) {
            // Check to see if this import has been labeled as externally realized
            // or not. If yes, we add it as an external import and move on.
            // If no, we add it as a new dependency that must be imported.
            if (!allImports.get(importRequest)) {
                ResolveFile file = findResolveFile(importRequest.getName());
                ModuleIdentifier id =
                        new ModuleIdentifier(importRequest.getName());
                ModuleIdentifier rootId = new ModuleIdentifier(root);
                ModuleDec module;

                // Search for the file in our processed modules
                if (!myCompileEnvironment.containsID(id)) {
                    module = createModuleAST(file);
                    myCompileEnvironment.constructRecord(file, module);

                    // Print out debugging message
                    if (myCompileEnvironment.flags
                            .isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                        myStatusHandler.info(null, "Importing New Module: "
                                + id.toString());
                    }
                }
                else {
                    module = myCompileEnvironment.getModuleAST(id);
                }

                // Import error
                if (module == null) {
                    throw new ImportException("Invalid import "
                            + importRequest.toString()
                            + "; Cannot import module of " + "type: "
                            + file.getModuleType().getExtension());
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
            else {
                addFileAsExternalImport(importRequest);
            }
        }
    }

    /**
     * <p>This method attempts to locate a file with the
     * specified name.</p>
     *
     * @param baseName The name of the file including the extension
     *
     * @return A <code>ResolveFile</code> object that is used by the compiler.
     *
     * @throws MiscErrorException We caught some kind of {@link IOException}.
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
                ModuleType extType =
                        Utilities.getModuleType(l.getFile().getName());
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
     * @return An ordered list of {@link ModuleIdentifier ModuleIdentifiers}.
     */
    private List<ModuleIdentifier> getCompileOrder(DefaultDirectedGraph g) {
        List<ModuleIdentifier> result = new ArrayList<>();

        EdgeReversedGraph<ModuleIdentifier, DefaultEdge> reversed =
                new EdgeReversedGraph<>(g);

        TopologicalOrderIterator<ModuleIdentifier, DefaultEdge> dependencies =
                new TopologicalOrderIterator<>(
                        reversed);
        while (dependencies.hasNext()) {
            // Ignore the modules that have been compiled
            ModuleIdentifier next = dependencies.next();
            if (!myCompileEnvironment.isCompleteModule(next)) {
                result.add(next);
            }
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
     * @return {@code true} if there is a cycle, {@code false} otherwise.
     */
    private boolean pathExists(DefaultDirectedGraph g, ModuleIdentifier src,
            ModuleIdentifier dest) {
        //If src doesn't exist in g, then there is obviously no path from
        //src -> ... -> dest
        if (!g.containsVertex(src)) {
            return false;
        }
        GraphIterator<ModuleIdentifier, DefaultEdge> iterator =
                new DepthFirstIterator<>(g, src);

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