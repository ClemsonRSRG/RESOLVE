/**
 * MathTypeAssertionAST.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absynnew.expr;

import edu.clemson.cs.r2jt.absynnew.MathTypeAST;
import edu.clemson.cs.r2jt.absynnew.decl.MathTypeTheoremAST;
import org.antlr.v4.runtime.Token;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>A <code>TypeAssertionExp</code>, generally, allows a sub-expression to be
 * asserted as belonging to some type.  It is permitted only in two places:
 *
 * <ul>
 *      <li>Inside the hierarchy of an {@link MathTypeAST}
 *          when that ty appears in the context of the type of a formal
 *          parameter to a mathematical definition.</li>
 *      <li>Inside a {@link MathTypeTheoremAST}, either at the top
 *          level of the overall assertion, or at the top level of the
 *          consequent of a top-level <em>implies</em> expression.</li>
 * </ul>
 *
 * <p>In the first case, it defines a slot to be bound implicitly by some
 * component of the type of the actual parameter.  In this case, the expression
 * must be a {@link MathSymbolAST}.  Some examples:</p>
 *
 * <pre>
 * Definition Foo(S : Str(T : Powerset(Z)) : T;
 * </pre>
 *
 * <p>Here, the <code>T : Powerset(Z)</code> part is an implicit type parameter.
 * If one were to pass a <code>Str(N)</code> to <code>Foo</code>, then the
 * return type would be <code>T</code>.  If one were to pass <code>Str(B)</code>
 * to <code>Foo</code> that would be a typechecking error, since <code>B</code>
 * is not in <code>Powerset(Z)</code>.</p>
 *
 * <pre>
 * Definition Bar(L : Set(T : MType), E : Powerset(T)) : T;
 * </pre>
 *
 * <p>Here, <code>T : MType</code> is an implicit type parameter, and the type
 * of both the second parameter and the return value are dependent on the
 * binding of that parameter.  Implicit parameters are always bound left to
 * right, so the first parameter's type may not depend on the second's.</p>
 *
 * <p>In the second case, it asserts that any expression on the left side of the
 * colon is of the type on the right side, provided the antecedents of any
 * top-level implication are fulfilled.  In this case, the expression may be any
 * kind of expression.</p>
 */
public class MathTypeAssertionAST extends ExprAST {

    private ExprAST myExpr;
    private MathTypeAST myAssertedType;

    public MathTypeAssertionAST(Token start, Token stop, ExprAST expr,
            MathTypeAST assertedType) {
        super(start, stop);
        myExpr = expr;
        myAssertedType = assertedType;
    }

    public ExprAST getExpression() {
        return myExpr;
    }

    public MathTypeAST getAssertedType() {
        return myAssertedType;
    }

    @Override
    public List<? extends ExprAST> getSubExpressions() {
        return Collections.singletonList(myExpr);
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {
        myExpr = e;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    protected ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        return new MathTypeAssertionAST(getStart(), getStop(), myExpr
                .substituteChildren(substitutions), new MathTypeAST(
                myAssertedType.getUnderlyingExpr().substitute(substitutions)));
    }

    @Override
    public String toString() {
        return myExpr + " : " + myAssertedType.getUnderlyingExpr();
    }
}
