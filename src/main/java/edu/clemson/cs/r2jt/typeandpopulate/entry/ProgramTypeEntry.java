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

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.VariableReplacingVisitor;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public class ProgramTypeEntry extends SymbolTableEntry {

    private final MTType myModelType;
    private final PTType myProgramType;

    /**
     * <p>A program type can masquerade as a math type.  This will represent the
     * (non-existent) symbol table entry for the "program type" when viewed as
     * a math type.</p>
     */
    private final MathSymbolEntry myMathTypeAlterEgo;

    public ProgramTypeEntry(TypeGraph g, String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, MTType modelType, PTType programType) {
        super(name, definingElement, sourceModule);

        myModelType = modelType;

        //TODO: Probably need to recajigger this to correctly account for any
        //      generics in the defining context
        myMathTypeAlterEgo =
                new MathSymbolEntry(g, name,
                        SymbolTableEntry.Quantification.NONE, definingElement,
                        g.MTYPE, modelType, null, null, sourceModule);
        myProgramType = programType;
    }

    public MTType getModelType() {
        return myModelType;
    }

    public PTType getProgramType() {
        return myProgramType;
    }

    @Override
    public ProgramTypeEntry toProgramTypeEntry(Location l) {
        return this;
    }

    @Override
    public MathSymbolEntry toMathSymbolEntry(Location l) {
        return myMathTypeAlterEgo;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a program type.";
    }

    @Override
    public ProgramTypeEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        Map<String, MTType> genericMathematicalInstantiations =
                SymbolTableEntry.buildMathTypeGenerics(genericInstantiations);

        VariableReplacingVisitor typeSubstitutor =
                new VariableReplacingVisitor(genericMathematicalInstantiations);
        myModelType.accept(typeSubstitutor);

        return new ProgramTypeEntry(myModelType.getTypeGraph(), getName(),
                getDefiningElement(), getSourceModuleIdentifier(),
                typeSubstitutor.getFinalExpression(), myProgramType
                        .instantiateGenerics(genericInstantiations,
                                instantiatingFacility));
    }

}
