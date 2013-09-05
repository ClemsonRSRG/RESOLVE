package edu.clemson.cs.r2jt.processing;

// Libraries
import edu.clemson.cs.r2jt.absyn.ArrayTy;
import edu.clemson.cs.r2jt.absyn.CallStmt;
import edu.clemson.cs.r2jt.absyn.ConceptBodyModuleDec;
import edu.clemson.cs.r2jt.absyn.ConditionItem;
import edu.clemson.cs.r2jt.absyn.Dec;
import edu.clemson.cs.r2jt.absyn.EnhancementBodyItem;
import edu.clemson.cs.r2jt.absyn.EnhancementBodyModuleDec;
import edu.clemson.cs.r2jt.absyn.EnhancementItem;
import edu.clemson.cs.r2jt.absyn.EqualsExp;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.FacilityDec;
import edu.clemson.cs.r2jt.absyn.FacilityModuleDec;
import edu.clemson.cs.r2jt.absyn.FacilityOperationDec;
import edu.clemson.cs.r2jt.absyn.FacilityTypeDec;
import edu.clemson.cs.r2jt.absyn.FuncAssignStmt;
import edu.clemson.cs.r2jt.absyn.IfStmt;
import edu.clemson.cs.r2jt.absyn.ModuleArgumentItem;
import edu.clemson.cs.r2jt.absyn.NameTy;
import edu.clemson.cs.r2jt.absyn.ParameterVarDec;
import edu.clemson.cs.r2jt.absyn.ProcedureDec;
import edu.clemson.cs.r2jt.absyn.ProgramExp;
import edu.clemson.cs.r2jt.absyn.ProgramOpExp;
import edu.clemson.cs.r2jt.absyn.ProgramParamExp;
import edu.clemson.cs.r2jt.absyn.RepresentationDec;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.absyn.SetExp;
import edu.clemson.cs.r2jt.absyn.SwapStmt;
import edu.clemson.cs.r2jt.absyn.UsesItem;
import edu.clemson.cs.r2jt.absyn.VarDec;
import edu.clemson.cs.r2jt.absyn.VarExp;
import edu.clemson.cs.r2jt.absyn.VariableArrayExp;
import edu.clemson.cs.r2jt.absyn.VariableDotExp;
import edu.clemson.cs.r2jt.absyn.VariableExp;
import edu.clemson.cs.r2jt.absyn.VariableNameExp;
import edu.clemson.cs.r2jt.absyn.WhileStmt;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.type.BooleanType;
import java.util.Iterator;

public class PreProcessor extends TreeWalkerStackVisitor {

    // ===========================================================
    // Global Variables 
    // ===========================================================

    // A counter used to keep track the number of things created by
    // the PreProcessor.
    private int myCounter;

    // Utilities class
    private Utilities myUtilities;

    // Error Handler
    private ErrorHandler myErr;

    // Map of all the local array types encountered
    private Map<String, NameTy> myArrayFacilityMap;

    // List of Concepts that gets added to the Uses List automatically
    private String[] myUsesItems =
            { "Location_Linking_Template_1", "Static_Array_Template" };

    // ===========================================================
    // Constructors
    // ===========================================================

    public PreProcessor(final CompileEnvironment instanceEnvironment) {
        myCounter = 1;
        myUtilities = new Utilities();
        myArrayFacilityMap = new Map<String, NameTy>();
        myErr = instanceEnvironment.getErrorHandler();
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

            //Check if we have a FacilityTypeDec, RepresentationDec or VarDec
            if (parent instanceof FacilityTypeDec) {
                // Set the Ty of the Parent
                ((FacilityTypeDec) parent).setRepresentation(newTy);
            }
            else if (parent instanceof RepresentationDec) {
                // Set the Ty of the Parent
                ((RepresentationDec) parent).setRepresentation(newTy);
            }
            else if (parent instanceof VarDec) {
                // Set the Ty of the Parent
                ((VarDec) parent).setTy(newTy);
            }

            // Create a list of arguments for the new FacilityDec
            List<ModuleArgumentItem> listItem = new List<ModuleArgumentItem>();
            String typeName = ((NameTy) ty.getEntryType()).getName().getName();

            // Add the type, Low and High for Arrays
            listItem.add(new ModuleArgumentItem(null, new PosSymbol(location,
                    Symbol.symbol(typeName)), null));
            listItem.add(new ModuleArgumentItem(null, null, ty.getLo()));
            listItem.add(new ModuleArgumentItem(null, null, ty.getHi()));

            // Call method to createFacilityDec
            FacilityDec arrayFacilityDec =
                    myUtilities.createFacilityDec(location, newArrayName,
                            "Static_Array_Template", "Std_Array_Realiz",
                            listItem, new List<ModuleArgumentItem>(),
                            new List<EnhancementItem>(),
                            new List<EnhancementBodyItem>());

            //Iterate through AST
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

            // Add the arrayFacilityDec to the list of Decs where it belongs
            myUtilities.addFacilityDec(it, arrayFacilityDec);

            // Saving the Ty of this ArrayFacility for future use
            myArrayFacilityMap.put(newArrayName, oldTy);
        }
        else {
            /** TODO: Error! Array not found! */
        }
    }

    // -----------------------------------------------------------
    // CallStmt
    // -----------------------------------------------------------

    @Override
    public void preCallStmt(CallStmt stmt) {
        // Variables
        PosSymbol name = stmt.getName();
        List<ProgramExp> argList = stmt.getArguments();
        boolean localOper = myUtilities.isLocalOper(name.getName());

        // Check if any of the arguments is a ProgramOpExp
        if (argList != null) {
            for (int i = 0; i < argList.size(); i++) {
                if (argList.get(i) instanceof ProgramOpExp) {
                    ProgramParamExp newExp =
                            replaceProgramOpExp((ProgramOpExp) argList.get(i));

                    if (newExp != null) {
                        argList.set(i, newExp);
                    }
                }
            }
        }

        // Check if the called operation is a local operation
        if (localOper) {
            // Iterators
            List<ParameterVarDec> parameterList =
                    myUtilities.retrieveParameterList(name.getName());

            // Make sure that we have the right operation
            if (parameterList.size() == argList.size()) {
                // Replaces the modified argument list
                stmt.setArguments(applyReplica(parameterList, argList));
            }
            else {
                /** TODO: Throw error because we got the wrong operation! */
            }

        }
        else {
            /** TODO: Invoke Hampton's new symbol table to look for the 
             * external operation.
             */
        }
    }

    @Override
    public void postCallStmt(CallStmt stmt) {
        // Variables
        PosSymbol stmtName = stmt.getName();
        List<ProgramExp> argList = stmt.getArguments();
        List<VarDec> explicitArgList = null;

        // Change the array syntactic sugar to use Static Array Template operations
        argList = arrayConversion(argList);

        // Add any new statements
        if (!myUtilities.isStatementListEmpty()) {
            // Ancestor iterator
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

            // Add the created swap statements
            myUtilities.loopAndAddStatements(it, stmtName.getLocation());

            // Clear the list
            myUtilities.clearStatementList();
        }

        // Put the argument list back to the statement
        stmt.setArguments(argList);
    }

    // -----------------------------------------------------------
    // ConceptBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        // Populate myUtilities with the initial things
        initRealization(dec.getDecs());
    }

    @Override
    public void postConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        // Cleanup myUtilities
        finalRealization();
    }

    // -----------------------------------------------------------
    // ConditionItem
    // -----------------------------------------------------------

    @Override
    public void preConditionItem(ConditionItem stmt) {
        // Check if the test condition is just a VariableExp
        if (stmt.getTest() instanceof VariableExp) {
            // Replace the test condition from "exp" to "exp = True()"
            stmt.setTest(createTrueExp(stmt.getTest()));
        }
        // Check if the test condition is a ProgramOpExp
        else if (stmt.getTest() instanceof ProgramOpExp) {
            ProgramParamExp newExp =
                    replaceProgramOpExp((ProgramOpExp) stmt.getTest());

            if (newExp != null) {
                stmt.setTest(newExp);
            }
        }
    }

    // -----------------------------------------------------------
    // EnhancementBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        // Populate myUtilities with the initial things
        initRealization(dec.getDecs());
    }

    @Override
    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        // Cleanup myUtilities
        finalRealization();
    }

    // -----------------------------------------------------------
    // FacilityModuleDec
    // -----------------------------------------------------------

    @Override
    public void preFacilityModuleDec(FacilityModuleDec dec) {
        // Populate myUtilities with the initial things
        initFacilities(dec.getDecs());
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec dec) {
        // Cleanup myUtilities
        finalFacilities();

        // Add files in the myUsesItem to the uses list
        List<UsesItem> usesList = dec.getUsesItems();
        Location location = usesList.get(0).getName().getLocation();
        usesList = addToUsesList(usesList, location);

        // Replace the uses item list
        dec.setUsesItems(usesList);
    }

    // -----------------------------------------------------------
    // FacilityOperationDec
    // -----------------------------------------------------------

    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        // Initialize the local variable and parameter lists
        initLocalVarLists(dec.getVariables(), dec.getParameters());
    }

    @Override
    public void postFacilityOperationDec(FacilityOperationDec dec) {
        // Temporary fix: Need to bring Integer/Boolean/Character/Char_Str into scope
        // Should be fixed at a later date - Chuck and Sami
        Location newLoc = dec.getName().getLocation();
        List<VarDec> localVarList = myUtilities.getLocalVarList();
        localVarList.add(new VarDec(new PosSymbol(newLoc, Symbol
                .symbol("_Integer")), new NameTy(null, new PosSymbol(newLoc,
                Symbol.symbol("Integer")))));
        localVarList.add(new VarDec(new PosSymbol(newLoc, Symbol
                .symbol("_Boolean")), new NameTy(null, new PosSymbol(newLoc,
                Symbol.symbol("Boolean")))));
        localVarList.add(new VarDec(new PosSymbol(newLoc, Symbol
                .symbol("_Character")), new NameTy(null, new PosSymbol(newLoc,
                Symbol.symbol("Character")))));
        localVarList.add(new VarDec(new PosSymbol(newLoc, Symbol
                .symbol("_Char_Str")), new NameTy(null, new PosSymbol(newLoc,
                Symbol.symbol("Char_Str")))));

        // Put the modified variable list into dec
        dec.setVariables(localVarList);

        // Set the list of local variables and parameter variables to null
        myUtilities.setLocalVarList(null);
        myUtilities.setParameterVarList(null);
    }

    // -----------------------------------------------------------
    // Function Assignment Statement
    // -----------------------------------------------------------

    @Override
    public void preFuncAssignStmt(FuncAssignStmt stmt) {
        // Variables
        ProgramExp oldExp = stmt.getAssign();

        // Apply the Replica function to any VariableNameExp
        if (oldExp instanceof VariableNameExp) {
            // Set new right hand side to stmt
            stmt.setAssign(myUtilities.createReplicaExp(oldExp));
        }
        //Apply the Replica function to any VariableDotExp that doesn't contain an array
        else if (oldExp instanceof VariableDotExp) {
            // List of segments
            List<VariableExp> segs = ((VariableDotExp) oldExp).getSegments();

            // Check if the last element is an array or not
            if (!(segs.get(segs.size() - 1) instanceof VariableArrayExp)) {
                // Set new right hand side to stmt
                stmt.setAssign(myUtilities.createReplicaExp(oldExp));
            }
        }
        // Check if the assign statement is a ProgramOpExp
        else if (oldExp instanceof ProgramOpExp) {
            ProgramParamExp newExp = replaceProgramOpExp((ProgramOpExp) oldExp);

            if (newExp != null) {
                stmt.setAssign(newExp);
            }
        }
    }

    @Override
    public void postFuncAssignStmt(FuncAssignStmt stmt) {
        // Variables
        VariableExp leftExp = stmt.getVar();
        ProgramExp rightExp = stmt.getAssign();

        // Check if we need to convert the right hand side into a Entry_Replica
        // operation for arrays
        if (rightExp instanceof VariableArrayExp) {
            // Create the new ProgramExp
            ProgramExp newExp =
                    createEntryReplicaExp((VariableArrayExp) rightExp, rightExp
                            .getLocation());

            // Set new right hand side to stmt
            stmt.setAssign(newExp);
        }
        else if (rightExp instanceof VariableDotExp) {
            // List of segments
            List<VariableExp> segs = ((VariableDotExp) rightExp).getSegments();

            // Check if the last element is an array or not
            if (segs.get(segs.size() - 1) instanceof VariableArrayExp) {
                // Last Element
                VariableArrayExp lastElement =
                        (VariableArrayExp) segs.get(segs.size() - 1);

                // Replace the name
                segs.set(segs.size() - 1, new VariableNameExp(lastElement
                        .getLocation(), lastElement.getQualifier(), lastElement
                        .getName()));
                ((VariableDotExp) rightExp).setSegments(segs);

                // Create the new ProgramExp
                ProgramExp newExp =
                        createEntryReplicaExp((VariableDotExp) rightExp,
                                lastElement.getArgument(), rightExp
                                        .getLocation());

                // Set new right hand side to stmt
                stmt.setAssign(newExp);
            }
        }

        // Check if we need to convert this to Assign_Entry operation for arrays
        if (leftExp instanceof VariableArrayExp) {
            // Parameter List
            List<ProgramExp> params = new List<ProgramExp>();
            params.add(new VariableNameExp(leftExp.getLocation(),
                    ((VariableArrayExp) leftExp).getQualifier(),
                    ((VariableArrayExp) leftExp).getName()));
            params.add(stmt.getAssign());
            params.add(((VariableArrayExp) leftExp).getArgument());

            // Create the Assign_Entry operation
            CallStmt newStmt =
                    new CallStmt(null, new PosSymbol(stmt.getLocation(), Symbol
                            .symbol("Assign_Entry")), params);

            // Ancestor Iterator
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

            // Add the created call stmt to the right place
            myUtilities.replaceStmt(it, stmt, newStmt);
        }

        // Check if we have any swap statements we need to add
        if (!myUtilities.isSwapCallListEmpty()) {
            // Ancestor iterator
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

            // Add the created swap statements
            myUtilities.loopAndAddSwapCalls(it, stmt.getLocation());

            // Clear the list
            myUtilities.clearSwapCallList();
        }
    }

    // -----------------------------------------------------------
    // IfStmt
    // -----------------------------------------------------------

    @Override
    public void preIfStmt(IfStmt stmt) {
        // Check if the test condition is just a VariableExp
        if (stmt.getTest() instanceof VariableExp) {
            // Replace the test condition from "exp" to "exp = True()"
            stmt.setTest(createTrueExp(stmt.getTest()));
        }
        // Check if the test condition is a ProgramOpExp
        else if (stmt.getTest() instanceof ProgramOpExp) {
            ProgramParamExp newExp =
                    replaceProgramOpExp((ProgramOpExp) stmt.getTest());

            if (newExp != null) {
                stmt.setTest(newExp);
            }
        }
    }

    // -----------------------------------------------------------
    // ProcedureDec
    // -----------------------------------------------------------

    @Override
    public void preProcedureDec(ProcedureDec dec) {
        // Initialize the local variable and parameter lists
        initLocalVarLists(dec.getVariables(), dec.getParameters());
    }

    @Override
    public void postProcedureDec(ProcedureDec dec) {
        // Temporary fix: Need to bring Integer/Boolean/Character/Char_Str into scope
        // Should be fixed at a later date - Chuck and Sami
        Location newLoc = dec.getName().getLocation();
        List<VarDec> localVarList = myUtilities.getLocalVarList();
        localVarList.add(new VarDec(new PosSymbol(newLoc, Symbol
                .symbol("_Integer")), new NameTy(null, new PosSymbol(newLoc,
                Symbol.symbol("Integer")))));
        localVarList.add(new VarDec(new PosSymbol(newLoc, Symbol
                .symbol("_Boolean")), new NameTy(null, new PosSymbol(newLoc,
                Symbol.symbol("Boolean")))));
        localVarList.add(new VarDec(new PosSymbol(newLoc, Symbol
                .symbol("_Character")), new NameTy(null, new PosSymbol(newLoc,
                Symbol.symbol("Character")))));
        localVarList.add(new VarDec(new PosSymbol(newLoc, Symbol
                .symbol("_Char_Str")), new NameTy(null, new PosSymbol(newLoc,
                Symbol.symbol("Char_Str")))));

        // Put the modified variable list into dec
        dec.setVariables(localVarList);

        // Set the list of local variables and parameter variables to null
        myUtilities.setLocalVarList(null);
        myUtilities.setParameterVarList(null);
    }

    // -----------------------------------------------------------
    // ProgramOpExp
    // -----------------------------------------------------------

    @Override
    public void preProgramOpExp(ProgramOpExp exp) {
        // Variables
        ProgramExp firstExp = exp.getFirst();
        ProgramExp secondExp = exp.getSecond();

        // Check if the firstExp is a ProgramOpExp
        if (firstExp instanceof ProgramOpExp) {
            ProgramParamExp newExp =
                    replaceProgramOpExp((ProgramOpExp) firstExp);

            if (newExp != null) {
                exp.setFirst(newExp);
            }
        }

        // Check if the second is a ProgramOpExp
        if (secondExp instanceof ProgramOpExp) {
            ProgramParamExp newExp =
                    replaceProgramOpExp((ProgramOpExp) secondExp);

            if (newExp != null) {
                exp.setSecond(newExp);
            }
        }
    }

    @Override
    public void postProgramOpExp(ProgramOpExp exp) {
        // Variables
        ProgramExp firstExp = exp.getFirst();
        ProgramExp secondExp = exp.getSecond();

        // Check first to see if it is an array
        if (firstExp instanceof VariableArrayExp) {
            // Create the new ProgramExp
            ProgramExp newExp =
                    createEntryReplicaExp((VariableArrayExp) firstExp, firstExp
                            .getLocation());

            // Set the newExp to first of exp
            exp.setFirst(newExp);
        }
        else if (firstExp instanceof VariableDotExp) {
            // List of segments
            List<VariableExp> segs = ((VariableDotExp) firstExp).getSegments();

            // Check if the last element is an array or not
            if (segs.get(segs.size() - 1) instanceof VariableArrayExp) {
                // Last Element
                VariableArrayExp lastElement =
                        (VariableArrayExp) segs.get(segs.size() - 1);

                // Replace the name
                segs.set(segs.size() - 1, new VariableNameExp(lastElement
                        .getLocation(), lastElement.getQualifier(), lastElement
                        .getName()));
                ((VariableDotExp) firstExp).setSegments(segs);

                // Create the new ProgramExp
                ProgramExp newExp =
                        createEntryReplicaExp((VariableDotExp) firstExp,
                                lastElement.getArgument(), firstExp
                                        .getLocation());

                // Set the newExp to first of exp
                exp.setFirst(newExp);
            }
        }

        // Check second to see if it is an array
        if (secondExp instanceof VariableArrayExp) {
            // Create the new ProgramExp
            ProgramExp newExp =
                    createEntryReplicaExp((VariableArrayExp) secondExp,
                            secondExp.getLocation());

            // Set the newExp to first of exp
            exp.setSecond(newExp);
        }
        else if (secondExp instanceof VariableDotExp) {
            // List of segments
            List<VariableExp> segs = ((VariableDotExp) secondExp).getSegments();

            // Check if the last element is an array or not
            if (segs.get(segs.size() - 1) instanceof VariableArrayExp) {
                // Last Element
                VariableArrayExp lastElement =
                        (VariableArrayExp) segs.get(segs.size() - 1);

                // Replace the name
                segs.set(segs.size() - 1, new VariableNameExp(lastElement
                        .getLocation(), lastElement.getQualifier(), lastElement
                        .getName()));
                ((VariableDotExp) secondExp).setSegments(segs);

                // Create the new ProgramExp
                ProgramExp newExp =
                        createEntryReplicaExp((VariableDotExp) secondExp,
                                lastElement.getArgument(), secondExp
                                        .getLocation());

                // Set the newExp to first of exp
                exp.setSecond(newExp);
            }
        }
    }

    // -----------------------------------------------------------
    // ProgramParamExp
    // -----------------------------------------------------------

    @Override
    public void preProgramParamExp(ProgramParamExp exp) {
        // Variables
        PosSymbol name = exp.getName();
        List<ProgramExp> argList = exp.getArguments();
        boolean localOper = myUtilities.isLocalOper(name.getName());

        // Check if any of the arguments is a ProgramOpExp
        if (argList != null) {
            for (int i = 0; i < argList.size(); i++) {
                if (argList.get(i) instanceof ProgramOpExp) {
                    ProgramParamExp newExp =
                            replaceProgramOpExp((ProgramOpExp) argList.get(i));

                    if (newExp != null) {
                        argList.set(i, newExp);
                    }
                }
            }
        }

        // Check if the called operation is a local operation
        if (localOper) {
            // Iterators
            List<ParameterVarDec> parameterList =
                    myUtilities.retrieveParameterList(name.getName());

            // Make sure that we have the right operation
            if (parameterList.size() == argList.size()) {
                // For each Evaluates mode parameter, if it is not a 
                // function call, we need to apply Replica to it.
                exp.setArguments(applyReplica(parameterList, argList));
            }
            else {
                /** TODO: Throw error because we got the wrong operation! */
            }

        }
        else {
            /** TODO: Invoke Hampton's new symbol table to look for the 
             * external operation.
             */
        }
    }

    @Override
    public void postProgramParamExp(ProgramParamExp exp) {
        // Variables
        List<ProgramExp> argList = exp.getArguments();
        Iterator<ResolveConceptualElement> it = this.getAncestorInterator();
        FuncAssignStmt stmt = null;

        // Loop until we find our parent
        while (it.hasNext() && stmt == null) {
            ResolveConceptualElement temp = it.next();

            if (temp instanceof FuncAssignStmt) {
                stmt = (FuncAssignStmt) temp;
            }
        }

        // Change the array syntactic sugar to use Static Array Template operations
        argList = arrayConversion(argList);

        // Add any new statements
        if (!myUtilities.isStatementListEmpty()) {
            // Ancestor iterator
            it = this.getAncestorInterator();

            // Add the created swap statements
            myUtilities.loopAndAddStatements(it, stmt.getLocation());

            // Clear the list
            myUtilities.clearStatementList();
        }

        // Put the argument list back to the program expression
        exp.setArguments(argList);
    }

    // -----------------------------------------------------------
    // Set Expression
    // ----------------------------------------------------------- 

    @Override
    public void preSetExp(SetExp exp) {
        // Check that we have syntactic sugar for empty set
        if (exp.getVar() == null && exp.getVars().isEmpty()) {
            // Convert to appropriate notation
            PosSymbol name = new PosSymbol(null, Symbol.symbol("empty_set"));
            VarExp emptySetExp = new VarExp(exp.getLocation(), null, name, 0);

            // Add it to the appropriate location in the ModuleDec
            ResolveConceptualElement parent = this.getAncestor(1);
            if (parent instanceof EqualsExp) {
                ((EqualsExp) parent).setRight(emptySetExp);
            }
        }
    }

    // -----------------------------------------------------------
    // Swap Statement
    // ----------------------------------------------------------- 

    @Override
    public void postSwapStmt(SwapStmt stmt) {
        // Ancestor Iterator
        Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

        // Variables
        Location currentLocation = stmt.getLocation();
        List<ProgramExp> expList = new List<ProgramExp>();

        // Check if both left hand side and right hand side is a VariableArrayExp
        if (stmt.getLeft() instanceof VariableArrayExp
                && stmt.getRight() instanceof VariableArrayExp) {
            // Check if the names of the array is the same
            VariableArrayExp left = (VariableArrayExp) stmt.getLeft();
            VariableArrayExp right = (VariableArrayExp) stmt.getRight();
            if (left.getName().getName().equals(right.getName().getName())) {
                // Create the arguments
                expList.add(new VariableNameExp(stmt.getLocation(), null, left
                        .getName()));
                expList.add(left.getArgument());
                expList.add(right.getArgument());

                // Create a CallStmt
                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(currentLocation,
                                Symbol.symbol("Swap_Two_Entries")), expList);

                // Add the created swap stmt to the right place
                myUtilities.replaceStmt(it, stmt, swapEntryStmt);
            }
        }
        else {
            // Check the left statement
            if (stmt.getLeft() instanceof VariableArrayExp) {
                // Check right hand side for VariableDotExp with Array
                boolean containArray = false;
                if (stmt.getRight() instanceof VariableDotExp) {
                    List<VariableExp> rightVarExpList =
                            ((VariableDotExp) stmt.getRight()).getSegments();
                    VariableExp rightLastExp =
                            rightVarExpList.get(rightVarExpList.size() - 1);
                    if (rightLastExp instanceof VariableArrayExp) {
                        containArray = true;
                    }
                }

                // If it doesn't contain array or is simply a VariableNameExp
                if (containArray == false) {
                    // Add the arguments necessary for Swap_Entry in Static_Array_Template
                    expList.add(new VariableNameExp(stmt.getLocation(), null,
                            ((VariableArrayExp) stmt.getLeft()).getName()));
                    expList.add(stmt.getRight());
                    expList.add(((VariableArrayExp) stmt.getLeft())
                            .getArgument());
                }

                // Create a CallStmt
                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(currentLocation,
                                Symbol.symbol("Swap_Entry")), expList);

                // Add the created swap stmt to the right place
                myUtilities.replaceStmt(it, stmt, swapEntryStmt);
            }
            // Check the right statement
            else if (stmt.getRight() instanceof VariableArrayExp) {
                // Check right hand side for VariableDotExp with Array
                boolean containArray = false;
                if (stmt.getLeft() instanceof VariableDotExp) {
                    List<VariableExp> leftVarExpList =
                            ((VariableDotExp) stmt.getLeft()).getSegments();
                    VariableExp leftLastExp =
                            leftVarExpList.get(leftVarExpList.size() - 1);
                    if (leftLastExp instanceof VariableArrayExp) {
                        containArray = true;
                    }
                }

                // If it doesn't contain array or is simply a VariableNameExp
                if (containArray == false) {
                    // Add the arguments necessary for Swap_Entry in Static_Array_Template
                    expList.add(new VariableNameExp(stmt.getLocation(), null,
                            ((VariableArrayExp) stmt.getRight()).getName()));
                    expList.add(stmt.getLeft());
                    expList.add(((VariableArrayExp) stmt.getRight())
                            .getArgument());
                }

                // Create a CallStmt
                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(currentLocation,
                                Symbol.symbol("Swap_Entry")), expList);

                // Add the created swap stmt to the right place
                myUtilities.replaceStmt(it, stmt, swapEntryStmt);
            }
        }

        // Check if left hand side and right hand side is a VariableDotExp
        if (stmt.getLeft() instanceof VariableDotExp
                && stmt.getRight() instanceof VariableDotExp) {
            // Variables
            List<VariableExp> leftVarExpList =
                    ((VariableDotExp) stmt.getLeft()).getSegments();
            List<VariableExp> rightVarExpList =
                    ((VariableDotExp) stmt.getRight()).getSegments();

            // If this VariableDotExp is really a VariableArrayExp
            VariableExp leftLastExp =
                    leftVarExpList.get(leftVarExpList.size() - 1);
            VariableExp rightLastExp =
                    rightVarExpList.get(rightVarExpList.size() - 1);

            if (leftLastExp instanceof VariableArrayExp
                    && rightLastExp instanceof VariableArrayExp
                    && leftVarExpList.size() == rightVarExpList.size()) {
                boolean isSame = true;
                for (int i = 0; i < leftVarExpList.size() - 1 && isSame; ++i) {
                    VariableExp temp1 = rightVarExpList.get(i);
                    VariableExp temp2 = leftVarExpList.get(i);
                    // Check if it is a VariableArrayExp
                    if (temp1 instanceof VariableArrayExp
                            && temp2 instanceof VariableArrayExp) {
                        if (!(((VariableArrayExp) temp1).getName().getName()
                                .equals(((VariableArrayExp) temp2).getName()
                                        .getName()))) {
                            isSame = false;
                        }
                    }
                    // Check if it is a VariableNameExpelse 
                    if (temp1 instanceof VariableNameExp
                            && temp2 instanceof VariableNameExp) {
                        if (!((VariableNameExp) temp1).getName().getName()
                                .equals(
                                        ((VariableNameExp) temp2).getName()
                                                .getName())) {
                            isSame = false;
                        }
                    }
                    //Else they are not the same
                    else {
                        isSame = false;
                    }
                }

                // Can only handle arrays with the same name
                if (isSame) {
                    // Create a new VariableNameExp
                    leftVarExpList
                            .set(leftVarExpList.size() - 1,
                                    new VariableNameExp(currentLocation, null,
                                            ((VariableArrayExp) leftLastExp)
                                                    .getName()));
                    VariableDotExp newDotExp =
                            ((VariableDotExp) stmt.getLeft());
                    newDotExp.setSegments(leftVarExpList);

                    // Create the arguments
                    expList.add(newDotExp);
                    expList.add(((VariableArrayExp) leftLastExp).getArgument());
                    expList
                            .add(((VariableArrayExp) rightLastExp)
                                    .getArgument());

                    // Create a CallStmt
                    CallStmt swapEntryStmt =
                            new CallStmt(null, new PosSymbol(currentLocation,
                                    Symbol.symbol("Swap_Two_Entries")), expList);

                    // Add the created swap stmt to the right place
                    myUtilities.replaceStmt(it, stmt, swapEntryStmt);
                }
                // Error
                else {
                    /** TODO: Handle error right here! */
                }
            }
            // If the leftLastExp is a Variable Array Expression
            else if (leftLastExp instanceof VariableArrayExp) {
                // Create a new VariableNameExp
                leftVarExpList.set(leftVarExpList.size() - 1,
                        new VariableNameExp(currentLocation, null,
                                ((VariableArrayExp) leftLastExp).getName()));
                VariableDotExp newDotExp = ((VariableDotExp) stmt.getLeft());
                newDotExp.setSegments(leftVarExpList);

                // Create the arguments
                expList.add(newDotExp);
                expList.add(((VariableDotExp) stmt.getRight()));
                expList.add(((VariableArrayExp) leftLastExp).getArgument());

                // Create a CallStmt
                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(currentLocation,
                                Symbol.symbol("Swap_Entry")), expList);

                // Add the created swap stmt to the right place
                myUtilities.replaceStmt(it, stmt, swapEntryStmt);
            }
            // If the rightLastExp is a Variable Array Expression
            else if (rightLastExp instanceof VariableArrayExp) {
                // Create a new VariableNameExp
                rightVarExpList.set(rightVarExpList.size() - 1,
                        new VariableNameExp(currentLocation, null,
                                ((VariableArrayExp) rightLastExp).getName()));
                VariableDotExp newDotExp = ((VariableDotExp) stmt.getRight());
                newDotExp.setSegments(rightVarExpList);

                // Create the arguments
                expList.add(newDotExp);
                expList.add(((VariableDotExp) stmt.getLeft()));
                expList.add(((VariableArrayExp) rightLastExp).getArgument());

                // Create a CallStmt
                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(currentLocation,
                                Symbol.symbol("Swap_Entry")), expList);

                // Add the created swap stmt to the right place
                myUtilities.replaceStmt(it, stmt, swapEntryStmt);
            }
        }
        else {
            // Check if left hand side is a VariableDotExp and right hand side is not an array
            if (stmt.getLeft() instanceof VariableDotExp
                    && stmt.getRight() instanceof VariableNameExp) {
                // Variables
                List<VariableExp> leftVarExpList =
                        ((VariableDotExp) stmt.getLeft()).getSegments();
                VariableExp leftLastExp =
                        leftVarExpList.get(leftVarExpList.size() - 1);

                // Do replacement if leftLastExp is an array
                if (leftLastExp instanceof VariableArrayExp) {
                    // Create a new VariableNameExp
                    leftVarExpList
                            .set(leftVarExpList.size() - 1,
                                    new VariableNameExp(currentLocation, null,
                                            ((VariableArrayExp) leftLastExp)
                                                    .getName()));
                    VariableDotExp newDotExp =
                            ((VariableDotExp) stmt.getLeft());
                    newDotExp.setSegments(leftVarExpList);

                    // Create the arguments
                    expList.add(newDotExp);
                    expList.add(stmt.getRight());
                    expList.add(((VariableArrayExp) leftLastExp).getArgument());

                    // Create a CallStmt
                    CallStmt swapEntryStmt =
                            new CallStmt(null, new PosSymbol(currentLocation,
                                    Symbol.symbol("Swap_Entry")), expList);

                    // Add the created swap stmt to the right place
                    myUtilities.replaceStmt(it, stmt, swapEntryStmt);
                }
            }
            // Check if right hand side is a VariableDotExp and left hand side is not an array
            else if (stmt.getRight() instanceof VariableDotExp
                    && stmt.getLeft() instanceof VariableNameExp) {
                // Variables
                List<VariableExp> rightVarExpList =
                        ((VariableDotExp) stmt.getRight()).getSegments();
                VariableExp rightLastExp =
                        rightVarExpList.get(rightVarExpList.size() - 1);

                // Do replacement if leftLastExp is an array
                if (rightLastExp instanceof VariableArrayExp) {
                    // Create a new VariableNameExp
                    rightVarExpList
                            .set(rightVarExpList.size() - 1,
                                    new VariableNameExp(currentLocation, null,
                                            ((VariableArrayExp) rightLastExp)
                                                    .getName()));
                    VariableDotExp newDotExp =
                            ((VariableDotExp) stmt.getRight());
                    newDotExp.setSegments(rightVarExpList);

                    // Create the arguments
                    expList.add(newDotExp);
                    expList.add(stmt.getLeft());
                    expList
                            .add(((VariableArrayExp) rightLastExp)
                                    .getArgument());

                    // Create a CallStmt
                    CallStmt swapEntryStmt =
                            new CallStmt(null, new PosSymbol(currentLocation,
                                    Symbol.symbol("Swap_Entry")), expList);

                    // Add the created swap stmt to the right place
                    myUtilities.replaceStmt(it, stmt, swapEntryStmt);
                }
            }
        }
    }

    // -----------------------------------------------------------
    // WhileStmt
    // -----------------------------------------------------------

    @Override
    public void preWhileStmt(WhileStmt stmt) {
        // Check if the test condition is just a VariableExp
        if (stmt.getTest() instanceof VariableExp) {
            // Replace the test condition from "exp" to "exp = True()"
            stmt.setTest(createTrueExp(stmt.getTest()));
        }
        // Check if the test condition is a ProgramOpExp
        else if (stmt.getTest() instanceof ProgramOpExp) {
            ProgramParamExp newExp =
                    replaceProgramOpExp((ProgramOpExp) stmt.getTest());

            if (newExp != null) {
                stmt.setTest(newExp);
            }
        }
    }

    @Override
    public void postWhileStmt(WhileStmt stmt) {
        // Variables
        Location whileLoc = stmt.getLocation();
        List<VariableExp> changingVarList = stmt.getChanging();
        Exp exp = stmt.getMaintaining();

        // Check if the list is empty
        if (changingVarList == null) {
            changingVarList = new List<VariableExp>();

            // Add all local variables and parameters
            changingVarList =
                    formNewVariableExpList(whileLoc, myUtilities
                            .getLocalVarList(), changingVarList);
            changingVarList =
                    formNewVariableExpList(whileLoc, myUtilities
                            .getParameterVarList(), changingVarList);

            // Add the list back into the while statement
            stmt.setChanging(changingVarList);
        }

        // Check if we have a maintaining or not
        if (exp == null) {
            // Create a boolean type instance
            BooleanType b = BooleanType.INSTANCE;

            // Create the new Exp
            Exp newExp =
                    new VarExp(whileLoc, null, new PosSymbol(whileLoc, Symbol
                            .symbol("true")));

            // Set the type to boolean
            newExp.setType(b);

            // Replace the old maintaining clause with this new one
            stmt.setMaintaining(newExp);
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Import any additional <code>ModuleDec</code> for <code>
     * FacilityModuleDec</code>
     * </p>
     * 
     * @param list The list containing all the <code>UsesItem</code>
     * @param location Location to be assigned to a new <code>UsesItem
     * </code>
     * 
     * @return The modified uses list
     */
    private List<UsesItem> addToUsesList(List<UsesItem> usesList,
            Location location) {
        for (String s : myUsesItems) {
            // Check if the file is already in the uses list or not
            boolean isThere = false;
            Iterator<UsesItem> it = usesList.iterator();
            while (it.hasNext()) {
                if (it.next().getName().getName().equals(s)) {
                    isThere = true;
                }
            }

            // Add file to uses list if it is not already there
            if (!isThere) {
                usesList.add(new UsesItem(new PosSymbol(location, Symbol
                        .symbol(s))));
            }
        }

        return usesList;
    }

    /**
     * <p>
     * Applies the Replica call to any <code>VariableExp</code> where
     * the Evaluates mode is specified.
     * </p>
     * 
     * @param parameterList The list of <code>ParameterVarDec</code>, which
     * contains the modes.
     * @param argList The list of arguments being used to invoke the current
     * operation/procedure.
     * </code>
     * 
     * @return The modified argument list
     */
    private List<ProgramExp> applyReplica(List<ParameterVarDec> parameterList,
            List<ProgramExp> argList) {
        List<ProgramExp> modifiedArgList = new List<ProgramExp>();

        // Loop
        Iterator<ParameterVarDec> parameterIt = parameterList.iterator();
        Iterator<ProgramExp> argListIt = argList.iterator();
        while (argListIt.hasNext() && parameterIt.hasNext()) {
            // Temp Variables
            ProgramExp tempExp = argListIt.next();
            ParameterVarDec tempParam = parameterIt.next();

            if (tempParam.getMode() == Mode.EVALUATES) {
                // Check if it is a VariableNameExp or VariableDotExp
                if (tempExp instanceof VariableNameExp
                        || tempExp instanceof VariableDotExp) {
                    // Creates a call to replica and modifies the original Exp
                    tempExp = myUtilities.createReplicaExp(tempExp);
                }
            }

            // Add it to the modified list
            modifiedArgList.add(tempExp);
        }

        return modifiedArgList;
    }

    /**
     * <p>
     * Converts any <code>VariableArrayExp</code> to a <code>VariableNameExp
     * </code> by creating a new <code>Exp</code> and applying swap calls from
     * the Static Array Template.
     * </p>
     * 
     * @param argList The list of arguments being used to invoke the current
     * operation/procedure.
     * </code>
     * 
     * @return The modified argument list
     */
    private List<ProgramExp> arrayConversion(List<ProgramExp> argList) {
        // Iterate through argument list
        for (int i = 0; i < argList.size(); i++) {
            // Temp variable
            ProgramExp temp = argList.get(i);

            // Check if it is a VariableArrayExp
            if (temp instanceof VariableArrayExp) {
                // Variable
                VariableNameExp retval =
                        createNewVariableArrayExp(temp.getLocation(),
                                (VariableArrayExp) temp);
                argList.set(i, retval);
            }
            // Check if it is a VariableDotExp containing an array
            else if (temp instanceof VariableDotExp) {
                // Get list of segments
                List<VariableExp> segList =
                        ((VariableDotExp) temp).getSegments();
                VariableExp firstElement = segList.get(0);
                VariableExp lastElement = segList.get(segList.size() - 1);

                // Check if the last entry is an instance of array. EX: S.A[i]
                if (lastElement instanceof VariableArrayExp) {
                    VariableNameExp retval =
                            createNewVariableArrayExp(temp.getLocation(),
                                    (VariableNameExp) firstElement,
                                    (VariableArrayExp) lastElement);

                    argList.set(i, retval);
                }
            }
        }

        return argList;
    }

    /**
     * <p>
     * Create a new <code>ProgramExp</code> so that in the case that we 
     * have a <code>VariableExp</code> as our condition, it will be 
     * syntactic sugar for saying <code>VariableExp = True()</code>
     * </p>
     * 
     * @param exp The <code>VariableExp</code> is in the user's condition
     * 
     * @return The new condition expression.
     */
    private ProgramExp createTrueExp(ProgramExp exp) {
        // Create a new True() function call
        ProgramExp trueExp =
                new ProgramParamExp(exp.getLocation(), new PosSymbol(exp
                        .getLocation(), Symbol.symbol("True")),
                        new List<ProgramExp>(), null);

        // Create a new Exp of the form "exp = True()"
        ProgramExp retExp =
                new ProgramOpExp(exp.getLocation(), 3, exp, trueExp);

        return retExp;
    }

    /**
     * <p>
     * Create a <code>Entry_Replica</code> call.
     * </p>
     *
     * @param exp The array variable
     * @param location A location of the array variable
     *
     * @return A new call to <code>Entry_Replica</code> from the <code>
     * Static_Array_Template</code>.
     */
    private ProgramExp createEntryReplicaExp(VariableArrayExp exp,
            Location location) {
        // Parameter List
        List<ProgramExp> params = new List<ProgramExp>();
        params.add(new VariableNameExp(exp.getLocation(), exp.getQualifier(),
                exp.getName()));
        params.add(exp.getArgument());

        // Create new right hand side with the Replica function
        ProgramExp newExp =
                new ProgramParamExp(location, new PosSymbol(location, Symbol
                        .symbol("Entry_Replica")), params, null);

        return newExp;
    }

    /**
     *  <p>
     * Create a <code>Entry_Replica</code> call.
     * </p>
     *
     * @param exp The array variable inside a VariableDotExp
     * @param argument The index of the array
     * @param location A location of the array variable
     * 
     * @return A new call to <code>Entry_Replica</code> from the <code>
     * Static_Array_Template</code>.
     */
    private ProgramExp createEntryReplicaExp(VariableDotExp exp,
            ProgramExp argument, Location location) {
        // Parameter List
        List<ProgramExp> params = new List<ProgramExp>();
        params.add(exp);
        params.add(argument);

        // Create new right hand side with the Replica function
        ProgramExp newExp =
                new ProgramParamExp(location, new PosSymbol(location, Symbol
                        .symbol("Entry_Replica")), params, null);

        return newExp;
    }

    /**
     * <p>
     * Creates a new <code>VariableArrayExp</code> so we can pass it as 
     * a parameter.
     * </p>
     *
     * @param location A location of the array variable
     * @param oldExp The array variable
     * 
     * @return The new array variable.
     */
    private VariableNameExp createNewVariableArrayExp(Location location,
            VariableArrayExp oldExp) {
        // Create new VariableNameExp
        PosSymbol newName =
                new PosSymbol(location, Symbol.symbol("_ArrayExp_"
                        + oldExp.getName().getName() + "_" + myCounter++));
        VariableNameExp newExp = new VariableNameExp(location, null, newName);

        // Create new VariableNameExp for the index
        PosSymbol newIndexName =
                new PosSymbol(location, Symbol.symbol("_ArrayIndex_"
                        + oldExp.getName().getName() + "_" + myCounter++));
        VariableNameExp newIndexExp =
                new VariableNameExp(location, null, newIndexName);
        NameTy integerTy =
                new NameTy(null, new PosSymbol(location, Symbol
                        .symbol("Integer")));

        // Locate the array declaration so can get the Ty of the the variables
        // inside the array
        VarDec arrayVarDec = myUtilities.returnVarDec(oldExp.getName());
        NameTy facilityTy = (NameTy) arrayVarDec.getTy();
        NameTy ty = myArrayFacilityMap.get(facilityTy.getQualifier().getName());

        // Create and add this new index statement
        FuncAssignStmt newStmt = new FuncAssignStmt();
        newStmt.setLocation(location);
        newStmt.setVar(newIndexExp);

        if (oldExp.getArgument() instanceof VariableExp) {
            newStmt.setAssign(myUtilities
                    .createReplicaExp(oldExp.getArgument()));
        }
        else {
            newStmt.setAssign(oldExp.getArgument());
        }

        myUtilities.addToStatementList(newStmt);

        // Create a new VarDec
        VarDec newVarDec = new VarDec(newName, ty);
        VarDec indexVarDec = new VarDec(newIndexName, integerTy);

        // Add it to our local variable list
        myUtilities.addToLocalVarList(newVarDec);
        myUtilities.addToLocalVarList(indexVarDec);

        // List
        List<ProgramExp> expList = new List<ProgramExp>();

        // Create the argument list and add the arguments necessary for
        // Swap_Entry in Static_Array_Template
        expList.add(new VariableNameExp(location, null, oldExp.getName()));
        expList.add(newExp);
        expList.add(newIndexExp);

        // Create a CallStmt
        CallStmt swapEntryStmt =
                new CallStmt(null, new PosSymbol(location, Symbol
                        .symbol("Swap_Entry")), expList);
        myUtilities.addToSwapList(swapEntryStmt);

        // Return created variable
        return newExp;
    }

    /**
     * Creates a new VariableArrayExp so we can pass it as a parameter if the
     * old VariableArrayExp is inside a record.
     *
     * @param location A location of the array variable
     * @param recordExp The name of the record
     * @param arrayExp The name of the array
     *
     */
    private VariableNameExp createNewVariableArrayExp(Location location,
            VariableNameExp recordExp, VariableArrayExp arrayExp) {
        // Create new VariableNameExp
        PosSymbol newName =
                new PosSymbol(location, Symbol.symbol("_Array_"
                        + arrayExp.getName().getName() + "_" + myCounter++));
        VariableNameExp newExp = new VariableNameExp(location, null, newName);

        // Create new VariableNameExp for the index
        PosSymbol newIndexName =
                new PosSymbol(location, Symbol.symbol("_ArrayIndex_"
                        + arrayExp.getName().getName() + "_" + myCounter++));
        VariableNameExp newIndexExp =
                new VariableNameExp(location, null, newIndexName);
        NameTy integerTy =
                new NameTy(null, new PosSymbol(location, Symbol
                        .symbol("Integer")));

        // Locate the array declaration so can get the ty of the the variables
        // inside the array
        VarDec arrayVarDec = myUtilities.returnVarDec(recordExp.getName());

        // Proceed if we found the arrayVarDec
        if (arrayVarDec != null) {
            // Create and add this new index statement
            FuncAssignStmt newStmt = new FuncAssignStmt();
            newStmt.setLocation(location);
            newStmt.setVar(newIndexExp);

            if (arrayExp.getArgument() instanceof VariableExp) {
                newStmt.setAssign(myUtilities.createReplicaExp(arrayExp
                        .getArgument()));
            }
            else {
                newStmt.setAssign(arrayExp.getArgument());
            }

            myUtilities.addToStatementList(newStmt);

            // Retrieve name of the ArrayTy
            String tyName =
                    myUtilities.retrieveTyNameFromRecords(arrayExp,
                            (NameTy) arrayVarDec.getTy());

            // Create a new VarDec
            VarDec newVarDec =
                    new VarDec(newName, myArrayFacilityMap.get(tyName));
            VarDec indexVarDec = new VarDec(newIndexName, integerTy);

            // Add it to our local variable list
            myUtilities.addToLocalVarList(newVarDec);
            myUtilities.addToLocalVarList(indexVarDec);

            // List
            List<ProgramExp> expList = new List<ProgramExp>();

            // Create the argument list and add the arguments necessary for
            // Swap_Entry in Static_Array_Template
            expList.add(new VariableNameExp(location, null,
                    ((VariableArrayExp) arrayExp).getName()));
            expList.add(newExp);
            expList.add(((VariableArrayExp) arrayExp).getArgument());

            // Create a CallStmt
            CallStmt swapEntryStmt =
                    new CallStmt(null, new PosSymbol(location, Symbol
                            .symbol("Swap_Entry")), expList);
            myUtilities.addToSwapList(swapEntryStmt);
        }

        return newExp;
    }

    /**
     * <p>
     * Performs cleanup the objects that the <code>PreProcessor</code>
     * stored from <code>FacilityModuleDec</code>.
     * </p>
     */
    private void finalFacilities() {
        // Set list containing records to null
        myUtilities.setFacilityTypeList(null);

        // Set list containing global variables to null
        myUtilities.setGlobalVarList(null);
    }

    /**
     * <p>
     * Performs cleanup the objects that the <code>PreProcessor</code>
     * stored from <code>ConceptBodyModuleDec</code> or <code>
     * EnhancementBodyModuleDec</code>.
     * </p>
     */
    private void finalRealization() {
        // Set list containing records to null
        myUtilities.setRepresentationDecList(null);

        // Set list containing global variables to null
        myUtilities.setGlobalVarList(null);
    }

    /**
     * Forms new <code>VariableExp</code> from the list of declared
     * <code>VarDec</code> and adds it if it doesn't already exist
     * in our <code>VariableExp</code> list.
     *
     * @param location Location in the file
     * @param decList List of variable declarations
     * @param list List of current <code>VariableExp</code>
     *
     * @return New list containing all the <code>VariableExp</code>.
     */
    private List<VariableExp> formNewVariableExpList(Location location,
            List<VarDec> decList, List<VariableExp> list) {
        // Iterate and add non duplicates
        Iterator<VarDec> it = decList.iterator();
        while (it.hasNext()) {
            // Temp
            VarDec temp = it.next();

            // New VariableNameExp
            VariableNameExp changingExp =
                    new VariableNameExp(location, null, temp.getName());

            // Check for duplicates
            if (!list.contains(changingExp)) {
                list.add(changingExp);
            }
        }

        return list;
    }

    /**
     * <p>
     * Obtains the initial elements needed for the <code>PreProcessor
     * </code> from <code>FacilityModuleDec</code>.
     * </p>
     * 
     * @param decList The list containing all the <code>Dec</code>.
     */
    private void initFacilities(List<Dec> decList) {
        // Retrieve any global variables or records from the list of Decs.
        myUtilities.retrieveRecordGVar(decList);

        // Retrieve the list of operations
        myUtilities.retrieveLocalProc(decList);
    }

    /**
     * <p>
     * Obtains the parameter and local variables for this procedure/operation. 
     * </p>
     * 
     * @param vList The list containing all local <code>VarDec</code>.
     * @param pList The list containing the parameter <code>VarDec</code>.
     */
    private void initLocalVarLists(List<VarDec> vList,
            List<ParameterVarDec> pList) {
        // Set the list of local variables
        myUtilities.setLocalVarList(vList);

        // Form VarDec from the list of parameter variables
        List<VarDec> parameterVarList = new List<VarDec>();
        Iterator<ParameterVarDec> it = pList.iterator();
        while (it.hasNext()) {
            ParameterVarDec temp = it.next();
            VarDec newVarDec = new VarDec(temp.getName(), temp.getTy());
            parameterVarList.add(newVarDec);
        }

        myUtilities.setParameterVarList(parameterVarList);
    }

    /**
     * <p>
     * Obtains the initial elements needed for the <code>PreProcessor
     * </code> from <code>ConceptBodyModuleDec</code> or <code>
     * EnhancementBodyModuleDec</code>.
     * 
     * @param decList The list containing all the <code>Dec</code>
     */
    private void initRealization(List<Dec> decList) {
        // Retrieve any global variables or records from the list of Decs.
        myUtilities.retrieveRecordGVar(decList);

        // Retrieve the list of operations
        myUtilities.retrieveLocalProc(decList);
    }

    /**
     * <p>
     * Convert the syntactic sugar for addition, subtraction, and,
     * or, etc. into a function call
     * </p>
     * 
     * @param exp The syntactic sugar <code>Exp</code>
     * 
     * @return Newly created operation call.
     */
    private ProgramParamExp replaceProgramOpExp(ProgramOpExp exp) {
        /*
        // Variables
        int operator = exp.getOperator();
        ProgramParamExp newExp = null;
        PosSymbol operationName = null;
        Location loc = exp.getLocation();
        List<ProgramExp> args = new List<ProgramExp>();

        // Convert the syntatic sugar for built-in types
        switch (operator) {
        case ProgramOpExp.AND:
            operationName = new PosSymbol(loc, Symbol.symbol("And"));
            break;
        case ProgramOpExp.OR:
            operationName = new PosSymbol(loc, Symbol.symbol("Or"));
            break;
        case ProgramOpExp.EQUAL:
            operationName = new PosSymbol(loc, Symbol.symbol("Are_Equal"));
            break;
        case ProgramOpExp.NOT_EQUAL:
            operationName = new PosSymbol(loc, Symbol.symbol("Are_Not_Equal"));
            break;
        case ProgramOpExp.LT:
            operationName = new PosSymbol(loc, Symbol.symbol("Less"));
            break;
        case ProgramOpExp.LT_EQL:
            operationName = new PosSymbol(loc, Symbol.symbol("Less_Or_Equal"));
            break;
        case ProgramOpExp.GT:
            operationName = new PosSymbol(loc, Symbol.symbol("Greater"));
            break;
        case ProgramOpExp.GT_EQL:
            operationName =
                    new PosSymbol(loc, Symbol.symbol("Greater_Or_Equal"));
            break;
        case ProgramOpExp.PLUS:
            operationName = new PosSymbol(loc, Symbol.symbol("Sum"));
            break;
        case ProgramOpExp.MINUS:
            operationName = new PosSymbol(loc, Symbol.symbol("Difference"));
            break;
        case ProgramOpExp.MULTIPLY:
            operationName = new PosSymbol(loc, Symbol.symbol("Product"));
            break;
        case ProgramOpExp.DIVIDE:
            operationName = new PosSymbol(loc, Symbol.symbol("Divide"));
            break;
        case ProgramOpExp.REM:
            operationName = new PosSymbol(loc, Symbol.symbol("Rem"));
            break;
        case ProgramOpExp.MOD:
            operationName = new PosSymbol(loc, Symbol.symbol("Mod"));
            break;
        case ProgramOpExp.DIV:
            operationName = new PosSymbol(loc, Symbol.symbol("Div"));
            break;
        case ProgramOpExp.EXP:
            operationName = new PosSymbol(loc, Symbol.symbol("Power"));
            break;
        case ProgramOpExp.NOT:
            operationName = new PosSymbol(loc, Symbol.symbol("Not"));
            break;
        case ProgramOpExp.UNARY_MINUS:
            operationName = new PosSymbol(loc, Symbol.symbol("Negate"));
            break;
        }

        // Add the first operand to the list
        if (exp.getFirst() != null) {
            args.add(exp.getFirst());
        }

        // Add the second operand to the list
        if (exp.getSecond() != null) {
            args.add(exp.getSecond());
        }

        // Create the new call
        if (operationName != null) {
            newExp = new ProgramParamExp(loc, operationName, args, null);
        }

        return newExp; */
        return null;
    }
}