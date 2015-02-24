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
package edu.clemson.cs.r2jt.typeandpopulate2.entry;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import org.antlr.v4.runtime.Token;

import java.util.Map;

/**
 *
 * @author hamptos
 */
public class RepresentationTypeEntry extends SymbolTableEntry {

    protected final ProgramTypeDefinitionEntry myDefinition;
    protected final PTType myRepresentation;
    protected final ExprAST myConvention;
    protected final ExprAST myCorrespondence;
    protected final TypeGraph myTypeGraph;

    public RepresentationTypeEntry(TypeGraph g, String name,
            ResolveAST definingElement, ModuleIdentifier sourceModule,
            ProgramTypeDefinitionEntry definition, PTType representation,
            ExprAST convention, ExprAST correspondence) {
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
    public RepresentationTypeEntry toRepresentationTypeEntry(Token l) {
        return this;
    }

    @Override
    public ProgramTypeEntry toProgramTypeEntry(Token l) {
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
