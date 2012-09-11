package edu.clemson.cs.r2jt.treewalk;

import edu.clemson.cs.r2jt.absyn.*;

public abstract class TreeWalkerVisitor {

    public void preAny(ResolveConceptualElement data) {}

    public void postAny(ResolveConceptualElement data) {}

    // AffectsItem
    public void preAffectsItem(AffectsItem data) {}

    public void midAffectsItem(AffectsItem node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postAffectsItem(AffectsItem data) {}

    // AlternativeExp
    public void preAlternativeExp(AlternativeExp data) {}

    public void midAlternativeExp(AlternativeExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postAlternativeExp(AlternativeExp data) {}

    public void preAlternativeExpAlternatives(AlternativeExp node) {}

    public void postAlternativeExpAlternatives(AlternativeExp node) {}

    // AltItemExp
    public void preAltItemExp(AltItemExp data) {}

    public void midAltItemExp(AltItemExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postAltItemExp(AltItemExp data) {}

    // ArrayTy
    public void preArrayTy(ArrayTy data) {}

    public void midArrayTy(ArrayTy node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postArrayTy(ArrayTy data) {}

    // AssumeStmt
    public void preAssumeStmt(AssumeStmt data) {}

    public void midAssumeStmt(AssumeStmt node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postAssumeStmt(AssumeStmt data) {}

    // AuxCodeStmt
    public void preAuxCodeStmt(AuxCodeStmt data) {}

    public void midAuxCodeStmt(AuxCodeStmt node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postAuxCodeStmt(AuxCodeStmt data) {}

    public void preAuxCodeStmtStatements(AuxCodeStmt node) {}

    public void postAuxCodeStmtStatements(AuxCodeStmt node) {}

    // AuxVarDec
    public void preAuxVarDec(AuxVarDec data) {}

    public void midAuxVarDec(AuxVarDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postAuxVarDec(AuxVarDec data) {}

    // BetweenExp
    public void preBetweenExp(BetweenExp data) {}

    public void midBetweenExp(BetweenExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postBetweenExp(BetweenExp data) {}

    public void preBetweenExpLessExps(BetweenExp node) {}

    public void postBetweenExpLessExps(BetweenExp node) {}

    // BooleanTy
    public void preBooleanTy(BooleanTy data) {}

    public void midBooleanTy(BooleanTy node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postBooleanTy(BooleanTy data) {}

    // CallStmt
    public void preCallStmt(CallStmt data) {}

    public void midCallStmt(CallStmt node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postCallStmt(CallStmt data) {}

    public void preCallStmtArguments(CallStmt node) {}

    public void postCallStmtArguments(CallStmt node) {}

    // CartProdTy
    public void preCartProdTy(CartProdTy data) {}

    public void midCartProdTy(CartProdTy node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postCartProdTy(CartProdTy data) {}

    public void preCartProdTyFields(CartProdTy node) {}

    public void postCartProdTyFields(CartProdTy node) {}

    // CategoricalDefinitionDec
    public void preCategoricalDefinitionDec(CategoricalDefinitionDec data) {}

    public void midCategoricalDefinitionDec(CategoricalDefinitionDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postCategoricalDefinitionDec(CategoricalDefinitionDec data) {}

    // CharExp
    public void preCharExp(CharExp data) {}

    public void midCharExp(CharExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postCharExp(CharExp data) {}

    // ChoiceItem
    public void preChoiceItem(ChoiceItem data) {}

    public void midChoiceItem(ChoiceItem node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postChoiceItem(ChoiceItem data) {}

    public void preChoiceItemTest(ChoiceItem node) {}

    public void postChoiceItemTest(ChoiceItem node) {}

    public void preChoiceItemThenclause(ChoiceItem node) {}

    public void postChoiceItemThenclause(ChoiceItem node) {}

    // ConceptBodyModuleDec
    public void preConceptBodyModuleDec(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDec(ConceptBodyModuleDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postConceptBodyModuleDec(ConceptBodyModuleDec data) {}

    public void preConceptBodyModuleDecParameters(ConceptBodyModuleDec node) {}

    public void postConceptBodyModuleDecParameters(ConceptBodyModuleDec node) {}

    public void preConceptBodyModuleDecEnhancementNames(
            ConceptBodyModuleDec node) {}

    public void postConceptBodyModuleDecEnhancementNames(
            ConceptBodyModuleDec node) {}

    public void preConceptBodyModuleDecUsesItems(ConceptBodyModuleDec node) {}

    public void postConceptBodyModuleDecUsesItems(ConceptBodyModuleDec node) {}

    public void preConceptBodyModuleDecConventions(ConceptBodyModuleDec node) {}

    public void postConceptBodyModuleDecConventions(ConceptBodyModuleDec node) {}

    public void preConceptBodyModuleDecCorrs(ConceptBodyModuleDec node) {}

    public void postConceptBodyModuleDecCorrs(ConceptBodyModuleDec node) {}

    public void preConceptBodyModuleDecDecs(ConceptBodyModuleDec node) {}

    public void postConceptBodyModuleDecDecs(ConceptBodyModuleDec node) {}

    // ConceptModuleDec
    public void preConceptModuleDec(ConceptModuleDec data) {}

    public void midConceptModuleDec(ConceptModuleDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postConceptModuleDec(ConceptModuleDec data) {}

    public void preConceptModuleDecParameters(ConceptModuleDec node) {}

    public void postConceptModuleDecParameters(ConceptModuleDec node) {}

    public void preConceptModuleDecUsesItems(ConceptModuleDec node) {}

    public void postConceptModuleDecUsesItems(ConceptModuleDec node) {}

    public void preConceptModuleDecConstraints(ConceptModuleDec node) {}

    public void postConceptModuleDecConstraints(ConceptModuleDec node) {}

    public void preConceptModuleDecDecs(ConceptModuleDec node) {}

    public void postConceptModuleDecDecs(ConceptModuleDec node) {}

    // ConceptTypeParamDec
    public void preConceptTypeParamDec(ConceptTypeParamDec data) {}

    public void midConceptTypeParamDec(ConceptTypeParamDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postConceptTypeParamDec(ConceptTypeParamDec data) {}

    // ConditionItem
    public void preConditionItem(ConditionItem data) {}

    public void midConditionItem(ConditionItem node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postConditionItem(ConditionItem data) {}

    public void preConditionItemThenclause(ConditionItem node) {}

    public void postConditionItemThenclause(ConditionItem node) {}

    // ConfirmStmt
    public void preConfirmStmt(ConfirmStmt data) {}

    public void midConfirmStmt(ConfirmStmt node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postConfirmStmt(ConfirmStmt data) {}

    // ConstantParamDec
    public void preConstantParamDec(ConstantParamDec data) {}

    public void midConstantParamDec(ConstantParamDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postConstantParamDec(ConstantParamDec data) {}

    // ConstructedTy
    public void preConstructedTy(ConstructedTy data) {}

    public void midConstructedTy(ConstructedTy node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postConstructedTy(ConstructedTy data) {}

    public void preConstructedTyArgs(ConstructedTy node) {}

    public void postConstructedTyArgs(ConstructedTy node) {}

    // Dec
    public void preDec(Dec data) {}

    public void midDec(Dec node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postDec(Dec data) {}

    // DeductionExp
    public void preDeductionExp(DeductionExp data) {}

    public void midDeductionExp(DeductionExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postDeductionExp(DeductionExp data) {}

    // DefinitionDec
    public void preDefinitionDec(DefinitionDec data) {}

    public void midDefinitionDec(DefinitionDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postDefinitionDec(DefinitionDec data) {}

    public void preDefinitionDecParameters(DefinitionDec node) {}

    public void postDefinitionDecParameters(DefinitionDec node) {}

    // DotExp
    public void preDotExp(DotExp data) {}

    public void midDotExp(DotExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postDotExp(DotExp data) {}

    public void preDotExpSegments(DotExp node) {}

    public void postDotExpSegments(DotExp node) {}

    // DoubleExp
    public void preDoubleExp(DoubleExp data) {}

    public void midDoubleExp(DoubleExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postDoubleExp(DoubleExp data) {}

    // EnhancementBodyItem
    public void preEnhancementBodyItem(EnhancementBodyItem data) {}

    public void midEnhancementBodyItem(EnhancementBodyItem node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postEnhancementBodyItem(EnhancementBodyItem data) {}

    public void preEnhancementBodyItemParams(EnhancementBodyItem node) {}

    public void postEnhancementBodyItemParams(EnhancementBodyItem node) {}

    public void preEnhancementBodyItemBodyParams(EnhancementBodyItem node) {}

    public void postEnhancementBodyItemBodyParams(EnhancementBodyItem node) {}

    // EnhancementBodyModuleDec
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDec(EnhancementBodyModuleDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec data) {}

    public void preEnhancementBodyModuleDecParameters(
            EnhancementBodyModuleDec node) {}

    public void postEnhancementBodyModuleDecParameters(
            EnhancementBodyModuleDec node) {}

    public void preEnhancementBodyModuleDecEnhancementBodies(
            EnhancementBodyModuleDec node) {}

    public void postEnhancementBodyModuleDecEnhancementBodies(
            EnhancementBodyModuleDec node) {}

    public void preEnhancementBodyModuleDecUsesItems(
            EnhancementBodyModuleDec node) {}

    public void postEnhancementBodyModuleDecUsesItems(
            EnhancementBodyModuleDec node) {}

    public void preEnhancementBodyModuleDecConventions(
            EnhancementBodyModuleDec node) {}

    public void postEnhancementBodyModuleDecConventions(
            EnhancementBodyModuleDec node) {}

    public void preEnhancementBodyModuleDecCorrs(EnhancementBodyModuleDec node) {}

    public void postEnhancementBodyModuleDecCorrs(EnhancementBodyModuleDec node) {}

    public void preEnhancementBodyModuleDecDecs(EnhancementBodyModuleDec node) {}

    public void postEnhancementBodyModuleDecDecs(EnhancementBodyModuleDec node) {}

    // EnhancementItem
    public void preEnhancementItem(EnhancementItem data) {}

    public void midEnhancementItem(EnhancementItem node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postEnhancementItem(EnhancementItem data) {}

    public void preEnhancementItemParams(EnhancementItem node) {}

    public void postEnhancementItemParams(EnhancementItem node) {}

    // EnhancementModuleDec
    public void preEnhancementModuleDec(EnhancementModuleDec data) {}

    public void midEnhancementModuleDec(EnhancementModuleDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postEnhancementModuleDec(EnhancementModuleDec data) {}

    public void preEnhancementModuleDecParameters(EnhancementModuleDec node) {}

    public void postEnhancementModuleDecParameters(EnhancementModuleDec node) {}

    public void preEnhancementModuleDecUsesItems(EnhancementModuleDec node) {}

    public void postEnhancementModuleDecUsesItems(EnhancementModuleDec node) {}

    public void preEnhancementModuleDecDecs(EnhancementModuleDec node) {}

    public void postEnhancementModuleDecDecs(EnhancementModuleDec node) {}

    // EqualsExp
    public void preEqualsExp(EqualsExp data) {}

    public void midEqualsExp(EqualsExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postEqualsExp(EqualsExp data) {}

    // Exp
    public void preExp(Exp data) {}

    public void midExp(Exp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postExp(Exp data) {}

    // FacilityDec
    public void preFacilityDec(FacilityDec data) {}

    public void midFacilityDec(FacilityDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postFacilityDec(FacilityDec data) {}

    public void preFacilityDecConceptParams(FacilityDec node) {}

    public void postFacilityDecConceptParams(FacilityDec node) {}

    public void preFacilityDecEnhancements(FacilityDec node) {}

    public void postFacilityDecEnhancements(FacilityDec node) {}

    public void preFacilityDecBodyParams(FacilityDec node) {}

    public void postFacilityDecBodyParams(FacilityDec node) {}

    public void preFacilityDecEnhancementBodies(FacilityDec node) {}

    public void postFacilityDecEnhancementBodies(FacilityDec node) {}

    // FacilityModuleDec
    public void preFacilityModuleDec(FacilityModuleDec data) {}

    public void midFacilityModuleDec(FacilityModuleDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postFacilityModuleDec(FacilityModuleDec data) {}

    public void preFacilityModuleDecUsesItems(FacilityModuleDec node) {}

    public void postFacilityModuleDecUsesItems(FacilityModuleDec node) {}

    public void preFacilityModuleDecDecs(FacilityModuleDec node) {}

    public void postFacilityModuleDecDecs(FacilityModuleDec node) {}

    // FacilityOperationDec
    public void preFacilityOperationDec(FacilityOperationDec data) {}

    public void midFacilityOperationDec(FacilityOperationDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postFacilityOperationDec(FacilityOperationDec data) {}

    public void preFacilityOperationDecParameters(FacilityOperationDec node) {}

    public void postFacilityOperationDecParameters(FacilityOperationDec node) {}

    public void preFacilityOperationDecStateVars(FacilityOperationDec node) {}

    public void postFacilityOperationDecStateVars(FacilityOperationDec node) {}

    public void preFacilityOperationDecFacilities(FacilityOperationDec node) {}

    public void postFacilityOperationDecFacilities(FacilityOperationDec node) {}

    public void preFacilityOperationDecVariables(FacilityOperationDec node) {}

    public void postFacilityOperationDecVariables(FacilityOperationDec node) {}

    public void preFacilityOperationDecAuxVariables(FacilityOperationDec node) {}

    public void postFacilityOperationDecAuxVariables(FacilityOperationDec node) {}

    public void preFacilityOperationDecStatements(FacilityOperationDec node) {}

    public void postFacilityOperationDecStatements(FacilityOperationDec node) {}

    // FacilityTypeDec
    public void preFacilityTypeDec(FacilityTypeDec data) {}

    public void midFacilityTypeDec(FacilityTypeDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postFacilityTypeDec(FacilityTypeDec data) {}

    // FieldExp
    public void preFieldExp(FieldExp data) {}

    public void midFieldExp(FieldExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postFieldExp(FieldExp data) {}

    // FinalItem
    public void preFinalItem(FinalItem data) {}

    public void midFinalItem(FinalItem node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postFinalItem(FinalItem data) {}

    public void preFinalItemStateVars(FinalItem node) {}

    public void postFinalItemStateVars(FinalItem node) {}

    public void preFinalItemFacilities(FinalItem node) {}

    public void postFinalItemFacilities(FinalItem node) {}

    public void preFinalItemVariables(FinalItem node) {}

    public void postFinalItemVariables(FinalItem node) {}

    public void preFinalItemAuxVariables(FinalItem node) {}

    public void postFinalItemAuxVariables(FinalItem node) {}

    public void preFinalItemStatements(FinalItem node) {}

    public void postFinalItemStatements(FinalItem node) {}

    // FuncAssignStmt
    public void preFuncAssignStmt(FuncAssignStmt data) {}

    public void midFuncAssignStmt(FuncAssignStmt node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postFuncAssignStmt(FuncAssignStmt data) {}

    // FunctionArgList
    public void preFunctionArgList(FunctionArgList data) {}

    public void midFunctionArgList(FunctionArgList node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postFunctionArgList(FunctionArgList data) {}

    public void preFunctionArgListArguments(FunctionArgList node) {}

    public void postFunctionArgListArguments(FunctionArgList node) {}

    // FunctionExp
    public void preFunctionExp(FunctionExp data) {}

    public void midFunctionExp(FunctionExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postFunctionExp(FunctionExp data) {}

    public void preFunctionExpParamList(FunctionExp node) {}

    public void postFunctionExpParamList(FunctionExp node) {}

    // FunctionTy
    public void preFunctionTy(FunctionTy data) {}

    public void midFunctionTy(FunctionTy node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postFunctionTy(FunctionTy data) {}

    // GoalExp
    public void preGoalExp(GoalExp data) {}

    public void midGoalExp(GoalExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postGoalExp(GoalExp data) {}

    // HypDesigExp
    public void preHypDesigExp(HypDesigExp data) {}

    public void midHypDesigExp(HypDesigExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postHypDesigExp(HypDesigExp data) {}

    // IfExp
    public void preIfExp(IfExp data) {}

    public void midIfExp(IfExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postIfExp(IfExp data) {}

    // IfStmt
    public void preIfStmt(IfStmt data) {}

    public void midIfStmt(IfStmt node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postIfStmt(IfStmt data) {}

    public void preIfStmtThenclause(IfStmt node) {}

    public void postIfStmtThenclause(IfStmt node) {}

    public void preIfStmtElseifpairs(IfStmt node) {}

    public void postIfStmtElseifpairs(IfStmt node) {}

    public void preIfStmtElseclause(IfStmt node) {}

    public void postIfStmtElseclause(IfStmt node) {}

    // InfixExp
    public void preInfixExp(InfixExp data) {}

    public void midInfixExp(InfixExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postInfixExp(InfixExp data) {}

    // InitItem
    public void preInitItem(InitItem data) {}

    public void midInitItem(InitItem node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postInitItem(InitItem data) {}

    public void preInitItemStateVars(InitItem node) {}

    public void postInitItemStateVars(InitItem node) {}

    public void preInitItemFacilities(InitItem node) {}

    public void postInitItemFacilities(InitItem node) {}

    public void preInitItemVariables(InitItem node) {}

    public void postInitItemVariables(InitItem node) {}

    public void preInitItemAuxVariables(InitItem node) {}

    public void postInitItemAuxVariables(InitItem node) {}

    public void preInitItemStatements(InitItem node) {}

    public void postInitItemStatements(InitItem node) {}

    // IntegerExp
    public void preIntegerExp(IntegerExp data) {}

    public void midIntegerExp(IntegerExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postIntegerExp(IntegerExp data) {}

    // IsInExp
    public void preIsInExp(IsInExp data) {}

    public void midIsInExp(IsInExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postIsInExp(IsInExp data) {}

    // IterateExitStmt
    public void preIterateExitStmt(IterateExitStmt data) {}

    public void midIterateExitStmt(IterateExitStmt node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postIterateExitStmt(IterateExitStmt data) {}

    public void preIterateExitStmtStatements(IterateExitStmt node) {}

    public void postIterateExitStmtStatements(IterateExitStmt node) {}

    // IterateStmt
    public void preIterateStmt(IterateStmt data) {}

    public void midIterateStmt(IterateStmt node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postIterateStmt(IterateStmt data) {}

    public void preIterateStmtChanging(IterateStmt node) {}

    public void postIterateStmtChanging(IterateStmt node) {}

    public void preIterateStmtStatements(IterateStmt node) {}

    public void postIterateStmtStatements(IterateStmt node) {}

    // IterativeExp
    public void preIterativeExp(IterativeExp data) {}

    public void midIterativeExp(IterativeExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postIterativeExp(IterativeExp data) {}

    // JustificationExp
    public void preJustificationExp(JustificationExp data) {}

    public void midJustificationExp(JustificationExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postJustificationExp(JustificationExp data) {}

    // JustifiedExp
    public void preJustifiedExp(JustifiedExp data) {}

    public void midJustifiedExp(JustifiedExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postJustifiedExp(JustifiedExp data) {}

    // LambdaExp
    public void preLambdaExp(LambdaExp data) {}

    public void midLambdaExp(LambdaExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postLambdaExp(LambdaExp data) {}

    // LineNumberedExp
    public void preLineNumberedExp(LineNumberedExp data) {}

    public void midLineNumberedExp(LineNumberedExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postLineNumberedExp(LineNumberedExp data) {}

    // MathAssertionDec
    public void preMathAssertionDec(MathAssertionDec data) {}

    public void midMathAssertionDec(MathAssertionDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postMathAssertionDec(MathAssertionDec data) {}

    // MathModuleDec
    public void preMathModuleDec(MathModuleDec data) {}

    public void midMathModuleDec(MathModuleDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postMathModuleDec(MathModuleDec data) {}

    public void preMathModuleDecParameters(MathModuleDec node) {}

    public void postMathModuleDecParameters(MathModuleDec node) {}

    public void preMathModuleDecUsesItems(MathModuleDec node) {}

    public void postMathModuleDecUsesItems(MathModuleDec node) {}

    public void preMathModuleDecDecs(MathModuleDec node) {}

    public void postMathModuleDecDecs(MathModuleDec node) {}

    // MathRefExp
    public void preMathRefExp(MathRefExp data) {}

    public void midMathRefExp(MathRefExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postMathRefExp(MathRefExp data) {}

    public void preMathRefExpParams(MathRefExp node) {}

    public void postMathRefExpParams(MathRefExp node) {}

    // MathTypeDec
    public void preMathTypeDec(MathTypeDec data) {}

    public void midMathTypeDec(MathTypeDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postMathTypeDec(MathTypeDec data) {}

    // MathTypeFormalDec
    public void preMathTypeFormalDec(MathTypeFormalDec data) {}

    public void midMathTypeFormalDec(MathTypeFormalDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postMathTypeFormalDec(MathTypeFormalDec data) {}

    // MathVarDec
    public void preMathVarDec(MathVarDec data) {}

    public void midMathVarDec(MathVarDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postMathVarDec(MathVarDec data) {}

    // MemoryStmt
    public void preMemoryStmt(MemoryStmt data) {}

    public void midMemoryStmt(MemoryStmt node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postMemoryStmt(MemoryStmt data) {}

    // ModuleArgumentItem
    public void preModuleArgumentItem(ModuleArgumentItem data) {}

    public void midModuleArgumentItem(ModuleArgumentItem node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postModuleArgumentItem(ModuleArgumentItem data) {}

    // ModuleDec
    public void preModuleDec(ModuleDec data) {}

    public void midModuleDec(ModuleDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postModuleDec(ModuleDec data) {}

    // NameTy
    public void preNameTy(NameTy data) {}

    public void midNameTy(NameTy node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postNameTy(NameTy data) {}

    // OldExp
    public void preOldExp(OldExp data) {}

    public void midOldExp(OldExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postOldExp(OldExp data) {}

    // OperationDec
    public void preOperationDec(OperationDec data) {}

    public void midOperationDec(OperationDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postOperationDec(OperationDec data) {}

    public void preOperationDecParameters(OperationDec node) {}

    public void postOperationDecParameters(OperationDec node) {}

    public void preOperationDecStateVars(OperationDec node) {}

    public void postOperationDecStateVars(OperationDec node) {}

    // OutfixExp
    public void preOutfixExp(OutfixExp data) {}

    public void midOutfixExp(OutfixExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postOutfixExp(OutfixExp data) {}

    // ParameterVarDec
    public void preParameterVarDec(ParameterVarDec data) {}

    public void midParameterVarDec(ParameterVarDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postParameterVarDec(ParameterVarDec data) {}

    // PerformanceFinalItem
    public void prePerformanceFinalItem(PerformanceFinalItem data) {}

    public void midPerformanceFinalItem(PerformanceFinalItem node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postPerformanceFinalItem(PerformanceFinalItem data) {}

    public void prePerformanceFinalItemStateVars(PerformanceFinalItem node) {}

    public void postPerformanceFinalItemStateVars(PerformanceFinalItem node) {}

    public void prePerformanceFinalItemFacilities(PerformanceFinalItem node) {}

    public void postPerformanceFinalItemFacilities(PerformanceFinalItem node) {}

    public void prePerformanceFinalItemVariables(PerformanceFinalItem node) {}

    public void postPerformanceFinalItemVariables(PerformanceFinalItem node) {}

    public void prePerformanceFinalItemAuxVariables(PerformanceFinalItem node) {}

    public void postPerformanceFinalItemAuxVariables(PerformanceFinalItem node) {}

    public void prePerformanceFinalItemStatements(PerformanceFinalItem node) {}

    public void postPerformanceFinalItemStatements(PerformanceFinalItem node) {}

    // PerformanceInitItem
    public void prePerformanceInitItem(PerformanceInitItem data) {}

    public void midPerformanceInitItem(PerformanceInitItem node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postPerformanceInitItem(PerformanceInitItem data) {}

    public void prePerformanceInitItemStateVars(PerformanceInitItem node) {}

    public void postPerformanceInitItemStateVars(PerformanceInitItem node) {}

    public void prePerformanceInitItemFacilities(PerformanceInitItem node) {}

    public void postPerformanceInitItemFacilities(PerformanceInitItem node) {}

    public void prePerformanceInitItemVariables(PerformanceInitItem node) {}

    public void postPerformanceInitItemVariables(PerformanceInitItem node) {}

    public void prePerformanceInitItemAuxVariables(PerformanceInitItem node) {}

    public void postPerformanceInitItemAuxVariables(PerformanceInitItem node) {}

    public void prePerformanceInitItemStatements(PerformanceInitItem node) {}

    public void postPerformanceInitItemStatements(PerformanceInitItem node) {}

    // PerformanceModuleDec
    public void prePerformanceModuleDec(PerformanceModuleDec data) {}

    public void midPerformanceModuleDec(PerformanceModuleDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postPerformanceModuleDec(PerformanceModuleDec data) {}

    public void prePerformanceModuleDecParameters(PerformanceModuleDec node) {}

    public void postPerformanceModuleDecParameters(PerformanceModuleDec node) {}

    public void prePerformanceModuleDecUsesItems(PerformanceModuleDec node) {}

    public void postPerformanceModuleDecUsesItems(PerformanceModuleDec node) {}

    public void prePerformanceModuleDecConstraints(PerformanceModuleDec node) {}

    public void postPerformanceModuleDecConstraints(PerformanceModuleDec node) {}

    public void prePerformanceModuleDecDecs(PerformanceModuleDec node) {}

    public void postPerformanceModuleDecDecs(PerformanceModuleDec node) {}

    // PerformanceOperationDec
    public void prePerformanceOperationDec(PerformanceOperationDec data) {}

    public void midPerformanceOperationDec(PerformanceOperationDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postPerformanceOperationDec(PerformanceOperationDec data) {}

    public void prePerformanceOperationDecParameters(
            PerformanceOperationDec node) {}

    public void postPerformanceOperationDecParameters(
            PerformanceOperationDec node) {}

    public void prePerformanceOperationDecStateVars(PerformanceOperationDec node) {}

    public void postPerformanceOperationDecStateVars(
            PerformanceOperationDec node) {}

    // PerformanceTypeDec
    public void prePerformanceTypeDec(PerformanceTypeDec data) {}

    public void midPerformanceTypeDec(PerformanceTypeDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postPerformanceTypeDec(PerformanceTypeDec data) {}

    // PrefixExp
    public void prePrefixExp(PrefixExp data) {}

    public void midPrefixExp(PrefixExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postPrefixExp(PrefixExp data) {}

    // ProcedureDec
    public void preProcedureDec(ProcedureDec data) {}

    public void midProcedureDec(ProcedureDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProcedureDec(ProcedureDec data) {}

    public void preProcedureDecParameters(ProcedureDec node) {}

    public void postProcedureDecParameters(ProcedureDec node) {}

    public void preProcedureDecStateVars(ProcedureDec node) {}

    public void postProcedureDecStateVars(ProcedureDec node) {}

    public void preProcedureDecFacilities(ProcedureDec node) {}

    public void postProcedureDecFacilities(ProcedureDec node) {}

    public void preProcedureDecVariables(ProcedureDec node) {}

    public void postProcedureDecVariables(ProcedureDec node) {}

    public void preProcedureDecAuxVariables(ProcedureDec node) {}

    public void postProcedureDecAuxVariables(ProcedureDec node) {}

    public void preProcedureDecStatements(ProcedureDec node) {}

    public void postProcedureDecStatements(ProcedureDec node) {}

    // ProgramCharExp
    public void preProgramCharExp(ProgramCharExp data) {}

    public void midProgramCharExp(ProgramCharExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProgramCharExp(ProgramCharExp data) {}

    // ProgramDotExp
    public void preProgramDotExp(ProgramDotExp data) {}

    public void midProgramDotExp(ProgramDotExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProgramDotExp(ProgramDotExp data) {}

    public void preProgramDotExpSegments(ProgramDotExp node) {}

    public void postProgramDotExpSegments(ProgramDotExp node) {}

    // ProgramDoubleExp
    public void preProgramDoubleExp(ProgramDoubleExp data) {}

    public void midProgramDoubleExp(ProgramDoubleExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProgramDoubleExp(ProgramDoubleExp data) {}

    // ProgramExp
    public void preProgramExp(ProgramExp data) {}

    public void midProgramExp(ProgramExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProgramExp(ProgramExp data) {}

    // ProgramFunctionExp
    public void preProgramFunctionExp(ProgramFunctionExp data) {}

    public void midProgramFunctionExp(ProgramFunctionExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProgramFunctionExp(ProgramFunctionExp data) {}

    public void preProgramFunctionExpArguments(ProgramFunctionExp node) {}

    public void postProgramFunctionExpArguments(ProgramFunctionExp node) {}

    // ProgramIntegerExp
    public void preProgramIntegerExp(ProgramIntegerExp data) {}

    public void midProgramIntegerExp(ProgramIntegerExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProgramIntegerExp(ProgramIntegerExp data) {}

    // ProgramOpExp
    public void preProgramOpExp(ProgramOpExp data) {}

    public void midProgramOpExp(ProgramOpExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProgramOpExp(ProgramOpExp data) {}

    // ProgramParamExp
    public void preProgramParamExp(ProgramParamExp data) {}

    public void midProgramParamExp(ProgramParamExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProgramParamExp(ProgramParamExp data) {}

    public void preProgramParamExpArguments(ProgramParamExp node) {}

    public void postProgramParamExpArguments(ProgramParamExp node) {}

    // ProgramStringExp
    public void preProgramStringExp(ProgramStringExp data) {}

    public void midProgramStringExp(ProgramStringExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProgramStringExp(ProgramStringExp data) {}

    // ProofDec
    public void preProofDec(ProofDec data) {}

    public void midProofDec(ProofDec node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProofDec(ProofDec data) {}

    public void preProofDecStatements(ProofDec node) {}

    public void postProofDecStatements(ProofDec node) {}

    public void preProofDecBaseCase(ProofDec node) {}

    public void postProofDecBaseCase(ProofDec node) {}

    public void preProofDecInductiveCase(ProofDec node) {}

    public void postProofDecInductiveCase(ProofDec node) {}

    // ProofDefinitionExp
    public void preProofDefinitionExp(ProofDefinitionExp data) {}

    public void midProofDefinitionExp(ProofDefinitionExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProofDefinitionExp(ProofDefinitionExp data) {}

    // ProofModuleDec
    public void preProofModuleDec(ProofModuleDec data) {}

    public void midProofModuleDec(ProofModuleDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postProofModuleDec(ProofModuleDec data) {}

    public void preProofModuleDecModuleParams(ProofModuleDec node) {}

    public void postProofModuleDecModuleParams(ProofModuleDec node) {}

    public void preProofModuleDecUsesItems(ProofModuleDec node) {}

    public void postProofModuleDecUsesItems(ProofModuleDec node) {}

    public void preProofModuleDecDecs(ProofModuleDec node) {}

    public void postProofModuleDecDecs(ProofModuleDec node) {}

    // QuantExp
    public void preQuantExp(QuantExp data) {}

    public void midQuantExp(QuantExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postQuantExp(QuantExp data) {}

    public void preQuantExpVars(QuantExp node) {}

    public void postQuantExpVars(QuantExp node) {}

    // RealizationParamDec
    public void preRealizationParamDec(RealizationParamDec data) {}

    public void midRealizationParamDec(RealizationParamDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postRealizationParamDec(RealizationParamDec data) {}

    // RecordTy
    public void preRecordTy(RecordTy data) {}

    public void midRecordTy(RecordTy node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postRecordTy(RecordTy data) {}

    public void preRecordTyFields(RecordTy node) {}

    public void postRecordTyFields(RecordTy node) {}

    // RenamingItem
    public void preRenamingItem(RenamingItem data) {}

    public void midRenamingItem(RenamingItem node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postRenamingItem(RenamingItem data) {}

    // RepresentationDec
    public void preRepresentationDec(RepresentationDec data) {}

    public void midRepresentationDec(RepresentationDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postRepresentationDec(RepresentationDec data) {}

    // ResolveConceptualElement
    public void preResolveConceptualElement(ResolveConceptualElement data) {}

    public void midResolveConceptualElement(ResolveConceptualElement node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postResolveConceptualElement(ResolveConceptualElement data) {}

    // SelectionStmt
    public void preSelectionStmt(SelectionStmt data) {}

    public void midSelectionStmt(SelectionStmt node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postSelectionStmt(SelectionStmt data) {}

    public void preSelectionStmtWhenpairs(SelectionStmt node) {}

    public void postSelectionStmtWhenpairs(SelectionStmt node) {}

    public void preSelectionStmtDefaultclause(SelectionStmt node) {}

    public void postSelectionStmtDefaultclause(SelectionStmt node) {}

    // SetExp
    public void preSetExp(SetExp data) {}

    public void midSetExp(SetExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postSetExp(SetExp data) {}

    public void preSetExpVars(SetExp node) {}

    public void postSetExpVars(SetExp node) {}

    // ShortFacilityModuleDec
    public void preShortFacilityModuleDec(ShortFacilityModuleDec data) {}

    public void midShortFacilityModuleDec(ShortFacilityModuleDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postShortFacilityModuleDec(ShortFacilityModuleDec data) {}

    public void preShortFacilityModuleDecUsesItems(ShortFacilityModuleDec node) {}

    public void postShortFacilityModuleDecUsesItems(ShortFacilityModuleDec node) {}

    // Statement
    public void preStatement(Statement data) {}

    public void midStatement(Statement node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postStatement(Statement data) {}

    // StringExp
    public void preStringExp(StringExp data) {}

    public void midStringExp(StringExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postStringExp(StringExp data) {}

    // SubtypeDec
    public void preSubtypeDec(SubtypeDec data) {}

    public void midSubtypeDec(SubtypeDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postSubtypeDec(SubtypeDec data) {}

    // SuppositionDeductionExp
    public void preSuppositionDeductionExp(SuppositionDeductionExp data) {}

    public void midSuppositionDeductionExp(SuppositionDeductionExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postSuppositionDeductionExp(SuppositionDeductionExp data) {}

    public void preSuppositionDeductionExpBody(SuppositionDeductionExp node) {}

    public void postSuppositionDeductionExpBody(SuppositionDeductionExp node) {}

    // SuppositionExp
    public void preSuppositionExp(SuppositionExp data) {}

    public void midSuppositionExp(SuppositionExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postSuppositionExp(SuppositionExp data) {}

    public void preSuppositionExpVars(SuppositionExp node) {}

    public void postSuppositionExpVars(SuppositionExp node) {}

    // SwapStmt
    public void preSwapStmt(SwapStmt data) {}

    public void midSwapStmt(SwapStmt node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postSwapStmt(SwapStmt data) {}

    // TupleExp
    public void preTupleExp(TupleExp data) {}

    public void midTupleExp(TupleExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postTupleExp(TupleExp data) {}

    public void preTupleExpFields(TupleExp node) {}

    public void postTupleExpFields(TupleExp node) {}

    // TupleTy
    public void preTupleTy(TupleTy data) {}

    public void midTupleTy(TupleTy node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postTupleTy(TupleTy data) {}

    public void preTupleTyFields(TupleTy node) {}

    public void postTupleTyFields(TupleTy node) {}

    // Ty
    public void preTy(Ty data) {}

    public void midTy(Ty node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postTy(Ty data) {}

    // TypeDec
    public void preTypeDec(TypeDec data) {}

    public void midTypeDec(TypeDec node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postTypeDec(TypeDec data) {}

    // TypeFunctionExp
    public void preTypeFunctionExp(TypeFunctionExp data) {}

    public void midTypeFunctionExp(TypeFunctionExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postTypeFunctionExp(TypeFunctionExp data) {}

    public void preTypeFunctionExpParams(TypeFunctionExp node) {}

    public void postTypeFunctionExpParams(TypeFunctionExp node) {}

    // UnaryMinusExp
    public void preUnaryMinusExp(UnaryMinusExp data) {}

    public void midUnaryMinusExp(UnaryMinusExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postUnaryMinusExp(UnaryMinusExp data) {}

    // UsesItem
    public void preUsesItem(UsesItem data) {}

    public void midUsesItem(UsesItem node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postUsesItem(UsesItem data) {}

    // VarDec
    public void preVarDec(VarDec data) {}

    public void midVarDec(VarDec node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postVarDec(VarDec data) {}

    // VarExp
    public void preVarExp(VarExp data) {}

    public void midVarExp(VarExp node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postVarExp(VarExp data) {}

    // VariableArrayExp
    public void preVariableArrayExp(VariableArrayExp data) {}

    public void midVariableArrayExp(VariableArrayExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postVariableArrayExp(VariableArrayExp data) {}

    // VariableDotExp
    public void preVariableDotExp(VariableDotExp data) {}

    public void midVariableDotExp(VariableDotExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postVariableDotExp(VariableDotExp data) {}

    public void preVariableDotExpSegments(VariableDotExp node) {}

    public void postVariableDotExpSegments(VariableDotExp node) {}

    // VariableExp
    public void preVariableExp(VariableExp data) {}

    public void midVariableExp(VariableExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postVariableExp(VariableExp data) {}

    // VariableNameExp
    public void preVariableNameExp(VariableNameExp data) {}

    public void midVariableNameExp(VariableNameExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postVariableNameExp(VariableNameExp data) {}

    // VariableRecordExp
    public void preVariableRecordExp(VariableRecordExp data) {}

    public void midVariableRecordExp(VariableRecordExp node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postVariableRecordExp(VariableRecordExp data) {}

    public void preVariableRecordExpFields(VariableRecordExp node) {}

    public void postVariableRecordExpFields(VariableRecordExp node) {}

    // WhileStmt
    public void preWhileStmt(WhileStmt data) {}

    public void midWhileStmt(WhileStmt node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {}

    public void postWhileStmt(WhileStmt data) {}

    public void preWhileStmtChanging(WhileStmt node) {}

    public void postWhileStmtChanging(WhileStmt node) {}

    public void preWhileStmtStatements(WhileStmt node) {}

    public void postWhileStmtStatements(WhileStmt node) {}

}
