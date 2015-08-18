/**
 * ResolveTokenFactory.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.parsing.data;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

/**
 * <p>A {@code ResolveTokenFactory} produces {@link ResolveToken}s. This
 * can be plugged into to the RESOLVE parser and lexer to outfit the parse tree
 * with {@link ResolveToken}s, as opposed to {@link CommonToken}s.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 1.0
 */
public class ResolveTokenFactory implements TokenFactory<ResolveToken> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Input data stream</p> */
    private final CharStream myInput;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This object creates {@link ResolveToken}s rather than the default tokens.</p>
     *
     * @param input Input data stream.
     */
    public ResolveTokenFactory(CharStream input) {
        myInput = input;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Creates a generic RESOLVE token.</p>
     *
     * @param type Token type.
     * @param text Token text.
     *
     * @return A token object.
     */
    @Override
    public ResolveToken create(int type, String text) {
        return new ResolveToken(type, text);
    }

    /**
     * <p>Creates a RESOLVE token from a source pair.</p>
     *
     * @param type Token type.
     * @param text Token text.
     *
     * @return A token object.
     */
    @Override
    public ResolveToken create(Pair<TokenSource, CharStream> source, int type,
            String text, int channel, int start, int stop, int line,
            int charPositionInLine) {
        ResolveToken t = new ResolveToken(source, type, channel, start, stop);
        t.setLine(line);
        t.setCharPositionInLine(charPositionInLine);
        t.mySourceName = myInput.getSourceName();

        return t;
    }

}