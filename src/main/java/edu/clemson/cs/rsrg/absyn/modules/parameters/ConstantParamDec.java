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
package edu.clemson.cs.rsrg.absyn.modules.parameters;

import edu.clemson.cs.rsrg.absyn.Ty;
import edu.clemson.cs.rsrg.absyn.variables.AbstractVarDec;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the constant parameter declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ConstantParamDec extends AbstractVarDec implements ModuleParameter {

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
        super(name.getLocation(), name, ty);
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
        sb.append(super.asStringVarDec(indentSize, innerIndentInc));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ConstantParamDec clone() {
        return new ConstantParamDec(myName.clone(), myTy.clone());
    }

}