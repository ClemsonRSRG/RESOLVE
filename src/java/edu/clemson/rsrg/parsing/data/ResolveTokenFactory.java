/*
 * ResolveTokenFactory.java
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
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

/**
 * <p>
 * A {@code ResolveTokenFactory} produces {@link ResolveToken}s. This can be plugged into to the RESOLVE lexer and
 * parser to outfit the parse tree with {@link ResolveToken}s, as opposed to {@link CommonToken}s.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 *
 * @version 1.0
 */
public class ResolveTokenFactory implements TokenFactory<ResolveToken> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The file that created this token factory.
     * </p>
     */
    private final ResolveFile myFile;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This object creates {@link ResolveToken}s rather than the default tokens.
     * </p>
     *
     * @param file
     *            Input file.
     */
    public ResolveTokenFactory(ResolveFile file) {
        myFile = file;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Creates a generic RESOLVE token.
     * </p>
     *
     * @param type
     *            Token type.
     * @param text
     *            Token text.
     *
     * @return A token object.
     */
    @Override
    public final ResolveToken create(int type, String text) {
        return new ResolveToken(myFile, type, text);
    }

    /**
     * <p>
     * Creates a RESOLVE token from a source pair.
     * </p>
     *
     * @param source
     *            Token source.
     * @param type
     *            Token type.
     * @param text
     *            Token text.
     * @param channel
     *            Channel that this token originated from.
     * @param start
     *            Token start location.
     * @param stop
     *            Token stop location.
     * @param line
     *            Token's line number
     * @param charPositionInLine
     *            Token's position in line.
     *
     * @return A token object.
     */
    @Override
    public final ResolveToken create(Pair<TokenSource, CharStream> source, int type, String text, int channel,
            int start, int stop, int line, int charPositionInLine) {
        ResolveToken t = new ResolveToken(myFile, source, type, channel, start, stop);
        t.setLine(line);
        t.setCharPositionInLine(charPositionInLine);

        return t;
    }

    /**
     * <p>
     * This returns the {@code file} we are associated with.
     * </p>
     *
     * @return A {@link ResolveFile}.
     */
    public final ResolveFile getFile() {
        return myFile;
    }

}
