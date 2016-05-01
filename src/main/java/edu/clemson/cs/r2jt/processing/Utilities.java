/**
 * Utilities.java
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
package edu.clemson.cs.r2jt.processing;

// Libraries
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.misc.SourceErrorException;

/**
 * TODO: Write a description of this module
 */
public class Utilities {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /**
     * <p>List of all facility type declarations</p>
     */
    private List<FacilityTypeDec> myFacilityTypeList;

    /**
     * <p>List of all global variables</p>
     */
    private List<VarDec> myGlobalVarList;

    /**
     * <p>Map of all local operations and their associated
     * parameter variables.</p>
     */
    private Map<String, List<ParameterVarDec>> myLocalOperMap;

    /**
     * <p>List of all local variables</p>
     */
    private List<VarDec> myLocalVarList;

    /**
     * <p>List of all parameter variables</p>
     */
    private List<VarDec> myParameterVarList;

    /**
     * <p>List of all representation type declarations</p>
     */
    private List<RepresentationDec> myRepresentationDecList;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Utilities() {
        myFacilityTypeList = null;
        myGlobalVarList = null;
        myLocalOperMap = new Map<String, List<ParameterVarDec>>();
        myLocalVarList = null;
        myParameterVarList = null;
        myRepresentationDecList = null;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    public void duplicateVariableDeclaration(Location location, String varName) {
        String message =
                "The Pre/Post Processor created a variable of the name "
                        + varName
                        + ", but it already exists inside this local operation.";
        throw new SourceErrorException(message, location);
    }

    // -----------------------------------------------------------
    // Initialization/Finalization for Concept/Enhancement
    // Realizations and Facilities
    // -----------------------------------------------------------

    /**
     * <p>Performs cleanup for all the lists instantiated while
     * running the Pre/Post Processor.</p>
     */
    public void finalModuleDec() {
        myGlobalVarList = null;
        myFacilityTypeList = null;
        myLocalOperMap = null;
        myRepresentationDecList = null;
    }

    /**
     * <p>Given the list of <code>Dec</code> for a particular
     * <code>ModuleDec</code>, we store all relevant information
     * for future use.</p>
     *
     * @param decList List of all the <code>Dec</code> declared
     *                by the Module.
     */
    public void initModuleDec(List<Dec> decList) {
        // Store any global variables, records, facilities
        // and local operations into their appropriate lists.
        for (Dec current : decList) {
            if (current instanceof FacilityOperationDec) {
                // Type cast to ProcedureDec
                FacilityOperationDec dec = (FacilityOperationDec) current;

                // Update the map with the new operation
                myLocalOperMap
                        .put(dec.getName().getName(), dec.getParameters());
            }
            else if (current instanceof FacilityTypeDec) {
                // Create the list for facility type declarations if it is null
                if (myFacilityTypeList == null) {
                    myFacilityTypeList = new List<FacilityTypeDec>();
                }

                // Add the FacilityTypeDec into our list
                myFacilityTypeList.add((FacilityTypeDec) current);
            }
            else if (current instanceof ProcedureDec) {
                // Type cast to ProcedureDec
                ProcedureDec dec = (ProcedureDec) current;

                // Update the map with the new operation
                myLocalOperMap
                        .put(dec.getName().getName(), dec.getParameters());
            }
            else if (current instanceof RepresentationDec) {
                // Create the list for representation type declarations if it is null
                if (myRepresentationDecList == null) {
                    myRepresentationDecList = new List<RepresentationDec>();
                }

                // Add the RepresentationDec into our list
                myRepresentationDecList.add((RepresentationDec) current);
            }
            else if (current instanceof VarDec) {
                // Create the list for global variables if it is null
                if (myGlobalVarList == null) {
                    myGlobalVarList = new List<VarDec>();
                }

                // Add the VarDec into our list
                myGlobalVarList.add((VarDec) current);
            }
        }
    }

    // -----------------------------------------------------------
    // Methods to handle things inside local operations
    // -----------------------------------------------------------

    /**
     * <p>Adds a newly created <code>VarDec</code> to our list of
     * local variables</p>
     */
    public void addNewLocalVariable(VarDec dec) {
        if (myLocalVarList.contains(dec)) {
            // Throws an exception before we have created this variable before
            duplicateVariableDeclaration(dec.getLocation(), dec.getName()
                    .getName());
        }
        else {
            myLocalVarList.add(dec);
        }
    }

    /**
     * <p>Performs cleanup for the parameter and variable list
     * instantiated while visiting a local operation.</p>
     */
    public void finalOperationDec() {
        myLocalVarList = null;
        myParameterVarList = null;
    }

    /**
     * <p>Retrieves the list of local variables</p>
     *
     * @return A list of <code>VarDecs</code>.
     */
    public List<VarDec> getLocalVarList() {
        return myLocalVarList;
    }

    /**
     * <p>Retrieves the list of local variables</p>
     *
     * @return A list of <code>VarDecs</code>.
     */
    public List<VarDec> getParameterVarList() {
        return myParameterVarList;
    }

    /**
     * <p>Stores the parameter and local variables for a given
     * operation for future use.</p>
     *
     * @param parameterVarDecList List of all parameter variables
     *                            for this operation.
     * @param varDecList List of all local variables for this
     *                   operation.
     */
    public void initOperationDec(List<ParameterVarDec> parameterVarDecList,
            List<VarDec> varDecList) {
        // Store local variables
        myLocalVarList = varDecList;

        // Convert parameter variables to regular variable
        // declarations.
        List<VarDec> parameterVarList = new List<VarDec>();
        for (ParameterVarDec p : parameterVarDecList) {
            VarDec v = new VarDec(p.getName(), p.getTy());
            parameterVarList.add(v);
        }
        myParameterVarList = parameterVarList;
    }

    /**
     * <p>Checks our map of operations to see if an operation with
     * string name passed is a local operation.</p>
     *
     * @param opName The string containing the name of the operation.
     *
     * @return true/false depending if it is in our map or not
     */
    public boolean isLocalOper(String opName) {
        return myLocalOperMap.containsKey(opName);
    }

    /**
     * <p>Returns list of parameters associated with the local
     * operation in the operation map.</p>
     *
     * @param opName <code>PosSymbol</code> of the operation
     *
     * @return A list of <code>ParameterVarDec</code>
     */
    public List<ParameterVarDec> retrieveParameterList(String opName) {
        return myLocalOperMap.get(opName);
    }

    /**
     * <p>Searches the first instance of the variable declaration
     * inside records.</p>
     *
     * @param rName Name of the record.
     * @param vName Name of the variable.
     *
     * @return A <code>VarDec</code> if found, null otherwise.
     */
    public VarDec searchRecords(PosSymbol rName, PosSymbol vName) {
        // Return variable
        VarDec v = null;

        // Concept/Enhancement Realization Records
        if (myRepresentationDecList != null) {
            for (RepresentationDec d : myRepresentationDecList) {
                // Locate a record with the record name
                // passed in.
                if (d.getName().equals(rName.getName())) {
                    RecordTy recordTy = (RecordTy) d.getRepresentation();

                    // Locate the variable inside the record
                    v = searchList(vName, recordTy.getFields());
                    break;
                }
            }
        }
        // Facility Module Records
        else {
            for (FacilityTypeDec d : myFacilityTypeList) {
                // Locate a record with the record name
                // passed in.
                if (d.getName().equals(rName)) {
                    RecordTy recordTy = (RecordTy) d.getRepresentation();

                    // Locate the variable inside the record
                    v = searchList(vName, recordTy.getFields());
                    break;
                }
            }
        }

        return v;
    }

    /**
     * <p>Searches the first instance of the variable declaration
     * inside our local variable list, parameter variable list
     * and global variable list. If not found, it will return
     * null.</p>
     *
     * @param name Name of the variable.
     *
     * @return A <code>VarDec</code> if found, null otherwise.
     */
    public VarDec searchVarDecLists(PosSymbol name) {
        // Search the local variable list first
        VarDec v = searchList(name, myLocalVarList);

        // Search the parameter variable list if not found
        if (v == null) {
            v = searchList(name, myParameterVarList);
        }

        // Search the global variable list if not found
        if (v == null) {
            v = searchList(name, myGlobalVarList);
        }

        return v;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Given a list, it searches for a variable with the same
     * name as the one passed in.</p>
     *
     * @param name Name of the variable.
     * @param vList List of variable declarations we are searching.
     *
     * @return A <code>VarDec</code> if found, null otherwise.
     */
    private VarDec searchList(PosSymbol name, List<VarDec> vList) {
        VarDec retVarDec = null;

        // Search through the list looking for our variable.
        for (VarDec v : vList) {
            PosSymbol vPos = v.getName();

            // We found it!
            if (vPos.equals(name.getName())) {
                retVarDec = v;
                break;
            }
        }

        return retVarDec;
    }
}
