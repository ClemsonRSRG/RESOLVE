/*
 * FacilityTypeRepresentationEntry.java
 * ---------------------------------
 * Copyright (c) 2017
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
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * Created by danielwelch on 10/17/14.
 */
public class FacilityTypeRepresentationEntry extends RepresentationTypeEntry {

    public FacilityTypeRepresentationEntry(TypeGraph g, String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType representation, Exp convention) {
        super(g, name, definingElement, sourceModule, null, representation,
                convention, g.getTrueVarExp());
        // TODO : According to murali, facility types might also have
        // correspondences -- so until we figure out which direction is up,
        // we'll hardcode it to 'True'.
    }

    @Override
    public String getEntryTypeDescription() {
        return "a facility type representation definition";
    }

    @Override
    public ProgramTypeEntry toProgramTypeEntry(Location l) {
        return new ProgramTypeEntry(myTypeGraph, getName(),
                getDefiningElement(), getSourceModuleIdentifier(),
                myRepresentation.toMath(), myRepresentation);
    }

    @Override
    public FacilityTypeRepresentationEntry toFacilityTypeRepresentationEntry(
            Location l) {
        return this;
    }
}