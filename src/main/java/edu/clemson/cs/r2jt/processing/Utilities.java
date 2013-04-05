package edu.clemson.cs.r2jt.processing;

// Libraries
import edu.clemson.cs.r2jt.absyn.CallStmt;
import edu.clemson.cs.r2jt.absyn.ChoiceItem;
import edu.clemson.cs.r2jt.absyn.ConceptBodyModuleDec;
import edu.clemson.cs.r2jt.absyn.ConditionItem;
import edu.clemson.cs.r2jt.absyn.Dec;
import edu.clemson.cs.r2jt.absyn.EnhancementBodyItem;
import edu.clemson.cs.r2jt.absyn.EnhancementBodyModuleDec;
import edu.clemson.cs.r2jt.absyn.EnhancementItem;
import edu.clemson.cs.r2jt.absyn.FacilityDec;
import edu.clemson.cs.r2jt.absyn.FacilityModuleDec;
import edu.clemson.cs.r2jt.absyn.FacilityOperationDec;
import edu.clemson.cs.r2jt.absyn.FacilityTypeDec;
import edu.clemson.cs.r2jt.absyn.FuncAssignStmt;
import edu.clemson.cs.r2jt.absyn.IfStmt;
import edu.clemson.cs.r2jt.absyn.InitItem;
import edu.clemson.cs.r2jt.absyn.IterateExitStmt;
import edu.clemson.cs.r2jt.absyn.IterateStmt;
import edu.clemson.cs.r2jt.absyn.ModuleArgumentItem;
import edu.clemson.cs.r2jt.absyn.NameTy;
import edu.clemson.cs.r2jt.absyn.ParameterVarDec;
import edu.clemson.cs.r2jt.absyn.ProcedureDec;
import edu.clemson.cs.r2jt.absyn.ProgramExp;
import edu.clemson.cs.r2jt.absyn.ProgramParamExp;
import edu.clemson.cs.r2jt.absyn.RecordTy;
import edu.clemson.cs.r2jt.absyn.RepresentationDec;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.absyn.SelectionStmt;
import edu.clemson.cs.r2jt.absyn.Statement;
import edu.clemson.cs.r2jt.absyn.VarDec;
import edu.clemson.cs.r2jt.absyn.VariableArrayExp;
import edu.clemson.cs.r2jt.absyn.WhileStmt;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import java.util.Iterator;

/**
 * TODO: Write a description of this module
 */
public class Utilities {

    // ===========================================================
    // Global Variables 
    // ===========================================================

    // Global Variables 
    private List<VarDec> myGlobalVarList;

    // Local Variables
    private List<VarDec> myLocalVarList;

    // Records (Facility)
    private List<FacilityTypeDec> myFacilityTypeList;

    // Records (Concept/Enhancement Realization)
    private List<RepresentationDec> myRepresentationDecList;

    // Operation/Procedure Parameters
    private List<VarDec> myParameterVarList;

    // List of Swap Statements to be added to the ModuleDec
    private List<Statement> myStatementList;

    // List of Swap Calls for Static_Array_Template arrays
    private List<CallStmt> mySwapCallList;

    //private List<SwapStmt> mySwapList;

    // Map of all local operations
    private Map<String, List<ParameterVarDec>> myLocalOperMap;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Utilities() {
        myGlobalVarList = null;
        myLocalVarList = null;
        myFacilityTypeList = null;
        myRepresentationDecList = null;
        myParameterVarList = null;
        myStatementList = new List<Statement>();
        mySwapCallList = new List<CallStmt>();
        //mySwapList = new List<SwapStmt>();
        myLocalOperMap = new Map<String, List<ParameterVarDec>>();
    }

    // ===========================================================
    // Mutator/Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /**
     * Returns the list containing the global variables.
     */
    public List<VarDec> getGlobalVarList() {
        return myGlobalVarList;
    }

    /**
     * Returns the list containing the local variables.
     */
    public List<VarDec> getLocalVarList() {
        return myLocalVarList;
    }

    /**
     * Returns the list containing the records variables in a Facility.
     */
    public List<FacilityTypeDec> getFacilityTypeList() {
        return myFacilityTypeList;
    }

    /**
     * Returns the list containing the records variables in a
     * Concept/Enhancement Realization.
     */
    public List<RepresentationDec> getRepresentationDecList() {
        return myRepresentationDecList;
    }

    /**
     * Returns the list containing the parameter variables of the current
     * operation.
     */
    public List<VarDec> getParameterVarList() {
        return myParameterVarList;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------
    /**
     * Sets the list containing the global variables.
     */
    public void setGlobalVarList(List<VarDec> list) {
        this.myGlobalVarList = list;
    }

    /**
     * Sets the list containing the local variables.
     */
    public void setLocalVarList(List<VarDec> list) {
        this.myLocalVarList = list;
    }

    /**
     * Sets the list containing the records variables in a Facility.
     */
    public void setFacilityTypeList(List<FacilityTypeDec> list) {
        this.myFacilityTypeList = list;
    }

    /**
     * Sets the list containing the records variables in a Concept/Enhancement
     * Realization.
     */
    public void setRepresentationDecList(List<RepresentationDec> list) {
        this.myRepresentationDecList = list;
    }

    /**
     * Sets the list containing the parameter variables of the current
     * operation.
     */
    public void setParameterVarList(List<VarDec> list) {
        this.myParameterVarList = list;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Adds the newly created <code>FacilityDec</code> to the right place
     * </p>
     * 
     * @param it The ancestor iterator.
     * @param newDec The name of the newly created <code>FacilityDec</code>.
     *
     */
    public void addFacilityDec(Iterator<ResolveConceptualElement> it,
            FacilityDec newDec) {
        // Loop
        while (it.hasNext()) {
            // Obtain a temp from it
            ResolveConceptualElement temp = it.next();

            /*
             * Check to see if it is an instance of FacilityModuleDec,
             * FacilityOperationDec, ConceptBodyModuleDec, ProcedureDec or
             * EnhancementBodyModuleDec
             */
            if (temp instanceof FacilityModuleDec) {
                // Obtain a list of Decs from FacilityModuleDec
                List<Dec> decList = ((FacilityModuleDec) temp).getDecs();

                // Add the FacilityDec created to decList
                decList.add(0, newDec);

                // Reinsert the modified list back into FacilityModuleDec
                ((FacilityModuleDec) temp).setDecs((List<Dec>) decList);

                break;
            }
            else if (temp instanceof FacilityOperationDec) {
                // Obtain a list of Decs from FacilityOperationDec
                List<FacilityDec> decList =
                        ((FacilityOperationDec) temp).getFacilities();

                // Add the FacilityDec created to decList
                decList.add(0, newDec);

                // Reinsert the modified list back into FacilityOperationDec
                ((FacilityOperationDec) temp)
                        .setFacilities((List<FacilityDec>) decList);
                break;
            }
            else if (temp instanceof ConceptBodyModuleDec) {
                // Obtain a list of Decs from ConceptBodyModuleDec
                List<Dec> decList = ((ConceptBodyModuleDec) temp).getDecs();

                // Add the FacilityDec created to decList
                decList.add(0, newDec);

                // Reinsert the modified list back into ConceptBodyModuleDec
                ((ConceptBodyModuleDec) temp).setDecs((List<Dec>) decList);

                break;
            }
            else if (temp instanceof ProcedureDec) {
                // Obtain a list of FacilityDecs from ProcedureDec
                List<FacilityDec> decList =
                        ((ProcedureDec) temp).getFacilities();

                // Add the FacilityDec created to decList
                decList.add(0, newDec);

                // Reinsert the modified list back into ProcedureDec
                ((ProcedureDec) temp)
                        .setFacilities((List<FacilityDec>) decList);
                break;
            }
            else if (temp instanceof EnhancementBodyModuleDec) {
                // Obtain a list of FacilityDecs from EnhancementBodyModuleDec
                List<Dec> decList = ((EnhancementBodyModuleDec) temp).getDecs();

                // Add the FacilityDec created to decList
                decList.add(0, newDec);

                // Reinsert the modified list back into EnhancementBodyModuleDec
                ((EnhancementBodyModuleDec) temp).setDecs((List<Dec>) decList);

                break;
            }
        }
    }

    /**
     * <p>
     * Add the swap call statements for arrays into the right location
     * </p>
     *
     * @param location Location of the CallStmt/FunctionAssignStmt
     * @param stmtList List of statements from the FacilityOperationDec or
     * ProcedureDec
     *
     * @return New list of statements.
     */
    private List<Statement> addStatements(Location location,
            List<Statement> stmtList) {
        // Loop through the list
        for (int i = 0; i < stmtList.size(); i++) {
            // CallStmts
            if (stmtList.get(i) instanceof CallStmt
                    && ((CallStmt) stmtList.get(i)).getName().getLocation() == location) {

                for (int j = 0; j < myStatementList.size(); j++) {
                    Statement newStmt = myStatementList.get(j);
                    stmtList.add(i, newStmt);
                }

                break;
            }
            // FunctionAssignStmts
            else if (stmtList.get(i) instanceof FuncAssignStmt
                    && ((FuncAssignStmt) stmtList.get(i)).getLocation() == location) {

                for (int j = 0; j < myStatementList.size(); j++) {
                    Statement newStmt = myStatementList.get(j);
                    stmtList.add(i, newStmt);
                }

                break;
            }
        }

        return stmtList;
    }

    /**
     * <p>
     * Add the swap call statements for arrays into the right location.
     * </p>
     *
     * @param location Location of the CallStmt/FunctionAssignStmt
     * @param stmtList List of statements from the FacilityOperationDec or
     * ProcedureDec
     *
     * @return New list of statements.
     */
    private List<Statement> addSwapCalls(Location location,
            List<Statement> stmtList) {
        // Loop through the list
        for (int i = 0; i < stmtList.size(); i++) {
            // CallStmts
            if (stmtList.get(i) instanceof CallStmt
                    && ((CallStmt) stmtList.get(i)).getName().getLocation() == location) {
                // Add the swap statements after the call statement
                for (int j = 0; j < mySwapCallList.size(); j++) {
                    CallStmt newCallSwapStmt = (CallStmt) mySwapCallList.get(j);
                    stmtList.add(i + 1, newCallSwapStmt);
                }

                // Add the swap statements before the call statement
                for (int j = mySwapCallList.size() - 1; j >= 0; j--) {
                    CallStmt newCallSwapStmt = (CallStmt) mySwapCallList.get(j);
                    stmtList.add(i, newCallSwapStmt);
                }

                break;
            }
            // FunctionAssignStmts
            else if (stmtList.get(i) instanceof FuncAssignStmt
                    && ((FuncAssignStmt) stmtList.get(i)).getLocation() == location) {
                // Add the swap statements after the call statement
                for (int j = 0; j < mySwapCallList.size(); j++) {
                    CallStmt newSwapStmt = (CallStmt) mySwapCallList.get(j);
                    stmtList.add(i + 1, newSwapStmt);
                }

                // Add the swap statements before the call statement
                for (int j = mySwapCallList.size() - 1; j >= 0; j--) {
                    CallStmt newSwapStmt = (CallStmt) mySwapCallList.get(j);
                    stmtList.add(i, newSwapStmt);
                }

                break;
            }
        }

        return stmtList;
    }

    /**
     * <p>
     * Adds the new <code>VarDec</code> to the list of local variables inside
     * this Utilities class.
     * </p>
     *
     * @param dec The newly created variable.
     */
    public void addToLocalVarList(VarDec dec) {
        myLocalVarList.add(dec);
    }

    /**
     * <p>
     * Adds the new <code>Statement</code> to the list of statements to be 
     * added to the <code>ModuleDec</code>.
     * </p>
     * 
     * @param statement Newly created statement that needs to be added.
     */
    public void addToStatementList(Statement statement) {
        myStatementList.add(statement);
    }

    /**
     * <p>
     * Adds the <code>Swap_Entry</code> <code>CallStmt</code> to the swap 
     * call list.
     * </p>
     * 
     * @param statement The created swap call.
     */
    public void addToSwapList(CallStmt statement) {
        mySwapCallList.add(statement);
    }

    /**
     * <p>
     * Clears the list of statements to be added.
     * </p>
     */
    public void clearStatementList() {
        myStatementList.clear();
    }

    /**
     * <p>
     * Clears the list of swap call statements to be added.
     * </p>
     */
    public void clearSwapCallList() {
        mySwapCallList.clear();
    }

    /**
     * <p>
     * Creates a new <code>FacilityDec</code>
     * </p>
     * 
     * @param location The location where the <code>FacilityDec</code> is created
     * @param name The name of the new <code>FacilityDec</code>.
     * @param conceptName The name of the Concept of this <code>FacilityDec</code>.
     * @param conceptRealizationName The name of the Concept Realization of this
     * <code>FacilityDec</code>.
     * @param conceptParam The list of parameters for the Concept.
     * @param conceptBodiesParam The list of parameters for the Concept
     * Realization.
     * @param enhancementParam The list of parameters for the Enhancement.
     * @param enhancementBodiesParam The list of parameters for the Enhancement
     * Realization.
     *
     * @return newly created <code>FacilityDec</code>
     */
    public FacilityDec createFacilityDec(Location location, String name,
            String conceptName, String conceptRealizationName,
            List<ModuleArgumentItem> conceptParam,
            List<ModuleArgumentItem> conceptBodiesParam,
            List<EnhancementItem> enhancementParam,
            List<EnhancementBodyItem> enhancementBodiesParam) {
        // Create a FacilityDec
        FacilityDec newFacilityDec = new FacilityDec();

        // Check for null
        if (newFacilityDec != null) {
            // Set the name
            newFacilityDec
                    .setName(new PosSymbol(location, Symbol.symbol(name)));

            // Set the Concept to "Static_Array_Template
            newFacilityDec.setConceptName(new PosSymbol(location, Symbol
                    .symbol(conceptName)));
            newFacilityDec.setConceptParams(conceptParam);

            // Set the Concept Realization to "Std_Array_Realiz */
            newFacilityDec.setBodyName(new PosSymbol(location, Symbol
                    .symbol(conceptRealizationName)));
            newFacilityDec.setBodyParams(conceptBodiesParam);

            // Set the Enhancement to empty
            newFacilityDec.setEnhancements(enhancementParam);
            newFacilityDec.setEnhancementBodies(enhancementBodiesParam);
        }

        return newFacilityDec;
    }

    /**
     * <p>
     * Creates a <code>ProgramParamExp</code> for Replica with the 
     * <code>VariableExp</code> passed in.
     * </p>
     *
     * @param oldExp Variable expression to be replicated
     *
     * @return Replica call with the oldExp as its argument.
     */
    public ProgramParamExp createReplicaExp(ProgramExp oldExp) {
        // Parameter list for the Replica call
        List<ProgramExp> params = new List<ProgramExp>();
        params.add(oldExp);

        // Create new right hand side with the Replica function
        ProgramParamExp newExp =
                new ProgramParamExp(oldExp.getLocation(), new PosSymbol(oldExp
                        .getLocation(), Symbol.symbol("Replica")), params, null);

        return newExp;
    }

    /**
     * <p>
     * Checks our map of ProcedureDec/FacilityOperationDec to see if 
     * the string passed in is a local operation.
     * </p>
     * 
     * @param opName The string containing the name of the operation.
     * 
     * @return true/false depending if it is in our map or not
     */
    public boolean isLocalOper(String opName) {
        if (myLocalOperMap.containsKey(opName)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * <p>
     * Checks if the list of statements is empty or not
     * </p>
     * 
     * @return true/false depending if it is empty or not.
     */
    public boolean isStatementListEmpty() {
        // If empty return true, else return false
        if (myStatementList.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * <p>
     * Checks if the list of swap call statements is empty or not
     * </p>
     *
     * @return true/false depending if it is empty or not.
     */
    public boolean isSwapCallListEmpty() {
        // If empty return true, else return false
        if (mySwapCallList.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * <p>
     * Loop through the list of ancestors and try to find a <code>
     * FacilityOperationDec</code> or a <code>ProcedureDec</code> and add 
     * the created swap statements into the list of statements.
     * </p>
     *
     * @param it Iterator for our list of ancestors
     * @param location Location of the <code>CallStmt</code>/<code>
     * FunctionAssignStmt</code>
     */
    public void loopAndAddStatements(Iterator<ResolveConceptualElement> it,
            Location location) {
        // Iterate through our ancestors
        while (it.hasNext()) {
            // Obtain a temp from it
            ResolveConceptualElement temp = it.next();

            // Look for FacilityOperationDec
            if (temp instanceof FacilityOperationDec) {
                // Update the list of statements inside the FacilityOperation
                List<Statement> stmtList =
                        addStatements(location, ((FacilityOperationDec) temp)
                                .getStatements());
                ((FacilityOperationDec) temp).setStatements(stmtList);
                break;
            } /*
               * Look for ProcedureDec
               */
            else if (temp instanceof ProcedureDec) {
                /*
                 * Update the list of statements inside the Procedure
                 */
                List<Statement> stmtList =
                        addStatements(location, ((ProcedureDec) temp)
                                .getStatements());
                ((ProcedureDec) temp).setStatements(stmtList);
                break;
            }
        }
    }

    /**
     * <p>
     * Loop through the list of ancestors and try to find a <code>
     * FacilityOperationDec</code> or a <code>ProcedureDec</code> and add the 
     * created swap call statements into the list of statements.
     * </p>
     *
     * @param it Iterator for our list of ancestors
     * @param location Location of the <code>CallStmt</code>/<code>
     * FunctionAssignStmt</code>
     *
     */
    public void loopAndAddSwapCalls(Iterator<ResolveConceptualElement> it,
            Location location) {
        // Iterate through our ancestors
        while (it.hasNext()) {
            // Obtain a temp from it
            ResolveConceptualElement temp = it.next();

            // Look for FacilityOperationDec
            if (temp instanceof FacilityOperationDec) {
                // Update the list of statements inside the FacilityOperation
                List<Statement> stmtList =
                        addSwapCalls(location, ((FacilityOperationDec) temp)
                                .getStatements());
                ((FacilityOperationDec) temp).setStatements(stmtList);
                break;
            }
            // Look for ProcedureDec
            else if (temp instanceof ProcedureDec) {
                // Update the list of statements inside the Procedure
                List<Statement> stmtList =
                        addSwapCalls(location, ((ProcedureDec) temp)
                                .getStatements());
                ((ProcedureDec) temp).setStatements(stmtList);
                break;
            }
        }
    }

    /**
     * <p>
     * Replaces the old statement with the newly created statement.
     * </p>
     *
     * @param it The list iterator.
     * @param oldStmt The old statement that needs to be replaced.
     * @param newStmt The new statement that will replace oldStmt.
     */
    public void replaceStmt(Iterator<ResolveConceptualElement> it,
            Statement oldStmt, Statement newStmt) {
        // Variables
        List<Statement> stmtList = null;
        boolean found = false;
        ResolveConceptualElement parent;

        while (!found && it.hasNext()) {
            parent = it.next();

            // Parent = FacilityOperationDec
            if (parent instanceof FacilityOperationDec) {
                // Get our list of statements
                stmtList = ((FacilityOperationDec) parent).getStatements();
            }
            // Parent = ProcedureDec
            else if (parent instanceof ProcedureDec) {
                // Get our list of statements
                stmtList = ((ProcedureDec) parent).getStatements();
            }
            // Parent = InitItem
            else if (parent instanceof InitItem) {
                // Get our list of statements
                stmtList = ((InitItem) parent).getStatements();
            }
            // Parent = IfStmt
            else if (parent instanceof IfStmt) {
                // Get our list of statements
                stmtList = ((IfStmt) parent).getThenclause();

                // Loop through until we find this current oldStmt that we are on
                for (int i = 0; i < stmtList.size() && found == false; i++) {
                    if (stmtList.get(i) == oldStmt) {
                        // Replace oldStmt with the newStmt
                        stmtList.set(i, newStmt);
                        found = true;
                    }
                }

                // Handle the Else Clause (oldStmt might be here)
                if (((IfStmt) parent).getElseclause() != null && found != true) {
                    stmtList = ((IfStmt) parent).getElseclause();
                }
            }
            // Parent = ConditionStmt
            else if (parent instanceof ConditionItem) {
                // Get our list of statements
                stmtList = ((ConditionItem) parent).getThenclause();
            }
            // Parent = IterativeStmt
            else if (parent instanceof IterateStmt) {
                // Get our list of statements
                stmtList = ((IterateStmt) parent).getStatements();
            }
            // Parent = IterativeExitStmt
            else if (parent instanceof IterateExitStmt) {
                // Get our list of statements
                stmtList = ((IterateExitStmt) parent).getStatements();
            }
            // Parent = SelectionStmt
            else if (parent instanceof SelectionStmt) {
                // Get our list of statements
                stmtList = ((SelectionStmt) parent).getDefaultclause();
            }
            // Parent = ChoiceItem
            else if (parent instanceof ChoiceItem) {
                // Get our list of statements
                stmtList = ((ChoiceItem) parent).getThenclause();
            }
            // Parent = WhileStmt
            else if (parent instanceof WhileStmt) {
                // Get our list of statements
                stmtList = ((WhileStmt) parent).getStatements();
            }

            // Make sure the list is not null
            if (stmtList != null) {
                // Loop through until we find this current oldStmt that we are on
                for (int i = 0; i < stmtList.size() && found == false; i++) {
                    if (stmtList.get(i) == oldStmt) {
                        // Replace oldStmt with the newStmt
                        stmtList.set(i, newStmt);
                        found = true;
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Keeps a map reference to all local operations, by iterating through list 
     * of <Code>Dec</Code> looking for any ProcedureDec or FacilityOperationDec
     * </p>
     * 
     * @param decList The list containing all the <code>Dec</code>. 
     */
    public void retrieveLocalProc(List<Dec> decList) {
        Iterator<Dec> it = decList.iterator();
        while (it.hasNext()) {
            // Temporary holder for the current Dec
            Dec current = it.next();

            // Check if it is a ProcedureDec or not
            if (current instanceof ProcedureDec) {
                // Type cast to ProcedureDec
                ProcedureDec currentProcDec = (ProcedureDec) current;

                // Update the map
                myLocalOperMap.put(currentProcDec.getName().getName(),
                        currentProcDec.getParameters());
            }
            // Check if it is a FacilityOperationDec or not
            else if (current instanceof FacilityOperationDec) {
                // Type cast to FacilityOperationDec
                FacilityOperationDec currentProcDec =
                        (FacilityOperationDec) current;

                // Update the map
                myLocalOperMap.put(currentProcDec.getName().getName(),
                        currentProcDec.getParameters());
            }
        }
    }

    /**
     * Returns list of parameters associated with the local operation 
     * in the operation map.
     *
     * @param opName <code>PosSymbol</code> of the operation
     *
     */
    public List<ParameterVarDec> retrieveParameterList(String opName) {
        return myLocalOperMap.get(opName);
    }

    /**
     * <p>
     * Initialize and populates global variables and records from the list of
     * <code>Dec</code> into lists that are in the current <code>ModuleDec
     * </code> we are pre-processing.
     * </p>
     *
     * @param decList The list containing all the <code>Dec</code>.
     */
    public void retrieveRecordGVar(List<Dec> decList) {
        Iterator<Dec> it = decList.iterator();
        while (it.hasNext()) {
            // Temporary holder for the current Dec
            Dec current = it.next();

            // Check if it is a FacilityTypeDec or not
            if (current instanceof FacilityTypeDec) {
                // Create the list if null
                if (myFacilityTypeList == null) {
                    myFacilityTypeList = new List<FacilityTypeDec>();
                }

                // Add current to our list
                myFacilityTypeList.add((FacilityTypeDec) current);
            }
            // Check if it is a global variable or not
            else if (current instanceof VarDec) {
                // Create the list if null
                if (myGlobalVarList == null) {
                    myGlobalVarList = new List<VarDec>();
                }

                // Add current to our list
                myGlobalVarList.add((VarDec) current);
            }
            // Check if it is a RepresentationDec or not
            else if (current instanceof RepresentationDec) {
                // Create the list if null
                if (myRepresentationDecList == null) {
                    myRepresentationDecList = new List<RepresentationDec>();
                }

                // Add current to our list
                myRepresentationDecList.add((RepresentationDec) current);
            }
        }
    }

    /**
     * <p>
     * Searches for the name of the <code>Ty</code> in a record.
     * </p>
     * 
     * @param exp Name of the array
     * @param ty Name of the record
     * 
     * @return String containing name of the array
     */
    public String retrieveTyNameFromRecords(VariableArrayExp exp, NameTy ty) {
        // Variables
        String nameOfArray = "";

        // Check if we are in an EnhancementRealization or ConceptRealization
        if (myRepresentationDecList != null) {
            Iterator<RepresentationDec> it = myRepresentationDecList.iterator();

            // Loop Through
            while (it.hasNext()) {
                RepresentationDec tempRepDec = it.next();

                // Check name of tempRepDec against ty of tempVarDec
                if (tempRepDec.getName().getName().equals(
                        ty.getName().getName())) {
                    RecordTy tempTy = (RecordTy) tempRepDec.getRepresentation();

                    // Get list of fields
                    VarDec varInRecord =
                            iterateFindVarDec(((VariableArrayExp) exp)
                                    .getName(), tempTy.getFields());
                    if (varInRecord != null) {
                        // Set the return ty
                        nameOfArray =
                                ((NameTy) varInRecord.getTy()).getQualifier()
                                        .getName();
                        break;
                    }
                }
            }
        }
        // We must be in a Facility
        else {
            Iterator<FacilityTypeDec> it = myFacilityTypeList.iterator();

            // Loop Through
            while (it.hasNext()) {
                FacilityTypeDec tempRepDec = it.next();

                // Check name of tempRepDec against ty of tempVarDec
                if (tempRepDec.getName().getName().equals(
                        ty.getName().getName())) {
                    RecordTy tempTy = (RecordTy) tempRepDec.getRepresentation();

                    // Get list of fields
                    VarDec varInRecord =
                            iterateFindVarDec(((VariableArrayExp) exp)
                                    .getName(), tempTy.getFields());

                    if (varInRecord != null) {
                        // Set the return ty
                        nameOfArray =
                                ((NameTy) varInRecord.getTy()).getQualifier()
                                        .getName();
                        break;
                    }
                }
            }
        }

        return nameOfArray;
    }

    /**
     * <p>
     * Return the specified <code>VarDec</code> if found
     * </p>
     * 
     * @param name The variable name represented as a <code>PosSymbol</code>
     *
     * @return The variable declaration or null.
     */
    public VarDec returnVarDec(PosSymbol name) {
        // Search the local variable list first
        VarDec retVarDec = iterateFindVarDec(name, myLocalVarList);

        // Search the parameters of the operation
        if (retVarDec == null) {
            retVarDec = iterateFindVarDec(name, myParameterVarList);
        }

        // Search the global variable list
        if (retVarDec == null) {
            retVarDec = iterateFindVarDec(name, myGlobalVarList);
        }

        return retVarDec;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Look for the variable declaration in the specified list
     * </p>
     *
     * @param name The name of the variable represented as a PosSymbol
     * @param list List of variable declarations that we are searching
     *
     * @return The variable with the specified name or null if not found.
     */
    private VarDec iterateFindVarDec(PosSymbol name, List<VarDec> list) {
        // Variables
        VarDec retVarDec = null;

        // Iterate through list of variables
        Iterator<VarDec> it = list.iterator();
        while (it.hasNext()) {
            // Obtain nextDec from the iterator
            VarDec nextVarDec = it.next();

            // We found it
            if (nextVarDec.getName().getName().compareTo(name.getName()) == 0) {
                retVarDec = nextVarDec;
                break;
            }
        }

        return retVarDec;
    }
}