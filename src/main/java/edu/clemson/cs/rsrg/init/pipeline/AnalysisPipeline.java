/**
 * AnalysisPipeline.java
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
package edu.clemson.cs.rsrg.init.pipeline;

import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;

public class AnalysisPipeline extends AbstractPipeline {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This generates a pipeline to populate new symbols and
     * perform semantic analysis.</p>
     *
     * @param ce The current compilation environment.
     * @param symbolTable The symbol table.
     */
    public AnalysisPipeline(CompileEnvironment ce,
            MathSymbolTableBuilder symbolTable) {
        super(ce, symbolTable);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(ModuleIdentifier currentTarget) {
    /* PopulatingVisitor populator = new PopulatingVisitor(mySymbolTable);

     if (!myCompileEnvironment.containsID(currentTarget)) {
         throw new IllegalStateException("module ast null");
     }
     ModuleAST moduleTarget =
             myCompileEnvironment.getModuleAST(currentTarget);
     //TreeWalker.walk(populator, moduleTarget);

     PopulatingVisitor.emitDebug("Type Graph:\n\n"
             + mySymbolTable.getTypeGraph().toString());*/
    }

}