/**
 * ParameterAST.java
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

import edu.clemson.cs.r2jt.absynnew.NamedTypeAST;
import org.antlr.v4.runtime.Token;

/**
 * <p>A <code>ParameterAST</code> represents a formal parameter to an
 * {@link OperationAST}.  A <em>passing mode</em> on each parameter indicates
 * the affect of a call to said operation.</p>
 */
public class ParameterAST extends DeclAST {

    private final NamedTypeAST myType;

    public ParameterAST(Token start, Token stop, Token name, NamedTypeAST type) {
        super(start, stop, name);
        myType = type;
    }

    //Todo: Use a legit mode found in...? Probably ParameterEntry (typeandpop).
    public String getMode() {
        return getStart().getText();
    }

    public NamedTypeAST getType() {
        return myType;
    }
}
