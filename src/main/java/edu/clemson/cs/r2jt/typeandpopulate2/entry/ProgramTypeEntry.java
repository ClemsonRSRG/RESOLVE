/**
 * ProgramTypeEntry.java
 * ---------------------------------
 * Copyright (c) 2015
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
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.VariableReplacingVisitor;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import org.antlr.v4.runtime.Token;

import java.util.Map;

public class ProgramTypeEntry extends AbstractProgramEntry {

    private final MTType myModelType;
    private final PTType myProgramType;

    /**
     * <p>A program type can masquerade as a math type.  This will represent the
     * (non-existent) symbol table entry for the "program type" when viewed as
     * a math type.</p>
     */
    private final MathSymbolEntry myMathTypeAlterEgo;

    public ProgramTypeEntry(TypeGraph g, String name,
            ResolveAST definingElement, ModuleIdentifier sourceModule,
            MTType modelType, PTType programType) {
        super(name, definingElement, sourceModule);

        myModelType = modelType;

        //TODO: Probably need to recajigger this to correctly account for any
        //      generics in the defining context
        myMathTypeAlterEgo =
                new MathSymbolEntry(g, name, Quantification.NONE,
                        definingElement, g.CLS, modelType, null, null,
                        sourceModule);
        myProgramType = programType;
    }

    public MTType getModelType() {
        return myModelType;
    }

    @Override
    public PTType getProgramType() {
        return myProgramType;
    }

    @Override
    public ProgramTypeEntry toProgramTypeEntry(Token l) {
        return this;
    }

    @Override
    public MathSymbolEntry toMathSymbolEntry(Token l) {
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
