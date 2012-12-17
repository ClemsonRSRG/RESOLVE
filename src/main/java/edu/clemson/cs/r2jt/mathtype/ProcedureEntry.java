package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import java.util.Map;

public class ProcedureEntry extends SymbolTableEntry {

    private final OperationEntry myCorrespondingOperation;

    public ProcedureEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, OperationEntry correspondingOperation) {
        super(name, definingElement, sourceModule);

        myCorrespondingOperation = correspondingOperation;
    }

    public OperationEntry getCorrespondingOperation() {
        return myCorrespondingOperation;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a procedure";
    }

    @Override
    public ProcedureEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        return new ProcedureEntry(getName(), getDefiningElement(),
                getSourceModuleIdentifier(), myCorrespondingOperation
                        .instantiateGenerics(genericInstantiations,
                                instantiatingFacility));
    }

    @Override
    public ProcedureEntry toProcedureEntry(Location l) {
        return this;
    }
}
