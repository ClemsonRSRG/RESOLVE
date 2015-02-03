/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
