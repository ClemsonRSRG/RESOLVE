/*
 * FacilityFormalToActuals.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.vcgeneration;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.data.PosSymbol;

import java.util.Map;
import java.util.Set;

/**
 * TODO: Write a description of this module
 */
public class FacilityFormalToActuals {

    /** <p>This maps all concept formal arguments to their actuals</p> */
    private final Map<Exp, Exp> myConceptArgMap;

    /** <p>This maps all concept realization formal arguments to their actuals</p> */
    private final Map<Exp, Exp> myConceptRealizArgMap;

    /** <p>This is a map from enhancement [realization] to a map of formal arguments to their actuals</p> */
    private final Map<PosSymbol, Map<Exp, Exp>> myEnhancementArgMaps;

    /**
     * <p>This creates a collection of formals to facility actuals.</p>
     *
     * @param cArgMap Concept argument mapping.
     * @param crArgMap Concept realization argument mapping.
     * @param eArgMaps Enhancement [realization] argument mappings.
     */
    public FacilityFormalToActuals(Map<Exp, Exp> cArgMap,
            Map<Exp, Exp> crArgMap, Map<PosSymbol, Map<Exp, Exp>> eArgMaps) {
        myConceptArgMap = cArgMap;
        myConceptRealizArgMap = crArgMap;
        myEnhancementArgMaps = eArgMaps;
    }

    /**
     * <p>Returns a map containing the concept formal and
     * actual arguments.</p>
     *
     * @return A {@link Map}.
     */
    public Map<Exp, Exp> getConceptArgMap() {
        return myConceptArgMap;
    }

    /**
     * <p>Returns a map containing the concept realization formal and
     * actual arguments.</p>
     *
     * @return A {@link Map}.
     */
    public Map<Exp, Exp> getConceptRealizArgMap() {
        return myConceptRealizArgMap;
    }

    /**
     * <p>Returns the names of enhancement and enhancement realizations
     * for this facility declaration.</p>
     *
     * @return A {@link Set}
     */
    public Set<PosSymbol> getEnhancementKeys() {
        return myEnhancementArgMaps.keySet();
    }

    /**
     * <p>Returns a map containing the enhancement [realization] formal
     * and actual arguments.</p>
     *
     * @param name Name of the enhancement or enhancement realization
     *
     * @return A {@link Map}
     */
    public Map<Exp, Exp> getEnhancementArgMap(PosSymbol name) {
        return myEnhancementArgMaps.get(name);
    }

}