/**
 * ProgramTypeDefinitionEntry.java
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
package edu.clemson.cs.r2jt.typeandpopulate2.entry;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTFamily;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import org.antlr.v4.runtime.Token;

/**
 * <p>Describes a "Type Family" introduction as would be found in a concept 
 * file.</p>
 */
public class ProgramTypeDefinitionEntry extends ProgramTypeEntry {

    private final MathSymbolEntry myExemplar;

    public ProgramTypeDefinitionEntry(TypeGraph g, String name,
            ResolveAST definingElement, ModuleIdentifier sourceModule,
            MTType modelType, PTFamily programType,
            MathSymbolEntry exemplarEntry) {
        super(g, name, definingElement, sourceModule, modelType, programType);

        myExemplar = exemplarEntry;
    }

    public MathSymbolEntry getExemplar() {
        return myExemplar;
    }

    @Override
    public PTFamily getProgramType() {
        return (PTFamily) super.getProgramType();
    }

    @Override
    public ProgramTypeDefinitionEntry toProgramTypeDefinitionEntry(Token l) {
        return this;
    }

    @Override
    public AbstractProgramEntry toProgrammaticEntry(Token l) {
        return this;
    }
}
