/**
 * RepresentationTypeEntry.java
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

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Map;

/**
 *
 * @author hamptos
 */
public class RepresentationTypeEntry extends SymbolTableEntry {

    private final ProgramTypeDefinitionEntry myDefinition;
    private final PTType myRepresentation;
    private final Exp myConvention;
    private final Exp myCorrespondence;
    private final TypeGraph myTypeGraph;

    public RepresentationTypeEntry(TypeGraph g, String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule,
            ProgramTypeDefinitionEntry definition, PTType representation,
            Exp convention, Exp correspondence) {
        super(name, definingElement, sourceModule);

        if (convention == null) {
            convention = representation.getTypeGraph().getTrueVarExp();
        }

        if (correspondence == null) {
            throw new RuntimeException();
        }

        myDefinition = definition;
        myRepresentation = representation;
        myConvention = convention;
        myCorrespondence = correspondence;
        myTypeGraph = g;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a program type representation definition";
    }

    @Override
    public RepresentationTypeEntry toRepresentationTypeEntry(Location l) {
        return this;
    }

    @Override
    public ProgramTypeEntry toProgramTypeEntry(Location l) {
        return new ProgramTypeEntry(myTypeGraph, getName(),
                getDefiningElement(), getSourceModuleIdentifier(), myDefinition
                        .getModelType(), myRepresentation);
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        //Representation is an internal implementation detail of a realization
        //and cannot be accessed through a facility instantiation
        throw new UnsupportedOperationException("Cannot instantiate "
                + this.getClass());
    }
}
