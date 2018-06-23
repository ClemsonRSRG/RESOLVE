/*
 * ResolveConceptualVisitor.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

public class ResolveConceptualVisitor {

    public void visitCrossTypeExpression(CrossTypeExpression data) {}

    public void visitArbitraryExpTy(ArbitraryExpTy data) {}

    public void visitImplicitTypeParameterExp(TypeAssertionExp data) {}

    public void visitAssumeStmt(AssumeStmt data) {}

    public void visitPressumeStmt(PresumeStmt data) {}

    public void visitAuxCodeStmt(AuxCodeStmt data) {}

    public void visitDoubleExp(DoubleExp data) {}

    public void visitBetweenExp(BetweenExp data) {}

    public void visitAlternativeExp(AlternativeExp data) {}

    public void visitAuxVarDec(AuxVarDec data) {}

    public void visitProgramFunctionExp(ProgramFunctionExp data) {}

    public void visitFinalItem(FinalItem data) {}

    public void visitCharExp(CharExp data) {}

    public void visitConfirmStmt(ConfirmStmt data) {}

    public void visitTupleTy(TupleTy data) {}

    public void visitConceptTypeParamDec(ConceptTypeParamDec data) {}

    public void visitIfStmt(IfStmt data) {}

    public void visitOperationDec(OperationDec data) {}

    public void visitDotExp(DotExp data) {}

    public void visitPrefixExp(PrefixExp data) {}

    public void visitFunctionExp(FunctionExp data) {}

    public void visitOldExp(OldExp data) {}

    public void visitEnhancementBodyModuleDec(EnhancementBodyModuleDec data) {}

    public void visitProgramDoubleExp(ProgramDoubleExp data) {}

    public void visitMemoryStmt(MemoryStmt data) {}

    public void visitWhileStmt(WhileStmt data) {}

    public void visitVarDec(VarDec data) {}

    public void visitIterateExitStmt(IterateExitStmt data) {}

    public void visitIntegerExp(IntegerExp data) {}

    public void visitCartProdTy(CartProdTy data) {}

    public void visitRealizationParamDec(RealizationParamDec data) {}

    public void visitMathTypeDec(MathTypeDec data) {}

    public void visitSwapStmt(SwapStmt data) {}

    public void visitFacilityTypeDec(FacilityTypeDec data) {}

    public void visitProgramDotExp(ProgramDotExp data) {}

    public void visitParameterVarDec(ParameterVarDec data) {}

    public void visitEqualsExp(EqualsExp data) {}

    public void visitIsInExp(IsInExp data) {}

    public void visitConceptBodyModuleDec(ConceptBodyModuleDec data) {}

    public void visitProgramIntegerExp(ProgramIntegerExp data) {}

    public void visitProgramCharExp(ProgramCharExp data) {}

    public void visitProgramStringExp(ProgramStringExp data) {}

    public void visitQuantExp(QuantExp data) {}

    public void visitStringExp(StringExp data) {}

    public void visitDefinitionDec(DefinitionDec data) {}

    public void visitLambdaExp(LambdaExp data) {}

    public void visitFacilityDec(FacilityDec data) {}

    public void visitArrayTy(ArrayTy data) {}

    public void visitIterativeExp(IterativeExp data) {}

    public void visitSetExp(SetExp data) {}

    public void visitSetCollectionExp(SetCollectionExp data) {}

    public void visitMathVarDec(MathVarDec data) {}

    public void visitVariableNameExp(VariableNameExp data) {}

    public void visitIterateStmt(IterateStmt data) {}

    public void visitSelectionStmt(SelectionStmt data) {}

    public void visitVarExp(VarExp data) {}

    public void visitFunctionArgList(FunctionArgList data) {}

    public void visitProgramParamExp(ProgramParamExp data) {}

    public void visitFacilityOperationDec(FacilityOperationDec data) {}

    public void visitConstantParamDec(ConstantParamDec data) {}

    public void visitAltItemExp(AltItemExp data) {}

    public void visitTypeFunctionExp(TypeFunctionExp data) {}

    public void visitModuleArgumentItem(ModuleArgumentItem data) {}

    public void visitAffectsItem(AffectsItem data) {}

    public void visitVariableRecordExp(VariableRecordExp data) {}

    public void visitMathTypeFormalDec(MathTypeFormalDec data) {}

    public void visitChoiceItem(ChoiceItem data) {}

    public void visitEnhancementModuleDec(EnhancementModuleDec data) {}

    public void visitFieldExp(FieldExp data) {}

    public void visitNameTy(NameTy data) {}

    public void visitBooleanTy(BooleanTy data) {}

    public void visitVariableDotExp(VariableDotExp data) {}

    public void visitUnaryMinusExp(UnaryMinusExp data) {}

    public void visitTupleExp(TupleExp data) {}

    public void visitCallStmt(CallStmt data) {}

    public void visitEnhancementBodyItem(EnhancementBodyItem data) {}

    public void visitOutfixExp(OutfixExp data) {}

    public void visitProcedureDec(ProcedureDec data) {}

    public void visitRenamingItem(RenamingItem data) {}

    public void visitMathAssertionDec(MathAssertionDec data) {}

    public void visitRepresentationDec(RepresentationDec data) {}

    public void visitConceptModuleDec(ConceptModuleDec data) {}

    public void visitFacilityModuleDec(FacilityModuleDec data) {}

    public void visitConditionItem(ConditionItem data) {}

    public void visitUsesItem(UsesItem data) {}

    public void visitFuncAssignStmt(FuncAssignStmt data) {}

    public void visitEnhancementItem(EnhancementItem data) {}

    public void visitInitItem(InitItem data) {}

    public void visitShortFacilityModuleDec(ShortFacilityModuleDec data) {}

    public void visitVariableArrayExp(VariableArrayExp data) {}

    public void visitMathModuleDec(MathModuleDec data) {}

    public void visitConstructedTy(ConstructedTy data) {}

    public void visitTypeDec(TypeDec data) {}

    public void visitFunctionTy(FunctionTy data) {}

    public void visitInfixExp(InfixExp data) {}

    public void visitIfExp(IfExp data) {}

    public void visitRecordTy(RecordTy data) {}

    public void visitGoalExp(GoalExp data) {}

    public void visitSuppositionExp(SuppositionExp data) {}

    public void visitDeductionExp(DeductionExp data) {}

    public void visitSuppositionDeductionExp(SuppositionDeductionExp data) {}

    public void visitProofDec(ProofDec data) {}

    public void visitProofDefinitionExp(ProofDefinitionExp data) {}

    public void visitProofModuleDec(ProofModuleDec data) {}

    public void visitJustifiedExp(JustifiedExp data) {}

    public void visitJustificationExp(JustificationExp data) {}

    public void visitHypDesigExp(HypDesigExp data) {}

    public void visitMathRefExp(MathRefExp data) {}

    public void visitSubtypeDec(SubtypeDec data) {}

    public void visitPerformanceFinalItem(PerformanceFinalItem data) {}

    public void visitPerformanceInitItem(PerformanceInitItem data) {}

    public void visitPerformanceOperationDec(PerformanceOperationDec data) {}

    public void visitPerformanceTypeDec(PerformanceTypeDec data) {}

    public void visitPerformanceEModuleDec(PerformanceEModuleDec data) {}

    public void visitPerformanceCModuleDec(PerformanceCModuleDec data) {}
}
