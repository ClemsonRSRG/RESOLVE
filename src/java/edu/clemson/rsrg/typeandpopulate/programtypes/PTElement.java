/*
 * PTElement.java
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
package edu.clemson.rsrg.typeandpopulate.programtypes;

import edu.clemson.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.Map;

/**
 * <p>
 * The program-type corresponding to {@link TypeGraph#ELEMENT}, i.e., the type of all program types.
 * </p>
 *
 * @version 2.0
 */
public class PTElement extends PTType {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a programming type corresponding to {@link TypeGraph#ELEMENT}.
     * </p>
     *
     * @param g
     *            The current type graph.
     */
    public PTElement(TypeGraph g) {
        super(g);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

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
        return myTypeGraph.ELEMENT;
    }

}
