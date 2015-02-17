/**
 * ExprAST.java
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
package edu.clemson.cs.r2jt.absynnew.expr;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import org.antlr.v4.runtime.Token;

import java.util.*;

public abstract class ExprAST extends ResolveAST {

    protected MTType myMathType = null;
    protected MTType myMathTypeValue = null;

    public ExprAST(Token start, Token stop) {
        super(start, stop);
    }

    public abstract List<? extends ExprAST> getSubExpressions();

    public abstract void setSubExpression(int index, ExprAST e);

    public abstract boolean isLiteral();

    /**
     * <p>Returns a <strong>deep copy</strong>of this expression, with all
     * instances of <code>ExprAST</code>s that occur as keys in
     * <code>substitutions</code> replaced with their corresponding values.</p>
     *
     * <p>In general, a key <code>ExprAST</code> "occurs" in this
     * <code>ExprAST</code> if either this <code>ExprAST</code> or some
     * subexpression is {@link #equivalent}.  However, if the key is a
     * <code>MathSymbolAST</code> function names are additionally matched, even
     * though they would not ordinarily match via {@link #equivalent}, so
     * function names can be substituted without affecting their arguments.</p>
     *
     * @param substitutions A mapping from <code>ExprAST</code>s that should be
     *                      substituted out to the <code>ExprAST</code> that
     *                      should replace them.
     * @return A new <code>ExprAST</code> that is a deep copy of the original
     *          with the provided substitutions made.
     */
    public final ExprAST substitute(Map<ExprAST, ExprAST> substitutions) {
        ExprAST retval;

        boolean match = false;
        Map.Entry<ExprAST, ExprAST> curEntry = null;
        if (substitutions.size() > 0) {
            Set<Map.Entry<ExprAST, ExprAST>> entries = substitutions.entrySet();
            Iterator<Map.Entry<ExprAST, ExprAST>> entryIter =
                    entries.iterator();
            while (entryIter.hasNext() && !match) {
                curEntry = entryIter.next();
                match = curEntry.getKey().equivalent(this);
            }

            if (match) {
                retval = curEntry.getValue();
            }
            else {
                retval = ExprAST.substituteChildren(this, substitutions);
            }
        }
        else {
            retval = ExprAST.copy(this);
        }

        return retval;
    }

    /**
     * <p>Implemented by concrete subclasses of <code>ExprAST</code> to
     * manufacture a copy of themselves where all subexpressions have been
     * appropriately substituted.  The concrete subclass may assume that
     * <code>this</code> does not match any key in <code>substitutions</code>
     * and thus need only concern itself with performing substitutions in its
     * children.</p>
     *
     * @param substitutions A mapping from <code>ExprAST</code>s that should be
     *                      substituted out to the <code>ExprAST</code> that
     *                      should replace them.
     * @return A new <code>ExprAST</code> that is a deep copy of the original
     *          with the provided substitutions made.
     */
    protected abstract ExprAST substituteChildren(
            Map<ExprAST, ExprAST> substitutions);

    public static final ExprAST substituteChildren(ExprAST target,
            Map<ExprAST, ExprAST> substitutions) {

        MTType originalType = target.getMathType();
        MTType originalTypeValue = target.getMathTypeValue();

        ExprAST result = target.substituteChildren(substitutions);

        result.setMathType(originalType);
        result.setMathTypeValue(originalTypeValue);

        return result;
    }

    public final ExprAST substituteNames(Map<String, ExprAST> substitutions) {
        Map<ExprAST, ExprAST> finalSubstitutions =
                new HashMap<ExprAST, ExprAST>();

        for (Map.Entry<String, ExprAST> substitution : substitutions.entrySet()) {

            finalSubstitutions.put(new MathSymbolAST.MathSymbolExprBuilder(
                    substitution.getKey()).build(), substitution.getValue());
        }

        return substitute(finalSubstitutions);
    }

    protected static ExprAST substitute(ExprAST e,
            Map<ExprAST, ExprAST> substitutions) {
        ExprAST retval;
        if (e == null) {
            retval = null;
        }
        else {
            retval = e.substitute(substitutions);
        }
        return retval;
    }

    public MTType getMathType() {
        return myMathType;
    }

    public void setMathType(MTType mathType) {
        if (mathType == null) {
            throw new RuntimeException("null math type on: " + this.getClass());

        }
        myMathType = mathType;
    }

    public MTType getMathTypeValue() {
        return myMathTypeValue;
    }

    public void setMathTypeValue(MTType mathTypeValue) {
        myMathTypeValue = mathTypeValue;
    }

    protected ExprAST copy() {
        System.out.println("shouldn't be calling ExprAST.copy() from type "
                + this.getClass());
        throw new RuntimeException();
    }

    public static ExprAST copy(ExprAST exp) {
        MTType originalType = exp.getMathType();
        MTType originalTypeValue = exp.getMathTypeValue();

        ExprAST result = exp.copy();

        result.setMathType(originalType);
        result.setMathTypeValue(originalTypeValue);

        return result;
    }

    /**
     * <p>Shallow compare is too weak for many things, and {@link #equals} is
     * too strict. This method returns <code>true</code> <strong>iff</strong>
     * this expression and the provided expression, <code>e</code>, are
     * equivalent with respect to structure and all function and variable
     * names.</p>
     *
     * @param e The expression to compare this one to.
     * @return True <strong>iff</strong> this expression and the provided
     *         expression are equivalent with respect to structure and all
     *         function and variable names.
     */
    public boolean equivalent(ExprAST e) {
        throw new UnsupportedOperationException(
                "equivalence for classes of type " + this.getClass()
                        + " is not currently supported");
    }

    public boolean isLiteralTrue() {
        boolean result = (this instanceof MathSymbolAST);

        result =
                result
                        && ((MathSymbolAST) this).getName().getText().equals(
                                "true")
                        && this.getMathType().equals(
                                this.getMathType().getTypeGraph().BOOLEAN);

        return result;
    }

    public boolean isLiteralFalse() {
        boolean result = (this instanceof MathSymbolAST);

        result =
                result
                        && ((MathSymbolAST) this).getName().getText().equals(
                                "false")
                        && this.getMathType().equals(
                                this.getMathType().getTypeGraph().BOOLEAN);

        return result;
    }

}