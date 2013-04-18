/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
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
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University. Contributors to the initial version are:
 * 
 * Steven Atkinson
 * Greg Kulczycki
 * Kunal Chopra
 * John Hunt
 * Heather Keown
 * Ben Markle
 * Kim Roche
 * Murali Sitaraman
 */
/*
 * Analyzer.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.analysis;

// replica
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Stack;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.entry.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.init.Environment;
import edu.clemson.cs.r2jt.location.DefinitionLocator;
import edu.clemson.cs.r2jt.location.ProofLocator;
import edu.clemson.cs.r2jt.location.SymbolSearchException;
import edu.clemson.cs.r2jt.location.TheoremLocator;
import edu.clemson.cs.r2jt.location.VariableLocator;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.proofchecking.ProofChecker;
import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.sanitycheck.SanityCheckException;
import edu.clemson.cs.r2jt.scope.*;
import edu.clemson.cs.r2jt.translation.PrettyJavaTranslator;
import edu.clemson.cs.r2jt.type.*;
import edu.clemson.cs.r2jt.utilities.Flag;

import java.util.Collection;

/*
 * XXX : TO DO:
 * 
 * Currently, when building up entries for definitions, you only get one entry
 * for an inductive definition. Originally it overwrote part (i) with part (ii)
 * of the definition. I have a hacked in work around, but really they should
 * each be a separate entry (at least the way ProofChecker is doing things).
 * -HwS
 */

public class Analyzer extends ResolveConceptualVisitor {

    private final static String SECTION_NAME = "General";

    /**
     * <p>Turns on full mathematical typechecking.  Currently typechecking is
     * performed on a best-effort basis in many places to support the prover,
     * but this flag will cause it to be performed everywhere and turn off the
     * "best effort" part, generating errors when its not possible.</p>
     */
    private final static String FLAG_DESC_TYPECHECK =
            "Perform full mathematical typechecking.";

    /**
     * <p>Turns on a number of sanity checks, including checking to make sure
     * that function calls use the correct number of arguments, and realizations
     * use appropriate parameter modes given their specifications.</p>
     */
    private final static String FLAG_DESC_SANITYCHECK =
            "Check for common errors.";

    public static final Flag FLAG_TYPECHECK =
            new Flag(SECTION_NAME, "typecheck", FLAG_DESC_TYPECHECK);
    public static final Flag FLAG_SANITYCHECK =
            new Flag(SECTION_NAME, "sanitycheck", FLAG_DESC_SANITYCHECK);

    // Variables 

    private ErrorHandler err;

    //private Environment env;
    private final CompileEnvironment myInstanceEnvironment;

    private OldSymbolTable table;

    private TypeMatcher tm = new TypeMatcher();

    private ProgramExpTypeResolver petr;

    private ProofChecker pc;

    private int origErrorCount;

    private List<String> myEncounteredProcedures;

    //HwS - Filthy hack.  These global variables keeps a pointer to the symbol
    //table of the concept associated with a realization that is currently
    //being parsed.  The name associated with the concept is also stored so
    //that the table can be retrieved lazily.
    private PosSymbol myCurrentModuleName = null;
    private OldSymbolTable myAssociatedConceptSymbolTable = null;

    // Stack of WhileStmts used for building the changing list
    private Stack<WhileStmt> whileStmts = new Stack<WhileStmt>();

    private ScopeRepository myManlySymbolTable;

    // Constructors

    /**
     * Constructs an analyzer.
     */
    public Analyzer(ScopeRepository aRealSymbolTable, OldSymbolTable table,
            CompileEnvironment instanceEnvironment) {
        myInstanceEnvironment = instanceEnvironment;

        this.table = table;
        this.err = instanceEnvironment.getErrorHandler();
        this.petr = new ProgramExpTypeResolver(table, instanceEnvironment);

        if (myInstanceEnvironment.flags.isFlagSet(ProofChecker.FLAG_PROOFCHECK)) {

            pc = new ProofChecker(table, tm, myInstanceEnvironment);
        }
        origErrorCount = err.getErrorCount();

        myEncounteredProcedures = new List<String>();
        err = instanceEnvironment.getErrorHandler();

        myManlySymbolTable = aRealSymbolTable;
    }

    public static void setUpFlags() {

    }

    public TypeMatcher getTypeMatcher() {
        return tm;
    }

    // Public Methods - Abstract Visit Methods

    public void visitModuleDec(ModuleDec dec) {
        //          TypeHolder holder = table.getTypeHolder();
        //          if (!holder.containsTypeB()) {
        //              String msg = mandMathTypeMessage();
        //              err.error(dec.getName().getLocation(), msg);
        //              return;
        //          }
        //          if (  dec instanceof ConceptBodyModuleDec ||
        //                dec instanceof EnhancementBodyModuleDec ||
        //                dec instanceof FacilityModuleDec) {
        //              if (  !holder.containsTypeN() ||
        //                    !holder.containsTypeBoolean() ||
        //                    !holder.containsTypeInteger()) {
        //                  String msg = mandProgTypeMessage();
        //                  err.error(dec.getName().getLocation(), msg);
        //                  return;
        //              }
        //          }
        dec.accept(this);
    }

    public void visitDec(Dec dec) {
        dec.accept(this);
    }

    public void visitStatement(Statement stmt) {
        stmt.accept(this);
    }

    //      public void visitExp(Exp exp) {
    //          if (exp == null) { return; }
    //          exp.accept(this);
    //      }

    public void visitModuleParameter(ModuleParameterDec par) {
        par.accept(this);
    }

    // Public Methods - Declarations

    // -----------------------------------------------------------
    // Module Declarations
    // -----------------------------------------------------------

    public void visitMathModuleDec(MathModuleDec dec) {
        table.beginModuleScope();
        visitModuleParameterList(dec.getParameters());
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_TYPECHECK)
                || myInstanceEnvironment.flags.isFlagSet(Prover.FLAG_PROVE)
                || myInstanceEnvironment.flags
                        .isFlagSet(Prover.FLAG_LEGACY_PROVE)) {

            visitDecList(dec.getDecs());
        }
        table.endModuleScope();
    }

    public void visitProofModuleDec(ProofModuleDec dec) {
        table.beginModuleScope();
        visitModuleParameterList(dec.getModuleParams());
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_TYPECHECK)) {
            visitDecList(dec.getDecs());
        }
        table.endModuleScope();
    }

    public void visitConceptModuleDec(ConceptModuleDec dec) {
        myCurrentModuleName = dec.getName();
        table.beginModuleScope();
        visitModuleParameterList(dec.getParameters());
        visitAssertion(dec.getRequirement());
        visitAssertionList(dec.getConstraints());
        if (dec.getFacilityInit() != null) {
            visitInitItem(dec.getFacilityInit());
        }
        if (dec.getFacilityFinal() != null) {
            visitFinalItem(dec.getFacilityFinal());
        }

        visitDecList(dec.getDecs());

        table.endModuleScope();
        myCurrentModuleName = null;
    }

    public void visitEnhancementModuleDec(EnhancementModuleDec dec) {
        table.beginModuleScope();
        visitModuleParameterList(dec.getParameters());
        visitAssertion(dec.getRequirement());

        visitDecList(dec.getDecs());

        table.endModuleScope();
    }

    public void visitConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        myCurrentModuleName = dec.getName();
        //We're going to need this deeper in the tree, so save it to a global
        //variable.  Would like to do this functionally, but I don't want to
        //break things by changing public method type signatures.  -HwS
        ModuleID id = ModuleID.createConceptID(dec.getConceptName());
        myAssociatedConceptSymbolTable =
                myInstanceEnvironment.getSymbolTable(id);

        table.beginModuleScope();
        visitModuleParameterList(dec.getParameters());
        visitAssertion(dec.getRequires());
        visitAssertionList(dec.getConventions());
        visitAssertionList(dec.getCorrs());
        if (dec.getFacilityInit() != null) {
            visitInitItem(dec.getFacilityInit());
        }
        if (dec.getFacilityFinal() != null) {
            visitFinalItem(dec.getFacilityFinal());
        }

        visitDecList(dec.getDecs());

        table.endModuleScope();
        myCurrentModuleName = null;
    }

    public void visitEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {

        myCurrentModuleName = dec.getName();
        //We're going to need this deeper in the tree, so save it to a global
        //variable.  Would like to do this functionally, but I don't want to
        //break things by changing public method type signatures.  -HwS
        ModuleID id =
                ModuleID.createEnhancementID(dec.getEnhancementName(), dec
                        .getConceptName());
        myAssociatedConceptSymbolTable =
                myInstanceEnvironment.getSymbolTable(id);

        table.beginModuleScope();
        // check instantiation of enhancement bodies
        //visitEnhancementBodyItemList(dec.getEnhancementBodies());
        visitModuleParameterList(dec.getParameters());
        visitAssertion(dec.getRequires());
        visitAssertionList(dec.getConventions());
        visitAssertionList(dec.getCorrs());
        if (dec.getFacilityInit() != null) {
            visitInitItem(dec.getFacilityInit());
        }
        if (dec.getFacilityFinal() != null) {
            visitFinalItem(dec.getFacilityFinal());
        }

        visitDecList(dec.getDecs());

        table.endModuleScope();
        myCurrentModuleName = null;

    }

    public void visitFacilityModuleDec(FacilityModuleDec dec) {
        myCurrentModuleName = dec.getName();
        table.beginModuleScope();
        if (dec.getFacilityInit() != null) {
            visitInitItem(dec.getFacilityInit());
        }
        if (dec.getFacilityFinal() != null) {
            visitFinalItem(dec.getFacilityFinal());
        }

        visitDecList(dec.getDecs());

        table.endModuleScope();
        myCurrentModuleName = null;
    }

    public void visitShortFacilityModuleDec(ShortFacilityModuleDec dec) {
        table.beginModuleScope();
        visitFacilityDec(dec.getDec());
        table.endModuleScope();
    }

    // -----------------------------------------------------------
    // Math Declarations
    // -----------------------------------------------------------

    private Boolean isDecAVar(DefinitionDec dec) {
        if (dec.getParameters() != null) {
            List<MathVarDec> params1 = dec.getParameters();
            Iterator<MathVarDec> i = params1.iterator();
            if (!i.hasNext()
                    && !(getMathType(dec.getReturnTy()) instanceof FunctionType)) {
                if (dec.getDefinition() == null && dec.getBase() == null
                        && dec.getHypothesis() == null) {
                    return true;
                }
            }
        }
        return false;
    }

    //    private List<Type> convertMathVarDecsToTypes(List<MathVarDec> decParams) {
    //    	List<Type> params = new List<Type>();
    //    	Iterator<MathVarDec> paramsIt = decParams.iterator();
    //    	while(paramsIt.hasNext()) {
    //    		MathVarDec currentParam = paramsIt.next();
    //    		Type currentType = getMathType(currentParam.getTy());
    //    		params.add(currentType);
    //    	}
    //    	return params;
    //    }

    public void visitDefinitionDec(DefinitionDec dec) {

        /*if (dec.getName().getName().equals("N") && !env.debugOff()) {
        	System.out.println("analysis.Analyzer.visitDefinitionDec found N");
        }*/

        Type returnType = getDefinitionReturnType(dec);

        table.beginDefinitionScope();

        doBaseStuff(dec, returnType);

        doHypothesisStuff(dec, returnType);

        if (dec.getDefinition() != null) {
            // If the defn. is null, don't typecheck
            storeValue(dec, dec.getDefinition());
        }
        table.endDefinitionScope();
    }

    private Exp unwrapQuantExp(Exp e) {
        if (e instanceof QuantExp) {
            return unwrapQuantExp(((QuantExp) e).getBody());
        }
        return e;
    }

    private boolean checkSelfReference(String name, Exp exp) {
        Exp newExp = unwrapQuantExp(exp);
        if (newExp instanceof IfExp) {
            return (findReference(name, ((IfExp) newExp).getThenclause()))
                    && ((findReference(name, ((IfExp) newExp).getElseclause())));
        }
        else if (newExp instanceof AlternativeExp) {
            Iterator<AltItemExp> it =
                    ((AlternativeExp) newExp).getAlternatives().iterator();
            while (it.hasNext()) {
                if (findReference(name, it.next().getAssignment()) == false)
                    return false;
            }
            return true;
        }
        return findReference(name, newExp);
    }

    private boolean findReference(String name, Exp exp) {
        if (exp == null || !(exp instanceof EqualsExp)) {
            return false;
        }
        if (((EqualsExp) exp).getOperator() != EqualsExp.EQUAL) {
            return false;
        }
        Exp LHS = ((EqualsExp) exp).getLeft();
        if (LHS instanceof VarExp) {
            return (name.equals(((VarExp) LHS).getName().getName()));
        }
        else if (LHS instanceof FunctionExp) {
            return (name.equals(((FunctionExp) LHS).getName().getName()));
        }
        else if (LHS instanceof InfixExp) {
            return (name.equals(((InfixExp) LHS).getOpName().getName()));
        }
        else if (LHS instanceof PrefixExp) {
            return (name.equals(((PrefixExp) LHS).getSymbol().getName()));
        }
        else if (LHS instanceof OutfixExp) {
            return matchOutfixOp(name, (OutfixExp) LHS);
        }
        return false;
    }

    private boolean matchOutfixOp(String name, OutfixExp exp) {
        return ((name.equals("<_>") && exp.getOperator() == OutfixExp.ANGLE)
                || (name.equals("<<_>>") && exp.getOperator() == OutfixExp.DBL_ANGLE)
                || (name.equals("[_]") && exp.getOperator() == OutfixExp.SQUARE)
                || (name.equals("[[_]]") && exp.getOperator() == OutfixExp.DBL_SQUARE)
                || (name.equals("|_|") && exp.getOperator() == OutfixExp.BAR) || (name
                .equals("||_||") && exp.getOperator() == OutfixExp.DBL_BAR));
    }

    private DefinitionEntry getDefinitionEntry(DefinitionDec d) {
        DefinitionEntry retval;

        DefinitionLocator locator = new DefinitionLocator(table, true, tm, err);
        PosSymbol searchKey = d.getName();
        String msg = "definition " + searchKey;

        try {
            retval = locator.locateDefinition(searchKey);
        }
        catch (SymbolSearchException e) {
            err.error("Could not located " + msg);
            retval = null;
        }

        return retval;
    }

    private void storeValue(Dec dec, Exp value) {
        if (myInstanceEnvironment.flags.isFlagSet(ProofChecker.FLAG_PROOFCHECK)) {

            String msg = null;
            try {
                if (dec instanceof DefinitionDec) {
                    DefinitionDec dec1 = ((DefinitionDec) dec);
                    DefinitionLocator locator =
                            new DefinitionLocator(table, true, tm, err);
                    PosSymbol searchKey = dec1.getName();
                    msg = "definition " + searchKey;
                    DefinitionEntry entry = locator.locateDefinition(searchKey);
                    entry.setValue(value);
                }
                else if (dec instanceof MathAssertionDec) {
                    MathAssertionDec dec1 = ((MathAssertionDec) dec);
                    TheoremLocator locator = new TheoremLocator(table, tm, err);
                    PosSymbol searchKey = dec1.getName();
                    int kind = dec1.getKind();
                    switch (kind) {
                    case MathAssertionDec.AXIOM:
                        msg = "axiom ";
                        break;
                    case MathAssertionDec.COROLLARY:
                        msg = "corollary ";
                        break;
                    case MathAssertionDec.LEMMA:
                        msg = "lemma ";
                        break;
                    case MathAssertionDec.PROPERTY:
                        msg = "property ";
                        break;
                    case MathAssertionDec.THEOREM:
                        msg = "theorem ";
                        break;
                    default:
                        msg = "math assertion ";
                        break;
                    }
                    msg += searchKey;
                    TheoremEntry entry = locator.locateTheorem(searchKey);
                    entry.setValue(value);
                }
                else if (dec instanceof ProofDec) {
                    ProofDec dec1 = ((ProofDec) dec);
                    ProofLocator locator = new ProofLocator(table, tm, err);
                    PosSymbol searchKey = dec1.getName();
                    msg = "proof " + searchKey;
                    ProofEntry entry = locator.locateProof(searchKey);
                    entry.setValue(value);
                }
                else {
                    return;
                }
            }
            catch (SymbolSearchException ex) {
                err.error("Could not locate the " + msg);
            }
        }
    }

    public void visitMathAssertionDec(MathAssertionDec dec) {
        table.beginExpressionScope();
        storeValue(dec, dec.getAssertion());
        table.endExpressionScope();
    }

    public void visitMathTypeDec(MathTypeDec dec) {
        ;
    }

    public void visitMathTypeFormalDec(MathTypeFormalDec dec) {
        ;
    }

    // -----------------------------------------------------------
    // Type Declarations
    // -----------------------------------------------------------

    public void visitFacilityTypeDec(FacilityTypeDec dec) {
        table.beginTypeScope();
        visitAssertion(dec.getConvention());
        if (dec.getInitialization() != null) {
            visitInitItem(dec.getInitialization());
        }
        if (dec.getFinalization() != null) {
            visitFinalItem(dec.getFinalization());
        }
        table.endTypeScope();
    }

    public void visitTypeDec(TypeDec dec) {
        table.beginTypeScope();
        visitAssertion(dec.getConstraint());
        if (dec.getInitialization() != null) {
            visitInitItem(dec.getInitialization());
        }
        if (dec.getFinalization() != null) {
            visitFinalItem(dec.getFinalization());
        }
        table.endTypeScope();
    }

    public void visitRepresentationDec(RepresentationDec dec) {
        table.beginTypeScope();
        visitAssertion(dec.getConvention());
        visitAssertion(dec.getCorrespondence());
        if (dec.getInitialization() != null) {
            visitInitItem(dec.getInitialization());
        }
        if (dec.getFinalization() != null) {
            visitFinalItem(dec.getFinalization());
        }
        table.endTypeScope();
    }

    // -----------------------------------------------------------
    // Operation Declarations
    // -----------------------------------------------------------

    public void visitFacilityOperationDec(FacilityOperationDec dec) {

        table.beginOperationScope();
        visitAssertion(dec.getRequires());
        visitAssertion(dec.getEnsures());
        table.beginProcedureScope();
        table.bindProcedureTypeNames();
        visitProgressMetric(dec.getDecreasing());
        visitFacilityDecList(dec.getFacilities());
        visitStatementList(dec.getStatements());
        table.endProcedureScope();
        table.endOperationScope();
    }

    public void visitOperationDec(OperationDec dec) {

        table.beginOperationScope();
        visitAssertion(dec.getRequires());
        visitAssertion(dec.getEnsures());
        table.endOperationScope();
    }

    public void visitProcedureDec(ProcedureDec dec) {

        table.beginOperationScope();
        table.beginProcedureScope();
        table.bindProcedureTypeNames();

        //This was added to do sanity check work, but this work is now handled
        //by sanityCheckProcedureArguments:
        //visitParameterVarDecList(dec.getParameters());
        visitProgressMetric(dec.getDecreasing());
        visitFacilityDecList(dec.getFacilities());
        visitStatementList(dec.getStatements());
        table.endProcedureScope();
        table.endOperationScope();
    }

    // -----------------------------------------------------------
    // Proof Declarations
    // -----------------------------------------------------------

    private void visitProofExp(Exp e) {
        // type check
        visitAssertion(e);
        // proof check
        if (origErrorCount != err.getErrorCount())
            return;

        if (myInstanceEnvironment.flags.isFlagSet(ProofChecker.FLAG_PROOFCHECK)) {

            pc.proofcheck(e);
        }
    }

    public void visitProofDec(ProofDec dec) {
        table.beginProofScope();
        table.bindProofTypeNames();
        Iterator<Exp> it = dec.getStatements().iterator();
        Exp e = null;
        //    	try {
        while (it.hasNext()) {
            e = it.next();
            if (e instanceof ProofDefinitionExp) {
                visitProofDefinitionExp((ProofDefinitionExp) e);
            }
            else {
                visitProofExp(e);
            }
        }
        if (dec.getBaseCase().size() != 0) {
            it = dec.getBaseCase().iterator();
            while (it.hasNext()) {
                e = it.next();
                if (e instanceof ProofDefinitionExp) {
                    visitProofDefinitionExp((ProofDefinitionExp) e);
                }
                else {
                    visitProofExp(e);
                }
            }
        }
        if (dec.getInductiveCase().size() != 0) {
            it = dec.getInductiveCase().iterator();
            while (it.hasNext()) {
                e = it.next();
                if (e instanceof ProofDefinitionExp) {
                    visitProofDefinitionExp((ProofDefinitionExp) e);
                }
                else {
                    visitProofExp(e);
                }
            }
        }
        //    	}
        //    	catch(TypeResolutionException trex) {
        //    		// Error already handled; do nothing
        //    	}
        table.endProofScope();
    }

    public void visitProofDefinitionExp(ProofDefinitionExp dec) {
        visitDefinitionDec(dec.getExp());
    }

    // -----------------------------------------------------------
    // Facility Declarations
    // -----------------------------------------------------------

    public void visitFacilityDec(FacilityDec dec) {

    }

    // -----------------------------------------------------------
    // Module Parameter Declarations
    // -----------------------------------------------------------

    public void visitConceptTypeParamDec(ConceptTypeParamDec dec) {
        ;
    }

    public void visitConstantParamDec(ConstantParamDec dec) {
        ;
    }

    public void visitRealizationParamDec(RealizationParamDec dec) {
    //FIX: Postpone this till later, since it doesn't even
    //     work like we want it to.
    }

    // Public Methods - Non-declarative Constructs

    public void visitAffectsItem(AffectsItem item) {
    // locate variables - make sure they are permitted here.
    }

    public void visitChoiceItem(ChoiceItem item) {
    // figure out what this does
    }

    public void visitConditionItem(ConditionItem item) {
        visitCondition(item.getTest());
        visitStatementList(item.getThenclause());
    }

    public void visitEnhancementBodyItem(EnhancementBodyItem item) {
    // check argument lists
    }

    public void visitEnhancementItem(EnhancementItem item) {
    // check argument lists
    }

    public void visitFinalItem(FinalItem item) {

        table.beginOperationScope();
        visitAssertion(item.getRequires());
        visitAssertion(item.getEnsures());
        table.beginProcedureScope();
        visitFacilityDecList(item.getFacilities());
        visitStatementList(item.getStatements());
        table.endProcedureScope();
        table.endOperationScope();
    }

    public void visitFunctionArgList(FunctionArgList list) {
    //visitExpList(list.getArguments());
    }

    public void visitInitItem(InitItem item) {
        table.beginOperationScope();
        visitAssertion(item.getRequires());
        visitAssertion(item.getEnsures());
        table.beginProcedureScope();
        visitFacilityDecList(item.getFacilities());
        visitStatementList(item.getStatements());
        table.endProcedureScope();
        table.endOperationScope();
    }

    /* Called instead of visitAssertion */
    public void visitMaintainingClause(Exp exp) {
        //metr.setMaintainingClause();
        visitAssertion(exp);
        //metr.unsetMaintainingClause();
    }

    public void visitRenamingItem(RenamingItem item) {
    // check if item renamed is in the facility ?
    }

    public void visitUsesItem(UsesItem item) {
        ;
    }

    // Statements

    public void visitCallStmt(CallStmt stmt) {

    }

    /*
     * XXX : Why is a FuncAssignStmt different from an ordinary AssignStmt???
     *               -HwS
     */
    public void visitFuncAssignStmt(FuncAssignStmt stmt) {

        //XXX : This is the only way to get the internal SemanticExp set for
        //      a VariableDotExp and so it must happen regardless of whether
        //      or not we're typechecking if we want to translate.  This seems
        //      like an odd restriction.  Can setting up SemanticExps be moved
        //      to a more rational location?  -HwS
        //ProgramExpTypeResolver petr = new ProgramExpTypeResolver(table);
        TypeMatcher matcher = new TypeMatcher();
        try {
            Type vtype = petr.getVariableExpType(stmt.getVar());

            Type atype = petr.getProgramExpType(stmt.getAssign()); // Problem is here

            if (myInstanceEnvironment.flags.isFlagSet(FLAG_TYPECHECK)) {
                if (!matcher.programMatches(vtype, atype)) {
                    Location loc = stmt.getAssign().getLocation();
                    String msg =
                            expectedDiffTypeMessage(vtype.getRelativeName(loc),
                                    atype.getRelativeName(loc));
                    err.error(loc, msg);
                    return;
                }
            }
            if (!myInstanceEnvironment.flags
                    .isFlagSet(PrettyJavaTranslator.FLAG_TRANSLATE)) {
                // Making sure that we do not have something of VariableExp on the right hand side
                // in a function assignment. - YS
                if (petr.isVariable(stmt.getAssign())) {
                    String msg =
                            "Right hand side of the function assignment cannot be a variable expression! ";
                    err.error(stmt.getAssign().getLocation(), msg);
                    throw new TypeResolutionException();
                }

                // Making sure that for any entry replica call for arrays, we have a replica function
                // defined for that type. - YS
                if (stmt.getAssign() instanceof ProgramParamExp) {
                    stmt.getAssign().accept(this);
                }
            }
        }
        catch (TypeResolutionException trex) {
            // do nothing - the error was already reported
        }
    }

    public void visitIfStmt(IfStmt stmt) {

        visitCondition(stmt.getTest());
        visitStatementList(stmt.getThenclause());
        visitConditionItemList(stmt.getElseifpairs());
        visitStatementList(stmt.getElseclause());
    }

    public void visitIterateStmt(IterateStmt stmt) {
        table.beginStatementScope();
        visitMaintainingClause(stmt.getMaintaining());
        visitProgressMetric(stmt.getDecreasing());
        visitStatementList(stmt.getStatements());
        table.endStatementScope();
    }

    public void visitIterateExitStmt(IterateExitStmt stmt) {
        visitCondition(stmt.getTest());
        visitStatementList(stmt.getStatements());
    }

    public void visitMemoryStmt(MemoryStmt stmt) {
        ;
    }

    //      public void visitSelectionStmt(SelectionStmt stmt) {
    //          visitVariableExp(stmt.getVar());
    //          visitChoiceItemList(stmt.getWhenpairs());
    //          visitStatementList(stmt.getDefaultclause());
    //      }

    public void visitSwapStmt(SwapStmt stmt) {
        //XXX : This is the only way to get the internal SemanticExp set for
        //      a VariableDotExp and so it must happen regardless of whether
        //      or not we're typechecking if we want to translate.  This seems
        //      like an odd restriction.  Can setting up SemanticExps be moved
        //      to a more rational location?  -HwS
        Type vtype1 = null;
        Type vtype2 = null;
        //ProgramExpTypeResolver petr = new ProgramExpTypeResolver(table);
        try {
            vtype1 = petr.getVariableExpType(stmt.getLeft());
            vtype2 = petr.getVariableExpType(stmt.getRight());

            if (myInstanceEnvironment.flags.isFlagSet(FLAG_TYPECHECK)) {
                TypeMatcher matcher = new TypeMatcher();

                if (!matcher.programMatches(vtype1, vtype2)) {
                    Location loc = stmt.getRight().getLocation();
                    String msg =
                            expectedDiffTypeMessage(
                                    vtype1.getRelativeName(loc), vtype2
                                            .getRelativeName(loc));
                    err.error(loc, msg);
                }
            }
        }
        catch (TypeResolutionException e) {
            err.error(stmt.getLocation(), "TypeResolutionException");
            //do nothing - the error was already reported
        }
    }

    public void visitWhileStmt(WhileStmt stmt) {

        table.beginStatementScope();
        visitCondition(stmt.getTest());
        visitMaintainingClause(stmt.getMaintaining());
        visitProgressMetric(stmt.getDecreasing());

        // We will now dynamically build the changing statement for the user.
        whileStmts.push(stmt);
        visitStatementList(stmt.getStatements());
        whileStmts.pop();
        table.endStatementScope();
    }

    // Public Methods - Expressions

    /*
     * Expressions are visited for the purpose of getting their
     * types. Therefore, expressions are analyzed in the
     * ExpTypeResolver, a class that extends TypeResolutionVisitor,
     * whose signature differs from ResolveConceptualVisitor in that
     * it returns a Type or throws a TypeResolutionException if a type
     * cannot be determined.
     */

    // Private Methods

    /*
     * XXX : I have no idea what the heck this code is meant to do.  I don't 
     * know what a definition's "base" is, just moving it here from
     * visitDefinitionDec to improve clarity. -HwS
     */
    private void doBaseStuff(DefinitionDec dec, Type definitionReturnType) {
        if (dec.getBase() != null) {
            if (!checkSelfReference(dec.getName().getName(), dec.getBase())) {
                String msg = noSelfReference();
                err.error(dec.getBase().getLocation(), msg);
            }

            DefinitionEntry definition = getDefinitionEntry(dec);

            if (definition != null) {
                definition.setBaseDefinition(dec.getBase());
                //storeValue(dec, dec.getBase());
            }
        }
    }

    /*
     * XXX : Again, no idea what this means.  Moved out of visitDefinitionDec.
     *          -HwS
     */
    private void doHypothesisStuff(DefinitionDec dec, Type definitionReturnType) {
        if (dec.getHypothesis() != null) {
            if (!checkSelfReference(dec.getName().getName(), dec
                    .getHypothesis())) {

                String msg = noSelfReference();
                err.error(dec.getHypothesis().getLocation(), msg);
            }
            storeValue(dec, dec.getHypothesis());
        }
    }

    /**
     * A helper function for visitDefinitionDec.
     * 
     * Takes a DefinitionDec and returns the math type returned by that
     * definition.
     */
    private Type getDefinitionReturnType(DefinitionDec dec) {

        //All hail the new type system!  Hail!  Hail!
        return new NewMathType(dec.getReturnTy().getMathTypeValue());
    }

    // -----------------------------------------------------------
    // Symbol Table searching methods
    // -----------------------------------------------------------
    private DefinitionEntry getDefinitionByName(Symbol name)
            throws NotFoundException {
        DefinitionEntry retval;

        ModuleScope curModuleScope = table.getModuleScope();
        if (curModuleScope.containsDefinition(name)) {
            retval = curModuleScope.getDefinition(name);
        }
        else {
            throw new NotFoundException("Couldn't find definition: " + name);
        }

        return retval;
    }

    private VarEntry getVariableByName(Symbol name) throws NotFoundException {
        VarEntry retval;

        ModuleScope curModuleScope = table.getModuleScope();
        if (curModuleScope.containsVariable(name)) {
            retval = curModuleScope.getVariable(name);
        }
        else {
            throw new NotFoundException("Couldn't find variable: " + name);
        }

        return retval;
    }

    // -----------------------------------------------------------
    // Iterative Visit Methods
    // -----------------------------------------------------------

    private void visitAssertionList(List<Exp> exps) {
        Iterator<Exp> i = exps.iterator();
        while (i.hasNext()) {
            visitAssertion(i.next());
        }
    }

    private void visitChoiceItemList(List<ChoiceItem> items) {
        Iterator<ChoiceItem> i = items.iterator();
        while (i.hasNext()) {
            visitChoiceItem(i.next());
        }
    }

    private void visitConditionItemList(List<ConditionItem> items) {
        Iterator<ConditionItem> i = items.iterator();
        while (i.hasNext()) {
            visitConditionItem(i.next());
        }
    }

    private void visitConditionList(List<ProgramExp> exps) {
        Iterator<ProgramExp> i = exps.iterator();
        while (i.hasNext()) {
            visitCondition(i.next());
        }
    }

    private void visitDecList(List<Dec> decs) {
        Iterator<Dec> i = decs.iterator();
        while (i.hasNext()) {
            visitDec(i.next());
        }
    }

    //      private void visitExpList(List<Exp> exps) {
    //          Iterator<Exp> i = exps.iterator();
    //          while (i.hasNext()) {
    //              visitExp(i.next());
    //          }
    //      }

    private void visitFacilityDecList(List<FacilityDec> decs) {
        Iterator<FacilityDec> i = decs.iterator();
        while (i.hasNext()) {
            visitFacilityDec(i.next());
        }
    }

    private void visitModuleParameterList(List<ModuleParameterDec> pars) {
        Iterator<ModuleParameterDec> i = pars.iterator();
        while (i.hasNext()) {
            visitModuleParameter(i.next());
        }
    }

    private void visitStatementList(List<Statement> stmts) {
        if (stmts != null) {
            Iterator<Statement> i = stmts.iterator();
            while (i.hasNext()) {
                visitStatement(i.next());
            }
        }
    }

    private void visitUsesItemList(List<UsesItem> items) {
        Iterator<UsesItem> i = items.iterator();
        while (i.hasNext()) {
            visitUsesItem(i.next());
        }
    }

    // -----------------------------------------------------------
    // Expression Related Methods
    // -----------------------------------------------------------

    /* Skipping analysis of math expressions for the time being. */
    //    private void visitExpOfType(Exp exp, Type type) { ; }

    //    private void visitAssertion(Exp exp) { }

    /**
     * Visits all ProgramOpExp subexpressions
     */
    public void visitProgramOpExp(ProgramOpExp exp) {
        Iterator<Exp> it = exp.getSubExpressions().iterator();
        while (it.hasNext()) {
            it.next().accept(this);
        }
    }

    /**
     * Visits ProgramParamExp
     */
    public void visitProgramParamExp(ProgramParamExp exp) {
        /* Check if the call statement is a Entry_Replica call */
        if (exp.getName().getName().equals("Entry_Replica")) {
            /* List of arguments */
            List<ProgramExp> argList = exp.getArguments();

            try {
                /* Call the ProgramExp type checker to go find a Replica operation */
                Type xtype = petr.getProgramExpType(argList.get(0));
                petr.checkReplica(argList.get(0).getLocation(), xtype);
            }
            catch (TypeResolutionException e) {
                /* Catch the error and print the message */
                String msg = "Illegal array access, use swap instead.";
                err.error(exp.getLocation(), msg);
            }
        }
    }

    private void visitAssertion(Exp exp) {

    }

    private void visitProgressMetric(Exp exp) {

    }

    private void visitProgramExpOfType(ProgramExp exp, Type type) {
        //ProgramExpTypeResolver petr = new ProgramExpTypeResolver(table);
        TypeMatcher matcher = new TypeMatcher();
        try {
            Type etype = petr.getProgramExpType(exp);
            if (!matcher.programMatches(etype, type)) {
                /*Location loc = exp.getLocation();
                String msg =
                        expectedDiffTypeMessage(etype.getRelativeName(loc),
                                type.getRelativeName(loc));
                err.error(loc, msg);*/
            }
            exp.setType(etype);
        }
        catch (TypeResolutionException trex) {
            err.error(exp.getLocation(), "TypeResolutionException");
            // do nothing - the error was already reported
        }
    }

    private void visitCondition(ProgramExp exp) {
        TypeHolder holder = table.getTypeHolder();
        if (holder.containsTypeBoolean()) {
            visitProgramExpOfType(exp, holder.getTypeBoolean());
        }
        else {
            String msg = cantFindType("Std_Boolean_Fac.Boolean");
            err.error(exp.getLocation(), msg);
        }

        // Making sure that for any entry replica call for arrays, we have a replica function
        // defined for that type. - YS
        exp.accept(this);
    }

    public Type getMathType(Ty ty) {
        TypeConverter tc = new TypeConverter(table);
        return tc.getMathType(ty);
    }

    // -----------------------------------------------------------
    // Error Related Methods
    // -----------------------------------------------------------

    /**
     * Returns a well-formatted error message indicating that the wrong number
     * of arguments were provided.
     * 
     * @return The error message.
     */
    private String wrongNumberOfArgumentsMessage() {
        return "Wrong number of arguments.";
    }

    /**
     * Returns a well-formatted error message indicating that no enhancement
     * realization exists with the given name for the given enhancement and
     * concept.
     * 
     * @param realization The realization that cannot be found.
     * @param enhancement The enhancement for which it cannot be found.
     * @param concept The name of the concept for which it cannot be found.
     *
     * @return The error message.
     */
    private String noSuchEnhancementRealization(String realizationName,
            String enhancementName, String conceptName) {

        return "Cannot find a realization called " + realizationName
                + " for enhancement " + enhancementName + " to concept "
                + conceptName + ".";
    }

    /**
     * Returns a well-formatted error message indicating that no enhancement
     * exists with the given name for the given concept.
     * 
     * @param enhancement The enhancement that cannot be found.
     * @param concept The name of the concept for which it cannot be found.
     * 
     * @return The error message.
     */
    private String noSuchEnhancementConcept(String enhancementName,
            String conceptName) {

        return "Cannot find an enhancement " + enhancementName + " to "
                + " concept " + conceptName + ".";
    }

    private String expectedProcMessage() {
        return "Found a function where a procedure was expected.";
    }

    private String expectedDiffTypeMessage(String t1, String t2) {
        return "  Expected type: " + t1 + "\n" + "  Found type: " + t2;
    }

    //      private String mandMathTypeMessage() {
    //          return "The type B must be visible to all modules.";
    //      }

    //      private String mandProgTypeMessage() {
    //          return "The types N, Boolean, and Integer must be visible "
    //              + "to all implementation modules.";
    //      }

    private String cantFindType(String name) {
        return "The type " + name + " is not visible from this module.";
    }

    private String facDecErrorMessage(String name) {
        return "Facility declaration error: " + name;
    }

    private String noSelfReference() {
        return "The inductive definition does not reference itself.";
    }

    /** 
     * Returns a well-formatted error message indicating that a parameter
     * of a procedure is defined using a mode incompatible with the
     * corresponding parameter of the corresponding operation definition.
     * 
     * Assumes <code>myCurrentConceptName</code> has been set appropriately.
     * 
     * @param operationMode The mode of the corresponding parameter in the
     *                      corresponding operation.
     * @param procedureMode The mode of the procedure parameter whose mode is
     *                      incompatible.
     *                      
     * @return The error message.
     */
    private String incompatibleParameterModes(Mode operationMode,
            Mode procedureMode) {

        return "Corresponding parameter in " + myCurrentModuleName
                + " is in mode '" + operationMode + "'.  Here, this parameter "
                + "is implemented with mode '" + procedureMode + "'.  This is "
                + "not allowed.";
    }

    /**
     * Returns a well-formatted error message indicating that a parameter
     * of a procedure is defined with an incompatible type to its corresponding
     * parameter in the corresponding operation definition.
     * 
     * Assumes <code>myCurrentConceptName</code> has been set appropriately.
     * 
     * @param operationType The type of the corresponding parameter in the
     *                      corresponding operation.
     * @param procedureType The type of the procedure parameter whose 
     *                      <code>Type</code> is incompatible.
     *                      
     * @return The error message.
     */
    private String incompatibleParameterTypes(String iName, Type operationType,
            Type procedureType) {

        return "Incorrect parameter type " + procedureType.toString()
                + " in procedure, does not match " + "type "
                + operationType.toString() + " in corresponding operation of "
                + iName + ":";
        /*return "Corresponding parameter in " + myCurrentConceptName +
        	" is of type " + operationType + " and does not match the " +
        	"type used here (" + procedureType + ").";*/
    }

    /**
     * Returns a well-formatted error message indicating that the number of
     * parameters given for a procedure does not match the number of parameters
     * given for its corresponding operation.
     * 
     * @return The error message.
     */
    private String procedureParameterCountDoesNotMatchOperation(String iName) {
        return "The number of arguments in this procedure does not match those in "
                + "the corresponding operation in " + iName + ":";
        /*return "Corresponding operation in " + 
        	myCurrentConceptName + " does not have the same number of " +
        	"arguments as this procedure.";*/
    }

    /**
     * Returns a well-formatted error message indicating that the procedure
     * with the given name does not have a corresponding operation definition.
     * 
     * @return The error message.
     */
    private String noMatchingOperation(Symbol name) {
        return "No operation named " + name + " to match this procedure in "
                + "concept " + myCurrentModuleName + ".";
    }

    /**
     * Returns a well-formatted error message indicating that an expression
     * was provided as an argument where a definition was expeted.
     * 
     * @return The error message.
     */
    private String expressionGivenWhereDefinitionExpected() {
        return "Expression given where a definition argument was expected.";
    }

    /**
     * Returns a well-formatted error message indicating that an expression
     * was provided as an argument where a type was expeted.
     * 
     * @return The error message.
     */
    private String expressionGivenWhereTypeExpected() {
        return "Expression given where a type name was expected.";
    }

    /**
     * Returns a well-formatted error message indicating that a variable was
     * given for a parameter that expected a definition name, but no such
     * definition exists.
     */
    private String noSuchDefinition(Symbol name) {
        return "This parameter expects a definition.  No definition found "
                + "named '" + name + "'.";
    }

    /**
     * Returns a well-formatted error message indicating that a variable was
     * given for a parameter, but no variable with that name can be found.
     * 
     * @return The error message.
     */
    private String noSuchVariableName(Symbol name) {
        return "This parameter expects a variable.  No variable found "
                + "named '" + name + "'.";
    }

    /**
     * Returns a well-formatted error message indicating that a definition
     * provided as an argument is not of the proper type.
     * 
     * @return The error message.
     */
    private String incompatibleDefinitionTypesMessage(Type expected, Type given) {
        return "Expected a definition with type: " + expected + "\n"
                + "Given a definition with type: " + given;
    }

    /** Returns a well-formatted error message indicating that an expression
     * was provided where an operation was expected.
     * 
     * @return The error message.
     */
    private String expressionFoundWhereOperationExpectedMessage() {
        return "Found an expression where a definition was expected.";
    }

    /**
     * Returns a well-formatted error message indicating that an operation
     * has too few arguments.
     * 
     * @return The error message.
     */
    private String operationHasTooFewArgumentsMessage() {
        return "Operation has too few arguments.";
    }

    /**
     * Returns a well-formatted error message indicating that an operation
     * has too many arguments.
     * 
     * @return The error message.
     */
    private String operationHasTooManyArgumentsMessage() {
        return "Operation has too many arguments.";
    }

    /**
     * Returns a well-formatted error message that encapsulates a secondary
     * error message indicating that one of the parameters of an operation
     * provided as an argument raised an error.
     * 
     * @param problemParameterIndex The index of the parameter that caused the
     *                              trouble, indexed from 1.
     * @param problem The inner error message.
     * 
     * @return The full error message.
     */
    private String problemWithProvidedOperationMessage(
            int problemParameterIndex, String problem) {
        return "Parameter " + problemParameterIndex + " (counting from 1) "
                + "of provided operation caused the following error:\n"
                + problem;
    }

    /** 
     * Returns a well-formatted error message indicating that the name of a
     * definition was expected.
     * 
     * @return The error message.
     */
    private String expectedDefinitionMessage() {
        return "Expected definition name.";
    }

    /**
     * Returns a well-formatted error message indicating that a definition was
     * provided where none was expected.
     * 
     * @return The error message.
     */
    private String noDefinitionExpectedMessage() {
        return "Not expecting a definition.";
    }

    private String changeNotPermitted(String varName) {
        return varName + " does no appear in the changing clause and "
                + "therefore cannot be modified.";
    }

    /*private String operationNotFoundMessage(String enhancementRealizationName) {
    	return "This operation not implemented in the enhancement " +
    		"realization " + enhancementRealizationName + ".";
    }*/

    /**
     * This method creates an error message made up of the name of the realization
     * and the prototype(s) of the missing procedure(s).
     * 
     * @param name Name of realization with missing procedure(s)
     * @param iName Name of the concept/enhancement this realization is associated with
     * @param syms List of missing procedures
     * @return
     */
    private String foundMissingProceduresMessage(String name, String iName,
            List<OperationEntry> syms) {
        String msg = "\n" + name;
        Boolean plural = (syms.size() != 1);
        if (!plural)
            msg += " is missing a required procedure; ";
        else
            msg += " is missing required procedures; ";
        if (!plural)
            msg += iName + " also requires an implementation of:\n";
        else
            msg += iName + " also requires implementations of:\n";
        OperationEntry entry;
        Iterator<OperationEntry> it = syms.iterator();
        while (it.hasNext()) {
            entry = it.next();
            msg +=
                    err.printErrorLine(entry.getLocation().getFile(), entry
                            .getLocation().getPos());
        }
        return msg;
    }

    // -----------------------------------------------------------
    // Various Sanity-Check Helper Methods
    // -----------------------------------------------------------

    /**
     * Returns the mathematical types associated with a series of parameters
     * stored in a Collection of ParameterVarDecs.  Since the type information
     * inside a ParameterVarDec is of type 'Ty', each is coerced into something
     * of type 'Type' by the <code>getMathType(Ty t)</code> method of the
     * analyzer's MathExpTypeResolver (which is a global variable called 'metr'.
     * 
     * @param parameters A Collection of ParameterVarDecs representing each
     *                   of the parameters in order.
     *                   
     * @return A <code>List</code> of <code>Type</code>s wherein the first 
     *         element corresponds to the type of the first parameter in 
     *         <code>parameters</code>, the second to the second, and so forth.
     */
    private List<Type> getParameterVarDecTypes(
            Collection<ParameterVarDec> parameters) {

        List<Type> parameterTypes = new List<Type>();

        Type wrkType = null;

        for (ParameterVarDec p : parameters) {
            parameterTypes.add(new NewMathType(p.getMathType()));
        }

        return parameterTypes;
    }

    /**
     * Returns the parameter modes associated with a series of parameters
     * stored in a Collection of ParameterVarDecs.  
     * 
     * @param parameters A Collection of ParameterVarDecs representing each
     *                   of the parameters in order.
     *                   
     * @return A <code>List</code> of <code>Mode</code>s wherein the first 
     *         element corresponds to the mode of the first parameter in 
     *         <code>parameters</code>, the second to the second, and so forth.
     */
    private List<Mode> getParameterVarDecModes(
            Collection<ParameterVarDec> parameters) {

        List<Mode> parameterModes = new List<Mode>();

        for (ParameterVarDec p : parameters) {
            parameterModes.add(p.getMode());
        }

        return parameterModes;
    }

    /**
     * Returns the types associated with a series of parameters
     * stored in an <code>Iterator</code> of <code>VarEntry</code>s.  Since the 
     * type information inside a <code>VarEntry</code> is already of type 
     * <code>Type</code>, it is copied directly into the final list (contrast
     * this with the method used in getParameterVarDecTypes.)
     * 
     * @param parameters A <code>Collection</code> of <code>VarEntry</code>s 
     *                   representing each of the parameters in order.
     *                   
     * @return A <code>List</code> of <code>Type</code>s wherein the first 
     *         element corresponds to the type of the first parameter in 
     *         <code>parameters</code>, the second to the second, and so forth.
     */
    private List<Type> getParameterVarEntryTypes(Iterator<VarEntry> parameters) {

        List<Type> parameterTypes = new List<Type>();

        VarEntry curParameter;
        while (parameters.hasNext()) {
            curParameter = parameters.next();
            parameterTypes.add(curParameter.getType());
        }

        return parameterTypes;
    }

    /**
     * Returns the parameter modes associated with a series of parameters
     * stored in an <code>Iterator</code> of <code>VarEntry</code>s.
     * 
     * @param parameters A <code>Collection</code> of <code>VarEntry</code>s 
     *                   representing each of the parameters in order.
     *                   
     * @return A <code>List</code> of <code>Mode</code>s wherein the first 
     *         element corresponds to the type of the first parameter in 
     *         <code>parameters</code>, the second to the second, and so forth.
     */
    private List<Mode> getParameterVarEntryModes(Iterator<VarEntry> parameters) {

        List<Mode> parameterModes = new List<Mode>();

        VarEntry curParameter;
        while (parameters.hasNext()) {
            curParameter = parameters.next();
            parameterModes.add(curParameter.getMode());
        }

        return parameterModes;
    }

    /**
     * This method retrieves an operation as an <code>OperationEntry</code>
     * from the current associated concept symbol table (which means that 
     * makeAssociatedConceptSymbolTableAvailable MUST be called before calling
     * this method.)  Throws a SanityCheckException if the named operation is
     * not found in the symbol table.
     * 
     * @param name The name of the operation to retrieve, as a 
     *             <code>Symbol</code>.
     *             
     * @return The OperationEntry associated with <code>name</code> in the
     *         symbol table of the current associated concept.
     *         
     * @throws SanityCheckException If an operation with the given name cannot
     *                              be found.
     */
    private OperationEntry getConceptOperation(Symbol name)
            throws SanityCheckException {

        ModuleScope conceptModuleScope =
                myAssociatedConceptSymbolTable.getModuleScope();

        OperationEntry operation = null;

        if (conceptModuleScope.containsOperation(name)) {
            operation = conceptModuleScope.getOperation(name);
        }
        else {
            //throw new SanityCheckException(noMatchingOperation(name));
            return null;
        }

        return operation;
    }
}
