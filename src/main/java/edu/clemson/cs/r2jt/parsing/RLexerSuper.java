/**
 * RLexerSuper.java
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

import org.antlr.runtime.*;

/**
 *
 * @author Mark T
 */
public abstract class RLexerSuper extends Lexer {

    public RLexerSuper(CharStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public RLexerSuper() {
        ;
    }

}
