/*
 * PTFamily.java
 * ---------------------------------
 * Copyright (c) 2018
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
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.Map;

/**
 * <p>Represents a <em>type family</em> as would be introduced inside a concept.
 * This is an abstract program type without a realization and without parameters
 * instantiated.</p>
 *
 * @version 2.0
 */
public class PTFamily extends PTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The type's mathematical model.</p> */
    private final MTType myModel;

    /** <p>Name associated with this type.</p> */
    private final String myName;

    /** <p>Exemplar associated with this type.</p> */
    private final String myExemplarName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a program type using a type family.</p>
     *
     * @param model The type family's mathematical model.
     * @param familyName The type family's name.
     * @param exemplarName The type family's exemplar name.
     */
    public PTFamily(MTType model, String familyName, String exemplarName) {
        super(model.getTypeGraph());

        myName = familyName;
        myModel = model;
        myExemplarName = exemplarName;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default equals method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        boolean result = (o instanceof PTFamily);

        if (result) {
            PTFamily oAsPTFamily = (PTFamily) o;

            result =
                    (myModel.equals(oAsPTFamily.myModel))
                            && (myName.equals(oAsPTFamily.myName))
                            && (myExemplarName
                                    .equals(oAsPTFamily.myExemplarName));
        }

        return result;
    }

    /**
     * <p>This method returns the exemplar name associated with this type.</p>
     *
     * @return A string.
     */
    public final String getExemplarName() {
        return myExemplarName;
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
        Map<String, MTType> stringToMathType =
                SymbolTableEntry.buildMathTypeGenerics(genericInstantiations);

        MTType newModel =
                myModel.getCopyWithVariablesSubstituted(stringToMathType);

        return new PTFamily(newModel, myName, myExemplarName);
    }

    /**
     * <p>This method returns the mathematical type associated with this program type.</p>
     *
     * @return A {@link MTType} representation object.
     */
    @Override
    public final MTType toMath() {
        return myModel;
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return myName;
    }

}