package edu.clemson.cs.r2jt.typeandpopulate.entry;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;

public class ProgramVariableEntry extends SymbolTableEntry {

    private String myName;
    private final PTType myType;
    private final ModuleIdentifier myTypeQualifierModule;
    private final MathSymbolEntry myMathSymbolAlterEgo;

    public ProgramVariableEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType type,
            ModuleIdentifier typeQualifierSrcModule) {
        super(name, definingElement, sourceModule);
        myName = name;
        myType = type;
        myTypeQualifierModule = typeQualifierSrcModule;

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

    // not sure if this is the best way to go about this but here goes.
    // translation really needs this kind of information. However, I'm
    // worried about the ModuleIdentifier class (see comment there)...

    // Returns a string of the program type qualified by its defining
    // module. (I.e., for "Integer", getFullyQualifiedVarType gives
    // "Integer_Template.Integer"
    public String getFullyQualifiedVarType() {
        return myTypeQualifierModule.fullyQualifiedRepresentation(myName);
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
                            getSourceModuleIdentifier());
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
