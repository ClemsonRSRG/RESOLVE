/**
 * TypeModelAST.java
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
package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.AbstractNodeBuilder;
import edu.clemson.cs.r2jt.absynnew.InitFinalAST;
import edu.clemson.cs.r2jt.absynnew.MathTypeAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

/**
 * An {@code TypeModelAST} is RESOLVE's mechanism of allowing users to
 * abstractly model and specify their own types. Instances of this class are
 * permitted within the following modules:</p>
 *
 * {@link edu.clemson.cs.r2jt.absynnew.ModuleAST.ConceptModuleAST} or
 * {@link edu.clemson.cs.r2jt.absynnew.ModuleAST.PrecisAST}.
 */
public class TypeModelAST extends DeclAST {

    /**
     * A model, in terms of a given type declaration, refers to the
     * user defined mathematical concept modeling that type. For
     * instance, {@code Stack is modeled by Str(Entry)}.
     */
    private final MathTypeAST myModelType;

    private final Token myExemplar;
    private final ExprAST myConstraint;

    private final InitFinalAST myInitialization, myFinalization;

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
     * Returns the {@link InitFinalAST} associated with this
     * {@code TypeModelAST}.
     *
     * @return An initialization item.
     */
    public InitFinalAST getInitialization() {
        return myInitialization;
    }

    public InitFinalAST getFinalization() {
        return myFinalization;
    }

    public static class TypeDeclBuilder
            extends
                AbstractNodeBuilder<TypeModelAST> {

        public MathTypeAST modelType;

        public final Token name, exemplar;
        public ExprAST constraint;

        public InitFinalAST initialization, finalization;

        public TypeDeclBuilder(Token start, Token stop, Token name,
                Token exemplar) {
            super(start, stop);
            this.name = name;
            this.exemplar = exemplar;
        }

        public TypeDeclBuilder model(MathTypeAST e) {
            modelType = e;
            return this;
        }

        public TypeDeclBuilder constraint(ExprAST e) {
            constraint = e;
            return this;
        }

        public TypeDeclBuilder init(InitFinalAST e) {
            initialization = e;
            return this;
        }

        public TypeDeclBuilder finalize(InitFinalAST e) {
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