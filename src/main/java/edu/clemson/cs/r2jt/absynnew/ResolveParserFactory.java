/**
 * ResolveParserFactory.java
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
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.parsing.ResolveLexer;
import edu.clemson.cs.r2jt.parsing.ResolveParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * <p>A <code>ResolveParserFactory</code> allow users to create instances of
 * <code>ResolveParser</code> on demand from a string or
 * {@link ANTLRInputStream}.</p>
 */
public class ResolveParserFactory {

    /**
     * <p>Returns an {@link ResolveParser} that recognizes
     * <code>inputAsString</code></p>.
     *
     * @param inputAsString The string we want to parse.
     * @return A {@link ResolveParser}.
     */
    public ResolveParser createParser(String inputAsString) {
        return createParser(new ANTLRInputStream(inputAsString));
    }

    /**
     * <p>Returns an {@link ResolveParser} that feeds off of the
     * {@link ANTLRInputStream} passed.</p>.
     *
     * @throws IllegalArgumentException If <code>input</code> is
     *                                              <code>null</code>
     * @param input An valid input stream; file or otherwise.
     * @return A {@link ResolveParser}.
     */
    public ResolveParser createParser(ANTLRInputStream input) {
        if (input == null) {
            throw new IllegalArgumentException("ANTLRInputStream null");
        }
        ResolveLexer lexer = new ResolveLexer(input);
        ResolveTokenFactory factory = new ResolveTokenFactory(input);
        lexer.setTokenFactory(factory);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ResolveParser result = new ResolveParser(tokens);
        result.setTokenFactory(factory);

        result.removeErrorListeners();
        result.addErrorListener(UnderliningErrorListener.INSTANCE);
        return result;
    }
}
