/**
 * AbstractPipeline.java
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
package edu.clemson.cs.r2jt.init;

import edu.clemson.cs.r2jt.absynnew.ResolveCompiler;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTableBuilder;

public abstract class AbstractPipeline {

    protected final ResolveCompiler myCompiler;
    protected final MathSymbolTableBuilder mySymbolTable;

    public AbstractPipeline(ResolveCompiler rc,
            MathSymbolTableBuilder symbolTable) {
        myCompiler = rc;
        mySymbolTable = symbolTable;
    }

    public abstract void process(ModuleIdentifier currentTarget);
}
