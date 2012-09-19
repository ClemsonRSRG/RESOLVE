package edu.clemson.cs.r2jt.treewalk;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.PosSymbol;

public abstract class TreeWalkerVisitor {

    public void preAny(ResolveConceptualElement data) {}

    public void postAny(ResolveConceptualElement data) {}

    // AffectsItem
    public void preAffectsItem(AffectsItem data) {}

    public void midAffectsItem(AffectsItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAffectsItem(AffectsItem data) {}

    // AlternativeExp
    public void preAlternativeExp(AlternativeExp data) {}

    public void midAlternativeExp(AlternativeExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAlternativeExp(AlternativeExp data) {}

    // AlternativeExpAlternatives
    public void preAlternativeExpAlternatives(AlternativeExp data) {}

    public void midAlternativeExpAlternatives(AlternativeExp node,
            AltItemExp previous, AltItemExp next) {}

    public void postAlternativeExpAlternatives(AlternativeExp data) {}

    // AltItemExp
    public void preAltItemExp(AltItemExp data) {}

    public void midAltItemExp(AltItemExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAltItemExp(AltItemExp data) {}

    // ArrayTy
    public void preArrayTy(ArrayTy data) {}

    public void midArrayTy(ArrayTy node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postArrayTy(ArrayTy data) {}

    // AssumeStmt
    public void preAssumeStmt(AssumeStmt data) {}

    public void midAssumeStmt(AssumeStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAssumeStmt(AssumeStmt data) {}

    // AuxCodeStmt
    public void preAuxCodeStmt(AuxCodeStmt data) {}

    public void midAuxCodeStmt(AuxCodeStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAuxCodeStmt(AuxCodeStmt data) {}

    // AuxCodeStmtStatements
    public void preAuxCodeStmtStatements(AuxCodeStmt data) {}

    public void midAuxCodeStmtStatements(AuxCodeStmt node, Statement previous,
            Statement next) {}

    public void postAuxCodeStmtStatements(AuxCodeStmt data) {}

    // AuxVarDec
    public void preAuxVarDec(AuxVarDec data) {}

    public void midAuxVarDec(AuxVarDec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postAuxVarDec(AuxVarDec data) {}

    // BetweenExp
    public void preBetweenExp(BetweenExp data) {}

    public void midBetweenExp(BetweenExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postBetweenExp(BetweenExp data) {}

    // BetweenExpLessExps
    public void preBetweenExpLessExps(BetweenExp data) {}

    public void midBetweenExpLessExps(BetweenExp node, Exp previous, Exp next) {}

    public void postBetweenExpLessExps(BetweenExp data) {}

    // BooleanTy 
    public void preBooleanTy(BooleanTy data) {}

    public void midBooleanTy(BooleanTy node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postBooleanTy(BooleanTy data) {}

    // CallStmt
    public void preCallStmt(CallStmt data) {}

    public void midCallStmt(CallStmt node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postCallStmt(CallStmt data) {}

    // CallStmtArguments
    public void preCallStmtArguments(CallStmt data) {}

    public void midCallStmtArguments(CallStmt node, ProgramExp previous,
            ProgramExp next) {}

    public void postCallStmtArguments(CallStmt data) {}

    // CartProdTy
    public void preCartProdTy(CartProdTy data) {}

    public void midCartProdTy(CartProdTy node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postCartProdTy(CartProdTy data) {}

    // CartProdTyFields
    public void preCartProdTyFields(CartProdTy data) {}

    public void midCartProdTyFields(CartProdTy node, MathVarDec previous,
            MathVarDec next) {}

    public void postCartProdTyFields(CartProdTy data) {}

    // CategoricalDefinitionDec
    public void preCategoricalDefinitionDec(CategoricalDefinitionDec data) {}

    public void midCategoricalDefinitionDec(CategoricalDefinitionDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postCategoricalDefinitionDec(CategoricalDefinitionDec data) {}

    // CharExp
    public void preCharExp(CharExp data) {}

    public void midCharExp(CharExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postCharExp(CharExp data) {}

    // ChoiceItem
    public void preChoiceItem(ChoiceItem data) {}

    public void midChoiceItem(ChoiceItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postChoiceItem(ChoiceItem data) {}

    // ChoiceItemTest
    public void preChoiceItemTest(ChoiceItem data) {}

    public void midChoiceItemTest(ChoiceItem node, ProgramExp previous,
            ProgramExp next) {}

    public void postChoiceItemTest(ChoiceItem data) {}

    // ChoiceItemThenclause
    public void preChoiceItemThenclause(ChoiceItem data) {}

    public void midChoiceItemThenclause(ChoiceItem node, Statement previous,
            Statement next) {}

    public void postChoiceItemThenclause(ChoiceItem data) {}

    // ConceptBodyModuleDec
    public void preConceptBodyModuleDec(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDec(ConceptBodyModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptBodyModuleDec(ConceptBodyModuleDec data) {}

    // ConceptBodyModuleDecParameters
    public void preConceptBodyModuleDecParameters(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDecParameters(ConceptBodyModuleDec node,
            ModuleParameter previous, ModuleParameter next) {}

    public void postConceptBodyModuleDecParameters(ConceptBodyModuleDec data) {}

    // ConceptBodyModuleDecEnhancementNames
    public void preConceptBodyModuleDecEnhancementNames(
            ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDecEnhancementNames(
            ConceptBodyModuleDec node, PosSymbol previous, PosSymbol next) {}

    public void postConceptBodyModuleDecEnhancementNames(
            ConceptBodyModuleDec data) {}

    // ConceptBodyModuleDecUsesItems
    public void preConceptBodyModuleDecUsesItems(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDecUsesItems(ConceptBodyModuleDec node,
            UsesItem previous, UsesItem next) {}

    public void postConceptBodyModuleDecUsesItems(ConceptBodyModuleDec data) {}

    // ConceptBodyModuleDecConventions
    public void preConceptBodyModuleDecConventions(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDecConventions(ConceptBodyModuleDec node,
            Exp previous, Exp next) {}

    public void postConceptBodyModuleDecConventions(ConceptBodyModuleDec data) {}

    // ConceptBodyModuleDecCorrs
    public void preConceptBodyModuleDecCorrs(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDecCorrs(ConceptBodyModuleDec node,
            Exp previous, Exp next) {}

    public void postConceptBodyModuleDecCorrs(ConceptBodyModuleDec data) {}

    // ConceptBodyModuleDecDecs
    public void preConceptBodyModuleDecDecs(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDecDecs(ConceptBodyModuleDec node,
            Dec previous, Dec next) {}

    public void postConceptBodyModuleDecDecs(ConceptBodyModuleDec data) {}

    // ConceptModuleDec
    public void preConceptModuleDec(ConceptModuleDec data) {}

    public void midConceptModuleDec(ConceptModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptModuleDec(ConceptModuleDec data) {}

    // ConceptModuleDecParameters
    public void preConceptModuleDecParameters(ConceptModuleDec data) {}

    public void midConceptModuleDecParameters(ConceptModuleDec node,
            ModuleParameter previous, ModuleParameter next) {}

    public void postConceptModuleDecParameters(ConceptModuleDec data) {}

    // ConceptModuleDecUsesItems
    public void preConceptModuleDecUsesItems(ConceptModuleDec data) {}

    public void midConceptModuleDecUsesItems(ConceptModuleDec node,
            UsesItem previous, UsesItem next) {}

    public void postConceptModuleDecUsesItems(ConceptModuleDec data) {}

    // ConceptModuleDecConstraints
    public void preConceptModuleDecConstraints(ConceptModuleDec data) {}

    public void midConceptModuleDecConstraints(ConceptModuleDec node,
            Exp previous, Exp next) {}

    public void postConceptModuleDecConstraints(ConceptModuleDec data) {}

    // ConceptModuleDecDecs
    public void preConceptModuleDecDecs(ConceptModuleDec data) {}

    public void midConceptModuleDecDecs(ConceptModuleDec node, Dec previous,
            Dec next) {}

    public void postConceptModuleDecDecs(ConceptModuleDec data) {}

    // ConceptTypeParamDec
    public void preConceptTypeParamDec(ConceptTypeParamDec data) {}

    public void midConceptTypeParamDec(ConceptTypeParamDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptTypeParamDec(ConceptTypeParamDec data) {}

    // ConditionItem
    public void preConditionItem(ConditionItem data) {}

    public void midConditionItem(ConditionItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConditionItem(ConditionItem data) {}

    // ConditionItemThenclause
    public void preConditionItemThenclause(ConditionItem data) {}

    public void midConditionItemThenclause(ConditionItem node,
            Statement previous, Statement next) {}

    public void postConditionItemThenclause(ConditionItem data) {}

    // ConfirmStmt
    public void preConfirmStmt(ConfirmStmt data) {}

    public void midConfirmStmt(ConfirmStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConfirmStmt(ConfirmStmt data) {}

    // ConstantParamDec
    public void preConstantParamDec(ConstantParamDec data) {}

    public void midConstantParamDec(ConstantParamDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConstantParamDec(ConstantParamDec data) {}

    // ConstructedTy
    public void preConstructedTy(ConstructedTy data) {}

    public void midConstructedTy(ConstructedTy node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConstructedTy(ConstructedTy data) {}

    // ConstructedTyArgs
    public void preConstructedTyArgs(ConstructedTy data) {}

    public void midConstructedTyArgs(ConstructedTy node, Ty previous, Ty next) {}

    public void postConstructedTyArgs(ConstructedTy data) {}

    // Dec
    public void preDec(Dec data) {}

    public void midDec(Dec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDec(Dec data) {}

    // DeductionExp
    public void preDeductionExp(DeductionExp data) {}

    public void midDeductionExp(DeductionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postDeductionExp(DeductionExp data) {}

    // DefinitionDec
    public void preDefinitionDec(DefinitionDec data) {}

    public void midDefinitionDec(DefinitionDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postDefinitionDec(DefinitionDec data) {}

    // DefinitionDecParameters
    public void preDefinitionDecParameters(DefinitionDec data) {}

    public void midDefinitionDecParameters(DefinitionDec node,
            MathVarDec previous, MathVarDec next) {}

    public void postDefinitionDecParameters(DefinitionDec data) {}

    // DotExp
    public void preDotExp(DotExp data) {}

    public void midDotExp(DotExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDotExp(DotExp data) {}

    // DotExpSegments
    public void preDotExpSegments(DotExp data) {}

    public void midDotExpSegments(DotExp node, Exp previous, Exp next) {}

    public void postDotExpSegments(DotExp data) {}

    // DoubleExp
    public void preDoubleExp(DoubleExp data) {}

    public void midDoubleExp(DoubleExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDoubleExp(DoubleExp data) {}

    // EnhancementBodyItem
    public void preEnhancementBodyItem(EnhancementBodyItem data) {}

    public void midEnhancementBodyItem(EnhancementBodyItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementBodyItem(EnhancementBodyItem data) {}

    // EnhancementBodyItemParams
    public void preEnhancementBodyItemParams(EnhancementBodyItem data) {}

    public void midEnhancementBodyItemParams(EnhancementBodyItem node,
            ModuleArgumentItem previous, ModuleArgumentItem next) {}

    public void postEnhancementBodyItemParams(EnhancementBodyItem data) {}

    // EnhancementBodyItemBodyParams
    public void preEnhancementBodyItemBodyParams(EnhancementBodyItem data) {}

    public void midEnhancementBodyItemBodyParams(EnhancementBodyItem node,
            ModuleArgumentItem previous, ModuleArgumentItem next) {}

    public void postEnhancementBodyItemBodyParams(EnhancementBodyItem data) {}

    // EnhancementBodyModuleDec
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDec(EnhancementBodyModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec data) {}

    // EnhancementBodyModuleDecParameters
    public void preEnhancementBodyModuleDecParameters(
            EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDecParameters(
            EnhancementBodyModuleDec node, ModuleParameter previous,
            ModuleParameter next) {}

    public void postEnhancementBodyModuleDecParameters(
            EnhancementBodyModuleDec data) {}

    // EnhancementBodyModuleDecEnhancementBodies
    public void preEnhancementBodyModuleDecEnhancementBodies(
            EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDecEnhancementBodies(
            EnhancementBodyModuleDec node, EnhancementBodyItem previous,
            EnhancementBodyItem next) {}

    public void postEnhancementBodyModuleDecEnhancementBodies(
            EnhancementBodyModuleDec data) {}

    // EnhancementBodyModuleDecUsesItems
    public void preEnhancementBodyModuleDecUsesItems(
            EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDecUsesItems(
            EnhancementBodyModuleDec node, UsesItem previous, UsesItem next) {}

    public void postEnhancementBodyModuleDecUsesItems(
            EnhancementBodyModuleDec data) {}

    // EnhancementBodyModuleDecConventions
    public void preEnhancementBodyModuleDecConventions(
            EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDecConventions(
            EnhancementBodyModuleDec node, Exp previous, Exp next) {}

    public void postEnhancementBodyModuleDecConventions(
            EnhancementBodyModuleDec data) {}

    // EnhancementBodyModuleDecCorrs
    public void preEnhancementBodyModuleDecCorrs(EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDecCorrs(EnhancementBodyModuleDec node,
            Exp previous, Exp next) {}

    public void postEnhancementBodyModuleDecCorrs(EnhancementBodyModuleDec data) {}

    // EnhancementBodyModuleDecDecs
    public void preEnhancementBodyModuleDecDecs(EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDecDecs(EnhancementBodyModuleDec node,
            Dec previous, Dec next) {}

    public void postEnhancementBodyModuleDecDecs(EnhancementBodyModuleDec data) {}

    // EnhancementItem
    public void preEnhancementItem(EnhancementItem data) {}

    public void midEnhancementItem(EnhancementItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementItem(EnhancementItem data) {}

    // EnhancementItemParams
    public void preEnhancementItemParams(EnhancementItem data) {}

    public void midEnhancementItemParams(EnhancementItem node,
            ModuleArgumentItem previous, ModuleArgumentItem next) {}

    public void postEnhancementItemParams(EnhancementItem data) {}

    // EnhancementModuleDec
    public void preEnhancementModuleDec(EnhancementModuleDec data) {}

    public void midEnhancementModuleDec(EnhancementModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementModuleDec(EnhancementModuleDec data) {}

    // EnhancementModuleDecParameters
    public void preEnhancementModuleDecParameters(EnhancementModuleDec data) {}

    public void midEnhancementModuleDecParameters(EnhancementModuleDec node,
            ModuleParameter previous, ModuleParameter next) {}

    public void postEnhancementModuleDecParameters(EnhancementModuleDec data) {}

    // EnhancementModuleDecUsesItems
    public void preEnhancementModuleDecUsesItems(EnhancementModuleDec data) {}

    public void midEnhancementModuleDecUsesItems(EnhancementModuleDec node,
            UsesItem previous, UsesItem next) {}

    public void postEnhancementModuleDecUsesItems(EnhancementModuleDec data) {}

    // EnhancementModuleDecDecs
    public void preEnhancementModuleDecDecs(EnhancementModuleDec data) {}

    public void midEnhancementModuleDecDecs(EnhancementModuleDec node,
            Dec previous, Dec next) {}

    public void postEnhancementModuleDecDecs(EnhancementModuleDec data) {}

    // EqualsExp
    public void preEqualsExp(EqualsExp data) {}

    public void midEqualsExp(EqualsExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postEqualsExp(EqualsExp data) {}

    // Exp
    public void preExp(Exp data) {}

    public void midExp(Exp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postExp(Exp data) {}

    // FacilityDec
    public void preFacilityDec(FacilityDec data) {}

    public void midFacilityDec(FacilityDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityDec(FacilityDec data) {}

    // FacilityDecConceptParams
    public void preFacilityDecConceptParams(FacilityDec data) {}

    public void midFacilityDecConceptParams(FacilityDec node,
            ModuleArgumentItem previous, ModuleArgumentItem next) {}

    public void postFacilityDecConceptParams(FacilityDec data) {}

    // FacilityDecEnhancements
    public void preFacilityDecEnhancements(FacilityDec data) {}

    public void midFacilityDecEnhancements(FacilityDec node,
            EnhancementItem previous, EnhancementItem next) {}

    public void postFacilityDecEnhancements(FacilityDec data) {}

    // FacilityDecBodyParams
    public void preFacilityDecBodyParams(FacilityDec data) {}

    public void midFacilityDecBodyParams(FacilityDec node,
            ModuleArgumentItem previous, ModuleArgumentItem next) {}

    public void postFacilityDecBodyParams(FacilityDec data) {}

    // FacilityDecEnhancementBodies
    public void preFacilityDecEnhancementBodies(FacilityDec data) {}

    public void midFacilityDecEnhancementBodies(FacilityDec node,
            EnhancementBodyItem previous, EnhancementBodyItem next) {}

    public void postFacilityDecEnhancementBodies(FacilityDec data) {}

    // FacilityModuleDec
    public void preFacilityModuleDec(FacilityModuleDec data) {}

    public void midFacilityModuleDec(FacilityModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityModuleDec(FacilityModuleDec data) {}

    // FacilityModuleDecUsesItems
    public void preFacilityModuleDecUsesItems(FacilityModuleDec data) {}

    public void midFacilityModuleDecUsesItems(FacilityModuleDec node,
            UsesItem previous, UsesItem next) {}

    public void postFacilityModuleDecUsesItems(FacilityModuleDec data) {}

    // FacilityModuleDecDecs
    public void preFacilityModuleDecDecs(FacilityModuleDec data) {}

    public void midFacilityModuleDecDecs(FacilityModuleDec node, Dec previous,
            Dec next) {}

    public void postFacilityModuleDecDecs(FacilityModuleDec data) {}

    // FacilityOperationDec
    public void preFacilityOperationDec(FacilityOperationDec data) {}

    public void midFacilityOperationDec(FacilityOperationDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityOperationDec(FacilityOperationDec data) {}

    // FacilityOperationDecParameters
    public void preFacilityOperationDecParameters(FacilityOperationDec data) {}

    public void midFacilityOperationDecParameters(FacilityOperationDec node,
            ParameterVarDec previous, ParameterVarDec next) {}

    public void postFacilityOperationDecParameters(FacilityOperationDec data) {}

    // FacilityOperationDecStateVars
    public void preFacilityOperationDecStateVars(FacilityOperationDec data) {}

    public void midFacilityOperationDecStateVars(FacilityOperationDec node,
            AffectsItem previous, AffectsItem next) {}

    public void postFacilityOperationDecStateVars(FacilityOperationDec data) {}

    // FacilityOperationDecFacilities
    public void preFacilityOperationDecFacilities(FacilityOperationDec data) {}

    public void midFacilityOperationDecFacilities(FacilityOperationDec node,
            FacilityDec previous, FacilityDec next) {}

    public void postFacilityOperationDecFacilities(FacilityOperationDec data) {}

    // FacilityOperationDecVariables
    public void preFacilityOperationDecVariables(FacilityOperationDec data) {}

    public void midFacilityOperationDecVariables(FacilityOperationDec node,
            VarDec previous, VarDec next) {}

    public void postFacilityOperationDecVariables(FacilityOperationDec data) {}

    // FacilityOperationDecAuxVariables
    public void preFacilityOperationDecAuxVariables(FacilityOperationDec data) {}

    public void midFacilityOperationDecAuxVariables(FacilityOperationDec node,
            AuxVarDec previous, AuxVarDec next) {}

    public void postFacilityOperationDecAuxVariables(FacilityOperationDec data) {}

    // FacilityOperationDecStatements
    public void preFacilityOperationDecStatements(FacilityOperationDec data) {}

    public void midFacilityOperationDecStatements(FacilityOperationDec node,
            Statement previous, Statement next) {}

    public void postFacilityOperationDecStatements(FacilityOperationDec data) {}

    // FacilityTypeDec
    public void preFacilityTypeDec(FacilityTypeDec data) {}

    public void midFacilityTypeDec(FacilityTypeDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityTypeDec(FacilityTypeDec data) {}

    // FieldExp
    public void preFieldExp(FieldExp data) {}

    public void midFieldExp(FieldExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postFieldExp(FieldExp data) {}

    // FinalItem
    public void preFinalItem(FinalItem data) {}

    public void midFinalItem(FinalItem node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postFinalItem(FinalItem data) {}

    // FinalItemStateVars
    public void preFinalItemStateVars(FinalItem data) {}

    public void midFinalItemStateVars(FinalItem node, AffectsItem previous,
            AffectsItem next) {}

    public void postFinalItemStateVars(FinalItem data) {}

    // FinalItemFacilities
    public void preFinalItemFacilities(FinalItem data) {}

    public void midFinalItemFacilities(FinalItem node, FacilityDec previous,
            FacilityDec next) {}

    public void postFinalItemFacilities(FinalItem data) {}

    // FinalItemVariables
    public void preFinalItemVariables(FinalItem data) {}

    public void midFinalItemVariables(FinalItem node, VarDec previous,
            VarDec next) {}

    public void postFinalItemVariables(FinalItem data) {}

    // FinalItemAuxVariables
    public void preFinalItemAuxVariables(FinalItem data) {}

    public void midFinalItemAuxVariables(FinalItem node, AuxVarDec previous,
            AuxVarDec next) {}

    public void postFinalItemAuxVariables(FinalItem data) {}

    // FinalItemStatements
    public void preFinalItemStatements(FinalItem data) {}

    public void midFinalItemStatements(FinalItem node, Statement previous,
            Statement next) {}

    public void postFinalItemStatements(FinalItem data) {}

    // FuncAssignStmt
    public void preFuncAssignStmt(FuncAssignStmt data) {}

    public void midFuncAssignStmt(FuncAssignStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFuncAssignStmt(FuncAssignStmt data) {}

    // FunctionArgList
    public void preFunctionArgList(FunctionArgList data) {}

    public void midFunctionArgList(FunctionArgList node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFunctionArgList(FunctionArgList data) {}

    // FunctionArgListArguments
    public void preFunctionArgListArguments(FunctionArgList data) {}

    public void midFunctionArgListArguments(FunctionArgList node, Exp previous,
            Exp next) {}

    public void postFunctionArgListArguments(FunctionArgList data) {}

    // FunctionExp
    public void preFunctionExp(FunctionExp data) {}

    public void midFunctionExp(FunctionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFunctionExp(FunctionExp data) {}

    // FunctionExpParamList
    public void preFunctionExpParamList(FunctionExp data) {}

    public void midFunctionExpParamList(FunctionExp node,
            FunctionArgList previous, FunctionArgList next) {}

    public void postFunctionExpParamList(FunctionExp data) {}

    // FunctionTy
    public void preFunctionTy(FunctionTy data) {}

    public void midFunctionTy(FunctionTy node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFunctionTy(FunctionTy data) {}

    // GoalExp
    public void preGoalExp(GoalExp data) {}

    public void midGoalExp(GoalExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postGoalExp(GoalExp data) {}

    // HypDesigExp
    public void preHypDesigExp(HypDesigExp data) {}

    public void midHypDesigExp(HypDesigExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postHypDesigExp(HypDesigExp data) {}

    // IfExp
    public void preIfExp(IfExp data) {}

    public void midIfExp(IfExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIfExp(IfExp data) {}

    // IfStmt
    public void preIfStmt(IfStmt data) {}

    public void midIfStmt(IfStmt node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIfStmt(IfStmt data) {}

    // IfStmtThenclause
    public void preIfStmtThenclause(IfStmt data) {}

    public void midIfStmtThenclause(IfStmt node, Statement previous,
            Statement next) {}

    public void postIfStmtThenclause(IfStmt data) {}

    // IfStmtElseifpairs
    public void preIfStmtElseifpairs(IfStmt data) {}

    public void midIfStmtElseifpairs(IfStmt node, ConditionItem previous,
            ConditionItem next) {}

    public void postIfStmtElseifpairs(IfStmt data) {}

    // IfStmtElseclause
    public void preIfStmtElseclause(IfStmt data) {}

    public void midIfStmtElseclause(IfStmt node, Statement previous,
            Statement next) {}

    public void postIfStmtElseclause(IfStmt data) {}

    // InfixExp
    public void preInfixExp(InfixExp data) {}

    public void midInfixExp(InfixExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postInfixExp(InfixExp data) {}

    // InitItem
    public void preInitItem(InitItem data) {}

    public void midInitItem(InitItem node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postInitItem(InitItem data) {}

    // InitItemStateVars
    public void preInitItemStateVars(InitItem data) {}

    public void midInitItemStateVars(InitItem node, AffectsItem previous,
            AffectsItem next) {}

    public void postInitItemStateVars(InitItem data) {}

    // InitItemFacilities
    public void preInitItemFacilities(InitItem data) {}

    public void midInitItemFacilities(InitItem node, FacilityDec previous,
            FacilityDec next) {}

    public void postInitItemFacilities(InitItem data) {}

    // InitItemVariables
    public void preInitItemVariables(InitItem data) {}

    public void midInitItemVariables(InitItem node, VarDec previous, VarDec next) {}

    public void postInitItemVariables(InitItem data) {}

    // InitItemAuxVariables
    public void preInitItemAuxVariables(InitItem data) {}

    public void midInitItemAuxVariables(InitItem node, AuxVarDec previous,
            AuxVarDec next) {}

    public void postInitItemAuxVariables(InitItem data) {}

    // InitItemStatements
    public void preInitItemStatements(InitItem data) {}

    public void midInitItemStatements(InitItem node, Statement previous,
            Statement next) {}

    public void postInitItemStatements(InitItem data) {}

    // IntegerExp
    public void preIntegerExp(IntegerExp data) {}

    public void midIntegerExp(IntegerExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIntegerExp(IntegerExp data) {}

    // IsInExp
    public void preIsInExp(IsInExp data) {}

    public void midIsInExp(IsInExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIsInExp(IsInExp data) {}

    // IterateExitStmt
    public void preIterateExitStmt(IterateExitStmt data) {}

    public void midIterateExitStmt(IterateExitStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIterateExitStmt(IterateExitStmt data) {}

    // IterateExitStmtStatements
    public void preIterateExitStmtStatements(IterateExitStmt data) {}

    public void midIterateExitStmtStatements(IterateExitStmt node,
            Statement previous, Statement next) {}

    public void postIterateExitStmtStatements(IterateExitStmt data) {}

    // IterateStmt
    public void preIterateStmt(IterateStmt data) {}

    public void midIterateStmt(IterateStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIterateStmt(IterateStmt data) {}

    // IterateStmtChanging
    public void preIterateStmtChanging(IterateStmt data) {}

    public void midIterateStmtChanging(IterateStmt node, VariableExp previous,
            VariableExp next) {}

    public void postIterateStmtChanging(IterateStmt data) {}

    // IterateStmtStatements
    public void preIterateStmtStatements(IterateStmt data) {}

    public void midIterateStmtStatements(IterateStmt node, Statement previous,
            Statement next) {}

    public void postIterateStmtStatements(IterateStmt data) {}

    // IterativeExp
    public void preIterativeExp(IterativeExp data) {}

    public void midIterativeExp(IterativeExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIterativeExp(IterativeExp data) {}

    // JustificationExp
    public void preJustificationExp(JustificationExp data) {}

    public void midJustificationExp(JustificationExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postJustificationExp(JustificationExp data) {}

    // JustifiedExp
    public void preJustifiedExp(JustifiedExp data) {}

    public void midJustifiedExp(JustifiedExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postJustifiedExp(JustifiedExp data) {}

    // LambdaExp
    public void preLambdaExp(LambdaExp data) {}

    public void midLambdaExp(LambdaExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postLambdaExp(LambdaExp data) {}

    // LineNumberedExp
    public void preLineNumberedExp(LineNumberedExp data) {}

    public void midLineNumberedExp(LineNumberedExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postLineNumberedExp(LineNumberedExp data) {}

    // MathAssertionDec
    public void preMathAssertionDec(MathAssertionDec data) {}

    public void midMathAssertionDec(MathAssertionDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathAssertionDec(MathAssertionDec data) {}

    // MathModuleDec
    public void preMathModuleDec(MathModuleDec data) {}

    public void midMathModuleDec(MathModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathModuleDec(MathModuleDec data) {}

    // MathModuleDecParameters
    public void preMathModuleDecParameters(MathModuleDec data) {}

    public void midMathModuleDecParameters(MathModuleDec node,
            ModuleParameter previous, ModuleParameter next) {}

    public void postMathModuleDecParameters(MathModuleDec data) {}

    // MathModuleDecUsesItems
    public void preMathModuleDecUsesItems(MathModuleDec data) {}

    public void midMathModuleDecUsesItems(MathModuleDec node,
            UsesItem previous, UsesItem next) {}

    public void postMathModuleDecUsesItems(MathModuleDec data) {}

    // MathModuleDecDecs
    public void preMathModuleDecDecs(MathModuleDec data) {}

    public void midMathModuleDecDecs(MathModuleDec node, Dec previous, Dec next) {}

    public void postMathModuleDecDecs(MathModuleDec data) {}

    // MathRefExp
    public void preMathRefExp(MathRefExp data) {}

    public void midMathRefExp(MathRefExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathRefExp(MathRefExp data) {}

    // MathRefExpParams
    public void preMathRefExpParams(MathRefExp data) {}

    public void midMathRefExpParams(MathRefExp node, VarExp previous,
            VarExp next) {}

    public void postMathRefExpParams(MathRefExp data) {}

    // MathTypeDec
    public void preMathTypeDec(MathTypeDec data) {}

    public void midMathTypeDec(MathTypeDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathTypeDec(MathTypeDec data) {}

    // MathTypeFormalDec
    public void preMathTypeFormalDec(MathTypeFormalDec data) {}

    public void midMathTypeFormalDec(MathTypeFormalDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathTypeFormalDec(MathTypeFormalDec data) {}

    // MathVarDec
    public void preMathVarDec(MathVarDec data) {}

    public void midMathVarDec(MathVarDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathVarDec(MathVarDec data) {}

    // MemoryStmt
    public void preMemoryStmt(MemoryStmt data) {}

    public void midMemoryStmt(MemoryStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMemoryStmt(MemoryStmt data) {}

    // ModuleArgumentItem
    public void preModuleArgumentItem(ModuleArgumentItem data) {}

    public void midModuleArgumentItem(ModuleArgumentItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postModuleArgumentItem(ModuleArgumentItem data) {}

    // ModuleDec
    public void preModuleDec(ModuleDec data) {}

    public void midModuleDec(ModuleDec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postModuleDec(ModuleDec data) {}

    // NameTy
    public void preNameTy(NameTy data) {}

    public void midNameTy(NameTy node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postNameTy(NameTy data) {}

    // OldExp
    public void preOldExp(OldExp data) {}

    public void midOldExp(OldExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postOldExp(OldExp data) {}

    // OperationDec
    public void preOperationDec(OperationDec data) {}

    public void midOperationDec(OperationDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postOperationDec(OperationDec data) {}

    // OperationDecParameters
    public void preOperationDecParameters(OperationDec data) {}

    public void midOperationDecParameters(OperationDec node,
            ParameterVarDec previous, ParameterVarDec next) {}

    public void postOperationDecParameters(OperationDec data) {}

    // OperationDecStateVars
    public void preOperationDecStateVars(OperationDec data) {}

    public void midOperationDecStateVars(OperationDec node,
            AffectsItem previous, AffectsItem next) {}

    public void postOperationDecStateVars(OperationDec data) {}

    // OutfixExp
    public void preOutfixExp(OutfixExp data) {}

    public void midOutfixExp(OutfixExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postOutfixExp(OutfixExp data) {}

    // ParameterVarDec
    public void preParameterVarDec(ParameterVarDec data) {}

    public void midParameterVarDec(ParameterVarDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postParameterVarDec(ParameterVarDec data) {}

    // PerformanceFinalItem
    public void prePerformanceFinalItem(PerformanceFinalItem data) {}

    public void midPerformanceFinalItem(PerformanceFinalItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceFinalItem(PerformanceFinalItem data) {}

    // PerformanceFinalItemStateVars
    public void prePerformanceFinalItemStateVars(PerformanceFinalItem data) {}

    public void midPerformanceFinalItemStateVars(PerformanceFinalItem node,
            AffectsItem previous, AffectsItem next) {}

    public void postPerformanceFinalItemStateVars(PerformanceFinalItem data) {}

    // PerformanceFinalItemFacilities
    public void prePerformanceFinalItemFacilities(PerformanceFinalItem data) {}

    public void midPerformanceFinalItemFacilities(PerformanceFinalItem node,
            FacilityDec previous, FacilityDec next) {}

    public void postPerformanceFinalItemFacilities(PerformanceFinalItem data) {}

    // PerformanceFinalItemVariables
    public void prePerformanceFinalItemVariables(PerformanceFinalItem data) {}

    public void midPerformanceFinalItemVariables(PerformanceFinalItem node,
            VarDec previous, VarDec next) {}

    public void postPerformanceFinalItemVariables(PerformanceFinalItem data) {}

    // PerformanceFinalItemAuxVariables
    public void prePerformanceFinalItemAuxVariables(PerformanceFinalItem data) {}

    public void midPerformanceFinalItemAuxVariables(PerformanceFinalItem node,
            AuxVarDec previous, AuxVarDec next) {}

    public void postPerformanceFinalItemAuxVariables(PerformanceFinalItem data) {}

    // PerformanceFinalItemStatements
    public void prePerformanceFinalItemStatements(PerformanceFinalItem data) {}

    public void midPerformanceFinalItemStatements(PerformanceFinalItem node,
            Statement previous, Statement next) {}

    public void postPerformanceFinalItemStatements(PerformanceFinalItem data) {}

    // PerformanceInitItem
    public void prePerformanceInitItem(PerformanceInitItem data) {}

    public void midPerformanceInitItem(PerformanceInitItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceInitItem(PerformanceInitItem data) {}

    // PerformanceInitItemStateVars
    public void prePerformanceInitItemStateVars(PerformanceInitItem data) {}

    public void midPerformanceInitItemStateVars(PerformanceInitItem node,
            AffectsItem previous, AffectsItem next) {}

    public void postPerformanceInitItemStateVars(PerformanceInitItem data) {}

    // PerformanceInitItemFacilities
    public void prePerformanceInitItemFacilities(PerformanceInitItem data) {}

    public void midPerformanceInitItemFacilities(PerformanceInitItem node,
            FacilityDec previous, FacilityDec next) {}

    public void postPerformanceInitItemFacilities(PerformanceInitItem data) {}

    // PerformanceInitItemVariables
    public void prePerformanceInitItemVariables(PerformanceInitItem data) {}

    public void midPerformanceInitItemVariables(PerformanceInitItem node,
            VarDec previous, VarDec next) {}

    public void postPerformanceInitItemVariables(PerformanceInitItem data) {}

    // PerformanceInitItemAuxVariables
    public void prePerformanceInitItemAuxVariables(PerformanceInitItem data) {}

    public void midPerformanceInitItemAuxVariables(PerformanceInitItem node,
            AuxVarDec previous, AuxVarDec next) {}

    public void postPerformanceInitItemAuxVariables(PerformanceInitItem data) {}

    // PerformanceInitItemStatements
    public void prePerformanceInitItemStatements(PerformanceInitItem data) {}

    public void midPerformanceInitItemStatements(PerformanceInitItem node,
            Statement previous, Statement next) {}

    public void postPerformanceInitItemStatements(PerformanceInitItem data) {}

    // PerformanceModuleDec
    public void prePerformanceModuleDec(PerformanceModuleDec data) {}

    public void midPerformanceModuleDec(PerformanceModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceModuleDec(PerformanceModuleDec data) {}

    // PerformanceModuleDecParameters
    public void prePerformanceModuleDecParameters(PerformanceModuleDec data) {}

    public void midPerformanceModuleDecParameters(PerformanceModuleDec node,
            ModuleParameter previous, ModuleParameter next) {}

    public void postPerformanceModuleDecParameters(PerformanceModuleDec data) {}

    // PerformanceModuleDecUsesItems
    public void prePerformanceModuleDecUsesItems(PerformanceModuleDec data) {}

    public void midPerformanceModuleDecUsesItems(PerformanceModuleDec node,
            UsesItem previous, UsesItem next) {}

    public void postPerformanceModuleDecUsesItems(PerformanceModuleDec data) {}

    // PerformanceModuleDecConstraints
    public void prePerformanceModuleDecConstraints(PerformanceModuleDec data) {}

    public void midPerformanceModuleDecConstraints(PerformanceModuleDec node,
            Exp previous, Exp next) {}

    public void postPerformanceModuleDecConstraints(PerformanceModuleDec data) {}

    // PerformanceModuleDecDecs
    public void prePerformanceModuleDecDecs(PerformanceModuleDec data) {}

    public void midPerformanceModuleDecDecs(PerformanceModuleDec node,
            Dec previous, Dec next) {}

    public void postPerformanceModuleDecDecs(PerformanceModuleDec data) {}

    // PerformanceOperationDec
    public void prePerformanceOperationDec(PerformanceOperationDec data) {}

    public void midPerformanceOperationDec(PerformanceOperationDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceOperationDec(PerformanceOperationDec data) {}

    // PerformanceOperationDecParameters
    public void prePerformanceOperationDecParameters(
            PerformanceOperationDec data) {}

    public void midPerformanceOperationDecParameters(
            PerformanceOperationDec node, ParameterVarDec previous,
            ParameterVarDec next) {}

    public void postPerformanceOperationDecParameters(
            PerformanceOperationDec data) {}

    // PerformanceOperationDecStateVars
    public void prePerformanceOperationDecStateVars(PerformanceOperationDec data) {}

    public void midPerformanceOperationDecStateVars(
            PerformanceOperationDec node, AffectsItem previous, AffectsItem next) {}

    public void postPerformanceOperationDecStateVars(
            PerformanceOperationDec data) {}

    // PerformanceTypeDec
    public void prePerformanceTypeDec(PerformanceTypeDec data) {}

    public void midPerformanceTypeDec(PerformanceTypeDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceTypeDec(PerformanceTypeDec data) {}

    // PrefixExp
    public void prePrefixExp(PrefixExp data) {}

    public void midPrefixExp(PrefixExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postPrefixExp(PrefixExp data) {}

    // ProcedureDec
    public void preProcedureDec(ProcedureDec data) {}

    public void midProcedureDec(ProcedureDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProcedureDec(ProcedureDec data) {}

    // ProcedureDecParameters
    public void preProcedureDecParameters(ProcedureDec data) {}

    public void midProcedureDecParameters(ProcedureDec node,
            ParameterVarDec previous, ParameterVarDec next) {}

    public void postProcedureDecParameters(ProcedureDec data) {}

    // ProcedureDecStateVars
    public void preProcedureDecStateVars(ProcedureDec data) {}

    public void midProcedureDecStateVars(ProcedureDec node,
            AffectsItem previous, AffectsItem next) {}

    public void postProcedureDecStateVars(ProcedureDec data) {}

    // ProcedureDecFacilities
    public void preProcedureDecFacilities(ProcedureDec data) {}

    public void midProcedureDecFacilities(ProcedureDec node,
            FacilityDec previous, FacilityDec next) {}

    public void postProcedureDecFacilities(ProcedureDec data) {}

    // ProcedureDecVariables
    public void preProcedureDecVariables(ProcedureDec data) {}

    public void midProcedureDecVariables(ProcedureDec node, VarDec previous,
            VarDec next) {}

    public void postProcedureDecVariables(ProcedureDec data) {}

    // ProcedureDecAuxVariables
    public void preProcedureDecAuxVariables(ProcedureDec data) {}

    public void midProcedureDecAuxVariables(ProcedureDec node,
            AuxVarDec previous, AuxVarDec next) {}

    public void postProcedureDecAuxVariables(ProcedureDec data) {}

    // ProcedureDecStatements
    public void preProcedureDecStatements(ProcedureDec data) {}

    public void midProcedureDecStatements(ProcedureDec node,
            Statement previous, Statement next) {}

    public void postProcedureDecStatements(ProcedureDec data) {}

    // ProgramCharExp
    public void preProgramCharExp(ProgramCharExp data) {}

    public void midProgramCharExp(ProgramCharExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramCharExp(ProgramCharExp data) {}

    // ProgramDotExp
    public void preProgramDotExp(ProgramDotExp data) {}

    public void midProgramDotExp(ProgramDotExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramDotExp(ProgramDotExp data) {}

    // ProgramDotExpSegments
    public void preProgramDotExpSegments(ProgramDotExp data) {}

    public void midProgramDotExpSegments(ProgramDotExp node,
            ProgramExp previous, ProgramExp next) {}

    public void postProgramDotExpSegments(ProgramDotExp data) {}

    // ProgramDoubleExp
    public void preProgramDoubleExp(ProgramDoubleExp data) {}

    public void midProgramDoubleExp(ProgramDoubleExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramDoubleExp(ProgramDoubleExp data) {}

    // ProgramExp
    public void preProgramExp(ProgramExp data) {}

    public void midProgramExp(ProgramExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramExp(ProgramExp data) {}

    // ProgramFunctionExp
    public void preProgramFunctionExp(ProgramFunctionExp data) {}

    public void midProgramFunctionExp(ProgramFunctionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramFunctionExp(ProgramFunctionExp data) {}

    // ProgramFunctionExpArguments
    public void preProgramFunctionExpArguments(ProgramFunctionExp data) {}

    public void midProgramFunctionExpArguments(ProgramFunctionExp node,
            ProgramExp previous, ProgramExp next) {}

    public void postProgramFunctionExpArguments(ProgramFunctionExp data) {}

    // ProgramIntegerExp
    public void preProgramIntegerExp(ProgramIntegerExp data) {}

    public void midProgramIntegerExp(ProgramIntegerExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramIntegerExp(ProgramIntegerExp data) {}

    // ProgramOpExp
    public void preProgramOpExp(ProgramOpExp data) {}

    public void midProgramOpExp(ProgramOpExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramOpExp(ProgramOpExp data) {}

    // ProgramParamExp
    public void preProgramParamExp(ProgramParamExp data) {}

    public void midProgramParamExp(ProgramParamExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramParamExp(ProgramParamExp data) {}

    // ProgramParamExpArguments
    public void preProgramParamExpArguments(ProgramParamExp data) {}

    public void midProgramParamExpArguments(ProgramParamExp node,
            ProgramExp previous, ProgramExp next) {}

    public void postProgramParamExpArguments(ProgramParamExp data) {}

    // ProgramStringExp
    public void preProgramStringExp(ProgramStringExp data) {}

    public void midProgramStringExp(ProgramStringExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramStringExp(ProgramStringExp data) {}

    // ProofDec
    public void preProofDec(ProofDec data) {}

    public void midProofDec(ProofDec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postProofDec(ProofDec data) {}

    // ProofDecStatements
    public void preProofDecStatements(ProofDec data) {}

    public void midProofDecStatements(ProofDec node, Exp previous, Exp next) {}

    public void postProofDecStatements(ProofDec data) {}

    // ProofDecBaseCase
    public void preProofDecBaseCase(ProofDec data) {}

    public void midProofDecBaseCase(ProofDec node, Exp previous, Exp next) {}

    public void postProofDecBaseCase(ProofDec data) {}

    // ProofDecInductiveCase
    public void preProofDecInductiveCase(ProofDec data) {}

    public void midProofDecInductiveCase(ProofDec node, Exp previous, Exp next) {}

    public void postProofDecInductiveCase(ProofDec data) {}

    // ProofDefinitionExp
    public void preProofDefinitionExp(ProofDefinitionExp data) {}

    public void midProofDefinitionExp(ProofDefinitionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProofDefinitionExp(ProofDefinitionExp data) {}

    // ProofModuleDec
    public void preProofModuleDec(ProofModuleDec data) {}

    public void midProofModuleDec(ProofModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProofModuleDec(ProofModuleDec data) {}

    // ProofModuleDecModuleParams
    public void preProofModuleDecModuleParams(ProofModuleDec data) {}

    public void midProofModuleDecModuleParams(ProofModuleDec node,
            ModuleParameter previous, ModuleParameter next) {}

    public void postProofModuleDecModuleParams(ProofModuleDec data) {}

    // ProofModuleDecUsesItems
    public void preProofModuleDecUsesItems(ProofModuleDec data) {}

    public void midProofModuleDecUsesItems(ProofModuleDec node,
            UsesItem previous, UsesItem next) {}

    public void postProofModuleDecUsesItems(ProofModuleDec data) {}

    // ProofModuleDecDecs
    public void preProofModuleDecDecs(ProofModuleDec data) {}

    public void midProofModuleDecDecs(ProofModuleDec node, Dec previous,
            Dec next) {}

    public void postProofModuleDecDecs(ProofModuleDec data) {}

    // QuantExp
    public void preQuantExp(QuantExp data) {}

    public void midQuantExp(QuantExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postQuantExp(QuantExp data) {}

    // QuantExpVars
    public void preQuantExpVars(QuantExp data) {}

    public void midQuantExpVars(QuantExp node, MathVarDec previous,
            MathVarDec next) {}

    public void postQuantExpVars(QuantExp data) {}

    // RealizationParamDec
    public void preRealizationParamDec(RealizationParamDec data) {}

    public void midRealizationParamDec(RealizationParamDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRealizationParamDec(RealizationParamDec data) {}

    // RecordTy
    public void preRecordTy(RecordTy data) {}

    public void midRecordTy(RecordTy node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postRecordTy(RecordTy data) {}

    // RecordTyFields
    public void preRecordTyFields(RecordTy data) {}

    public void midRecordTyFields(RecordTy node, VarDec previous, VarDec next) {}

    public void postRecordTyFields(RecordTy data) {}

    // RenamingItem
    public void preRenamingItem(RenamingItem data) {}

    public void midRenamingItem(RenamingItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRenamingItem(RenamingItem data) {}

    // RepresentationDec
    public void preRepresentationDec(RepresentationDec data) {}

    public void midRepresentationDec(RepresentationDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRepresentationDec(RepresentationDec data) {}

    // ResolveConceptualElement
    public void preResolveConceptualElement(ResolveConceptualElement data) {}

    public void midResolveConceptualElement(ResolveConceptualElement node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postResolveConceptualElement(ResolveConceptualElement data) {}

    // SelectionStmt
    public void preSelectionStmt(SelectionStmt data) {}

    public void midSelectionStmt(SelectionStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSelectionStmt(SelectionStmt data) {}

    // SelectionStmtWhenpairs
    public void preSelectionStmtWhenpairs(SelectionStmt data) {}

    public void midSelectionStmtWhenpairs(SelectionStmt node,
            ChoiceItem previous, ChoiceItem next) {}

    public void postSelectionStmtWhenpairs(SelectionStmt data) {}

    // SelectionStmtDefaultclause
    public void preSelectionStmtDefaultclause(SelectionStmt data) {}

    public void midSelectionStmtDefaultclause(SelectionStmt node,
            Statement previous, Statement next) {}

    public void postSelectionStmtDefaultclause(SelectionStmt data) {}

    // SetExp
    public void preSetExp(SetExp data) {}

    public void midSetExp(SetExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postSetExp(SetExp data) {}

    // SetExpVars
    public void preSetExpVars(SetExp data) {}

    public void midSetExpVars(SetExp node, VarExp previous, VarExp next) {}

    public void postSetExpVars(SetExp data) {}

    // ShortFacilityModuleDec
    public void preShortFacilityModuleDec(ShortFacilityModuleDec data) {}

    public void midShortFacilityModuleDec(ShortFacilityModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postShortFacilityModuleDec(ShortFacilityModuleDec data) {}

    // ShortFacilityModuleDecUsesItems
    public void preShortFacilityModuleDecUsesItems(ShortFacilityModuleDec data) {}

    public void midShortFacilityModuleDecUsesItems(ShortFacilityModuleDec node,
            UsesItem previous, UsesItem next) {}

    public void postShortFacilityModuleDecUsesItems(ShortFacilityModuleDec data) {}

    // Statement
    public void preStatement(Statement data) {}

    public void midStatement(Statement node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postStatement(Statement data) {}

    // StringExp
    public void preStringExp(StringExp data) {}

    public void midStringExp(StringExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postStringExp(StringExp data) {}

    // SubtypeDec
    public void preSubtypeDec(SubtypeDec data) {}

    public void midSubtypeDec(SubtypeDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSubtypeDec(SubtypeDec data) {}

    // SuppositionDeductionExp
    public void preSuppositionDeductionExp(SuppositionDeductionExp data) {}

    public void midSuppositionDeductionExp(SuppositionDeductionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSuppositionDeductionExp(SuppositionDeductionExp data) {}

    // SuppositionDeductionExpBody
    public void preSuppositionDeductionExpBody(SuppositionDeductionExp data) {}

    public void midSuppositionDeductionExpBody(SuppositionDeductionExp node,
            Exp previous, Exp next) {}

    public void postSuppositionDeductionExpBody(SuppositionDeductionExp data) {}

    // SuppositionExp
    public void preSuppositionExp(SuppositionExp data) {}

    public void midSuppositionExp(SuppositionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSuppositionExp(SuppositionExp data) {}

    // SuppositionExpVars
    public void preSuppositionExpVars(SuppositionExp data) {}

    public void midSuppositionExpVars(SuppositionExp node, MathVarDec previous,
            MathVarDec next) {}

    public void postSuppositionExpVars(SuppositionExp data) {}

    // SwapStmt
    public void preSwapStmt(SwapStmt data) {}

    public void midSwapStmt(SwapStmt node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postSwapStmt(SwapStmt data) {}

    // TupleExp
    public void preTupleExp(TupleExp data) {}

    public void midTupleExp(TupleExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTupleExp(TupleExp data) {}

    // TupleExpFields
    public void preTupleExpFields(TupleExp data) {}

    public void midTupleExpFields(TupleExp node, Exp previous, Exp next) {}

    public void postTupleExpFields(TupleExp data) {}

    // TupleTy
    public void preTupleTy(TupleTy data) {}

    public void midTupleTy(TupleTy node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTupleTy(TupleTy data) {}

    // TupleTyFields
    public void preTupleTyFields(TupleTy data) {}

    public void midTupleTyFields(TupleTy node, Ty previous, Ty next) {}

    public void postTupleTyFields(TupleTy data) {}

    // Ty
    public void preTy(Ty data) {}

    public void midTy(Ty node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTy(Ty data) {}

    // TypeDec
    public void preTypeDec(TypeDec data) {}

    public void midTypeDec(TypeDec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTypeDec(TypeDec data) {}

    // TypeFunctionExp
    public void preTypeFunctionExp(TypeFunctionExp data) {}

    public void midTypeFunctionExp(TypeFunctionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeFunctionExp(TypeFunctionExp data) {}

    // TypeFunctionExpParams
    public void preTypeFunctionExpParams(TypeFunctionExp data) {}

    public void midTypeFunctionExpParams(TypeFunctionExp node, Exp previous,
            Exp next) {}

    public void postTypeFunctionExpParams(TypeFunctionExp data) {}

    // UnaryMinusExp
    public void preUnaryMinusExp(UnaryMinusExp data) {}

    public void midUnaryMinusExp(UnaryMinusExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postUnaryMinusExp(UnaryMinusExp data) {}

    // UsesItem
    public void preUsesItem(UsesItem data) {}

    public void midUsesItem(UsesItem node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postUsesItem(UsesItem data) {}

    // VarDec
    public void preVarDec(VarDec data) {}

    public void midVarDec(VarDec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postVarDec(VarDec data) {}

    // VarExp
    public void preVarExp(VarExp data) {}

    public void midVarExp(VarExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postVarExp(VarExp data) {}

    // VariableArrayExp
    public void preVariableArrayExp(VariableArrayExp data) {}

    public void midVariableArrayExp(VariableArrayExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVariableArrayExp(VariableArrayExp data) {}

    // VariableDotExp
    public void preVariableDotExp(VariableDotExp data) {}

    public void midVariableDotExp(VariableDotExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVariableDotExp(VariableDotExp data) {}

    // VariableDotExpSegments
    public void preVariableDotExpSegments(VariableDotExp data) {}

    public void midVariableDotExpSegments(VariableDotExp node,
            VariableExp previous, VariableExp next) {}

    public void postVariableDotExpSegments(VariableDotExp data) {}

    // VariableExp
    public void preVariableExp(VariableExp data) {}

    public void midVariableExp(VariableExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVariableExp(VariableExp data) {}

    // VariableNameExp
    public void preVariableNameExp(VariableNameExp data) {}

    public void midVariableNameExp(VariableNameExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVariableNameExp(VariableNameExp data) {}

    // VariableRecordExp
    public void preVariableRecordExp(VariableRecordExp data) {}

    public void midVariableRecordExp(VariableRecordExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVariableRecordExp(VariableRecordExp data) {}

    // VariableRecordExpFields
    public void preVariableRecordExpFields(VariableRecordExp data) {}

    public void midVariableRecordExpFields(VariableRecordExp node,
            VariableExp previous, VariableExp next) {}

    public void postVariableRecordExpFields(VariableRecordExp data) {}

    // WhileStmt
    public void preWhileStmt(WhileStmt data) {}

    public void midWhileStmt(WhileStmt node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postWhileStmt(WhileStmt data) {}

    // WhileStmtChanging
    public void preWhileStmtChanging(WhileStmt data) {}

    public void midWhileStmtChanging(WhileStmt node, VariableExp previous,
            VariableExp next) {}

    public void postWhileStmtChanging(WhileStmt data) {}

    // WhileStmtStatements
    public void preWhileStmtStatements(WhileStmt data) {}

    public void midWhileStmtStatements(WhileStmt node, Statement previous,
            Statement next) {}

    public void postWhileStmtStatements(WhileStmt data) {}

}
