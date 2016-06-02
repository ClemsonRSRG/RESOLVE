/**
 * RealizationParamDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.paramdecl;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the realization parameter declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class RealizationParamDec extends Dec implements ModuleParameter {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The name of the concept.</p> */
    private final PosSymbol myConceptName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type representation that is passed as a parameter
     * to a module.</p>
     *
     * @param name A {@link PosSymbol} representing the name of the realization.
     * @param conceptName A {@link PosSymbol} representing the name of the concept.
     */
    public RealizationParamDec(PosSymbol name, PosSymbol conceptName) {
        super(name.getLocation(), name);
        myConceptName = conceptName;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append(myName.asString(indentSize + innerIndentInc, innerIndentInc));
        sb.append(" of ");
        sb.append(myConceptName.asString(0, innerIndentInc));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        RealizationParamDec that = (RealizationParamDec) o;

        return myConceptName.equals(that.myConceptName);

    }

    /**
     * <p>Returns the symbol representation of the concept name.</p>
     *
     * @return The concept name in {@link PosSymbol} format.
     */
    public final PosSymbol getConceptName() {
        return myConceptName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myConceptName.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final RealizationParamDec copy() {
        return new RealizationParamDec(myName.clone(), myConceptName.clone());
    }

}