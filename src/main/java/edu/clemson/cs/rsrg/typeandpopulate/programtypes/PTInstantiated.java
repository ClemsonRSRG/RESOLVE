/**
 * PTInstantiated.java
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
package edu.clemson.cs.rsrg.typeandpopulate.programtypes;

import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;

/**
 * <p>This abstract class serves as the parent class of all
 * program types that have been instantiated or is a record
 * that contains instantiated types..</p>
 *
 * @version 2.0
 */
public abstract class PTInstantiated extends PTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The facility qualifier (if type is instantiated)</p> */
    protected String myFacilityQualifier = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the type graph
     * of any objects created from a class that inherits from
     * {@code PTInstantiated}.</p>
     *
     * @param g The current type graph.
     */
    protected PTInstantiated(TypeGraph g) {
        super(g);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the facility qualifier.</p>
     *
     * @return A string.
     */
    public final String getFacilityQualifier() {
        return myFacilityQualifier;
    }

    /**
     * <p>This methods adds a facility qualifier to this type.</p>
     *
     * @param facilityName The facility associated with this type.
     */
    public final void setFacilityQualifier(String facilityName) {
        myFacilityQualifier = facilityName;
    }

}