/**
 * AnalysisPipeline.java
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
package edu.clemson.cs.rsrg.init.pipeline;

import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.ModuleIdentifier;

public class AnalysisPipeline extends AbstractPipeline {

    public AnalysisPipeline(CompileEnvironment compileEnvironment,
            MathSymbolTableBuilder symbolTable) {
        super(compileEnvironment, symbolTable);
    }

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