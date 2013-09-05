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
import edu.clemson.cs.r2jt.absyn.ConceptModuleDec;
import edu.clemson.cs.r2jt.absyn.EnhancementBodyModuleDec;
import edu.clemson.cs.r2jt.absyn.EnhancementModuleDec;
import edu.clemson.cs.r2jt.absyn.ProcedureDec;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.scope.OldSymbolTable;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;

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

    // Compile Environment
    private CompileEnvironment myInstanceEnvironment;

    // Utilties
    private Utilities myUtilities;

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

        myInstanceEnvironment = env;
        myUtilities = new Utilities(myInstanceEnvironment);

        /*
         * Check if we have set the flag for Verbose Output * if
         * (myInstanceEnvironment.flags.isFlagSet(FLAG_VERBOSE_VC)) {
         * myVerboseHandler = new
         * VerboseOutput(myInstanceEnvironment.getTargetFileName()); } else {
         * myVerboseHandler = null;
        }
         */
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
    // ProcedureDec
    // -----------------------------------------------------------

    @Override
    public void preProcedureDec(ProcedureDec dec) {

    }

    @Override
    public void postProcedureDec(ProcedureDec dec) {

    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     *
     * @param assertion This
     * <code>AssertiveCode</code> will be stored for later use and therefore
     * should be considered immutable after a call to this method.
     */
    /*
    private void applyEBRules(AssertiveCode assertion) {
        // Loop through all the assertions
        while (assertion.hasAnotherAssertion()) {
            // Obtain the last VerificationStatement
            VerificationStatement curAssertion = assertion.getLastAssertion();
            
            // Check if it is an ASSUME statement
            if (curAssertion.getType() == VerificationStatement.ASSUME) {
                applyAssumeRule(curAssertion, assertion);
            }
            // Check if it is a CONFIRM statement
            else if (curAssertion.getType() == VerificationStatement.CONFIRM) {
                applyConfirmRule(curAssertion, assertion);
            }
            // Check if it is a CODE statement
            else if (curAssertion.getType() == VerificationStatement.CODE) {
                visitEBCodeRule(curAssertion, assertion);
                
                // Don't do anything if it is a WHILE or IF statement
                if ((Statement) curAssertion.getAssertion() instanceof WhileStmt
                        || (Statement) curAssertion.getAssertion() instanceof IfStmt) {
                    return;
                }
            } 
            // Check if it is a REMEMBER statement
            else if (curAssertion.getType() == VerificationStatement.REMEMBER) {
                applyRememberRule(curAssertion, assertion);
            } 
            // Check if it is a VARIABLE declaration
            else if (curAssertion.getType() == VerificationStatement.VARIABLE) {
                applyVariableDeclRule(curAssertion, assertion);
            }
            // Check if it is a CHANGE statement
            else if (curAssertion.getType() == VerificationStatement.CHANGE) {
                applyChangeRule(curAssertion, assertion);
            } 
            // TODO should throw an exception here!!
            else {
            }
            
            // Apply simplyfication if the flag is set
            if (myInstanceEnvironment.flags.isFlagSet(FLAG_SIMPLIFY_VC)) {
                applySimplificationRules(assertion);
            }
        }
        
        
        assertion.setName(name);
        
        myFinalVCs.add(assertion);
        
        assrtBuf.append(assertion.assertionToString(true) + "\n\n");
        
        return;
    }*/
}