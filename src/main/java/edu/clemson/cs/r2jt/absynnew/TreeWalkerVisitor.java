/**
 * TreeWalkerVisitor.java
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

import edu.clemson.cs.r2jt.absynnew.decl.*;
import edu.clemson.cs.r2jt.absynnew.expr.*;
import edu.clemson.cs.r2jt.absynnew.stmt.CallAST;
import edu.clemson.cs.r2jt.absynnew.stmt.StmtAST;

public abstract class TreeWalkerVisitor {

    public void preAny(ResolveAST e) {}

    public void postAny(ResolveAST e) {}

    public void preEnhancementAST(ModuleAST.EnhancementAST e) {}

    public void midEnhancementAST(ModuleAST.EnhancementAST e,
                                  ResolveAST previous, ResolveAST next) {}

    public void postEnhancementAST(ModuleAST.EnhancementAST e) {}

    public boolean walkEnhancementAST(ModuleAST.EnhancementAST e) {
        return false;
    }

    public boolean walkOperationImplAST(OperationImplAST e) {
        return false;
    }

    public void preOperationImplAST(OperationImplAST e) {}

    public void midOperationImplAST(OperationImplAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postOperationImplAST(OperationImplAST e) {}

    public boolean walkTypeModelAST(TypeModelAST e) {
        return false;
    }

    public void preTypeModelAST(TypeModelAST e) {}

    public void midTypeModelAST(TypeModelAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postTypeModelAST(TypeModelAST e) {}

    public boolean walkModuleAST(ModuleAST e) {
        return false;
    }

    public void preModuleAST(ModuleAST e) {}

    public void midModuleAST(ModuleAST e, ResolveAST previous, ResolveAST next) {}

    public void postModuleAST(ModuleAST e) {}

    public boolean walkModuleParameterAST(ModuleParameterAST e) {
        return false;
    }

    public void preModuleParameterAST(ModuleParameterAST e) {}

    public void midModuleParameterAST(ModuleParameterAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postModuleParameterAST(ModuleParameterAST e) {}

    public boolean walkProgIntegerRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgIntegerRefAST e) {
        return false;
    }

    public void preProgIntegerRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgIntegerRefAST e) {}

    public void midProgIntegerRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgIntegerRefAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postProgIntegerRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgIntegerRefAST e) {}

    public boolean walkModuleFinalAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.ModuleFinalAST e) {
        return false;
    }

    public void preModuleFinalAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.ModuleFinalAST e) {}

    public void midModuleFinalAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.ModuleFinalAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postModuleFinalAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.ModuleFinalAST e) {}

    public boolean walkTypeAST(TypeAST e) {
        return false;
    }

    public void preMathTypeAssertionAST(MathTypeAssertionAST e) {}

    public void midMathTypeAssertionAST(MathTypeAssertionAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postMathTypeAssertionAST(MathTypeAssertionAST e) {}

    public boolean walkMathTypeAssertionAST(MathTypeAssertionAST e) {
        return false;
    }

    public void preTypeAST(TypeAST e) {}

    public void midTypeAST(TypeAST e, ResolveAST previous, ResolveAST next) {}

    public void postTypeAST(TypeAST e) {}

    public void prePrecisAST(ModuleAST.PrecisAST e) {}

    public void midPrecisAST(ModuleAST.PrecisAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postPrecisAST(ModuleAST.PrecisAST e) {}

    public boolean walkPrecisAST(ModuleAST.PrecisAST e) {
        return false;
    }

    public boolean walkTypeParameterAST(TypeParameterAST e) {
        return false;
    }

    public void preTypeParameterAST(TypeParameterAST e) {}

    public void midTypeParameterAST(TypeParameterAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postTypeParameterAST(TypeParameterAST e) {}

    public boolean walkProgStringRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgStringRefAST e) {
        return false;
    }

    public void preProgStringRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgStringRefAST e) {}

    public void midProgStringRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgStringRefAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postProgStringRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgStringRefAST e) {}

    public boolean walkProgOperationRefAST(ProgOperationRefAST e) {
        return false;
    }

    public void preProgOperationRefAST(ProgOperationRefAST e) {}

    public void midProgOperationRefAST(ProgOperationRefAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postProgOperationRefAST(ProgOperationRefAST e) {}

    public boolean walkProgExprAST(ProgExprAST e) {
        return false;
    }

    public void preProgExprAST(ProgExprAST e) {}

    public void midProgExprAST(ProgExprAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postProgExprAST(ProgExprAST e) {}

    public boolean walkOperationSigAST(OperationSigAST e) {
        return false;
    }

    public void preOperationSigAST(OperationSigAST e) {}

    public void midOperationSigAST(OperationSigAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postOperationSigAST(OperationSigAST e) {}

    public boolean walkTypeInitAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.TypeInitAST e) {
        return false;
    }

    public void preTypeInitAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.TypeInitAST e) {}

    public void midTypeInitAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.TypeInitAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postTypeInitAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.TypeInitAST e) {}

    public boolean walkImportCollectionAST(ImportCollectionAST e) {
        return false;
    }

    public void preImportCollectionAST(ImportCollectionAST e) {}

    public void midImportCollectionAST(ImportCollectionAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postImportCollectionAST(ImportCollectionAST e) {}

    public boolean walkProgLiteralRefAST(ProgLiteralRefAST e) {
        return false;
    }

    public void preProgLiteralRefAST(ProgLiteralRefAST e) {}

    public void midProgLiteralRefAST(ProgLiteralRefAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postProgLiteralRefAST(ProgLiteralRefAST e) {}

    public boolean walkInitFinalAST(InitFinalAST e) {
        return false;
    }

    public void preInitFinalAST(InitFinalAST e) {}

    public void midInitFinalAST(InitFinalAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postInitFinalAST(InitFinalAST e) {}

    public boolean walkModuleInitAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.ModuleInitAST e) {
        return false;
    }

    public void preModuleInitAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.ModuleInitAST e) {}

    public void midModuleInitAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.ModuleInitAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postModuleInitAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.ModuleInitAST e) {}

    public boolean walkOperationAST(OperationAST e) {
        return false;
    }

    public void preOperationAST(OperationAST e) {}

    public void midOperationAST(OperationAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postOperationAST(OperationAST e) {}

    public boolean walkMathTypeTheoremAST(MathTypeTheoremAST e) {
        return false;
    }

    public void preMathTypeTheoremAST(MathTypeTheoremAST e) {}

    public void midMathTypeTheoremAST(MathTypeTheoremAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postMathTypeTheoremAST(MathTypeTheoremAST e) {}

    public boolean walkProgCharacterRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgCharacterRefAST e) {
        return false;
    }

    public void preProgCharacterRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgCharacterRefAST e) {}

    public void midProgCharacterRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgCharacterRefAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postProgCharacterRefAST(
            edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST.ProgCharacterRefAST e) {}

    public boolean walkCallAST(CallAST e) {
        return false;
    }

    public void preCallAST(CallAST e) {}

    public void midCallAST(CallAST e, ResolveAST previous, ResolveAST next) {}

    public void postCallAST(CallAST e) {}

    public boolean walkMathTheoremAST(MathTheoremAST e) {
        return false;
    }

    public void preMathTheoremAST(MathTheoremAST e) {}

    public void midMathTheoremAST(MathTheoremAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postMathTheoremAST(MathTheoremAST e) {}

    public boolean walkMathTupleAST(MathTupleAST e) {
        return false;
    }

    public void preMathTupleAST(MathTupleAST e) {}

    public void midMathTupleAST(MathTupleAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postMathTupleAST(MathTupleAST e) {}

    public boolean walkFacilityAST(
            edu.clemson.cs.r2jt.absynnew.ModuleAST.FacilityAST e) {
        return false;
    }

    public void preFacilityAST(
            edu.clemson.cs.r2jt.absynnew.ModuleAST.FacilityAST e) {}

    public void midFacilityAST(
            edu.clemson.cs.r2jt.absynnew.ModuleAST.FacilityAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postFacilityAST(
            edu.clemson.cs.r2jt.absynnew.ModuleAST.FacilityAST e) {}

    public void preFacilityAST(FacilityAST e) {}

    public void midFacilityAST(FacilityAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postFacilityAST(FacilityAST e) {}

    public boolean walkFacilityAST(FacilityAST e) {
        return false;
    }

    public boolean walkParameterAST(ParameterAST e) {
        return false;
    }

    public void preParameterAST(ParameterAST e) {}

    public void midParameterAST(ParameterAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postParameterAST(ParameterAST e) {}

    public boolean walkMathDefinitionAST(MathDefinitionAST e) {
        return false;
    }

    public void preMathDefinitionAST(MathDefinitionAST e) {}

    public void midMathDefinitionAST(MathDefinitionAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postMathDefinitionAST(MathDefinitionAST e) {}

    public boolean walkExprAST(ExprAST e) {
        return false;
    }

    public void preExprAST(ExprAST e) {}

    public void midExprAST(ExprAST e, ResolveAST previous, ResolveAST next) {}

    public void postExprAST(ExprAST e) {}

    public boolean walkBlockAST(BlockAST e) {
        return false;
    }

    public void preBlockAST(BlockAST e) {}

    public void midBlockAST(BlockAST e, ResolveAST previous, ResolveAST next) {}

    public void postBlockAST(BlockAST e) {}

    public boolean walkNamedTypeAST(NamedTypeAST e) {
        return false;
    }

    public void preNamedTypeAST(NamedTypeAST e) {}

    public void midNamedTypeAST(NamedTypeAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postNamedTypeAST(NamedTypeAST e) {}

    public boolean walkMathTypeAST(MathTypeAST e) {
        return false;
    }

    public void preMathTypeAST(MathTypeAST e) {}

    public void midMathTypeAST(MathTypeAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postMathTypeAST(MathTypeAST e) {}

    public boolean walkMathVariableAST(MathVariableAST e) {
        return false;
    }

    public void preMathVariableAST(MathVariableAST e) {}

    public void midMathVariableAST(MathVariableAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postMathVariableAST(MathVariableAST e) {}

    public boolean walkConceptAST(
            edu.clemson.cs.r2jt.absynnew.ModuleAST.ConceptAST e) {
        return false;
    }

    public void preConceptAST(
            edu.clemson.cs.r2jt.absynnew.ModuleAST.ConceptAST e) {}

    public void midConceptAST(
            edu.clemson.cs.r2jt.absynnew.ModuleAST.ConceptAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postConceptAST(
            edu.clemson.cs.r2jt.absynnew.ModuleAST.ConceptAST e) {}

    public boolean walkVariableAST(VariableAST e) {
        return false;
    }

    public void preVariableAST(VariableAST e) {}

    public void midVariableAST(VariableAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postVariableAST(VariableAST e) {}

    public boolean walkTypeFinalAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.TypeFinalAST e) {
        return false;
    }

    public void preTypeFinalAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.TypeFinalAST e) {}

    public void midTypeFinalAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.TypeFinalAST e,
            ResolveAST previous, ResolveAST next) {}

    public void postTypeFinalAST(
            edu.clemson.cs.r2jt.absynnew.InitFinalAST.TypeFinalAST e) {}

    public boolean walkMathSymbolAST(MathSymbolAST e) {
        return false;
    }

    public void preMathQuantifiedAST(MathQuantifiedAST e) {}

    public void midMathQuantifiedAST(MathQuantifiedAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postMathQuantifiedAST(MathQuantifiedAST e) {}

    public boolean walkMathQuantifiedAST(MathQuantifiedAST e) {
        return false;
    }

    public void preMathSymbolAST(MathSymbolAST e) {}

    public void midMathSymbolAST(MathSymbolAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postMathSymbolAST(MathSymbolAST e) {}

    public boolean walkStmtAST(StmtAST e) {
        return false;
    }

    public void preStmtAST(StmtAST e) {}

    public void midStmtAST(StmtAST e, ResolveAST previous, ResolveAST next) {}

    public void postStmtAST(StmtAST e) {}

    public boolean walkProgNameRefAST(ProgNameRefAST e) {
        return false;
    }

    public void preMathSetAST(MathSetAST e) {}

    public void midMathSetAST(MathSetAST e, ResolveAST next, ResolveAST previous) {}

    public void postMathSetAST(MathSetAST e) {}

    public boolean walkMathSetAST(MathSetAST e) {
        return false;
    }

    public void preProgNameRefAST(ProgNameRefAST e) {}

    public void midProgNameRefAST(ProgNameRefAST e, ResolveAST previous,
            ResolveAST next) {}

    public void postProgNameRefAST(ProgNameRefAST e) {}

    public boolean walkDeclAST(DeclAST e) {
        return false;
    }

    public void preDeclAST(DeclAST e) {}

    public void midDeclAST(DeclAST e, ResolveAST previous, ResolveAST next) {}

    public void postDeclAST(DeclAST e) {}
}