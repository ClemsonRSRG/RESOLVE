/**
 * TypeRepresentationAST.java
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
import edu.clemson.cs.r2jt.absynnew.TypeAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import org.antlr.v4.runtime.Token;

public class TypeRepresentationAST extends DeclAST {

    private final TypeAST myRepresentationType;
    private final ExprAST myConvention, myCorrespondence;

    private final InitFinalAST myInitialization, myFinalization;

    private TypeRepresentationAST(RepresentationBuilder builder) {
        super(builder.getStart(), builder.getStop(), builder.name);

        myRepresentationType = builder.representation;
        myConvention = builder.convention;
        myCorrespondence = builder.correspondence;
        myInitialization = builder.initialization;
        myFinalization = builder.finalization;
    }

    public TypeAST getRepresentation() {
        return myRepresentationType;
    }

    public ExprAST getConvention() {
        return myConvention;
    }

    public ExprAST getCorrespondence() {
        return myCorrespondence;
    }

    public InitFinalAST getInitialization() {
        return myInitialization;
    }

    public InitFinalAST getFinalization() {
        return myFinalization;
    }

    public static class RepresentationBuilder
            extends
                AbstractNodeBuilder<TypeRepresentationAST> {

        protected final Token name;
        protected TypeAST representation;

        protected ExprAST convention, correspondence;
        protected InitFinalAST initialization, finalization;

        public RepresentationBuilder(Token start, Token stop, Token name) {
            super(start, stop);
            this.name = name;
        }

        public RepresentationBuilder representation(TypeAST e) {
            sanityCheckAddition(e);
            representation = e;
            return this;
        }

        public RepresentationBuilder correspondence(ExprAST e) {
            correspondence = e;
            return this;
        }

        public RepresentationBuilder convention(ExprAST e) {
            convention = e;
            return this;
        }

        public RepresentationBuilder initialization(InitFinalAST e) {
            initialization = e;
            return this;
        }

        public RepresentationBuilder finalization(InitFinalAST e) {
            finalization = e;
            return this;
        }

        @Override
        public TypeRepresentationAST build() {
            if (correspondence == null) {
                correspondence =
                        new MathSymbolAST.MathSymbolExprBuilder("true")
                                .literal(true).build();
            }
            if (convention == null) {
                convention =
                        new MathSymbolAST.MathSymbolExprBuilder("true")
                                .literal(true).build();
            }
            return new TypeRepresentationAST(this);
        }
    }
}
