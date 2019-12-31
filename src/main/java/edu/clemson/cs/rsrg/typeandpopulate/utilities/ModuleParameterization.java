/*
 * ModuleParameterization.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.utilities;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.cs.rsrg.absyn.items.programitems.ModuleArgumentItem;
import edu.clemson.cs.rsrg.misc.RCollections;
import edu.clemson.cs.rsrg.misc.Utilities;
import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeRepository;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.InstantiatedScope;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import java.util.*;

/**
 * <p>
 * A <code>ModuleParameterization</code> is part of a facility declaration where
 * it is using a spec
 * ({@link ConceptModuleDec}/{@link EnhancementModuleDec}) or a realization
 * ({@link ConceptRealizModuleDec}/{@link EnhancementRealizModuleDec}).
 * </p>
 *
 * @version 2.0
 */
public class ModuleParameterization {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The scope that this instantiation is happening in.
     * </p>
     */
    private final ScopeRepository mySourceRepository;

    /**
     * <p>
     * The module that is being instantiated.
     * </p>
     */
    private final ModuleIdentifier myModule;

    /**
     * <p>
     * The list of parameters needed to instantiate this module.
     * </p>
     */
    private final List<ModuleArgumentItem> myParameters = new LinkedList<>();

    /**
     * <p>
     * The facility that is instantiating the module.
     * </p>
     */
    private final FacilityEntry myInstantiatingFacility;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a module parameter object with no arguments.
     * </p>
     *
     * @param module The module that is being instantiated.
     * @param instantiatingFacility The facility that is instantiating the
     *        module.
     * @param sourceRepository The scope where this instantiation is happening
     *        in.
     */
    public ModuleParameterization(ModuleIdentifier module,
            FacilityEntry instantiatingFacility,
            ScopeRepository sourceRepository) {
        this(module, new LinkedList<ModuleArgumentItem>(),
                instantiatingFacility, sourceRepository);
    }

    /**
     * <p>
     * This constructs a module parameter object.
     * </p>
     *
     * @param module The module that is being instantiated.
     * @param parameters The list of parameters needed to instantiate this
     *        module.
     * @param instantiatingFacility The facility that is instantiating the
     *        module.
     * @param sourceRepository The scope where this instantiation is happening
     *        in.
     */
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

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the module identifier for the module that this object
     * refers to.
     * </p>
     *
     * @return A {@link ModuleIdentifier} representation object.
     */
    public final ModuleIdentifier getModuleIdentifier() {
        return myModule;
    }

    /**
     * <p>
     * This method returns the module arguments used to instantiate this object.
     * </p>
     *
     * @return A unmodifiable list of {@link ModuleArgumentItem}s.
     */
    public final List<ModuleArgumentItem> getParameters() {
        return Collections.unmodifiableList(myParameters);
    }

    /**
     * <p>
     * This method returns the scope for this module parameter object.
     * </p>
     *
     * @param instantiated Boolean flag to indicate whether or not this object
     *        has been instantiated.
     *
     * @return An {@link InstantiatedScope} with either the generic parameters
     *         or instantiated version
     *         of the parameters.
     */
    public final Scope getScope(boolean instantiated) {
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
                // realized by ...
                // enhanced by Beta_Capability ...
                //
                // The instantiation of the type will be Integer and
                // we need that information for our searchers to work.
                if (originalScope
                        .getDefiningElement() instanceof EnhancementModuleDec) {
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
                    genericInstantiations = getGenericInstantiations(
                            originalScope, myParameters);
                }

                result = new InstantiatedScope(originalScope,
                        genericInstantiations, myInstantiatingFacility);
            }
        }
        catch (NoSuchSymbolException nsse) {
            // Shouldn't be possible--we'd have caught it by now
            throw new RuntimeException(nsse);
        }

        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This obtains the generic instantiations for the generic module arguments
     * from the module scope.
     * </p>
     *
     * @param moduleScope The scope whose parameters we wish to instantiate.
     * @param parameters The list of actual parameter instantiations.
     *
     * @return A map containing {@link PTType}s that can are necessary to
     *         instantiate this module.
     */
    private Map<String, PTType> getGenericInstantiations(
            ModuleScope moduleScope, List<ModuleArgumentItem> parameters) {
        Map<String, PTType> result = new HashMap<>();

        List<ProgramParameterEntry> formalParams =
                moduleScope.getFormalParameterEntries();

        result = RCollections.foldr2(formalParams, parameters,
                BuildGenericInstantiations.INSTANCE, result);

        return result;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * This is a helper class that builds a three parameter mapping for generic
     * instantiations.
     * </p>
     */
    private static class BuildGenericInstantiations
            implements
                Utilities.Mapping3<ProgramParameterEntry, ModuleArgumentItem, Map<String, PTType>, Map<String, PTType>> {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * A singleton instance that builds the mapping.
         * </p>
         */
        public static final BuildGenericInstantiations INSTANCE =
                new BuildGenericInstantiations();

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * This method creates a map of program types that has been instantiated
         * by facility
         * declaration's module arguments.
         * </p>
         *
         * @param p1 A generic {@link ProgramParameterEntry}.
         * @param p2 The module argument that instantiates {@code p1}.
         * @param p3 The {@link PTType}s used to instantiate {@code p1}.
         *
         * @return The instantiated {@link ProgramParameterEntry}.
         */
        @Override
        public final Map<String, PTType> map(ProgramParameterEntry p1,
                ModuleArgumentItem p2, Map<String, PTType> p3) {
            if (p1.getParameterMode()
                    .equals(ProgramParameterEntry.ParameterMode.TYPE)) {
                if (p2.getProgramTypeValue() == null) {
                    // Should have caught this before now!
                    throw new RuntimeException("null program type");
                }

                p3.put(p1.getName(), p2.getProgramTypeValue());
            }

            return p3;
        }

    }

}
