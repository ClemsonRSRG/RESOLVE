/*
 * TreeWalkerVisitor.java
 * ---------------------------------
 * Copyright (c) 2017
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
import edu.clemson.cs.rsrg.absyn.clauses.*;
import edu.clemson.cs.rsrg.absyn.statements.*;
import edu.clemson.cs.rsrg.absyn.*;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.*;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.absyn.expressions.*;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.*;
import edu.clemson.cs.rsrg.absyn.declarations.*;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.cs.rsrg.absyn.items.mathitems.*;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.*;
import edu.clemson.cs.rsrg.absyn.items.programitems.*;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.*;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.*;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.*;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.*;
import edu.clemson.cs.rsrg.absyn.rawtypes.*;

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

    public boolean walkAltItemExp(AltItemExp e) {
        return false;
    }

    public void preAltItemExp(AltItemExp e) {}

    public void midAltItemExp(AltItemExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postAltItemExp(AltItemExp e) {}

    public boolean walkDotExp(DotExp e) {
        return false;
    }

    public void preDotExp(DotExp e) {}

    public void midDotExp(DotExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDotExp(DotExp e) {}

    public boolean walkDotExpMySegmentExps(DotExp e) {
        return false;
    }

    public void preDotExpMySegmentExps(DotExp e) {}

    public void midDotExpMySegmentExps(DotExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postDotExpMySegmentExps(DotExp e) {}

    public boolean walkOldExp(OldExp e) {
        return false;
    }

    public void preOldExp(OldExp e) {}

    public void midOldExp(OldExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postOldExp(OldExp e) {}

    public boolean walkAffectsClause(AffectsClause e) {
        return false;
    }

    public void preAffectsClause(AffectsClause e) {}

    public void midAffectsClause(AffectsClause e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAffectsClause(AffectsClause e) {}

    public boolean walkAffectsClauseMyAffectedExps(AffectsClause e) {
        return false;
    }

    public void preAffectsClauseMyAffectedExps(AffectsClause e) {}

    public void midAffectsClauseMyAffectedExps(AffectsClause e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAffectsClauseMyAffectedExps(AffectsClause e) {}

    public boolean walkChangeStmt(ChangeStmt e) {
        return false;
    }

    public void preChangeStmt(ChangeStmt e) {}

    public void midChangeStmt(ChangeStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postChangeStmt(ChangeStmt e) {}

    public boolean walkChangeStmtMyChangingVars(ChangeStmt e) {
        return false;
    }

    public void preChangeStmtMyChangingVars(ChangeStmt e) {}

    public void midChangeStmtMyChangingVars(ChangeStmt e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postChangeStmtMyChangingVars(ChangeStmt e) {}

    public boolean walkVirtualListNode(VirtualListNode e) {
        return false;
    }

    public void preVirtualListNode(VirtualListNode e) {}

    public void midVirtualListNode(VirtualListNode e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVirtualListNode(VirtualListNode e) {}

    public boolean walkVirtualListNodeMyList(VirtualListNode e) {
        return false;
    }

    public void preVirtualListNodeMyList(VirtualListNode e) {}

    public void midVirtualListNodeMyList(VirtualListNode e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVirtualListNodeMyList(VirtualListNode e) {}

    public boolean walkConstantParamDec(ConstantParamDec e) {
        return false;
    }

    public void preConstantParamDec(ConstantParamDec e) {}

    public void midConstantParamDec(ConstantParamDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConstantParamDec(ConstantParamDec e) {}

    public boolean walkProgramDoubleExp(ProgramDoubleExp e) {
        return false;
    }

    public void preProgramDoubleExp(ProgramDoubleExp e) {}

    public void midProgramDoubleExp(ProgramDoubleExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramDoubleExp(ProgramDoubleExp e) {}

    public boolean walkExp(Exp e) {
        return false;
    }

    public void preExp(Exp e) {}

    public void midExp(Exp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postExp(Exp e) {}

    public boolean walkMathCategoricalDefinitionDec(
            MathCategoricalDefinitionDec e) {
        return false;
    }

    public void preMathCategoricalDefinitionDec(MathCategoricalDefinitionDec e) {}

    public void midMathCategoricalDefinitionDec(MathCategoricalDefinitionDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathCategoricalDefinitionDec(MathCategoricalDefinitionDec e) {}

    public boolean walkMathCategoricalDefinitionDecMyDefinitions(
            MathCategoricalDefinitionDec e) {
        return false;
    }

    public void preMathCategoricalDefinitionDecMyDefinitions(
            MathCategoricalDefinitionDec e) {}

    public void midMathCategoricalDefinitionDecMyDefinitions(
            MathCategoricalDefinitionDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postMathCategoricalDefinitionDecMyDefinitions(
            MathCategoricalDefinitionDec e) {}

    public boolean walkDec(Dec e) {
        return false;
    }

    public void preDec(Dec e) {}

    public void midDec(Dec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDec(Dec e) {}

    public boolean walkProgramVariableArrayExp(ProgramVariableArrayExp e) {
        return false;
    }

    public void preProgramVariableArrayExp(ProgramVariableArrayExp e) {}

    public void midProgramVariableArrayExp(ProgramVariableArrayExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramVariableArrayExp(ProgramVariableArrayExp e) {}

    public boolean walkRealizationParamDec(RealizationParamDec e) {
        return false;
    }

    public void preRealizationParamDec(RealizationParamDec e) {}

    public void midRealizationParamDec(RealizationParamDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRealizationParamDec(RealizationParamDec e) {}

    public boolean walkCharExp(CharExp e) {
        return false;
    }

    public void preCharExp(CharExp e) {}

    public void midCharExp(CharExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postCharExp(CharExp e) {}

    public boolean walkConceptModuleDec(ConceptModuleDec e) {
        return false;
    }

    public void preConceptModuleDec(ConceptModuleDec e) {}

    public void midConceptModuleDec(ConceptModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptModuleDec(ConceptModuleDec e) {}

    public boolean walkConceptModuleDecMyParameterDecs(ConceptModuleDec e) {
        return false;
    }

    public void preConceptModuleDecMyParameterDecs(ConceptModuleDec e) {}

    public void midConceptModuleDecMyParameterDecs(ConceptModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptModuleDecMyParameterDecs(ConceptModuleDec e) {}

    public boolean walkConceptModuleDecMyDecs(ConceptModuleDec e) {
        return false;
    }

    public void preConceptModuleDecMyDecs(ConceptModuleDec e) {}

    public void midConceptModuleDecMyDecs(ConceptModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptModuleDecMyDecs(ConceptModuleDec e) {}

    public boolean walkConceptModuleDecMyConstraints(ConceptModuleDec e) {
        return false;
    }

    public void preConceptModuleDecMyConstraints(ConceptModuleDec e) {}

    public void midConceptModuleDecMyConstraints(ConceptModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptModuleDecMyConstraints(ConceptModuleDec e) {}

    public boolean walkConceptModuleDecMyUsesItems(ConceptModuleDec e) {
        return false;
    }

    public void preConceptModuleDecMyUsesItems(ConceptModuleDec e) {}

    public void midConceptModuleDecMyUsesItems(ConceptModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptModuleDecMyUsesItems(ConceptModuleDec e) {}

    public boolean walkVCVarExp(VCVarExp e) {
        return false;
    }

    public void preVCVarExp(VCVarExp e) {}

    public void midVCVarExp(VCVarExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postVCVarExp(VCVarExp e) {}

    public boolean walkProgramVariableExp(ProgramVariableExp e) {
        return false;
    }

    public void preProgramVariableExp(ProgramVariableExp e) {}

    public void midProgramVariableExp(ProgramVariableExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramVariableExp(ProgramVariableExp e) {}

    public boolean walkAssertionClause(AssertionClause e) {
        return false;
    }

    public void preAssertionClause(AssertionClause e) {}

    public void midAssertionClause(AssertionClause e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAssertionClause(AssertionClause e) {}

    public boolean walkAssertionClauseMyInvolvedSharedVars(AssertionClause e) {
        return false;
    }

    public void preAssertionClauseMyInvolvedSharedVars(AssertionClause e) {}

    public void midAssertionClauseMyInvolvedSharedVars(AssertionClause e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAssertionClauseMyInvolvedSharedVars(AssertionClause e) {}

    public boolean walkDefinitionBodyItem(DefinitionBodyItem e) {
        return false;
    }

    public void preDefinitionBodyItem(DefinitionBodyItem e) {}

    public void midDefinitionBodyItem(DefinitionBodyItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postDefinitionBodyItem(DefinitionBodyItem e) {}

    public boolean walkVarExp(VarExp e) {
        return false;
    }

    public void preVarExp(VarExp e) {}

    public void midVarExp(VarExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postVarExp(VarExp e) {}

    public boolean walkModuleParameterDec(ModuleParameterDec e) {
        return false;
    }

    public void preModuleParameterDec(ModuleParameterDec e) {}

    public void midModuleParameterDec(ModuleParameterDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postModuleParameterDec(ModuleParameterDec e) {}

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

    public boolean walkEnhancementSpecItem(EnhancementSpecItem e) {
        return false;
    }

    public void preEnhancementSpecItem(EnhancementSpecItem e) {}

    public void midEnhancementSpecItem(EnhancementSpecItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementSpecItem(EnhancementSpecItem e) {}

    public boolean walkEnhancementSpecItemMyParams(EnhancementSpecItem e) {
        return false;
    }

    public void preEnhancementSpecItemMyParams(EnhancementSpecItem e) {}

    public void midEnhancementSpecItemMyParams(EnhancementSpecItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementSpecItemMyParams(EnhancementSpecItem e) {}

    public boolean walkConfirmStmt(ConfirmStmt e) {
        return false;
    }

    public void preConfirmStmt(ConfirmStmt e) {}

    public void midConfirmStmt(ConfirmStmt e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConfirmStmt(ConfirmStmt e) {}

    public boolean walkParameterVarDec(ParameterVarDec e) {
        return false;
    }

    public void preParameterVarDec(ParameterVarDec e) {}

    public void midParameterVarDec(ParameterVarDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postParameterVarDec(ParameterVarDec e) {}

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

    public boolean walkProgramVariableNameExp(ProgramVariableNameExp e) {
        return false;
    }

    public void preProgramVariableNameExp(ProgramVariableNameExp e) {}

    public void midProgramVariableNameExp(ProgramVariableNameExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramVariableNameExp(ProgramVariableNameExp e) {}

    public boolean walkPerformanceTypeFamilyDec(PerformanceTypeFamilyDec e) {
        return false;
    }

    public void prePerformanceTypeFamilyDec(PerformanceTypeFamilyDec e) {}

    public void midPerformanceTypeFamilyDec(PerformanceTypeFamilyDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceTypeFamilyDec(PerformanceTypeFamilyDec e) {}

    public boolean walkTypeRepresentationDec(TypeRepresentationDec e) {
        return false;
    }

    public void preTypeRepresentationDec(TypeRepresentationDec e) {}

    public void midTypeRepresentationDec(TypeRepresentationDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeRepresentationDec(TypeRepresentationDec e) {}

    public boolean walkInfixExp(InfixExp e) {
        return false;
    }

    public void preInfixExp(InfixExp e) {}

    public void midInfixExp(InfixExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postInfixExp(InfixExp e) {}

    public boolean walkShortFacilityModuleDec(ShortFacilityModuleDec e) {
        return false;
    }

    public void preShortFacilityModuleDec(ShortFacilityModuleDec e) {}

    public void midShortFacilityModuleDec(ShortFacilityModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postShortFacilityModuleDec(ShortFacilityModuleDec e) {}

    public boolean walkShortFacilityModuleDecMyParameterDecs(
            ShortFacilityModuleDec e) {
        return false;
    }

    public void preShortFacilityModuleDecMyParameterDecs(
            ShortFacilityModuleDec e) {}

    public void midShortFacilityModuleDecMyParameterDecs(
            ShortFacilityModuleDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postShortFacilityModuleDecMyParameterDecs(
            ShortFacilityModuleDec e) {}

    public boolean walkShortFacilityModuleDecMyDecs(ShortFacilityModuleDec e) {
        return false;
    }

    public void preShortFacilityModuleDecMyDecs(ShortFacilityModuleDec e) {}

    public void midShortFacilityModuleDecMyDecs(ShortFacilityModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postShortFacilityModuleDecMyDecs(ShortFacilityModuleDec e) {}

    public boolean walkShortFacilityModuleDecMyUsesItems(
            ShortFacilityModuleDec e) {
        return false;
    }

    public void preShortFacilityModuleDecMyUsesItems(ShortFacilityModuleDec e) {}

    public void midShortFacilityModuleDecMyUsesItems(ShortFacilityModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postShortFacilityModuleDecMyUsesItems(ShortFacilityModuleDec e) {}

    public boolean walkProgramIntegerExp(ProgramIntegerExp e) {
        return false;
    }

    public void preProgramIntegerExp(ProgramIntegerExp e) {}

    public void midProgramIntegerExp(ProgramIntegerExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramIntegerExp(ProgramIntegerExp e) {}

    public boolean walkPerformanceConceptModuleDec(PerformanceConceptModuleDec e) {
        return false;
    }

    public void prePerformanceConceptModuleDec(PerformanceConceptModuleDec e) {}

    public void midPerformanceConceptModuleDec(PerformanceConceptModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceConceptModuleDec(PerformanceConceptModuleDec e) {}

    public boolean walkPerformanceConceptModuleDecMyParameterDecs(
            PerformanceConceptModuleDec e) {
        return false;
    }

    public void prePerformanceConceptModuleDecMyParameterDecs(
            PerformanceConceptModuleDec e) {}

    public void midPerformanceConceptModuleDecMyParameterDecs(
            PerformanceConceptModuleDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postPerformanceConceptModuleDecMyParameterDecs(
            PerformanceConceptModuleDec e) {}

    public boolean walkPerformanceConceptModuleDecMyDecs(
            PerformanceConceptModuleDec e) {
        return false;
    }

    public void prePerformanceConceptModuleDecMyDecs(
            PerformanceConceptModuleDec e) {}

    public void midPerformanceConceptModuleDecMyDecs(
            PerformanceConceptModuleDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postPerformanceConceptModuleDecMyDecs(
            PerformanceConceptModuleDec e) {}

    public boolean walkPerformanceConceptModuleDecMyUsesItems(
            PerformanceConceptModuleDec e) {
        return false;
    }

    public void prePerformanceConceptModuleDecMyUsesItems(
            PerformanceConceptModuleDec e) {}

    public void midPerformanceConceptModuleDecMyUsesItems(
            PerformanceConceptModuleDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postPerformanceConceptModuleDecMyUsesItems(
            PerformanceConceptModuleDec e) {}

    public boolean walkCallStmt(CallStmt e) {
        return false;
    }

    public void preCallStmt(CallStmt e) {}

    public void midCallStmt(CallStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postCallStmt(CallStmt e) {}

    public boolean walkAbstractInitFinalItem(AbstractInitFinalItem e) {
        return false;
    }

    public void preAbstractInitFinalItem(AbstractInitFinalItem e) {}

    public void midAbstractInitFinalItem(AbstractInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractInitFinalItem(AbstractInitFinalItem e) {}

    public boolean walkAbstractInitFinalItemMyFacilityDecs(
            AbstractInitFinalItem e) {
        return false;
    }

    public void preAbstractInitFinalItemMyFacilityDecs(AbstractInitFinalItem e) {}

    public void midAbstractInitFinalItemMyFacilityDecs(AbstractInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractInitFinalItemMyFacilityDecs(AbstractInitFinalItem e) {}

    public boolean walkAbstractInitFinalItemMyStatements(AbstractInitFinalItem e) {
        return false;
    }

    public void preAbstractInitFinalItemMyStatements(AbstractInitFinalItem e) {}

    public void midAbstractInitFinalItemMyStatements(AbstractInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractInitFinalItemMyStatements(AbstractInitFinalItem e) {}

    public boolean walkAbstractInitFinalItemMyVariableDecs(
            AbstractInitFinalItem e) {
        return false;
    }

    public void preAbstractInitFinalItemMyVariableDecs(AbstractInitFinalItem e) {}

    public void midAbstractInitFinalItemMyVariableDecs(AbstractInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractInitFinalItemMyVariableDecs(AbstractInitFinalItem e) {}

    public boolean walkAbstractFunctionExp(AbstractFunctionExp e) {
        return false;
    }

    public void preAbstractFunctionExp(AbstractFunctionExp e) {}

    public void midAbstractFunctionExp(AbstractFunctionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractFunctionExp(AbstractFunctionExp e) {}

    public boolean walkCrossTypeExp(CrossTypeExp e) {
        return false;
    }

    public void preCrossTypeExp(CrossTypeExp e) {}

    public void midCrossTypeExp(CrossTypeExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postCrossTypeExp(CrossTypeExp e) {}

    public boolean walkMathTypeTheoremDec(MathTypeTheoremDec e) {
        return false;
    }

    public void preMathTypeTheoremDec(MathTypeTheoremDec e) {}

    public void midMathTypeTheoremDec(MathTypeTheoremDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathTypeTheoremDec(MathTypeTheoremDec e) {}

    public boolean walkMathTypeTheoremDecMyUniversalVars(MathTypeTheoremDec e) {
        return false;
    }

    public void preMathTypeTheoremDecMyUniversalVars(MathTypeTheoremDec e) {}

    public void midMathTypeTheoremDecMyUniversalVars(MathTypeTheoremDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathTypeTheoremDecMyUniversalVars(MathTypeTheoremDec e) {}

    public boolean walkEnhancementSpecRealizItem(EnhancementSpecRealizItem e) {
        return false;
    }

    public void preEnhancementSpecRealizItem(EnhancementSpecRealizItem e) {}

    public void midEnhancementSpecRealizItem(EnhancementSpecRealizItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementSpecRealizItem(EnhancementSpecRealizItem e) {}

    public boolean walkEnhancementSpecRealizItemMyEnhancementRealizParams(
            EnhancementSpecRealizItem e) {
        return false;
    }

    public void preEnhancementSpecRealizItemMyEnhancementRealizParams(
            EnhancementSpecRealizItem e) {}

    public void midEnhancementSpecRealizItemMyEnhancementRealizParams(
            EnhancementSpecRealizItem e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postEnhancementSpecRealizItemMyEnhancementRealizParams(
            EnhancementSpecRealizItem e) {}

    public boolean walkEnhancementSpecRealizItemMyEnhancementParams(
            EnhancementSpecRealizItem e) {
        return false;
    }

    public void preEnhancementSpecRealizItemMyEnhancementParams(
            EnhancementSpecRealizItem e) {}

    public void midEnhancementSpecRealizItemMyEnhancementParams(
            EnhancementSpecRealizItem e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postEnhancementSpecRealizItemMyEnhancementParams(
            EnhancementSpecRealizItem e) {}

    public boolean walkFunctionExp(FunctionExp e) {
        return false;
    }

    public void preFunctionExp(FunctionExp e) {}

    public void midFunctionExp(FunctionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFunctionExp(FunctionExp e) {}

    public boolean walkFunctionExpMyArguments(FunctionExp e) {
        return false;
    }

    public void preFunctionExpMyArguments(FunctionExp e) {}

    public void midFunctionExpMyArguments(FunctionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFunctionExpMyArguments(FunctionExp e) {}

    public boolean walkSpecInitFinalItem(SpecInitFinalItem e) {
        return false;
    }

    public void preSpecInitFinalItem(SpecInitFinalItem e) {}

    public void midSpecInitFinalItem(SpecInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSpecInitFinalItem(SpecInitFinalItem e) {}

    public boolean walkSharedStateDec(SharedStateDec e) {
        return false;
    }

    public void preSharedStateDec(SharedStateDec e) {}

    public void midSharedStateDec(SharedStateDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSharedStateDec(SharedStateDec e) {}

    public boolean walkSharedStateDecMyAbstractStateVars(SharedStateDec e) {
        return false;
    }

    public void preSharedStateDecMyAbstractStateVars(SharedStateDec e) {}

    public void midSharedStateDecMyAbstractStateVars(SharedStateDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSharedStateDecMyAbstractStateVars(SharedStateDec e) {}

    public boolean walkEnhancementModuleDec(EnhancementModuleDec e) {
        return false;
    }

    public void preEnhancementModuleDec(EnhancementModuleDec e) {}

    public void midEnhancementModuleDec(EnhancementModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementModuleDec(EnhancementModuleDec e) {}

    public boolean walkEnhancementModuleDecMyParameterDecs(
            EnhancementModuleDec e) {
        return false;
    }

    public void preEnhancementModuleDecMyParameterDecs(EnhancementModuleDec e) {}

    public void midEnhancementModuleDecMyParameterDecs(EnhancementModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementModuleDecMyParameterDecs(EnhancementModuleDec e) {}

    public boolean walkEnhancementModuleDecMyDecs(EnhancementModuleDec e) {
        return false;
    }

    public void preEnhancementModuleDecMyDecs(EnhancementModuleDec e) {}

    public void midEnhancementModuleDecMyDecs(EnhancementModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementModuleDecMyDecs(EnhancementModuleDec e) {}

    public boolean walkEnhancementModuleDecMyUsesItems(EnhancementModuleDec e) {
        return false;
    }

    public void preEnhancementModuleDecMyUsesItems(EnhancementModuleDec e) {}

    public void midEnhancementModuleDecMyUsesItems(EnhancementModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementModuleDecMyUsesItems(EnhancementModuleDec e) {}

    public boolean walkDoubleExp(DoubleExp e) {
        return false;
    }

    public void preDoubleExp(DoubleExp e) {}

    public void midDoubleExp(DoubleExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDoubleExp(DoubleExp e) {}

    public boolean walkMathDefinitionDec(MathDefinitionDec e) {
        return false;
    }

    public void preMathDefinitionDec(MathDefinitionDec e) {}

    public void midMathDefinitionDec(MathDefinitionDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathDefinitionDec(MathDefinitionDec e) {}

    public boolean walkMathDefinitionDecMyParameters(MathDefinitionDec e) {
        return false;
    }

    public void preMathDefinitionDecMyParameters(MathDefinitionDec e) {}

    public void midMathDefinitionDecMyParameters(MathDefinitionDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathDefinitionDecMyParameters(MathDefinitionDec e) {}

    public boolean walkUnaryMinusExp(UnaryMinusExp e) {
        return false;
    }

    public void preUnaryMinusExp(UnaryMinusExp e) {}

    public void midUnaryMinusExp(UnaryMinusExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postUnaryMinusExp(UnaryMinusExp e) {}

    public boolean walkProgramVariableDotExp(ProgramVariableDotExp e) {
        return false;
    }

    public void preProgramVariableDotExp(ProgramVariableDotExp e) {}

    public void midProgramVariableDotExp(ProgramVariableDotExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramVariableDotExp(ProgramVariableDotExp e) {}

    public boolean walkProgramVariableDotExpMySegmentExps(
            ProgramVariableDotExp e) {
        return false;
    }

    public void preProgramVariableDotExpMySegmentExps(ProgramVariableDotExp e) {}

    public void midProgramVariableDotExpMySegmentExps(ProgramVariableDotExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramVariableDotExpMySegmentExps(ProgramVariableDotExp e) {}

    public boolean walkModuleArgumentItem(ModuleArgumentItem e) {
        return false;
    }

    public void preModuleArgumentItem(ModuleArgumentItem e) {}

    public void midModuleArgumentItem(ModuleArgumentItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postModuleArgumentItem(ModuleArgumentItem e) {}

    public boolean walkConceptTypeParamDec(ConceptTypeParamDec e) {
        return false;
    }

    public void preConceptTypeParamDec(ConceptTypeParamDec e) {}

    public void midConceptTypeParamDec(ConceptTypeParamDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptTypeParamDec(ConceptTypeParamDec e) {}

    public boolean walkPerformanceOperationDec(PerformanceOperationDec e) {
        return false;
    }

    public void prePerformanceOperationDec(PerformanceOperationDec e) {}

    public void midPerformanceOperationDec(PerformanceOperationDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceOperationDec(PerformanceOperationDec e) {}

    public boolean walkStringExp(StringExp e) {
        return false;
    }

    public void preStringExp(StringExp e) {}

    public void midStringExp(StringExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postStringExp(StringExp e) {}

    public boolean walkFacilityDec(FacilityDec e) {
        return false;
    }

    public void preFacilityDec(FacilityDec e) {}

    public void midFacilityDec(FacilityDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityDec(FacilityDec e) {}

    public boolean walkFacilityDecMyEnhancementRealizPairs(FacilityDec e) {
        return false;
    }

    public void preFacilityDecMyEnhancementRealizPairs(FacilityDec e) {}

    public void midFacilityDecMyEnhancementRealizPairs(FacilityDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityDecMyEnhancementRealizPairs(FacilityDec e) {}

    public boolean walkFacilityDecMyConceptParams(FacilityDec e) {
        return false;
    }

    public void preFacilityDecMyConceptParams(FacilityDec e) {}

    public void midFacilityDecMyConceptParams(FacilityDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityDecMyConceptParams(FacilityDec e) {}

    public boolean walkFacilityDecMyConceptRealizParams(FacilityDec e) {
        return false;
    }

    public void preFacilityDecMyConceptRealizParams(FacilityDec e) {}

    public void midFacilityDecMyConceptRealizParams(FacilityDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityDecMyConceptRealizParams(FacilityDec e) {}

    public boolean walkFacilityDecMyEnhancements(FacilityDec e) {
        return false;
    }

    public void preFacilityDecMyEnhancements(FacilityDec e) {}

    public void midFacilityDecMyEnhancements(FacilityDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityDecMyEnhancements(FacilityDec e) {}

    public boolean walkProgramLiteralExp(ProgramLiteralExp e) {
        return false;
    }

    public void preProgramLiteralExp(ProgramLiteralExp e) {}

    public void midProgramLiteralExp(ProgramLiteralExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramLiteralExp(ProgramLiteralExp e) {}

    public boolean walkProcedureDec(ProcedureDec e) {
        return false;
    }

    public void preProcedureDec(ProcedureDec e) {}

    public void midProcedureDec(ProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProcedureDec(ProcedureDec e) {}

    public boolean walkProcedureDecMyVariableDecs(ProcedureDec e) {
        return false;
    }

    public void preProcedureDecMyVariableDecs(ProcedureDec e) {}

    public void midProcedureDecMyVariableDecs(ProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProcedureDecMyVariableDecs(ProcedureDec e) {}

    public boolean walkProcedureDecMyStatements(ProcedureDec e) {
        return false;
    }

    public void preProcedureDecMyStatements(ProcedureDec e) {}

    public void midProcedureDecMyStatements(ProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProcedureDecMyStatements(ProcedureDec e) {}

    public boolean walkProcedureDecMyFacilityDecs(ProcedureDec e) {
        return false;
    }

    public void preProcedureDecMyFacilityDecs(ProcedureDec e) {}

    public void midProcedureDecMyFacilityDecs(ProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProcedureDecMyFacilityDecs(ProcedureDec e) {}

    public boolean walkProcedureDecMyParameters(ProcedureDec e) {
        return false;
    }

    public void preProcedureDecMyParameters(ProcedureDec e) {}

    public void midProcedureDecMyParameters(ProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProcedureDecMyParameters(ProcedureDec e) {}

    public boolean walkArbitraryExpTy(ArbitraryExpTy e) {
        return false;
    }

    public void preArbitraryExpTy(ArbitraryExpTy e) {}

    public void midArbitraryExpTy(ArbitraryExpTy e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postArbitraryExpTy(ArbitraryExpTy e) {}

    public boolean walkAlternativeExp(AlternativeExp e) {
        return false;
    }

    public void preAlternativeExp(AlternativeExp e) {}

    public void midAlternativeExp(AlternativeExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAlternativeExp(AlternativeExp e) {}

    public boolean walkAlternativeExpMyAlternatives(AlternativeExp e) {
        return false;
    }

    public void preAlternativeExpMyAlternatives(AlternativeExp e) {}

    public void midAlternativeExpMyAlternatives(AlternativeExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAlternativeExpMyAlternatives(AlternativeExp e) {}

    public boolean walkOperationProcedureDec(OperationProcedureDec e) {
        return false;
    }

    public void preOperationProcedureDec(OperationProcedureDec e) {}

    public void midOperationProcedureDec(OperationProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postOperationProcedureDec(OperationProcedureDec e) {}

    public boolean walkOperationProcedureDecMyFacilityDecs(
            OperationProcedureDec e) {
        return false;
    }

    public void preOperationProcedureDecMyFacilityDecs(OperationProcedureDec e) {}

    public void midOperationProcedureDecMyFacilityDecs(OperationProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postOperationProcedureDecMyFacilityDecs(OperationProcedureDec e) {}

    public boolean walkOperationProcedureDecMyStatements(OperationProcedureDec e) {
        return false;
    }

    public void preOperationProcedureDecMyStatements(OperationProcedureDec e) {}

    public void midOperationProcedureDecMyStatements(OperationProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postOperationProcedureDecMyStatements(OperationProcedureDec e) {}

    public boolean walkOperationProcedureDecMyVariableDecs(
            OperationProcedureDec e) {
        return false;
    }

    public void preOperationProcedureDecMyVariableDecs(OperationProcedureDec e) {}

    public void midOperationProcedureDecMyVariableDecs(OperationProcedureDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postOperationProcedureDecMyVariableDecs(OperationProcedureDec e) {}

    public boolean walkNameTy(NameTy e) {
        return false;
    }

    public void preNameTy(NameTy e) {}

    public void midNameTy(NameTy e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postNameTy(NameTy e) {}

    public boolean walkConceptRealizModuleDec(ConceptRealizModuleDec e) {
        return false;
    }

    public void preConceptRealizModuleDec(ConceptRealizModuleDec e) {}

    public void midConceptRealizModuleDec(ConceptRealizModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptRealizModuleDec(ConceptRealizModuleDec e) {}

    public boolean walkConceptRealizModuleDecMyParameterDecs(
            ConceptRealizModuleDec e) {
        return false;
    }

    public void preConceptRealizModuleDecMyParameterDecs(
            ConceptRealizModuleDec e) {}

    public void midConceptRealizModuleDecMyParameterDecs(
            ConceptRealizModuleDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postConceptRealizModuleDecMyParameterDecs(
            ConceptRealizModuleDec e) {}

    public boolean walkConceptRealizModuleDecMyDecs(ConceptRealizModuleDec e) {
        return false;
    }

    public void preConceptRealizModuleDecMyDecs(ConceptRealizModuleDec e) {}

    public void midConceptRealizModuleDecMyDecs(ConceptRealizModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptRealizModuleDecMyDecs(ConceptRealizModuleDec e) {}

    public boolean walkConceptRealizModuleDecMyUsesItems(
            ConceptRealizModuleDec e) {
        return false;
    }

    public void preConceptRealizModuleDecMyUsesItems(ConceptRealizModuleDec e) {}

    public void midConceptRealizModuleDecMyUsesItems(ConceptRealizModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptRealizModuleDecMyUsesItems(ConceptRealizModuleDec e) {}

    public boolean walkMathExp(MathExp e) {
        return false;
    }

    public void preMathExp(MathExp e) {}

    public void midMathExp(MathExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postMathExp(MathExp e) {}

    public boolean walkProgramExp(ProgramExp e) {
        return false;
    }

    public void preProgramExp(ProgramExp e) {}

    public void midProgramExp(ProgramExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postProgramExp(ProgramExp e) {}

    public boolean walkQuantExp(QuantExp e) {
        return false;
    }

    public void preQuantExp(QuantExp e) {}

    public void midQuantExp(QuantExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postQuantExp(QuantExp e) {}

    public boolean walkQuantExpMyVars(QuantExp e) {
        return false;
    }

    public void preQuantExpMyVars(QuantExp e) {}

    public void midQuantExpMyVars(QuantExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postQuantExpMyVars(QuantExp e) {}

    public boolean walkIfConditionItem(IfConditionItem e) {
        return false;
    }

    public void preIfConditionItem(IfConditionItem e) {}

    public void midIfConditionItem(IfConditionItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIfConditionItem(IfConditionItem e) {}

    public boolean walkIfConditionItemMyStatements(IfConditionItem e) {
        return false;
    }

    public void preIfConditionItemMyStatements(IfConditionItem e) {}

    public void midIfConditionItemMyStatements(IfConditionItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIfConditionItemMyStatements(IfConditionItem e) {}

    public boolean walkRealizInitFinalItem(RealizInitFinalItem e) {
        return false;
    }

    public void preRealizInitFinalItem(RealizInitFinalItem e) {}

    public void midRealizInitFinalItem(RealizInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRealizInitFinalItem(RealizInitFinalItem e) {}

    public boolean walkRealizInitFinalItemMyFacilityDecs(RealizInitFinalItem e) {
        return false;
    }

    public void preRealizInitFinalItemMyFacilityDecs(RealizInitFinalItem e) {}

    public void midRealizInitFinalItemMyFacilityDecs(RealizInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRealizInitFinalItemMyFacilityDecs(RealizInitFinalItem e) {}

    public boolean walkRealizInitFinalItemMyStatements(RealizInitFinalItem e) {
        return false;
    }

    public void preRealizInitFinalItemMyStatements(RealizInitFinalItem e) {}

    public void midRealizInitFinalItemMyStatements(RealizInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRealizInitFinalItemMyStatements(RealizInitFinalItem e) {}

    public boolean walkRealizInitFinalItemMyVariableDecs(RealizInitFinalItem e) {
        return false;
    }

    public void preRealizInitFinalItemMyVariableDecs(RealizInitFinalItem e) {}

    public void midRealizInitFinalItemMyVariableDecs(RealizInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRealizInitFinalItemMyVariableDecs(RealizInitFinalItem e) {}

    public boolean walkUsesItem(UsesItem e) {
        return false;
    }

    public void preUsesItem(UsesItem e) {}

    public void midUsesItem(UsesItem e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postUsesItem(UsesItem e) {}

    public boolean walkProgramStringExp(ProgramStringExp e) {
        return false;
    }

    public void preProgramStringExp(ProgramStringExp e) {}

    public void midProgramStringExp(ProgramStringExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramStringExp(ProgramStringExp e) {}

    public boolean walkEqualsExp(EqualsExp e) {
        return false;
    }

    public void preEqualsExp(EqualsExp e) {}

    public void midEqualsExp(EqualsExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postEqualsExp(EqualsExp e) {}

    public boolean walkTy(Ty e) {
        return false;
    }

    public void preTy(Ty e) {}

    public void midTy(Ty e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTy(Ty e) {}

    public boolean walkBetweenExp(BetweenExp e) {
        return false;
    }

    public void preBetweenExp(BetweenExp e) {}

    public void midBetweenExp(BetweenExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postBetweenExp(BetweenExp e) {}

    public boolean walkBetweenExpMyJoiningExps(BetweenExp e) {
        return false;
    }

    public void preBetweenExpMyJoiningExps(BetweenExp e) {}

    public void midBetweenExpMyJoiningExps(BetweenExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postBetweenExpMyJoiningExps(BetweenExp e) {}

    public boolean walkModuleDec(ModuleDec e) {
        return false;
    }

    public void preModuleDec(ModuleDec e) {}

    public void midModuleDec(ModuleDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postModuleDec(ModuleDec e) {}

    public boolean walkModuleDecMyParameterDecs(ModuleDec e) {
        return false;
    }

    public void preModuleDecMyParameterDecs(ModuleDec e) {}

    public void midModuleDecMyParameterDecs(ModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postModuleDecMyParameterDecs(ModuleDec e) {}

    public boolean walkModuleDecMyDecs(ModuleDec e) {
        return false;
    }

    public void preModuleDecMyDecs(ModuleDec e) {}

    public void midModuleDecMyDecs(ModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postModuleDecMyDecs(ModuleDec e) {}

    public boolean walkModuleDecMyUsesItems(ModuleDec e) {
        return false;
    }

    public void preModuleDecMyUsesItems(ModuleDec e) {}

    public void midModuleDecMyUsesItems(ModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postModuleDecMyUsesItems(ModuleDec e) {}

    public boolean walkPerformanceEnhancementModuleDec(
            PerformanceEnhancementModuleDec e) {
        return false;
    }

    public void prePerformanceEnhancementModuleDec(
            PerformanceEnhancementModuleDec e) {}

    public void midPerformanceEnhancementModuleDec(
            PerformanceEnhancementModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceEnhancementModuleDec(
            PerformanceEnhancementModuleDec e) {}

    public boolean walkPerformanceEnhancementModuleDecMyParameterDecs(
            PerformanceEnhancementModuleDec e) {
        return false;
    }

    public void prePerformanceEnhancementModuleDecMyParameterDecs(
            PerformanceEnhancementModuleDec e) {}

    public void midPerformanceEnhancementModuleDecMyParameterDecs(
            PerformanceEnhancementModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceEnhancementModuleDecMyParameterDecs(
            PerformanceEnhancementModuleDec e) {}

    public boolean walkPerformanceEnhancementModuleDecMyDecs(
            PerformanceEnhancementModuleDec e) {
        return false;
    }

    public void prePerformanceEnhancementModuleDecMyDecs(
            PerformanceEnhancementModuleDec e) {}

    public void midPerformanceEnhancementModuleDecMyDecs(
            PerformanceEnhancementModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceEnhancementModuleDecMyDecs(
            PerformanceEnhancementModuleDec e) {}

    public boolean walkPerformanceEnhancementModuleDecMyUsesItems(
            PerformanceEnhancementModuleDec e) {
        return false;
    }

    public void prePerformanceEnhancementModuleDecMyUsesItems(
            PerformanceEnhancementModuleDec e) {}

    public void midPerformanceEnhancementModuleDecMyUsesItems(
            PerformanceEnhancementModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceEnhancementModuleDecMyUsesItems(
            PerformanceEnhancementModuleDec e) {}

    public boolean walkMathDefVariableDec(MathDefVariableDec e) {
        return false;
    }

    public void preMathDefVariableDec(MathDefVariableDec e) {}

    public void midMathDefVariableDec(MathDefVariableDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathDefVariableDec(MathDefVariableDec e) {}

    public boolean walkLambdaExp(LambdaExp e) {
        return false;
    }

    public void preLambdaExp(LambdaExp e) {}

    public void midLambdaExp(LambdaExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postLambdaExp(LambdaExp e) {}

    public boolean walkLambdaExpMyParameters(LambdaExp e) {
        return false;
    }

    public void preLambdaExpMyParameters(LambdaExp e) {}

    public void midLambdaExpMyParameters(LambdaExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postLambdaExpMyParameters(LambdaExp e) {}

    public boolean walkIfExp(IfExp e) {
        return false;
    }

    public void preIfExp(IfExp e) {}

    public void midIfExp(IfExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIfExp(IfExp e) {}

    public boolean walkFacilityInitFinalItem(FacilityInitFinalItem e) {
        return false;
    }

    public void preFacilityInitFinalItem(FacilityInitFinalItem e) {}

    public void midFacilityInitFinalItem(FacilityInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityInitFinalItem(FacilityInitFinalItem e) {}

    public boolean walkFacilityInitFinalItemMyFacilityDecs(
            FacilityInitFinalItem e) {
        return false;
    }

    public void preFacilityInitFinalItemMyFacilityDecs(FacilityInitFinalItem e) {}

    public void midFacilityInitFinalItemMyFacilityDecs(FacilityInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityInitFinalItemMyFacilityDecs(FacilityInitFinalItem e) {}

    public boolean walkFacilityInitFinalItemMyStatements(FacilityInitFinalItem e) {
        return false;
    }

    public void preFacilityInitFinalItemMyStatements(FacilityInitFinalItem e) {}

    public void midFacilityInitFinalItemMyStatements(FacilityInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityInitFinalItemMyStatements(FacilityInitFinalItem e) {}

    public boolean walkFacilityInitFinalItemMyVariableDecs(
            FacilityInitFinalItem e) {
        return false;
    }

    public void preFacilityInitFinalItemMyVariableDecs(FacilityInitFinalItem e) {}

    public void midFacilityInitFinalItemMyVariableDecs(FacilityInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityInitFinalItemMyVariableDecs(FacilityInitFinalItem e) {}

    public boolean walkTypeAssertionExp(TypeAssertionExp e) {
        return false;
    }

    public void preTypeAssertionExp(TypeAssertionExp e) {}

    public void midTypeAssertionExp(TypeAssertionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeAssertionExp(TypeAssertionExp e) {}

    public boolean walkOperationDec(OperationDec e) {
        return false;
    }

    public void preOperationDec(OperationDec e) {}

    public void midOperationDec(OperationDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postOperationDec(OperationDec e) {}

    public boolean walkOperationDecMyParameters(OperationDec e) {
        return false;
    }

    public void preOperationDecMyParameters(OperationDec e) {}

    public void midOperationDecMyParameters(OperationDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postOperationDecMyParameters(OperationDec e) {}

    public boolean walkAbstractSharedStateRealizationDec(
            AbstractSharedStateRealizationDec e) {
        return false;
    }

    public void preAbstractSharedStateRealizationDec(
            AbstractSharedStateRealizationDec e) {}

    public void midAbstractSharedStateRealizationDec(
            AbstractSharedStateRealizationDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractSharedStateRealizationDec(
            AbstractSharedStateRealizationDec e) {}

    public boolean walkAbstractSharedStateRealizationDecMyStateVars(
            AbstractSharedStateRealizationDec e) {
        return false;
    }

    public void preAbstractSharedStateRealizationDecMyStateVars(
            AbstractSharedStateRealizationDec e) {}

    public void midAbstractSharedStateRealizationDecMyStateVars(
            AbstractSharedStateRealizationDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractSharedStateRealizationDecMyStateVars(
            AbstractSharedStateRealizationDec e) {}

    public boolean walkAbstractVarDec(AbstractVarDec e) {
        return false;
    }

    public void preAbstractVarDec(AbstractVarDec e) {}

    public void midAbstractVarDec(AbstractVarDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractVarDec(AbstractVarDec e) {}

    public boolean walkMemoryStmt(MemoryStmt e) {
        return false;
    }

    public void preMemoryStmt(MemoryStmt e) {}

    public void midMemoryStmt(MemoryStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postMemoryStmt(MemoryStmt e) {}

    public boolean walkPerformanceSpecInitFinalItem(
            PerformanceSpecInitFinalItem e) {
        return false;
    }

    public void prePerformanceSpecInitFinalItem(PerformanceSpecInitFinalItem e) {}

    public void midPerformanceSpecInitFinalItem(PerformanceSpecInitFinalItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceSpecInitFinalItem(PerformanceSpecInitFinalItem e) {}

    public boolean walkIfStmt(IfStmt e) {
        return false;
    }

    public void preIfStmt(IfStmt e) {}

    public void midIfStmt(IfStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIfStmt(IfStmt e) {}

    public boolean walkIfStmtMyElseIfs(IfStmt e) {
        return false;
    }

    public void preIfStmtMyElseIfs(IfStmt e) {}

    public void midIfStmtMyElseIfs(IfStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIfStmtMyElseIfs(IfStmt e) {}

    public boolean walkIfStmtMyElseStatements(IfStmt e) {
        return false;
    }

    public void preIfStmtMyElseStatements(IfStmt e) {}

    public void midIfStmtMyElseStatements(IfStmt e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIfStmtMyElseStatements(IfStmt e) {}

    public boolean walkProgramFunctionExp(ProgramFunctionExp e) {
        return false;
    }

    public void preProgramFunctionExp(ProgramFunctionExp e) {}

    public void midProgramFunctionExp(ProgramFunctionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramFunctionExp(ProgramFunctionExp e) {}

    public boolean walkProgramFunctionExpMyExpressionArgs(ProgramFunctionExp e) {
        return false;
    }

    public void preProgramFunctionExpMyExpressionArgs(ProgramFunctionExp e) {}

    public void midProgramFunctionExpMyExpressionArgs(ProgramFunctionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramFunctionExpMyExpressionArgs(ProgramFunctionExp e) {}

    public boolean walkSwapStmt(SwapStmt e) {
        return false;
    }

    public void preSwapStmt(SwapStmt e) {}

    public void midSwapStmt(SwapStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postSwapStmt(SwapStmt e) {}

    public boolean walkIntegerExp(IntegerExp e) {
        return false;
    }

    public void preIntegerExp(IntegerExp e) {}

    public void midIntegerExp(IntegerExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIntegerExp(IntegerExp e) {}

    public boolean walkTypeDefinitionDec(TypeDefinitionDec e) {
        return false;
    }

    public void preTypeDefinitionDec(TypeDefinitionDec e) {}

    public void midTypeDefinitionDec(TypeDefinitionDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeDefinitionDec(TypeDefinitionDec e) {}

    public boolean walkPrecisModuleDec(PrecisModuleDec e) {
        return false;
    }

    public void prePrecisModuleDec(PrecisModuleDec e) {}

    public void midPrecisModuleDec(PrecisModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPrecisModuleDec(PrecisModuleDec e) {}

    public boolean walkPrecisModuleDecMyParameterDecs(PrecisModuleDec e) {
        return false;
    }

    public void prePrecisModuleDecMyParameterDecs(PrecisModuleDec e) {}

    public void midPrecisModuleDecMyParameterDecs(PrecisModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPrecisModuleDecMyParameterDecs(PrecisModuleDec e) {}

    public boolean walkPrecisModuleDecMyDecs(PrecisModuleDec e) {
        return false;
    }

    public void prePrecisModuleDecMyDecs(PrecisModuleDec e) {}

    public void midPrecisModuleDecMyDecs(PrecisModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPrecisModuleDecMyDecs(PrecisModuleDec e) {}

    public boolean walkPrecisModuleDecMyUsesItems(PrecisModuleDec e) {
        return false;
    }

    public void prePrecisModuleDecMyUsesItems(PrecisModuleDec e) {}

    public void midPrecisModuleDecMyUsesItems(PrecisModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPrecisModuleDecMyUsesItems(PrecisModuleDec e) {}

    public boolean walkEnhancementRealizModuleDec(EnhancementRealizModuleDec e) {
        return false;
    }

    public void preEnhancementRealizModuleDec(EnhancementRealizModuleDec e) {}

    public void midEnhancementRealizModuleDec(EnhancementRealizModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementRealizModuleDec(EnhancementRealizModuleDec e) {}

    public boolean walkEnhancementRealizModuleDecMyParameterDecs(
            EnhancementRealizModuleDec e) {
        return false;
    }

    public void preEnhancementRealizModuleDecMyParameterDecs(
            EnhancementRealizModuleDec e) {}

    public void midEnhancementRealizModuleDecMyParameterDecs(
            EnhancementRealizModuleDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postEnhancementRealizModuleDecMyParameterDecs(
            EnhancementRealizModuleDec e) {}

    public boolean walkEnhancementRealizModuleDecMyDecs(
            EnhancementRealizModuleDec e) {
        return false;
    }

    public void preEnhancementRealizModuleDecMyDecs(EnhancementRealizModuleDec e) {}

    public void midEnhancementRealizModuleDecMyDecs(
            EnhancementRealizModuleDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postEnhancementRealizModuleDecMyDecs(
            EnhancementRealizModuleDec e) {}

    public boolean walkEnhancementRealizModuleDecMyUsesItems(
            EnhancementRealizModuleDec e) {
        return false;
    }

    public void preEnhancementRealizModuleDecMyUsesItems(
            EnhancementRealizModuleDec e) {}

    public void midEnhancementRealizModuleDecMyUsesItems(
            EnhancementRealizModuleDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postEnhancementRealizModuleDecMyUsesItems(
            EnhancementRealizModuleDec e) {}

    public boolean walkIterativeExp(IterativeExp e) {
        return false;
    }

    public void preIterativeExp(IterativeExp e) {}

    public void midIterativeExp(IterativeExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIterativeExp(IterativeExp e) {}

    public boolean walkStatement(Statement e) {
        return false;
    }

    public void preStatement(Statement e) {}

    public void midStatement(Statement e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postStatement(Statement e) {}

    public boolean walkOutfixExp(OutfixExp e) {
        return false;
    }

    public void preOutfixExp(OutfixExp e) {}

    public void midOutfixExp(OutfixExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postOutfixExp(OutfixExp e) {}

    public boolean walkRecordTy(RecordTy e) {
        return false;
    }

    public void preRecordTy(RecordTy e) {}

    public void midRecordTy(RecordTy e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postRecordTy(RecordTy e) {}

    public boolean walkRecordTyMyInnerFields(RecordTy e) {
        return false;
    }

    public void preRecordTyMyInnerFields(RecordTy e) {}

    public void midRecordTyMyInnerFields(RecordTy e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRecordTyMyInnerFields(RecordTy e) {}

    public boolean walkPrefixExp(PrefixExp e) {
        return false;
    }

    public void prePrefixExp(PrefixExp e) {}

    public void midPrefixExp(PrefixExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postPrefixExp(PrefixExp e) {}

    public boolean walkVarDec(VarDec e) {
        return false;
    }

    public void preVarDec(VarDec e) {}

    public void midVarDec(VarDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postVarDec(VarDec e) {}

    public boolean walkWhileStmt(WhileStmt e) {
        return false;
    }

    public void preWhileStmt(WhileStmt e) {}

    public void midWhileStmt(WhileStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postWhileStmt(WhileStmt e) {}

    public boolean walkWhileStmtMyWhileStatements(WhileStmt e) {
        return false;
    }

    public void preWhileStmtMyWhileStatements(WhileStmt e) {}

    public void midWhileStmtMyWhileStatements(WhileStmt e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postWhileStmtMyWhileStatements(WhileStmt e) {}

    public boolean walkAssumeStmt(AssumeStmt e) {
        return false;
    }

    public void preAssumeStmt(AssumeStmt e) {}

    public void midAssumeStmt(AssumeStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postAssumeStmt(AssumeStmt e) {}

    public boolean walkProgramCharExp(ProgramCharExp e) {
        return false;
    }

    public void preProgramCharExp(ProgramCharExp e) {}

    public void midProgramCharExp(ProgramCharExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramCharExp(ProgramCharExp e) {}

    public boolean walkMathAssertionDec(MathAssertionDec e) {
        return false;
    }

    public void preMathAssertionDec(MathAssertionDec e) {}

    public void midMathAssertionDec(MathAssertionDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathAssertionDec(MathAssertionDec e) {}

    public boolean walkLiteralExp(LiteralExp e) {
        return false;
    }

    public void preLiteralExp(LiteralExp e) {}

    public void midLiteralExp(LiteralExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postLiteralExp(LiteralExp e) {}

    public boolean walkFacilityModuleDec(FacilityModuleDec e) {
        return false;
    }

    public void preFacilityModuleDec(FacilityModuleDec e) {}

    public void midFacilityModuleDec(FacilityModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityModuleDec(FacilityModuleDec e) {}

    public boolean walkFacilityModuleDecMyParameterDecs(FacilityModuleDec e) {
        return false;
    }

    public void preFacilityModuleDecMyParameterDecs(FacilityModuleDec e) {}

    public void midFacilityModuleDecMyParameterDecs(FacilityModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityModuleDecMyParameterDecs(FacilityModuleDec e) {}

    public boolean walkFacilityModuleDecMyDecs(FacilityModuleDec e) {
        return false;
    }

    public void preFacilityModuleDecMyDecs(FacilityModuleDec e) {}

    public void midFacilityModuleDecMyDecs(FacilityModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityModuleDecMyDecs(FacilityModuleDec e) {}

    public boolean walkFacilityModuleDecMyUsesItems(FacilityModuleDec e) {
        return false;
    }

    public void preFacilityModuleDecMyUsesItems(FacilityModuleDec e) {}

    public void midFacilityModuleDecMyUsesItems(FacilityModuleDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityModuleDecMyUsesItems(FacilityModuleDec e) {}

    public boolean walkMathVarDec(MathVarDec e) {
        return false;
    }

    public void preMathVarDec(MathVarDec e) {}

    public void midMathVarDec(MathVarDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postMathVarDec(MathVarDec e) {}

    public boolean walkTypeFamilyDec(TypeFamilyDec e) {
        return false;
    }

    public void preTypeFamilyDec(TypeFamilyDec e) {}

    public void midTypeFamilyDec(TypeFamilyDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeFamilyDec(TypeFamilyDec e) {}

    public boolean walkTypeFamilyDecMyDefVarList(TypeFamilyDec e) {
        return false;
    }

    public void preTypeFamilyDecMyDefVarList(TypeFamilyDec e) {}

    public void midTypeFamilyDecMyDefVarList(TypeFamilyDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeFamilyDecMyDefVarList(TypeFamilyDec e) {}

    public boolean walkLoopVerificationItem(LoopVerificationItem e) {
        return false;
    }

    public void preLoopVerificationItem(LoopVerificationItem e) {}

    public void midLoopVerificationItem(LoopVerificationItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postLoopVerificationItem(LoopVerificationItem e) {}

    public boolean walkLoopVerificationItemMyChangingVars(LoopVerificationItem e) {
        return false;
    }

    public void preLoopVerificationItemMyChangingVars(LoopVerificationItem e) {}

    public void midLoopVerificationItemMyChangingVars(LoopVerificationItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postLoopVerificationItemMyChangingVars(LoopVerificationItem e) {}

    public boolean walkSetCollectionExp(SetCollectionExp e) {
        return false;
    }

    public void preSetCollectionExp(SetCollectionExp e) {}

    public void midSetCollectionExp(SetCollectionExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSetCollectionExp(SetCollectionExp e) {}

    public boolean walkSetExp(SetExp e) {
        return false;
    }

    public void preSetExp(SetExp e) {}

    public void midSetExp(SetExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postSetExp(SetExp e) {}

    public boolean walkPresumeStmt(PresumeStmt e) {
        return false;
    }

    public void prePresumeStmt(PresumeStmt e) {}

    public void midPresumeStmt(PresumeStmt e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPresumeStmt(PresumeStmt e) {}

    public boolean walkFuncAssignStmt(FuncAssignStmt e) {
        return false;
    }

    public void preFuncAssignStmt(FuncAssignStmt e) {}

    public void midFuncAssignStmt(FuncAssignStmt e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFuncAssignStmt(FuncAssignStmt e) {}

    public boolean walkTupleExp(TupleExp e) {
        return false;
    }

    public void preTupleExp(TupleExp e) {}

    public void midTupleExp(TupleExp e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTupleExp(TupleExp e) {}

    public boolean walkTupleExpMyFields(TupleExp e) {
        return false;
    }

    public void preTupleExpMyFields(TupleExp e) {}

    public void midTupleExpMyFields(TupleExp e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTupleExpMyFields(TupleExp e) {}

    public boolean walkSharedStateRealizationDec(SharedStateRealizationDec e) {
        return false;
    }

    public void preSharedStateRealizationDec(SharedStateRealizationDec e) {}

    public void midSharedStateRealizationDec(SharedStateRealizationDec e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSharedStateRealizationDec(SharedStateRealizationDec e) {}

    public boolean walkSharedStateRealizationDecMyStateVars(
            SharedStateRealizationDec e) {
        return false;
    }

    public void preSharedStateRealizationDecMyStateVars(
            SharedStateRealizationDec e) {}

    public void midSharedStateRealizationDecMyStateVars(
            SharedStateRealizationDec e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postSharedStateRealizationDecMyStateVars(
            SharedStateRealizationDec e) {}

}