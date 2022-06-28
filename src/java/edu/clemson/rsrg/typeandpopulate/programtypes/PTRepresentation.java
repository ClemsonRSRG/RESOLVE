/*
 * PTRepresentation.java
 * ---------------------------------
 * Copyright (c) 2022
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
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.entry.TypeFamilyEntry;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.Map;

/**
 * <p>
 * A <code>PTRepresentation</code> wraps an existing {@link PTType} with additional information about a {@link PTFamily}
 * this type represents. An instance of <code>PTRepresentation</code> is thus a special case of its wrapped type that
 * happens to be functioning as a representation type.
 * </p>
 *
 * @version 2.0
 */
public class PTRepresentation extends PTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The program type (facility instantiated type or a record) that is used to implement this type representation.
     * </p>
     */
    private final PTInstantiated myBaseType;

    /**
     * <p>
     * The entry for the type family.
     * </p>
     */
    private final TypeFamilyEntry myFamily;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a program type that is a type realization for a type family.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param baseType
     *            The program type that was used to realize this type. (Note: Can only be a facility instantiated type
     *            or a record).
     * @param family
     *            The entry for the type family.
     */
    public PTRepresentation(TypeGraph g, PTInstantiated baseType, TypeFamilyEntry family) {
        super(g);
        myBaseType = baseType;
        myFamily = family;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns {@code true} <strong>iff</strong> an value of this type would be acceptable where one of type
     * {@code t} were required.
     * </p>
     *
     * @param t
     *            The required type.
     *
     * @return {@code true} <strong>iff</strong> an value of this type would be acceptable where one of type {@code t}
     *         were required, {@code false} otherwise.
     */
    @Override
    public final boolean acceptableFor(PTType t) {
        boolean result = super.acceptableFor(t);

        if (!result) {
            result = myFamily.getProgramType().acceptableFor(t);
        }

        return result;
    }

    /**
     * <p>
     * This method returns the program type that was used to implement this type.
     * </p>
     *
     * @return A {@link PTInstantiated} representation object.
     */
    public final PTInstantiated getBaseType() {
        return myBaseType;
    }

    /**
     * <p>
     * This method returns the {@link SymbolTableEntry} that corresponding to the type family.
     * </p>
     *
     * @return A {@link TypeFamilyEntry} representation object.
     */
    public final TypeFamilyEntry getFamily() {
        return myFamily;
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
        throw new UnsupportedOperationException(this.getClass() + " cannot " + "be instantiated.");
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
        return myBaseType.toMath();
    }

    /**
     * <p>
     * This method returns the object in string format.
     * </p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return myFamily.getName() + " as " + myBaseType;
    }

}
