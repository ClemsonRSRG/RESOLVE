/**
 * TypeGraph.java
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
package edu.clemson.cs.r2jt.typereasoning2;

import edu.clemson.cs.r2jt.absynnew.ResolveToken;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST;
import edu.clemson.cs.r2jt.typeandpopulate2.*;

import java.util.List;

public class TypeGraph {

    /**
     * <p>A set of non-thread-safe resources to be used during general type
     * reasoning. This really doesn't belong here, but anything that's reasoning
     * about types should already have access to a type graph, and only one type
     * graph is created per thread, so this is a convenient place to put it.</p>
     */
    public final PerThreadReasoningResources threadResources =
            new PerThreadReasoningResources();

    public final MTType ELEMENT = new MTProper(this, "Element");
    public final MTType ENTITY = new MTProper(this, "Entity");
    public final MTProper CLS = new MTProper(this, null, true, "MType");

    public final MTProper SET = new MTProper(this, CLS, true, "SSet");
    public final MTProper BOOLEAN = new MTProper(this, CLS, false, "B");
    public final MTProper Z = new MTProper(this, CLS, false, "Z");
    public final MTProper R = new MTProper(this, CLS, false, "R");
    public final MTProper ATOM = new MTProper(this, CLS, false, "Atom");
    public final MTProper VOID = new MTProper(this, CLS, false, "Void");
    public final MTProper EMPTY_SET =
            new MTProper(this, CLS, false, "Empty_Set");

    private final static FunctionApplicationFactory POWERTYPE_APPLICATION =
            new PowertypeApplicationFactory();
    private final static FunctionApplicationFactory UNION_APPLICATION =
            new UnionApplicationFactory();
    private final static FunctionApplicationFactory INTERSECT_APPLICATION =
            new IntersectApplicationFactory();
    private final static FunctionApplicationFactory FUNCTION_CONSTRUCTOR_APPLICATION =
            new FunctionConstructorApplicationFactory();
    private final static FunctionApplicationFactory CARTESIAN_PRODUCT_APPLICATION =
            new CartesianProductApplicationFactory();

    public final MTFunction POWERTYPE =
            new MTFunction(this, true, POWERTYPE_APPLICATION, CLS, CLS);
    public final MTFunction POWERCLASS =
            new MTFunction(this, true, POWERTYPE_APPLICATION, CLS, CLS);
    public final MTFunction UNION =
            new MTFunction(this, UNION_APPLICATION, CLS, CLS, CLS);
    public final MTFunction INTERSECT =
            new MTFunction(this, INTERSECT_APPLICATION, CLS, CLS, CLS);
    public final MTFunction FUNCTION =
            new MTFunction(this, FUNCTION_CONSTRUCTOR_APPLICATION, CLS, CLS,
                    CLS);
    public final MTFunction CROSS =
            new MTFunction(this, CARTESIAN_PRODUCT_APPLICATION, CLS, CLS, CLS);

    public boolean isSubtype(MTType subtype, MTType supertype) {
        return false;
    }

    public boolean isKnownToBeIn(ExprAST value, MTType expected) {
        return false;
    }

    public boolean isKnownToBeIn(MTType value, MTType expected) {
        return false;
    }

    private static class PowertypeApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTPowertypeApplication(g, arguments.get(0));
        }
    }

    private static class UnionApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTUnion(g, arguments);
        }
    }

    private static class IntersectApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTUnion(g, arguments);
        }
    }

    private static class FunctionConstructorApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTFunction(g, arguments.get(1), arguments.get(0));
        }
    }

    private static class CartesianProductApplicationFactory
            implements
                FunctionApplicationFactory {

        @Override
        public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTCartesian(g,
                    new MTCartesian.Element(arguments.get(0)),
                    new MTCartesian.Element(arguments.get(1)));
        }
    }

    public ExprAST getNothingExp() {
        MathSymbolAST result =
                new MathSymbolAST.MathSymbolExprBuilder(null, null,
                        new ResolveToken("nothing"), null).build();

        result.setMathType(VOID);
        return result;
    }

    public ExprAST getTrueVarExp() {
        MathSymbolAST result =
                new MathSymbolAST.MathSymbolExprBuilder(null, null,
                        new ResolveToken("true"), null).build();
        result.setMathType(BOOLEAN);
        return result;
    }

    public ExprAST getFalseVarExp() {
        MathSymbolAST result =
                new MathSymbolAST.MathSymbolExprBuilder(null, null,
                        new ResolveToken("false"), null).build();
        result.setMathType(BOOLEAN);
        return result;
    }

    public ExprAST formDisjunct(ExprAST d1, ExprAST d2) {

        MathSymbolAST result =
                new MathSymbolAST.MathSymbolExprBuilder(null, null,
                        new ResolveToken("or"), null).arguments(d1, d2).build();

        result.setMathType(BOOLEAN);
        return result;
    }

    public MathSymbolAST formConjunct(ExprAST d1, ExprAST d2) {

        MathSymbolAST result =
                new MathSymbolAST.MathSymbolExprBuilder(null, null,
                        new ResolveToken("and"), null).arguments(d1, d2)
                        .build();

        result.setMathType(BOOLEAN);
        return result;
    }

    public MathSymbolAST formImplies(ExprAST d1, ExprAST d2) {
        MathSymbolAST result =
                new MathSymbolAST.MathSymbolExprBuilder(null, null,
                        new ResolveToken("implies"), null).arguments(d1, d2)
                        .build();

        result.setMathType(BOOLEAN);
        return result;
    }
}