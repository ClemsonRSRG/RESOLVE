/**
 * TypeModelAST.java
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
import edu.clemson.cs.r2jt.absynnew.InitFinalAST;
import edu.clemson.cs.r2jt.absynnew.MathTypeAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.parsing.ResolveParser;
import org.antlr.v4.runtime.Token;

/**
 * <p>A <code>TypeModelAST</code> is <tt>RESOLVE</tt>'s mechanism of allowing
 * users to abstractly model and specify their own types. Instances of this
 * class are permitted within the following modules:</p>
 *
 * {@link edu.clemson.cs.r2jt.absynnew.ModuleAST.ConceptAST} or .
 */
public class TypeModelAST extends DeclAST {

    /**
     * <p>A <em>model</em>, in terms of a given type declaration, refers to the
     * user defined mathematical concept modeling that type. For
     * instance, a <code>Stack</code> <pre>is modeled by Str(Entry)</pre>.</p>
     */
    private final MathTypeAST myModelType;

    private final Token myExemplar;
    private final ExprAST myConstraint;

    private final InitFinalAST.TypeInitAST myInitialization;
    private final InitFinalAST.TypeFinalAST myFinalization;

    private TypeModelAST(TypeDeclBuilder builder) {
        super(builder.getStart(), builder.getStop(), builder.name);
        myModelType = builder.modelType;
        myExemplar = builder.exemplar;
        myConstraint = builder.constraint;
        myInitialization = builder.initialization;
        myFinalization = builder.finalization;
    }

    public MathTypeAST getModel() {
        return myModelType;
    }

    public ExprAST getConstraint() {
        return myConstraint;
    }

    public Token getExemplar() {
        return myExemplar;
    }

    /**
     * <p>Returns the {@link InitFinalAST.TypeInitAST} associated with this
     * <code>TypeModelAST</code>.</p>
     *
     * @return An initialization item.
     */
    public InitFinalAST.TypeInitAST getInitialization() {
        return myInitialization;
    }

    public InitFinalAST.TypeFinalAST getFinalization() {
        return myFinalization;
    }

    /**
     * <p>Allows for building-up the component pieces of a {@link
     * TypeModelAST} one by one, thus avoiding the need to for a large,
     * unwieldy constructor.</p>
     */
    public static class TypeDeclBuilder
            extends
                AbstractNodeBuilder<TypeModelAST> {

        public MathTypeAST modelType;

        public final Token name, exemplar;
        public ExprAST constraint;

        public InitFinalAST.TypeInitAST initialization;
        public InitFinalAST.TypeFinalAST finalization;

        public TypeDeclBuilder(ResolveParser.TypeModelDeclContext ctx) {
            super(ctx);
            name = ctx.name;
            exemplar = ctx.exemplar;
        }

        public TypeDeclBuilder model(MathTypeAST e) {
            modelType = e;
            return this;
        }

        public TypeDeclBuilder constraint(ExprAST e) {
            constraint = e;
            return this;
        }

        public TypeDeclBuilder init(InitFinalAST.TypeInitAST e) {
            initialization = e;
            return this;
        }

        public TypeDeclBuilder finalize(InitFinalAST.TypeFinalAST e) {
            finalization = e;
            return this;
        }

        @Override
        public TypeModelAST build() {
            if (modelType == null) {
                throw new IllegalStateException("null model on type " + name);
            }
            return new TypeModelAST(this);
        }
    }
}