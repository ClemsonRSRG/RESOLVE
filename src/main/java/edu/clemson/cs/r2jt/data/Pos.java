/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.data;

/**
 * This class stores two integers representing the line and column
 * position in an input file.
 */
public class Pos {

    // ===========================================================
    // Variables
    // ===========================================================

    private int line;

    private int column;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Pos(int line, int column) {
        this.line = line;
        this.column = column;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    /** Returns the line number. */
    public int getLine() {
        return line;
    }

    /** Returns the column number. */
    public int getColumn() {
        return column;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public boolean equals(Pos pos) {
        return (this.line == pos.getLine() && this.column == pos.getColumn());
    }

    /** Returns s formatted representation of the position. */
    public String toString() {
        return "(" + line + "," + column + ")";
    }
}
