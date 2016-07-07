/**
 * PTType.java
 * ---------------------------------
 * Copyright (c) 2016
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
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.Map;

/**
 * <p>This abstract class serves as the parent class of all
 * program types.</p>
 *
 * @version 2.0
 */
public abstract class PTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The current type graph object in use.</p> */
    protected final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the type graph
     * for any objects created from a class that inherits from
     * {@code PTType}.</p>
     *
     * @param g The current type graph.
     */
    protected PTType(TypeGraph g) {
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns {@code true} <strong>iff</strong> an value of this type
     * would be acceptable where one of type {@code t} were required.</p>
     *
     * @param t The required type.
     *
     * @return {@code true} <strong>iff</strong> an value of this type
     *         would be acceptable where one of type {@code t} were
     *         required, {@code false} otherwise.
     */
    public boolean acceptableFor(PTType t) {
        return equals(t);
    }

    /**
     * <p>The type graph containing all the type relationships.</p>
     *
     * @return The type graph for the compiler.
     */
    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    /**
     * <p>This method converts a generic {@link PTType} to a program type
     * that has all the generic types and variables replaced with actual
     * values.</p>
     *
     * @param genericInstantiations Map containing all the instantiations.
     * @param instantiatingFacility Facility that instantiated this type.
     *
     * @return A {@link PTType} that has been instantiated.
     */
    public abstract PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility);

    /**
     * <p>This method returns the mathematical type associated with this program type.</p>
     *
     * @return A {@link MTType} representation object.
     */
    public abstract MTType toMath();

}