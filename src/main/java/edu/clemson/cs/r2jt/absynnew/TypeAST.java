package edu.clemson.cs.r2jt.absynnew;

import org.antlr.v4.runtime.Token;

/**
 * <p>The root of all types representable in <tt>RESOLVE</tt>'s current syntax.
 * While program types for the moment are somewhat limited to identifiers
 * (records not yet supported fully), the range of mathematical types
 * ({@link MathTypeAST}) are more flexible as they are represented under the
 * hood simply as {@link edu.clemson.cs.r2jt.absynnew.expr.ExprAST}s.</p>
 */
public abstract class TypeAST extends ResolveAST {

    public TypeAST(Token start, Token stop) {
        super(start, stop);
    }
}