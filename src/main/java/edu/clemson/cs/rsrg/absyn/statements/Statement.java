/**
 * Statement.java
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
package edu.clemson.cs.rsrg.absyn.statements;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;

/**
 * <p>This is the abstract base class for all the statements objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public abstract class Statement extends ResolveConceptualElement {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the location
     * of any objects created from a class that inherits from
     * {@code Statement}.</p>
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