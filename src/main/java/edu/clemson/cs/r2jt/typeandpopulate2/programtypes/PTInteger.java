/**
 * PTInteger.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2.programtypes;

import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author hamptos
 */
public class PTInteger extends PTType {

    private static WeakHashMap<TypeGraph, PTInteger> instances =
            new WeakHashMap<TypeGraph, PTInteger>();

    public static PTInteger getInstance(TypeGraph g) {
        PTInteger result = instances.get(g);

        if (result == null) {
            result = new PTInteger(g);
            instances.put(g, result);
        }

        return result;
    }

    private PTInteger(TypeGraph g) {
        super(g);
    }

    @Override
    public MTType toMath() {
        return getTypeGraph().Z;
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        return this;
    }

}
