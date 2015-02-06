package edu.clemson.cs.r2jt.absynnew.stmt;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import org.antlr.v4.runtime.Token;

public abstract class StmtAST<D extends ResolveAST> extends ResolveAST {

    public StmtAST(Token start, Token stop) {
        super(start, stop);
    }
}
