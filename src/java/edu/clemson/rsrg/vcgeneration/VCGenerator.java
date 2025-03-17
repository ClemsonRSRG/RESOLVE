/*
 * VCGenerator.java
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
package edu.clemson.rsrg.vcgeneration;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.rsrg.absyn.declarations.operationdecl.OperationProcedureDec;
import edu.clemson.rsrg.absyn.declarations.operationdecl.ProcedureDec;
import edu.clemson.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.rsrg.absyn.declarations.sharedstatedecl.SharedStateRealizationDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.AbstractTypeRepresentationDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.FacilityTypeRepresentationDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.TypeRepresentationDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.DotExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.MathExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.OldExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramVariableExp;
import edu.clemson.rsrg.absyn.items.programitems.AbstractInitFinalItem;
import edu.clemson.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.rsrg.absyn.items.programitems.IfConditionItem;
import edu.clemson.rsrg.absyn.items.programitems.RealizInitFinalItem;
import edu.clemson.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.rsrg.absyn.rawtypes.RecordTy;
import edu.clemson.rsrg.absyn.statements.*;
import edu.clemson.rsrg.absyn.statements.MemoryStmt.StatementType;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.flag.Flag;
import edu.clemson.rsrg.init.flag.FlagDependencies;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.prover.immutableadts.ImmutableList;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.statushandling.Fault;
import edu.clemson.rsrg.statushandling.FaultType;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.rsrg.typeandpopulate.entry.*;
import edu.clemson.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTFamily;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTRepresentation;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.proofrules.declarations.facilitydecl.FacilityDeclRule;
import edu.clemson.rsrg.vcgeneration.proofrules.declarations.operationdecl.ProcedureDeclRule;
import edu.clemson.rsrg.vcgeneration.proofrules.declarations.sharedstatedecl.SharedStateCorrRule;
import edu.clemson.rsrg.vcgeneration.proofrules.declarations.sharedstatedecl.SharedStateRepresentationInitRule;
import edu.clemson.rsrg.vcgeneration.proofrules.declarations.typedecl.TypeRepresentationCorrRule;
import edu.clemson.rsrg.vcgeneration.proofrules.declarations.typedecl.TypeRepresentationFinalRule;
import edu.clemson.rsrg.vcgeneration.proofrules.declarations.typedecl.TypeRepresentationInitRule;
import edu.clemson.rsrg.vcgeneration.proofrules.other.WhichEntailsRule;
import edu.clemson.rsrg.vcgeneration.proofrules.statements.*;
import edu.clemson.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.rsrg.vcgeneration.utilities.formaltoactual.InstantiatedFacilityDecl;
import edu.clemson.rsrg.vcgeneration.utilities.helperstmts.FacilityInitStmt;
import edu.clemson.rsrg.vcgeneration.utilities.helperstmts.FinalizeVarStmt;
import edu.clemson.rsrg.vcgeneration.utilities.helperstmts.InitializeVarStmt;
import edu.clemson.rsrg.vcgeneration.utilities.helperstmts.VCConfirmStmt;
import edu.clemson.rsrg.vcgeneration.utilities.treewalkers.ConceptSharedStateExtractor;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * <p>
 * This class generates verification conditions (VCs) using the provided RESOLVE abstract syntax tree. This visitor
 * logic is implemented as a {@link TreeWalkerVisitor}.
 * </p>
 *
 * @author Heather Keown Harton
 * @author Yu-Shan Sun
 * @author Nighat Yasmin
 *
 * @version 3.0
 */
public class VCGenerator extends TreeWalkerVisitor {

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
     * The verification context for the file we are generating {@code VCs} for.
     * </p>
     */
    private VerificationContext myCurrentVerificationContext;

    /**
     * <p>
     * The module scope for the file we are generating {@code VCs} for.
     * </p>
     */
    private ModuleScope myCurrentModuleScope;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    // -----------------------------------------------------------
    // Initialization and Finalization-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * While walking a {@link SharedStateRealizationDec}, this will be set to its corresponding shared state declaration
     * in the concept.
     * </p>
     */
    private SharedStateDec myCorrespondingSharedStateDec;

    /**
     * <p>
     * An outer declaration that has {@link RealizInitFinalItem RealizInitFinalItems} that needs to be processed.
     * </p>
     */
    private Dec myRealizInitFinalOuterDec;

    // -----------------------------------------------------------
    // Operation Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * While walking a procedure, this stores all the local {@link VarDec VarDec's} program type entry.
     * </p>
     */
    private final Map<VarDec, SymbolTableEntry> myVariableTypeEntries;

    // -----------------------------------------------------------
    // VC Generation-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * The current {@link AssertiveCodeBlock} that the inner declarations will operate on.
     * </p>
     */
    private AssertiveCodeBlock myCurrentAssertiveCodeBlock;

    /**
     * <p>
     * All the completed {@link AssertiveCodeBlock AssertiveCodeBlocks} that only contain the final {@link Sequent
     * Sequents}.
     * </p>
     */
    private final List<AssertiveCodeBlock> myFinalAssertiveCodeBlocks;

    /**
     * <p>
     * All the {@link AssertiveCodeBlock AssertiveCodeBlocks} generated by the various different declaration and
     * statements that still needs more proof rule applications.
     * </p>
     */
    private final Deque<AssertiveCodeBlock> myIncompleteAssertiveCodeBlocks;

    // -----------------------------------------------------------
    // Output-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * String template for the each of the assertive code blocks.
     * </p>
     */
    private final Map<AssertiveCodeBlock, ST> myAssertiveCodeBlockModels;

    /**
     * <p>
     * String template groups for storing all the VC generation details.
     * </p>
     */
    private final STGroup mySTGroup;

    /**
     * <p>
     * String template for the VC generation details model.
     * </p>
     */
    private final ST myVCGenDetailsModel;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_SECTION_NAME = "VCGenerator";
    private static final String FLAG_DESC_VERIFY_VC = "Generate VCs.";
    private static final String FLAG_DESC_PERF_VC = "Generate Performance VCs";
    private static final String FLAG_DESC_ADD_CONSTRAINT = "Add constraints as givens.";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>
     * Tells the compiler to generate VCs.
     * </p>
     */
    public static final Flag FLAG_VERIFY_VC = new Flag(FLAG_SECTION_NAME, "VCs", FLAG_DESC_VERIFY_VC);

    /**
     * <p>
     * Tells the compiler to generate performance VCs.
     * </p>
     */
    private static final Flag FLAG_PVCS_VC = new Flag(FLAG_SECTION_NAME, "PVCs", FLAG_DESC_PERF_VC);

    /**
     * <p>
     * Tells the compiler to generate VCs.
     * </p>
     */
    public static final Flag FLAG_ADD_CONSTRAINT = new Flag(FLAG_SECTION_NAME, "addConstraints",
            FLAG_DESC_ADD_CONSTRAINT);

    /**
     * <p>
     * Add all the required and implied flags for the {@code VCGenerator}.
     * </p>
     */
    public static void setUpFlags() {
        FlagDependencies.addImplies(FLAG_PVCS_VC, FLAG_VERIFY_VC);

        // Make sure we have one of these on.
        Flag[] dependencies = { FLAG_VERIFY_VC, FLAG_PVCS_VC };
        FlagDependencies.addRequires(FLAG_ADD_CONSTRAINT, dependencies);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that overrides methods to generate VCs from a {@link ModuleDec}.
     * </p>
     *
     * @param builder
     *            A scope builder for a symbol table.
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     */
    public VCGenerator(MathSymbolTableBuilder builder, CompileEnvironment compileEnvironment) {
        myAssertiveCodeBlockModels = new LinkedHashMap<>();
        myBuilder = builder;
        myCompileEnvironment = compileEnvironment;
        myCorrespondingSharedStateDec = null;
        myFinalAssertiveCodeBlocks = new LinkedList<>();
        myIncompleteAssertiveCodeBlocks = new LinkedList<>();
        myRealizInitFinalOuterDec = null;
        mySTGroup = new STGroupFile("templates/VCGenVerboseOutput.stg");
        myTypeGraph = myBuilder.getTypeGraph();
        myVariableTypeEntries = new LinkedHashMap<>();
        myVCGenDetailsModel = mySTGroup.getInstanceOf("outputVCGenDetails");
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    @Override
    public void postWhileStmt(WhileStmt e) {

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
        // Set the current module scope
        try {
            myCurrentModuleScope = myBuilder.getModuleScope(new ModuleIdentifier(dec));
            myCurrentVerificationContext = new VerificationContext(dec.getName(), myCurrentModuleScope, myBuilder,
                    myCompileEnvironment);

            // Apply the facility declaration rule to imported facility declarations.
            List<FacilityEntry> results = myCurrentModuleScope.query(new EntryTypeQuery<>(FacilityEntry.class,
                    ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_INSTANTIATE));

            for (SymbolTableEntry s : results) {
                // YS: Only deal with imported facility declarations right now.
                // The facility declarations from this module will be handled in
                // postFacilityDec.
                if (s.getSourceModuleIdentifier().compareTo(myCurrentModuleScope.getModuleIdentifier()) != 0) {
                    // Do all the facility declaration logic, but don't add this
                    // to our incomplete assertive code stack. We shouldn't need to
                    // verify facility declarations that are imported.
                    FacilityDec facDec = (FacilityDec) s.toFacilityEntry(dec.getLocation()).getDefiningElement();

                    // Create a new model for this assertive code block
                    ST blockModel = mySTGroup.getInstanceOf("outputAssertiveCodeBlock");
                    blockModel.add("blockName", dec.getName());

                    FacilityDeclRule ruleApplication = new FacilityDeclRule(facDec, false, myBuilder,
                            myCurrentModuleScope, new AssertiveCodeBlock(facDec.getName(), facDec, myTypeGraph),
                            myCurrentVerificationContext, mySTGroup, blockModel);
                    ruleApplication.applyRule();

                    // Store this facility's InstantiatedFacilityDecl for future use
                    myCurrentVerificationContext
                            .storeInstantiatedFacilityDecl(ruleApplication.getInstantiatedFacilityDecl());

                    // Store all requires/constraint from the imported concept
                    PosSymbol conceptName = facDec.getConceptName();
                    ModuleIdentifier coId = new ModuleIdentifier(conceptName.getName());
                    myCurrentVerificationContext.storeConceptAssertionClauses(conceptName.getLocation(), coId, true);

                    // Store all requires/constraint from the imported concept realization
                    // if it is not externally realized
                    if (!facDec.getExternallyRealizedFlag()) {
                        PosSymbol conceptRealizName = facDec.getConceptRealizName();
                        ModuleIdentifier coRealizId = new ModuleIdentifier(conceptRealizName.getName());
                        myCurrentVerificationContext.storeConceptRealizAssertionClauses(conceptRealizName.getLocation(),
                                coRealizId, true);
                    }

                    for (EnhancementSpecRealizItem specRealizItem : facDec.getEnhancementRealizPairs()) {
                        // Store all requires/constraint from the imported enhancement(s)
                        PosSymbol enhancementName = specRealizItem.getEnhancementName();
                        ModuleIdentifier enId = new ModuleIdentifier(enhancementName.getName());
                        myCurrentVerificationContext.storeEnhancementAssertionClauses(enhancementName.getLocation(),
                                enId, true);

                        // Store all requires/constraint from the imported enhancement realization(s)
                        PosSymbol enhancementRealizName = specRealizItem.getEnhancementRealizName();
                        ModuleIdentifier enRealizId = new ModuleIdentifier(enhancementRealizName.getName());
                        myCurrentVerificationContext.storeEnhancementRealizAssertionClauses(
                                enhancementRealizName.getLocation(), enRealizId, true);
                    }
                }
            }
        } catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(dec.getLocation());
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
    public final void postModuleDec(ModuleDec dec) {
        // Loop through our incomplete assertive code blocks until it is empty
        while (!myIncompleteAssertiveCodeBlocks.isEmpty()) {
            // Use the first assertive code block in the incomplete blocks list
            // as our current assertive code block.
            myCurrentAssertiveCodeBlock = myIncompleteAssertiveCodeBlocks.removeFirst();

            applyStatementRules(myCurrentAssertiveCodeBlock);

            // Render the assertive block model
            ST blockModel = myAssertiveCodeBlockModels.remove(myCurrentAssertiveCodeBlock);
            myVCGenDetailsModel.add("assertiveCodeBlocks", blockModel.render());

            // Add this to our final assertive code block list
            myFinalAssertiveCodeBlocks.add(myCurrentAssertiveCodeBlock);

            // Set the current assertive code block to null
            myCurrentAssertiveCodeBlock = null;
        }

        // Assign a name to all of the VCs
        int blockCount = 0;
        for (AssertiveCodeBlock block : myFinalAssertiveCodeBlocks) {
            // Obtain the final list of vcs
            int vcCount = 1;
            List<VerificationCondition> vcs = block.getVCs();
            List<VerificationCondition> namedVCs = new ArrayList<>(vcs.size());
            for (VerificationCondition vc : vcs) {
                namedVCs.add(new VerificationCondition(vc.getLocation(), blockCount + "_" + vcCount, vc.getSequent(),
                        vc.getHasImpactingReductionFlag(), vc.getLocationDetailModel()));
                vcCount++;
            }

            // Store the named VCs and increase the block number
            block.setVCs(namedVCs);

            // YS: Only increment the block count if the current block has VCs to prove
            if (!namedVCs.isEmpty()) {
                blockCount++;
            }
        }
    }

    // -----------------------------------------------------------
    // Concept Module
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link ConceptModuleDec}.
     * </p>
     *
     * @param concept
     *            A concept module declaration.
     */
    @Override
    public final void preConceptModuleDec(ConceptModuleDec concept) {
        PosSymbol conceptName = concept.getName();

        // Store the concept requires and constraint clauses
        myCurrentVerificationContext.storeConceptAssertionClauses(conceptName.getLocation(),
                new ModuleIdentifier(conceptName.getName()), false);

        // Add to VC detail model
        ST header = mySTGroup.getInstanceOf("outputConceptHeader").add("conceptName", conceptName.getName());
        myVCGenDetailsModel.add("fileHeader", header.render());
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
        PosSymbol conceptRealizName = conceptRealization.getName();

        // Store the concept realization requires clause
        myCurrentVerificationContext.storeConceptRealizAssertionClauses(conceptRealizName.getLocation(),
                new ModuleIdentifier(conceptRealizName.getName()), false);

        // Store all requires/constraint from the imported concept
        PosSymbol conceptName = conceptRealization.getConceptName();
        ModuleIdentifier coId = new ModuleIdentifier(conceptName.getName());
        myCurrentVerificationContext.storeConceptAssertionClauses(conceptName.getLocation(), coId, false);

        // Store all the shared states declared in the concept
        myCurrentVerificationContext.storeConceptSharedStateDecs(conceptName.getLocation(), coId);

        // Store all the type families declared in the concept
        myCurrentVerificationContext.storeConceptTypeFamilyDecs(conceptName.getLocation(), coId);

        // Add to VC detail model
        ST header = mySTGroup.getInstanceOf("outputConceptRealizHeader").add("realizName", conceptRealizName.getName())
                .add("conceptName", conceptName.getName());
        myVCGenDetailsModel.add("fileHeader", header.render());
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
        PosSymbol enhancementName = enhancement.getName();

        // Store the enhancement requires clause
        myCurrentVerificationContext.storeEnhancementAssertionClauses(enhancementName.getLocation(),
                new ModuleIdentifier(enhancementName.getName()), false);

        // Store all requires/constraint from the imported concept
        PosSymbol conceptName = enhancement.getConceptName();
        ModuleIdentifier coId = new ModuleIdentifier(conceptName.getName());
        myCurrentVerificationContext.storeConceptAssertionClauses(conceptName.getLocation(), coId, false);

        // Store all the shared states declared in the concept
        myCurrentVerificationContext.storeConceptSharedStateDecs(conceptName.getLocation(), coId);

        // Store all the type families declared in the concept
        myCurrentVerificationContext.storeConceptTypeFamilyDecs(conceptName.getLocation(), coId);

        // Add to VC detail model
        ST header = mySTGroup.getInstanceOf("outputEnhancementHeader").add("enhancementName", enhancementName.getName())
                .add("conceptName", conceptName.getName());
        myVCGenDetailsModel.add("fileHeader", header.render());
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
        PosSymbol enhancementRealizName = enhancementRealization.getName();

        // Store the enhancement realization requires clause
        myCurrentVerificationContext.storeEnhancementRealizAssertionClauses(enhancementRealizName.getLocation(),
                new ModuleIdentifier(enhancementRealizName.getName()), false);

        // Store all requires/constraint from the imported concept
        PosSymbol conceptName = enhancementRealization.getConceptName();
        ModuleIdentifier coId = new ModuleIdentifier(conceptName.getName());
        myCurrentVerificationContext.storeConceptAssertionClauses(conceptName.getLocation(), coId, false);

        // Store all the shared states declared in the concept
        myCurrentVerificationContext.storeConceptSharedStateDecs(conceptName.getLocation(), coId);

        // Store all the type families declared in the concept
        myCurrentVerificationContext.storeConceptTypeFamilyDecs(conceptName.getLocation(), coId);

        // Store all requires/constraint from the imported enhancement
        PosSymbol enhancementName = enhancementRealization.getEnhancementName();
        ModuleIdentifier enId = new ModuleIdentifier(enhancementName.getName());
        myCurrentVerificationContext.storeEnhancementAssertionClauses(enhancementName.getLocation(), enId, false);

        // Add to VC detail model
        ST header = mySTGroup.getInstanceOf("outputEnhancementRealizHeader")
                .add("realizName", enhancementRealizName.getName()).add("enhancementName", enhancementName.getName())
                .add("conceptName", conceptName.getName());
        myVCGenDetailsModel.add("fileHeader", header.render());
    }

    // -----------------------------------------------------------
    // Facility Module
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link FacilityModuleDec}.
     * </p>
     *
     * @param facility
     *            A concept module declaration.
     */
    @Override
    public final void preFacilityModuleDec(FacilityModuleDec facility) {
        PosSymbol facilityName = facility.getName();

        // Store the facility requires clause
        myCurrentVerificationContext.storeFacilityModuleAssertionClauses(facilityName.getLocation(),
                new ModuleIdentifier(facilityName.getName()));

        // Add to VC detail model
        ST header = mySTGroup.getInstanceOf("outputFacilityHeader").add("facilityName", facilityName.getName());
        myVCGenDetailsModel.add("fileHeader", header.render());
    }

    // -----------------------------------------------------------
    // Facility-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting a {@link FacilityDec}.
     * </p>
     *
     * @param dec
     *            A facility declaration.
     */
    @Override
    public final void postFacilityDec(FacilityDec dec) {
        // Create a new assertive code block
        myCurrentAssertiveCodeBlock = new AssertiveCodeBlock(dec.getName(), dec, myTypeGraph);

        // Add shared variables in scope to the free variable's list
        addSharedVarsToFreeVariableList(myCurrentAssertiveCodeBlock);

        // Create the top most level assume statement and
        // add it to the assertive code block as the first statement
        AssumeStmt topLevelAssumeStmt = new AssumeStmt(dec.getLocation().clone(),
                myCurrentVerificationContext.createTopLevelAssumeExpFromContext(dec.getLocation(), false, false),
                false);
        myCurrentAssertiveCodeBlock.addStatement(topLevelAssumeStmt);

        // Create a new model for this assertive code block
        ST blockModel = mySTGroup.getInstanceOf("outputAssertiveCodeBlock");
        blockModel.add("blockName", dec.getName());

        // Apply facility declaration rule
        FacilityDeclRule declRule = new FacilityDeclRule(dec, true, myBuilder, myCurrentModuleScope,
                myCurrentAssertiveCodeBlock, myCurrentVerificationContext, mySTGroup, blockModel);
        declRule.applyRule();

        // Store this facility's InstantiatedFacilityDecl for future use
        myCurrentVerificationContext.storeInstantiatedFacilityDecl(declRule.getInstantiatedFacilityDecl());

        // Update the current assertive code block and its associated block model.
        myCurrentAssertiveCodeBlock = declRule.getAssertiveCodeBlocks().getFirst();
        myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock, declRule.getBlockModel());

        // Add this as a new incomplete assertive code block
        myIncompleteAssertiveCodeBlocks.add(myCurrentAssertiveCodeBlock);
    }

    // -----------------------------------------------------------
    // Operation-Related
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
        // Store the associated OperationEntry for future use
        List<PTType> argTypes = new LinkedList<>();
        for (ParameterVarDec p : dec.getWrappedOpDec().getParameters()) {
            argTypes.add(p.getTy().getProgramType());
        }
        OperationEntry correspondingOperation = Utilities.searchOperation(dec.getLocation(), null, dec.getName(),
                argTypes, ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_IGNORE, myCurrentModuleScope);

        // TODO: Add the performance logic
        // Obtain the performance duration clause
        /*
         * if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) { myCurrentOperationProfileEntry =
         * Utilities.searchOperationProfile(dec.getLocation(), null, dec.getName(), argTypes, myCurrentModuleScope); }
         */

        // Create a new assertive code block
        if (dec.getRecursive()) {
            // Store any decreasing clauses for future use
            myCurrentAssertiveCodeBlock = new AssertiveCodeBlock(dec.getName(), dec, correspondingOperation,
                    dec.getDecreasing().getAssertionExp(), myTypeGraph);
        } else {
            myCurrentAssertiveCodeBlock = new AssertiveCodeBlock(dec.getName(), dec, correspondingOperation,
                    myTypeGraph);
        }

        // Add shared variables in scope to the free variable's list
        addSharedVarsToFreeVariableList(myCurrentAssertiveCodeBlock);

        // Check to see if we are in a concept realization
        boolean inConceptRealiz = myCurrentModuleScope.getDefiningElement() instanceof ConceptRealizModuleDec;

        // Create the top most level assume statement, replace any facility formal
        // with actual and add it to the assertive code block as the first statement.
        Exp topLevelAssumeExp = createTopLevelAssumeExpForProcedureDec(dec.getLocation(), myCurrentAssertiveCodeBlock,
                correspondingOperation, false, false, inConceptRealiz, true);
        AssumeStmt topLevelAssumeStmt = new AssumeStmt(dec.getLocation().clone(), topLevelAssumeExp, false);
        myCurrentAssertiveCodeBlock.addStatement(topLevelAssumeStmt);

        // Create Remember statement
        MemoryStmt rememberStmt = new MemoryStmt(dec.getLocation().clone(), StatementType.REMEMBER);
        myCurrentAssertiveCodeBlock.addStatement(rememberStmt);

        // TODO: NY - Add any procedure duration clauses

        // Create a new model for this assertive code block
        ST blockModel = mySTGroup.getInstanceOf("outputAssertiveCodeBlock");
        blockModel.add("blockName", dec.getName());
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", "Procedure Declaration Rule (Part 1)").add("currentStateOfBlock",
                myCurrentAssertiveCodeBlock);
        blockModel.add("vcGenSteps", stepModel.render());
        myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock, blockModel);
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
        // Apply procedure declaration rule
        OperationDec wrappedOpDec = dec.getWrappedOpDec();
        ProcedureDec procedureDec = new ProcedureDec(dec.getName(), wrappedOpDec.getParameters(),
                wrappedOpDec.getReturnTy(), wrappedOpDec.getAffectedVars(), dec.getDecreasing(), dec.getFacilities(),
                dec.getVariables(), dec.getStatements(), dec.getRecursive());
        ProcedureDeclRule declRule = new ProcedureDeclRule(procedureDec, myVariableTypeEntries, myBuilder,
                myCurrentModuleScope, myCurrentAssertiveCodeBlock, myCurrentVerificationContext, mySTGroup,
                myAssertiveCodeBlockModels.remove(myCurrentAssertiveCodeBlock));
        declRule.applyRule();

        // Update the current assertive code block and its associated block model.
        myCurrentAssertiveCodeBlock = declRule.getAssertiveCodeBlocks().getFirst();
        myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock, declRule.getBlockModel());

        // Add this as a new incomplete assertive code block
        myIncompleteAssertiveCodeBlocks.add(myCurrentAssertiveCodeBlock);

        myVariableTypeEntries.clear();
        myCurrentAssertiveCodeBlock = null;
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
        // Store the associated OperationEntry for future use
        List<PTType> argTypes = new LinkedList<>();
        for (ParameterVarDec p : dec.getParameters()) {
            // YS: If type is a PTRepresentation, we want the type family
            // it is pointing to. Not the realization type.
            PTType type = p.getTy().getProgramType();
            if (type instanceof PTRepresentation) {
                type = ((PTRepresentation) type).getFamily().getProgramType();
            }

            argTypes.add(type);
        }
        OperationEntry correspondingOperation = Utilities.searchOperation(dec.getLocation(), null, dec.getName(),
                argTypes, ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_IGNORE, myCurrentModuleScope);

        // TODO: Add the performance logic
        // Obtain the performance duration clause
        /*
         * if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) { myCurrentOperationProfileEntry =
         * Utilities.searchOperationProfile(dec.getLocation(), null, dec.getName(), argTypes, myCurrentModuleScope); }
         */

        // Check to see if we are in a concept realization
        boolean inConceptRealiz = myCurrentModuleScope.getDefiningElement() instanceof ConceptRealizModuleDec;

        // Check to see if this a local operation
        boolean isLocal = Utilities.isLocationOperation(dec.getName().getName(), myCurrentModuleScope);

        // Create a new assertive code block
        if (dec.getRecursive()) {
            // Store any decreasing clauses for future use
            myCurrentAssertiveCodeBlock = new AssertiveCodeBlock(dec.getName(), dec, correspondingOperation,
                    dec.getDecreasing().getAssertionExp(), myTypeGraph);
        } else {
            myCurrentAssertiveCodeBlock = new AssertiveCodeBlock(dec.getName(), dec, correspondingOperation,
                    myTypeGraph);
        }

        // Add shared variables in scope to the free variable's list
        addSharedVarsToFreeVariableList(myCurrentAssertiveCodeBlock);

        // Create the top most level assume statement, replace any facility formal
        // with actual and add it to the assertive code block as the first statement.
        Exp topLevelAssumeExp = createTopLevelAssumeExpForProcedureDec(dec.getLocation(), myCurrentAssertiveCodeBlock,
                correspondingOperation, !isLocal, !isLocal, inConceptRealiz, isLocal);
        AssumeStmt topLevelAssumeStmt = new AssumeStmt(dec.getLocation().clone(), topLevelAssumeExp, false);
        myCurrentAssertiveCodeBlock.addStatement(topLevelAssumeStmt);

        // Create Remember statement
        MemoryStmt rememberStmt = new MemoryStmt(dec.getLocation().clone(), StatementType.REMEMBER);
        myCurrentAssertiveCodeBlock.addStatement(rememberStmt);

        // TODO: NY - Add any procedure duration clauses

        // Create a new model for this assertive code block
        ST blockModel = mySTGroup.getInstanceOf("outputAssertiveCodeBlock");
        blockModel.add("blockName", dec.getName());
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", "Procedure Declaration Rule (Part 1)").add("currentStateOfBlock",
                myCurrentAssertiveCodeBlock);
        blockModel.add("vcGenSteps", stepModel.render());
        myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock, blockModel);
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
        // Apply procedure declaration rule
        ProcedureDeclRule declRule = new ProcedureDeclRule(dec, myVariableTypeEntries, myBuilder, myCurrentModuleScope,
                myCurrentAssertiveCodeBlock, myCurrentVerificationContext, mySTGroup,
                myAssertiveCodeBlockModels.remove(myCurrentAssertiveCodeBlock));
        declRule.applyRule();

        // Update the current assertive code block and its associated block model.
        myCurrentAssertiveCodeBlock = declRule.getAssertiveCodeBlocks().getFirst();
        myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock, declRule.getBlockModel());

        // Add this as a new incomplete assertive code block
        myIncompleteAssertiveCodeBlocks.add(myCurrentAssertiveCodeBlock);

        myVariableTypeEntries.clear();
        myCurrentAssertiveCodeBlock = null;
    }

    // -----------------------------------------------------------
    // Shared State/Realization-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting a {@link SharedStateDec}.
     * </p>
     *
     * @param dec
     *            A shared state declared in a {@code Concept}.
     */
    @Override
    public final void postSharedStateDec(SharedStateDec dec) {
        myCurrentVerificationContext.storeConceptSharedStateDec(dec);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link SharedStateRealizationDec}.
     * </p>
     *
     * @param dec
     *            A shared state realization in a {@code Concept Realization}.
     */
    @Override
    public final void preSharedStateRealizationDec(SharedStateRealizationDec dec) {
        // Obtain the concept module for this concept realization
        ConceptRealizModuleDec conceptRealizModuleDec = (ConceptRealizModuleDec) myCurrentModuleScope
                .getDefiningElement();
        try {
            // Obtain the concept module for this concept realization
            ConceptModuleDec conceptModuleDec = (ConceptModuleDec) myBuilder
                    .getModuleScope(new ModuleIdentifier(conceptRealizModuleDec.getConceptName().getName()))
                    .getDefiningElement();

            // Locate the corresponding shared state
            ConceptSharedStateExtractor sharedStateExtractor = new ConceptSharedStateExtractor();
            TreeWalker.visit(sharedStateExtractor, conceptModuleDec);

            // Store the corresponding shared state declaration
            myCorrespondingSharedStateDec = sharedStateExtractor.getSharedStateDecs().get(0);
        } catch (NoSuchSymbolException e) {
            Utilities.noSuchModule(conceptRealizModuleDec.getConceptName().getLocation());
        }

        myRealizInitFinalOuterDec = dec;
    }

    /**
     * <p>
     * Code that gets executed in between visiting items inside {@link SharedStateRealizationDec}.
     * </p>
     *
     * @param dec
     *            A shared state realization in a {@code Concept Realization}.
     * @param prevChild
     *            The previous child item visited.
     * @param nextChild
     *            The next child item to be visited.
     */
    @Override
    public final void midSharedStateRealizationDec(SharedStateRealizationDec dec, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        // Apply the well defined correspondence rule after we finish walking it!
        if (prevChild instanceof AssertionClause
                && ((AssertionClause) prevChild).getClauseType().equals(AssertionClause.ClauseType.CORRESPONDENCE)) {
            // Create a new assertive code block
            AssertiveCodeBlock block = new AssertiveCodeBlock(dec.getName(), dec, myTypeGraph);

            // Add shared variables in scope to the free variable's list
            addSharedVarsToFreeVariableList(block);

            // Create a new model for this assertive code block
            ST blockModel = mySTGroup.getInstanceOf("outputAssertiveCodeBlock");
            blockModel.add("blockName", dec.getName());

            // Apply well defined correspondence rule for concept shared variable realizations
            SharedStateCorrRule declRule = new SharedStateCorrRule(dec, myCorrespondingSharedStateDec, myBuilder, block,
                    myCurrentVerificationContext, mySTGroup, blockModel);
            declRule.applyRule();

            // Update the current assertive code blocks and its associated block model.
            block = declRule.getAssertiveCodeBlocks().getFirst();
            myAssertiveCodeBlockModels.put(block, declRule.getBlockModel());

            // Add this as a new incomplete assertive code block
            myIncompleteAssertiveCodeBlocks.add(block);
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link SharedStateRealizationDec}.
     * </p>
     *
     * @param dec
     *            A shared state realization in a {@code Concept Realization}.
     */
    @Override
    public final void postSharedStateRealizationDec(SharedStateRealizationDec dec) {
        myCurrentVerificationContext.storeLocalSharedRealizationDec(dec);
        myRealizInitFinalOuterDec = null;
    }

    // -----------------------------------------------------------
    // Type Family/Representation-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting an {@link AbstractTypeRepresentationDec}.
     * </p>
     *
     * @param dec
     *            A type representation declaration.
     */
    @Override
    public final void postAbstractTypeRepresentationDec(AbstractTypeRepresentationDec dec) {
        myCurrentVerificationContext.storeLocalTypeRepresentationDec(dec);
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
        myCurrentVerificationContext.storeConceptTypeFamilyDec(dec);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link TypeRepresentationDec}.
     * </p>
     *
     * @param dec
     *            A type representation declared in a {@code Concept Realization}.
     */
    @Override
    public final void preTypeRepresentationDec(TypeRepresentationDec dec) {
        myRealizInitFinalOuterDec = dec;

        // TODO - YS: Remove this once we include proof rules for handling dependent correspondence.
        AssertionClause correspondence = dec.getCorrespondence();
        AssertionClause.ClauseType clauseType = dec.getCorrespondence().getClauseType();
        if (clauseType.equals(AssertionClause.ClauseType.DEPENDENT_CORRESPONDENCE)) {
            throw new SourceErrorException("[VCGenerator] Dependent correspondences are currently not supported.",
                    correspondence.getLocation());
        }
    }

    /**
     * <p>
     * Code that gets executed in between visiting items inside {@link TypeRepresentationDec}.
     * </p>
     *
     * @param dec
     *            A type representation declared in a {@code Concept Realization}.
     * @param prevChild
     *            The previous child item visited.
     * @param nextChild
     *            The next child item to be visited.
     */
    @Override
    public final void midTypeRepresentationDec(TypeRepresentationDec dec, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        // Apply the well defined correspondence rule after we finish walking it!
        if (prevChild instanceof AssertionClause
                && ((AssertionClause) prevChild).getClauseType().equals(AssertionClause.ClauseType.CORRESPONDENCE)) {
            // Create a new assertive code block
            AssertiveCodeBlock block = new AssertiveCodeBlock(dec.getName(), dec, myTypeGraph);

            // Add shared variables in scope to the free variable's list
            addSharedVarsToFreeVariableList(block);

            // Create a new model for this assertive code block
            ST blockModel = mySTGroup.getInstanceOf("outputAssertiveCodeBlock");
            blockModel.add("blockName", dec.getName());

            // Apply well defined correspondence rule for concept type realizations
            TypeRepresentationCorrRule declRule = new TypeRepresentationCorrRule(dec, myBuilder, block,
                    myCurrentVerificationContext, mySTGroup, blockModel);
            declRule.applyRule();

            // Update the current assertive code blocks and its associated block model.
            block = declRule.getAssertiveCodeBlocks().getFirst();
            myAssertiveCodeBlockModels.put(block, declRule.getBlockModel());

            // Add this as a new incomplete assertive code block
            myIncompleteAssertiveCodeBlocks.add(block);
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link TypeRepresentationDec}.
     * </p>
     *
     * @param dec
     *            A type representation declared in a {@code Concept Realization}.
     */
    @Override
    public final void postTypeRepresentationDec(TypeRepresentationDec dec) {
        myRealizInitFinalOuterDec = null;
    }

    // -----------------------------------------------------------
    // Initialization and Finalization-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link RealizInitFinalItem}.
     * </p>
     *
     * @param item
     *            A realization for an initialization or finalization block.
     */
    @Override
    public final void preRealizInitFinalItem(RealizInitFinalItem item) {
        // Create a new assertive code block
        myCurrentAssertiveCodeBlock = new AssertiveCodeBlock(myRealizInitFinalOuterDec.getName(), item, myTypeGraph);

        // Add shared variables in scope to the free variable's list
        addSharedVarsToFreeVariableList(myCurrentAssertiveCodeBlock);

        // For type representation's initialization block, we will need to declare and
        // initialize a variable with the exemplar as its name and the representation type
        // as its programming type.
        if (myRealizInitFinalOuterDec instanceof TypeRepresentationDec) {
            if (item.getItemType().equals(AbstractInitFinalItem.ItemType.INITIALIZATION)) {
                // Create the top most level assume statement and
                // add it to the assertive code block as the first statement
                Exp topLevelAssumeExp = createTopLevelAssumeExpForRealizInitFinalItem(item.getLocation(), false);
                AssumeStmt topLevelAssumeStmt = new AssumeStmt(item.getLocation().clone(), topLevelAssumeExp, false);
                myCurrentAssertiveCodeBlock.addStatement(topLevelAssumeStmt);

                // Create Remember statement
                MemoryStmt rememberStmt = new MemoryStmt(item.getLocation().clone(), StatementType.REMEMBER);
                myCurrentAssertiveCodeBlock.addStatement(rememberStmt);

                // Obtain the associated type family declaration
                TypeRepresentationDec typeRepresentationDec = (TypeRepresentationDec) myRealizInitFinalOuterDec;
                TypeFamilyDec typeFamilyDec = Utilities.getAssociatedTypeFamilyDec(typeRepresentationDec,
                        myCurrentVerificationContext);

                // Query for the type representation entry in the symbol table.
                SymbolTableEntry symbolTableEntry = Utilities.searchProgramType(typeRepresentationDec.getLocation(),
                        null, typeRepresentationDec.getName(), myCurrentModuleScope);

                // YS: Simply create the proper variable initialization statement that
                // allow us to deal with generating question mark variables
                // and duration logic when we backtrack through the code.
                myCurrentAssertiveCodeBlock.addStatement(new InitializeVarStmt(
                        new VarDec(typeFamilyDec.getExemplar(), typeRepresentationDec.getRepresentation()),
                        symbolTableEntry, false));

                // TODO: NY - Add any variable initialization duration clauses
            } else {
                // Create the top most level assume statement and
                // add it to the assertive code block as the first statement
                Exp topLevelAssumeExp = createTopLevelAssumeExpForRealizInitFinalItem(item.getLocation(), true);
                AssumeStmt topLevelAssumeStmt = new AssumeStmt(item.getLocation().clone(), topLevelAssumeExp, false);
                myCurrentAssertiveCodeBlock.addStatement(topLevelAssumeStmt);

                // Create Remember statement
                MemoryStmt rememberStmt = new MemoryStmt(item.getLocation().clone(), StatementType.REMEMBER);
                myCurrentAssertiveCodeBlock.addStatement(rememberStmt);
            }
        } else {
            // Create the top most level assume statement and
            // add it to the assertive code block as the first statement
            Exp topLevelAssumeExp = createTopLevelAssumeExpForRealizInitFinalItem(item.getLocation(), false);
            AssumeStmt topLevelAssumeStmt = new AssumeStmt(item.getLocation().clone(), topLevelAssumeExp, false);
            myCurrentAssertiveCodeBlock.addStatement(topLevelAssumeStmt);

            if (item.getItemType().equals(AbstractInitFinalItem.ItemType.INITIALIZATION)) {
                // YS: For each instantiated facility declaration, we generate a facility initialization
                // statement. This will allow us to obtain the proper facility initialization ensures
                // and prove our own initialization.
                List<InstantiatedFacilityDecl> instantiatedFacilityDecls = myCurrentVerificationContext
                        .getProcessedInstFacilityDecls();
                for (InstantiatedFacilityDecl decl : instantiatedFacilityDecls) {
                    // Only need to deal with local facility declarations.
                    if (decl.isLocalFacility()) {
                        myCurrentAssertiveCodeBlock
                                .addStatement(new FacilityInitStmt(decl.getInstantiatedFacilityDec()));
                    }
                }

                // Create Remember statement
                MemoryStmt rememberStmt = new MemoryStmt(item.getLocation().clone(), StatementType.REMEMBER);
                myCurrentAssertiveCodeBlock.addStatement(rememberStmt);

                // For each shared variable, we query for the its programming type entry
                // in the symbol table.
                SharedStateRealizationDec sharedStateRealizationDec = (SharedStateRealizationDec) myRealizInitFinalOuterDec;
                for (VarDec varDec : sharedStateRealizationDec.getStateVars()) {
                    NameTy nameTy = (NameTy) varDec.getTy();
                    SymbolTableEntry symbolTableEntry = Utilities.searchProgramType(varDec.getLocation(), null,
                            nameTy.getName(), myCurrentModuleScope);

                    // YS: Simply create the proper variable initialization statement that
                    // allow us to deal with generating question mark variables
                    // and duration logic when we backtrack through the code.
                    myCurrentAssertiveCodeBlock.addStatement(new InitializeVarStmt(varDec, symbolTableEntry, false));

                    // TODO: NY - Add any variable initialization duration clauses
                }
            }
        }

        // Create the proper name for the block
        String blockName;
        if (item.getItemType().equals(AbstractInitFinalItem.ItemType.INITIALIZATION)) {
            blockName = "Initialization of ";
        } else {
            blockName = "Finalization of ";
        }

        // Create a new model for this assertive code block
        ST blockModel = mySTGroup.getInstanceOf("outputAssertiveCodeBlock");
        blockModel.add("blockName", blockName + myRealizInitFinalOuterDec.getName());
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", blockName + myRealizInitFinalOuterDec.getName() + " (Setup)")
                .add("currentStateOfBlock", myCurrentAssertiveCodeBlock);
        blockModel.add("vcGenSteps", stepModel.render());
        myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock, blockModel);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link RealizInitFinalItem}.
     * </p>
     *
     * @param item
     *            A realization for an initialization or finalization block.
     */
    @Override
    public final void postRealizInitFinalItem(RealizInitFinalItem item) {
        // Generate the proper item block declaration rule.
        ProofRuleApplication ruleApplication;
        if (myRealizInitFinalOuterDec instanceof TypeRepresentationDec) {
            TypeRepresentationDec typeRepresentationDec = (TypeRepresentationDec) myRealizInitFinalOuterDec;

            // Generate a new type initialization rule application.
            if (item.getItemType().equals(AbstractInitFinalItem.ItemType.INITIALIZATION)) {
                ruleApplication = new TypeRepresentationInitRule(typeRepresentationDec, myVariableTypeEntries,
                        myBuilder, myCurrentModuleScope, myCurrentAssertiveCodeBlock, myCurrentVerificationContext,
                        mySTGroup, myAssertiveCodeBlockModels.remove(myCurrentAssertiveCodeBlock));
            }
            // Generate a new type finalization rule application.
            else {
                ruleApplication = new TypeRepresentationFinalRule(typeRepresentationDec, myVariableTypeEntries,
                        myBuilder, myCurrentModuleScope, myCurrentAssertiveCodeBlock, myCurrentVerificationContext,
                        mySTGroup, myAssertiveCodeBlockModels.remove(myCurrentAssertiveCodeBlock));
            }
        } else {
            SharedStateRealizationDec sharedStateRealizationDec = (SharedStateRealizationDec) myRealizInitFinalOuterDec;

            // Generate a new shared variable initialization rule application.
            ruleApplication = new SharedStateRepresentationInitRule(sharedStateRealizationDec,
                    myCorrespondingSharedStateDec, myVariableTypeEntries, myBuilder, myCurrentModuleScope,
                    myCurrentAssertiveCodeBlock, myCurrentVerificationContext, mySTGroup,
                    myAssertiveCodeBlockModels.remove(myCurrentAssertiveCodeBlock));
        }

        // Apply the proof rule
        ruleApplication.applyRule();

        // Update the current assertive code block and its associated block model.
        myCurrentAssertiveCodeBlock = ruleApplication.getAssertiveCodeBlocks().getFirst();
        myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock, ruleApplication.getBlockModel());

        // Add this as a new incomplete assertive code block
        myIncompleteAssertiveCodeBlocks.add(myCurrentAssertiveCodeBlock);

        myVariableTypeEntries.clear();
        myCurrentAssertiveCodeBlock = null;
    }

    // -----------------------------------------------------------
    // Variable Declaration-Related
    // -----------------------------------------------------------

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
        // Ty should always be a NameTy
        if (dec.getTy() instanceof NameTy) {
            NameTy nameTy = (NameTy) dec.getTy();

            // Query for the type entry in the symbol table
            SymbolTableEntry ste = Utilities.searchProgramType(nameTy.getLocation(), nameTy.getQualifier(),
                    nameTy.getName(), myCurrentModuleScope);
            ProgramTypeEntry typeEntry;
            if (ste instanceof ProgramTypeEntry) {
                typeEntry = ste.toProgramTypeEntry(nameTy.getLocation());
            } else {
                // YS: This also includes any local type representations with
                // no corresponding type family (FacilityTypeRepresentationEntry)
                typeEntry = ste.toTypeRepresentationEntry(nameTy.getLocation()).getDefiningTypeEntry();
            }

            // Check to see if the variable's type is known or it is generic.
            boolean isGenericVar = true;
            if ((typeEntry.getDefiningElement() instanceof TypeFamilyDec)
                    || (typeEntry.getDefiningElement() instanceof FacilityTypeRepresentationDec)) {
                // The program type has an associated TypeFamilyDec or it is a local
                // program type, therefore it is not generic.
                isGenericVar = false;

                // Store the symbol table entry for this variable for
                // when we deal with finalization.
                myVariableTypeEntries.put(dec, ste);
            }

            // YS: Simply create the proper variable initialization statement that
            // allow us to deal with generating question mark variables
            // and duration logic when we backtrack through the code.
            ST blockModel = myAssertiveCodeBlockModels.remove(myCurrentAssertiveCodeBlock);
            myCurrentAssertiveCodeBlock.addStatement(new InitializeVarStmt(dec, ste, isGenericVar));

            // Add this as a free variable
            myCurrentAssertiveCodeBlock.addFreeVar(
                    Utilities.createVarExp(dec.getLocation(), null, dec.getName(), dec.getMathType(), null));

            // Update the associated block model.
            myAssertiveCodeBlockModels.put(myCurrentAssertiveCodeBlock, blockModel);
        } else {
            // Shouldn't be possible but just in case it ever happens
            // by accident.
            Utilities.tyNotHandled(dec.getTy(), dec.getLocation());
        }
    }

    // -----------------------------------------------------------
    // Raw Type-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * This method redefines how a {@link RecordTy} should be walked.
     * </p>
     *
     * @param ty
     *            A raw record type.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkRecordTy(RecordTy ty) {
        preAny(ty);
        preTy(ty);
        preRecordTy(ty);

        // YS: Don't walk any variable declarations inside the record.
        // Logic for dealing with those variables are handled by
        // RealizInitFinalItem.

        postRecordTy(ty);
        postTy(ty);
        postAny(ty);

        return true;
    }

    // -----------------------------------------------------------
    // Other
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting an {@link AssertionClause}.
     * </p>
     *
     * @param clause
     *            An assertion clause declaration.
     */
    @Override
    public final void postAssertionClause(AssertionClause clause) {
        if (clause.getWhichEntailsExp() != null) {
            // Create a new assertive code block
            PosSymbol name = new PosSymbol(clause.getWhichEntailsExp().getLocation().clone(),
                    "Which_Entails Expression Located at " + clause.getWhichEntailsExp().getLocation());
            AssertiveCodeBlock block = new AssertiveCodeBlock(name, clause, myTypeGraph);

            // Add shared variables in scope to the free variable's list
            addSharedVarsToFreeVariableList(block);

            // Create a new model for this assertive code block
            ST blockModel = mySTGroup.getInstanceOf("outputAssertiveCodeBlock");
            blockModel.add("blockName", name);

            // Apply which_entails rule
            WhichEntailsRule entailsRule = new WhichEntailsRule(clause, block, myCurrentVerificationContext, mySTGroup,
                    blockModel);
            entailsRule.applyRule();

            // Update the current assertive code blocks and its associated block model.
            block = entailsRule.getAssertiveCodeBlocks().getFirst();
            myAssertiveCodeBlockModels.put(block, entailsRule.getBlockModel());

            // Add this as a new incomplete assertive code block
            myIncompleteAssertiveCodeBlocks.add(block);
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the final {@link AssertiveCodeBlock AssertiveCodeBlocks} containing the generated
     * {@link Sequent Sequents}.
     * </p>
     *
     * @return A list containing {@link AssertiveCodeBlock AssertiveCodeBlocks}.
     */
    public final List<AssertiveCodeBlock> getFinalAssertiveCodeBlocks() {
        return myFinalAssertiveCodeBlocks;
    }

    /**
     * <p>
     * This method returns the verbose mode output with how we generated the {@code VCs} for this {@link ModuleDec}.
     * </p>
     *
     * @return A string containing lots of details.
     */
    public final String getVerboseModeOutput() {
        return myVCGenDetailsModel.render();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method that adds the operation's type constraints.
     * </p>
     *
     * @param loc
     *            The location in the AST that we are currently visiting.
     * @param exp
     *            The top level assume expression we have built so far.
     * @param substitutionsMap
     *            A map of substitutions for any parameter constraints we generate.
     * @param scope
     *            The module scope to start our search.
     * @param currentBlock
     *            The current {@link AssertiveCodeBlock} we are currently generating.
     * @param entries
     *            List of operation's parameter entries.
     *
     * @return The original {@code exp} plus any operation parameter's type constraints.
     */
    private Exp addParamTypeConstraints(Location loc, Exp exp, Map<Exp, Exp> substitutionsMap, ModuleScope scope,
            AssertiveCodeBlock currentBlock, ImmutableList<ProgramParameterEntry> entries) {
        Exp retExp = exp;

        // Loop through each of the parameters in the operation entry.
        for (ProgramParameterEntry entry : entries) {
            ParameterVarDec parameterVarDec = (ParameterVarDec) entry.getDefiningElement();
            PTType declaredType = entry.getDeclaredType();
            ProgramParameterEntry.ParameterMode parameterMode = entry.getParameterMode();

            // Only deal with actual types and don't deal
            // with entry types passed in to the concept realization
            if (!(declaredType instanceof PTGeneric)) {
                // Query for the type entry in the symbol table
                NameTy nameTy = (NameTy) parameterVarDec.getTy();
                SymbolTableEntry ste = Utilities.searchProgramType(loc, nameTy.getQualifier(), nameTy.getName(), scope);

                ProgramTypeEntry typeEntry;
                if (ste instanceof ProgramTypeEntry) {
                    typeEntry = ste.toProgramTypeEntry(nameTy.getLocation());
                } else {
                    // YS: This also includes any local type representations with
                    // no corresponding type family (FacilityTypeRepresentationEntry)
                    typeEntry = ste.toTypeRepresentationEntry(nameTy.getLocation()).getDefiningTypeEntry();
                }

                // YS: Local program types do not have a constraint clauses,
                // therefore we skip the following logic.
                if (!(typeEntry.getDefiningElement() instanceof FacilityTypeRepresentationDec)) {
                    // Obtain the original dec from the AST
                    TypeFamilyDec typeFamilyDec = (TypeFamilyDec) typeEntry.getDefiningElement();

                    // Other than the replaces mode, constraints for the
                    // other parameter modes needs to be added
                    // to the requires clause as conjuncts.
                    if (parameterMode != ProgramParameterEntry.ParameterMode.REPLACES) {
                        if (!VarExp.isLiteralTrue(typeFamilyDec.getConstraint().getAssertionExp())) {
                            AssertionClause constraintClause = typeFamilyDec.getConstraint();
                            AssertionClause modifiedConstraintClause = Utilities.getTypeConstraintClause(
                                    constraintClause, loc, null, parameterVarDec.getName(), typeFamilyDec.getExemplar(),
                                    typeEntry.getModelType(), null);

                            // Replace any facility formal with actual
                            Exp constraintExp = modifiedConstraintClause.getAssertionExp();
                            constraintExp = Utilities.replaceFacilityFormalWithActual(constraintExp,
                                    Collections.singletonList(parameterVarDec), scope.getDefiningElement().getName(),
                                    myCurrentVerificationContext);

                            // Apply any pre-defined substitutions
                            constraintExp = constraintExp.substitute(substitutionsMap);

                            Exp whichEntailsExp = modifiedConstraintClause.getWhichEntailsExp();
                            if (whichEntailsExp != null) {
                                whichEntailsExp = Utilities.replaceFacilityFormalWithActual(whichEntailsExp,
                                        Collections.singletonList(parameterVarDec),
                                        scope.getDefiningElement().getName(), myCurrentVerificationContext);

                                // Apply any pre-defined substitutions
                                whichEntailsExp = whichEntailsExp.substitute(substitutionsMap);
                            }

                            modifiedConstraintClause = new AssertionClause(
                                    modifiedConstraintClause.getLocation().clone(),
                                    modifiedConstraintClause.getClauseType(), constraintExp, whichEntailsExp);

                            // Form a conjunct with the modified constraint clause and add
                            // the location detail associated with it.
                            Location constraintLoc = modifiedConstraintClause.getAssertionExp().getLocation();
                            retExp = Utilities.formConjunct(loc, retExp, modifiedConstraintClause,
                                    new LocationDetailModel(constraintLoc.clone(), constraintLoc.clone(),
                                            "Constraint Clause of " + parameterVarDec.getName()));
                        }
                    }
                }
            }

            // Add the current variable to our list of free variables
            currentBlock.addFreeVar(Utilities.createVarExp(parameterVarDec.getLocation(), null,
                    parameterVarDec.getName(), declaredType.toMath(), null));

        }

        return retExp;
    }

    /**
     * <p>
     * An helper method that adds the all the {@code Shared Variables} to the assertive code block's free variables
     * list.
     * </p>
     *
     * @param block
     *            An {@link AssertiveCodeBlock}.
     */
    private void addSharedVarsToFreeVariableList(AssertiveCodeBlock block) {
        // Add all shared variables to the free variables list.
        List<SharedStateDec> sharedStateDecs = myCurrentVerificationContext.getConceptSharedVars();
        for (SharedStateDec stateDec : sharedStateDecs) {
            for (MathVarDec varDec : stateDec.getAbstractStateVars()) {
                block.addFreeVar(Utilities.createVarExp(varDec.getLocation(), null, varDec.getName(),
                        varDec.getMathType(), null));
            }
        }

        // Add all facility instantiated shared variables to the free variables list.
        List<InstantiatedFacilityDecl> facilityDecls = myCurrentVerificationContext.getProcessedInstFacilityDecls();
        for (InstantiatedFacilityDecl facilityDecl : facilityDecls) {
            for (SharedStateDec stateDec : facilityDecl.getConceptSharedStates()) {
                for (MathVarDec varDec : stateDec.getAbstractStateVars()) {
                    block.addFreeVar(Utilities.createVarExp(varDec.getLocation(),
                            facilityDecl.getInstantiatedFacilityName(), varDec.getName(), varDec.getMathType(), null));
                }
            }
        }
    }

    /**
     * <p>
     * Applies each of the statement proof rules. After this call, we are done processing {@code assertiveCodeBlock}.
     * </p>
     *
     * @param assertiveCodeBlock
     *            An assertive block that we are trying apply the proof rules to the various {@link Statement
     *            Statements}.
     */
    private void applyStatementRules(AssertiveCodeBlock assertiveCodeBlock) {
        // Obtain the assertive code block model
        ST blockModel = myAssertiveCodeBlockModels.remove(assertiveCodeBlock);

        // Apply a statement proof rule to each of the assertions.
        while (assertiveCodeBlock.hasMoreStatements()) {
            // Work our way from the last statement
            Statement statement = assertiveCodeBlock.removeLastStatement();

            // Generate one of the statement proof rule applications
            ProofRuleApplication ruleApplication;
            if (statement instanceof AssumeStmt) {
                // Generate a new assume rule application.
                ruleApplication = new AssumeStmtRule((AssumeStmt) statement, assertiveCodeBlock,
                        myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof CallStmt) {
                // Generate a new call rule application.
                ruleApplication = new CallStmtRule((CallStmt) statement, myBuilder, myCurrentModuleScope,
                        assertiveCodeBlock, myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof ChangeStmt) {
                // Generate a new change rule application.
                ruleApplication = new ChangeStmtRule((ChangeStmt) statement, assertiveCodeBlock,
                        myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof ConfirmStmt) {
                // Generate a new confirm rule application.
                ruleApplication = new ConfirmStmtRule((ConfirmStmt) statement, assertiveCodeBlock,
                        myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof FacilityInitStmt) {
                // Generate a new facility initialization rule application.
                ruleApplication = new FacilityInitStmtRule((FacilityInitStmt) statement, myBuilder, assertiveCodeBlock,
                        myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof FinalizeVarStmt) {
                // Generate a new variable finalization rule application.
                ruleApplication = new FinalizeVarStmtRule((FinalizeVarStmt) statement, myBuilder, myCurrentModuleScope,
                        assertiveCodeBlock, myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof FuncAssignStmt) {
                // Generate a new function assignment rule application.
                ruleApplication = new FuncAssignStmtRule((FuncAssignStmt) statement, myBuilder, myCurrentModuleScope,
                        assertiveCodeBlock, myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof IfStmt) {
                // Generate a new if-else rule application.
                ruleApplication = new IfStmtRule((IfStmt) statement, myBuilder, myCurrentModuleScope,
                        assertiveCodeBlock, myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof InitializeVarStmt) {
                // Generate a new variable declaration/initialization rule application.
                ruleApplication = new InitializeVarStmtRule((InitializeVarStmt) statement, myBuilder,
                        myCurrentModuleScope, assertiveCodeBlock, myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof MemoryStmt) {
                if (((MemoryStmt) statement).getStatementType() == StatementType.REMEMBER) {
                    // Generate a new remember rule application.
                    ruleApplication = new RememberStmtRule(assertiveCodeBlock, myCurrentVerificationContext, mySTGroup,
                            blockModel);
                } else {
                    throw new SourceErrorException("[VCGenerator] Forget statements are not handled.",
                            statement.getLocation());
                }
            } else if (statement instanceof PresumeStmt) {
                // Generate a new presume rule application.
                ruleApplication = new PresumeStmtRule((PresumeStmt) statement, assertiveCodeBlock,
                        myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof SwapStmt) {
                // Generate a new swap rule application.
                ruleApplication = new SwapStmtRule((SwapStmt) statement, myCurrentModuleScope, assertiveCodeBlock,
                        myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof VCConfirmStmt) {
                // Generate a new VCConfirm rule application.
                ruleApplication = new VCConfirmStmtRule((VCConfirmStmt) statement, assertiveCodeBlock,
                        myCurrentVerificationContext, mySTGroup, blockModel);
            } else if (statement instanceof WhileStmt) {
                // Generate a new while rule application
                handleWhileCheck(((WhileStmt) statement).getStatements(),
                        ((WhileStmt) statement).getLoopVerificationBlock().getChangingVars());
                ruleApplication = new WhileStmtRule((WhileStmt) statement, myCurrentModuleScope, myTypeGraph,
                        assertiveCodeBlock, myCurrentVerificationContext, mySTGroup, blockModel);
            } else {
                throw new SourceErrorException(
                        "[VCGenerator] Statement type not handled: " + statement.getClass().getSimpleName(),
                        statement.getLocation());
            }

            // Apply the proof rule
            ruleApplication.applyRule();

            // Some of the proof rules might generate more than more
            // than one assertive code block. The first one is always
            // the one we passed in to the rule. We add the rest to the
            // front of the incomplete stack.
            Deque<AssertiveCodeBlock> resultingBlocks = ruleApplication.getAssertiveCodeBlocks();
            assertiveCodeBlock = resultingBlocks.removeFirst();
            while (!resultingBlocks.isEmpty()) {
                myIncompleteAssertiveCodeBlocks.addFirst(resultingBlocks.removeLast());
            }

            // Store any new block models
            myAssertiveCodeBlockModels.putAll(ruleApplication.getNewAssertiveCodeBlockModels());

            // Update our block model
            blockModel = ruleApplication.getBlockModel();
        }

        // If this block contains any branching conditions, add it
        // to our block model.
        Deque<String> branchingConditions = assertiveCodeBlock.getBranchingConditions();
        if (!branchingConditions.isEmpty()) {
            ST branchingModel = mySTGroup.getInstanceOf("outputBranchingConditions");
            ST test = branchingModel.add("conditions", branchingConditions);
            blockModel.add("branchingConditions", test.render());
        }

        myAssertiveCodeBlockModels.put(assertiveCodeBlock, blockModel);
    }

    public void handleWhileCheck(List<Statement> stmts, List<ProgramVariableExp> changingVars) {
        for (ResolveConceptualElement subElement : stmts) {
            Statement subStatement = (Statement) subElement;
            if (subStatement instanceof WhileStmt) {
                // Check for changing clause from both outer and inner loop - not a huge fan of this
                changingVars.addAll(((WhileStmt) subStatement).getLoopVerificationBlock().getChangingVars());
                handleWhileCheck(((WhileStmt) subStatement).getStatements(), changingVars);
            } else if (subStatement instanceof IfStmt) {
                List<Statement> new_stmts = new ArrayList<>();
                new_stmts.addAll(((IfStmt) subStatement).getIfClause().getStatements());
                List<IfConditionItem> elseIfs = ((IfStmt) subStatement).getElseifpairs();
                for (IfConditionItem elseif : elseIfs) {
                    new_stmts.addAll(elseif.getStatements());
                }
                new_stmts.addAll(((IfStmt) subStatement).getElseclause());
                handleWhileCheck(new_stmts, changingVars);
            } else if (subStatement instanceof SwapStmt) {
                if (!changingVars.contains(((SwapStmt) subStatement).getLeft())) {
                    myCompileEnvironment.getStatusHandler()
                            .registerAndStreamFault(
                                    new Fault(FaultType.COMPILER_EXCEPTION, subStatement.getLocation(),
                                            "Error: left variable " + ((SwapStmt) subStatement).getLeft().asString(0, 0)
                                                    + " appears in a swap statement but not the changing clause",
                                            true));
                }
                if (!changingVars.contains(((SwapStmt) subStatement).getRight())) {
                    myCompileEnvironment.getStatusHandler()
                            .registerAndStreamFault(new Fault(FaultType.COMPILER_EXCEPTION, subStatement.getLocation(),
                                    "Error: right variable " + ((SwapStmt) subStatement).getRight().asString(0, 0)
                                            + " appears in a swap statement but not the changing clause",
                                    true));
                }
            } else if (subStatement instanceof FuncAssignStmt) {
                if (!changingVars.contains(((FuncAssignStmt) subStatement).getVariableExp())) {
                    myCompileEnvironment.getStatusHandler()
                            .registerAndStreamFault(new Fault(FaultType.COMPILER_EXCEPTION, subStatement.getLocation(),
                                    "Error: variable " + ((FuncAssignStmt) subStatement).getVariableExp().asString(0, 0)
                                            + " appears in an assign statement but not the changing clause",
                                    true));
                }
            } else if (subStatement instanceof CallStmt) {
                List<ProgramExp> args = ((CallStmt) subStatement).getFunctionExp().getArguments();

                ProgramFunctionExp functionExp = ((CallStmt) subStatement).getFunctionExp();

                // Call a method to locate the operation entry for this call
                OperationEntry operationEntry = Utilities.getOperationEntry(functionExp, myCurrentModuleScope);
                ImmutableList<ProgramParameterEntry> params = operationEntry.getParameters();
                for (int i = 0; i < params.size(); i++) {
                    ProgramParameterEntry param = params.get(i);
                    ProgramVariableExp arg = (ProgramVariableExp) args.get(i);
                    if (!changingVars.contains(arg) && (param.getParameterMode().name().equals("ALTERS")
                            || param.getParameterMode().name().equals("UPDATES")
                            || param.getParameterMode().name().equals("CLEARS")
                            || param.getParameterMode().name().equals("REPLACES"))) {
                        myCompileEnvironment.getStatusHandler()
                                .registerAndStreamFault(
                                        new Fault(FaultType.COMPILER_EXCEPTION, subStatement.getLocation(),
                                                "Error: variable " + arg.asString(0, 0)
                                                        + " appears in a call statement but not the changing clause",
                                                true));
                    }
                }
            }
        }
    }

    /**
     * <p>
     * An helper method that uses all the {@code requires} and {@code constraint} clauses from the various different
     * sources (see below for complete list) and builds the appropriate {@code assume} clause that goes at the beginning
     * an {@link AssertiveCodeBlock}.
     * </p>
     *
     * <p>
     * List of different places where clauses can originate from:
     * </p>
     * <ul>
     * <li>{@code Concept}'s {@code requires} clause.</li>
     * <li>{@code Concept}'s module {@code constraint} clause.</li>
     * <li>{@code Shared Variables}' {@code constraint} clause.</li>
     * <li>{@code Concept Realization}'s {@code requires} clause.</li>
     * <li>{@code Shared Variables}' {@code convention} clause.</li>
     * <li>{@code Shared Variables}' {@code correspondence} clause
     * <li>Type {@code convention} clause (if we are are in a {@link TypeRepresentationDec} and processing type
     * finalization block).</li>
     * <li>Type {@code correspondence} clause (if we are in a {@link TypeRepresentationDec}).</li>
     * <li>Any {@code which_entails} expressions that originated from any of the clauses above.</li>
     * </ul>
     *
     * <p>
     * See the {@code Shared Variable Initialization}, {@code Type Initialization} and {@code Type Finalization} rule
     * for more detail.
     * </p>
     *
     * @param loc
     *            The location in the AST that we are currently visiting.
     * @param isTypeFinalItem
     *            A flag that indicates if this is a convention.
     *
     * @return The top-level assumed expression.
     */
    private Exp createTopLevelAssumeExpForRealizInitFinalItem(Location loc, boolean isTypeFinalItem) {
        // YS: Only add the shared variable's convention and correspondence
        // if we are in a type realization dec.
        boolean inTypeRepresentationDec = myRealizInitFinalOuterDec instanceof TypeRepresentationDec;
        Exp retExp = myCurrentVerificationContext.createTopLevelAssumeExpFromContext(loc.clone(),
                inTypeRepresentationDec, inTypeRepresentationDec);

        // YS: Only add the type correspondence if we are in a type realization dec.
        if (inTypeRepresentationDec) {
            TypeRepresentationDec typeRepresentationDec = (TypeRepresentationDec) myRealizInitFinalOuterDec;

            // YS: We can assume the convention in finalization block
            if (isTypeFinalItem) {
                AssertionClause typeConventionClause = typeRepresentationDec.getConvention().clone();
                retExp = Utilities.formConjunct(loc.clone(), retExp, typeConventionClause,
                        new LocationDetailModel(typeConventionClause.getLocation().clone(), loc.clone(),
                                "Type " + typeRepresentationDec.getName().getName() + "'s Convention"));
            }

            AssertionClause typeCorrespondenceClause = typeRepresentationDec.getCorrespondence().clone();
            retExp = Utilities.formConjunct(loc.clone(), retExp, typeCorrespondenceClause,
                    new LocationDetailModel(typeCorrespondenceClause.getLocation().clone(), loc.clone(),
                            "Type " + typeRepresentationDec.getName().getName() + "'s Correspondence"));
        }

        return retExp;
    }

    /**
     * <p>
     * An helper method that uses all the {@code requires} and {@code constraint} clauses from the various different
     * sources (see below for complete list) and builds the appropriate {@code assume} clause that goes at the beginning
     * an {@link AssertiveCodeBlock}.
     * </p>
     *
     * <p>
     * List of different places where clauses can originate from:
     * </p>
     * <ul>
     * <li>{@code Concept}'s {@code requires} clause.</li>
     * <li>{@code Concept}'s module {@code constraint} clause.</li>
     * <li>{@code Shared Variables}' {@code constraint} clause.</li>
     * <li>{@code Concept Realization}'s {@code requires} clause.</li>
     * <li>{@code Shared Variables}' {@code convention} clause.</li>
     * <li>{@code Shared Variables}' {@code correspondence} clause.</li>
     * <li>{@code constraint} clauses for all the parameters with the appropriate substitutions made.</li>
     * <li>The {@code operation}'s {@code requires} clause with the following change if it is an implementation for a
     * {@code concept}'s operation:</li>
     * <li>
     * <ul>
     * <li>Substitute the parameter name with {@code Conc.<name>} if this is the type we are implementing in a
     * {@code concept realization}.</li>
     * </ul>
     * </li>
     * <li>Any {@code which_entails} expressions that originated from any of the clauses above.</li>
     * </ul>
     *
     * <p>
     * See the {@code Procedure} declaration rule for more detail.
     * </p>
     *
     * @param loc
     *            The location in the AST that we are currently visiting.
     * @param currentBlock
     *            The current {@link AssertiveCodeBlock} we are currently generating.
     * @param correspondingOperationEntry
     *            The corresponding {@link OperationEntry}.
     * @param addSharedConventionFlag
     *            A flag that indicates whether or not we need to add the {@code Shared Variable}'s {@code convention}.
     * @param addSharedCorrespondenceFlag
     *            A flag that indicates whether or not we need to add the {@code Shared Variable}'s
     *            {@code correspondence}.
     * @param inConceptRealiz
     *            A flag that indicates whether or not this {@link ProcedureDec} is inside a
     *            {@code Concept Realization}.
     * @param isLocal
     *            A flag that indicates whether or not this is a local operation.
     *
     * @return The top-level assumed expression.
     */
    private Exp createTopLevelAssumeExpForProcedureDec(Location loc, AssertiveCodeBlock currentBlock,
            OperationEntry correspondingOperationEntry, boolean addSharedConventionFlag,
            boolean addSharedCorrespondenceFlag, boolean inConceptRealiz, boolean isLocal) {
        // Add all the expressions we can assume from the current context
        Exp retExp = myCurrentVerificationContext.createTopLevelAssumeExpFromContext(loc, addSharedConventionFlag,
                addSharedCorrespondenceFlag);

        // Create a replacement map for substituting parameter
        // variables with representation types.
        Map<Exp, Exp> substitutionParamToConc = new LinkedHashMap<>();

        // If we are in a concept realization, loop through all
        // shared variable declared from the associated concept.
        List<SharedStateDec> sharedStateDecs = myCurrentVerificationContext.getConceptSharedVars();
        if (inConceptRealiz && !isLocal) {
            // Our requires clause should say something about the conceptual
            // shared variables.
            for (SharedStateDec stateDec : sharedStateDecs) {
                for (MathVarDec mathVarDec : stateDec.getAbstractStateVars()) {
                    // Convert the math variables to variable expressions
                    VarExp varExp = Utilities.createVarExp(loc.clone(), null, mathVarDec.getName(),
                            mathVarDec.getMathType(), null);

                    // Create the appropriate conceptual versions of the shared variable
                    DotExp concVarExp = Utilities.createConcVarExp(new VarDec(mathVarDec.getName(), mathVarDec.getTy()),
                            mathVarDec.getMathType(), myTypeGraph.BOOLEAN);

                    // Add these to our substitution map
                    substitutionParamToConc.put(varExp, concVarExp);
                }
            }
        }

        // Create a new expression with any conventions and correspondence
        Exp aggConventionCorrespondenceExp = VarExp.getTrueVarExp(loc, myTypeGraph);
        for (ParameterVarDec parameterVarDec : correspondingOperationEntry.getOperationDec().getParameters()) {
            // Query for the type entry in the symbol table
            NameTy nameTy = (NameTy) parameterVarDec.getTy();
            SymbolTableEntry ste = Utilities.searchProgramType(loc, nameTy.getQualifier(), nameTy.getName(),
                    myCurrentModuleScope);

            // If the type is a type representation, then our ensures clause
            // should really say something about the conceptual type and not
            // the variable. Must also be in a concept realization and not a
            // local operation.
            if (ste.getDefiningElement() instanceof TypeRepresentationDec && inConceptRealiz && !isLocal) {
                // Add the conventions of parameters that have representation types.
                PTFamily familyType = (PTFamily) nameTy.getProgramType();
                AssertionClause conventionClause = Utilities.getTypeConventionClause(
                        ((TypeRepresentationDec) ste.getDefiningElement()).getConvention(), loc.clone(),
                        parameterVarDec.getName(), new PosSymbol(loc.clone(), familyType.getExemplarName()),
                        nameTy.getMathType(), null);
                LocationDetailModel conventionDetailModel = new LocationDetailModel(loc.clone(), loc.clone(),
                        "Type Convention for " + nameTy.getName().getName() + " Generated by "
                                + correspondingOperationEntry.getName());

                if (VarExp.isLiteralTrue(aggConventionCorrespondenceExp)) {
                    aggConventionCorrespondenceExp = Utilities.formConjunct(loc, null, conventionClause,
                            conventionDetailModel);
                } else {
                    aggConventionCorrespondenceExp = Utilities.formConjunct(loc, aggConventionCorrespondenceExp,
                            conventionClause, conventionDetailModel);
                }

                // Add the correspondence of parameters that have representation types.
                AssertionClause correspondenceClause = Utilities.getTypeCorrespondenceClause(
                        ((TypeRepresentationDec) ste.getDefiningElement()).getCorrespondence(), loc.clone(),
                        parameterVarDec.getName(), nameTy, new PosSymbol(loc.clone(), familyType.getExemplarName()),
                        nameTy, nameTy.getMathType(), null, myTypeGraph.BOOLEAN);
                LocationDetailModel correspondenceDetailModel = new LocationDetailModel(loc.clone(), loc.clone(),
                        "Type Correspondence for " + nameTy.getName().getName() + " Generated by "
                                + correspondingOperationEntry.getName());

                if (VarExp.isLiteralTrue(aggConventionCorrespondenceExp)) {
                    aggConventionCorrespondenceExp = Utilities.formConjunct(loc, null, correspondenceClause,
                            correspondenceDetailModel);
                } else {
                    aggConventionCorrespondenceExp = Utilities.formConjunct(loc, aggConventionCorrespondenceExp,
                            correspondenceClause, correspondenceDetailModel);
                }

                // Parameter variable
                VarExp parameterExp = Utilities.createVarExp(parameterVarDec.getLocation().clone(), null,
                        parameterVarDec.getName().clone(), nameTy.getMathTypeValue(), null);

                // Conceptual parameter variable
                DotExp concVarExp = Utilities.createConcVarExp(new VarDec(parameterVarDec.getName(), nameTy),
                        parameterVarDec.getMathType(), myTypeGraph.BOOLEAN);
                OldExp oldConcVarExp = new OldExp(loc, concVarExp.clone());
                oldConcVarExp.setMathType(concVarExp.getMathType());

                // Add this to our substitution map
                substitutionParamToConc.put(parameterExp, concVarExp);
            }
            // YS: For local program types, all we need is their conventions
            // with no substitutions. This is done regardless
            // of the module and operation scope.
            else if (ste.getDefiningElement() instanceof FacilityTypeRepresentationDec) {
                // Add the conventions of parameters that have representation types.
                // TODO: Figure out where the exemplar for local types is located.
                throw new RuntimeException(); // Remove this once we figure out how to add the convention.
            }
        }

        // Add any type conventions or correspondences
        if (inConceptRealiz && !isLocal) {
            if (!VarExp.isLiteralTrue(aggConventionCorrespondenceExp)) {
                retExp = MathExp.formConjunct(loc.clone(), retExp, aggConventionCorrespondenceExp);
            }
        }

        // Add the operation's requires clause (and any which_entails clause)
        AssertionClause requiresClause = correspondingOperationEntry.getRequiresClause();
        Exp requiresExp = requiresClause.getAssertionExp().clone();
        if (!VarExp.isLiteralTrue(requiresExp)) {
            // Replace any parameter variables with representation types
            // with the conceptual counterparts
            requiresExp = requiresExp.substitute(substitutionParamToConc);

            Exp whichEntailsExp = requiresClause.getWhichEntailsExp();
            if (whichEntailsExp != null) {
                whichEntailsExp = whichEntailsExp.substitute(substitutionParamToConc);
            }

            // Form a conjunct with the requires clause and add
            // the location detail associated with it.
            AssertionClause modifiedRequiresClause = new AssertionClause(requiresClause.getLocation().clone(),
                    requiresClause.getClauseType(), requiresExp, whichEntailsExp,
                    requiresClause.getInvolvedSharedVars());
            retExp = Utilities.formConjunct(loc, retExp, modifiedRequiresClause,
                    new LocationDetailModel(modifiedRequiresClause.getLocation().clone(),
                            modifiedRequiresClause.getLocation().clone(),
                            "Requires Clause for " + correspondingOperationEntry.getName()));
        }

        // Add the operation parameter's type constraints.
        // YS: We are not adding these automatically. Most of the time, these
        // constraints wouldn't really help us prove any of the VCs. If you
        // are ever interested in adding these to the givens list, use the
        // "addConstraints" flag. Note that these constraints still need to be
        // processed by the parsimonious step, so there is no guarantee that they
        // will show up in all of the VCs.
        if (myCompileEnvironment.flags.isFlagSet(FLAG_ADD_CONSTRAINT)) {
            retExp = addParamTypeConstraints(loc, retExp, substitutionParamToConc, myCurrentModuleScope, currentBlock,
                    correspondingOperationEntry.getParameters());
        }

        return retExp;
    }
}
