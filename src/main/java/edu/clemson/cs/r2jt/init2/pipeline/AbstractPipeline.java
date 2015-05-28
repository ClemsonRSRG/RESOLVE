/**
 * AbstractPipeline.java
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
package edu.clemson.cs.r2jt.init2.pipeline;

import edu.clemson.cs.r2jt.init2.CompileEnvironment;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTableBuilder;

/**
 * TODO: Description for this class
 */
public abstract class AbstractPipeline {

    protected final CompileEnvironment myCompileEnvironment;
    protected final MathSymbolTableBuilder mySymbolTable;

    protected AbstractPipeline(CompileEnvironment ce,
            MathSymbolTableBuilder symbolTable) {
        myCompileEnvironment = ce;
        mySymbolTable = symbolTable;
    }

    public abstract void process(ModuleIdentifier currentTarget);

}