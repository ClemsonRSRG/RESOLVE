/**
 * SyntacticSugarConverter.java
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
package edu.clemson.cs.rsrg.parsing.utilities;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.VirtualListNode;
import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationProcedureDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.ProcedureDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.FacilityTypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.AbstractVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.AbstractFunctionExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.items.mathitems.LoopVerificationItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.FacilityTypeInitFinalItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.IfConditionItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.TypeInitFinalItem;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.absyn.statements.*;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.errorhandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import java.util.*;

/**
 * <p>This class performs the various different syntactic sugar conversions
 * using part of the RESOLVE abstract syntax tree. This visitor logic is
 * implemented as a {@link TreeWalkerVisitor}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SyntacticSugarConverter extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>This map provides a mapping between the newly declared array name types
     * to the types of elements in the array.</p>
     */
    private final Map<NameTy, NameTy> myArrayNameTyToInnerTyMap;

    /**
     * <p>Once we are done walking the tree, the top most node will create
     * the new element.</p>
     */
    private ResolveConceptualElement myFinalProcessedElement;

    /**
     * <p>Since we don't have symbol table, we really don't know if
     * we are generating a new object with the same name. In order to avoid
     * problems, all of our objects will have a name that starts with "_" and
     * end the current new element counter. This number increases by 1 each
     * time we create a new element.</p>
     */
    private int myNewElementCounter;

    /**
     * <p>This stores the new statements created by current statement
     * we are visiting.</p>
     */
    private NewStatementsContainer myNewStatementsContainer;

    /**
     * <p>This stores the {@link ParameterVarDec}, {@link FacilityDec} and
     * {@link VarDec} obtained from the parent node.</p>
     */
    private ParentNodeElementsContainer myParentNodeElementsContainer;

    /**
     * <p>This is a map from the original {@link ResolveConceptualElement} to
     * the replacing {@link ResolveConceptualElement}.</p>
     */
    private final Map<ResolveConceptualElement, ResolveConceptualElement> myReplacingElementsMap;

    /**
     * <p>Once we are done walking an element that could have a syntactic
     * sugar conversion, we add that element into the top-most collector.</p>
     *
     * <p>When we are done walking a {@link ResolveConceptualElement} that
     * can contain a list of {@link Statement}s, we build a new instance
     * of the object using the elements in the collector.</p>
     */
    private Stack<ResolveConceptualElementCollector> myResolveElementCollectorStack;

    // ===========================================================
    // Constructors
    // ===========================================================

    // TODO: Javadocs here!
    public SyntacticSugarConverter(Map<NameTy, NameTy> arrayNameTyToInnerTyMap,
            int newElementCounter) {
        myArrayNameTyToInnerTyMap = arrayNameTyToInnerTyMap;
        myFinalProcessedElement = null;
        myNewElementCounter = newElementCounter;
        myReplacingElementsMap = new HashMap<>();
        myResolveElementCollectorStack = new Stack<>();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Parent Nodes
    // -----------------------------------------------------------

    /**
     * <p>This should be the top-most node that we start with
     * when processing syntactic sugar conversions inside a
     * {@link FacilityTypeInitFinalItem}.</p>
     *
     * @param e Current {@link FacilityTypeInitFinalItem} we are visiting.
     */
    @Override
    public void preFacilityTypeInitFinalItem(FacilityTypeInitFinalItem e) {
        // Store the params, facility and variable declarations
        myParentNodeElementsContainer =
                new ParentNodeElementsContainer(
                        new ArrayList<ParameterVarDec>(), e.getFacilities(), e
                                .getVariables());

        // Create a new collector
        myResolveElementCollectorStack
                .push(new ResolveConceptualElementCollector(e));
    }

    /**
     * <p>This should be the last element we walk. All syntactic
     * sugar should have been performed and we can safely create
     * the new {@link FacilityTypeInitFinalItem} to be returned.</p>
     *
     * @param e Current {@link FacilityTypeInitFinalItem} we are visiting.
     */
    @Override
    public void postFacilityTypeInitFinalItem(FacilityTypeInitFinalItem e) {
        // Affects clause (if any)
        AffectsClause affectsClause = null;
        if (e.getAffectedVars() != null) {
            affectsClause = e.getAffectedVars().clone();
        }

        // Build the new ProcedureDec
        ResolveConceptualElementCollector collector =
                myResolveElementCollectorStack.pop();
        myFinalProcessedElement =
                new FacilityTypeInitFinalItem(new Location(e.getLocation()), e
                        .getItemType(), affectsClause, e.getRequires().clone(),
                        e.getEnsures().clone(),
                        myParentNodeElementsContainer.facilityDecs,
                        myParentNodeElementsContainer.varDecs, collector.stmts);

        // Just in case
        myParentNodeElementsContainer = null;
    }

    /**
     * <p>This should be the top-most node that we start with
     * when processing syntactic sugar conversions inside a
     * {@link OperationProcedureDec}.</p>
     *
     * @param e Current {@link OperationProcedureDec} we are visiting.
     */
    @Override
    public void preOperationProcedureDec(OperationProcedureDec e) {
        // Store the params, facility and variable declarations
        myParentNodeElementsContainer =
                new ParentNodeElementsContainer(e.getWrappedOpDec()
                        .getParameters(), e.getFacilities(), e.getVariables());

        // Create a new collector
        myResolveElementCollectorStack
                .push(new ResolveConceptualElementCollector(e));
    }

    /**
     * <p>This should be the last element we walk. All syntactic
     * sugar should have been performed and we can safely create
     * the new {@link OperationProcedureDec} to be returned.</p>
     *
     * @param e Current {@link OperationProcedureDec} we are visiting.
     */
    @Override
    public void postOperationProcedureDec(OperationProcedureDec e) {
        // Get the original OperationDec
        OperationDec opDec = e.getWrappedOpDec();

        // Return type (if any)
        Ty returnTy = null;
        if (opDec.getReturnTy() != null) {
            returnTy = opDec.getReturnTy().clone();
        }

        // Affects clause (if any)
        AffectsClause affectsClause = null;
        if (opDec.getAffectedVars() != null) {
            affectsClause = opDec.getAffectedVars().clone();
        }

        // New OperationDec
        OperationDec newOperationDec =
                new OperationDec(opDec.getName().clone(),
                        myParentNodeElementsContainer.parameterVarDecs,
                        returnTy, affectsClause, opDec.getRequires().clone(),
                        opDec.getEnsures().clone());

        // Decreasing clause (if any)
        AssertionClause decreasingClause = null;
        boolean recursiveFlag = false;
        if (e.getDecreasing() != null) {
            decreasingClause = e.getDecreasing().clone();
            recursiveFlag = true;
        }

        // Build the new OperationProcedureDec
        ResolveConceptualElementCollector collector =
                myResolveElementCollectorStack.pop();
        myFinalProcessedElement =
                new OperationProcedureDec(newOperationDec, decreasingClause,
                        myParentNodeElementsContainer.facilityDecs,
                        myParentNodeElementsContainer.varDecs, collector.stmts,
                        recursiveFlag);

        // Just in case
        myParentNodeElementsContainer = null;
    }

    /**
     * <p>This should be the top-most node that we start with
     * when processing syntactic sugar conversions inside a
     * {@link ProcedureDec}.</p>
     *
     * @param e Current {@link ProcedureDec} we are visiting.
     */
    @Override
    public void preProcedureDec(ProcedureDec e) {
        // Store the params, facility and variable declarations
        myParentNodeElementsContainer =
                new ParentNodeElementsContainer(e.getParameters(), e
                        .getFacilities(), e.getVariables());

        // Create a new collector
        myResolveElementCollectorStack
                .push(new ResolveConceptualElementCollector(e));
    }

    /**
     * <p>This should be the last element we walk. All syntactic
     * sugar should have been performed and we can safely create
     * the new {@link ProcedureDec} to be returned.</p>
     *
     * @param e Current {@link ProcedureDec} we are visiting.
     */
    @Override
    public void postProcedureDec(ProcedureDec e) {
        // Return type (if any)
        Ty returnTy = null;
        if (e.getReturnTy() != null) {
            returnTy = e.getReturnTy().clone();
        }

        // Affects clause (if any)
        AffectsClause affectsClause = null;
        if (e.getAffectedVars() != null) {
            affectsClause = e.getAffectedVars().clone();
        }

        // Decreasing clause (if any)
        AssertionClause decreasingClause = null;
        boolean recursiveFlag = false;
        if (e.getDecreasing() != null) {
            decreasingClause = e.getDecreasing().clone();
            recursiveFlag = true;
        }

        // Build the new ProcedureDec
        ResolveConceptualElementCollector collector =
                myResolveElementCollectorStack.pop();
        myFinalProcessedElement =
                new ProcedureDec(e.getName().clone(),
                        myParentNodeElementsContainer.parameterVarDecs,
                        returnTy, affectsClause, decreasingClause,
                        myParentNodeElementsContainer.facilityDecs,
                        myParentNodeElementsContainer.varDecs, collector.stmts,
                        recursiveFlag);

        // Just in case
        myParentNodeElementsContainer = null;
    }

    /**
     * <p>This should be the top-most node that we start with
     * when processing syntactic sugar conversions inside a
     * {@link TypeInitFinalItem}.</p>
     *
     * @param e Current {@link TypeInitFinalItem} we are visiting.
     */
    @Override
    public void preTypeInitFinalItem(TypeInitFinalItem e) {
        // Store the params, facility and variable declarations
        myParentNodeElementsContainer =
                new ParentNodeElementsContainer(
                        new ArrayList<ParameterVarDec>(), e.getFacilities(), e
                                .getVariables());

        // Create a new collector
        myResolveElementCollectorStack
                .push(new ResolveConceptualElementCollector(e));
    }

    /**
     * <p>This should be the last element we walk. All syntactic
     * sugar should have been performed and we can safely create
     * the new {@link TypeInitFinalItem} to be returned.</p>
     *
     * @param e Current {@link TypeInitFinalItem} we are visiting.
     */
    @Override
    public void postTypeInitFinalItem(TypeInitFinalItem e) {
        // Affects clause (if any)
        AffectsClause affectsClause = null;
        if (e.getAffectedVars() != null) {
            affectsClause = e.getAffectedVars().clone();
        }

        // Build the new ProcedureDec
        ResolveConceptualElementCollector collector =
                myResolveElementCollectorStack.pop();
        myFinalProcessedElement =
                new TypeInitFinalItem(new Location(e.getLocation()), e
                        .getItemType(), affectsClause,
                        myParentNodeElementsContainer.facilityDecs,
                        myParentNodeElementsContainer.varDecs, collector.stmts);

        // Just in case
        myParentNodeElementsContainer = null;
    }

    // -----------------------------------------------------------
    // Statement Nodes
    // -----------------------------------------------------------

    /**
     * <p>This statement could have syntactic sugar conversions, so
     * we will need to have a way to store the new {@link Statement}s that get
     * generated.</p>
     *
     * @param e Current {@link CallStmt} we are visiting.
     */
    @Override
    public void preCallStmt(CallStmt e) {
        myNewStatementsContainer = new NewStatementsContainer();
    }

    /**
     * <p>This statement could have syntactic sugar conversions, so it checks to see
     * if we have generated new {@link Statement}s and place those in the appropriate
     * location.</p>
     *
     * @param e Current {@link CallStmt} we are visiting.
     */
    @Override
    public void postCallStmt(CallStmt e) {
        // Add any statements that need to appear before this one.
        while (!myNewStatementsContainer.newPreStmts.empty()) {
            addToInnerMostCollector(myNewStatementsContainer.newPreStmts.pop());
        }

        // Rebuild the new call statement with the new args (if necessary)
        ProgramFunctionExp exp;
        if (myReplacingElementsMap.containsKey(e.getFunctionExp())) {
            exp =
                    (ProgramFunctionExp) myReplacingElementsMap.remove(e
                            .getFunctionExp());
        }
        else {
            exp = (ProgramFunctionExp) e.getFunctionExp().clone();
        }
        addToInnerMostCollector(new CallStmt(new Location(e.getLocation()), exp));

        // Add any statements that need to appear after this one.
        while (!myNewStatementsContainer.newPostStmts.isEmpty()) {
            addToInnerMostCollector(myNewStatementsContainer.newPostStmts
                    .remove());
        }

        myNewStatementsContainer = null;
    }

    /**
     * <p>This statement doesn't need to do any syntactic sugar conversions,
     * therefore we create a new {@link ConfirmStmt} for future use.</p>
     *
     * @param e Current {@link ConfirmStmt} we are visiting.
     */
    @Override
    public void postConfirmStmt(ConfirmStmt e) {
        addToInnerMostCollector(e.clone());
    }

    /**
     * <p>This statement could have syntactic sugar conversions, so
     * we will need to have a way to store the new {@link Statement}s that get
     * generated.</p>
     *
     * @param e Current {@link FuncAssignStmt} we are visiting.
     */
    @Override
    public void preFuncAssignStmt(FuncAssignStmt e) {
        myNewStatementsContainer = new NewStatementsContainer();
    }

    /**
     * <p>This statement could have syntactic sugar conversions, so it checks to see
     * if we have generated new {@link Statement}s and place those in the appropriate
     * location.</p>
     *
     * @param e Current {@link FuncAssignStmt} we are visiting.
     */
    @Override
    public void postFuncAssignStmt(FuncAssignStmt e) {
        // Add any statements that need to appear before this one.
        while (!myNewStatementsContainer.newPreStmts.empty()) {
            addToInnerMostCollector(myNewStatementsContainer.newPreStmts.pop());
        }

        // Build the various different "function assignment" statements.
        Location l = e.getLocation();
        ProgramVariableExp leftExp = e.getVariableExp();
        ProgramExp rightExp = e.getAssignExp();

        // Boolean that indicates whether or not the expressions are
        // some kind of ProgramVariableArrayExp
        boolean isLeftArrayExp =
                ArrayConversionUtilities.isProgArrayExp(leftExp);
        boolean isRightArrayExp =
                ArrayConversionUtilities.isProgArrayExp(rightExp);

        // Case #1: Left expression is not an expression that is a ProgramVariableArrayExp,
        // but the right is a ProgramVariableArrayExp.
        // (ie: x := A[i], where "A" is an array, "i" is
        // index and "x" is a variable.)
        Statement newStatement;
        if (!isLeftArrayExp && isRightArrayExp) {
            // Obtain the array type, name and index
            ProgramVariableExp arrayNameExp =
                    ArrayConversionUtilities.getArrayNameExp(rightExp);
            ProgramExp arrayIndexExp =
                    ArrayConversionUtilities.getArrayIndexExp(rightExp);
            NameTy arrayTy = findArrayType(arrayNameExp);

            newStatement = new FuncAssignStmt(new Location(l), (ProgramVariableExp) leftExp.clone(),
                    ArrayConversionUtilities.buildEntryReplicaCall(l, arrayTy.getQualifier(),
                            arrayNameExp, arrayIndexExp));
        }
        // Case #2: Right expression is not an expression that is a ProgramVariableArrayExp,
        // but the left is a ProgramVariableArrayExp.
        // (ie: A[i] := x, where "A" is an array, "i" is
        // index and "x" is a variable.)
        else if (isLeftArrayExp && !isRightArrayExp) {
            // Obtain the array type, name and index
            ProgramVariableExp arrayNameExp =
                    ArrayConversionUtilities.getArrayNameExp(leftExp);
            ProgramExp arrayIndexExp =
                    ArrayConversionUtilities.getArrayIndexExp(leftExp);
            NameTy arrayTy = findArrayType(arrayNameExp);

            newStatement = ArrayConversionUtilities.buildAssignEntryCall(new Location(l),
                    rightExp.clone(), arrayTy.getQualifier(), arrayNameExp, arrayIndexExp);
        }
        // Case #3: Both left and right expressions are ProgramVariableArrayExp
        // expressions.
        // Note: They can be array expressions from different arrays
        // (ie: A[i] := B[j], where "A" is an array and
        // "i" and "j" are indexes)
        else if (isLeftArrayExp && isRightArrayExp) {
            // Obtain the array type, name and index
            ProgramVariableExp leftArrayNameExp =
                    ArrayConversionUtilities.getArrayNameExp(leftExp);
            ProgramVariableExp rightArrayNameExp =
                    ArrayConversionUtilities.getArrayNameExp(rightExp);
            ProgramExp leftArrayIndexExp =
                    ArrayConversionUtilities.getArrayIndexExp(leftExp);
            ProgramExp rightArrayIndexExp =
                    ArrayConversionUtilities.getArrayIndexExp(rightExp);
            NameTy leftArrayTy = findArrayType(leftArrayNameExp);
            NameTy rightArrayTy = findArrayType(rightArrayNameExp);

            ProgramFunctionExp newEntryReplicaCall = ArrayConversionUtilities.buildEntryReplicaCall(l,
                    rightArrayTy.getQualifier(), rightArrayNameExp, rightArrayIndexExp);
            newStatement = ArrayConversionUtilities.buildAssignEntryCall(new Location(l),
                    newEntryReplicaCall, leftArrayTy.getQualifier(), leftArrayNameExp, leftArrayIndexExp);
        }
        // Case #4: If it is not cases 1-4, then we build a regular
        // FuncAssignStmt.
        else {
            ProgramExp newRightExp;
            if (rightExp instanceof ProgramFunctionExp) {
                if (myReplacingElementsMap.containsKey(rightExp)) {
                    newRightExp = (ProgramExp) myReplacingElementsMap.remove(rightExp);
                }
                else {
                    newRightExp = rightExp.clone();
                }
            }
            else {
                // Add a call to "Replica"
                List<ProgramExp> args = new ArrayList<>();
                args.add(rightExp.clone());

                newRightExp = new ProgramFunctionExp(new Location(rightExp.getLocation()),
                        null, new PosSymbol(new Location(rightExp.getLocation()), "Replica"), args);
            }

            newStatement = new FuncAssignStmt(new Location(l),
                    (ProgramVariableExp) leftExp.clone(), newRightExp);
        }
        addToInnerMostCollector(newStatement);

        // Add any statements that need to appear after this one.
        while (!myNewStatementsContainer.newPostStmts.isEmpty()) {
            addToInnerMostCollector(myNewStatementsContainer.newPostStmts
                    .remove());
        }

        myNewStatementsContainer = null;
    }

    /**
     * <p>This statement could have syntactic sugar conversions, so
     * we will need to have a way to store the new {@link Statement}s that get
     * generated.</p>
     *
     * @param e Current {@link IfStmt} we are visiting.
     */
    @Override
    public void preIfStmt(IfStmt e) {
        // We have began a new block that can contain statements,
        // we need to store this in our stack.
        myResolveElementCollectorStack
                .push(new ResolveConceptualElementCollector(e));
    }

    /**
     * <p>We are done visiting this node, therefore we create a
     * new {@link IfStmt} for future use.</p>
     *
     * @param e Current {@link IfStmt} we are visiting.
     */
    @Override
    public void postIfStmt(IfStmt e) {
        // Done visiting this IfStmt, so we can pop it off the stack.
        ResolveConceptualElementCollector collector =
                myResolveElementCollectorStack.pop();

        // Check we got the right collector
        if (!collector.instantiatingElement.equals(e)) {
            throw new MiscErrorException(
                    "Something went wrong during the syntactic sugar conversion",
                    new IllegalStateException());
        }

        // Retrieve the new IfConditionItem
        IfConditionItem newIfConditionItem = (IfConditionItem) myReplacingElementsMap.remove(e.getIfClause());

        // Retrieve all the Else-If's IfConditionItems
        // Note: Right now there shouldn't be any.
        List<IfConditionItem> newElseIfItems = new ArrayList<>();
        List<IfConditionItem> elseIfPairs = e.getElseifpairs();
        for (IfConditionItem item : elseIfPairs) {
            newElseIfItems.add((IfConditionItem) myReplacingElementsMap.remove(item));
        }

        // Build the new IfStmt and add it to the 'next' collector.
        addToInnerMostCollector(new IfStmt(new Location(e.getLocation()),
                newIfConditionItem, newElseIfItems, collector.stmts));
    }

    /**
     * <p>This statement doesn't need to do any syntactic sugar conversions,
     * therefore we create a new {@link MemoryStmt} for future use.</p>
     *
     * @param e Current {@link MemoryStmt} we are visiting.
     */
    @Override
    public void postMemoryStmt(MemoryStmt e) {
        addToInnerMostCollector(e.clone());
    }

    /**
     * <p>This statement doesn't need to do any syntactic sugar conversions,
     * therefore we create a new {@link PresumeStmt} for future use.</p>
     *
     * @param e Current {@link PresumeStmt} we are visiting.
     */
    @Override
    public void postPresumeStmt(PresumeStmt e) {
        addToInnerMostCollector(e.clone());
    }

    /**
     * <p>This statement could have syntactic sugar conversions, so it generates
     * the appropriate "swap" call.</p>
     *
     * @param e Current {@link SwapStmt} we are visiting.
     */
    @Override
    public void postSwapStmt(SwapStmt e) {
        // Build the various different "swap" statements.
        Location l = e.getLocation();
        ProgramVariableExp leftExp = e.getLeft();
        ProgramVariableExp rightExp = e.getRight();

        // Boolean that indicates whether or not the expressions are
        // some kind of ProgramVariableArrayExp
        boolean isLeftArrayExp =
                ArrayConversionUtilities.isProgArrayExp(leftExp);
        boolean isRightArrayExp =
                ArrayConversionUtilities.isProgArrayExp(rightExp);

        // Case #1: Left expression is not an expression that is a ProgramVariableArrayExp,
        // but the right is a ProgramVariableArrayExp.
        // (ie: x :=: A[i], where "A" is an array, "i" is
        // index and "x" is a variable.)
        Statement newStatement;
        if (!isLeftArrayExp && isRightArrayExp) {
            // Obtain the array type, name and index
            ProgramVariableExp arrayNameExp =
                    ArrayConversionUtilities.getArrayNameExp(rightExp);
            ProgramExp arrayIndexExp =
                    ArrayConversionUtilities.getArrayIndexExp(rightExp);
            NameTy arrayTy = findArrayType(arrayNameExp);

            // New "Swap_Entry" call
            newStatement =
                    ArrayConversionUtilities
                            .buildSwapEntryCall(l, leftExp, arrayTy
                                    .getQualifier(), arrayNameExp,
                                    arrayIndexExp);
        }
        // Case #2: Right expression is not an expression that is a ProgramVariableArrayExp,
        // but the left is a ProgramVariableArrayExp.
        // (ie: A[i] :=: x, where "A" is an array, "i" is
        // index and "x" is a variable.)
        else if (isLeftArrayExp && !isRightArrayExp) {
            // Obtain the array type, name and index
            ProgramVariableExp arrayNameExp =
                    ArrayConversionUtilities.getArrayNameExp(leftExp);
            ProgramExp arrayIndexExp =
                    ArrayConversionUtilities.getArrayIndexExp(leftExp);
            NameTy arrayTy = findArrayType(arrayNameExp);

            // New "Swap_Entry" call
            newStatement =
                    ArrayConversionUtilities
                            .buildSwapEntryCall(l, rightExp, arrayTy
                                    .getQualifier(), arrayNameExp,
                                    arrayIndexExp);
        }
        // Case #3: Both left and right expressions are ProgramVariableArrayExp
        // expressions.
        // (ie: A[i] :=: A[j], where "A" is an array and
        // "i" and "j" are indexes)
        else if (isLeftArrayExp && isRightArrayExp) {
            // Obtain the array type, name and index
            ProgramVariableExp leftArrayNameExp =
                    ArrayConversionUtilities.getArrayNameExp(leftExp);
            ProgramVariableExp rightArrayNameExp =
                    ArrayConversionUtilities.getArrayNameExp(rightExp);
            ProgramExp leftArrayIndexExp =
                    ArrayConversionUtilities.getArrayIndexExp(leftExp);
            ProgramExp rightArrayIndexExp =
                    ArrayConversionUtilities.getArrayIndexExp(rightExp);

            // Throw an exception if the array names are not equivalent
            if (!leftArrayNameExp.equivalent(rightArrayNameExp)) {
                PosSymbol rightArrayNameAsPosSymbol;
                if (rightArrayNameExp instanceof ProgramVariableNameExp) {
                    rightArrayNameAsPosSymbol =
                            ((ProgramVariableNameExp) rightArrayNameExp)
                                    .getName();
                }
                else {
                    StringBuilder sb = new StringBuilder();
                    Iterator<ProgramVariableExp> segsIt =
                            ((ProgramVariableDotExp) rightArrayNameExp)
                                    .getSegments().iterator();
                    while (segsIt.hasNext()) {
                        ProgramVariableNameExp current =
                                (ProgramVariableNameExp) segsIt.next();
                        sb.append(current.getName().getName());

                        if (segsIt.hasNext()) {
                            sb.append(".");
                        }
                    }

                    rightArrayNameAsPosSymbol =
                            new PosSymbol(new Location(rightArrayNameExp
                                    .getLocation()), sb.toString());
                }

                throw new SourceErrorException(
                        "Cannot swap elements between two different arrays!",
                        rightArrayNameAsPosSymbol);
            }

            // New "Swap_Two_Entries" call
            NameTy arrayTy = findArrayType(leftArrayNameExp);
            newStatement =
                    ArrayConversionUtilities.buildSwapTwoEntriesCall(l, arrayTy
                            .getQualifier(), leftArrayNameExp,
                            leftArrayIndexExp, rightArrayIndexExp);
        }
        // Case #4: If it is not cases 1-4, then we build a regular
        // SwapStmt.
        else {
            newStatement =
                    new SwapStmt(new Location(l), (ProgramVariableExp) leftExp
                            .clone(), (ProgramVariableExp) rightExp.clone());
        }

        addToInnerMostCollector(newStatement);
    }

    /**
     * <p>This loop condition could have syntactic sugar conversions, so
     * we will need to have a way to store the new {@link Statement}s that get
     * generated.</p>
     *
     * @param e Current {@link WhileStmt} we are visiting.
     */
    @Override
    public void preWhileStmt(WhileStmt e) {
        // We have began a new block that can contain statements,
        // we need to store this in our stack.
        myResolveElementCollectorStack
                .push(new ResolveConceptualElementCollector(e));

        // A container for the loop-condition
        myNewStatementsContainer = new NewStatementsContainer();
    }

    /**
     * <p>After visiting the loop condition, we could have generated new {@link Statement}s, so
     * we will need to place those in the appropriate locations.</p>
     *
     * @param e Current {@link WhileStmt} we are visiting.
     * @param previous The previous {@link ResolveConceptualElement} visited.
     * @param next The next {@link ResolveConceptualElement} to visit.
     */
    @Override
    public void midWhileStmt(WhileStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {
        // Check to see if the condition item has any syntactic sugar conversions.
        // If yes, we will need to add it back to the correct locations.
        if (previous instanceof ProgramExp
                && next instanceof LoopVerificationItem) {
            // Obtain the right container to insert the statements that appear before
            // this WhileStmt.
            ResolveConceptualElementCollector whileStmtCollector =
                    myResolveElementCollectorStack.pop();

            // Check we got the right collector
            if (!whileStmtCollector.instantiatingElement.equals(e)) {
                throw new MiscErrorException(
                        "Something went wrong during the syntactic sugar conversion",
                        new IllegalStateException());
            }

            while (!myNewStatementsContainer.newPreStmts.empty()) {
                addToInnerMostCollector(myNewStatementsContainer.newPreStmts
                        .pop());
            }

            // Put the collector stack back the way it was and
            // Add any statements that need to appear after this WhileStmt.
            myResolveElementCollectorStack.push(whileStmtCollector);
            while (!myNewStatementsContainer.newPostStmts.isEmpty()) {
                addToInnerMostCollector(myNewStatementsContainer.newPostStmts
                        .remove());
            }

            // Set myNewStatementsContainer to null if we are done visiting the loop condition
            myNewStatementsContainer = null;
        }
    }

    /**
     * <p>We are done visiting this node, therefore we create a
     * new {@link WhileStmt} for future use.</p>
     *
     * @param e Current {@link WhileStmt} we are visiting.
     */
    @Override
    public void postWhileStmt(WhileStmt e) {
        // Done visiting this WhileStmt, so we can pop it off the stack.
        ResolveConceptualElementCollector collector =
                myResolveElementCollectorStack.pop();

        // Check we got the right collector
        if (!collector.instantiatingElement.equals(e)) {
            throw new MiscErrorException(
                    "Something went wrong during the syntactic sugar conversion",
                    new IllegalStateException());
        }

        // Retrieve the new loop condition (if any)
        ProgramExp newConditionExp;
        ProgramExp conditionExp = e.getTest();
        if (myReplacingElementsMap.containsKey(conditionExp)) {
            newConditionExp =
                    (ProgramExp) myReplacingElementsMap.remove(conditionExp);
        }
        else {
            newConditionExp = conditionExp.clone();
        }

        // Retrieve the new LoopVerificationItem (if any)
        LoopVerificationItem newItem;
        LoopVerificationItem item = e.getLoopVerificationBlock();
        if (myReplacingElementsMap.containsKey(item)) {
            newItem = (LoopVerificationItem) myReplacingElementsMap.get(item);
        }
        else {
            newItem = item.clone();
        }

        // Build the new WhileStmt and add it to the 'next' collector.
        addToInnerMostCollector(new WhileStmt(new Location(e.getLocation()),
                newConditionExp, newItem, collector.stmts));
    }

    // -----------------------------------------------------------
    // Item Nodes
    // -----------------------------------------------------------

    /**
     * <p>This if-condition could have syntactic sugar conversions, so
     * we will need to have a way to store the new {@link Statement}s that get
     * generated.</p>
     *
     * @param e Current {@link IfConditionItem} we are visiting.
     */
    @Override
    public void preIfConditionItem(IfConditionItem e) {
        // We have began a new block that can contain statements,
        // we need to store this in our stack.
        myResolveElementCollectorStack
                .push(new ResolveConceptualElementCollector(e));

        // A container for the if-condition
        myNewStatementsContainer = new NewStatementsContainer();
    }

    /**
     * <p>After visiting the if-condition, we could have generated new {@link Statement}s, so
     * we will need to place those in the appropriate locations.</p>
     *
     * @param e Current {@link IfConditionItem} we are visiting.
     * @param previous The previous {@link ResolveConceptualElement} visited.
     * @param next The next {@link ResolveConceptualElement} to visit.
     */
    @Override
    public void midIfConditionItem(IfConditionItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {
        // Check to see if the condition item has any syntactic sugar conversions.
        // If yes, we will need to add it back to both the statements inside the if and also inside the else.
        if (previous instanceof ProgramExp && next instanceof VirtualListNode) {
            // Obtain the container for IfConditionItem and the container for a IfStmt.
            ResolveConceptualElementCollector ifConditionItemCollector =
                    myResolveElementCollectorStack.pop();
            ResolveConceptualElementCollector ifStmtCollector =
                    myResolveElementCollectorStack.pop();

            // Check we got the right collector
            if (!ifConditionItemCollector.instantiatingElement.equals(e)) {
                throw new MiscErrorException(
                        "Something went wrong during the syntactic sugar conversion",
                        new IllegalStateException());
            }

            // Check we got the right collector
            if (!(ifStmtCollector.instantiatingElement instanceof IfStmt)) {
                throw new MiscErrorException(
                        "Something went wrong during the syntactic sugar conversion",
                        new IllegalStateException());
            }

            // Add all the new statements generated by the if-condition to the collector
            // that will contain this IfStmt.
            while (!myNewStatementsContainer.newPreStmts.empty()) {
                addToInnerMostCollector(myNewStatementsContainer.newPreStmts
                        .pop());
            }

            // Add all the new statements generated by the if-condition to both the
            // IfConditionItem collector and the IfStmt collector (else statements)
            while (!myNewStatementsContainer.newPostStmts.isEmpty()) {
                Statement statement =
                        myNewStatementsContainer.newPostStmts.remove();
                ifConditionItemCollector.stmts.add(statement.clone());
                ifStmtCollector.stmts.add(statement);
            }

            // Put the collectors back in the right place.
            myResolveElementCollectorStack.push(ifStmtCollector);
            myResolveElementCollectorStack.push(ifConditionItemCollector);

            // Set myNewStatementsContainer to null if we are done visiting the if condition
            myNewStatementsContainer = null;
        }
    }

    /**
     * <p>We are done visiting this node, therefore we create a
     * new {@link IfConditionItem} for future use.</p>
     *
     * @param e Current {@link IfConditionItem} we are visiting.
     */
    @Override
    public void postIfConditionItem(IfConditionItem e) {
        // Done visiting this IfConditionItem, so we can pop it off the stack.
        ResolveConceptualElementCollector collector =
                myResolveElementCollectorStack.pop();

        // Check we got the right collector
        if (!collector.instantiatingElement.equals(e)) {
            throw new MiscErrorException(
                    "Something went wrong during the syntactic sugar conversion",
                    new IllegalStateException());
        }

        // Retrieve the new if-condition (if any)
        ProgramExp newConditionExp;
        ProgramExp conditionExp = e.getTest();
        if (myReplacingElementsMap.containsKey(conditionExp)) {
            newConditionExp =
                    (ProgramExp) myReplacingElementsMap.remove(conditionExp);
        }
        else {
            newConditionExp = conditionExp.clone();
        }

        // Build the new IfConditionItem and add it to our new items map.
        myReplacingElementsMap.put(e, new IfConditionItem(new Location(e
                .getLocation()), newConditionExp, collector.stmts));
    }

    /**
     * <p>The user might not have supplied a {@code changing} clause,
     * therefore we will need to generate one.</p>
     *
     * @param e Current {@link LoopVerificationItem} we are visiting.
     */
    @Override
    public void postLoopVerificationItem(LoopVerificationItem e) {
        if (e.getChangingVars().isEmpty()) {
            AssertionClause newElapsedTimeClause = null;
            if (e.getElapsedTimeClause() != null) {
                newElapsedTimeClause = e.getElapsedTimeClause().clone();
            }
            AssertionClause newMaintainingClause =
                    e.getMaintainingClause().clone();
            AssertionClause newDecreasingClause =
                    e.getDecreasingClause().clone();

            myReplacingElementsMap.put(e, new LoopVerificationItem(
                    new Location(e.getLocation()), formChangingClause(),
                    newMaintainingClause, newDecreasingClause,
                    newElapsedTimeClause));
        }
    }

    // -----------------------------------------------------------
    // Program Expression Nodes
    // -----------------------------------------------------------

    /**
     * <p>This replaces the {@link ProgramVariableArrayExp} inside
     * the calling arguments with appropriate swapping operation from
     * {@code Static_Array_Template}.</p>
     *
     * @param e Current {@link ProgramFunctionExp} we are visiting.
     */
    @Override
    public void postProgramFunctionExp(ProgramFunctionExp e) {
        List<ProgramExp> args = e.getArguments();
        List<ProgramExp> newArgs = new ArrayList<>();
        for (ProgramExp arg : args) {
            // Check each of the args to see if we have ProgramVariableArrayExp.
            if (ArrayConversionUtilities.isProgArrayExp(arg)) {
                // Create a new variable to store the contents of the array expression
                ProgramVariableExp arrayNameExp = ArrayConversionUtilities.getArrayNameExp(arg);
                NameTy arrayTy = findArrayType(arrayNameExp);
                NameTy arrayContentsTy = myArrayNameTyToInnerTyMap.get(arrayTy);
                VarDec newArrayVarDec =
                        ArrayConversionUtilities.buildTempArrayNameVarDec(arrayNameExp,
                                arrayContentsTy, myNewElementCounter++);
                myParentNodeElementsContainer.varDecs.add(newArrayVarDec);

                // Array index expression
                ProgramExp arrayIndexExp = ArrayConversionUtilities.getArrayIndexExp(arg);

                // Create the new call to "Swap_Entry" and add it to the pre/post
                ProgramVariableNameExp newArrayVarDecAsProgramExp =
                        new ProgramVariableNameExp(new Location(newArrayVarDec.getLocation()),
                                null, newArrayVarDec.getName());
                CallStmt swapEntryCall = ArrayConversionUtilities.buildSwapEntryCall(arg.getLocation(),
                        newArrayVarDecAsProgramExp, arrayTy.getQualifier(), arrayNameExp, arrayIndexExp);
                myNewStatementsContainer.newPreStmts.push(swapEntryCall.clone());
                myNewStatementsContainer.newPostStmts.offer(swapEntryCall);
            }
            // ProgramFunctionExp
            else if (arg instanceof ProgramFunctionExp) {
                // Check to see if we have created a replacement expression
                if (myReplacingElementsMap.containsKey(arg)) {
                    newArgs.add((ProgramExp) myReplacingElementsMap.remove(arg));
                }
                else {
                    throw new MiscErrorException("Could not locate the replacement expression for: " + arg.toString(),
                            new IllegalStateException());
                }
            }
            else {
                // Make a deep copy from the original ProgramExp.
                newArgs.add(arg.clone());
            }
        }

        // Construct a new ProgramFunctionExp to put in our map
        PosSymbol qualifier = null;
        if (e.getQualifier() != null) {
            qualifier = e.getQualifier().clone();
        }

        myReplacingElementsMap.put(e, new ProgramFunctionExp(new Location(e.getLocation()),
                qualifier, e.getName().clone(), newArgs));
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the current integer value for the
     * new element counter.</p>
     *
     * @return The counter integer.
     */
    public final int getNewElementCounter() {
        return myNewElementCounter;
    }

    /**
     * <p>This method returns the new (and potentially modified) element
     * that includes the syntactic sugar conversions.</p>
     *
     * @return A {@link ResolveConceptualElement} object.
     */
    public final ResolveConceptualElement getProcessedElement() {
        if (myFinalProcessedElement == null) {
            throw new MiscErrorException(
                    "The new element didn't get constructed appropriately!",
                    new NullPointerException());
        }

        return myFinalProcessedElement;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method to add a {@link Statement} to the top-most
     * collector.</p>
     *
     * @param statement A {@link Statement} object.
     */
    private void addToInnerMostCollector(Statement statement) {
        // Get the top-most collector and add the statement
        ResolveConceptualElementCollector collector =
                myResolveElementCollectorStack.pop();
        collector.stmts.add(statement);

        // Put it back on the stack
        myResolveElementCollectorStack.push(collector);
    }

    /**
     * <p>An helper method that retrieves the static array type for
     * the array expression.</p>
     *
     * @param exp The array expression name.
     *
     * @return The {@link Ty} for the array.
     *
     * @exception MiscErrorException
     */
    private NameTy findArrayType(ProgramVariableExp exp) {
        NameTy contentTy;
        if (exp instanceof ProgramVariableDotExp) {
            // TODO: Change this when we pass shared state/type realization to this class.
            // Right now we assume the array expressions won't be in some kind of record.
            throw new RuntimeException();
        }
        else if (exp instanceof ProgramVariableNameExp) {
            // TODO: Change this when we pass shared state to this class.
            String arrayVarName =
                    ((ProgramVariableNameExp) exp).getName().getName();

            // Search parameter variables
            AbstractVarDec varDec = searchParameterVarDecs(arrayVarName);

            // Search local variables (if not found);
            if (varDec == null) {
                varDec = searchVarDecs(arrayVarName);
            }

            // Throw exception if we can't find it.
            if (varDec == null) {
                throw new MiscErrorException(
                        "Cannot locate the content type for the array: "
                                + exp.toString(), new IllegalStateException());
            }
            else {
                contentTy = (NameTy) varDec.getTy().clone();
            }
        }
        else {
            throw new MiscErrorException(
                    "Cannot locate the content type for the array: "
                            + exp.toString(), new IllegalStateException());
        }

        return contentTy;
    }

    /**
     * <p>An helper method that adds all the variables in scope as {@code changing}.</p>
     *
     * @return A list of {@link ProgramVariableExp} that are changing.
     */
    private List<ProgramVariableExp> formChangingClause() {
        List<ProgramVariableExp> changingList = new ArrayList<>();

        // TODO: Add all the shared state variables

        // Add all the parameter variables
        for (ParameterVarDec v : myParentNodeElementsContainer.parameterVarDecs) {
            changingList.add(new ProgramVariableNameExp(new Location(v.getLocation()), null, v.getName().clone()));
        }

        // Add all the local variables
        for (VarDec v : myParentNodeElementsContainer.varDecs) {
            changingList.add(new ProgramVariableNameExp(new Location(v.getLocation()), null, v.getName().clone()));
        }

        return changingList;
    }

    /**
     * <p>An helper method to try to locate a {@link ParameterVarDec} based on
     * the string name passed in.</p>
     *
     * @param name Name of the variable we are trying to find.
     *
     * @return A copy of the {@link ParameterVarDec} if found, else {@code null}.
     *
     * @exception MiscErrorException
     */
    private ParameterVarDec searchParameterVarDecs(String name) {
        ParameterVarDec parameterVarDec = null;
        for (ParameterVarDec v : myParentNodeElementsContainer.parameterVarDecs) {
            if (v.getName().getName().equals(name)) {
                if (parameterVarDec == null) {
                    parameterVarDec = (ParameterVarDec) v.clone();
                }
                else {
                    throw new MiscErrorException(
                            "Found two parameter variables for: " + name,
                            new IllegalStateException());
                }
            }
        }

        return parameterVarDec;
    }

    /**
     * <p>An helper method to try to locate a {@link VarDec} based on
     * the string name passed in.</p>
     *
     * @param name Name of the variable we are trying to find.
     *
     * @return A copy of the {@link VarDec} if found, else {@code null}.
     *
     * @exception MiscErrorException
     */
    private VarDec searchVarDecs(String name) {
        VarDec varDec = null;
        for (VarDec v : myParentNodeElementsContainer.varDecs) {
            if (varDec == null) {
                varDec = (VarDec) v.clone();
            }
            else {
                throw new MiscErrorException(
                        "Found two variables for: " + name,
                        new IllegalStateException());
            }
        }

        return varDec;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>This holds a copy of the {@link ParameterVarDec}, {@link VarDec} and
     * {@link FacilityDec} for the incoming parent node.</p>
     */
    private class ParentNodeElementsContainer {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>List of parameter variable declaration objects.</p>
         */
        final List<ParameterVarDec> parameterVarDecs;

        /**
         * <p>List of variable declaration objects.</p>
         */
        final List<FacilityDec> facilityDecs;

        /**
         * <p>List of variable declaration objects.</p>
         */
        final List<VarDec> varDecs;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs a temporary structure to store the elements from
         * the incoming parent node we are walking.</p>
         *
         * @param params List of parameter variables.
         * @param facs List of facility declarations.
         * @param vars List of regular variables.
         */
        ParentNodeElementsContainer(List<ParameterVarDec> params,
                List<FacilityDec> facs, List<VarDec> vars) {
            parameterVarDecs = copyParamDecls(params);
            facilityDecs = copyFacDecls(facs);
            varDecs = copyVarDecls(vars);
        }

        // ===========================================================
        // Private Methods
        // ===========================================================

        /**
         * <p>An helper method to create a new list of {@link FacilityDec}s
         * that is a deep copy of the one passed in.</p>
         *
         * @param facilityDecs The original list of {@link FacilityDec}s.
         *
         * @return A list of {@link FacilityDec}s.
         */
        private List<FacilityDec> copyFacDecls(List<FacilityDec> facilityDecs) {
            List<FacilityDec> copyFacilityDecs = new ArrayList<>();
            for (FacilityDec facilityDec : facilityDecs) {
                copyFacilityDecs.add((FacilityDec) facilityDec.clone());
            }

            return copyFacilityDecs;
        }

        /**
         * <p>An helper method to create a new list of {@link ParameterVarDec}s
         * that is a deep copy of the one passed in.</p>
         *
         * @param parameterVarDecs The original list of {@link ParameterVarDec}s.
         *
         * @return A list of {@link ParameterVarDec}s.
         */
        private List<ParameterVarDec> copyParamDecls(List<ParameterVarDec> parameterVarDecs) {
            List<ParameterVarDec> copyParamDecs = new ArrayList<>();
            for (ParameterVarDec parameterVarDec : parameterVarDecs) {
                copyParamDecs.add((ParameterVarDec) parameterVarDec.clone());
            }

            return copyParamDecs;
        }

        /**
         * <p>An helper method to create a new list of {@link VarDec}s
         * that is a deep copy of the one passed in.</p>
         *
         * @param varDecs The original list of {@link VarDec}s.
         *
         * @return A list of {@link VarDec}s.
         */
        private List<VarDec> copyVarDecls(List<VarDec> varDecs) {
            List<VarDec> copyVarDecs = new ArrayList<>();
            for (VarDec varDec : varDecs) {
                copyVarDecs.add((VarDec) varDec.clone());
            }

            return copyVarDecs;
        }
    }

    /**
     * <p>As we walk though the various different nodes that can contain
     * a list of {@link Statement}s, once we are done with a particular
     * {@link Statement}, it will be added to this class. Once we are done
     * walking all the statements and we are back to the
     * {@link ResolveConceptualElement} that contains the list of
     * {@link Statement}s, we will use the elements
     * stored in this class to create the new object.</p>
     */
    private class ResolveConceptualElementCollector {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>The {@link ResolveConceptualElement} that created this collector.</p>
         */
        final ResolveConceptualElement instantiatingElement;

        /**
         * <p>List of statement objects.</p>
         */
        final List<Statement> stmts;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs a temporary structure to store the list of
         * {@link Statement}s.</p>
         *
         * @param e The element that created this object.
         */
        ResolveConceptualElementCollector(ResolveConceptualElement e) {
            instantiatingElement = e;
            stmts = new ArrayList<>();
        }
    }

    /**
     * <p>This holds new {@link Statement}s related to syntactic sugar conversions for
     * {@link ProgramVariableArrayExp}.</p>
     */
    private class NewStatementsContainer {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>A stack of new statements that needs to be inserted before the code
         * that contains a program array expression.</p>
         *
         * <p><strong>Note:</strong> The only statements generated at the moment are
         * either new function assignment statements from indexes in
         * program array expressions or call statements to swap elements in the array(s).</p>
         */
        final Stack<Statement> newPreStmts;

        /**
         * <p>A queue of new statements that needs to be inserted after the code
         * that contains a program array expression.</p>
         *
         * <p><strong>Note:</strong> The only statements generated at the moment are
         * call statements to swap elements in the array(s).</p>
         */
        final Queue<Statement> newPostStmts;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs a temporary structure to store all the new statements that
         * resulted from syntactic sugar conversions for {@link ProgramVariableArrayExp}.</p>
         */
        NewStatementsContainer() {
            newPreStmts = new Stack<>();
            newPostStmts = new ArrayDeque<>();
        }
    }
}