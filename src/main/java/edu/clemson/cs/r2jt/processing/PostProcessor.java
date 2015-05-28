/**
 * PostProcessor.java
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
package edu.clemson.cs.r2jt.processing;

// Libraries
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.misc.SourceErrorException;

/**
 * TODO: Write a description of this module
 */
public class PostProcessor extends TreeWalkerStackVisitor {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /**
     * <p>Collection of all symbol tables other than my own.
     * Mine doesn't exist because I haven't gone through the
     * population stage yet.</p>
     */
    private final MathSymbolTableBuilder mySymbolTable;

    /**
     * <p>Type graph of mathematical expressions.</p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>Utilities class that contains methods that are used
     * in both pre and post Processors.</p>
     */
    private Utilities myUtilities;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PostProcessor(ScopeRepository table) {
        mySymbolTable = (MathSymbolTableBuilder) table;
        myTypeGraph = mySymbolTable.getTypeGraph();
        myUtilities = new Utilities();
    }

    // ===========================================================
    // TreeWalker Methods
    // ===========================================================

    // -----------------------------------------------------------
    // CallStmt
    // -----------------------------------------------------------

    @Override
    public void preCallStmt(CallStmt stmt) {
    /*   Commented out until we hear back from Murali
    // Variables
    Location loc = stmt.getLocation();
    PosSymbol name = stmt.getName();
    List<ProgramExp> argList = stmt.getArguments();
    boolean localOper = myUtilities.isLocalOper(name.getName());

    // Check if the called operation is a local operation
    if (localOper) {
        List<ParameterVarDec> parameterList =
                myUtilities.retrieveParameterList(name.getName());

        // Make sure that we have the right operation
        if (parameterList.size() == argList.size()) {
            // Replaces the modified argument list
            stmt.setArguments(applyReplica(parameterList, argList));
        }
        else {
            // Found an operation with the same name,
            // but different argument size
            wrongOperation(loc, name, parameterList.size(), argList.size());
        }
    }
    else {
        // TODO: Invoke Hampton's new symbol table to look for the
        // external operation.
    }      */
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
        // Clean up myUtilities
        myUtilities.finalModuleDec();
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
        // Store the local variable list
        dec.setVariables(myUtilities.getLocalVarList());

        // Clean up myUtilities
        myUtilities.finalOperationDec();
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
        // Store the local variable list
        dec.setVariables(myUtilities.getLocalVarList());

        // Clean up myUtilities
        myUtilities.finalOperationDec();
    }

    // -----------------------------------------------------------
    // WhileStmt
    // -----------------------------------------------------------

    @Override
    public void postWhileStmt(WhileStmt stmt) {
        Location location = stmt.getLocation();

        // If we don't have a changing clause,
        // set all local variables to be changing.
        if (stmt.getChanging() == null) {
            stmt.setChanging(createChangingVarList(location));
        }

        // If we don't have a maintaining clause,
        // then set it to "maintaining true"
        if (stmt.getMaintaining() == null) {
            stmt.setMaintaining(myTypeGraph.getTrueVarExp());
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    public void wrongOperation(Location location, PosSymbol name,
            int paramSize, int argSize) {
        String message =
                "Expecting an operation with the name " + name + " with "
                        + argSize + " arguments.\n"
                        + "Found an operation with same name, but with "
                        + paramSize + " arguments.";
        throw new SourceErrorException(message, location);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Applies the Replica call to any <code>VariableExp</code>
     * where the evaluates mode is specified.</p>
     *
     * @param parameterList The list of <code>ParameterVarDec</code>,
     *                      which contains the modes.
     * @param argList The list of arguments being used to invoke the
     *                current operation/procedure.
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
                    tempExp = createReplicaExp(tempExp);
                }
            }

            // Add it to the modified list
            modifiedArgList.add(tempExp);
        }

        return modifiedArgList;
    }

    /**
     * <p>Creates a list of all the local and parameter variables.</p>
     *
     * @param location The location for the each of the new
     *                 variable expressions.
     *
     * @return A list of <code>VariableExp</code>.
     */
    private List<VariableExp> createChangingVarList(Location location) {
        // Lists
        List<VariableExp> retList = new List<VariableExp>();
        List<VarDec> variableList =
                new List<VarDec>(myUtilities.getParameterVarList());
        variableList.addAllUnique(myUtilities.getLocalVarList());

        // Add variables into the variable list once.
        for (VarDec v : variableList) {
            retList.addUnique(new VariableNameExp(location, null, v.getName()));
        }

        return retList;
    }

    /**
     * <p>Creates a <code>ProgramParamExp</code> for Replica with the
     * <code>ProgramExp</code> passed in.</p>
     *
     * @param oldExp Expression to be replicated.
     *
     * @return A call to Replica with the oldExp as its argument.
     */
    private ProgramParamExp createReplicaExp(ProgramExp oldExp) {
        // Parameter list for Replica
        List<ProgramExp> params = new List<ProgramExp>();
        params.add(oldExp);

        // Create new right hand side with the Replica operation
        return new ProgramParamExp(oldExp.getLocation(), new PosSymbol(oldExp
                .getLocation(), Symbol.symbol("Replica")), params, null);
    }
}
