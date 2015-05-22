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

import edu.clemson.cs.r2jt.absynnew.*;
import edu.clemson.cs.r2jt.init2.CompileEnvironment;
import edu.clemson.cs.r2jt.init2.CompileReport;
import edu.clemson.cs.r2jt.init2.file.ResolveFile;
import edu.clemson.cs.r2jt.errors.ErrorHandler2;
import edu.clemson.cs.r2jt.parsing.ResolveParser;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import java.io.IOException;

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
    private final ResolveParserFactory myParserFactory;
    private final MathSymbolTableBuilder mySymbolTable;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Controller(CompileEnvironment e) {
        myCompileEnvironment = e;
        myCompileReport = e.getCompileReport();
        myErrorHandler = e.getErrorHandler();
        myParserFactory = new ResolveParserFactory();
        mySymbolTable = (MathSymbolTableBuilder) e.getSymbolTable();
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
        // Set this as our target file in the compile environment
        myCompileEnvironment.setTargetFile(file);

        // Use ANTLR4 to build the AST
        ModuleAST targetModule = createModuleAST(file);

        // Add this file to our compile environment
        myCompileEnvironment.constructRecord(file, targetModule);

        // Create a dependencies graph and search for import
        // dependencies.
        DefaultDirectedGraph<ModuleIdentifier, DefaultEdge> g =
                new DefaultDirectedGraph<ModuleIdentifier, DefaultEdge>(
                        DefaultEdge.class);
        g.addVertex(new ModuleIdentifier(targetModule));
        findDependencies(g, targetModule);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This method uses the <code>ResolveFile</code> provided
     * to construct a parser and create an ANTLR4 module AST.</p>
     *
     * @param file The RESOLVE file that we are going to compile.
     *
     * @return The ANTLR4 Module AST.
     */
    private ModuleAST createModuleAST(ResolveFile file) {
        ResolveParser parser =
                myParserFactory.createParser(file.getInputStream());
        ParserRuleContext start = parser.module();
        return TreeUtil.createASTNodeFrom(start);
    }

    private void findDependencies(DefaultDirectedGraph g, ModuleAST root) {
    /*for (Token importRequest : root.getImports().getImportsExcluding(
            ImportCollectionAST.ImportType.EXTERNAL)) {

        File file = findResolveFile(importRequest.getText(), NATIVE_EXT);
        ModuleAST module = myModules.get(importRequest);

        if (module == null) {
            module = createModuleAST(file);

            myModules.put(id(module), module);
            myFiles.put(id(module), file);
        }

        if (root.getImports().inCategory(
                ImportCollectionAST.ImportType.IMPLICIT, importRequest)) {
            if (!module.appropriateForImport()) {
                throw new IllegalArgumentException("invalid import "
                        + module.getName() + "; cannot import module of "
                        + "type: " + module.getClass());
            }
        }
        if (pathExists(g, id(module), id(root))) {
            throw new CircularDependencyException(
                    "circular dependency detected");
        }
        Graphs.addEdgeWithVertices(g, id(root), id(module));
        findDependencies(g, module);
    }

    addFilesForExternalImports(root);*/
    }

}