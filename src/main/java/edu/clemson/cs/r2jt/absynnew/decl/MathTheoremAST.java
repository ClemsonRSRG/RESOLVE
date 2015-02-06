package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

/**
 * <p>Represents a mathematical theorem or corollary,
 * as would be found within {@link edu.clemson.cs.r2jt.absyn.PrecisModule}</p>
 */
public class MathTheoremAST extends DeclAST {

    private final ExprAST myAssertion;

    public MathTheoremAST(Token start, Token stop, Token name, ExprAST assertion) {
        super(start, stop, name);
        myAssertion = assertion;
    }
}