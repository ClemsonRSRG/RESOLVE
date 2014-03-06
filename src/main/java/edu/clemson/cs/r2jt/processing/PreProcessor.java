/**
 * PreProcessor.java
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
package edu.clemson.cs.r2jt.processing;

// Libraries
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.util.ArrayList;
import java.util.Set;

/**
 * TODO: Write a description of this module
 */
public class PreProcessor extends TreeWalkerStackVisitor {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /**
     * <p>Map of all the local array types encountered.</p>
     */
    private Map<String, NameTy> myArrayFacilityMap;

    /**
     * <p>A counter used to keep track the number of things
     * created by the PreProcessor.</p>
     */
    private int myCounter;

    /**
     * <p>A list of all <code>FacilityDec</code> created
     * by the PreProcessor.</p>
     */
    private List<FacilityDec> myCreatedFacDecList;

    /**
     * <p>A mapping between the statement that created the
     * list of new statements and the new statement list.</p>
     */
    private Map<Statement, List<Statement>> myCreatedStmtMap;

    /**
     * <p>A mapping between the statement that created the
     * list of new call statements and the new call statement
     * list containing all our swap calls.</p>
     */
    private Map<Statement, List<CallStmt>> myCreatedSwapCallMap;

    /**
     * <p>List of new variable expressions that need to be
     * added to your changing clause list.</p>
     */
    private List<VariableExp> myNewChangingVarExpList;

    /**
     * <p>A mapping between the original statement and the
     * new statement that needs to take the place of the
     * original statement in our AST.</p>
     */
    private Map<Statement, Statement> myReplacingStmtMap;

    /**
     * <p>A temporary placeholder for created
     * <code>FacilityDecs</code>.</p>
     */
    private List<FacilityDec> myTempFacDecList;

    /**
     * <p>Utilities class that contains methods that are used
     * in both pre and post Processors.</p>
     */
    private Utilities myUtilities;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PreProcessor() {
        myArrayFacilityMap = new Map<String, NameTy>();
        myCounter = 1;
        myCreatedFacDecList = new List<FacilityDec>();
        myCreatedStmtMap = new Map<Statement, List<Statement>>();
        myCreatedSwapCallMap = new Map<Statement, List<CallStmt>>();
        myNewChangingVarExpList = null;
        myReplacingStmtMap = new Map<Statement, Statement>();
        myTempFacDecList = null;
        myUtilities = new Utilities();
    }

    // ===========================================================
    // TreeWalker Methods
    // ===========================================================

    // -----------------------------------------------------------
    // ArrayTy
    // -----------------------------------------------------------

    @Override
    public void postArrayTy(ArrayTy ty) {
        // Variables
        Location location = ty.getLocation();
        NameTy oldTy = (NameTy) ty.getEntryType();
        ResolveConceptualElement parent = this.getAncestor(1);
        String arrayName = null;

        // Check if we have a FacilityTypeDec, RepresentationDec or VarDec
        if (parent instanceof FacilityTypeDec) {
            arrayName = ((FacilityTypeDec) parent).getName().getName();
        }
        else if (parent instanceof RepresentationDec) {
            arrayName = ((RepresentationDec) parent).getName().getName();
        }
        else if (parent instanceof VarDec) {
            arrayName = ((VarDec) parent).getName().getName();
        }

        // Check for not null
        if (arrayName != null) {
            // Create name in the format of "_(Name of Variable)_Array_Fac_(myCounter)"
            String newArrayName = "";
            newArrayName += ("_" + arrayName + "_Array_Fac_" + myCounter++);

            // Create newTy
            NameTy newTy =
                    new NameTy(new PosSymbol(location, Symbol
                            .symbol(newArrayName)), new PosSymbol(location,
                            Symbol.symbol("Static_Array")));

            // Check if we have a FacilityTypeDec, RepresentationDec or VarDec
            // and set the Ty of the parent node.
            if (parent instanceof FacilityTypeDec) {
                ((FacilityTypeDec) parent).setRepresentation(newTy);
            }
            else if (parent instanceof RepresentationDec) {
                ((RepresentationDec) parent).setRepresentation(newTy);
            }
            else if (parent instanceof VarDec) {
                ((VarDec) parent).setTy(newTy);
            }

            // Create a list of arguments for the new FacilityDec
            List<ModuleArgumentItem> listItem = new List<ModuleArgumentItem>();
            String typeName = oldTy.getName().getName();

            // Add the type, Low and High for Arrays
            listItem.add(new ModuleArgumentItem(null, new PosSymbol(location,
                    Symbol.symbol(typeName)), null));
            listItem.add(new ModuleArgumentItem(null, null, ty.getLo()));
            listItem.add(new ModuleArgumentItem(null, null, ty.getHi()));

            // Call method to createFacilityDec
            FacilityDec arrayFacilityDec =
                    createFacilityDec(location, newArrayName,
                            "Static_Array_Template", "Std_Array_Realiz",
                            listItem, new List<ModuleArgumentItem>(),
                            new List<EnhancementItem>(),
                            new List<EnhancementBodyItem>(), true);

            // Add the newly created array facility to our list
            myCreatedFacDecList.add(arrayFacilityDec);

            // Save the Ty of this array for future use
            myArrayFacilityMap.put(newArrayName, oldTy);
        }
        else {
            notHandledArrayTyParent(ty.getLocation(), ty, parent);
        }
    }

    // -----------------------------------------------------------
    // AuxCodeStmt
    // -----------------------------------------------------------

    @Override
    public void postAuxCodeStmt(AuxCodeStmt stmt) {
        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = stmt.getStatements();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        stmt.setStatements(stmtList);
    }

    // -----------------------------------------------------------
    // CallStmt
    // -----------------------------------------------------------

    @Override
    public void postCallStmt(CallStmt stmt) {
        // Variables
        List<ProgramExp> argList = stmt.getArguments();

        // Change any instances of A[i] and S.A[i] to actual
        // calls to operations in Static_Array_Template
        argList = arrayExpConversion(stmt, argList);

        // Replace the original argument list with the one
        // returned by the conversion method.
        stmt.setArguments(argList);
    }

    // -----------------------------------------------------------
    // ChoiceItem
    // -----------------------------------------------------------

    @Override
    public void postChoiceItem(ChoiceItem item) {
        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = item.getThenclause();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        item.setThenclause(stmtList);
    }

    // -----------------------------------------------------------
    // ConceptBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        // Store all global variables, facility declarations,
        // records and local operations.
        myUtilities.initModuleDec(dec.getDecs());
    }

    @Override
    public void postConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        // Check to see if we created any new FacilityDecs
        // and add those to the list. When we are done, we
        // clear our list of created facility declarations.
        if (!myCreatedFacDecList.isEmpty()) {
            dec.setDecs(modifyFacDecList(dec.getDecs()));
            myCreatedFacDecList.clear();
        }

        // Clean up myUtilities
        myUtilities.finalModuleDec();
    }

    // -----------------------------------------------------------
    // ConditionItem
    // -----------------------------------------------------------

    @Override
    public void postConditionItem(ConditionItem item) {
        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = item.getThenclause();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        item.setThenclause(stmtList);
    }

    // -----------------------------------------------------------
    // EnhancementBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        // Store all global variables, facility declarations,
        // records and local operations.
        myUtilities.initModuleDec(dec.getDecs());
    }

    @Override
    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        // Check to see if we created any new FacilityDecs
        // and add those to the list. When we are done, we
        // clear our list of created facility declarations.
        if (!myCreatedFacDecList.isEmpty()) {
            dec.setDecs(modifyFacDecList(dec.getDecs()));
            myCreatedFacDecList.clear();
        }

        // Clean up myUtilities
        myUtilities.finalModuleDec();
    }

    // -----------------------------------------------------------
    // FacilityModuleDec
    // -----------------------------------------------------------

    @Override
    public void preFacilityModuleDec(FacilityModuleDec dec) {
        // Store all global variables, facility declarations,
        // records and local operations.
        myUtilities.initModuleDec(dec.getDecs());
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec dec) {
        // Check to see if we created any new FacilityDecs
        // and add those to the list. When we are done, we
        // clear our list of created facility declarations.
        if (!myCreatedFacDecList.isEmpty()) {
            dec.setDecs(modifyFacDecList(dec.getDecs()));
            myCreatedFacDecList.clear();
        }

        // Clean up myUtilities
        myUtilities.finalModuleDec();
    }

    // -----------------------------------------------------------
    // FacilityOperationDec
    // -----------------------------------------------------------

    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        // Store all parameter and local variables
        myUtilities.initOperationDec(dec.getParameters(), dec.getVariables());

        // Store any global facility creations in the
        // temp list, so we don't by accident add them here.
        myTempFacDecList = myCreatedFacDecList;
        myCreatedFacDecList = new List<FacilityDec>();
    }

    @Override
    public void postFacilityOperationDec(FacilityOperationDec dec) {
        // Check to see if we created any new FacilityDecs
        // and add those to the list. When we are done, we
        // clear our list of created facility declarations.
        if (!myCreatedFacDecList.isEmpty()) {
            dec.setFacilities(modifyFacDecListForOps(dec.getFacilities()));
            myCreatedFacDecList.clear();
        }

        // Put our original global facility list back
        myCreatedFacDecList = myTempFacDecList;
        myTempFacDecList = null;

        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = dec.getStatements();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        dec.setStatements(stmtList);

        // Store the local variable list
        dec.setVariables(myUtilities.getLocalVarList());

        // Clean up myUtilities
        myUtilities.finalOperationDec();
    }

    // -----------------------------------------------------------
    // FinalItem
    // -----------------------------------------------------------

    @Override
    public void postFinalItem(FinalItem item) {
        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = item.getStatements();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        item.setStatements(stmtList);
    }

    // -----------------------------------------------------------
    // FuncAssignStmt
    // -----------------------------------------------------------

    @Override
    public void postFuncAssignStmt(FuncAssignStmt stmt) {
        // Variables
        Location stmtLoc = stmt.getLocation();
        VariableExp leftExp = stmt.getVar();
        ProgramExp rightExp = stmt.getAssign();

        // Check to see if we need to convert the right hand side
        // to a call to Entry_Replica.
        // Case #1: VariableArrayExp
        if (rightExp instanceof VariableArrayExp) {
            Location varLoc = rightExp.getLocation();
            VariableNameExp varName =
                    new VariableNameExp(varLoc, ((VariableArrayExp) rightExp)
                            .getQualifier(), ((VariableArrayExp) rightExp)
                            .getName());

            // Call to Entry_Replica and replace it in
            // the statement
            rightExp =
                    createEntryReplicaExp(varLoc, varName,
                            ((VariableArrayExp) rightExp).getArgument());
            stmt.setAssign(rightExp);
        }
        // Check to see if we need to convert the right hand side
        // to a call to Entry_Replica.
        // Case #2: VariableDotExp with a VariableArrayExp
        else if (rightExp instanceof VariableDotExp) {
            // Check the last segment to see if it is
            // a VariableArrayExp.
            List<VariableExp> segs = ((VariableDotExp) rightExp).getSegments();
            VariableExp lastElement = segs.get(segs.size() - 1);
            if (lastElement instanceof VariableArrayExp) {
                Location varLoc = lastElement.getLocation();
                VariableNameExp varName =
                        new VariableNameExp(
                                varLoc,
                                ((VariableArrayExp) lastElement).getQualifier(),
                                ((VariableArrayExp) lastElement).getName());

                // Make the replacement in the dot expression.
                segs.set(segs.size() - 1, varName);
                ((VariableDotExp) rightExp).setSegments(segs);

                // Call to Entry_Replica and replace it in
                // the statement
                Location expLoc = rightExp.getLocation();
                rightExp =
                        createEntryReplicaExp(expLoc,
                                ((VariableDotExp) rightExp),
                                ((VariableArrayExp) lastElement).getArgument());
                stmt.setAssign(rightExp);
            }
        }
        else if (rightExp instanceof ProgramParamExp) {
            ProgramParamExp funcCall = (ProgramParamExp) rightExp;
            List<ProgramExp> argList = funcCall.getArguments();

            // Change any instances of A[i] and S.A[i] to actual
            // calls to operations in Static_Array_Template
            argList = arrayExpConversion(stmt, argList);

            // Replace the original argument list with the one
            // returned by the conversion method.
            funcCall.setArguments(argList);

            // Replace the right hand side expression
            stmt.setAssign(funcCall);
        }

        // Check to see if we need to convert the statement
        // to a call to Assign_Entry.
        // Case #1: VariableArrayExp
        if (leftExp instanceof VariableArrayExp) {
            VariableArrayExp arrayExp = (VariableArrayExp) leftExp;

            // Call to Assign_Entry
            VariableNameExp temp =
                    new VariableNameExp(arrayExp.getLocation(), arrayExp
                            .getQualifier(), arrayExp.getName());
            CallStmt newStmt =
                    createAssignEntryCall(stmtLoc, temp, arrayExp.getArgument());

            // Add it to our list of statements to be replaced.
            myReplacingStmtMap.put(stmt, newStmt);
        }
        // Check to see if we need to convert the statement
        // to a call to Assign_Entry.
        // Case #2: VariableDotExp with a VariableArrayExp
        else if (leftExp instanceof VariableDotExp) {
            // Check the last segment to see if it is
            // a VariableArrayExp.
            if (containsArray((VariableDotExp) leftExp)) {
                List<VariableExp> segs =
                        ((VariableDotExp) leftExp).getSegments();
                VariableExp lastElement = segs.get(segs.size() - 1);
                Location varLoc = lastElement.getLocation();
                VariableNameExp varName =
                        new VariableNameExp(
                                varLoc,
                                ((VariableArrayExp) lastElement).getQualifier(),
                                ((VariableArrayExp) lastElement).getName());

                // Make the replacement in the dot expression.
                segs.set(segs.size() - 1, varName);
                ((VariableDotExp) rightExp).setSegments(segs);

                // Call to Assign_Entry
                CallStmt newStmt =
                        createAssignEntryCall(stmtLoc, rightExp,
                                ((VariableArrayExp) lastElement).getArgument());

                // Add it to our list of statements to be replaced.
                myReplacingStmtMap.put(stmt, newStmt);
            }
        }
    }

    // -----------------------------------------------------------
    // IfStmt
    // -----------------------------------------------------------

    @Override
    public void postIfStmt(IfStmt stmt) {
        // Update our list of then clause statements
        // with any PreProcessor created statements..
        List<Statement> stmtList = stmt.getThenclause();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        stmt.setThenclause(stmtList);

        // Update our list of else clause statements
        // with any PreProcessor created statements.
        List<Statement> stmtList2 = stmt.getElseclause();
        stmtList2 = updateStatementList(stmtList2);
        stmtList2 = updateStmtListWithSwapCalls(stmtList2);
        stmtList2 = updateStmtListByReplacingStmts(stmtList2);
        stmt.setElseclause(stmtList2);
    }

    // -----------------------------------------------------------
    // InitItem
    // -----------------------------------------------------------

    @Override
    public void postInitItem(InitItem item) {
        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = item.getStatements();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        item.setStatements(stmtList);
    }

    // -----------------------------------------------------------
    // IterateExitStmt
    // -----------------------------------------------------------

    @Override
    public void postIterateExitStmt(IterateExitStmt stmt) {
        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = stmt.getStatements();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        stmt.setStatements(stmtList);
    }

    // -----------------------------------------------------------
    // IterateStmt
    // -----------------------------------------------------------

    @Override
    public void postIterateStmt(IterateStmt stmt) {
        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = stmt.getStatements();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        stmt.setStatements(stmtList);
    }

    // -----------------------------------------------------------
    // PerformanceFinalItem
    // -----------------------------------------------------------

    @Override
    public void postPerformanceFinalItem(PerformanceFinalItem item) {
        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = item.getStatements();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        item.setStatements(stmtList);
    }

    // -----------------------------------------------------------
    // PerformanceInitItem
    // -----------------------------------------------------------

    @Override
    public void postPerformanceInitItem(PerformanceInitItem item) {
        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = item.getStatements();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        item.setStatements(stmtList);
    }

    // -----------------------------------------------------------
    // ProcedureDec
    // -----------------------------------------------------------

    @Override
    public void preProcedureDec(ProcedureDec dec) {
        // Store all parameter and local variables
        myUtilities.initOperationDec(dec.getParameters(), dec.getVariables());

        // Store any global facility creations in the
        // temp list, so we don't by accident add them here.
        myTempFacDecList = myCreatedFacDecList;
        myCreatedFacDecList = new List<FacilityDec>();
    }

    @Override
    public void postProcedureDec(ProcedureDec dec) {
        // Check to see if we created any new FacilityDecs
        // and add those to the list. When we are done, we
        // clear our list of created facility declarations.
        if (!myCreatedFacDecList.isEmpty()) {
            dec.setFacilities(modifyFacDecListForOps(dec.getFacilities()));
            myCreatedFacDecList.clear();
        }

        // Put our original global facility list back
        myCreatedFacDecList = myTempFacDecList;
        myTempFacDecList = null;

        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = dec.getStatements();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        dec.setStatements(stmtList);

        // Store the local variable list
        dec.setVariables(myUtilities.getLocalVarList());

        // Clean up myUtilities
        myUtilities.finalOperationDec();
    }

    // -----------------------------------------------------------
    // SelectionStmt
    // -----------------------------------------------------------

    @Override
    public void postSelectionStmt(SelectionStmt stmt) {
        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = stmt.getDefaultclause();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        stmt.setDefaultclause(stmtList);
    }

    // -----------------------------------------------------------
    // SwapStmt
    // -----------------------------------------------------------

    @Override
    public void postSwapStmt(SwapStmt stmt) {
        // Variables
        Location location = stmt.getLocation();
        VariableExp leftExp = stmt.getLeft();
        VariableExp rightExp = stmt.getRight();

        // Case #1: VariableNameExp :=: VariableArrayExp or
        // VariableArrayExp :=: VariableNameExp
        // (ie: x :=: A[i], where "A" is an array, "i" is
        // index and "x" is a variable.)
        if ((leftExp instanceof VariableNameExp && rightExp instanceof VariableArrayExp)
                || (leftExp instanceof VariableArrayExp && rightExp instanceof VariableNameExp)) {
            // Convert to correct types
            VariableNameExp nameExp;
            VariableArrayExp arrayExp;
            if (leftExp instanceof VariableNameExp) {
                nameExp = (VariableNameExp) leftExp;
                arrayExp = (VariableArrayExp) rightExp;
            }
            else {
                nameExp = (VariableNameExp) rightExp;
                arrayExp = (VariableArrayExp) leftExp;
            }

            // Obtain name of the array
            VariableNameExp arrayName =
                    new VariableNameExp(location, null, arrayExp.getName());

            // Call to Swap_Entry
            CallStmt newStmt =
                    createSwapEntryCall(location, arrayName, nameExp, arrayExp
                            .getArgument());

            // Add it to our list of statements to be replaced.
            myReplacingStmtMap.put(stmt, newStmt);
        }
        // Case #2: VariableNameExp :=: VariableArrayExp
        // but the array is inside a VariableDotExp.
        // Same if the left and right are exchanged.
        // (ie: x :=: S.A[i], where "S" is a record,
        // "A" is an array, "i" is index and
        // "x" is a variable.)
        else if ((leftExp instanceof VariableNameExp && rightExp instanceof VariableDotExp)
                || (leftExp instanceof VariableDotExp && rightExp instanceof VariableNameExp)) {
            // Convert to correct types
            VariableNameExp nameExp;
            VariableDotExp dotExp;
            if (leftExp instanceof VariableNameExp) {
                nameExp = (VariableNameExp) leftExp;
                dotExp = (VariableDotExp) rightExp;
            }
            else {
                nameExp = (VariableNameExp) rightExp;
                dotExp = (VariableDotExp) leftExp;
            }

            // Check if the dotExp contains an array or not.
            if (containsArray(dotExp)) {
                // Obtain name of the array
                VariableDotExp arrayName = dotExp;

                // Modify the last segment
                List<VariableExp> tempList = arrayName.getSegments();
                int lastIndex = tempList.size() - 1;
                VariableArrayExp lastExp =
                        (VariableArrayExp) tempList.get(lastIndex);
                tempList.set(lastIndex, new VariableNameExp(location, null,
                        lastExp.getName()));
                arrayName.setSegments(tempList);

                // Call to Swap_Entry
                CallStmt newStmt =
                        createSwapEntryCall(location, arrayName, nameExp,
                                lastExp.getArgument());

                // Add it to our list of statements to be replaced.
                myReplacingStmtMap.put(stmt, newStmt);
            }
        }
        // Case #3: VariableArrayExp :=: VariableArrayExp
        // (ie: A[i] :=: A[j], where "A" is an array and
        // "i" and "j" are indexes)
        else if (leftExp instanceof VariableArrayExp
                && rightExp instanceof VariableArrayExp) {
            VariableArrayExp left = (VariableArrayExp) leftExp;
            VariableArrayExp right = (VariableArrayExp) rightExp;

            // Check to see if they are swapping entries within
            // the same array. It is an error if they try to
            // swap entries between two distinct arrays.
            if (left.getName().equals(right.getName())) {
                // Obtain name of the array
                VariableNameExp arrayName =
                        new VariableNameExp(location, null, left.getName());

                // Call to Swap_Entry
                CallStmt newStmt =
                        createSwapTwoEntriesCall(location, arrayName, left
                                .getArgument(), right.getArgument());

                // Add it to our list of statements to be replaced.
                myReplacingStmtMap.put(stmt, newStmt);
            }
            else {
                arrayMismatch(location, left, right);
            }
        }
        // Case #4: VariableArrayExp :=: VariableDotExp or
        // VariableDotExp :=: VariableArrayExp
        // Same if the left and right are exchanged.
        // (ie: A[i] :=: S.x, where "S" is a record,
        // "A" is an array, "i" is index and "x" is a variable.)
        else if ((leftExp instanceof VariableArrayExp && rightExp instanceof VariableDotExp)
                || (leftExp instanceof VariableDotExp && rightExp instanceof VariableArrayExp)) {
            // Convert to correct types
            VariableArrayExp arrayExp;
            VariableDotExp dotExp;
            if (leftExp instanceof VariableArrayExp) {
                arrayExp = (VariableArrayExp) leftExp;
                dotExp = (VariableDotExp) rightExp;
            }
            else {
                arrayExp = (VariableArrayExp) rightExp;
                dotExp = (VariableDotExp) leftExp;
            }

            // Error if both expressions are arrays.
            if (containsArray(dotExp)) {
                arrayMismatch(location, arrayExp, dotExp);
            }
            else {
                // Obtain name of the array
                VariableNameExp arrayName =
                        new VariableNameExp(location, null, arrayExp.getName());

                // Call to Swap_Entry
                CallStmt newStmt =
                        createSwapEntryCall(location, arrayName, dotExp,
                                arrayExp.getArgument());

                // Add it to our list of statements to be replaced.
                myReplacingStmtMap.put(stmt, newStmt);
            }
        }
        // Case #5: Both left and right are VariableDotExp
        else if (leftExp instanceof VariableDotExp
                && rightExp instanceof VariableDotExp) {
            VariableDotExp leftDotExp = (VariableDotExp) leftExp;
            VariableDotExp rightDotExp = (VariableDotExp) rightExp;

            // Case #5a: VariableArrayExp :=: VariableArrayExp,
            // but the arrays are inside a VariableDotExp.
            // (ie: S.A[i] :=: S.A[j], where "S" is a record,
            // "A" is an array and "i" and "j" are indexes)
            if (containsArray(leftDotExp) && containsArray(rightDotExp)) {
                // Lists of dot expression segments
                List<VariableExp> leftDotList = leftDotExp.getSegments();
                List<VariableExp> rightDotList = rightDotExp.getSegments();

                // Check if they are arrays inside the same record
                if (isSameRecord(leftDotList, rightDotList)) {
                    // Last segment variable expressions
                    int lastIndex = leftDotList.size() - 1;
                    VariableArrayExp leftLastExp =
                            (VariableArrayExp) leftDotList.get(lastIndex);
                    VariableArrayExp rightLastExp =
                            (VariableArrayExp) rightDotList.get(lastIndex);
                    VariableDotExp arrayName =
                            (VariableDotExp) leftDotExp.copy();

                    // Modify the last segment
                    List<VariableExp> tempList = arrayName.getSegments();
                    tempList.set(lastIndex, new VariableNameExp(location, null,
                            leftLastExp.getName()));
                    arrayName.setSegments(tempList);

                    // Check to see if they are swapping entries within
                    // the same array. It is an error if they try to
                    // swap entries between two distinct arrays.
                    if (leftLastExp.getName().equals(rightLastExp.getName())) {
                        // Call to Swap_Two_Entries
                        CallStmt newStmt =
                                createSwapTwoEntriesCall(location, arrayName,
                                        leftLastExp.getArgument(), rightLastExp
                                                .getArgument());

                        // Add it to our list of statements to be replaced.
                        myReplacingStmtMap.put(stmt, newStmt);
                    }
                    // Error
                    else {
                        arrayMismatch(location, leftExp, rightExp);
                    }
                }
                else {
                    recordMismatch(location, ((VariableDotExp) leftExp),
                            ((VariableDotExp) rightExp));
                }
            }
            // Case #5b: VariableNameExp :=: VariableArrayExp,
            // but the variable and arrays are inside a VariableDotExp.
            // Same if the left and right are exchanged.
            // (ie: S.x :=: S.A[i], where "S" is a record,
            // "A" is an array and "i" is index and "x" is a variable.)
            else if (containsArray(leftDotExp) || containsArray(rightDotExp)) {
                VariableDotExp dotArrayExp;
                VariableDotExp dotExp;
                if (containsArray(leftDotExp)) {
                    dotArrayExp = leftDotExp;
                    dotExp = rightDotExp;
                }
                else {
                    dotArrayExp = rightDotExp;
                    dotExp = leftDotExp;
                }

                // Modify the last segment
                VariableDotExp arrayName = (VariableDotExp) dotArrayExp.copy();
                List<VariableExp> tempList = arrayName.getSegments();
                int lastIndex = tempList.size() - 1;
                VariableArrayExp arrayLastExp =
                        (VariableArrayExp) tempList.get(lastIndex);
                tempList.set(lastIndex, new VariableNameExp(location, null,
                        arrayLastExp.getName()));
                arrayName.setSegments(tempList);

                // Call to Swap_Entry
                CallStmt newStmt =
                        createSwapEntryCall(location, arrayName, dotExp,
                                arrayLastExp.getArgument());

                // Add it to our list of statements to be replaced.
                myReplacingStmtMap.put(stmt, newStmt);
            }
        }
    }

    // -----------------------------------------------------------
    // WhileStmt
    // -----------------------------------------------------------

    @Override
    public void preWhileStmt(WhileStmt stmt) {
        myNewChangingVarExpList = new List<VariableExp>();
    }

    @Override
    public void postWhileStmt(WhileStmt stmt) {
        // Update the changing variable list if
        // we have newly created variable expressions
        if (!myNewChangingVarExpList.isEmpty()) {
            List<VariableExp> changingList = stmt.getChanging();
            for (VariableExp v : myNewChangingVarExpList) {
                changingList.add(v);
            }
            stmt.setChanging(changingList);
            myNewChangingVarExpList = null;
        }

        // Update our list of statements with any
        // PreProcessor created statements.
        List<Statement> stmtList = stmt.getStatements();
        stmtList = updateStatementList(stmtList);
        stmtList = updateStmtListWithSwapCalls(stmtList);
        stmtList = updateStmtListByReplacingStmts(stmtList);
        stmt.setStatements(stmtList);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    public void arrayMismatch(Location location, VariableExp left,
            VariableExp right) {
        String message =
                "Illegal operation. Cannot swap entries between two different arrays.\n";
        message += "Left array expression has array: " + left.toString() + "\n";
        message += "Right array expression has array: " + right.toString();
        throw new SourceErrorException(message, location);
    }

    public void notHandledArrayTyParent(Location location, ArrayTy ty,
            ResolveConceptualElement parent) {
        String message =
                "ArrayTy "
                        + ty.toString()
                        + "'s parent is "
                        + parent.toString()
                        + ". This type of parent is not handled in the PreProcessor.";
        throw new SourceErrorException(message, location);
    }

    public void recordMismatch(Location location, VariableDotExp exp1,
            VariableDotExp exp2) {
        String message =
                "Illegal operation. Cannot swap entries between two different arrays.\n";
        message += "Left array expression has array: " + exp1.toString() + "\n";
        message += "Right array expression has array: " + exp2.toString();
        throw new SourceErrorException(message, location);
    }

    public void recordNotFound(Location location, PosSymbol name) {
        String message =
                "Cannot find a record with the name: " + name.getName();
        throw new SourceErrorException(message, location);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Converts any <code>VariableArrayExp</code> to a <code>VariableNameExp</code>
     * by creating a new <code>Exp</code> and applying swap calls provided by the
     * Static_Array_Template.</p>
     *
     * @param stmt The statement that is calling this operation.
     * @param argList The list of arguments being used to invoke the current
     *                operation.
     *
     * @return The modified argument list
     */
    private List<ProgramExp> arrayExpConversion(Statement stmt,
            List<ProgramExp> argList) {
        // Lists to store our newly created items
        List<Statement> newStmtList = new List<Statement>();
        List<CallStmt> newCallStmtList = new List<CallStmt>();

        // Iterate through the argument list
        for (int i = 0; i < argList.size(); i++) {
            ProgramExp current = argList.get(i);
            boolean isArrayExp = false;
            Location location = current.getLocation();
            PosSymbol name = null;
            NameTy arrayTy = null;
            ProgramExp argument = null;
            VariableExp newNameExp = null;

            // Check if it is a VariableArrayExp.
            if (current instanceof VariableArrayExp) {
                isArrayExp = true;
                name = ((VariableArrayExp) current).getName();
                argument = ((VariableArrayExp) current).getArgument();

                // Locate the type of the array.
                VarDec arrayVarDec = myUtilities.searchVarDecLists(name);
                newNameExp =
                        createVariableNameExp(location, "", name.getName(), "");
                NameTy facilityTy = (NameTy) arrayVarDec.getTy();
                arrayTy =
                        myArrayFacilityMap.get(facilityTy.getQualifier()
                                .getName());
            }
            // Check if this VariableDotExp contains a
            // VariableArrayExp as its last segment.
            else if (current instanceof VariableDotExp) {
                // Get list of segments
                List<VariableExp> segList =
                        ((VariableDotExp) current).getSegments();
                VariableNameExp first = (VariableNameExp) segList.get(0);
                VariableExp last = segList.get(segList.size() - 1);

                // Check to see if our dot expression contains an
                // array expression as its last segment. Ex: S.A[i]
                if (last instanceof VariableArrayExp) {
                    isArrayExp = true;
                    name = ((VariableArrayExp) last).getName();
                    argument = ((VariableArrayExp) last).getArgument();

                    // Locate the array declaration inside the record
                    VarDec recordVarDec =
                            myUtilities.searchVarDecLists(first.getName());

                    if (recordVarDec != null) {
                        // Locate the type of the array inside a record
                        NameTy recordTy = (NameTy) recordVarDec.getTy();
                        VarDec arrayVarDec =
                                myUtilities.searchRecords(recordTy.getName(),
                                        name);
                        List<VariableExp> newSegList = segList;
                        newSegList.remove(segList.size() - 1);
                        newSegList.add(createVariableNameExp(location, "", name
                                .getName(), ""));
                        newNameExp =
                                new VariableDotExp(current.getLocation(),
                                        newSegList, ((VariableDotExp) current)
                                                .getSemanticExp());
                        NameTy facilityTy = (NameTy) arrayVarDec.getTy();
                        arrayTy =
                                myArrayFacilityMap.get(facilityTy
                                        .getQualifier().getName());
                    }
                    else {
                        recordNotFound(location, first.getName());
                    }
                }
            }

            if (isArrayExp) {
                // Create a new variable name expression for the entire
                // array expression
                VariableNameExp newExp =
                        createVariableNameExp(location, "_ArrayExp_", name
                                .getName(), "_" + myCounter++);

                // Create a new variable name expression for the index
                // of the array expression
                VariableNameExp newIndexExp =
                        createVariableNameExp(location, "_ArrayIndex_", name
                                .getName(), "_" + myCounter++);

                // Add these to our list of new changing variable list
                // if we are in a while loop
                if (myNewChangingVarExpList != null) {
                    myNewChangingVarExpList.add(newExp);
                    myNewChangingVarExpList.add(newIndexExp);
                }

                // Create new variables for these two new variable
                // expressions and add these to our list of local
                // variables.
                VarDec expVarDec = new VarDec(newExp.getName(), arrayTy);
                VarDec indexVarDec =
                        new VarDec(newIndexExp.getName(),
                                createIntegerTy(location));
                myUtilities.addNewLocalVariable(expVarDec);
                myUtilities.addNewLocalVariable(indexVarDec);

                // If our index argument list is just a variable
                // expression, apply Replica to it
                if (argument instanceof VariableExp) {
                    List<ProgramExp> list = new List<ProgramExp>();
                    list.add(argument);
                    argument =
                            new ProgramParamExp(location, new PosSymbol(
                                    location, Symbol.symbol("Replica")), list,
                                    null);
                }

                // Store the index of the array inside "newIndexExp" by
                // creating a <code>FunctionAssignStmt</code> and add it
                // to the list of statements to be inserted later.
                FuncAssignStmt funcAssignStmt =
                        new FuncAssignStmt(location, newIndexExp, argument);
                newStmtList.add(funcAssignStmt);

                // Create a call to Swap_Entry
                CallStmt swapEntryStmt =
                        createSwapEntryCall(location, newNameExp, newExp,
                                newIndexExp);
                newCallStmtList.add(swapEntryStmt);

                // Replace current with the newExp
                argList.set(i, newExp);
            }
        }

        // Add the new lists to our maps if they are
        // not empty
        if (!newStmtList.isEmpty()) {
            myCreatedStmtMap.put(stmt, newStmtList);
        }
        if (!newCallStmtList.isEmpty()) {
            myCreatedSwapCallMap.put(stmt, newCallStmtList);
        }

        return argList;
    }

    /**
     * <p>Checks if the last entry in the <code>VariableDotExp</code>
     * contains a <code>VariableArrayExp</code> or not.</p>
     *
     * @param exp The variable expression to test.
     *
     * @return Boolean result of the test.
     */
    private boolean containsArray(VariableDotExp exp) {
        // Variables
        List<VariableExp> segs = exp.getSegments();
        int last = segs.size() - 1;

        // Check if the last entry is an array or not.
        if (segs.get(last) instanceof VariableArrayExp)
            return true;
        else
            return false;
    }

    /**
     * <p>Creates a call to AssignEntry.</p>
     *
     * @param location A given location in the AST.
     * @param exp1 Argument #1.
     * @param exp2 Argument #2.
     *
     * @return A <code>CallStmt</code>.
     */
    private CallStmt createAssignEntryCall(Location location, ProgramExp exp1,
            ProgramExp exp2) {
        // Argument list
        List<ProgramExp> callArgList = new List<ProgramExp>();
        callArgList.add(exp1);
        callArgList.add(exp2);

        return new CallStmt(null, new PosSymbol(location, Symbol
                .symbol("Assign_Entry")), callArgList);
    }

    /**
     * <p>Creates a call to the Entry_Replica operation provided by the
     * Static_Array_Template.</p>
     *
     * @param location The location where the variable expression was found.
     * @param exp The original variable to be replicated.
     * @param indexes The indexes of the array expression.
     *
     * @return A <code>ProgramParamExp</code> with the call.
     */
    private ProgramParamExp createEntryReplicaExp(Location location,
            VariableExp exp, ProgramExp indexes) {
        // Create the parameter list
        List<ProgramExp> params = new List<ProgramExp>();
        params.add(exp);
        params.add(indexes);

        return new ProgramParamExp(location, new PosSymbol(location, Symbol
                .symbol("Entry_Replica")), params, null);
    }

    /**
     * <p>Creates a new <code>FacilityDec</code>.</p>
     *
     * @param location The location where the <code>FacilityDec</code> is created
     * @param name The name of the new <code>FacilityDec</code>.
     * @param conceptName The name of the Concept of this <code>FacilityDec</code>.
     * @param conceptRealizationName The name of the Concept Realization of this
     *                               <code>FacilityDec</code>.
     * @param conceptParam The list of parameters for the Concept.
     * @param conceptBodiesParam The list of parameters for the Concept
     *                           Realization.
     * @param enhancementParam The list of parameters for the Enhancement.
     * @param enhancementBodiesParam The list of parameters for the Enhancement
     *                               Realization.
     *
     * @return Newly created <code>FacilityDec</code>
     */
    private FacilityDec createFacilityDec(Location location, String name,
            String conceptName, String conceptRealizationName,
            List<ModuleArgumentItem> conceptParam,
            List<ModuleArgumentItem> conceptBodiesParam,
            List<EnhancementItem> enhancementParam,
            List<EnhancementBodyItem> enhancementBodiesParam,
            boolean externallyRealized) {
        // Create a FacilityDec
        FacilityDec newFacilityDec = new FacilityDec();

        // Set the name
        newFacilityDec.setName(new PosSymbol(location, Symbol.symbol(name)));

        // Set the Concept
        newFacilityDec.setConceptName(new PosSymbol(location, Symbol
                .symbol(conceptName)));
        newFacilityDec.setConceptParams(conceptParam);

        // Set the Concept Realization
        newFacilityDec.setBodyName(new PosSymbol(location, Symbol
                .symbol(conceptRealizationName)));
        newFacilityDec.setBodyParams(conceptBodiesParam);

        // Set the Enhancement and Enhancement Realization list
        newFacilityDec.setEnhancements(enhancementParam);
        newFacilityDec.setEnhancementBodies(enhancementBodiesParam);

        // Set the boolean that notes if this file is externally
        // realized or not.
        newFacilityDec.setExternallyRealizedFlag(externallyRealized);

        return newFacilityDec;
    }

    /**
     * <p>Creates a <code>Ty</code> for Integers.</p>
     *
     * @param location A given location in the AST.
     *
     * @return The <code>Ty</code> form for Integers.
     */
    private Ty createIntegerTy(Location location) {
        return new NameTy(null, new PosSymbol(location, Symbol
                .symbol("Integer")));
    }

    /**
     * <p>Creates a call to SwapEntry.</p>
     *
     * @param location A given location in the AST.
     * @param exp1 Argument #1.
     * @param exp2 Argument #2.
     * @param exp3 Argument #3.
     *
     * @return A <code>CallStmt</code>.
     */
    private CallStmt createSwapEntryCall(Location location, ProgramExp exp1,
            ProgramExp exp2, ProgramExp exp3) {
        // Argument list
        List<ProgramExp> callArgList = new List<ProgramExp>();
        callArgList.add(exp1);
        callArgList.add(exp2);
        callArgList.add(exp3);

        return new CallStmt(null, new PosSymbol(location, Symbol
                .symbol("Swap_Entry")), callArgList);
    }

    /**
     * <p>Creates a call to SwapTwoEntries.</p>
     *
     * @param location A given location in the AST.
     * @param exp1 Argument #1.
     * @param exp2 Argument #2.
     * @param exp3 Argument #3.
     *
     * @return A <code>CallStmt</code>.
     */
    private CallStmt createSwapTwoEntriesCall(Location location,
            ProgramExp exp1, ProgramExp exp2, ProgramExp exp3) {
        // Argument list
        List<ProgramExp> callArgList = new List<ProgramExp>();
        callArgList.add(exp1);
        callArgList.add(exp2);
        callArgList.add(exp3);

        return new CallStmt(null, new PosSymbol(location, Symbol
                .symbol("Swap_Two_Entries")), callArgList);
    }

    /**
     * <p>Creates a new <code>VariableNameExp</code> given a prefix and
     * the old variable name.</p>
     *
     * @param location Location of the array variable.
     * @param prefix Prefix for the new variable expression.
     * @param name Name of the old variable expression.
     * @param suffix Suffix for the new variable expression.
     *
     * @return A <code>VariableNameExp</code> of the form
     *         "prefix_(name)_suffix".
     */
    private VariableNameExp createVariableNameExp(Location location,
            String prefix, String name, String suffix) {
        // Create a new name
        String newNameStr = prefix + name + suffix;
        PosSymbol newName = new PosSymbol(location, Symbol.symbol(newNameStr));

        return new VariableNameExp(location, null, newName);
    }

    /**
     * <p>Checks if two lists point to the same record.</p>
     *
     * @param list1 List of variable expressions 1.
     * @param list2 List of variable expressions 2.
     *
     * @return The boolean result of the check.
     */
    private boolean isSameRecord(List<VariableExp> list1,
            List<VariableExp> list2) {
        boolean returnVal = true;

        // Need to check deeper if they are the same size
        if (list1.size() == list2.size()) {
            for (int i = 0; i < list1.size() - 1; i++) {
                VariableExp temp1 = list1.get(i);
                VariableExp temp2 = list2.get(i);

                // Case #1: temp1 and temp2 are both VariableArrayExp
                if (temp1 instanceof VariableArrayExp
                        && temp2 instanceof VariableArrayExp) {
                    // Set to false if they don't have the same name
                    if (!((VariableArrayExp) temp1).getName().equals(
                            ((VariableArrayExp) temp2).getName())) {
                        returnVal = false;
                    }
                }
                // Case #2: temp1 and temp2 are both VariableNameExp
                else if (temp1 instanceof VariableNameExp
                        && temp2 instanceof VariableNameExp) {
                    // Set to false if they don't have the same name
                    if (!((VariableNameExp) temp1).getName().equals(
                            ((VariableNameExp) temp2).getName())) {
                        returnVal = false;
                    }
                }
                // Case #3: temp1 and temp2 aren't the same type
                else {
                    returnVal = false;
                }
            }
        }
        // Obviously they are different because they don't have
        // the same length.
        else {
            returnVal = false;
        }

        return returnVal;
    }

    /**
     * <p>Modifies the list of <code>Decs</code> passed in by
     * adding the facilities created by the PreProcessor to the
     * front of the list.</p>
     *
     * @param decList List of <code>Decs</code> to be modified.
     *
     * @return Modified list of <code>Decs</code>.
     */
    private List<Dec> modifyFacDecList(List<Dec> decList) {
        // Loop through the list
        for (int i = myCreatedFacDecList.size() - 1; i >= 0; i--) {
            // Add to the front of the list
            decList.add(0, myCreatedFacDecList.get(i));
        }

        return decList;
    }

    /**
     * <p>Modifies the list of <code>FacilityDecs</code> passed in by
     * adding the facilities created by the PreProcessor to the
     * front of the list.</p>
     *
     * @param decList List of <code>FacilityDecs</code> to be modified.
     *
     * @return Modified list of <code>FacilityDecs</code>.
     */
    private List<FacilityDec> modifyFacDecListForOps(List<FacilityDec> decList) {
        // Loop through the list
        for (int i = myCreatedFacDecList.size() - 1; i >= 0; i--) {
            // Add to the front of the list
            decList.add(0, myCreatedFacDecList.get(i));
        }

        return decList;
    }

    /**
     * <p>Modifies the statement list passed in by adding
     * the statements created by the PreProcessor in the right
     * location in our AST.</p>
     *
     * @param statement The original statement that created
     *                  these extra statements.
     * @param stmtList List of statements to be modified.
     *
     * @return Modified statement list.
     */
    private List<Statement> modifyStatementList(Statement statement,
            List<Statement> stmtList) {
        // Loop through the list
        for (int i = 0; i < stmtList.size(); i++) {
            Statement current = stmtList.get(i);

            // Check if the current statement is the same
            // as the one passed in.
            if (current.getLocation().equals(statement.getLocation())) {
                // Add all created statements before current
                List<Statement> newStatements = myCreatedStmtMap.get(statement);
                for (Statement s : newStatements) {
                    stmtList.add(i, s);
                }
                break;
            }
        }

        return stmtList;
    }

    /**
     * <p>Modifies the statement list passed in by adding
     * the swap call statements created by the PreProcessor
     * before and after the specified location in our AST.</p>
     *
     * @param statement The original statement that created
     *                  these extra statements.
     * @param stmtList List of statements to be modified.
     *
     * @return Modified statement list.
     */
    private List<Statement> modifyStatementListForSwapCalls(
            Statement statement, List<Statement> stmtList) {
        // Loop through the list
        for (int i = 0; i < stmtList.size(); i++) {
            Statement current = stmtList.get(i);

            // Check if the current statement is the same
            // as the one passed in.
            if (current.getLocation().equals(statement.getLocation())) {
                // Add all created statements before current
                List<CallStmt> newCallStmts =
                        myCreatedSwapCallMap.get(statement);
                for (int j = 0; j < newCallStmts.size(); j++) {
                    stmtList.add(i + 1, newCallStmts.get(j));
                }

                // Add all created statements before current
                for (int j = newCallStmts.size() - 1; j >= 0; j--) {
                    stmtList.add(i, newCallStmts.get(j));
                }
                break;
            }
        }

        return stmtList;
    }

    /**
     * <p>Modify the statement list passed in by replacing
     * the new statements created by the PreProcessor in the
     * specified location in our AST.</p>
     *
     * @param statement The original statement that needs to
     *                  be replaced.
     * @param stmtList List of statements to be modified.
     *
     * @return Modified statement list.
     */
    private List<Statement> replaceStatementListWithNewStmt(
            Statement statement, List<Statement> stmtList) {
        // Loop through the list
        for (int i = 0; i < stmtList.size(); i++) {
            Statement current = stmtList.get(i);

            // Check if the current statement is the same
            // as the one passed in.
            if (current.getLocation().equals(statement.getLocation())) {
                // Obtain the new statement from the map
                Statement newStatement = myReplacingStmtMap.get(statement);
                stmtList.set(i, newStatement);
                break;
            }
        }

        return stmtList;
    }

    /**
     * <p>Checks to see if the list of statements passed in needs
     * to be updated or not. If yes, it will update the list
     * accordingly. If not, it returns the original list.</p>
     *
     * @param stmtList List of statements to be modified.
     *
     * @return Modified statement list.
     */
    private List<Statement> updateStatementList(List<Statement> stmtList) {
        // Check to see if we have any statements we need
        // to add to the original list.
        if (!myCreatedStmtMap.isEmpty()) {
            Set<Statement> keys = myCreatedStmtMap.keySet();

            // Loop through each statement
            for (Statement s : keys) {
                stmtList = modifyStatementList(s, stmtList);
                myCreatedStmtMap.remove(s);
            }
        }

        return stmtList;
    }

    /**
     * <p>Checks to see if the list of statements passed in needs
     * to be updated or not. If yes, it will update the list
     * accordingly by replacing the statement with the one located
     * in the map. If not, it returns the original list.</p>
     *
     * @param stmtList List of statements to be modified.
     *
     * @return Modified statement list.
     */
    private List<Statement> updateStmtListByReplacingStmts(
            List<Statement> stmtList) {
        // Check to see if we have any statements we need
        // to add to the original list.
        if (!myReplacingStmtMap.isEmpty()) {
            List<Statement> tempList = new List<Statement>();
            Set<Statement> keys = myReplacingStmtMap.keySet();

            // Loop through each statement
            for (Statement s : keys) {
                stmtList = replaceStatementListWithNewStmt(s, stmtList);
                tempList.add(s);
            }

            // Remove statements from the map
            // Note: Can't do it in the loop above because Java
            // throws a concurrent access error!
            for (Statement s : tempList) {
                myReplacingStmtMap.remove(s);
            }
        }

        return stmtList;
    }

    /**
     * <p>Checks to see if the list of statements passed in needs
     * to be updated or not. If yes, it will update the list
     * accordingly by adding the swap call before and after.
     * If not, it returns the original list.</p>
     *
     * @param stmtList List of statements to be modified.
     *
     * @return Modified statement list.
     */
    private List<Statement> updateStmtListWithSwapCalls(List<Statement> stmtList) {
        // Check to see if we have any statements we need
        // to add to the original list.
        if (!myCreatedSwapCallMap.isEmpty()) {
            Set<Statement> keys = myCreatedSwapCallMap.keySet();

            // Loop through each statement
            for (Statement s : keys) {
                stmtList = modifyStatementListForSwapCalls(s, stmtList);
                myCreatedSwapCallMap.remove(s);
            }
        }

        return stmtList;
    }
}