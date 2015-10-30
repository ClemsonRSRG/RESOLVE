/**
 * NestedFuncWalker.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.vcgeneration.treewalkers;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PLambda;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.vcgeneration.AssertiveCode;
import edu.clemson.cs.r2jt.vcgeneration.FacilityFormalToActuals;
import edu.clemson.cs.r2jt.vcgeneration.Utilities;

import java.util.*;

/**
 * TODO: Write a description of this module
 */
public class NestedFuncWalker extends TreeWalkerVisitor {

    // ===========================================================
    // Global Variables
    // ===========================================================

    // Symbol table related items
    private final MathSymbolTableBuilder mySymbolTable;
    private final TypeGraph myTypeGraph;

    // Module Scope
    private final ModuleScope myCurrentModuleScope;

    // Location that instantiated this walker
    private final Location myCurrentLocation;

    // The current assertive code
    private final AssertiveCode myCurrentAssertiveCode;

    // Current Procedure/Operation Module for the Assertive Code
    private final OperationEntry myCurrentOperationEntry;

    // Decreasing clause (if any)
    private final Exp myOperationDecreasingExp;

    // Facility Formal to Actual Map
    private final Map<VarExp, FacilityFormalToActuals> myInstantiatedFacilityArgMap;

    // Requires/Ensures
    private Exp myRequiresClause;
    private Map<String, Exp> myEnsuresClauseMap;

    // Items needed during the walking
    private PosSymbol myQualifier;

    // ===========================================================
    // Constructors
    // ===========================================================

    public NestedFuncWalker(OperationEntry entry, Exp decreasingExp,
            ScopeRepository table, ModuleScope scope,
            AssertiveCode assertiveCode,
            Map<VarExp, FacilityFormalToActuals> instantiatedFacilityArgMap) {
        mySymbolTable = (MathSymbolTableBuilder) table;
        myTypeGraph = mySymbolTable.getTypeGraph();
        myRequiresClause = myTypeGraph.getTrueVarExp();
        myEnsuresClauseMap = new HashMap<String, Exp>();
        myCurrentAssertiveCode = assertiveCode;
        myCurrentLocation = null;
        myCurrentModuleScope = scope;
        myCurrentOperationEntry = entry;
        myOperationDecreasingExp = decreasingExp;
        myInstantiatedFacilityArgMap = instantiatedFacilityArgMap;
        myQualifier = null;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // ProgramDotExp
    // -----------------------------------------------------------

    public void preProgramDotExp(ProgramDotExp exp) {
        myQualifier = exp.getQualifier();
    }

    public void postProgramDotExp(ProgramDotExp exp) {
        myQualifier = null;
    }

    // -----------------------------------------------------------
    // ProgramParamExp
    // -----------------------------------------------------------

    public void postProgramParamExp(ProgramParamExp exp) {
        // Call a method to locate the operation dec for this call
        OperationDec opDec =
                getOperationDec(myCurrentLocation, myQualifier, exp.getName(),
                        exp.getArguments());

        // Get the requires clause for this operation
        Exp opRequires;
        if (opDec.getRequires() != null) {
            opRequires = Exp.copy(opDec.getRequires());
        }
        else {
            opRequires = myTypeGraph.getTrueVarExp();
        }

        // Modify the location of the requires clause and add it to myCurrentAssertiveCode
        // Obtain the current location.
        // Note: If we don't have a location, we use the current global location.
        Location reqloc;
        if (exp.getName().getLocation() != null) {
            reqloc = (Location) exp.getName().getLocation().clone();
        }
        else {
            reqloc = (Location) myCurrentLocation.clone();
        }

        // Replace PreCondition variables in the requires clause
        opRequires =
                replaceFormalWithActualReq(opRequires, opDec.getParameters(),
                        exp.getArguments());

        // Replace facility actuals variables in the requires clause
        opRequires =
                replaceFacilityFormalWithActual(myCurrentLocation, opRequires,
                        opDec.getParameters());

        // Append the name of the current procedure
        String details = "";
        if (myCurrentOperationEntry != null) {
            details = " in Procedure " + myCurrentOperationEntry.getName();
        }

        // Set the details of the current location
        reqloc.setDetails("Requires Clause of " + opDec.getName() + details);
        Utilities.setLocation(opRequires, reqloc);

        // Form one requires clause if it is necessary
        if (!opRequires.isLiteralTrue()) {
            if (myRequiresClause.isLiteralTrue()) {
                myRequiresClause = opRequires;
            }
            else {
                myRequiresClause =
                        myTypeGraph.formConjunct(myRequiresClause, opRequires);
            }
        }

        // Get the ensures clause for this operation
        // Note: If there isn't an ensures clause, it is set to "True"
        Exp opEnsures;
        if (opDec.getEnsures() != null) {
            opEnsures = Exp.copy(opDec.getEnsures());

            // Make sure we have an EqualsExp, else it is an error.
            if (opEnsures instanceof EqualsExp) {
                EqualsExp opEqEnsures = (EqualsExp) opEnsures;

                // Has to be a VarExp on the left hand side (containing the name
                // of the function operation)
                if (opEqEnsures.getLeft() instanceof VarExp) {
                    VarExp leftExp = (VarExp) opEqEnsures.getLeft();

                    // Check if it has the name of the operation
                    if (leftExp.getName().equals(opDec.getName())) {
                        Exp ensures = opEqEnsures.getRight();

                        // Obtain the current location
                        Location ensuresLoc;
                        if (exp.getName().getLocation() != null) {
                            // Set the details of the current location
                            ensuresLoc =
                                    (Location) exp.getName().getLocation()
                                            .clone();
                        }
                        else {
                            ensuresLoc = (Location) myCurrentLocation.clone();
                        }
                        ensuresLoc.setDetails("Ensures Clause of "
                                + opDec.getName());
                        Utilities.setLocation(ensures, ensuresLoc);

                        // Replace the formal with the actual
                        ensures =
                                replaceFormalWithActualEns(ensures, opDec
                                        .getParameters(), opDec.getStateVars(),
                                        exp.getArguments(), false);

                        // Replace facility actuals variables in the ensures clause
                        ensures =
                                replaceFacilityFormalWithActual(
                                        myCurrentLocation, ensures, opDec
                                                .getParameters());

                        // Add this ensures clause to our map
                        myEnsuresClauseMap
                                .put(exp.getName().getName(), ensures);
                    }
                    else {
                        Utilities.illegalOperationEnsures(opDec.getLocation());
                    }
                }
                else {
                    Utilities.illegalOperationEnsures(opDec.getLocation());
                }
            }
            else {
                Utilities.illegalOperationEnsures(opDec.getLocation());
            }
        }

        // Check for recursive call of itself
        if (myCurrentOperationEntry != null
                && myCurrentOperationEntry.getName().equals(
                        opDec.getName().getName())
                && myCurrentOperationEntry.getReturnType() != null) {
            // Create a new confirm statement using P_val and the decreasing clause
            VarExp pVal =
                    Utilities.createPValExp(myOperationDecreasingExp
                            .getLocation(), myCurrentModuleScope);

            // Create a new infix expression
            IntegerExp oneExp = new IntegerExp();
            oneExp.setValue(1);
            oneExp.setMathType(myOperationDecreasingExp.getMathType());
            InfixExp leftExp =
                    new InfixExp(myCurrentLocation, oneExp, Utilities
                            .createPosSymbol("+"), Exp
                            .copy(myOperationDecreasingExp));
            leftExp.setMathType(myOperationDecreasingExp.getMathType());
            InfixExp infixExp =
                    Utilities.createLessThanEqExp(myCurrentLocation, leftExp,
                            pVal, myTypeGraph.BOOLEAN);

            // Create the new confirm statement
            Location loc;
            if (myOperationDecreasingExp.getLocation() != null) {
                loc = (Location) myOperationDecreasingExp.getLocation().clone();
            }
            else {
                loc = (Location) myCurrentLocation.clone();
            }
            loc.setDetails("Show Termination of Recursive Call");
            Utilities.setLocation(infixExp, loc);
            ConfirmStmt conf = new ConfirmStmt(loc, infixExp, false);

            // Add it to our list of assertions
            myCurrentAssertiveCode.addCode(conf);
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Returns the final modified ensures clause after all the
     * necessary alterations have been made.</p>
     *
     * @return The complete ensures clause.
     */
    public Exp getEnsuresClause() {
        Set<String> opNameSet = myEnsuresClauseMap.keySet();
        Exp ensures;

        // We can't have more than one thing left in our map.
        // This must mean something went wrong and we have leftover
        // ensures clause that we haven't replaced.
        if (opNameSet.size() > 1) {
            throw new RuntimeException(
                    "An error occurred while walking the tree!");
        }
        // Retrieve the ensures clause of the nested function call.
        else if (opNameSet.size() == 1) {
            ensures = myEnsuresClauseMap.remove(opNameSet.iterator().next());
        }
        // Ideally, this should be checked before us. The ensures clause of any
        // operation that returns a value must be of the explicit form:
        // <Return_Variable_Name> = <Return_Value>
        else {
            throw new RuntimeException("Wrong format for the ensures clause!");
        }

        return ensures;
    }

    /**
     * <p>Returns the final modified requires clause after all the
     * necessary alterations have been made.</p>
     *
     * @return The complete requires clause.
     */
    public Exp getRequiresClause() {
        return myRequiresClause;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Locate and return the corresponding operation dec based on the qualifier,
     * name, and arguments.</p>
     *
     * @param loc Location of the calling statement.
     * @param qual Qualifier of the operation
     * @param name Name of the operation.
     * @param args List of arguments for the operation.
     *
     * @return The operation corresponding to the calling statement in <code>OperationDec</code> form.
     */
    private OperationDec getOperationDec(Location loc, PosSymbol qual,
            PosSymbol name, List<ProgramExp> args) {
        // Obtain the corresponding OperationEntry and OperationDec
        List<PTType> argTypes = new LinkedList<PTType>();
        for (ProgramExp arg : args) {
            argTypes.add(arg.getProgramType());
        }
        OperationEntry opEntry =
                Utilities.searchOperation(loc, qual, name, argTypes,
                        myCurrentModuleScope);

        // Obtain an OperationDec from the OperationEntry
        ResolveConceptualElement element = opEntry.getDefiningElement();
        OperationDec opDec;
        if (element instanceof OperationDec) {
            opDec = (OperationDec) opEntry.getDefiningElement();
        }
        else {
            FacilityOperationDec fOpDec =
                    (FacilityOperationDec) opEntry.getDefiningElement();
            opDec =
                    new OperationDec(fOpDec.getName(), fOpDec.getParameters(),
                            fOpDec.getReturnTy(), fOpDec.getStateVars(), fOpDec
                                    .getRequires(), fOpDec.getEnsures());
        }

        return opDec;
    }

    /**
     * <p>Replace the formal with the actual variables from the facility declaration to
     * the passed in clause.</p>
     *
     * @param opLoc Location of the calling statement.
     * @param clause The requires/ensures clause.
     * @param paramList The list of parameter variables.
     *
     * @return The clause in <code>Exp</code> form.
     */
    private Exp replaceFacilityFormalWithActual(Location opLoc, Exp clause,
            List<ParameterVarDec> paramList) {
        // Make a copy of the original clause
        Exp newClause = Exp.copy(clause);

        for (ParameterVarDec dec : paramList) {
            if (dec.getTy() instanceof NameTy) {
                NameTy ty = (NameTy) dec.getTy();
                PosSymbol tyName = ty.getName().copy();
                PosSymbol tyQualifier = null;
                if (ty.getQualifier() != null) {
                    tyQualifier = ty.getQualifier().copy();
                }

                FacilityFormalToActuals formalToActuals = null;
                for (VarExp v : myInstantiatedFacilityArgMap.keySet()) {
                    FacilityFormalToActuals temp = null;
                    if (tyQualifier != null) {
                        if (tyQualifier.getName().equals(
                                v.getQualifier().getName())
                                && tyName.getName().equals(
                                        v.getName().getName())) {
                            temp = myInstantiatedFacilityArgMap.get(v);
                        }
                    }
                    else {
                        if (tyName.getName().equals(v.getName().getName())) {
                            temp = myInstantiatedFacilityArgMap.get(v);
                        }
                    }

                    // Check to see if we already found one. If we did, it means that
                    // the type is ambiguous and we can't be sure which one it is.
                    if (temp != null) {
                        if (formalToActuals == null) {
                            formalToActuals = temp;
                        }
                        else {
                            Utilities.ambiguousTy(ty, opLoc);
                        }
                    }
                }

                if (formalToActuals != null) {
                    Map<Exp, Exp> conceptMap =
                            formalToActuals.getConceptArgMap();
                    for (Exp e : conceptMap.keySet()) {
                        newClause =
                                Utilities.replace(newClause, e, conceptMap
                                        .get(e));
                    }
                }
                else {
                    // It is OK here, because it is a generic type
                    if (!(ty.getProgramTypeValue() instanceof PTGeneric)) {
                        Utilities.noSuchSymbol(tyQualifier, tyName.getName(),
                                opLoc);
                    }
                }
            }
        }

        return newClause;
    }

    /**
     * <p>Replace the formal with the actual variables
     * inside the ensures clause.</p>
     *
     * @param ensures The ensures clause.
     * @param paramList The list of parameter variables.
     * @param stateVarList The list of state variables.
     * @param argList The list of arguments from the operation call.
     * @param isSimple Check if it is a simple replacement.
     *
     * @return The ensures clause in <code>Exp</code> form.
     */
    private Exp replaceFormalWithActualEns(Exp ensures,
            List<ParameterVarDec> paramList, List<AffectsItem> stateVarList,
            List<ProgramExp> argList, boolean isSimple) {
        // Current final confirm
        Exp newConfirm;

        // List to hold temp and real values of variables in case
        // of duplicate spec and real variables
        List<Exp> undRepList = new ArrayList<Exp>();
        List<Exp> replList = new ArrayList<Exp>();

        // Replace state variables in the ensures clause
        // and create new confirm statements if needed.
        for (int i = 0; i < stateVarList.size(); i++) {
            ConfirmStmt confirmStmt = myCurrentAssertiveCode.getFinalConfirm();
            newConfirm = confirmStmt.getAssertion();
            AffectsItem stateVar = stateVarList.get(i);

            // Only deal with Alters/Reassigns/Replaces/Updates modes
            if (stateVar.getMode() == Mode.ALTERS
                    || stateVar.getMode() == Mode.REASSIGNS
                    || stateVar.getMode() == Mode.REPLACES
                    || stateVar.getMode() == Mode.UPDATES) {
                // Obtain the variable from our free variable list
                Exp globalFreeVar =
                        myCurrentAssertiveCode.getFreeVar(stateVar.getName(),
                                true);
                if (globalFreeVar != null) {
                    VarExp oldNamesVar = new VarExp();
                    oldNamesVar.setName(stateVar.getName());

                    // Create a local free variable if it is not there
                    Exp localFreeVar =
                            myCurrentAssertiveCode.getFreeVar(stateVar
                                    .getName(), false);
                    if (localFreeVar == null) {
                        // TODO: Don't have a type for state variables?
                        localFreeVar =
                                new VarExp(null, null, stateVar.getName());
                        localFreeVar =
                                Utilities.createQuestionMarkVariable(
                                        myTypeGraph.formConjunct(ensures,
                                                newConfirm),
                                        (VarExp) localFreeVar);
                        myCurrentAssertiveCode.addFreeVar(localFreeVar);
                    }
                    else {
                        localFreeVar =
                                Utilities.createQuestionMarkVariable(
                                        myTypeGraph.formConjunct(ensures,
                                                newConfirm),
                                        (VarExp) localFreeVar);
                    }

                    // Creating "#" expressions and replace these in the
                    // ensures clause.
                    OldExp osVar = new OldExp(null, Exp.copy(globalFreeVar));
                    OldExp oldNameOSVar =
                            new OldExp(null, Exp.copy(oldNamesVar));
                    ensures =
                            Utilities.replace(ensures, oldNamesVar,
                                    globalFreeVar);
                    ensures = Utilities.replace(ensures, oldNameOSVar, osVar);

                    // If it is not simple replacement, replace all ensures clauses
                    // with the appropriate expressions.
                    if (!isSimple) {
                        ensures =
                                Utilities.replace(ensures, globalFreeVar,
                                        localFreeVar);
                        ensures =
                                Utilities
                                        .replace(ensures, osVar, globalFreeVar);
                        newConfirm =
                                Utilities.replace(newConfirm, globalFreeVar,
                                        localFreeVar);
                    }

                    // Set newConfirm as our new final confirm statement
                    myCurrentAssertiveCode.setFinalConfirm(newConfirm,
                            confirmStmt.getSimplify());
                }
                // Error: Why isn't it a free variable.
                else {
                    Utilities.notInFreeVarList(stateVar.getName(), stateVar
                            .getLocation());
                }
            }
        }

        // Replace post condition variables in the ensures clause
        for (int i = 0; i < argList.size(); i++) {
            ParameterVarDec varDec = paramList.get(i);
            ProgramExp pExp = argList.get(i);
            PosSymbol VDName = varDec.getName();
            ConfirmStmt confirmStmt = myCurrentAssertiveCode.getFinalConfirm();
            newConfirm = confirmStmt.getAssertion();

            // VarExp form of the parameter variable
            VarExp oldExp = new VarExp(null, null, VDName);
            oldExp.setMathType(pExp.getMathType());
            oldExp.setMathTypeValue(pExp.getMathTypeValue());

            // Deal with nested function calls
            Exp undqRep = null, quesRep = null;
            OldExp oSpecVar, oRealVar;
            String replName = null;
            Exp repl;
            if (pExp instanceof ProgramParamExp) {
                String opName = ((ProgramParamExp) pExp).getName().getName();

                // Check to see if we have an ensures clause
                // for this nested call
                if (myEnsuresClauseMap.containsKey(opName)) {
                    // The replacement will be the inner operation's
                    // ensures clause.
                    repl = myEnsuresClauseMap.remove(opName);
                }
                else {
                    // Something went wrong with the walking mechanism.
                    // We should have seen this inner operation call before
                    // processing the outer operation call.
                    throw new RuntimeException();
                }
            }
            // All other types of expressions
            else {
                // Convert the pExp into a something we can use
                repl = Utilities.convertExp(pExp, myCurrentModuleScope);
            }

            // Case #1: ProgramIntegerExp
            // Case #2: ProgramCharExp
            // Case #3: ProgramStringExp
            if (pExp instanceof ProgramIntegerExp
                    || pExp instanceof ProgramCharExp
                    || pExp instanceof ProgramStringExp) {
                Exp convertExp =
                        Utilities.convertExp(pExp, myCurrentModuleScope);
                if (pExp instanceof ProgramIntegerExp) {
                    replName =
                            Integer.toString(((IntegerExp) convertExp)
                                    .getValue());
                }
                else if (pExp instanceof ProgramCharExp) {
                    replName =
                            Character.toString(((CharExp) convertExp)
                                    .getValue());
                }
                else {
                    replName = ((StringExp) convertExp).getValue();
                }

                // Create a variable expression of the form "_?[Argument Name]"
                undqRep =
                        Utilities.createVarExp(null, null, Utilities
                                .createPosSymbol("_?" + replName), pExp
                                .getMathType(), pExp.getMathTypeValue());

                // Create a variable expression of the form "?[Argument Name]"
                quesRep =
                        Utilities.createVarExp(null, null, Utilities
                                .createPosSymbol("?" + replName), pExp
                                .getMathType(), pExp.getMathTypeValue());
            }
            // Case #4: VariableDotExp
            else if (pExp instanceof VariableDotExp) {
                if (repl instanceof DotExp) {
                    Exp pE = ((DotExp) repl).getSegments().get(0);
                    replName = pE.toString(0);

                    // Create a variable expression of the form "_?[Argument Name]"
                    undqRep = Exp.copy(repl);
                    edu.clemson.cs.r2jt.collections.List<Exp> segList =
                            ((DotExp) undqRep).getSegments();
                    VariableNameExp undqNameRep =
                            new VariableNameExp(null, null, Utilities
                                    .createPosSymbol("_?" + replName));
                    undqNameRep.setMathType(pE.getMathType());
                    segList.set(0, undqNameRep);
                    ((DotExp) undqRep).setSegments(segList);

                    // Create a variable expression of the form "?[Argument Name]"
                    quesRep = Exp.copy(repl);
                    segList = ((DotExp) quesRep).getSegments();
                    segList
                            .set(0, ((VariableDotExp) pExp).getSegments()
                                    .get(0));
                    ((DotExp) quesRep).setSegments(segList);
                }
                else if (repl instanceof VariableDotExp) {
                    Exp pE = ((VariableDotExp) repl).getSegments().get(0);
                    replName = pE.toString(0);

                    // Create a variable expression of the form "_?[Argument Name]"
                    undqRep = Exp.copy(repl);
                    edu.clemson.cs.r2jt.collections.List<VariableExp> segList =
                            ((VariableDotExp) undqRep).getSegments();
                    VariableNameExp undqNameRep =
                            new VariableNameExp(null, null, Utilities
                                    .createPosSymbol("_?" + replName));
                    undqNameRep.setMathType(pE.getMathType());
                    segList.set(0, undqNameRep);
                    ((VariableDotExp) undqRep).setSegments(segList);

                    // Create a variable expression of the form "?[Argument Name]"
                    quesRep = Exp.copy(repl);
                    segList = ((VariableDotExp) quesRep).getSegments();
                    segList
                            .set(0, ((VariableDotExp) pExp).getSegments()
                                    .get(0));
                    ((VariableDotExp) quesRep).setSegments(segList);
                }
                // Error: Case not handled!
                else {
                    Utilities.expNotHandled(pExp, pExp.getLocation());
                }
            }
            // Case #5: VariableNameExp
            else if (pExp instanceof VariableNameExp) {
                // Name of repl in string form
                replName = ((VariableNameExp) pExp).getName().getName();

                // Create a variable expression of the form "_?[Argument Name]"
                undqRep =
                        Utilities.createVarExp(null, null, Utilities
                                .createPosSymbol("_?" + replName), pExp
                                .getMathType(), pExp.getMathTypeValue());

                // Create a variable expression of the form "?[Argument Name]"
                quesRep =
                        Utilities.createVarExp(null, null, Utilities
                                .createPosSymbol("?" + replName), pExp
                                .getMathType(), pExp.getMathTypeValue());
            }
            // Case #6: ProgramParamExp
            else if (pExp instanceof ProgramParamExp) {
                // Name of repl in string form
                replName = ((ProgramParamExp) pExp).getName().getName();

                // Create a variable expression of the form "_?[Argument Name]"
                undqRep =
                        Utilities.createVarExp(null, null, Utilities
                                .createPosSymbol("_?" + replName), pExp
                                .getMathType(), pExp.getMathTypeValue());

                // Create a variable expression of the form "?[Argument Name]"
                quesRep =
                        Utilities.createVarExp(null, null, Utilities
                                .createPosSymbol("?" + replName), pExp
                                .getMathType(), pExp.getMathTypeValue());
            }
            // Error: Case not handled!
            else {
                Utilities.expNotHandled(pExp, pExp.getLocation());
            }

            // "#" versions of oldExp and repl
            oSpecVar = new OldExp(null, Exp.copy(oldExp));
            oRealVar = new OldExp(null, Exp.copy(repl));

            // Nothing can be null!
            if (oldExp != null && quesRep != null && oSpecVar != null
                    && repl != null && oRealVar != null) {
                // Alters, Clears, Reassigns, Replaces, Updates
                if (varDec.getMode() == Mode.ALTERS
                        || varDec.getMode() == Mode.CLEARS
                        || varDec.getMode() == Mode.REASSIGNS
                        || varDec.getMode() == Mode.REPLACES
                        || varDec.getMode() == Mode.UPDATES) {
                    Exp quesVar;

                    // Obtain the free variable
                    VarExp freeVar =
                            (VarExp) myCurrentAssertiveCode.getFreeVar(
                                    Utilities.createPosSymbol(replName), false);
                    if (freeVar == null) {
                        freeVar =
                                Utilities
                                        .createVarExp(
                                                varDec.getLocation(),
                                                null,
                                                Utilities
                                                        .createPosSymbol(replName),
                                                varDec.getTy()
                                                        .getMathTypeValue(),
                                                null);
                    }

                    // Apply the question mark to the free variable
                    freeVar =
                            Utilities
                                    .createQuestionMarkVariable(myTypeGraph
                                            .formConjunct(ensures, newConfirm),
                                            freeVar);

                    if (pExp instanceof ProgramDotExp
                            || pExp instanceof VariableDotExp) {
                        // Make a copy from repl
                        quesVar = Exp.copy(repl);

                        // Replace the free variable in the question mark variable as the first element
                        // in the dot expression.
                        VarExp tmpVar =
                                new VarExp(null, null, freeVar.getName());
                        tmpVar.setMathType(myTypeGraph.BOOLEAN);
                        edu.clemson.cs.r2jt.collections.List<Exp> segs =
                                ((DotExp) quesVar).getSegments();
                        segs.set(0, tmpVar);
                        ((DotExp) quesVar).setSegments(segs);
                    }
                    else {
                        // Create a variable expression from free variable
                        quesVar = new VarExp(null, null, freeVar.getName());
                        quesVar.setMathType(freeVar.getMathType());
                        quesVar.setMathTypeValue(freeVar.getMathTypeValue());
                    }

                    // Add the new free variable to free variable list
                    myCurrentAssertiveCode.addFreeVar(freeVar);

                    // Check if our ensures clause has the parameter variable in it.
                    if (ensures.containsVar(VDName.getName(), true)
                            || ensures.containsVar(VDName.getName(), false)) {
                        // Replace the ensures clause
                        ensures = Utilities.replace(ensures, oldExp, undqRep);
                        ensures = Utilities.replace(ensures, oSpecVar, repl);

                        // Add it to our list of variables to be replaced later
                        undRepList.add(undqRep);
                        replList.add(quesVar);
                    }
                    else {
                        // Replace the ensures clause
                        ensures = Utilities.replace(ensures, oldExp, quesRep);
                        ensures = Utilities.replace(ensures, oSpecVar, repl);
                    }

                    // Update our final confirm with the parameter argument
                    newConfirm = Utilities.replace(newConfirm, repl, quesVar);
                    myCurrentAssertiveCode.setFinalConfirm(newConfirm,
                            confirmStmt.getSimplify());
                }
                // All other modes
                else {
                    // Check if our ensures clause has the parameter variable in it.
                    if (ensures.containsVar(VDName.getName(), true)
                            || ensures.containsVar(VDName.getName(), false)) {
                        // Replace the ensures clause
                        ensures = Utilities.replace(ensures, oldExp, undqRep);
                        ensures = Utilities.replace(ensures, oSpecVar, undqRep);

                        // Add it to our list of variables to be replaced later
                        undRepList.add(undqRep);
                        replList.add(repl);
                    }
                    else {
                        // Replace the ensures clause
                        ensures = Utilities.replace(ensures, oldExp, repl);
                        ensures = Utilities.replace(ensures, oSpecVar, repl);
                    }
                }
            }
        }

        // Replace the temp values with the actual values
        for (int i = 0; i < undRepList.size(); i++) {
            ensures =
                    Utilities.replace(ensures, undRepList.get(i), replList
                            .get(i));
        }

        return ensures;
    }

    /**
     * <p>Replace the formal with the actual variables
     * inside the requires clause.</p>
     *
     * @param requires The requires clause.
     * @param paramList The list of parameter variables.
     * @param argList The list of arguments from the operation call.
     *
     * @return The requires clause in <code>Exp</code> form.
     */
    private Exp replaceFormalWithActualReq(Exp requires,
            List<ParameterVarDec> paramList, List<ProgramExp> argList) {
        // List to hold temp and real values of variables in case
        // of duplicate spec and real variables
        List<Exp> undRepList = new ArrayList<Exp>();
        List<Exp> replList = new ArrayList<Exp>();

        // Replace precondition variables in the requires clause
        for (int i = 0; i < argList.size(); i++) {
            ParameterVarDec varDec = paramList.get(i);
            ProgramExp pExp = argList.get(i);

            // VarExp form of the parameter variable
            VarExp oldExp =
                    Utilities.createVarExp(null, null, varDec.getName(), pExp
                            .getMathType(), pExp.getMathTypeValue());

            // Deal with nested function calls
            Exp repl;
            if (pExp instanceof ProgramParamExp) {
                String opName = ((ProgramParamExp) pExp).getName().getName();

                // Check to see if we have an ensures clause
                // for this nested call
                if (myEnsuresClauseMap.containsKey(opName)) {
                    // The replacement will be the inner operation's
                    // ensures clause.
                    repl = myEnsuresClauseMap.get(opName);
                }
                else {
                    // Something went wrong with the walking mechanism.
                    // We should have seen this inner operation call before
                    // processing the outer operation call.
                    throw new RuntimeException();
                }
            }
            // All other types of expressions
            else {
                // Convert the pExp into a something we can use
                repl = Utilities.convertExp(pExp, myCurrentModuleScope);
            }

            // New VarExp
            VarExp newExp =
                    Utilities.createVarExp(null, null, Utilities
                            .createPosSymbol("_" + varDec.getName().getName()),
                            repl.getMathType(), repl.getMathTypeValue());

            // Replace the old with the new in the requires clause
            requires = Utilities.replace(requires, oldExp, newExp);

            // Add it to our list
            undRepList.add(newExp);
            replList.add(repl);
        }

        // Replace the temp values with the actual values
        for (int i = 0; i < undRepList.size(); i++) {
            requires =
                    Utilities.replace(requires, undRepList.get(i), replList
                            .get(i));
        }

        return requires;
    }

}