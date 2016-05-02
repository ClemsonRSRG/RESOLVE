/**
 * MathSymbolAST.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absynnew.expr;

import edu.clemson.cs.r2jt.absynnew.AbstractNodeBuilder;
import edu.clemson.cs.r2jt.absynnew.ResolveToken;
import edu.clemson.cs.r2jt.misc.Utils;
import edu.clemson.cs.r2jt.parsing.ResolveLexer;
import edu.clemson.cs.r2jt.rewriteprover.absyn2.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate2.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.*;

/**
 * An {@code MathSymbolAST} represents a reference to a named element such
 * as a variable, constant, or function. More specifically, all three are
 * represented as function calls, with the former two represented as functions
 * with no arguments.
 */
public class MathSymbolAST extends ExprAST {

    public static enum DisplayStyle {
        INFIX, OUTFIX, PREFIX
    }

    private final Token myLeftPrint, myRightPrint, myName;

    private final List<ExprAST> myArguments = new ArrayList<ExprAST>();
    private final boolean myLiteralFlag, myIncomingFlag;
    private SymbolTableEntry.Quantification myQuantification;
    private final DisplayStyle myDisplayStyle;

    private MathSymbolAST(MathSymbolExprBuilder builder) {
        super(builder.getStart(), builder.getStop());

        myName = builder.name;
        myLeftPrint = builder.lprint;
        myRightPrint = builder.rprint;

        myArguments.addAll(builder.arguments);
        myLiteralFlag = builder.literal;
        myIncomingFlag = builder.incoming;
        myDisplayStyle = builder.style;
        myQuantification = builder.quantification;
    }

    /**
     * This class represents function applications.  The type of a
     * function application is the type of the range of the function.  Often
     * we'd like to think about the type of the function itself, not
     * the type of the result of its application.  Unfortunately our AST does
     * not consider that the 'function' part of a FunctionExp (as distinct from
     * its parameters) might be a first-class citizen with a type of its own.
     * This method emulates retrieving the (not actually extant) first-class
     * function part and guessing its type.  In this case, the guess is
     * "conservative", in that we guess the smallest set that can't be
     * contradicted by the available information.  For nodes without a true,
     * first-class function to consult (which, at the moment, is all of them),
     * this means that for the formal parameter types, we'll guess the types of
     * the actual parameters, and for the return type we'll guess
     * {@code Empty_Set} (since we have no information about how the
     * return value is used.)  This guarantees that the type we return will be
     * a subset of the actual type of the function the RESOLVE programmer
     * intends (assuming she has called it correctly.)
     */
    public MTFunction getConservativePreApplicationType(TypeGraph g) {
        List<MTType> subTypes = new LinkedList<MTType>();

        for (ExprAST arg : myArguments) {
            subTypes.add(arg.getMathType());
        }
        return new MTFunction(g, g.EMPTY_SET, subTypes);
    }

    public Token getName() {
        return myName;
    }

    public List<ExprAST> getArguments() {
        return myArguments;
    }

    public boolean isIncoming() {
        return myIncomingFlag;
    }

    public boolean isFunction() {
        return myArguments.size() > 0;
    }

    public DisplayStyle getStyle() {
        return myDisplayStyle;
    }

    public void setQuantification(SymbolTableEntry.Quantification q) {
        myQuantification = q;
    }

    //Todo: Figure out what qualifiers are going to look like in a
    //mathematical setting.
    public Token getQualifier() {
        return null;
    }

    public SymbolTableEntry.Quantification getQuantification() {
        return myQuantification;
    }

    @Override
    public List<ExprAST> getSubExpressions() {
        return myArguments;
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {
        myArguments.set(index, e);
    }

    @Override
    public boolean isLiteral() {
        return myLiteralFlag;
    }

    @Override
    protected ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        List<ExprAST> newArguments = new ArrayList<ExprAST>();

        for (ExprAST e : myArguments) {
            newArguments.add(substitute(e, substitutions));
        }

        MathSymbolAST newName =
                new MathSymbolExprBuilder(getStart(), getStop(), myName, null)
                        .arguments(myArguments).literal(myLiteralFlag)
                        .quantification(myQuantification).incoming(
                                myIncomingFlag).build();

        if (substitutions.containsKey(newName)) {
            //Note that there's no particular mathematical justification why
            //we can only replace a function with a different function NAME (as
            //opposed to a function-valued expression), but we have no way of
            //representing such a thing.  It doesn't tend to come up, but if it
            //ever did, this would throw a ClassCastException.
            newName =
                    new MathSymbolExprBuilder(getStart(), getStop(),
                            ((MathSymbolAST) substitutions.get(newName))
                                    .getName(), null).arguments(myArguments)
                            .literal(myLiteralFlag).quantification(
                                    myQuantification).incoming(myIncomingFlag)
                            .build();
        }

        MathSymbolAST result =
                new MathSymbolExprBuilder(getStart(), getStop(), newName
                        .getName(), null).arguments(myArguments).literal(
                        myLiteralFlag).quantification(myQuantification)
                        .incoming(myIncomingFlag).build();

        result.setMathType(myMathType);
        result.setMathTypeValue(myMathTypeValue);
        return result;
    }

    @Override
    public boolean equivalent(ExprAST e) {
        boolean result = (e instanceof MathSymbolAST);

        if (result) {
            MathSymbolAST eAsSymbol = (MathSymbolAST) e;

            result =
                    myName.equals(((MathSymbolAST) e).myName)
                            && argumentsEquivalent(myArguments,
                                    eAsSymbol.myArguments)
                            && myQuantification == eAsSymbol.myQuantification;
        }
        return result;
    }

    private boolean argumentsEquivalent(List<ExprAST> original,
            List<ExprAST> compare) {
        boolean result = true;

        Iterator<ExprAST> args1 = original.iterator();
        Iterator<ExprAST> args2 = compare.iterator();

        while (result && args1.hasNext() && args2.hasNext()) {
            result = args1.next().equivalent(args2.next());
        }
        return result;
    }

    @Override
    public ExprAST copy() {

        Token newName = new ResolveToken(myName.getText());
        List<ExprAST> newArgs = new ArrayList<ExprAST>(myArguments);

        MathSymbolAST result =
                new MathSymbolExprBuilder(getStart(), getStop(), newName, null)
                        .arguments(newArgs).quantification(myQuantification)
                        .style(myDisplayStyle).literal(myLiteralFlag).incoming(
                                myIncomingFlag).build();

        result.setMathType(myMathType);
        result.setMathTypeValue(myMathTypeValue);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        if (isFunction()) {
            if (myDisplayStyle == DisplayStyle.INFIX) {
                result.append(myArguments.get(0)).append(" " + myName + " ")
                        .append(myArguments.get(1));
            }
            else if (myDisplayStyle == DisplayStyle.OUTFIX) {
                result.append(myLeftPrint).append(myArguments.get(0)).append(
                        myRightPrint);
            }
            else {
                result.append(myName.getText()).append("(");
                result.append(Utils.join(myArguments, ", ")).append(")");
            }
        }
        else {
            result.append(myName.getText());
        }
        return result.toString();
    }

    /**
     * A builder for {@link MathSymbolAST}s intended to ease construction of
     * math symbols needed on-the-fly in both
     * {@link edu.clemson.cs.r2jt.absynnew.TreeBuildingVisitor} and
     * {@link edu.clemson.cs.r2jt.typereasoning.TypeGraph}.
     */
    public static class MathSymbolExprBuilder
            extends
                AbstractNodeBuilder<MathSymbolAST> {

        protected final Token name, lprint, rprint;

        protected boolean incoming = false;
        protected boolean literal = false;

        protected DisplayStyle style = DisplayStyle.PREFIX;
        protected SymbolTableEntry.Quantification quantification =
                SymbolTableEntry.Quantification.NONE;

        protected final List<ExprAST> arguments = new ArrayList<ExprAST>();

        public MathSymbolExprBuilder(String name) {
            this(null, null, new ResolveToken(ResolveLexer.IDENTIFIER, name),
                    null);
        }

        public MathSymbolExprBuilder(Token start, Token stop, Token lprint,
                Token rprint) {
            super(start, stop);

            if (rprint == null) {
                if (lprint == null) {
                    throw new IllegalStateException("null name; all math "
                            + "symbols must be named.");
                }
                rprint = lprint;
                this.name = lprint;
            }
            else {
                this.name =
                        new ResolveToken(ResolveLexer.IDENTIFIER, lprint
                                .getText()
                                + "..." + rprint.getText());
            }
            this.lprint = lprint;
            this.rprint = rprint;
        }

        public MathSymbolExprBuilder(ParserRuleContext ctx, Token lprint,
                Token rprint) {
            this(ctx.getStart(), ctx.getStop(), lprint, rprint);
        }

        public MathSymbolExprBuilder literal(boolean e) {
            literal = e;
            return this;
        }

        public MathSymbolExprBuilder quantification(
                SymbolTableEntry.Quantification q) {
            quantification = q;
            return this;
        }

        public MathSymbolExprBuilder style(DisplayStyle e) {
            style = e;
            return this;
        }

        public MathSymbolExprBuilder incoming(boolean e) {
            incoming = e;
            return this;
        }

        public MathSymbolExprBuilder arguments(ExprAST... e) {
            arguments(Arrays.asList(e));
            return this;
        }

        public MathSymbolExprBuilder arguments(Collection<ExprAST> args) {
            sanityCheckAdditions(args);
            arguments.addAll(args);
            return this;
        }

        @Override
        public MathSymbolAST build() {
            return new MathSymbolAST(this);
        }
    }
}