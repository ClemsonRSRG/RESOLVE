/**
 * ConstantParamDec.java
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
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry;

/**
 * <p>This is the class for all the constant parameter declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ConstantParamDec extends Dec implements ModuleParameter {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The parameter variable.</p> */
    private final ParameterVarDec myParameterDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a constant variable that is passed as a parameter
     * to a module.</p>
     *
     * @param name A {@link PosSymbol} representing the variable's name.
     * @param ty A {@link Ty} representing the variable's raw type.
     */
    public ConstantParamDec(PosSymbol name, Ty ty) {
        super(name.getLocation(), name);
        myParameterDec =
                new ParameterVarDec(
                        ProgramParameterEntry.ParameterMode.EVALUATES, name, ty);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        return myParameterDec.asString(indentSize, innerIndentInc);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final ConstantParamDec copy() {
        return new ConstantParamDec(myName.clone(), myParameterDec.getTy()
                .clone());
    }

}