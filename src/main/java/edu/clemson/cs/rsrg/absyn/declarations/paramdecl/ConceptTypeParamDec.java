/*
 * ConceptTypeParamDec.java
 * ---------------------------------
 * Copyright (c) 2019
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
 * <p>This is the class for all the concept module type parameter
 * declaration objects that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ConceptTypeParamDec extends Dec implements ModuleParameter {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type representation that is passed as a parameter
     * to a module.</p>
     *
     * @param name A {@link PosSymbol} representing the type's name.
     */
    public ConceptTypeParamDec(PosSymbol name) {
        super(name.getLocation(), name);
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
        sb.append("TYPE ");
        sb.append(myName.asString(0, innerIndentInc));

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final ConceptTypeParamDec copy() {
        return new ConceptTypeParamDec(myName.clone());
    }

}