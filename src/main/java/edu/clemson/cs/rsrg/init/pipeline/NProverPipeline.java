/*
 * NProverPipeline.java
 * ---------------------------------
 * Copyright (c) 2020
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
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;

/**
 * <p>
 * This is pipeline that invokes an automated nprover to verify sequent
 * verification
 * conditions (VCs) using the symbol table.
 * </p>
 */
public class NProverPipeline extends AbstractPipeline {

    public NProverPipeline(CompileEnvironment ce,
            MathSymbolTableBuilder symbolTable) {
        super(ce, symbolTable);
    }

    //public methods
    @Override
    public final void process(ModuleIdentifier currentTarget) {

    }
}
