/**
 * Statement.java
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
package edu.clemson.cs.rsrg.absyn;

import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the abstract base class for all the statements
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public abstract class Statement extends ResolveConceptualElement {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>A helper constructor that allow us to store the location
     * of the created object directly in the this class.</p>
     *
     * @param l A {@link Location} representation object.
     */
    protected Statement(Location l) {
        super(l);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default clone method implementation
     * for all the classes that extend from {@link Statement}.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final Statement clone() {
        return this.copy();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by concrete subclasses of {@link Statement} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link Statement} that is a deep copy of the original.
     */
    protected Statement copy() {
        throw new MiscErrorException(
                "Shouldn't be calling copy() from statement " + this.getClass(),
                new CloneNotSupportedException());
    }

}