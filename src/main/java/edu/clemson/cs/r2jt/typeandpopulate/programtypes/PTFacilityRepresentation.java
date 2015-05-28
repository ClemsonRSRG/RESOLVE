/**
 * PTFacilityRepresentation.java
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
package edu.clemson.cs.r2jt.typeandpopulate.programtypes;

import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

import java.util.Map;

/**
 * Created by danielwelch on 10/22/14.
 */
// TODO: Determine if we really want this wrapper or if we could simply live w/
// PTRepresentation (I'm thinking we probably could... but the lack of a
// family makes me wonder).
public class PTFacilityRepresentation extends PTType {

    private final PTType myBaseType;

    /**
     * <p>Since facility representation types do not have a corresponding
     * <code>PTFamily</code>, we just store the name they go by here.</p>
     */
    private final String myTypeName;

    public PTFacilityRepresentation(TypeGraph g, PTType baseType,
            String typeName) {
        super(g);

        myBaseType = baseType;
        myTypeName = typeName;
    }

    public PTType getBaseType() {
        return myBaseType;
    }

    public String getName() {
        return myTypeName;
    }

    @Override
    public MTType toMath() {
        return myBaseType.toMath();
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        throw new UnsupportedOperationException(this.getClass() + " cannot "
                + "be instantiated.");
    }

    @Override
    public boolean acceptableFor(PTType t) {
        boolean result = super.acceptableFor(t);

        if (!result) {
            result = myBaseType.acceptableFor(t);
        }

        return result;
    }

    @Override
    public String toString() {
        return myTypeName + " as " + myBaseType;
    }
}