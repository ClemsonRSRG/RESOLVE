/*
 * ResolveToken.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.parsing.data;

import edu.clemson.rsrg.init.file.ResolveFile;
import edu.clemson.rsrg.parsing.ResolveLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

/**
 * <p>
 * A special token that overrides the {@link #equals(Object)}} logic present in the default implementation of
 * {@link CommonToken}.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 *
 * @version 1.0
 */
public class ResolveToken extends CommonToken {

    /**
     * <p>
     * The source file for this token.
     * </p>
     */
    private final ResolveFile mySourceFile;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a RESOLVE identifier token.
     * </p>
     *
     * @param file
     *            Source file.
     * @param text
     *            Token text.
     */
    public ResolveToken(ResolveFile file, String text) {
        super(ResolveLexer.IDENTIFIER, text);
        mySourceFile = file;
    }

    /**
     * <p>
     * This creates a generic RESOLVE token.
     * </p>
     *
     * @param file
     *            Source file.
     * @param type
     *            Token type.
     * @param text
     *            Token text.
     */
    public ResolveToken(ResolveFile file, int type, String text) {
        super(type, text);
        mySourceFile = file;
    }

    /**
     * <p>
     * This constructor allows you to create a token from a source pair.
     * </p>
     *
     * @param file
     *            Source file.
     * @param source
     *            Token source.
     * @param type
     *            Token type.
     * @param channel
     *            Channel that this token originated from.
     * @param start
     *            Token start location.
     * @param stop
     *            Token stop location.
     */
    public ResolveToken(ResolveFile file, Pair<TokenSource, CharStream> source, int type, int channel, int start,
            int stop) {
        super(source, type, channel, start, stop);
        mySourceFile = file;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Equals method to compare two token objects.
     * </p>
     *
     * @param o
     *            The object to compare.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        boolean result;
        if (o == this) {
            result = true;
        } else if (o == null || !(o instanceof ResolveToken)) {
            result = false;
        } else {
            result = ((ResolveToken) o).getText().equals(getText());
        }

        return result;
    }

    /**
     * <p>
     * Returns the location for the token.
     * </p>
     *
     * @return The {@link Location} object.
     */
    public final Location getLocation() {
        return new Location(mySourceFile, getLine(), getCharPositionInLine());
    }

    /**
     * <p>
     * Returns a hash code for this string.
     * </p>
     *
     * @return A hash code value for this object.
     */
    @Override
    public final int hashCode() {
        return getText().hashCode();
    }

    /**
     * <p>
     * Returns the token in string format.
     * </p>
     *
     * @return Token as a string.
     */
    @Override
    public final String toString() {
        return getText();
    }

}
