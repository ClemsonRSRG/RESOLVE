/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
