/**
 * OperationEntry.java
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
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.LazilyMappedImmutableList;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.misc.Utils.Mapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationEntry extends SymbolTableEntry {

    private final PTType myReturnType;
    private final ImmutableList<ProgramParameterEntry> myParameters;

    public OperationEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType returnType,
            List<ProgramParameterEntry> parameters) {

        this(name, definingElement, sourceModule, returnType,
                new ArrayBackedImmutableList<ProgramParameterEntry>(parameters));
    }

    public OperationEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType returnType,
            ImmutableList<ProgramParameterEntry> parameters) {

        super(name, definingElement, sourceModule);

        myParameters = parameters;
        myReturnType = returnType;
    }

    public OperationEntry toOperationEntry(Location l) {
        return this;
    }

    public ImmutableList<ProgramParameterEntry> getParameters() {
        return myParameters;
    }

    public PTType getReturnType() {
        return myReturnType;
    }

    @Override
    public String getEntryTypeDescription() {
        return "an operation";
    }

    @Override
    public OperationEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        return new OperationEntry(
                getName(),
                getDefiningElement(),
                getSourceModuleIdentifier(),
                myReturnType.instantiateGenerics(genericInstantiations,
                        instantiatingFacility),
                new LazilyMappedImmutableList<ProgramParameterEntry, ProgramParameterEntry>(
                        myParameters, new InstantiationMapping(
                                genericInstantiations, instantiatingFacility)));
    }

    private static class InstantiationMapping
            implements
                Mapping<ProgramParameterEntry, ProgramParameterEntry> {

        private final Map<String, PTType> myGenericInstantiations;
        private final FacilityEntry myInstantiatingFacility;

        public InstantiationMapping(Map<String, PTType> instantiations,
                FacilityEntry instantiatingFacility) {
            myGenericInstantiations =
                    new HashMap<String, PTType>(instantiations);
            myInstantiatingFacility = instantiatingFacility;
        }

        @Override
        public ProgramParameterEntry map(ProgramParameterEntry input) {
            return (ProgramParameterEntry) input.instantiateGenerics(
                    myGenericInstantiations, myInstantiatingFacility);
        }

    }
}
