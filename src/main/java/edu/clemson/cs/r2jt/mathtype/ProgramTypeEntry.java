package edu.clemson.cs.r2jt.mathtype;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
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
