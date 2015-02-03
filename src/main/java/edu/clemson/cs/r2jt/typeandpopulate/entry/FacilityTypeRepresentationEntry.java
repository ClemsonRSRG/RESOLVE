/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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