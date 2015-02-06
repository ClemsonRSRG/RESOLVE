package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import org.antlr.v4.runtime.Token;

public abstract class DeclAST extends ResolveAST {

    private final Token myName;
    protected MTType myMathType = null;

    public DeclAST(Token start, Token stop, Token name) {
        super(start, stop);
        myName = name;
    }

    public MTType getMathType() {
        return myMathType;
    }

    public void setMathType(MTType mt) {
        if (mt == null) {
            throw new RuntimeException("trying to set null type on "
                    + this.getClass().getSimpleName());
        }
        myMathType = mt;
    }

    public Token getName() {
        return myName;
    }
}
