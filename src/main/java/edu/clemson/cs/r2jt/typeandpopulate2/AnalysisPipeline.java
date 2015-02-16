/**
 * AnalysisPipeline.java
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
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.absynnew.ModuleAST;
import edu.clemson.cs.r2jt.absynnew.ResolveCompiler;
import edu.clemson.cs.r2jt.absynnew.TreeWalker;
import edu.clemson.cs.r2jt.init.AbstractPipeline;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;

public class AnalysisPipeline extends AbstractPipeline {

    public AnalysisPipeline(ResolveCompiler rc,
            MathSymbolTableBuilder symbolTable) {
        super(rc, symbolTable);
    }

    @Override
    public void process(ModuleIdentifier currentTarget) {

        System.out.println("populating: " + currentTarget);
        PopulatingVisitor populator = new PopulatingVisitor(mySymbolTable);

        ModuleAST moduleTarget = myCompiler.myModules.get(currentTarget);
        if (moduleTarget == null) {
            throw new IllegalStateException("module ast null");
        }
        TreeWalker.walk(populator, moduleTarget);

    }
}
