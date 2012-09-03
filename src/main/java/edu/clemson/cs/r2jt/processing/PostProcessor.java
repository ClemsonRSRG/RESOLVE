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
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
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
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Yu-Shan Sun
 *     Chuck Cook
 *     Hampton Smith
 *     Murali Sitaraman 
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

import edu.clemson.cs.r2jt.absyn.CallStmt;
import edu.clemson.cs.r2jt.absyn.ConceptBodyModuleDec;
import edu.clemson.cs.r2jt.absyn.Dec;
import edu.clemson.cs.r2jt.absyn.EnhancementBodyModuleDec;
import edu.clemson.cs.r2jt.absyn.FacilityModuleDec;
import edu.clemson.cs.r2jt.absyn.FacilityOperationDec;
import edu.clemson.cs.r2jt.absyn.FuncAssignStmt;
import edu.clemson.cs.r2jt.absyn.ParameterVarDec;
import edu.clemson.cs.r2jt.absyn.ProcedureDec;
import edu.clemson.cs.r2jt.absyn.ProgramDotExp;
import edu.clemson.cs.r2jt.absyn.ProgramExp;
import edu.clemson.cs.r2jt.absyn.ProgramParamExp;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.absyn.VarDec;
import edu.clemson.cs.r2jt.absyn.VariableArrayExp;
import edu.clemson.cs.r2jt.absyn.VariableExp;
import edu.clemson.cs.r2jt.absyn.VariableNameExp;
import edu.clemson.cs.r2jt.absyn.VariableDotExp;
import edu.clemson.cs.r2jt.analysis.ProgramExpTypeResolver;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.entry.OperationEntry;
import edu.clemson.cs.r2jt.entry.VarEntry;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.location.OperationLocator;
import edu.clemson.cs.r2jt.location.SymbolSearchException;
import edu.clemson.cs.r2jt.location.VariableLocator;
import edu.clemson.cs.r2jt.scope.Scope;
import edu.clemson.cs.r2jt.scope.SymbolTable;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.type.Type;

/**
 * TODO: Write a description of this module
 */
public class PostProcessor extends TreeWalkerStackVisitor {
	// ===========================================================
    // Global Variables 
    // ===========================================================
	
	/* A counter used to keep track the number of variables created */
	private int myCounter;
	
	/* Keeps track if the symbol table has changed or not */
	private Boolean myCondition;
	
	/* Utilities */
	private Utilities myUtilities;

	/* Symbol Table */
	private SymbolTable mySymbolTable;
	
	/* Compile Environment */
	private CompileEnvironment  myInstanceEnvironment;
	
	/* Error Handler */
	private ErrorHandler myErr;
	
	/* Used to find types for variable and program expressions */
    private ProgramExpTypeResolver myPetr;
	
    // ===========================================================
    // Constructors
    // ===========================================================

    public PostProcessor(SymbolTable table, final CompileEnvironment instanceEnvironment) {
    	/* Global variables */
    	mySymbolTable = table;
		myInstanceEnvironment = instanceEnvironment;
        myCounter = 1;
        myUtilities = new Utilities();
        myErr = myInstanceEnvironment.getErrorHandler();
        myPetr = new ProgramExpTypeResolver(mySymbolTable, myInstanceEnvironment);
        
        // Set myCondition to false since we haven't changed anything
        setFalse();
    }
    
    // ===========================================================
    // TreeWalker Methods
    // ===========================================================
    
    // -----------------------------------------------------------
    // Call Statement
    // -----------------------------------------------------------
    
    @Override
    public void preCallStmt(CallStmt stmt) {
    	/* Variables */
    	List<ProgramExp> argList = stmt.getArguments();
    	List<ProgramExp> modifiedArgList = new List<ProgramExp>();
    	OperationEntry operation = retrieveOperationEntry(stmt);
    	Iterator<VarEntry> parameterIt = operation.getParameters();
    	Iterator<ProgramExp> argListIt = argList.iterator();
    	
    	/* Loop through each argument */
    	while (argListIt.hasNext() && parameterIt.hasNext()) {
    		ProgramExp tempExp = argListIt.next();
    		VarEntry tempVarEntry = parameterIt.next();
    		
    		/* Check if tempVarEntry is evaluates mode */
    		if (tempVarEntry.getMode() == Mode.EVALUATES) {
    			/* Check if it is a VariableNameExp or VariableDotExp */
    			if (tempExp instanceof VariableNameExp ||
    					tempExp instanceof VariableDotExp) {
    				/* Creates a call to replica and modifies the original Exp */
    				tempExp = myUtilities.createReplicaCall(tempExp);
    			}
    		}
    		
    		/* Add it to the modified list */
    		modifiedArgList.add(tempExp);
    	}
    	
    	/* Replaces the modified argument list */
    	stmt.setArguments(modifiedArgList);
    }
    
    @Override
    public void postCallStmt(CallStmt stmt) {
    	/* Variables */
    	List<ProgramExp> argList = stmt.getArguments();
    	OperationEntry operation = retrieveOperationEntry(stmt);
    	Scope operationScope = operation.getScope();
    	
    	/* Checks for repeated arguments */
    	List<VarDec> newVarList = explicitRepArgCheck(operationScope, argList);
    	
    	/* If we have detected explicit repeated arguments
    	   and created new variables */
    	if (newVarList.size() != 0) {
    		/* We have modified the ModuleDec */
    		setTrue();
    		
    		// TODO
    	}
    	
    	/* Check if we have any swap statements we need to add */
    	if (!myUtilities.isSwapListEmpty()) {
    		/* Ancestor iterator */
    		Iterator<ResolveConceptualElement> it = this.getAncestorInterator();
	    	
    		/* Add the created swap statements */
    		myUtilities.loopAndAddSwapStmts(it, stmt.getName().getLocation());
	    	
	    	/* Clear the list */
    		myUtilities.clearSwapList();
    	}
    }
    
    // -----------------------------------------------------------
    // ConceptBodyModuleDec
    // -----------------------------------------------------------
  
    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec dec) {
    	/* Begin Scope */
    	mySymbolTable.beginModuleScope();
    	
    	/* Get the list of Decs inside this ConceptBodyModuleDec */
    	List<Dec> decList = dec.getDecs();
   
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
    	
    	/* End Scope */
    	mySymbolTable.endModuleScope();
    }
    
    // -----------------------------------------------------------
    // EnhancementBodyModuleDec
    // -----------------------------------------------------------
  
    @Override
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
    	/* Begin Scope */
    	mySymbolTable.beginModuleScope();
    	
    	/* Get the list of Decs inside this EnhancementBodyModuleDec */
    	List<Dec> decList = dec.getDecs();
   
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
    	
    	/* End Scope */
    	mySymbolTable.endModuleScope();
    }
    
    // -----------------------------------------------------------
    // FacilityModuleDec
    // -----------------------------------------------------------
  
    @Override
    public void preFacilityModuleDec(FacilityModuleDec dec) {
    	/* Begin Scope */
    	mySymbolTable.beginModuleScope();
    	
    	/* Get the list of Decs inside this FacilityModuleDec */
    	List<Dec> decList = dec.getDecs();
   
    	/* Invoke private method (in last section) to find any FacilityTypeDec
    	 * or global variables */
    	myUtilities.initVarDecList(decList);
    }
    
    @Override
    public void postFacilityModuleDec(FacilityModuleDec dec) {    	
    	/* Set list containing records to null */
    	myUtilities.setFacilityTypeList(null);
    	
    	/* Set list containing global variables to null */
    	myUtilities.setGlobalVarList(null);
    	
    	/* End Scope */
    	mySymbolTable.endModuleScope();
    }
    
    // -----------------------------------------------------------
    // FacilityOperationDec
    // -----------------------------------------------------------
    
    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {
    	/* Begin Scope */
    	mySymbolTable.beginOperationScope();
    	mySymbolTable.beginProcedureScope();
    	mySymbolTable.bindProcedureTypeNames();
    	
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
    	
    	/* Set the list of local variables and parameter variables to null */
    	myUtilities.setLocalVarList(null);
    	myUtilities.setParameterVarList(null);
    	
    	/* End Scope */
    	mySymbolTable.endProcedureScope();
    	mySymbolTable.endOperationScope();
    }
    
	// -----------------------------------------------------------
    // FuncAssignStmt
    // -----------------------------------------------------------
    
    @Override
    public void postFuncAssignStmt(FuncAssignStmt stmt) {
    	/* Check if we have any swap statements we need to add */
    	if (!myUtilities.isSwapListEmpty()) {
    		/* Ancestor iterator */
    		Iterator<ResolveConceptualElement> it = this.getAncestorInterator();
	    	
    		/* Add the created swap statements */
    		myUtilities.loopAndAddSwapStmts(it, stmt.getLocation());
	    	
	    	/* Clear the list */
    		myUtilities.clearSwapList();
    	}
    }
    
    // -----------------------------------------------------------
    // ProcedureDec
    // -----------------------------------------------------------
    
    @Override
    public void preProcedureDec(ProcedureDec dec) {
    	/* Begin Scope */
    	mySymbolTable.beginOperationScope();
    	mySymbolTable.beginProcedureScope();
    	mySymbolTable.bindProcedureTypeNames();
    	
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
    	
    	/* Set the list of local variables and parameter variables to null */
    	myUtilities.setLocalVarList(null);
    	myUtilities.setParameterVarList(null);
    	
    	/* End Scope */
    	mySymbolTable.endProcedureScope();
    	mySymbolTable.endOperationScope();
    }
    
    // -----------------------------------------------------------
    // ProgramParamExp
    // -----------------------------------------------------------
    
    @Override
    public void preProgramParamExp(ProgramParamExp exp) {
    	/* Variables */
        List<ProgramExp> argList = ((ProgramParamExp) exp).getArguments();
        List<ProgramExp> modifiedArgList = new List<ProgramExp>();
        PosSymbol name = exp.getName();
        PosSymbol qual = null;
        
        /* Check if this is a function call from another module */
        ResolveConceptualElement parent = this.getParent();
        if (parent instanceof ProgramDotExp) {
        	VariableNameExp programName = (VariableNameExp) ((ProgramDotExp) parent).getSegments().get(0);
        	qual = programName.getName();
        }
        
        /* Try to locate the operation declaration */
        OperationEntry operation = retrieveOperationEntry(argList, qual, name);
        
        /* Iterators */
        Iterator<VarEntry> parameterIt = operation.getParameters();
        Iterator<ProgramExp> argListIt = argList.iterator();
        
        /* Loop through each argument */
        while (argListIt.hasNext() && parameterIt.hasNext()) {
        	ProgramExp tempExp = argListIt.next();
        	VarEntry tempVarEntry = parameterIt.next();
        		
        	/* Check if tempVarEntry is evaluates mode */
        	if (tempVarEntry.getMode() == Mode.EVALUATES) {
        			/* Check if it is a VariableNameExp or VariableDotExp */
        		if (tempExp instanceof VariableNameExp ||
        				tempExp instanceof VariableDotExp) {
        			/* Creates a call to replica and modifies the original Exp */
        			tempExp = myUtilities.createReplicaCall(tempExp);
        		}
        	}
        		
        	/* Add it to the modified list */
        	modifiedArgList.add(tempExp);
        }
        	
        /* Replaces the modified argument list */
        exp.setArguments(modifiedArgList);
    }
    
    @Override
    public void postProgramParamExp(ProgramParamExp exp) {
    	/* Variables */
    	List<ProgramExp> argList = exp.getArguments();    	
    	PosSymbol name = exp.getName();
        PosSymbol qual = null;
        
        /* Check if this is a function call from another module */
        ResolveConceptualElement parent = this.getParent();
        if (parent instanceof ProgramDotExp) {
        	VariableNameExp programName = (VariableNameExp) ((ProgramDotExp) parent).getSegments().get(0);
        	qual = programName.getName();
        }
        
        /* Try to locate the operation declaration */
        OperationEntry operation = retrieveOperationEntry(argList, qual, name);
        Scope operationScope = operation.getScope();
    	
    	/* Checks for repeated arguments */
    	List<VarDec> newVarList = explicitRepArgCheck(operationScope, argList);
    	
    	/* If we have detected explicit repeated arguments
    	   and created new variables */
    	if (newVarList.size() != 0) {
    		/* We have modified the ModuleDec */
    		setTrue();
    		
    		// TODO
    	}
    }
    
    // ===========================================================
    // Methods - Changes to the AST
    // ===========================================================
        
    // Function to check if we have changed the AST or not
    public Boolean haveChanged() {    	   	
    	return myCondition;
    }
    
    // Set boolean myCondition to true
    // Note: Only used when we modify the AST
	private void setTrue() {
    	myCondition = true;
    }
    
    // Set boolean myCondition to false
    // Note: Only used in RepArguments constructor
    private void setFalse() {
    	myCondition = false;
    }
    
	// ===========================================================
    // Private Methods
    // ===========================================================
    
    /**
     * Retrieves the operation declaration given the CallStmt
     * Note: In order for this to work, we need to make sure
     *       we begin and end each scope.
     * 
     * @param stmt CallStmt that we are currently on
     * 
     */    
    private OperationEntry retrieveOperationEntry(CallStmt stmt) {
    	/* Variables */
    	PosSymbol qual = stmt.getQualifier();
    	PosSymbol name = stmt.getName();
    	OperationEntry oper = null;
    	OperationLocator locator = new OperationLocator(mySymbolTable, myErr);
    	List<ProgramExp> argList = stmt.getArguments();
    	
    	/* Construct list of types from argList */
    	List<Type> argType = constructTypeList(argList);
    	
    	/* Try to locate the operation */
    	try {
			oper = locator.locateOperation(qual, name, argType);
		} catch (SymbolSearchException e) {
			String msg = "Cannot find operation: " + stmt.toString() + " with these arguments!";
			myErr.error(name.getLocation(), msg);
		}
    	
    	return oper;
    }
    
    /**
     * Retrieves the operation declaration given the argument list,
     * facility name if any and the function call name.
     * Note: In order for this to work, we need to make sure
     *       we begin and end each scope.
     * 
     * @param argList List of arguments
     * @param qual Name of the facility
     * @param name Name of the function
     * 
     */    
    private OperationEntry retrieveOperationEntry(List<ProgramExp> argList, 
    		PosSymbol qual, PosSymbol name) {
    	/* Variables */
    	OperationEntry oper = null;
    	OperationLocator locator = new OperationLocator(mySymbolTable, myErr);
    	
    	/* Construct list of types from argList */
    	List<Type> argType = constructTypeList(argList);
    	
    	/* Try to locate the operation */
    	try {
			oper = locator.locateOperation(qual, name, argType);
		} catch (SymbolSearchException e) {
			String msg = "Cannot find operation: " + name + " with these arguments!";
			myErr.error(name.getLocation(), msg);
		}
    	
    	return oper;
    }
    
    /**
     * Retrieves the Type given a ProgramExp
     * Note: In order for this to work, we need to make sure
     *       we begin and end each scope.
     * 
     * @param argList List of arguments
     * 
     */  
    private List<Type> constructTypeList(List<ProgramExp> argList) {
    	/* Variables */
    	Iterator<ProgramExp> it = argList.iterator();
    	List<Type> argType = new List<Type>();
    	
    	/* Loop */
    	while (it.hasNext()) {
    		ProgramExp temp = it.next();    		
    		
    		try {
    			/* Try to locate the type for this ProgramExp and add it to the retList */
    			Type atype = myPetr.getProgramExpType(temp);
    			argType.add(atype);
    		} catch (TypeResolutionException e) {
    			/* Print error message */
    			String msg = "Cannot get the program type of: " + temp.toString();
    			myErr.error(temp.getLocation(), msg);
    		}
    	}
    	
    	return argType;
    }
    
    /**
     * Checks for repeated arguments of the following types:
     * Type 1 repeated arguments: {F(U, U)}
     * Type 3 repeated arguments: {F(A, A[i])}
     * Type 4 repeated arguments: {F(R, R.x)}
     * Type 5 repeated arguments: {F(U), where U is a global variable}
     * 
     * @param argList List of arguments
     * 
     */
    private List<VarDec> explicitRepArgCheck(Scope scope, List<ProgramExp> args) {
    	/* Variables */
    	List<VarDec> createdVariablesList = new List<VarDec>();
		VariableNameExp retVariableNameExp;
		VarDec retVarDec;
    	
    	/* Loop and check for repeated arguments */
    	for (int i = 0; i < args.size() && args.size() > 1; i++) {
			/* Get the current element in the arg list */
			ProgramExp iExp = args.get(i);
			
			/* Check to see if it is of type VariableExp */
			if (iExp instanceof VariableExp && !(iExp instanceof VariableArrayExp)) {
				/* Check to see if it is a repeated argument of type 5 */
				if (iExp instanceof VariableNameExp && (myUtilities.getGlobalVarList() != null)) {
					retVarDec = type5Check(scope, (VariableNameExp) iExp);
				} else {
					retVarDec = null;
				}
				
				/* Found a type 5 repeated argument */
				if (retVarDec != null) {
					/* Create a new variable expression */
					retVariableNameExp = myUtilities.createVariableNameExp((VariableNameExp) iExp, myCounter++);
					
					/* Set i to newly created VariableNameExp */
					args.set(i, retVariableNameExp);
					
					/* Change name of retVarDec with the new name */
					retVarDec.setName(retVariableNameExp.getName());
					
					/* Add the new VarDec into our list of created VarDecs */
					createdVariablesList.add(retVarDec);
				}
				
				// TODO
			}
    	}
    	
    	return createdVariablesList;
    }
        
    /**
     * Checks for repeated arguments of the type:
     * Type 5 repeated arguments: {F(U), where U is a global variable}
     * 
     * @param exp Variable
     * 
     */
    private VarDec type5Check(Scope scope, VariableNameExp exp) {
    	/* Variables */
    	VarDec retVarDec = null;
    	VariableLocator vlocator = new VariableLocator(mySymbolTable, myErr);
    	
    	/* Checks to see if this is a global variable */
    	if (myUtilities.inGlobalVarList(exp)) {
	    	/* Tries to locate the variable in our symbol table */
			try {
				VarEntry vEntry = vlocator.locateProgramVariable(exp.getQualifier(), exp.getName());
				
				/* If the scope of the variable is the same as the scope of the function,
				 * we have located a potential type 5 repeated argument */
				if (vEntry.getScope().getScopeID().equals(scope.getScopeID())) {
					VarDec expVarDec = myUtilities.returnVarDec(exp.getName());
					retVarDec = new VarDec(expVarDec.getName(), expVarDec.getTy());
				}
			} catch (SymbolSearchException e) {
				String msg = "Cannot locate the global variable: " + exp.toString() + " in our symbol table.";
				myErr.error(exp.getLocation(), msg);
			}
    	}
    	
    	return retVarDec;
    }
}
