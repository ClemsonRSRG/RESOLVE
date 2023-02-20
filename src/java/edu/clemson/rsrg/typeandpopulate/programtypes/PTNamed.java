/*
 * PTNamed.java
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
package edu.clemson.rsrg.typeandpopulate.programtypes;

import edu.clemson.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.Map;

/**
 * <p>
 * A <code>PTNamed</code> represents a {@link PTFamily} that has been instantiated via a facility.
 * </p>
 *
 * <p>
 * Note that, while an instantiated type must have all parameters "filled in", it's possible that some have been filled
 * in with constant parameters or type parameters from the facility's source module.
 * </p>
 *
 * @version 2.0
 */
public class PTNamed extends PTInstantiated {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A pointer to the entry in the symbol table corresponding to the facility that instantiated this type.
     * </p>
     */
    private final FacilityEntry mySourceFacility;

    /**
     * <p>
     * The program type family with with the generics instantiated.
     * </p>
     */
    private final PTFamily myInstantiatedPTFamily;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a program type that represents a {@link PTFamily} that has been instantiated by a
     * {@link FacilityEntry}.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param facility
     *            The facility that instantiated this type.
     * @param instantiatedFamilyType
     *            The {@link PTFamily} with the generics instantiated.
     */
    public PTNamed(TypeGraph g, FacilityEntry facility, PTFamily instantiatedFamilyType) {
        super(g);
        mySourceFacility = facility;
        myInstantiatedPTFamily = instantiatedFamilyType;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method overrides the default equals method implementation.
     * </p>
     *
     * @param o
     *            Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        boolean result = (o instanceof PTNamed);

        if (result) {
            PTNamed oAsPTNamed = (PTNamed) o;

            result = (mySourceFacility.equals(oAsPTNamed.getInstantiatingFacility()))
                    && myInstantiatedPTFamily.equals(oAsPTNamed.getInstantiatedFamilyType());
        }

        return result;
    }

    /**
     * <p>
     * This method returns the facility used to instantiate this type.
     * </p>
     *
     * @return A {@link FacilityEntry} representation object.
     */
    public final FacilityEntry getInstantiatingFacility() {
        return mySourceFacility;
    }

    /**
     * <p>
     * This method returns the program type family this type.
     * </p>
     *
     * @return A {@link PTFamily} representation object.
     */
    public final PTFamily getInstantiatedFamilyType() {
        return myInstantiatedPTFamily;
    }

    /**
     * <p>
     * This method converts a generic {@link PTType} to a program type that has all the generic types and variables
     * replaced with actual values.
     * </p>
     *
     * @param genericInstantiations
     *            Map containing all the instantiations.
     * @param instantiatingFacility
     *            Facility that instantiated this type.
     *
     * @return A {@link PTType} that has been instantiated.
     */
    @Override
    public final PTType instantiateGenerics(Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        // I'm already instantiated!
        return this;
    }

    /**
     * <p>
     * This method returns the mathematical type associated with this program type.
     * </p>
     *
     * @return A {@link MTType} representation object.
     */
    @Override
    public final MTType toMath() {
        return myInstantiatedPTFamily.toMath();
    }

}
