/**
 * TreeWalkerVisitor.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.treewalk;

import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.*;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.*;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.absyn.rawtypes.*;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.*;
import edu.clemson.cs.rsrg.absyn.clauses.*;
import edu.clemson.cs.rsrg.absyn.statements.*;
import edu.clemson.cs.rsrg.absyn.items.mathitems.*;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.cs.rsrg.absyn.items.programitems.*;
import edu.clemson.cs.rsrg.absyn.declarations.*;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.*;
import edu.clemson.cs.rsrg.absyn.*;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.*;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.*;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.*;
import edu.clemson.cs.rsrg.absyn.expressions.*;

public abstract class TreeWalkerVisitor {

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // All objects
    // -----------------------------------------------------------

    public void preAny(ResolveConceptualElement e) {}

    public void postAny(ResolveConceptualElement e) {}

    // -----------------------------------------------------------
    // Each object
    // -----------------------------------------------------------

    public boolean walkFunctionExp(FunctionExp e) {
        return false;
    }

    public void preFunctionExp(FunctionExp e) {}

    public void midFunctionExp(FunctionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFunctionExp(FunctionExp e) {}

    public boolean walkOperationDec(OperationDec e) {
        return false;
    }

    public void preOperationDec(OperationDec e) {}

    public void midOperationDec(OperationDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postOperationDec(OperationDec e) {}

    public boolean walkEqualsExp(EqualsExp e) {
        return false;
    }

    public void preEqualsExp(EqualsExp e) {}

    public void midEqualsExp(EqualsExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postEqualsExp(EqualsExp e) {}

    public boolean walkOldExp(OldExp e) {
        return false;
    }

    public void preOldExp(OldExp e) {}

    public void midOldExp(OldExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postOldExp(OldExp e) {}

    public boolean walkAbstractTypeRepresentationDec(
            AbstractTypeRepresentationDec e) {
        return false;
    }

    public void preAbstractTypeRepresentationDec(AbstractTypeRepresentationDec e) {}

    public void midAbstractTypeRepresentationDec(
            AbstractTypeRepresentationDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postAbstractTypeRepresentationDec(
            AbstractTypeRepresentationDec e) {}

    public boolean walkVarExp(VarExp e) {
        return false;
    }

    public void preVarExp(VarExp e) {}

    public void midVarExp(VarExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postVarExp(VarExp e) {}

    public boolean walkTypeRepresentationDec(TypeRepresentationDec e) {
        return false;
    }

    public void preTypeRepresentationDec(TypeRepresentationDec e) {}

    public void midTypeRepresentationDec(TypeRepresentationDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeRepresentationDec(TypeRepresentationDec e) {}

    public boolean walkSetCollectionExp(SetCollectionExp e) {
        return false;
    }

    public void preSetCollectionExp(SetCollectionExp e) {}

    public void midSetCollectionExp(SetCollectionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSetCollectionExp(SetCollectionExp e) {}

    public boolean walkProgramCharExp(ProgramCharExp e) {
        return false;
    }

    public void preProgramCharExp(ProgramCharExp e) {}

    public void midProgramCharExp(ProgramCharExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramCharExp(ProgramCharExp e) {}

    public boolean walkAltItemExp(AltItemExp e) {
        return false;
    }

    public void preAltItemExp(AltItemExp e) {}

    public void midAltItemExp(AltItemExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postAltItemExp(AltItemExp e) {}

    public boolean walkDoubleExp(DoubleExp e) {
        return false;
    }

    public void preDoubleExp(DoubleExp e) {}

    public void midDoubleExp(DoubleExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDoubleExp(DoubleExp e) {}

    public boolean walkNameTy(NameTy e) {
        return false;
    }

    public void preNameTy(NameTy e) {}

    public void midNameTy(NameTy e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postNameTy(NameTy e) {}

    public boolean walkMathDefinitionDec(MathDefinitionDec e) {
        return false;
    }

    public void preMathDefinitionDec(MathDefinitionDec e) {}

    public void midMathDefinitionDec(MathDefinitionDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathDefinitionDec(MathDefinitionDec e) {}

    public boolean walkAssertionClause(AssertionClause e) {
        return false;
    }

    public void preAssertionClause(AssertionClause e) {}

    public void midAssertionClause(AssertionClause e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAssertionClause(AssertionClause e) {}

    public boolean walkSwapStmt(SwapStmt e) {
        return false;
    }

    public void preSwapStmt(SwapStmt e) {}

    public void midSwapStmt(SwapStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postSwapStmt(SwapStmt e) {}

    public boolean walkTy(Ty e) {
        return false;
    }

    public void preTy(Ty e) {}

    public void midTy(Ty e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTy(Ty e) {}

    public boolean walkDotExp(DotExp e) {
        return false;
    }

    public void preDotExp(DotExp e) {}

    public void midDotExp(DotExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDotExp(DotExp e) {}

    public boolean walkProgramVariableDotExp(ProgramVariableDotExp e) {
        return false;
    }

    public void preProgramVariableDotExp(ProgramVariableDotExp e) {}

    public void midProgramVariableDotExp(ProgramVariableDotExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramVariableDotExp(ProgramVariableDotExp e) {}

    public boolean walkProgramExp(ProgramExp e) {
        return false;
    }

    public void preProgramExp(ProgramExp e) {}

    public void midProgramExp(ProgramExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postProgramExp(ProgramExp e) {}

    public boolean walkProgramStringExp(ProgramStringExp e) {
        return false;
    }

    public void preProgramStringExp(ProgramStringExp e) {}

    public void midProgramStringExp(ProgramStringExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramStringExp(ProgramStringExp e) {}

    public boolean walkWhileStmt(WhileStmt e) {
        return false;
    }

    public void preWhileStmt(WhileStmt e) {}

    public void midWhileStmt(WhileStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postWhileStmt(WhileStmt e) {}

    public boolean walkSpecInitFinalItem(SpecInitFinalItem e) {
        return false;
    }

    public void preSpecInitFinalItem(SpecInitFinalItem e) {}

    public void midSpecInitFinalItem(SpecInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSpecInitFinalItem(SpecInitFinalItem e) {}

    public boolean walkProgramVariableNameExp(ProgramVariableNameExp e) {
        return false;
    }

    public void preProgramVariableNameExp(ProgramVariableNameExp e) {}

    public void midProgramVariableNameExp(ProgramVariableNameExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramVariableNameExp(ProgramVariableNameExp e) {}

    public boolean walkConceptRealizModuleDec(ConceptRealizModuleDec e) {
        return false;
    }

    public void preConceptRealizModuleDec(ConceptRealizModuleDec e) {}

    public void midConceptRealizModuleDec(ConceptRealizModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptRealizModuleDec(ConceptRealizModuleDec e) {}

    public boolean walkModuleArgumentItem(ModuleArgumentItem e) {
        return false;
    }

    public void preModuleArgumentItem(ModuleArgumentItem e) {}

    public void midModuleArgumentItem(ModuleArgumentItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postModuleArgumentItem(ModuleArgumentItem e) {}

    public boolean walkDec(Dec e) {
        return false;
    }

    public void preDec(Dec e) {}

    public void midDec(Dec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDec(Dec e) {}

    public boolean walkStringExp(StringExp e) {
        return false;
    }

    public void preStringExp(StringExp e) {}

    public void midStringExp(StringExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postStringExp(StringExp e) {}

    public boolean walkMathVarDec(MathVarDec e) {
        return false;
    }

    public void preMathVarDec(MathVarDec e) {}

    public void midMathVarDec(MathVarDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postMathVarDec(MathVarDec e) {}

    public boolean walkLoopVerificationItem(LoopVerificationItem e) {
        return false;
    }

    public void preLoopVerificationItem(LoopVerificationItem e) {}

    public void midLoopVerificationItem(LoopVerificationItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postLoopVerificationItem(LoopVerificationItem e) {}

    public boolean walkVirtualListNode(VirtualListNode e) {
        return false;
    }

    public void preVirtualListNode(VirtualListNode e) {}

    public void midVirtualListNode(VirtualListNode e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVirtualListNode(VirtualListNode e) {}

    public boolean walkFacilityDec(FacilityDec e) {
        return false;
    }

    public void preFacilityDec(FacilityDec e) {}

    public void midFacilityDec(FacilityDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityDec(FacilityDec e) {}

    public boolean walkLambdaExp(LambdaExp e) {
        return false;
    }

    public void preLambdaExp(LambdaExp e) {}

    public void midLambdaExp(LambdaExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postLambdaExp(LambdaExp e) {}

    public boolean walkParameterVarDec(ParameterVarDec e) {
        return false;
    }

    public void preParameterVarDec(ParameterVarDec e) {}

    public void midParameterVarDec(ParameterVarDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postParameterVarDec(ParameterVarDec e) {}

    public boolean walkIterativeExp(IterativeExp e) {
        return false;
    }

    public void preIterativeExp(IterativeExp e) {}

    public void midIterativeExp(IterativeExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIterativeExp(IterativeExp e) {}

    public boolean walkModuleDec(ModuleDec e) {
        return false;
    }

    public void preModuleDec(ModuleDec e) {}

    public void midModuleDec(ModuleDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postModuleDec(ModuleDec e) {}

    public boolean walkPrefixExp(PrefixExp e) {
        return false;
    }

    public void prePrefixExp(PrefixExp e) {}

    public void midPrefixExp(PrefixExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postPrefixExp(PrefixExp e) {}

    public boolean walkUnaryMinusExp(UnaryMinusExp e) {
        return false;
    }

    public void preUnaryMinusExp(UnaryMinusExp e) {}

    public void midUnaryMinusExp(UnaryMinusExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postUnaryMinusExp(UnaryMinusExp e) {}

    public boolean walkModuleParameterDec(ModuleParameterDec e) {
        return false;
    }

    public void preModuleParameterDec(ModuleParameterDec e) {}

    public void midModuleParameterDec(ModuleParameterDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postModuleParameterDec(ModuleParameterDec e) {}

    public boolean walkCrossTypeExp(CrossTypeExp e) {
        return false;
    }

    public void preCrossTypeExp(CrossTypeExp e) {}

    public void midCrossTypeExp(CrossTypeExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postCrossTypeExp(CrossTypeExp e) {}

    public boolean walkPerformanceOperationDec(PerformanceOperationDec e) {
        return false;
    }

    public void prePerformanceOperationDec(PerformanceOperationDec e) {}

    public void midPerformanceOperationDec(PerformanceOperationDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceOperationDec(PerformanceOperationDec e) {}

    public boolean walkUsesItem(UsesItem e) {
        return false;
    }

    public void preUsesItem(UsesItem e) {}

    public void midUsesItem(UsesItem e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postUsesItem(UsesItem e) {}

    public boolean walkPresumeStmt(PresumeStmt e) {
        return false;
    }

    public void prePresumeStmt(PresumeStmt e) {}

    public void midPresumeStmt(PresumeStmt e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPresumeStmt(PresumeStmt e) {}

    public boolean walkInfixExp(InfixExp e) {
        return false;
    }

    public void preInfixExp(InfixExp e) {}

    public void midInfixExp(InfixExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postInfixExp(InfixExp e) {}

    public boolean walkDefinitionBodyItem(DefinitionBodyItem e) {
        return false;
    }

    public void preDefinitionBodyItem(DefinitionBodyItem e) {}

    public void midDefinitionBodyItem(DefinitionBodyItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postDefinitionBodyItem(DefinitionBodyItem e) {}

    public boolean walkProgramVariableArrayExp(ProgramVariableArrayExp e) {
        return false;
    }

    public void preProgramVariableArrayExp(ProgramVariableArrayExp e) {}

    public void midProgramVariableArrayExp(ProgramVariableArrayExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramVariableArrayExp(ProgramVariableArrayExp e) {}

    public boolean walkSharedStateDec(SharedStateDec e) {
        return false;
    }

    public void preSharedStateDec(SharedStateDec e) {}

    public void midSharedStateDec(SharedStateDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSharedStateDec(SharedStateDec e) {}

    public boolean walkTypeAssertionExp(TypeAssertionExp e) {
        return false;
    }

    public void preTypeAssertionExp(TypeAssertionExp e) {}

    public void midTypeAssertionExp(TypeAssertionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeAssertionExp(TypeAssertionExp e) {}

    public boolean walkMathAssertionDec(MathAssertionDec e) {
        return false;
    }

    public void preMathAssertionDec(MathAssertionDec e) {}

    public void midMathAssertionDec(MathAssertionDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathAssertionDec(MathAssertionDec e) {}

    public boolean walkProgramVariableExp(ProgramVariableExp e) {
        return false;
    }

    public void preProgramVariableExp(ProgramVariableExp e) {}

    public void midProgramVariableExp(ProgramVariableExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramVariableExp(ProgramVariableExp e) {}

    public boolean walkQuantExp(QuantExp e) {
        return false;
    }

    public void preQuantExp(QuantExp e) {}

    public void midQuantExp(QuantExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postQuantExp(QuantExp e) {}

    public boolean walkEnhancementSpecRealizItem(EnhancementSpecRealizItem e) {
        return false;
    }

    public void preEnhancementSpecRealizItem(EnhancementSpecRealizItem e) {}

    public void midEnhancementSpecRealizItem(EnhancementSpecRealizItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementSpecRealizItem(EnhancementSpecRealizItem e) {}

    public boolean walkAbstractFunctionExp(AbstractFunctionExp e) {
        return false;
    }

    public void preAbstractFunctionExp(AbstractFunctionExp e) {}

    public void midAbstractFunctionExp(AbstractFunctionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractFunctionExp(AbstractFunctionExp e) {}

    public boolean walkRealizationParamDec(RealizationParamDec e) {
        return false;
    }

    public void preRealizationParamDec(RealizationParamDec e) {}

    public void midRealizationParamDec(RealizationParamDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRealizationParamDec(RealizationParamDec e) {}

    public boolean walkSetExp(SetExp e) {
        return false;
    }

    public void preSetExp(SetExp e) {}

    public void midSetExp(SetExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postSetExp(SetExp e) {}

    public boolean walkProgramDoubleExp(ProgramDoubleExp e) {
        return false;
    }

    public void preProgramDoubleExp(ProgramDoubleExp e) {}

    public void midProgramDoubleExp(ProgramDoubleExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramDoubleExp(ProgramDoubleExp e) {}

    public boolean walkIntegerExp(IntegerExp e) {
        return false;
    }

    public void preIntegerExp(IntegerExp e) {}

    public void midIntegerExp(IntegerExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIntegerExp(IntegerExp e) {}

    public boolean walkPerfTypeInitFinalSpecItem(PerfTypeInitFinalSpecItem e) {
        return false;
    }

    public void prePerfTypeInitFinalSpecItem(PerfTypeInitFinalSpecItem e) {}

    public void midPerfTypeInitFinalSpecItem(PerfTypeInitFinalSpecItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerfTypeInitFinalSpecItem(PerfTypeInitFinalSpecItem e) {}

    public boolean walkConceptTypeParamDec(ConceptTypeParamDec e) {
        return false;
    }

    public void preConceptTypeParamDec(ConceptTypeParamDec e) {}

    public void midConceptTypeParamDec(ConceptTypeParamDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptTypeParamDec(ConceptTypeParamDec e) {}

    public boolean walkIfConditionItem(IfConditionItem e) {
        return false;
    }

    public void preIfConditionItem(IfConditionItem e) {}

    public void midIfConditionItem(IfConditionItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIfConditionItem(IfConditionItem e) {}

    public boolean walkTupleExp(TupleExp e) {
        return false;
    }

    public void preTupleExp(TupleExp e) {}

    public void midTupleExp(TupleExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTupleExp(TupleExp e) {}

    public boolean walkOutfixExp(OutfixExp e) {
        return false;
    }

    public void preOutfixExp(OutfixExp e) {}

    public void midOutfixExp(OutfixExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postOutfixExp(OutfixExp e) {}

    public boolean walkVarDec(VarDec e) {
        return false;
    }

    public void preVarDec(VarDec e) {}

    public void midVarDec(VarDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postVarDec(VarDec e) {}

    public boolean walkProgramLiteralExp(ProgramLiteralExp e) {
        return false;
    }

    public void preProgramLiteralExp(ProgramLiteralExp e) {}

    public void midProgramLiteralExp(ProgramLiteralExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramLiteralExp(ProgramLiteralExp e) {}

    public boolean walkFacilityTypeRepresentationDec(
            FacilityTypeRepresentationDec e) {
        return false;
    }

    public void preFacilityTypeRepresentationDec(FacilityTypeRepresentationDec e) {}

    public void midFacilityTypeRepresentationDec(
            FacilityTypeRepresentationDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postFacilityTypeRepresentationDec(
            FacilityTypeRepresentationDec e) {}

    public boolean walkMathCategoricalDefinitionDec(
            MathCategoricalDefinitionDec e) {
        return false;
    }

    public void preMathCategoricalDefinitionDec(MathCategoricalDefinitionDec e) {}

    public void midMathCategoricalDefinitionDec(MathCategoricalDefinitionDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathCategoricalDefinitionDec(MathCategoricalDefinitionDec e) {}

    public boolean walkIfStmt(IfStmt e) {
        return false;
    }

    public void preIfStmt(IfStmt e) {}

    public void midIfStmt(IfStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIfStmt(IfStmt e) {}

    public boolean walkConstantParamDec(ConstantParamDec e) {
        return false;
    }

    public void preConstantParamDec(ConstantParamDec e) {}

    public void midConstantParamDec(ConstantParamDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConstantParamDec(ConstantParamDec e) {}

    public boolean walkMathExp(MathExp e) {
        return false;
    }

    public void preMathExp(MathExp e) {}

    public void midMathExp(MathExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postMathExp(MathExp e) {}

    public boolean walkProcedureDec(ProcedureDec e) {
        return false;
    }

    public void preProcedureDec(ProcedureDec e) {}

    public void midProcedureDec(ProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProcedureDec(ProcedureDec e) {}

    public boolean walkLiteralExp(LiteralExp e) {
        return false;
    }

    public void preLiteralExp(LiteralExp e) {}

    public void midLiteralExp(LiteralExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postLiteralExp(LiteralExp e) {}

    public boolean walkPrecisModuleDec(PrecisModuleDec e) {
        return false;
    }

    public void prePrecisModuleDec(PrecisModuleDec e) {}

    public void midPrecisModuleDec(PrecisModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPrecisModuleDec(PrecisModuleDec e) {}

    public boolean walkAlternativeExp(AlternativeExp e) {
        return false;
    }

    public void preAlternativeExp(AlternativeExp e) {}

    public void midAlternativeExp(AlternativeExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAlternativeExp(AlternativeExp e) {}

    public boolean walkFacilityModuleDec(FacilityModuleDec e) {
        return false;
    }

    public void preFacilityModuleDec(FacilityModuleDec e) {}

    public void midFacilityModuleDec(FacilityModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityModuleDec(FacilityModuleDec e) {}

    public boolean walkTypeFamilyDec(TypeFamilyDec e) {
        return false;
    }

    public void preTypeFamilyDec(TypeFamilyDec e) {}

    public void midTypeFamilyDec(TypeFamilyDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeFamilyDec(TypeFamilyDec e) {}

    public boolean walkArbitraryExpTy(ArbitraryExpTy e) {
        return false;
    }

    public void preArbitraryExpTy(ArbitraryExpTy e) {}

    public void midArbitraryExpTy(ArbitraryExpTy e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postArbitraryExpTy(ArbitraryExpTy e) {}

    public boolean walkProgramIntegerExp(ProgramIntegerExp e) {
        return false;
    }

    public void preProgramIntegerExp(ProgramIntegerExp e) {}

    public void midProgramIntegerExp(ProgramIntegerExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramIntegerExp(ProgramIntegerExp e) {}

    public boolean walkFuncAssignStmt(FuncAssignStmt e) {
        return false;
    }

    public void preFuncAssignStmt(FuncAssignStmt e) {}

    public void midFuncAssignStmt(FuncAssignStmt e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFuncAssignStmt(FuncAssignStmt e) {}

    public boolean walkBetweenExp(BetweenExp e) {
        return false;
    }

    public void preBetweenExp(BetweenExp e) {}

    public void midBetweenExp(BetweenExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postBetweenExp(BetweenExp e) {}

    public boolean walkAbstractVarDec(AbstractVarDec e) {
        return false;
    }

    public void preAbstractVarDec(AbstractVarDec e) {}

    public void midAbstractVarDec(AbstractVarDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractVarDec(AbstractVarDec e) {}

    public boolean walkStatement(Statement e) {
        return false;
    }

    public void preStatement(Statement e) {}

    public void midStatement(Statement e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postStatement(Statement e) {}

    public boolean walkOperationProcedureDec(OperationProcedureDec e) {
        return false;
    }

    public void preOperationProcedureDec(OperationProcedureDec e) {}

    public void midOperationProcedureDec(OperationProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postOperationProcedureDec(OperationProcedureDec e) {}

    public boolean walkShortFacilityModuleDec(ShortFacilityModuleDec e) {
        return false;
    }

    public void preShortFacilityModuleDec(ShortFacilityModuleDec e) {}

    public void midShortFacilityModuleDec(ShortFacilityModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postShortFacilityModuleDec(ShortFacilityModuleDec e) {}

    public boolean walkExp(Exp e) {
        return false;
    }

    public void preExp(Exp e) {}

    public void midExp(Exp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postExp(Exp e) {}

    public boolean walkAffectsClause(AffectsClause e) {
        return false;
    }

    public void preAffectsClause(AffectsClause e) {}

    public void midAffectsClause(AffectsClause e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAffectsClause(AffectsClause e) {}

    public boolean walkEnhancementRealizModuleDec(EnhancementRealizModuleDec e) {
        return false;
    }

    public void preEnhancementRealizModuleDec(EnhancementRealizModuleDec e) {}

    public void midEnhancementRealizModuleDec(EnhancementRealizModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementRealizModuleDec(EnhancementRealizModuleDec e) {}

    public boolean walkEnhancementSpecItem(EnhancementSpecItem e) {
        return false;
    }

    public void preEnhancementSpecItem(EnhancementSpecItem e) {}

    public void midEnhancementSpecItem(EnhancementSpecItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementSpecItem(EnhancementSpecItem e) {}

    public boolean walkCharExp(CharExp e) {
        return false;
    }

    public void preCharExp(CharExp e) {}

    public void midCharExp(CharExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postCharExp(CharExp e) {}

    public boolean walkRecordTy(RecordTy e) {
        return false;
    }

    public void preRecordTy(RecordTy e) {}

    public void midRecordTy(RecordTy e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postRecordTy(RecordTy e) {}

    public boolean walkMemoryStmt(MemoryStmt e) {
        return false;
    }

    public void preMemoryStmt(MemoryStmt e) {}

    public void midMemoryStmt(MemoryStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postMemoryStmt(MemoryStmt e) {}

    public boolean walkTypeInitFinalItem(TypeInitFinalItem e) {
        return false;
    }

    public void preTypeInitFinalItem(TypeInitFinalItem e) {}

    public void midTypeInitFinalItem(TypeInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeInitFinalItem(TypeInitFinalItem e) {}

    public boolean walkIfExp(IfExp e) {
        return false;
    }

    public void preIfExp(IfExp e) {}

    public void midIfExp(IfExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIfExp(IfExp e) {}

    public boolean walkMathTypeTheoremDec(MathTypeTheoremDec e) {
        return false;
    }

    public void preMathTypeTheoremDec(MathTypeTheoremDec e) {}

    public void midMathTypeTheoremDec(MathTypeTheoremDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathTypeTheoremDec(MathTypeTheoremDec e) {}

    public boolean walkProgramFunctionExp(ProgramFunctionExp e) {
        return false;
    }

    public void preProgramFunctionExp(ProgramFunctionExp e) {}

    public void midProgramFunctionExp(ProgramFunctionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramFunctionExp(ProgramFunctionExp e) {}

    public boolean walkCallStmt(CallStmt e) {
        return false;
    }

    public void preCallStmt(CallStmt e) {}

    public void midCallStmt(CallStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postCallStmt(CallStmt e) {}

    public boolean walkConceptModuleDec(ConceptModuleDec e) {
        return false;
    }

    public void preConceptModuleDec(ConceptModuleDec e) {}

    public void midConceptModuleDec(ConceptModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptModuleDec(ConceptModuleDec e) {}

    public boolean walkEnhancementModuleDec(EnhancementModuleDec e) {
        return false;
    }

    public void preEnhancementModuleDec(EnhancementModuleDec e) {}

    public void midEnhancementModuleDec(EnhancementModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementModuleDec(EnhancementModuleDec e) {}

    public boolean walkConfirmStmt(ConfirmStmt e) {
        return false;
    }

    public void preConfirmStmt(ConfirmStmt e) {}

    public void midConfirmStmt(ConfirmStmt e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConfirmStmt(ConfirmStmt e) {}

}