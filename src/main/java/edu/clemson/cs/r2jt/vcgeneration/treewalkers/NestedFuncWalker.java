/**
 * NestedFuncWalker.java
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
package edu.clemson.cs.r2jt.vcgeneration.treewalkers;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.vcgeneration.Utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    // Requires/Ensures
    private Exp myRequiresClause;
    private Exp myEnsuresClause;

    // Items needed during the walking
    private Location myCurrentLocation;
    private PosSymbol myCurrentQualifier;

    // ===========================================================
    // Constructors
    // ===========================================================

    public NestedFuncWalker(ScopeRepository table, ModuleScope scope) {
        // Symbol table items
        mySymbolTable = (MathSymbolTableBuilder) table;
        myTypeGraph = mySymbolTable.getTypeGraph();

        // Module Scope
        myCurrentModuleScope = scope;

        // The operation's requires and ensures clause
        myRequiresClause = null;
        myEnsuresClause = null;

        // Qualifier
        myCurrentQualifier = null;

        // Location
        myCurrentLocation = null;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // CallStmt
    // -----------------------------------------------------------

    public void preCallStmt(CallStmt stmt) {
        // Location
        myCurrentLocation = stmt.getLocation();

        // Call a method to locate the operation dec for this call
        OperationDec opDec =
                getOperationDec(myCurrentLocation, stmt.getQualifier(), stmt
                        .getName(), stmt.getArguments());
        boolean isLocal =
                Utilities.isLocationOperation(stmt.getName().getName(),
                        myCurrentModuleScope);

        // Get the ensures clause for this operation
        // Note: If there isn't an ensures clause, it is set to "True"
        if (opDec.getEnsures() != null) {
            myEnsuresClause = Exp.copy(opDec.getEnsures());
        }
        else {
            myEnsuresClause = myTypeGraph.getTrueVarExp();
        }

        // Get the requires clause for this operation
        boolean simplify = false;
        if (opDec.getRequires() != null) {
            myRequiresClause = Exp.copy(opDec.getRequires());

            // Simplify if we just have true
            if (myRequiresClause.isLiteralTrue()) {
                simplify = true;
            }
        }
        else {
            myRequiresClause = myTypeGraph.getTrueVarExp();
            simplify = true;
        }
    }

    public void postCallStmt(CallStmt stmt) {
        myCurrentLocation = null;
    }

    // -----------------------------------------------------------
    // ProgramDotExp
    // -----------------------------------------------------------

    public void preProgramDotExp(ProgramDotExp exp) {
        myCurrentQualifier = exp.getQualifier();
    }

    public void postProgramDotExp(ProgramDotExp exp) {
        myCurrentQualifier = null;
    }

    // -----------------------------------------------------------
    // ProgramParamExp
    // -----------------------------------------------------------

    public void postProgramParamExp(ProgramParamExp exp) {
        // Call a method to locate the operation dec for this call
        OperationDec opDec =
                getOperationDec(myCurrentLocation, myCurrentQualifier,
                        exp.getName(), exp.getArguments());

        // Get the requires clause for this operation
        Exp requires;
        boolean simplify = false;
        if (opDec.getRequires() != null) {
            requires = Exp.copy(opDec.getRequires());

            // Simplify if we just have true
            if (requires.isLiteralTrue()) {
                simplify = true;
            }
        }
        else {
            requires = myTypeGraph.getTrueVarExp();
            simplify = true;
        }

        // Replace PreCondition variables in the requires clause
        requires =
                replaceFormalWithActualReq(requires, opDec.getParameters(),
                        exp.getArguments());
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
        return myEnsuresClause;
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

            // Convert the pExp into a something we can use
            Exp repl = Utilities.convertExp(pExp);

            // VarExp form of the parameter variable
            VarExp oldExp =
                    Utilities.createVarExp(null, null, varDec.getName(), pExp
                            .getMathType(), pExp.getMathTypeValue());

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