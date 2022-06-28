/*
 * MTAbstract.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.mathtypes;

import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;

/**
 * <p>
 * This abstract class serves as the parent class of all abstract mathematical types that contains other
 * {@link MTType}s.
 * </p>
 *
 * @version 2.0
 */
public abstract class MTAbstract<T extends MTType> extends MTType {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * An helper constructor that allow us to store the type graph of any objects created from a class that inherits
     * from {@link MTAbstract}.
     * </p>
     *
     * @param g
     *            The current type graph.
     */
    protected MTAbstract(TypeGraph g) {
        super(g);
    }

}
