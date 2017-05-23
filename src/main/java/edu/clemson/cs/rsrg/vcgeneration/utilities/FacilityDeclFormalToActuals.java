/*
 * FacilityDeclFormalToActuals.java
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
package edu.clemson.cs.rsrg.vcgeneration.utilities;

import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.Map;
import java.util.Set;

/**
 * <p>This class stores mappings of {@link FacilityDec FacilityDec's} formal
 * arguments in the specifications/implementations to their actual arguments
 * in the instantiation.</p>
 *
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class FacilityDeclFormalToActuals {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>This maps all {@code Concept} formal arguments to the instantiated
     * actual arguments.</p>
     */
    private final Map<Exp, Exp> myConceptArgMap;

    /**
     * <p>This maps all {@code Concept Realization} formal arguments to the instantiated
     * actual arguments.</p>
     */
    private final Map<Exp, Exp> myConceptRealizArgMap;

    /**
     * <p>This maps all {@code Enhancement} and {@code Enhancement Realization} formal arguments
     * to the instantiated actual arguments.</p>
     */
    private final Map<PosSymbol, Map<Exp, Exp>> myEnhancementArgMaps;

    /** <p>The instantiated {@code Facility}.</p> */
    private final FacilityDec myInstantiatedFacilityDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that stores mapping collections for
     * {@link FacilityDec FacilityDec's} formals to the instantiated
     * actual arguments.</p>
     *
     * @param dec The instantiated {@code Facility} declaration.
     * @param cArgMap Argument mapping for the instantiating {@code Concept}.
     * @param crArgMap Argument mapping for the instantiating {@code Concept Realization}.
     * @param eArgMaps Argument mapping for the instantiating {@code Enhancement} and
     *                 {@code Enhancement Realization}.
     */
    public FacilityDeclFormalToActuals(FacilityDec dec, Map<Exp, Exp> cArgMap,
            Map<Exp, Exp> crArgMap, Map<PosSymbol, Map<Exp, Exp>> eArgMaps) {
        myInstantiatedFacilityDec = dec;
        myConceptArgMap = cArgMap;
        myConceptRealizArgMap = crArgMap;
        myEnhancementArgMaps = eArgMaps;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default {@code equals} method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FacilityDeclFormalToActuals that = (FacilityDeclFormalToActuals) o;

        if (!myConceptArgMap.equals(that.myConceptArgMap))
            return false;
        if (!myConceptRealizArgMap.equals(that.myConceptRealizArgMap))
            return false;
        if (!myEnhancementArgMaps.equals(that.myEnhancementArgMaps))
            return false;
        return myInstantiatedFacilityDec.equals(that.myInstantiatedFacilityDec);
    }

    /**
     * <p>This method returns a map containing the {@code Concept's}
     * formal to actual arguments for the instantiated {@code Facility}.</p>
     *
     * @return A {@link Map} containing the formal to actual mapping.
     */
    public final Map<Exp, Exp> getConceptArgMap() {
        return myConceptArgMap;
    }

    /**
     * <p>This method returns a map containing the {@code Concept Realization's}
     * formal to actual arguments for the instantiated {@code Facility}.</p>
     *
     * @return A {@link Map} containing the formal to actual mapping.
     */
    public final Map<Exp, Exp> getConceptRealizArgMap() {
        return myConceptRealizArgMap;
    }

    /**
     * <p>This method returns the names of {@code Enhancement} and
     * {@code Enhancement Realization} for the instantiated {@code Facility}.</p>
     *
     * @return A {@link Set} containing {@link PosSymbol} representation
     * of {@code Enhancement} and {@code Enhancement Realization} names.
     */
    public final Set<PosSymbol> getEnhancementKeys() {
        return myEnhancementArgMaps.keySet();
    }

    /**
     * <p>This method returns a map containing the {@code Enhancement's} or
     * {@code Enhancement Realization's} formal to actual arguments for
     * the instantiated {@code Facility}.</p>
     *
     * @param name Name of the enhancement or enhancement realization
     *
     * @return A {@link Map} containing the formal to actual mapping.
     */
    public final Map<Exp, Exp> getEnhancementArgMap(PosSymbol name) {
        return myEnhancementArgMaps.get(name);
    }

    /**
     * <p>This method returns the instantiated {@code Facility} declaration.</p>
     *
     * @return A {@link FacilityDec}.
     */
    public final FacilityDec getInstantiatedFacilityDec() {
        return myInstantiatedFacilityDec;
    }

    /**
     * <p>This method returns the instantiated {@code Facility}'s name.</p>
     *
     * @return {@code Facility} name as a {@link PosSymbol}.
     */
    public final PosSymbol getInstantiatedFacilityName() {
        return myInstantiatedFacilityDec.getName();
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myConceptArgMap.hashCode();
        result = 31 * result + myConceptRealizArgMap.hashCode();
        result = 31 * result + myEnhancementArgMaps.hashCode();
        result = 31 * result + myInstantiatedFacilityDec.hashCode();
        return result;
    }

}