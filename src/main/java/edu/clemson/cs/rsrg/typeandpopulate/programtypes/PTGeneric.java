/*
 * PTGeneric.java
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
package edu.clemson.cs.rsrg.typeandpopulate.programtypes;

import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTNamed;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.Map;

/**
 * <p>
 * This class creates a generic type that hasn't been instantiated.
 * </p>
 *
 * @version 2.0
 */
public class PTGeneric extends PTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Name associated with this type.
     * </p>
     */
    private final String myName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a generic programming type.
     * </p>
     *
     * @param g The current type graph.
     * @param name Name associated with this type.
     */
    public PTGeneric(TypeGraph g, String name) {
        super(g);
        myName = name;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method overrides the default equals method implementation.
     * </p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false}
     *         otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        boolean result = (o instanceof PTGeneric);

        if (result) {
            PTGeneric oAsPTGeneric = (PTGeneric) o;

            result = myName.equals(oAsPTGeneric.getName());
        }

        return result;
    }

    /**
     * <p>
     * This method returns the name associated with this type.
     * </p>
     *
     * @return A string.
     */
    public final String getName() {
        return myName;
    }

    /**
     * <p>
     * This method converts a generic {@link PTType} to a program type that has
     * all the generic types
     * and variables replaced with actual values.
     * </p>
     *
     * @param genericInstantiations Map containing all the instantiations.
     * @param instantiatingFacility Facility that instantiated this type.
     *
     * @return A {@link PTType} that has been instantiated.
     */
    @Override
    public final PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        PTType result = this;

        if (genericInstantiations.containsKey(myName)) {
            result = genericInstantiations.get(myName);
        }

        return result;
    }

    /**
     * <p>
     * This method returns the mathematical type associated with this program
     * type.
     * </p>
     *
     * @return A {@link MTType} representation object.
     */
    @Override
    public MTType toMath() {
        return new MTNamed(myTypeGraph, myName);
    }

}
