/**
 * ModuleAST.java
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
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.decl.ModuleParameterAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

import java.util.Collections;
import java.util.List;

/**
 * <p>The parent class of all <tt>RESOLVE</tt> module types.</p>
 */
public abstract class ModuleAST extends ResolveAST {

    private final Token myName;
    private final ImportCollectionAST myImports;
    private final List<ModuleParameterAST> myModuleParams;
    private final ExprAST myRequires;
    private final BlockAST myBodyBlock;

    public ModuleAST(Token start, Token stop, Token name,
            ImportCollectionAST imports, List<ModuleParameterAST> params,
            ExprAST req, BlockAST block) {
        super(start, stop);
        myName = name;

        myModuleParams = params;
        myImports = imports;
        myRequires = req;
        myBodyBlock = block;
    }

    public Token getName() {
        return myName;
    }

    public List<ModuleParameterAST> getParameters() {
        return myModuleParams;
    }

    public ImportCollectionAST getImportBlock() {
        return myImports;
    }

    public ExprAST getRequires() {
        return myRequires;
    }

    public BlockAST getBodyBlock() {
        return myBodyBlock;
    }

    public boolean appropriateForTranslation() {
        return true;
    }

    public boolean appropriateForImport() {
        return true;
    }

    //Todo ImplModuleAST
    public static class ImplModuleAST extends ModuleAST {

        private ImplModuleAST(ImplModuleBuilder builder) {
            super(builder.getStart(), builder.getStop(), builder.getName(),
                    builder.usesBlock, Collections
                            .<ModuleParameterAST> emptyList(),
                    builder.requires, builder.block);
        }

        public static class ImplModuleBuilder
                extends
                    ModuleBuilderExtension<ImplModuleBuilder> {

            public ImplModuleBuilder(Token start, Token stop, Token name) {
                super(start, stop, name);
            }

            @Override
            public ImplModuleAST build() {
                return new ImplModuleAST(this);
            }
        }
    }

    public static class PrecisAST extends ModuleAST {

        private PrecisAST(PrecisBuilder builder) {
            super(builder.getStart(), builder.getStop(), builder.getName(),
                    builder.usesBlock, Collections
                            .<ModuleParameterAST> emptyList(), null,
                    builder.block);
        }

        public static class PrecisBuilder
                extends
                    ModuleBuilderExtension<PrecisBuilder> {

            public PrecisBuilder(Token start, Token stop, Token name) {
                super(start, stop, name);
            }

            @Override
            public PrecisAST build() {
                return new PrecisAST(this);
            }
        }
    }

    /**
     * <p>A <code>SpecModuleAST</code> is <tt>RESOLVE</tt>'s abstract syntax
     * encapsulation of an 'interface-like-module' containing formal
     * specifications for user defined types and operations.</p>
     */
    public static class SpecModuleAST extends ModuleAST {

        private final Token myConceptName;

        private SpecModuleAST(SpecModuleBuilder builder) {
            super(builder.getStart(), builder.getStop(), builder.getName(),
                    builder.usesBlock, Collections
                            .<ModuleParameterAST> emptyList(), null,
                    builder.block);
            myConceptName = builder.conceptName;
        }

        public Token getConceptName() {
            return myConceptName;
        }

        public static class SpecModuleBuilder
                extends
                    ModuleBuilderExtension<SpecModuleBuilder> {

            protected Token conceptName;

            public SpecModuleBuilder(Token start, Token stop, Token name) {
                super(start, stop, name);
            }

            public SpecModuleBuilder concept(Token t) {
                this.conceptName = t;
                return this;
            }

            @Override
            public SpecModuleAST build() {
                return new SpecModuleAST(this);
            }
        }
    }
}
