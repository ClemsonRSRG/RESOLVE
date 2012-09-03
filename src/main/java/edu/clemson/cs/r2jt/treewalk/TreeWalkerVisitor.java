package edu.clemson.cs.r2jt.treewalk;

import edu.clemson.cs.r2jt.absyn.*;

public abstract class TreeWalkerVisitor {
	public void preAny(ResolveConceptualElement node) { }
	public void postAny(ResolveConceptualElement node) { }
	//public void preAbstractFunctionExp(AbstractFunctionExp node) { }
	//public void midAbstractFunctionExp(AbstractFunctionExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	//public void postAbstractFunctionExp(AbstractFunctionExp node) { }
	public void preAffectsItem(AffectsItem node) { }
	public void midAffectsItem(AffectsItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postAffectsItem(AffectsItem node) { }
	public void preAlternativeExp(AlternativeExp node) { }
	public void midAlternativeExp(AlternativeExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postAlternativeExp(AlternativeExp node) { }
	public void preAltItemExp(AltItemExp node) { }
	public void midAltItemExp(AltItemExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postAltItemExp(AltItemExp node) { }
	//public void preArbitraryExpTy(ArbitraryExpTy node) { }
	//public void midArbitraryExpTy(ArbitraryExpTy node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	//public void postArbitraryExpTy(ArbitraryExpTy node) { }
	public void preArrayTy(ArrayTy node) { }
	public void midArrayTy(ArrayTy node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postArrayTy(ArrayTy node) { }
	public void preAssumeStmt(AssumeStmt node) { }
	public void midAssumeStmt(AssumeStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postAssumeStmt(AssumeStmt node) { }
	public void preAuxCodeStmt(AuxCodeStmt node) { }
	public void midAuxCodeStmt(AuxCodeStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postAuxCodeStmt(AuxCodeStmt node) { }
	public void preAuxVarDec(AuxVarDec node) { }
	public void midAuxVarDec(AuxVarDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postAuxVarDec(AuxVarDec node) { }
	public void preBetweenExp(BetweenExp node) { }
	public void midBetweenExp(BetweenExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postBetweenExp(BetweenExp node) { }
	public void preBooleanTy(BooleanTy node) { }
	public void midBooleanTy(BooleanTy node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postBooleanTy(BooleanTy node) { }
	public void preCallStmt(CallStmt node) { }
	public void midCallStmt(CallStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postCallStmt(CallStmt node) { }
	public void preCartProdTy(CartProdTy node) { }
	public void midCartProdTy(CartProdTy node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postCartProdTy(CartProdTy node) { }
	public void preCategoricalDefinitionDec(CategoricalDefinitionDec node) { }
	public void midCategoricalDefinitionDec(CategoricalDefinitionDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postCategoricalDefinitionDec(CategoricalDefinitionDec node) { }
	public void preCharExp(CharExp node) { }
	public void midCharExp(CharExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postCharExp(CharExp node) { }
	public void preChoiceItem(ChoiceItem node) { }
	public void midChoiceItem(ChoiceItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postChoiceItem(ChoiceItem node) { }
	public void preConceptBodyModuleDec(ConceptBodyModuleDec node) { }
	public void midConceptBodyModuleDec(ConceptBodyModuleDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postConceptBodyModuleDec(ConceptBodyModuleDec node) { }
	public void preConceptModuleDec(ConceptModuleDec node) { }
	public void midConceptModuleDec(ConceptModuleDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postConceptModuleDec(ConceptModuleDec node) { }
	public void preConceptTypeParamDec(ConceptTypeParamDec node) { }
	public void midConceptTypeParamDec(ConceptTypeParamDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postConceptTypeParamDec(ConceptTypeParamDec node) { }
	public void preConditionItem(ConditionItem node) { }
	public void midConditionItem(ConditionItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postConditionItem(ConditionItem node) { }
	public void preConfirmStmt(ConfirmStmt node) { }
	public void midConfirmStmt(ConfirmStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postConfirmStmt(ConfirmStmt node) { }
	public void preConstantParamDec(ConstantParamDec node) { }
	public void midConstantParamDec(ConstantParamDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postConstantParamDec(ConstantParamDec node) { }
	public void preConstructedTy(ConstructedTy node) { }
	public void midConstructedTy(ConstructedTy node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postConstructedTy(ConstructedTy node) { }
	//public void preCrossTypeExpression(CrossTypeExpression node) { }
	//public void midCrossTypeExpression(CrossTypeExpression node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	//public void postCrossTypeExpression(CrossTypeExpression node) { }
	public void preDec(Dec node) { }
	public void midDec(Dec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postDec(Dec node) { }
	public void preDeductionExp(DeductionExp node) { }
	public void midDeductionExp(DeductionExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postDeductionExp(DeductionExp node) { }
	//public void preDefinitionBody(DefinitionBody node) { }
	//public void midDefinitionBody(DefinitionBody node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	//public void postDefinitionBody(DefinitionBody node) { }
	public void preDefinitionDec(DefinitionDec node) { }
	public void midDefinitionDec(DefinitionDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postDefinitionDec(DefinitionDec node) { }
	public void preDotExp(DotExp node) { }
	public void midDotExp(DotExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postDotExp(DotExp node) { }
	public void preDoubleExp(DoubleExp node) { }
	public void midDoubleExp(DoubleExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postDoubleExp(DoubleExp node) { }
	public void preEnhancementBodyItem(EnhancementBodyItem node) { }
	public void midEnhancementBodyItem(EnhancementBodyItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postEnhancementBodyItem(EnhancementBodyItem node) { }
	public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec node) { }
	public void midEnhancementBodyModuleDec(EnhancementBodyModuleDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec node) { }
	public void preEnhancementItem(EnhancementItem node) { }
	public void midEnhancementItem(EnhancementItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postEnhancementItem(EnhancementItem node) { }
	public void preEnhancementModuleDec(EnhancementModuleDec node) { }
	public void midEnhancementModuleDec(EnhancementModuleDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postEnhancementModuleDec(EnhancementModuleDec node) { }
	public void preEqualsExp(EqualsExp node) { }
	public void midEqualsExp(EqualsExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postEqualsExp(EqualsExp node) { }
	public void preExp(Exp node) { }
	public void midExp(Exp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postExp(Exp node) { }
	public void preFacilityDec(FacilityDec node) { }
	public void midFacilityDec(FacilityDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postFacilityDec(FacilityDec node) { }
	public void preFacilityModuleDec(FacilityModuleDec node) { }
	public void midFacilityModuleDec(FacilityModuleDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postFacilityModuleDec(FacilityModuleDec node) { }
	public void preFacilityOperationDec(FacilityOperationDec node) { }
	public void midFacilityOperationDec(FacilityOperationDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postFacilityOperationDec(FacilityOperationDec node) { }
	public void preFacilityTypeDec(FacilityTypeDec node) { }
	public void midFacilityTypeDec(FacilityTypeDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postFacilityTypeDec(FacilityTypeDec node) { }
	public void preFieldExp(FieldExp node) { }
	public void midFieldExp(FieldExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postFieldExp(FieldExp node) { }
	public void preFinalItem(FinalItem node) { }
	public void midFinalItem(FinalItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postFinalItem(FinalItem node) { }
	public void preFuncAssignStmt(FuncAssignStmt node) { }
	public void midFuncAssignStmt(FuncAssignStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postFuncAssignStmt(FuncAssignStmt node) { }
	public void preFunctionArgList(FunctionArgList node) { }
	public void midFunctionArgList(FunctionArgList node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postFunctionArgList(FunctionArgList node) { }
	public void preFunctionExp(FunctionExp node) { }
	public void midFunctionExp(FunctionExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postFunctionExp(FunctionExp node) { }
	public void preFunctionTy(FunctionTy node) { }
	public void midFunctionTy(FunctionTy node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postFunctionTy(FunctionTy node) { }
	//public void preFunctionValueExp(FunctionValueExp node) { }
	//public void midFunctionValueExp(FunctionValueExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	//public void postFunctionValueExp(FunctionValueExp node) { }
	public void preGoalExp(GoalExp node) { }
	public void midGoalExp(GoalExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postGoalExp(GoalExp node) { }
	public void preHypDesigExp(HypDesigExp node) { }
	public void midHypDesigExp(HypDesigExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postHypDesigExp(HypDesigExp node) { }
	public void preIfExp(IfExp node) { }
	public void midIfExp(IfExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postIfExp(IfExp node) { }
	public void preIfStmt(IfStmt node) { }
	public void midIfStmt(IfStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postIfStmt(IfStmt node) { }
	public void preInfixExp(InfixExp node) { }
	public void midInfixExp(InfixExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postInfixExp(InfixExp node) { }
	public void preInitItem(InitItem node) { }
	public void midInitItem(InitItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postInitItem(InitItem node) { }
	public void preIntegerExp(IntegerExp node) { }
	public void midIntegerExp(IntegerExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postIntegerExp(IntegerExp node) { }
	public void preIsInExp(IsInExp node) { }
	public void midIsInExp(IsInExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postIsInExp(IsInExp node) { }
	public void preIterateExitStmt(IterateExitStmt node) { }
	public void midIterateExitStmt(IterateExitStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postIterateExitStmt(IterateExitStmt node) { }
	public void preIterateStmt(IterateStmt node) { }
	public void midIterateStmt(IterateStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postIterateStmt(IterateStmt node) { }
	public void preIterativeExp(IterativeExp node) { }
	public void midIterativeExp(IterativeExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postIterativeExp(IterativeExp node) { }
	public void preJustificationExp(JustificationExp node) { }
	public void midJustificationExp(JustificationExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postJustificationExp(JustificationExp node) { }
	public void preJustifiedExp(JustifiedExp node) { }
	public void midJustifiedExp(JustifiedExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postJustifiedExp(JustifiedExp node) { }
	public void preLambdaExp(LambdaExp node) { }
	public void midLambdaExp(LambdaExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postLambdaExp(LambdaExp node) { }
	public void preLineNumberedExp(LineNumberedExp node) { }
	public void midLineNumberedExp(LineNumberedExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postLineNumberedExp(LineNumberedExp node) { }
	public void preMathAssertionDec(MathAssertionDec node) { }
	public void midMathAssertionDec(MathAssertionDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postMathAssertionDec(MathAssertionDec node) { }
	public void preMathModuleDec(MathModuleDec node) { }
	public void midMathModuleDec(MathModuleDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postMathModuleDec(MathModuleDec node) { }
	public void preMathRefExp(MathRefExp node) { }
	public void midMathRefExp(MathRefExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postMathRefExp(MathRefExp node) { }
	public void preMathTypeDec(MathTypeDec node) { }
	public void midMathTypeDec(MathTypeDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postMathTypeDec(MathTypeDec node) { }
	public void preMathTypeFormalDec(MathTypeFormalDec node) { }
	public void midMathTypeFormalDec(MathTypeFormalDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postMathTypeFormalDec(MathTypeFormalDec node) { }
	public void preMathVarDec(MathVarDec node) { }
	public void midMathVarDec(MathVarDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postMathVarDec(MathVarDec node) { }
	public void preMemoryStmt(MemoryStmt node) { }
	public void midMemoryStmt(MemoryStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postMemoryStmt(MemoryStmt node) { }
	public void preModuleArgumentItem(ModuleArgumentItem node) { }
	public void midModuleArgumentItem(ModuleArgumentItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postModuleArgumentItem(ModuleArgumentItem node) { }
	public void preModuleDec(ModuleDec node) { }
	public void midModuleDec(ModuleDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postModuleDec(ModuleDec node) { }
	public void preNameTy(NameTy node) { }
	public void midNameTy(NameTy node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postNameTy(NameTy node) { }
	public void preOldExp(OldExp node) { }
	public void midOldExp(OldExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postOldExp(OldExp node) { }
	public void preOperationDec(OperationDec node) { }
	public void midOperationDec(OperationDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postOperationDec(OperationDec node) { }
	public void preOutfixExp(OutfixExp node) { }
	public void midOutfixExp(OutfixExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postOutfixExp(OutfixExp node) { }
	public void preParameterVarDec(ParameterVarDec node) { }
	public void midParameterVarDec(ParameterVarDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postParameterVarDec(ParameterVarDec node) { }
	public void prePerformanceFinalItem(PerformanceFinalItem node) { }
	public void midPerformanceFinalItem(PerformanceFinalItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postPerformanceFinalItem(PerformanceFinalItem node) { }
	public void prePerformanceInitItem(PerformanceInitItem node) { }
	public void midPerformanceInitItem(PerformanceInitItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postPerformanceInitItem(PerformanceInitItem node) { }
	public void prePerformanceModuleDec(PerformanceModuleDec node) { }
	public void midPerformanceModuleDec(PerformanceModuleDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postPerformanceModuleDec(PerformanceModuleDec node) { }
	public void prePerformanceOperationDec(PerformanceOperationDec node) { }
	public void midPerformanceOperationDec(PerformanceOperationDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postPerformanceOperationDec(PerformanceOperationDec node) { }
	public void prePerformanceTypeDec(PerformanceTypeDec node) { }
	public void midPerformanceTypeDec(PerformanceTypeDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postPerformanceTypeDec(PerformanceTypeDec node) { }
	public void prePrefixExp(PrefixExp node) { }
	public void midPrefixExp(PrefixExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postPrefixExp(PrefixExp node) { }
	public void preProcedureDec(ProcedureDec node) { }
	public void midProcedureDec(ProcedureDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProcedureDec(ProcedureDec node) { }
	public void preProgramCharExp(ProgramCharExp node) { }
	public void midProgramCharExp(ProgramCharExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProgramCharExp(ProgramCharExp node) { }
	public void preProgramDotExp(ProgramDotExp node) { }
	public void midProgramDotExp(ProgramDotExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProgramDotExp(ProgramDotExp node) { }
	public void preProgramDoubleExp(ProgramDoubleExp node) { }
	public void midProgramDoubleExp(ProgramDoubleExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProgramDoubleExp(ProgramDoubleExp node) { }
	public void preProgramExp(ProgramExp node) { }
	public void midProgramExp(ProgramExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProgramExp(ProgramExp node) { }
	public void preProgramFunctionExp(ProgramFunctionExp node) { }
	public void midProgramFunctionExp(ProgramFunctionExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProgramFunctionExp(ProgramFunctionExp node) { }
	public void preProgramIntegerExp(ProgramIntegerExp node) { }
	public void midProgramIntegerExp(ProgramIntegerExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProgramIntegerExp(ProgramIntegerExp node) { }
	public void preProgramOpExp(ProgramOpExp node) { }
	public void midProgramOpExp(ProgramOpExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProgramOpExp(ProgramOpExp node) { }
	public void preProgramParamExp(ProgramParamExp node) { }
	public void midProgramParamExp(ProgramParamExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProgramParamExp(ProgramParamExp node) { }
	public void preProgramStringExp(ProgramStringExp node) { }
	public void midProgramStringExp(ProgramStringExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProgramStringExp(ProgramStringExp node) { }
	public void preProofDec(ProofDec node) { }
	public void midProofDec(ProofDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProofDec(ProofDec node) { }
	public void preProofDefinitionExp(ProofDefinitionExp node) { }
	public void midProofDefinitionExp(ProofDefinitionExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProofDefinitionExp(ProofDefinitionExp node) { }
	public void preProofModuleDec(ProofModuleDec node) { }
	public void midProofModuleDec(ProofModuleDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postProofModuleDec(ProofModuleDec node) { }
	public void preQuantExp(QuantExp node) { }
	public void midQuantExp(QuantExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postQuantExp(QuantExp node) { }
	public void preRealizationParamDec(RealizationParamDec node) { }
	public void midRealizationParamDec(RealizationParamDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postRealizationParamDec(RealizationParamDec node) { }
	public void preRecordTy(RecordTy node) { }
	public void midRecordTy(RecordTy node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postRecordTy(RecordTy node) { }
	public void preRenamingItem(RenamingItem node) { }
	public void midRenamingItem(RenamingItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postRenamingItem(RenamingItem node) { }
	public void preRepresentationDec(RepresentationDec node) { }
	public void midRepresentationDec(RepresentationDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postRepresentationDec(RepresentationDec node) { }
	public void preResolveConceptualElement(ResolveConceptualElement node) { }
	public void midResolveConceptualElement(ResolveConceptualElement node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postResolveConceptualElement(ResolveConceptualElement node) { }
	public void preSelectionStmt(SelectionStmt node) { }
	public void midSelectionStmt(SelectionStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postSelectionStmt(SelectionStmt node) { }
	public void preSetExp(SetExp node) { }
	public void midSetExp(SetExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postSetExp(SetExp node) { }
	public void preShortFacilityModuleDec(ShortFacilityModuleDec node) { }
	public void midShortFacilityModuleDec(ShortFacilityModuleDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postShortFacilityModuleDec(ShortFacilityModuleDec node) { }
	public void preStatement(Statement node) { }
	public void midStatement(Statement node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postStatement(Statement node) { }
	public void preStringExp(StringExp node) { }
	public void midStringExp(StringExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postStringExp(StringExp node) { }
	public void preSubtypeDec(SubtypeDec node) { }
	public void midSubtypeDec(SubtypeDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postSubtypeDec(SubtypeDec node) { }
	public void preSuppositionDeductionExp(SuppositionDeductionExp node) { }
	public void midSuppositionDeductionExp(SuppositionDeductionExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postSuppositionDeductionExp(SuppositionDeductionExp node) { }
	public void preSuppositionExp(SuppositionExp node) { }
	public void midSuppositionExp(SuppositionExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postSuppositionExp(SuppositionExp node) { }
	public void preSwapStmt(SwapStmt node) { }
	public void midSwapStmt(SwapStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postSwapStmt(SwapStmt node) { }
	public void preTupleExp(TupleExp node) { }
	public void midTupleExp(TupleExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postTupleExp(TupleExp node) { }
	public void preTupleTy(TupleTy node) { }
	public void midTupleTy(TupleTy node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postTupleTy(TupleTy node) { }
	public void preTy(Ty node) { }
	public void midTy(Ty node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postTy(Ty node) { }
	public void preTypeDec(TypeDec node) { }
	public void midTypeDec(TypeDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postTypeDec(TypeDec node) { }
	public void preTypeFunctionExp(TypeFunctionExp node) { }
	public void midTypeFunctionExp(TypeFunctionExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postTypeFunctionExp(TypeFunctionExp node) { }
	public void preUnaryMinusExp(UnaryMinusExp node) { }
	public void midUnaryMinusExp(UnaryMinusExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postUnaryMinusExp(UnaryMinusExp node) { }
	public void preUsesItem(UsesItem node) { }
	public void midUsesItem(UsesItem node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postUsesItem(UsesItem node) { }
	public void preVarDec(VarDec node) { }
	public void midVarDec(VarDec node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postVarDec(VarDec node) { }
	public void preVarExp(VarExp node) { }
	public void midVarExp(VarExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postVarExp(VarExp node) { }
	public void preVariableArrayExp(VariableArrayExp node) { }
	public void midVariableArrayExp(VariableArrayExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postVariableArrayExp(VariableArrayExp node) { }
	public void preVariableDotExp(VariableDotExp node) { }
	public void midVariableDotExp(VariableDotExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postVariableDotExp(VariableDotExp node) { }
	public void preVariableExp(VariableExp node) { }
	public void midVariableExp(VariableExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postVariableExp(VariableExp node) { }
	public void preVariableNameExp(VariableNameExp node) { }
	public void midVariableNameExp(VariableNameExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postVariableNameExp(VariableNameExp node) { }
	public void preVariableRecordExp(VariableRecordExp node) { }
	public void midVariableRecordExp(VariableRecordExp node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postVariableRecordExp(VariableRecordExp node) { }
	public void preWhileStmt(WhileStmt node) { }
	public void midWhileStmt(WhileStmt node, ResolveConceptualElement prevChild, ResolveConceptualElement nextChild) { }
	public void postWhileStmt(WhileStmt node) { }
}