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

    private final Map<Exp, Exp> myConceptArgMap;

    public FacilityFormalToActuals(Map<Exp, Exp> cArgMap) {
        myConceptArgMap = cArgMap;
    }

    public Map<Exp, Exp> getConceptArgMap() {
        return myConceptArgMap;
    }

}