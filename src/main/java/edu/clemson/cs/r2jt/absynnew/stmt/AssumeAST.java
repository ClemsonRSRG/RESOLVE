package edu.clemson.cs.r2jt.absynnew.stmt;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

public class AssumeAST extends StmtAST {

    private final ExprAST myAssertion;

    public AssumeAST(Token start, Token stop, ExprAST assertion) {
        super(start, stop);
        myAssertion = assertion;
    }

    public ExprAST getAssertion() {
        return myAssertion;
    }

    @Override
    public String toString() {
        return "assume " + myAssertion + ";";
    }
}
