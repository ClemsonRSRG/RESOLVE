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

public class ResolveParserFactory {

    public ResolveParser createParser(String inputAsString) {
        return createParser(new ANTLRInputStream(inputAsString));
    }

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
