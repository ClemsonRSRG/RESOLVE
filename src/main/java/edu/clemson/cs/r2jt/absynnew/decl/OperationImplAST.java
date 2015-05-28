/**
 * OperationImplAST.java
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
import edu.clemson.cs.r2jt.absynnew.NamedTypeAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.absynnew.stmt.StmtAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>An <code>OperationImplAST</code> represents the implementation of an
 * {@link OperationSigAST}. This means that both private facility operations and
 * procedures implementing a conceptual contract are represented by this
 * class.</p>
 *
 * <p>In case one needs to differentiate between the two, refer to the
 * <code>{@link #implementsContract()}</code> method.</p>
 */
public class OperationImplAST extends OperationAST {

    /**
     * <p>If <code>true</code>, then we represent a procedure in a realization
     * implementing an {@link OperationSigAST}; otherwise we're a private
     * operation within some facility module.</p>
     */
    private final boolean myImplementsContractFlag;

    private final List<VariableAST> myVariables;
    private final List<StmtAST> myStatements;

    private OperationImplAST(OperationImplBuilder builder) {
        super(builder.getStart(), builder.getStop(), builder.name,
                builder.parameters, builder.returnType, builder.requires,
                builder.ensures);

        myImplementsContractFlag = builder.implementsContract;
        myVariables = builder.variables;
        myStatements = builder.statements;
    }

    /**
     * <p>Returns <code>true</code> iff this <code>OperationImplAST</code>
     * implements an {@link OperationSigAST} declared elsewhere;
     * <code>false</code> otherwise.</p>
     * 
     * @return <code>true</code> iff this implements a specification;
     *         <code>false</code> otherwise.
     */
    public boolean implementsContract() {
        return myImplementsContractFlag;
    }

    public List<VariableAST> getVariables() {
        return myVariables;
    }

    public List<StmtAST> getStatements() {
        return myStatements;
    }

    public static class OperationImplBuilder
            extends
                AbstractNodeBuilder<OperationImplAST> {

        protected final Token name;

        protected List<ParameterAST> parameters = new ArrayList<ParameterAST>();
        protected List<VariableAST> variables = new ArrayList<VariableAST>();
        protected List<StmtAST> statements = new ArrayList<StmtAST>();

        protected NamedTypeAST returnType = null;
        protected boolean recursive, implementsContract = false;

        protected ExprAST requires, ensures;

        public OperationImplBuilder(Token start, Token stop, Token name) {
            super(start, stop);
            this.name = name;
        }

        public OperationImplBuilder(OperationSigAST sig) {
            this(sig.getStart(), sig.getStop(), sig.getName());
            parameters.addAll(sig.getParameters());
            returnType = sig.getReturnType();
            requires = sig.getRequires();
            ensures = sig.getEnsures();
        }

        public OperationImplBuilder recursive(boolean e) {
            recursive = e;
            return this;
        }

        public OperationImplBuilder implementsContract(boolean e) {
            implementsContract = e;
            return this;
        }

        public OperationImplBuilder parameters(ParameterAST... e) {
            parameters(Arrays.asList(e));
            return this;
        }

        public OperationImplBuilder parameters(List<ParameterAST> e) {
            sanityCheckAdditions(e);
            parameters.addAll(e);
            return this;
        }

        public OperationImplBuilder returnType(NamedTypeAST e) {
            returnType = e;
            return this;
        }

        public OperationImplBuilder requires(ExprAST e) {
            requires = e;
            return this;
        }

        public OperationImplBuilder ensures(ExprAST e) {
            ensures = e;
            return this;
        }

        public OperationImplBuilder localVariables(VariableAST... e) {
            localVariables(Arrays.asList(e));
            return this;
        }

        public OperationImplBuilder localVariables(List<VariableAST> e) {
            sanityCheckAdditions(e);
            variables.addAll(e);
            return this;
        }

        public OperationImplBuilder statements(List<StmtAST> e) {
            sanityCheckAdditions(e);
            statements.addAll(e);
            return this;
        }

        @Override
        public OperationImplAST build() {
            return new OperationImplAST(this);
        }
    }
}