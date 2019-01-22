/*
 * LineNumberedExp.java
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
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.data.PosSymbol;

public abstract class LineNumberedExp extends Exp {

    protected PosSymbol myLineNumber;

    public LineNumberedExp(PosSymbol lineNumber) {
        myLineNumber = lineNumber;
    }

    /** Returns the line number for this expression. */
    public PosSymbol getLineNum() {
        return myLineNumber;
    }

    /** Sets the line number for this expression. */
    public void setLineNum(PosSymbol lineNumber) {
        myLineNumber = lineNumber;
    }
}
