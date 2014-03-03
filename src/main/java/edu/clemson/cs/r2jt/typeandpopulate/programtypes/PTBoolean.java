/**
 * PTBoolean.java
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
package edu.clemson.cs.r2jt.typeandpopulate.programtypes;

import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author hamptos
 */
public class PTBoolean extends PTType {

    private static WeakHashMap<TypeGraph, PTBoolean> instances =
            new WeakHashMap<TypeGraph, PTBoolean>();

    public static PTBoolean getInstance(TypeGraph g) {
        PTBoolean result = instances.get(g);

        if (result == null) {
            result = new PTBoolean(g);
            instances.put(g, result);
        }

        return result;
    }

    private PTBoolean(TypeGraph g) {
        super(g);
    }

    @Override
    public MTType toMath() {
        return getTypeGraph().BOOLEAN;
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        return this;
    }

}
