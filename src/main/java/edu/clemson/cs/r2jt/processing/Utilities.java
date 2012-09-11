package edu.clemson.cs.r2jt.processing;

/* Libraries */
import java.util.Iterator;

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
import edu.clemson.cs.r2jt.absyn.SwapStmt;
import edu.clemson.cs.r2jt.absyn.UsesItem;
import edu.clemson.cs.r2jt.absyn.VarDec;
import edu.clemson.cs.r2jt.absyn.VariableArrayExp;
import edu.clemson.cs.r2jt.absyn.VariableNameExp;
import edu.clemson.cs.r2jt.absyn.WhileStmt;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class Utilities {

    // ===========================================================
    // Global Variables 
    // ===========================================================

    /* List of global/local variables and record types */
    private List<VarDec> myGlobalVarList;
    private List<VarDec> myLocalVarList;
    private List<FacilityTypeDec> myFacilityTypeList;
    private List<RepresentationDec> myRepresentationDecList;
    private List<VarDec> myParameterVarList;
    private List<UsesItem> myUsesList;
    private List<CallStmt> mySwapCallList;
    private List<SwapStmt> mySwapList;
    private Map<PosSymbol, List<ParameterVarDec>> myLocalOperMap;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Utilities() {
        myGlobalVarList = null;
        myLocalVarList = null;
        myFacilityTypeList = null;
        myRepresentationDecList = null;
        myParameterVarList = null;
        myUsesList = null;
        mySwapCallList = new List<CallStmt>();
        mySwapList = new List<SwapStmt>();
        myLocalOperMap = new Map<PosSymbol, List<ParameterVarDec>>();
    }

    // ===========================================================
    // Mutator/Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the list containing the global variables. */
    public List<VarDec> getGlobalVarList() {
        return myGlobalVarList;
    }

    /** Returns the list containing the local variables. */
    public List<VarDec> getLocalVarList() {
        return myLocalVarList;
    }

    /** Returns the list containing the records variables in a Facility. */
    public List<FacilityTypeDec> getFacilityTypeList() {
        return myFacilityTypeList;
    }

    /** Returns the list containing the records variables in a Concept/Enhancement Realization. */
    public List<RepresentationDec> getRepresentationDecList() {
        return myRepresentationDecList;
    }

    /** Returns the list containing the parameter variables of the current operation. */
    public List<VarDec> getParameterVarList() {
        return myParameterVarList;
    }

    /** Returns the list containing uses items. */
    public List<UsesItem> getUsesList() {
        return myUsesList;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the list containing the global variables. */
    public void setGlobalVarList(List<VarDec> list) {
        this.myGlobalVarList = list;
    }

    /** Sets the list containing the local variables. */
    public void setLocalVarList(List<VarDec> list) {
        this.myLocalVarList = list;
    }

    /** Sets the list containing the records variables in a Facility. */
    public void setFacilityTypeList(List<FacilityTypeDec> list) {
        this.myFacilityTypeList = list;
    }

    /** Sets the list containing the records variables in a Concept/Enhancement Realization. */
    public void setRepresentationDecList(List<RepresentationDec> list) {
        this.myRepresentationDecList = list;
    }

    /** Sets the list containing the parameter variables of the current operation. */
    public void setParameterVarList(List<VarDec> list) {
        this.myParameterVarList = list;
    }

    /** Sets the list containing uses items. */
    public void setUsesList(List<UsesItem> list) {
        this.myUsesList = list;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // FacilityDec Related Methods
    // -----------------------------------------------------------

    /**
     * Creates a new FacilityDec
     * 
     * @param location The location where the FacilityDec is created
     * @param name The name of the new FacilityDec.
     * @param conceptName The name of the Concept of this FacilityDec.
     * @param conceptRealizationName The name of the Concept Realization of this FacilityDec.
     * @param conceptParam The list of parameters for the Concept.
     * @param conceptBodiesParam The list of parameters for the Concept Realization.
     * @param enhancementParam The list of parameters for the Enhancement.
     * @param enhancementBodiesParam The list of parameters for the Enhancement Realization.
     * 
     * @return newly created FacilityDec
     */
    public FacilityDec createFacilityDec(Location location, String name,
            String conceptName, String conceptRealizationName,
            List<ModuleArgumentItem> conceptParam,
            List<ModuleArgumentItem> conceptBodiesParam,
            List<EnhancementItem> enhancementParam,
            List<EnhancementBodyItem> enhancementBodiesParam) {
        /* Create a FacilityDec */
        FacilityDec newFacilityDec = new FacilityDec();

        /* Check for null */
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
     * Adds the newly created FacilityDec to the right place
     * 
     * @param it The ancestor iterator.
     * @param newDec The name of the newly created FacilityDec.
     * 
     */
    public void addFacilityDec(Iterator<ResolveConceptualElement> it,
            FacilityDec newDec) {
        /* Loop */
        while (it.hasNext()) {
            /* Obtain a temp from it */
            ResolveConceptualElement temp = it.next();

            /* Check to see if it is an instance of FacilityModuleDec, FacilityOperationDec, 
             * ConceptBodyModuleDec, ProcedureDec or EnhancementBodyModuleDec */
            if (temp instanceof FacilityModuleDec) {
                /* Obtain a list of Decs from FacilityModuleDec */
                List<Dec> decList = ((FacilityModuleDec) temp).getDecs();

                /* Add the FacilityDec created to decList */
                decList.add(0, newDec);

                /* Reinsert the modified list back into FacilityModuleDec */
                ((FacilityModuleDec) temp).setDecs((List<Dec>) decList);

                break;
            }
            else if (temp instanceof FacilityOperationDec) {
                /* Obtain a list of Decs from FacilityOperationDec */
                List<FacilityDec> decList =
                        ((FacilityOperationDec) temp).getFacilities();

                /* Add the FacilityDec created to decList */
                decList.add(0, newDec);

                /* Reinsert the modified list back into FacilityOperationDec */
                ((FacilityOperationDec) temp)
                        .setFacilities((List<FacilityDec>) decList);
                break;
            }
            else if (temp instanceof ConceptBodyModuleDec) {
                /* Obtain a list of Decs from ConceptBodyModuleDec */
                List<Dec> decList = ((ConceptBodyModuleDec) temp).getDecs();

                /* Add the FacilityDec created to decList */
                decList.add(0, newDec);

                /* Reinsert the modified list back into ConceptBodyModuleDec */
                ((ConceptBodyModuleDec) temp).setDecs((List<Dec>) decList);

                break;
            }
            else if (temp instanceof ProcedureDec) {
                /* Obtain a list of FacilityDecs from ProcedureDec */
                List<FacilityDec> decList =
                        ((ProcedureDec) temp).getFacilities();

                /* Add the FacilityDec created to decList */
                decList.add(0, newDec);

                /* Reinsert the modified list back into ProcedureDec */
                ((ProcedureDec) temp)
                        .setFacilities((List<FacilityDec>) decList);
                break;
            }
            else if (temp instanceof EnhancementBodyModuleDec) {
                /* Obtain a list of FacilityDecs from EnhancementBodyModuleDec */
                List<Dec> decList = ((EnhancementBodyModuleDec) temp).getDecs();

                /* Add the FacilityDec created to decList */
                decList.add(0, newDec);

                /* Reinsert the modified list back into EnhancementBodyModuleDec */
                ((EnhancementBodyModuleDec) temp).setDecs((List<Dec>) decList);

                break;
            }
        }
    }

    // -----------------------------------------------------------
    // DecList Related Methods
    // -----------------------------------------------------------

    /**
     * Keeps a map reference to all local operations
     * 
     * @param decList The list of Decs.
     * 
     */
    public void initLocalProcMap(List<Dec> decList) {
        /* Iterate through list of Decs looking for any ProcedureDec */
        Iterator<Dec> it = decList.iterator();
        while (it.hasNext()) {
            /* Temporary holder for the current item */
            Dec current = it.next();

            /* Check if it is a ProcedureDec or not */
            if (current instanceof ProcedureDec) {
                /* Variables */
                ProcedureDec currentProcDec = (ProcedureDec) current;
                PosSymbol procName = currentProcDec.getName();
                List<ParameterVarDec> parameterList =
                        currentProcDec.getParameters();

                /* Update the map */
                myLocalOperMap.put(procName, parameterList);
            }
            /* Check if it is a FacilityOperationDec or not */
            else if (current instanceof FacilityOperationDec) {
                /* Variables */
                FacilityOperationDec currentProcDec =
                        (FacilityOperationDec) current;
                PosSymbol procName = currentProcDec.getName();
                List<ParameterVarDec> parameterList =
                        currentProcDec.getParameters();

                /* Update the map */
                myLocalOperMap.put(procName, parameterList);
            }
        }
    }

    /**
     * Searches for any global variables
     * 
     * @param decList The list of Decs.
     * 
     */
    public void initVarDecList(List<Dec> decList) {
        /* Iterate through list of Decs looking for any FacilityTypeDec */
        Iterator<Dec> it = decList.iterator();
        while (it.hasNext()) {
            /* Temporary holder for the current item */
            Dec current = it.next();

            /* Check if it is a FacilityTypeDec or not */
            if (current instanceof FacilityTypeDec) {
                /* Create the list if null */
                if (myFacilityTypeList == null) {
                    myFacilityTypeList = new List<FacilityTypeDec>();
                }

                /* Add current to our list */
                myFacilityTypeList.add((FacilityTypeDec) current);
            }
            /* Check if it is a global variable or not */
            else if (current instanceof VarDec) {
                /* Create the list if null */
                if (myGlobalVarList == null) {
                    myGlobalVarList = new List<VarDec>();
                }

                /* Add current to our list */
                myGlobalVarList.add((VarDec) current);
            }
            /* Check if it is a RepresentationDec or not */
            else if (current instanceof RepresentationDec) {
                /* Create the list if null */
                if (myRepresentationDecList == null) {
                    myRepresentationDecList = new List<RepresentationDec>();
                }

                /* Add current to our list */
                myRepresentationDecList.add((RepresentationDec) current);
            }
        }
    }

    // -----------------------------------------------------------
    // Statement Related Methods
    // -----------------------------------------------------------

    /**
     * Replaces the old statement with the newly created statement
     * 
     * @param it The list iterator.
     * @param oldStmt The old statement that needs to be replaced.
     * @param newStmt The new statement that will replace oldStmt.
     * 
     */
    public void replaceStmt(Iterator<ResolveConceptualElement> it,
            Statement oldStmt, Statement newStmt) {
        /* Variables */
        List<Statement> stmtList = null;
        boolean found = false;
        ResolveConceptualElement parent;

        while (!found && it.hasNext()) {
            parent = it.next();

            /* Parent = FacilityOperationDec */
            if (parent instanceof FacilityOperationDec) {
                /* Get our list of statements */
                stmtList = ((FacilityOperationDec) parent).getStatements();
            }
            /* Parent = ProcedureDec */
            else if (parent instanceof ProcedureDec) {
                /* Get our list of statements */
                stmtList = ((ProcedureDec) parent).getStatements();
            }
            /* Parent = InitItem */
            else if (parent instanceof InitItem) {
                /* Get our list of statements */
                stmtList = ((InitItem) parent).getStatements();
            }
            /* Parent = IfStmt */
            else if (parent instanceof IfStmt) {
                /* Get our list of statements */
                stmtList = ((IfStmt) parent).getThenclause();

                /* Loop through until we find this current SwapStmt that we are on */
                for (int i = 0; i < stmtList.size() && found == false; i++) {
                    if (stmtList.get(i) == oldStmt) {
                        /* Replace SwapStmt with the newly created swapEntryStmt call */
                        stmtList.set(i, newStmt);
                        found = true;
                    }
                }

                /* Handle the Else Clause (oldStmt might be here) */
                if (((IfStmt) parent).getElseclause() != null && found != true) {
                    stmtList = ((IfStmt) parent).getElseclause();
                }
            }
            /* Parent = ConditionStmt */
            else if (parent instanceof ConditionItem) {
                /* Get our list of statements */
                stmtList = ((ConditionItem) parent).getThenclause();
            }
            /* Parent = IterativeStmt */
            else if (parent instanceof IterateStmt) {
                /* Get our list of statements */
                stmtList = ((IterateStmt) parent).getStatements();
            }
            /* Parent = IterativeExitStmt */
            else if (parent instanceof IterateExitStmt) {
                /* Get our list of statements */
                stmtList = ((IterateExitStmt) parent).getStatements();
            }
            /* Parent = SelectionStmt */
            else if (parent instanceof SelectionStmt) {
                /* Get our list of statements */
                stmtList = ((SelectionStmt) parent).getDefaultclause();
            }
            /* Parent = ChoiceItem */
            else if (parent instanceof ChoiceItem) {
                /* Get our list of statements */
                stmtList = ((ChoiceItem) parent).getThenclause();
            }
            /* Parent = WhileStmt */
            else if (parent instanceof WhileStmt) {
                /* Get our list of statements */
                stmtList = ((WhileStmt) parent).getStatements();
            }

            /* Make sure the list is not null */
            if (stmtList != null) {
                /* Loop through until we find this current SwapStmt that we are on */
                for (int i = 0; i < stmtList.size() && found == false; i++) {
                    if (stmtList.get(i) == oldStmt) {
                        /* Replace SwapStmt with the newly created swapEntryStmt call */
                        stmtList.set(i, newStmt);
                        found = true;
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------
    // UsesList Related Methods
    // -----------------------------------------------------------

    /**
     * Manually add in files for the uses list
     * 
     * @param filename The concept filename
     * @param location A location (Doesn't really point to the right place)
     * 
     */
    public void addToUsesList(String filename, Location location) {
        /* Check if our uses list is null or not */
        if (myUsesList == null) {
            myUsesList = new List<UsesItem>();
        }

        /* Check if the file is already in the uses list or not */
        boolean isThere = false;
        Iterator<UsesItem> it = myUsesList.iterator();

        while (it.hasNext()) {
            if (it.next().getName().getName().equals(filename)) {
                isThere = true;
            }
        }

        /* Add file to uses list if it is not already there */
        if (!isThere) {
            myUsesList.add(new UsesItem(new PosSymbol(location, Symbol
                    .symbol(filename))));
        }
    }

    // -----------------------------------------------------------
    // VarDec/VariableExp Related Methods
    // -----------------------------------------------------------

    /**
     * Return the specified VarDec 
     * 
     * @param name The name of the variable represented as a PosSymbol
     * 
     */
    public VarDec returnVarDec(PosSymbol name) {
        /* Search the local variable list first */
        VarDec retVarDec = iterateFindVarDec(name, myLocalVarList);

        /* Search the parameters of the operation */
        if (retVarDec == null) {
            retVarDec = iterateFindVarDec(name, myParameterVarList);
        }

        /* Search the global variable list */
        if (retVarDec == null) {
            retVarDec = iterateFindVarDec(name, myGlobalVarList);
        }

        return retVarDec;
    }

    /**
     * Creates a new VariableNameExp with the given parameters
     * 
     * @param oldExp The old VariableExp
     * @param counter A number that we append to the end of the variable
     *                to make sure we do not have duplicates.
     * 
     */
    public VariableNameExp createVariableNameExp(VariableNameExp oldExp,
            int counter) {
        /* Variable */
        VariableNameExp newExp = new VariableNameExp();

        /* Create the new name */
        PosSymbol oldName = newExp.getName();
        PosSymbol newName =
                new PosSymbol(newExp.getLocation(), Symbol.symbol("_RepArg_"
                        + oldName.getName() + "_" + counter));

        /* Set the fields */
        newExp.setQualifier(oldExp.getQualifier());
        newExp.setName(newName);
        newExp.setLocation(oldExp.getLocation());

        return newExp;
    }

    // -----------------------------------------------------------
    // Ty Related Methods
    // -----------------------------------------------------------

    /**
     * Searches for the name of the Ty in a Record
     * 
     * @param exp Name of the array
     * @param ty Name of the record
     * 
     */
    public String retrieveTyNameFromRecords(VariableArrayExp exp, NameTy ty) {
        /* Variables */
        String nameOfArray = "";

        /* Check if we are in an EnhancementRealization or ConceptRealization */
        if (myRepresentationDecList != null) {
            Iterator<RepresentationDec> it = myRepresentationDecList.iterator();

            /* Loop Through */
            while (it.hasNext()) {
                RepresentationDec tempRepDec = it.next();

                /* Check name of tempRepDec against ty of tempVarDec */
                if (tempRepDec.getName().getName().equals(
                        ty.getName().getName())) {
                    RecordTy tempTy = (RecordTy) tempRepDec.getRepresentation();

                    /* Get list of fields */
                    VarDec varInRecord =
                            iterateFindVarDec(((VariableArrayExp) exp)
                                    .getName(), tempTy.getFields());
                    if (varInRecord != null) {
                        /* Set the return ty */
                        nameOfArray =
                                ((NameTy) varInRecord.getTy()).getQualifier()
                                        .getName();
                        break;
                    }
                }
            }
        }
        /* We must be in a Facility */
        else {
            Iterator<FacilityTypeDec> it = myFacilityTypeList.iterator();

            /* Loop Through */
            while (it.hasNext()) {
                FacilityTypeDec tempRepDec = it.next();

                /* Check name of tempRepDec against ty of tempVarDec */
                if (tempRepDec.getName().getName().equals(
                        ty.getName().getName())) {
                    RecordTy tempTy = (RecordTy) tempRepDec.getRepresentation();

                    /* Get list of fields */
                    VarDec varInRecord =
                            iterateFindVarDec(((VariableArrayExp) exp)
                                    .getName(), tempTy.getFields());
                    if (varInRecord != null) {
                        /* Set the return ty */
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

    // -----------------------------------------------------------
    // SwapList Related Methods
    // -----------------------------------------------------------

    /**
     * Adds the Swap_Entry CallStmt to the swap call list 
     * 
     * @param statement The created Swap_Entry CallStmt
     * 
     */
    public void addToSwapList(CallStmt statement) {
        mySwapCallList.add(statement);
    }

    /**
     * Adds the SwapStmt to the swap list
     * 
     * @param statement The created SwapStmt
     * 
     */
    public void addToSwapList(SwapStmt statement) {
        mySwapList.add(statement);
    }

    /**
     * Checks if the list of swap call statements is empty or not
     * 
     */
    public boolean isSwapCallListEmpty() {
        /* If empty return true, else return false */
        if (mySwapCallList.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks if the list of swap statements is empty or not
     * 
     */
    public boolean isSwapListEmpty() {
        /* If empty return true, else return false */
        if (mySwapList.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Clears the list of swap call statements
     * 
     */
    public void clearSwapCallList() {
        mySwapCallList.clear();
    }

    /**
     * Clears the list of swap statements
     * 
     */
    public void clearSwapList() {
        mySwapCallList.clear();
    }

    /**
     * Loop through the list of ancestors and try to find a FacilityOperationDec
     * or a ProcedureDec and add the created swap statements into the list of statements
     * 
     * @param it Iterator for our list of ancestors
     * @param location Location of the CallStmt/FunctionAssignStmt
     * 
     */
    public void loopAndAddSwapCalls(Iterator<ResolveConceptualElement> it,
            Location location) {
        /* Iterate through our ancestors */
        while (it.hasNext()) {
            /* Obtain a temp from it */
            ResolveConceptualElement temp = it.next();

            /* Look for FacilityOperationDec */
            if (temp instanceof FacilityOperationDec) {
                /* Update the list of statements inside the FacilityOperation */
                List<Statement> stmtList =
                        addSwapCalls(location, ((FacilityOperationDec) temp)
                                .getStatements());
                ((FacilityOperationDec) temp).setStatements(stmtList);
                break;
            }
            /* Look for ProcedureDec */
            else if (temp instanceof ProcedureDec) {
                /* Update the list of statements inside the Procedure */
                List<Statement> stmtList =
                        addSwapCalls(location, ((ProcedureDec) temp)
                                .getStatements());
                ((ProcedureDec) temp).setStatements(stmtList);
                break;
            }
        }
    }

    /**
     * Loop through the list of ancestors and try to find a FacilityOperationDec
     * or a ProcedureDec and add the created swap statements into the list of statements
     * 
     * @param it Iterator for our list of ancestors
     * @param location Location of the CallStmt/FunctionAssignStmt
     * 
     */
    public void loopAndAddSwapStmts(Iterator<ResolveConceptualElement> it,
            Location location) {
        /* Iterate through our ancestors */
        while (it.hasNext()) {
            /* Obtain a temp from it */
            ResolveConceptualElement temp = it.next();

            /* Look for FacilityOperationDec */
            if (temp instanceof FacilityOperationDec) {
                /* Update the list of statements inside the FacilityOperation */
                List<Statement> stmtList =
                        addSwapStmts(location, ((FacilityOperationDec) temp)
                                .getStatements());
                ((FacilityOperationDec) temp).setStatements(stmtList);
                break;
            }
            /* Look for ProcedureDec */
            else if (temp instanceof ProcedureDec) {
                /* Update the list of statements inside the Procedure */
                List<Statement> stmtList =
                        addSwapStmts(location, ((ProcedureDec) temp)
                                .getStatements());
                ((ProcedureDec) temp).setStatements(stmtList);
                break;
            }
        }
    }

    // -----------------------------------------------------------
    // GlobalVarList Related Methods
    // -----------------------------------------------------------

    /**
     * Tries to find the passed in variable exp in our global variable
     * list. If found return true, false otherwise.
     * 
     * @param exp VariableExp to be found in the global variable list.
     * 
     */
    public boolean inGlobalVarList(VariableNameExp exp) {
        /* Variable */
        boolean retVal = false;
        Iterator<VarDec> it = myGlobalVarList.iterator();

        /* Loop to find a VarDec with the same name as the passed
         * in variable expression */
        while (it.hasNext()) {
            VarDec tempDec = it.next();

            /* Check names */
            if (tempDec.getName().getName().equals(exp.getName().getName())) {
                retVal = true;
                break;
            }
        }

        return retVal;
    }

    // -----------------------------------------------------------
    // Operation Related Methods
    // -----------------------------------------------------------

    /**
     * Looks for the local operation in the operation map. If found,
     * it returns the list of parameters associated with it. Else, it
     * returns null. 
     * 
     * @param name PosSymbol of the operation
     * 
     */
    public List<ParameterVarDec> retrieveParameterList(PosSymbol name) {
        if (myLocalOperMap.containsKey(name)) {
            return myLocalOperMap.get(name);
        }
        else {
            return null;
        }
    }

    // -----------------------------------------------------------
    // ProgramExp Related Methods
    // -----------------------------------------------------------

    /**
     * Creates a function call statement for Replica
     * 
     * @param oldExp expression to be Replicated
     * 
     */
    public ProgramParamExp createReplicaCall(ProgramExp oldExp) {
        /* Variables */
        ProgramParamExp newExp;

        /* Parameter List */
        List<ProgramExp> params = new List<ProgramExp>();
        params.add(oldExp);

        /* Create new right hand side with the Replica function */
        newExp =
                new ProgramParamExp(oldExp.getLocation(), new PosSymbol(oldExp
                        .getLocation(), Symbol.symbol("Replica")), params, null);

        return newExp;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * Look for the variable declaration in the specified list
     * 
     * @param name The name of the variable represented as a PosSymbol
     * @param list List of variable declarations that we are searching
     * 
     */
    private VarDec iterateFindVarDec(PosSymbol name, List<VarDec> list) {
        /* Variables */
        VarDec retVarDec = null;

        /* Iterate through list of Local Variables */
        Iterator<VarDec> it = list.iterator();
        while (it.hasNext()) {
            /* Obtain nextDec from the iterator */
            VarDec nextVarDec = it.next();

            /* We found it */
            if (nextVarDec.getName().getName().compareTo(name.getName()) == 0) {
                retVarDec = nextVarDec;
                break;
            }
        }

        return retVarDec;
    }

    /**
     * Add the swap call statements for arrays into the right location
     * 
     * @param location Location of the CallStmt/FunctionAssignStmt
     * @param stmtList List of statements from the FacilityOperationDec or ProcedureDec
     * 
     */
    private List<Statement> addSwapCalls(Location location,
            List<Statement> stmtList) {
        /* Loop through the list */
        for (int i = 0; i < stmtList.size(); i++) {
            /* CallStmts */
            if (stmtList.get(i) instanceof CallStmt
                    && ((CallStmt) stmtList.get(i)).getName().getLocation() == location) {
                /* Add the swap statements after the call statement */
                for (int j = 0; j < mySwapCallList.size(); j++) {
                    CallStmt newCallSwapStmt = (CallStmt) mySwapCallList.get(j);
                    stmtList.add(i + 1, newCallSwapStmt);
                }

                /* Add the swap statements before the call statement */
                for (int j = mySwapCallList.size() - 1; j >= 0; j--) {
                    CallStmt newCallSwapStmt = (CallStmt) mySwapCallList.get(j);
                    stmtList.add(i, newCallSwapStmt);
                }

                break;
            }
            /* FunctionAssignStmts */
            else if (stmtList.get(i) instanceof FuncAssignStmt
                    && ((FuncAssignStmt) stmtList.get(i)).getLocation() == location) {
                /* Add the swap statements after the call statement */
                for (int j = 0; j < mySwapCallList.size(); j++) {
                    CallStmt newSwapStmt = (CallStmt) mySwapCallList.get(j);
                    stmtList.add(i + 1, newSwapStmt);
                }

                /* Add the swap statements before the call statement */
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
     * Add the swap statements into the right location
     * 
     * @param location Location of the CallStmt/FunctionAssignStmt
     * @param stmtList List of statements from the FacilityOperationDec or ProcedureDec
     * 
     */
    private List<Statement> addSwapStmts(Location location,
            List<Statement> stmtList) {
        /* Loop through the list */
        for (int i = 0; i < stmtList.size(); i++) {
            /* CallStmts */
            if (stmtList.get(i) instanceof CallStmt
                    && ((CallStmt) stmtList.get(i)).getName().getLocation() == location) {
                /* Add the swap statements after the call statement */
                for (int j = 0; j < mySwapList.size(); j++) {
                    SwapStmt newCallSwapStmt = (SwapStmt) mySwapList.get(j);
                    stmtList.add(i + 1, newCallSwapStmt);
                }

                /* Add the swap statements before the call statement */
                for (int j = mySwapList.size() - 1; j >= 0; j--) {
                    SwapStmt newCallSwapStmt = (SwapStmt) mySwapList.get(j);
                    stmtList.add(i, newCallSwapStmt);
                }

                break;
            }
            /* FunctionAssignStmts */
            else if (stmtList.get(i) instanceof FuncAssignStmt
                    && ((FuncAssignStmt) stmtList.get(i)).getLocation() == location) {
                /* Add the swap statements after the call statement */
                for (int j = 0; j < mySwapList.size(); j++) {
                    SwapStmt newSwapStmt = (SwapStmt) mySwapList.get(j);
                    newSwapStmt.setLocation(location);
                    stmtList.add(i + 1, newSwapStmt);
                }

                /* Add the swap statements before the call statement */
                for (int j = mySwapList.size() - 1; j >= 0; j--) {
                    SwapStmt newSwapStmt = (SwapStmt) mySwapList.get(j);
                    newSwapStmt.setLocation(location);
                    stmtList.add(i, newSwapStmt);
                }

                break;
            }
        }

        return stmtList;
    }
}
