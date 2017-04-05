/*
 * PTFacilityRepresentation.java
 * ---------------------------------
 * Copyright (c) 2017
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
 * <p>A <code>PTFacilityRepresentation</code> is similar to a {@link PTRepresentation},
 * but doesn't implement a {@link PTFamily}. An instance of <code>PTFacilityRepresentation</code>
 * is thus a special case of its wrapped type that happens to be functioning as a
 * representation type.</p>
 *
 * @version 2.0
 */
public class PTFacilityRepresentation extends PTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The program type (facility instantiated type or a record)
     * that is used to implement this type representation.</p>
     */
    private final PTInstantiated myBaseType;

    /**
     * <p>Since facility representation types do not have a corresponding
     * <code>PTFamily</code>, we just store the name they go by here.</p>
     */
    private final String myName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a program type that is a facility type realization.</p>
     *
     * @param g The current type graph.
     * @param baseType The program type that was used to realize
     *                 this type. (Note: Can only be a facility
     *                 instantiated type or a record).
     * @param typeName This type's name.
     */
    public PTFacilityRepresentation(TypeGraph g, PTInstantiated baseType,
            String typeName) {
        super(g);
        myBaseType = baseType;
        myName = typeName;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the program type that was used to implement
     * this type.</p>
     *
     * @return A {@link PTInstantiated} representation object.
     */
    public final PTInstantiated getBaseType() {
        return myBaseType;
    }

    /**
     * <p>This method returns the name associated with this type.</p>
     *
     * @return A string.
     */
    public final String getName() {
        return myName;
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
    @Override
    public final PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        throw new UnsupportedOperationException(this.getClass() + " cannot "
                + "be instantiated.");
    }

    /**
     * <p>This method returns the mathematical type associated with this program type.</p>
     *
     * @return A {@link MTType} representation object.
     */
    @Override
    public final MTType toMath() {
        return myBaseType.toMath();
    }

}