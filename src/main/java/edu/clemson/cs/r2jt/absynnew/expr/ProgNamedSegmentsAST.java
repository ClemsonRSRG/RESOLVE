/**
 * ProgNamedSegmentsAST.java
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

import edu.clemson.cs.r2jt.misc.Utils;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a series of {@link ProgNameRefAST}s delimited by '.' (dot)
 * notation. Note that {@code ProgNamedSegmentsAST} was explicitly designed
 * to be a "flat", meaning may only contain segments that are not themselves
 * other segments--only names.
 */
//Todo: Technically this class needs a 'starting element' as well that can
//be ProgParamAST. Think operation that returns a record that therefore allows
//dots to proceed function calls (e.g. Foo(x).y.z). Since no examples contain
//this case, we'll leave this class be for now.
public class ProgNamedSegmentsAST extends ProgExprAST {

    private final List<ProgNameRefAST> mySegments =
            new ArrayList<ProgNameRefAST>();

    public ProgNamedSegmentsAST(Token start, Token stop,
            List<ProgNameRefAST> segs) {
        super(start, stop);
        mySegments.addAll(segs);
    }

    public List<ProgNameRefAST> getSegments() {
        return mySegments;
    }

    @Override
    public List<? extends ExprAST> getSubExpressions() {
        return mySegments;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {
        mySegments.set(index, (ProgNameRefAST) e);
    }

    @Override
    protected ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        ExprAST retval;

        List<ProgNameRefAST> newSegments = new ArrayList<ProgNameRefAST>();
        for (ExprAST e : mySegments) {
            newSegments.add((ProgNameRefAST) substitute(e, substitutions));
        }
        retval = new ProgNamedSegmentsAST(getStart(), getStop(), newSegments);
        return retval;
    }

    @Override
    public String toString() {
        return Utils.join(mySegments, ".");
    }
}