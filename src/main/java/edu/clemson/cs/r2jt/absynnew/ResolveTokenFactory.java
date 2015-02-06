package edu.clemson.cs.r2jt.absynnew;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

/**
 * <p>A <code>ResolveTokenFactory</code> produces {@link ResolveToken}s. This
 * can be plugged into to the RESOLVE parser and lexer to outfit the parse tree
 * with {@link ResolveToken}s, as opposed to {@link CommonToken}s.</p>
 */
public class ResolveTokenFactory implements TokenFactory<ResolveToken> {

    private final CharStream myInput;

    public ResolveTokenFactory(CharStream input) {
        myInput = input;
    }

    @Override
    public ResolveToken create(int type, String text) {
        return new ResolveToken(type, text);
    }

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