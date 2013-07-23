/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;

import java.util.Map;

/**
 *
 * @author Welch D
 */
public class ProgramQualifiedEntry extends SymbolTableEntry {

    private String myName;
    private String myQualifier;
    private String mySpecification;
    private final PTType myType;

    public ProgramQualifiedEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, String spec, String qualifier,
            PTType type) {
        super(name, definingElement, sourceModule);

        myName = name;
        myQualifier = qualifier;
        mySpecification = spec;
        myType = type;
    }

    public ProgramQualifiedEntry toProgramQualifiedSymbolEntry(Location l) {
        return this;
    }

    public PTType getProgramType() {
        return myType;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a qualified entry";
    }

    public String getQualifier() {
        return myQualifier;
    }

    public String getSpecification() {
        return mySpecification.toString();
    }

    // Still not sure what this is doing, for now I'll 
    // keep it mostly the same as 
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
                    new ProgramQualifiedEntry(getName(), getDefiningElement(),
                            getSourceModuleIdentifier(), getName(), getName(),
                            instantiatedType);
        }
        else {
            result = this;
        }

        return result;
        /*    SymbolTableEntry result;

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

            return result;*/
    }
}
