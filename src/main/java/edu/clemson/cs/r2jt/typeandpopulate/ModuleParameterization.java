/**
 * ModuleParameterization.java
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
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.absyn.EnhancementModuleDec;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ModuleArgumentItem;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry.ParameterMode;
import edu.clemson.cs.r2jt.misc.Utils.Mapping3;
import edu.clemson.cs.r2jt.misc.RCollections;

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
                Map<String, PTType> genericInstantiations;

                // YS Edits
                // If the scope we are looking at is a enhancement module dec,
                // then we will need to obtain the generic instantiations
                // from our concept module dec.
                //
                // Ex: Facility Foo_Fac is Alpha_Template(Integer)
                //           realized by ...
                //           enhanced by Beta_Capability ...
                //
                // The instantiation of the type will be Integer and
                // we need that information for our searchers to work.
                if (originalScope.getDefiningElement() instanceof EnhancementModuleDec) {
                    ModuleParameterization conceptParameterization =
                            myInstantiatingFacility.getFacility()
                                    .getSpecification();
                    ModuleIdentifier conceptID =
                            conceptParameterization.getModuleIdentifier();
                    ModuleScope conceptScope =
                            mySourceRepository.getModuleScope(conceptID);
                    genericInstantiations =
                            getGenericInstantiations(conceptScope,
                                    conceptParameterization.getParameters());
                }
                else {
                    genericInstantiations =
                            getGenericInstantiations(originalScope,
                                    myParameters);
                }

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

    private Map<String, PTType> getGenericInstantiations(
            ModuleScope moduleScope, List<ModuleArgumentItem> parameters) {

        Map<String, PTType> result = new HashMap<String, PTType>();

        List<ProgramParameterEntry> formalParams =
                moduleScope.getFormalParameterEntries();

        result =
                RCollections.foldr2(formalParams, parameters,
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
