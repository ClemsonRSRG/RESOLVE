/**
 * ColsToken.java
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
package edu.clemson.cs.r2jt.parsing;

/**
 * An extension to ANTLR's token class to store column numbers with
 * the tokens.
 * 
 * @author Steven Atkinson
 */
public class ColsToken extends antlr.Token {

    // ===========================================================
    // Variables
    // ===========================================================

    protected int type;
    protected int line;
    protected int column;
    protected String text = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ColsToken() {
        ;
    }

    public ColsToken(int type, String text) {
        this.type = type;
        this.text = text;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getText() {
        return text;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setText(String str) {
        text = str;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Returns A string representation for this object. */
    public String toString() {
        String str =
                "[\"" + getText() + "\",type:<" + getType() + ">,line:"
                        + getLine() + ",column:" + getColumn() + "]";
        return (str);
    }
}
