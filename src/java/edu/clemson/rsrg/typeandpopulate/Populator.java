/*
 * Populator.java
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
package edu.clemson.rsrg.typeandpopulate;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.declarations.mathdecl.*;
import edu.clemson.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.rsrg.absyn.declarations.operationdecl.*;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ConceptTypeParamDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ConstantParamDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.*;
import edu.clemson.rsrg.absyn.declarations.variabledecl.*;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.rsrg.absyn.items.mathitems.DefinitionBodyItem;
import edu.clemson.rsrg.absyn.items.programitems.AbstractInitFinalItem;
import edu.clemson.rsrg.absyn.items.programitems.ModuleArgumentItem;
import edu.clemson.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.rsrg.absyn.rawtypes.*;
import edu.clemson.rsrg.absyn.statements.FuncAssignStmt;
import edu.clemson.rsrg.absyn.statements.SwapStmt;
import edu.clemson.rsrg.absyn.statements.WhileStmt;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.ResolveCompiler;
import edu.clemson.rsrg.init.flag.Flag;
import edu.clemson.rsrg.init.flag.FlagDependencies;
import edu.clemson.rsrg.misc.Utilities.Indirect;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.rsrg.typeandpopulate.entry.*;
import edu.clemson.rsrg.typeandpopulate.exception.*;
import edu.clemson.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.rsrg.typeandpopulate.programtypes.*;
import edu.clemson.rsrg.typeandpopulate.query.*;
import edu.clemson.rsrg.typeandpopulate.sanitychecking.*;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScopeBuilder;
import edu.clemson.rsrg.typeandpopulate.symboltables.ScopeBuilder;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeComparison;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.typeandpopulate.utilities.HardCoded;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.*;

/**
 * <p>
 * This class populates the symbol table and assigns mathematical types to the provided RESOLVE abstract syntax tree.
 * This visitor logic is implemented as a {@link TreeWalkerVisitor}.
 * </p>
 *
 * @version 2.0
 */
public class Populator extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The symbol table we are currently building.
     * </p>
     */
    private final MathSymbolTableBuilder myBuilder;

    /**
     * <p>
     * The current job's compilation environment that stores all necessary objects and flags.
     * </p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /**
     * <p>
     * The current scope for the module we are currently building.
     * </p>
     */
    private ModuleScopeBuilder myCurModuleScope;

    /**
     * <p>
     * This is the status handler for the RESOLVE compiler.
     * </p>
     */
    private final StatusHandler myStatusHandler;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    // -----------------------------------------------------------
    // Type Domain-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * A {@link TypeComparison} for to find exact domain match between a {@link AbstractFunctionExp} and a
     * {@link MTType}.
     * </p>
     */
    private static final TypeComparison<AbstractFunctionExp, MTFunction> EXACT_DOMAIN_MATCH = new ExactDomainMatch();

    /**
     * <p>
     * An exact parameter {@link Comparator} for {@link MTType}.
     * </p>
     */
    private static final Comparator<MTType> EXACT_PARAMETER_MATCH = new ExactParameterMatch();

    /**
     * <p>
     * A {@link TypeComparison} for to find inexact domain match between a {@link AbstractFunctionExp} and a
     * {@link MTType}.
     * </p>
     */
    private final TypeComparison<AbstractFunctionExp, MTFunction> INEXACT_DOMAIN_MATCH = new InexactDomainMatch();

    /**
     * <p>
     * A {@link TypeComparison} for to find inexact parameter match between a {@link Exp} and a {@link MTType}.
     * </p>
     */
    private final TypeComparison<Exp, MTType> INEXACT_PARAMETER_MATCH = new InexactParameterMatch();

    // -----------------------------------------------------------
    // Operation Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * While walking a procedure, this is set to the entry for the operation or {@link OperationProcedureDec} that the
     * procedure is attempting to implement.
     * </p>
     *
     * <p>
     * <strong>INVARIANT:</strong> <code>myCorrespondingOperation != null</code> <em>implies</em>
     * <code>myCurrentParameters != null</code>.
     * </p>
     */
    private OperationEntry myCorrespondingOperation;

    /**
     * <p>
     * While we walk the children of an operation, {@link OperationProcedureDec}, or procedure, this list will contain
     * all formal parameters encountered so far, otherwise it will be null. Since none of these structures can be be
     * nested, there's no need for a stack.
     * </p>
     *
     * <p>
     * If you need to distinguish if you're in the middle of an operation/{@link OperationProcedureDec} or a procedure,
     * check {@code myCorrespondingOperation}.
     * </p>
     */
    private List<ProgramParameterEntry> myCurrentParameters;

    /**
     * <p>
     * While we walk the children of a {@link OperationProcedureDec}, this will be set to the
     * {@link OperationProcedureDec}. Otherwise it will be {@code null}.
     * </p>
     */
    private OperationProcedureDec myCurrentLocalProcedure;

    /**
     * <p>
     * While we walk the children of a {@link OperationProcedureDec}, this will be set to the scope prior to the
     * {@link OperationProcedureDec} scope. Otherwise it will be {@code null}.
     * </p>
     */
    private ScopeBuilder myPreOperationProcedureDecScope;

    /**
     * <p>
     * While we walk the children of a {@link OperationProcedureDec} or {@link ProcedureDec}, this will initially be set
     * to {@code null}. If we detect a recursive call to itself, this will be set to the location of the first recursive
     * call.
     * </p>
     */
    private Location myRecursiveCallLocation;

    // -----------------------------------------------------------
    // Math Assertion Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * A set of operators for the current mathematical assertion declaration that we are walking.
     * </p>
     */
    private Set<Exp> myMathAssertionOps;

    // -----------------------------------------------------------
    // Type Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * When parsing a type realization declaration, this is set to the entry corresponding to the conceptual declaration
     * from the concept. When not inside such a declaration, this will be null.
     * </p>
     */
    private TypeFamilyEntry myTypeFamilyEntry;

    /**
     * <p>
     * When parsing a type realization declaration, this is set to its representation entry obtained from its children.
     * When not inside such a declaration, this will be null.
     * </p>
     */
    private PTRepresentation myPTRepresentationType;

    // -----------------------------------------------------------
    // Math/Program Typing-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Any quantification-introducing syntactic node (like, e.g., a {@link QuantExp}), introduces a level to this stack
     * to reflect the quantification that should be applied to named variables as they are encountered. Note that this
     * may change as the children of the node are processed--for example, {@link MathVarDec MathVarDecs} found in the
     * declaration portion of a {@link QuantExp} should have quantification (universal or existential) applied, while
     * those found in the body of the {@link QuantExp} should have no quantification (unless there is an embedded
     * {@link QuantExp}). In this case, {@link QuantExp} should <em>not</em> remove its layer, but rather change it to
     * MathSymbolTableEntry.None.
     * </p>
     *
     * <p>
     * This stack is never empty, but rather the bottom layer is always MathSymbolTableEntry.None.
     * </p>
     */
    private final Deque<SymbolTableEntry.Quantification> myActiveQuantifications = new LinkedList<>();

    /**
     * <p>
     * While we walk the children of a direct definition, this will be set with a pointer to the definition declaration
     * we are walking, otherwise it will be null. Note that definitions cannot be nested, so there's no need for a
     * stack.
     * </p>
     */
    private MathDefinitionDec myCurrentDirectDefinition;

    /**
     * <p>
     * This simply enables an error check--as a definition uses named types, we keep track of them, and when an implicit
     * type is introduced, we make sure that it hasn't been "used" yet, thus leading to a confusing scenario where some
     * instances of the name should refer to a type already in scope as the definition is declared and other instance
     * refer to the implicit type parameter.
     * </p>
     */
    private final Set<String> myDefinitionNamedTypes = new HashSet<>();

    /**
     * <p>
     * While walking the parameters of a definition, this flag will be set to true.
     * </p>
     */
    private boolean myDefinitionParameterSectionFlag = false;

    /**
     * <p>
     * A mapping for definition defined schematic types.
     * </p>
     */
    private final Map<String, MTType> myDefinitionSchematicTypes = new HashMap<>();

    /**
     * <p>
     * A mapping from generic types that appear in the module to the math types that bound their possible values.
     * </p>
     */
    private final Map<String, MTType> myGenericTypes = new HashMap<>();

    /**
     * <p>
     * An helper value that helps evaluate how deep is the expression we are trying to evaluate.
     * </p>
     */
    private int myExpressionDepth = 0;

    /**
     * <p>
     * A flag that indicates whether or not we are binding an expression inside a type theorem.
     * </p>
     */
    private boolean myInTypeTheoremBindingExpFlag = false;

    /**
     * <p>
     * An helper value that helps evaluate mathematical type values.
     * </p>
     */
    private int myTypeValueDepth = 0;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_POPULATOR_NAME = "Populator";
    private static final String FLAG_POPULATOR_DEBUG_INFO = "Populator Debug Flag";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>
     * Tells the compiler to print out {@code Populator}/{@code TypeGraph} information messages.
     * </p>
     */
    public static final Flag FLAG_POPULATOR_DEBUG = new Flag(FLAG_POPULATOR_NAME, "populatorDebug",
            FLAG_POPULATOR_DEBUG_INFO);

    /**
     * <p>
     * Add all the required and implied flags for the {@code Populator}.
     * </p>
     */
    public static void setUpFlags() {
        FlagDependencies.addImplies(FLAG_POPULATOR_DEBUG, ResolveCompiler.FLAG_DEBUG);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that overrides methods to populate and analyze a generated {@link ModuleDec}.
     * </p>
     *
     * @param builder
     *            A scope builder for a symbol table.
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     */
    public Populator(MathSymbolTableBuilder builder, CompileEnvironment compileEnvironment) {
        myActiveQuantifications.push(SymbolTableEntry.Quantification.NONE);
        myTypeGraph = builder.getTypeGraph();
        myBuilder = builder;
        myCompileEnvironment = compileEnvironment;
        myStatusHandler = myCompileEnvironment.getStatusHandler();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    /**
     * <p>
     * Code that gets executed after visiting any {@link ResolveConceptualElement}.
     * </p>
     *
     * @param e
     *            Any element that inherits from {@link ResolveConceptualElement}.
     */
    @Override
    public final void postAny(ResolveConceptualElement e) {
        if (e instanceof Ty) {
            Ty eTy = (Ty) e;
            if (eTy.getMathTypeValue() == null) {
                throw new NullMathTypeException("Ty " + e + " (" + e.getClass() + ", " + e.getLocation()
                        + ") got through the populator with no math type value.");
            }
            if (!(e instanceof ArbitraryExpTy) && eTy.getProgramType() == null) {
                throw new NullProgramTypeException("Ty " + e + " (" + e.getClass() + ", " + e.getLocation()
                        + ") got through the " + "populator with no program type value.");
            }
        }
    }

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
        emitDebug(null, "----------------------\nModule: " + dec.getName().getName() + "\n----------------------");
        myCurModuleScope = myBuilder.startModuleScope(dec);
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
    public final void postModuleDec(ModuleDec dec) {
        myBuilder.endScope();
        emitDebug(null, "END POPULATOR\n----------------------\n");
    }

    // -----------------------------------------------------------
    // Concept Module
    // -----------------------------------------------------------

    /**
     * <p>
     * This method redefines how a {@link ConceptModuleDec} should be walked.
     * </p>
     *
     * @param dec
     *            A concept module declaration.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkConceptModuleDec(ConceptModuleDec dec) {
        preAny(dec);
        preDec(dec);
        preModuleDec(dec);
        preConceptModuleDec(dec);

        // Walk our uses list first! Our parameters might need this
        for (UsesItem item : dec.getUsesItems()) {
            TreeWalker.visit(this, item);
        }

        // Walk our parameters
        for (ModuleParameterDec varDec : dec.getParameterDecs()) {
            TreeWalker.visit(this, varDec);
        }

        // Walk the requires clause
        TreeWalker.visit(this, dec.getRequires());

        // Walk our declarations
        for (Dec innerDecl : dec.getDecList()) {
            TreeWalker.visit(this, innerDecl);
        }

        // Walk the list of constraints clauses
        for (AssertionClause constraintClause : dec.getConstraints()) {
            TreeWalker.visit(this, constraintClause);
        }

        postConceptModuleDec(dec);
        postModuleDec(dec);
        postDec(dec);
        postAny(dec);

        return true;
    }

    // -----------------------------------------------------------
    // Concept Realization Module
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link ConceptRealizModuleDec}.
     * </p>
     *
     * @param conceptRealization
     *            A concept realization module declaration.
     */
    @Override
    public final void preConceptRealizModuleDec(ConceptRealizModuleDec conceptRealization) {
        // Concept Module Identifier
        ModuleIdentifier id = new ModuleIdentifier(conceptRealization.getConceptName().getName());

        // Check if the concept realization implements all operations specified
        // by the concept.
        try {
            ConceptModuleDec concept = (ConceptModuleDec) myBuilder.getModuleScope(id).getDefiningElement();
            ImplementAllOperChecker allOperChecker = new ImplementAllOperChecker(conceptRealization.getLocation(),
                    concept.getDecList(), conceptRealization.getDecList());
            allOperChecker.implementAllOper();
        } catch (NoSuchSymbolException e) {
            noSuchModule(conceptRealization.getConceptName());
        }

        // Concept realizations implicitly import the concepts they realize
        myCurModuleScope.addImport(id);
    }

    /**
     * <p>
     * This method redefines how a {@link ConceptRealizModuleDec} should be walked.
     * </p>
     *
     * @param dec
     *            A concept realization module declaration.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkConceptRealizModuleDec(ConceptRealizModuleDec dec) {
        preAny(dec);
        preDec(dec);
        preModuleDec(dec);
        preConceptRealizModuleDec(dec);

        // Walk our uses list first! Our parameters might need this
        for (UsesItem item : dec.getUsesItems()) {
            TreeWalker.visit(this, item);
        }

        // Walk our parameters
        for (ModuleParameterDec varDec : dec.getParameterDecs()) {
            TreeWalker.visit(this, varDec);
        }

        // Walk the requires clause
        TreeWalker.visit(this, dec.getRequires());

        // Walk our declarations
        for (Dec innerDecl : dec.getDecList()) {
            TreeWalker.visit(this, innerDecl);
        }

        postConceptRealizModuleDec(dec);
        postModuleDec(dec);
        postDec(dec);
        postAny(dec);

        return true;
    }

    // -----------------------------------------------------------
    // Enhancement Module
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting an {@link EnhancementModuleDec}.
     * </p>
     *
     * @param enhancement
     *            An enhancement module declaration.
     */
    @Override
    public final void preEnhancementModuleDec(EnhancementModuleDec enhancement) {
        // Enhancements implicitly import the concepts they enhance
        myCurModuleScope.addImport(new ModuleIdentifier(enhancement.getConceptName().getName()));
    }

    /**
     * <p>
     * This method redefines how an {@link EnhancementModuleDec} should be walked.
     * </p>
     *
     * @param dec
     *            An enhancement module declaration.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkEnhancementModuleDec(EnhancementModuleDec dec) {
        preAny(dec);
        preDec(dec);
        preModuleDec(dec);
        preEnhancementModuleDec(dec);

        // Walk our uses list first! Our parameters might need this
        for (UsesItem item : dec.getUsesItems()) {
            TreeWalker.visit(this, item);
        }

        // Walk our parameters
        for (ModuleParameterDec varDec : dec.getParameterDecs()) {
            TreeWalker.visit(this, varDec);
        }

        // Walk the requires clause
        TreeWalker.visit(this, dec.getRequires());

        // Walk our declarations
        for (Dec innerDecl : dec.getDecList()) {
            TreeWalker.visit(this, innerDecl);
        }

        postEnhancementModuleDec(dec);
        postModuleDec(dec);
        postDec(dec);
        postAny(dec);

        return true;
    }

    // -----------------------------------------------------------
    // Enhancement Realization Module
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting an {@link EnhancementRealizModuleDec}.
     * </p>
     *
     * @param enhancementRealization
     *            An enhancement realization module declaration.
     */
    @Override
    public final void preEnhancementRealizModuleDec(EnhancementRealizModuleDec enhancementRealization) {
        // Concept Module Identifier
        ModuleIdentifier coId = new ModuleIdentifier(enhancementRealization.getConceptName().getName());

        // Enhancement Module Identifier
        ModuleIdentifier enId = new ModuleIdentifier(enhancementRealization.getEnhancementName().getName());

        // Check if the enhancement realization implements all operations specified
        // by the enhancement.
        try {
            EnhancementModuleDec enhancement = (EnhancementModuleDec) myBuilder.getModuleScope(enId)
                    .getDefiningElement();
            ImplementAllOperChecker allOperChecker = new ImplementAllOperChecker(enhancementRealization.getLocation(),
                    enhancement.getDecList(), enhancementRealization.getDecList());
            allOperChecker.implementAllOper();
        } catch (NoSuchSymbolException e) {
            noSuchModule(enhancementRealization.getEnhancementName());
        }

        // Enhancement realizations implicitly import the concepts they enhance
        // and the enhancements they realize
        myCurModuleScope.addImport(coId);
        myCurModuleScope.addImport(enId);

        // Enhancement realizations implicitly import the performance profiles
        // if they are specified.
        PosSymbol profileName = enhancementRealization.getProfileName();
        if (profileName != null) {
            myCurModuleScope.addImport(new ModuleIdentifier(profileName.getName()));
        }
    }

    /**
     * <p>
     * This method redefines how an {@link EnhancementRealizModuleDec} should be walked.
     * </p>
     *
     * @param dec
     *            An enhancement realization module declaration.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkEnhancementRealizModuleDec(EnhancementRealizModuleDec dec) {
        preAny(dec);
        preDec(dec);
        preModuleDec(dec);
        preEnhancementRealizModuleDec(dec);

        // Walk our uses list first! Our parameters might need this
        for (UsesItem item : dec.getUsesItems()) {
            TreeWalker.visit(this, item);
        }

        // Walk our parameters
        for (ModuleParameterDec varDec : dec.getParameterDecs()) {
            TreeWalker.visit(this, varDec);
        }

        // Walk the requires clause
        TreeWalker.visit(this, dec.getRequires());

        // Walk our declarations
        for (Dec innerDecl : dec.getDecList()) {
            TreeWalker.visit(this, innerDecl);
        }

        postEnhancementRealizModuleDec(dec);
        postModuleDec(dec);
        postDec(dec);
        postAny(dec);

        return true;
    }

    // -----------------------------------------------------------
    // Performance Profile Module for Concepts
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link PerformanceConceptModuleDec}.
     * </p>
     *
     * @param conceptProfile
     *            A concept profile module declaration.
     */
    @Override
    public final void prePerformanceConceptModuleDec(PerformanceConceptModuleDec conceptProfile) {
        // Concept performance profiles implicitly import the concepts they are profiling
        myCurModuleScope.addImport(new ModuleIdentifier(conceptProfile.getConceptName().getName()));
    }

    // -----------------------------------------------------------
    // Performance Profile Module for Enhancements
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link PerformanceEnhancementModuleDec}.
     * </p>
     *
     * @param enhancementProfile
     *            An enhancement profile module declaration.
     */
    @Override
    public final void prePerformanceEnhancementModuleDec(PerformanceEnhancementModuleDec enhancementProfile) {
        // Enhancement performance profiles implicitly import the concepts,
        // concept profile and enhancement they are profiling.
        myCurModuleScope.addImport(new ModuleIdentifier(enhancementProfile.getConceptName().getName()));
        myCurModuleScope.addImport(new ModuleIdentifier(enhancementProfile.getConceptProfileName().getName()));
        myCurModuleScope.addImport(new ModuleIdentifier(enhancementProfile.getEnhancementName().getName()));
    }

    // -----------------------------------------------------------
    // Uses Items (Imports)
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting a {@link UsesItem}.
     * </p>
     *
     * @param uses
     *            An uses item declaration.
     */
    @Override
    public final void postUsesItem(UsesItem uses) {
        // We add the module identifier to the current module scope
        // because we might need to search our imports.
        ModuleIdentifier id = new ModuleIdentifier(uses);
        myCurModuleScope.addImport(id);

        // Check if we are importing a sharing concept.
        try {
            ModuleDec moduleDec = myBuilder.getModuleScope(id).getDefiningElement();
            NoSharingConceptImportChecker checker = new NoSharingConceptImportChecker(uses.getLocation(), moduleDec,
                    myBuilder.getInnermostActiveScope());
            if (checker.importingSharingConcept()) {
                throw new SourceErrorException("Cannot import a Sharing Concept or a module that "
                        + "contains an instantiation of a Sharing Concept.", uses.getLocation());
            }
        } catch (NoSuchSymbolException e) {
            noSuchModule(uses.getName());
        }
    }

    // -----------------------------------------------------------
    // Module parameter declarations
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting a {@link ModuleParameterDec}.
     * </p>
     *
     * @param d
     *            A module parameter declaration.
     */
    @Override
    public final void postModuleParameterDec(ModuleParameterDec d) {
        if (!(d.getWrappedDec() instanceof OperationDec)) {
            if (d.getWrappedDec().getMathType() == null) {
                throw new NullMathTypeException(d.getWrappedDec().getClass() + " has null type");
            }

            d.setMathType(d.getWrappedDec().getMathType());
        } else {
            MTType t = (d.getWrappedDec()).getMathType();
            if (t == null) {
                t = myTypeGraph.VOID;
            }
            d.setMathType(t);
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ConceptTypeParamDec}.
     * </p>
     *
     * @param param
     *            A concept type parameter declaration.
     */
    @Override
    public final void postConceptTypeParamDec(ConceptTypeParamDec param) {
        try {
            String paramName = param.getName().getName();

            myBuilder.getInnermostActiveScope().addFormalParameter(paramName, param,
                    ProgramParameterEntry.ParameterMode.TYPE, new PTElement(myTypeGraph));

            myGenericTypes.put(paramName, myTypeGraph.SSET);
            param.setMathType(myTypeGraph.SSET);
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(param.getName().getName(), param.getName().getLocation());
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ConstantParamDec}.
     * </p>
     *
     * @param param
     *            A constant parameter declaration.
     */
    @Override
    public final void postConstantParamDec(ConstantParamDec param) {
        try {
            String paramName = param.getName().getName();

            Ty rawType = param.getVarDec().getTy();
            myBuilder.getInnermostActiveScope().addFormalParameter(paramName, param,
                    ProgramParameterEntry.ParameterMode.EVALUATES, rawType.getProgramType());
            param.setMathType(rawType.getMathTypeValue());
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(param.getName().getName(), param.getName().getLocation());
        }
    }

    /**
     * <p>
     * This method redefines how a {@link ConstantParamDec} should be walked.
     * </p>
     *
     * @param param
     *            A constant parameter declaration.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkConstantParamDec(ConstantParamDec param) {
        preAny(param);
        preDec(param);
        preConstantParamDec(param);

        // YS - Since ConstantParamDec uses a ParameterVarDec as its inner
        // representation, we don't want to walk ParameterVarDec object.
        // The reason is because ParameterVarDec contains additional logic
        // for Operation-like items. Instead, we simply walk the raw type.
        TreeWalker.visit(this, param.getVarDec().getTy());

        postConstantParamDec(param);
        postDec(param);
        postAny(param);

        return true;
    }

    // -----------------------------------------------------------
    // Mathematical Type Definition
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link TypeDefinitionDec}.
     * </p>
     *
     * @param dec
     *            A mathematical type definition declaration.
     */
    @Override
    public final void preTypeDefinitionDec(TypeDefinitionDec dec) {
        myBuilder.startScope(dec);

        myDefinitionSchematicTypes.clear();
        myDefinitionNamedTypes.clear();
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link TypeDefinitionDec}.
     * </p>
     *
     * @param dec
     *            A mathematical type definition declaration.
     */
    @Override
    public final void postTypeDefinitionDec(TypeDefinitionDec dec) {
        myBuilder.endScope();

        String definitionSymbol = dec.getName().getName();
        MTType declaredType = myTypeGraph.SSET;
        MTType typeValue = dec.getModel().getMathTypeValue();

        addBinding(definitionSymbol, dec.getName().getLocation(), dec, declaredType, typeValue,
                myDefinitionSchematicTypes);

        emitDebug(dec.getLocation(), "\t\tNew type definition: " + definitionSymbol + " of type " + declaredType
                + " with type value " + typeValue);

        myCurrentDirectDefinition = null;
        myDefinitionSchematicTypes.clear();

        dec.setMathType(declaredType);
    }

    // -----------------------------------------------------------
    // Mathematical Assertion/Theorem-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link MathAssertionDec}.
     * </p>
     *
     * @param dec
     *            A mathematical assertion declaration.
     */
    @Override
    public final void preMathAssertionDec(MathAssertionDec dec) {
        myMathAssertionOps = new LinkedHashSet<>();
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link MathAssertionDec}.
     * </p>
     *
     * @param dec
     *            A mathematical assertion declaration.
     */
    @Override
    public final void postMathAssertionDec(MathAssertionDec dec) {
        expectType(dec.getAssertion(), myTypeGraph.BOOLEAN);

        String name = dec.getName().getName();
        try {
            myBuilder.getInnermostActiveScope().addTheorem(name, dec, myMathAssertionOps);
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(name, dec.getName().getLocation());
        }

        myDefinitionSchematicTypes.clear();
        myMathAssertionOps = null;

        emitDebug(dec.getLocation(), "\t\tNew theorem: " + name);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link MathDefinitionDec}.
     * </p>
     *
     * @param dec
     *            A mathematical definition declaration.
     */
    @Override
    public final void preMathDefinitionDec(MathDefinitionDec dec) {
        myBuilder.startScope(dec);

        if (!dec.getIsInductiveFlag()) {
            myCurrentDirectDefinition = dec;
        }

        myDefinitionSchematicTypes.clear();
        myDefinitionNamedTypes.clear();
    }

    /**
     * <p>
     * Code that gets executed before visiting the list of parameter declarations inside a {@link MathDefinitionDec}.
     * </p>
     *
     * @param dec
     *            A mathematical definition declaration.
     */
    @Override
    public final void preMathDefinitionDecMyParameters(MathDefinitionDec dec) {
        myDefinitionParameterSectionFlag = true;
    }

    /**
     * <p>
     * Code that gets executed after visiting the list of parameter declarations inside a {@link MathDefinitionDec}.
     * </p>
     *
     * @param dec
     *            A mathematical definition declaration.
     */
    @Override
    public final void postMathDefinitionDecMyParameters(MathDefinitionDec dec) {
        myDefinitionParameterSectionFlag = false;
    }

    /**
     * <p>
     * Code that gets executed in between visiting items inside {@link MathDefinitionDec}.
     * </p>
     *
     * @param dec
     *            A mathematical definition declaration.
     * @param prevChild
     *            The previous child item visited.
     * @param nextChild
     *            The next child item to be visited.
     */
    @Override
    public final void midMathDefinitionDec(MathDefinitionDec dec, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (dec.getIsInductiveFlag() && nextChild instanceof DefinitionBodyItem) {
            try {
                myBuilder.getInnermostActiveScope().addBinding(dec.getName().getName(), dec,
                        new MTFunction(myTypeGraph, dec));
            } catch (DuplicateSymbolException e) {
                // we tried!
            }
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link MathDefinitionDec}.
     * </p>
     *
     * @param dec
     *            A mathematical definition declaration.
     */
    @Override
    public final void postMathDefinitionDec(MathDefinitionDec dec) {
        myBuilder.endScope();

        MTType declaredType = dec.getReturnTy().getMathTypeValue();

        if (dec.getDefinition() != null) {
            expectType(dec.getDefinition(), declaredType);
        } else if (dec.getIsInductiveFlag()) {
            expectType(dec.getBase(), myTypeGraph.BOOLEAN);
            expectType(dec.getHypothesis(), myTypeGraph.BOOLEAN);
        }

        List<MathVarDec> listVarDec = dec.getParameters();
        if (listVarDec.size() > 0) {
            declaredType = new MTFunction(myTypeGraph, dec);
        }

        String definitionSymbol = dec.getName().getName();

        MTType typeValue = null;
        if (dec.getDefinition() != null) {
            typeValue = dec.getDefinition().getMathTypeValue();
        }

        // Note that, even if typeValue is null at this point, if declaredType
        // returns true from knownToContainOnlyMTypes(), a new type value will
        // still be created by the symbol table
        addBinding(definitionSymbol, dec.getName().getLocation(), dec, declaredType, typeValue,
                myDefinitionSchematicTypes);

        emitDebug(dec.getLocation(), "\t\tNew definition: " + definitionSymbol + " of type " + declaredType
                + ((typeValue != null) ? " with type value " + typeValue : ""));

        myCurrentDirectDefinition = null;
        myDefinitionSchematicTypes.clear();

        dec.setMathType(declaredType);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link MathDefVariableDec}.
     * </p>
     *
     * @param dec
     *            A mathematical definition variable declaration.
     */
    @Override
    public final void preMathDefVariableDec(MathDefVariableDec dec) {
        myBuilder.startScope(dec);
        myDefinitionSchematicTypes.clear();
        myDefinitionNamedTypes.clear();
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link MathDefVariableDec}.
     * </p>
     *
     * @param dec
     *            A mathematical definition variable declaration.
     */
    @Override
    public final void postMathDefVariableDec(MathDefVariableDec dec) {
        myBuilder.endScope();

        // The return type for this definition variable is inside
        // the inner MathVarDec.
        MathVarDec varDec = dec.getVariable();
        MTType declaredType = varDec.getTy().getMathTypeValue();

        // Make sure that our definition matches the declared type
        if (dec.getDefinitionAsExp() != null) {
            // TODO (YS): Try to see why our definition variable's type doesn't match the definition.
            // expectType(dec.getDefinitionAsExp(), declaredType);
        }

        String definitionSymbol = varDec.getName().getName();

        MTType typeValue = null;
        if (dec.getDefinitionAsExp() != null) {
            typeValue = dec.getDefinitionAsExp().getMathTypeValue();
        }

        // Note that, even if typeValue is null at this point, if declaredType
        // returns true from knownToContainOnlyMTypes(), a new type value will
        // still be created by the symbol table
        myCurModuleScope.addBinding(definitionSymbol, SymbolTableEntry.Quantification.NONE, dec, declaredType,
                typeValue, myDefinitionSchematicTypes, myGenericTypes);

        emitDebug(dec.getLocation(), "\t\tNew definition variable: " + definitionSymbol + " of type " + declaredType
                + ((typeValue != null) ? " with type value " + typeValue : ""));

        myCurrentDirectDefinition = null;
        myDefinitionSchematicTypes.clear();

        dec.setMathType(declaredType);
    }

    /**
     * <p>
     * This method redefines how a {@link MathDefVariableDec} should be walked.
     * </p>
     *
     * @param dec
     *            A mathematical definition variable declaration.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkMathDefVariableDec(MathDefVariableDec dec) {
        preAny(dec);
        preDec(dec);
        preMathDefVariableDec(dec);

        // YS - Since MathDefVariableDec uses a MathVarDec as its inner
        // representation, we don't want to walk MathVarDec object.
        // The reason is because MathVarDec will add a binding for
        // the math variable in the wrong place and will contain random
        // type values. Instead, we only walk the raw type
        // and the definition item. The definition variable binding
        // will happen during postMathDefVariableDec
        TreeWalker.visit(this, dec.getVariable().getTy());
        TreeWalker.visit(this, dec.getDefinitionItem());

        postMathDefVariableDec(dec);
        postDec(dec);
        postAny(dec);

        return true;
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link MathTypeTheoremDec}.
     * </p>
     *
     * @param dec
     *            A mathematical type theorem declaration.
     */
    @Override
    public final void preMathTypeTheoremDec(MathTypeTheoremDec dec) {
        myBuilder.startScope(dec);
        myInTypeTheoremBindingExpFlag = false;
        myActiveQuantifications.push(SymbolTableEntry.Quantification.UNIVERSAL);
    }

    /**
     * <p>
     * Code that gets executed after visiting the list of universal variable declarations inside a
     * {@link MathTypeTheoremDec}.
     * </p>
     *
     * @param dec
     *            A mathematical type theorem declaration.
     */
    @Override
    public final void postMathTypeTheoremDecMyUniversalVars(MathTypeTheoremDec dec) {
        myInTypeTheoremBindingExpFlag = true;
        myActiveQuantifications.pop();
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link MathTypeTheoremDec}.
     * </p>
     *
     * @param dec
     *            A mathematical type theorem declaration.
     */
    @Override
    public final void postMathTypeTheoremDec(MathTypeTheoremDec dec) {
        dec.setMathType(myTypeGraph.BOOLEAN);

        Exp assertion = dec.getAssertion();
        Exp condition;
        Exp bindingExpression;
        ArbitraryExpTy typeExp;
        try {
            if (assertion instanceof InfixExp) {
                InfixExp assertionsAsInfixExp = (InfixExp) assertion;
                String operator = assertionsAsInfixExp.getOperatorAsString();

                if (operator.equals("implies")) {
                    condition = assertionsAsInfixExp.getLeft();
                    assertion = assertionsAsInfixExp.getRight();
                } else {
                    throw new ClassCastException();
                }
            } else {
                condition = MathExp.getTrueVarExp(dec.getLocation(), myTypeGraph);
            }

            TypeAssertionExp assertionAsTAE = (TypeAssertionExp) assertion;

            bindingExpression = assertionAsTAE.getExp();
            typeExp = assertionAsTAE.getAssertedTy();

            try {
                myTypeGraph.addRelationship(bindingExpression, typeExp.getMathTypeValue(), condition,
                        myBuilder.getInnermostActiveScope());
            } catch (IllegalArgumentException iae) {
                throw new SourceErrorException(iae.getMessage(), dec.getLocation());
            }
        } catch (ClassCastException cse) {
            throw new SourceErrorException("top level of type theorem " + "assertion must be 'implies' or ':'",
                    assertion.getLocation());
        }

        myBuilder.endScope();
    }

    // -----------------------------------------------------------
    // Facility Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting a {@link FacilityDec}.
     * </p>
     *
     * @param facility
     *            A facility declaration.
     */
    @Override
    public final void postFacilityDec(FacilityDec facility) {
        // Sanity check the facility declaration
        ValidFacilityDeclChecker facilityDeclChecker = new ValidFacilityDeclChecker(facility, myBuilder);
        facilityDeclChecker.hasValidModuleArgumentItems();

        // Concept Module Identifier
        ModuleIdentifier id = new ModuleIdentifier(facility.getConceptName().getName());

        // Check to see if we are instantiating a sharing concept
        try {
            ConceptModuleDec concept = (ConceptModuleDec) myBuilder.getModuleScope(id).getDefiningElement();
            myBuilder.getInnermostActiveScope().addFacility(facility, concept.isSharingConcept());
        } catch (NoSuchSymbolException e) {
            noSuchModule(facility.getConceptName());
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(facility.getName().getName(), facility.getName().getLocation());
        }
    }

    /**
     * <p>
     * This method redefines how a {@link ModuleArgumentItem} should be walked.
     * </p>
     *
     * @param item
     *            A module argument item from a facility declaration.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkModuleArgumentItem(ModuleArgumentItem item) {
        preAny(item);

        // YS - There are two possible scenarios for a module argument item.
        // 1. It is a program variable name that refers to some definition,
        // operation or type.
        // 2. It is a program expression that is getting evaluated.
        // For #1, we will need to query for a symbol table entry with the
        // same name and assign whatever type we find. Sanity checking happens
        // when we reach postFacilityDec. For #2, we simply walk the expression and
        // use the existing logic.
        ProgramExp argumentExp = item.getArgumentExp();
        if (argumentExp instanceof ProgramVariableNameExp) {
            // YS - My guess is that this is the only kind of program variable
            // we will have to deal with. I don't see any case where we could
            // possibly pass a ProgramVariableDotExp in a module argument item
            // that isn't used as an evaluated expression. If this ever comes up,
            // we will need to do some kind of special logic.
            ProgramVariableNameExp argExpAsProgVarNameExp = (ProgramVariableNameExp) argumentExp;
            List<SymbolTableEntry> es = myBuilder.getInnermostActiveScope()
                    .query(new NameQuery(argExpAsProgVarNameExp.getQualifier(), argExpAsProgVarNameExp.getName(),
                            ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_INSTANTIATE, false));

            if (es.isEmpty()) {
                noSuchSymbol(argExpAsProgVarNameExp.getQualifier(), argExpAsProgVarNameExp.getName());
            } else if (es.size() > 1) {
                ambiguousSymbol(argExpAsProgVarNameExp.getName(), es);
            } else {
                try {
                    SymbolTableEntry ste = es.get(0);
                    ResolveConceptualElement rce = ste.getDefiningElement();
                    PTType pt;

                    // Store it's math type
                    if (rce instanceof TypeFamilyDec) {
                        pt = ste.toProgramTypeEntry(argExpAsProgVarNameExp.getLocation()).getProgramType();
                    } else if (rce instanceof OperationDec || rce instanceof OperationProcedureDec) {
                        pt = ste.toOperationEntry(argExpAsProgVarNameExp.getLocation()).getReturnType();
                    } else if (rce instanceof FacilityTypeRepresentationDec) {
                        pt = ste.toFacilityTypeRepresentationEntry(argExpAsProgVarNameExp.getLocation())
                                .getRepresentationType();
                    } else {
                        pt = ste.toProgramVariableEntry(argExpAsProgVarNameExp.getLocation()).getProgramType();
                    }
                    argExpAsProgVarNameExp.setMathType(pt.toMath());

                    // Store it's program type
                    ProgramTypeEntry e = ste.toProgramTypeEntry(argExpAsProgVarNameExp.getLocation());
                    argExpAsProgVarNameExp.setProgramType(e.getProgramType());
                } catch (SourceErrorException see) {
                    // YS - Our sanity check will detect this as an error.
                    argExpAsProgVarNameExp.setProgramType(PTVoid.getInstance(myTypeGraph));
                }
            }
        } else {
            TreeWalker.visit(this, argumentExp);
        }

        postAny(item);

        return true;
    }

    // -----------------------------------------------------------
    // Operation Declaration-Related
    // -----------------------------------------------------------

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
        // Store the innermost active scope for future use.
        myPreOperationProcedureDecScope = myBuilder.getInnermostActiveScope();

        myBuilder.startScope(dec);
        myCurrentLocalProcedure = dec;

        // This will be set to a location if we are recursively calling ourselves.
        // Once we finish walking all the children, we will make sure this dec
        // is declared as recursive.
        myRecursiveCallLocation = null;
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
        myBuilder.endScope();

        // Sanity checks to make sure it is a valid recursive procedure
        ValidOperationDeclChecker validOperationDeclChecker = new ValidOperationDeclChecker(dec.getLocation(),
                myCorrespondingOperation, myCurrentParameters);
        validOperationDeclChecker.isValidRecursiveProcedure(dec.getRecursive(), myRecursiveCallLocation);

        myCurrentParameters = null;
        myPreOperationProcedureDecScope = null;
        myCurrentLocalProcedure = null;
        myRecursiveCallLocation = null;
    }

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
        // If this is not an OperationDec wrapped inside a OperationProcedureDec,
        // then we need to start a new scope
        if (myCurrentLocalProcedure == null) {
            myBuilder.startScope(dec);
        }

        // Create a new list for parameter entries.
        myCurrentParameters = new LinkedList<>();
    }

    /**
     * <p>
     * Code that gets executed in between visiting items inside {@link OperationDec}.
     * </p>
     *
     * @param node
     *            An operation declaration.
     * @param prevChild
     *            The previous child item visited.
     * @param nextChild
     *            The next child item to be visited.
     */
    @Override
    public final void midOperationDec(OperationDec node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        // If this is not an OperationDec wrapped inside a OperationProcedureDec,
        // then we need to add the return variable as a mathematical symbol to the OperationDec scope.
        if (myCurrentLocalProcedure == null) {
            if (prevChild == node.getReturnTy() && node.getReturnTy() != null) {
                try {
                    // Inside the operation's assertions, the name of the operation
                    // refers to its return value
                    myBuilder.getInnermostActiveScope().addBinding(node.getName().getName(), node,
                            node.getReturnTy().getMathTypeValue());
                } catch (DuplicateSymbolException dse) {
                    // This shouldn't be possible--the operation declaration has a
                    // scope all its own and we're the first ones to get to
                    // introduce anything
                    throw new RuntimeException(dse);
                }
            }
        }
        // Else we need to add the return variable as a programming variable to the
        // OperationProcedureDec scope.
        else {
            if (prevChild == node.getReturnTy() && node.getReturnTy() != null) {
                try {
                    // Inside the operation's assertions, the name of the operation
                    // refers to its return value
                    myBuilder.getInnermostActiveScope().addProgramVariable(node.getName().getName(), node,
                            node.getReturnTy().getProgramType());
                } catch (DuplicateSymbolException dse) {
                    // This shouldn't be possible--the operation declaration has a
                    // scope all its own and we're the first ones to get to
                    // introduce anything
                    throw new RuntimeException(dse);
                }
            }
        }
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
        // If this is not an OperationDec wrapped inside a FacilityOperationDec,
        // we need to end the current scope, add the operation declaration using
        // the inner most active scope and set the parameter list to null.
        if (myCurrentLocalProcedure == null) {
            myBuilder.endScope();

            putOperationLikeThingInSymbolTable(dec.getName(), dec.getReturnTy(), dec,
                    myBuilder.getInnermostActiveScope());

            myCurrentParameters = null;
        }
        // Else we need to add the OperationProcedureDec to the SymbolTable
        // using the stored pre-OperationProcedureDec scope.
        else {
            putOperationLikeThingInSymbolTable(dec.getName(), dec.getReturnTy(), myCurrentLocalProcedure,
                    myPreOperationProcedureDecScope);

            // Similar to preProcedureDec, need to store the OperationEntry for
            // walking the statements.
            try {
                // Figure out what Operation we correspond to (we don't use
                // OperationQuery because we want to check parameter types
                // separately in postProcedureDec)
                myCorrespondingOperation = myBuilder.getInnermostActiveScope()
                        .queryForOne(new NameAndEntryTypeQuery<>(null, dec.getName(), OperationEntry.class,
                                ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_IGNORE, false))
                        .toOperationEntry(dec.getLocation());
            } catch (NoSuchSymbolException nsse) {
                // We just added this, so this is not possible.
                throw new RuntimeException("Cannot find an Operation with name " + dec.getName().getName() + "?");
            } catch (DuplicateSymbolException dse) {
                // We should have caught this before now, like when we defined the
                // duplicate Operation
                throw new RuntimeException("Duplicate Operations for " + dec.getName().getName() + "?");
            }
        }
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
        try {
            // Figure out what Operation we correspond to (we don't use
            // OperationQuery because we want to check parameter types
            // separately in postProcedureDec)
            myCorrespondingOperation = myBuilder.getInnermostActiveScope()
                    .queryForOne(new NameAndEntryTypeQuery<>(null, dec.getName(), OperationEntry.class,
                            ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_IGNORE, false))
                    .toOperationEntry(dec.getLocation());

            myBuilder.startScope(dec);

            myCurrentParameters = new LinkedList<>();

            // This will be set to a location if we are recursively calling ourselves.
            // Once we finish walking all the children, we will make sure this dec
            // is declared as recursive.
            myRecursiveCallLocation = null;
        } catch (NoSuchSymbolException nsse) {
            throw new SourceErrorException(
                    "Procedure " + dec.getName().getName() + " does not implement any known operation.",
                    dec.getName().getLocation());
        } catch (DuplicateSymbolException dse) {
            // We should have caught this before now, like when we defined the
            // duplicate Operation
            throw new RuntimeException("Duplicate Operations for " + dec.getName().getName() + "?");
        }
    }

    /**
     * <p>
     * Code that gets executed in between visiting items inside {@link ProcedureDec}.
     * </p>
     *
     * @param node
     *            An operation declaration.
     * @param prevChild
     *            The previous child item visited.
     * @param nextChild
     *            The next child item to be visited.
     */
    @Override
    public final void midProcedureDec(ProcedureDec node, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (prevChild != null && prevChild == node.getReturnTy()) {
            try {
                myBuilder.getInnermostActiveScope().addProgramVariable(node.getName().getName(), node,
                        node.getReturnTy().getProgramType());
            } catch (DuplicateSymbolException dse) {
                duplicateSymbol(node.getName().getName(), node.getName().getLocation());
            }
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
        myBuilder.endScope();

        // We're about to throw away all information about procedure parameters,
        // since they're redundant anyway. So we sanity-check them first.
        Ty returnTy = dec.getReturnTy();
        PTType returnType;
        if (returnTy == null) {
            returnType = PTVoid.getInstance(myTypeGraph);
        } else {
            returnType = returnTy.getProgramType();
        }

        // Various different sanity checks
        ValidOperationDeclChecker validOperationDeclChecker = new ValidOperationDeclChecker(dec.getLocation(),
                myCorrespondingOperation, myCurrentParameters);
        validOperationDeclChecker.isSameReturnType(returnType);
        validOperationDeclChecker.isSameNumberOfParameters();
        validOperationDeclChecker.hasValidParameterModesImpl();
        validOperationDeclChecker.isValidRecursiveProcedure(dec.getRecursive(), myRecursiveCallLocation);

        try {
            myBuilder.getInnermostActiveScope().addProcedure(dec.getName().getName(), dec, myCorrespondingOperation);
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(dec.getName().getName(), dec.getName().getLocation());
        }

        myCurrentParameters = null;
        myRecursiveCallLocation = null;
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link PerformanceOperationDec}.
     * </p>
     *
     * @param dec
     *            A performance operation declaration.
     */
    @Override
    public final void prePerformanceOperationDec(PerformanceOperationDec dec) {
        try {
            // Figure out what Operation we correspond to (we don't use
            // OperationQuery because we want to check parameter types
            // separately in postProcedureDec)
            myCorrespondingOperation = myBuilder.getInnermostActiveScope()
                    .queryForOne(new NameAndEntryTypeQuery<>(null, dec.getName(), OperationEntry.class,
                            ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_IGNORE, false))
                    .toOperationEntry(dec.getLocation());

            myBuilder.startScope(dec);

            myCurrentParameters = new LinkedList<>();
        } catch (NoSuchSymbolException nsse) {
            throw new SourceErrorException(
                    "Operation Profile " + dec.getName().getName() + " does not profile any known operation.",
                    dec.getName().getLocation());
        } catch (DuplicateSymbolException dse) {
            // We should have caught this before now, like when we defined the
            // duplicate Operation
            throw new RuntimeException("Duplicate Operation Profiles for " + dec.getName().getName() + "?");
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link PerformanceOperationDec}.
     * </p>
     *
     * @param dec
     *            A performance operation declaration.
     */
    @Override
    public final void postPerformanceOperationDec(PerformanceOperationDec dec) {
        myBuilder.endScope();

        try {
            myBuilder.getInnermostActiveScope().addOperationProfile(dec.getName().getName(), dec,
                    myCorrespondingOperation);
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(dec.getName().getName(), dec.getName().getLocation());
        }

        myCurrentParameters = null;
    }

    // -----------------------------------------------------------
    // Statement-Related
    // -----------------------------------------------------------

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
        // Sanity check to make sure left and right program types are the same.
        SameProgTypeChecker checker = new SameProgTypeChecker(stmt.getVariableExp(), stmt.getAssignExp());
        checker.hasSameProgrammingType();
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
        // Sanity check to make sure left and right program types are the same.
        SameProgTypeChecker checker = new SameProgTypeChecker(stmt.getLeft(), stmt.getRight());
        checker.hasSameProgrammingType();
    }

    // -----------------------------------------------------------
    // Variable Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting a {@link MathVarDec}.
     * </p>
     *
     * @param dec
     *            A mathematical variable declaration.
     */
    @Override
    public final void postMathVarDec(MathVarDec dec) {
        MTType mathTypeValue = dec.getTy().getMathTypeValue();
        String varName = dec.getName().getName();

        if (myCurrentDirectDefinition != null && mathTypeValue.isKnownToContainOnlyMTypes()
                && myDefinitionNamedTypes.contains(varName)) {

            throw new SourceErrorException(
                    "Introduction of type " + "parameter must precede any use of that variable " + "name.",
                    dec.getLocation());
        }

        if ((myDefinitionParameterSectionFlag || (myActiveQuantifications.size() > 0
                && myActiveQuantifications.peek() != SymbolTableEntry.Quantification.NONE))
                && mathTypeValue.isKnownToContainOnlyMTypes()) {
            myDefinitionSchematicTypes.put(varName, mathTypeValue);
        }

        dec.setMathType(mathTypeValue);

        SymbolTableEntry.Quantification q;
        if (myDefinitionParameterSectionFlag && myTypeValueDepth == 0) {
            q = SymbolTableEntry.Quantification.UNIVERSAL;
        } else {
            q = myActiveQuantifications.peek();
        }

        addBinding(varName, dec.getName().getLocation(), q, dec, mathTypeValue, new HashMap<String, MTType>());

        emitDebug(dec.getLocation(), "\t\tNew variable: " + varName + " of type " + mathTypeValue.toString()
                + " with quantification " + q + ".");
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ParameterVarDec}.
     * </p>
     *
     * @param dec
     *            A parameter declaration.
     */
    @Override
    public final void postParameterVarDec(ParameterVarDec dec) {
        try {
            ProgramParameterEntry paramEntry = myBuilder.getInnermostActiveScope()
                    .addFormalParameter(dec.getName().getName(), dec, dec.getMode(), dec.getTy().getProgramType());
            myCurrentParameters.add(paramEntry);
        } catch (DuplicateSymbolException e) {
            duplicateSymbol(dec.getName().getName(), dec.getName().getLocation());
        }

        dec.setMathType(dec.getTy().getMathTypeValue());
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link VarDec}.
     * </p>
     *
     * @param dec
     *            A variable declaration.
     */
    @Override
    public final void postVarDec(VarDec dec) {
        MTType mathTypeValue = dec.getTy().getMathTypeValue();
        String varName = dec.getName().getName();

        dec.setMathType(mathTypeValue);
        try {
            myBuilder.getInnermostActiveScope().addProgramVariable(varName, dec, dec.getTy().getProgramType());
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(varName, dec.getLocation());
        }

        emitDebug(dec.getLocation(), "\t\tNew program variable: " + varName + " of type " + mathTypeValue.toString()
                + " with quantification NONE");
    }

    // -----------------------------------------------------------
    // Type Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link TypeFamilyDec}.
     * </p>
     *
     * @param dec
     *            A type family declared in a {@code Concept}.
     */
    @Override
    public final void preTypeFamilyDec(TypeFamilyDec dec) {
        myBuilder.startScope(dec);
    }

    /**
     * <p>
     * Code that gets executed in between visiting items inside {@link TypeFamilyDec}.
     * </p>
     *
     * @param dec
     *            A type family declared in a {@code Concept}.
     * @param prevChild
     *            The previous child item visited.
     * @param nextChild
     *            The next child item to be visited.
     */
    @Override
    public final void midTypeFamilyDec(TypeFamilyDec dec, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (prevChild == dec.getModel()) {
            // We've parsed the model, but nothing else, so we can add our
            // exemplar to scope
            PosSymbol exemplar = dec.getExemplar();
            MathSymbolEntry exemplarEntry;

            try {
                exemplarEntry = myBuilder.getInnermostActiveScope().addBinding(exemplar.getName(), dec,
                        dec.getModel().getMathTypeValue());
            } catch (DuplicateSymbolException dse) {
                // This shouldn't be possible--the type declaration has a
                // scope all its own and we're the first ones to get to
                // introduce anything
                throw new RuntimeException(dse);
            }

            // YS: The logical thing to do is to add the new program type
            // after we finish walking the TypeFamilyDec. However, we could have
            // definition variables that needs that in the symbol table. So
            // we add it here to the current module scope.
            try {
                myCurModuleScope.addProgramTypeDefinition(dec.getName().getName(), dec,
                        dec.getModel().getMathTypeValue(), exemplarEntry);
            } catch (DuplicateSymbolException dse) {
                duplicateSymbol(dec.getName().getName(), dec.getName().getLocation());
            }
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link TypeFamilyDec}.
     * </p>
     *
     * @param dec
     *            A type family declared in a {@code Concept}.
     */
    @Override
    public final void postTypeFamilyDec(TypeFamilyDec dec) {
        myBuilder.endScope();
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link PerformanceTypeFamilyDec}.
     * </p>
     *
     * @param dec
     *            A type family declared in a {@code Concept} performance profile.
     */
    @Override
    public final void prePerformanceTypeFamilyDec(PerformanceTypeFamilyDec dec) {
        myBuilder.startScope(dec);

        try {
            ProgramTypeEntry correspondingTypeDeclaration = myBuilder.getInnermostActiveScope()
                    .queryForOne(new NameAndEntryTypeQuery<>(null, dec.getName(), ProgramTypeEntry.class,
                            ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_IGNORE, false))
                    .toProgramTypeEntry(null);

            TypeFamilyDec typeFamilyDec = (TypeFamilyDec) correspondingTypeDeclaration.getDefiningElement();

            // Sanity check the raw types to make sure they are the same
            if (!typeFamilyDec.getModel().equals(dec.getModel())) {
                throw new SourceErrorException(
                        "Type declared in the profile does not match the type declared in the concept.",
                        dec.getModel().getLocation());
            }

            PosSymbol exemplar = typeFamilyDec.getExemplar();
            if (exemplar != null) {
                try {
                    myBuilder.getInnermostActiveScope().addBinding(exemplar.getName(), typeFamilyDec,
                            typeFamilyDec.getModel().getMathTypeValue());
                } catch (DuplicateSymbolException dse) {
                    // This shouldn't be possible--the type declaration has a
                    // scope all its own and we're the first ones to get to
                    // introduce anything
                    throw new RuntimeException(dse);
                }
            }
        } catch (DuplicateSymbolException dse) {
            throw new SourceErrorException("Multiple types named " + dec.getName() + ".", dec.getName().getLocation());
        } catch (NoSuchSymbolException nsse) {
            throw new SourceErrorException("No corresponding type definition for \"" + dec.getName() + "\".",
                    dec.getName().getLocation());
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link PerformanceTypeFamilyDec}.
     * </p>
     *
     * @param dec
     *            A type family declared in a {@code Concept} performance profile.
     */
    @Override
    public final void postPerformanceTypeFamilyDec(PerformanceTypeFamilyDec dec) {
        myBuilder.endScope();
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link TypeRepresentationDec}.
     * </p>
     *
     * @param r
     *            A type representation declared in a {@code Concept Realization}.
     */
    @Override
    public final void preTypeRepresentationDec(TypeRepresentationDec r) {
        myBuilder.startScope(r);

        PosSymbol type = r.getName();

        List<SymbolTableEntry> es = myBuilder.getInnermostActiveScope()
                .query(new NameQuery(null, type, ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_IGNORE, false));

        if (es.isEmpty()) {
            noSuchSymbol(null, type);
        } else if (es.size() > 1) {
            ambiguousSymbol(type, es);
        } else {
            myTypeFamilyEntry = es.get(0).toTypeFamilyEntry(r.getLocation());

            // Sanity check: Make sure that when we have an independent or dependent
            // correspondence, the concept is declared to be a shared concept.
            AssertionClause correspondence = r.getCorrespondence();
            AssertionClause.ClauseType clauseType = r.getCorrespondence().getClauseType();
            if (clauseType.equals(AssertionClause.ClauseType.INDEPENDENT_CORRESPONDENCE)
                    || clauseType.equals(AssertionClause.ClauseType.DEPENDENT_CORRESPONDENCE)) {
                ModuleIdentifier id = myTypeFamilyEntry.getSourceModuleIdentifier();
                ConceptModuleDec concept = (ConceptModuleDec) myBuilder.getModuleScope(id).getDefiningElement();
                if (!concept.isSharingConcept()) {
                    String message;
                    if (clauseType.equals(AssertionClause.ClauseType.INDEPENDENT_CORRESPONDENCE)) {
                        message = "n " + correspondence.getClauseType().toString();
                    } else {
                        message = " " + correspondence.getClauseType().toString();
                    }

                    throw new SourceErrorException(
                            "Concept: " + concept.getName().getName()
                                    + " must be declared to be a sharing concept in order to use a" + message,
                            correspondence.getLocation());
                }
            }
        }
    }

    /**
     * <p>
     * Code that gets executed in between visiting items inside {@link TypeRepresentationDec}.
     * </p>
     *
     * @param r
     *            A type representation declared in a {@code Concept Realization}.
     * @param prevChild
     *            The previous child item visited.
     * @param nextChild
     *            The next child item to be visited.
     */
    @Override
    public final void midTypeRepresentationDec(TypeRepresentationDec r, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (prevChild instanceof Ty) {
            // We've finished the representation and are about to parse
            // conventions, etc. We introduce the exemplar gets added as
            // a program variable with the appropriate type.
            myPTRepresentationType = new PTRepresentation(myTypeGraph,
                    (PTInstantiated) ((Ty) prevChild).getProgramType(), myTypeFamilyEntry);
            try {
                myBuilder.getInnermostActiveScope().addProgramVariable(
                        myTypeFamilyEntry.getProgramType().getExemplarName(), r, myPTRepresentationType);
            } catch (DuplicateSymbolException dse) {
                // This shouldn't be possible--the type declaration has a
                // scope all its own and we're the first ones to get to
                // introduce anything
                throw new RuntimeException(dse);
            }
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link TypeRepresentationDec}.
     * </p>
     *
     * @param r
     *            A type representation declared in a {@code Concept Realization}.
     */
    @Override
    public final void postTypeRepresentationDec(TypeRepresentationDec r) {
        myBuilder.endScope();

        try {
            myBuilder.getInnermostActiveScope().addRepresentationTypeEntry(r.getName().getName(), r, myTypeFamilyEntry,
                    myPTRepresentationType, r.getConvention(), r.getCorrespondence());
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(r.getName());
        }

        myPTRepresentationType = null;
        myTypeFamilyEntry = null;
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link FacilityTypeRepresentationDec}.
     * </p>
     *
     * @param e
     *            A type representation declared in a facility.
     */
    @Override
    public final void preFacilityTypeRepresentationDec(FacilityTypeRepresentationDec e) {
        myBuilder.startScope(e);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link FacilityTypeRepresentationDec}.
     * </p>
     *
     * @param e
     *            A type representation declared in a facility.
     */
    @Override
    public final void postFacilityTypeRepresentationDec(FacilityTypeRepresentationDec e) {
        myBuilder.endScope();

        try {
            // Since FacilityTypeRepresentation can only exist inside Facilities,
            // so any type we use to implement it must be an instantiated type.
            myBuilder.getInnermostActiveScope().addFacilityRepresentationEntry(e.getName().getName(), e,
                    (PTInstantiated) e.getRepresentation().getProgramType(), e.getConvention());
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(e.getName());
        }
    }

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
        // Create a new list for parameter entries.
        myCurrentParameters = new LinkedList<>();
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
        myCurrentParameters = null;
    }

    // -----------------------------------------------------------
    // Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting an {@link Exp}.
     * </p>
     *
     * @param exp
     *            An expression.
     */
    @Override
    public final void preExp(Exp exp) {
        myExpressionDepth++;
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link Exp}.
     * </p>
     *
     * @param exp
     *            An expression.
     */
    @Override
    public final void postExp(Exp exp) {
        if (exp.getMathType() == null) {
            throw new NullMathTypeException("Exp " + exp + " (" + exp.getClass() + ", " + exp.getLocation()
                    + ") got through the populator " + "with no math type.");
        }

        if (exp instanceof ProgramExp && ((ProgramExp) exp).getProgramType() == null) {
            throw new NullProgramTypeException("Exp " + exp + " (" + exp.getClass() + ", " + exp.getLocation()
                    + ") got through the populator " + "with no program type.");
        }

        myExpressionDepth--;
    }

    // -----------------------------------------------------------
    // Math Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting an {@link AbstractFunctionExp}.
     * </p>
     *
     * @param exp
     *            An abstract function expression.
     */
    @Override
    public final void postAbstractFunctionExp(AbstractFunctionExp exp) {
        MTFunction foundExpType;
        foundExpType = exp.getConservativePreApplicationType(myTypeGraph);

        emitDebug(exp.getLocation(),
                "\tExpression: " + exp.toString() + "[" + exp.getLocation() + "] of type " + foundExpType.toString());

        MathSymbolEntry intendedEntry = getIntendedFunction(exp);

        MTFunction expectedType = (MTFunction) intendedEntry.getType();

        // We know we match expectedType--otherwise the above would have thrown
        // an exception.

        exp.setMathType(expectedType.getRange());
        exp.setQuantification(intendedEntry.getQuantification());

        if (myTypeValueDepth > 0) {
            // I had better identify a type
            MTFunction entryType = (MTFunction) intendedEntry.getType();

            List<MTType> arguments = new LinkedList<>();
            MTType argTypeValue;
            for (Exp arg : exp.getParameters()) {
                argTypeValue = arg.getMathTypeValue();

                if (argTypeValue == null) {
                    notAType(arg);
                }

                arguments.add(argTypeValue);
            }

            exp.setMathTypeValue(entryType.getApplicationType(intendedEntry.getName(), arguments));
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link AlternativeExp}.
     * </p>
     *
     * @param exp
     *            An alternative expression.
     */
    @Override
    public final void postAlternativeExp(AlternativeExp exp) {
        MTType establishedType = null;
        MTType establishedTypeValue = null;
        for (AltItemExp alt : exp.getAlternatives()) {
            if (establishedType == null) {
                establishedType = alt.getAssignment().getMathType();
                establishedTypeValue = alt.getAssignment().getMathTypeValue();
            } else {
                expectType(alt, establishedType);
            }
        }

        exp.setMathType(establishedType);
        exp.setMathTypeValue(establishedTypeValue);
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link AltItemExp}.
     * </p>
     *
     * @param exp
     *            An alternative item expression.
     */
    @Override
    public final void postAltItemExp(AltItemExp exp) {
        if (exp.getTest() != null) {
            expectType(exp.getTest(), myTypeGraph.BOOLEAN);
        }

        exp.setMathType(exp.getAssignment().getMathType());
        exp.setMathTypeValue(exp.getAssignment().getMathTypeValue());
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link BetweenExp}.
     * </p>
     *
     * @param exp
     *            A conjunct expression.
     */
    @Override
    public final void postBetweenExp(BetweenExp exp) {
        exp.setMathType(myTypeGraph.BOOLEAN);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link CrossTypeExp}.
     * </p>
     *
     * @param exp
     *            A cartesian product expression.
     */
    @Override
    public final void preCrossTypeExp(CrossTypeExp exp) {
        myTypeValueDepth++;
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link CrossTypeExp}.
     * </p>
     *
     * @param exp
     *            A cartesian product expression.
     */
    @Override
    public final void postCrossTypeExp(CrossTypeExp exp) {
        List<MTCartesian.Element> fieldTypes = new LinkedList<>();

        Map<PosSymbol, ArbitraryExpTy> tagsToFieldsMap = exp.getTagsToFieldsMap();
        for (PosSymbol psTag : tagsToFieldsMap.keySet()) {
            fieldTypes.add(new MTCartesian.Element(psTag.getName(), tagsToFieldsMap.get(psTag).getMathTypeValue()));
        }

        exp.setMathType(myTypeGraph.CLS);
        exp.setMathTypeValue(new MTCartesian(myTypeGraph, fieldTypes));

        myTypeValueDepth--;
    }

    /**
     * <p>
     * This method redefines how a {@link CrossTypeExp} should be walked.
     * </p>
     *
     * @param exp
     *            A cartesian product expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkCrossTypeExp(CrossTypeExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preCrossTypeExp(exp);

        // YS - Originally we keep both a mapping from the name to the ArbitraryTy AND
        // a list containing just the ArbitraryTys just so that the TreeWalker
        // would detect it as a list and walk it. We could in theory add maps to the
        // TreeWalker logic, but this is easier...
        Map<PosSymbol, ArbitraryExpTy> tagsToFieldsMap = exp.getTagsToFieldsMap();
        for (PosSymbol psTag : tagsToFieldsMap.keySet()) {
            TreeWalker.visit(this, tagsToFieldsMap.get(psTag));
        }

        postCrossTypeExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link DotExp}.
     * </p>
     *
     * @param exp
     *            A dotted expression.
     */
    @Override
    public final void postDotExp(DotExp exp) {
        // Might already have been set in preDotExp(), in which case our children
        // weren't visited
        if (exp.getMathType() == null) {
            List<Exp> segments = exp.getSegments();

            Exp lastSeg = segments.get(segments.size() - 1);

            exp.setMathType(lastSeg.getMathType());
            exp.setMathTypeValue(lastSeg.getMathTypeValue());
        }
    }

    /**
     * <p>
     * This method redefines how a {@link DotExp} should be walked.
     * </p>
     *
     * @param exp
     *            A dotted expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkDotExp(DotExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preDotExp(exp);

        Indirect<Exp> lastGoodOut = new Indirect<>();
        Iterator<Exp> segments = exp.getSegments().iterator();
        MathSymbolEntry entry = getTopLevelValue(segments, lastGoodOut);

        Location lastGood = lastGoodOut.data.getLocation();

        MTType curType = entry.getType();
        MTCartesian curTypeCartesian;
        Exp nextSegment = lastGoodOut.data, lastSegment;
        while (segments.hasNext()) {
            lastSegment = nextSegment;
            nextSegment = segments.next();
            String segmentName = getName(nextSegment);

            try {
                curTypeCartesian = (MTCartesian) curType;
                curType = curTypeCartesian.getFactor(segmentName);
            } catch (ClassCastException cce) {
                curType = HardCoded.getMetaFieldType(myTypeGraph, lastSegment, segmentName);

                if (curType == null) {
                    throw new SourceErrorException("Value not a tuple.", lastGood);
                }
                // YS: Make sure we don't have more segments to process after a
                // MetaFieldType. In other words, this is illegal: Entry.Is_Initial(x).Foo
                else if (segments.hasNext()) {
                    throw new SourceErrorException("Illegal dotted expression following: " + segmentName,
                            nextSegment.getLocation());
                }
            } catch (NoSuchElementException nsee) {
                curType = HardCoded.getMetaFieldType(myTypeGraph, lastSegment, segmentName);

                if (curType == null) {
                    throw new SourceErrorException("No such factor.", lastGood);
                }
                // YS: Make sure we don't have more segments to process after a
                // MetaFieldType. In other words, this is illegal: Entry.Is_Initial(x).Foo
                else if (segments.hasNext()) {
                    throw new SourceErrorException("Illegal dotted expression following: " + segmentName,
                            nextSegment.getLocation());
                }
            }

            // getName() would have thrown an exception if nextSegment wasn't
            // a VarExp or a FunctionExp. In the former case, we're good to
            // go--but in the latter case, we still need to typecheck
            // parameters, assure they match the signature, and adjust
            // curType to reflect the RANGE of the function type rather than
            // the entire type
            if (nextSegment instanceof FunctionExp) {
                FunctionExp nextSegmentAsFunctionExp = (FunctionExp) nextSegment;

                // YS: If we happen to encounter a "Val_in" function, we will need to
                // typecheck it differently than an "Is_Initial" function.
                if (nextSegmentAsFunctionExp.getOperatorAsString().equals("Val_in")) {
                    curType = applyValInFunction(lastSegment, nextSegmentAsFunctionExp, curType);
                } else {
                    curType = applyFunction(nextSegmentAsFunctionExp, curType);
                }
            }

            nextSegment.setMathType(curType);
            lastGood = nextSegment.getLocation();
        }

        postDotExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * This method redefines how a {@link FunctionExp} should be walked.
     * </p>
     *
     * @param exp
     *            A function expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkFunctionExp(FunctionExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preAbstractFunctionExp(exp);
        preFunctionExp(exp);
        emitDebug(exp.getLocation(), "\tEntering walkFunctionExp...");

        List<Exp> arguments = exp.getArguments();
        for (Exp e : arguments) {
            TreeWalker.visit(this, e);
        }

        if (exp.getCaratExp() != null) {
            TreeWalker.visit(this, exp.getCaratExp());
        }

        VarExp name = exp.getName();
        name.setMathType(new MTNamed(myTypeGraph, name.getName().getName()));

        // Add it to our list of operators if we are in a theorem
        if (myMathAssertionOps != null) {
            myMathAssertionOps.add(name.clone());
        }

        emitDebug(exp.getLocation(), "\tExiting walkFunctionExp.");
        postFunctionExp(exp);
        postAbstractFunctionExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link IfExp}.
     * </p>
     *
     * @param exp
     *            An if expression.
     */
    @Override
    public final void postIfExp(IfExp exp) {
        // An "if expression" is a functional condition, as in the following
        // example:
        // x = (if (y > 0) then y else -y)
        // Its condition had better be a boolean. Its type resolves to the
        // shared type of its branches.
        expectType(exp.getTest(), myTypeGraph.BOOLEAN);

        Exp ifClause = exp.getThen();
        Exp elseClause = exp.getElse();

        MTType ifType = ifClause.getMathType();
        MTType elseType = elseClause.getMathType();

        boolean ifIsSuperType = myTypeGraph.isSubtype(elseType, ifType);

        // One of these had better be a (non-strict) subtype of the other
        if (!ifIsSuperType && !myTypeGraph.isSubtype(ifType, elseType)) {
            throw new SourceErrorException(
                    "Branches must share a type.\n" + "If branch:   " + ifType + "\n" + "Else branch: " + elseType,
                    exp.getLocation());
        }

        MTType finalType, finalTypeValue;
        if (ifIsSuperType) {
            finalType = ifType;
            finalTypeValue = ifClause.getMathTypeValue();
        } else {
            finalType = elseType;
            finalTypeValue = elseClause.getMathTypeValue();
        }

        exp.setMathType(finalType);
        exp.setMathTypeValue(finalTypeValue);
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link InfixExp}.
     * </p>
     *
     * @param exp
     *            An infix expression.
     */
    @Override
    public final void postInfixExp(InfixExp exp) {
        // Add it to our list of operators if we are in a theorem
        if (myMathAssertionOps != null) {
            PosSymbol qual = exp.getQualifier() != null ? exp.getQualifier().clone() : null;
            myMathAssertionOps.add(new VarExp(exp.getOperatorAsPosSymbol().getLocation().clone(), qual,
                    exp.getOperatorAsPosSymbol().clone()));
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link IntegerExp}.
     * </p>
     *
     * @param exp
     *            An integer expression.
     */
    @Override
    public final void postIntegerExp(IntegerExp exp) {
        postSymbolExp(exp.getQualifier(), "" + exp.getValue(), exp);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link LambdaExp}.
     * </p>
     *
     * @param exp
     *            A lambda expression.
     */
    @Override
    public final void preLambdaExp(LambdaExp exp) {
        myBuilder.startScope(exp);
        emitDebug(exp.getLocation(), "\tLambda Expression: " + exp);
    }

    /**
     * <p>
     * Code that gets executed before visiting the list of parameter expressions inside a {@link LambdaExp}.
     * </p>
     *
     * @param exp
     *            A lambda expression.
     */
    @Override
    public final void preLambdaExpMyParameters(LambdaExp exp) {
        myDefinitionParameterSectionFlag = true;
    }

    /**
     * <p>
     * Code that gets executed after visiting the list of parameter expressions inside a {@link LambdaExp}.
     * </p>
     *
     * @param exp
     *            A lambda expression.
     */
    @Override
    public final void postLambdaExpMyParameters(LambdaExp exp) {
        myDefinitionParameterSectionFlag = false;
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link LambdaExp}.
     * </p>
     *
     * @param exp
     *            A lambda expression.
     */
    @Override
    public final void postLambdaExp(LambdaExp exp) {
        myBuilder.endScope();

        List<MTType> parameterTypes = new LinkedList<>();
        for (MathVarDec p : exp.getParameters()) {
            parameterTypes.add(p.getTy().getMathTypeValue());
        }

        exp.setMathType(new MTFunction(myTypeGraph, exp.getBody().getMathType(), parameterTypes));
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link OldExp}.
     * </p>
     *
     * @param exp
     *            An {@code old} expression.
     */
    @Override
    public final void postOldExp(OldExp exp) {
        exp.setMathType(exp.getExp().getMathType());
        exp.setMathTypeValue(exp.getExp().getMathTypeValue());
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link OutfixExp}.
     * </p>
     *
     * @param exp
     *            An outfix expression.
     */
    @Override
    public final void postOutfixExp(OutfixExp exp) {
        // Add it to our list of operators if we are in a theorem
        if (myMathAssertionOps != null) {
            PosSymbol qual = exp.getQualifier() != null ? exp.getQualifier().clone() : null;
            myMathAssertionOps.add(new VarExp(exp.getOperatorAsPosSymbol().getLocation().clone(), qual,
                    exp.getOperatorAsPosSymbol().clone()));
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link PrefixExp}.
     * </p>
     *
     * @param exp
     *            A prefix expression.
     */
    @Override
    public final void postPrefixExp(PrefixExp exp) {
        // Add it to our list of operators if we are in a theorem
        if (myMathAssertionOps != null) {
            PosSymbol qual = exp.getQualifier() != null ? exp.getQualifier().clone() : null;
            myMathAssertionOps.add(new VarExp(exp.getOperatorAsPosSymbol().getLocation().clone(), qual,
                    exp.getOperatorAsPosSymbol().clone()));
        }
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link QuantExp}.
     * </p>
     *
     * @param exp
     *            A quantified expression.
     */
    @Override
    public final void preQuantExp(QuantExp exp) {
        emitDebug(exp.getLocation(), "\tEntering preQuantExp...");
        myBuilder.startScope(exp);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link QuantExp}.
     * </p>
     *
     * @param exp
     *            A quantified expression.
     */
    @Override
    public final void postQuantExp(QuantExp exp) {
        myBuilder.endScope();

        expectType(exp.getBody(), myTypeGraph.BOOLEAN);
        exp.setMathType(myTypeGraph.BOOLEAN);
    }

    /**
     * <p>
     * This method redefines how a {@link QuantExp} should be walked.
     * </p>
     *
     * @param exp
     *            A quantified expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkQuantExp(QuantExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preQuantExp(exp);
        emitDebug(exp.getLocation(), "\tEntering walkQuantExp...");

        List<MathVarDec> vars = exp.getVars();
        SymbolTableEntry.Quantification quantification = exp.getQuantification();
        myActiveQuantifications.push(quantification);
        for (MathVarDec v : vars) {
            TreeWalker.visit(this, v);
        }
        myActiveQuantifications.pop();

        myActiveQuantifications.push(SymbolTableEntry.Quantification.NONE);
        TreeWalker.visit(this, exp.getBody());
        myActiveQuantifications.pop();

        emitDebug(exp.getLocation(), "\tExiting walkQuantExp.");
        postQuantExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link RecpExp}.
     * </p>
     *
     * @param exp
     *            A recp expression.
     */
    @Override
    public final void postRecpExp(RecpExp exp) {
        exp.setMathType(myTypeGraph.RECEPTACLES);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link SetExp}.
     * </p>
     *
     * @param exp
     *            A set expression.
     */
    @Override
    public final void postSetExp(SetExp exp) {
        MathVarDec varDec = exp.getVar();
        MTType varType = varDec.getMathType();

        Exp body = exp.getBody();

        expectType(body, myTypeGraph.BOOLEAN);

        if (exp.getWhere() != null) {
            body = MathExp.formConjunct(exp.getLocation(), exp.getWhere(), body);
        }

        exp.setMathType(new MTSetRestriction(myTypeGraph, varType, varDec.getName().getName(), body));
        exp.setMathTypeValue(new MTPowersetApplication(myTypeGraph, varType));
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link SetCollectionExp}.
     * </p>
     *
     * @param exp
     *            A set collection expression.
     */
    @Override
    public final void postSetCollectionExp(SetCollectionExp exp) {
        MTType setType = myTypeGraph.SSET;
        MTType setTypeValue;

        // Add it to our list of operators if we are in a theorem
        if (myMathAssertionOps != null) {
            myMathAssertionOps
                    .add(new VarExp(exp.getLocation().clone(), null, new PosSymbol(exp.getLocation().clone(), "{_}")));
        }

        // Check to see if have any elements in the set. If we don't, it is
        // simply the empty set!
        if (exp.getVars().isEmpty()) {
            setTypeValue = myTypeGraph.EMPTY_SET;
        } else {
            // Else make sure that everything in the set collection is known to be in
            // some super type. If they are, we use it to build a Powerset application
            // of the elements in the set.
            MathExp expContainingSuperType = null;
            MTType superTypeOfAllElementsInSet = null;
            Set<MathExp> vars = exp.getVars();
            for (MathExp v : vars) {
                MTType vType = v.getMathType();
                if (superTypeOfAllElementsInSet == null || superTypeOfAllElementsInSet.isSubtypeOf(vType)) {
                    expContainingSuperType = v;
                    superTypeOfAllElementsInSet = vType;
                }
            }

            for (MathExp v : vars) {
                if (!v.equals(expContainingSuperType)) {
                    expectType(v, superTypeOfAllElementsInSet);
                }
            }

            setTypeValue = new MTPowersetApplication(myTypeGraph, superTypeOfAllElementsInSet);
        }

        // This must be our type and type value
        exp.setMathType(setType);
        exp.setMathTypeValue(setTypeValue);
    }

    /**
     * <p>
     * This method redefines how a {@link SetCollectionExp} should be walked.
     * </p>
     *
     * @param exp
     *            A set collection expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkSetCollectionExp(SetCollectionExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preSetCollectionExp(exp);
        emitDebug(exp.getLocation(), "\tEntering walkSetCollectionExp...");

        // Walk each of the expressions inside SetCollectionExp
        Set<MathExp> vars = exp.getVars();
        for (MathExp v : vars) {
            TreeWalker.visit(this, v);
        }

        emitDebug(exp.getLocation(), "\tExiting walkSetCollectionExp.");
        postSetCollectionExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link TupleExp}.
     * </p>
     *
     * @param exp
     *            A tuple expression.
     */
    @Override
    public final void postTupleExp(TupleExp exp) {
        // Add it to our list of operators if we are in a theorem
        if (myMathAssertionOps != null) {
            myMathAssertionOps
                    .add(new VarExp(exp.getLocation().clone(), null, new PosSymbol(exp.getLocation().clone(), "(_)")));
        }

        // See the note in TupleExp on why TupleExp isn't an AbstractFunctionExp
        List<Exp> fields = exp.getFields();
        List<MTCartesian.Element> fieldTypes = new LinkedList<>();
        for (Exp field : fields) {
            fieldTypes.add(new MTCartesian.Element(field.getMathType()));
        }

        exp.setMathType(new MTCartesian(myTypeGraph, fieldTypes));
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link TypeAssertionExp}.
     * </p>
     *
     * @param exp
     *            A type assertion expression.
     */
    @Override
    public final void postTypeAssertionExp(TypeAssertionExp exp) {
        if (myTypeValueDepth == 0 && (myExpressionDepth > 2 || !myInTypeTheoremBindingExpFlag)) {
            throw new SourceErrorException("This construct only permitted in "
                    + "type declarations or in expressions matching: \n\n" + "   Type Theorem <name>: <quantifiers>, \n"
                    + "       [<condition> implies] <expression> : " + "<assertedType>", exp.getLocation());
        } else if (myActiveQuantifications.size() > 0
                && myActiveQuantifications.peek() != SymbolTableEntry.Quantification.NONE) {
            throw new SourceErrorException("Implicit types are not permitted inside "
                    + "quantified variable declarations. \n" + "Quantify the type explicitly instead.",
                    exp.getLocation());
        }
        // Note that postMathTypeTheoremDec() checks the "form" of a type theorem at
        // the top two levels. So all we're checking for here is that the type
        // assertion didn't happen deeper than that (where it shouldn't appear).

        // If we're the assertion of a type theorem, then postMathTypeTheoremDec()
        // will take care of any logic. If we're part of a type declaration,
        // on the other hand, we've got some bookkeeping to do...
        if (myTypeValueDepth > 0) {
            try {
                VarExp nodeExp = (VarExp) exp.getExp();
                String nodeExpName = nodeExp.getName().getName();
                try {
                    MTType expType = exp.getAssertedTy().getMathTypeValue();
                    myBuilder.getInnermostActiveScope().addBinding(nodeExpName,
                            SymbolTableEntry.Quantification.UNIVERSAL, exp, expType);

                    // YS: So we don't really care about what the raw type's
                    // math type is. What we really care about is what type
                    // it will produce, so we use its type value!
                    exp.setMathType(expType);
                    exp.setMathTypeValue(new MTNamed(myTypeGraph, nodeExpName));

                    // See walkTypeAssertionExp(): we are responsible for
                    // setting the VarExp's type.
                    // YS: The variable simply is an MTNamed that has the
                    // same name as its name.
                    nodeExp.setMathType(new MTNamed(myTypeGraph, nodeExpName));

                    if (myDefinitionNamedTypes.contains(nodeExpName)) {
                        // Regardless of where in the expression it appears, an
                        // implicit type parameter exists at the top level of a
                        // definition, and thus a definition that contains, e.g.,
                        // an implicit type parameter T cannot make reference
                        // to some existing type with that name (except via full
                        // qualification), thus the introduction of an implicit
                        // type parameter must precede any use of that
                        // parameter's name, even if the name exists in-scope
                        // before the parameter is declared
                        throw new SourceErrorException("Introduction of " + "implicit type parameter must precede any "
                                + "use of that variable name.", nodeExp.getLocation());
                    }

                    // Note that a redundantly named type parameter would be
                    // caught when we add a symbol to the symbol table, so no
                    // need to check here. Simply store the name and its type
                    // into our current definition's schematic type map.
                    myDefinitionSchematicTypes.put(nodeExpName, expType);

                    emitDebug(exp.getLocation(), "\tAdded schematic variable: " + nodeExpName + " with type: "
                            + exp.getMathType() + " and type value: " + exp.getMathTypeValue());
                } catch (DuplicateSymbolException dse) {
                    duplicateSymbol(nodeExpName, nodeExp.getLocation());
                }
            } catch (ClassCastException cce) {
                throw new SourceErrorException("Must be a variable name.", exp.getExp().getLocation());
            }
        } else {
            exp.setMathType(myTypeGraph.BOOLEAN);
        }
    }

    /**
     * <p>
     * This method redefines how a {@link TypeAssertionExp} should be walked.
     * </p>
     *
     * @param exp
     *            A type assertion expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkTypeAssertionExp(TypeAssertionExp exp) {
        preTypeAssertionExp(exp);

        // If we exist as an implicit type parameter, there's no way our
        // expression can know its own type (that's defined by the asserted Ty),
        // so we skip walking it and let postTypeAssertionExp() set its type for
        // it
        if (myTypeValueDepth == 0) {
            TreeWalker.visit(this, exp.getExp());
        }

        TreeWalker.visit(this, exp.getAssertedTy());
        postTypeAssertionExp(exp);

        return true;
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link TypeReceptaclesExp}.
     * </p>
     *
     * @param exp
     *            A type receptacles expression.
     */
    @Override
    public final void postTypeReceptaclesExp(TypeReceptaclesExp exp) {
        exp.setMathType(myTypeGraph.SSET);
        exp.setMathTypeValue(myTypeGraph.RECEPTACLES);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link VarExp}.
     * </p>
     *
     * @param exp
     *            A variable expression.
     */
    @Override
    public final void postVarExp(VarExp exp) {
        MathSymbolEntry intendedEntry = postSymbolExp(exp.getQualifier(), exp.getName().getName(), exp);

        if (myTypeValueDepth > 0 && exp.getQualifier() == null) {
            try {
                intendedEntry.getTypeValue();
                myDefinitionNamedTypes.add(intendedEntry.getName());
            } catch (SymbolNotOfKindTypeException snokte) {
                // No problem, just don't need to add it
            }
        }

        // Our quantifier might have changed, so set it using the
        // corresponding MathSymbolEntry
        exp.setQuantification(intendedEntry.getQuantification());

        // YS: Check to see if this refers to a definition name from a Precis.
        // This information will be used by the VC generator to generate
        // parsimonious VCs. Note that we also have to consider things from
        // that are built-in.
        ModuleIdentifier id = intendedEntry.getSourceModuleIdentifier();
        if (id.equals(ModuleIdentifier.GLOBAL) || myCompileEnvironment.getModuleAST(id) instanceof PrecisModuleDec) {
            exp.setIsPrecisDefinitionName();
        }
    }

    // -----------------------------------------------------------
    // Program Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting a {@link ProgramCharExp}.
     * </p>
     *
     * @param exp
     *            A programming character expression.
     */
    @Override
    public final void postProgramCharExp(ProgramCharExp exp) {
        // YS: We need to set the PTType to Character
        // and the MathType to N. Not sure if this the right fix,
        // but I simply got the the math type of the PTType (Character),
        // which has N as its math type.
        PTType ptType = getCharProgramType(exp.getLocation());
        exp.setProgramType(ptType);
        exp.setMathType(ptType.toMath());
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
        PosSymbol qualifier = exp.getQualifier();
        PosSymbol name = exp.getName();
        List<ProgramExp> args = exp.getArguments();

        List<PTType> argTypes = new LinkedList<>();
        for (ProgramExp arg : args) {
            argTypes.add(arg.getProgramType());
        }

        try {
            OperationEntry op = myBuilder.getInnermostActiveScope()
                    .queryForOne(new OperationQuery(qualifier, name, argTypes));

            // YS: Sanity check preserves/evaluates mode.
            ValidFunctionCallChecker checker = new ValidFunctionCallChecker(exp, op, myCurrentParameters);
            checker.areValidExpArgs();

            // YS: Set the types only if it passes the sanity checker.
            exp.setProgramType(op.getReturnType());
            exp.setMathType(op.getReturnType().toMath());

            // Check to see if we are recursively calling ourselves
            if (myCorrespondingOperation != null && myCorrespondingOperation.equals(op)
                    && myRecursiveCallLocation == null) {
                myRecursiveCallLocation = exp.getName().getLocation();
            }
        } catch (NoSuchSymbolException nsse) {
            throw new SourceErrorException(
                    "No operation found corresponding " + "to the call with the specified arguments: " + exp.getName(),
                    exp.getLocation());
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(exp.getName().getName(), exp.getLocation());
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ProgramIntegerExp}.
     * </p>
     *
     * @param exp
     *            A programming integer expression.
     */
    @Override
    public final void postProgramIntegerExp(ProgramIntegerExp exp) {
        // YS: We need to set the PTType to Integer
        // and the MathType to Z. Not sure if this the right fix,
        // but I simply got the the math type of the PTType (Integer),
        // which has Z as its math type.
        PTType ptType = getIntegerProgramType(exp.getLocation());
        exp.setProgramType(ptType);
        exp.setMathType(ptType.toMath());
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ProgramStringExp}.
     * </p>
     *
     * @param exp
     *            A programming string expression.
     */
    @Override
    public final void postProgramStringExp(ProgramStringExp exp) {
        // YS: Hampton wanted Str(N) to be the math type.
        // Not sure if this the right fix, but I simply got the
        // the math type of the PTType (Char_Str), which has
        // Str(N) as its math type.
        PTType ptType = getStringProgramType(exp.getLocation());
        exp.setProgramType(ptType);
        exp.setMathType(ptType.toMath());
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link ProgramVariableArrayExp}.
     * </p>
     *
     * @param exp
     *            A programming variable array expression.
     */
    @Override
    public final void preProgramVariableArrayExp(ProgramVariableArrayExp exp) {
        // This should have been converted when constructing our AST, so nothing should
        // be a ProgramVariableArrayExp. This check is to make sure we don't accidentally
        // construct one in the future.
        throw new SourceErrorException("This array expression should have been converted an operation call.",
                exp.getLocation(), new IllegalStateException());
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ProgramVariableNameExp}.
     * </p>
     *
     * @param exp
     *            A programming variable name expression.
     */
    @Override
    public final void postProgramVariableNameExp(ProgramVariableNameExp exp) {
        try {
            ProgramVariableEntry entry = myBuilder.getInnermostActiveScope()
                    .queryForOne(new ProgramVariableQuery(exp.getQualifier(), exp.getName()));

            // YS - For generic concept types, we need to query the actual type
            // and not just assign the PTElement that is associated with the actual parameter.
            if (entry.getDefiningElement() instanceof ConceptTypeParamDec) {
                ConceptTypeParamDec typeParamDec = (ConceptTypeParamDec) entry.getDefiningElement();

                try {
                    ProgramTypeEntry typeEntry = myBuilder.getInnermostActiveScope()
                            .queryForOne(GenericProgramTypeQuery.INSTANCE);

                    exp.setProgramType(typeEntry.getProgramType());
                } catch (NoSuchSymbolException nsse2) {
                    noSuchSymbol(null, typeParamDec.getName());
                } catch (DuplicateSymbolException dse2) {
                    duplicateSymbol(typeParamDec.getName());
                }
            }
            // YS - For all other types, simply assign the program type from the
            // associated program variable.
            else {
                exp.setProgramType(entry.getProgramType());
            }

            // Handle math typing stuff
            postSymbolExp(exp.getQualifier(), exp.getName().getName(), exp);
        } catch (NoSuchSymbolException nsse) {
            noSuchSymbol(exp.getQualifier(), exp.getName().getName(), exp.getLocation());
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(exp.getName());
        }
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
        // Dot expressions are handled ridiculously, even for this compiler, so
        // this method just deals with the cases we've encountered so far and
        // lots of assumptions are made. Expect it to break frequently when you
        // encounter some new case

        PosSymbol firstNamePos = ((ProgramVariableNameExp) exp.getSegments().get(0)).getName();
        String firstName = firstNamePos.getName();

        try {
            ProgramVariableEntry eEntry = myBuilder.getInnermostActiveScope()
                    .queryForOne(new NameQuery(null, firstName)).toProgramVariableEntry(firstNamePos.getLocation());
            exp.getSegments().get(0).setProgramType(eEntry.getProgramType());
            exp.getSegments().get(0).setMathType(eEntry.getProgramType().toMath());

            PTType eType = eEntry.getProgramType();

            if (eType instanceof PTRepresentation) {
                eType = ((PTRepresentation) eType).getBaseType();
            }

            if (eType instanceof PTFacilityRepresentation) {
                eType = ((PTFacilityRepresentation) eType).getBaseType();
            }

            PTRecord recordType = (PTRecord) eType;

            String fieldName = ((ProgramVariableNameExp) exp.getSegments().get(1)).getName().getName();

            PTType fieldType = recordType.getFieldType(fieldName);

            if (fieldType == null) {
                throw new NullProgramTypeException("Could not retrieve type of " + " field '" + fieldName
                        + "'. Either it doesn't exist " + "in the record or it's missing a type.");
            }

            exp.getSegments().get(1).setProgramType(fieldType);
            exp.setProgramType(fieldType);

            exp.getSegments().get(1).setMathType(fieldType.toMath());
            exp.setMathType(fieldType.toMath());
        } catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, firstNamePos);
        } catch (DuplicateSymbolException dse) {
            // This flavor of name query shouldn't be able to throw this--we're
            // only looking in the local module so there's no overloading
            throw new RuntimeException(dse);
        }
    }

    /**
     * <p>
     * This method redefines how a {@link ProgramVariableDotExp} should be walked.
     * </p>
     *
     * @param exp
     *            A programming variable dotted expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkProgramVariableDotExp(ProgramVariableDotExp exp) {
        preAny(exp);
        preExp(exp);
        preProgramExp(exp);
        preProgramVariableExp(exp);
        preProgramVariableDotExp(exp);

        postProgramVariableDotExp(exp);
        postProgramVariableExp(exp);
        postProgramExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    // -----------------------------------------------------------
    // Raw Type-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting an {@link ArbitraryExpTy}.
     * </p>
     *
     * @param ty
     *            A raw arbitrary type.
     */
    @Override
    public final void preArbitraryExpTy(ArbitraryExpTy ty) {
        enteringTypeValueNode();
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link ArbitraryExpTy}.
     * </p>
     *
     * @param ty
     *            A raw arbitrary type.
     */
    @Override
    public final void postArbitraryExpTy(ArbitraryExpTy ty) {
        leavingTypeValueNode();

        Exp typeExp = ty.getArbitraryExp();
        MTType mathType = typeExp.getMathType();
        MTType mathTypeValue = typeExp.getMathTypeValue();
        if (mathTypeValue == null) {
            notAType(typeExp);
        }

        ty.setMathType(mathType);
        ty.setMathTypeValue(mathTypeValue);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link NameTy}.
     * </p>
     *
     * @param ty
     *            A raw named type.
     */
    @Override
    public final void postNameTy(NameTy ty) {
        // Note that all mathematical types are ArbitraryExpTys, so this must
        // be in a program-type syntactic slot.
        PosSymbol tySymbol = ty.getName();
        PosSymbol tyQualifier = ty.getQualifier();
        Location tyLocation = tySymbol.getLocation();
        String tyName = tySymbol.getName();

        try {
            ProgramTypeEntry type = myBuilder
                    .getInnermostActiveScope().queryForOne(new NameQuery(tyQualifier, tySymbol,
                            ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_INSTANTIATE, true))
                    .toProgramTypeEntry(tyLocation);

            // Check to see if we have a facility qualifier
            if (tyQualifier != null) {
                FacilityEntry facilityEntry = myBuilder
                        .getInnermostActiveScope().queryForOne(new NameQuery(null, tyQualifier,
                                ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_INSTANTIATE, true))
                        .toFacilityEntry(tyLocation);
                ty.setProgramType(new PTNamed(myTypeGraph, facilityEntry, (PTFamily) type.getProgramType()));
            } else {
                ty.setProgramType(type.getProgramType());
            }

            ty.setMathType(myTypeGraph.SSET);
            ty.setMathTypeValue(type.getModelType());
        } catch (NoSuchSymbolException nsse) {
            noSuchSymbol(tyQualifier, tyName, tyLocation);
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(ty.getName().getName(), ty.getLocation());
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link RecordTy}.
     * </p>
     *
     * @param ty
     *            A raw record type.
     */
    @Override
    public final void postRecordTy(RecordTy ty) {
        Map<String, PTType> fieldMap = new HashMap<>();
        List<VarDec> fields = ty.getFields();
        for (VarDec field : fields) {
            fieldMap.put(field.getName().getName(), field.getTy().getProgramType());
        }

        PTRecord record = new PTRecord(myTypeGraph, fieldMap);

        ty.setProgramType(record);
        ty.setMathType(myTypeGraph.SSET);
        ty.setMathTypeValue(record.toMath());
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * The type graph containing all the type relationships.
     * </p>
     *
     * @return The type graph for the compiler.
     */
    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // -----------------------------------------------------------
    // General
    // -----------------------------------------------------------

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
    private void emitDebug(Location l, String message) {
        if (myCompileEnvironment.flags.isFlagSet(FLAG_POPULATOR_DEBUG)) {
            myStatusHandler.info(l, message);
        }
    }

    // -----------------------------------------------------------
    // Operation-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * This is an helper method that puts operation-like item ({@link OperationDec} or {@link ProcedureDec}) into the
     * symbol table.
     * </p>
     *
     * @param name
     *            Name of the operation.
     * @param returnTy
     *            The return value for the operation.
     * @param dec
     *            The defining element.
     * @param innermostActiveScope
     *            The current scope.
     */
    private void putOperationLikeThingInSymbolTable(PosSymbol name, Ty returnTy, ResolveConceptualElement dec,
            ScopeBuilder innermostActiveScope) {
        try {
            PTType returnType;
            if (returnTy == null) {
                returnType = PTVoid.getInstance(myTypeGraph);
            } else {
                returnType = returnTy.getProgramType();
            }

            innermostActiveScope.addOperation(name.getName(), dec, myCurrentParameters, returnType);
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(name.getName(), name.getLocation());
        }
    }

    // -----------------------------------------------------------
    // Math Type-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Add a new binding for a {@link ResolveConceptualElement}.
     * </p>
     *
     * @param name
     *            Name of the entry.
     * @param l
     *            Location where this element was found.
     * @param q
     *            A quantifier.
     * @param definingElement
     *            The object that is receiving the binding.
     * @param type
     *            The mathematical type associated with the object.
     * @param typeValue
     *            The mathematical type value associated with the object.
     * @param schematicTypes
     *            The schematic types associated with the object.
     */
    private void addBinding(String name, Location l, SymbolTableEntry.Quantification q,
            ResolveConceptualElement definingElement, MTType type, MTType typeValue,
            Map<String, MTType> schematicTypes) {
        if (type == null) {
            throw new NullPointerException();
        } else {
            try {
                myBuilder.getInnermostActiveScope().addBinding(name, q, definingElement, type, typeValue,
                        schematicTypes, myGenericTypes);
            } catch (DuplicateSymbolException dse) {
                duplicateSymbol(name, l);
                throw new RuntimeException(); // This will never fire
            }
        }
    }

    /**
     * <p>
     * Add a new binding for a {@link ResolveConceptualElement} with no quantification.
     * </p>
     *
     * @param name
     *            Name of the entry.
     * @param l
     *            Location where this element was found.
     * @param definingElement
     *            The object that is receiving the binding.
     * @param type
     *            The mathematical type associated with the object.
     * @param typeValue
     *            The mathematical type value associated with the object.
     * @param schematicTypes
     *            The schematic types associated with the object.
     */
    private void addBinding(String name, Location l, ResolveConceptualElement definingElement, MTType type,
            MTType typeValue, Map<String, MTType> schematicTypes) {
        addBinding(name, l, SymbolTableEntry.Quantification.NONE, definingElement, type, typeValue, schematicTypes);
    }

    /**
     * <p>
     * Add a new binding for a {@link ResolveConceptualElement} with no mathematical type value.
     * </p>
     *
     * @param name
     *            Name of the entry.
     * @param l
     *            Location where this element was found.
     * @param q
     *            A quantifier.
     * @param definingElement
     *            The object that is receiving the binding.
     * @param type
     *            The mathematical type associated with the object.
     * @param schematicTypes
     *            The schematic types associated with the object.
     */
    private void addBinding(String name, Location l, SymbolTableEntry.Quantification q,
            ResolveConceptualElement definingElement, MTType type, Map<String, MTType> schematicTypes) {
        addBinding(name, l, q, definingElement, type, null, schematicTypes);
    }

    /**
     * <p>
     * Applies the provided mathematical type to a {@link FunctionExp}.
     * </p>
     *
     * @param functionSegment
     *            A function expression.
     * @param type
     *            The type to be applied.
     *
     * @return The resulting mathematical type.
     */
    private MTType applyFunction(FunctionExp functionSegment, MTType type) {
        MTType result;

        try {
            MTFunction functionType = (MTFunction) type;

            // Ok, we need to type check our arguments before we can
            // continue
            for (Exp exp : functionSegment.getArguments()) {
                TreeWalker.visit(this, exp);
            }

            if (!INEXACT_DOMAIN_MATCH.compare(functionSegment,
                    functionSegment.getConservativePreApplicationType(myTypeGraph), functionType)) {
                throw new SourceErrorException(
                        "Parameters do not " + "match function range.\n\nExpected: " + functionType.getDomain()
                                + "\nFound:    "
                                + functionSegment.getConservativePreApplicationType(myTypeGraph).getDomain(),
                        functionSegment.getLocation());
            }

            result = functionType.getRange();
        } catch (ClassCastException cce) {
            throw new SourceErrorException("Not a function.", functionSegment.getLocation());
        }

        return result;
    }

    /**
     * <p>
     * Applies the provided mathematical type to the {@code Val_in} function.
     * </p>
     *
     * @param lastSegment
     *            The segment that contains the type information for the {@code Val_in} function.
     * @param functionSegment
     *            A function expression.
     * @param type
     *            The type to be applied.
     *
     * @return The resulting mathematical type.
     */
    private MTType applyValInFunction(Exp lastSegment, FunctionExp functionSegment, MTType type) {
        MTType result;

        try {
            MTFunction functionType = (MTFunction) type;

            // Val_in only takes in one argument
            List<Exp> functionArguments = functionSegment.getArguments();
            if (functionArguments.size() != 1) {
                throw new SourceErrorException("Wrong number of arguments.", functionSegment.getLocation());
            } else {
                // Ok, we need to type check our argument before we can
                // continue
                Exp argExp = functionArguments.get(0);
                TreeWalker.visit(this, argExp);

                // Sanity check to make sure the argument is valid
                ValidVal_inChecker checker = new ValidVal_inChecker(myTypeGraph, myBuilder);
                checker.validArgument(lastSegment, argExp);
            }

            result = functionType.getRange();
        } catch (ClassCastException cce) {
            throw new SourceErrorException("Not a function.", functionSegment.getLocation());
        }

        return result;
    }

    /**
     * <p>
     * An helper method that indicates we are beginning to evaluate a type value node.
     * </p>
     */
    private void enteringTypeValueNode() {
        myTypeValueDepth++;
    }

    /**
     * <p>
     * Attempts to use the list of {@link SymbolTableEntry SymbolTableEntries} candidates to find the
     * {@link MathSymbolEntry} that match the given expression using a type comparison algorithm.
     * </p>
     *
     * @param e
     *            The expression we are searching for.
     * @param eType
     *            The expression's type. (<em>Note:</em> It might be different from calling {@code e.getMathType()}.)
     * @param candidates
     *            List of candidate symbol table entries.
     * @param comparison
     *            The domain type comparator to be used.
     *
     * @return The corresponding {@link MathSymbolEntry}.
     *
     * @throws NoSolutionException
     *             We simply couldn't find it.
     */
    private MathSymbolEntry getDomainTypeMatch(AbstractFunctionExp e, MTFunction eType,
            List<MathSymbolEntry> candidates, TypeComparison<AbstractFunctionExp, MTFunction> comparison)
            throws NoSolutionException {
        MathSymbolEntry match = null;

        MTFunction candidateType;
        for (MathSymbolEntry candidate : candidates) {
            if (candidate.getType() instanceof MTFunction) {
                try {
                    candidate = candidate.deschematize(e.getParameters(), myBuilder.getInnermostActiveScope(),
                            myDefinitionSchematicTypes);
                    candidateType = (MTFunction) candidate.getType();
                    emitDebug(e.getLocation(), "\t" + eType + " deschematizes to " + candidateType);

                    if (comparison.compare(e, eType, candidateType)) {
                        if (match != null) {
                            throw new SourceErrorException(
                                    "Multiple " + comparison.description() + " domain " + "matches.  For example, "
                                            + match.getName() + " : " + match.getType() + " and " + candidate.getName()
                                            + " : " + candidate.getType() + ".  Consider explicitly qualifying.",
                                    e.getLocation());
                        }

                        match = candidate;
                    }
                    // YS: eType's domain could be a function application that generates
                    // the candidate type, therefore we will need check the function inside
                    // the function application to see if it matches.
                    else if (eType.getDomain() instanceof MTFunctionApplication) {
                        MTFunctionApplication eTypeDomainType = (MTFunctionApplication) eType.getDomain();
                        if (comparison.compare(e, eTypeDomainType.getFunction(), candidateType)) {
                            if (match != null) {
                                throw new SourceErrorException("Multiple " + comparison.description() + " domain "
                                        + "matches.  For example, " + match.getName() + " : " + match.getType()
                                        + " and " + candidate.getName() + " : " + candidate.getType()
                                        + ".  Consider explicitly qualifying.", e.getLocation());
                            }

                            match = candidate;
                        }
                    }
                } catch (NoSolutionException nse) {
                    // couldn't deschematize--try the next one
                    emitDebug(e.getLocation(), "\t" + candidate.getType() + " doesn't deschematize " + "against "
                            + e.getParameters() + " using " + comparison.description());
                }
            }
        }

        if (match == null) {
            throw new NoSolutionException(
                    "Could not find a symbol entry for: " + e + " using " + comparison.description(), null);
        }

        emitDebug(e.getLocation(), "\tFound a match using " + comparison.description());

        return match;
    }

    /**
     * <p>
     * Attempts to use the list of {@link SymbolTableEntry SymbolTableEntries} candidates to find the
     * {@link MathSymbolEntry} that match the given expression using an exact domain match.
     * </p>
     *
     * @param e
     *            The expression we are searching for.
     * @param eType
     *            The expression's type. (<em>Note:</em> It might be different from calling {@code e.getMathType()}.)
     * @param candidates
     *            List of candidate symbol table entries.
     *
     * @return The corresponding {@link MathSymbolEntry}.
     *
     * @throws NoSolutionException
     *             We simply couldn't find it.
     */
    private MathSymbolEntry getExactDomainTypeMatch(AbstractFunctionExp e, MTFunction eType,
            List<MathSymbolEntry> candidates) throws NoSolutionException {
        return getDomainTypeMatch(e, eType, candidates, EXACT_DOMAIN_MATCH);
    }

    /**
     * <p>
     * Attempts to use the list of {@link SymbolTableEntry SymbolTableEntries} candidates to find the
     * {@link MathSymbolEntry} that match the given expression using an inexact domain match.
     * </p>
     *
     * @param e
     *            The expression we are searching for.
     * @param eType
     *            The expression's type. (<em>Note:</em> It might be different from calling {@code e.getMathType()}.)
     * @param candidates
     *            List of candidate symbol table entries.
     *
     * @return The corresponding {@link MathSymbolEntry}.
     *
     * @throws NoSolutionException
     *             We simply couldn't find it.
     */
    private MathSymbolEntry getInexactDomainTypeMatch(AbstractFunctionExp e, MTFunction eType,
            List<MathSymbolEntry> candidates) throws NoSolutionException {
        return getDomainTypeMatch(e, eType, candidates, INEXACT_DOMAIN_MATCH);
    }

    /**
     * <p>
     * For a given {@link AbstractFunctionExp}, finds the entry in the symbol table to which it refers.
     * </p>
     *
     * @param e
     *            The expression we are searching for.
     *
     * @return The corresponding {@link MathSymbolEntry}.
     */
    private MathSymbolEntry getIntendedFunction(AbstractFunctionExp e) {
        MTFunction eType = e.getConservativePreApplicationType(myTypeGraph);

        PosSymbol eOperator = e.getOperatorAsPosSymbol();
        String eOperatorString = eOperator.getName();

        List<MathSymbolEntry> sameNameFunctions = myBuilder.getInnermostActiveScope()
                .query(new MathFunctionNamedQuery(e.getQualifier(), e.getOperatorAsPosSymbol()));

        if (sameNameFunctions.isEmpty()) {
            throw new SourceErrorException("No such function: " + e.getOperatorAsString(), e.getLocation());
        }

        MathSymbolEntry intendedEntry;
        try {
            intendedEntry = getExactDomainTypeMatch(e, eType, sameNameFunctions);
        } catch (NoSolutionException nse) {
            try {
                emitDebug(e.getLocation(), "\t" + nse.getMessage());
                intendedEntry = getInexactDomainTypeMatch(e, eType, sameNameFunctions);
            } catch (NoSolutionException nsee2) {
                try {
                    emitDebug(e.getLocation(), "\t" + nsee2.getMessage());

                    if (!myDefinitionSchematicTypes.isEmpty()) {
                        // Create a copy of the original expression and assign it the conservative type
                        AbstractFunctionExp eCopy = (AbstractFunctionExp) e.clone();
                        eCopy.setMathType(eType);

                        // Obtain a new AbstractFunctionExp with the schematic types replaced.
                        eCopy = (AbstractFunctionExp) TypeGraph.getCopyWithVariablesSubstituted(eCopy,
                                myDefinitionSchematicTypes);

                        // If we happen to have any generic program types, replace those as well
                        if (!myGenericTypes.isEmpty()) {
                            eCopy = (AbstractFunctionExp) TypeGraph.getCopyWithVariablesSubstituted(eCopy,
                                    myGenericTypes);
                        }

                        MTFunction eCopyType = (MTFunction) eCopy.getMathType();

                        emitDebug(eCopy.getLocation(), "\tReplaced schematic types in expression: " + eCopy.toString()
                                + "(" + eCopy.getLocation() + ") with new type " + eCopyType.toString());

                        intendedEntry = getInexactDomainTypeMatch(eCopy, eCopyType, sameNameFunctions);
                    } else {
                        throw new NoSolutionException("No schematic types", null);
                    }
                } catch (NoSolutionException nsee3) {
                    emitDebug(e.getLocation(), "\t" + nsee3.getMessage());

                    boolean foundOne = false;
                    String errorMessage = "No function applicable for " + "domain: " + eType.getDomain() + "\t["
                            + e.getLocation() + "]\n\nCandidates:\n";

                    StringBuilder sb = new StringBuilder(errorMessage);
                    for (SymbolTableEntry entry : sameNameFunctions) {
                        if (entry instanceof MathSymbolEntry
                                && ((MathSymbolEntry) entry).getType() instanceof MTFunction) {
                            sb.append("\t[");
                            sb.append(entry.getDefiningElement().getLocation());
                            sb.append("]\t");
                            sb.append(entry.getName());
                            sb.append(" : ");
                            sb.append(((MathSymbolEntry) entry).getType());
                            sb.append("\n");

                            foundOne = true;
                        }
                    }

                    if (!foundOne) {
                        throw new SourceErrorException("No such function.", e.getLocation());
                    }

                    throw new SourceErrorException(sb.toString(), (Location) null);
                }
            }
        }

        if (intendedEntry.getDefiningElement() == myCurrentDirectDefinition) {
            throw new SourceErrorException("Direct definition cannot " + "contain recursive call.", e.getLocation());
        }

        MTFunction intendedEntryType = (MTFunction) intendedEntry.getType();

        emitDebug(e.getLocation(), "\tMatching " + eOperatorString + " : " + eType + " to " + intendedEntry.getName()
                + " : " + intendedEntryType + ".");

        return intendedEntry;
    }

    /**
     * <p>
     * Returns the name component of a {@link VarExp} or {@link FunctionExp}.
     * </p>
     *
     * @param e
     *            Expression to be evaluated.
     *
     * @return The expression name as a string.
     */
    private String getName(Exp e) {
        String result;

        if (e instanceof VarExp) {
            result = ((VarExp) e).getName().getName();
        } else if (e instanceof FunctionExp) {
            result = getName(((FunctionExp) e).getName());
        } else {
            throw new RuntimeException("Not a VarExp or FunctionExp:  " + e + " (" + e.getClass() + ")");
        }

        return result;
    }

    /**
     * <p>
     * This method has to do an annoying amount of work, so pay attention: takes an iterator over segments as returned
     * from DotExp.getSegments(). Either the first segment or first two segments will be advanced over from the
     * iterator, depending on whether this method determines the DotExp refers to a local value (one segment), is a
     * qualified name referring to a value in another module (two segments), or is a Conc expression (two segments). The
     * segments will receive appropriate types. The data field of lastGood will be set with the location of the last
     * segment read. Then, the <code>MathSymbolEntry</code> corresponding to the correct top-level value will be
     * returned.
     * </p>
     *
     * @param segments
     *            An iterator for the various segments of an {@link Exp}.
     * @param lastGood
     *            An object that indirectly refer to the last good segment.
     */
    private MathSymbolEntry getTopLevelValue(Iterator<Exp> segments, Indirect<Exp> lastGood) {
        MathSymbolEntry result;

        Exp first = segments.next();

        PosSymbol firstQualifier = null;
        PosSymbol firstName;
        if (first instanceof OldExp) {
            firstQualifier = ((VarExp) ((OldExp) first).getExp()).getQualifier();
            firstName = ((VarExp) ((OldExp) first).getExp()).getName();
        } else if (first instanceof VarExp) {
            firstQualifier = ((VarExp) first).getQualifier();
            firstName = ((VarExp) first).getName();
        } else {
            throw new RuntimeException(
                    "DotExp must start with VarExp or " + "OldExp, found: " + first + " (" + first.getClass() + ")");
        }

        // First, we'll see if we're a Conc expression
        if (firstName.getName().equals("Conc")) {
            VarExp second = (VarExp) segments.next();

            // We are in a type realization and our second segment
            // refer to the exemplar.
            if (myTypeFamilyEntry != null) {
                if (!second.toString().equals(myTypeFamilyEntry.getProgramType().getExemplarName())) {
                    throw new RuntimeException("No idea what's going on here.");
                }

                second.setMathType(myTypeFamilyEntry.getModelType());
                result = myTypeFamilyEntry.getExemplar();
            }
            // We are in a shared state realization and our second segment
            // refers to one of the global variables.
            else {
                result = myBuilder
                        .getInnermostActiveScope().queryForOne(new NameQuery(null, second.getName(),
                                ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_IGNORE, true))
                        .toMathSymbolEntry(second.getLocation());
                second.setMathType(result.getType());
            }

            // The Conc segment doesn't have a sensible type, but we'll set one
            // for completeness.
            first.setMathType(myTypeGraph.BOOLEAN);
            lastGood.data = second;
        } else {
            // Next, we'll see if there's a locally-accessible symbol with this
            // name
            try {
                result = myBuilder
                        .getInnermostActiveScope().queryForOne(new NameQuery(firstQualifier, firstName,
                                ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_INSTANTIATE, true))
                        .toMathSymbolEntry(first.getLocation());

                // There is. Cool. We type it and we're done
                lastGood.data = first;
                first.setMathType(result.getType());
                try {
                    first.setMathTypeValue(result.getTypeValue());
                } catch (SymbolNotOfKindTypeException snokte) {

                }
            } catch (NoSuchSymbolException nsse) {
                // TODO: Figure out if the following is needed now that we don't use dots as qualifier
                // symbol
                // No such luck. Maybe firstName identifies a module and the
                // second segment (which had better be a VarExp) is the name of
                // the value we want
                Exp second = segments.next();
                if (second instanceof VarExp) {
                    VarExp secondAsVarExp = (VarExp) second;

                    try {
                        result = myBuilder.getInnermostActiveScope()
                                .queryForOne(new NameQuery(firstName, secondAsVarExp.getName(),
                                        ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_IGNORE, true))
                                .toMathSymbolEntry(first.getLocation());

                        // A qualifier doesn't have a sensible type, but we'll set one
                        // for completeness.
                        first.setMathType(myTypeGraph.BOOLEAN);

                        // Now the value itself
                        lastGood.data = second;
                        second.setMathType(result.getType());
                        try {
                            second.setMathTypeValue(result.getTypeValue());
                        } catch (SymbolNotOfKindTypeException snokte) {

                        }
                    } catch (NoSuchSymbolException nsse2) {
                        noSuchSymbol(firstName, secondAsVarExp.getName());
                        throw new RuntimeException(); // This will never fire
                    } catch (DuplicateSymbolException dse) {
                        // This shouldn't be possible--there can only be one symbol
                        // with the given name inside a particular module
                        throw new RuntimeException();
                    }
                } else {
                    throw new SourceErrorException("Expecting a VarExp.\nFound: " + second.getClass().getSimpleName(),
                            second.getLocation());
                }
            } catch (DuplicateSymbolException dse) {
                duplicateSymbol(firstName);
                throw new RuntimeException(); // This will never fire
            }
        }

        return result;
    }

    /**
     * <p>
     * An helper method that indicates we are leaving a type value node.
     * </p>
     */
    private void leavingTypeValueNode() {
        myTypeValueDepth--;
    }

    // -----------------------------------------------------------
    // Program Type-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * An helper method that returns the built-in <code>Character</code> program type.
     * </p>
     *
     * @param l
     *            A {@link Location} in file.
     *
     * @return A {@link PTType}.
     */
    private PTType getCharProgramType(Location l) {
        PTType result;

        try {
            ProgramTypeEntry type = myBuilder.getInnermostActiveScope().queryForOne(new NameQuery(null, "Character",
                    ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_INSTANTIATE, false)).toProgramTypeEntry(l);

            result = type.getProgramType();
        } catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("No program Character type in scope???");
        } catch (DuplicateSymbolException dse) {
            // Shouldn't be possible--NameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

    /**
     * <p>
     * An helper method that returns the built-in <code>Integer</code> program type.
     * </p>
     *
     * @param l
     *            A {@link Location} in file.
     *
     * @return A {@link PTType}.
     */
    private PTType getIntegerProgramType(Location l) {
        PTType result;

        try {
            ProgramTypeEntry type = myBuilder.getInnermostActiveScope().queryForOne(new NameQuery(null, "Integer",
                    ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_INSTANTIATE, false)).toProgramTypeEntry(l);

            result = type.getProgramType();
        } catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("No program Integer type in scope???");
        } catch (DuplicateSymbolException dse) {
            // Shouldn't be possible--NameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

    /**
     * <p>
     * An helper method that returns the built-in <code>Char_Str</code> program type.
     * </p>
     *
     * @param l
     *            A {@link Location} in file.
     *
     * @return A {@link PTType}.
     */
    private PTType getStringProgramType(Location l) {
        PTType result;

        try {
            ProgramTypeEntry type = myBuilder.getInnermostActiveScope().queryForOne(new NameQuery(null, "Char_Str",
                    ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_INSTANTIATE, false)).toProgramTypeEntry(l);

            result = type.getProgramType();
        } catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("No program String type in scope???");
        } catch (DuplicateSymbolException dse) {
            // Shouldn't be possible--NameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

    // -----------------------------------------------------------
    // Math and Program Type-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * An helper method that retrieves the mathematical symbol entry using the provided information.
     * </p>
     *
     * @param qualifier
     *            Qualifier for the expression.
     * @param symbolName
     *            The symbol name.
     * @param node
     *            The expression encountered.
     *
     * @return A {@link MathSymbolEntry} that is associated with the provided information.
     */
    private MathSymbolEntry getIntendedEntry(PosSymbol qualifier, String symbolName, Exp node) {
        MathSymbolEntry result;

        try {
            result = myBuilder.getInnermostActiveScope()
                    .queryForOne(new MathSymbolQuery(qualifier, symbolName, node.getLocation()));
        } catch (DuplicateSymbolException dse) {
            duplicateSymbol(symbolName, node.getLocation());
            throw new RuntimeException(); // This will never fire
        } catch (NoSuchSymbolException nsse) {
            noSuchSymbol(qualifier, symbolName, node.getLocation());
            throw new RuntimeException(); // This will never fire
        }

        return result;
    }

    /**
     * <p>
     * An helper method that handles the additional logic that happens after we encounter a symbol expression.
     * </p>
     *
     * @param qualifier
     *            Qualifier for the expression.
     * @param symbolName
     *            The symbol name.
     * @param node
     *            The expression encountered.
     *
     * @return A typed {@link MathSymbolEntry} that is associated with the provided information.
     */
    private MathSymbolEntry postSymbolExp(PosSymbol qualifier, String symbolName, Exp node) {
        MathSymbolEntry intendedEntry = getIntendedEntry(qualifier, symbolName, node);
        node.setMathType(intendedEntry.getType());

        setSymbolTypeValue(node, symbolName, intendedEntry);

        String typeValueDesc = "";

        if (node.getMathTypeValue() != null) {
            typeValueDesc = ", referencing math type " + node.getMathTypeValue() + " ("
                    + node.getMathTypeValue().getClass() + ")";
        }

        emitDebug(node.getLocation(),
                "\tProcessed symbol " + symbolName + " with type " + node.getMathType() + typeValueDesc);

        return intendedEntry;
    }

    /**
     * <p>
     * An helper method that handles the logic for assigning the mathematical type value for a given expression.
     * </p>
     *
     * @param node
     *            The expression encountered.
     * @param symbolName
     *            The symbol name.
     * @param intendedEntry
     *            An untyped {@link MathSymbolEntry} that is associated with the provided information.
     */
    private void setSymbolTypeValue(Exp node, String symbolName, MathSymbolEntry intendedEntry) {
        try {
            if (intendedEntry.getQuantification() == SymbolTableEntry.Quantification.NONE) {
                node.setMathTypeValue(intendedEntry.getTypeValue());
            } else {
                if (intendedEntry.getType().isKnownToContainOnlyMTypes()) {
                    node.setMathTypeValue(new MTNamed(myTypeGraph, symbolName));
                }
            }
        } catch (SymbolNotOfKindTypeException snokte) {
            if (myTypeValueDepth > 0) {
                // I had better identify a type
                notAType(intendedEntry, node.getLocation());
            }
        }
    }

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    /**
     * <p>
     * An helper method that indicates we have an ambiguous symbol.
     * </p>
     *
     * @param symbol
     *            The symbol represented as a {@link PosSymbol}.
     * @param candidates
     *            List of symbol entries that match {@code symbol}.
     * @param <T>
     *            A type that extends from {@link SymbolTableEntry}.
     */
    private <T extends SymbolTableEntry> void ambiguousSymbol(PosSymbol symbol, List<T> candidates) {
        ambiguousSymbol(symbol.getName(), symbol.getLocation(), candidates);
    }

    /**
     * <p>
     * An helper method that indicates we have an ambiguous symbol.
     * </p>
     *
     * @param symbolName
     *            The symbol represented as a {@link String}.
     * @param l
     *            The location where {@code symbolName} was found.
     * @param candidates
     *            List of symbol entries that match {@code symbolName}.
     * @param <T>
     *            A type that extends from {@link SymbolTableEntry}.
     */
    private <T extends SymbolTableEntry> void ambiguousSymbol(String symbolName, Location l, List<T> candidates) {
        StringBuilder sb = new StringBuilder();

        sb.append("Ambiguous symbol.  Candidates: ");
        boolean first = true;
        for (SymbolTableEntry candidate : candidates) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(candidate.getSourceModuleIdentifier().fullyQualifiedRepresentation(symbolName));
        }
        sb.append(".  Consider qualifying.");

        throw new SourceErrorException(sb.toString(), l);
    }

    /**
     * <p>
     * An helper method that indicates we have a duplicate symbol.
     * </p>
     *
     * @param symbol
     *            The symbol represented as a {@link PosSymbol}.
     */
    private void duplicateSymbol(PosSymbol symbol) {
        duplicateSymbol(symbol.getName(), symbol.getLocation());
    }

    /**
     * <p>
     * An helper method that indicates we have a duplicate symbol.
     * </p>
     *
     * @param symbol
     *            The symbol represented as a {@link PosSymbol}.
     * @param l
     *            The location where {@code symbol} was found.
     */
    private void duplicateSymbol(String symbol, Location l) {
        throw new SourceErrorException("Duplicate symbol: " + symbol, l);
    }

    /**
     * <p>
     * An helper method that indicates the expected type differs from the one we found.
     * </p>
     *
     * @param e
     *            Expression that is being evaluated.
     * @param expectedType
     *            The expected type for {@code e}.
     */
    private void expected(Exp e, MTType expectedType) {
        throw new SourceErrorException("Expected: " + expectedType + "\nFound: " + e.getMathType(), e.getLocation());
    }

    /**
     * <p>
     * An helper method that indicates the type we found is not known to be in the expected type.
     * </p>
     *
     * @param e
     *            Expression that is being evaluated.
     * @param expectedType
     *            The expected type for {@code e}.
     */
    private void expectType(Exp e, MTType expectedType) {
        if (!myTypeGraph.isKnownToBeIn(e, expectedType)) {
            // YS: Our expected type might be the generic program type, so
            // we replace it and try to see if it is known to be in.
            if (!myGenericTypes.isEmpty()) {
                MTType newExpectedType = expectedType.getCopyWithVariablesSubstituted(myGenericTypes);

                if (!myTypeGraph.isKnownToBeIn(e, newExpectedType)) {
                    expected(e, newExpectedType);
                }
            } else {
                expected(e, expectedType);
            }
        }
    }

    /**
     * <p>
     * An helper method that indicates that the symbol table entry is not known to be a type.
     * </p>
     *
     * @param entry
     *            An entry in our symbol table.
     * @param l
     *            The location where this entry was found.
     */
    private void notAType(SymbolTableEntry entry, Location l) {
        throw new SourceErrorException(entry.getSourceModuleIdentifier().fullyQualifiedRepresentation(entry.getName())
                + " is not known to be a type.", l);
    }

    /**
     * <p>
     * An helper method that indicates that the evaluated expression is not known to be a type.
     * </p>
     *
     * @param e
     *            An expression that is being evaluated.
     */
    private void notAType(Exp e) {
        throw new SourceErrorException("Not known to be a type.", e.getLocation());
    }

    /**
     * <p>
     * An helper method that indicates that a module with the specified name cannot be found.
     * </p>
     *
     * @param qualifier
     *            The name of a module.
     */
    private void noSuchModule(PosSymbol qualifier) {
        throw new SourceErrorException("Module does not exist or is not in scope.", qualifier);
    }

    /**
     * <p>
     * An helper method that indicates that a symbol with the specified qualifier and name cannot be found.
     * </p>
     *
     * @param qualifier
     *            The module qualifier for the symbol.
     * @param symbol
     *            The name of the symbol represented as a {@link PosSymbol}.
     */
    private void noSuchSymbol(PosSymbol qualifier, PosSymbol symbol) {
        noSuchSymbol(qualifier, symbol.getName(), symbol.getLocation());
    }

    /**
     * <p>
     * An helper method that indicates that a symbol with the specified qualifier and name cannot be found.
     * </p>
     *
     * @param qualifier
     *            The module qualifier for the symbol.
     * @param symbolName
     *            The name of the symbol represented as a {@link String}.
     * @param l
     *            The location where this symbol was found.
     */
    private void noSuchSymbol(PosSymbol qualifier, String symbolName, Location l) {
        String message;

        if (qualifier == null) {
            message = "No such symbol: " + symbolName;
        } else {
            message = "No such symbol in module: " + qualifier.getName() + "::" + symbolName;
        }

        throw new SourceErrorException(message, l);
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * An helper class that indicates an exact domain match between an {@link AbstractFunctionExp} and a
     * {@link MTFunction}.
     * </p>
     */
    private static class ExactDomainMatch implements TypeComparison<AbstractFunctionExp, MTFunction> {

        /**
         * <p>
         * Takes an instance of {@link AbstractFunctionExp} and use the {@link MTType} found in the expression and
         * compare it with the expected {@link MTType}.
         * </p>
         *
         * @param foundValue
         *            The expression to be compared.
         * @param foundType
         *            The type for the expression.
         * @param expectedType
         *            The expected type for the expression.
         *
         * @return {@code true} if {@code foundType = expectedType}, {@code false} otherwise.
         */
        @Override
        public final boolean compare(AbstractFunctionExp foundValue, MTFunction foundType, MTFunction expectedType) {
            return foundType.parameterTypesMatch(expectedType, EXACT_PARAMETER_MATCH);
        }

        /**
         * <p>
         * This method returns a string description for each type comparison.
         * </p>
         *
         * @return A string.
         */
        @Override
        public final String description() {
            return "Exact Domain Match";
        }

    }

    /**
     * <p>
     * An helper class that indicates an inexact domain match between an {@link AbstractFunctionExp} and a
     * {@link MTFunction}.
     * </p>
     */
    private class InexactDomainMatch implements TypeComparison<AbstractFunctionExp, MTFunction> {

        /**
         * <p>
         * Takes an instance of {@link AbstractFunctionExp} and use the {@link MTType} found in the expression and
         * compare it with the expected {@link MTType}.
         * </p>
         *
         * @param foundValue
         *            The expression to be compared.
         * @param foundType
         *            The type for the expression.
         * @param expectedType
         *            The expected type for the expression.
         *
         * @return {@code true} if {@code foundType = expectedType}, {@code false} otherwise.
         */
        @Override
        public final boolean compare(AbstractFunctionExp foundValue, MTFunction foundType, MTFunction expectedType) {
            return expectedType.parametersMatch(foundValue.getParameters(), INEXACT_PARAMETER_MATCH);
        }

        /**
         * <p>
         * This method returns a string description for each type comparison.
         * </p>
         *
         * @return A string.
         */
        @Override
        public final String description() {
            return "Inexact Domain Match";
        }

    }

    /**
     * <p>
     * An helper class that indicates an exact parameter match between two {@link MTType MTTypes}.
     * </p>
     */
    private static class ExactParameterMatch implements Comparator<MTType> {

        /**
         * <p>
         * Compares <code>o1</code> and <code>o2</code>.
         * </p>
         *
         * @param o1
         *            A mathematical type.
         * @param o2
         *            Another mathematical type.
         *
         * @return Comparison results expressed as an integer.
         */
        @Override
        public final int compare(MTType o1, MTType o2) {
            int result;

            if (o1.equals(o2)) {
                result = 0;
            } else {
                result = 1;
            }

            return result;
        }

    }

    /**
     * <p>
     * An helper class that indicates an inexact domain match between an {@link Exp} and a {@link MTType}.
     * </p>
     */
    private class InexactParameterMatch implements TypeComparison<Exp, MTType> {

        /**
         * <p>
         * Takes an instance of {@link Exp} and use the {@link MTType} found in the expression and compare it with the
         * expected {@link MTType}.
         * </p>
         *
         * @param foundValue
         *            The expression to be compared.
         * @param foundType
         *            The type for the expression.
         * @param expectedType
         *            The expected type for the expression.
         *
         * @return {@code true} if {@code foundType = expectedType}, {@code false} otherwise.
         */
        @Override
        public final boolean compare(Exp foundValue, MTType foundType, MTType expectedType) {

            boolean result = myTypeGraph.isKnownToBeIn(foundValue, expectedType);

            if (!result && foundValue instanceof LambdaExp && expectedType instanceof MTFunction) {
                LambdaExp foundValueAsLambda = (LambdaExp) foundValue;
                MTFunction expectedTypeAsFunction = (MTFunction) expectedType;
                MTFunction foundTypeAsFunction = (MTFunction) foundValueAsLambda.getMathType();

                result = myTypeGraph.isSubtype(foundTypeAsFunction.getDomain(), expectedTypeAsFunction.getDomain())
                        && myTypeGraph.isKnownToBeIn(foundValueAsLambda.getBody(), expectedTypeAsFunction.getRange());
            }

            // YS: Not sure if this is quite right, but here is the explanation:
            // If "foundValue" happens to be a VarExp. Then we proceed to check if there
            // is a type value. If there is one and it is an MTNamed that has the same name as
            // the parameter and "foundType" is a subtype of "expectedType", then it must be a
            // schematic type, so we let it through.
            if (!result && foundValue instanceof VarExp) {
                VarExp foundValueAsVarExp = (VarExp) foundValue;
                if (foundValueAsVarExp.getMathTypeValue() != null
                        && foundValueAsVarExp.getMathTypeValue() instanceof MTNamed) {
                    String foundValueName = foundValueAsVarExp.getName().getName();
                    String foundTypeValueAsString = ((MTNamed) foundValueAsVarExp.getMathTypeValue()).getName();
                    result = foundTypeValueAsString.equals(foundValueName) && foundType.isSubtypeOf(expectedType);
                }
            }

            // YS: Not sure if this is quite right, but here is the explanation:
            // If the expectedType happens to be a function application, then check to see
            // if the foundValue is known to be in the range of the function.
            if (!result && expectedType instanceof MTFunctionApplication) {
                MTFunction expectedTypeAsFunction = ((MTFunctionApplication) expectedType).getFunction();
                result = myTypeGraph.isKnownToBeIn(foundValue, expectedTypeAsFunction.getRange());
            }

            // YS: Not sure if this is quite right, but here is the explanation:
            // If the foundType happens to be a function application, then check to see
            // if the range of the function applied is a subtype of the expectedType.
            if (!result && foundType instanceof MTFunctionApplication) {
                MTFunction foundTypeAsFunction = ((MTFunctionApplication) foundType).getFunction();
                result = myTypeGraph.isSubtype(foundTypeAsFunction.getRange(), expectedType);
            }

            return result;
        }

        /**
         * <p>
         * This method returns a string description for each type comparison.
         * </p>
         *
         * @return A string.
         */
        @Override
        public final String description() {
            return "Inexact Parameter Match";
        }

    }

}
