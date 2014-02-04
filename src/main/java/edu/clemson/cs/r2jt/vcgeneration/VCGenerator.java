/**
 * VCGenerator.java
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
package edu.clemson.cs.r2jt.vcgeneration;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.*;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO: Write a description of this module
 */
public class VCGenerator extends TreeWalkerVisitor {

    // ===========================================================
    // Global Variables
    // ===========================================================

    // Symbol table related items
    private final MathSymbolTableBuilder mySymbolTable;
    private final TypeGraph myTypeGraph;
    private final MTType BOOLEAN;
    private final MTType MTYPE;
    private final MTType Z;
    private ModuleScope myCurrentModuleScope;

    // Current Operation Entry
    private OperationEntry myCurrentOperationEntry;

    // Compile Environment
    private CompileEnvironment myInstanceEnvironment;

    // Assertive Code
    private AssertiveCode myAssertion;

    // This buffer holds the verbose data
    private StringBuffer myVCBuffer;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_ALTSECTION_NAME = "GenerateVCs";
    private static final String FLAG_DESC_ATLVERIFY_VC = "Generate VCs.";
    private static final String FLAG_DESC_ATTLISTVCS_VC = "";

    // ===========================================================
    // Flags
    // ===========================================================

    public static final Flag FLAG_ALTVERIFY_VC =
            new Flag(FLAG_ALTSECTION_NAME, "altVCs", FLAG_DESC_ATLVERIFY_VC);

    public static final Flag FLAG_ALTLISTVCS_VC =
            new Flag(FLAG_ALTSECTION_NAME, "altListVCs",
                    FLAG_DESC_ATTLISTVCS_VC, Flag.Type.HIDDEN);

    public static final void setUpFlags() {
        FlagDependencies.addImplies(FLAG_ALTVERIFY_VC, FLAG_ALTLISTVCS_VC);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    public VCGenerator(ScopeRepository table, final CompileEnvironment env) {
        // Symbol table items
        mySymbolTable = (MathSymbolTableBuilder) table;
        myTypeGraph = mySymbolTable.getTypeGraph();
        BOOLEAN = myTypeGraph.BOOLEAN;
        MTYPE = myTypeGraph.MTYPE;
        Z = myTypeGraph.Z;
        myCurrentModuleScope = null;
        myCurrentOperationEntry = null;

        myInstanceEnvironment = env;
        myAssertion = null;
        myVCBuffer = new StringBuffer(buildHeaderComment());
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // EnhancementBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {

    }

    @Override
    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {

    }

    // -----------------------------------------------------------
    // FacilityOperationDec
    // -----------------------------------------------------------

    @Override
    public void postFacilityOperationDec(FacilityOperationDec dec) {
        // Create assertive code
        myAssertion = new AssertiveCode(myInstanceEnvironment);

        // Obtain the id for the module we are in.
        ModuleIdentifier id = mySymbolTable.getScope(dec).getRootModule();
        try {
            // Verbose Mode Debug Messages
            myVCBuffer.append("\n Procedure Name:\t");
            myVCBuffer.append(dec.getName().getSymbol().toString());
            myVCBuffer.append("\n");

            // Obtain the module dec and use it to obtain the global requires clause
            myCurrentModuleScope = mySymbolTable.getModuleScope(id);
            ModuleDec mDec = myCurrentModuleScope.getDefiningElement();
            Location loc = dec.getLocation();
            Exp gRequires = getRequiresClause(mDec);

            // Keep the current operation dec
            List<PTType> argTypes = new LinkedList<PTType>();
            for (ParameterVarDec p : dec.getParameters()) {
                argTypes.add(p.getTy().getProgramTypeValue());
            }
            myCurrentOperationEntry =
                    searchOperation(loc, null, dec.getName(), argTypes);

            // Obtains items from the current operation
            Exp requires = modifyRequiresClause(getRequiresClause(dec), loc);
            Exp ensures = modifyEnsuresClause(getEnsuresClause(dec), loc);
            List<Statement> statementList = dec.getStatements();
            List<VarDec> variableList = dec.getAllVariables();
            Exp decreasing = dec.getDecreasing();

            // Apply the procedure declaration rule
            applyProcedureDeclRule(gRequires, requires, ensures, decreasing,
                    variableList, statementList);

            // Apply proof rules
            applyEBRules();

            // Verbose Mode Debug Messages
            myVCBuffer.append("\n_____________________ \n\n");

            myCurrentOperationEntry = null;
            myCurrentModuleScope = null;
        }
        catch (NoSuchSymbolException nsse) {
            System.err.println("Module " + id
                    + " does not exist or is not in scope.");
            noSuchModule(dec.getLocation());
        }

        myAssertion = null;
    }

    // -----------------------------------------------------------
    // ProcedureDec
    // -----------------------------------------------------------

    @Override
    public void postProcedureDec(ProcedureDec dec) {}

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    public void noSuchModule(Location location) {
        throw new SourceErrorException(
                "Module does not exist or is not in scope.", location);
    }

    public void noSuchModule(PosSymbol qualifier) {
        throw new SourceErrorException(
                "Module does not exist or is not in scope.", qualifier);
    }

    public void noSuchSymbol(PosSymbol qualifier, PosSymbol symbol) {
        noSuchSymbol(qualifier, symbol.getName(), symbol.getLocation());
    }

    public void noSuchSymbol(PosSymbol qualifier, String symbolName, Location l) {

        String message;

        if (qualifier == null) {
            message = "No such symbol: " + symbolName;
        }
        else {
            message =
                    "No such symbol in module: " + qualifier.getName() + "."
                            + symbolName;
        }

        throw new SourceErrorException(message, l);
    }

    public void tyNotHandled(Ty ty, Location location) {
        String message = "Ty not handled: " + ty.toString();
        throw new SourceErrorException(message, location);
    }

    // -----------------------------------------------------------
    // Debugging/Verbose Mode
    // -----------------------------------------------------------

    public String verboseOutput() {
        return myVCBuffer.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Loop through the list of <code>VarDec</code>, search
     * for their corresponding <code>ProgramVariableEntry</code>
     * and add the result to the list of free variables.</p>
     *
     * @param variableList List of the all variables as
     *                     <code>VarDec</code>.
     */
    public void addVarDecsAsFreeVars(List<VarDec> variableList) {
        // Loop through the variable list
        for (VarDec v : variableList) {
            myAssertion.addFreeVar(createVarExp(v.getLocation(), v.getName(), v
                    .getTy().getMathTypeValue()));
        }
    }

    /**
     * <p>Builds a comment header to identify VC files generated
     * by the compiler and from which RESOLVE source file the generated
     * file is derived.</p>
     */
    private String buildHeaderComment() {
        return "//\n"
                + "// Generated by the RESOLVE VC Generator, February 2014 version"
                + "\n" + "// from file:  "
                + myInstanceEnvironment.getTargetFile().getName() + "\n"
                + "// on:         " + new Date() + "\n" + "//\n";
    }

    /**
     * <p>Returns a newly created <code>PosSymbol</code>
     * with the string provided.</p>
     *
     * @param name String of the new <code>PosSymbol</code>.
     *
     * @return The new <code>PosSymbol</code>.
     */
    private PosSymbol createPosSymbol(String name) {
        // Create the PosSymbol
        PosSymbol posSym = new PosSymbol();
        posSym.setSymbol(Symbol.symbol(name));
        return posSym;
    }

    /**
     * <p>Returns a newly created <code>VarExp</code>
     * with the <code>PosSymbol</code> and math type provided.</p>
     *
     * @param loc Location of the new <code>VarExp</code>
     * @param name <code>PosSymbol</code> of the new <code>VarExp</code>.
     * @param type Math type of the new <code>VarExp</code>.
     *
     * @return The new <code>VarExp</code>.
     */
    private VarExp createVarExp(Location loc, PosSymbol name, MTType type) {
        // Create the VarExp
        VarExp exp = new VarExp(loc, null, name);
        exp.setMathType(type);
        return exp;
    }

    /**
     * <p>Returns the ensures clause for the current <code>Dec</code>.</p>
     *
     * @param dec The corresponding <code>Dec</code>.
     *
     * @return The ensures clause <code>Exp</code>.
     */
    private Exp getEnsuresClause(Dec dec) {
        PosSymbol name = dec.getName();
        Exp ensures = null;
        Exp retExp = null;

        // Check for each kind of ModuleDec possible
        if (dec instanceof FacilityOperationDec) {
            ensures = ((FacilityOperationDec) dec).getEnsures();
        }
        else if (dec instanceof OperationDec) {
            ensures = ((OperationDec) dec).getEnsures();
        }

        // Deep copy and fill in the details of this location
        if (ensures != null) {
            retExp = Exp.copy(ensures);
            if (retExp.getLocation() != null) {
                Location myLoc = retExp.getLocation();
                myLoc.setDetails("Ensures Clause of " + name);
                setLocation(retExp, myLoc);
            }
        }

        return retExp;
    }

    /**
     * <p>Returns the requires clause for the current <code>Dec</code>.</p>
     *
     * @param dec The corresponding <code>Dec</code>.
     *
     * @return The requires clause <code>Exp</code>.
     */
    private Exp getRequiresClause(Dec dec) {
        PosSymbol name = dec.getName();
        Exp requires = null;
        Exp retExp = null;

        // Check for each kind of ModuleDec possible
        if (dec instanceof FacilityOperationDec) {
            requires = ((FacilityOperationDec) dec).getRequires();
        }
        else if (dec instanceof OperationDec) {
            requires = ((OperationDec) dec).getRequires();
        }
        else if (dec instanceof ConceptModuleDec) {
            requires = ((ConceptModuleDec) dec).getRequirement();
        }
        else if (dec instanceof ConceptBodyModuleDec) {
            requires = ((ConceptBodyModuleDec) dec).getRequires();
        }
        else if (dec instanceof EnhancementModuleDec) {
            requires = ((EnhancementModuleDec) dec).getRequirement();
        }
        else if (dec instanceof EnhancementBodyModuleDec) {
            requires = ((EnhancementBodyModuleDec) dec).getRequires();
        }
        else if (dec instanceof FacilityModuleDec) {
            requires = ((FacilityModuleDec) dec).getRequirement();
        }

        // Deep copy and fill in the details of this location
        if (requires != null) {
            retExp = Exp.copy(requires);
            if (retExp.getLocation() != null) {
                Location myLoc = retExp.getLocation();
                myLoc.setDetails("Requires Clause for " + name);
                setLocation(retExp, myLoc);
            }
        }

        return retExp;
    }

    /**
     * <p>Get the <code>PosSymbol</code> associated with the
     * <code>VariableExp</code> left.</p>
     *
     * @param left The variable expression.
     *
     * @return The <code>PosSymbol</code> of left.
     */
    private PosSymbol getVarName(VariableExp left) {
        // Return value
        PosSymbol name;

        // Variable Name Expression
        if (left instanceof VariableNameExp) {
            name = ((VariableNameExp) left).getName();
        }
        // Variable Dot Expression
        else if (left instanceof VariableDotExp) {
            VariableRecordExp varRecExp =
                    (VariableRecordExp) ((VariableDotExp) left)
                            .getSemanticExp();
            name = varRecExp.getName();
        }
        // Variable Record Expression
        else if (left instanceof VariableRecordExp) {
            VariableRecordExp varRecExp = (VariableRecordExp) left;
            name = varRecExp.getName();
        }
        //
        // Creates an expression with "false" as its name
        else {
            name = createPosSymbol("false");
        }

        return name;
    }

    /**
     * <p>Modifies the ensures clause based on the parameter mode.</p>
     *
     * @param ensures The <code>Exp</code> containing the ensures clause.
     * @param opLocation The <code>Location</code> for the operation
     *
     * @return The modified ensures clause <code>Exp</code>.
     */
    private Exp modifyEnsuresByParameter(Exp ensures, Location opLocation) {
        // Obtain the list of parameters
        List<ParameterVarDec> parameterVarDecList;
        if (myCurrentOperationEntry.getDefiningElement() instanceof FacilityOperationDec) {
            parameterVarDecList =
                    ((FacilityOperationDec) myCurrentOperationEntry
                            .getDefiningElement()).getParameters();
        }
        else {
            parameterVarDecList =
                    ((OperationDec) myCurrentOperationEntry
                            .getDefiningElement()).getParameters();
        }

        // Loop through each parameter
        for (ParameterVarDec p : parameterVarDecList) {
            ProgramTypeEntry typeEntry;

            // Ty is NameTy
            if (p.getTy() instanceof NameTy) {
                NameTy pNameTy = (NameTy) p.getTy();
                try {
                    // Query for the type entry in the symbol table
                    typeEntry =
                            myCurrentModuleScope
                                    .queryForOne(
                                            new NameQuery(
                                                    null,
                                                    pNameTy.getName(),
                                                    ImportStrategy.IMPORT_NAMED,
                                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                                    false)).toProgramTypeEntry(
                                            pNameTy.getLocation());

                    // Obtain the original dec from the AST
                    TypeDec type = (TypeDec) typeEntry.getDefiningElement();

                    // Preserves or Restores mode
                    if (p.getMode() == Mode.PRESERVES
                            || p.getMode() == Mode.RESTORES) {
                        // Exp form of the parameter variable
                        VarExp parameterExp =
                                new VarExp(p.getLocation(), null, p.getName()
                                        .copy());
                        parameterExp.setMathType(pNameTy.getMathTypeValue());

                        // Create an old exp (#parameterExp)
                        OldExp oldParameterExp =
                                new OldExp(p.getLocation(), Exp
                                        .copy(parameterExp));
                        oldParameterExp.setMathType(pNameTy.getMathTypeValue());

                        // Create an equals expression of the form "parameterExp = #parameterExp"
                        EqualsExp equalsExp =
                                new EqualsExp(opLocation, parameterExp,
                                        EqualsExp.EQUAL, oldParameterExp);
                        equalsExp.setMathType(BOOLEAN);

                        // Set the details for the new location
                        Location equalLoc;
                        if (ensures != null && ensures.getLocation() != null) {
                            Location enLoc = ensures.getLocation();
                            equalLoc = ((Location) enLoc.clone());
                        }
                        else {
                            equalLoc = ((Location) opLocation.clone());
                        }
                        equalLoc.setDetails("Condition from "
                                + p.getMode().getModeName()
                                + " parameter mode.");
                        equalsExp.setLocation(equalLoc);

                        // Create an AND infix expression with the ensures clause
                        if (ensures != null
                                && !ensures.equals(myTypeGraph.getTrueVarExp())) {
                            ensures =
                                    myTypeGraph
                                            .formConjunct(ensures, equalsExp);
                        }
                        // Make new expression the ensures clause
                        else {
                            ensures = equalsExp;
                        }
                    }
                    // Evaluates mode
                    else if (p.getMode() == Mode.EVALUATES) {
                        // TODO
                    }
                }
                catch (NoSuchSymbolException e) {
                    noSuchSymbol(null, pNameTy.getName().getName(), p
                            .getLocation());
                }
                catch (DuplicateSymbolException dse) {
                    //This should be caught earlier, when the duplicate type is
                    //created
                    throw new RuntimeException(dse);
                }
            }
            else {
                // Ty not handled.
                tyNotHandled(p.getTy(), p.getLocation());
            }
        }

        return ensures;
    }

    /**
     * <p>Returns the ensures clause.</p>
     *
     * @param ensures The <code>Exp</code> containing the ensures clause.
     * @param opLocation The <code>Location</code> for the operation
     *
     * @return The modified ensures clause <code>Exp</code>.
     */
    private Exp modifyEnsuresClause(Exp ensures, Location opLocation) {
        // Modifies the existing ensures clause based on
        // the parameter modes.
        ensures = modifyEnsuresByParameter(ensures, opLocation);

        return ensures;
    }

    /**
     * <p>Modifies the requires clause based on .</p>
     *
     * @param requires The <code>Exp</code> containing the requires clause.
     *
     * @return The modified requires clause <code>Exp</code>.
     */
    private Exp modifyRequiresByGlobalMode(Exp requires) {
        return requires;
    }

    /**
     * <p>Modifies the requires clause based on the parameter mode.</p>
     *
     * @param requires The <code>Exp</code> containing the requires clause.
     * @param opLocation The <code>Location</code> for the operation
     *
     * @return The modified requires clause <code>Exp</code>.
     */
    private Exp modifyRequiresByParameter(Exp requires, Location opLocation) {
        // Obtain the list of parameters
        List<ParameterVarDec> parameterVarDecList;
        if (myCurrentOperationEntry.getDefiningElement() instanceof FacilityOperationDec) {
            parameterVarDecList =
                    ((FacilityOperationDec) myCurrentOperationEntry
                            .getDefiningElement()).getParameters();
        }
        else {
            parameterVarDecList =
                    ((OperationDec) myCurrentOperationEntry
                            .getDefiningElement()).getParameters();
        }

        // Loop through each parameter
        for (ParameterVarDec p : parameterVarDecList) {
            ProgramTypeEntry typeEntry;

            // Ty is NameTy
            if (p.getTy() instanceof NameTy) {
                NameTy pNameTy = (NameTy) p.getTy();
                try {
                    // Query for the type entry in the symbol table
                    typeEntry =
                            myCurrentModuleScope
                                    .queryForOne(
                                            new NameQuery(
                                                    null,
                                                    pNameTy.getName(),
                                                    ImportStrategy.IMPORT_NAMED,
                                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                                    false)).toProgramTypeEntry(
                                            pNameTy.getLocation());

                    // Obtain the original dec from the AST
                    TypeDec type = (TypeDec) typeEntry.getDefiningElement();

                    // Deep copy the original initialization ensures and the constraint
                    Exp init = Exp.copy(type.getInitialization().getEnsures());
                    Exp constraint = Exp.copy(type.getConstraint());

                    // Only worry about replaces mode parameters
                    if (p.getMode() == Mode.REPLACES && init != null) {
                        // Set the details for the new location
                        if (init.getLocation() != null) {
                            Location initLoc;
                            if (requires != null
                                    && requires.getLocation() != null) {
                                Location reqLoc = requires.getLocation();
                                initLoc = ((Location) reqLoc.clone());
                            }
                            else {
                                initLoc = ((Location) opLocation.clone());
                            }
                            initLoc.setDetails("Assumption from "
                                    + p.getMode().getModeName()
                                    + " parameter mode.");
                            init.setLocation(initLoc);
                        }

                        // Create an AND infix expression with the requires clause
                        if (requires != null
                                && !requires
                                        .equals(myTypeGraph.getTrueVarExp())) {
                            requires = myTypeGraph.formConjunct(requires, init);
                        }
                        // Make initialization expression the requires clause
                        else {
                            requires = init;
                        }
                    }
                    // Constraints for the other parameter modes needs to be added
                    // to the requires clause as conjuncts.
                    else {
                        if (constraint != null
                                && !constraint.equals(myTypeGraph
                                        .getTrueVarExp())) {
                            // Set the details for the new location
                            if (constraint.getLocation() != null) {
                                Location constLoc;
                                if (requires != null
                                        && requires.getLocation() != null) {
                                    Location reqLoc = requires.getLocation();
                                    constLoc = ((Location) reqLoc.clone());
                                }
                                else {
                                    constLoc = ((Location) opLocation.clone());
                                }
                                constLoc.setDetails("Constraint from "
                                        + p.getMode().getModeName()
                                        + " parameter mode.");
                                constraint.setLocation(constLoc);
                            }

                            // Create an AND infix expression with the requires clause
                            if (requires != null
                                    && !requires.equals(myTypeGraph
                                            .getTrueVarExp())) {
                                requires =
                                        myTypeGraph.formConjunct(requires,
                                                constraint);
                            }
                            // Make constraint expression the requires clause
                            else {
                                requires = constraint;
                            }
                        }
                    }

                    // Add the current variable to our list of free variables
                    myAssertion.addFreeVar(createVarExp(p.getLocation(), p
                            .getName(), pNameTy.getMathTypeValue()));
                }
                catch (NoSuchSymbolException e) {
                    noSuchSymbol(null, pNameTy.getName().getName(), p
                            .getLocation());
                }
                catch (DuplicateSymbolException dse) {
                    //This should be caught earlier, when the duplicate type is
                    //created
                    throw new RuntimeException(dse);
                }
            }
            else {
                // Ty not handled.
                tyNotHandled(p.getTy(), p.getLocation());
            }
        }

        return requires;
    }

    /**
     * <p>Modifies the requires clause.</p>
     *
     * @param requires The <code>Exp</code> containing the requires clause.
     * @param opLocation The <code>Location</code> for the operation.
     *
     * @return The modified requires clause <code>Exp</code>.
     */
    private Exp modifyRequiresClause(Exp requires, Location opLocation) {
        // Modifies the existing requires clause based on
        // the parameter modes.
        requires = modifyRequiresByParameter(requires, opLocation);

        // Modifies the existing requires clause based on
        // the parameter modes.
        // TODO: Ask Murali what this means
        requires = modifyRequiresByGlobalMode(requires);

        return requires;
    }

    /**
     * <p>Copy and replace the old <code>Exp</code>.</p>
     *
     * @param exp The <code>Exp</code> to be replaced.
     * @param old The old sub-expression of <code>exp</code>.
     * @param repl The new sub-expression of <code>exp</code>.
     *
     * @return The new <code>Exp</code>.
     */
    private Exp replace(Exp exp, Exp old, Exp repl) {
        // Clone old and repl and use the Exp replace to do all its work
        Exp tmp = Exp.replace(exp, Exp.copy(old), Exp.copy(repl));

        // Return the corresponding Exp
        if (tmp != null)
            return tmp;
        else
            return exp;
    }

    /**
     * <p>Given the qualifier, name and the list of argument
     * types, locate and return the <code>OperationEntry</code>
     * stored in the symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The qualifier of the operation.
     * @param name The name of the operation.
     * @param argTypes The list of argument types.
     *
     * @return An <code>OperationEntry</code> from the
     *         symbol table.
     */
    private OperationEntry searchOperation(Location loc, PosSymbol qualifier,
            PosSymbol name, List<PTType> argTypes) {
        // Query for the corresponding operation
        OperationEntry op = null;
        try {
            op =
                    myCurrentModuleScope.queryForOne(new OperationQuery(
                            qualifier, name, argTypes));
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, name.getName(), loc);
        }
        catch (DuplicateSymbolException dse) {
            //This should be caught earlier, when the duplicate operation is
            //created
            throw new RuntimeException(dse);
        }

        return op;
    }

    /**
     * <p>Changes the <code>Exp</code> with the new
     * <code>Location</code>.</p>
     *
     * @param exp The <code>Exp</code> that needs to be modified.
     * @param loc The new <code>Location</code>.
     */
    private void setLocation(Exp exp, Location loc) {
        // Special handling for InfixExp
        if (exp instanceof InfixExp) {
            ((InfixExp) exp).setAllLocations(loc);
        }
        else {
            exp.setLocation(loc);
        }
    }

    // -----------------------------------------------------------
    // Proof Rules
    // -----------------------------------------------------------

    /**
     * <p>Applies the assume rule.</p>
     *
     * @param assume The assume clause
     */
    private void applyAssumeRule(VerificationStatement assume) {
        if (assume.getAssertion() instanceof VarExp
                && ((VarExp) assume.getAssertion()).equals(Exp
                        .getTrueVarExp(myTypeGraph))) {
            // Verbose Mode Debug Messages
            myVCBuffer.append("\n_____________________ \n");
            myVCBuffer.append("\nAssume Rule Applied and Simplified: \n");
            myVCBuffer.append(myAssertion.assertionToString());
        }
        else {
            // Obtain the current final confirm clause
            Exp conf = myAssertion.getFinalConfirm();

            // Create a new implies expression
            InfixExp newConf =
                    myTypeGraph.formImplies((Exp) assume.getAssertion(), conf);

            // Set this new expression as the new final confirm
            myAssertion.setFinalConfirm(newConf);

            // Verbose Mode Debug Messages
            myVCBuffer.append("\n_____________________ \n");
            myVCBuffer.append("\nAssume Rule Applied: \n");
            myVCBuffer.append(myAssertion.assertionToString());
        }
    }

    /**
     * <p>Applies different rules to code statements.</p>
     *
     * @param statement The different statements.
     */
    private void applyCodeRules(Statement statement) {
        // Apply each statement rule here.
        if (statement instanceof AssumeStmt) {
            applyEBAssumeStmtRule((AssumeStmt) statement);
        }
        else if (statement instanceof CallStmt) {
            applyEBCallStmtRule((CallStmt) statement);
        }
        else if (statement instanceof ConfirmStmt) {
            applyEBConfirmStmtRule((ConfirmStmt) statement);
        }
        else if (statement instanceof FuncAssignStmt) {
            applyEBFuncAssignStmtRule((FuncAssignStmt) statement);
        }
        else if (statement instanceof SwapStmt) {
            applyEBSwapStmtRule((SwapStmt) statement);
        }
    }

    /**
     * <p>Applies the confirm rule.</p>
     *
     * @param confirm The confirm clause
     */
    private void applyConfirmRule(VerificationStatement confirm) {
        if (confirm.getAssertion() instanceof VarExp
                && ((VarExp) confirm.getAssertion()).equals(Exp
                        .getTrueVarExp(myTypeGraph))) {
            myVCBuffer.append("\n_____________________ \n");
            myVCBuffer.append("\nConfirm Rule Applied and Simplified: \n");
            myVCBuffer.append(myAssertion.assertionToString());
        }
        else {
            // Obtain the current final confirm clause
            Exp conf = myAssertion.getFinalConfirm();

            // Create a new and expression
            InfixExp newConf =
                    myTypeGraph
                            .formConjunct((Exp) confirm.getAssertion(), conf);

            // Set this new expression as the new final confirm
            myAssertion.setFinalConfirm(newConf);

            // Verbose Mode Debug Messages
            myVCBuffer.append("\n_____________________ \n");
            myVCBuffer.append("\nConfirm Rule Applied: \n");
            myVCBuffer.append(myAssertion.assertionToString());
        }
    }

    /**
     * <p>Applies the assume rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>AssumeStmt</code>.
     */
    private void applyEBAssumeStmtRule(AssumeStmt stmt) {
        // Check to see if our assertion just has "True"
        Exp assertion = stmt.getAssertion();
        if (assertion instanceof VarExp
                && assertion.equals(myTypeGraph.getTrueVarExp())) {
            // Verbose Mode Debug Messages
            myVCBuffer.append("\n_____________________ \n");
            myVCBuffer.append("\nAssume Rule Applied and Simplified: \n");
            myVCBuffer.append(myAssertion.assertionToString());

            return;
        }

        // Obtain the current final confirm statement
        Exp currentFinalConfirm = myAssertion.getFinalConfirm();

        // TODO: Some replacement needs to happen here
        //currentFinalConfirm = replaceAssumeRule(stmt, currentFinalConfirm, assertion);

        if (assertion != null) {
            // Create a new implies expression
            InfixExp newConf =
                    myTypeGraph.formImplies(assertion, currentFinalConfirm);

            // Set this new expression as the new final confirm
            myAssertion.setFinalConfirm(newConf);

            // Verbose Mode Debug Messages
            myVCBuffer.append("\n_____________________ \n");
            myVCBuffer.append("\nAssume Rule Applied: \n");
            myVCBuffer.append(myAssertion.assertionToString());
        }
        else {
            myAssertion.setFinalConfirm(currentFinalConfirm);
        }
    }

    /**
     * <p>Applies the call statement rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>CallStmt</code>.
     */
    private void applyEBCallStmtRule(CallStmt stmt) {
        Exp ensures = null;
        Exp requires = null;

        // Obtain the corresponding OperationEntry and OperationDec
        List<PTType> argTypes = new LinkedList<PTType>();
        for (ProgramExp arg : stmt.getArguments()) {
            argTypes.add(arg.getProgramType());
        }
        OperationEntry opEntry =
                searchOperation(stmt.getLocation(), stmt.getQualifier(), stmt
                        .getName(), argTypes);

        // TODO: Beware, it might also be a FacilityOperationDec
        OperationDec opDec = (OperationDec) opEntry.getDefiningElement();

        // Get the ensures clause for this operation
        // Note: If there isn't an ensures clause, it is set to "True"
        if (opDec.getEnsures() != null) {
            ensures = Exp.copy(opDec.getEnsures());
        }
        else {
            ensures = myTypeGraph.getTrueVarExp();
        }

        // Get the requires clause for this operation
        if (opDec.getRequires() != null) {
            requires = Exp.copy(opDec.getRequires());
        }

        // TODO: Quantified Ensures clauses
        //ensures =
        //        modifyEnsuresIfCallingQuantified(ensures, opDec, assertion,
        //                ensures);

        if (ensures.getLocation() != null) {
            ensures.getLocation().setDetails(
                    "Ensures Clause For " + opDec.getName());
        }

        // TODO:  Check for  recursive call of itself
        // TODO:  Modify ensures using the parameter modes
        //ensures = modifyEnsuresForParameterModes(ensures, opDec, stmt);

        // TODO :Replace PreCondition Variables
        //requires =
        //replacePreConditionVariables(requires, stmt.getArguments(),
        // opDec, assertion);

        // TODO: Replace PostCondition Variables
        //ensures =
        //replacePostConditionVariables(stmt.getArguments(), ensures,
        //opDec, assertion);

        // Modify the location of the requires clause and add it to myAssertion
        if (requires != null) {
            // Obtain the current location
            // Note: If we don't have a location, we create one
            Location loc;
            if (stmt.getName().getLocation() != null) {
                loc = (Location) stmt.getName().getLocation().clone();
            }
            else {
                loc = new Location(null, null);
            }

            // Append the name of the current procedure
            String details = "";
            if (myCurrentOperationEntry != null) {
                details = " in Procedure " + myCurrentOperationEntry.getName();
            }

            // Set the details of the current location
            loc.setDetails("Requires Clause of " + opDec.getName() + details);
            setLocation(requires, loc);

            // Add this to our list of things to confirm
            myAssertion.addConfirm(requires);
        }

        // Modify the location of the requires clause and add it to myAssertion
        if (ensures != null) {
            // Obtain the current location
            if (stmt.getName().getLocation() != null) {
                // Set the details of the current location
                Location loc = (Location) stmt.getName().getLocation().clone();
                loc.setDetails("Ensures Clause of " + opDec.getName());
                setLocation(ensures, loc);
            }

            // Add this to our list of things to assume
            myAssertion.addAssume(ensures);
        }
    }

    /**
     * <p>Applies the confirm rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>ConfirmStmt</code>.
     */
    private void applyEBConfirmStmtRule(ConfirmStmt stmt) {
        // Check to see if our assertion just has "True"
        Exp assertion = stmt.getAssertion();
        if (assertion instanceof VarExp
                && assertion.equals(myTypeGraph.getTrueVarExp())) {
            // Verbose Mode Debug Messages
            myVCBuffer.append("\n_____________________ \n");
            myVCBuffer.append("\nConfirm Rule Applied and Simplified: \n");
            myVCBuffer.append(myAssertion.assertionToString());

            return;
        }

        // Obtain the current final confirm statement
        Exp currentFinalConfirm = myAssertion.getFinalConfirm();

        // Check to see if we have a final confirm of "True"
        if (currentFinalConfirm instanceof VarExp
                && currentFinalConfirm.equals(myTypeGraph.getTrueVarExp())) {
            myAssertion.setFinalConfirm(assertion);

            // Verbose Mode Debug Messages
            myVCBuffer.append("\n_____________________ \n");
            myVCBuffer.append("\nConfirm Rule Applied and Simplified: \n");
            myVCBuffer.append(myAssertion.assertionToString());
        }
        else {
            // Create a new and expression
            InfixExp newConf =
                    myTypeGraph.formConjunct(assertion, currentFinalConfirm);

            // Set this new expression as the new final confirm
            myAssertion.setFinalConfirm(newConf);

            // Verbose Mode Debug Messages
            myVCBuffer.append("\n_____________________ \n");
            myVCBuffer.append("\nConfirm Rule Applied: \n");
            myVCBuffer.append(myAssertion.assertionToString());
        }
    }

    /**
     * <p>Applies each of the proof rules. This <code>AssertiveCode</code> will be
     * stored for later use and therefore should be considered immutable after
     * a call to this method.</p>
     */
    private void applyEBRules() {
        // Apply a proof rule to each of the assertions
        while (myAssertion.hasAnotherAssertion()) {
            // Work our way from the last assertion
            VerificationStatement curAssertion = myAssertion.getLastAssertion();

            // Assume Assertion
            if (curAssertion.getType() == VerificationStatement.ASSUME) {
                applyAssumeRule(curAssertion);
            }
            // Confirm Assertion
            else if (curAssertion.getType() == VerificationStatement.CONFIRM) {
                applyConfirmRule(curAssertion);
            }
            // Code
            else if (curAssertion.getType() == VerificationStatement.CODE) {
                applyCodeRules((Statement) curAssertion.getAssertion());
                if ((Statement) curAssertion.getAssertion() instanceof WhileStmt
                        || (Statement) curAssertion.getAssertion() instanceof IfStmt) {
                    return;
                }
            }
            // Remember Assertion
            else if (curAssertion.getType() == VerificationStatement.REMEMBER) {
                applyRememberRule();
            }
        }
    }

    /**
     * <p>Applies the function assignment rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>FuncAssignStmt</code>.
     */
    private void applyEBFuncAssignStmtRule(FuncAssignStmt stmt) {
    /*ProgramExp assignExp = stmt.getAssign();

    // Check to see what kind of expression is on the right hand side
    if (assignExp instanceof ProgramParamExp) {
        // Cast to a ProgramParamExp
        ProgramParamExp assignParamExp = (ProgramParamExp) assignExp;

        // Items needed to use the query
        List<ProgramExp> args = assignParamExp.getArguments();
        List<PTType> argTypes = new LinkedList<PTType>();
        for (ProgramExp arg : args) {
            argTypes.add(arg.getProgramType());
        }

        // Query for the corresponding operation
        try {
            OperationEntry op =
                    myCurrentModuleScope.queryForOne(
                            new OperationQuery(null, assignParamExp.getName(), argTypes));
            System.out.println(op.getName());
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, assignParamExp.getName().getName(), assignParamExp.getLocation());
        }
        catch (DuplicateSymbolException dse) {
            //This should be caught earlier, when the duplicate operation is
            //created
            throw new RuntimeException(dse);
        }
    }            */
    }

    /**
     * <p>Applies the swap statement rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>SwapStmt</code>.
     */
    private void applyEBSwapStmtRule(SwapStmt stmt) {
        // Obtain the current final confirm clause
        Exp conf = myAssertion.getFinalConfirm();

        // Create a copy of the left and right hand side
        VariableExp left = (VariableExp) Exp.copy(stmt.getLeft());
        VariableExp right = (VariableExp) Exp.copy(stmt.getRight());

        // Append a _ to the front of the left variable
        String lftStr = getVarName(left).toString();
        String lftTmp = "_";
        lftTmp = lftTmp.concat(lftStr);

        // New left and right
        Exp leftV;
        Exp rightV;

        // VariableDotExp
        if (left instanceof VariableDotExp) {
            // Make a copy of left into leftV
            leftV = new DotExp();
            ((DotExp) leftV).setSemanticExp(((VariableDotExp) left)
                    .getSemanticExp());
            edu.clemson.cs.r2jt.collections.List<Exp> myList =
                    new edu.clemson.cs.r2jt.collections.List<Exp>();
            for (int i = 0; i < ((VariableDotExp) left).getSegments().size(); i++) {
                VariableExp varExp =
                        ((VariableDotExp) left).getSegments().get(i);
                varExp.setType(left.getType());
                varExp.setMathType(left.getMathType());
                varExp.setMathTypeValue(left.getMathTypeValue());
                myList.add(i, varExp);
            }
            ((DotExp) leftV).setSegments(myList);
        }
        // VariableNameExp
        else {
            leftV = new VarExp();
            ((VarExp) leftV).setName(getVarName(left));
        }

        // Copy the math type information into leftV
        leftV.setType(left.getType());
        leftV.setMathType(left.getMathType());
        leftV.setMathTypeValue(left.getMathTypeValue());

        // VariableDotExp
        if (right instanceof VariableDotExp) {
            // Make a copy of right into rightV
            rightV = new DotExp();
            ((DotExp) rightV).setSemanticExp(((VariableDotExp) right)
                    .getSemanticExp());
            edu.clemson.cs.r2jt.collections.List<Exp> myList =
                    new edu.clemson.cs.r2jt.collections.List<Exp>();
            for (int i = 0; i < ((VariableDotExp) right).getSegments().size(); i++) {
                VariableExp varExp =
                        ((VariableDotExp) right).getSegments().get(i);
                varExp.setType(right.getType());
                varExp.setMathType(right.getMathType());
                varExp.setMathTypeValue(right.getMathTypeValue());
                myList.add(i, varExp);
            }
            ((DotExp) rightV).setSegments(myList);
        }
        // VariableNameExp
        else {
            rightV = new VarExp();
            ((VarExp) rightV).setName(getVarName(right));
            rightV.setMathType(right.getMathType());
            rightV.setMathTypeValue(right.getMathTypeValue());
        }

        // Need to Set Exp for rightV and leftV
        List lst = conf.getSubExpressions();
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i) instanceof VarExp) {
                VarExp thisExp = (VarExp) lst.get(i);
                if (rightV instanceof VarExp) {
                    if (thisExp.getName().toString().equals(
                            ((VarExp) rightV).getName().toString())) {
                        rightV.setType(thisExp.getType());
                        rightV.setMathType(thisExp.getMathType());
                        rightV.setMathTypeValue(thisExp.getMathTypeValue());
                    }
                }
                if (leftV instanceof VarExp) {
                    if (thisExp.getName().toString().equals(
                            ((VarExp) leftV).getName().toString())) {
                        leftV.setType(thisExp.getType());
                        leftV.setMathType(thisExp.getMathType());
                        leftV.setMathTypeValue(thisExp.getMathTypeValue());
                    }
                }
            }
        }

        // Temp variable
        VarExp tmp = new VarExp();
        tmp.setName(createPosSymbol(lftTmp));
        tmp.setType(left.getType());
        tmp.setMathType(left.getMathType());
        tmp.setMathTypeValue(left.getMathTypeValue());

        // Replace according to the swap rule
        conf = replace(conf, rightV, tmp);
        conf = replace(conf, leftV, rightV);
        conf = replace(conf, tmp, leftV);

        // Set this new expression as the new final confirm
        myAssertion.setFinalConfirm(conf);

        // Verbose Mode Debug Messages
        myVCBuffer.append("\n_____________________ \n");
        myVCBuffer.append("\nSwap Rule Applied: \n");
        myVCBuffer.append(myAssertion.assertionToString());
    }

    /**
     * <p>Applies the procedure declaration rule.</p>
     *
     * @param gRequires Global requires clause
     * @param requires Requires clause
     * @param ensures Ensures clause
     * @param decreasing Decreasing clause (if any)
     * @param variableList List of all variables for this procedure
     * @param statementList List of statements for this procedure
     */
    private void applyProcedureDeclRule(Exp gRequires, Exp requires,
            Exp ensures, Exp decreasing, List<VarDec> variableList,
            List<Statement> statementList) {
        // Add the global requires clause
        if (gRequires != null) {
            myAssertion.addAssume(gRequires);
        }

        // Add the requires clause
        if (requires != null) {
            myAssertion.addAssume(requires);
        }

        // Add the remember rule
        myAssertion.addRemember();

        // Add declared variables into the assertion. Also add
        // them to the list of free variables.
        myAssertion.addVariableDecs(variableList);
        addVarDecsAsFreeVars(variableList);

        // Check to see if we have a recursive procedure.
        // If yes, we will need to create an additional assume clause
        // (P_val = (decreasing clause)) in our list of assertions.
        if (decreasing != null) {
            // Add P_val as a free variable
            VarExp pVal =
                    createVarExp(decreasing.getLocation(),
                            createPosSymbol("P_val"), Z);
            myAssertion.addFreeVar(pVal);

            // Create an equals expression
            EqualsExp equalsExp =
                    new EqualsExp(null, pVal, EqualsExp.EQUAL, Exp
                            .copy(decreasing));
            equalsExp.setMathType(BOOLEAN);
            Location eqLoc = (Location) decreasing.getLocation().clone();
            eqLoc.setDetails("Progress Metric for Recursive Procedure");
            setLocation(equalsExp, eqLoc);

            // Add it to our things to assume
            myAssertion.addAssume(equalsExp);
        }

        // Add the list of statements
        myAssertion.addStatements(statementList);

        // Add the final confirms clause
        myAssertion.setFinalConfirm(ensures);

        // Verbose Mode Debug Messages
        myVCBuffer.append("\n_____________________ \n");
        myVCBuffer.append("\nProcedure Declaration Rule Applied: \n");
        myVCBuffer.append(myAssertion.assertionToString());
    }

    /**
     * <p>Applies the Proof rule for Remember.</p>
     */
    private void applyRememberRule() {
        // Obtain the final confirm and apply the remember method for Exp
        Exp conf = myAssertion.getFinalConfirm();
        conf = conf.remember();
        myAssertion.setFinalConfirm(conf);

        // Verbose Mode Debug Messages
        myVCBuffer.append("\n_____________________ \n");
        myVCBuffer.append("\nRemember Rule Applied: \n");
        myVCBuffer.append(myAssertion.assertionToString());
    }
}