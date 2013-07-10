package edu.clemson.cs.r2jt.typeandpopulate.entry;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;

public class ProgramVariableEntry extends SymbolTableEntry {

    private String myName;
    private String myTypeQualifier;
    private String myTypeSpecification;

    private final PTType myType;
    private final MathSymbolEntry myMathSymbolAlterEgo;

    public ProgramVariableEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType type, String typeQualifier,
            String spec) {
        super(name, definingElement, sourceModule);
        myName = name;
        myType = type;
        myTypeQualifier = typeQualifier;
        myTypeSpecification = spec;

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

    public String getTypeQualifier() {
        return myTypeQualifier;
    }

    public String getTypeSpecification() {
        return myTypeSpecification;
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
                            getSourceModuleIdentifier(), instantiatedType,
                            getName(), getName());
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
