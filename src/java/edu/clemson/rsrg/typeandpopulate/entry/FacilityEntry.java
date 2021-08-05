/*
 * FacilityEntry.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.entry;

import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.items.programitems.EnhancementSpecItem;
import edu.clemson.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.symboltables.ScopeRepository;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleParameterization;
import edu.clemson.rsrg.typeandpopulate.utilities.SpecRealizationPairing;
import java.util.*;

/**
 * <p>
 * This creates a symbol table entry for a facility declaration.
 * </p>
 *
 * @version 2.0
 */
public class FacilityEntry extends SymbolTableEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A flag that indicates whether or not this entry instantiates a {@code sharing concept}.
     * </p>
     */
    private final boolean myIsSharingConceptInstantiation;

    /**
     * <p>
     * The module specification/realization that this facility entry is instantiating.
     * </p>
     */
    private final SpecRealizationPairing myType;

    /**
     * <p>
     * The list of enhancements in this facility entry.
     * </p>
     */
    private final List<ModuleParameterization> myEnhancements = new LinkedList<>();

    /**
     * <p>
     * The scope where this instantiation is happening in.
     * </p>
     */
    private final ScopeRepository mySourceRepository;

    /**
     * <p>
     * The map that indicates where each spec is being realized.
     * </p>
     */
    private final Map<ModuleParameterization, ModuleParameterization> myEnhancementRealizations = new HashMap<>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a facility declaration.
     * </p>
     *
     * @param facility
     *            The element that created this entry.
     * @param isSharingConceptInstantiation
     *            This flag indicates whether or not this {@code facility} is an instantiation of a
     *            {@code sharing concept}.
     * @param sourceModule
     *            The module where this entry was created from.
     * @param sourceRepository
     *            The scope where this instantiation is happening in.
     */
    public FacilityEntry(FacilityDec facility, boolean isSharingConceptInstantiation, ModuleIdentifier sourceModule,
            ScopeRepository sourceRepository) {
        super(facility.getName().getName(), facility, sourceModule);

        myIsSharingConceptInstantiation = isSharingConceptInstantiation;
        mySourceRepository = sourceRepository;

        ModuleParameterization spec = new ModuleParameterization(
                new ModuleIdentifier(facility.getConceptName().getName()), facility.getConceptParams(), this,
                mySourceRepository);

        ModuleParameterization realization = null;
        if (facility.getConceptRealizName() != null) {
            realization = new ModuleParameterization(new ModuleIdentifier(facility.getConceptRealizName().getName()),
                    facility.getConceptParams(), this, mySourceRepository);
        }

        myType = new SpecRealizationPairing(facility.getLocation(), spec, realization);

        // These are realized by the concept realization
        for (EnhancementSpecItem realizationEnhancement : facility.getEnhancements()) {

            spec = new ModuleParameterization(new ModuleIdentifier(realizationEnhancement.getName().getName()),
                    realizationEnhancement.getParams(), this, mySourceRepository);

            myEnhancements.add(spec);
            myEnhancementRealizations.put(spec, realization);
        }

        // These are realized by individual enhancement realizations
        for (EnhancementSpecRealizItem enhancement : facility.getEnhancementRealizPairs()) {
            spec = new ModuleParameterization(new ModuleIdentifier(enhancement.getEnhancementName().getName()),
                    enhancement.getEnhancementParams(), this, mySourceRepository);
            realization = new ModuleParameterization(
                    new ModuleIdentifier(enhancement.getEnhancementRealizName().getName()),
                    enhancement.getEnhancementRealizParams(), this, mySourceRepository);

            myEnhancements.add(spec);
            myEnhancementRealizations.put(spec, realization);
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns all the enhancements declared in this facility entry.
     * </p>
     *
     * @return A list of enhancement {@link ModuleParameterization} objects.
     */
    public final List<ModuleParameterization> getEnhancements() {
        return Collections.unmodifiableList(myEnhancements);
    }

    /**
     * <p>
     * Do not assume that each enhancement is realized by a corresponding, individual realization. Some enhancements may
     * be realized by the base concept, in which case this method will return that module.
     * </p>
     *
     * <p>
     * For example, <code>Stack_Template</code> might have an enhancement <code>Get_Nth_Ability</code>, which replicates
     * and returns the <em>n</em>th element from the top of the stack. There may be an
     * <code>Obvious_Get_Nth_Realization</code> that does what you'd expect: repeatedly pop some elements off the top to
     * get to the requested element, replicate it, then push everything back on. Using this enhancement would be a fine
     * choice for extending <code>Stack_Template</code> as realized by <code>Pointer_Realization</code>. However, if we
     * happen to know that we're using <code>Array_Based_Realization</code>, we can do much better. For this purpose,
     * <code>Array_Based_Realization</code> can directly incorporate enhancements and provide a direct realization that
     * takes advantage of implementation details. I.e., <code>Get_Nth()</code> would be included as a procedure inside
     * <code>Array_Based_Realization</code>. In such a case, we would declare a facility like this:
     * </p>
     *
     * <pre>
     * Facility Indexible_Stack is Stack_Template enhanced with Get_Nth_Ability
     *     realized by Array_Based_Realization;
     * </pre>
     *
     * <p>
     * And, calling this method to ask the realization for <code>Get_Nth_Ability</code> would return
     * <code>Array_Based_Realization</code>.
     * </p>
     *
     * @param enhancement
     *            The enhancement that we are searching for its realization.
     *
     * @return The module that is realizing the specified enhancement.
     */
    public final ModuleParameterization getEnhancementRealization(ModuleParameterization enhancement) {
        return myEnhancementRealizations.get(enhancement);
    }

    /**
     * <p>
     * This method returns a description associated with this entry.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getEntryTypeDescription() {
        return "a facility";
    }

    /**
     * <p>
     * This returns the module specification and realization pairing for this facility entry.
     * </p>
     *
     * @return A {@link SpecRealizationPairing} representation object.
     */
    public final SpecRealizationPairing getFacility() {
        return myType;
    }

    /**
     * <p>
     * This method converts a generic {@link SymbolTableEntry} to an entry that has all the generic types and variables
     * replaced with actual values.
     * </p>
     *
     * @param genericInstantiations
     *            Map containing all the instantiations.
     * @param instantiatingFacility
     *            Facility that instantiated this type.
     *
     * @return A {@link FacilityEntry} that has been instantiated.
     */
    @Override
    public final FacilityEntry instantiateGenerics(Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        // TODO : This is probably wrong. One of the parameters to a module
        // used in the facility could be a generic, in which case it
        // should be replaced with the corresponding concrete type--but
        // how?
        return this;
    }

    /**
     * <p>
     * This method returns a boolean that indicates whether or not this entry instantiates a {@code sharing concept}.
     * </p>
     *
     * @return {@code true} if it instantiates a {@code sharing concept}, {@code false} otherwise.
     */
    public final boolean isSharingConceptInstantion() {
        return myIsSharingConceptInstantiation;
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a {@link FacilityEntry}.
     * </p>
     *
     * @param l
     *            Location where we encountered this entry.
     *
     * @return A {@link FacilityEntry} if possible. Otherwise, it throws a {@link SourceErrorException}.
     */
    @Override
    public final FacilityEntry toFacilityEntry(Location l) {
        return this;
    }

}
