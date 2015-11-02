/**
 * FacilityFormalToActuals.java
 * ---------------------------------
 * Copyright (c) 2015
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
import java.util.Map;

/**
 * TODO: Write a description of this module
 */
public class FacilityFormalToActuals {

    /** <p>This maps all concept formal arguments to their actuals</p> */
    private final Map<Exp, Exp> myConceptArgMap;

    /** <p>This maps all concept realization formal arguments to their actuals</p> */
    private final Map<Exp, Exp> myConceptRealizArgMap;

    public FacilityFormalToActuals(Map<Exp, Exp> cArgMap, Map<Exp, Exp> crArgMap) {
        myConceptArgMap = cArgMap;
        myConceptRealizArgMap = crArgMap;
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

}