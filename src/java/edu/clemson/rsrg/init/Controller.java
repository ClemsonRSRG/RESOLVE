/*
 * Controller.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.init;

import edu.clemson.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.rsrg.init.file.FileLocator;
import edu.clemson.rsrg.init.file.ModuleType;
import edu.clemson.rsrg.init.file.ResolveFile;
import edu.clemson.rsrg.init.file.ResolveFileBasicInfo;
import edu.clemson.rsrg.init.pipeline.*;
import edu.clemson.rsrg.misc.Utilities;
import edu.clemson.rsrg.nProver.GeneralPurposeProver;
import edu.clemson.rsrg.parsing.ResolveLexer;
import edu.clemson.rsrg.parsing.ResolveParser;
import edu.clemson.rsrg.parsing.TreeBuildingListener;
import edu.clemson.rsrg.parsing.data.ResolveTokenFactory;
import edu.clemson.rsrg.prover.CongruenceClassProver;
import edu.clemson.rsrg.statushandling.*;
import edu.clemson.rsrg.statushandling.exception.*;
import edu.clemson.rsrg.translation.AbstractTranslator;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.rsrg.vcgeneration.VCGenerator;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
 * <p>
 * A manager for the target file of a compilation.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 *
 * @version 1.0
 */
class Controller {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This is the lexer error listener for all ANTLR4 related objects.
     * </p>
     */
    private final AntlrLexerErrorListener myAntlrLexerErrorListener;

    /**
     * <p>
     * This is the parser error listener for all ANTLR4 related objects.
     * </p>
     */
    private final AntlrParserErrorListener myAntlrParserErrorListener;

    /**
     * <p>
     * The current job's compilation environment that stores all necessary objects and flags.
     * </p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /**
     * <p>
     * This is the status handler for the RESOLVE compiler.
     * </p>
     */
    private final StatusHandler myStatusHandler;

    /**
     * <p>
     * The symbol table for the compiler.
     * </p>
     */
    private final MathSymbolTableBuilder mySymbolTable;

    // ===========================================================
    // Objects
    // ===========================================================

    /**
     * <p>
     * This is a list of all externally realized file extensions accepted by the RESOLVE compiler. When adding a new
     * type of extension, simply add the extension name into the list.
     * </p>
     */
    private static final List<String> NON_NATIVE_EXT = Collections.unmodifiableList(Arrays.asList("java", "c", "h"));

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This object takes care of all dependency imports, population, semantic analysis, translation, VC generation
     * and/or verification.
     * </p>
     *
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     */
    Controller(CompileEnvironment compileEnvironment) {
        myCompileEnvironment = compileEnvironment;
        myStatusHandler = compileEnvironment.getStatusHandler();
        myAntlrLexerErrorListener = new AntlrLexerErrorListener(myStatusHandler);
        myAntlrParserErrorListener = new AntlrParserErrorListener(myStatusHandler);
        mySymbolTable = (MathSymbolTableBuilder) compileEnvironment.getSymbolTable();
    }

    // ===========================================================
    // Package Private Methods
    // ===========================================================

    /**
     * <p>
     * Compiles a target file. A target file is one that is specified on the command line of the compiler as opposed to
     * one that is being compiled because it was imported by another file.
     * </p>
     *
     * @param file
     *            The compiling RESOLVE file.
     */
    final void compileTargetFile(ResolveFile file) {
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
            DefaultDirectedGraph<ModuleIdentifier, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
            g.addVertex(new ModuleIdentifier(targetModule));
            findDependencies(g, targetModule, file.getParentPath());

            // Perform different compilation tasks to each file
            for (ModuleIdentifier m : getCompileOrder(g)) {
                // Print the entire ModuleDec
                if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_PRINT_MODULE)
                        && m.equals(new ModuleIdentifier(targetModule))) {
                    RawASTOutputPipeline rawASTOutputPipe = new RawASTOutputPipeline(myCompileEnvironment,
                            mySymbolTable);
                    rawASTOutputPipe.process(m);
                }

                // Output AST to Graphviz dot file. (Only for argument files)
                if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_EXPORT_AST)
                        && m.equals(new ModuleIdentifier(targetModule))) {
                    GraphicalASTOutputPipeline astOutputPipe = new GraphicalASTOutputPipeline(myCompileEnvironment,
                            mySymbolTable);
                    astOutputPipe.process(m);
                }

                // Type and populate symbol table
                AnalysisPipeline analysisPipe = new AnalysisPipeline(myCompileEnvironment, mySymbolTable);
                analysisPipe.process(m);

                // Translate source file to target file
                if (myCompileEnvironment.flags.isFlagSet(AbstractTranslator.FLAG_TRANSLATE)
                        && m.equals(new ModuleIdentifier(targetModule))) {
                    TranslatorPipeline translatorPipeline = new TranslatorPipeline(myCompileEnvironment, mySymbolTable);
                    translatorPipeline.process(m);
                }

                // Generate VCs
                if (myCompileEnvironment.flags.isFlagSet(VCGenerator.FLAG_VERIFY_VC)
                        && m.equals(new ModuleIdentifier(targetModule))) {
                    VCGenPipeline vcGenPipeline = new VCGenPipeline(myCompileEnvironment, mySymbolTable);
                    vcGenPipeline.process(m);
                    List<VerificationCondition> vcs = vcGenPipeline.getVerificationConditions();

                    // Invoke Automated Prover (if requested)
                    if (myCompileEnvironment.flags.isFlagSet(CongruenceClassProver.FLAG_PROVE)
                            && m.equals(new ModuleIdentifier(targetModule))) {
                        ProverPipeline proverPipeline = new ProverPipeline(myCompileEnvironment, mySymbolTable);
                        proverPipeline.process(m);
                    }

                    // Invoke nProver (if requested)
                    if (myCompileEnvironment.flags.isFlagSet(GeneralPurposeProver.FLAG_PROVE)
                            && m.equals(new ModuleIdentifier(targetModule))) {
                        NProverPipeline nproverPipeline = new NProverPipeline(myCompileEnvironment, mySymbolTable, vcs);
                        nproverPipeline.process(m);
                    }
                }

                // Complete compilation for this module
                myCompileEnvironment.completeRecord(m);
                if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                    myStatusHandler.info(null, "Done Compiling: " + m.toString());
                }
            }
        } catch (Throwable e) {
            Throwable cause = e;
            while (cause != null && !(cause instanceof CompilerException)) {
                cause = cause.getCause();
            }

            if (cause == null) {
                // All exceptions should extend the CompilerException class.
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new MiscErrorException("Unknown Exception", e);
            } else {
                CompilerException see = (CompilerException) cause;
                Fault fault = new Fault(FaultType.COMPILER_EXCEPTION, see.getErrorLocation(), see.getMessage(), false);
                myStatusHandler.registerAndStreamFault(fault);
                if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG_STACK_TRACE)) {
                    myStatusHandler.printStackTrace(see);
                }
                myStatusHandler.stopLogging();
            }
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * For concept/enhancement realizations, the user can supply Non-RESOLVE type files. This method locates all
     * externally supplied files and add them to the compile environment for future use.
     * </p>
     *
     * @param importName
     *            A filename that we have labeled as externally import
     *
     * @throws MiscErrorException
     *             We caught some kind of {@link IOException}.
     */
    private void addFileAsExternalImport(String importName) {
        try {
            FileLocator l = new FileLocator(importName, NON_NATIVE_EXT);
            File workspaceDir = myCompileEnvironment.getWorkspaceDir();
            Files.walkFileTree(workspaceDir.toPath(), l);

            // Only attempt to add
            List<File> foundFiles = l.getFiles();
            if (foundFiles.size() == 1) {
                ModuleIdentifier externalImport = new ModuleIdentifier(importName);

                // Add this as an external realiz file if it is not already declared to be one.
                if (!myCompileEnvironment.isExternalRealizFile(externalImport)) {
                    myCompileEnvironment.addExternalRealizFile(externalImport, l.getFile());

                    // Print out debugging message
                    if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                        myStatusHandler.info(null, "Skipping External Import: " + importName);
                    }
                }
            } else if (foundFiles.size() > 1) {
                throw new ImportException("Found more than one external import with the name " + importName + ";");
            }
        } catch (IOException ioe) {
            throw new MiscErrorException(ioe.getMessage(), ioe.getCause());
        }
    }

    /**
     * <p>
     * This method uses the {@link ResolveFile} provided to construct a parser and create an ANTLR4 module AST.
     * </p>
     *
     * @param file
     *            The RESOLVE file that we are going to compile.
     *
     * @return The inner representation for a module. See {@link ModuleDec}.
     *
     * @throws MiscErrorException
     *             Some how we couldn't instantiate an {@link CharStream}.
     * @throws SourceErrorException
     *             There are errors in the source file.
     */
    private ModuleDec createModuleAST(ResolveFile file) {
        CharStream input = file.getInputStream();
        if (input == null) {
            throw new MiscErrorException("CharStream null", new IllegalArgumentException());
        }

        // Create a RESOLVE language lexer
        ResolveLexer lexer = new ResolveLexer(input);
        ResolveTokenFactory factory = new ResolveTokenFactory(file);
        lexer.removeErrorListeners();
        lexer.addErrorListener(myAntlrLexerErrorListener);
        lexer.setTokenFactory(factory);

        // Create a RESOLVE language parser
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ResolveParser parser = new ResolveParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(myAntlrParserErrorListener);
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
        } catch (Exception ex) {
            tokens.seek(0);
            parser.reset();
            parser.getInterpreter().setPredictionMode(PredictionMode.LL);
            rootModuleCtx = parser.module();
        }

        // Check for any parsing errors
        int numParserErrors = parser.getNumberOfSyntaxErrors();
        if (numParserErrors != 0) {
            throw new MiscErrorException("Found " + numParserErrors + " errors while parsing " + file.toString(),
                    new IllegalStateException());
        }

        // Build the intermediate representation
        TreeBuildingListener v = new TreeBuildingListener(file, myCompileEnvironment.getTypeGraph());
        ParseTreeWalker.DEFAULT.walk(v, rootModuleCtx);

        return v.getModule();
    }

    /**
     * <p>
     * A recursive method to find all the import dependencies needed by the specified module.
     * </p>
     *
     * @param g
     *            The compilation's file dependency graph.
     * @param root
     *            Current compiling module.
     * @param parentPath
     *            The parent path if it is known. Otherwise, this can be {@code null}.
     *
     * @throws CircularDependencyException
     *             Some of the source files form a circular dependency.
     * @throws ImportException
     *             Incorrect import type.
     * @throws SourceErrorException
     *             There are errors in the source file.
     */
    private void findDependencies(DefaultDirectedGraph<ModuleIdentifier, DefaultEdge> g, ModuleDec root,
            Path parentPath) {
        ModuleIdentifier rootId = new ModuleIdentifier(root);
        Map<ResolveFileBasicInfo, Boolean> allImports = root.getModuleDependencies();
        for (ResolveFileBasicInfo importRequest : allImports.keySet()) {
            // Don't try to import the built-in Cls_Theory
            if (!importRequest.getName().equals("Cls_Theory")) {
                // Check to see if this import has been labeled as externally realized
                // or not. If yes, we add it as an external import and move on.
                // If no, we add it as a new dependency that must be imported.
                if (!allImports.get(importRequest)) {
                    // Only need to deal with imports we haven't seen yet.
                    ModuleIdentifier id = new ModuleIdentifier(importRequest.getName());
                    if (!myCompileEnvironment.containsID(id)) {
                        // Print out debugging message
                        if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                            myStatusHandler.info(null, "Importing New Module: " + id.toString());
                        }

                        ResolveFile file = findResolveFile(importRequest, parentPath);
                        ModuleDec module = createModuleAST(file);
                        if (module == null) {
                            // Import error
                            throw new ImportException("Invalid import: " + importRequest.toString()
                                    + "; Cannot import module of " + "type: " + file.getModuleType().getExtension());
                        } else {
                            // Construct a record and check this new module for dependencies
                            myCompileEnvironment.constructRecord(file, module);
                            findDependencies(g, module, file.getParentPath());
                        }
                    } else {
                        ModuleDec module = myCompileEnvironment.getModuleAST(id);
                        if (module == null) {
                            // Import error
                            throw new ImportException("Import error: " + importRequest.toString()
                                    + "; Module does not exist in our current compile environment.");
                        }
                    }

                    // Check for circular dependency
                    if (pathExists(g, id, rootId)) {
                        throw new CircularDependencyException(
                                "Circular dependency detected: " + importRequest.getName() + "<->" + root.getName());
                    }

                    // Add new edge to our graph indicating the relationship between
                    // the two files.
                    Graphs.addEdgeWithVertices(g, rootId, id);
                } else {
                    addFileAsExternalImport(importRequest.getName());
                }
            }
        }
    }

    /**
     * <p>
     * This method attempts to locate a file with the specified name.
     * </p>
     *
     * @param fileBasicInfo
     *            The name of the file including any known parent directory.
     * @param parentPath
     *            The parent path if it is known. Otherwise, this can be {@code null}.
     *
     * @return A {@link ResolveFile} object that is used by the compiler.
     *
     * @throws MiscErrorException
     *             We caught some kind of {@link IOException}.
     */
    private ResolveFile findResolveFile(ResolveFileBasicInfo fileBasicInfo, Path parentPath) {
        // First check to see if this is a user created
        // file from the WebIDE/WebAPI.
        ResolveFile file;
        if (myCompileEnvironment.isMetaFile(fileBasicInfo)) {
            file = myCompileEnvironment.getUserFileFromMap(fileBasicInfo);
        }
        // If not, use the file locator to locate our file
        else {
            try {
                // There might be files with the same name all throughout the workspace,
                // so ideally we want to start from the innermost path possible.
                File actualFile = null;
                if (parentPath != null) {
                    try {
                        FileLocator l = new FileLocator(fileBasicInfo.getName(), ModuleType.getAllExtensions());

                        // If our file's basic information contains a parent directory
                        // that matches a file we have already compiled, use that path
                        // instead of the parent path passed in.
                        if (myCompileEnvironment.containsID(new ModuleIdentifier(fileBasicInfo.getParentDirName()))) {
                            Files.walkFileTree(myCompileEnvironment
                                    .getFile(new ModuleIdentifier(fileBasicInfo.getParentDirName())).getParentPath(),
                                    l);
                        } else {
                            Files.walkFileTree(parentPath, l);
                        }

                        actualFile = l.getFile();
                    } catch (IOException ioe2) {
                        // Don't do anything. We simply didn't find it using the parent path.
                    }
                }

                // If we couldn't find it, try searching the entire workspace.
                File workspaceDir = myCompileEnvironment.getWorkspaceDir();
                if (actualFile == null) {
                    FileLocator l = new FileLocator(fileBasicInfo.getName(), ModuleType.getAllExtensions());
                    Files.walkFileTree(workspaceDir.toPath(), l);
                    actualFile = l.getFile();
                }

                // Convert to ResolveFile
                ModuleType extType = Utilities.getModuleType(actualFile.getName());
                file = Utilities.convertToResolveFile(actualFile, extType, workspaceDir.getAbsolutePath());
            } catch (IOException ioe) {
                throw new MiscErrorException(ioe.getMessage(), ioe.getCause());
            }
        }

        return file;
    }

    /**
     * <p>
     * This method returns the order that our modules need to be compiled.
     * </p>
     *
     * @param g
     *            The compilation's file dependency graph.
     *
     * @return An ordered list of {@link ModuleIdentifier ModuleIdentifiers}.
     */
    private List<ModuleIdentifier> getCompileOrder(DefaultDirectedGraph<ModuleIdentifier, DefaultEdge> g) {
        List<ModuleIdentifier> result = new ArrayList<>();

        EdgeReversedGraph<ModuleIdentifier, DefaultEdge> reversed = new EdgeReversedGraph<>(g);

        TopologicalOrderIterator<ModuleIdentifier, DefaultEdge> dependencies = new TopologicalOrderIterator<>(reversed);
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
     * <p>
     * This method is used to check for circular dependencies when importing modules using our file dependencies graph.
     * </p>
     *
     * @param g
     *            The compilation's file dependency graph.
     * @param src
     *            The source file module.
     * @param dest
     *            The destination file module.
     *
     * @return {@code true} if there is a cycle, {@code false} otherwise.
     */
    private boolean pathExists(DefaultDirectedGraph<ModuleIdentifier, DefaultEdge> g, ModuleIdentifier src,
            ModuleIdentifier dest) {
        // If src doesn't exist in g, then there is obviously no path from
        // src -> ... -> dest
        if (!g.containsVertex(src)) {
            return false;
        }
        GraphIterator<ModuleIdentifier, DefaultEdge> iterator = new DepthFirstIterator<>(g, src);

        while (iterator.hasNext()) {
            ModuleIdentifier next = iterator.next();
            // we've reached dest from src -- a path exists.
            if (next.equals(dest)) {
                return true;
            }
        }
        return false;
    }
}
