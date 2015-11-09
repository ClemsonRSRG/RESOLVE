/**
 * ModuleAST.java
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
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.decl.ModuleParameterAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * The parent class of all module types.
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

    public ImportCollectionAST getImports() {
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

    public static class ConceptImplModuleAST extends ModuleAST {

        private final Token myConcept;

        public ConceptImplModuleAST(Token start, Token stop, Token name,
                Token concept, ImportCollectionAST imports,
                List<ModuleParameterAST> params, ExprAST req, BlockAST block) {
            super(start, stop, name, imports, params, req, block);
            myConcept = concept;
        }

        public Token getConcept() {
            return myConcept;
        }
    }

    public static class EnhancementImplModuleAST extends ModuleAST {

        private final Token myConcept, myEnhancement;

        public EnhancementImplModuleAST(Token start, Token stop, Token name,
                Token concept, Token enhancement, ImportCollectionAST imports,
                List<ModuleParameterAST> params, ExprAST req, BlockAST block) {
            super(start, stop, name, imports, params, req, block);
            myConcept = concept;
            myEnhancement = enhancement;
        }

        public Token getConcept() {
            return myConcept;
        }

        public Token getEnhancement() {
            return myEnhancement;
        }

    }

    public static class PrecisAST extends ModuleAST {

        public PrecisAST(Token start, Token stop, Token name,
                ImportCollectionAST imports, List<ModuleParameterAST> params,
                ExprAST req, BlockAST block) {
            super(start, stop, name, imports, params, req, block);
        }
    }

    public static class ConceptModuleAST extends ModuleAST {

        public ConceptModuleAST(Token start, Token stop, Token name,
                ImportCollectionAST imports, List<ModuleParameterAST> params,
                ExprAST req, BlockAST block) {
            super(start, stop, name, imports, params, req, block);
        }
    }

    public static class EnhancementModuleAST extends ModuleAST {

        private final Token myConcept;

        public EnhancementModuleAST(Token start, Token stop, Token name,
                Token concept, ImportCollectionAST imports,
                List<ModuleParameterAST> params, ExprAST req, BlockAST block) {
            super(start, stop, name, imports, params, req, block);
            myConcept = concept;
        }
    }
}
