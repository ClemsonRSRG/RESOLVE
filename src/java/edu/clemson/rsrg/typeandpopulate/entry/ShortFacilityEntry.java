/*
 * ShortFacilityEntry.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.entry;

import edu.clemson.rsrg.absyn.declarations.moduledecl.ShortFacilityModuleDec;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import java.util.Map;

/**
 * <p>
 * This creates a symbol table entry for a short facility module that only contains a facility declaration.
 * </p>
 *
 * @version 2.0
 */
public class ShortFacilityEntry extends ModuleEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Facility entry located inside this entry.
     * </p>
     */
    private final FacilityEntry myEnclosedFacility;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a {@link ShortFacilityModuleDec}.
     * </p>
     *
     * @param name
     *            Name associated with this entry.
     * @param definingElement
     *            The element that created this entry.
     * @param enclosedFacility
     *            The facility entry located inside this entry.
     */
    public ShortFacilityEntry(String name, ShortFacilityModuleDec definingElement, FacilityEntry enclosedFacility) {
        super(name, definingElement);
        myEnclosedFacility = enclosedFacility;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * A short facility module contains exactly one facility declaration. This method returns the entry corresponding to
     * that declaration.
     * </p>
     *
     * @return The entry corresponding to the single facility enclosed in the short facility module.
     */
    public final FacilityEntry getEnclosedFacility() {
        return myEnclosedFacility;
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
        return "a short facility module";
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
     * @return A {@link SymbolTableEntry} that has been instantiated.
     */
    @Override
    public final ShortFacilityEntry instantiateGenerics(Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        return new ShortFacilityEntry(getName(), (ShortFacilityModuleDec) getDefiningElement(),
                myEnclosedFacility.instantiateGenerics(genericInstantiations, instantiatingFacility));
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a {@link ShortFacilityEntry}.
     * </p>
     *
     * @param l
     *            Location where we encountered this entry.
     *
     * @return A {@link ShortFacilityEntry} if possible. Otherwise, it throws a {@link SourceErrorException}.
     */
    @Override
    public final ShortFacilityEntry toShortFacilityEntry(Location l) {
        return this;
    }

}
