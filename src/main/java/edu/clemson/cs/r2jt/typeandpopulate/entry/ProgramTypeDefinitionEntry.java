/*
 * ProgramTypeDefinitionEntry.java
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
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTFamily;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>
 * Describes a "Type Family" introduction as would be found in a concept file.
 * </p>
 */
public class ProgramTypeDefinitionEntry extends ProgramTypeEntry {

    private final MathSymbolEntry myExemplar;

    public ProgramTypeDefinitionEntry(TypeGraph g, String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, MTType modelType,
            PTFamily programType, MathSymbolEntry exemplarEntry) {
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
    public ProgramTypeDefinitionEntry toProgramTypeDefinitionEntry(Location l) {
        return this;
    }
}
