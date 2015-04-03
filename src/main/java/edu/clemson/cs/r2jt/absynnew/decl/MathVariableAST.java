/**
 * MathVariableAST.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.MathTypeAST;
import org.antlr.v4.runtime.Token;

public class MathVariableAST extends DeclAST {

    private final MathTypeAST mySyntacticType;

    public MathVariableAST(Token start, Token stop, Token name, MathTypeAST type) {
        super(start, stop, name);
        mySyntacticType = type;
    }

    public MathTypeAST getSyntaxType() {
        return mySyntacticType;
    }

    @Override
    public String toString() {
        return getName() + " : " + mySyntacticType;
    }
}