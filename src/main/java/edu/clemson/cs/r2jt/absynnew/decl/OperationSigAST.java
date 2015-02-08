/**
 * OperationSigAST.java
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
package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.AbstractNodeBuilder;
import edu.clemson.cs.r2jt.absynnew.NamedTypeAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.parsing.ResolveParser;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>An <code>OperationDeclAST</code> encapsulates the signature of a function
 * (operation). This includes specifications in the form of <tt>requires</tt>
 * and <tt>ensures</tt> clauses, as well as a set of formal, mode-preceded
 * {@link ParameterAST}s.</p>
 *
 * <p>This class should only appear within the context of
 * {@link edu.clemson.cs.r2jt.absynnew.ModuleAST.ConceptAST}s or
 * ....</p>
 */
public class OperationSigAST extends OperationAST {

    private OperationSigAST(OperationDeclBuilder builder) {
        super(builder.getStart(), builder.getStop(), builder.name,
                builder.params, builder.returnType, builder.requires,
                builder.ensures);
    }

    public static class OperationDeclBuilder
            extends
                AbstractNodeBuilder<OperationSigAST> {

        protected final Token name;
        protected NamedTypeAST returnType;

        protected ExprAST requires, ensures;
        protected final List<ParameterAST> params =
                new ArrayList<ParameterAST>();

        public OperationDeclBuilder(Token start, Token stop, Token name) {
            super(start, stop);
            this.name = name;
        }

        public OperationDeclBuilder(ResolveParser.OperationDeclContext ctx) {
            this(ctx.getStart(), ctx.getStop(), ctx.name);
        }

        public OperationDeclBuilder requires(ExprAST e) {
            requires = e;
            return this;
        }

        public OperationDeclBuilder ensures(ExprAST e) {
            ensures = e;
            return this;
        }

        //It's ok if the return type is null--so no need to sanitycheck e
        public OperationDeclBuilder type(NamedTypeAST e) {
            returnType = e;
            return this;
        }

        public OperationDeclBuilder params(List<ParameterAST> e) {
            sanityCheckAdditions(e);
            params.addAll(e);
            return this;
        }

        @Override
        public OperationSigAST build() {
            return new OperationSigAST(this);
        }
    }
}