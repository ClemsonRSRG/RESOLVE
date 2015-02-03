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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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

    protected final ProgramTypeDefinitionEntry myDefinition;
    protected final PTType myRepresentation;
    protected final Exp myConvention;
    protected final Exp myCorrespondence;
    protected final TypeGraph myTypeGraph;

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

    public PTType getRepresentationType() {
        return myRepresentation;
    }

    public ProgramTypeDefinitionEntry getDefiningTypeEntry() {
        return myDefinition;
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
