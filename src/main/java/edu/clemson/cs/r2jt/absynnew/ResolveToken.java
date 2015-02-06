package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.parsing.ResolveLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

/**
 * <p>A special token that overrides the "equals" logic present in the default
 * implementation of {@link CommonToken}. Turns out this is functionally
 * equivalent to our now removed <tt>PosSymbol</tt> class.</p>
 */
public class ResolveToken extends CommonToken {

    public String mySourceName;

    public ResolveToken(String text) {
        super(ResolveLexer.Identifier, text);
    }

    public ResolveToken(int type, String text) {
        super(type, text);
    }

    public ResolveToken(Pair<TokenSource, CharStream> source, int type,
            int channel, int start, int stop) {
        super(source, type, channel, start, stop);
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public int hashCode() {
        return getText().hashCode();
    }

    public boolean equals(Object o) {
        boolean result = (o instanceof ResolveToken);

        if (result) {
            result = ((ResolveToken) o).getText().equals(getText());
        }
        return result;
    }
}