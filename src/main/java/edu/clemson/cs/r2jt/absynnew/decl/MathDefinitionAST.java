/**
 * MathDefinitionAST.java
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
import edu.clemson.cs.r2jt.absynnew.MathTypeAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>A mathematical definition. A <code>MathDefinitionAST</code> comes in three
 * flavors: Inductive, categorical, and standard. The right hand side is
 * optional in all.</p>
 */
public class MathDefinitionAST extends DeclAST {

    public static enum DefinitionType {

        INDUCTIVE {

            @Override
            public String getTemplateName() {
                return "InductiveDefAST";
            }
        },
        STANDARD {

            @Override
            public String getTemplateName() {
                return "StandardDefAST";
            }
        },
        DEFINES {

            @Override
            public String getTemplateName() {
                return "DefinesDefAST";
            }
        };

        public abstract String getTemplateName();
    }

    private final List<MathVariableAST> myParameters;
    private final MathTypeAST myReturnType;

    private MathDefinitionAST(DefinitionBuilder builder) {
        super(builder.getStart(), builder.getStop(), builder.name);
        myReturnType = builder.returnType;
        myParameters = builder.parameters;
    }

    public static class DefinitionBuilder
            extends
                AbstractNodeBuilder<MathDefinitionAST> {

        protected final Token name;
        protected MathTypeAST returnType;

        protected final List<MathVariableAST> parameters =
                new ArrayList<MathVariableAST>();

        public DefinitionBuilder(Token start, Token stop, Token name) {
            super(start, stop);
            this.name = name;
        }

        public DefinitionBuilder returnType(MathTypeAST e) {
            returnType = e;
            return this;
        }

        public DefinitionBuilder parameters(MathVariableAST ... e) {
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
            return new MathDefinitionAST(this);
        }
    }
}