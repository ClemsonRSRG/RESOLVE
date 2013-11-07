package edu.clemson.cs.r2jt.treewalk;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.PosSymbol;

public abstract class TreeWalkerVisitor {

    public void preAny(ResolveConceptualElement data) {}

    public void postAny(ResolveConceptualElement data) {}

    // AbstractFunctionExp
    public boolean walkAbstractFunctionExp(AbstractFunctionExp data) {
        return false;
    }

    public void preAbstractFunctionExp(AbstractFunctionExp data) {}

    public void midAbstractFunctionExp(AbstractFunctionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractFunctionExp(AbstractFunctionExp data) {}

    // AbstractParameterizedModuleDec
    public boolean walkAbstractParameterizedModuleDec(
            AbstractParameterizedModuleDec data) {
        return false;
    }

    public void preAbstractParameterizedModuleDec(
            AbstractParameterizedModuleDec data) {}

    public void midAbstractParameterizedModuleDec(
            AbstractParameterizedModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAbstractParameterizedModuleDec(
            AbstractParameterizedModuleDec data) {}

    // AbstractParameterizedModuleDecParameters
    public boolean walkAbstractParameterizedModuleDecParameters(
            AbstractParameterizedModuleDec data) {
        return false;
    }

    public void preAbstractParameterizedModuleDecParameters(
            AbstractParameterizedModuleDec data) {}

    public void midAbstractParameterizedModuleDecParameters(
            AbstractParameterizedModuleDec node, ModuleParameterDec previous,
            ModuleParameterDec next) {}

    public void postAbstractParameterizedModuleDecParameters(
            AbstractParameterizedModuleDec data) {}

    // AffectsItem
    public boolean walkAffectsItem(AffectsItem data) {
        return false;
    }

    public void preAffectsItem(AffectsItem data) {}

    public void midAffectsItem(AffectsItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAffectsItem(AffectsItem data) {}

    // AlternativeExp
    public boolean walkAlternativeExp(AlternativeExp data) {
        return false;
    }

    public void preAlternativeExp(AlternativeExp data) {}

    public void midAlternativeExp(AlternativeExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAlternativeExp(AlternativeExp data) {}

    // AlternativeExpAlternatives
    public boolean walkAlternativeExpAlternatives(AlternativeExp data) {
        return false;
    }

    public void preAlternativeExpAlternatives(AlternativeExp data) {}

    public void midAlternativeExpAlternatives(AlternativeExp node,
            AltItemExp previous, AltItemExp next) {}

    public void postAlternativeExpAlternatives(AlternativeExp data) {}

    // AltItemExp
    public boolean walkAltItemExp(AltItemExp data) {
        return false;
    }

    public void preAltItemExp(AltItemExp data) {}

    public void midAltItemExp(AltItemExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAltItemExp(AltItemExp data) {}

    // ArbitraryExpTy
    public boolean walkArbitraryExpTy(ArbitraryExpTy data) {
        return false;
    }

    public void preArbitraryExpTy(ArbitraryExpTy data) {}

    public void midArbitraryExpTy(ArbitraryExpTy node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postArbitraryExpTy(ArbitraryExpTy data) {}

    // ArrayTy
    public boolean walkArrayTy(ArrayTy data) {
        return false;
    }

    public void preArrayTy(ArrayTy data) {}

    public void midArrayTy(ArrayTy node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postArrayTy(ArrayTy data) {}

    // AssumeStmt
    public boolean walkAssumeStmt(AssumeStmt data) {
        return false;
    }

    public void preAssumeStmt(AssumeStmt data) {}

    public void midAssumeStmt(AssumeStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAssumeStmt(AssumeStmt data) {}

    // AuxCodeStmt
    public boolean walkAuxCodeStmt(AuxCodeStmt data) {
        return false;
    }

    public void preAuxCodeStmt(AuxCodeStmt data) {}

    public void midAuxCodeStmt(AuxCodeStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postAuxCodeStmt(AuxCodeStmt data) {}

    // AuxCodeStmtStatements
    public boolean walkAuxCodeStmtStatements(AuxCodeStmt data) {
        return false;
    }

    public void preAuxCodeStmtStatements(AuxCodeStmt data) {}

    public void midAuxCodeStmtStatements(AuxCodeStmt node, Statement previous,
            Statement next) {}

    public void postAuxCodeStmtStatements(AuxCodeStmt data) {}

    // AuxVarDec
    public boolean walkAuxVarDec(AuxVarDec data) {
        return false;
    }

    public void preAuxVarDec(AuxVarDec data) {}

    public void midAuxVarDec(AuxVarDec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postAuxVarDec(AuxVarDec data) {}

    // BetweenExp
    public boolean walkBetweenExp(BetweenExp data) {
        return false;
    }

    public void preBetweenExp(BetweenExp data) {}

    public void midBetweenExp(BetweenExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postBetweenExp(BetweenExp data) {}

    // BetweenExpLessExps
    public boolean walkBetweenExpLessExps(BetweenExp data) {
        return false;
    }

    public void preBetweenExpLessExps(BetweenExp data) {}

    public void midBetweenExpLessExps(BetweenExp node, Exp previous, Exp next) {}

    public void postBetweenExpLessExps(BetweenExp data) {}

    // BooleanTy
    public boolean walkBooleanTy(BooleanTy data) {
        return false;
    }

    public void preBooleanTy(BooleanTy data) {}

    public void midBooleanTy(BooleanTy node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postBooleanTy(BooleanTy data) {}

    // CallStmt
    public boolean walkCallStmt(CallStmt data) {
        return false;
    }

    public void preCallStmt(CallStmt data) {}

    public void midCallStmt(CallStmt node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postCallStmt(CallStmt data) {}

    // CallStmtArguments
    public boolean walkCallStmtArguments(CallStmt data) {
        return false;
    }

    public void preCallStmtArguments(CallStmt data) {}

    public void midCallStmtArguments(CallStmt node, ProgramExp previous,
            ProgramExp next) {}

    public void postCallStmtArguments(CallStmt data) {}

    // CartProdTy
    public boolean walkCartProdTy(CartProdTy data) {
        return false;
    }

    public void preCartProdTy(CartProdTy data) {}

    public void midCartProdTy(CartProdTy node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postCartProdTy(CartProdTy data) {}

    // CartProdTyFields
    public boolean walkCartProdTyFields(CartProdTy data) {
        return false;
    }

    public void preCartProdTyFields(CartProdTy data) {}

    public void midCartProdTyFields(CartProdTy node, MathVarDec previous,
            MathVarDec next) {}

    public void postCartProdTyFields(CartProdTy data) {}

    // CategoricalDefinitionDec
    public boolean walkCategoricalDefinitionDec(CategoricalDefinitionDec data) {
        return false;
    }

    public void preCategoricalDefinitionDec(CategoricalDefinitionDec data) {}

    public void midCategoricalDefinitionDec(CategoricalDefinitionDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postCategoricalDefinitionDec(CategoricalDefinitionDec data) {}

    // CategoricalDefinitionDecDefinitions
    public boolean walkCategoricalDefinitionDecDefinitions(
            CategoricalDefinitionDec data) {
        return false;
    }

    public void preCategoricalDefinitionDecDefinitions(
            CategoricalDefinitionDec data) {}

    public void midCategoricalDefinitionDecDefinitions(
            CategoricalDefinitionDec node, DefinitionDec previous,
            DefinitionDec next) {}

    public void postCategoricalDefinitionDecDefinitions(
            CategoricalDefinitionDec data) {}

    // CharExp
    public boolean walkCharExp(CharExp data) {
        return false;
    }

    public void preCharExp(CharExp data) {}

    public void midCharExp(CharExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postCharExp(CharExp data) {}

    // ChoiceItem
    public boolean walkChoiceItem(ChoiceItem data) {
        return false;
    }

    public void preChoiceItem(ChoiceItem data) {}

    public void midChoiceItem(ChoiceItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postChoiceItem(ChoiceItem data) {}

    // ChoiceItemTest
    public boolean walkChoiceItemTest(ChoiceItem data) {
        return false;
    }

    public void preChoiceItemTest(ChoiceItem data) {}

    public void midChoiceItemTest(ChoiceItem node, ProgramExp previous,
            ProgramExp next) {}

    public void postChoiceItemTest(ChoiceItem data) {}

    // ChoiceItemThenclause
    public boolean walkChoiceItemThenclause(ChoiceItem data) {
        return false;
    }

    public void preChoiceItemThenclause(ChoiceItem data) {}

    public void midChoiceItemThenclause(ChoiceItem node, Statement previous,
            Statement next) {}

    public void postChoiceItemThenclause(ChoiceItem data) {}

    // ConceptBodyModuleDec
    public boolean walkConceptBodyModuleDec(ConceptBodyModuleDec data) {
        return false;
    }

    public void preConceptBodyModuleDec(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDec(ConceptBodyModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptBodyModuleDec(ConceptBodyModuleDec data) {}

    // ConceptBodyModuleDecEnhancementNames
    public boolean walkConceptBodyModuleDecEnhancementNames(
            ConceptBodyModuleDec data) {
        return false;
    }

    public void preConceptBodyModuleDecEnhancementNames(
            ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDecEnhancementNames(
            ConceptBodyModuleDec node, PosSymbol previous, PosSymbol next) {}

    public void postConceptBodyModuleDecEnhancementNames(
            ConceptBodyModuleDec data) {}

    // ConceptBodyModuleDecConventions
    public boolean walkConceptBodyModuleDecConventions(ConceptBodyModuleDec data) {
        return false;
    }

    public void preConceptBodyModuleDecConventions(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDecConventions(ConceptBodyModuleDec node,
            Exp previous, Exp next) {}

    public void postConceptBodyModuleDecConventions(ConceptBodyModuleDec data) {}

    // ConceptBodyModuleDecCorrs
    public boolean walkConceptBodyModuleDecCorrs(ConceptBodyModuleDec data) {
        return false;
    }

    public void preConceptBodyModuleDecCorrs(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDecCorrs(ConceptBodyModuleDec node,
            Exp previous, Exp next) {}

    public void postConceptBodyModuleDecCorrs(ConceptBodyModuleDec data) {}

    // ConceptBodyModuleDecDecs
    public boolean walkConceptBodyModuleDecDecs(ConceptBodyModuleDec data) {
        return false;
    }

    public void preConceptBodyModuleDecDecs(ConceptBodyModuleDec data) {}

    public void midConceptBodyModuleDecDecs(ConceptBodyModuleDec node,
            Dec previous, Dec next) {}

    public void postConceptBodyModuleDecDecs(ConceptBodyModuleDec data) {}

    // ConceptModuleDec
    public boolean walkConceptModuleDec(ConceptModuleDec data) {
        return false;
    }

    public void preConceptModuleDec(ConceptModuleDec data) {}

    public void midConceptModuleDec(ConceptModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptModuleDec(ConceptModuleDec data) {}

    // ConceptModuleDecDecs
    public boolean walkConceptModuleDecDecs(ConceptModuleDec data) {
        return false;
    }

    public void preConceptModuleDecDecs(ConceptModuleDec data) {}

    public void midConceptModuleDecDecs(ConceptModuleDec node, Dec previous,
            Dec next) {}

    public void postConceptModuleDecDecs(ConceptModuleDec data) {}

    // ConceptModuleDecConstraints
    public boolean walkConceptModuleDecConstraints(ConceptModuleDec data) {
        return false;
    }

    public void preConceptModuleDecConstraints(ConceptModuleDec data) {}

    public void midConceptModuleDecConstraints(ConceptModuleDec node,
            Exp previous, Exp next) {}

    public void postConceptModuleDecConstraints(ConceptModuleDec data) {}

    // ConceptTypeParamDec
    public boolean walkConceptTypeParamDec(ConceptTypeParamDec data) {
        return false;
    }

    public void preConceptTypeParamDec(ConceptTypeParamDec data) {}

    public void midConceptTypeParamDec(ConceptTypeParamDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConceptTypeParamDec(ConceptTypeParamDec data) {}

    // ConditionItem
    public boolean walkConditionItem(ConditionItem data) {
        return false;
    }

    public void preConditionItem(ConditionItem data) {}

    public void midConditionItem(ConditionItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConditionItem(ConditionItem data) {}

    // ConditionItemThenclause
    public boolean walkConditionItemThenclause(ConditionItem data) {
        return false;
    }

    public void preConditionItemThenclause(ConditionItem data) {}

    public void midConditionItemThenclause(ConditionItem node,
            Statement previous, Statement next) {}

    public void postConditionItemThenclause(ConditionItem data) {}

    // ConfirmStmt
    public boolean walkConfirmStmt(ConfirmStmt data) {
        return false;
    }

    public void preConfirmStmt(ConfirmStmt data) {}

    public void midConfirmStmt(ConfirmStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConfirmStmt(ConfirmStmt data) {}

    // ConstantParamDec
    public boolean walkConstantParamDec(ConstantParamDec data) {
        return false;
    }

    public void preConstantParamDec(ConstantParamDec data) {}

    public void midConstantParamDec(ConstantParamDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConstantParamDec(ConstantParamDec data) {}

    // ConstructedTy
    public boolean walkConstructedTy(ConstructedTy data) {
        return false;
    }

    public void preConstructedTy(ConstructedTy data) {}

    public void midConstructedTy(ConstructedTy node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postConstructedTy(ConstructedTy data) {}

    // ConstructedTyArgs
    public boolean walkConstructedTyArgs(ConstructedTy data) {
        return false;
    }

    public void preConstructedTyArgs(ConstructedTy data) {}

    public void midConstructedTyArgs(ConstructedTy node, Ty previous, Ty next) {}

    public void postConstructedTyArgs(ConstructedTy data) {}

    // CrossTypeExpression
    public boolean walkCrossTypeExpression(CrossTypeExpression data) {
        return false;
    }

    public void preCrossTypeExpression(CrossTypeExpression data) {}

    public void midCrossTypeExpression(CrossTypeExpression node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postCrossTypeExpression(CrossTypeExpression data) {}

    // Dec
    public boolean walkDec(Dec data) {
        return false;
    }

    public void preDec(Dec data) {}

    public void midDec(Dec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDec(Dec data) {}

    // DeductionExp
    public boolean walkDeductionExp(DeductionExp data) {
        return false;
    }

    public void preDeductionExp(DeductionExp data) {}

    public void midDeductionExp(DeductionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postDeductionExp(DeductionExp data) {}

    // DefinitionBody
    public boolean walkDefinitionBody(DefinitionBody data) {
        return false;
    }

    public void preDefinitionBody(DefinitionBody data) {}

    public void midDefinitionBody(DefinitionBody node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postDefinitionBody(DefinitionBody data) {}

    // DefinitionDec
    public boolean walkDefinitionDec(DefinitionDec data) {
        return false;
    }

    public void preDefinitionDec(DefinitionDec data) {}

    public void midDefinitionDec(DefinitionDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postDefinitionDec(DefinitionDec data) {}

    // DefinitionDecParameters
    public boolean walkDefinitionDecParameters(DefinitionDec data) {
        return false;
    }

    public void preDefinitionDecParameters(DefinitionDec data) {}

    public void midDefinitionDecParameters(DefinitionDec node,
            MathVarDec previous, MathVarDec next) {}

    public void postDefinitionDecParameters(DefinitionDec data) {}

    // DotExp
    public boolean walkDotExp(DotExp data) {
        return false;
    }

    public void preDotExp(DotExp data) {}

    public void midDotExp(DotExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDotExp(DotExp data) {}

    // DotExpSegments
    public boolean walkDotExpSegments(DotExp data) {
        return false;
    }

    public void preDotExpSegments(DotExp data) {}

    public void midDotExpSegments(DotExp node, Exp previous, Exp next) {}

    public void postDotExpSegments(DotExp data) {}

    // DoubleExp
    public boolean walkDoubleExp(DoubleExp data) {
        return false;
    }

    public void preDoubleExp(DoubleExp data) {}

    public void midDoubleExp(DoubleExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postDoubleExp(DoubleExp data) {}

    // EnhancementBodyItem
    public boolean walkEnhancementBodyItem(EnhancementBodyItem data) {
        return false;
    }

    public void preEnhancementBodyItem(EnhancementBodyItem data) {}

    public void midEnhancementBodyItem(EnhancementBodyItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementBodyItem(EnhancementBodyItem data) {}

    // EnhancementBodyItemParams
    public boolean walkEnhancementBodyItemParams(EnhancementBodyItem data) {
        return false;
    }

    public void preEnhancementBodyItemParams(EnhancementBodyItem data) {}

    public void midEnhancementBodyItemParams(EnhancementBodyItem node,
            ModuleArgumentItem previous, ModuleArgumentItem next) {}

    public void postEnhancementBodyItemParams(EnhancementBodyItem data) {}

    // EnhancementBodyItemBodyParams
    public boolean walkEnhancementBodyItemBodyParams(EnhancementBodyItem data) {
        return false;
    }

    public void preEnhancementBodyItemBodyParams(EnhancementBodyItem data) {}

    public void midEnhancementBodyItemBodyParams(EnhancementBodyItem node,
            ModuleArgumentItem previous, ModuleArgumentItem next) {}

    public void postEnhancementBodyItemBodyParams(EnhancementBodyItem data) {}

    // EnhancementBodyModuleDec
    public boolean walkEnhancementBodyModuleDec(EnhancementBodyModuleDec data) {
        return false;
    }

    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDec(EnhancementBodyModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec data) {}

    // EnhancementBodyModuleDecEnhancementBodies
    public boolean walkEnhancementBodyModuleDecEnhancementBodies(
            EnhancementBodyModuleDec data) {
        return false;
    }

    public void preEnhancementBodyModuleDecEnhancementBodies(
            EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDecEnhancementBodies(
            EnhancementBodyModuleDec node, EnhancementBodyItem previous,
            EnhancementBodyItem next) {}

    public void postEnhancementBodyModuleDecEnhancementBodies(
            EnhancementBodyModuleDec data) {}

    // EnhancementBodyModuleDecConventions
    public boolean walkEnhancementBodyModuleDecConventions(
            EnhancementBodyModuleDec data) {
        return false;
    }

    public void preEnhancementBodyModuleDecConventions(
            EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDecConventions(
            EnhancementBodyModuleDec node, Exp previous, Exp next) {}

    public void postEnhancementBodyModuleDecConventions(
            EnhancementBodyModuleDec data) {}

    // EnhancementBodyModuleDecCorrs
    public boolean walkEnhancementBodyModuleDecCorrs(
            EnhancementBodyModuleDec data) {
        return false;
    }

    public void preEnhancementBodyModuleDecCorrs(EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDecCorrs(EnhancementBodyModuleDec node,
            Exp previous, Exp next) {}

    public void postEnhancementBodyModuleDecCorrs(EnhancementBodyModuleDec data) {}

    // EnhancementBodyModuleDecDecs
    public boolean walkEnhancementBodyModuleDecDecs(
            EnhancementBodyModuleDec data) {
        return false;
    }

    public void preEnhancementBodyModuleDecDecs(EnhancementBodyModuleDec data) {}

    public void midEnhancementBodyModuleDecDecs(EnhancementBodyModuleDec node,
            Dec previous, Dec next) {}

    public void postEnhancementBodyModuleDecDecs(EnhancementBodyModuleDec data) {}

    // EnhancementItem
    public boolean walkEnhancementItem(EnhancementItem data) {
        return false;
    }

    public void preEnhancementItem(EnhancementItem data) {}

    public void midEnhancementItem(EnhancementItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementItem(EnhancementItem data) {}

    // EnhancementItemParams
    public boolean walkEnhancementItemParams(EnhancementItem data) {
        return false;
    }

    public void preEnhancementItemParams(EnhancementItem data) {}

    public void midEnhancementItemParams(EnhancementItem node,
            ModuleArgumentItem previous, ModuleArgumentItem next) {}

    public void postEnhancementItemParams(EnhancementItem data) {}

    // EnhancementModuleDec
    public boolean walkEnhancementModuleDec(EnhancementModuleDec data) {
        return false;
    }

    public void preEnhancementModuleDec(EnhancementModuleDec data) {}

    public void midEnhancementModuleDec(EnhancementModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postEnhancementModuleDec(EnhancementModuleDec data) {}

    // EnhancementModuleDecDecs
    public boolean walkEnhancementModuleDecDecs(EnhancementModuleDec data) {
        return false;
    }

    public void preEnhancementModuleDecDecs(EnhancementModuleDec data) {}

    public void midEnhancementModuleDecDecs(EnhancementModuleDec node,
            Dec previous, Dec next) {}

    public void postEnhancementModuleDecDecs(EnhancementModuleDec data) {}

    // EqualsExp
    public boolean walkEqualsExp(EqualsExp data) {
        return false;
    }

    public void preEqualsExp(EqualsExp data) {}

    public void midEqualsExp(EqualsExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postEqualsExp(EqualsExp data) {}

    // Exp
    public boolean walkExp(Exp data) {
        return false;
    }

    public void preExp(Exp data) {}

    public void midExp(Exp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postExp(Exp data) {}

    // FacilityDec
    public boolean walkFacilityDec(FacilityDec data) {
        return false;
    }

    public void preFacilityDec(FacilityDec data) {}

    public void midFacilityDec(FacilityDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityDec(FacilityDec data) {}

    // FacilityDecConceptParams
    public boolean walkFacilityDecConceptParams(FacilityDec data) {
        return false;
    }

    public void preFacilityDecConceptParams(FacilityDec data) {}

    public void midFacilityDecConceptParams(FacilityDec node,
            ModuleArgumentItem previous, ModuleArgumentItem next) {}

    public void postFacilityDecConceptParams(FacilityDec data) {}

    // FacilityDecEnhancements
    public boolean walkFacilityDecEnhancements(FacilityDec data) {
        return false;
    }

    public void preFacilityDecEnhancements(FacilityDec data) {}

    public void midFacilityDecEnhancements(FacilityDec node,
            EnhancementItem previous, EnhancementItem next) {}

    public void postFacilityDecEnhancements(FacilityDec data) {}

    // FacilityDecBodyParams
    public boolean walkFacilityDecBodyParams(FacilityDec data) {
        return false;
    }

    public void preFacilityDecBodyParams(FacilityDec data) {}

    public void midFacilityDecBodyParams(FacilityDec node,
            ModuleArgumentItem previous, ModuleArgumentItem next) {}

    public void postFacilityDecBodyParams(FacilityDec data) {}

    // FacilityDecEnhancementBodies
    public boolean walkFacilityDecEnhancementBodies(FacilityDec data) {
        return false;
    }

    public void preFacilityDecEnhancementBodies(FacilityDec data) {}

    public void midFacilityDecEnhancementBodies(FacilityDec node,
            EnhancementBodyItem previous, EnhancementBodyItem next) {}

    public void postFacilityDecEnhancementBodies(FacilityDec data) {}

    // FacilityModuleDec
    public boolean walkFacilityModuleDec(FacilityModuleDec data) {
        return false;
    }

    public void preFacilityModuleDec(FacilityModuleDec data) {}

    public void midFacilityModuleDec(FacilityModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityModuleDec(FacilityModuleDec data) {}

    // FacilityModuleDecDecs
    public boolean walkFacilityModuleDecDecs(FacilityModuleDec data) {
        return false;
    }

    public void preFacilityModuleDecDecs(FacilityModuleDec data) {}

    public void midFacilityModuleDecDecs(FacilityModuleDec node, Dec previous,
            Dec next) {}

    public void postFacilityModuleDecDecs(FacilityModuleDec data) {}

    // FacilityOperationDec
    public boolean walkFacilityOperationDec(FacilityOperationDec data) {
        return false;
    }

    public void preFacilityOperationDec(FacilityOperationDec data) {}

    public void midFacilityOperationDec(FacilityOperationDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityOperationDec(FacilityOperationDec data) {}

    // FacilityOperationDecParameters
    public boolean walkFacilityOperationDecParameters(FacilityOperationDec data) {
        return false;
    }

    public void preFacilityOperationDecParameters(FacilityOperationDec data) {}

    public void midFacilityOperationDecParameters(FacilityOperationDec node,
            ParameterVarDec previous, ParameterVarDec next) {}

    public void postFacilityOperationDecParameters(FacilityOperationDec data) {}

    // FacilityOperationDecStateVars
    public boolean walkFacilityOperationDecStateVars(FacilityOperationDec data) {
        return false;
    }

    public void preFacilityOperationDecStateVars(FacilityOperationDec data) {}

    public void midFacilityOperationDecStateVars(FacilityOperationDec node,
            AffectsItem previous, AffectsItem next) {}

    public void postFacilityOperationDecStateVars(FacilityOperationDec data) {}

    // FacilityOperationDecFacilities
    public boolean walkFacilityOperationDecFacilities(FacilityOperationDec data) {
        return false;
    }

    public void preFacilityOperationDecFacilities(FacilityOperationDec data) {}

    public void midFacilityOperationDecFacilities(FacilityOperationDec node,
            FacilityDec previous, FacilityDec next) {}

    public void postFacilityOperationDecFacilities(FacilityOperationDec data) {}

    // FacilityOperationDecVariables
    public boolean walkFacilityOperationDecVariables(FacilityOperationDec data) {
        return false;
    }

    public void preFacilityOperationDecVariables(FacilityOperationDec data) {}

    public void midFacilityOperationDecVariables(FacilityOperationDec node,
            VarDec previous, VarDec next) {}

    public void postFacilityOperationDecVariables(FacilityOperationDec data) {}

    // FacilityOperationDecAuxVariables
    public boolean walkFacilityOperationDecAuxVariables(
            FacilityOperationDec data) {
        return false;
    }

    public void preFacilityOperationDecAuxVariables(FacilityOperationDec data) {}

    public void midFacilityOperationDecAuxVariables(FacilityOperationDec node,
            AuxVarDec previous, AuxVarDec next) {}

    public void postFacilityOperationDecAuxVariables(FacilityOperationDec data) {}

    // FacilityOperationDecStatements
    public boolean walkFacilityOperationDecStatements(FacilityOperationDec data) {
        return false;
    }

    public void preFacilityOperationDecStatements(FacilityOperationDec data) {}

    public void midFacilityOperationDecStatements(FacilityOperationDec node,
            Statement previous, Statement next) {}

    public void postFacilityOperationDecStatements(FacilityOperationDec data) {}

    // FacilityTypeDec
    public boolean walkFacilityTypeDec(FacilityTypeDec data) {
        return false;
    }

    public void preFacilityTypeDec(FacilityTypeDec data) {}

    public void midFacilityTypeDec(FacilityTypeDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFacilityTypeDec(FacilityTypeDec data) {}

    // FieldExp
    public boolean walkFieldExp(FieldExp data) {
        return false;
    }

    public void preFieldExp(FieldExp data) {}

    public void midFieldExp(FieldExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postFieldExp(FieldExp data) {}

    // FinalItem
    public boolean walkFinalItem(FinalItem data) {
        return false;
    }

    public void preFinalItem(FinalItem data) {}

    public void midFinalItem(FinalItem node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postFinalItem(FinalItem data) {}

    // FinalItemStateVars
    public boolean walkFinalItemStateVars(FinalItem data) {
        return false;
    }

    public void preFinalItemStateVars(FinalItem data) {}

    public void midFinalItemStateVars(FinalItem node, AffectsItem previous,
            AffectsItem next) {}

    public void postFinalItemStateVars(FinalItem data) {}

    // FinalItemFacilities
    public boolean walkFinalItemFacilities(FinalItem data) {
        return false;
    }

    public void preFinalItemFacilities(FinalItem data) {}

    public void midFinalItemFacilities(FinalItem node, FacilityDec previous,
            FacilityDec next) {}

    public void postFinalItemFacilities(FinalItem data) {}

    // FinalItemVariables
    public boolean walkFinalItemVariables(FinalItem data) {
        return false;
    }

    public void preFinalItemVariables(FinalItem data) {}

    public void midFinalItemVariables(FinalItem node, VarDec previous,
            VarDec next) {}

    public void postFinalItemVariables(FinalItem data) {}

    // FinalItemAuxVariables
    public boolean walkFinalItemAuxVariables(FinalItem data) {
        return false;
    }

    public void preFinalItemAuxVariables(FinalItem data) {}

    public void midFinalItemAuxVariables(FinalItem node, AuxVarDec previous,
            AuxVarDec next) {}

    public void postFinalItemAuxVariables(FinalItem data) {}

    // FinalItemStatements
    public boolean walkFinalItemStatements(FinalItem data) {
        return false;
    }

    public void preFinalItemStatements(FinalItem data) {}

    public void midFinalItemStatements(FinalItem node, Statement previous,
            Statement next) {}

    public void postFinalItemStatements(FinalItem data) {}

    // FuncAssignStmt
    public boolean walkFuncAssignStmt(FuncAssignStmt data) {
        return false;
    }

    public void preFuncAssignStmt(FuncAssignStmt data) {}

    public void midFuncAssignStmt(FuncAssignStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFuncAssignStmt(FuncAssignStmt data) {}

    // FunctionArgList
    public boolean walkFunctionArgList(FunctionArgList data) {
        return false;
    }

    public void preFunctionArgList(FunctionArgList data) {}

    public void midFunctionArgList(FunctionArgList node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFunctionArgList(FunctionArgList data) {}

    // FunctionArgListArguments
    public boolean walkFunctionArgListArguments(FunctionArgList data) {
        return false;
    }

    public void preFunctionArgListArguments(FunctionArgList data) {}

    public void midFunctionArgListArguments(FunctionArgList node, Exp previous,
            Exp next) {}

    public void postFunctionArgListArguments(FunctionArgList data) {}

    // FunctionExp
    public boolean walkFunctionExp(FunctionExp data) {
        return false;
    }

    public void preFunctionExp(FunctionExp data) {}

    public void midFunctionExp(FunctionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFunctionExp(FunctionExp data) {}

    // FunctionExpParamList
    public boolean walkFunctionExpParamList(FunctionExp data) {
        return false;
    }

    public void preFunctionExpParamList(FunctionExp data) {}

    public void midFunctionExpParamList(FunctionExp node,
            FunctionArgList previous, FunctionArgList next) {}

    public void postFunctionExpParamList(FunctionExp data) {}

    // FunctionTy
    public boolean walkFunctionTy(FunctionTy data) {
        return false;
    }

    public void preFunctionTy(FunctionTy data) {}

    public void midFunctionTy(FunctionTy node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFunctionTy(FunctionTy data) {}

    // FunctionValueExp
    public boolean walkFunctionValueExp(FunctionValueExp data) {
        return false;
    }

    public void preFunctionValueExp(FunctionValueExp data) {}

    public void midFunctionValueExp(FunctionValueExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postFunctionValueExp(FunctionValueExp data) {}

    // GoalExp
    public boolean walkGoalExp(GoalExp data) {
        return false;
    }

    public void preGoalExp(GoalExp data) {}

    public void midGoalExp(GoalExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postGoalExp(GoalExp data) {}

    // HypDesigExp
    public boolean walkHypDesigExp(HypDesigExp data) {
        return false;
    }

    public void preHypDesigExp(HypDesigExp data) {}

    public void midHypDesigExp(HypDesigExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postHypDesigExp(HypDesigExp data) {}

    // IfExp
    public boolean walkIfExp(IfExp data) {
        return false;
    }

    public void preIfExp(IfExp data) {}

    public void midIfExp(IfExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIfExp(IfExp data) {}

    // IfStmt
    public boolean walkIfStmt(IfStmt data) {
        return false;
    }

    public void preIfStmt(IfStmt data) {}

    public void midIfStmt(IfStmt node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIfStmt(IfStmt data) {}

    // IfStmtThenclause
    public boolean walkIfStmtThenclause(IfStmt data) {
        return false;
    }

    public void preIfStmtThenclause(IfStmt data) {}

    public void midIfStmtThenclause(IfStmt node, Statement previous,
            Statement next) {}

    public void postIfStmtThenclause(IfStmt data) {}

    // IfStmtElseifpairs
    public boolean walkIfStmtElseifpairs(IfStmt data) {
        return false;
    }

    public void preIfStmtElseifpairs(IfStmt data) {}

    public void midIfStmtElseifpairs(IfStmt node, ConditionItem previous,
            ConditionItem next) {}

    public void postIfStmtElseifpairs(IfStmt data) {}

    // IfStmtElseclause
    public boolean walkIfStmtElseclause(IfStmt data) {
        return false;
    }

    public void preIfStmtElseclause(IfStmt data) {}

    public void midIfStmtElseclause(IfStmt node, Statement previous,
            Statement next) {}

    public void postIfStmtElseclause(IfStmt data) {}

    // InfixExp
    public boolean walkInfixExp(InfixExp data) {
        return false;
    }

    public void preInfixExp(InfixExp data) {}

    public void midInfixExp(InfixExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postInfixExp(InfixExp data) {}

    // InitItem
    public boolean walkInitItem(InitItem data) {
        return false;
    }

    public void preInitItem(InitItem data) {}

    public void midInitItem(InitItem node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postInitItem(InitItem data) {}

    // InitItemStateVars
    public boolean walkInitItemStateVars(InitItem data) {
        return false;
    }

    public void preInitItemStateVars(InitItem data) {}

    public void midInitItemStateVars(InitItem node, AffectsItem previous,
            AffectsItem next) {}

    public void postInitItemStateVars(InitItem data) {}

    // InitItemFacilities
    public boolean walkInitItemFacilities(InitItem data) {
        return false;
    }

    public void preInitItemFacilities(InitItem data) {}

    public void midInitItemFacilities(InitItem node, FacilityDec previous,
            FacilityDec next) {}

    public void postInitItemFacilities(InitItem data) {}

    // InitItemVariables
    public boolean walkInitItemVariables(InitItem data) {
        return false;
    }

    public void preInitItemVariables(InitItem data) {}

    public void midInitItemVariables(InitItem node, VarDec previous, VarDec next) {}

    public void postInitItemVariables(InitItem data) {}

    // InitItemAuxVariables
    public boolean walkInitItemAuxVariables(InitItem data) {
        return false;
    }

    public void preInitItemAuxVariables(InitItem data) {}

    public void midInitItemAuxVariables(InitItem node, AuxVarDec previous,
            AuxVarDec next) {}

    public void postInitItemAuxVariables(InitItem data) {}

    // InitItemStatements
    public boolean walkInitItemStatements(InitItem data) {
        return false;
    }

    public void preInitItemStatements(InitItem data) {}

    public void midInitItemStatements(InitItem node, Statement previous,
            Statement next) {}

    public void postInitItemStatements(InitItem data) {}

    // IntegerExp
    public boolean walkIntegerExp(IntegerExp data) {
        return false;
    }

    public void preIntegerExp(IntegerExp data) {}

    public void midIntegerExp(IntegerExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIntegerExp(IntegerExp data) {}

    // IsInExp
    public boolean walkIsInExp(IsInExp data) {
        return false;
    }

    public void preIsInExp(IsInExp data) {}

    public void midIsInExp(IsInExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postIsInExp(IsInExp data) {}

    // IterateExitStmt
    public boolean walkIterateExitStmt(IterateExitStmt data) {
        return false;
    }

    public void preIterateExitStmt(IterateExitStmt data) {}

    public void midIterateExitStmt(IterateExitStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIterateExitStmt(IterateExitStmt data) {}

    // IterateExitStmtStatements
    public boolean walkIterateExitStmtStatements(IterateExitStmt data) {
        return false;
    }

    public void preIterateExitStmtStatements(IterateExitStmt data) {}

    public void midIterateExitStmtStatements(IterateExitStmt node,
            Statement previous, Statement next) {}

    public void postIterateExitStmtStatements(IterateExitStmt data) {}

    // IterateStmt
    public boolean walkIterateStmt(IterateStmt data) {
        return false;
    }

    public void preIterateStmt(IterateStmt data) {}

    public void midIterateStmt(IterateStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIterateStmt(IterateStmt data) {}

    // IterateStmtChanging
    public boolean walkIterateStmtChanging(IterateStmt data) {
        return false;
    }

    public void preIterateStmtChanging(IterateStmt data) {}

    public void midIterateStmtChanging(IterateStmt node, VariableExp previous,
            VariableExp next) {}

    public void postIterateStmtChanging(IterateStmt data) {}

    // IterateStmtStatements
    public boolean walkIterateStmtStatements(IterateStmt data) {
        return false;
    }

    public void preIterateStmtStatements(IterateStmt data) {}

    public void midIterateStmtStatements(IterateStmt node, Statement previous,
            Statement next) {}

    public void postIterateStmtStatements(IterateStmt data) {}

    // IterativeExp
    public boolean walkIterativeExp(IterativeExp data) {
        return false;
    }

    public void preIterativeExp(IterativeExp data) {}

    public void midIterativeExp(IterativeExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postIterativeExp(IterativeExp data) {}

    // JustificationExp
    public boolean walkJustificationExp(JustificationExp data) {
        return false;
    }

    public void preJustificationExp(JustificationExp data) {}

    public void midJustificationExp(JustificationExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postJustificationExp(JustificationExp data) {}

    // JustifiedExp
    public boolean walkJustifiedExp(JustifiedExp data) {
        return false;
    }

    public void preJustifiedExp(JustifiedExp data) {}

    public void midJustifiedExp(JustifiedExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postJustifiedExp(JustifiedExp data) {}

    // LambdaExp
    public boolean walkLambdaExp(LambdaExp data) {
        return false;
    }

    public void preLambdaExp(LambdaExp data) {}

    public void midLambdaExp(LambdaExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postLambdaExp(LambdaExp data) {}

    // LambdaExpParameters
    public boolean walkLambdaExpParameters(LambdaExp data) {
        return false;
    }

    public void preLambdaExpParameters(LambdaExp data) {}

    public void midLambdaExpParameters(LambdaExp node, MathVarDec previous,
            MathVarDec next) {}

    public void postLambdaExpParameters(LambdaExp data) {}

    // LineNumberedExp
    public boolean walkLineNumberedExp(LineNumberedExp data) {
        return false;
    }

    public void preLineNumberedExp(LineNumberedExp data) {}

    public void midLineNumberedExp(LineNumberedExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postLineNumberedExp(LineNumberedExp data) {}

    // MathAssertionDec
    public boolean walkMathAssertionDec(MathAssertionDec data) {
        return false;
    }

    public void preMathAssertionDec(MathAssertionDec data) {}

    public void midMathAssertionDec(MathAssertionDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathAssertionDec(MathAssertionDec data) {}

    // MathModuleDec
    public boolean walkMathModuleDec(MathModuleDec data) {
        return false;
    }

    public void preMathModuleDec(MathModuleDec data) {}

    public void midMathModuleDec(MathModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathModuleDec(MathModuleDec data) {}

    // MathModuleDecDecs
    public boolean walkMathModuleDecDecs(MathModuleDec data) {
        return false;
    }

    public void preMathModuleDecDecs(MathModuleDec data) {}

    public void midMathModuleDecDecs(MathModuleDec node, Dec previous, Dec next) {}

    public void postMathModuleDecDecs(MathModuleDec data) {}

    // MathRefExp
    public boolean walkMathRefExp(MathRefExp data) {
        return false;
    }

    public void preMathRefExp(MathRefExp data) {}

    public void midMathRefExp(MathRefExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathRefExp(MathRefExp data) {}

    // MathRefExpParams
    public boolean walkMathRefExpParams(MathRefExp data) {
        return false;
    }

    public void preMathRefExpParams(MathRefExp data) {}

    public void midMathRefExpParams(MathRefExp node, VarExp previous,
            VarExp next) {}

    public void postMathRefExpParams(MathRefExp data) {}

    // MathTypeDec
    public boolean walkMathTypeDec(MathTypeDec data) {
        return false;
    }

    public void preMathTypeDec(MathTypeDec data) {}

    public void midMathTypeDec(MathTypeDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathTypeDec(MathTypeDec data) {}

    // MathTypeFormalDec
    public boolean walkMathTypeFormalDec(MathTypeFormalDec data) {
        return false;
    }

    public void preMathTypeFormalDec(MathTypeFormalDec data) {}

    public void midMathTypeFormalDec(MathTypeFormalDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathTypeFormalDec(MathTypeFormalDec data) {}

    // MathVarDec
    public boolean walkMathVarDec(MathVarDec data) {
        return false;
    }

    public void preMathVarDec(MathVarDec data) {}

    public void midMathVarDec(MathVarDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMathVarDec(MathVarDec data) {}

    // MemoryStmt
    public boolean walkMemoryStmt(MemoryStmt data) {
        return false;
    }

    public void preMemoryStmt(MemoryStmt data) {}

    public void midMemoryStmt(MemoryStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postMemoryStmt(MemoryStmt data) {}

    // ModuleArgumentItem
    public boolean walkModuleArgumentItem(ModuleArgumentItem data) {
        return false;
    }

    public void preModuleArgumentItem(ModuleArgumentItem data) {}

    public void midModuleArgumentItem(ModuleArgumentItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postModuleArgumentItem(ModuleArgumentItem data) {}

    // ModuleDec
    public boolean walkModuleDec(ModuleDec data) {
        return false;
    }

    public void preModuleDec(ModuleDec data) {}

    public void midModuleDec(ModuleDec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postModuleDec(ModuleDec data) {}

    // ModuleDecUsesItems
    public boolean walkModuleDecUsesItems(ModuleDec data) {
        return false;
    }

    public void preModuleDecUsesItems(ModuleDec data) {}

    public void midModuleDecUsesItems(ModuleDec node, UsesItem previous,
            UsesItem next) {}

    public void postModuleDecUsesItems(ModuleDec data) {}

    // ModuleParameterDec
    public boolean walkModuleParameterDec(ModuleParameterDec data) {
        return false;
    }

    public void preModuleParameterDec(ModuleParameterDec data) {}

    public void midModuleParameterDec(ModuleParameterDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postModuleParameterDec(ModuleParameterDec data) {}

    // NameTy
    public boolean walkNameTy(NameTy data) {
        return false;
    }

    public void preNameTy(NameTy data) {}

    public void midNameTy(NameTy node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postNameTy(NameTy data) {}

    // OldExp
    public boolean walkOldExp(OldExp data) {
        return false;
    }

    public void preOldExp(OldExp data) {}

    public void midOldExp(OldExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postOldExp(OldExp data) {}

    // OperationDec
    public boolean walkOperationDec(OperationDec data) {
        return false;
    }

    public void preOperationDec(OperationDec data) {}

    public void midOperationDec(OperationDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postOperationDec(OperationDec data) {}

    // OperationDecParameters
    public boolean walkOperationDecParameters(OperationDec data) {
        return false;
    }

    public void preOperationDecParameters(OperationDec data) {}

    public void midOperationDecParameters(OperationDec node,
            ParameterVarDec previous, ParameterVarDec next) {}

    public void postOperationDecParameters(OperationDec data) {}

    // OperationDecStateVars
    public boolean walkOperationDecStateVars(OperationDec data) {
        return false;
    }

    public void preOperationDecStateVars(OperationDec data) {}

    public void midOperationDecStateVars(OperationDec node,
            AffectsItem previous, AffectsItem next) {}

    public void postOperationDecStateVars(OperationDec data) {}

    // OutfixExp
    public boolean walkOutfixExp(OutfixExp data) {
        return false;
    }

    public void preOutfixExp(OutfixExp data) {}

    public void midOutfixExp(OutfixExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postOutfixExp(OutfixExp data) {}

    // ParameterVarDec
    public boolean walkParameterVarDec(ParameterVarDec data) {
        return false;
    }

    public void preParameterVarDec(ParameterVarDec data) {}

    public void midParameterVarDec(ParameterVarDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postParameterVarDec(ParameterVarDec data) {}

    // PerformanceCModuleDec
    public boolean walkPerformanceCModuleDec(PerformanceCModuleDec data) {
        return false;
    }

    public void prePerformanceCModuleDec(PerformanceCModuleDec data) {}

    public void midPerformanceCModuleDec(PerformanceCModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceCModuleDec(PerformanceCModuleDec data) {}

    // PerformanceCModuleDecParameters
    public boolean walkPerformanceCModuleDecParameters(
            PerformanceCModuleDec data) {
        return false;
    }

    public void prePerformanceCModuleDecParameters(PerformanceCModuleDec data) {}

    public void midPerformanceCModuleDecParameters(PerformanceCModuleDec node,
            ModuleParameterDec previous, ModuleParameterDec next) {}

    public void postPerformanceCModuleDecParameters(PerformanceCModuleDec data) {}

    // PerformanceCModuleDecUsesItems
    public boolean walkPerformanceCModuleDecUsesItems(PerformanceCModuleDec data) {
        return false;
    }

    public void prePerformanceCModuleDecUsesItems(PerformanceCModuleDec data) {}

    public void midPerformanceCModuleDecUsesItems(PerformanceCModuleDec node,
            UsesItem previous, UsesItem next) {}

    public void postPerformanceCModuleDecUsesItems(PerformanceCModuleDec data) {}

    // PerformanceCModuleDecConstraints
    public boolean walkPerformanceCModuleDecConstraints(
            PerformanceCModuleDec data) {
        return false;
    }

    public void prePerformanceCModuleDecConstraints(PerformanceCModuleDec data) {}

    public void midPerformanceCModuleDecConstraints(PerformanceCModuleDec node,
            Exp previous, Exp next) {}

    public void postPerformanceCModuleDecConstraints(PerformanceCModuleDec data) {}

    // PerformanceCModuleDecDecs
    public boolean walkPerformanceCModuleDecDecs(PerformanceCModuleDec data) {
        return false;
    }

    public void prePerformanceCModuleDecDecs(PerformanceCModuleDec data) {}

    public void midPerformanceCModuleDecDecs(PerformanceCModuleDec node,
            Dec previous, Dec next) {}

    public void postPerformanceCModuleDecDecs(PerformanceCModuleDec data) {}

    // PerformanceEModuleDec
    public boolean walkPerformanceEModuleDec(PerformanceEModuleDec data) {
        return false;
    }

    public void prePerformanceEModuleDec(PerformanceEModuleDec data) {}

    public void midPerformanceEModuleDec(PerformanceEModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceEModuleDec(PerformanceEModuleDec data) {}

    // PerformanceEModuleDecParameters
    public boolean walkPerformanceEModuleDecParameters(
            PerformanceEModuleDec data) {
        return false;
    }

    public void prePerformanceEModuleDecParameters(PerformanceEModuleDec data) {}

    public void midPerformanceEModuleDecParameters(PerformanceEModuleDec node,
            ModuleParameterDec previous, ModuleParameterDec next) {}

    public void postPerformanceEModuleDecParameters(PerformanceEModuleDec data) {}

    // PerformanceEModuleDecUsesItems
    public boolean walkPerformanceEModuleDecUsesItems(PerformanceEModuleDec data) {
        return false;
    }

    public void prePerformanceEModuleDecUsesItems(PerformanceEModuleDec data) {}

    public void midPerformanceEModuleDecUsesItems(PerformanceEModuleDec node,
            UsesItem previous, UsesItem next) {}

    public void postPerformanceEModuleDecUsesItems(PerformanceEModuleDec data) {}

    // PerformanceEModuleDecDecs
    public boolean walkPerformanceEModuleDecDecs(PerformanceEModuleDec data) {
        return false;
    }

    public void prePerformanceEModuleDecDecs(PerformanceEModuleDec data) {}

    public void midPerformanceEModuleDecDecs(PerformanceEModuleDec node,
            Dec previous, Dec next) {}

    public void postPerformanceEModuleDecDecs(PerformanceEModuleDec data) {}

    // PerformanceFinalItem
    public boolean walkPerformanceFinalItem(PerformanceFinalItem data) {
        return false;
    }

    public void prePerformanceFinalItem(PerformanceFinalItem data) {}

    public void midPerformanceFinalItem(PerformanceFinalItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceFinalItem(PerformanceFinalItem data) {}

    // PerformanceFinalItemStateVars
    public boolean walkPerformanceFinalItemStateVars(PerformanceFinalItem data) {
        return false;
    }

    public void prePerformanceFinalItemStateVars(PerformanceFinalItem data) {}

    public void midPerformanceFinalItemStateVars(PerformanceFinalItem node,
            AffectsItem previous, AffectsItem next) {}

    public void postPerformanceFinalItemStateVars(PerformanceFinalItem data) {}

    // PerformanceFinalItemFacilities
    public boolean walkPerformanceFinalItemFacilities(PerformanceFinalItem data) {
        return false;
    }

    public void prePerformanceFinalItemFacilities(PerformanceFinalItem data) {}

    public void midPerformanceFinalItemFacilities(PerformanceFinalItem node,
            FacilityDec previous, FacilityDec next) {}

    public void postPerformanceFinalItemFacilities(PerformanceFinalItem data) {}

    // PerformanceFinalItemVariables
    public boolean walkPerformanceFinalItemVariables(PerformanceFinalItem data) {
        return false;
    }

    public void prePerformanceFinalItemVariables(PerformanceFinalItem data) {}

    public void midPerformanceFinalItemVariables(PerformanceFinalItem node,
            VarDec previous, VarDec next) {}

    public void postPerformanceFinalItemVariables(PerformanceFinalItem data) {}

    // PerformanceFinalItemAuxVariables
    public boolean walkPerformanceFinalItemAuxVariables(
            PerformanceFinalItem data) {
        return false;
    }

    public void prePerformanceFinalItemAuxVariables(PerformanceFinalItem data) {}

    public void midPerformanceFinalItemAuxVariables(PerformanceFinalItem node,
            AuxVarDec previous, AuxVarDec next) {}

    public void postPerformanceFinalItemAuxVariables(PerformanceFinalItem data) {}

    // PerformanceFinalItemStatements
    public boolean walkPerformanceFinalItemStatements(PerformanceFinalItem data) {
        return false;
    }

    public void prePerformanceFinalItemStatements(PerformanceFinalItem data) {}

    public void midPerformanceFinalItemStatements(PerformanceFinalItem node,
            Statement previous, Statement next) {}

    public void postPerformanceFinalItemStatements(PerformanceFinalItem data) {}

    // PerformanceInitItem
    public boolean walkPerformanceInitItem(PerformanceInitItem data) {
        return false;
    }

    public void prePerformanceInitItem(PerformanceInitItem data) {}

    public void midPerformanceInitItem(PerformanceInitItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceInitItem(PerformanceInitItem data) {}

    // PerformanceInitItemStateVars
    public boolean walkPerformanceInitItemStateVars(PerformanceInitItem data) {
        return false;
    }

    public void prePerformanceInitItemStateVars(PerformanceInitItem data) {}

    public void midPerformanceInitItemStateVars(PerformanceInitItem node,
            AffectsItem previous, AffectsItem next) {}

    public void postPerformanceInitItemStateVars(PerformanceInitItem data) {}

    // PerformanceInitItemFacilities
    public boolean walkPerformanceInitItemFacilities(PerformanceInitItem data) {
        return false;
    }

    public void prePerformanceInitItemFacilities(PerformanceInitItem data) {}

    public void midPerformanceInitItemFacilities(PerformanceInitItem node,
            FacilityDec previous, FacilityDec next) {}

    public void postPerformanceInitItemFacilities(PerformanceInitItem data) {}

    // PerformanceInitItemVariables
    public boolean walkPerformanceInitItemVariables(PerformanceInitItem data) {
        return false;
    }

    public void prePerformanceInitItemVariables(PerformanceInitItem data) {}

    public void midPerformanceInitItemVariables(PerformanceInitItem node,
            VarDec previous, VarDec next) {}

    public void postPerformanceInitItemVariables(PerformanceInitItem data) {}

    // PerformanceInitItemAuxVariables
    public boolean walkPerformanceInitItemAuxVariables(PerformanceInitItem data) {
        return false;
    }

    public void prePerformanceInitItemAuxVariables(PerformanceInitItem data) {}

    public void midPerformanceInitItemAuxVariables(PerformanceInitItem node,
            AuxVarDec previous, AuxVarDec next) {}

    public void postPerformanceInitItemAuxVariables(PerformanceInitItem data) {}

    // PerformanceInitItemStatements
    public boolean walkPerformanceInitItemStatements(PerformanceInitItem data) {
        return false;
    }

    public void prePerformanceInitItemStatements(PerformanceInitItem data) {}

    public void midPerformanceInitItemStatements(PerformanceInitItem node,
            Statement previous, Statement next) {}

    public void postPerformanceInitItemStatements(PerformanceInitItem data) {}

    // PerformanceOperationDec
    public boolean walkPerformanceOperationDec(PerformanceOperationDec data) {
        return false;
    }

    public void prePerformanceOperationDec(PerformanceOperationDec data) {}

    public void midPerformanceOperationDec(PerformanceOperationDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceOperationDec(PerformanceOperationDec data) {}

    // PerformanceOperationDecParameters
    public boolean walkPerformanceOperationDecParameters(
            PerformanceOperationDec data) {
        return false;
    }

    public void prePerformanceOperationDecParameters(
            PerformanceOperationDec data) {}

    public void midPerformanceOperationDecParameters(
            PerformanceOperationDec node, ParameterVarDec previous,
            ParameterVarDec next) {}

    public void postPerformanceOperationDecParameters(
            PerformanceOperationDec data) {}

    // PerformanceOperationDecStateVars
    public boolean walkPerformanceOperationDecStateVars(
            PerformanceOperationDec data) {
        return false;
    }

    public void prePerformanceOperationDecStateVars(PerformanceOperationDec data) {}

    public void midPerformanceOperationDecStateVars(
            PerformanceOperationDec node, AffectsItem previous, AffectsItem next) {}

    public void postPerformanceOperationDecStateVars(
            PerformanceOperationDec data) {}

    // PerformanceTypeDec
    public boolean walkPerformanceTypeDec(PerformanceTypeDec data) {
        return false;
    }

    public void prePerformanceTypeDec(PerformanceTypeDec data) {}

    public void midPerformanceTypeDec(PerformanceTypeDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postPerformanceTypeDec(PerformanceTypeDec data) {}

    // PrefixExp
    public boolean walkPrefixExp(PrefixExp data) {
        return false;
    }

    public void prePrefixExp(PrefixExp data) {}

    public void midPrefixExp(PrefixExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postPrefixExp(PrefixExp data) {}

    // ProcedureDec
    public boolean walkProcedureDec(ProcedureDec data) {
        return false;
    }

    public void preProcedureDec(ProcedureDec data) {}

    public void midProcedureDec(ProcedureDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProcedureDec(ProcedureDec data) {}

    // ProcedureDecParameters
    public boolean walkProcedureDecParameters(ProcedureDec data) {
        return false;
    }

    public void preProcedureDecParameters(ProcedureDec data) {}

    public void midProcedureDecParameters(ProcedureDec node,
            ParameterVarDec previous, ParameterVarDec next) {}

    public void postProcedureDecParameters(ProcedureDec data) {}

    // ProcedureDecStateVars
    public boolean walkProcedureDecStateVars(ProcedureDec data) {
        return false;
    }

    public void preProcedureDecStateVars(ProcedureDec data) {}

    public void midProcedureDecStateVars(ProcedureDec node,
            AffectsItem previous, AffectsItem next) {}

    public void postProcedureDecStateVars(ProcedureDec data) {}

    // ProcedureDecFacilities
    public boolean walkProcedureDecFacilities(ProcedureDec data) {
        return false;
    }

    public void preProcedureDecFacilities(ProcedureDec data) {}

    public void midProcedureDecFacilities(ProcedureDec node,
            FacilityDec previous, FacilityDec next) {}

    public void postProcedureDecFacilities(ProcedureDec data) {}

    // ProcedureDecVariables
    public boolean walkProcedureDecVariables(ProcedureDec data) {
        return false;
    }

    public void preProcedureDecVariables(ProcedureDec data) {}

    public void midProcedureDecVariables(ProcedureDec node, VarDec previous,
            VarDec next) {}

    public void postProcedureDecVariables(ProcedureDec data) {}

    // ProcedureDecAuxVariables
    public boolean walkProcedureDecAuxVariables(ProcedureDec data) {
        return false;
    }

    public void preProcedureDecAuxVariables(ProcedureDec data) {}

    public void midProcedureDecAuxVariables(ProcedureDec node,
            AuxVarDec previous, AuxVarDec next) {}

    public void postProcedureDecAuxVariables(ProcedureDec data) {}

    // ProcedureDecStatements
    public boolean walkProcedureDecStatements(ProcedureDec data) {
        return false;
    }

    public void preProcedureDecStatements(ProcedureDec data) {}

    public void midProcedureDecStatements(ProcedureDec node,
            Statement previous, Statement next) {}

    public void postProcedureDecStatements(ProcedureDec data) {}

    // ProgramCharExp
    public boolean walkProgramCharExp(ProgramCharExp data) {
        return false;
    }

    public void preProgramCharExp(ProgramCharExp data) {}

    public void midProgramCharExp(ProgramCharExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramCharExp(ProgramCharExp data) {}

    // ProgramDotExp
    public boolean walkProgramDotExp(ProgramDotExp data) {
        return false;
    }

    public void preProgramDotExp(ProgramDotExp data) {}

    public void midProgramDotExp(ProgramDotExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramDotExp(ProgramDotExp data) {}

    // ProgramDotExpSegments
    public boolean walkProgramDotExpSegments(ProgramDotExp data) {
        return false;
    }

    public void preProgramDotExpSegments(ProgramDotExp data) {}

    public void midProgramDotExpSegments(ProgramDotExp node,
            ProgramExp previous, ProgramExp next) {}

    public void postProgramDotExpSegments(ProgramDotExp data) {}

    // ProgramDoubleExp
    public boolean walkProgramDoubleExp(ProgramDoubleExp data) {
        return false;
    }

    public void preProgramDoubleExp(ProgramDoubleExp data) {}

    public void midProgramDoubleExp(ProgramDoubleExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramDoubleExp(ProgramDoubleExp data) {}

    // ProgramExp
    public boolean walkProgramExp(ProgramExp data) {
        return false;
    }

    public void preProgramExp(ProgramExp data) {}

    public void midProgramExp(ProgramExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramExp(ProgramExp data) {}

    // ProgramFunctionExp
    public boolean walkProgramFunctionExp(ProgramFunctionExp data) {
        return false;
    }

    public void preProgramFunctionExp(ProgramFunctionExp data) {}

    public void midProgramFunctionExp(ProgramFunctionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramFunctionExp(ProgramFunctionExp data) {}

    // ProgramFunctionExpArguments
    public boolean walkProgramFunctionExpArguments(ProgramFunctionExp data) {
        return false;
    }

    public void preProgramFunctionExpArguments(ProgramFunctionExp data) {}

    public void midProgramFunctionExpArguments(ProgramFunctionExp node,
            ProgramExp previous, ProgramExp next) {}

    public void postProgramFunctionExpArguments(ProgramFunctionExp data) {}

    // ProgramIntegerExp
    public boolean walkProgramIntegerExp(ProgramIntegerExp data) {
        return false;
    }

    public void preProgramIntegerExp(ProgramIntegerExp data) {}

    public void midProgramIntegerExp(ProgramIntegerExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramIntegerExp(ProgramIntegerExp data) {}

    // ProgramOpExp
    public boolean walkProgramOpExp(ProgramOpExp data) {
        return false;
    }

    public void preProgramOpExp(ProgramOpExp data) {}

    public void midProgramOpExp(ProgramOpExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramOpExp(ProgramOpExp data) {}

    // ProgramParamExp
    public boolean walkProgramParamExp(ProgramParamExp data) {
        return false;
    }

    public void preProgramParamExp(ProgramParamExp data) {}

    public void midProgramParamExp(ProgramParamExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramParamExp(ProgramParamExp data) {}

    // ProgramParamExpArguments
    public boolean walkProgramParamExpArguments(ProgramParamExp data) {
        return false;
    }

    public void preProgramParamExpArguments(ProgramParamExp data) {}

    public void midProgramParamExpArguments(ProgramParamExp node,
            ProgramExp previous, ProgramExp next) {}

    public void postProgramParamExpArguments(ProgramParamExp data) {}

    // ProgramStringExp
    public boolean walkProgramStringExp(ProgramStringExp data) {
        return false;
    }

    public void preProgramStringExp(ProgramStringExp data) {}

    public void midProgramStringExp(ProgramStringExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProgramStringExp(ProgramStringExp data) {}

    // ProofDec
    public boolean walkProofDec(ProofDec data) {
        return false;
    }

    public void preProofDec(ProofDec data) {}

    public void midProofDec(ProofDec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postProofDec(ProofDec data) {}

    // ProofDecStatements
    public boolean walkProofDecStatements(ProofDec data) {
        return false;
    }

    public void preProofDecStatements(ProofDec data) {}

    public void midProofDecStatements(ProofDec node, Exp previous, Exp next) {}

    public void postProofDecStatements(ProofDec data) {}

    // ProofDecBaseCase
    public boolean walkProofDecBaseCase(ProofDec data) {
        return false;
    }

    public void preProofDecBaseCase(ProofDec data) {}

    public void midProofDecBaseCase(ProofDec node, Exp previous, Exp next) {}

    public void postProofDecBaseCase(ProofDec data) {}

    // ProofDecInductiveCase
    public boolean walkProofDecInductiveCase(ProofDec data) {
        return false;
    }

    public void preProofDecInductiveCase(ProofDec data) {}

    public void midProofDecInductiveCase(ProofDec node, Exp previous, Exp next) {}

    public void postProofDecInductiveCase(ProofDec data) {}

    // ProofDefinitionExp
    public boolean walkProofDefinitionExp(ProofDefinitionExp data) {
        return false;
    }

    public void preProofDefinitionExp(ProofDefinitionExp data) {}

    public void midProofDefinitionExp(ProofDefinitionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProofDefinitionExp(ProofDefinitionExp data) {}

    // ProofModuleDec
    public boolean walkProofModuleDec(ProofModuleDec data) {
        return false;
    }

    public void preProofModuleDec(ProofModuleDec data) {}

    public void midProofModuleDec(ProofModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postProofModuleDec(ProofModuleDec data) {}

    // ProofModuleDecModuleParams
    public boolean walkProofModuleDecModuleParams(ProofModuleDec data) {
        return false;
    }

    public void preProofModuleDecModuleParams(ProofModuleDec data) {}

    public void midProofModuleDecModuleParams(ProofModuleDec node,
            ModuleParameterDec previous, ModuleParameterDec next) {}

    public void postProofModuleDecModuleParams(ProofModuleDec data) {}

    // ProofModuleDecDecs
    public boolean walkProofModuleDecDecs(ProofModuleDec data) {
        return false;
    }

    public void preProofModuleDecDecs(ProofModuleDec data) {}

    public void midProofModuleDecDecs(ProofModuleDec node, Dec previous,
            Dec next) {}

    public void postProofModuleDecDecs(ProofModuleDec data) {}

    // QuantExp
    public boolean walkQuantExp(QuantExp data) {
        return false;
    }

    public void preQuantExp(QuantExp data) {}

    public void midQuantExp(QuantExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postQuantExp(QuantExp data) {}

    // QuantExpVars
    public boolean walkQuantExpVars(QuantExp data) {
        return false;
    }

    public void preQuantExpVars(QuantExp data) {}

    public void midQuantExpVars(QuantExp node, MathVarDec previous,
            MathVarDec next) {}

    public void postQuantExpVars(QuantExp data) {}

    // RealizationParamDec
    public boolean walkRealizationParamDec(RealizationParamDec data) {
        return false;
    }

    public void preRealizationParamDec(RealizationParamDec data) {}

    public void midRealizationParamDec(RealizationParamDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRealizationParamDec(RealizationParamDec data) {}

    // RecordTy
    public boolean walkRecordTy(RecordTy data) {
        return false;
    }

    public void preRecordTy(RecordTy data) {}

    public void midRecordTy(RecordTy node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postRecordTy(RecordTy data) {}

    // RecordTyFields
    public boolean walkRecordTyFields(RecordTy data) {
        return false;
    }

    public void preRecordTyFields(RecordTy data) {}

    public void midRecordTyFields(RecordTy node, VarDec previous, VarDec next) {}

    public void postRecordTyFields(RecordTy data) {}

    // RenamingItem
    public boolean walkRenamingItem(RenamingItem data) {
        return false;
    }

    public void preRenamingItem(RenamingItem data) {}

    public void midRenamingItem(RenamingItem node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRenamingItem(RenamingItem data) {}

    // RepresentationDec
    public boolean walkRepresentationDec(RepresentationDec data) {
        return false;
    }

    public void preRepresentationDec(RepresentationDec data) {}

    public void midRepresentationDec(RepresentationDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postRepresentationDec(RepresentationDec data) {}

    // ResolveConceptualElement
    public boolean walkResolveConceptualElement(ResolveConceptualElement data) {
        return false;
    }

    public void preResolveConceptualElement(ResolveConceptualElement data) {}

    public void midResolveConceptualElement(ResolveConceptualElement node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postResolveConceptualElement(ResolveConceptualElement data) {}

    // SelectionStmt
    public boolean walkSelectionStmt(SelectionStmt data) {
        return false;
    }

    public void preSelectionStmt(SelectionStmt data) {}

    public void midSelectionStmt(SelectionStmt node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSelectionStmt(SelectionStmt data) {}

    // SelectionStmtWhenpairs
    public boolean walkSelectionStmtWhenpairs(SelectionStmt data) {
        return false;
    }

    public void preSelectionStmtWhenpairs(SelectionStmt data) {}

    public void midSelectionStmtWhenpairs(SelectionStmt node,
            ChoiceItem previous, ChoiceItem next) {}

    public void postSelectionStmtWhenpairs(SelectionStmt data) {}

    // SelectionStmtDefaultclause
    public boolean walkSelectionStmtDefaultclause(SelectionStmt data) {
        return false;
    }

    public void preSelectionStmtDefaultclause(SelectionStmt data) {}

    public void midSelectionStmtDefaultclause(SelectionStmt node,
            Statement previous, Statement next) {}

    public void postSelectionStmtDefaultclause(SelectionStmt data) {}

    // SetExp
    public boolean walkSetExp(SetExp data) {
        return false;
    }

    public void preSetExp(SetExp data) {}

    public void midSetExp(SetExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postSetExp(SetExp data) {}

    // SetExpVars
    public boolean walkSetExpVars(SetExp data) {
        return false;
    }

    public void preSetExpVars(SetExp data) {}

    public void midSetExpVars(SetExp node, VarExp previous, VarExp next) {}

    public void postSetExpVars(SetExp data) {}

    // ShortFacilityModuleDec
    public boolean walkShortFacilityModuleDec(ShortFacilityModuleDec data) {
        return false;
    }

    public void preShortFacilityModuleDec(ShortFacilityModuleDec data) {}

    public void midShortFacilityModuleDec(ShortFacilityModuleDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postShortFacilityModuleDec(ShortFacilityModuleDec data) {}

    // Statement
    public boolean walkStatement(Statement data) {
        return false;
    }

    public void preStatement(Statement data) {}

    public void midStatement(Statement node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postStatement(Statement data) {}

    // StringExp
    public boolean walkStringExp(StringExp data) {
        return false;
    }

    public void preStringExp(StringExp data) {}

    public void midStringExp(StringExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postStringExp(StringExp data) {}

    // StructureExp
    public boolean walkStructureExp(StructureExp data) {
        return false;
    }

    public void preStructureExp(StructureExp data) {}

    public void midStructureExp(StructureExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postStructureExp(StructureExp data) {}

    // SubtypeDec
    public boolean walkSubtypeDec(SubtypeDec data) {
        return false;
    }

    public void preSubtypeDec(SubtypeDec data) {}

    public void midSubtypeDec(SubtypeDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSubtypeDec(SubtypeDec data) {}

    // SuppositionDeductionExp
    public boolean walkSuppositionDeductionExp(SuppositionDeductionExp data) {
        return false;
    }

    public void preSuppositionDeductionExp(SuppositionDeductionExp data) {}

    public void midSuppositionDeductionExp(SuppositionDeductionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSuppositionDeductionExp(SuppositionDeductionExp data) {}

    // SuppositionDeductionExpBody
    public boolean walkSuppositionDeductionExpBody(SuppositionDeductionExp data) {
        return false;
    }

    public void preSuppositionDeductionExpBody(SuppositionDeductionExp data) {}

    public void midSuppositionDeductionExpBody(SuppositionDeductionExp node,
            Exp previous, Exp next) {}

    public void postSuppositionDeductionExpBody(SuppositionDeductionExp data) {}

    // SuppositionExp
    public boolean walkSuppositionExp(SuppositionExp data) {
        return false;
    }

    public void preSuppositionExp(SuppositionExp data) {}

    public void midSuppositionExp(SuppositionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postSuppositionExp(SuppositionExp data) {}

    // SuppositionExpVars
    public boolean walkSuppositionExpVars(SuppositionExp data) {
        return false;
    }

    public void preSuppositionExpVars(SuppositionExp data) {}

    public void midSuppositionExpVars(SuppositionExp node, MathVarDec previous,
            MathVarDec next) {}

    public void postSuppositionExpVars(SuppositionExp data) {}

    // SwapStmt
    public boolean walkSwapStmt(SwapStmt data) {
        return false;
    }

    public void preSwapStmt(SwapStmt data) {}

    public void midSwapStmt(SwapStmt node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postSwapStmt(SwapStmt data) {}

    // TupleExp
    public boolean walkTupleExp(TupleExp data) {
        return false;
    }

    public void preTupleExp(TupleExp data) {}

    public void midTupleExp(TupleExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTupleExp(TupleExp data) {}

    // TupleExpFields
    public boolean walkTupleExpFields(TupleExp data) {
        return false;
    }

    public void preTupleExpFields(TupleExp data) {}

    public void midTupleExpFields(TupleExp node, Exp previous, Exp next) {}

    public void postTupleExpFields(TupleExp data) {}

    // TupleTy
    public boolean walkTupleTy(TupleTy data) {
        return false;
    }

    public void preTupleTy(TupleTy data) {}

    public void midTupleTy(TupleTy node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTupleTy(TupleTy data) {}

    // TupleTyFields
    public boolean walkTupleTyFields(TupleTy data) {
        return false;
    }

    public void preTupleTyFields(TupleTy data) {}

    public void midTupleTyFields(TupleTy node, Ty previous, Ty next) {}

    public void postTupleTyFields(TupleTy data) {}

    // Ty
    public boolean walkTy(Ty data) {
        return false;
    }

    public void preTy(Ty data) {}

    public void midTy(Ty node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTy(Ty data) {}

    // TypeAssertionExp
    public boolean walkTypeAssertionExp(TypeAssertionExp data) {
        return false;
    }

    public void preTypeAssertionExp(TypeAssertionExp data) {}

    public void midTypeAssertionExp(TypeAssertionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeAssertionExp(TypeAssertionExp data) {}

    // TypeDec
    public boolean walkTypeDec(TypeDec data) {
        return false;
    }

    public void preTypeDec(TypeDec data) {}

    public void midTypeDec(TypeDec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postTypeDec(TypeDec data) {}

    // TypeFunctionExp
    public boolean walkTypeFunctionExp(TypeFunctionExp data) {
        return false;
    }

    public void preTypeFunctionExp(TypeFunctionExp data) {}

    public void midTypeFunctionExp(TypeFunctionExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeFunctionExp(TypeFunctionExp data) {}

    // TypeFunctionExpParams
    public boolean walkTypeFunctionExpParams(TypeFunctionExp data) {
        return false;
    }

    public void preTypeFunctionExpParams(TypeFunctionExp data) {}

    public void midTypeFunctionExpParams(TypeFunctionExp node, Exp previous,
            Exp next) {}

    public void postTypeFunctionExpParams(TypeFunctionExp data) {}

    // TypeTheoremDec
    public boolean walkTypeTheoremDec(TypeTheoremDec data) {
        return false;
    }

    public void preTypeTheoremDec(TypeTheoremDec data) {}

    public void midTypeTheoremDec(TypeTheoremDec node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postTypeTheoremDec(TypeTheoremDec data) {}

    // TypeTheoremDecMyUniversalVars
    public boolean walkTypeTheoremDecMyUniversalVars(TypeTheoremDec data) {
        return false;
    }

    public void preTypeTheoremDecMyUniversalVars(TypeTheoremDec data) {}

    public void midTypeTheoremDecMyUniversalVars(TypeTheoremDec node,
            MathVarDec previous, MathVarDec next) {}

    public void postTypeTheoremDecMyUniversalVars(TypeTheoremDec data) {}

    // UnaryMinusExp
    public boolean walkUnaryMinusExp(UnaryMinusExp data) {
        return false;
    }

    public void preUnaryMinusExp(UnaryMinusExp data) {}

    public void midUnaryMinusExp(UnaryMinusExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postUnaryMinusExp(UnaryMinusExp data) {}

    // UsesItem
    public boolean walkUsesItem(UsesItem data) {
        return false;
    }

    public void preUsesItem(UsesItem data) {}

    public void midUsesItem(UsesItem node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postUsesItem(UsesItem data) {}

    // VarDec
    public boolean walkVarDec(VarDec data) {
        return false;
    }

    public void preVarDec(VarDec data) {}

    public void midVarDec(VarDec node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postVarDec(VarDec data) {}

    // VarExp
    public boolean walkVarExp(VarExp data) {
        return false;
    }

    public void preVarExp(VarExp data) {}

    public void midVarExp(VarExp node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postVarExp(VarExp data) {}

    // VariableArrayExp
    public boolean walkVariableArrayExp(VariableArrayExp data) {
        return false;
    }

    public void preVariableArrayExp(VariableArrayExp data) {}

    public void midVariableArrayExp(VariableArrayExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVariableArrayExp(VariableArrayExp data) {}

    // VariableDotExp
    public boolean walkVariableDotExp(VariableDotExp data) {
        return false;
    }

    public void preVariableDotExp(VariableDotExp data) {}

    public void midVariableDotExp(VariableDotExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVariableDotExp(VariableDotExp data) {}

    // VariableDotExpSegments
    public boolean walkVariableDotExpSegments(VariableDotExp data) {
        return false;
    }

    public void preVariableDotExpSegments(VariableDotExp data) {}

    public void midVariableDotExpSegments(VariableDotExp node,
            VariableExp previous, VariableExp next) {}

    public void postVariableDotExpSegments(VariableDotExp data) {}

    // VariableExp
    public boolean walkVariableExp(VariableExp data) {
        return false;
    }

    public void preVariableExp(VariableExp data) {}

    public void midVariableExp(VariableExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVariableExp(VariableExp data) {}

    // VariableNameExp
    public boolean walkVariableNameExp(VariableNameExp data) {
        return false;
    }

    public void preVariableNameExp(VariableNameExp data) {}

    public void midVariableNameExp(VariableNameExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVariableNameExp(VariableNameExp data) {}

    // VariableRecordExp
    public boolean walkVariableRecordExp(VariableRecordExp data) {
        return false;
    }

    public void preVariableRecordExp(VariableRecordExp data) {}

    public void midVariableRecordExp(VariableRecordExp node,
            ResolveConceptualElement previous, ResolveConceptualElement next) {}

    public void postVariableRecordExp(VariableRecordExp data) {}

    // VariableRecordExpFields
    public boolean walkVariableRecordExpFields(VariableRecordExp data) {
        return false;
    }

    public void preVariableRecordExpFields(VariableRecordExp data) {}

    public void midVariableRecordExpFields(VariableRecordExp node,
            VariableExp previous, VariableExp next) {}

    public void postVariableRecordExpFields(VariableRecordExp data) {}

    // WhileStmt
    public boolean walkWhileStmt(WhileStmt data) {
        return false;
    }

    public void preWhileStmt(WhileStmt data) {}

    public void midWhileStmt(WhileStmt node, ResolveConceptualElement previous,
            ResolveConceptualElement next) {}

    public void postWhileStmt(WhileStmt data) {}

    // WhileStmtChanging
    public boolean walkWhileStmtChanging(WhileStmt data) {
        return false;
    }

    public void preWhileStmtChanging(WhileStmt data) {}

    public void midWhileStmtChanging(WhileStmt node, VariableExp previous,
            VariableExp next) {}

    public void postWhileStmtChanging(WhileStmt data) {}

    // WhileStmtStatements
    public boolean walkWhileStmtStatements(WhileStmt data) {
        return false;
    }

    public void preWhileStmtStatements(WhileStmt data) {}

    public void midWhileStmtStatements(WhileStmt node, Statement previous,
            Statement next) {}

    public void postWhileStmtStatements(WhileStmt data) {}

}
