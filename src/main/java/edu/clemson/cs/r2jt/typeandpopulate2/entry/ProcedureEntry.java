/**
 * ProcedureEntry.java
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
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import org.antlr.v4.runtime.Token;

import java.util.Map;

public class ProcedureEntry extends SymbolTableEntry {

    private final OperationEntry myCorrespondingOperation;

    public ProcedureEntry(String name, ResolveAST definingElement,
            ModuleIdentifier sourceModule, OperationEntry correspondingOperation) {
        super(name, definingElement, sourceModule);

        myCorrespondingOperation = correspondingOperation;
    }

    public OperationEntry getCorrespondingOperation() {
        return myCorrespondingOperation;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a procedure";
    }

    @Override
    public ProcedureEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        return new ProcedureEntry(getName(), getDefiningElement(),
                getSourceModuleIdentifier(), myCorrespondingOperation
                        .instantiateGenerics(genericInstantiations,
                                instantiatingFacility));
    }

    @Override
    public ProcedureEntry toProcedureEntry(Token l) {
        return this;
    }
}
