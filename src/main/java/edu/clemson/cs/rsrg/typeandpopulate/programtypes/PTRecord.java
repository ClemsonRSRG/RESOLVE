/*
 * PTRecord.java
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
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTCartesian;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTCartesian.Element;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * A {@code PTRecord} is record of variables with {@link PTInstantiated} types
 * or from
 * {@link PTGeneric} types from the specification field that is used to
 * implement a
 * {@link PTFamily}. The record itself should be a {@link PTInstantiated}
 * object.
 * </p>
 *
 * @version 2.0
 */
public class PTRecord extends PTInstantiated {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A map of variable names and program types.
     * </p>
     */
    private final Map<String, PTType> myFields = new HashMap<>();

    /**
     * <p>
     * The mathematical type corresponding to this record type.
     * </p>
     */
    private final MTType myMathTypeAlterEgo;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a record programming type.
     * </p>
     *
     * @param g The current type graph.
     * @param types A map of all programming types in this record.
     */
    public PTRecord(TypeGraph g, Map<String, PTType> types) {
        super(g);

        myFields.putAll(types);

        Element[] elements = new Element[types.size()];
        int index = 0;
        for (Map.Entry<String, PTType> field : types.entrySet()) {
            elements[index] =
                    new Element(field.getKey(), field.getValue().toMath());
            index++;
        }
        myMathTypeAlterEgo = new MTCartesian(g, elements);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the {@link PTType} associated with the name.
     * </p>
     *
     * @param name Name of a field variable.
     *
     * @return The {@link PTType} for {@code name}.
     */
    public final PTType getFieldType(String name) {
        return myFields.get(name);
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

        Map<String, PTType> newFields = new HashMap<>();
        for (Map.Entry<String, PTType> type : myFields.entrySet()) {
            newFields.put(type.getKey(), type.getValue().instantiateGenerics(
                    genericInstantiations, instantiatingFacility));
        }

        return new PTRecord(getTypeGraph(), newFields);
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
    public final MTType toMath() {
        return myMathTypeAlterEgo;
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
        return "Record " + myFields;
    }

}
