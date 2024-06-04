/*
 * PTVoid.java
 * ---------------------------------
 * Copyright (c) 2024
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
import java.util.WeakHashMap;

/**
 * <p>
 * Since we are trying to give everything a type, there are operations that simply don't return anything and we would
 * still like to give it a type. This class creates a generic {code Void} type.
 * </p>
 *
 * @version 2.0
 */
public class PTVoid extends PTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A map to store all the instances created by this class
     * </p>
     */
    private static WeakHashMap<TypeGraph, PTVoid> instances = new WeakHashMap<>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a programming {code Void} type.
     * </p>
     *
     * @param g
     *            The current type graph.
     */
    private PTVoid(TypeGraph g) {
        super(g);
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
        // We override this simply to show that we've given it some thought
        return super.equals(o);
    }

    /**
     * <p>
     * This method returns an instance of {@link PTVoid}.
     * </p>
     *
     * @param g
     *            The current type graph.
     *
     * @return A {@link PTVoid} object.
     */
    public static PTVoid getInstance(TypeGraph g) {
        PTVoid result = instances.get(g);

        if (result == null) {
            result = new PTVoid(g);
            instances.put(g, result);
        }

        return result;
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
        return myTypeGraph.VOID;
    }

}
