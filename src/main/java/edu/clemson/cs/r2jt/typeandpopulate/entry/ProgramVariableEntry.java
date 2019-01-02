/*
 * ProgramVariableEntry.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;

public class ProgramVariableEntry extends SymbolTableEntry {

    private final PTType myType;
    private final MathSymbolEntry myMathSymbolAlterEgo;

    public ProgramVariableEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType type) {
        super(name, definingElement, sourceModule);

        myType = type;

        //TODO: Probably need to recajigger this to correctly account for any
        //      generics in the defining context
        myMathSymbolAlterEgo =
                new MathSymbolEntry(type.getTypeGraph(), name,
                        Quantification.NONE, definingElement, type.toMath(),
                        null, null, null, sourceModule);
    }

    public PTType getProgramType() {
        return myType;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a program variable";
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        SymbolTableEntry result;

        PTType instantiatedType =
                myType.instantiateGenerics(genericInstantiations,
                        instantiatingFacility);

        if (instantiatedType != myType) {
            result =
                    new ProgramVariableEntry(getName(), getDefiningElement(),
                            getSourceModuleIdentifier(), instantiatedType);
        }
        else {
            result = this;
        }

        return result;
    }

    public ProgramVariableEntry toProgramVariableEntry(Location l) {
        return this;
    }

    public MathSymbolEntry toMathSymbolEntry(Location l) {
        return myMathSymbolAlterEgo;
    }
}
