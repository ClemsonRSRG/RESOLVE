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
    private final ProgramQualifiedEntry myQualifiedAlterEgo;

    public ProgramVariableEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, String typeSpec, PTType type,
            String typeQualifier) {
        super(name, definingElement, sourceModule);
        myName = name;
        myType = type;

        //TODO: Probably need to recajigger this to correctly account for any
        //      generics in the defining context
        myMathSymbolAlterEgo =
                new MathSymbolEntry(type.getTypeGraph(), name,
                        Quantification.NONE, definingElement, type.toMath(),
                        null, null, null, sourceModule);

        myQualifiedAlterEgo =
                new ProgramQualifiedEntry(name, getDefiningElement(),
                        getSourceModuleIdentifier(), typeSpec, typeQualifier,
                        type);

    }

    public PTType getProgramType() {
        return myType;
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
                            getSourceModuleIdentifier(), getName(),
                            instantiatedType, getName());
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

    public ProgramQualifiedEntry toProgramQualifiedEntry(Location l) {
        return myQualifiedAlterEgo;
    }
}
