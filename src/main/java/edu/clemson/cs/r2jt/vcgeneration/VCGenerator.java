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
import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.*;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.io.File;
import java.util.*;
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

    // Module level global variables
    private Exp myGlobalRequiresExp;
    private Exp myGlobalConstraintExp;

    // Operation/Procedure level global variables
    private OperationEntry myCurrentOperationEntry;
    private Exp myOperationDecreasingExp;

    /**
     * <p>The current assertion we are applying
     * VC rules to.</p>
     */
    private AssertiveCode myCurrentAssertiveCode;

    /**
     * <p>The current compile environment used throughout
     * the compiler.</p>
     */
    private CompileEnvironment myInstanceEnvironment;

    /**
     * <p>A list that will be built up with <code>AssertiveCode</code>
     * objects, each representing a VC or group of VCs that must be
     * satisfied to verify a parsed program.</p>
     */
    private Collection<AssertiveCode> myFinalAssertiveCodeList;

    /**
     * <p>This object creates the different VC outputs.</p>
     */
    private OutputVCs myOutputGenerator;

    /**
     * <p>A stack that is used to keep track of the <code>AssertiveCode</code>
     * that we still need to apply proof rules to.</p>
     */
    private Stack<AssertiveCode> myIncAssertiveCodeStack;

    /**
     * <p>A stack that is used to keep track of the information that we
     * haven't printed for the <code>AssertiveCode</code>
     * that we still need to apply proof rules to.</p>
     */
    private Stack<String> myIncAssertiveCodeStackInfo;

    /**
     * <p>This string buffer holds all the steps
     * the VC generator takes to generate VCs.</p>
     */
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

        // Current items
        myCurrentModuleScope = null;
        myCurrentOperationEntry = null;
        myGlobalConstraintExp = null;
        myGlobalRequiresExp = null;
        myOperationDecreasingExp = null;

        // Instance Environment
        myInstanceEnvironment = env;

        // VCs + Debugging String
        myCurrentAssertiveCode = null;
        myFinalAssertiveCodeList = new LinkedList<AssertiveCode>();
        myOutputGenerator = null;
        myIncAssertiveCodeStack = new Stack<AssertiveCode>();
        myIncAssertiveCodeStackInfo = new Stack<String>();
        myVCBuffer = new StringBuffer();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // ConceptBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        // Verbose Mode Debug Messages
        myVCBuffer.append("\n=========================");
        myVCBuffer.append(" VC Generation Details ");
        myVCBuffer.append(" =========================\n");
        myVCBuffer.append("\n Concept Realization Name:\t");
        myVCBuffer.append(dec.getName().getName());
        myVCBuffer.append("\n Concept Name:\t");
        myVCBuffer.append(dec.getConceptName().getName());
        myVCBuffer.append("\n");
        myVCBuffer.append("\n====================================");
        myVCBuffer.append("======================================\n");
        myVCBuffer.append("\n");

        // Set the current module scope
        try {
            myCurrentModuleScope =
                    mySymbolTable.getModuleScope(new ModuleIdentifier(dec));

            // From the list of imports, obtain the global constraints
            // of the imported modules.
            myGlobalConstraintExp =
                    getConstraints(dec.getLocation(), myCurrentModuleScope
                            .getImports());

            // Store the global requires clause
            myGlobalRequiresExp = getRequiresClause(dec);
        }
        catch (NoSuchSymbolException e) {
            System.err.println("Module " + dec.getName()
                    + " does not exist or is not in scope.");
            noSuchModule(dec.getLocation());
        }
    }

    @Override
    public void postConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        // Set the module level global variables to null
        myCurrentModuleScope = null;
        myGlobalConstraintExp = null;
        myGlobalRequiresExp = null;
    }

    // -----------------------------------------------------------
    // EnhancementBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        // Verbose Mode Debug Messages
        myVCBuffer.append("\n=========================");
        myVCBuffer.append(" VC Generation Details ");
        myVCBuffer.append(" =========================\n");
        myVCBuffer.append("\n Enhancement Realization Name:\t");
        myVCBuffer.append(dec.getName().getName());
        myVCBuffer.append("\n Enhancement Name:\t");
        myVCBuffer.append(dec.getEnhancementName().getName());
        myVCBuffer.append("\n Concept Name:\t");
        myVCBuffer.append(dec.getConceptName().getName());
        myVCBuffer.append("\n");
        myVCBuffer.append("\n====================================");
        myVCBuffer.append("======================================\n");
        myVCBuffer.append("\n");

        // Set the current module scope
        try {
            myCurrentModuleScope =
                    mySymbolTable.getModuleScope(new ModuleIdentifier(dec));

            // From the list of imports, obtain the global constraints
            // of the imported modules.
            myGlobalConstraintExp =
                    getConstraints(dec.getLocation(), myCurrentModuleScope
                            .getImports());

            // Store the global requires clause
            myGlobalRequiresExp = getRequiresClause(dec);
        }
        catch (NoSuchSymbolException e) {
            System.err.println("Module " + dec.getName()
                    + " does not exist or is not in scope.");
            noSuchModule(dec.getLocation());
        }
    }

    @Override
    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        // Set the module level global variables to null
        myCurrentModuleScope = null;
        myGlobalConstraintExp = null;
        myGlobalRequiresExp = null;
    }

    // -----------------------------------------------------------
    // FacilityDec
    // -----------------------------------------------------------

    @Override
    public void postFacilityDec(FacilityDec dec) {
        // Applies the facility declaration rule
        applyFacilityDeclRule(dec);

        // Loop through assertive code stack
        loopAssertiveCodeStack();
    }

    // -----------------------------------------------------------
    // FacilityModuleDec
    // -----------------------------------------------------------

    @Override
    public void preFacilityModuleDec(FacilityModuleDec dec) {
        // Verbose Mode Debug Messages
        myVCBuffer.append("\n=========================");
        myVCBuffer.append(" VC Generation Details ");
        myVCBuffer.append(" =========================\n");
        myVCBuffer.append("\n Facility Name:\t");
        myVCBuffer.append(dec.getName().getName());
        myVCBuffer.append("\n");
        myVCBuffer.append("\n====================================");
        myVCBuffer.append("======================================\n");
        myVCBuffer.append("\n");

        // Set the current module scope
        try {
            myCurrentModuleScope =
                    mySymbolTable.getModuleScope(new ModuleIdentifier(dec));

            // From the list of imports, obtain the global constraints
            // of the imported modules.
            myGlobalConstraintExp =
                    getConstraints(dec.getLocation(), myCurrentModuleScope
                            .getImports());

            // Store the global requires clause
            myGlobalRequiresExp = getRequiresClause(dec);
        }
        catch (NoSuchSymbolException e) {
            System.err.println("Module " + dec.getName()
                    + " does not exist or is not in scope.");
            noSuchModule(dec.getLocation());
        }
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec dec) {
        // Set the module level global variables to null
        myCurrentModuleScope = null;
        myGlobalConstraintExp = null;
        myGlobalRequiresExp = null;
    }

    // -----------------------------------------------------------
    // FacilityOperationDec
    // -----------------------------------------------------------

    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        // Keep the current operation dec
        List<PTType> argTypes = new LinkedList<PTType>();
        for (ParameterVarDec p : dec.getParameters()) {
            argTypes.add(p.getTy().getProgramTypeValue());
        }
        myCurrentOperationEntry =
                searchOperation(dec.getLocation(), null, dec.getName(),
                        argTypes);
    }

    @Override
    public void postFacilityOperationDec(FacilityOperationDec dec) {
        // Verbose Mode Debug Messages
        myVCBuffer.append("\n=========================");
        myVCBuffer.append(" Procedure: ");
        myVCBuffer.append(dec.getName().getName());
        myVCBuffer.append(" =========================\n");

        // The current assertive code
        myCurrentAssertiveCode = new AssertiveCode(myInstanceEnvironment);

        // Obtains items from the current operation
        Location loc = dec.getLocation();
        String name = dec.getName().getName();
        Exp requires = modifyRequiresClause(getRequiresClause(dec), loc, name);
        Exp ensures = modifyEnsuresClause(getEnsuresClause(dec), loc, name);
        List<Statement> statementList = dec.getStatements();
        List<VarDec> variableList = dec.getAllVariables();
        Exp decreasing = dec.getDecreasing();

        // Apply the procedure declaration rule
        applyProcedureDeclRule(requires, ensures, decreasing, variableList,
                statementList);

        // Add this to our stack of to be processed assertive codes.
        myIncAssertiveCodeStack.push(myCurrentAssertiveCode);
        myIncAssertiveCodeStackInfo.push("");

        // Set the current assertive code to null
        // YS: (We the modify requires and ensures clause needs to have
        // and current assertive code to work. Not very clean way to
        // solve the problem, but should work.)
        myCurrentAssertiveCode = null;

        // Loop through assertive code stack
        loopAssertiveCodeStack();

        myOperationDecreasingExp = null;
        myCurrentOperationEntry = null;
    }

    // -----------------------------------------------------------
    // ModuleDec
    // -----------------------------------------------------------

    @Override
    public void postModuleDec(ModuleDec dec) {
        // Create the output generator and finalize output
        myOutputGenerator =
                new OutputVCs(myInstanceEnvironment, myFinalAssertiveCodeList,
                        myVCBuffer);

        // Check if it is generating VCs for WebIDE or not.
        if (myInstanceEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_XML_OUT)) {
            myOutputGenerator.outputToJSON();
        }
        else {
            // Print to file if we are in debug mode
            // TODO: Add debug flag here
            String filename;
            if (myInstanceEnvironment.getOutputFilename() != null) {
                filename = myInstanceEnvironment.getOutputFilename();
            }
            else {
                filename = createVCFileName();
            }
            myOutputGenerator.outputToFile(filename);
        }
    }

    // -----------------------------------------------------------
    // ProcedureDec
    // -----------------------------------------------------------

    @Override
    public void preProcedureDec(ProcedureDec dec) {
        // Keep the current operation dec
        List<PTType> argTypes = new LinkedList<PTType>();
        for (ParameterVarDec p : dec.getParameters()) {
            argTypes.add(p.getTy().getProgramTypeValue());
        }
        myCurrentOperationEntry =
                searchOperation(dec.getLocation(), null, dec.getName(),
                        argTypes);
    }

    @Override
    public void postProcedureDec(ProcedureDec dec) {
        // Verbose Mode Debug Messages
        myVCBuffer.append("\n=========================");
        myVCBuffer.append(" Procedure: ");
        myVCBuffer.append(dec.getName().getName());
        myVCBuffer.append(" =========================\n");

        // The current assertive code
        myCurrentAssertiveCode = new AssertiveCode(myInstanceEnvironment);

        // Obtains items from the current operation
        OperationDec opDec =
                (OperationDec) myCurrentOperationEntry.getDefiningElement();
        Location loc = dec.getLocation();
        String name = dec.getName().getName();
        Exp requires =
                modifyRequiresClause(getRequiresClause(opDec), loc, name);
        Exp ensures = modifyEnsuresClause(getEnsuresClause(opDec), loc, name);
        List<Statement> statementList = dec.getStatements();
        List<VarDec> variableList = dec.getAllVariables();
        Exp decreasing = dec.getDecreasing();

        // Apply the procedure declaration rule
        applyProcedureDeclRule(requires, ensures, decreasing, variableList,
                statementList);

        // Add this to our stack of to be processed assertive codes.
        myIncAssertiveCodeStack.push(myCurrentAssertiveCode);
        myIncAssertiveCodeStackInfo.push("");

        // Set the current assertive code to null
        // YS: (We the modify requires and ensures clause needs to have
        // and current assertive code to work. Not very clean way to
        // solve the problem, but should work.)
        myCurrentAssertiveCode = null;

        // Loop through assertive code stack
        loopAssertiveCodeStack();

        myOperationDecreasingExp = null;
        myCurrentOperationEntry = null;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    public void expNotHandled(Exp exp, Location l) {
        String message = "Exp not handled: " + exp.toString();
        throw new SourceErrorException(message, l);
    }

    public void illegalOperationEnsures(Location l) {
        // TODO: Move this to sanity check.
        String message =
                "Ensures clauses of operations that return a value should be of the form <OperationName> = <value>";
        throw new SourceErrorException(message, l);
    }

    public void notAType(SymbolTableEntry entry, Location l) {
        throw new SourceErrorException(entry.getSourceModuleIdentifier()
                .fullyQualifiedRepresentation(entry.getName())
                + " is not known to be a type.", l);
    }

    public void notInFreeVarList(PosSymbol name, Location l) {
        String message =
                "State variable " + name + " not in free variable list";
        throw new SourceErrorException(message, l);
    }

    public void noSuchModule(Location location) {
        throw new SourceErrorException(
                "Module does not exist or is not in scope.", location);
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
    // Prover Mode
    // -----------------------------------------------------------

    /**
     * <p>The set of immmutable VCs that the in house provers can use.</p>
     *
     * @return VCs to be proved.
     */
    public List<VC> proverOutput() {
        return myOutputGenerator.getProverOutput();
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
            myCurrentAssertiveCode.addFreeVar(createVarExp(v.getLocation(), v
                    .getName(), v.getTy().getMathTypeValue()));
        }
    }

    /**
     * <p>Append VC Generator step details to the expression's
     * location.</p>
     *
     * @param exp The current expression we are dealing with.
     * @param text VC Generator step details.
     *
     * @return The modified expression.
     */
    private Exp appendToLocation(Exp exp, String text) {
        // Check if the expression is empty or not
        // and it must have a valid location.
        if (exp != null && exp.getLocation() != null) {
            // Recursively apply to infix expressions.
            if (exp instanceof InfixExp) {
                appendToLocation(((InfixExp) exp).getLeft(), text);
                appendToLocation(((InfixExp) exp).getRight(), text);
            }
            else {
                Location loc = exp.getLocation();
                if (loc.getDetails() == null) {
                    loc.setDetails(text);
                }
                else {
                    String details = loc.getDetails().concat(text);
                    loc.setDetails(details);
                }
            }
        }

        return exp;
    }

    /**
     * <p>Converts the different types of <code>Exp</code> to the
     * ones used by the VC Generator.</p>
     *
     * @param oldExp The expression to be converted.
     *
     * @return An <code>Exp</code>.
     */
    private Exp convertExp(Exp oldExp) {
        // Case #1: ProgramIntegerExp
        if (oldExp instanceof ProgramIntegerExp) {
            IntegerExp exp = new IntegerExp();
            exp.setValue(((ProgramIntegerExp) oldExp).getValue());
            exp.setMathType(Z);
            return exp;
        }
        // Case #2: VariableDotExp
        else if (oldExp instanceof VariableDotExp) {
            DotExp exp = new DotExp();
            List<VariableExp> segments =
                    ((VariableDotExp) oldExp).getSegments();
            edu.clemson.cs.r2jt.collections.List<Exp> newSegments =
                    new edu.clemson.cs.r2jt.collections.List<Exp>();

            // Need to replace each of the segments in a dot expression
            MTType lastMathType = null;
            MTType lastMathTypeValue = null;
            for (VariableExp v : segments) {
                VarExp varExp = new VarExp();

                // Can only be a VariableNameExp. Anything else
                // is a case we have not handled.
                if (v instanceof VariableNameExp) {
                    varExp.setName(((VariableNameExp) v).getName());
                    lastMathType = v.getMathType();
                    lastMathTypeValue = v.getMathTypeValue();
                    newSegments.add(varExp);
                }
                else {
                    expNotHandled(v, v.getLocation());
                }
            }

            // Set the segments and the type information.
            exp.setSegments(newSegments);
            exp.setMathType(lastMathType);
            exp.setMathTypeValue(lastMathTypeValue);
            return exp;
        }
        // Case #3: VariableNameExp
        else if (oldExp instanceof VariableNameExp) {
            VarExp exp = new VarExp();
            exp.setName(((VariableNameExp) oldExp).getName());
            exp.setMathType(oldExp.getMathType());
            exp.setMathTypeValue(oldExp.getMathTypeValue());
            return exp;
        }

        return oldExp;
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
     * <p>Returns an <code>DotExp</code> with the <code>VarDec</code>
     * and its initialization ensures clause.</p>
     *
     * @param var The declared variable.
     *
     * @return The new <code>DotExp</code>.
     */
    private DotExp createInitExp(VarDec var) {
        // Convert the declared variable into a VarExp
        VarExp varExp =
                createVarExp(var.getLocation(), var.getName(), var.getTy()
                        .getMathTypeValue());

        // Left hand side of the expression
        VarExp left = null;

        // NameTy
        if (var.getTy() instanceof NameTy) {
            NameTy ty = (NameTy) var.getTy();
            left = createVarExp(ty.getLocation(), ty.getName(), MTYPE);
        }
        else {
            tyNotHandled(var.getTy(), var.getTy().getLocation());
        }

        // Complicated steps to construct the argument list
        // YS: No idea why it is so complicated!
        edu.clemson.cs.r2jt.collections.List<Exp> expList =
                new edu.clemson.cs.r2jt.collections.List<Exp>();
        expList.add(varExp);
        FunctionArgList argList = new FunctionArgList();
        argList.setArguments(expList);
        edu.clemson.cs.r2jt.collections.List<FunctionArgList> functionArgLists =
                new edu.clemson.cs.r2jt.collections.List<FunctionArgList>();
        functionArgLists.add(argList);

        // Right hand side of the expression
        FunctionExp right =
                new FunctionExp(var.getLocation(), null,
                        createPosSymbol("Is_Initial"), null, functionArgLists);
        right.setMathType(BOOLEAN);

        // Create the DotExp
        edu.clemson.cs.r2jt.collections.List<Exp> exps =
                new edu.clemson.cs.r2jt.collections.List<Exp>();
        exps.add(left);
        exps.add(right);
        DotExp exp = new DotExp(var.getLocation(), exps, null);
        exp.setMathType(BOOLEAN);

        return exp;
    }

    /**
     * <p>Creates a variable expression with the name
     * "P_val" and has type "N".</p>
     *
     * @param location Location that wants to create
     *                 this variable.
     *
     * @return The created <code>VarExp</code>.
     */
    private VarExp createPValExp(Location location) {
        // Locate "N" (Natural Number)
        MathSymbolEntry mse = searchMathSymbol(location, "N");
        try {
            // Create a variable with the name P_val
            return createVarExp(location, createPosSymbol("P_val"), mse
                    .getTypeValue());
        }
        catch (SymbolNotOfKindTypeException e) {
            notAType(mse, location);
        }

        return null;
    }

    /**
     * <p>Create a question mark variable with the oldVar
     * passed in.</p>
     *
     * @param exp The full expression clause.
     * @param oldVar The old variable expression.
     *
     * @return A new variable with the question mark in <code>VarExp</code> form.
     */
    private VarExp createQuestionMarkVariable(Exp exp, VarExp oldVar) {
        // Add an extra question mark to the front of oldVar
        VarExp newOldVar =
                new VarExp(null, null, createPosSymbol("?"
                        + oldVar.getName().getName()));
        newOldVar.setMathType(oldVar.getMathType());
        newOldVar.setMathTypeValue(oldVar.getMathTypeValue());

        // Applies the question mark to oldVar if it is our first time visiting.
        if (exp.containsVar(oldVar.getName().getName(), false)) {
            return createQuestionMarkVariable(exp, newOldVar);
        }
        // Don't need to apply the question mark here.
        else if (exp.containsVar(newOldVar.getName().toString(), false)) {
            return createQuestionMarkVariable(exp, newOldVar);
        }
        else {
            // Return the new variable expression with the question mark
            if (oldVar.getName().getName().charAt(0) != '?') {
                return newOldVar;
            }
        }

        // Return our old self.
        return oldVar;
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
     * <p>Creates the name of the output file.</p>
     *
     * @return Name of the file
     */
    private String createVCFileName() {
        File file = myInstanceEnvironment.getTargetFile();
        ModuleID cid = myInstanceEnvironment.getModuleID(file);
        file = myInstanceEnvironment.getFile(cid);
        String filename = file.toString();
        int temp = filename.indexOf(".");
        String tempfile = filename.substring(0, temp);
        String mainFileName;

        mainFileName = tempfile + ".asrt_new";

        return mainFileName;
    }

    /**
     * <p>Returns all the constraint clauses combined together for the
     * for the current <code>ModuleDec</code>.</p>
     *
     * @param loc The location of the <code>ModuleDec</code>.
     * @param imports The list of imported modules.
     *
     * @return The constraint clause <code>Exp</code>.
     */
    private Exp getConstraints(Location loc, List<ModuleIdentifier> imports) {
        Exp retExp = null;

        // Loop
        for (ModuleIdentifier mi : imports) {
            try {
                ModuleDec dec =
                        mySymbolTable.getModuleScope(mi).getDefiningElement();
                List<Exp> contraintExpList = null;

                // Handling for facility imports
                if (dec instanceof ShortFacilityModuleDec) {
                    FacilityDec facDec =
                            ((ShortFacilityModuleDec) dec).getDec();
                    dec =
                            mySymbolTable.getModuleScope(
                                    new ModuleIdentifier(facDec
                                            .getConceptName().getName()))
                                    .getDefiningElement();
                }

                if (dec instanceof ConceptModuleDec) {
                    contraintExpList =
                            ((ConceptModuleDec) dec).getConstraints();

                    // Copy all the constraints
                    for (Exp e : contraintExpList) {
                        // Deep copy and set the location detail
                        Exp constraint = Exp.copy(e);
                        if (constraint.getLocation() != null) {
                            Location theLoc = constraint.getLocation();
                            theLoc.setDetails("Constraint of Module: "
                                    + dec.getName());
                            setLocation(constraint, theLoc);
                        }

                        // Form conjunct if needed.
                        if (retExp == null) {
                            retExp = Exp.copy(e);
                        }
                        else {
                            retExp =
                                    myTypeGraph.formConjunct(retExp, Exp
                                            .copy(e));
                        }
                    }
                }
            }
            catch (NoSuchSymbolException e) {
                System.err.println("Module " + mi.toString()
                        + " does not exist or is not in scope.");
                noSuchModule(loc);
            }
        }

        return retExp;
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
        Exp retExp;

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
        }
        else {
            Location loc = (Location) dec.getLocation().clone();
            retExp = myTypeGraph.getTrueVarExp();
            setLocation(retExp, loc);
        }

        if (retExp.getLocation() != null) {
            Location loc = retExp.getLocation();
            loc.setDetails("Ensures Clause of " + name);
            setLocation(retExp, loc);
        }

        return retExp;
    }

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
        OperationEntry opEntry = searchOperation(loc, qual, name, argTypes);

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
     * <p>Returns the requires clause for the current <code>Dec</code>.</p>
     *
     * @param dec The corresponding <code>Dec</code>.
     *
     * @return The requires clause <code>Exp</code>.
     */
    private Exp getRequiresClause(Dec dec) {
        PosSymbol name = dec.getName();
        Exp requires = null;
        Exp retExp;

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
        }
        else {
            Location loc = (Location) dec.getLocation().clone();
            retExp = myTypeGraph.getTrueVarExp();
            setLocation(retExp, loc);
        }

        if (retExp.getLocation() != null) {
            Location loc = retExp.getLocation();
            loc.setDetails("Requires Clause for " + name);
            setLocation(retExp, loc);
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
     * <p>Checks to see if the expression passed in is a
     * verification variable or not. A verification variable
     * is either "P_val" or starts with "?".</p>
     *
     * @param name Expression that we want to check
     *
     * @return True/False
     */
    private boolean isVerificationVar(Exp name) {
        // VarExp
        if (name instanceof VarExp) {
            String strName = ((VarExp) name).getName().getName();
            // Case #1: Question mark variables
            if (strName.charAt(0) == '?') {
                return true;
            }
            // Case #2: P_val
            else if (strName.equals("P_val")) {
                return true;
            }
        }
        // DotExp
        else if (name instanceof DotExp) {
            // Recursively call this method until we get
            // either true or false.
            List<Exp> names = ((DotExp) name).getSegments();
            return isVerificationVar(names.get(0));
        }

        // Definitely not a verification variable.
        return false;
    }

    /**
     * <p>Loop through our stack of incomplete assertive codes.</p>
     */
    private void loopAssertiveCodeStack() {
        // Loop until our to process assertive code stack is empty
        while (!myIncAssertiveCodeStack.empty()) {
            // Set the incoming assertive code as our current assertive
            // code we are working on.
            myCurrentAssertiveCode = myIncAssertiveCodeStack.pop();

            myVCBuffer.append("\n***********************");
            myVCBuffer.append("***********************\n");

            // Append any information that still needs to be added to our
            // Debug VC Buffer
            myVCBuffer.append(myIncAssertiveCodeStackInfo.pop());

            // Apply proof rules
            applyEBRules();

            myVCBuffer.append("\n***********************");
            myVCBuffer.append("***********************\n");

            // Add it to our list of final assertive codes
            myFinalAssertiveCodeList.add(myCurrentAssertiveCode);

            // Set the current assertive code to null
            myCurrentAssertiveCode = null;
        }
    }

    /**
     * <p>Modifies the ensures clause based on the parameter mode.</p>
     *
     * @param ensures The <code>Exp</code> containing the ensures clause.
     * @param opLocation The <code>Location</code> for the operation
     * @param opName The name of the operation.
     * @param parameterVarDecList The list of parameter variables for the operation.
     *
     * @return The modified ensures clause <code>Exp</code>.
     */
    private Exp modifyEnsuresByParameter(Exp ensures, Location opLocation,
            String opName, List<ParameterVarDec> parameterVarDecList) {
        // Loop through each parameter
        for (ParameterVarDec p : parameterVarDecList) {
            // Ty is NameTy
            if (p.getTy() instanceof NameTy) {
                NameTy pNameTy = (NameTy) p.getTy();

                // Exp form of the parameter variable
                VarExp parameterExp =
                        new VarExp(p.getLocation(), null, p.getName().copy());
                parameterExp.setMathType(pNameTy.getMathTypeValue());

                // Create an old exp (#parameterExp)
                OldExp oldParameterExp =
                        new OldExp(p.getLocation(), Exp.copy(parameterExp));
                oldParameterExp.setMathType(pNameTy.getMathTypeValue());

                // Preserves or Restores mode
                if (p.getMode() == Mode.PRESERVES
                        || p.getMode() == Mode.RESTORES) {
                    // Create an equals expression of the form "#parameterExp = parameterExp"
                    EqualsExp equalsExp =
                            new EqualsExp(opLocation, oldParameterExp,
                                    EqualsExp.EQUAL, parameterExp);
                    equalsExp.setMathType(BOOLEAN);

                    // Set the details for the new location
                    Location equalLoc;
                    if (ensures != null && ensures.getLocation() != null) {
                        Location enLoc = ensures.getLocation();
                        equalLoc = ((Location) enLoc.clone());
                    }
                    else {
                        equalLoc = ((Location) opLocation.clone());
                        equalLoc.setDetails("Ensures Clause of " + opName);
                    }
                    equalLoc.setDetails(equalLoc.getDetails()
                            + " (Condition from \"" + p.getMode().getModeName()
                            + "\" parameter mode)");
                    equalsExp.setLocation(equalLoc);

                    // Create an AND infix expression with the ensures clause
                    if (ensures != null
                            && !ensures.equals(myTypeGraph.getTrueVarExp())) {
                        ensures = myTypeGraph.formConjunct(ensures, equalsExp);
                    }
                    // Make new expression the ensures clause
                    else {
                        ensures = equalsExp;
                    }
                }
                // Clears mode
                else if (p.getMode() == Mode.CLEARS) {
                    // Query for the type entry in the symbol table
                    ProgramTypeEntry typeEntry =
                            searchProgramType(pNameTy.getLocation(), pNameTy
                                    .getName());

                    Exp init;
                    if (typeEntry.getDefiningElement() instanceof TypeDec) {
                        // Obtain the original dec from the AST
                        TypeDec type = (TypeDec) typeEntry.getDefiningElement();

                        // Obtain the exemplar in VarExp form
                        VarExp exemplar =
                                new VarExp(null, null, type.getExemplar());
                        exemplar.setMathType(pNameTy.getMathTypeValue());

                        // Deep copy the original initialization ensures and the constraint
                        init = Exp.copy(type.getInitialization().getEnsures());

                        // Replace the formal with the actual
                        init = replace(init, exemplar, parameterExp);

                        // Set the details for the new location
                        if (init.getLocation() != null) {
                            Location initLoc;
                            if (ensures != null
                                    && ensures.getLocation() != null) {
                                Location reqLoc = ensures.getLocation();
                                initLoc = ((Location) reqLoc.clone());
                            }
                            else {
                                initLoc = ((Location) opLocation.clone());
                                initLoc.setDetails("Ensures Clause of "
                                        + opName);
                            }
                            initLoc.setDetails(initLoc.getDetails()
                                    + " (Condition from \""
                                    + p.getMode().getModeName()
                                    + "\" parameter mode)");
                            init.setLocation(initLoc);
                        }
                    }
                    // Since the type is generic, we can only use the is_initial predicate
                    // to ensure that the value is initial value.
                    else {
                        // Obtain the original dec from the AST
                        Location varLoc = p.getLocation();

                        // Create an is_initial dot expression
                        init =
                                createInitExp(new VarDec(p.getName(), p.getTy()));
                        if (varLoc != null) {
                            Location loc = (Location) varLoc.clone();
                            loc.setDetails("Initial Value for "
                                    + p.getName().getName());
                            setLocation(init, loc);
                        }
                    }

                    // Create an AND infix expression with the ensures clause
                    if (ensures != null
                            && !ensures.equals(myTypeGraph.getTrueVarExp())) {
                        ensures = myTypeGraph.formConjunct(ensures, init);
                    }
                    // Make initialization expression the ensures clause
                    else {
                        ensures = init;
                    }
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
     * @param opLocation The <code>Location</code> for the operation.
     * @param opName The name for the operation.
     *
     * @return The modified ensures clause <code>Exp</code>.
     */
    private Exp modifyEnsuresClause(Exp ensures, Location opLocation,
            String opName) {
        // Obtain the list of parameters for the current operation
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

        // Modifies the existing ensures clause based on
        // the parameter modes.
        ensures =
                modifyEnsuresByParameter(ensures, opLocation, opName,
                        parameterVarDecList);

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
     * @param opLocation The <code>Location</code> for the operation.
     * @param opName The name for the operation.
     *
     * @return The modified requires clause <code>Exp</code>.
     */
    private Exp modifyRequiresByParameter(Exp requires, Location opLocation,
            String opName) {
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

                // Query for the type entry in the symbol table
                typeEntry =
                        searchProgramType(pNameTy.getLocation(), pNameTy
                                .getName());

                // Obtain the original dec from the AST
                TypeDec type = (TypeDec) typeEntry.getDefiningElement();

                // Convert p to a VarExp
                VarExp pExp = new VarExp(null, null, p.getName());
                pExp.setMathType(pNameTy.getMathTypeValue());

                // Obtain the exemplar in VarExp form
                VarExp exemplar = new VarExp(null, null, type.getExemplar());
                exemplar.setMathType(pNameTy.getMathTypeValue());

                // Deep copy the original initialization ensures and the constraint
                Exp init = Exp.copy(type.getInitialization().getEnsures());
                Exp constraint = Exp.copy(type.getConstraint());

                // Only worry about replaces mode parameters
                if (p.getMode() == Mode.REPLACES && init != null) {
                    // Replace the formal with the actual
                    init = replace(init, exemplar, pExp);

                    // Set the details for the new location
                    if (init.getLocation() != null) {
                        Location initLoc;
                        if (requires != null && requires.getLocation() != null) {
                            Location reqLoc = requires.getLocation();
                            initLoc = ((Location) reqLoc.clone());
                        }
                        else {
                            // Append the name of the current procedure
                            String details = "";
                            if (myCurrentOperationEntry != null) {
                                details =
                                        " in Procedure "
                                                + myCurrentOperationEntry
                                                        .getName();
                            }

                            // Set the details of the current location
                            initLoc = ((Location) opLocation.clone());
                            initLoc.setDetails("Requires Clause of " + opName
                                    + details);
                        }
                        initLoc.setDetails(initLoc.getDetails()
                                + " (Assumption from \""
                                + p.getMode().getModeName()
                                + "\" parameter mode)");
                        init.setLocation(initLoc);
                    }

                    // Create an AND infix expression with the requires clause
                    if (requires != null
                            && !requires.equals(myTypeGraph.getTrueVarExp())) {
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
                            && !constraint.equals(myTypeGraph.getTrueVarExp())) {
                        // Replace the formal with the actual
                        constraint = replace(constraint, exemplar, pExp);

                        // Set the details for the new location
                        if (constraint.getLocation() != null) {
                            Location constLoc;
                            if (requires != null
                                    && requires.getLocation() != null) {
                                Location reqLoc = requires.getLocation();
                                constLoc = ((Location) reqLoc.clone());
                            }
                            else {
                                // Append the name of the current procedure
                                String details = "";
                                if (myCurrentOperationEntry != null) {
                                    details =
                                            " in Procedure "
                                                    + myCurrentOperationEntry
                                                            .getName();
                                }

                                constLoc = ((Location) opLocation.clone());
                                constLoc.setDetails("Requires Clause of "
                                        + opName + details);
                            }
                            constLoc.setDetails(constLoc.getDetails()
                                    + " (Constraint from \""
                                    + p.getMode().getModeName()
                                    + "\" parameter mode)");
                            constraint.setLocation(constLoc);
                        }

                        // Create an AND infix expression with the requires clause
                        if (requires != null
                                && !requires
                                        .equals(myTypeGraph.getTrueVarExp())) {
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
                myCurrentAssertiveCode.addFreeVar(createVarExp(p.getLocation(),
                        p.getName(), pNameTy.getMathTypeValue()));
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
     * @param opName The name of the operation.
     *
     * @return The modified requires clause <code>Exp</code>.
     */
    private Exp modifyRequiresClause(Exp requires, Location opLocation,
            String opName) {
        // Modifies the existing requires clause based on
        // the parameter modes.
        requires = modifyRequiresByParameter(requires, opLocation, opName);

        // Modifies the existing requires clause based on
        // the parameter modes.
        // TODO: Ask Murali what this means
        requires = modifyRequiresByGlobalMode(requires);

        return requires;
    }

    /**
     * <p>Negate the incoming expression.</p>
     *
     * @param exp Expression to be negated.
     *
     * @return Negated expression.
     */
    private Exp negateExp(Exp exp) {
        Exp retExp = Exp.copy(exp);
        if (exp instanceof EqualsExp) {
            if (((EqualsExp) exp).getOperator() == EqualsExp.EQUAL)
                ((EqualsExp) retExp).setOperator(EqualsExp.NOT_EQUAL);
            else
                ((EqualsExp) retExp).setOperator(EqualsExp.EQUAL);
        }
        else if (exp instanceof PrefixExp) {
            if (((PrefixExp) exp).getSymbol().getName().toString()
                    .equals("not")) {
                retExp = ((PrefixExp) exp).getArgument();
            }
        }
        else {
            PrefixExp tmp = new PrefixExp();
            setLocation(tmp, exp.getLocation());
            tmp.setArgument(exp);
            tmp.setSymbol(createPosSymbol("not"));
            tmp.setMathType(BOOLEAN);
            retExp = tmp;
        }
        return retExp;
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
     * <p>Replace the formal with the actual variables
     * from the facility declaration rule.</p>
     *
     * @param exp The expression to be replaced.
     * @param facParam The list of facility declaration parameter variables.
     * @param concParam The list of concept parameter variables.
     *
     * @return The modified expression.
     */
    private Exp replaceFacilityDeclarationVariables(Exp exp, List facParam,
            List concParam) {
        for (int i = 0; i < facParam.size(); i++) {
            if (facParam.get(i) instanceof Dec
                    && (concParam.get(i) instanceof Dec)) {
                // Both are instances of Dec
                Dec facDec = (Dec) facParam.get(i);
                Dec concDec = (Dec) concParam.get(i);

                // Variable to be replaced
                VarExp expToReplace =
                        createVarExp(facDec.getLocation(), facDec.getName(),
                                facDec.getMathType());

                // Concept variable
                VarExp expToUse =
                        createVarExp(concDec.getLocation(), concDec.getName(),
                                concDec.getMathType());

                // Temporary replacement to avoid formal and actuals being the same
                exp = replace(exp, expToReplace, expToUse);

                // Create a old exp from expToReplace
                OldExp r = new OldExp(null, expToReplace);
                r.setMathType(expToReplace.getMathType());

                // Create a old exp from expToUse
                OldExp u = new OldExp(null, expToUse);
                u.setMathType(expToUse.getMathType());

                // Actually perform the desired replacement
                exp = replace(exp, r, u);
            }
            else if (facParam.get(i) instanceof Dec
                    && concParam.get(i) instanceof ModuleArgumentItem) {
                // We have a ModuleArgumentItem
                Dec facDec = (Dec) facParam.get(i);
                ModuleArgumentItem concItem =
                        (ModuleArgumentItem) concParam.get(i);

                // Variable to be replaced
                VarExp expToReplace =
                        createVarExp(facDec.getLocation(), facDec.getName(),
                                facDec.getMathType());

                // Concept variable
                VarExp expToUse = new VarExp();
                if (concItem.getName() != null) {
                    expToUse.setName(concItem.getName());
                }
                else {
                    expToUse.setName(createPosSymbol(concItem.getEvalExp()
                            .toString()));
                }

                // Set the math type for the concept variable
                if (concItem.getProgramTypeValue() != null) {
                    expToUse.setMathType(concItem.getProgramTypeValue()
                            .toMath());
                }
                else {
                    expToUse.setMathType(concItem.getMathType());
                }

                // Temporary replacement to avoid formal and actuals being the same
                exp = replace(exp, expToReplace, expToUse);

                // Create a old exp from expToReplace
                OldExp r = new OldExp(null, expToReplace);
                r.setMathType(expToReplace.getMathType());

                // Create a old exp from expToUse
                OldExp u = new OldExp(null, expToUse);
                u.setMathType(expToUse.getMathType());

                // Actually perform the desired replacement
                exp = replace(exp, r, u);
            }
        }

        return exp;
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
            newConfirm = myCurrentAssertiveCode.getFinalConfirm();
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
                                createQuestionMarkVariable(myTypeGraph
                                        .formConjunct(ensures, newConfirm),
                                        (VarExp) localFreeVar);
                        myCurrentAssertiveCode.addFreeVar(localFreeVar);
                    }
                    else {
                        localFreeVar =
                                createQuestionMarkVariable(myTypeGraph
                                        .formConjunct(ensures, newConfirm),
                                        (VarExp) localFreeVar);
                    }

                    // Creating "#" expressions and replace these in the
                    // ensures clause.
                    OldExp osVar = new OldExp(null, Exp.copy(globalFreeVar));
                    OldExp oldNameOSVar =
                            new OldExp(null, Exp.copy(oldNamesVar));
                    ensures = replace(ensures, oldNamesVar, globalFreeVar);
                    ensures = replace(ensures, oldNameOSVar, osVar);

                    // If it is not simple replacement, replace all ensures clauses
                    // with the appropriate expressions.
                    if (!isSimple) {
                        ensures = replace(ensures, globalFreeVar, localFreeVar);
                        ensures = replace(ensures, osVar, globalFreeVar);
                        newConfirm =
                                replace(newConfirm, globalFreeVar, localFreeVar);
                    }

                    // Set newConfirm as our new final confirm statement
                    myCurrentAssertiveCode.setFinalConfirm(newConfirm);
                }
                // Error: Why isn't it a free variable.
                else {
                    notInFreeVarList(stateVar.getName(), stateVar.getLocation());
                }
            }
        }

        // Replace postcondition variables in the ensures clause
        for (int i = 0; i < argList.size(); i++) {
            ParameterVarDec varDec = paramList.get(i);
            ProgramExp pExp = argList.get(i);
            PosSymbol VDName = varDec.getName();
            newConfirm = myCurrentAssertiveCode.getFinalConfirm();

            // VarExp form of the parameter variable
            VarExp oldExp = new VarExp(null, null, VDName);
            oldExp.setMathType(pExp.getMathType());
            oldExp.setMathTypeValue(pExp.getMathTypeValue());

            // Convert the pExp into a something we can use
            Exp repl = convertExp(pExp);
            Exp undqRep = null, quesRep = null;
            OldExp oSpecVar, oRealVar;
            String replName = null;

            // Case #1: ProgramIntegerExp
            if (pExp instanceof ProgramIntegerExp) {
                replName =
                        Integer.toString(((ProgramIntegerExp) repl).getValue());

                // Create a variable expression of the form "_?[Argument Name]"
                undqRep =
                        new VarExp(null, null, createPosSymbol("_?" + replName));
                undqRep.setMathType(pExp.getMathType());
                undqRep.setMathTypeValue(pExp.getMathTypeValue());

                // Create a variable expression of the form "?[Argument Name]"
                quesRep =
                        new VarExp(null, null, createPosSymbol("?" + replName));
                quesRep.setMathType(pExp.getMathType());
                quesRep.setMathTypeValue(pExp.getMathTypeValue());
            }
            // Case #2: VariableDotExp
            else if (pExp instanceof VariableDotExp) {
                if (repl instanceof DotExp) {
                    Exp pE = ((DotExp) repl).getSegments().get(0);
                    replName = pE.toString(0);

                    // Create a variable expression of the form "_?[Argument Name]"
                    undqRep = Exp.copy(repl);
                    edu.clemson.cs.r2jt.collections.List<Exp> segList =
                            ((DotExp) undqRep).getSegments();
                    VariableNameExp undqNameRep =
                            new VariableNameExp(null, null,
                                    createPosSymbol("_?" + replName));
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
                            new VariableNameExp(null, null,
                                    createPosSymbol("_?" + replName));
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
                    expNotHandled(pExp, pExp.getLocation());
                }
            }
            // Case #3: VariableNameExp
            else if (pExp instanceof VariableNameExp) {
                // Name of repl in string form
                replName = ((VariableNameExp) pExp).getName().getName();

                // Create a variable expression of the form "_?[Argument Name]"
                undqRep =
                        new VarExp(null, null, createPosSymbol("_?" + replName));
                undqRep.setMathType(pExp.getMathType());
                undqRep.setMathTypeValue(pExp.getMathTypeValue());

                // Create a variable expression of the form "?[Argument Name]"
                quesRep =
                        new VarExp(null, null, createPosSymbol("?" + replName));
                quesRep.setMathType(pExp.getMathType());
                quesRep.setMathTypeValue(pExp.getMathTypeValue());
            }
            // Error: Case not handled!
            else {
                expNotHandled(pExp, pExp.getLocation());
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
                                    createPosSymbol(replName), false);
                    if (freeVar == null) {
                        freeVar =
                                createVarExp(varDec.getLocation(),
                                        createPosSymbol(replName), varDec
                                                .getTy().getMathTypeValue());
                    }

                    // Apply the question mark to the free variable
                    freeVar =
                            createQuestionMarkVariable(myTypeGraph
                                    .formConjunct(ensures, newConfirm), freeVar);

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
                        ensures = replace(ensures, oldExp, undqRep);
                        ensures = replace(ensures, oSpecVar, repl);

                        // Add it to our list of variables to be replaced later
                        undRepList.add(undqRep);
                        replList.add(quesVar);
                    }
                    else {
                        // Replace the ensures clause
                        ensures = replace(ensures, oldExp, quesRep);
                        ensures = replace(ensures, oSpecVar, repl);
                    }

                    // Update our final confirm with the parameter argument
                    newConfirm = replace(newConfirm, repl, quesVar);
                    myCurrentAssertiveCode.setFinalConfirm(newConfirm);
                }
                // All other modes
                else {
                    // Check if our ensures clause has the parameter variable in it.
                    if (ensures.containsVar(VDName.getName(), true)
                            || ensures.containsVar(VDName.getName(), false)) {
                        // Replace the ensures clause
                        ensures = replace(ensures, oldExp, undqRep);
                        ensures = replace(ensures, oSpecVar, undqRep);

                        // Add it to our list of variables to be replaced later
                        undRepList.add(undqRep);
                        replList.add(repl);
                    }
                    else {
                        // Replace the ensures clause
                        ensures = replace(ensures, oldExp, repl);
                        ensures = replace(ensures, oSpecVar, repl);
                    }
                }
            }
        }

        // Replace the temp values with the actual values
        for (int i = 0; i < undRepList.size(); i++) {
            ensures = replace(ensures, undRepList.get(i), replList.get(i));
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

            // Convert the pExp into a something we can use
            Exp repl = convertExp(pExp);

            // VarExp form of the parameter variable
            VarExp oldExp = new VarExp(null, null, varDec.getName());
            oldExp.setMathType(pExp.getMathType());
            oldExp.setMathTypeValue(pExp.getMathTypeValue());

            // New VarExp
            VarExp newExp =
                    new VarExp(null, null, createPosSymbol("_"
                            + varDec.getName().getName()));
            newExp.setMathType(repl.getMathType());
            newExp.setMathTypeValue(repl.getMathTypeValue());

            // Replace the old with the new in the requires clause
            requires = replace(requires, oldExp, newExp);

            // Add it to our list
            undRepList.add(newExp);
            replList.add(repl);
        }

        // Replace the temp values with the actual values
        for (int i = 0; i < undRepList.size(); i++) {
            requires = replace(requires, undRepList.get(i), replList.get(i));
        }

        return requires;
    }

    /**
     * <p>Given a math symbol name, locate and return
     * the <code>MathSymbolEntry</code> stored in the
     * symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param name The string name of the math symbol.
     *
     * @return An <code>MathSymbolEntry</code> from the
     *         symbol table.
     */
    private MathSymbolEntry searchMathSymbol(Location loc, String name) {
        // Query for the corresponding math symbol
        MathSymbolEntry ms = null;
        try {
            ms =
                    myCurrentModuleScope.queryForOne(
                            new UnqualifiedNameQuery(name,
                                    ImportStrategy.IMPORT_RECURSIVE,
                                    FacilityStrategy.FACILITY_IGNORE, true,
                                    true)).toMathSymbolEntry(loc);
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, name, loc);
        }
        catch (DuplicateSymbolException dse) {
            //This should be caught earlier, when the duplicate symbol is
            //created
            throw new RuntimeException(dse);
        }

        return ms;
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
     * <p>Given the name of the type locate and return
     * the <code>ProgramTypeEntry</code> stored in the
     * symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param name The name of the type.
     *
     * @return An <code>ProgramTypeEntry</code> from the
     *         symbol table.
     */
    private ProgramTypeEntry searchProgramType(Location loc, PosSymbol name) {
        // Query for the corresponding operation
        ProgramTypeEntry pt = null;
        try {
            pt =
                    myCurrentModuleScope.queryForOne(
                            new NameQuery(null, name,
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false)).toProgramTypeEntry(loc);
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, name.getName(), loc);
        }
        catch (DuplicateSymbolException dse) {
            //This should be caught earlier, when the duplicate type is
            //created
            throw new RuntimeException(dse);
        }

        return pt;
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

    /**
     * <p>Simplify the assume statement where possible.</p>
     *
     * @param stmt The assume statement we want to simplify.
     * @param exp The current expression we are dealing with.
     *
     * @return The modified expression in <code>Exp/code> form.
     */
    private Exp simplifyAssumeRule(AssumeStmt stmt, Exp exp) {
        // Variables
        Exp assertion = stmt.getAssertion();
        boolean keepAssumption = false;

        // EqualsExp
        if (assertion instanceof EqualsExp) {
            EqualsExp equalsExp = (EqualsExp) assertion;

            // Only do simplifications if we have an equals
            if (equalsExp.getOperator() == EqualsExp.EQUAL) {
                boolean verificationVariable =
                        isVerificationVar(equalsExp.getLeft());

                // Create a temp expression where left is replaced with the right
                Exp tmp =
                        replace(exp, equalsExp.getLeft(), equalsExp.getRight());
                if (equalsExp.getLeft() instanceof VarExp) {
                    // If left is still part of confirm
                    VarExp left = (VarExp) equalsExp.getLeft();
                    if (tmp.containsVar(left.getName().getName(), false)) {
                        keepAssumption = true;
                    }
                }

                // If tmp is not null, then it means we have to check the right
                if (tmp == null) {
                    // Create a temp expression where right is replaced with the left
                    verificationVariable =
                            isVerificationVar(equalsExp.getRight());
                    tmp =
                            replace(exp, equalsExp.getRight(), equalsExp
                                    .getLeft());
                    if (equalsExp.getRight() instanceof VarExp) {
                        // If right is still part of confirm
                        VarExp right = (VarExp) equalsExp.getRight();
                        if (tmp.containsVar(right.getName().getName(), false)) {
                            keepAssumption = true;
                        }
                    }
                }

                // We clear our assertion for this assumes if this is a
                // verification variable or if we don't have to
                // keep this assumption.
                if (verificationVariable || !keepAssumption) {
                    assertion = null;
                }

                // Update exp
                if (!tmp.equals(exp)) {
                    exp = tmp;
                }
            }
        }
        // InfixExp
        else if (assertion instanceof InfixExp) {
            InfixExp infixExp = (InfixExp) assertion;

            // Only do simplifications if we have an and operator
            if (infixExp.getOpName().equals("and")) {
                // Recursively call simplify on the left and on the right
                AssumeStmt left = new AssumeStmt(Exp.copy(infixExp.getLeft()));
                AssumeStmt right =
                        new AssumeStmt(Exp.copy(infixExp.getRight()));
                exp = simplifyAssumeRule(left, exp);
                exp = simplifyAssumeRule(right, exp);

                // Case #1: Nothing left
                if (left.getAssertion() == null && right.getAssertion() == null) {
                    assertion = null;
                }
                // Case #2: Both still have assertions
                else if (left.getAssertion() != null
                        && right.getAssertion() != null) {
                    assertion =
                            myTypeGraph.formConjunct(left.getAssertion(), right
                                    .getAssertion());
                }
                // Case #3: Left still has assertions
                else if (left.getAssertion() != null) {
                    assertion = left.getAssertion();
                }
                // Case #r: Right still has assertions
                else {
                    assertion = right.getAssertion();
                }
            }
        }

        // Store the new assertion
        stmt.setAssertion(assertion);

        return exp;
    }

    // -----------------------------------------------------------
    // Proof Rules
    // -----------------------------------------------------------

    /**
     *  <p>Applies the change rule.</p>
     *
     * @param change The change clause
     */
    private void applyChangeRule(VerificationStatement change) {
        List<VariableExp> changeList =
                (List<VariableExp>) change.getAssertion();
        Exp finalConfirm = myCurrentAssertiveCode.getFinalConfirm();

        // Loop through each variable
        for (VariableExp v : changeList) {
            // v is an instance of VariableNameExp
            if (v instanceof VariableNameExp) {
                VariableNameExp vNameExp = (VariableNameExp) v;

                // Create VarExp for vNameExp
                VarExp vExp =
                        createVarExp(vNameExp.getLocation(),
                                vNameExp.getName(), vNameExp.getMathType());

                // Create a new question mark variable
                VarExp newV = createQuestionMarkVariable(finalConfirm, vExp);

                // Add this new variable to our list of free variables
                myCurrentAssertiveCode.addFreeVar(newV);

                // Replace all instances of vExp with newV
                finalConfirm = replace(finalConfirm, vExp, newV);
            }
        }

        // Set the modified statement as our new final confirm
        myCurrentAssertiveCode.setFinalConfirm(finalConfirm);

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nChange Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");
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
        else if (statement instanceof IfStmt) {
            applyEBIfStmtRule((IfStmt) statement);
        }
        else if (statement instanceof SwapStmt) {
            applyEBSwapStmtRule((SwapStmt) statement);
        }
        else if (statement instanceof WhileStmt) {
            applyEBWhileStmtRule((WhileStmt) statement);
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
            myVCBuffer.append("\nAssume Rule Applied and Simplified: \n");
            myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
            myVCBuffer.append("\n_____________________ \n");
        }
        else {
            // Apply simplification
            Exp currentFinalConfirm =
                    simplifyAssumeRule(stmt, myCurrentAssertiveCode
                            .getFinalConfirm());
            if (stmt.getAssertion() != null) {
                // Create a new implies expression
                currentFinalConfirm =
                        myTypeGraph.formImplies(stmt.getAssertion(),
                                currentFinalConfirm);
            }

            // Set this as our new final confirm
            myCurrentAssertiveCode.setFinalConfirm(currentFinalConfirm);

            // Verbose Mode Debug Messages
            myVCBuffer.append("\nAssume Rule Applied: \n");
            myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
            myVCBuffer.append("\n_____________________ \n");
        }
    }

    /**
     * <p>Applies the call statement rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>CallStmt</code>.
     */
    private void applyEBCallStmtRule(CallStmt stmt) {
        // Call a method to locate the operation dec for this call
        OperationDec opDec =
                getOperationDec(stmt.getLocation(), stmt.getQualifier(), stmt
                        .getName(), stmt.getArguments());

        // Get the ensures clause for this operation
        // Note: If there isn't an ensures clause, it is set to "True"
        Exp ensures;
        if (opDec.getEnsures() != null) {
            ensures = Exp.copy(opDec.getEnsures());
        }
        else {
            ensures = myTypeGraph.getTrueVarExp();
        }

        // Get the requires clause for this operation
        Exp requires;
        if (opDec.getRequires() != null) {
            requires = Exp.copy(opDec.getRequires());
        }
        else {
            requires = myTypeGraph.getTrueVarExp();
        }

        // Check for recursive call of itself
        if (myCurrentOperationEntry.getName().equals(opDec.getName())
                && myCurrentOperationEntry.getReturnType() != null) {
            // Create a new confirm statement using P_val and the decreasing clause
            VarExp pVal = createPValExp(myOperationDecreasingExp.getLocation());

            // Create a new infix expression
            InfixExp exp =
                    new InfixExp(stmt.getLocation(), Exp
                            .copy(myOperationDecreasingExp),
                            createPosSymbol("<"), pVal);
            exp.setMathType(BOOLEAN);

            // Create the new confirm statement
            Location loc;
            if (myOperationDecreasingExp.getLocation() != null) {
                loc = (Location) myOperationDecreasingExp.getLocation().clone();
            }
            else {
                loc = (Location) stmt.getLocation().clone();
            }
            loc.setDetails("Show Termination of Recursive Call");
            setLocation(exp, loc);
            ConfirmStmt conf = new ConfirmStmt(loc, exp);

            // Add it to our list of assertions
            myCurrentAssertiveCode.addCode(conf);
        }

        // Modify ensures using the parameter modes
        ensures =
                modifyEnsuresByParameter(ensures, stmt.getLocation(), opDec
                        .getName().getName(), opDec.getParameters());

        // Replace PreCondition variables in the requires clause
        requires =
                replaceFormalWithActualReq(requires, opDec.getParameters(),
                        stmt.getArguments());

        // Replace PostCondition variables in the ensures clause
        ensures =
                replaceFormalWithActualEns(ensures, opDec.getParameters(),
                        opDec.getStateVars(), stmt.getArguments(), false);

        // Modify the location of the requires clause and add it to myCurrentAssertiveCode
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
            myCurrentAssertiveCode.addConfirm(requires);
        }

        // Modify the location of the requires clause and add it to myCurrentAssertiveCode
        if (ensures != null) {
            // Obtain the current location
            if (stmt.getName().getLocation() != null) {
                // Set the details of the current location
                Location loc = (Location) stmt.getName().getLocation().clone();
                loc.setDetails("Ensures Clause of " + opDec.getName());
                setLocation(ensures, loc);
            }

            // Add this to our list of things to assume
            myCurrentAssertiveCode.addAssume(ensures);
        }

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nOperation Call Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");
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
            myVCBuffer.append("\nConfirm Rule Applied and Simplified: \n");
            myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
            myVCBuffer.append("\n_____________________ \n");
        }
        else {
            // Obtain the current final confirm statement
            Exp currentFinalConfirm = myCurrentAssertiveCode.getFinalConfirm();

            // Check to see if we have a final confirm of "True"
            if (currentFinalConfirm instanceof VarExp
                    && currentFinalConfirm.equals(myTypeGraph.getTrueVarExp())) {

                // Obtain the current location
                if (assertion.getLocation() != null) {
                    // Set the details of the current location
                    Location loc = (Location) assertion.getLocation().clone();
                    setLocation(assertion, loc);
                }

                myCurrentAssertiveCode.setFinalConfirm(assertion);

                // Verbose Mode Debug Messages
                myVCBuffer.append("\nConfirm Rule Applied and Simplified: \n");
                myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
                myVCBuffer.append("\n_____________________ \n");
            }
            else {
                // Create a new and expression
                InfixExp newConf =
                        myTypeGraph
                                .formConjunct(assertion, currentFinalConfirm);

                // Set this new expression as the new final confirm
                myCurrentAssertiveCode.setFinalConfirm(newConf);

                // Verbose Mode Debug Messages
                myVCBuffer.append("\nConfirm Rule Applied: \n");
                myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
                myVCBuffer.append("\n_____________________ \n");
            }
        }
    }

    /**
     * <p>Applies each of the proof rules. This <code>AssertiveCode</code> will be
     * stored for later use and therefore should be considered immutable after
     * a call to this method.</p>
     */
    private void applyEBRules() {
        // Apply a proof rule to each of the assertions
        while (myCurrentAssertiveCode.hasAnotherAssertion()) {
            // Work our way from the last assertion
            VerificationStatement curAssertion =
                    myCurrentAssertiveCode.getLastAssertion();

            switch (curAssertion.getType()) {
            // Change Assertion
            case VerificationStatement.CHANGE:
                applyChangeRule(curAssertion);
                break;
            // Code
            case VerificationStatement.CODE:
                applyCodeRules((Statement) curAssertion.getAssertion());
                break;
            // Remember Assertion
            case VerificationStatement.REMEMBER:
                applyRememberRule();
                break;
            // Variable Declaration Assertion
            case VerificationStatement.VARIABLE:
                applyVarDeclRule(curAssertion);
                break;
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
        PosSymbol qualifier = null;
        ProgramExp assignExp = stmt.getAssign();
        ProgramParamExp assignParamExp = null;

        // Check to see what kind of expression is on the right hand side
        if (assignExp instanceof ProgramParamExp) {
            // Cast to a ProgramParamExp
            assignParamExp = (ProgramParamExp) assignExp;
        }
        else if (assignExp instanceof ProgramDotExp) {
            // Cast to a ProgramParamExp
            ProgramDotExp dotExp = (ProgramDotExp) assignExp;
            assignParamExp = (ProgramParamExp) dotExp.getExp();
            qualifier = dotExp.getQualifier();
        }
        else {
            // TODO: ERROR!
        }

        // Call a method to locate the operation dec for this call
        OperationDec opDec =
                getOperationDec(stmt.getLocation(), qualifier, assignParamExp
                        .getName(), assignParamExp.getArguments());

        // Check for recursive call of itself
        if (myCurrentOperationEntry.getName().equals(opDec.getName())
                && myCurrentOperationEntry.getReturnType() != null) {
            // Create a new confirm statement using P_val and the decreasing clause
            VarExp pVal = createPValExp(myOperationDecreasingExp.getLocation());

            // Create a new infix expression
            InfixExp exp =
                    new InfixExp(stmt.getLocation(), Exp
                            .copy(myOperationDecreasingExp),
                            createPosSymbol("<"), pVal);
            exp.setMathType(BOOLEAN);

            // Create the new confirm statement
            Location loc;
            if (myOperationDecreasingExp.getLocation() != null) {
                loc = (Location) myOperationDecreasingExp.getLocation().clone();
            }
            else {
                loc = (Location) stmt.getLocation().clone();
            }
            loc.setDetails("Show Termination of Recursive Call");
            setLocation(exp, loc);
            ConfirmStmt conf = new ConfirmStmt(loc, exp);

            // Add it to our list of assertions
            myCurrentAssertiveCode.addCode(conf);
        }

        // Get the requires clause for this operation
        Exp requires;
        if (opDec.getRequires() != null) {
            requires = Exp.copy(opDec.getRequires());
        }
        else {
            requires = myTypeGraph.getTrueVarExp();
        }

        // Replace PreCondition variables in the requires clause
        requires =
                replaceFormalWithActualReq(requires, opDec.getParameters(),
                        assignParamExp.getArguments());

        // Modify the location of the requires clause and add it to myCurrentAssertiveCode
        // Obtain the current location
        // Note: If we don't have a location, we create one
        Location reqloc;
        if (assignParamExp.getName().getLocation() != null) {
            reqloc = (Location) assignParamExp.getName().getLocation().clone();
        }
        else {
            reqloc = new Location(null, null);
        }

        // Append the name of the current procedure
        String details = "";
        if (myCurrentOperationEntry != null) {
            details = " in Procedure " + myCurrentOperationEntry.getName();
        }

        // Set the details of the current location
        reqloc.setDetails("Requires Clause of " + opDec.getName() + details);
        setLocation(requires, reqloc);

        // Add this to our list of things to confirm
        myCurrentAssertiveCode.addConfirm(requires);

        // Get the ensures clause for this operation
        // Note: If there isn't an ensures clause, it is set to "True"
        Exp ensures, opEnsures;
        if (opDec.getEnsures() != null) {
            opEnsures = Exp.copy(opDec.getEnsures());

            // Make sure we have an EqualsExp, else it is an error.
            if (opEnsures instanceof EqualsExp) {
                // Has to be a VarExp on the left hand side (containing the name
                // of the function operation)
                if (((EqualsExp) opEnsures).getLeft() instanceof VarExp) {
                    VarExp leftExp = (VarExp) ((EqualsExp) opEnsures).getLeft();

                    // Check if it has the name of the operation
                    if (leftExp.getName().equals(opDec.getName())) {
                        ensures = ((EqualsExp) opEnsures).getRight();

                        // Obtain the current location
                        if (assignParamExp.getName().getLocation() != null) {
                            // Set the details of the current location
                            Location loc =
                                    (Location) assignParamExp.getName()
                                            .getLocation().clone();
                            loc.setDetails("Ensures Clause of "
                                    + opDec.getName());
                            setLocation(ensures, loc);
                        }

                        // Replace all instances of the variable on the left hand side
                        // in the ensures clause with the expression on the right.
                        Exp leftVariable;

                        // We have a variable inside a record as the variable being assigned.
                        if (stmt.getVar() instanceof VariableDotExp) {
                            VariableDotExp v = (VariableDotExp) stmt.getVar();
                            List<VariableExp> vList = v.getSegments();
                            edu.clemson.cs.r2jt.collections.List<Exp> newSegments =
                                    new edu.clemson.cs.r2jt.collections.List<Exp>();

                            // Loot through each variable expression and add it to our dot list
                            for (VariableExp vr : vList) {
                                VarExp varExp = new VarExp();
                                if (vr instanceof VariableNameExp) {
                                    varExp.setName(((VariableNameExp) vr)
                                            .getName());
                                    varExp.setMathType(vr.getMathType());
                                    varExp.setMathTypeValue(vr
                                            .getMathTypeValue());
                                    newSegments.add(varExp);
                                }
                            }

                            // Expression to be replaced
                            leftVariable =
                                    new DotExp(v.getLocation(), newSegments,
                                            null);
                            leftVariable.setMathType(v.getMathType());
                            leftVariable.setMathTypeValue(v.getMathTypeValue());
                        }
                        // We have a regular variable being assigned.
                        else {
                            // Expression to be replaced
                            VariableNameExp v = (VariableNameExp) stmt.getVar();
                            leftVariable =
                                    new VarExp(v.getLocation(), null, v
                                            .getName());
                            leftVariable.setMathType(v.getMathType());
                            leftVariable.setMathTypeValue(v.getMathTypeValue());
                        }

                        // Replace all instances of the left hand side
                        // variable in the current final confirm statement.
                        Exp newConf = myCurrentAssertiveCode.getFinalConfirm();
                        newConf = replace(newConf, leftVariable, ensures);

                        // Replace the formals with the actuals.
                        newConf =
                                replaceFormalWithActualEns(newConf, opDec
                                        .getParameters(), opDec.getStateVars(),
                                        assignParamExp.getArguments(), false);

                        // Set this as our new final confirm statement.
                        myCurrentAssertiveCode.setFinalConfirm(newConf);
                    }
                    else {
                        illegalOperationEnsures(opDec.getLocation());
                    }
                }
                else {
                    illegalOperationEnsures(opDec.getLocation());
                }
            }
            else {
                illegalOperationEnsures(opDec.getLocation());
            }
        }

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nFunction Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");
    }

    /**
     * <p>Applies the if statement rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>IfStmt</code>.
     */
    private void applyEBIfStmtRule(IfStmt stmt) {
        // Note: In the If Rule, we will have two instances of the assertive code.
        // One for when the if condition is true and one for the else condition.
        // The current global assertive code variable is going to be used for the if path,
        // and we are going to create a new assertive code for the else path (this includes
        // the case when there is no else clause).
        ProgramExp ifCondition = stmt.getTest();

        // Negation of If (Need to make a copy before we start modifying
        // the current assertive code for the if part)
        AssertiveCode negIfAssertiveCode =
                new AssertiveCode(myCurrentAssertiveCode);

        // TODO: Might need to take this out when we figure out the evaluates mode business
        // Call a method to locate the operation dec for this call
        PosSymbol qualifier = null;
        ProgramParamExp testParamExp = null;

        // Check to see what kind of expression is on the right hand side
        if (ifCondition instanceof ProgramParamExp) {
            // Cast to a ProgramParamExp
            testParamExp = (ProgramParamExp) ifCondition;
        }
        else if (ifCondition instanceof ProgramDotExp) {
            // Cast to a ProgramParamExp
            ProgramDotExp dotExp = (ProgramDotExp) ifCondition;
            testParamExp = (ProgramParamExp) dotExp.getExp();
            qualifier = dotExp.getQualifier();
        }
        else {
            // TODO: ERROR!
        }
        OperationDec opDec =
                getOperationDec(ifCondition.getLocation(), qualifier,
                        testParamExp.getName(), testParamExp.getArguments());

        // Confirm the invoking condition
        // Get the requires clause for this operation
        Exp requires;
        if (opDec.getRequires() != null) {
            requires = Exp.copy(opDec.getRequires());
        }
        else {
            requires = myTypeGraph.getTrueVarExp();
        }

        // Replace PreCondition variables in the requires clause
        requires =
                replaceFormalWithActualReq(requires, opDec.getParameters(),
                        testParamExp.getArguments());

        // Modify the location of the requires clause and add it to myCurrentAssertiveCode
        // Obtain the current location
        // Note: If we don't have a location, we create one
        Location reqloc;
        if (testParamExp.getName().getLocation() != null) {
            reqloc = (Location) testParamExp.getName().getLocation().clone();
        }
        else {
            reqloc = new Location(null, null);
        }

        // Append the name of the current procedure
        String details = " from If Statement Condition";

        // Set the details of the current location
        reqloc.setDetails("Requires Clause of " + opDec.getName() + details);
        setLocation(requires, reqloc);

        // Add this to our list of things to confirm
        myCurrentAssertiveCode.addConfirm(requires);

        // Add the if condition as the assume clause
        // Get the ensures clause for this operation
        // Note: If there isn't an ensures clause, it is set to "True"
        Exp ensures, negEnsures = null, opEnsures;
        if (opDec.getEnsures() != null) {
            opEnsures = Exp.copy(opDec.getEnsures());

            // Make sure we have an EqualsExp, else it is an error.
            if (opEnsures instanceof EqualsExp) {
                // Has to be a VarExp on the left hand side (containing the name
                // of the function operation)
                if (((EqualsExp) opEnsures).getLeft() instanceof VarExp) {
                    VarExp leftExp = (VarExp) ((EqualsExp) opEnsures).getLeft();

                    // Check if it has the name of the operation
                    if (leftExp.getName().equals(opDec.getName())) {
                        ensures = ((EqualsExp) opEnsures).getRight();

                        // Obtain the current location
                        if (testParamExp.getName().getLocation() != null) {
                            // Set the details of the current location
                            Location loc =
                                    (Location) testParamExp.getName()
                                            .getLocation().clone();
                            loc.setDetails("If Statement Condition");
                            setLocation(ensures, loc);
                        }

                        // Replace the formals with the actuals.
                        ensures =
                                replaceFormalWithActualEns(ensures, opDec
                                        .getParameters(), opDec.getStateVars(),
                                        testParamExp.getArguments(), false);
                        myCurrentAssertiveCode.addAssume(ensures);

                        // Negation of the condition
                        negEnsures = negateExp(ensures);
                    }
                    else {
                        illegalOperationEnsures(opDec.getLocation());
                    }
                }
                else {
                    illegalOperationEnsures(opDec.getLocation());
                }
            }
            else {
                illegalOperationEnsures(opDec.getLocation());
            }
        }

        // Add any statements inside the then clause
        if (stmt.getThenclause() != null) {
            myCurrentAssertiveCode.addStatements(stmt.getThenclause());
        }

        // Modify the confirm details
        Exp ifConfirm = myCurrentAssertiveCode.getFinalConfirm();
        String ifDetail =
                " , If \"if\" condition at "
                        + ifCondition.getLocation().toString() + " is true";
        ifConfirm = appendToLocation(ifConfirm, ifDetail);
        myCurrentAssertiveCode.setFinalConfirm(ifConfirm);

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nIf Part Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");

        // Add the negation of the if condition as the assume clause
        if (negEnsures != null) {
            negIfAssertiveCode.addAssume(negEnsures);
        }
        else {
            illegalOperationEnsures(opDec.getLocation());
        }

        // Add any statements inside the else clause
        if (stmt.getElseclause() != null) {
            negIfAssertiveCode.addStatements(stmt.getElseclause());
        }

        // Modify the confirm details
        Exp negIfConfirm = negIfAssertiveCode.getFinalConfirm();
        String negIfDetail =
                " , If \"if\" condition at "
                        + ifCondition.getLocation().toString() + " is false";
        negIfConfirm = appendToLocation(negIfConfirm, negIfDetail);
        negIfAssertiveCode.setFinalConfirm(negIfConfirm);

        // Add this new assertive code to our incomplete assertive code stack
        myIncAssertiveCodeStack.push(negIfAssertiveCode);

        // Verbose Mode Debug Messages
        String newString = "\nNegation of If Part Rule Applied: \n";
        newString += negIfAssertiveCode.assertionToString();
        newString += "\n_____________________ \n";
        myIncAssertiveCodeStackInfo.push(newString);
    }

    /**
     * <p>Applies the swap statement rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>SwapStmt</code>.
     */
    private void applyEBSwapStmtRule(SwapStmt stmt) {
        // Obtain the current final confirm clause
        Exp conf = myCurrentAssertiveCode.getFinalConfirm();

        // Create a copy of the left and right hand side
        VariableExp stmtLeft = (VariableExp) Exp.copy(stmt.getLeft());
        VariableExp stmtRight = (VariableExp) Exp.copy(stmt.getRight());

        // New left and right
        Exp newLeft = convertExp(stmtLeft);
        Exp newRight = convertExp(stmtRight);

        // Use our final confirm to obtain the math types
        List lst = conf.getSubExpressions();
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i) instanceof VarExp) {
                VarExp thisExp = (VarExp) lst.get(i);
                if (newRight instanceof VarExp) {
                    if (thisExp.getName().equals(
                            ((VarExp) newRight).getName().getName())) {
                        newRight.setMathType(thisExp.getMathType());
                        newRight.setMathTypeValue(thisExp.getMathTypeValue());
                    }
                }
                if (newLeft instanceof VarExp) {
                    if (thisExp.getName().equals(
                            ((VarExp) newLeft).getName().getName())) {
                        newLeft.setMathType(thisExp.getMathType());
                        newLeft.setMathTypeValue(thisExp.getMathTypeValue());
                    }
                }
            }
        }

        // Temp variable
        VarExp tmp = new VarExp();
        tmp.setName(createPosSymbol("_" + getVarName(stmtLeft).getName()));
        tmp.setMathType(stmtLeft.getMathType());
        tmp.setMathTypeValue(stmtLeft.getMathTypeValue());

        // Replace according to the swap rule
        conf = replace(conf, newRight, tmp);
        conf = replace(conf, newLeft, newRight);
        conf = replace(conf, tmp, newLeft);

        // Set this new expression as the new final confirm
        myCurrentAssertiveCode.setFinalConfirm(conf);

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nSwap Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");
    }

    /**
     * <p>Applies the while statement rule.</p>
     *
     * @param stmt Our current <code>WhileStmt</code>.
     */
    private void applyEBWhileStmtRule(WhileStmt stmt) {
        // Obtain the loop invariant
        Exp invariant;
        if (stmt.getMaintaining() != null) {
            invariant = Exp.copy(stmt.getMaintaining());
            invariant.setMathType(stmt.getMaintaining().getMathType());
        }
        else {
            invariant = myTypeGraph.getTrueVarExp();
        }

        // Confirm the base case of invariant
        Exp baseCase = Exp.copy(invariant);
        Location baseLoc;
        if (invariant.getLocation() != null) {
            baseLoc = (Location) invariant.getLocation().clone();
        }
        else {
            baseLoc = (Location) stmt.getLocation().clone();
        }
        baseLoc.setDetails("Base Case of the Invariant of While Statement");
        setLocation(baseCase, baseLoc);
        myCurrentAssertiveCode.addConfirm(baseCase);

        // Add the change rule
        if (stmt.getChanging() != null) {
            myCurrentAssertiveCode.addChange(stmt.getChanging());
        }

        // Assume the invariant and NQV(RP, P_Val) = P_Exp
        Location whileLoc = stmt.getLocation();
        Exp assume;
        Exp finalConfirm = myCurrentAssertiveCode.getFinalConfirm();
        Exp decreasingExp = stmt.getDecreasing();
        Exp nqv;

        if (decreasingExp != null) {
            VarExp pval = createPValExp((Location) whileLoc.clone());
            nqv = createQuestionMarkVariable(finalConfirm, pval);
            nqv.setMathType(pval.getMathType());
            Exp equalPExp =
                    new EqualsExp((Location) whileLoc.clone(), Exp.copy(nqv),
                            1, Exp.copy(decreasingExp));
            equalPExp.setMathType(BOOLEAN);
            assume = myTypeGraph.formConjunct(Exp.copy(invariant), equalPExp);
        }
        else {
            decreasingExp = myTypeGraph.getTrueVarExp();
            nqv = myTypeGraph.getTrueVarExp();
            assume = Exp.copy(invariant);
        }

        myCurrentAssertiveCode.addAssume(assume);

        // if statement body
        edu.clemson.cs.r2jt.collections.List<Statement> ifStmtList =
                stmt.getStatements();

        // Confirm the inductive case of invariant
        Exp inductiveCase = Exp.copy(invariant);
        Location inductiveLoc;
        if (invariant.getLocation() != null) {
            inductiveLoc = (Location) invariant.getLocation().clone();
        }
        else {
            inductiveLoc = (Location) stmt.getLocation().clone();
        }
        inductiveLoc
                .setDetails("Inductive Case of Invariant of While Statement");
        setLocation(inductiveCase, inductiveLoc);
        ifStmtList.add(new ConfirmStmt(inductiveLoc, inductiveCase));

        // Confirm the termination of the loop.
        if (decreasingExp != null) {
            Location decreasingLoc =
                    (Location) decreasingExp.getLocation().clone();
            if (decreasingLoc != null) {
                decreasingLoc.setDetails("Termination of While Statement");
            }

            Exp infixExp =
                    new InfixExp(decreasingLoc, Exp.copy(decreasingExp),
                            createPosSymbol("<"), Exp.copy(nqv));
            infixExp.setMathType(BOOLEAN);
            ifStmtList.add(new ConfirmStmt(decreasingLoc, infixExp));
        }

        // empty elseif pair
        edu.clemson.cs.r2jt.collections.List<ConditionItem> elseIfPairList =
                new edu.clemson.cs.r2jt.collections.List<ConditionItem>();

        // else body
        Location elseConfirmLoc;
        if (finalConfirm.getLocation() != null) {
            elseConfirmLoc = (Location) finalConfirm.getLocation().clone();
        }
        else {
            elseConfirmLoc = (Location) whileLoc.clone();
        }
        edu.clemson.cs.r2jt.collections.List<Statement> elseStmtList =
                new edu.clemson.cs.r2jt.collections.List<Statement>();
        elseStmtList
                .add(new ConfirmStmt(elseConfirmLoc, Exp.copy(finalConfirm)));

        // condition
        ProgramExp condition = (ProgramExp) Exp.copy(stmt.getTest());
        if (condition.getLocation() != null) {
            Location condLoc = (Location) condition.getLocation().clone();
            condLoc.setDetails("While Loop Condition");
            setLocation(condition, condLoc);
        }

        // add it back to your assertive code
        IfStmt newIfStmt =
                new IfStmt(condition, ifStmtList, elseIfPairList, elseStmtList);
        myCurrentAssertiveCode.addCode(newIfStmt);

        // Change our final confirm to "True"
        Exp trueVarExp = myTypeGraph.getTrueVarExp();
        trueVarExp.setLocation((Location) whileLoc.clone());
        myCurrentAssertiveCode.setFinalConfirm(trueVarExp);

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nWhile Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");
    }

    /**
     * <p>Applies the facility declaration rule.</p>
     *
     * @param dec Facility declaration object.
     */
    private void applyFacilityDeclRule(FacilityDec dec) {
        // Create a new assertive code to hold the facility declaration VCs
        AssertiveCode assertiveCode = new AssertiveCode(myInstanceEnvironment);

        // Add the global constraints as given
        assertiveCode.addAssume(myGlobalConstraintExp);

        // Add the global require clause as given
        assertiveCode.addAssume(myGlobalRequiresExp);

        // Loop through every enhancement/enhancement realization declaration,
        // if any.
        /*if (dec.getEnhancementBodies() != null) {
            for (EnhancementBodyItem e : dec.getEnhancementBodies()) {

            }
        }*/

        // Obtain the concept module for the facility
        try {
            ConceptModuleDec facConceptDec =
                    (ConceptModuleDec) mySymbolTable
                            .getModuleScope(
                                    new ModuleIdentifier(dec.getConceptName()
                                            .getName())).getDefiningElement();

            // Concept requires clause
            Exp req = getRequiresClause(facConceptDec);
            Location loc =
                    (Location) dec.getConceptName().getLocation().clone();
            loc.setDetails("Facility Declaration Rule");

            req =
                    replaceFacilityDeclarationVariables(req, facConceptDec
                            .getParameters(), dec.getConceptParams());
            req.setLocation(loc);
            assertiveCode.setFinalConfirm(req);

            // TODO: Add the parameters to the facility concept to the free variable list
        }
        catch (NoSuchSymbolException e) {
            noSuchModule(dec.getLocation());
        }

        // Add this new assertive code to our incomplete assertive code stack
        myIncAssertiveCodeStack.push(assertiveCode);

        // Verbose Mode Debug Messages
        String newString =
                "\n========================= Facility Dec Name:\t"
                        + dec.getName().getName()
                        + " =========================\n";
        newString += "\nFacility Declaration Rule Applied: \n";
        newString += assertiveCode.assertionToString();
        newString += "\n_____________________ \n";
        myIncAssertiveCodeStackInfo.push(newString);
    }

    /**
     * <p>Applies the procedure declaration rule.</p>
     *
     * @param requires Requires clause
     * @param ensures Ensures clause
     * @param decreasing Decreasing clause (if any)
     * @param variableList List of all variables for this procedure
     * @param statementList List of statements for this procedure
     */
    private void applyProcedureDeclRule(Exp requires, Exp ensures,
            Exp decreasing, List<VarDec> variableList,
            List<Statement> statementList) {
        // Add the global requires clause
        if (myGlobalRequiresExp != null) {
            myCurrentAssertiveCode.addAssume(myGlobalRequiresExp);
        }

        // Add the global constraints
        if (myGlobalConstraintExp != null) {
            myCurrentAssertiveCode.addAssume(myGlobalConstraintExp);
        }

        // Add the requires clause
        if (requires != null) {
            myCurrentAssertiveCode.addAssume(requires);
        }

        // Add the remember rule
        myCurrentAssertiveCode.addRemember();

        // Add declared variables into the assertion. Also add
        // them to the list of free variables.
        myCurrentAssertiveCode.addVariableDecs(variableList);
        addVarDecsAsFreeVars(variableList);

        // Check to see if we have a recursive procedure.
        // If yes, we will need to create an additional assume clause
        // (P_val = (decreasing clause)) in our list of assertions.
        if (decreasing != null) {
            // Store for future use
            myOperationDecreasingExp = decreasing;

            // Add P_val as a free variable
            VarExp pVal = createPValExp(decreasing.getLocation());
            myCurrentAssertiveCode.addFreeVar(pVal);

            // Create an equals expression
            EqualsExp equalsExp =
                    new EqualsExp(null, pVal, EqualsExp.EQUAL, Exp
                            .copy(decreasing));
            equalsExp.setMathType(BOOLEAN);
            Location eqLoc = (Location) decreasing.getLocation().clone();
            eqLoc.setDetails("Progress Metric for Recursive Procedure");
            setLocation(equalsExp, eqLoc);

            // Add it to our things to assume
            myCurrentAssertiveCode.addAssume(equalsExp);
        }

        // Add the list of statements
        myCurrentAssertiveCode.addStatements(statementList);

        // Add the final confirms clause
        myCurrentAssertiveCode.setFinalConfirm(ensures);

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nProcedure Declaration Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");
    }

    /**
     * <p>Applies the remember rule.</p>
     */
    private void applyRememberRule() {
        // Obtain the final confirm and apply the remember method for Exp
        Exp conf = myCurrentAssertiveCode.getFinalConfirm();
        conf = conf.remember();
        myCurrentAssertiveCode.setFinalConfirm(conf);

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nRemember Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");
    }

    /**
     * <p>Applies the variable declaration rule.</p>
     *
     * @param var A declared variable stored as a
     *            <code>VerificationStatement</code>
     */
    private void applyVarDeclRule(VerificationStatement var) {
        // Obtain the variable from the verification statement
        VarDec varDec = (VarDec) var.getAssertion();
        ProgramTypeEntry typeEntry;

        // Ty is NameTy
        if (varDec.getTy() instanceof NameTy) {
            NameTy pNameTy = (NameTy) varDec.getTy();

            // Query for the type entry in the symbol table
            typeEntry =
                    searchProgramType(pNameTy.getLocation(), pNameTy.getName());

            // Make sure we don't have a generic type
            if (typeEntry.getDefiningElement() instanceof TypeDec) {
                // Obtain the original dec from the AST
                TypeDec type = (TypeDec) typeEntry.getDefiningElement();

                // Create a variable expression from the declared variable
                VarExp varDecExp =
                        createVarExp(varDec.getLocation(), varDec.getName(),
                                typeEntry.getModelType());

                // Create a variable expression from the type exemplar
                VarExp exemplar =
                        createVarExp(type.getLocation(), type.getExemplar(),
                                typeEntry.getModelType());

                // Deep copy the original initialization ensures
                Exp init = Exp.copy(type.getInitialization().getEnsures());
                init = replace(init, exemplar, varDecExp);

                // Make sure we have a constraint
                Exp constraint;
                if (type.getConstraint() == null) {
                    constraint = myTypeGraph.getTrueVarExp();
                }
                else {
                    constraint = Exp.copy(type.getConstraint());
                }
                constraint = replace(constraint, exemplar, varDecExp);

                // Set the location for the constraint
                Location loc;
                if (constraint.getLocation() != null) {
                    loc = (Location) constraint.getLocation().clone();
                }
                else {
                    loc = (Location) type.getLocation().clone();
                }
                loc.setDetails("Constraints on " + varDec.getName().getName());
                setLocation(constraint, loc);

                // Final confirm clause
                Exp finalConfirm = myCurrentAssertiveCode.getFinalConfirm();

                // Obtain the string form of the variable
                String varName = varDec.getName().getName();

                // Check to see if we have a variable dot expression.
                // If we do, we will need to extract the name.
                int dotIndex = varName.indexOf(".");
                if (dotIndex > 0) {
                    varName = varName.substring(0, dotIndex);
                }

                // Check if our confirm clause uses this variable
                if (finalConfirm.containsVar(varName, false)) {
                    Exp exp;
                    // We don't have any constraints, so add the initialization
                    // clause as a new assume clause.
                    if (constraint.equals(myTypeGraph.getTrueVarExp())) {
                        exp = init;
                    }
                    // We actually have a constraint, so add both the initialization
                    // and constraint as a new assume clause.
                    else {
                        exp = myTypeGraph.formConjunct(constraint, init);
                    }

                    // Add the new assume clause to our assertive code.
                    myCurrentAssertiveCode.addAssume(exp);
                }
            }
            // Since the type is generic, we can only use the is_initial predicate
            // to ensure that the value is initial value.
            else {
                // Obtain the original dec from the AST
                Location varLoc = varDec.getLocation();

                // Create an is_initial dot expression
                DotExp isInitialExp = createInitExp(varDec);
                if (varLoc != null) {
                    Location loc = (Location) varLoc.clone();
                    loc.setDetails("Initial Value for "
                            + varDec.getName().getName());
                    setLocation(isInitialExp, loc);
                }

                // Add to our assertive code as an assume
                myCurrentAssertiveCode.addAssume(isInitialExp);
            }

            // Verbose Mode Debug Messages
            myVCBuffer.append("\nVariable Declaration Rule Applied: \n");
            myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
            myVCBuffer.append("\n_____________________ \n");
        }
        else {
            // Ty not handled.
            tyNotHandled(varDec.getTy(), varDec.getLocation());
        }
    }
}