package edu.clemson.cs.r2jt.mathtype;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ModuleArgumentItem;
import edu.clemson.cs.r2jt.mathtype.ProgramParameterEntry.ParameterMode;
import edu.clemson.cs.r2jt.utilities.Mapping3;
import edu.clemson.cs.r2jt.utilities.RCollections;

public class ModuleParameterization {

    private final ScopeRepository mySourceRepository;
    private final ModuleIdentifier myModule;
    private final List<ModuleArgumentItem> myParameters =
            new LinkedList<ModuleArgumentItem>();

    private final FacilityEntry myInstantiatingFacility;

    public ModuleParameterization(ModuleIdentifier module,
            FacilityEntry instantiatingFacility,
            ScopeRepository sourceRepository) {
        this(module, new LinkedList<ModuleArgumentItem>(),
                instantiatingFacility, sourceRepository);
    }

    public ModuleParameterization(ModuleIdentifier module,
            List<ModuleArgumentItem> parameters,
            FacilityEntry instantiatingFacility,
            ScopeRepository sourceRepository) {

        myInstantiatingFacility = instantiatingFacility;
        mySourceRepository = sourceRepository;

        if (parameters != null) {
            myParameters.addAll(parameters);
        }

        myModule = module;
    }

    public ModuleIdentifier getModuleIdentifier() {
        return myModule;
    }

    public List<ModuleArgumentItem> getParameters() {
        return Collections.unmodifiableList(myParameters);
    }

    public Scope getScope(boolean instantiated) {

        Scope result;

        try {
            ModuleScope originalScope =
                    mySourceRepository.getModuleScope(myModule);
            result = originalScope;

            if (instantiated) {
                Map<String, PTType> genericInstantiations =
                        getGenericInstantiations(originalScope);

                result =
                        new InstantiatedScope(originalScope,
                                genericInstantiations, myInstantiatingFacility);
            }
        }
        catch (NoSuchSymbolException nsse) {
            //Shouldn't be possible--we'd have caught it by now
            throw new RuntimeException(nsse);
        }

        return result;
    }

    private Map<String, PTType> getGenericInstantiations(ModuleScope moduleScope) {

        Map<String, PTType> result = new HashMap<String, PTType>();

        List<ProgramParameterEntry> formalParams =
                moduleScope.getFormalParameterEntries();

        result =
                RCollections.foldr2(formalParams, myParameters,
                        BuildGenericInstantiations.INSTANCE, result);

        return result;
    }

    private static class BuildGenericInstantiations
            implements
                Mapping3<ProgramParameterEntry, ModuleArgumentItem, Map<String, PTType>, Map<String, PTType>> {

        public static final BuildGenericInstantiations INSTANCE =
                new BuildGenericInstantiations();

        @Override
        public Map<String, PTType> map(ProgramParameterEntry p1,
                ModuleArgumentItem p2, Map<String, PTType> p3) {

            if (p1.getParameterMode().equals(ParameterMode.TYPE)) {
                if (p2.getProgramTypeValue() == null) {
                    //Should have caught this before now!
                    throw new RuntimeException("null program type");
                }

                p3.put(p1.getName(), p2.getProgramTypeValue());
            }

            return p3;
        }

    }
}
