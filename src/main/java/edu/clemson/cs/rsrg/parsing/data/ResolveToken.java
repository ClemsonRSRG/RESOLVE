/**
 * ResolveToken.java
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
package edu.clemson.cs.rsrg.parsing.data;

import edu.clemson.cs.r2jt.parsing.ResolveLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

/**
 * <p>A special token that overrides the "equals" logic present in the default
 * implementation of {@link CommonToken}. Turns out this is functionally
 * equivalent to our now removed PosSymbol class.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 1.0
 */
public class ResolveToken extends CommonToken {

    /** <p>The source location for this token.</p> */
    protected String mySourceName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a RESOLVE identifier token.</p>
     *
     * @param text Token text.
     */
    public ResolveToken(String text) {
        super(ResolveLexer.IDENTIFIER, text);
    }

    /**
     * <p>This creates a generic RESOLVE token.</p>
     *
     * @param type Token type.
     * @param text Token text.
     */
    public ResolveToken(int type, String text) {
        super(type, text);
    }

    /**
     * <p>This constructor allows you
     * to create a token from a source pair.</p>
     *
     * @param source Token source.
     * @param type Token type.
     * @param channel Channel that this token originated from.
     * @param start Token start location.
     * @param stop Token stop location.
     */
    public ResolveToken(Pair<TokenSource, CharStream> source, int type,
            int channel, int start, int stop) {
        super(source, type, channel, start, stop);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Equals method to compare two token objects.</p>
     *
     * @param o The object to compare.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        boolean result;
        if (o == this) {
            result = true;
        }
        else if (o == null || !(o instanceof ResolveToken)) {
            result = false;
        }
        else {
            result = ((ResolveToken) o).getText().equals(getText());
        }

        return result;
    }

    /**
     * <p>Returns the location from the token in string format.</p>
     *
     * @return Location as a String.
     */
    public final String getLocation() {
        StringBuilder sb = new StringBuilder();
        sb.append(groomFileName(mySourceName));
        sb.append(" ");

        // Append the line and column number
        sb.append("(");
        sb.append(getLine());
        sb.append(":");
        sb.append(getCharPositionInLine());
        sb.append(")");

        return sb.toString();
    }

    /**
     * <p>Returns a hash code for this string.</p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public final int hashCode() {
        return getText().hashCode();
    }

    /**
     * <p>Returns the token in string format.</p>
     *
     * @return Token as a string.
     */
    @Override
    public final String toString() {
        return getText();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Trims all the path information from the filename.</p>
     *
     * @param fileName The full path filename.
     *
     * @return Filename only.
     */
    private String groomFileName(String fileName) {
        int start = fileName.lastIndexOf("/");
        if (start == -1) {
            return fileName;
        }
        return fileName.substring(start + 1, fileName.length());
    }

}