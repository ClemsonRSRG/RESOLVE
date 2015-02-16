package edu.clemson.cs.r2jt.typeandpopulate2.entry;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;

import java.util.Map;

public class FacilityEntry extends SymbolTableEntry{
    public FacilityEntry(String name, ResolveAST definingElement,
                         ModuleIdentifier sourceModule) {
        super(name, definingElement, sourceModule);
    }

    @Override
    public String getEntryTypeDescription() {
        return null;
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        return null;
    }
}
