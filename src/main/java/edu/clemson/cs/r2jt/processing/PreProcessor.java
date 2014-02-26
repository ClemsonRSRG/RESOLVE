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
     * <p>A mapping between the original statement and the
     * new statement that needs to take the place of the
     * original statement in our AST.</p>
     */
    private Map<Statement, Statement> myReplacingStmtMap;

    /**
     * <p>A list of all <code>UsesItems</code> created
     * by the PreProcessor.</p>
     */
    private List<UsesItem> myUsesItemList;

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
        myReplacingStmtMap = new Map<Statement, Statement>();
        myUsesItemList = new List<UsesItem>();
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

            // Add Static_Array_Template to our uses list
            // if is not there already.
            myUsesItemList.addUnique(new UsesItem(new PosSymbol(null, Symbol
                    .symbol("Static_Array_Template"))));
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

        // Add any new concepts needed to our
        // list of imports.
        List<UsesItem> usesList = dec.getUsesItems();
        Location location = usesList.get(0).getName().getLocation();
        for (UsesItem item : myUsesItemList) {
            // Edit the location of the name
            // and put it back into our uses item.
            PosSymbol name = item.getName();
            name.setLocation(location);
            item.setName(name);

            // Adds it if it is not there already.
            usesList.addUnique(item);
        }
        dec.setUsesItems(usesList);

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

        // Add any new concepts needed to our
        // list of imports.
        List<UsesItem> usesList = dec.getUsesItems();
        Location location = usesList.get(0).getName().getLocation();
        for (UsesItem item : myUsesItemList) {
            // Edit the location of the name
            // and put it back into our uses item.
            PosSymbol name = item.getName();
            name.setLocation(location);
            item.setName(name);

            // Adds it if it is not there already.
            usesList.addUnique(item);
        }
        dec.setUsesItems(usesList);

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

        // Add any new concepts needed to our
        // list of imports.
        List<UsesItem> usesList = dec.getUsesItems();
        Location location = usesList.get(0).getName().getLocation();
        for (UsesItem item : myUsesItemList) {
            // Edit the location of the name
            // and put it back into our uses item.
            PosSymbol name = item.getName();
            name.setLocation(location);
            item.setName(name);

            // Adds it if it is not there already.
            usesList.addUnique(item);
        }
        dec.setUsesItems(usesList);

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
            // Check the last segment to make sure it is not
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
        // to a call to Assign_Entry because the left hand side
        // is a VariableArrayExp.
        if (leftExp instanceof VariableArrayExp) {
            VariableArrayExp arrayExp = (VariableArrayExp) leftExp;

            // Parameter List
            List<ProgramExp> params = new List<ProgramExp>();
            params.add(new VariableNameExp(arrayExp.getLocation(), arrayExp
                    .getQualifier(), arrayExp.getName()));
            params.add(arrayExp.getArgument());

            // Call to Assign_Entry
            CallStmt newStmt =
                    new CallStmt(null, new PosSymbol(stmtLoc, Symbol
                            .symbol("Assign_Entry")), params);

            // Add it to our list of statements to be replaced.
            myReplacingStmtMap.put(stmt, newStmt);
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
    // WhileStmt
    // -----------------------------------------------------------

    @Override
    public void postWhileStmt(WhileStmt stmt) {
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

            // Check if it is a VariableArrayExp.
            if (current instanceof VariableArrayExp) {
                isArrayExp = true;
                name = ((VariableArrayExp) current).getName();

                // Locate the type of the array.
                VarDec arrayVarDec = myUtilities.searchVarDecLists(name);
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

                    // Locate the array declaration inside the record
                    VarDec recordVarDec =
                            myUtilities.searchVarDecLists(first.getName());

                    if (recordVarDec != null) {
                        // Locate the type of the array inside a record
                        NameTy recordTy = (NameTy) recordVarDec.getTy();
                        VarDec arrayVarDec =
                                myUtilities.searchRecords(recordTy.getName(),
                                        name);
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

                // Create new variables for these two new variable
                // expressions and add these to our list of local
                // variables.
                VarDec expVarDec = new VarDec(newExp.getName(), arrayTy);
                VarDec indexVarDec =
                        new VarDec(newIndexExp.getName(),
                                createIntegerTy(location));
                myUtilities.addNewLocalVariable(expVarDec);
                myUtilities.addNewLocalVariable(indexVarDec);

                // Store the index of the array inside "newIndexExp" by
                // creating a <code>FunctionAssignStmt</code> and add it
                // to the list of statements to be inserted later.
                FuncAssignStmt funcAssignStmt =
                        new FuncAssignStmt(location, newIndexExp,
                                ((VariableArrayExp) current).getArgument());
                newStmtList.add(funcAssignStmt);

                // Create a call to Swap_Entry
                List<ProgramExp> callArgList = new List<ProgramExp>();
                callArgList.add(createVariableNameExp(location, "", name
                        .getName(), ""));
                callArgList.add(newExp);
                callArgList.add(newIndexExp);

                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(location, Symbol
                                .symbol("Swap_Entry")), callArgList);
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
            Set<Statement> keys = myReplacingStmtMap.keySet();

            // Loop through each statement
            for (Statement s : keys) {
                stmtList = replaceStatementListWithNewStmt(s, stmtList);
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