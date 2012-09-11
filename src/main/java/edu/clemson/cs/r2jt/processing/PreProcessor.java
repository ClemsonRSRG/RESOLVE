/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2012, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the Clemson University nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software has been developed by past and present members of the
 * Reusable Software Research Group (RSRG) in the School of Computing at
 * Clemson University. Contributors to the initial version are:
 * 
 * Yu-Shan Sun
 * Chuck Cook
 * Hampton Smith
 * Murali Sitaraman
 */
/*
 * PreProcessor.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2012
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.processing;

/* Libraries */
import java.util.Iterator;

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
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.scope.SymbolTable;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.type.BooleanType;

/**
 * TODO: Write a description of this module
 */
public class PreProcessor extends TreeWalkerStackVisitor {

    // ===========================================================
    // Global Variables 
    // ===========================================================

    /* A counter used to keep track the number of variables created */
    private int myCounter;

    /* Utilities */
    private Utilities myUtilities;

    /* List of Symbol Tables */
    List<SymbolTable> mySymbolTables;

    /* Error Handler */
    //private ErrorHandler myErr;

    /* Map of all arrray types encountered */
    private Map<String, NameTy> myArrayFacilityMap;

    private String[] myUsesItems =
            { "Location_Linking_Template_1", "Static_Array_Template" };

    // ===========================================================
    // Constructors
    // ===========================================================

    public PreProcessor(final CompileEnvironment instanceEnvironment) {
        myCounter = 1;
        myUtilities = new Utilities();
        myArrayFacilityMap = new Map<String, NameTy>();
        mySymbolTables = instanceEnvironment.getSymbolTables();
        //myErr = instanceEnvironment.getErrorHandler();
    }

    // ===========================================================
    // TreeWalker Methods
    // ===========================================================

    // -----------------------------------------------------------
    // ArrayTy
    // -----------------------------------------------------------

    @Override
    public void postArrayTy(ArrayTy data) {
        /* Variables */
        Location currentLocation = data.getLocation();
        NameTy newTy = null;
        NameTy oldTy = (NameTy) data.getEntryType();
        ResolveConceptualElement parent = this.getParent();
        String arrayName = null;
        String newArrayName = "";

        /* Check if we have a FacilityTypeDec, RepresentationDec or VarDec */
        if (parent instanceof FacilityTypeDec) {
            arrayName = ((FacilityTypeDec) parent).getName().getName();
        }
        else if (parent instanceof RepresentationDec) {
            arrayName = ((RepresentationDec) parent).getName().getName();
        }
        else if (parent instanceof VarDec) {
            arrayName = ((VarDec) parent).getName().getName();
        }

        /* Check for not null */
        if (arrayName != null) {
            /* Create name in the format of "_(Name of Variable)_Array_Fac_(myCounter)" */
            newArrayName += ("_" + arrayName + "_Array_Fac_" + myCounter++);

            /* Create newTy */
            newTy =
                    new NameTy(new PosSymbol(currentLocation, Symbol
                            .symbol(newArrayName)), new PosSymbol(
                            currentLocation, Symbol.symbol("Static_Array")));

            /* Check if we have a FacilityTypeDec, RepresentationDec or VarDec */
            if (parent instanceof FacilityTypeDec) {
                /* Set the Ty of the Parent */
                ((FacilityTypeDec) parent).setRepresentation(newTy);
            }
            else if (parent instanceof RepresentationDec) {
                /* Set the Ty of the Parent */
                ((RepresentationDec) parent).setRepresentation(newTy);
            }
            else if (parent instanceof VarDec) {
                /* Set the Ty of the Parent */
                ((VarDec) parent).setTy(newTy);
            }

            /* Create a list of arguments for the new FacilityDec */
            List<ModuleArgumentItem> listItem = new List<ModuleArgumentItem>();
            String typeName =
                    ((NameTy) data.getEntryType()).getName().getName();

            // Add the type, Low and High for Arrays
            listItem.add(new ModuleArgumentItem(null, new PosSymbol(
                    currentLocation, Symbol.symbol(typeName)), null));
            listItem.add(new ModuleArgumentItem(null, null, data.getLo()));
            listItem.add(new ModuleArgumentItem(null, null, data.getHi()));

            // Call method to createFacilityDec
            FacilityDec arrayFacilityDec =
                    myUtilities.createFacilityDec(currentLocation,
                            newArrayName, "Static_Array_Template",
                            "Std_Array_Realiz", listItem,
                            new List<ModuleArgumentItem>(),
                            new List<EnhancementItem>(),
                            new List<EnhancementBodyItem>());

            /* Iterate through AST */
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

            /* Add the arrayFacilityDec to the list of Decs where it belongs */
            myUtilities.addFacilityDec(it, arrayFacilityDec);

            /* Saving the Ty of this ArrayFacility for future use */
            myArrayFacilityMap.put(newArrayName, oldTy);
        }
    }

    // -----------------------------------------------------------
    // CallStmt
    // -----------------------------------------------------------

    @Override
    public void preCallStmt(CallStmt stmt) {
    /* Variables *
    List<ProgramExp> argList = stmt.getArguments();
    List<ProgramExp> modifiedArgList = new List<ProgramExp>();
    OperationEntry operation = retrieveOperationEntry(stmt);
    Iterator<VarEntry> parameterIt = operation.getParameters();
    Iterator<ProgramExp> argListIt = argList.iterator();
    
    /* Loop through each argument *
    while (argListIt.hasNext() && parameterIt.hasNext()) {
    	ProgramExp tempExp = argListIt.next();
    	VarEntry tempVarEntry = parameterIt.next();
    
    	/* Check if tempVarEntry is evaluates mode *
    	if (tempVarEntry.getMode() == Mode.EVALUATES) {
    		/* Check if it is a VariableNameExp or VariableDotExp *
    		if (tempExp instanceof VariableNameExp ||
    				tempExp instanceof VariableDotExp) {
    			/* Creates a call to replica and modifies the original Exp *
    			tempExp = myUtilities.createReplicaCall(tempExp);
    		}
    	}
    	
    	/* Add it to the modified list *
    	modifiedArgList.add(tempExp);
    }
    
    /* Replaces the modified argument list *
    stmt.setArguments(modifiedArgList); */
    }

    @Override
    public void postCallStmt(CallStmt stmt) {
        /* Variables */
        List<ProgramExp> argList = stmt.getArguments();

        /* Iterate through argument list */
        for (int i = 0; i < argList.size(); i++) {
            /* Temp variable */
            ProgramExp temp = argList.get(i);

            /* Check if it is a VariableArrayExp */
            if (temp instanceof VariableArrayExp) {
                /* Variable */
                VariableNameExp retval =
                        createNewVariableArrayExp(temp.getLocation(),
                                (VariableArrayExp) temp);
                argList.set(i, retval);
            }
            /* Check if it is a VariableDotExp */
            else if (temp instanceof VariableDotExp) {
                /* Get list of segments */
                List<VariableExp> segList =
                        ((VariableDotExp) temp).getSegments();
                VariableExp firstElement = segList.get(0);
                VariableExp lastElement = segList.get(segList.size() - 1);

                /* Check if the last entry is an instance of array. EX: S.A[i] */
                if (lastElement instanceof VariableArrayExp) {
                    VariableNameExp retval =
                            createNewVariableArrayExp(temp.getLocation(),
                                    (VariableNameExp) firstElement,
                                    (VariableArrayExp) lastElement);

                    argList.set(i, retval);
                }
            }
        }

        /* Check if we have any swap statements we need to add */
        if (!myUtilities.isSwapCallListEmpty()) {
            /* Ancestor iterator */
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

            /* Add the created swap statements */
            myUtilities.loopAndAddSwapCalls(it, stmt.getName().getLocation());

            /* Clear the list */
            myUtilities.clearSwapCallList();
        }
    }

    // -----------------------------------------------------------
    // ConceptBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        /* Get the list of Decs inside this ConceptBodyModuleDec */
        List<Dec> decList = dec.getDecs();

        /* Retrieve the list of operations */
        myUtilities.initLocalProcMap(decList);

        /* Invoke private method (in last section) to find any FacilityTypeDec
         * or global variables */
        myUtilities.initVarDecList(decList);
    }

    @Override
    public void postConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        /* Set list containing records to null */
        myUtilities.setRepresentationDecList(null);

        /* Set list containing global variables to null */
        myUtilities.setGlobalVarList(null);
    }

    // -----------------------------------------------------------
    // ConditionItem (Duplicate code from preIfStmt)
    // -----------------------------------------------------------

    @Override
    public void preConditionItem(ConditionItem stmt) {
        /* Check if the test condition is just a VariableExp */
        if (stmt.getTest() instanceof VariableExp) {
            /* Variables */
            ProgramExp exp = stmt.getTest();
            ProgramExp trueExp =
                    new ProgramParamExp(exp.getLocation(), new PosSymbol(exp
                            .getLocation(), Symbol.symbol("True")),
                            new List<ProgramExp>(), null);

            /* Replace the test condition from "exp" to "exp = True()" */
            stmt.setTest(new ProgramOpExp(exp.getLocation(), 3, exp, trueExp));
        }
    }

    // -----------------------------------------------------------
    // EnhancementBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        /* Get the list of Decs inside this EnhancementBodyModuleDec */
        List<Dec> decList = dec.getDecs();

        /* Retrieve the list of operations */
        myUtilities.initLocalProcMap(decList);

        /* Invoke private method (in last section) to find any FacilityTypeDec
         * or global variables */
        myUtilities.initVarDecList(decList);
    }

    @Override
    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        /* Set list containing records to null */
        myUtilities.setRepresentationDecList(null);

        /* Set list containing global variables to null */
        myUtilities.setGlobalVarList(null);
    }

    // -----------------------------------------------------------
    // FacilityModuleDec
    // -----------------------------------------------------------

    @Override
    public void preFacilityModuleDec(FacilityModuleDec dec) {
        /* Get the list of Decs inside this FacilityModuleDec */
        List<Dec> decList = dec.getDecs();

        /* Save our uses list */
        myUtilities.setUsesList(dec.getUsesItems());

        /* Retrieve the list of operations */
        myUtilities.initLocalProcMap(decList);

        /* Find any FacilityTypeDec or global variables */
        myUtilities.initVarDecList(decList);
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec dec) {
        /* Set list containing records to null */
        myUtilities.setFacilityTypeList(null);

        /* Set list containing global variables to null */
        myUtilities.setGlobalVarList(null);

        /* Add files in the myUsesItem to the uses list */
        for (String s : myUsesItems) {
            myUtilities.addToUsesList(s, dec.getName().getLocation());
        }

        /* Replace the uses item list */
        dec.setUsesItems(myUtilities.getUsesList());
        myUtilities.setUsesList(null);
    }

    // -----------------------------------------------------------
    // FacilityOperationDec
    // -----------------------------------------------------------

    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        /* Set the list of local variables */
        myUtilities.setLocalVarList(dec.getVariables());

        /* Get list of parameter variables and add it to myUtilities*/
        List<VarDec> parameterVarList = new List<VarDec>();
        Iterator<ParameterVarDec> it = dec.getParameters().iterator();
        while (it.hasNext()) {
            ParameterVarDec temp = it.next();
            VarDec newVarDec = new VarDec(temp.getName(), temp.getTy());
            parameterVarList.add(newVarDec);
        }

        myUtilities.setParameterVarList(parameterVarList);
    }

    @Override
    public void postFacilityOperationDec(FacilityOperationDec dec) {
        /* Put the modified variable list into dec */
        dec.setVariables(myUtilities.getLocalVarList());
        List<VarDec> vars = myUtilities.getLocalVarList();
        Location newLoc = dec.getName().getLocation();
        VarDec newVarDec =
                new VarDec(new PosSymbol(newLoc, Symbol.symbol("_Integer")),
                        new NameTy(null, new PosSymbol(newLoc, Symbol
                                .symbol("Integer"))));
        vars.add(newVarDec);
        newVarDec =
                new VarDec(new PosSymbol(newLoc, Symbol.symbol("_Boolean")),
                        new NameTy(null, new PosSymbol(newLoc, Symbol
                                .symbol("Boolean"))));
        vars.add(newVarDec);
        newVarDec =
                new VarDec(new PosSymbol(newLoc, Symbol.symbol("_Character")),
                        new NameTy(null, new PosSymbol(newLoc, Symbol
                                .symbol("Character"))));
        vars.add(newVarDec);
        newVarDec =
                new VarDec(new PosSymbol(newLoc, Symbol.symbol("_Char_Str")),
                        new NameTy(null, new PosSymbol(newLoc, Symbol
                                .symbol("Char_Str"))));
        vars.add(newVarDec);
        dec.setVariables(vars);

        /* Set the list of local variables and parameter variables to null */
        myUtilities.setLocalVarList(null);
        myUtilities.setParameterVarList(null);
    }

    // -----------------------------------------------------------
    // Function Assignment Statement
    // -----------------------------------------------------------

    @Override
    public void preFuncAssignStmt(FuncAssignStmt stmt) {
        /* Variables */
        ProgramExp oldExp = stmt.getAssign();

        /* Apply the Replica function to any VariableNameExp */
        if (oldExp instanceof VariableNameExp) {
            /* Set new right hand side to stmt */
            stmt.setAssign(myUtilities.createReplicaCall(oldExp));
        }
        /* Apply the Replica function to any VariableDotExp that doesn't contain an array */
        else if (oldExp instanceof VariableDotExp) {
            /* List of segments */
            List<VariableExp> segs = ((VariableDotExp) oldExp).getSegments();

            /* Check if the last element is an array or not */
            if (!(segs.get(segs.size() - 1) instanceof VariableArrayExp)) {
                /* Set new right hand side to stmt */
                stmt.setAssign(myUtilities.createReplicaCall(oldExp));
            }
        }
    }

    @Override
    public void postFuncAssignStmt(FuncAssignStmt stmt) {
        /* Variables */
        VariableExp leftExp = stmt.getVar();
        ProgramExp rightExp = stmt.getAssign();

        /* Check if we need to convert the right hand side into a Entry_Replica operation for arrays */
        if (rightExp instanceof VariableArrayExp) {
            /* Create the new ProgramExp */
            ProgramExp newExp =
                    createEntryReplicaExp((VariableArrayExp) rightExp, rightExp
                            .getLocation());

            /* Set new right hand side to stmt */
            stmt.setAssign(newExp);
        }
        else if (rightExp instanceof VariableDotExp) {
            /* List of segments */
            List<VariableExp> segs = ((VariableDotExp) rightExp).getSegments();

            /* Check if the last element is an array or not */
            if (segs.get(segs.size() - 1) instanceof VariableArrayExp) {
                /* Last Element */
                VariableArrayExp lastElement =
                        (VariableArrayExp) segs.get(segs.size() - 1);

                /* Replace the name */
                segs.set(segs.size() - 1, new VariableNameExp(lastElement
                        .getLocation(), lastElement.getQualifier(), lastElement
                        .getName()));
                ((VariableDotExp) rightExp).setSegments(segs);

                /* Create the new ProgramExp */
                ProgramExp newExp =
                        createEntryReplicaExp((VariableDotExp) rightExp,
                                lastElement.getArgument(), rightExp
                                        .getLocation());

                /* Set new right hand side to stmt */
                stmt.setAssign(newExp);
            }
        }

        /* Check if we need to convert this to Assign_Entry operation for arrays */
        if (leftExp instanceof VariableArrayExp) {
            /* Parameter List */
            List<ProgramExp> params = new List<ProgramExp>();
            params.add(new VariableNameExp(leftExp.getLocation(),
                    ((VariableArrayExp) leftExp).getQualifier(),
                    ((VariableArrayExp) leftExp).getName()));
            params.add(stmt.getAssign());
            params.add(((VariableArrayExp) leftExp).getArgument());

            /* Create the Assign_Entry operation */
            CallStmt newStmt =
                    new CallStmt(null, new PosSymbol(stmt.getLocation(), Symbol
                            .symbol("Assign_Entry")), params);

            /* Ancestor Iterator */
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

            /* Add the created call stmt to the right place */
            myUtilities.replaceStmt(it, stmt, newStmt);
        }

        /* Check if we have any swap statements we need to add */
        if (!myUtilities.isSwapCallListEmpty()) {
            /* Ancestor iterator */
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

            /* Add the created swap statements */
            myUtilities.loopAndAddSwapCalls(it, stmt.getLocation());

            /* Clear the list */
            myUtilities.clearSwapCallList();
        }
    }

    // -----------------------------------------------------------
    // IfStmt
    // -----------------------------------------------------------

    @Override
    public void preIfStmt(IfStmt stmt) {
        /* Check if the test condition is just a VariableExp */
        if (stmt.getTest() instanceof VariableExp) {
            /* Variables */
            ProgramExp exp = stmt.getTest();
            ProgramExp trueExp =
                    new ProgramParamExp(exp.getLocation(), new PosSymbol(exp
                            .getLocation(), Symbol.symbol("True")),
                            new List<ProgramExp>(), null);

            /* Replace the test condition from "exp" to "exp = True()" */
            stmt.setTest(new ProgramOpExp(exp.getLocation(), 3, exp, trueExp));
        }
    }

    // -----------------------------------------------------------
    // ProcedureDec
    // -----------------------------------------------------------

    @Override
    public void preProcedureDec(ProcedureDec dec) {
        /* Set the list of local variables */
        myUtilities.setLocalVarList(dec.getVariables());

        /* Get list of parameter variables and add it to myUtilities*/
        List<VarDec> parameterVarList = new List<VarDec>();
        Iterator<ParameterVarDec> it = dec.getParameters().iterator();
        while (it.hasNext()) {
            ParameterVarDec temp = it.next();
            VarDec newVarDec = new VarDec(temp.getName(), temp.getTy());
            parameterVarList.add(newVarDec);
        }

        myUtilities.setParameterVarList(parameterVarList);
    }

    @Override
    public void postProcedureDec(ProcedureDec dec) {
        /* Put the modified variable list into dec */
        dec.setVariables(myUtilities.getLocalVarList());
        List<VarDec> vars = myUtilities.getLocalVarList();
        Location newLoc = dec.getName().getLocation();
        VarDec newVarDec =
                new VarDec(new PosSymbol(newLoc, Symbol.symbol("_Integer")),
                        new NameTy(null, new PosSymbol(newLoc, Symbol
                                .symbol("Integer"))));
        vars.add(newVarDec);
        newVarDec =
                new VarDec(new PosSymbol(newLoc, Symbol.symbol("_Boolean")),
                        new NameTy(null, new PosSymbol(newLoc, Symbol
                                .symbol("Boolean"))));
        vars.add(newVarDec);
        newVarDec =
                new VarDec(new PosSymbol(newLoc, Symbol.symbol("_Character")),
                        new NameTy(null, new PosSymbol(newLoc, Symbol
                                .symbol("Character"))));
        vars.add(newVarDec);
        newVarDec =
                new VarDec(new PosSymbol(newLoc, Symbol.symbol("_Char_Str")),
                        new NameTy(null, new PosSymbol(newLoc, Symbol
                                .symbol("Char_Str"))));
        vars.add(newVarDec);
        dec.setVariables(vars);
        /* Set the list of local variables and parameter variables to null */
        myUtilities.setLocalVarList(null);
        myUtilities.setParameterVarList(null);
    }

    // -----------------------------------------------------------
    // ProgramOpExp
    // -----------------------------------------------------------

    @Override
    public void postProgramOpExp(ProgramOpExp exp) {
        /* Variables */
        ProgramExp firstExp = exp.getFirst();
        ProgramExp secondExp = exp.getSecond();

        /* Check first to see if it is an array */
        if (firstExp instanceof VariableArrayExp) {
            /* Create the new ProgramExp */
            ProgramExp newExp =
                    createEntryReplicaExp((VariableArrayExp) firstExp, firstExp
                            .getLocation());

            /* Set the newExp to first of exp */
            exp.setFirst(newExp);
        }
        else if (firstExp instanceof VariableDotExp) {
            /* List of segments */
            List<VariableExp> segs = ((VariableDotExp) firstExp).getSegments();

            /* Check if the last element is an array or not */
            if (segs.get(segs.size() - 1) instanceof VariableArrayExp) {
                /* Last Element */
                VariableArrayExp lastElement =
                        (VariableArrayExp) segs.get(segs.size() - 1);

                /* Replace the name */
                segs.set(segs.size() - 1, new VariableNameExp(lastElement
                        .getLocation(), lastElement.getQualifier(), lastElement
                        .getName()));
                ((VariableDotExp) firstExp).setSegments(segs);

                /* Create the new ProgramExp */
                ProgramExp newExp =
                        createEntryReplicaExp((VariableDotExp) firstExp,
                                lastElement.getArgument(), firstExp
                                        .getLocation());

                /* Set the newExp to first of exp */
                exp.setFirst(newExp);
            }
        }

        /* Check second to see if it is an array */
        if (secondExp instanceof VariableArrayExp) {
            /* Create the new ProgramExp */
            ProgramExp newExp =
                    createEntryReplicaExp((VariableArrayExp) secondExp,
                            secondExp.getLocation());

            /* Set the newExp to first of exp */
            exp.setSecond(newExp);
        }
        else if (secondExp instanceof VariableDotExp) {
            /* List of segments */
            List<VariableExp> segs = ((VariableDotExp) secondExp).getSegments();

            /* Check if the last element is an array or not */
            if (segs.get(segs.size() - 1) instanceof VariableArrayExp) {
                /* Last Element */
                VariableArrayExp lastElement =
                        (VariableArrayExp) segs.get(segs.size() - 1);

                /* Replace the name */
                segs.set(segs.size() - 1, new VariableNameExp(lastElement
                        .getLocation(), lastElement.getQualifier(), lastElement
                        .getName()));
                ((VariableDotExp) secondExp).setSegments(segs);

                /* Create the new ProgramExp */
                ProgramExp newExp =
                        createEntryReplicaExp((VariableDotExp) secondExp,
                                lastElement.getArgument(), secondExp
                                        .getLocation());

                /* Set the newExp to first of exp */
                exp.setSecond(newExp);
            }
        }
    }

    // -----------------------------------------------------------
    // ProgramParamExp
    // -----------------------------------------------------------

    @Override
    public void postProgramParamExp(ProgramParamExp exp) {
        /* Variables */
        List<ProgramExp> argList = exp.getArguments();

        /* Iterate through argument list */
        for (int i = 0; i < argList.size(); i++) {
            /* Temp variable */
            ProgramExp temp = argList.get(i);

            /* Check if it is a VariableArrayExp */
            if (temp instanceof VariableArrayExp) {
                /* Variable */
                VariableNameExp retval =
                        createNewVariableArrayExp(temp.getLocation(),
                                (VariableArrayExp) temp);
                argList.set(i, retval);
            }
            /* Check if it is a VariableDotExp */
            else if (temp instanceof VariableDotExp) {
                /* Get list of segments */
                List<VariableExp> segList =
                        ((VariableDotExp) temp).getSegments();
                VariableExp firstElement = segList.get(0);
                VariableExp lastElement = segList.get(segList.size() - 1);

                /* Check if the last element is an VariableArrayExp */
                if (lastElement instanceof VariableArrayExp) {
                    VariableNameExp retval =
                            createNewVariableArrayExp(temp.getLocation(),
                                    (VariableNameExp) firstElement,
                                    (VariableArrayExp) lastElement);
                    argList.set(i, retval);
                }
            }
        }
    }

    // -----------------------------------------------------------
    // Swap Statement
    // ----------------------------------------------------------- 

    @Override
    public void postSwapStmt(SwapStmt stmt) {
        /* Ancestor Iterator */
        Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

        /* Variables */
        Location currentLocation = stmt.getLocation();
        List<ProgramExp> expList = new List<ProgramExp>();

        /* Check if both left hand side and right hand side is a VariableArrayExp */
        if (stmt.getLeft() instanceof VariableArrayExp
                && stmt.getRight() instanceof VariableArrayExp) {
            /* Check if the names of the array is the same */
            VariableArrayExp left = (VariableArrayExp) stmt.getLeft();
            VariableArrayExp right = (VariableArrayExp) stmt.getRight();
            if (left.getName().getName().equals(right.getName().getName())) {
                /* Create the arguments */
                expList.add(new VariableNameExp(stmt.getLocation(), null, left
                        .getName()));
                expList.add(left.getArgument());
                expList.add(right.getArgument());

                /* Create a CallStmt */
                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(currentLocation,
                                Symbol.symbol("Swap_Two_Entries")), expList);

                /* Add the created swap stmt to the right place */
                myUtilities.replaceStmt(it, stmt, swapEntryStmt);
            }
        }
        else {
            /* Check the left statement */
            if (stmt.getLeft() instanceof VariableArrayExp) {
                /* Check right hand side for VariableDotExp with Array */
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

                /* If it doesn't contain array or is simply a VariableNameExp */
                if (containArray == false) {
                    /* Add the arguments necessary for Swap_Entry in Static_Array_Template */
                    expList.add(new VariableNameExp(stmt.getLocation(), null,
                            ((VariableArrayExp) stmt.getLeft()).getName()));
                    expList.add(stmt.getRight());
                    expList.add(((VariableArrayExp) stmt.getLeft())
                            .getArgument());
                }

                /* Create a CallStmt */
                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(currentLocation,
                                Symbol.symbol("Swap_Entry")), expList);

                /* Add the created swap stmt to the right place */
                myUtilities.replaceStmt(it, stmt, swapEntryStmt);
            }
            /* Check the right statement */
            else if (stmt.getRight() instanceof VariableArrayExp) {
                /* Check right hand side for VariableDotExp with Array */
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

                /* If it doesn't contain array or is simply a VariableNameExp */
                if (containArray == false) {
                    /* Add the arguments necessary for Swap_Entry in Static_Array_Template */
                    expList.add(new VariableNameExp(stmt.getLocation(), null,
                            ((VariableArrayExp) stmt.getRight()).getName()));
                    expList.add(stmt.getLeft());
                    expList.add(((VariableArrayExp) stmt.getRight())
                            .getArgument());
                }

                /* Create a CallStmt */
                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(currentLocation,
                                Symbol.symbol("Swap_Entry")), expList);

                /* Add the created swap stmt to the right place */
                myUtilities.replaceStmt(it, stmt, swapEntryStmt);
            }
        }

        /* Check if left hand side and right hand side is a VariableDotExp */
        if (stmt.getLeft() instanceof VariableDotExp
                && stmt.getRight() instanceof VariableDotExp) {
            /* Variables */
            List<VariableExp> leftVarExpList =
                    ((VariableDotExp) stmt.getLeft()).getSegments();
            List<VariableExp> rightVarExpList =
                    ((VariableDotExp) stmt.getRight()).getSegments();

            /* If this VariableDotExp is really a VariableArrayExp */
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
                    /* Check if it is a VariableArrayExp */
                    if (temp1 instanceof VariableArrayExp
                            && temp2 instanceof VariableArrayExp) {
                        if (!(((VariableArrayExp) temp1).getName().getName()
                                .equals(((VariableArrayExp) temp2).getName()
                                        .getName()))) {
                            isSame = false;
                        }
                    }
                    /* Check if it is a VariableNameExp */
                    else if (temp1 instanceof VariableNameExp
                            && temp2 instanceof VariableNameExp) {
                        if (!((VariableNameExp) temp1).getName().getName()
                                .equals(
                                        ((VariableNameExp) temp2).getName()
                                                .getName())) {
                            isSame = false;
                        }
                    }
                    /* Else they are not the same */
                    else {
                        isSame = false;
                    }
                }

                /* Can only handle arrays with the same name */
                if (isSame) {
                    /* Create a new VariableNameExp */
                    leftVarExpList
                            .set(leftVarExpList.size() - 1,
                                    new VariableNameExp(currentLocation, null,
                                            ((VariableArrayExp) leftLastExp)
                                                    .getName()));
                    VariableDotExp newDotExp =
                            ((VariableDotExp) stmt.getLeft());
                    newDotExp.setSegments(leftVarExpList);

                    /* Create the arguments */
                    expList.add(newDotExp);
                    expList.add(((VariableArrayExp) leftLastExp).getArgument());
                    expList
                            .add(((VariableArrayExp) rightLastExp)
                                    .getArgument());

                    /* Create a CallStmt */
                    CallStmt swapEntryStmt =
                            new CallStmt(null, new PosSymbol(currentLocation,
                                    Symbol.symbol("Swap_Two_Entries")), expList);

                    /* Add the created swap stmt to the right place */
                    myUtilities.replaceStmt(it, stmt, swapEntryStmt);
                }
            }
            /* If the leftLastExp is a Variable Array Expression */
            else if (leftLastExp instanceof VariableArrayExp) {
                /* Create a new VariableNameExp */
                leftVarExpList.set(leftVarExpList.size() - 1,
                        new VariableNameExp(currentLocation, null,
                                ((VariableArrayExp) leftLastExp).getName()));
                VariableDotExp newDotExp = ((VariableDotExp) stmt.getLeft());
                newDotExp.setSegments(leftVarExpList);

                /* Create the arguments */
                expList.add(newDotExp);
                expList.add(((VariableDotExp) stmt.getRight()));
                expList.add(((VariableArrayExp) leftLastExp).getArgument());

                /* Create a CallStmt */
                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(currentLocation,
                                Symbol.symbol("Swap_Entry")), expList);

                /* Add the created swap stmt to the right place */
                myUtilities.replaceStmt(it, stmt, swapEntryStmt);
            }
            /* If the rightLastExp is a Variable Array Expression */
            else if (rightLastExp instanceof VariableArrayExp) {
                /* Create a new VariableNameExp */
                rightVarExpList.set(rightVarExpList.size() - 1,
                        new VariableNameExp(currentLocation, null,
                                ((VariableArrayExp) rightLastExp).getName()));
                VariableDotExp newDotExp = ((VariableDotExp) stmt.getRight());
                newDotExp.setSegments(rightVarExpList);

                /* Create the arguments */
                expList.add(newDotExp);
                expList.add(((VariableDotExp) stmt.getLeft()));
                expList.add(((VariableArrayExp) rightLastExp).getArgument());

                /* Create a CallStmt */
                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(currentLocation,
                                Symbol.symbol("Swap_Entry")), expList);

                /* Add the created swap stmt to the right place */
                myUtilities.replaceStmt(it, stmt, swapEntryStmt);
            }
        }
        else {
            /* Check if left hand side is a VariableDotExp and right hand side is not an array */
            if (stmt.getLeft() instanceof VariableDotExp
                    && stmt.getRight() instanceof VariableNameExp) {
                /* Variables */
                List<VariableExp> leftVarExpList =
                        ((VariableDotExp) stmt.getLeft()).getSegments();
                VariableExp leftLastExp =
                        leftVarExpList.get(leftVarExpList.size() - 1);

                /* Do replacement if leftLastExp is an array */
                if (leftLastExp instanceof VariableArrayExp) {
                    /* Create a new VariableNameExp */
                    leftVarExpList
                            .set(leftVarExpList.size() - 1,
                                    new VariableNameExp(currentLocation, null,
                                            ((VariableArrayExp) leftLastExp)
                                                    .getName()));
                    VariableDotExp newDotExp =
                            ((VariableDotExp) stmt.getLeft());
                    newDotExp.setSegments(leftVarExpList);

                    /* Create the arguments */
                    expList.add(newDotExp);
                    expList.add(stmt.getRight());
                    expList.add(((VariableArrayExp) leftLastExp).getArgument());

                    /* Create a CallStmt */
                    CallStmt swapEntryStmt =
                            new CallStmt(null, new PosSymbol(currentLocation,
                                    Symbol.symbol("Swap_Entry")), expList);

                    /* Add the created swap stmt to the right place */
                    myUtilities.replaceStmt(it, stmt, swapEntryStmt);
                }
            }
            /* Check if right hand side is a VariableDotExp and left hand side is not an array */
            else if (stmt.getRight() instanceof VariableDotExp
                    && stmt.getLeft() instanceof VariableNameExp) {
                /* Variables */
                List<VariableExp> rightVarExpList =
                        ((VariableDotExp) stmt.getRight()).getSegments();
                VariableExp rightLastExp =
                        rightVarExpList.get(rightVarExpList.size() - 1);

                /* Do replacement if leftLastExp is an array */
                if (rightLastExp instanceof VariableArrayExp) {
                    /* Create a new VariableNameExp */
                    rightVarExpList
                            .set(rightVarExpList.size() - 1,
                                    new VariableNameExp(currentLocation, null,
                                            ((VariableArrayExp) rightLastExp)
                                                    .getName()));
                    VariableDotExp newDotExp =
                            ((VariableDotExp) stmt.getRight());
                    newDotExp.setSegments(rightVarExpList);

                    /* Create the arguments */
                    expList.add(newDotExp);
                    expList.add(stmt.getLeft());
                    expList
                            .add(((VariableArrayExp) rightLastExp)
                                    .getArgument());

                    /* Create a CallStmt */
                    CallStmt swapEntryStmt =
                            new CallStmt(null, new PosSymbol(currentLocation,
                                    Symbol.symbol("Swap_Entry")), expList);

                    /* Add the created swap stmt to the right place */
                    myUtilities.replaceStmt(it, stmt, swapEntryStmt);
                }
            }
        }
    }

    // -----------------------------------------------------------
    // WhileStmt (Duplicate code from preIfStmt)
    // -----------------------------------------------------------

    @Override
    public void preWhileStmt(WhileStmt stmt) {
        /* Check if the test condition is just a VariableExp */
        if (stmt.getTest() instanceof VariableExp) {
            /* Variables */
            ProgramExp exp = stmt.getTest();
            ProgramExp trueExp =
                    new ProgramParamExp(exp.getLocation(), new PosSymbol(exp
                            .getLocation(), Symbol.symbol("True")),
                            new List<ProgramExp>(), null);

            /* Replace the test condition from "exp" to "exp = True()" */
            stmt.setTest(new ProgramOpExp(exp.getLocation(), 3, exp, trueExp));
        }
    }

    @Override
    public void postWhileStmt(WhileStmt stmt) {
        /* Variables */
        Location whileLoc = stmt.getLocation();
        List<VariableExp> changingVarList = stmt.getChanging();
        Exp exp = stmt.getMaintaining();

        /* Check if the list is empty */
        if (changingVarList == null) {
            changingVarList = new List<VariableExp>();

            /* Add all local variables and parameters */
            changingVarList =
                    addAllVariables(whileLoc, myUtilities.getLocalVarList(),
                            changingVarList);
            changingVarList =
                    addAllVariables(whileLoc,
                            myUtilities.getParameterVarList(), changingVarList);

            /* Add the list back into the while statement */
            stmt.setChanging(changingVarList);
        }

        /* Check if we have a maintaining or not */
        if (exp == null) {
            /* Create a boolean type instance */
            BooleanType b = BooleanType.INSTANCE;

            /* Create the new Exp */
            Exp newExp =
                    new VarExp(whileLoc, null, new PosSymbol(whileLoc, Symbol
                            .symbol("true")));

            /* Set the type to boolean */
            newExp.setType(b);

            /* Replace the old maintaining clause with this new one */
            stmt.setMaintaining(newExp);
        }
    }

    @Override
    public void preSetExp(SetExp exp) {
        if (exp.getVar() == null && exp.getVars().isEmpty()) {
            PosSymbol name = new PosSymbol(null, Symbol.symbol("empty_set"));
            VarExp emptySetExp = new VarExp(exp.getLocation(), null, name, 0);
            ResolveConceptualElement parent = this.getAncestor(1);
            if (parent instanceof EqualsExp) {
                ((EqualsExp) parent).setRight(emptySetExp);
            }
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * Create a Entry_Replica call
     * 
     * @param exp The array variable
     * @param location A location of the array variable
     * 
     */
    private ProgramExp createEntryReplicaExp(VariableArrayExp exp,
            Location location) {
        /* Parameter List */
        List<ProgramExp> params = new List<ProgramExp>();
        params.add(new VariableNameExp(exp.getLocation(), exp.getQualifier(),
                exp.getName()));
        params.add(exp.getArgument());

        /* Create new right hand side with the Replica function */
        ProgramExp newExp =
                new ProgramParamExp(location, new PosSymbol(location, Symbol
                        .symbol("Entry_Replica")), params, null);

        return newExp;
    }

    /**
     * Create a Entry_Replica call
     * 
     * @param exp The array variable inside a VariableDotExp
     * @param argument The index of the array
     * @param location A location of the array variable
     * 
     */
    private ProgramExp createEntryReplicaExp(VariableDotExp exp,
            ProgramExp argument, Location location) {
        /* Parameter List */
        List<ProgramExp> params = new List<ProgramExp>();
        params.add(exp);
        params.add(argument);

        /* Create new right hand side with the Replica function */
        ProgramExp newExp =
                new ProgramParamExp(location, new PosSymbol(location, Symbol
                        .symbol("Entry_Replica")), params, null);

        return newExp;
    }

    /**
     * Creates a new VariableArrayExp so we can pass it as a parameter.
     * 
     * @param location A location of the array variable
     * @param oldExp The array variable
     * 
     */
    private VariableNameExp createNewVariableArrayExp(Location location,
            VariableArrayExp oldExp) {
        /* Create new VariableNameExp */
        PosSymbol newName =
                new PosSymbol(location, Symbol.symbol("_Array_"
                        + oldExp.getName().getName() + "_" + myCounter++));
        VariableNameExp newExp = new VariableNameExp(location, null, newName);

        /* Locate the array declaration so can get the ty of the the variables inside the array */
        VarDec arrayVarDec = myUtilities.returnVarDec(oldExp.getName());
        NameTy ty = myArrayFacilityMap.get(arrayVarDec.getName().getName());
        if (ty == null) {
            ty =
                    myArrayFacilityMap.get(((NameTy) arrayVarDec.getTy())
                            .getName().getName());
        }

        /* Create a new VarDec */
        VarDec newVarDec = new VarDec(newName, ty);

        /* Add it to our local variable list */
        List<VarDec> varList = myUtilities.getLocalVarList();
        varList.add(newVarDec);
        myUtilities.setLocalVarList(varList);

        /* List */
        List<ProgramExp> expList = new List<ProgramExp>();

        /* Create the argument list and add the arguments necessary for Swap_Entry in Static_Array_Template */
        expList.add(new VariableNameExp(location, null,
                ((VariableArrayExp) oldExp).getName()));
        expList.add(newExp);
        expList.add(((VariableArrayExp) oldExp).getArgument());

        /* Create a CallStmt */
        CallStmt swapEntryStmt =
                new CallStmt(null, new PosSymbol(location, Symbol
                        .symbol("Swap_Entry")), expList);
        myUtilities.addToSwapList(swapEntryStmt);

        /* Return created variable */
        return newExp;
    }

    /**
     * Creates a new VariableArrayExp so we can pass it as a parameter
     * if the old VariableArrayExp is inside a record.
     * 
     * @param location A location of the array variable
     * @param recordExp The name of the record
     * @param arrayExp The name of the array
     * 
     */
    private VariableNameExp createNewVariableArrayExp(Location location,
            VariableNameExp recordExp, VariableArrayExp arrayExp) {
        /* Create new VariableNameExp */
        PosSymbol newName =
                new PosSymbol(location, Symbol.symbol("_Array_"
                        + arrayExp.getName().getName() + "_" + myCounter++));
        VariableNameExp newExp = new VariableNameExp(location, null, newName);

        /* Locate the array declaration so can get the ty of the the variables inside the array */
        VarDec arrayVarDec = myUtilities.returnVarDec(recordExp.getName());

        /* Proceed if we found the arrayVarDec */
        if (arrayVarDec != null) {
            /* Retrieve name of the array ty */
            String tyName =
                    myUtilities.retrieveTyNameFromRecords(arrayExp,
                            (NameTy) arrayVarDec.getTy());

            /* Create a new VarDec */
            VarDec newVarDec =
                    new VarDec(newName, myArrayFacilityMap.get(tyName));

            /* Add it to our local variable list */
            List<VarDec> varList = myUtilities.getLocalVarList();
            varList.add(newVarDec);
            myUtilities.setLocalVarList(varList);

            /* List */
            List<ProgramExp> expList = new List<ProgramExp>();

            /* Create the argument list and add the arguments necessary for Swap_Entry in Static_Array_Template */
            expList.add(new VariableNameExp(location, null,
                    ((VariableArrayExp) arrayExp).getName()));
            expList.add(newExp);
            expList.add(((VariableArrayExp) arrayExp).getArgument());

            /* Create a CallStmt */
            CallStmt swapEntryStmt =
                    new CallStmt(null, new PosSymbol(location, Symbol
                            .symbol("Swap_Entry")), expList);
            myUtilities.addToSwapList(swapEntryStmt);
        }

        return newExp;
    }

    /**
     * Creates new instances of VariableExp as VariableNameExp from 
     * the decList and add them to list.
     * 
     * @param location Location in the file
     * @param decList List of variable declarations
     * @param list List of new variable expressions
     * 
     */
    private List<VariableExp> addAllVariables(Location location,
            List<VarDec> decList, List<VariableExp> list) {
        /* Variables */
        Iterator<VarDec> it = decList.iterator();

        /* Iterate and add non duplicates */
        while (it.hasNext()) {
            /* Temp */
            VarDec temp = it.next();

            /* New VariableNameExp */
            VariableNameExp changingExp =
                    new VariableNameExp(location, null, temp.getName());

            /* Check for duplicates */
            if (!list.contains(changingExp)) {
                list.add(changingExp);
            }
        }

        return list;
    }
}
