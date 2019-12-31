/*
 * Pos.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.data;

/**
 * This class stores two integers representing the line and column position in
 * an input file.
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
