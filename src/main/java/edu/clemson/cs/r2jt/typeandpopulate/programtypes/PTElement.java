/**
 * PTElement.java
 * ---------------------------------
 * Copyright (c) 2016
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
import java.util.Map;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>The program-type corresponding to TypeGraph.ELEMENT, i.e., the type of
 * all program types.</p>
 */
public class PTElement extends PTType {

    public PTElement(TypeGraph g) {
        super(g);
    }

    @Override
    public MTType toMath() {
        return getTypeGraph().ELEMENT;
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        return this;
    }
}
