package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.proving.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.LazilyMappedImmutableList;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationEntry extends SymbolTableEntry {

	/**
	 * This flag is True <strong>iff</strong> this particular
	 * <code>operationEntry</code> is a formal parameter to a module.
	 */
	private boolean myModuleParameterFlag;
    private final PTType myReturnType;
    private final ImmutableList<ProgramParameterEntry> myParameters;

    public OperationEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType returnType,
            List<ProgramParameterEntry> parameters, boolean isParameter) {

        this(name, definingElement, sourceModule, returnType,
                new ArrayBackedImmutableList<ProgramParameterEntry>(parameters), isParameter);
    }

    public OperationEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType returnType,
            ImmutableList<ProgramParameterEntry> parameters, boolean isParameter) {

        super(name, definingElement, sourceModule);

        myParameters = parameters;
        myReturnType = returnType;
		myModuleParameterFlag = isParameter;
    }

    public OperationEntry toOperationEntry(Location l) {
        return this;
    }

    public ImmutableList<ProgramParameterEntry> getParameters() {
        return myParameters;
    }

	public boolean isModuleParameter() {
		return myModuleParameterFlag;
	}

    public PTType getReturnType() {
        return myReturnType;
    }

    @Override
    public String getEntryTypeDescription() {
        return "an operation";
    }

    @Override
    public OperationEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        return new OperationEntry(
                getName(),
                getDefiningElement(),
                getSourceModuleIdentifier(),
                myReturnType.instantiateGenerics(genericInstantiations,
                        instantiatingFacility),
                new LazilyMappedImmutableList<ProgramParameterEntry, ProgramParameterEntry>(
                        myParameters, new InstantiationMapping(
                                genericInstantiations, instantiatingFacility)), myModuleParameterFlag);
    }

    private static class InstantiationMapping
            implements
                Mapping<ProgramParameterEntry, ProgramParameterEntry> {

        private final Map<String, PTType> myGenericInstantiations;
        private final FacilityEntry myInstantiatingFacility;

        public InstantiationMapping(Map<String, PTType> instantiations,
                FacilityEntry instantiatingFacility) {
            myGenericInstantiations =
                    new HashMap<String, PTType>(instantiations);
            myInstantiatingFacility = instantiatingFacility;
        }

        @Override
        public ProgramParameterEntry map(ProgramParameterEntry input) {
            return (ProgramParameterEntry) input.instantiateGenerics(
                    myGenericInstantiations, myInstantiatingFacility);
        }

    }
}
