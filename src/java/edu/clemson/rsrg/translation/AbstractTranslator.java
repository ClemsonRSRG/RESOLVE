/*
 * AbstractTranslator.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.translation;

import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.rsrg.absyn.declarations.operationdecl.OperationProcedureDec;
import edu.clemson.rsrg.absyn.declarations.operationdecl.ProcedureDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.rsrg.absyn.items.mathitems.LoopVerificationItem;
import edu.clemson.rsrg.absyn.items.programitems.AbstractInitFinalItem;
import edu.clemson.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.rsrg.absyn.statements.*;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.file.ModuleType;
import edu.clemson.rsrg.init.file.ResolveFile;
import edu.clemson.rsrg.init.flag.Flag;
import edu.clemson.rsrg.init.flag.Flag.Type;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.treewalk.TreeWalkerStackVisitor;
import edu.clemson.rsrg.typeandpopulate.entry.*;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.rsrg.typeandpopulate.programtypes.*;
import edu.clemson.rsrg.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.rsrg.typeandpopulate.query.OperationQuery;
import edu.clemson.rsrg.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleParameterization;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This is the abstract base class for all target language translators using the RESOLVE abstract syntax tree. This
 * visitor logic is implemented as a {@link TreeWalkerStackVisitor}.
 * </p>
 *
 * @author Daniel Welch
 * @author Mark Todd
 * @author Yu-Shan Sun
 *
 * @version 2.0
 */
public abstract class AbstractTranslator extends TreeWalkerStackVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The symbol table we are currently building.
     * </p>
     */
    protected final MathSymbolTableBuilder myBuilder;

    /**
     * <p>
     * The current job's compilation environment that stores all necessary objects and flags.
     * </p>
     */
    protected final CompileEnvironment myCompileEnvironment;

    /**
     * <p>
     * A pointer to a {@link SymbolTableEntry} that corresponds to the {@link FacilityDec} currently being walked. If
     * one isn't being walked, this should be {@code null}.
     * </p>
     */
    protected FacilityEntry myCurrentFacilityEntry;

    /**
     * <p>
     * The module scope for the file we are generating {@code VCs} for.
     * </p>
     */
    protected ModuleScope myCurrentModuleScope;

    /**
     * <p>
     * This set keeps track of any additional <code>includes</code> or <code>imports</code> needed to run the translated
     * file. We call it <em>dynamic</em> since only certain nodes trigger additions to this set (i.e.
     * <code>FacilityDec</code>s).
     * </p>
     */
    protected final Set<String> myDynamicImports;

    /**
     * <p>
     * This list keeps track of the any {@link OperationDec OperationDec's} parameters.
     * </p>
     */
    protected final List<String> myOperationParameterNames;

    /**
     * <p>
     * This flag is {@code true} when walking the children of a {@code WhileStmtChanging} clause, {@code false}
     * otherwise.
     * </p>
     */
    // TODO : This global can be safely removed once walk methods for virtual
    // list nodes are fixed. Talk to Blair about this.
    protected boolean myWhileStmtChangingClause = false;

    /**
     * <p>
     * This flag is {@code true} when walking the children of a {@link AbstractInitFinalItem}, {@code false} otherwise.
     * </p>
     */
    private boolean myWalkingInitFinalItemFlag = false;

    /**
     * <p>
     * These are special files that should already exist in the current workspace and shouldn't be overwritten.
     * </p>
     */
    private static final List<String> noTranslate = Arrays.asList("Std_Boolean_Fac.fa", "Std_Char_Str_Fac.fa",
            "Std_Character_Fac.fa", "Std_Integer_Fac.fa", "Std_Boolean_Realiz", "Boolean_Template.co",
            "Integer_Template.co", "Character_Template.co", "Char_Str_Template.co", "Seq_Input_Template.co",
            "Seq_Output_Template.co", "Print.co");

    /**
     * <p>
     * While we walk the children of a {@link OperationProcedureDec}, this will be set to the
     * {@link OperationProcedureDec}. Otherwise it will be {@code null}.
     * </p>
     */
    private OperationProcedureDec myCurrentPrivateProcedure;

    /**
     * <p>
     * This is the status handler for the RESOLVE compiler.
     * </p>
     */
    private final StatusHandler myStatusHandler;

    // -----------------------------------------------------------
    // Output-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * The top of this {@link Stack} maintains a reference to the template actively being built or added to, and the
     * bottom refers to {@code shell} - the outermost enclosing template for all currently supported target languages.
     * </p>
     * <p>
     * Proper usage should generally involve: Pushing in <tt>pre</tt>, modifying top arbitrarily with <tt>pre</tt>'s
     * children, popping in the corresponding <tt>post</tt>, then adding the popped template to the appropriate
     * enclosing template (i.e. the new/current top).
     * </p>
     */
    protected final Stack<ST> myActiveTemplates;

    /**
     * <p>
     * String template groups that houses all templates used by a given target language.
     * </p>
     */
    protected final STGroup mySTGroup;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    /**
     * <p>
     * This indicates that this section translates {@code RESOLVE} source files to other target languages.
     * </p>
     */
    protected static final String FLAG_SECTION_NAME = "Translation";

    /**
     * <p>
     * This indicates that the {@code Translator} is going to print debugging information wherever possible.
     * </p>
     */
    private static final String FLAG_TRANSLATE_DEBUG_INFO = "Translation Debug Flag";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>
     * An auxiliary flag that indicates we are translating to a target file.
     * </p>
     */
    public static final Flag FLAG_TRANSLATE = new Flag(FLAG_SECTION_NAME, "translate",
            "An auxiliary flag that indicates we are translating a source file", Type.AUXILIARY);

    /**
     * <p>
     * Tells the compiler to print out {@code Translator} information messages.
     * </p>
     */
    protected static final Flag FLAG_TRANSLATE_DEBUG = new Flag(FLAG_SECTION_NAME, "translateDebug",
            FLAG_TRANSLATE_DEBUG_INFO);

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * An helper constructor that creates and stores all the common objects used by classes that inherit from this
     * class.
     * </p>
     *
     * @param builder
     *            A scope builder for a symbol table.
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     * @param group
     *            The string template group to be used by each of the implementing subclass.
     */
    protected AbstractTranslator(MathSymbolTableBuilder builder, CompileEnvironment compileEnvironment, STGroup group) {
        myActiveTemplates = new Stack<>();
        myBuilder = builder;
        myCompileEnvironment = compileEnvironment;
        myCurrentFacilityEntry = null;
        myCurrentModuleScope = null;
        myCurrentPrivateProcedure = null;
        myDynamicImports = new LinkedHashSet<>();
        myOperationParameterNames = new ArrayList<>();
        mySTGroup = group;
        myStatusHandler = myCompileEnvironment.getStatusHandler();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Module Declarations
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link ModuleDec}.
     * </p>
     *
     * @param dec
     *            A module declaration.
     */
    @Override
    public final void preModuleDec(ModuleDec dec) {
        try {
            // Check for any shared variables in the current scope.
            // TODO: Add the proper translation and remove the following method.
            noSharedVarModule(dec);

            myCurrentModuleScope = myBuilder.getModuleScope(new ModuleIdentifier(dec));

            // Add to translation model
            ST outermostEnclosingTemplate = mySTGroup.getInstanceOf("module");
            outermostEnclosingTemplate.add("includes",
                    mySTGroup.getInstanceOf("include").add("directories", "RESOLVE"));

            // Store this as our current outermost template
            myActiveTemplates.push(outermostEnclosingTemplate);

            emitDebug(dec.getLocation(), "Beginning translating: " + dec.getName());
        } catch (NoSuchSymbolException nsse) {
            noSuchModule(dec.getLocation());
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ModuleDec}.
     * </p>
     *
     * @param dec
     *            A module declaration.
     */
    @Override
    public void postModuleDec(ModuleDec dec) {
        if (!myDynamicImports.isEmpty()) {
            myActiveTemplates.firstElement().add("includes", myDynamicImports);
        }

        emitDebug(dec.getLocation(), "Done translating: " + dec.getName() + "\n");
    }

    // -----------------------------------------------------------
    // Uses Items (Imports)
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link UsesItem}.
     * </p>
     *
     * @param uses
     *            An uses item declaration.
     */
    @Override
    public final void preUsesItem(UsesItem uses) {
        ResolveFile file = myCompileEnvironment.getFile(new ModuleIdentifier(uses));

        // YS: When translating, we don't really need theory files.
        if (!file.getModuleType().equals(ModuleType.THEORY)) {
            // Deal with concept imports
            List<String> pkgDirectories;
            if (file.getModuleType().equals(ModuleType.CONCEPT)) {
                pkgDirectories = getFile(file.getName()).getPkgList();
            }
            // Deal with both kinds of facility imports
            else {
                ModuleDec dec = myCompileEnvironment.getModuleAST(new ModuleIdentifier(uses));

                // Short facility imports
                if (dec instanceof ShortFacilityModuleDec) {
                    FacilityDec facilityDec = ((ShortFacilityModuleDec) dec).getDec();
                    pkgDirectories = getFile(facilityDec.getConceptName().getName()).getPkgList();
                }
                // Facility imports
                else if (dec instanceof FacilityModuleDec) {
                    pkgDirectories = getFile(dec.getName().getName()).getPkgList();
                } else {
                    pkgDirectories = new ArrayList<>();
                    unsupportedImport(uses.getName());
                }
            }

            // Create an import string for this uses item.
            myActiveTemplates.firstElement().add("includes",
                    mySTGroup.getInstanceOf("include").add("directories", pkgDirectories));

            emitDebug(uses.getLocation(), "Adding import: " + uses.getName());
        }
    }

    // -----------------------------------------------------------
    // Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link ProgramFunctionExp}.
     * </p>
     *
     * @param exp
     *            A programming function call
     */
    @Override
    public final void preProgramFunctionExp(ProgramFunctionExp exp) {
        ST paramExp;
        String qualifier = getCallQualifier(exp.getQualifier(), exp.getName(), exp.getArguments());
        if (myOperationParameterNames.contains(exp.getName().getName())) {
            qualifier = exp.getName().getName() + "Param";
        }

        if (qualifier != null) {
            paramExp = mySTGroup.getInstanceOf("qualified_param_exp").add("qualifier", qualifier).add("name",
                    exp.getName().getName());
        } else {
            paramExp = mySTGroup.getInstanceOf("unqualified_param_exp").add("name", exp.getName().getName());
        }

        myActiveTemplates.push(paramExp);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ProgramFunctionExp}.
     * </p>
     *
     * @param exp
     *            A programming function call
     */
    @Override
    public final void postProgramFunctionExp(ProgramFunctionExp exp) {
        ST paramExp = myActiveTemplates.pop();
        myActiveTemplates.peek().add("arguments", paramExp);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link ProgramIntegerExp}.
     * </p>
     *
     * @param exp
     *            A programming integer expression.
     */
    @Override
    public final void preProgramIntegerExp(ProgramIntegerExp exp) {
        ST integerExp = mySTGroup.getInstanceOf("var_init").add("type", getVariableTypeTemplate(exp.getProgramType()));

        integerExp.add("facility", getDefiningFacilityEntry(exp.getProgramType()).getName()).add("arguments",
                exp.getValue());

        myActiveTemplates.peek().add("arguments", integerExp);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link ProgramStringExp}.
     * </p>
     *
     * @param exp
     *            A programming string expression.
     */
    @Override
    public final void preProgramStringExp(ProgramStringExp exp) {
        ST stringExp = mySTGroup.getInstanceOf("var_init").add("type", getVariableTypeTemplate(exp.getProgramType()));

        stringExp.add("facility", getDefiningFacilityEntry(exp.getProgramType()).getName()).add("arguments",
                exp.getValue());

        myActiveTemplates.peek().add("arguments", stringExp);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link ProgramVariableDotExp}.
     * </p>
     *
     * @param exp
     *            A programming variable dotted expression.
     */
    @Override
    public final void preProgramVariableDotExp(ProgramVariableDotExp exp) {
        PTType type = exp.getSegments().get(0).getProgramType();

        ST dotExp = mySTGroup.getInstanceOf("variable_dot_exp")
                .add("modulename", myCurrentModuleScope.getDefiningElement().getName().getName())
                .add("typename", getTypeName(type));

        myActiveTemplates.push(dotExp);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ProgramVariableDotExp}.
     * </p>
     *
     * @param exp
     *            A programming variable dotted expression.
     */
    @Override
    public final void postProgramVariableDotExp(ProgramVariableDotExp exp) {
        ST dotExp = myActiveTemplates.pop();
        myActiveTemplates.peek().add("arguments", dotExp);
    }

    /**
     * <p>
     * This method redefines how a {@link ProgramVariableDotExp} should be walked.
     * </p>
     *
     * @param exp
     *            A programming variable dotted expression.
     *
     * @return {@code true} if we are in a init/final block, {@code false} otherwise.
     */
    @Override
    public final boolean walkProgramVariableDotExp(ProgramVariableDotExp exp) {
        // If we encounter a dot expression in an initialization clause,
        // we to basically pretend that its a normal name expression. This is not
        // ideal, but for now, our model of java code requires/expects this.

        // TODO: Think about cases in which the java will actually need a
        // variableDotExp, and the RESOLVE source that will elicit this.
        if (myWalkingInitFinalItemFlag) {
            preAny(exp);
            preExp(exp);
            preProgramExp(exp);
            preProgramVariableExp(exp);

            // For now we assume we're dealing with a name, since we need to
            // initialize just the name.
            preProgramVariableNameExp((ProgramVariableNameExp) exp.getSegments().get(1));
            postProgramVariableNameExp((ProgramVariableNameExp) exp.getSegments().get(1));

            postProgramVariableExp(exp);
            postProgramExp(exp);
            postExp(exp);
            postAny(exp);
        }

        return myWalkingInitFinalItemFlag;
    }

    // -----------------------------------------------------------
    // Operation Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting an {@link OperationDec}.
     * </p>
     *
     * @param dec
     *            An operation declaration.
     */
    @Override
    public final void preOperationDec(OperationDec dec) {
        // Check to see if we are an OperationDec
        // inside a OperationProcedureDec. If we are,
        // then we have a body.
        boolean hasBody = false;
        if (myCurrentPrivateProcedure != null) {
            hasBody = true;
        }

        ST operation = getOperationLikeTemplate((dec.getReturnTy() != null) ? dec.getReturnTy().getProgramType() : null,
                dec.getName().getName(), hasBody);

        myActiveTemplates.push(operation);
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link OperationDec}.
     * </p>
     *
     * @param dec
     *            An operation declaration.
     */
    @Override
    public final void postOperationDec(OperationDec dec) {
        // Perform different actions depending if we are
        // a standalone OperationDec or if we are an
        // OperationDec inside a FacilityOperationDec
        if (myCurrentPrivateProcedure == null) {
            ST operation = myActiveTemplates.pop();
            myActiveTemplates.peek().add("functions", operation);

            emitDebug(dec.getLocation(), "Adding operation: " + dec.getName());
        } else {
            if (dec.getReturnTy() != null) {
                addVariableTemplate(dec.getLocation(), dec.getReturnTy().getProgramType(), dec.getName().getName());
            }
        }
    }

    /**
     * <p>
     * Code that gets executed before visiting an {@link OperationProcedureDec}.
     * </p>
     *
     * @param dec
     *            A local operation with procedure declaration.
     */
    @Override
    public final void preOperationProcedureDec(OperationProcedureDec dec) {
        myCurrentPrivateProcedure = dec;
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link OperationProcedureDec}.
     * </p>
     *
     * @param dec
     *            A local operation with procedure declaration.
     */
    @Override
    public final void postOperationProcedureDec(OperationProcedureDec dec) {
        if (dec.getWrappedOpDec().getReturnTy() != null) {
            ST returnStmt = mySTGroup.getInstanceOf("return_stmt").add("name", dec.getName().getName());

            myActiveTemplates.peek().add("stmts", returnStmt);
        }

        ST operation = myActiveTemplates.pop();
        myActiveTemplates.peek().add("functions", operation);

        emitDebug(dec.getLocation(), "Adding local operation: " + dec.getName());

        myCurrentPrivateProcedure = null;
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link ProcedureDec}.
     * </p>
     *
     * @param dec
     *            A procedure declaration.
     */
    @Override
    public final void preProcedureDec(ProcedureDec dec) {
        ST operation = getOperationLikeTemplate((dec.getReturnTy() != null) ? dec.getReturnTy().getProgramType() : null,
                dec.getName().getName(), true);

        myActiveTemplates.push(operation);

        if (dec.getReturnTy() != null) {
            addVariableTemplate(dec.getLocation(), dec.getReturnTy().getProgramType(), dec.getName().getName());
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ProcedureDec}.
     * </p>
     *
     * @param dec
     *            A procedure declaration.
     */
    @Override
    public final void postProcedureDec(ProcedureDec dec) {
        if (dec.getReturnTy() != null) {
            ST returnStmt = mySTGroup.getInstanceOf("return_stmt").add("name", dec.getName().getName());

            myActiveTemplates.peek().add("stmts", returnStmt);
        }

        ST operation = myActiveTemplates.pop();
        myActiveTemplates.peek().add("functions", operation);

        emitDebug(dec.getLocation(), "Adding procedure: " + dec.getName());
    }

    // -----------------------------------------------------------
    // Statement-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting a {@link CallStmt}.
     * </p>
     *
     * @param stmt
     *            A function call statement.
     */
    @Override
    public final void postCallStmt(CallStmt stmt) {
        ST callStmt = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", callStmt);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link FuncAssignStmt}.
     * </p>
     *
     * @param stmt
     *            A function assignment statement.
     */
    @Override
    public final void preFuncAssignStmt(FuncAssignStmt stmt) {
        String qualifier = getDefiningFacilityEntry(stmt.getVariableExp().getProgramType()).getName();

        ST assignStmt = mySTGroup.getInstanceOf("qualified_call").add("name", "assign").add("qualifier", qualifier);

        myActiveTemplates.push(assignStmt);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link FuncAssignStmt}.
     * </p>
     *
     * @param stmt
     *            A function assignment statement.
     */
    @Override
    public final void postFuncAssignStmt(FuncAssignStmt stmt) {
        ST assignStmt = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", assignStmt);
    }

    /**
     * <p>
     * Code that gets executed before visiting an {@link IfStmt}.
     * </p>
     *
     * @param stmt
     *            An if statement.
     */
    @Override
    public final void preIfStmt(IfStmt stmt) {
        ST ifStmt = mySTGroup.getInstanceOf("if");
        myActiveTemplates.push(ifStmt);
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link IfStmt}.
     * </p>
     *
     * @param stmt
     *            An if statement.
     */
    @Override
    public final void postIfStmt(IfStmt stmt) {
        ST ifStmt = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", ifStmt);
    }

    /**
     * <p>
     * Code that gets executed before visiting the {@code else} block in an {@link IfStmt}.
     * </p>
     *
     * @param stmt
     *            An if statement.
     */
    @Override
    public final void preIfStmtMyElseStatements(IfStmt stmt) {
        // TODO : This is probably going to need some tweaking once else-ifs
        // are fixed.

        // IfStmtElseClauses are nested within the tree. So if we're here,
        // add the if part to the outermost stmt block.
        ST ifPart = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", ifPart);

        ST elseStmt = mySTGroup.getInstanceOf("else");
        myActiveTemplates.push(elseStmt);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link SwapStmt}.
     * </p>
     *
     * @param stmt
     *            A swap statement.
     */
    @Override
    public final void postSwapStmt(SwapStmt stmt) {
        ST swapStmt = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", swapStmt);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link WhileStmt}.
     * </p>
     *
     * @param stmt
     *            A while statement.
     */
    @Override
    public final void preWhileStmt(WhileStmt stmt) {
        ST whileStmt = mySTGroup.getInstanceOf("while");
        myActiveTemplates.push(whileStmt);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link WhileStmt}.
     * </p>
     *
     * @param stmt
     *            A while statement.
     */
    @Override
    public final void postWhileStmt(WhileStmt stmt) {
        ST whileStmt = myActiveTemplates.pop();
        myActiveTemplates.peek().add("stmts", whileStmt);
    }

    // -----------------------------------------------------------
    // Loop Verification Item-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link LoopVerificationItem}.
     * </p>
     *
     * @param item
     *            A loop verification item.
     */
    @Override
    public final void preLoopVerificationItemMyChangingVars(LoopVerificationItem item) {
        myWhileStmtChangingClause = true;
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link WhileStmt}.
     * </p>
     *
     * @param item
     *            A loop verification item.
     */
    @Override
    public final void postLoopVerificationItemMyChangingVars(LoopVerificationItem item) {
        myWhileStmtChangingClause = false;
    }

    // -----------------------------------------------------------
    // Variable Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link ParameterVarDec}.
     * </p>
     *
     * @param dec
     *            A parameter declaration.
     */
    @Override
    public void preParameterVarDec(ParameterVarDec dec) {
        PTType type = dec.getTy().getProgramType();

        ST parameter = mySTGroup.getInstanceOf("parameter").add("type", getParameterTypeTemplate(type)).add("name",
                dec.getName().getName());

        myActiveTemplates.peek().add("parameters", parameter);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link VarDec}.
     * </p>
     *
     * @param dec
     *            A variable declaration.
     */
    @Override
    public final void preVarDec(VarDec dec) {
        addVariableTemplate(dec.getLocation(), dec.getTy().getProgramType(), dec.getName().getName());
    }

    // -----------------------------------------------------------
    // Type Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting an {@link AbstractInitFinalItem}.
     * </p>
     *
     * @param item
     *            An initialization or finalization block.
     */
    @Override
    public final void preAbstractInitFinalItem(AbstractInitFinalItem item) {
        myWalkingInitFinalItemFlag = true;
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link AbstractInitFinalItem}.
     * </p>
     *
     * @param item
     *            An initialization or finalization block.
     */
    @Override
    public final void postAbstractInitFinalItem(AbstractInitFinalItem item) {
        myWalkingInitFinalItemFlag = false;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the translated source file as a string.
     * </p>
     *
     * @return A file content string.
     */
    public final String getOutputCode() {
        return myActiveTemplates.peek().render();
    }

    /**
     * <p>
     * This method checks to see if we should translate a file.
     * </p>
     *
     * @param identifier
     *            A module identifier.
     *
     * @return {@code true} if it is a special file that we shouldn't translate, {@code false} otherwise.
     */
    public static boolean onNoTranslateList(ModuleIdentifier identifier) {
        return noTranslate.contains(identifier.toString());
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * An helper method to print debugging messages if the debug flag is on.
     * </p>
     *
     * @param l
     *            Location that generated the message.
     * @param message
     *            The message to be outputted.
     */
    protected final void emitDebug(Location l, String message) {
        if (myCompileEnvironment.flags.isFlagSet(FLAG_TRANSLATE_DEBUG)) {
            myStatusHandler.info(l, message);
        }
    }

    // -----------------------------------------------------------
    // Utility methods
    // -----------------------------------------------------------

    /**
     * <p>
     * This method constructs and adds a {@code parameter} to the currently active template.
     * </p>
     *
     * @param loc
     *            The {@link Location} where we are trying to add the variable.
     * @param type
     *            A {@link PTType} representing the 'declared type' of the parameter.
     * @param name
     *            The name of the parameter.
     */
    protected final void addParameterTemplate(Location loc, PTType type, String name) {
        ST parameter = mySTGroup.getInstanceOf("parameter").add("type", getVariableTypeTemplate(type)).add("name",
                name);

        myActiveTemplates.peek().add("parameters", parameter);

        emitDebug(loc, "Adding parameter variable: " + name + " with type: " + getTypeName(type));
    }

    /**
     * <p>
     * This method searches for a {@code qualifier} associated with the operation call.
     * </p>
     *
     * @param qualifier
     *            A qualifier symbol
     * @param name
     *            Name of the operation we are calling.
     * @param args
     *            The arguments passed to this operation.
     *
     * @return The {@code qualifier} associated with this operation call.
     */
    protected final String getCallQualifier(PosSymbol qualifier, PosSymbol name, List<ProgramExp> args) {
        String result = null;
        List<PTType> argTypes = new LinkedList<>();
        FacilityEntry definingFacility = null;

        try {
            for (ProgramExp arg : args) {
                argTypes.add(arg.getProgramType());
            }

            OperationEntry oe = myCurrentModuleScope.queryForOne(new OperationQuery(null, name, argTypes))
                    .toOperationEntry(null);

            // We're dealing with local operation, then no qualifier.
            if (myCurrentModuleScope.getModuleIdentifier().equals(oe.getSourceModuleIdentifier())) {
                return null;
            }

            // Grab FacilityEntries in scope whose specification matches
            // oe's SourceModuleIdentifier.
            List<FacilityEntry> facilities = myCurrentModuleScope.query(new EntryTypeQuery<>(FacilityEntry.class,
                    MathSymbolTable.ImportStrategy.IMPORT_NAMED, MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));

            boolean comesFromEnhancement = false;
            for (FacilityEntry f : facilities) {
                if (qualifier != null && f.getName().equals(qualifier.getName())) {
                    definingFacility = f;
                    break;
                }

                if (oe.getSourceModuleIdentifier().equals(f.getFacility().getSpecification().getModuleIdentifier())) {
                    definingFacility = f;
                }

                for (ModuleParameterization p : f.getEnhancements()) {
                    if (oe.getSourceModuleIdentifier().equals(p.getModuleIdentifier())) {
                        definingFacility = f;
                        comesFromEnhancement = true;
                    }
                }
            }

            // If we're in an enhancement realization, some calls rightly won't
            // have a facility, and hence no qualifier should be returned.
            if (definingFacility == null) {
                return null;
            }

            // This is the idiotic part, really this is mixing the model and
            // view (since I've put the '(' .. ')' cast parens in but it was
            // mostly so I wouldn't have to write yet another super specific
            // template -- there is likely a more elegant way.
            if (definingFacility.getEnhancements().size() >= 2 && comesFromEnhancement) {
                result = "((" + oe.getSourceModuleIdentifier() + ")" + definingFacility.getName() + ")";
            } else {
                result = definingFacility.getName();
            }
        } catch (NoSuchSymbolException nsse) {
            noSuchSymbol(qualifier, name.getName(), null);
        } catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse); // shouldn't fire.
        }

        return result;
    }

    /**
     * <p>
     * This method searches for the {@link FacilityEntry} responsible for bringing the {@link SymbolTableEntry}
     * referenced by {@code type} into the {@link ModuleScope} being translated.
     * </p>
     *
     * @param type
     *            The {@link PTType} we want symbol table info for.
     *
     * @return The {@link FacilityEntry} that defines {@code type}.
     */
    protected final FacilityEntry getDefiningFacilityEntry(PTType type) {
        FacilityEntry result = null;

        try {
            ProgramTypeEntry te = myCurrentModuleScope.queryForOne(new UnqualifiedNameQuery(type.toString()))
                    .toProgramTypeEntry(null);

            List<FacilityEntry> facilities = myCurrentModuleScope.query(new EntryTypeQuery<>(FacilityEntry.class,
                    MathSymbolTable.ImportStrategy.IMPORT_NAMED, MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));

            for (FacilityEntry facility : facilities) {
                if (te.getSourceModuleIdentifier()
                        .equals(facility.getFacility().getSpecification().getModuleIdentifier())) {

                    result = facility;
                }
            }
        } catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, type.toString(), null);
        } catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse); // shouldn't fire.
        }

        return result;
    }

    /**
     * <p>
     * This method returns a {@link ResolveFile} given a string containing the name.
     * </p>
     *
     * @param name
     *            A module's name.
     *
     * @return A {@link ResolveFile}.
     */
    protected final ResolveFile getFile(String name) {
        return myCompileEnvironment.getFile(new ModuleIdentifier(name));
    }

    /**
     * <p>
     * This method returns the default function modifier specified by the target language.
     * </p>
     *
     * @return Function modifier as a string.
     */
    protected abstract String getFunctionModifier();

    /**
     * <p>
     * This method returns a list of {@link ProgramParameterEntry ProgramParameterEntries} representing the formal
     * parameters of module {@code moduleName}.
     * </p>
     *
     * @param moduleName
     *            A {@link PosSymbol} containing the name of the module whose parameters we are trying to extract.
     *
     * @return A (possibly empty) list of formal parameters.
     */
    protected final List<ProgramParameterEntry> getModuleFormalParameters(PosSymbol moduleName) {
        List<ProgramParameterEntry> parameterEntries = null;
        try {
            ModuleDec spec = myBuilder.getModuleScope(new ModuleIdentifier(moduleName.getName())).getDefiningElement();

            parameterEntries = myBuilder.getScope(spec).getFormalParameterEntries();
        } catch (NoSuchSymbolException nsse) {
            noSuchModule(moduleName);
        }

        return parameterEntries;
    }

    /**
     * <p>
     * This method returns a {@code function} template 'filled in' with the attributes provided.
     * </p>
     *
     * @param returnType
     *            A {@link PTType} representative of the function's return {@code type} attribute.
     * @param name
     *            The name attribute.
     * @param hasBody
     *            A boolean indicating whether or not the function being created should have a body or not.
     *
     * @return A {@code function} template with the {@code type} and {@code name} attributes formed and filled in.
     */
    protected final ST getOperationLikeTemplate(PTType returnType, String name, boolean hasBody) {
        String attributeName = (hasBody) ? "function_def" : "function_decl";

        ST operationLikeThingy = mySTGroup.getInstanceOf(attributeName).add("name", name).add("modifier",
                getFunctionModifier());

        operationLikeThingy.add("type", (returnType != null) ? getOperationTypeTemplate(returnType) : "void");

        return operationLikeThingy;
    }

    /**
     * <p>
     * This method returns the operation type template for the target language.
     * </p>
     *
     * @param type
     *            A {@link PTType}.
     *
     * @return A {@link ST} associated with the given program type.
     */
    protected abstract ST getOperationTypeTemplate(PTType type);

    /**
     * <p>
     * This method returns the program parameter type template for the target language.
     * </p>
     *
     * @param type
     *            A {@link PTType}.
     *
     * @return A {@link ST} associated with the given program parameter type.
     */
    protected abstract ST getParameterTypeTemplate(PTType type);

    /**
     * <p>
     * This method returns the 'name' component of a {@link PTType}.
     *
     * @param type
     *            A {@link PTType}.
     *
     * @return {@code type}'s actual name rather than the more easily accessible {@code toString()} representation.
     */
    protected final String getTypeName(PTType type) {
        String result;

        if (type == null) {
            return null;
        }

        if (type instanceof PTElement) {
            // Not sure under what conditions this would appear in output.
            result = "PTELEMENT";
        } else if (type instanceof PTGeneric) {
            result = ((PTGeneric) type).getName();
        } else if (type instanceof PTRepresentation) {
            result = ((PTRepresentation) type).getFamily().getName();
        } else if (type instanceof PTFacilityRepresentation) {
            result = ((PTFacilityRepresentation) type).getName();
        } else if (type instanceof PTFamily) {
            result = ((PTFamily) type).getName();
        } else {
            throw new UnsupportedOperationException(
                    "Translation has " + "encountered an unrecognized PTType: " + type.toString() + ". Backing out.");
        }

        return result;
    }

    /**
     * <p>
     * This method returns the program variable type template for the target language.
     * </p>
     *
     * @param type
     *            A {@link PTType}.
     *
     * @return A {@link ST} associated with the given program variable type.
     */
    protected abstract ST getVariableTypeTemplate(PTType type);

    // -----------------------------------------------------------
    // Error handling methods
    // -----------------------------------------------------------

    /**
     * <p>
     * An helper method that throws the appropriate no module found message.
     * </p>
     *
     * @param loc
     *            Location where this module name was found.
     */
    protected final void noSuchModule(Location loc) {
        throw new SourceErrorException(
                "[" + getClass().getCanonicalName() + "] " + "Module does not exist or is not in scope.", loc);
    }

    /**
     * <p>
     * An helper method that indicates that a module with the specified name cannot be found.
     * </p>
     *
     * @param name
     *            The name of a module.
     */
    protected final void noSuchModule(PosSymbol name) {
        throw new SourceErrorException(
                "[" + getClass().getCanonicalName() + "] " + "Module does not exist or is not in scope.", name);
    }

    /**
     * <p>
     * An helper method that throws the appropriate no symbol found message.
     * </p>
     *
     * @param qualifier
     *            The symbol's qualifier.
     * @param symbol
     *            The symbol not found.
     */
    protected final void noSuchSymbol(PosSymbol qualifier, PosSymbol symbol) {
        noSuchSymbol(qualifier, symbol.getName(), symbol.getLocation());
    }

    /**
     * <p>
     * An helper method that throws the appropriate no symbol found message.
     * </p>
     *
     * @param qualifier
     *            The symbol's qualifier.
     * @param symbolName
     *            The symbol's name.
     * @param loc
     *            Location where this symbol was found.
     */
    protected final void noSuchSymbol(PosSymbol qualifier, String symbolName, Location loc) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(getClass().getCanonicalName());
        sb.append("] ");

        if (qualifier == null) {
            sb.append("Translation was unable to find symbol: ");
            sb.append(symbolName);
        } else {
            sb.append("No such symbol in module: ");
            sb.append(qualifier.getName());
            sb.append("::");
            sb.append(symbolName);
        }

        throw new SourceErrorException(sb.toString(), loc);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This method constructs and adds a {@code variable} template to the currently active template.
     * </p>
     *
     * @param loc
     *            The {@link Location} where we are trying to add the variable.
     * @param type
     *            A {@link PTType} representing the type of the variable.
     * @param name
     *            The name of the variable.
     */
    private void addVariableTemplate(Location loc, PTType type, String name) {
        ST init, variable;

        // Case 1: Generic types ("Entry", "Info", etc.)
        if (type instanceof PTGeneric) {
            init = mySTGroup.getInstanceOf("rtype_init").add("typeName", getTypeName(type));
        }
        // Case 2: Program types declared and implemented in a facility module.
        else if (type instanceof PTFacilityRepresentation) {
            init = mySTGroup.getInstanceOf("facility_type_var_init").add("name", getTypeName(type));
        }
        // Case 3: This is an instantiated version of a concept type.
        else if (getDefiningFacilityEntry(type) != null) {
            init = mySTGroup.getInstanceOf("var_init").add("type", getVariableTypeTemplate(type)).add("facility",
                    getDefiningFacilityEntry(type).getName());
        }
        // Case 4: Program types declared by the concept.
        else {
            init = mySTGroup.getInstanceOf("enhancement_var_init").add("type", getVariableTypeTemplate(type));
        }

        variable = mySTGroup.getInstanceOf("var_decl").add("name", name).add("type", getVariableTypeTemplate(type))
                .add("init", init);

        emitDebug(loc, "Adding variable: " + name + " with type: " + getTypeName(type));

        myActiveTemplates.peek().add("variables", variable);
    }

    /**
     * <p>
     * An helper method that checks to see if the specified module contains shared variables.
     * </p>
     *
     * @param dec
     *            A module declaration.
     */
    private void noSharedVarModule(ModuleDec dec) {
        boolean containsSharedVar = false;
        if (dec instanceof ConceptModuleDec) {
            containsSharedVar = ((ConceptModuleDec) dec).isSharingConcept();
        } else {
            PosSymbol conceptName;
            if (dec instanceof ConceptRealizModuleDec) {
                conceptName = ((ConceptRealizModuleDec) dec).getConceptName();
            } else if (dec instanceof EnhancementModuleDec) {
                conceptName = ((EnhancementModuleDec) dec).getConceptName();
            } else {
                conceptName = ((EnhancementRealizModuleDec) dec).getConceptName();
            }

            try {
                ConceptModuleDec conceptModuleDec = (ConceptModuleDec) myCompileEnvironment
                        .getModuleAST(new ModuleIdentifier(conceptName.getName()));
                containsSharedVar = conceptModuleDec.isSharingConcept();
            } catch (NoSuchSymbolException nsse) {
                noSuchModule(conceptName.getLocation());
            }
        }

        // Throw error if we detect there is a shared variable
        if (containsSharedVar) {
            unsupportedSharedTranslation(dec.getName());
        }
    }

    /**
     * <p>
     * An helper method that throws the appropriate unsupported import message.
     * </p>
     *
     * @param name
     *            Name of the file that isn't an import file that is supported by the translator.
     */
    private void unsupportedImport(PosSymbol name) {
        throw new SourceErrorException("[" + getClass().getCanonicalName() + "] " + "Module: " + name
                + " can't be translated into the appropriate import string.", name.getLocation());
    }

    /**
     * <p>
     * An helper method that throws the appropriate unsupported shared variable translation message.
     * </p>
     * <p>
     * Note: Remove this once we figure out how to translate shared variables to Java/C.
     * </p>
     *
     * @param name
     *            Name of the file that contains a shared variable.
     */
    private void unsupportedSharedTranslation(PosSymbol name) {
        throw new SourceErrorException("[" + getClass().getCanonicalName() + "] " + "Module: " + name
                + " contains share variables and can't be translated appropriately.", name.getLocation());
    }

}
