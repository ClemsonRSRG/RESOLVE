/*
 * PTInstantiated.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.programtypes;

import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;

/**
 * <p>
 * This abstract class serves as the parent class of all program types that have been instantiated or is a record that
 * contains instantiated types..
 * </p>
 *
 * @version 2.0
 */
public abstract class PTInstantiated extends PTType {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * An helper constructor that allow us to store the type graph of any objects created from a class that inherits
     * from {@code PTInstantiated}.
     * </p>
     *
     * @param g
     *            The current type graph.
     */
    protected PTInstantiated(TypeGraph g) {
        super(g);
    }

}
