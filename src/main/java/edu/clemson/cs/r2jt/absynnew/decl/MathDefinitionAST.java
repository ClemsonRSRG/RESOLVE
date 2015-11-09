/**
 * MathDefinitionAST.java
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
import edu.clemson.cs.r2jt.absynnew.MathTypeAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mathematical definition. A {@code MathDefinitionAST} comes in three
 * flavors: Inductive, categorical, and standard. The right hand side is
 * currently optional in all except inductive -- where it is enforced.
 */
public class MathDefinitionAST extends DeclAST {

    public static enum DefinitionType {
        INDUCTIVE, STANDARD, DEFINES
    }

    private final List<MathVariableAST> myParameters;
    private final MathTypeAST myReturnType;
    private final DefinitionType myDefinitionType;

    /**
     * If this {@code MathDefinitionDeclAST} represents an non-inductive
     * definition, then both <code>myInductiveBaseCase == null</code> and
     * <code>myInductiveHypothesis == null</code>. In this case,
     * <code>myStandardBody</code> may be <code>null</code> -- depending on
     * whether or not users provided a rhs with their definition.</p>
     *
     * <p>Otherwise we represent an inductive definition and
     * <code>myStandardBody</code> <strong>will always</strong> be
     * <code>null</code>, while the the base case and inductive hypothesis
     * {@link ExprAST}s will never be <code>null</code> (inductive definitions
     * currently <em>require</em> a body).</p>
     */
    private final ExprAST myStandardBody;
    private final ExprAST myInductiveBaseCase, myInductiveHypothesis;

    private MathDefinitionAST(DefinitionBuilder builder) {
        super(builder.getStart(), builder.getStop(), builder.name);
        myReturnType = builder.returnType;
        myParameters = builder.parameters;
        myDefinitionType = builder.type;

        myStandardBody = builder.standardBody;
        myInductiveBaseCase = builder.inductiveBaseCase;
        myInductiveHypothesis = builder.inductiveHypothesis;
    }

    public DefinitionType getDefinitionType() {
        return myDefinitionType;
    }

    public MathTypeAST getReturnType() {
        return myReturnType;
    }

    public List<MathVariableAST> getParameters() {
        return myParameters;
    }

    /**
     * <p>Returns the (possibly-<code>null</code>) right hand side of this
     * <code>MathDefinitionAST</code>.</p>
     *
     * @return The {@link ExprAST} representing the body; <code>null</code> if
     *          this <code>MathDefinitionAST</code> is inductive.
     */
    public ExprAST getDefinitionBody() {
        return myStandardBody;
    }

    /**
     * <p>Returns a non-<code>null</code> expression representing the base case
     * of this <code>MathDefinitionAST</code> iff our {@link DefinitionType} is
     * <code>INDUCTIVE</code>.</p>
     *
     * @return The {@link ExprAST} representing the base case if we're an
     *         inductive definition; <code>null</code> if we're non-inductive.
     */
    public ExprAST getInductiveBaseCase() {
        return myInductiveBaseCase;
    }

    public ExprAST getInductiveHypothesis() {
        return myInductiveHypothesis;
    }

    public static class DefinitionBuilder
            extends
                AbstractNodeBuilder<MathDefinitionAST> {

        protected Token name;
        protected MathTypeAST returnType;
        protected DefinitionType type;

        protected ExprAST standardBody;
        protected ExprAST inductiveBaseCase, inductiveHypothesis;

        protected final List<MathVariableAST> parameters =
                new ArrayList<MathVariableAST>();

        public DefinitionBuilder(Token start, Token stop) {
            super(start, stop);
        }

        public DefinitionBuilder name(Token e) {
            name = e;
            return this;
        }

        public DefinitionBuilder returnType(MathTypeAST e) {
            returnType = e;
            return this;
        }

        public DefinitionBuilder type(DefinitionType e) {
            type = e;
            return this;
        }

        public DefinitionBuilder standardBody(ExprAST e) {
            standardBody = e;
            return this;
        }

        public DefinitionBuilder inductiveBaseCase(ExprAST e) {
            inductiveBaseCase = e;
            return this;
        }

        public DefinitionBuilder inductiveHypo(ExprAST e) {
            inductiveHypothesis = e;
            return this;
        }

        public DefinitionBuilder parameters(MathVariableAST... e) {
            parameters(Arrays.asList(e));
            return this;
        }

        public DefinitionBuilder parameters(List<MathVariableAST> e) {
            sanityCheckAdditions(e);
            parameters.addAll(e);
            return this;
        }

        @Override
        public MathDefinitionAST build() {
            if (name == null) {
                throw new IllegalStateException("definition w/o name; all "
                        + "must be named");
            }
            if (returnType == null) {
                throw new IllegalStateException("attempting to build a "
                        + "definition with null return type");
            }
            if (inductiveHypothesis != null || inductiveBaseCase != null) {
                if (type != DefinitionType.INDUCTIVE) {
                    throw new IllegalStateException(
                            "only inductive defintions "
                                    + "may be provided with a base case and inductive "
                                    + "hypothesis");
                }
            }
            return new MathDefinitionAST(this);
        }
    }
}