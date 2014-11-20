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
import edu.clemson.cs.r2jt.rewriteprover.VC;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.*;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.misc.Flag;
import edu.clemson.cs.r2jt.misc.FlagDependencies;

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
    private ModuleScope myCurrentModuleScope;

    // Module level global variables
    private Exp myGlobalRequiresExp;
    private Exp myGlobalConstraintExp;

    // Conventions/Correspondence
    private Exp myConventionExp;
    private Exp myCorrespondenceExp;

    // Operation/Procedure level global variables
    private OperationEntry myCurrentOperationEntry;
    private OperationProfileEntry myCurrentOperationProfileEntry;
    private Exp myOperationDecreasingExp;

    /**
     * <p>The current assertion we are applying
     * VC rules to.</p>
     */
    private AssertiveCode myCurrentAssertiveCode;

    /**
     * <p>A map of facility declarations to <code>Exp</code>, where the expression
     * contains the things we can assume from the facility declaration.</p>
     */
    private Map<FacilityDec, Exp> myFacilityDeclarationMap;

    // TODO: Change this!
    /**
     * <p>A map of facility declarations to a list of formal and actual constraints.</p>
     */
    private Map<FacilityDec, List<EqualsExp>> myFacilityFormalActualMap;

    /**
     * <p>A list that will be built up with <code>AssertiveCode</code>
     * objects, each representing a VC or group of VCs that must be
     * satisfied to verify a parsed program.</p>
     */
    private Collection<AssertiveCode> myFinalAssertiveCodeList;

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
     * <p>The current compile environment used throughout
     * the compiler.</p>
     */
    private CompileEnvironment myInstanceEnvironment;

    /**
     * <p>This object creates the different VC outputs.</p>
     */
    private OutputVCs myOutputGenerator;

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
    private static final String FLAG_DESC_ATTPVCS_VC =
            "Generate Performance VCs";

    // ===========================================================
    // Flags
    // ===========================================================

    public static final Flag FLAG_ALTVERIFY_VC =
            new Flag(FLAG_ALTSECTION_NAME, "altVCs", FLAG_DESC_ATLVERIFY_VC);

    public static final Flag FLAG_ALTPVCS_VC =
            new Flag(FLAG_ALTSECTION_NAME, "PVCs", FLAG_DESC_ATTPVCS_VC);

    public static final void setUpFlags() {
        FlagDependencies.addImplies(FLAG_ALTPVCS_VC, FLAG_ALTVERIFY_VC);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    public VCGenerator(ScopeRepository table, final CompileEnvironment env) {
        // Symbol table items
        mySymbolTable = (MathSymbolTableBuilder) table;
        myTypeGraph = mySymbolTable.getTypeGraph();
        BOOLEAN = myTypeGraph.BOOLEAN;
        MTYPE = myTypeGraph.CLS;

        // Current items
        myConventionExp = null;
        myCorrespondenceExp = null;
        myCurrentModuleScope = null;
        myCurrentOperationEntry = null;
        myCurrentOperationProfileEntry = null;
        myGlobalConstraintExp = null;
        myGlobalRequiresExp = null;
        myOperationDecreasingExp = null;

        // Instance Environment
        myInstanceEnvironment = env;

        // VCs + Debugging String
        myCurrentAssertiveCode = null;
        myFacilityDeclarationMap = new HashMap<FacilityDec, Exp>();
        myFacilityFormalActualMap = new HashMap<FacilityDec, List<EqualsExp>>();
        myFinalAssertiveCodeList = new LinkedList<AssertiveCode>();
        myIncAssertiveCodeStack = new Stack<AssertiveCode>();
        myIncAssertiveCodeStackInfo = new Stack<String>();
        myOutputGenerator = null;
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
            myGlobalRequiresExp = getRequiresClause(dec.getLocation(), dec);

            // Obtain the global requires clause from the Concept
            ConceptModuleDec conceptModuleDec =
                    (ConceptModuleDec) mySymbolTable
                            .getModuleScope(
                                    new ModuleIdentifier(dec.getConceptName()
                                            .getName())).getDefiningElement();
            Exp conceptRequires =
                    getRequiresClause(conceptModuleDec.getLocation(),
                            conceptModuleDec);
            if (!conceptRequires.isLiteralTrue()) {
                if (myGlobalRequiresExp.isLiteralTrue()) {
                    myGlobalRequiresExp = conceptRequires;
                }
                else {
                    myGlobalRequiresExp =
                            myTypeGraph.formConjunct(myGlobalRequiresExp,
                                    conceptRequires);
                }
            }
        }
        catch (NoSuchSymbolException e) {
            System.err.println("Module " + dec.getName()
                    + " does not exist or is not in scope.");
            Utilities.noSuchModule(dec.getLocation());
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
            myGlobalRequiresExp = getRequiresClause(dec.getLocation(), dec);

            // Obtain the global requires clause from the Concept
            ConceptModuleDec conceptModuleDec =
                    (ConceptModuleDec) mySymbolTable
                            .getModuleScope(
                                    new ModuleIdentifier(dec.getConceptName()
                                            .getName())).getDefiningElement();
            Exp conceptRequires =
                    getRequiresClause(conceptModuleDec.getLocation(),
                            conceptModuleDec);
            if (!conceptRequires.isLiteralTrue()) {
                if (myGlobalRequiresExp.isLiteralTrue()) {
                    myGlobalRequiresExp = conceptRequires;
                }
                else {
                    myGlobalRequiresExp =
                            myTypeGraph.formConjunct(myGlobalRequiresExp,
                                    conceptRequires);
                }
            }

            // Obtain the global requires clause from the Enhancement
            EnhancementModuleDec enhancementModuleDec =
                    (EnhancementModuleDec) mySymbolTable.getModuleScope(
                            new ModuleIdentifier(dec.getEnhancementName()
                                    .getName())).getDefiningElement();
            Exp enhancementRequires =
                    getRequiresClause(enhancementModuleDec.getLocation(),
                            enhancementModuleDec);
            if (!enhancementRequires.isLiteralTrue()) {
                if (myGlobalRequiresExp.isLiteralTrue()) {
                    myGlobalRequiresExp = enhancementRequires;
                }
                else {
                    myGlobalRequiresExp =
                            myTypeGraph.formConjunct(myGlobalRequiresExp,
                                    enhancementRequires);
                }
            }
        }
        catch (NoSuchSymbolException e) {
            System.err.println("Module " + dec.getName()
                    + " does not exist or is not in scope.");
            Utilities.noSuchModule(dec.getLocation());
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
            myGlobalRequiresExp = getRequiresClause(dec.getLocation(), dec);
        }
        catch (NoSuchSymbolException e) {
            System.err.println("Module " + dec.getName()
                    + " does not exist or is not in scope.");
            Utilities.noSuchModule(dec.getLocation());
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
                Utilities.searchOperation(dec.getLocation(), null, dec
                        .getName(), argTypes, myCurrentModuleScope);
        // Obtain the performance duration clause
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
            myCurrentOperationProfileEntry =
                    Utilities.searchOperationProfile(dec.getLocation(), null,
                            dec.getName(), argTypes, myCurrentModuleScope);
        }
    }

    @Override
    public void postFacilityOperationDec(FacilityOperationDec dec) {
        // Verbose Mode Debug Messages
        myVCBuffer.append("\n=========================");
        myVCBuffer.append(" Procedure: ");
        myVCBuffer.append(dec.getName().getName());
        myVCBuffer.append(" =========================\n");

        // The current assertive code
        myCurrentAssertiveCode = new AssertiveCode(myInstanceEnvironment, dec);

        // Obtains items from the current operation
        Location loc = dec.getLocation();
        String name = dec.getName().getName();
        boolean isLocal =
                Utilities.isLocationOperation(dec.getName().getName(),
                        myCurrentModuleScope);
        Exp requires =
                modifyRequiresClause(getRequiresClause(loc, dec), loc, name,
                        isLocal);
        Exp ensures =
                modifyEnsuresClause(getEnsuresClause(loc, dec), loc, name,
                        isLocal);
        List<Statement> statementList = dec.getStatements();
        List<VarDec> variableList = dec.getAllVariables();
        Exp decreasing = dec.getDecreasing();
        Exp procDur = null;
        Exp varFinalDur = null;

        // Obtain type constrains from parameter
        // TODO: Only add type constraints if they use the facility;
        Exp typeConstraint = null;
        for (FacilityDec fDec : myFacilityDeclarationMap.keySet()) {
            Exp temp = Exp.copy(myFacilityDeclarationMap.get(fDec));

            if (typeConstraint == null) {
                typeConstraint = temp;
            }
            else {
                typeConstraint = myTypeGraph.formConjunct(typeConstraint, temp);
            }
        }

        // NY YS
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
            procDur = myCurrentOperationProfileEntry.getDurationClause();

            // Loop through local variables to get their finalization duration
            for (VarDec v : dec.getVariables()) {
                Exp finalVarDur =
                        Utilities.createFinalizAnyDur(v, myTypeGraph.R);

                // Create/Add the duration expression
                if (varFinalDur == null) {
                    varFinalDur = finalVarDur;
                }
                else {
                    varFinalDur =
                            new InfixExp((Location) loc.clone(), varFinalDur,
                                    Utilities.createPosSymbol("+"), finalVarDur);
                }
                varFinalDur.setMathType(myTypeGraph.R);
            }

            // Loop through parameter variables to get their finalization duration
            // (if they are evaluates mode)
            for (ParameterVarDec p : dec.getParameters()) {
                if (p.getMode() == Mode.EVALUATES) {
                    VarDec varDec = new VarDec(p.getName(), p.getTy());
                    varDec.setMathType(p.getMathType());
                    Exp finalVarDur =
                            Utilities
                                    .createFinalizAnyDur(varDec, myTypeGraph.R);

                    // Create/Add the duration expression
                    if (varFinalDur == null) {
                        varFinalDur = finalVarDur;
                    }
                    else {
                        varFinalDur =
                                new InfixExp((Location) loc.clone(),
                                        varFinalDur, Utilities
                                                .createPosSymbol("+"),
                                        finalVarDur);
                    }
                    varFinalDur.setMathType(myTypeGraph.R);
                }
            }
        }

        // Apply the procedure declaration rule
        applyProcedureDeclRule(loc, name, requires, ensures, decreasing,
                procDur, varFinalDur, typeConstraint, variableList,
                statementList, isLocal);

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
        myCurrentOperationProfileEntry = null;
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
                Utilities.searchOperation(dec.getLocation(), null, dec
                        .getName(), argTypes, myCurrentModuleScope);
        // Obtain the performance duration clause
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
            myCurrentOperationProfileEntry =
                    Utilities.searchOperationProfile(dec.getLocation(), null,
                            dec.getName(), argTypes, myCurrentModuleScope);
        }
    }

    @Override
    public void postProcedureDec(ProcedureDec dec) {
        // Verbose Mode Debug Messages
        myVCBuffer.append("\n=========================");
        myVCBuffer.append(" Procedure: ");
        myVCBuffer.append(dec.getName().getName());
        myVCBuffer.append(" =========================\n");

        // The current assertive code
        myCurrentAssertiveCode = new AssertiveCode(myInstanceEnvironment, dec);

        // Obtains items from the current operation
        OperationDec opDec =
                (OperationDec) myCurrentOperationEntry.getDefiningElement();
        Location loc = dec.getLocation();
        String name = dec.getName().getName();
        boolean isLocal =
                Utilities.isLocationOperation(dec.getName().getName(),
                        myCurrentModuleScope);
        Exp requires =
                modifyRequiresClause(getRequiresClause(loc, opDec), loc, name,
                        isLocal);
        Exp ensures =
                modifyEnsuresClause(getEnsuresClause(loc, opDec), loc, name,
                        isLocal);
        List<Statement> statementList = dec.getStatements();
        List<VarDec> variableList = dec.getAllVariables();
        Exp decreasing = dec.getDecreasing();
        Exp procDur = null;
        Exp varFinalDur = null;

        // Obtain type constrains from parameter
        // TODO: Only add type constraints if they use the facility;
        Exp facTypeConstraint = null;
        for (FacilityDec fDec : myFacilityDeclarationMap.keySet()) {
            Exp temp = Exp.copy(myFacilityDeclarationMap.get(fDec));

            if (facTypeConstraint == null) {
                facTypeConstraint = temp;
            }
            else {
                facTypeConstraint =
                        myTypeGraph.formConjunct(facTypeConstraint, temp);
            }
        }

        // NY YS
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
            procDur = myCurrentOperationProfileEntry.getDurationClause();

            // Loop through local variables to get their finalization duration
            for (VarDec v : dec.getVariables()) {
                Exp finalVarDur = Utilities.createFinalizAnyDur(v, BOOLEAN);

                // Create/Add the duration expression
                if (varFinalDur == null) {
                    varFinalDur = finalVarDur;
                }
                else {
                    varFinalDur =
                            new InfixExp((Location) loc.clone(), varFinalDur,
                                    Utilities.createPosSymbol("+"), finalVarDur);
                }
                varFinalDur.setMathType(myTypeGraph.R);
            }

            // Loop through parameter variables to get their finalization duration
            // (if they are evaluates mode)
            for (ParameterVarDec p : dec.getParameters()) {
                if (p.getMode() == Mode.EVALUATES) {
                    VarDec varDec = new VarDec(p.getName(), p.getTy());
                    varDec.setMathType(p.getMathType());
                    Exp finalVarDur =
                            Utilities.createFinalizAnyDur(varDec, BOOLEAN);

                    // Create/Add the duration expression
                    if (varFinalDur == null) {
                        varFinalDur = finalVarDur;
                    }
                    else {
                        varFinalDur =
                                new InfixExp((Location) loc.clone(),
                                        varFinalDur, Utilities
                                                .createPosSymbol("+"),
                                        finalVarDur);
                    }
                    varFinalDur.setMathType(myTypeGraph.R);
                }
            }
        }

        // Apply the procedure declaration rule
        applyProcedureDeclRule(loc, name, requires, ensures, decreasing,
                procDur, varFinalDur, facTypeConstraint, variableList,
                statementList, isLocal);

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
        myCurrentOperationProfileEntry = null;
    }

    // -----------------------------------------------------------
    // RepresentationDec
    // -----------------------------------------------------------

    @Override
    public void postRepresentationDec(RepresentationDec dec) {
        // Applies the initialization rule
        applyInitializationRule(dec);

        // Applies the correspondence rule
        applyCorrespondenceRule(dec);

        // Loop through assertive code stack
        loopAssertiveCodeStack();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

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
    private void addVarDecsAsFreeVars(List<VarDec> variableList) {
        // Loop through the variable list
        for (VarDec v : variableList) {
            myCurrentAssertiveCode.addFreeVar(Utilities.createVarExp(v
                    .getLocation(), null, v.getName(), v.getTy()
                    .getMathTypeValue(), null));
        }
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
        List<String> importedConceptName = new LinkedList<String>();

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

                if (dec instanceof ConceptModuleDec
                        && !importedConceptName.contains(dec.getName()
                                .getName())) {
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
                            Utilities.setLocation(constraint, theLoc);
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

                    // Avoid importing constraints for the same concept twice
                    importedConceptName.add(dec.getName().getName());
                }
            }
            catch (NoSuchSymbolException e) {
                System.err.println("Module " + mi.toString()
                        + " does not exist or is not in scope.");
                Utilities.noSuchModule(loc);
            }
        }

        return retExp;
    }

    /**
     * <p>Returns the ensures clause for the current <code>Dec</code>.</p>
     *
     * @param location The location of the ensures clause.
     * @param dec The corresponding <code>Dec</code>.
     *
     * @return The ensures clause <code>Exp</code>.
     */
    private Exp getEnsuresClause(Location location, Dec dec) {
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
            retExp = myTypeGraph.getTrueVarExp();
        }

        if (retExp.getLocation() != null) {
            Location loc = (Location) location.clone();
            loc.setDetails("Ensures Clause of " + name);
            Utilities.setLocation(retExp, loc);
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
     * <p>Returns the requires clause for the current <code>Dec</code>.</p>
     *
     * @param location The location of the requires clause.
     * @param dec The corresponding <code>Dec</code>.
     *
     * @return The requires clause <code>Exp</code>.
     */
    private Exp getRequiresClause(Location location, Dec dec) {
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
            retExp = myTypeGraph.getTrueVarExp();
        }

        if (location != null) {
            Location loc = (Location) location.clone();
            loc.setDetails("Requires Clause for " + name);
            Utilities.setLocation(retExp, loc);
        }

        return retExp;
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
            applyRules();

            myVCBuffer.append("\n***********************");
            myVCBuffer.append("***********************\n");

            // Add it to our list of final assertive codes if we don't have confirm true
            // as our goal.
            if (!myCurrentAssertiveCode.getFinalConfirm().getAssertion()
                    .isLiteralTrue()) {
                myFinalAssertiveCodeList.add(myCurrentAssertiveCode);
            }

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
     * @param isLocal True if it is a local operation, false otherwise.
     *
     * @return The modified ensures clause <code>Exp</code>.
     */
    private Exp modifyEnsuresByParameter(Exp ensures, Location opLocation,
            String opName, List<ParameterVarDec> parameterVarDecList,
            boolean isLocal) {
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

                // Query for the type entry in the symbol table
                SymbolTableEntry ste =
                        Utilities.searchProgramType(pNameTy.getLocation(),
                                pNameTy.getQualifier(), pNameTy.getName(),
                                myCurrentModuleScope);

                ProgramTypeEntry typeEntry;
                if (ste instanceof ProgramTypeEntry) {
                    typeEntry = ste.toProgramTypeEntry(pNameTy.getLocation());
                }
                else {
                    typeEntry =
                            ste
                                    .toRepresentationTypeEntry(
                                            pNameTy.getLocation())
                                    .getDefiningTypeEntry();
                }

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
                        Location newEnsuresLoc =
                                (Location) ensures.getLocation().clone();
                        ensures = myTypeGraph.formConjunct(ensures, equalsExp);
                        ensures.setLocation(newEnsuresLoc);
                    }
                    // Make new expression the ensures clause
                    else {
                        ensures = equalsExp;
                    }
                }
                // Clears mode
                else if (p.getMode() == Mode.CLEARS) {
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
                        init = Utilities.replace(init, exemplar, parameterExp);

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
                                Utilities.createInitExp(new VarDec(p.getName(),
                                        p.getTy()), MTYPE, BOOLEAN);
                        if (varLoc != null) {
                            Location loc = (Location) varLoc.clone();
                            loc.setDetails("Initial Value for "
                                    + p.getName().getName());
                            Utilities.setLocation(init, loc);
                        }
                    }

                    // Create an AND infix expression with the ensures clause
                    if (ensures != null
                            && !ensures.equals(myTypeGraph.getTrueVarExp())) {
                        Location newEnsuresLoc =
                                (Location) ensures.getLocation().clone();
                        ensures = myTypeGraph.formConjunct(ensures, init);
                        ensures.setLocation(newEnsuresLoc);
                    }
                    // Make initialization expression the ensures clause
                    else {
                        ensures = init;
                    }
                }

                // If the type is a type representation, then our requires clause
                // should really say something about the conceptual type and not
                // the variable
                if (ste instanceof RepresentationTypeEntry && !isLocal) {
                    Exp conceptualExp =
                            Utilities.createConcVarExp(opLocation,
                                    parameterExp, parameterExp.getMathType(),
                                    BOOLEAN);
                    OldExp oldConceptualExp =
                            new OldExp(opLocation, Exp.copy(conceptualExp));
                    ensures =
                            Utilities.replace(ensures, parameterExp,
                                    conceptualExp);
                    ensures =
                            Utilities.replace(ensures, oldParameterExp,
                                    oldConceptualExp);
                }
            }
            else {
                // Ty not handled.
                Utilities.tyNotHandled(p.getTy(), p.getLocation());
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
     * @param isLocal True if it is a local operation, false otherwise.
     *
     * @return The modified ensures clause <code>Exp</code>.
     */
    private Exp modifyEnsuresClause(Exp ensures, Location opLocation,
            String opName, boolean isLocal) {
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
                        parameterVarDecList, isLocal);

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
     * @param isLocal True if it is a local operation, false otherwise.
     *
     * @return The modified requires clause <code>Exp</code>.
     */
    private Exp modifyRequiresByParameter(Exp requires, Location opLocation,
            String opName, boolean isLocal) {
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
                PTType ptType = pNameTy.getProgramTypeValue();

                // Only deal with actual types and don't deal
                // with entry types passed in to the concept realization
                if (!(ptType instanceof PTGeneric)) {
                    // Convert p to a VarExp
                    VarExp parameterExp = new VarExp(null, null, p.getName());
                    parameterExp.setMathType(pNameTy.getMathTypeValue());

                    // Query for the type entry in the symbol table
                    SymbolTableEntry ste =
                            Utilities.searchProgramType(pNameTy.getLocation(),
                                    pNameTy.getQualifier(), pNameTy.getName(),
                                    myCurrentModuleScope);

                    if (ste instanceof ProgramTypeEntry) {
                        typeEntry =
                                ste.toProgramTypeEntry(pNameTy.getLocation());
                    }
                    else {
                        typeEntry =
                                ste.toRepresentationTypeEntry(
                                        pNameTy.getLocation())
                                        .getDefiningTypeEntry();
                    }

                    // Obtain the original dec from the AST
                    VarExp exemplar = null;
                    Exp init = null;
                    Exp constraint = null;
                    if (typeEntry.getDefiningElement() instanceof TypeDec) {
                        TypeDec type = (TypeDec) typeEntry.getDefiningElement();

                        // Obtain the exemplar in VarExp form
                        exemplar = new VarExp(null, null, type.getExemplar());
                        exemplar.setMathType(pNameTy.getMathTypeValue());

                        // If we have a type representation, then there are no initialization
                        // or constraint clauses.
                        if (ste instanceof ProgramTypeEntry) {
                            // Deep copy the original initialization ensures and the constraint
                            init =
                                    Exp.copy(type.getInitialization()
                                            .getEnsures());
                            constraint = Exp.copy(type.getConstraint());
                        }
                    }

                    // Only worry about replaces mode parameters
                    if (p.getMode() == Mode.REPLACES && init != null) {
                        // Replace the formal with the actual
                        if (exemplar != null) {
                            init =
                                    Utilities.replace(init, exemplar,
                                            parameterExp);
                        }

                        // Set the details for the new location
                        if (init.getLocation() != null) {
                            Location initLoc;
                            if (requires != null
                                    && requires.getLocation() != null) {
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
                                initLoc.setDetails("Requires Clause of "
                                        + opName + details);
                            }
                            initLoc.setDetails(initLoc.getDetails()
                                    + " (Assumption from \""
                                    + p.getMode().getModeName()
                                    + "\" parameter mode)");
                            init.setLocation(initLoc);
                        }

                        // Create an AND infix expression with the requires clause
                        if (requires != null
                                && !requires
                                        .equals(myTypeGraph.getTrueVarExp())) {
                            requires = myTypeGraph.formConjunct(requires, init);
                            requires.setLocation((Location) opLocation.clone());
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
                            // Replace the formal with the actual
                            if (exemplar != null) {
                                constraint =
                                        Utilities.replace(constraint, exemplar,
                                                parameterExp);
                            }

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
                                    && !requires.equals(myTypeGraph
                                            .getTrueVarExp())) {
                                requires =
                                        myTypeGraph.formConjunct(requires,
                                                constraint);
                                requires.setLocation((Location) opLocation
                                        .clone());
                            }
                            // Make constraint expression the requires clause
                            else {
                                requires = constraint;
                            }
                        }
                    }

                    // If the type is a type representation, then our requires clause
                    // should really say something about the conceptual type and not
                    // the variable
                    if (ste instanceof RepresentationTypeEntry && !isLocal) {
                        requires =
                                Utilities.replace(requires, parameterExp,
                                        Utilities
                                                .createConcVarExp(opLocation,
                                                        parameterExp,
                                                        parameterExp
                                                                .getMathType(),
                                                        BOOLEAN));
                    }
                }

                // Add the current variable to our list of free variables
                myCurrentAssertiveCode.addFreeVar(Utilities.createVarExp(p
                        .getLocation(), null, p.getName(), pNameTy
                        .getMathTypeValue(), null));
            }
            else {
                // Ty not handled.
                Utilities.tyNotHandled(p.getTy(), p.getLocation());
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
     * @param isLocal True if it is a local operation, false otherwise.
     *
     * @return The modified requires clause <code>Exp</code>.
     */
    private Exp modifyRequiresClause(Exp requires, Location opLocation,
            String opName, boolean isLocal) {
        // Modifies the existing requires clause based on
        // the parameter modes.
        requires =
                modifyRequiresByParameter(requires, opLocation, opName, isLocal);

        // Modifies the existing requires clause based on
        // the parameter modes.
        // TODO: Ask Murali what this means
        requires = modifyRequiresByGlobalMode(requires);

        return requires;
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

        // Replace postcondition variables in the ensures clause
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

            // Convert the pExp into a something we can use
            Exp repl = Utilities.convertExp(pExp);
            Exp undqRep = null, quesRep = null;
            OldExp oSpecVar, oRealVar;
            String replName = null;

            // Case #1: ProgramIntegerExp
            // Case #2: ProgramCharExp
            // Case #3: ProgramStringExp
            if (pExp instanceof ProgramIntegerExp
                    || pExp instanceof ProgramCharExp
                    || pExp instanceof ProgramStringExp) {
                Exp convertExp = Utilities.convertExp(pExp);
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

        // EqualsExp
        if (assertion instanceof EqualsExp) {
            EqualsExp equalsExp = (EqualsExp) assertion;

            // Only do simplifications if we have an equals
            if (equalsExp.getOperator() == EqualsExp.EQUAL) {
                // Create a temp expression where left is replaced with the right
                Exp tmp =
                        Utilities.replace(exp, equalsExp.getLeft(), equalsExp
                                .getRight());

                // If tmp hasn't changed, then it means we have to check the right
                if (tmp.equals(exp)) {
                    tmp =
                            Utilities.replace(exp, equalsExp.getRight(),
                                    equalsExp.getLeft());
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
     * <p>Applies the assume rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>AssumeStmt</code>.
     */
    private void applyAssumeStmtRule(AssumeStmt stmt) {
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
            ConfirmStmt finalConfirm = myCurrentAssertiveCode.getFinalConfirm();
            boolean simplify = finalConfirm.getSimplify();
            Exp currentFinalConfirm =
                    simplifyAssumeRule(stmt, finalConfirm.getAssertion());

            // Only create an implies expression if the goal is not just "true".
            // If the goal is "true", then simplify should be true as well.
            if (stmt.getAssertion() != null && !simplify) {
                // Create a new implies expression
                currentFinalConfirm =
                        myTypeGraph.formImplies(stmt.getAssertion(),
                                currentFinalConfirm);
                simplify = false;
            }

            // Set this as our new final confirm
            myCurrentAssertiveCode.setFinalConfirm(currentFinalConfirm,
                    simplify);

            // Verbose Mode Debug Messages
            myVCBuffer.append("\nAssume Rule Applied: \n");
            myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
            myVCBuffer.append("\n_____________________ \n");
        }
    }

    /**
     *  <p>Applies the change rule.</p>
     *
     * @param change The change clause
     */
    private void applyChangeRule(VerificationStatement change) {
        List<VariableExp> changeList =
                (List<VariableExp>) change.getAssertion();
        ConfirmStmt confirmStmt = myCurrentAssertiveCode.getFinalConfirm();
        Exp finalConfirm = confirmStmt.getAssertion();

        // Loop through each variable
        for (VariableExp v : changeList) {
            // v is an instance of VariableNameExp
            if (v instanceof VariableNameExp) {
                VariableNameExp vNameExp = (VariableNameExp) v;

                // Create VarExp for vNameExp
                VarExp vExp =
                        Utilities.createVarExp(vNameExp.getLocation(), vNameExp
                                .getQualifier(), vNameExp.getName(), vNameExp
                                .getMathType(), vNameExp.getMathTypeValue());

                // Create a new question mark variable
                VarExp newV =
                        Utilities
                                .createQuestionMarkVariable(finalConfirm, vExp);

                // Add this new variable to our list of free variables
                myCurrentAssertiveCode.addFreeVar(newV);

                // Replace all instances of vExp with newV
                finalConfirm = Utilities.replace(finalConfirm, vExp, newV);
            }
        }

        // Set the modified statement as our new final confirm
        myCurrentAssertiveCode.setFinalConfirm(finalConfirm, confirmStmt
                .getSimplify());

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nChange Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");
    }

    /**
     * <p>Applies the call statement rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>CallStmt</code>.
     */
    private void applyCallStmtRule(CallStmt stmt) {
        // Call a method to locate the operation dec for this call
        OperationDec opDec =
                getOperationDec(stmt.getLocation(), stmt.getQualifier(), stmt
                        .getName(), stmt.getArguments());
        boolean isLocal =
                Utilities.isLocationOperation(stmt.getName().getName(),
                        myCurrentModuleScope);

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

        // Check for recursive call of itself
        if (myCurrentOperationEntry != null
                && myCurrentOperationEntry.getName().equals(opDec.getName())
                && myCurrentOperationEntry.getReturnType() != null) {
            // Create a new confirm statement using P_val and the decreasing clause
            VarExp pVal =
                    Utilities.createPValExp(myOperationDecreasingExp
                            .getLocation(), myCurrentModuleScope);

            // Create a new infix expression
            InfixExp exp =
                    Utilities.createLessThanExp(stmt.getLocation(), Exp
                            .copy(myOperationDecreasingExp), pVal, BOOLEAN);

            // Create the new confirm statement
            Location loc;
            if (myOperationDecreasingExp.getLocation() != null) {
                loc = (Location) myOperationDecreasingExp.getLocation().clone();
            }
            else {
                loc = (Location) stmt.getLocation().clone();
            }
            loc.setDetails("Show Termination of Recursive Call");
            Utilities.setLocation(exp, loc);
            ConfirmStmt conf = new ConfirmStmt(loc, exp, false);

            // Add it to our list of assertions
            myCurrentAssertiveCode.addCode(conf);
        }

        // Modify ensures using the parameter modes
        ensures =
                modifyEnsuresByParameter(ensures, stmt.getLocation(), opDec
                        .getName().getName(), opDec.getParameters(), isLocal);

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
            Utilities.setLocation(requires, loc);

            // Add this to our list of things to confirm
            myCurrentAssertiveCode.addConfirm((Location) loc.clone(), requires,
                    simplify);
        }

        // Modify the location of the requires clause and add it to myCurrentAssertiveCode
        if (ensures != null) {
            // Obtain the current location
            if (stmt.getName().getLocation() != null) {
                // Set the details of the current location
                Location loc = (Location) stmt.getName().getLocation().clone();
                loc.setDetails("Ensures Clause of " + opDec.getName());
                Utilities.setLocation(ensures, loc);
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
     * <p>Applies different rules to code statements.</p>
     *
     * @param statement The different statements.
     */
    private void applyCodeRules(Statement statement) {
        // Apply each statement rule here.
        if (statement instanceof AssumeStmt) {
            applyAssumeStmtRule((AssumeStmt) statement);
        }
        else if (statement instanceof CallStmt) {
            applyCallStmtRule((CallStmt) statement);
        }
        else if (statement instanceof ConfirmStmt) {
            applyConfirmStmtRule((ConfirmStmt) statement);
        }
        else if (statement instanceof FuncAssignStmt) {
            applyFuncAssignStmtRule((FuncAssignStmt) statement);
        }
        else if (statement instanceof IfStmt) {
            applyIfStmtRule((IfStmt) statement);
        }
        else if (statement instanceof SwapStmt) {
            applySwapStmtRule((SwapStmt) statement);
        }
        else if (statement instanceof WhileStmt) {
            applyWhileStmtRule((WhileStmt) statement);
        }
    }

    /**
     * <p>Applies the confirm rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>ConfirmStmt</code>.
     */
    private void applyConfirmStmtRule(ConfirmStmt stmt) {
        // Check to see if our assertion can be simplified
        Exp assertion = stmt.getAssertion();
        if (stmt.getSimplify()) {
            // Verbose Mode Debug Messages
            myVCBuffer.append("\nConfirm Rule Applied and Simplified: \n");
            myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
            myVCBuffer.append("\n_____________________ \n");
        }
        else {
            // Obtain the current final confirm statement
            ConfirmStmt currentFinalConfirm =
                    myCurrentAssertiveCode.getFinalConfirm();

            // Check to see if we can simplify the final confirm
            if (currentFinalConfirm.getSimplify()) {
                // Obtain the current location
                if (assertion.getLocation() != null) {
                    // Set the details of the current location
                    Location loc = (Location) assertion.getLocation().clone();
                    Utilities.setLocation(assertion, loc);
                }

                myCurrentAssertiveCode.setFinalConfirm(assertion, stmt
                        .getSimplify());

                // Verbose Mode Debug Messages
                myVCBuffer.append("\nConfirm Rule Applied and Simplified: \n");
                myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
                myVCBuffer.append("\n_____________________ \n");
            }
            else {
                // Create a new and expression
                InfixExp newConf =
                        myTypeGraph.formConjunct(assertion, currentFinalConfirm
                                .getAssertion());

                // Set this new expression as the new final confirm
                myCurrentAssertiveCode.setFinalConfirm(newConf, false);

                // Verbose Mode Debug Messages
                myVCBuffer.append("\nConfirm Rule Applied: \n");
                myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
                myVCBuffer.append("\n_____________________ \n");
            }
        }
    }

    /**
     * <p>Applies the correspondence rule.</p>
     *
     * @param dec Representation declaration object.
     */
    private void applyCorrespondenceRule(RepresentationDec dec) {
        // Create a new assertive code to hold the correspondence VCs
        AssertiveCode assertiveCode =
                new AssertiveCode(myInstanceEnvironment, dec);

        // Add the global constraints as given
        assertiveCode.addAssume(myGlobalConstraintExp);

        // Add the global require clause as given
        assertiveCode.addAssume(myGlobalRequiresExp);

        // Add the convention as given
        assertiveCode.addAssume(dec.getConvention());

        // Add the correspondence as given
        myCorrespondenceExp = dec.getCorrespondence();
        assertiveCode.addAssume(myCorrespondenceExp);

        // Search for the type we are implementing
        SymbolTableEntry ste =
                Utilities.searchProgramType(dec.getLocation(), null, dec
                        .getName(), myCurrentModuleScope);

        ProgramTypeEntry typeEntry;
        if (ste instanceof ProgramTypeEntry) {
            typeEntry = ste.toProgramTypeEntry(dec.getLocation());
        }
        else {
            typeEntry =
                    ste.toRepresentationTypeEntry(dec.getLocation())
                            .getDefiningTypeEntry();
        }

        // Make sure we don't have a generic type
        if (typeEntry.getDefiningElement() instanceof TypeDec) {
            // Obtain the original dec from the AST
            TypeDec type = (TypeDec) typeEntry.getDefiningElement();

            // Create a variable expression from the type exemplar
            VarExp exemplar =
                    Utilities.createVarExp(type.getLocation(), null, type
                            .getExemplar(), typeEntry.getModelType(), null);

            DotExp conceptualVar =
                    Utilities.createConcVarExp(null, exemplar, typeEntry
                            .getModelType(), BOOLEAN);

            // Make sure we have a constraint
            Exp constraint;
            if (type.getConstraint() == null) {
                constraint = myTypeGraph.getTrueVarExp();
            }
            else {
                constraint = Exp.copy(type.getConstraint());
            }
            constraint = Utilities.replace(constraint, exemplar, conceptualVar);

            // Set the location for the constraint
            Location loc;
            if (myCorrespondenceExp.getLocation() != null) {
                loc = (Location) myCorrespondenceExp.getLocation().clone();
            }
            else {
                loc = (Location) type.getLocation().clone();
            }
            loc.setDetails("Well Defined Correspondence for "
                    + dec.getName().getName());
            Utilities.setLocation(constraint, loc);

            // We need to make sure the constraints for the type we are
            // implementing is met.
            boolean simplify = false;
            // Simplify if we just have true
            if (constraint.isLiteralTrue()) {
                simplify = true;
            }
            assertiveCode.setFinalConfirm(constraint, simplify);

            // Add the constraints for the implementing facility
            // or for each of the fields inside the record.
            Exp fieldConstraints = myTypeGraph.getTrueVarExp();
            if (dec.getRepresentation() instanceof NameTy) {
                NameTy ty = (NameTy) dec.getRepresentation();
                fieldConstraints =
                        Utilities.retrieveConstraint(ty.getLocation(), ty
                                .getQualifier(), ty.getName(), exemplar,
                                myCurrentModuleScope);
            }
            else {
                RecordTy ty = (RecordTy) dec.getRepresentation();

                // Find the constraints for each field inside the record.
                for (VarDec v : ty.getFields()) {
                    NameTy vTy = (NameTy) v.getTy();

                    // Create the name of the variable
                    VarExp vName =
                            Utilities.createVarExp(null, null, v.getName(), vTy
                                    .getMathType(), vTy.getMathTypeValue());

                    // Create [Exemplar].[v] dotted expression
                    edu.clemson.cs.r2jt.collections.List<Exp> dotExpList =
                            new edu.clemson.cs.r2jt.collections.List<Exp>();
                    dotExpList.add(exemplar);
                    dotExpList.add(vName);
                    DotExp varNameExp =
                            Utilities.createDotExp(v.getLocation(), dotExpList,
                                    v.getMathType());

                    Exp vConstraint =
                            Utilities.retrieveConstraint(vTy.getLocation(), vTy
                                    .getQualifier(), vTy.getName(), varNameExp,
                                    myCurrentModuleScope);

                    if (fieldConstraints.isLiteralTrue()) {
                        fieldConstraints = vConstraint;
                    }
                    else {
                        fieldConstraints =
                                myTypeGraph.formConjunct(fieldConstraints,
                                        vConstraint);
                    }
                }
            }

            // Only add the field constraints if we don't have true
            if (!fieldConstraints.isLiteralTrue()) {
                assertiveCode.addAssume(fieldConstraints);
            }
        }

        // Add this new assertive code to our incomplete assertive code stack
        myIncAssertiveCodeStack.push(assertiveCode);

        // Verbose Mode Debug Messages
        String newString =
                "\n========================= Type Representation Name:\t"
                        + dec.getName().getName()
                        + " =========================\n";
        newString += "\nCorrespondence Rule Applied: \n";
        newString += assertiveCode.assertionToString();
        newString += "\n_____________________ \n";
        myIncAssertiveCodeStackInfo.push(newString);
    }

    /**
     * <p>Applies the facility declaration rule.</p>
     *
     * @param dec Facility declaration object.
     */
    private void applyFacilityDeclRule(FacilityDec dec) {
        // Create a new assertive code to hold the facility declaration VCs
        AssertiveCode assertiveCode =
                new AssertiveCode(myInstanceEnvironment, dec);

        // Add the global constraints as given
        assertiveCode.addAssume(myGlobalConstraintExp);

        // Add the global require clause as given
        assertiveCode.addAssume(myGlobalRequiresExp);

        // TODO: Loop through every enhancement/enhancement realization declaration, if any.

        // Obtain the concept module for the facility
        try {
            ConceptModuleDec facConceptDec =
                    (ConceptModuleDec) mySymbolTable
                            .getModuleScope(
                                    new ModuleIdentifier(dec.getConceptName()
                                            .getName())).getDefiningElement();

            // Concept parameters
            List<ModuleArgumentItem> conceptParams = dec.getConceptParams();

            // Concept requires clause
            Exp req =
                    getRequiresClause(facConceptDec.getLocation(),
                            facConceptDec);
            Location loc = (Location) dec.getName().getLocation().clone();
            loc.setDetails("Facility Declaration Rule");

            req =
                    Utilities.replaceFacilityDeclarationVariables(req,
                            facConceptDec.getParameters(), conceptParams);
            req.setLocation(loc);

            boolean simplify = false;
            // Simplify if we just have true
            if (req.isLiteralTrue()) {
                simplify = true;
            }
            assertiveCode.setFinalConfirm(req, simplify);

            // Obtain the constraint of the concept type
            Exp assumeExp = null;

            // TODO: This is ugly! Need to clean this up!
            List<ModuleParameterDec> moduleParameterList =
                    facConceptDec.getParameters();
            List<EqualsExp> formalToActualList = new LinkedList<EqualsExp>();
            for (int i = 0; i < moduleParameterList.size(); i++) {
                ModuleParameterDec m = moduleParameterList.get(i);
                if (m.getWrappedDec() instanceof ConstantParamDec) {
                    ConstantParamDec constantParamDec =
                            (ConstantParamDec) m.getWrappedDec();
                    VarDec tempDec =
                            new VarDec(constantParamDec.getName(),
                                    constantParamDec.getTy());

                    // Search for the ProgramTypeEntry
                    // Ty is NameTy
                    if (tempDec.getTy() instanceof NameTy) {
                        NameTy pNameTy = (NameTy) tempDec.getTy();

                        // Query for the type entry in the symbol table
                        SymbolTableEntry ste =
                                Utilities
                                        .searchProgramType(pNameTy
                                                .getLocation(), pNameTy
                                                .getQualifier(), pNameTy
                                                .getName(),
                                                myCurrentModuleScope);

                        ProgramTypeEntry typeEntry;
                        if (ste instanceof ProgramTypeEntry) {
                            typeEntry =
                                    ste.toProgramTypeEntry(pNameTy
                                            .getLocation());
                        }
                        else {
                            typeEntry =
                                    ste.toRepresentationTypeEntry(
                                            pNameTy.getLocation())
                                            .getDefiningTypeEntry();
                        }

                        // Obtain the original dec from the AST
                        TypeDec type = (TypeDec) typeEntry.getDefiningElement();

                        // Create a variable expression from the declared variable
                        VarExp varDecExp =
                                Utilities.createVarExp(tempDec.getLocation(),
                                        null, tempDec.getName(), typeEntry
                                                .getModelType(), null);

                        // Create a variable expression from the type exemplar
                        VarExp exemplar =
                                Utilities.createVarExp(type.getLocation(),
                                        null, type.getExemplar(), typeEntry
                                                .getModelType(), null);

                        // Make sure we have a constraint
                        Exp constraint;
                        if (type.getConstraint() == null) {
                            constraint = myTypeGraph.getTrueVarExp();
                        }
                        else {
                            constraint = Exp.copy(type.getConstraint());
                        }
                        constraint =
                                Utilities.replace(constraint, exemplar,
                                        varDecExp);

                        // Set the location for the constraint
                        Location constraintLoc;
                        if (constraint.getLocation() != null) {
                            constraintLoc =
                                    (Location) constraint.getLocation().clone();
                        }
                        else {
                            constraintLoc =
                                    (Location) type.getLocation().clone();
                        }
                        constraintLoc.setDetails("Constraints on "
                                + tempDec.getName().getName());
                        Utilities.setLocation(constraint, constraintLoc);

                        // Replace with facility declaration variables
                        constraint =
                                Utilities
                                        .replaceFacilityDeclarationVariables(
                                                constraint, facConceptDec
                                                        .getParameters(),
                                                conceptParams);

                        if (assumeExp == null) {
                            assumeExp = constraint;
                        }
                        else {
                            assumeExp =
                                    myTypeGraph.formConjunct(assumeExp,
                                            constraint);
                        }

                        // TODO: Change this! This is such a hack!
                        // Create an equals expression from formal to actual
                        Exp actualExp;
                        if (conceptParams.get(i).getEvalExp() != null) {
                            actualExp =
                                    Utilities.convertExp(conceptParams.get(i)
                                            .getEvalExp());
                        }
                        else {
                            actualExp = Exp.copy(varDecExp);
                        }
                        EqualsExp formalEq =
                                new EqualsExp(dec.getLocation(), varDecExp, 1,
                                        actualExp);
                        formalEq.setMathType(BOOLEAN);
                        formalToActualList.add(formalEq);
                    }
                    else {
                        // Ty not handled.
                        Utilities.tyNotHandled(tempDec.getTy(), tempDec
                                .getLocation());
                    }
                }
            }

            // Make sure it is not null
            if (assumeExp == null) {
                assumeExp = myTypeGraph.getTrueVarExp();
            }

            myFacilityDeclarationMap.put(dec, assumeExp);
            myFacilityFormalActualMap.put(dec, formalToActualList);
        }
        catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(dec.getLocation());
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
     * <p>Applies the function assignment rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>FuncAssignStmt</code>.
     */
    private void applyFuncAssignStmtRule(FuncAssignStmt stmt) {
        PosSymbol qualifier = null;
        ProgramExp assignExp = stmt.getAssign();
        ProgramParamExp assignParamExp = null;

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
                    varExp.setName(((VariableNameExp) vr).getName());
                    varExp.setMathType(vr.getMathType());
                    varExp.setMathTypeValue(vr.getMathTypeValue());
                    newSegments.add(varExp);
                }
            }

            // Expression to be replaced
            leftVariable = new DotExp(v.getLocation(), newSegments, null);
            leftVariable.setMathType(v.getMathType());
            leftVariable.setMathTypeValue(v.getMathTypeValue());
        }
        // We have a regular variable being assigned.
        else {
            // Expression to be replaced
            VariableNameExp v = (VariableNameExp) stmt.getVar();
            leftVariable = new VarExp(v.getLocation(), null, v.getName());
            leftVariable.setMathType(v.getMathType());
            leftVariable.setMathTypeValue(v.getMathTypeValue());
        }

        // Simply replace the numbers/characters/strings
        if (assignExp instanceof ProgramIntegerExp
                || assignExp instanceof ProgramCharExp
                || assignExp instanceof ProgramStringExp) {
            Exp replaceExp = Utilities.convertExp(assignExp);

            // Replace all instances of the left hand side
            // variable in the current final confirm statement.
            ConfirmStmt confirmStmt = myCurrentAssertiveCode.getFinalConfirm();
            Exp newConf = confirmStmt.getAssertion();
            newConf = Utilities.replace(newConf, leftVariable, replaceExp);

            // Set this as our new final confirm statement.
            myCurrentAssertiveCode.setFinalConfirm(newConf, confirmStmt
                    .getSimplify());
        }
        else {
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

            // Call a method to locate the operation dec for this call
            OperationDec opDec =
                    getOperationDec(stmt.getLocation(), qualifier,
                            assignParamExp.getName(), assignParamExp
                                    .getArguments());

            // Check for recursive call of itself
            if (myCurrentOperationEntry != null
                    && myCurrentOperationEntry.getName()
                            .equals(opDec.getName())
                    && myCurrentOperationEntry.getReturnType() != null) {
                // Create a new confirm statement using P_val and the decreasing clause
                VarExp pVal =
                        Utilities.createPValExp(myOperationDecreasingExp
                                .getLocation(), myCurrentModuleScope);

                // Create a new infix expression
                InfixExp exp =
                        Utilities.createLessThanExp(stmt.getLocation(), Exp
                                .copy(myOperationDecreasingExp), pVal, BOOLEAN);

                // Create the new confirm statement
                Location loc;
                if (myOperationDecreasingExp.getLocation() != null) {
                    loc =
                            (Location) myOperationDecreasingExp.getLocation()
                                    .clone();
                }
                else {
                    loc = (Location) stmt.getLocation().clone();
                }
                loc.setDetails("Show Termination of Recursive Call");
                Utilities.setLocation(exp, loc);
                ConfirmStmt conf = new ConfirmStmt(loc, exp, false);

                // Add it to our list of assertions
                myCurrentAssertiveCode.addCode(conf);
            }

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
                            assignParamExp.getArguments());

            // Modify the location of the requires clause and add it to myCurrentAssertiveCode
            // Obtain the current location
            // Note: If we don't have a location, we create one
            Location reqloc;
            if (assignParamExp.getName().getLocation() != null) {
                reqloc =
                        (Location) assignParamExp.getName().getLocation()
                                .clone();
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
            reqloc
                    .setDetails("Requires Clause of " + opDec.getName()
                            + details);
            Utilities.setLocation(requires, reqloc);

            // Add this to our list of things to confirm
            myCurrentAssertiveCode.addConfirm((Location) reqloc.clone(),
                    requires, simplify);

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
                        VarExp leftExp =
                                (VarExp) ((EqualsExp) opEnsures).getLeft();

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
                                Utilities.setLocation(ensures, loc);
                            }

                            // Replace the formal with the actual
                            ensures =
                                    replaceFormalWithActualEns(ensures, opDec
                                            .getParameters(), opDec
                                            .getStateVars(), assignParamExp
                                            .getArguments(), true);

                            // Replace all instances of the left hand side
                            // variable in the current final confirm statement.
                            ConfirmStmt confirmStmt =
                                    myCurrentAssertiveCode.getFinalConfirm();
                            Exp newConf = confirmStmt.getAssertion();
                            newConf =
                                    Utilities.replace(newConf, leftVariable,
                                            ensures);

                            // Set this as our new final confirm statement.
                            myCurrentAssertiveCode.setFinalConfirm(newConf,
                                    confirmStmt.getSimplify());
                        }
                        else {
                            Utilities.illegalOperationEnsures(opDec
                                    .getLocation());
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
    private void applyIfStmtRule(IfStmt stmt) {
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

        // Set the details of the current location
        reqloc.setDetails("Requires Clause of " + opDec.getName());
        Utilities.setLocation(requires, reqloc);

        // Add this to our list of things to confirm
        myCurrentAssertiveCode.addConfirm((Location) reqloc.clone(), requires,
                simplify);

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
                            Utilities.setLocation(ensures, loc);
                        }

                        // Replace the formals with the actuals.
                        ensures =
                                replaceFormalWithActualEns(ensures, opDec
                                        .getParameters(), opDec.getStateVars(),
                                        testParamExp.getArguments(), false);
                        myCurrentAssertiveCode.addAssume(ensures);

                        // Negation of the condition
                        negEnsures = Utilities.negateExp(ensures, BOOLEAN);
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

        // Create a list for the then clause
        edu.clemson.cs.r2jt.collections.List<Statement> thenStmtList;
        if (stmt.getThenclause() != null) {
            thenStmtList = stmt.getThenclause();
        }
        else {
            thenStmtList =
                    new edu.clemson.cs.r2jt.collections.List<Statement>();
        }

        // Modify the confirm details
        ConfirmStmt ifConfirm = myCurrentAssertiveCode.getFinalConfirm();
        Exp ifConfirmExp = ifConfirm.getAssertion();
        Location ifLocation;
        if (ifConfirm.getLocation() != null) {
            ifLocation = (Location) ifConfirm.getLocation().clone();
        }
        else {
            ifLocation = (Location) stmt.getLocation().clone();
        }
        String ifDetail = "Condition at " + ifLocation.toString() + " is true";
        ifLocation.setDetails(ifDetail);
        ifConfirm.setLocation(ifLocation);

        // NY YS
        // Duration for If Part
        InfixExp sumEvalDur = null;
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
            Location loc = (Location) ifLocation.clone();
            VarExp cumDur;
            boolean trueConfirm = false;

            // Our previous rule must have been a while rule
            ConfirmStmt conf = null;
            if (ifConfirmExp.isLiteralTrue() && thenStmtList.size() != 0) {
                Statement st = thenStmtList.get(thenStmtList.size() - 1);
                if (st instanceof ConfirmStmt) {
                    conf = (ConfirmStmt) st;
                    cumDur =
                            Utilities.createVarExp((Location) loc.clone(),
                                    null, Utilities.createPosSymbol(Utilities
                                            .getCumDur(conf.getAssertion())),
                                    myTypeGraph.R, null);
                    trueConfirm = true;
                }
                else {
                    cumDur = null;
                    Utilities.noSuchSymbol(null, "Cum_Dur", loc);
                }
            }
            else {
                cumDur =
                        Utilities.createVarExp((Location) loc.clone(), null,
                                Utilities.createPosSymbol(Utilities
                                        .getCumDur(ifConfirmExp)),
                                myTypeGraph.R, null);
            }

            // Search for operation profile
            List<PTType> argTypes = new LinkedList<PTType>();
            List<ProgramExp> argsList = testParamExp.getArguments();
            for (ProgramExp arg : argsList) {
                argTypes.add(arg.getProgramType());
            }
            OperationProfileEntry ope =
                    Utilities.searchOperationProfile(loc, qualifier,
                            testParamExp.getName(), argTypes,
                            myCurrentModuleScope);
            Exp opDur = Exp.copy(ope.getDurationClause());

            Exp durCallExp =
                    Utilities.createDurCallExp(loc, Integer.toString(opDec
                            .getParameters().size()), myTypeGraph.Z,
                            myTypeGraph.R);
            sumEvalDur =
                    new InfixExp((Location) loc.clone(), opDur, Utilities
                            .createPosSymbol("+"), durCallExp);
            sumEvalDur.setMathType(myTypeGraph.R);

            Exp sumPlusCumDur =
                    new InfixExp((Location) loc.clone(), Exp.copy(cumDur),
                            Utilities.createPosSymbol("+"), Exp
                                    .copy(sumEvalDur));
            sumPlusCumDur.setMathType(myTypeGraph.R);

            if (trueConfirm && conf != null) {
                // Replace "Cum_Dur" with "Cum_Dur + Dur_Call(<num of args>) + <duration of call>"
                Exp confirm = conf.getAssertion();
                confirm =
                        Utilities.replace(confirm, cumDur, Exp
                                .copy(sumPlusCumDur));
                conf.setAssertion(confirm);
                thenStmtList.set(thenStmtList.size() - 1, conf);
            }
            else {
                // Replace "Cum_Dur" with "Cum_Dur + Dur_Call(<num of args>) + <duration of call>"
                ifConfirmExp =
                        Utilities.replace(ifConfirmExp, cumDur, Exp
                                .copy(sumPlusCumDur));
            }
        }

        // Add the statements inside the if to the assertive code
        myCurrentAssertiveCode.addStatements(thenStmtList);

        // Set the final if confirm
        myCurrentAssertiveCode.setFinalConfirm(ifConfirmExp, ifConfirm
                .getSimplify());

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nIf Part Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");

        // Add the negation of the if condition as the assume clause
        if (negEnsures != null) {
            negIfAssertiveCode.addAssume(negEnsures);
        }
        else {
            Utilities.illegalOperationEnsures(opDec.getLocation());
        }

        // Create a list for the then clause
        edu.clemson.cs.r2jt.collections.List<Statement> elseStmtList;
        if (stmt.getElseclause() != null) {
            elseStmtList = stmt.getElseclause();
        }
        else {
            elseStmtList =
                    new edu.clemson.cs.r2jt.collections.List<Statement>();
        }

        // Modify the confirm details
        ConfirmStmt negIfConfirm = negIfAssertiveCode.getFinalConfirm();
        Exp negIfConfirmExp = negIfConfirm.getAssertion();
        Location negIfLocation = (Location) ifConfirm.getLocation().clone();
        String negIfDetail =
                "Condition at " + negIfLocation.toString() + " is false";
        negIfLocation.setDetails(negIfDetail);
        negIfConfirm.setLocation(negIfLocation);

        // NY YS
        // Duration for Else Part
        if (sumEvalDur != null) {
            Location loc = (Location) negIfLocation.clone();
            VarExp cumDur;
            boolean trueConfirm = false;

            // Our previous rule must have been a while rule
            ConfirmStmt conf = null;
            if (negIfConfirmExp.isLiteralTrue() && elseStmtList.size() != 0) {
                Statement st = elseStmtList.get(elseStmtList.size() - 1);
                if (st instanceof ConfirmStmt) {
                    conf = (ConfirmStmt) st;
                    cumDur =
                            Utilities.createVarExp((Location) loc.clone(),
                                    null, Utilities.createPosSymbol(Utilities
                                            .getCumDur(conf.getAssertion())),
                                    myTypeGraph.R, null);
                    trueConfirm = true;
                }
                else {
                    cumDur = null;
                    Utilities.noSuchSymbol(null, "Cum_Dur", loc);
                }
            }
            else {
                cumDur =
                        Utilities.createVarExp((Location) loc.clone(), null,
                                Utilities.createPosSymbol(Utilities
                                        .getCumDur(negIfConfirmExp)),
                                myTypeGraph.R, null);
            }

            Exp sumPlusCumDur =
                    new InfixExp((Location) loc.clone(), Exp.copy(cumDur),
                            Utilities.createPosSymbol("+"), Exp
                                    .copy(sumEvalDur));
            sumPlusCumDur.setMathType(myTypeGraph.R);

            if (trueConfirm && conf != null) {
                // Replace "Cum_Dur" with "Cum_Dur + Dur_Call(<num of args>) + <duration of call>"
                Exp confirm = conf.getAssertion();
                confirm =
                        Utilities.replace(confirm, cumDur, Exp
                                .copy(sumPlusCumDur));
                conf.setAssertion(confirm);
                elseStmtList.set(elseStmtList.size() - 1, conf);
            }
            else {
                // Replace "Cum_Dur" with "Cum_Dur + Dur_Call(<num of args>) + <duration of call>"
                negIfConfirmExp =
                        Utilities.replace(negIfConfirmExp, cumDur, Exp
                                .copy(sumPlusCumDur));
            }
        }

        // Add the statements inside the else to the assertive code
        negIfAssertiveCode.addStatements(elseStmtList);

        // Set the final else confirm
        negIfAssertiveCode.setFinalConfirm(negIfConfirmExp, negIfConfirm
                .getSimplify());

        // Add this new assertive code to our incomplete assertive code stack
        myIncAssertiveCodeStack.push(negIfAssertiveCode);

        // Verbose Mode Debug Messages
        String newString = "\nNegation of If Part Rule Applied: \n";
        newString += negIfAssertiveCode.assertionToString();
        newString += "\n_____________________ \n";
        myIncAssertiveCodeStackInfo.push(newString);
    }

    /**
     * <p>Applies the initialization rule.</p>
     *
     * @param dec Representation declaration object.
     */
    private void applyInitializationRule(RepresentationDec dec) {
        // Create a new assertive code to hold the correspondence VCs
        AssertiveCode assertiveCode =
                new AssertiveCode(myInstanceEnvironment, dec);

        // Add the global constraints as given
        assertiveCode.addAssume(myGlobalConstraintExp);

        // Add the global require clause as given
        assertiveCode.addAssume(myGlobalRequiresExp);

        // Add any variable declarations for records
        if (dec.getRepresentation() instanceof RecordTy) {
            RecordTy ty = (RecordTy) dec.getRepresentation();
            assertiveCode.addVariableDecs(ty.getFields());
        }

        // Add any statements in the initialization block
        if (dec.getInitialization() != null) {
            InitItem initItem = dec.getInitialization();
            assertiveCode.addStatements(initItem.getStatements());
        }

        // Search for the type we are implementing
        SymbolTableEntry ste =
                Utilities.searchProgramType(dec.getLocation(), null, dec
                        .getName(), myCurrentModuleScope);

        ProgramTypeEntry typeEntry;
        if (ste instanceof ProgramTypeEntry) {
            typeEntry = ste.toProgramTypeEntry(dec.getLocation());
        }
        else {
            typeEntry =
                    ste.toRepresentationTypeEntry(dec.getLocation())
                            .getDefiningTypeEntry();
        }

        // Make sure we don't have a generic type
        if (typeEntry.getDefiningElement() instanceof TypeDec) {
            // Obtain the original dec from the AST
            TypeDec type = (TypeDec) typeEntry.getDefiningElement();

            // Create a variable expression from the type exemplar
            VarExp exemplar =
                    Utilities.createVarExp(type.getLocation(), null, type
                            .getExemplar(), typeEntry.getModelType(), null);

            // Add the correspondence as given
            assertiveCode.addAssume(dec.getCorrespondence());

            // Make sure we have a convention
            if (dec.getConvention() == null) {
                myConventionExp = myTypeGraph.getTrueVarExp();
            }
            else {
                myConventionExp = Exp.copy(dec.getConvention());
            }

            // Set the location for the constraint
            Location loc;
            if (myConventionExp.getLocation() != null) {
                loc = (Location) myConventionExp.getLocation().clone();
            }
            else {
                loc = (Location) dec.getLocation().clone();
            }
            loc.setDetails("Convention for " + dec.getName().getName());
            Utilities.setLocation(myConventionExp, loc);

            // Add the convention as something we need to confirm
            boolean simplify = false;
            // Simplify if we just have true
            if (myConventionExp.isLiteralTrue()) {
                simplify = true;
            }
            Exp convention = Exp.copy(myConventionExp);
            Location conventionLoc =
                    (Location) convention.getLocation().clone();
            conventionLoc.setDetails(conventionLoc.getDetails()
                    + " generated by Initialization Rule");
            Utilities.setLocation(convention, conventionLoc);
            assertiveCode.addConfirm(loc, convention, simplify);

            // Create a variable that refers to the conceptual exemplar
            DotExp conceptualVar =
                    Utilities.createConcVarExp(null, exemplar, typeEntry
                            .getModelType(), BOOLEAN);

            // Make sure we have a constraint
            Exp init;
            if (type.getInitialization().getEnsures() == null) {
                init = myTypeGraph.getTrueVarExp();
            }
            else {
                init = Exp.copy(type.getInitialization().getEnsures());
            }
            init = Utilities.replace(init, exemplar, conceptualVar);

            // Set the location for the constraint
            Location initLoc;
            initLoc = (Location) dec.getLocation().clone();
            initLoc.setDetails("Initialization Rule for "
                    + dec.getName().getName());
            Utilities.setLocation(init, initLoc);

            // Add the initialization as something we need to confirm
            simplify = false;
            // Simplify if we just have true
            if (init.isLiteralTrue()) {
                simplify = true;
            }
            assertiveCode.addConfirm(loc, init, simplify);
        }

        // Add this new assertive code to our incomplete assertive code stack
        myIncAssertiveCodeStack.push(assertiveCode);

        // Verbose Mode Debug Messages
        String newString =
                "\n========================= Type Representation Name:\t"
                        + dec.getName().getName()
                        + " =========================\n";
        newString += "\nInitialization Rule Applied: \n";
        newString += assertiveCode.assertionToString();
        newString += "\n_____________________ \n";
        myIncAssertiveCodeStackInfo.push(newString);
    }

    /**
     * <p>Applies the procedure declaration rule.</p>
     *
     * @param opLoc Location of the procedure declaration.
     * @param name Name of the procedure.
     * @param requires Requires clause
     * @param ensures Ensures clause
     * @param decreasing Decreasing clause (if any)
     * @param procDur Procedure duration clause (if in performance mode)
     * @param varFinalDur Local variable finalization duration clause (if in performance mode)
     * @param typeConstraint Facility type constraints (if any)
     * @param variableList List of all variables for this procedure
     * @param statementList List of statements for this procedure
     * @param isLocal True if the it is a local operation. False otherwise.
     */
    private void applyProcedureDeclRule(Location opLoc, String name,
            Exp requires, Exp ensures, Exp decreasing, Exp procDur,
            Exp varFinalDur, Exp typeConstraint, List<VarDec> variableList,
            List<Statement> statementList, boolean isLocal) {
        // Add the global requires clause
        if (myGlobalRequiresExp != null) {
            myCurrentAssertiveCode.addAssume(myGlobalRequiresExp);
        }

        // Add the global constraints
        if (myGlobalConstraintExp != null) {
            myCurrentAssertiveCode.addAssume(myGlobalConstraintExp);
        }

        // Add the convention as something we need to ensure
        if (myConventionExp != null && !isLocal) {
            Exp convention = Exp.copy(myConventionExp);
            myCurrentAssertiveCode.addAssume(convention);
        }

        // Add the correspondence as a given
        if (myCorrespondenceExp != null && !isLocal) {
            Exp correspondence = Exp.copy(myCorrespondenceExp);
            myCurrentAssertiveCode.addAssume(correspondence);
        }

        // Add the requires clause
        if (requires != null) {
            myCurrentAssertiveCode.addAssume(requires);
        }

        // Add the facility formal to actuals
        Exp formalActualExp = myTypeGraph.getTrueVarExp();
        for (FacilityDec fDec : myFacilityFormalActualMap.keySet()) {
            List<EqualsExp> eList = myFacilityFormalActualMap.get(fDec);
            for (EqualsExp e : eList) {
                EqualsExp newEquals = (EqualsExp) Exp.copy(e);
                VarExp oldLeft = (VarExp) Exp.copy(newEquals.getLeft());
                VarExp newLeft;
                if (formalActualExp.containsVar(oldLeft.getName().getName(),
                        false)) {
                    newLeft =
                            Utilities.createQuestionMarkVariable(
                                    formalActualExp, oldLeft);
                }
                else {
                    newLeft = oldLeft;
                }
                newEquals.setLeft(newLeft);

                // Get rid of true
                if (formalActualExp.isLiteralTrue()) {
                    formalActualExp = newEquals;
                }
                else {
                    formalActualExp =
                            myTypeGraph
                                    .formConjunct(formalActualExp, newEquals);
                }
            }
        }
        if (!formalActualExp.isLiteralTrue()) {
            myCurrentAssertiveCode.addAssume(formalActualExp);
        }

        // NY - Add any procedure duration clauses
        if (procDur != null) {
            // Add Cum_Dur as a free variable
            VarExp cumDur =
                    Utilities.createVarExp((Location) opLoc.clone(), null,
                            Utilities.createPosSymbol("Cum_Dur"),
                            myTypeGraph.R, null);
            myCurrentAssertiveCode.addFreeVar(cumDur);

            // Create 0.0
            VarExp zeroPtZero =
                    Utilities.createVarExp(opLoc, null, Utilities
                            .createPosSymbol("0.0"), myTypeGraph.R, null);

            // Create an equals expression (Cum_Dur = 0.0)
            EqualsExp equalsExp =
                    new EqualsExp(null, Exp.copy(cumDur), EqualsExp.EQUAL,
                            zeroPtZero);
            equalsExp.setMathType(BOOLEAN);
            Location eqLoc = (Location) opLoc.clone();
            eqLoc.setDetails("Initialization of Cum_Dur for Procedure " + name);
            Utilities.setLocation(equalsExp, eqLoc);

            // Add it to our things to assume
            myCurrentAssertiveCode.addAssume(equalsExp);

            // Create the duration expression
            Exp durationExp;
            if (varFinalDur != null) {
                durationExp =
                        new InfixExp(null, Exp.copy(cumDur), Utilities
                                .createPosSymbol("+"), varFinalDur);
            }
            else {
                durationExp = Exp.copy(cumDur);
            }
            durationExp.setMathType(myTypeGraph.R);
            Location sumLoc = (Location) opLoc.clone();
            sumLoc
                    .setDetails("Summation of Finalization Duration for Procedure "
                            + name);
            Utilities.setLocation(durationExp, sumLoc);

            InfixExp greaterEqExp =
                    new InfixExp(null, durationExp, Utilities
                            .createPosSymbol("<="), procDur);
            greaterEqExp.setMathType(BOOLEAN);
            Location andLoc = (Location) opLoc.clone();
            andLoc.setDetails("Duration Clause of " + name);
            Utilities.setLocation(greaterEqExp, andLoc);

            // Append the duration to the ensures clause
            ensures = myTypeGraph.formConjunct(ensures, greaterEqExp);
        }

        // Add the facility type constraints
        if (typeConstraint != null) {
            myCurrentAssertiveCode.addAssume(typeConstraint);
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
            VarExp pVal =
                    Utilities.createPValExp(decreasing.getLocation(),
                            myCurrentModuleScope);
            myCurrentAssertiveCode.addFreeVar(pVal);

            // Create an equals expression
            EqualsExp equalsExp =
                    new EqualsExp(null, pVal, EqualsExp.EQUAL, Exp
                            .copy(decreasing));
            equalsExp.setMathType(BOOLEAN);
            Location eqLoc = (Location) decreasing.getLocation().clone();
            eqLoc.setDetails("Progress Metric for Recursive Procedure");
            Utilities.setLocation(equalsExp, eqLoc);

            // Add it to our things to assume
            myCurrentAssertiveCode.addAssume(equalsExp);
        }

        // Add the list of statements
        myCurrentAssertiveCode.addStatements(statementList);

        // Add the correspondence as a given again
        if (myCorrespondenceExp != null && !isLocal) {
            Exp correspondence = Exp.copy(myCorrespondenceExp);
            myCurrentAssertiveCode.addAssume(correspondence);
        }

        // Add the convention as something we need to ensure
        if (myConventionExp != null && !isLocal) {
            Exp convention = Exp.copy(myConventionExp);
            Location conventionLoc = (Location) opLoc.clone();
            conventionLoc.setDetails(convention.getLocation().getDetails()
                    + " generated by " + name);
            Utilities.setLocation(convention, conventionLoc);

            // Simplify if we just have true
            boolean simplify = false;
            if (myConventionExp.isLiteralTrue()) {
                simplify = true;
            }
            myCurrentAssertiveCode.addConfirm(conventionLoc, convention,
                    simplify);
        }

        // Simplify if we just have true
        boolean simplify = false;
        if (ensures.isLiteralTrue()) {
            simplify = true;
        }
        // Add the final confirms clause
        myCurrentAssertiveCode.setFinalConfirm(ensures, simplify);

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
        ConfirmStmt confirmStmt = myCurrentAssertiveCode.getFinalConfirm();
        Exp conf = confirmStmt.getAssertion();
        conf = conf.remember();
        myCurrentAssertiveCode.setFinalConfirm(conf, confirmStmt.getSimplify());

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nRemember Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");
    }

    /**
     * <p>Applies each of the proof rules. This <code>AssertiveCode</code> will be
     * stored for later use and therefore should be considered immutable after
     * a call to this method.</p>
     */
    private void applyRules() {
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
     * <p>Applies the swap statement rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>SwapStmt</code>.
     */
    private void applySwapStmtRule(SwapStmt stmt) {
        // Obtain the current final confirm clause
        ConfirmStmt confirmStmt = myCurrentAssertiveCode.getFinalConfirm();
        Exp conf = confirmStmt.getAssertion();

        // Create a copy of the left and right hand side
        VariableExp stmtLeft = (VariableExp) Exp.copy(stmt.getLeft());
        VariableExp stmtRight = (VariableExp) Exp.copy(stmt.getRight());

        // New left and right
        Exp newLeft = Utilities.convertExp(stmtLeft);
        Exp newRight = Utilities.convertExp(stmtRight);

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
        tmp.setName(Utilities.createPosSymbol("_"
                + Utilities.getVarName(stmtLeft).getName()));
        tmp.setMathType(stmtLeft.getMathType());
        tmp.setMathTypeValue(stmtLeft.getMathTypeValue());

        // Replace according to the swap rule
        conf = Utilities.replace(conf, newRight, tmp);
        conf = Utilities.replace(conf, newLeft, newRight);
        conf = Utilities.replace(conf, tmp, newLeft);

        // NY YS
        // Duration for swap statements
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
            Location loc = stmt.getLocation();
            VarExp cumDur =
                    Utilities
                            .createVarExp((Location) loc.clone(), null,
                                    Utilities.createPosSymbol(Utilities
                                            .getCumDur(conf)), myTypeGraph.R,
                                    null);

            Exp swapDur =
                    Utilities.createVarExp((Location) loc.clone(), null,
                            Utilities.createPosSymbol("DurSwap"),
                            myTypeGraph.R, null);
            InfixExp sumSwapDur =
                    new InfixExp((Location) loc.clone(), Exp.copy(cumDur),
                            Utilities.createPosSymbol("+"), swapDur);
            sumSwapDur.setMathType(myTypeGraph.R);

            conf = Utilities.replace(conf, cumDur, sumSwapDur);
        }

        // Set this new expression as the new final confirm
        myCurrentAssertiveCode.setFinalConfirm(conf, confirmStmt.getSimplify());

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nSwap Rule Applied: \n");
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
            SymbolTableEntry ste =
                    Utilities.searchProgramType(pNameTy.getLocation(), pNameTy
                            .getQualifier(), pNameTy.getName(),
                            myCurrentModuleScope);

            if (ste instanceof ProgramTypeEntry) {
                typeEntry = ste.toProgramTypeEntry(pNameTy.getLocation());
            }
            else {
                typeEntry =
                        ste.toRepresentationTypeEntry(pNameTy.getLocation())
                                .getDefiningTypeEntry();
            }

            // Make sure we don't have a generic type
            if (typeEntry.getDefiningElement() instanceof TypeDec) {
                // Obtain the original dec from the AST
                TypeDec type = (TypeDec) typeEntry.getDefiningElement();

                // Create a variable expression from the declared variable
                VarExp varDecExp =
                        Utilities.createVarExp(varDec.getLocation(), null,
                                varDec.getName(), typeEntry.getModelType(),
                                null);

                // Create a variable expression from the type exemplar
                VarExp exemplar =
                        Utilities.createVarExp(type.getLocation(), null, type
                                .getExemplar(), typeEntry.getModelType(), null);

                // Deep copy the original initialization ensures
                Exp init = Exp.copy(type.getInitialization().getEnsures());
                init = Utilities.replace(init, exemplar, varDecExp);

                // Make sure we have a constraint
                Exp constraint;
                if (type.getConstraint() == null) {
                    constraint = myTypeGraph.getTrueVarExp();
                }
                else {
                    constraint = Exp.copy(type.getConstraint());
                }
                constraint = Utilities.replace(constraint, exemplar, varDecExp);

                // Set the location for the constraint
                Location loc;
                if (constraint.getLocation() != null) {
                    loc = (Location) constraint.getLocation().clone();
                }
                else {
                    loc = (Location) type.getLocation().clone();
                }
                loc.setDetails("Constraints on " + varDec.getName().getName());
                Utilities.setLocation(constraint, loc);

                // Final confirm clause
                Exp finalConfirm =
                        myCurrentAssertiveCode.getFinalConfirm().getAssertion();

                // Obtain the string form of the variable
                String varName = varDec.getName().getName();

                // Check to see if we have a variable dot expression.
                // If we do, we will need to extract the name.
                int dotIndex = varName.indexOf(".");
                if (dotIndex > 0) {
                    varName = varName.substring(0, dotIndex);
                }

                // Check to see if this variable was declared inside a record
                ResolveConceptualElement element =
                        myCurrentAssertiveCode.getInstantiatingElement();
                if (element instanceof RepresentationDec) {
                    RepresentationDec dec = (RepresentationDec) element;

                    if (dec.getRepresentation() instanceof RecordTy) {
                        SymbolTableEntry repSte =
                                Utilities.searchProgramType(dec.getLocation(),
                                        null, dec.getName(),
                                        myCurrentModuleScope);

                        ProgramTypeDefinitionEntry representationTypeEntry =
                                repSte.toRepresentationTypeEntry(
                                        pNameTy.getLocation())
                                        .getDefiningTypeEntry();

                        // Create a variable expression from the type exemplar
                        VarExp representationExemplar =
                                Utilities
                                        .createVarExp(
                                                varDec.getLocation(),
                                                null,
                                                Utilities
                                                        .createPosSymbol(representationTypeEntry
                                                                .getExemplar()
                                                                .getName()),
                                                representationTypeEntry
                                                        .getModelType(), null);

                        // Create a dotted expression
                        edu.clemson.cs.r2jt.collections.List<Exp> expList =
                                new edu.clemson.cs.r2jt.collections.List<Exp>();
                        expList.add(representationExemplar);
                        expList.add(varDecExp);
                        DotExp dotExp =
                                Utilities.createDotExp(loc, expList, varDecExp
                                        .getMathType());

                        // Replace both the initialization and constraint clauses appropriately
                        init = Utilities.replace(init, varDecExp, dotExp);
                        constraint =
                                Utilities
                                        .replace(constraint, varDecExp, dotExp);

                    }
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
                DotExp isInitialExp =
                        Utilities.createInitExp(varDec, MTYPE, BOOLEAN);
                if (varLoc != null) {
                    Location loc = (Location) varLoc.clone();
                    loc.setDetails("Initial Value for "
                            + varDec.getName().getName());
                    Utilities.setLocation(isInitialExp, loc);
                }

                // Add to our assertive code as an assume
                myCurrentAssertiveCode.addAssume(isInitialExp);
            }

            // NY YS
            // Initialization duration for this variable
            if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
                ConfirmStmt finalConfirmStmt =
                        myCurrentAssertiveCode.getFinalConfirm();
                Exp finalConfirm = finalConfirmStmt.getAssertion();

                Location loc =
                        ((NameTy) varDec.getTy()).getName().getLocation();
                VarExp cumDur =
                        Utilities.createVarExp((Location) loc.clone(), null,
                                Utilities.createPosSymbol(Utilities
                                        .getCumDur(finalConfirm)),
                                myTypeGraph.R, null);
                Exp initDur = Utilities.createInitAnyDur(varDec, myTypeGraph.R);
                InfixExp sumInitDur =
                        new InfixExp((Location) loc.clone(), Exp.copy(cumDur),
                                Utilities.createPosSymbol("+"), initDur);
                sumInitDur.setMathType(myTypeGraph.R);

                finalConfirm =
                        Utilities.replace(finalConfirm, cumDur, sumInitDur);
                myCurrentAssertiveCode.setFinalConfirm(finalConfirm,
                        finalConfirmStmt.getSimplify());
            }

            // Verbose Mode Debug Messages
            myVCBuffer.append("\nVariable Declaration Rule Applied: \n");
            myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
            myVCBuffer.append("\n_____________________ \n");
        }
        else {
            // Ty not handled.
            Utilities.tyNotHandled(varDec.getTy(), varDec.getLocation());
        }
    }

    /**
     * <p>Applies the while statement rule.</p>
     *
     * @param stmt Our current <code>WhileStmt</code>.
     */
    private void applyWhileStmtRule(WhileStmt stmt) {
        // Obtain the loop invariant
        Exp invariant;
        boolean simplifyInvariant = false;
        if (stmt.getMaintaining() != null) {
            invariant = Exp.copy(stmt.getMaintaining());
            invariant.setMathType(stmt.getMaintaining().getMathType());

            // Simplify if we just have true
            if (invariant.isLiteralTrue()) {
                simplifyInvariant = true;
            }
        }
        else {
            invariant = myTypeGraph.getTrueVarExp();
            simplifyInvariant = true;
        }

        // NY YS
        // Obtain the elapsed time duration of loop
        Exp elapsedTimeDur = null;
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
            if (stmt.getElapsed_Time() != null) {
                elapsedTimeDur = Exp.copy(stmt.getElapsed_Time());
                elapsedTimeDur.setMathType(myTypeGraph.R);
            }
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
        Utilities.setLocation(baseCase, baseLoc);

        // NY YS
        // Confirm that elapsed time is 0.0
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)
                && elapsedTimeDur != null) {
            Exp initElapseDurExp = Exp.copy(elapsedTimeDur);
            Location initElapseLoc;
            if (elapsedTimeDur != null && elapsedTimeDur.getLocation() != null) {
                initElapseLoc = (Location) elapsedTimeDur.getLocation().clone();
            }
            else {
                initElapseLoc = (Location) elapsedTimeDur.getLocation().clone();
            }
            initElapseLoc
                    .setDetails("Base Case of Elapsed Time Duration of While Statement");
            Utilities.setLocation(initElapseDurExp, initElapseLoc);
            Exp zeroEqualExp =
                    new EqualsExp((Location) initElapseLoc.clone(),
                            initElapseDurExp, 1, Utilities.createVarExp(
                                    (Location) initElapseLoc.clone(), null,
                                    Utilities.createPosSymbol("0.0"),
                                    myTypeGraph.R, null));
            zeroEqualExp.setMathType(BOOLEAN);
            baseCase = myTypeGraph.formConjunct(baseCase, zeroEqualExp);
        }
        myCurrentAssertiveCode.addConfirm((Location) baseLoc.clone(), baseCase,
                simplifyInvariant);

        // Add the change rule
        if (stmt.getChanging() != null) {
            myCurrentAssertiveCode.addChange(stmt.getChanging());
        }

        // Assume the invariant and NQV(RP, P_Val) = P_Exp
        Location whileLoc = stmt.getLocation();
        Exp assume;
        Exp finalConfirm =
                myCurrentAssertiveCode.getFinalConfirm().getAssertion();
        boolean simplifyFinalConfirm =
                myCurrentAssertiveCode.getFinalConfirm().getSimplify();
        Exp decreasingExp = stmt.getDecreasing();
        Exp nqv;

        if (decreasingExp != null) {
            VarExp pval =
                    Utilities.createPValExp((Location) whileLoc.clone(),
                            myCurrentModuleScope);
            nqv = Utilities.createQuestionMarkVariable(finalConfirm, pval);
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

        // NY YS
        // Also assume NQV(RP, Cum_Dur) = El_Dur_Exp
        Exp nqv2 = null;
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)
                & elapsedTimeDur != null) {
            VarExp cumDurExp =
                    Utilities.createVarExp((Location) whileLoc.clone(), null,
                            Utilities.createPosSymbol("Cum_Dur"),
                            myTypeGraph.R, null);
            nqv2 =
                    Utilities.createQuestionMarkVariable(finalConfirm,
                            cumDurExp);
            nqv2.setMathType(cumDurExp.getMathType());
            Exp equalPExp =
                    new EqualsExp((Location) whileLoc.clone(), Exp.copy(nqv2),
                            1, Exp.copy(elapsedTimeDur));
            equalPExp.setMathType(BOOLEAN);
            assume = myTypeGraph.formConjunct(assume, equalPExp);
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
        Utilities.setLocation(inductiveCase, inductiveLoc);
        ifStmtList.add(new ConfirmStmt(inductiveLoc, inductiveCase,
                simplifyInvariant));

        // Confirm the termination of the loop.
        if (decreasingExp != null) {
            Location decreasingLoc =
                    (Location) decreasingExp.getLocation().clone();
            if (decreasingLoc != null) {
                decreasingLoc.setDetails("Termination of While Statement");
            }

            Exp infixExp =
                    Utilities.createLessThanExp(decreasingLoc, Exp
                            .copy(decreasingExp), Exp.copy(nqv), BOOLEAN);

            // Confirm NQV(RP, Cum_Dur) <= El_Dur_Exp
            if (nqv2 != null) {
                Location elapsedTimeLoc =
                        (Location) elapsedTimeDur.getLocation().clone();
                if (elapsedTimeLoc != null) {
                    elapsedTimeLoc.setDetails("Termination of While Statement");
                }

                Exp infixExp2 =
                        Utilities.createLessThanEqExp(elapsedTimeLoc, Exp
                                .copy(nqv2), Exp.copy(elapsedTimeDur), BOOLEAN);

                infixExp = myTypeGraph.formConjunct(infixExp, infixExp2);
                infixExp.setLocation(decreasingLoc);
            }

            ifStmtList.add(new ConfirmStmt(decreasingLoc, infixExp, false));
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

        // NY YS
        // Form the confirm clause for the else
        Exp elseConfirm = Exp.copy(finalConfirm);
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)
                & elapsedTimeDur != null) {
            Location loc = stmt.getLocation();
            VarExp cumDur =
                    Utilities.createVarExp((Location) loc.clone(), null,
                            Utilities.createPosSymbol(Utilities
                                    .getCumDur(elseConfirm)), myTypeGraph.R,
                            null);
            InfixExp sumWhileDur =
                    new InfixExp((Location) loc.clone(), Exp.copy(cumDur),
                            Utilities.createPosSymbol("+"), Exp
                                    .copy(elapsedTimeDur));
            sumWhileDur.setMathType(myTypeGraph.R);

            elseConfirm = Utilities.replace(elseConfirm, cumDur, sumWhileDur);
        }

        elseStmtList.add(new ConfirmStmt(elseConfirmLoc, elseConfirm,
                simplifyFinalConfirm));

        // condition
        ProgramExp condition = (ProgramExp) Exp.copy(stmt.getTest());
        if (condition.getLocation() != null) {
            Location condLoc = (Location) condition.getLocation().clone();
            condLoc.setDetails("While Loop Condition");
            Utilities.setLocation(condition, condLoc);
        }

        // add it back to your assertive code
        IfStmt newIfStmt =
                new IfStmt(condition, ifStmtList, elseIfPairList, elseStmtList);
        myCurrentAssertiveCode.addCode(newIfStmt);

        // Change our final confirm to "True"
        Exp trueVarExp = myTypeGraph.getTrueVarExp();
        trueVarExp.setLocation((Location) whileLoc.clone());
        myCurrentAssertiveCode.setFinalConfirm(trueVarExp, true);

        // Verbose Mode Debug Messages
        myVCBuffer.append("\nWhile Rule Applied: \n");
        myVCBuffer.append(myCurrentAssertiveCode.assertionToString());
        myVCBuffer.append("\n_____________________ \n");
    }
}