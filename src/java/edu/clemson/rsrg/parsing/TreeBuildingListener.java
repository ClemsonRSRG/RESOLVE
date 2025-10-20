/*
 * TreeBuildingListener.java
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
package edu.clemson.rsrg.parsing;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.declarations.mathdecl.*;
import edu.clemson.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.rsrg.absyn.declarations.operationdecl.OperationProcedureDec;
import edu.clemson.rsrg.absyn.declarations.operationdecl.PerformanceOperationDec;
import edu.clemson.rsrg.absyn.declarations.operationdecl.ProcedureDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ConceptTypeParamDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ConstantParamDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.RealizationParamDec;
import edu.clemson.rsrg.absyn.declarations.sharedstatedecl.AbstractSharedStateRealizationDec;
import edu.clemson.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.rsrg.absyn.declarations.sharedstatedecl.SharedStateRealizationDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.*;
import edu.clemson.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.rsrg.absyn.items.mathitems.DefinitionBodyItem;
import edu.clemson.rsrg.absyn.items.mathitems.LoopVerificationItem;
import edu.clemson.rsrg.absyn.items.mathitems.PerformanceSpecInitFinalItem;
import edu.clemson.rsrg.absyn.items.mathitems.SpecInitFinalItem;
import edu.clemson.rsrg.absyn.items.programitems.*;
import edu.clemson.rsrg.absyn.rawtypes.ArbitraryExpTy;
import edu.clemson.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.rsrg.absyn.rawtypes.RecordTy;
import edu.clemson.rsrg.absyn.rawtypes.Ty;
import edu.clemson.rsrg.absyn.statements.*;
import edu.clemson.rsrg.init.ResolveCompiler;
import edu.clemson.rsrg.init.file.ResolveFile;
import edu.clemson.rsrg.init.file.ResolveFileBasicInfo;
import edu.clemson.rsrg.misc.Utilities;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.parsing.sanitychecking.ValidFunctionOpDeclChecker;
import edu.clemson.rsrg.parsing.sanitychecking.ValidSharedStateChecker;
import edu.clemson.rsrg.parsing.sanitychecking.ValidTypeFamilyChecker;
import edu.clemson.rsrg.parsing.utilities.SyntacticSugarConverter;
import edu.clemson.rsrg.statushandling.Fault;
import edu.clemson.rsrg.statushandling.FaultType;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * <p>
 * This replaces the old RESOLVE ANTLR3 builder and builds the intermediate representation objects used during the
 * compilation process.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 *
 * @version 1.0
 */
public class TreeBuildingListener extends ResolveParserBaseListener {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Stores all the parser nodes we have encountered.
     * </p>
     */
    private final ParseTreeProperty<ResolveConceptualElement> myNodes;

    /**
     * <p>
     * Stores the information gathered from the children nodes of {@code ResolveParser.DefinitionSignatureContext}
     * </p>
     */
    private List<DefinitionMembers> myDefinitionMemberList;

    /**
     * <p>
     * Boolean that indicates that we are processing a module argument.
     * </p>
     */
    private boolean myIsProcessingModuleArgument;

    /**
     * <p>
     * This helper class help us keep track of all the module level array facilities.
     * </p>
     */
    private NewModuleDecMembers myModuleLevelDecs;

    /**
     * <p>
     * This is a stack that contains containers for potential new array facilities.
     * </p>
     */
    private Stack<ArrayFacilityDecContainer> myArrayFacilityDecContainerStack;

    /**
     * <p>
     * This map provides a mapping between the newly declared array name types to the types of elements in the array.
     * </p>
     */
    private Map<NameTy, NameTy> myArrayNameTyToInnerTyMap;

    /**
     * <p>
     * This is a deep copy of all the type representations created during the tree building process. Note that the list
     * contains either all {@link TypeRepresentationDec}s or all {@link FacilityTypeRepresentationDec}s.
     * </p>
     */
    private final List<AbstractTypeRepresentationDec> myCopyTRList;

    /**
     * <p>
     * This is a deep copy of all the shared state representations created during the tree building process. Note that
     * the list contains all {@link SharedStateRealizationDec}s.
     * </p>
     */
    private final List<AbstractSharedStateRealizationDec> myCopySSRList;

    /**
     * <p>
     * Since we don't have symbol table, we really don't know if we are generating a new object with the same name. In
     * order to avoid problems, all of our objects will have a name that starts with "_" and end the current new element
     * counter. This number increases by 1 each time we create a new element.
     * </p>
     */
    private int myNewElementCounter;

    /**
     * <p>
     * All the different modules that the current file depend on.
     * </p>
     */
    private final Map<ResolveFileBasicInfo, Boolean> myModuleDependencies;

    /**
     * <p>
     * The complete module representation.
     * </p>
     */
    private ModuleDec myFinalModule;

    /**
     * <p>
     * The current file we are compiling.
     * </p>
     */
    private final ResolveFile myFile;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final StatusHandler myStatusHandler;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Create a listener to walk the entire compiler generated ANTLR4 parser tree and generate the intermediate
     * representation objects used by the subsequent modules.
     * </p>
     *
     * @param file
     *            The current file we are compiling.
     * @param typeGraph
     *            Type graph that indicates relationship between different mathematical types.
     */
    public TreeBuildingListener(ResolveFile file, TypeGraph typeGraph, StatusHandler statusHandler) {
        myTypeGraph = typeGraph;
        myStatusHandler = statusHandler;
        myFile = file;
        myFinalModule = null;
        myNodes = new ParseTreeProperty<>();
        myDefinitionMemberList = null;
        myIsProcessingModuleArgument = false;
        myModuleLevelDecs = null;
        myArrayFacilityDecContainerStack = new Stack<>();
        myArrayNameTyToInnerTyMap = new LinkedHashMap<>();
        myCopyTRList = new ArrayList<>();
        myCopySSRList = new ArrayList<>();
        myNewElementCounter = 0;
        myModuleDependencies = new LinkedHashMap<>();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Module Declaration
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates and saves the complete module declaration.
     * </p>
     *
     * @param ctx
     *            Module node in ANTLR4 AST.
     */
    @Override
    public void exitModule(ResolveParser.ModuleContext ctx) {
        myNodes.put(ctx, myNodes.get(ctx.getChild(0)));
        myFinalModule = (ModuleDec) myNodes.get(ctx.getChild(0));
    }

    // -----------------------------------------------------------
    // Precis Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if the {@link ResolveFile} name matches the open and close names given in the file.
     * </p>
     *
     * @param ctx
     *            Precis module node in ANTLR4 AST.
     */
    @Override
    public void enterPrecisModule(ResolveParser.PrecisModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "Precis name does not match filename.",
                    true);
            myStatusHandler.registerAndStreamFault(f);
        }

        // if (!myFile.getName().equals(ctx.name.getText())) {
        // Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "Precis name does not match filename.",
        // true);
        // myStatusHandler.registerAndStreamFault(f);
        // }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "End name does not match the filename.",
                    true);
            myStatusHandler.registerAndStreamFault(f);
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a {@code Precis} module declaration.
     * </p>
     *
     * @param ctx
     *            Precis module node in ANTLR4 AST.
     */
    @Override
    public void exitPrecisModule(ResolveParser.PrecisModuleContext ctx) {
        List<ModuleParameterDec> parameterDecls = new ArrayList<>();
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(), myNodes);
        List<Dec> decls = Utilities.collect(Dec.class,
                ctx.precisItems() != null ? ctx.precisItems().precisItem() : new ArrayList<ParseTree>(), myNodes);

        PrecisModuleDec precis = new PrecisModuleDec(createLocation(ctx), createPosSymbol(ctx.name), parameterDecls,
                uses, decls, myModuleDependencies);

        myNodes.put(ctx, precis);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the generated precis item.
     * </p>
     *
     * @param ctx
     *            Precis item node in ANTLR4 AST.
     */
    @Override
    public void exitPrecisItem(ResolveParser.PrecisItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Facility Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if the {@link ResolveFile} name matches the open and close names given in the file.
     * </p>
     * <p>
     * If everything checks out, we create a new object to store all the elements that can be created by the syntatic
     * sugar conversions.
     * </p>
     *
     * @param ctx
     *            Facility module node in ANTLR4 AST.
     */
    @Override
    public void enterFacilityModule(ResolveParser.FacilityModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "Facility name does not match filename.", true);
            myStatusHandler.registerAndStreamFault(f);
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "End name does not match the filename.",
                    true);
            myStatusHandler.registerAndStreamFault(f);
        }

        // Create a helper class to keep track of new module level declarations
        myModuleLevelDecs = new NewModuleDecMembers();
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a {@code Facility} module declaration.
     * </p>
     *
     * @param ctx
     *            Facility module node in ANTLR4 AST.
     */
    @Override
    public void exitFacilityModule(ResolveParser.FacilityModuleContext ctx) {
        // No module parameters, but we need to pass an empty list
        List<ModuleParameterDec> parameterDecls = new ArrayList<>();

        // Uses items (if any)
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(), myNodes);

        // Add any auto import files if needed
        PosSymbol moduleName = createPosSymbol(ctx.name);
        if (!inNoAutoImportExceptionList(moduleName)) {
            uses = addToUsesList(uses, generateAutoImportUsesItems(moduleName.getLocation()));
        }

        // Module requires (if any)
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires = (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        } else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        // Decs (if any)
        List<Dec> decls = new ArrayList<>();
        if (ctx.facilityItems() != null) {
            List<ResolveParser.FacilityItemContext> itemContexts = ctx.facilityItems().facilityItem();
            for (ResolveParser.FacilityItemContext item : itemContexts) {
                // Add any new array facility declarations that was generated
                // by this facility type representation.
                if (item.facilityTypeRepresentationDecl() != null) {
                    if (myModuleLevelDecs.newFacilityDecsMap.containsKey(item.facilityTypeRepresentationDecl())) {
                        decls.addAll(
                                myModuleLevelDecs.newFacilityDecsMap.remove(item.facilityTypeRepresentationDecl()));
                    }
                }

                // Add the item to the declaration list
                decls.add((Dec) myNodes.removeFrom(item));
            }
        }

        FacilityModuleDec facility = new FacilityModuleDec(createLocation(ctx), createPosSymbol(ctx.name),
                parameterDecls, uses, requires, decls, myModuleDependencies);
        myNodes.put(ctx, facility);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the generated facility item.
     * </p>
     *
     * @param ctx
     *            Facility item node in ANTLR4 AST.
     */
    @Override
    public void exitFacilityItem(ResolveParser.FacilityItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Short Facility Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a short facility module declaration.
     * </p>
     *
     * @param ctx
     *            Short facility module node in ANTLR4 AST.
     */
    @Override
    public void exitShortFacilityModule(ResolveParser.ShortFacilityModuleContext ctx) {
        FacilityDec facilityDec = (FacilityDec) myNodes.removeFrom(ctx.facilityDecl());
        ShortFacilityModuleDec shortFacility = new ShortFacilityModuleDec(createLocation(ctx), facilityDec.getName(),
                facilityDec, myModuleDependencies);

        myNodes.put(ctx, shortFacility);
    }

    // -----------------------------------------------------------
    // Concept Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if the {@link ResolveFile} name matches the open and close names given in the file.
     * </p>
     *
     * @param ctx
     *            Concept module node in ANTLR4 AST.
     */
    @Override
    public void enterConceptModule(ResolveParser.ConceptModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "Concept name does not match filename.",
                    true);
            myStatusHandler.registerAndStreamFault(f);
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "End name does not match the filename.",
                    true);
            myStatusHandler.registerAndStreamFault(f);
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a {@code Concept} module declaration.
     * </p>
     *
     * @param ctx
     *            Concept module node in ANTLR4 AST.
     */
    @Override
    public void exitConceptModule(ResolveParser.ConceptModuleContext ctx) {
        // Module parameters (if any)
        List<ModuleParameterDec> parameterDecls = getModuleArguments(ctx.moduleParameterList());

        // Uses items (if any)
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(), myNodes);

        // Add any auto import files if needed
        PosSymbol moduleName = createPosSymbol(ctx.name);
        if (!inNoAutoImportExceptionList(moduleName)) {
            uses = addToUsesList(uses, generateAutoImportUsesItems(moduleName.getLocation()));
        }

        // Module requires (if any)
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires = (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        } else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        // Add any Constraints or Decs (if any)
        List<AssertionClause> constraints = new ArrayList<>();
        List<Dec> decls = new ArrayList<>();
        if (ctx.conceptItems() != null) {
            List<ResolveParser.ConceptItemContext> itemContexts = ctx.conceptItems().conceptItem();
            for (ResolveParser.ConceptItemContext item : itemContexts) {
                if (item.constraintClause() != null) {
                    constraints.add((AssertionClause) myNodes.removeFrom(item));
                } else {
                    decls.add((Dec) myNodes.removeFrom(item));
                }
            }
        }

        // Check to see if it is actually a sharing concept
        // i.e. Has a shared variables block and/or definition variable
        boolean isSharingConcept = false;
        boolean hasSharingConstructs = hasSharingConstructs(decls);
        if (ctx.SHARED() != null) {
            // Construct a fault if we can't find any sharing constructs
            if (!hasSharingConstructs) {
                Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                        "This sharing concept does not have any sharing constructs declared!", true);
                myStatusHandler.registerAndStreamFault(f);
            }

            int numSharedStateDecs = 0;
            for (Dec dec : decls) {
                if (dec instanceof SharedStateDec) {
                    numSharedStateDecs++;
                }
            }

            // YS: Right now we only allow 1 shared state declaration.
            // Construct a fault if we found more than 1.
            if (numSharedStateDecs > 1) {
                Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                        "A sharing concept can only have one shared variable block declared!", true);
                myStatusHandler.registerAndStreamFault(f);
            }

            isSharingConcept = true;
        } else {
            // Construct a fault if we found sharing constructs, but the concept wasn't
            // declared as shared.
            if (hasSharingConstructs) {
                Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                        "The concept has sharing constructs declared, but isn't declared as shared!", true);
                myStatusHandler.registerAndStreamFault(f);
            }
        }

        ConceptModuleDec concept = new ConceptModuleDec(createLocation(ctx), createPosSymbol(ctx.name), parameterDecls,
                uses, requires, constraints, decls, isSharingConcept, myModuleDependencies);
        myNodes.put(ctx, concept);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the generated concept item.
     * </p>
     *
     * @param ctx
     *            Concept item node in ANTLR4 AST.
     */
    @Override
    public void exitConceptItem(ResolveParser.ConceptItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Concept Realization Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if the {@link ResolveFile} name matches the open and close names given in the file.
     * </p>
     * <p>
     * If everything checks out, we create a new object to store all the new array facilities that can be created by the
     * syntactic sugar conversions.
     * </p>
     *
     * @param ctx
     *            Concept impl module node in ANTLR4 AST.
     */
    @Override
    public void enterConceptImplModule(ResolveParser.ConceptImplModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "Concept realization name does not match filename.", true);
            myStatusHandler.registerAndStreamFault(f);
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "End name does not match the filename.",
                    true);
            myStatusHandler.registerAndStreamFault(f);
        }

        // Create a helper class to keep track of new module level declarations
        myModuleLevelDecs = new NewModuleDecMembers();
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a {@code Realization} module declaration for an {@code Concept} module.
     * </p>
     *
     * @param ctx
     *            Concept impl module node in ANTLR4 AST.
     */
    @Override
    public void exitConceptImplModule(ResolveParser.ConceptImplModuleContext ctx) {
        // Module parameters (if any)
        List<ModuleParameterDec> parameterDecls = getModuleArguments(ctx.moduleParameterList());

        // Uses items (if any)
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(), myNodes);

        // Add any auto import files if needed
        PosSymbol moduleName = createPosSymbol(ctx.name);
        if (!inNoAutoImportExceptionList(moduleName)) {
            uses = addToUsesList(uses, generateAutoImportUsesItems(moduleName.getLocation()));
        }

        // Module requires (if any)
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires = (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        } else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        // Profile (if any)
        PosSymbol profileName = null;
        if (ctx.profile != null) {
            profileName = createPosSymbol(ctx.profile);
        }

        // Decs (if any)
        List<Dec> decls = new ArrayList<>();
        if (ctx.conceptImplItems() != null) {
            List<ResolveParser.ConceptImplItemContext> itemContexts = ctx.conceptImplItems().conceptImplItem();
            int numSharedStateRealizDecs = 0;
            for (ResolveParser.ConceptImplItemContext item : itemContexts) {
                // Add any new array facility declarations that was generated
                // by this shared state representation.
                if (item.sharedStateRepresentationDecl() != null) {
                    if (myModuleLevelDecs.newFacilityDecsMap.containsKey(item.sharedStateRepresentationDecl())) {
                        decls.addAll(myModuleLevelDecs.newFacilityDecsMap.remove(item.sharedStateRepresentationDecl()));
                    }

                    numSharedStateRealizDecs++;
                }
                // Add any new array facility declarations that was generated
                // by this type representation.
                else if (item.typeRepresentationDecl() != null) {
                    if (myModuleLevelDecs.newFacilityDecsMap.containsKey(item.typeRepresentationDecl())) {
                        decls.addAll(myModuleLevelDecs.newFacilityDecsMap.remove(item.typeRepresentationDecl()));
                    }
                }

                // Add the item to the declaration list
                decls.add((Dec) myNodes.removeFrom(item));
            }

            // YS: Right now we only allow 1 shared state realization.
            // Construct a fault if we found more than 1.
            if (numSharedStateRealizDecs > 1) {
                Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                        "Found more than one shared variable realization block!", true);
                myStatusHandler.registerAndStreamFault(f);
            }
        }

        // Add concept as a module dependency
        addNewModuleDependency(ctx.concept.getText(), ctx.concept.getText(), false);
        if (ctx.profile != null) {
            addNewModuleDependency(ctx.profile.getText(), ctx.concept.getText(), false);
        }

        ConceptRealizModuleDec realization = new ConceptRealizModuleDec(createLocation(ctx), createPosSymbol(ctx.name),
                parameterDecls, profileName, createPosSymbol(ctx.concept), uses, requires, decls, myModuleDependencies);
        myNodes.put(ctx, realization);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the generated realization item.
     * </p>
     *
     * @param ctx
     *            Concept implementation item node in ANTLR4 AST.
     */
    @Override
    public void exitConceptImplItem(ResolveParser.ConceptImplItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Enhancement Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if the {@link ResolveFile} name matches the open and close names given in the file.
     * </p>
     *
     * @param ctx
     *            Enhancement module node in ANTLR4 AST.
     */
    @Override
    public void enterEnhancementModule(ResolveParser.EnhancementModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "Enhancement name does not match filename.", true);
            myStatusHandler.registerAndStreamFault(f);
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "End name does not match the filename.",
                    true);
            myStatusHandler.registerAndStreamFault(f);
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of an {@code Enhancement} module declaration.
     * </p>
     *
     * @param ctx
     *            Enhancement module node in ANTLR4 AST.
     */
    @Override
    public void exitEnhancementModule(ResolveParser.EnhancementModuleContext ctx) {
        // Module parameters (if any)
        List<ModuleParameterDec> parameterDecls = getModuleArguments(ctx.moduleParameterList());

        // Uses items (if any)
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(), myNodes);

        // Add any auto import files if needed
        PosSymbol moduleName = createPosSymbol(ctx.name);
        if (!inNoAutoImportExceptionList(moduleName)) {
            uses = addToUsesList(uses, generateAutoImportUsesItems(moduleName.getLocation()));
        }

        // Module requires (if any)
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires = (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        } else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        // Decs (if any)
        List<Dec> decls = Utilities.collect(Dec.class,
                ctx.enhancementItems() != null ? ctx.enhancementItems().enhancementItem() : new ArrayList<ParseTree>(),
                myNodes);

        // Add concept as a module dependency
        addNewModuleDependency(ctx.concept.getText(), ctx.concept.getText(), false);

        EnhancementModuleDec enhancement = new EnhancementModuleDec(createLocation(ctx), createPosSymbol(ctx.name),
                parameterDecls, createPosSymbol(ctx.concept), uses, requires, decls, myModuleDependencies);
        myNodes.put(ctx, enhancement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the generated enhancement item.
     * </p>
     *
     * @param ctx
     *            Enhancement item node in ANTLR4 AST.
     */
    @Override
    public void exitEnhancementItem(ResolveParser.EnhancementItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Enhancement Realization Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if the {@link ResolveFile} name matches the open and close names given in the file.
     * </p>
     * <p>
     * If everything checks out, we create a new object to store all the new array facilities that can be created by the
     * syntactic sugar conversions.
     * </p>
     *
     * @param ctx
     *            Enhancement impl module node in ANTLR4 AST.
     */
    @Override
    public void enterEnhancementImplModule(ResolveParser.EnhancementImplModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "Enhancement realization name does not match filename.", true);
            myStatusHandler.registerAndStreamFault(f);
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "End name does not match the filename.",
                    true);
            myStatusHandler.registerAndStreamFault(f);
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a {@code Realization} module declaration for an {@code Enhancement}
     * module.
     * </p>
     *
     * @param ctx
     *            Enhancement impl module node in ANTLR4 AST.
     */
    @Override
    public void exitEnhancementImplModule(ResolveParser.EnhancementImplModuleContext ctx) {
        // Module parameters (if any)
        List<ModuleParameterDec> parameterDecls = getModuleArguments(ctx.moduleParameterList());

        // Uses items (if any)
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(), myNodes);

        // Add any auto import files if needed
        PosSymbol moduleName = createPosSymbol(ctx.name);
        if (!inNoAutoImportExceptionList(moduleName)) {
            uses = addToUsesList(uses, generateAutoImportUsesItems(moduleName.getLocation()));
        }

        // Module requires (if any)
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires = (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        } else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        // Profile (if any)
        PosSymbol profileName = null;
        if (ctx.profile != null) {
            profileName = createPosSymbol(ctx.profile);
        }

        // Decs (if any)
        List<Dec> decls = Utilities.collect(Dec.class,
                ctx.implItems() != null ? ctx.implItems().implItem() : new ArrayList<ParseTree>(), myNodes);

        // Add concept and enhancement as module dependencies
        addNewModuleDependency(ctx.concept.getText(), ctx.concept.getText(), false);
        addNewModuleDependency(ctx.enhancement.getText(), ctx.concept.getText(), false);
        if (ctx.profile != null) {
            addNewModuleDependency(ctx.profile.getText(), ctx.concept.getText(), false);
        }

        EnhancementRealizModuleDec realization = new EnhancementRealizModuleDec(createLocation(ctx),
                createPosSymbol(ctx.name), parameterDecls, profileName, createPosSymbol(ctx.enhancement),
                createPosSymbol(ctx.concept), uses, requires, decls, myModuleDependencies);
        myNodes.put(ctx, realization);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the generated realization item.
     * </p>
     *
     * @param ctx
     *            Implementation item node in ANTLR4 AST.
     */
    @Override
    public void exitImplItem(ResolveParser.ImplItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Performance Profile Module for Concepts
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if the {@link ResolveFile} name matches the open and close names given in the file.
     * </p>
     *
     * @param ctx
     *            Concept performance module node in ANTLR4 AST.
     */
    @Override
    public void enterConceptPerformanceModule(ResolveParser.ConceptPerformanceModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "Concept profile name does not match filename.", true);
            myStatusHandler.registerAndStreamFault(f);
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "End name does not match the filename.",
                    true);
            myStatusHandler.registerAndStreamFault(f);
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of an {@code Profile} module declaration for an {@code Concept} module.
     * </p>
     *
     * @param ctx
     *            Concept performance module node in ANTLR4 AST.
     */
    @Override
    public void exitConceptPerformanceModule(ResolveParser.ConceptPerformanceModuleContext ctx) {
        // Module parameters (if any)
        List<ModuleParameterDec> parameterDecls = getModuleArguments(ctx.moduleParameterList());

        // Uses items (if any)
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(), myNodes);

        // Add any auto import files if needed
        PosSymbol moduleName = createPosSymbol(ctx.name);
        if (!inNoAutoImportExceptionList(moduleName)) {
            uses = addToUsesList(uses, generateAutoImportUsesItems(moduleName.getLocation()));
        }

        // Module requires (if any)
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires = (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        } else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        // Decs (if any)
        List<Dec> decls = Utilities.collect(Dec.class,
                ctx.conceptPerformanceItems() != null ? ctx.conceptPerformanceItems().conceptPerformanceItem()
                        : new ArrayList<ParseTree>(),
                myNodes);

        // Add concept as a module dependency
        addNewModuleDependency(ctx.concept.getText(), ctx.concept.getText(), false);

        PerformanceConceptModuleDec performance = new PerformanceConceptModuleDec(createLocation(ctx),
                createPosSymbol(ctx.name), parameterDecls, createPosSymbol(ctx.fullName), createPosSymbol(ctx.concept),
                uses, requires, decls, myModuleDependencies);
        myNodes.put(ctx, performance);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the generated item for concept performance profiles.
     * </p>
     *
     * @param ctx
     *            Concept performance item node in ANTLR4 AST.
     */
    @Override
    public void exitConceptPerformanceItem(ResolveParser.ConceptPerformanceItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Performance Profile Module for Enhancements
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if the {@link ResolveFile} name matches the open and close names given in the file.
     * </p>
     *
     * @param ctx
     *            Enhancement performance module node in ANTLR4 AST.
     */
    @Override
    public void enterEnhancementPerformanceModule(ResolveParser.EnhancementPerformanceModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "Concept profile name does not match filename.", true);
            myStatusHandler.registerAndStreamFault(f);
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx), "End name does not match the filename.",
                    true);
            myStatusHandler.registerAndStreamFault(f);
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of an {@code Profile} module declaration for an {@code Enhancement}
     * module.
     * </p>
     *
     * @param ctx
     *            Enhancement performance module node in ANTLR4 AST.
     */
    @Override
    public void exitEnhancementPerformanceModule(ResolveParser.EnhancementPerformanceModuleContext ctx) {
        // Module parameters (if any)
        List<ModuleParameterDec> parameterDecls = getModuleArguments(ctx.moduleParameterList());

        // Uses items (if any)
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(), myNodes);

        // Add any auto import files if needed
        PosSymbol moduleName = createPosSymbol(ctx.name);
        if (!inNoAutoImportExceptionList(moduleName)) {
            uses = addToUsesList(uses, generateAutoImportUsesItems(moduleName.getLocation()));
        }

        // Module requires (if any)
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires = (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        } else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        // Decs (if any)
        List<Dec> decls = Utilities.collect(Dec.class,
                ctx.enhancementPerformanceItems() != null
                        ? ctx.enhancementPerformanceItems().enhancementPerformanceItem()
                        : new ArrayList<ParseTree>(),
                myNodes);

        // Add concept/concept profile/enhancement as module dependencies
        addNewModuleDependency(ctx.concept.getText(), ctx.concept.getText(), false);
        addNewModuleDependency(ctx.conceptProfile.getText(), ctx.concept.getText(), false);
        addNewModuleDependency(ctx.enhancement.getText(), ctx.concept.getText(), false);

        PerformanceEnhancementModuleDec performance = new PerformanceEnhancementModuleDec(createLocation(ctx),
                createPosSymbol(ctx.name), parameterDecls, createPosSymbol(ctx.fullName),
                createPosSymbol(ctx.enhancement), createPosSymbol(ctx.concept), createPosSymbol(ctx.conceptProfile),
                uses, requires, decls, myModuleDependencies);
        myNodes.put(ctx, performance);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the generated item for enhancement performance profiles.
     * </p>
     *
     * @param ctx
     *            Enhancement performance item node in ANTLR4 AST.
     */
    @Override
    public void exitEnhancementPerformanceItem(ResolveParser.EnhancementPerformanceItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Uses Items (Imports)
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation for an import module name.
     * </p>
     *
     * @param ctx
     *            Uses item node in ANTLR4 AST.
     */
    @Override
    public void exitUsesItem(ResolveParser.UsesItemContext ctx) {
        // Add the module we are importing as module dependency
        addNewModuleDependency(ctx.getStart().getText(), "", false);

        myNodes.put(ctx, new UsesItem(createPosSymbol(ctx.getStart())));
    }

    // -----------------------------------------------------------
    // Module parameter declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a module parameter declaration.
     * </p>
     *
     * @param ctx
     *            Module parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitModuleParameterDecl(ResolveParser.ModuleParameterDeclContext ctx) {
        if (ctx.definitionParameterDecl() != null) {
            myNodes.put(ctx, new ModuleParameterDec<>((MathDefinitionDec) myNodes.removeFrom(ctx.getChild(0))));
        } else if (ctx.typeParameterDecl() != null) {
            myNodes.put(ctx, new ModuleParameterDec<>((ConceptTypeParamDec) myNodes.removeFrom(ctx.getChild(0))));
        } else if (ctx.constantParameterDecl() != null) {
            // Could have multiple variables declared as a group
            List<TerminalNode> varNames = ctx.constantParameterDecl().variableDeclGroup().IDENTIFIER();
            for (TerminalNode ident : varNames) {
                myNodes.put(ident, new ModuleParameterDec<>((ConstantParamDec) myNodes.removeFrom(ident)));
            }
        } else if (ctx.operationParameterDecl() != null) {
            myNodes.put(ctx, new ModuleParameterDec<>((OperationDec) myNodes.removeFrom(ctx.getChild(0))));
        } else {
            myNodes.put(ctx, new ModuleParameterDec<>((RealizationParamDec) myNodes.removeFrom(ctx.getChild(0))));
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method creates a temporary list to store all the temporary definition members
     * </p>
     *
     * @param ctx
     *            Definition parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void enterDefinitionParameterDecl(ResolveParser.DefinitionParameterDeclContext ctx) {
        myDefinitionMemberList = new ArrayList<>();
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a definition parameter declaration.
     * </p>
     *
     * @param ctx
     *            Definition parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitDefinitionParameterDecl(ResolveParser.DefinitionParameterDeclContext ctx) {
        DefinitionMembers members = myDefinitionMemberList.remove(0);
        MathDefinitionDec definitionDec = new MathDefinitionDec(members.name, members.params, members.rawType, null,
                false);
        myDefinitionMemberList = null;

        myNodes.put(ctx, definitionDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a concept type parameter declaration.
     * </p>
     *
     * @param ctx
     *            Type parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitTypeParameterDecl(ResolveParser.TypeParameterDeclContext ctx) {
        myNodes.put(ctx, new ConceptTypeParamDec(createPosSymbol(ctx.name)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if this parameter declaration has a programming array type. If yes, then this is an error, because
     * there is no way the caller can pass a variable of the same type to the calling statement.
     * </p>
     *
     * @param ctx
     *            Constant parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void enterConstantParameterDecl(ResolveParser.ConstantParameterDeclContext ctx) {
        if (ctx.variableDeclGroup().programArrayType() != null) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "Array types cannot be used as a type for the parameter variables", true);
            myStatusHandler.registerAndStreamFault(f);
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a constant parameter declaration for each of the variables in the variable group.
     * </p>
     *
     * @param ctx
     *            Constant parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitConstantParameterDecl(ResolveParser.ConstantParameterDeclContext ctx) {
        // Since we have ruled out array types, this should be a NameTy
        ResolveParser.VariableDeclGroupContext variableDeclGroupContext = ctx.variableDeclGroup();
        NameTy rawType = (NameTy) myNodes.removeFrom(variableDeclGroupContext.programNamedType());

        // Generate a new parameter declaration for each of the
        // variables in the variable group.
        List<TerminalNode> variableIndents = variableDeclGroupContext.IDENTIFIER();
        for (TerminalNode node : variableIndents) {
            myNodes.put(node, new ConstantParamDec(createPosSymbol(node.getSymbol()), rawType.clone()));
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates an operation parameter declaration.
     * </p>
     *
     * @param ctx
     *            Operation parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitOperationParameterDecl(ResolveParser.OperationParameterDeclContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.operationDecl()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a realization parameter declaration.
     * </p>
     *
     * @param ctx
     *            Concept implementation parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitConceptImplParameterDecl(ResolveParser.ConceptImplParameterDeclContext ctx) {
        myNodes.put(ctx, new RealizationParamDec(createPosSymbol(ctx.name), createPosSymbol(ctx.concept)));
    }

    // -----------------------------------------------------------
    // Operation parameter declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if this parameter declaration has a programming array type. If yes, then this is an error, because
     * there is no way the caller can pass a variable of the same type to the calling statement.
     * </p>
     *
     * @param ctx
     *            Parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void enterParameterDecl(ResolveParser.ParameterDeclContext ctx) {
        if (ctx.variableDeclGroup().programArrayType() != null) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "Array types cannot be used as a type for the parameter variables", true);
            myStatusHandler.registerAndStreamFault(f);
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the parameter declaration(s).
     * </p>
     *
     * @param ctx
     *            Parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitParameterDecl(ResolveParser.ParameterDeclContext ctx) {
        // Since we have ruled out array types, this should be a NameTy
        ResolveParser.VariableDeclGroupContext variableDeclGroupContext = ctx.variableDeclGroup();
        NameTy rawType = (NameTy) myNodes.removeFrom(variableDeclGroupContext.programNamedType());

        // Generate a new parameter declaration for each of the
        // variables in the variable group.
        List<TerminalNode> variableIndents = variableDeclGroupContext.IDENTIFIER();
        for (TerminalNode node : variableIndents) {
            myNodes.put(node, new ParameterVarDec(getMode(ctx.parameterMode()), createPosSymbol(node.getSymbol()),
                    rawType.clone()));
        }
    }

    // -----------------------------------------------------------
    // Programming raw types
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new program named type.
     * </p>
     *
     * @param ctx
     *            Program named type node in ANTLR4 AST.
     */
    @Override
    public void exitProgramNamedType(ResolveParser.ProgramNamedTypeContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        myNodes.put(ctx, new NameTy(createLocation(ctx), qualifier, createPosSymbol(ctx.name)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new program record type.
     * </p>
     *
     * @param ctx
     *            Program record type node in ANTLR4 AST.
     */
    @Override
    public void exitProgramRecordType(ResolveParser.ProgramRecordTypeContext ctx) {
        List<VarDec> fields = new ArrayList<>();
        List<ResolveParser.VariableDeclGroupContext> variableDeclGroupContexts = ctx.variableDeclGroup();
        for (ResolveParser.VariableDeclGroupContext variableDeclGroupContext : variableDeclGroupContexts) {
            NameTy rawNameTy;
            if (variableDeclGroupContext.programArrayType() != null) {
                ResolveParser.ProgramArrayTypeContext arrayTypeContext = variableDeclGroupContext.programArrayType();
                Location loc = createLocation(arrayTypeContext);
                String firstIdent = variableDeclGroupContext.IDENTIFIER(0).getText();

                rawNameTy = createNameTyFromArrayType(loc, firstIdent, arrayTypeContext);
            } else {
                rawNameTy = (NameTy) myNodes.removeFrom(variableDeclGroupContext.programNamedType());
            }

            // For each identifier, create a new variable declaration
            for (TerminalNode ident : variableDeclGroupContext.IDENTIFIER()) {
                fields.add(new VarDec(createPosSymbol(ident.getSymbol()), rawNameTy.clone()));
            }
        }

        // Note: Sanity check that we have at least 2 elements inside this record.
        // The reason being it's math type (MTCartesian) doesn't make sense
        // when it is a single element. - YS
        if (fields.size() < 2) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "A record type must have 2 or more fields.", true);
            myStatusHandler.registerAndStreamFault(f);
        }

        myNodes.put(ctx, new RecordTy(createLocation(ctx), fields));
    }

    // -----------------------------------------------------------
    // Type Spec/Realization Declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a type model declaration.
     * </p>
     *
     * @param ctx
     *            Type model declaration node in ANTLR4 AST.
     */
    @Override
    public void exitTypeModelDecl(ResolveParser.TypeModelDeclContext ctx) {
        Ty mathTy = (Ty) myNodes.removeFrom(ctx.mathTypeExp());

        List<MathDefVariableDec> mathDefVariableDecs = new ArrayList<>();
        if (ctx.definitionVariable().size() > 0) {
            mathDefVariableDecs = Utilities.collect(MathDefVariableDec.class, ctx.definitionVariable(), myNodes);
        }

        AssertionClause constraint;
        if (ctx.constraintClause() != null) {
            constraint = (AssertionClause) myNodes.removeFrom(ctx.constraintClause());
        } else {
            constraint = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.CONSTRAINT);
        }

        SpecInitFinalItem initItem;
        if (ctx.specModelInit() != null) {
            initItem = (SpecInitFinalItem) myNodes.removeFrom(ctx.specModelInit());
        } else {
            initItem = new SpecInitFinalItem(createLocation(ctx), SpecInitFinalItem.ItemType.INITIALIZATION, null,
                    createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES));
        }

        SpecInitFinalItem finalItem;
        if (ctx.specModelFinal() != null) {
            finalItem = (SpecInitFinalItem) myNodes.removeFrom(ctx.specModelFinal());
        } else {
            finalItem = new SpecInitFinalItem(createLocation(ctx), SpecInitFinalItem.ItemType.FINALIZATION, null,
                    createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES));
        }

        // Build the type family declaration
        TypeFamilyDec typeFamilyDec = new TypeFamilyDec(createPosSymbol(ctx.name), mathTy,
                createPosSymbol(ctx.exemplar), mathDefVariableDecs, constraint, initItem, finalItem);

        // Sanity checks to make sure the type family declaration has
        // valid initialization and finalization ensures clauses.
        ValidTypeFamilyChecker validTypeFamilyChecker = new ValidTypeFamilyChecker(typeFamilyDec);
        validTypeFamilyChecker.hasValidAssertionClauses();

        myNodes.put(ctx, typeFamilyDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Type realization declaration node in ANTLR4 AST.
     */
    @Override
    public void enterTypeRepresentationDecl(ResolveParser.TypeRepresentationDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a type realization declaration.
     * </p>
     *
     * @param ctx
     *            Type realization declaration node in ANTLR4 AST.
     */
    @Override
    public void exitTypeRepresentationDecl(ResolveParser.TypeRepresentationDeclContext ctx) {
        // Obtain the raw programing type. If we encounter an array type,
        // we add a new array facility and create a new raw name type that
        // refers to this array facility.
        Ty rawTy;
        if (ctx.programArrayType() != null) {
            Location loc = createLocation(ctx.programArrayType());
            String name = ctx.name.getText();

            rawTy = createNameTyFromArrayType(loc, name, ctx.programArrayType());
        } else if (ctx.programRecordType() != null) {
            rawTy = (Ty) myNodes.removeFrom(ctx.programRecordType());
        } else {
            rawTy = (Ty) myNodes.removeFrom(ctx.programNamedType());
        }

        // Obtain any array facility declarations and
        // store it into our module level members
        ArrayFacilityDecContainer innerMostContainer = myArrayFacilityDecContainerStack.pop();
        myModuleLevelDecs.newFacilityDecsMap.put(ctx, innerMostContainer.newFacilityDecs);

        AssertionClause convention;
        if (ctx.conventionClause() != null) {
            convention = (AssertionClause) myNodes.removeFrom(ctx.conventionClause());
        } else {
            convention = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.CONVENTION);
        }

        AssertionClause correspondence;
        if (ctx.correspondenceClause() != null) {
            correspondence = (AssertionClause) myNodes.removeFrom(ctx.correspondenceClause());

            // Sanity check: Make sure that if the correspondence involves any global variables,
            // it is declared either as independent or dependent.
            AssertionClause.ClauseType clauseType = correspondence.getClauseType();
            if (correspondence.getInvolvedSharedVars().size() > 0
                    && clauseType.equals(AssertionClause.ClauseType.CORRESPONDENCE)) {
                Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                        "A type realization's correspondence must be declared as independent "
                                + "or dependent when it involves shared variables.",
                        true);
                myStatusHandler.registerAndStreamFault(f);
            }
        } else {
            correspondence = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.CORRESPONDENCE);
        }

        RealizInitFinalItem initItem;
        if (ctx.representationInit() != null) {
            initItem = (RealizInitFinalItem) myNodes.removeFrom(ctx.representationInit());
        } else {
            initItem = new RealizInitFinalItem(createLocation(ctx), RealizInitFinalItem.ItemType.INITIALIZATION, null,
                    new ArrayList<FacilityDec>(), new ArrayList<VarDec>(), new ArrayList<Statement>());
        }

        RealizInitFinalItem finalItem;
        if (ctx.representationFinal() != null) {
            finalItem = (RealizInitFinalItem) myNodes.removeFrom(ctx.representationFinal());
        } else {
            finalItem = new RealizInitFinalItem(createLocation(ctx), RealizInitFinalItem.ItemType.FINALIZATION, null,
                    new ArrayList<FacilityDec>(), new ArrayList<VarDec>(), new ArrayList<Statement>());
        }

        TypeRepresentationDec representationDec = new TypeRepresentationDec(createPosSymbol(ctx.name), rawTy,
                convention, correspondence, initItem, finalItem);
        myCopyTRList.add((TypeRepresentationDec) representationDec.clone());

        myNodes.put(ctx, representationDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Facility type realization declaration node in ANTLR4 AST.
     */
    @Override
    public void enterFacilityTypeRepresentationDecl(ResolveParser.FacilityTypeRepresentationDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a facility type realization declaration.
     * </p>
     *
     * @param ctx
     *            Facility type realization declaration node in ANTLR4 AST.
     */
    @Override
    public void exitFacilityTypeRepresentationDecl(ResolveParser.FacilityTypeRepresentationDeclContext ctx) {
        // Obtain the raw programing type. If we encounter an array type,
        // we add a new array facility and create a new raw name type that
        // refers to this array facility.
        Ty rawTy;
        if (ctx.programArrayType() != null) {
            Location loc = createLocation(ctx.programArrayType());
            String name = ctx.name.getText();

            rawTy = createNameTyFromArrayType(loc, name, ctx.programArrayType());
        } else if (ctx.programRecordType() != null) {
            rawTy = (RecordTy) myNodes.removeFrom(ctx.programRecordType());
        } else {
            rawTy = (NameTy) myNodes.removeFrom(ctx.programNamedType());
        }

        // Obtain any array facility declarations and
        // store it into our module level members
        ArrayFacilityDecContainer innerMostContainer = myArrayFacilityDecContainerStack.pop();
        myModuleLevelDecs.newFacilityDecsMap.put(ctx, innerMostContainer.newFacilityDecs);

        AssertionClause convention;
        if (ctx.conventionClause() != null) {
            convention = (AssertionClause) myNodes.removeFrom(ctx.conventionClause());
        } else {
            convention = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.CONVENTION);
        }

        FacilityInitFinalItem initItem;
        if (ctx.facilityRepresentationInit() != null) {
            initItem = (FacilityInitFinalItem) myNodes.removeFrom(ctx.facilityRepresentationInit());
        } else {
            initItem = new FacilityInitFinalItem(createLocation(ctx), FacilityInitFinalItem.ItemType.INITIALIZATION,
                    null, createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES),
                    createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES),
                    new ArrayList<FacilityDec>(), new ArrayList<VarDec>(), new ArrayList<Statement>());
        }

        FacilityInitFinalItem finalItem;
        if (ctx.facilityRepresentationFinal() != null) {
            finalItem = (FacilityInitFinalItem) myNodes.removeFrom(ctx.facilityRepresentationFinal());
        } else {
            finalItem = new FacilityInitFinalItem(createLocation(ctx), FacilityInitFinalItem.ItemType.FINALIZATION,
                    null, createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES),
                    createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES),
                    new ArrayList<FacilityDec>(), new ArrayList<VarDec>(), new ArrayList<Statement>());
        }

        FacilityTypeRepresentationDec representationDec = new FacilityTypeRepresentationDec(createPosSymbol(ctx.name),
                rawTy, convention, initItem, finalItem);
        myCopyTRList.add((FacilityTypeRepresentationDec) representationDec.clone());

        myNodes.put(ctx, representationDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a type model declaration for performance profiles..
     * </p>
     *
     * @param ctx
     *            Performance type model declaration node in ANTLR4 AST.
     */
    @Override
    public void exitPerformanceTypeModelDecl(ResolveParser.PerformanceTypeModelDeclContext ctx) {
        Ty mathTy = (Ty) myNodes.removeFrom(ctx.mathTypeExp());

        AssertionClause constraint;
        if (ctx.constraintClause() != null) {
            constraint = (AssertionClause) myNodes.removeFrom(ctx.constraintClause());
        } else {
            constraint = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.CONSTRAINT);
        }

        PerformanceSpecInitFinalItem initItem = null;
        if (ctx.performanceSpecModelInit() != null) {
            initItem = (PerformanceSpecInitFinalItem) myNodes.removeFrom(ctx.performanceSpecModelInit());
        }

        PerformanceSpecInitFinalItem finalItem = null;
        if (ctx.performanceSpecModelFinal() != null) {
            finalItem = (PerformanceSpecInitFinalItem) myNodes.removeFrom(ctx.performanceSpecModelFinal());
        }

        myNodes.put(ctx,
                new PerformanceTypeFamilyDec(createPosSymbol(ctx.name), mathTy, constraint, initItem, finalItem));
    }

    // -----------------------------------------------------------
    // Shared State Spec/Realization Declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a shared state declaration.
     * </p>
     *
     * @param ctx
     *            Shared state declaration node in ANTLR4 AST.
     */
    @Override
    public void exitSharedStateDecl(ResolveParser.SharedStateDeclContext ctx) {
        List<ResolveParser.ModuleStateVariableDeclContext> stateVariableDeclContexts = ctx.moduleStateVariableDecl();
        List<MathVarDec> abstractStateVars = new ArrayList<>();
        for (ResolveParser.ModuleStateVariableDeclContext stateVariableDeclContext : stateVariableDeclContexts) {
            List<TerminalNode> idents = stateVariableDeclContext.mathVariableDeclGroup().IDENTIFIER();
            for (TerminalNode ident : idents) {
                abstractStateVars.add((MathVarDec) myNodes.removeFrom(ident));
            }
        }

        AssertionClause constraint;
        if (ctx.constraintClause() != null) {
            constraint = (AssertionClause) myNodes.removeFrom(ctx.constraintClause());
        } else {
            constraint = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.CONSTRAINT);
        }

        SpecInitFinalItem initItem;
        if (ctx.specModelInit() != null) {
            initItem = (SpecInitFinalItem) myNodes.removeFrom(ctx.specModelInit());
        } else {
            initItem = new SpecInitFinalItem(createLocation(ctx), SpecInitFinalItem.ItemType.INITIALIZATION, null,
                    createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES));
        }

        // Build the shared state declaration
        SharedStateDec sharedStateDec = new SharedStateDec(createPosSymbol(ctx.start), abstractStateVars, constraint,
                initItem);

        // Sanity checks to make sure the shared state has
        // valid initialization ensures clause.
        ValidSharedStateChecker validSharedStateChecker = new ValidSharedStateChecker(sharedStateDec);
        validSharedStateChecker.hasValidAssertionClauses();

        myNodes.put(ctx, sharedStateDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Shared state realization declaration node in ANTLR4 AST.
     */
    @Override
    public void enterSharedStateRepresentationDecl(ResolveParser.SharedStateRepresentationDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a shared state realization declaration.
     * </p>
     *
     * @param ctx
     *            Shared state realization declaration node in ANTLR4 AST.
     */
    @Override
    public void exitSharedStateRepresentationDecl(ResolveParser.SharedStateRepresentationDeclContext ctx) {
        List<VarDec> sharedStateVars = new ArrayList<>();
        List<ResolveParser.VariableDeclContext> variableDeclContexts = ctx.variableDecl();
        for (ResolveParser.VariableDeclContext variableDeclContext : variableDeclContexts) {
            for (TerminalNode ident : variableDeclContext.variableDeclGroup().IDENTIFIER()) {
                sharedStateVars.add((VarDec) myNodes.removeFrom(ident));
            }
        }

        // Obtain any array facility declarations and
        // store it into our module level members
        ArrayFacilityDecContainer innerMostContainer = myArrayFacilityDecContainerStack.pop();
        myModuleLevelDecs.newFacilityDecsMap.put(ctx, innerMostContainer.newFacilityDecs);

        AssertionClause convention;
        if (ctx.conventionClause() != null) {
            convention = (AssertionClause) myNodes.removeFrom(ctx.conventionClause());
        } else {
            convention = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.CONVENTION);
        }

        AssertionClause correspondence;
        if (ctx.correspondenceClause() != null) {
            correspondence = (AssertionClause) myNodes.removeFrom(ctx.correspondenceClause());

            // Sanity check: Make sure that we don't have an independent or
            // dependent correspondence.
            AssertionClause.ClauseType clauseType = correspondence.getClauseType();
            if (clauseType.equals(AssertionClause.ClauseType.INDEPENDENT_CORRESPONDENCE)
                    || clauseType.equals(AssertionClause.ClauseType.DEPENDENT_CORRESPONDENCE)) {
                String message;
                if (clauseType.equals(AssertionClause.ClauseType.INDEPENDENT_CORRESPONDENCE)) {
                    message = "n " + correspondence.getClauseType().toString();
                } else {
                    message = " " + correspondence.getClauseType().toString();
                }
                Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                        "A Shared Variable realization cannot have a" + message, true);
                myStatusHandler.registerAndStreamFault(f);
            }
        } else {
            correspondence = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.CORRESPONDENCE);
        }

        RealizInitFinalItem initItem;
        if (ctx.representationInit() != null) {
            initItem = (RealizInitFinalItem) myNodes.removeFrom(ctx.representationInit());
        } else {
            initItem = new RealizInitFinalItem(createLocation(ctx), RealizInitFinalItem.ItemType.INITIALIZATION, null,
                    new ArrayList<FacilityDec>(), new ArrayList<VarDec>(), new ArrayList<Statement>());
        }

        // YS: We should only have shared variable realization dec
        if (!myCopySSRList.isEmpty()) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "A concept realization can only have one Shared Variables realization block.", true);
            myStatusHandler.registerAndStreamFault(f);
        }

        SharedStateRealizationDec realizationDec = new SharedStateRealizationDec(createPosSymbol(ctx.start),
                sharedStateVars, convention, correspondence, initItem);
        myCopySSRList.add((SharedStateRealizationDec) realizationDec.clone());

        myNodes.put(ctx, realizationDec);
    }

    // -----------------------------------------------------------
    // Definition Variable
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new definition variable for a type model.
     * </p>
     *
     * @param ctx
     *            Definition variable node in ANTLR4 AST.
     */
    @Override
    public void exitDefinitionVariable(ResolveParser.DefinitionVariableContext ctx) {
        MathVarDec varDec = (MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl());
        DefinitionBodyItem bodyItem = null;
        if (ctx.mathExp() != null) {
            bodyItem = new DefinitionBodyItem((Exp) myNodes.removeFrom(ctx.mathExp()));
        }

        myNodes.put(ctx, new MathDefVariableDec(varDec, bodyItem));
    }

    // -----------------------------------------------------------
    // Initialization/Finalization Items
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a type model initialization item.
     * </p>
     *
     * @param ctx
     *            Spec model init node in ANTLR4 AST.
     */
    @Override
    public void exitSpecModelInit(ResolveParser.SpecModelInitContext ctx) {
        AffectsClause affects = null;
        if (ctx.affectsClause() != null) {
            affects = (AffectsClause) myNodes.removeFrom(ctx.affectsClause());
        }

        AssertionClause ensures;
        if (ctx.ensuresClause() != null) {
            ensures = (AssertionClause) myNodes.removeFrom(ctx.ensuresClause());
        } else {
            ensures = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES);
        }

        myNodes.put(ctx, new SpecInitFinalItem(createLocation(ctx), SpecInitFinalItem.ItemType.INITIALIZATION, affects,
                ensures));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a type model finalization item.
     * </p>
     *
     * @param ctx
     *            Spec model final node in ANTLR4 AST.
     */
    @Override
    public void exitSpecModelFinal(ResolveParser.SpecModelFinalContext ctx) {
        AffectsClause affects = null;
        if (ctx.affectsClause() != null) {
            affects = (AffectsClause) myNodes.removeFrom(ctx.affectsClause());
        }

        AssertionClause ensures;
        if (ctx.ensuresClause() != null) {
            ensures = (AssertionClause) myNodes.removeFrom(ctx.ensuresClause());
        } else {
            ensures = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES);
        }

        myNodes.put(ctx,
                new SpecInitFinalItem(createLocation(ctx), SpecInitFinalItem.ItemType.FINALIZATION, affects, ensures));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Representation init node in ANTLR4 AST.
     */
    @Override
    public void enterRepresentationInit(ResolveParser.RepresentationInitContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a type realization initialization item.
     * </p>
     *
     * @param ctx
     *            Representation init node in ANTLR4 AST.
     */
    @Override
    public void exitRepresentationInit(ResolveParser.RepresentationInitContext ctx) {
        AffectsClause affects = null;
        if (ctx.affectsClause() != null) {
            affects = (AffectsClause) myNodes.removeFrom(ctx.affectsClause());
        }

        myNodes.put(ctx,
                createRealizInitFinalItem(createLocation(ctx), RealizInitFinalItem.ItemType.INITIALIZATION, affects,
                        getFacilityDecls(ctx.facilityDecl()), getVarDecls(ctx.variableDecl()),
                        Utilities.collect(Statement.class, ctx.stmt(), myNodes)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Representation final node in ANTLR4 AST.
     */
    @Override
    public void enterRepresentationFinal(ResolveParser.RepresentationFinalContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a type realization finalization item.
     * </p>
     *
     * @param ctx
     *            Representation final node in ANTLR4 AST.
     */
    @Override
    public void exitRepresentationFinal(ResolveParser.RepresentationFinalContext ctx) {
        AffectsClause affects = null;
        if (ctx.affectsClause() != null) {
            affects = (AffectsClause) myNodes.removeFrom(ctx.affectsClause());
        }

        myNodes.put(ctx,
                createRealizInitFinalItem(createLocation(ctx), RealizInitFinalItem.ItemType.FINALIZATION, affects,
                        getFacilityDecls(ctx.facilityDecl()), getVarDecls(ctx.variableDecl()),
                        Utilities.collect(Statement.class, ctx.stmt(), myNodes)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Facility representation init node in ANTLR4 AST.
     */
    @Override
    public void enterFacilityRepresentationInit(ResolveParser.FacilityRepresentationInitContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a facility type realization initialization item.
     * </p>
     *
     * @param ctx
     *            Facility representation init node in ANTLR4 AST.
     */
    @Override
    public void exitFacilityRepresentationInit(ResolveParser.FacilityRepresentationInitContext ctx) {
        AffectsClause affects = null;
        if (ctx.affectsClause() != null) {
            affects = (AffectsClause) myNodes.removeFrom(ctx.affectsClause());
        }

        // Requires and ensures
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires = (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        } else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        AssertionClause ensures;
        if (ctx.ensuresClause() != null) {
            ensures = (AssertionClause) myNodes.removeFrom(ctx.ensuresClause());
        } else {
            ensures = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES);
        }

        myNodes.put(ctx,
                createFacilityTypeInitFinalItem(createLocation(ctx), FacilityInitFinalItem.ItemType.INITIALIZATION,
                        affects, requires, ensures, getFacilityDecls(ctx.facilityDecl()),
                        getVarDecls(ctx.variableDecl()), Utilities.collect(Statement.class, ctx.stmt(), myNodes)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Facility representation final node in ANTLR4 AST.
     */
    @Override
    public void enterFacilityRepresentationFinal(ResolveParser.FacilityRepresentationFinalContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a facility type realization finalization item.
     * </p>
     *
     * @param ctx
     *            Facility representation final node in ANTLR4 AST.
     */
    @Override
    public void exitFacilityRepresentationFinal(ResolveParser.FacilityRepresentationFinalContext ctx) {
        AffectsClause affects = null;
        if (ctx.affectsClause() != null) {
            affects = (AffectsClause) myNodes.removeFrom(ctx.affectsClause());
        }

        // Requires and ensures
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires = (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        } else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        AssertionClause ensures;
        if (ctx.ensuresClause() != null) {
            ensures = (AssertionClause) myNodes.removeFrom(ctx.ensuresClause());
        } else {
            ensures = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES);
        }

        myNodes.put(ctx,
                createFacilityTypeInitFinalItem(createLocation(ctx), FacilityInitFinalItem.ItemType.FINALIZATION,
                        affects, requires, ensures, getFacilityDecls(ctx.facilityDecl()),
                        getVarDecls(ctx.variableDecl()), Utilities.collect(Statement.class, ctx.stmt(), myNodes)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a type model initialization item for performance profiles.
     * </p>
     *
     * @param ctx
     *            Performance spec model init node in ANTLR4 AST.
     */
    @Override
    public void exitPerformanceSpecModelInit(ResolveParser.PerformanceSpecModelInitContext ctx) {
        AssertionClause duration = null;
        if (ctx.durationClause() != null) {
            duration = (AssertionClause) myNodes.removeFrom(ctx.durationClause());
        }

        AssertionClause manipDisp = null;
        if (ctx.manipulationDispClause() != null) {
            manipDisp = (AssertionClause) myNodes.removeFrom(ctx.manipulationDispClause());
        }

        myNodes.put(ctx, new PerformanceSpecInitFinalItem(createLocation(ctx),
                PerformanceSpecInitFinalItem.ItemType.INITIALIZATION, duration, manipDisp));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a type model finalization item for performance profiles.
     * </p>
     *
     * @param ctx
     *            Performance spec model final node in ANTLR4 AST.
     */
    @Override
    public void exitPerformanceSpecModelFinal(ResolveParser.PerformanceSpecModelFinalContext ctx) {
        AssertionClause duration = null;
        if (ctx.durationClause() != null) {
            duration = (AssertionClause) myNodes.removeFrom(ctx.durationClause());
        }

        AssertionClause manipDisp = null;
        if (ctx.manipulationDispClause() != null) {
            manipDisp = (AssertionClause) myNodes.removeFrom(ctx.manipulationDispClause());
        }

        myNodes.put(ctx, new PerformanceSpecInitFinalItem(createLocation(ctx),
                PerformanceSpecInitFinalItem.ItemType.FINALIZATION, duration, manipDisp));
    }

    // -----------------------------------------------------------
    // Operation-related declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void enterProcedureDecl(ResolveParser.ProcedureDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
        ResolveParser.OperationParameterListContext parameterList = ctx.operationParameterList();
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a procedure declaration. Any syntactic sugar will be taken care of
     * before we are done processing this node.
     * </p>
     *
     * @param ctx
     *            Procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void exitProcedureDecl(ResolveParser.ProcedureDeclContext ctx) {
        // Return type (if any)
        Ty returnTy = null;
        if (ctx.programNamedType() != null) {
            returnTy = (Ty) myNodes.removeFrom(ctx.programNamedType());
        }

        // Affects clause (if any)
        AffectsClause affectsClause = null;
        if (ctx.affectsClause() != null) {
            affectsClause = (AffectsClause) myNodes.removeFrom(ctx.affectsClause());
        }

        // Create the procedure declaration that we are going to perform
        // the syntactic sugar conversions on.
        ProcedureDec beforeConversionProcDec = new ProcedureDec(createPosSymbol(ctx.name),
                getParameterDecls(ctx.operationParameterList().parameterDecl()), returnTy, affectsClause,
                getFacilityDecls(ctx.facilityDecl()), getVarDecls(ctx.variableDecl()),
                Utilities.collect(Statement.class, ctx.stmt(), myNodes));

        // Attempt to resolve all the syntactic sugar conversions
        SyntacticSugarConverter converter = new SyntacticSugarConverter(myArrayNameTyToInnerTyMap, myCopyTRList,
                myCopySSRList, myNewElementCounter);
        TreeWalker.visit(converter, beforeConversionProcDec);

        // Obtain the new ProcedureDec generated by the converter
        ProcedureDec afterConversionProcDec = (ProcedureDec) converter.getProcessedElement();
        myNewElementCounter = converter.getNewElementCounter();

        myNodes.put(ctx, afterConversionProcDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Recursive procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void enterRecursiveProcedureDecl(ResolveParser.RecursiveProcedureDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a recursive procedure declaration. Any syntactic sugar will be
     * taken care of before we are done processing this node.
     * </p>
     *
     * @param ctx
     *            Recursive procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void exitRecursiveProcedureDecl(ResolveParser.RecursiveProcedureDeclContext ctx) {
        // Return type (if any)
        Ty returnTy = null;
        if (ctx.programNamedType() != null) {
            returnTy = (Ty) myNodes.removeFrom(ctx.programNamedType());
        }

        // Affects clause (if any)
        AffectsClause affectsClause = null;
        if (ctx.affectsClause() != null) {
            affectsClause = (AffectsClause) myNodes.removeFrom(ctx.affectsClause());
        }

        // Decreasing clause
        AssertionClause decreasingClause = (AssertionClause) myNodes.removeFrom(ctx.decreasingClause());

        // Create the procedure declaration that we are going to perform
        // the syntactic sugar conversions on.
        ProcedureDec beforeConversionProcDec = new ProcedureDec(createPosSymbol(ctx.name),
                getParameterDecls(ctx.operationParameterList().parameterDecl()), returnTy, affectsClause,
                decreasingClause, getFacilityDecls(ctx.facilityDecl()), getVarDecls(ctx.variableDecl()),
                Utilities.collect(Statement.class, ctx.stmt(), myNodes), true);

        // Attempt to resolve all the syntactic sugar conversions
        SyntacticSugarConverter converter = new SyntacticSugarConverter(myArrayNameTyToInnerTyMap, myCopyTRList,
                myCopySSRList, myNewElementCounter);
        TreeWalker.visit(converter, beforeConversionProcDec);

        // Obtain the new ProcedureDec generated by the converter
        ProcedureDec afterConversionProcDec = (ProcedureDec) converter.getProcessedElement();
        myNodes.put(ctx, afterConversionProcDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Operation procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void enterOperationProcedureDecl(ResolveParser.OperationProcedureDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for an operation procedure declaration. Any syntactic sugar will be
     * taken care of before we are done processing this node.
     * </p>
     *
     * @param ctx
     *            Operation procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void exitOperationProcedureDecl(ResolveParser.OperationProcedureDeclContext ctx) {
        // Operation declaration
        OperationDec operationDec = (OperationDec) myNodes.removeFrom(ctx.operationDecl());

        // Create the local operation procedure declaration that
        // we are going to perform the syntactic sugar conversions on.
        OperationProcedureDec beforeConversionOpProcDec = new OperationProcedureDec(operationDec,
                getFacilityDecls(ctx.facilityDecl()), getVarDecls(ctx.variableDecl()),
                Utilities.collect(Statement.class, ctx.stmt(), myNodes));

        // Attempt to resolve all the syntactic sugar conversions
        SyntacticSugarConverter converter = new SyntacticSugarConverter(myArrayNameTyToInnerTyMap, myCopyTRList,
                myCopySSRList, myNewElementCounter);
        TreeWalker.visit(converter, beforeConversionOpProcDec);

        // Obtain the new OperationProcedureDec generated by the converter
        OperationProcedureDec afterConversionOpProcDec = (OperationProcedureDec) converter.getProcessedElement();
        myNodes.put(ctx, afterConversionOpProcDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * We create a new object to store all the new array facilities that can be created by the syntactic sugar
     * conversions.
     * </p>
     *
     * @param ctx
     *            Recursive operation procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void enterRecursiveOperationProcedureDecl(ResolveParser.RecursiveOperationProcedureDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack.push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a recursive operation procedure declaration. Any syntactic sugar
     * will be taken care of before we are done processing this node.
     * </p>
     *
     * @param ctx
     *            Recursive operation procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void exitRecursiveOperationProcedureDecl(ResolveParser.RecursiveOperationProcedureDeclContext ctx) {
        // Operation declaration
        OperationDec operationDec = (OperationDec) myNodes.removeFrom(ctx.operationDecl());

        // Decreasing clause
        AssertionClause decreasingClause = (AssertionClause) myNodes.removeFrom(ctx.decreasingClause());

        // Create the local operation procedure declaration that
        // we are going to perform the syntactic sugar conversions on.
        OperationProcedureDec beforeConversionOpProcDec = new OperationProcedureDec(operationDec, decreasingClause,
                getFacilityDecls(ctx.facilityDecl()), getVarDecls(ctx.variableDecl()),
                Utilities.collect(Statement.class, ctx.stmt(), myNodes), true);

        // Attempt to resolve all the syntactic sugar conversions
        SyntacticSugarConverter converter = new SyntacticSugarConverter(myArrayNameTyToInnerTyMap, myCopyTRList,
                myCopySSRList, myNewElementCounter);
        TreeWalker.visit(converter, beforeConversionOpProcDec);

        // Obtain the new OperationProcedureDec generated by the converter
        OperationProcedureDec afterConversionOpProcDec = (OperationProcedureDec) converter.getProcessedElement();
        myNodes.put(ctx, afterConversionOpProcDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for an operation declaration.
     * </p>
     *
     * @param ctx
     *            Operation declaration node in ANTLR4 AST.
     */
    @Override
    public void exitOperationDecl(ResolveParser.OperationDeclContext ctx) {// Parameters
        List<ParameterVarDec> varDecs = getParameterDecls(ctx.operationParameterList().parameterDecl());

        // Return type (if any)
        Ty returnTy = null;
        if (ctx.programNamedType() != null) {
            returnTy = (Ty) myNodes.removeFrom(ctx.programNamedType());
        }

        // Affects clause (if any)
        AffectsClause affectsClause = null;
        if (ctx.affectsClause() != null) {
            affectsClause = (AffectsClause) myNodes.removeFrom(ctx.affectsClause());
        }

        // Requires and ensures
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires = (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        } else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        AssertionClause ensures;
        if (ctx.ensuresClause() != null) {
            ensures = (AssertionClause) myNodes.removeFrom(ctx.ensuresClause());
        } else {
            ensures = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES);
        }

        // Build the operation declaration
        OperationDec dec = new OperationDec(createPosSymbol(ctx.name), varDecs, returnTy, affectsClause, requires,
                ensures);

        // If this is a function operation declaration, then we need to make sure
        // it is a valid declaration.
        if (dec.getReturnTy() != null) {
            ValidFunctionOpDeclChecker declChecker = new ValidFunctionOpDeclChecker(dec);
            declChecker.checkFunctionOpDecl();
        }

        Exp assertionExp = dec.getEnsures().getAssertionExp();
        for (ParameterVarDec pvd : dec.getParameters()) {
            switch (pvd.getMode()) {
                case RESTORES:
                case PRESERVES:
                case EVALUATES:
                case REPLACES:
                    if (assertionExp.containsVar(pvd.getName().asString(0, 0), true)) {
                        String str = "Ensures clause at " + assertionExp.getLocation().toString() + " contains #"
                                + pvd.getName().asString(0, 0) + " while having parameter mode "
                                + pvd.getMode().toString();
                        myStatusHandler.registerAndStreamFault(
                                new Fault(FaultType.INCORRECT_PARAMETER_MODE_USAGE, pvd.getLocation(), str, false));
                    }
                    break;
                case ALTERS:
                    if (assertionExp.containsVar(pvd.getName().asString(0, 0), false)) {
                        String str = "Ensures clause at " + assertionExp.getLocation().toString() + " contains "
                                + pvd.getName().asString(0, 0) + " while having parameter mode "
                                + pvd.getMode().toString();
                        myStatusHandler.registerAndStreamFault(
                                new Fault(FaultType.INCORRECT_PARAMETER_MODE_USAGE, pvd.getLocation(), str, false));
                    }
            }
        }

        myNodes.put(ctx, dec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for an operation declaration for performance profiles.
     * </p>
     *
     * @param ctx
     *            Operation declaration node in ANTLR4 AST.
     */
    @Override
    public void exitPerformanceOperationDecl(ResolveParser.PerformanceOperationDeclContext ctx) {
        // Operation declaration
        OperationDec operationDec = (OperationDec) myNodes.removeFrom(ctx.operationDecl());

        // duration and manipulation displacement
        AssertionClause duration = null;
        if (ctx.durationClause() != null) {
            duration = (AssertionClause) myNodes.removeFrom(ctx.durationClause());
        }

        AssertionClause manipDisp = null;
        if (ctx.manipulationDispClause() != null) {
            manipDisp = (AssertionClause) myNodes.removeFrom(ctx.manipulationDispClause());
        }

        myNodes.put(ctx, new PerformanceOperationDec(operationDec, duration, manipDisp));
    }

    // -----------------------------------------------------------
    // Facility declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a facility declaration.
     * </p>
     *
     * @param ctx
     *            Facility declaration node in ANTLR4 AST.
     */
    @Override
    public void exitFacilityDecl(ResolveParser.FacilityDeclContext ctx) {
        // Concept arguments
        List<ModuleArgumentItem> conceptArgs = new ArrayList<>();
        if (ctx.specArgs != null) {
            List<ResolveParser.ModuleArgumentContext> conceptArgContext = ctx.specArgs.moduleArgument();
            for (ResolveParser.ModuleArgumentContext context : conceptArgContext) {
                conceptArgs.add((ModuleArgumentItem) myNodes.removeFrom(context));
            }
        }

        // EnhacementSpec items
        List<EnhancementSpecItem> enhancements = Utilities.collect(EnhancementSpecItem.class,
                ctx.conceptEnhancementDecl(), myNodes);

        // Externally realized flag
        boolean externallyRealized = false;
        if (ctx.externally != null) {
            externallyRealized = true;
        }

        // Concept realization arguments
        List<ModuleArgumentItem> conceptRealizArgs = new ArrayList<>();
        if (ctx.implArgs != null) {
            List<ResolveParser.ModuleArgumentContext> conceptRealizArgContext = ctx.implArgs.moduleArgument();
            for (ResolveParser.ModuleArgumentContext context : conceptRealizArgContext) {
                conceptRealizArgs.add((ModuleArgumentItem) myNodes.removeFrom(context));
            }
        }

        // EnhacementSpecRealiz items
        List<EnhancementSpecRealizItem> enhancementBodies = Utilities.collect(EnhancementSpecRealizItem.class,
                ctx.enhancementPairDecl(), myNodes);

        // Profile name (if any)
        PosSymbol profileName = null;
        if (ctx.profile != null) {
            profileName = createPosSymbol(ctx.profile);
        }

        // Add all the modules in the facility declaration as module dependencies
        addNewModuleDependency(ctx.concept.getText(), ctx.concept.getText(), false);
        addNewModuleDependency(ctx.impl.getText(), ctx.concept.getText(), externallyRealized);
        if (ctx.profile != null) {
            addNewModuleDependency(ctx.profile.getText(), ctx.concept.getText(), false);
        }

        for (EnhancementSpecItem specItem : enhancements) {
            addNewModuleDependency(specItem.getName().getName(), ctx.concept.getText(), false);
        }

        for (EnhancementSpecRealizItem specRealizItem : enhancementBodies) {
            // Add the facility's enhancement/enhancement realization/enhancement profiles as module
            // dependencies
            addNewModuleDependency(specRealizItem.getEnhancementName().getName(), ctx.concept.getText(), false);
            addNewModuleDependency(specRealizItem.getEnhancementRealizName().getName(), ctx.concept.getText(), false);
            if (ctx.profile != null) {
                addNewModuleDependency(ctx.profile.getText(), ctx.concept.getText(), false);
            }
        }

        myNodes.put(ctx,
                new FacilityDec(createPosSymbol(ctx.name), createPosSymbol(ctx.concept), conceptArgs, enhancements,
                        createPosSymbol(ctx.impl), conceptRealizArgs, enhancementBodies, profileName,
                        externallyRealized));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for a concept enhancement declaration.
     * </p>
     *
     * @param ctx
     *            Concept enhancement declaration node in ANTLR4 AST.
     */
    @Override
    public void exitConceptEnhancementDecl(ResolveParser.ConceptEnhancementDeclContext ctx) {
        // Enhancement arguments
        List<ModuleArgumentItem> enhancementArgs = new ArrayList<>();
        if (ctx.specArgs != null) {
            List<ResolveParser.ModuleArgumentContext> enhancementArgContext = ctx.specArgs.moduleArgument();
            for (ResolveParser.ModuleArgumentContext context : enhancementArgContext) {
                enhancementArgs.add((ModuleArgumentItem) myNodes.removeFrom(context));
            }
        }

        myNodes.put(ctx, new EnhancementSpecItem(createPosSymbol(ctx.spec), enhancementArgs));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new representation for an enhancement/enhancement realization pair declaration.
     * </p>
     *
     * @param ctx
     *            Enhancement pair declaration node in ANTLR4 AST.
     */
    @Override
    public void exitEnhancementPairDecl(ResolveParser.EnhancementPairDeclContext ctx) {
        // Enhancement arguments
        List<ModuleArgumentItem> enhancementArgs = new ArrayList<>();
        if (ctx.specArgs != null) {
            List<ResolveParser.ModuleArgumentContext> enhancementArgContext = ctx.specArgs.moduleArgument();
            for (ResolveParser.ModuleArgumentContext context : enhancementArgContext) {
                enhancementArgs.add((ModuleArgumentItem) myNodes.removeFrom(context));
            }
        }

        // Profile name (if any)
        PosSymbol profileName = null;
        if (ctx.profile != null) {
            profileName = createPosSymbol(ctx.profile);
        }

        // Enhancement realization arguments
        List<ModuleArgumentItem> enhancementRealizArgs = new ArrayList<>();
        if (ctx.implArgs != null) {
            List<ResolveParser.ModuleArgumentContext> enhancementRealizArgContext = ctx.implArgs.moduleArgument();
            for (ResolveParser.ModuleArgumentContext context : enhancementRealizArgContext) {
                enhancementRealizArgs.add((ModuleArgumentItem) myNodes.removeFrom(context));
            }
        }

        myNodes.put(ctx, new EnhancementSpecRealizItem(createPosSymbol(ctx.spec), enhancementArgs,
                createPosSymbol(ctx.impl), enhancementRealizArgs, profileName));
    }

    // -----------------------------------------------------------
    // Module arguments
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * Since programming array expressions are simply syntactic sugar that gets converted to call statements, they are
     * not allowed to be passed as module argument. This method stores a boolean that indicates we are in a module
     * argument.
     * </p>
     *
     * @param ctx
     *            Module argument node in ANTLR4 AST.
     */
    @Override
    public void enterModuleArgument(ResolveParser.ModuleArgumentContext ctx) {
        myIsProcessingModuleArgument = true;
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a module argument.
     * </p>
     *
     * @param ctx
     *            Module argument node in ANTLR4 AST.
     */
    @Override
    public void exitModuleArgument(ResolveParser.ModuleArgumentContext ctx) {
        myNodes.put(ctx, new ModuleArgumentItem((ProgramExp) myNodes.removeFrom(ctx.progExp())));
        myIsProcessingModuleArgument = false;
    }

    // -----------------------------------------------------------
    // Variable declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores all math variable declarations.
     * </p>
     *
     * @param ctx
     *            Math variable declaration groups node in ANTLR4 AST.
     */
    @Override
    public void exitMathVariableDeclGroup(ResolveParser.MathVariableDeclGroupContext ctx) {
        Ty rawType = (Ty) myNodes.removeFrom(ctx.mathTypeExp());
        List<TerminalNode> varNames = ctx.IDENTIFIER();
        for (TerminalNode varName : varNames) {
            myNodes.put(varName, new MathVarDec(createPosSymbol(varName.getSymbol()), rawType.clone()));
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a math variable declaration.
     * </p>
     *
     * @param ctx
     *            Math variable declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathVariableDecl(ResolveParser.MathVariableDeclContext ctx) {
        Ty rawType = (Ty) myNodes.removeFrom(ctx.mathTypeExp());
        myNodes.put(ctx, new MathVarDec(createPosSymbol(ctx.IDENTIFIER().getSymbol()), rawType));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a programming variable declaration.
     * </p>
     *
     * @param ctx
     *            Variable declaration node in ANTLR4 AST.
     */
    @Override
    public void exitVariableDecl(ResolveParser.VariableDeclContext ctx) {
        ResolveParser.VariableDeclGroupContext variableDeclGroupContext = ctx.variableDeclGroup();

        // Obtain the raw programing type. If we encounter an array type,
        // we add a new array facility and create a new raw name type that
        // refers to this array facility.
        NameTy rawNameTy;
        if (variableDeclGroupContext.programArrayType() != null) {
            Location loc = createLocation(variableDeclGroupContext.programArrayType());

            String firstIdent = variableDeclGroupContext.IDENTIFIER(0).getText();

            rawNameTy = createNameTyFromArrayType(loc, firstIdent, variableDeclGroupContext.programArrayType());
        } else {
            rawNameTy = (NameTy) myNodes.removeFrom(variableDeclGroupContext.programNamedType());
        }

        // For each identifier, create a new variable declaration
        for (TerminalNode ident : variableDeclGroupContext.IDENTIFIER()) {
            myNodes.put(ident, new VarDec(createPosSymbol(ident.getSymbol()), rawNameTy.clone()));
        }
    }

    // -----------------------------------------------------------
    // Statements
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the statement representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Statement node in ANTLR4 AST.
     */
    @Override
    public void exitStmt(ResolveParser.StmtContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a function assignment statement.
     * </p>
     *
     * @param ctx
     *            Assign statement node in ANTLR4 AST.
     */
    @Override
    public void exitAssignStmt(ResolveParser.AssignStmtContext ctx) {
        myNodes.put(ctx, new FuncAssignStmt(createLocation(ctx), (ProgramVariableExp) myNodes.removeFrom(ctx.left),
                (ProgramExp) myNodes.removeFrom(ctx.right)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a swap statement.
     * </p>
     *
     * @param ctx
     *            Swap statement node in ANTLR4 AST.
     */
    @Override
    public void exitSwapStmt(ResolveParser.SwapStmtContext ctx) {
        myNodes.put(ctx, new SwapStmt(createLocation(ctx), (ProgramVariableExp) myNodes.removeFrom(ctx.left),
                (ProgramVariableExp) myNodes.removeFrom(ctx.right)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a call statement.
     * </p>
     *
     * @param ctx
     *            Call statement node in ANTLR4 AST.
     */
    @Override
    public void exitCallStmt(ResolveParser.CallStmtContext ctx) {
        myNodes.put(ctx,
                new CallStmt(createLocation(ctx), (ProgramFunctionExp) myNodes.removeFrom(ctx.progParamExp())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a presume statement.
     * </p>
     *
     * @param ctx
     *            Presume statement node in ANTLR4 AST.
     */
    @Override
    public void exitPresumeStmt(ResolveParser.PresumeStmtContext ctx) {
        myNodes.put(ctx, new PresumeStmt(createLocation(ctx), (Exp) myNodes.removeFrom(ctx.mathExp())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a confirm statement with {@code false} as its simplify flag.
     * </p>
     *
     * @param ctx
     *            Confirm statement node in ANTLR4 AST.
     */
    @Override
    public void exitConfirmStmt(ResolveParser.ConfirmStmtContext ctx) {
        myNodes.put(ctx, new ConfirmStmt(createLocation(ctx), (Exp) myNodes.removeFrom(ctx.mathExp()), false));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates either a {@code Remember} or a {@code Forget} statement.
     * </p>
     *
     * @param ctx
     *            Memory statement node in ANTLR4 AST.
     */
    @Override
    public void exitMemoryStmt(ResolveParser.MemoryStmtContext ctx) {
        ResolveConceptualElement element;
        if (ctx.FORGET() != null) {
            element = new MemoryStmt(createLocation(ctx), MemoryStmt.StatementType.FORGET);
        } else {
            element = new MemoryStmt(createLocation(ctx), MemoryStmt.StatementType.REMEMBER);
        }

        myNodes.put(ctx, element);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates an if statement.
     * </p>
     *
     * @param ctx
     *            If statement node in ANTLR4 AST.
     */
    @Override
    public void exitIfStmt(ResolveParser.IfStmtContext ctx) {
        // Condition
        ProgramExp conditionExp = (ProgramExp) myNodes.removeFrom(ctx.progExp());

        // Statements inside the "if"
        List<Statement> ifStmts = new ArrayList<>();
        List<ResolveParser.StmtContext> ifStmtContext = ctx.stmt();
        for (ResolveParser.StmtContext stmtContext : ifStmtContext) {
            ifStmts.add((Statement) myNodes.removeFrom(stmtContext));
        }

        // Statements inside the "else" (if any)
        List<Statement> elseStmts = new ArrayList<>();
        if (ctx.elsePart() != null) {
            List<ResolveParser.StmtContext> elseStmtContext = ctx.elsePart().stmt();
            for (ResolveParser.StmtContext stmtContext : elseStmtContext) {
                elseStmts.add((Statement) myNodes.removeFrom(stmtContext));
            }
        }

        myNodes.put(ctx,
                new IfStmt(createLocation(ctx), new IfConditionItem(createLocation(ctx), conditionExp, ifStmts),
                        new ArrayList<IfConditionItem>(), elseStmts));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a while statement.
     * </p>
     *
     * @param ctx
     *            While statement node in ANTLR4 AST.
     */
    @Override
    public void exitWhileStmt(ResolveParser.WhileStmtContext ctx) {
        // Condition
        ProgramExp conditionExp = (ProgramExp) myNodes.removeFrom(ctx.progExp());

        // Changing clause
        List<ProgramVariableExp> changingVars = new ArrayList<>();
        if (ctx.changingClause() != null) {
            List<ResolveParser.ProgVarNameExpContext> changingVarContexts = ctx.changingClause().progVarNameExp();
            for (ResolveParser.ProgVarNameExpContext context : changingVarContexts) {
                changingVars.add((ProgramVariableExp) myNodes.removeFrom(context));
            }
        }

        // Maintaining clause
        AssertionClause maintainingClause;
        if (ctx.maintainingClause() != null) {
            maintainingClause = (AssertionClause) myNodes.removeFrom(ctx.maintainingClause());
        } else {
            maintainingClause = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.MAINTAINING);
        }

        // Decreasing clause
        AssertionClause decreasingClause = (AssertionClause) myNodes.removeFrom(ctx.decreasingClause());

        // Statements inside the "while"
        List<Statement> whileStmts = new ArrayList<>();
        List<ResolveParser.StmtContext> whileStmtContext = ctx.stmt();
        for (ResolveParser.StmtContext stmtContext : whileStmtContext) {
            whileStmts.add((Statement) myNodes.removeFrom(stmtContext));
        }
        myNodes.put(ctx, new WhileStmt(createLocation(ctx), conditionExp,
                new LoopVerificationItem(createLocation(ctx), changingVars, maintainingClause, decreasingClause, null),
                whileStmts));
    }

    // -----------------------------------------------------------
    // Mathematical type theorems
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a math type theorem declaration.
     * </p>
     *
     * @param ctx
     *            Type theorem declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathTypeTheoremDecl(ResolveParser.MathTypeTheoremDeclContext ctx) {
        List<MathVarDec> varDecls = new ArrayList<>();
        List<ResolveParser.MathVariableDeclGroupContext> variableDeclGroupContexts = ctx.mathVariableDeclGroup();
        for (ResolveParser.MathVariableDeclGroupContext context : variableDeclGroupContexts) {
            // Get each math variable declaration
            List<TerminalNode> idents = context.IDENTIFIER();
            for (TerminalNode ident : idents) {
                varDecls.add((MathVarDec) myNodes.removeFrom(ident));
            }
        }
        Exp assertionExp = (Exp) myNodes.removeFrom(ctx.mathImpliesExp());

        myNodes.put(ctx, new MathTypeTheoremDec(createPosSymbol(ctx.name), varDecls, assertionExp));
    }

    // -----------------------------------------------------------
    // Mathematical theorems, corollaries, etc
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a representation of a math assertion declaration.
     * </p>
     *
     * @param ctx
     *            Math assertion declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathAssertionDecl(ResolveParser.MathAssertionDeclContext ctx) {
        ResolveConceptualElement newElement;

        Exp mathExp = (Exp) myNodes.removeFrom(ctx.mathExp());
        switch (ctx.assertionType.getType()) {
            case ResolveLexer.THEOREM:
            case ResolveLexer.THEOREM_ASSOCIATIVE:
            case ResolveLexer.THEOREM_COMMUTATIVE:
                MathAssertionDec.TheoremSubtype theoremSubtype;
                if (ctx.assertionType.getType() == ResolveLexer.THEOREM_ASSOCIATIVE) {
                    theoremSubtype = MathAssertionDec.TheoremSubtype.ASSOCIATIVITY;
                } else if (ctx.assertionType.getType() == ResolveLexer.THEOREM_COMMUTATIVE) {
                    theoremSubtype = MathAssertionDec.TheoremSubtype.COMMUTATIVITY;
                } else {
                    theoremSubtype = MathAssertionDec.TheoremSubtype.NONE;
                }

                newElement = new MathAssertionDec(createPosSymbol(ctx.name.getStart()), theoremSubtype, mathExp);
                break;
            case ResolveLexer.AXIOM:
                newElement = new MathAssertionDec(createPosSymbol(ctx.name.getStart()),
                        MathAssertionDec.AssertionType.AXIOM, mathExp);
                break;
            case ResolveLexer.COROLLARY:
                newElement = new MathAssertionDec(createPosSymbol(ctx.name.getStart()),
                        MathAssertionDec.AssertionType.COROLLARY, mathExp);
                break;
            case ResolveLexer.LEMMA:
                newElement = new MathAssertionDec(createPosSymbol(ctx.name.getStart()),
                        MathAssertionDec.AssertionType.LEMMA, mathExp);
                break;
            default:
                newElement = new MathAssertionDec(createPosSymbol(ctx.name.getStart()),
                        MathAssertionDec.AssertionType.PROPERTY, mathExp);
                break;
        }

        myNodes.put(ctx, newElement);
    }

    // -----------------------------------------------------------
    // Mathematical type definitions
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a type definition declaration.
     * </p>
     *
     * @param ctx
     *            Type definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathTypeDefinitionDecl(ResolveParser.MathTypeDefinitionDeclContext ctx) {
        myNodes.put(ctx, new TypeDefinitionDec(createPosSymbol(ctx.name), (Ty) myNodes.removeFrom(ctx.mathTypeExp())));
    }

    // -----------------------------------------------------------
    // Mathematical definitions
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method creates a temporary list to store all the temporary definition members
     * </p>
     *
     * @param ctx
     *            Defines declaration node in ANTLR4 AST.
     */
    @Override
    public void enterMathDefinesDecl(ResolveParser.MathDefinesDeclContext ctx) {
        myDefinitionMemberList = new ArrayList<>();
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a defines declaration.
     * </p>
     *
     * @param ctx
     *            Defines declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathDefinesDecl(ResolveParser.MathDefinesDeclContext ctx) {
        DefinitionMembers members = myDefinitionMemberList.remove(0);
        MathDefinitionDec definitionDec = new MathDefinitionDec(members.name, members.params, members.rawType, null,
                false);
        myDefinitionMemberList = null;

        myNodes.put(ctx, definitionDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method creates a temporary list to store all the temporary definition members
     * </p>
     *
     * @param ctx
     *            Definition declaration node in ANTLR4 AST.
     */
    @Override
    public void enterMathDefinitionDecl(ResolveParser.MathDefinitionDeclContext ctx) {
        myDefinitionMemberList = new ArrayList<>();
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the definition representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathDefinitionDecl(ResolveParser.MathDefinitionDeclContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a categorical definition declaration.
     * </p>
     *
     * @param ctx
     *            Categorical definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathCategoricalDecl(ResolveParser.MathCategoricalDeclContext ctx) {
        // Create all the definition declarations inside
        // the categorical definition
        List<MathDefinitionDec> definitionDecls = new ArrayList<>();
        for (DefinitionMembers members : myDefinitionMemberList) {
            definitionDecls.add(new MathDefinitionDec(members.name, members.params, members.rawType, null, false));
        }
        myDefinitionMemberList = null;

        myNodes.put(ctx, new MathCategoricalDefinitionDec(new PosSymbol(createLocation(ctx.start), ""), definitionDecls,
                (Exp) myNodes.removeFrom(ctx.mathExp())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates an implicit definition declaration.
     * </p>
     *
     * @param ctx
     *            Implicit definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathImplicitDefinitionDecl(ResolveParser.MathImplicitDefinitionDeclContext ctx) {
        DefinitionMembers members = myDefinitionMemberList.remove(0);
        MathDefinitionDec definitionDec = new MathDefinitionDec(members.name, members.params, members.rawType,
                new DefinitionBodyItem((Exp) myNodes.removeFrom(ctx.mathExp())), true);
        myDefinitionMemberList = null;

        myNodes.put(ctx, definitionDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates an inductive definition declaration.
     * </p>
     *
     * @param ctx
     *            Inductive definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathInductiveDefinitionDecl(ResolveParser.MathInductiveDefinitionDeclContext ctx) {
        DefinitionMembers members = myDefinitionMemberList.remove(0);
        MathDefinitionDec definitionDec = new MathDefinitionDec(members.name, members.params, members.rawType,
                new DefinitionBodyItem((Exp) myNodes.removeFrom(ctx.mathExp(0)),
                        (Exp) myNodes.removeFrom(ctx.mathExp(1))),
                false);
        myDefinitionMemberList = null;

        myNodes.put(ctx, definitionDec);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a standard definition declaration.
     * </p>
     *
     * @param ctx
     *            Standard definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathStandardDefinitionDecl(ResolveParser.MathStandardDefinitionDeclContext ctx) {
        DefinitionBodyItem bodyItem = null;
        if (ctx.mathExp() != null) {
            bodyItem = new DefinitionBodyItem((Exp) myNodes.removeFrom(ctx.mathExp()));
        }

        DefinitionMembers members = myDefinitionMemberList.remove(0);
        MathDefinitionDec definitionDec = new MathDefinitionDec(members.name, members.params, members.rawType, bodyItem,
                false);
        myDefinitionMemberList = null;

        myNodes.put(ctx, definitionDec);
    }

    // -----------------------------------------------------------
    // Standard definition signatures
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a temporary definition member object that stores all the relevant information needed by the
     * parent rule.
     * </p>
     *
     * @param ctx
     *            Infix definition signature node in ANTLR4 AST.
     */
    @Override
    public void exitStandardInfixSignature(ResolveParser.StandardInfixSignatureContext ctx) {
        PosSymbol name;
        if (ctx.IDENTIFIER() != null) {
            name = createPosSymbol(ctx.IDENTIFIER().getSymbol());
        } else {
            name = createPosSymbol(ctx.infixOp().op);
        }

        List<MathVarDec> varDecls = new ArrayList<>();
        varDecls.add((MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl(0)));
        varDecls.add((MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl(1)));

        myDefinitionMemberList.add(new DefinitionMembers(name, varDecls, (Ty) myNodes.removeFrom(ctx.mathTypeExp())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a temporary definition member object that stores all the relevant information needed by the
     * parent rule.
     * </p>
     *
     * @param ctx
     *            Outfix definition signature node in ANTLR4 AST.
     */
    @Override
    public void exitStandardOutfixSignature(ResolveParser.StandardOutfixSignatureContext ctx) {
        PosSymbol name = new PosSymbol(createLocation(ctx.lOp), ctx.lOp.getText() + "_" + ctx.rOp.getText());

        List<MathVarDec> varDecls = new ArrayList<>();
        varDecls.add((MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl()));

        myDefinitionMemberList.add(new DefinitionMembers(name, varDecls, (Ty) myNodes.removeFrom(ctx.mathTypeExp())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a temporary definition member object that stores all the relevant information needed by the
     * parent rule.
     * </p>
     *
     * @param ctx
     *            Prefix definition signature node in ANTLR4 AST.
     */
    @Override
    public void exitStandardPrefixSignature(ResolveParser.StandardPrefixSignatureContext ctx) {
        Token nameToken;
        if (ctx.prefixOp() != null) {
            nameToken = ctx.prefixOp().getStart();
        } else {
            nameToken = ctx.getStart();
        }
        PosSymbol name = createPosSymbol(nameToken);

        // We may have multiple identifiers per MathVariableDeclGroupContext, so
        // we don't do anything in exitDefinitionParameterListContext and handle the
        // logic here.
        List<MathVarDec> paramVarDecls = new ArrayList<>();
        if (ctx.definitionParameterList() != null) {
            ResolveParser.DefinitionParameterListContext definitionParameterListContext = ctx.definitionParameterList();
            List<ResolveParser.MathVariableDeclGroupContext> variableDeclGroups = definitionParameterListContext
                    .mathVariableDeclGroup();
            for (ResolveParser.MathVariableDeclGroupContext context : variableDeclGroups) {
                List<TerminalNode> identifiers = context.IDENTIFIER();
                for (TerminalNode id : identifiers) {
                    paramVarDecls.add((MathVarDec) myNodes.removeFrom(id));
                    myNodes.put(context, myNodes.removeFrom(id));
                }
            }
        }

        myDefinitionMemberList
                .add(new DefinitionMembers(name, paramVarDecls, (Ty) myNodes.removeFrom(ctx.mathTypeExp())));
    }

    // -----------------------------------------------------------
    // Different Types of Clauses
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new affects clause.
     * </p>
     *
     * @param ctx
     *            Affects clause node in ANTLR4 AST.
     */
    @Override
    public void exitAffectsClause(ResolveParser.AffectsClauseContext ctx) {
        // Obtain the list of affected expressions
        List<ResolveParser.MathVarNameExpContext> varNameExpContexts = ctx.mathVarNameExp();
        List<Exp> affectedExps = new ArrayList<>();
        for (ResolveParser.MathVarNameExpContext context : varNameExpContexts) {
            affectedExps.add((Exp) myNodes.removeFrom(context));
        }

        myNodes.put(ctx, new AffectsClause(createLocation(ctx), affectedExps));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new requires clause.
     * </p>
     *
     * @param ctx
     *            Requires clause node in ANTLR4 AST.
     */
    @Override
    public void exitRequiresClause(ResolveParser.RequiresClauseContext ctx) {
        myNodes.put(ctx,
                createAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES, ctx.mathExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new ensures clause.
     * </p>
     *
     * @param ctx
     *            Ensures clause node in ANTLR4 AST.
     */
    @Override
    public void exitEnsuresClause(ResolveParser.EnsuresClauseContext ctx) {
        myNodes.put(ctx, createAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES, ctx.mathExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new ensures clause.
     * </p>
     *
     * @param ctx
     *            Constraint clause node in ANTLR4 AST.
     */
    @Override
    public void exitConstraintClause(ResolveParser.ConstraintClauseContext ctx) {
        myNodes.put(ctx,
                createAssertionClause(createLocation(ctx), AssertionClause.ClauseType.CONSTRAINT, ctx.mathExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new maintaining clause.
     * </p>
     *
     * @param ctx
     *            Maintaining clause node in ANTLR4 AST.
     */
    @Override
    public void exitMaintainingClause(ResolveParser.MaintainingClauseContext ctx) {
        myNodes.put(ctx,
                createAssertionClause(createLocation(ctx), AssertionClause.ClauseType.MAINTAINING, ctx.mathExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new decreasing clause.
     * </p>
     *
     * @param ctx
     *            Decreasing clause node in ANTLR4 AST.
     */
    @Override
    public void exitDecreasingClause(ResolveParser.DecreasingClauseContext ctx) {
        Exp whichEntailsExp = null;
        if (ctx.mathExp() != null) {
            whichEntailsExp = (Exp) myNodes.removeFrom(ctx.mathExp());
        }
        Exp decreasingExp = (Exp) myNodes.removeFrom(ctx.mathAddingExp());

        myNodes.put(ctx, new AssertionClause(createLocation(ctx), AssertionClause.ClauseType.DECREASING, decreasingExp,
                whichEntailsExp));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new correspondence clause.
     * </p>
     *
     * @param ctx
     *            Correspondence clause node in ANTLR4 AST.
     */
    @Override
    public void exitCorrespondenceClause(ResolveParser.CorrespondenceClauseContext ctx) {
        AssertionClause.ClauseType clauseType;

        // Case #1: This is either a shared variable's realization correspondence
        // or a non-sharing type correspondence.
        if (ctx.type == null) {
            clauseType = AssertionClause.ClauseType.CORRESPONDENCE;
        } else {
            // Case #2: This is a type correspondence with some kind of sharing,
            // but it is independent of other objects of the same type.
            if (ctx.type.getType() == ResolveLexer.INDEPENDENT) {
                clauseType = AssertionClause.ClauseType.INDEPENDENT_CORRESPONDENCE;
            }
            // Case #3: This is type correspondence with some kind of sharing
            // and it is dependent on other objects of the same type.
            else {
                clauseType = AssertionClause.ClauseType.DEPENDENT_CORRESPONDENCE;
            }
        }

        if (ctx.mathVarNameExp().isEmpty()) {
            myNodes.put(ctx, createAssertionClause(createLocation(ctx), clauseType, ctx.mathExp()));
        } else {
            myNodes.put(ctx,
                    createAssertionClause(createLocation(ctx), clauseType, ctx.mathExp(), ctx.mathVarNameExp()));
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new convention clause.
     * </p>
     *
     * @param ctx
     *            Convention clause node in ANTLR4 AST.
     */
    @Override
    public void exitConventionClause(ResolveParser.ConventionClauseContext ctx) {
        myNodes.put(ctx,
                createAssertionClause(createLocation(ctx), AssertionClause.ClauseType.CONVENTION, ctx.mathExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new duration clause.
     * </p>
     *
     * @param ctx
     *            Duration clause node in ANTLR4 AST.
     */
    @Override
    public void exitDurationClause(ResolveParser.DurationClauseContext ctx) {
        Exp whichEntailsExp = null;
        if (ctx.mathExp() != null) {
            whichEntailsExp = (Exp) myNodes.removeFrom(ctx.mathExp());
        }
        Exp decreasingExp = (Exp) myNodes.removeFrom(ctx.mathAddingExp());

        myNodes.put(ctx, new AssertionClause(createLocation(ctx), AssertionClause.ClauseType.DURATION, decreasingExp,
                whichEntailsExp));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new manipulation displacement clause.
     * </p>
     *
     * @param ctx
     *            Manipulation displacement clause node in ANTLR4 AST.
     */
    @Override
    public void exitManipulationDispClause(ResolveParser.ManipulationDispClauseContext ctx) {
        Exp whichEntailsExp = null;
        if (ctx.mathExp() != null) {
            whichEntailsExp = (Exp) myNodes.removeFrom(ctx.mathExp());
        }
        Exp decreasingExp = (Exp) myNodes.removeFrom(ctx.mathAddingExp());

        myNodes.put(ctx, new AssertionClause(createLocation(ctx), AssertionClause.ClauseType.MANIPDISP, decreasingExp,
                whichEntailsExp));
    }

    // -----------------------------------------------------------
    // Arbitrary raw type built from a math expression
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new arbitrary type with the math type expression generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math type expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathTypeExp(ResolveParser.MathTypeExpContext ctx) {
        myNodes.put(ctx, new ArbitraryExpTy((Exp) myNodes.removeFrom(ctx.getChild(0))));
    }

    // -----------------------------------------------------------
    // Mathematical expressions
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathExp(ResolveParser.MathExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates an
     * iterated math expression.
     * </p>
     *
     * @param ctx
     *            Math iterated expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathIteratedExp(ResolveParser.MathIteratedExpContext ctx) {
        IterativeExp.Operator operator;
        switch (ctx.op.getType()) {
            case ResolveLexer.BIG_CONCAT:
                operator = IterativeExp.Operator.CONCATENATION;
                break;
            case ResolveLexer.BIG_INTERSECT:
                operator = IterativeExp.Operator.INTERSECTION;
                break;
            case ResolveLexer.BIG_PRODUCT:
                operator = IterativeExp.Operator.PRODUCT;
                break;
            case ResolveLexer.BIG_SUM:
                operator = IterativeExp.Operator.SUM;
                break;
            default:
                operator = IterativeExp.Operator.UNION;
                break;
        }

        MathVarDec varDecl = (MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl());
        Exp whereExp = (Exp) myNodes.removeFrom(ctx.mathWhereExp());
        Exp bodyExp = (Exp) myNodes.removeFrom(ctx.mathExp());

        myNodes.put(ctx, new IterativeExp(createLocation(ctx), operator, varDecl, whereExp, bodyExp));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates a
     * quantified math expression.
     * </p>
     *
     * @param ctx
     *            Math quantified expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathQuantifiedExp(ResolveParser.MathQuantifiedExpContext ctx) {
        ResolveConceptualElement newElement;
        ParseTree child = ctx.getChild(0);

        // Only need to construct a new ResolveConceptualElement if
        // it is a quantified expression.
        if (child instanceof ResolveParser.MathImpliesExpContext) {
            newElement = myNodes.removeFrom(child);
        } else {
            SymbolTableEntry.Quantification quantification;
            switch (ctx.getStart().getType()) {
                case ResolveLexer.FORALL:
                    quantification = SymbolTableEntry.Quantification.UNIVERSAL;
                    break;
                case ResolveLexer.EXISTS:
                    quantification = SymbolTableEntry.Quantification.EXISTENTIAL;
                    break;
                default:
                    quantification = SymbolTableEntry.Quantification.UNIQUE;
                    break;
            }

            List<MathVarDec> mathVarDecls = Utilities.collect(MathVarDec.class,
                    ctx.mathVariableDeclGroup() != null ? ctx.mathVariableDeclGroup().IDENTIFIER()
                            : new ArrayList<ParseTree>(),
                    myNodes);
            Exp whereExp = (Exp) myNodes.removeFrom(ctx.mathWhereExp());
            Exp bodyExp = (Exp) myNodes.removeFrom(ctx.mathQuantifiedExp());

            newElement = new QuantExp(createLocation(ctx), quantification, mathVarDecls, whereExp, bodyExp);
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates a new math
     * implies expression.
     * </p>
     *
     * @param ctx
     *            Math implies expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathImpliesExp(ResolveParser.MathImpliesExpContext ctx) {
        ResolveConceptualElement newElement;

        // if-then-else expressions
        if (ctx.getStart().getType() == ResolveLexer.IF) {
            Exp testExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(0));
            Exp thenExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(1));
            Exp elseExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(2));

            newElement = new IfExp(createLocation(ctx), testExp, thenExp, elseExp);
        }
        // iff and implies expressions
        else if (ctx.op != null) {
            Exp leftExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(0));
            Exp rightExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(1));

            newElement = new InfixExp(createLocation(ctx), leftExp, null, createPosSymbol(ctx.op), rightExp);
        } else {
            newElement = myNodes.removeFrom(ctx.mathLogicalExp(0));
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates a new math
     * infix expression that contains all the logical expressions.
     * </p>
     *
     * @param ctx
     *            Math logical expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathLogicalExp(ResolveParser.MathLogicalExpContext ctx) {
        ResolveConceptualElement newElement;
        List<ResolveParser.MathRelationalExpContext> relationalExpContexts = ctx.mathRelationalExp();

        // relational expressions
        if (relationalExpContexts.size() == 1) {
            newElement = myNodes.removeFrom(ctx.mathRelationalExp(0));
        }
        // build logical expressions
        else {
            // Obtain all the expressions
            List<Exp> exps = new ArrayList<>();
            for (ResolveParser.MathRelationalExpContext context : relationalExpContexts) {
                exps.add((Exp) myNodes.removeFrom(context));
            }

            // Reduce the expressions until we have 1 left
            while (exps.size() > 1) {
                Exp leftExp = exps.remove(0);
                Exp rightExp = exps.remove(0);

                // Form an infix expression using the operator
                Exp newFirstExp = new InfixExp(leftExp.getLocation().clone(), leftExp, null, createPosSymbol(ctx.op),
                        rightExp);

                // Add it back to the list for the next iteration of the loop
                exps.add(0, newFirstExp);
            }

            // Remove the final element from the list
            newElement = exps.remove(0);
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules, generates a new math
     * between expression or generates a new math infix expression with the specified operators.
     * </p>
     *
     * @param ctx
     *            Math relational expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathRelationalExp(ResolveParser.MathRelationalExpContext ctx) {
        ResolveConceptualElement newElement;

        // Check to see if this a between expression
        if (ctx.op1 != null && ctx.op2 != null) {
            // Obtain the 3 expressions
            Exp exp1 = (Exp) myNodes.removeFrom(ctx.mathInfixExp(0));
            Exp exp2 = (Exp) myNodes.removeFrom(ctx.mathInfixExp(1));
            Exp exp3 = (Exp) myNodes.removeFrom(ctx.mathInfixExp(2));

            List<Exp> joiningExps = new ArrayList<>();
            joiningExps.add(new InfixExp(exp1.getLocation().clone(), exp1.clone(), null, createPosSymbol(ctx.op1),
                    exp2.clone()));
            joiningExps.add(new InfixExp(exp2.getLocation().clone(), exp2.clone(), null, createPosSymbol(ctx.op2),
                    exp3.clone()));

            newElement = new BetweenExp(createLocation(ctx), joiningExps);
        } else {
            // Create a math infix expression with the specified operator if needed
            if (ctx.mathInfixExp().size() > 1) {
                // Obtain the 2 expressions
                Exp exp1 = (Exp) myNodes.removeFrom(ctx.mathInfixExp(0));
                Exp exp2 = (Exp) myNodes.removeFrom(ctx.mathInfixExp(1));

                switch (ctx.op.getType()) {
                    case ResolveLexer.EQL:
                    case ResolveLexer.NOT_EQL:
                        EqualsExp.Operator op;
                        if (ctx.op.getType() == ResolveLexer.EQL) {
                            op = EqualsExp.Operator.EQUAL;
                        } else {
                            op = EqualsExp.Operator.NOT_EQUAL;
                        }

                        newElement = new EqualsExp(createLocation(ctx), exp1, null, op, exp2);
                        break;
                    default:
                        newElement = new InfixExp(createLocation(ctx), exp1, null, createPosSymbol(ctx.op), exp2);
                        break;
                }
            } else {
                newElement = myNodes.removeFrom(ctx.mathInfixExp(0));
            }
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates a new math
     * infix expression with a range operator.
     * </p>
     *
     * @param ctx
     *            Math infix expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathInfixExp(ResolveParser.MathInfixExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create a math infix expression with a range operator if needed
        if (ctx.mathTypeAssertionExp().size() > 1) {
            newElement = new InfixExp(createLocation(ctx), (Exp) myNodes.removeFrom(ctx.mathTypeAssertionExp(0)), null,
                    createPosSymbol(ctx.RANGE().getSymbol()), (Exp) myNodes.removeFrom(ctx.mathTypeAssertionExp(1)));
        } else {
            newElement = myNodes.removeFrom(ctx.mathTypeAssertionExp(0));
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates a new math
     * type assertion expression.
     * </p>
     *
     * @param ctx
     *            Math type assertion expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathTypeAssertionExp(ResolveParser.MathTypeAssertionExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create a math type assertion expression if needed
        if (ctx.mathTypeExp() != null) {
            newElement = new TypeAssertionExp(createLocation(ctx), (Exp) myNodes.removeFrom(ctx.mathFunctionTypeExp()),
                    (ArbitraryExpTy) myNodes.removeFrom(ctx.mathTypeExp()));
        } else {
            newElement = myNodes.removeFrom(ctx.mathFunctionTypeExp());
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates a new math
     * function type expression.
     * </p>
     *
     * @param ctx
     *            Math function type expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathFunctionTypeExp(ResolveParser.MathFunctionTypeExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create an function type expression if needed
        if (ctx.mathAddingExp().size() > 1) {
            newElement = new InfixExp(createLocation(ctx), (Exp) myNodes.removeFrom(ctx.mathAddingExp(0)), null,
                    createPosSymbol(ctx.FUNCARROW().getSymbol()), (Exp) myNodes.removeFrom(ctx.mathAddingExp(1)));
        } else {
            newElement = myNodes.removeFrom(ctx.mathAddingExp(0));
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates a new math
     * adding expression.
     * </p>
     *
     * @param ctx
     *            Math adding expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathAddingExp(ResolveParser.MathAddingExpContext ctx) {
        // Our left most expression
        Exp exp = (Exp) myNodes.removeFrom(ctx.mathMultiplyingExp());

        // Create a repeated expression if needed
        List<ResolveParser.MathRepeatAddExpContext> mathRepeatAddExpContexts = ctx.mathRepeatAddExp();
        for (ResolveParser.MathRepeatAddExpContext context : mathRepeatAddExpContexts) {
            PosSymbol qualifier = null;
            if (context.qualifier != null) {
                qualifier = createPosSymbol(context.qualifier);
            }

            // Obtain the left and right expressions
            Exp leftExp = exp;
            Exp rightExp = (Exp) myNodes.removeFrom(context.mathMultiplyingExp());

            // Form an infix expression using the operator
            exp = new InfixExp(leftExp.getLocation().clone(), leftExp, qualifier, createPosSymbol(context.op),
                    rightExp);
        }

        myNodes.put(ctx, exp);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates a new math
     * multiplication expression.
     * </p>
     *
     * @param ctx
     *            Math multiplication expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathMultiplyingExp(ResolveParser.MathMultiplyingExpContext ctx) {
        // Our left most expression
        Exp exp = (Exp) myNodes.removeFrom(ctx.mathExponentialExp());

        // Create a repeated expression if needed
        List<ResolveParser.MathRepeatMultExpContext> mathRepeatMultExpContexts = ctx.mathRepeatMultExp();
        for (ResolveParser.MathRepeatMultExpContext context : mathRepeatMultExpContexts) {
            PosSymbol qualifier = null;
            if (context.qualifier != null) {
                qualifier = createPosSymbol(context.qualifier);
            }

            // Obtain the left and right expressions
            Exp leftExp = exp;
            Exp rightExp = (Exp) myNodes.removeFrom(context.mathExponentialExp());

            // Form an infix expression using the operator
            exp = new InfixExp(leftExp.getLocation().clone(), leftExp, qualifier, createPosSymbol(context.op),
                    rightExp);
        }

        myNodes.put(ctx, exp);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates a new math
     * exponential expression.
     * </p>
     *
     * @param ctx
     *            Math exponential expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathExponentialExp(ResolveParser.MathExponentialExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create a exponential expression if needed
        if (ctx.mathExponentialExp() != null) {
            newElement = new InfixExp(createLocation(ctx), (Exp) myNodes.removeFrom(ctx.mathPrefixExp()), null,
                    createPosSymbol(ctx.EXP().getSymbol()), (Exp) myNodes.removeFrom(ctx.mathExponentialExp()));
        } else {
            newElement = myNodes.removeFrom(ctx.mathPrefixExp());
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expression representation generated by its child rules or generates a new math
     * prefix expression.
     * </p>
     *
     * @param ctx
     *            Math prefix expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathPrefixExp(ResolveParser.MathPrefixExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create a prefix expression if needed
        if (ctx.prefixOp() != null) {
            PosSymbol qualifier = null;
            if (ctx.qualifier != null) {
                qualifier = createPosSymbol(ctx.qualifier);
            }

            newElement = new PrefixExp(createLocation(ctx), qualifier, createPosSymbol(ctx.prefixOp().op),
                    (Exp) myNodes.removeFrom(ctx.mathPrimaryExp()));
        } else {
            newElement = myNodes.removeFrom(ctx.mathPrimaryExp());
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a math expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math primary expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathPrimaryExp(ResolveParser.MathPrimaryExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the mathematical alternative expression.
     * </p>
     *
     * @param ctx
     *            Math alternative expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathAlternativeExp(ResolveParser.MathAlternativeExpContext ctx) {
        List<ResolveParser.MathAlternativeExpItemContext> mathExps = ctx.mathAlternativeExpItem();
        List<AltItemExp> alternatives = new ArrayList<>();
        for (ResolveParser.MathAlternativeExpItemContext context : mathExps) {
            alternatives.add((AltItemExp) myNodes.removeFrom(context));
        }

        myNodes.put(ctx, new AlternativeExp(createLocation(ctx), alternatives));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the different alternatives for the mathematical alternative expression.
     * </p>
     *
     * @param ctx
     *            Math alternative expression item node in ANTLR4 AST.
     */
    @Override
    public void exitMathAlternativeExpItem(ResolveParser.MathAlternativeExpItemContext ctx) {
        Exp testExp = null;
        if (ctx.mathLogicalExp() != null) {
            testExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp());
        }

        myNodes.put(ctx, new AltItemExp(createLocation(ctx), testExp, (Exp) myNodes.removeFrom(ctx.mathAddingExp())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math boolean literal.
     * </p>
     *
     * @param ctx
     *            Math boolean literal node in ANTLR4 AST.
     */
    @Override
    public void exitMathBooleanExp(ResolveParser.MathBooleanExpContext ctx) {
        myNodes.put(ctx, new VarExp(createLocation(ctx.BOOLEAN_LITERAL().getSymbol()), null,
                createPosSymbol(ctx.BOOLEAN_LITERAL().getSymbol())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math integer literal.
     * </p>
     *
     * @param ctx
     *            Math integer literal node in ANTLR4 AST.
     */
    @Override
    public void exitMathIntegerExp(ResolveParser.MathIntegerExpContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        myNodes.put(ctx,
                new IntegerExp(createLocation(ctx), qualifier, Integer.valueOf(ctx.INTEGER_LITERAL().getText())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math real literal.
     * </p>
     *
     * @param ctx
     *            Math real literal node in ANTLR4 AST.
     */
    @Override
    public void exitMathRealExp(ResolveParser.MathRealExpContext ctx) {
        myNodes.put(ctx, new DoubleExp(createLocation(ctx.REAL_LITERAL().getSymbol()),
                Double.valueOf(ctx.REAL_LITERAL().getText())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math character literal.
     * </p>
     *
     * @param ctx
     *            Math character literal node in ANTLR4 AST.
     */
    @Override
    public void exitMathCharacterExp(ResolveParser.MathCharacterExpContext ctx) {
        myNodes.put(ctx, new CharExp(createLocation(ctx.CHARACTER_LITERAL().getSymbol()),
                ctx.CHARACTER_LITERAL().getText().charAt(1)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math string literal.
     * </p>
     *
     * @param ctx
     *            Math string literal node in ANTLR4 AST.
     */
    @Override
    public void exitMathStringExp(ResolveParser.MathStringExpContext ctx) {
        myNodes.put(ctx,
                new StringExp(createLocation(ctx.STRING_LITERAL().getSymbol()), ctx.STRING_LITERAL().getText()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method either stores the math expressions representation generated by its child rules or generates a new
     * math dotted expression.
     * </p>
     *
     * @param ctx
     *            Math dot expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathDotExp(ResolveParser.MathDotExpContext ctx) {
        ResolveConceptualElement newElement;

        // Check to see if is a receptacles expression
        if (ctx.mathRecpExp() != null) {
            newElement = myNodes.removeFrom(ctx.mathRecpExp());
        } else if (ctx.mathTypeReceptaclesExp() != null) {
            newElement = myNodes.removeFrom(ctx.mathTypeReceptaclesExp());
        } else {
            // Create a dot expression if needed
            List<ResolveParser.MathCleanFunctionExpContext> mathExps = ctx.mathCleanFunctionExp();
            if (mathExps.size() > 0) {
                // dotted expressions
                List<Exp> dotExps = new ArrayList<>();

                dotExps.add((Exp) myNodes.removeFrom(ctx.mathFunctionApplicationExp()));
                for (ResolveParser.MathCleanFunctionExpContext context : mathExps) {
                    dotExps.add((Exp) myNodes.removeFrom(context));
                }

                newElement = new DotExp(createLocation(ctx), dotExps);
            } else {
                newElement = myNodes.removeFrom(ctx.mathFunctionApplicationExp());
            }
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a math function expression or variable expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math function or variable expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathFunctOrVarExp(ResolveParser.MathFunctOrVarExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.mathCleanFunctionExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math old expression representation.
     * </p>
     *
     * @param ctx
     *            Math old expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathOldExp(ResolveParser.MathOldExpContext ctx) {
        myNodes.put(ctx, new OldExp(createLocation(ctx), (Exp) myNodes.removeFrom(ctx.mathCleanFunctionExp())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math function expression representation.
     * </p>
     *
     * @param ctx
     *            Math function expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathFunctionExp(ResolveParser.MathFunctionExpContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        // function name
        VarExp functionNameExp = new VarExp(createLocation(ctx.name), qualifier, createPosSymbol(ctx.name));

        // exponent-like part to the name
        Exp caratExp = (Exp) myNodes.removeFrom(ctx.mathNestedExp());

        // function arguments
        List<ResolveParser.MathExpContext> mathExps = ctx.mathExp();
        List<Exp> functionArgs = new ArrayList<>();
        for (ResolveParser.MathExpContext context : mathExps) {
            functionArgs.add((Exp) myNodes.removeFrom(context));
        }

        myNodes.put(ctx, new FunctionExp(createLocation(ctx), functionNameExp, caratExp, functionArgs));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math type receptacles expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math type receptacles node in ANTLR4 AST.
     */
    @Override
    public void exitMathTypeRecpExp(ResolveParser.MathTypeRecpExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.mathTypeReceptaclesExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math variable expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math variable expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathVarExp(ResolveParser.MathVarExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.mathVarNameExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math variable name expression representation.
     * </p>
     *
     * @param ctx
     *            Math variable name expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathVarNameExp(ResolveParser.MathVarNameExpContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        myNodes.put(ctx, new VarExp(createLocation(ctx), qualifier, createPosSymbol(ctx.name)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math operator name expression representation.
     * </p>
     *
     * @param ctx
     *            Math operator name expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathOpNameExp(ResolveParser.MathOpNameExpContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        Token opToken;
        if (ctx.infixOp() != null) {
            opToken = ctx.infixOp().op;
        } else {
            opToken = ctx.op;
        }

        myNodes.put(ctx, new VarExp(createLocation(ctx), qualifier, createPosSymbol(opToken)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math outfix expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math outfix expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathOutfixExp(ResolveParser.MathOutfixExpContext ctx) {
        OutfixExp.Operator operator;
        if (ctx.lop.getType() == ResolveLexer.LT) {
            operator = OutfixExp.Operator.ANGLE;
        } else if (ctx.lop.getType() == ResolveLexer.LL) {
            operator = OutfixExp.Operator.DBL_ANGLE;
        } else if (ctx.lop.getType() == ResolveLexer.BAR) {
            operator = OutfixExp.Operator.BAR;
        } else {
            operator = OutfixExp.Operator.DBL_BAR;
        }

        // math expression
        Exp mathExp;
        if (ctx.mathExp() != null) {
            mathExp = (Exp) myNodes.removeFrom(ctx.mathExp());
        } else {
            mathExp = (Exp) myNodes.removeFrom(ctx.mathInfixExp());
        }

        myNodes.put(ctx, new OutfixExp(createLocation(ctx), operator, mathExp));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math set builder expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math set builder expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathSetBuilderExp(ResolveParser.MathSetBuilderExpContext ctx) {
        MathVarDec varDec = (MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl());
        Exp whereExp = (Exp) myNodes.removeFrom(ctx.mathWhereExp());
        Exp bodyExp = (Exp) myNodes.removeFrom(ctx.mathExp());

        myNodes.put(ctx, new SetExp(createLocation(ctx), varDec, whereExp, bodyExp));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math set collection expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math set collection expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathSetCollectionExp(ResolveParser.MathSetCollectionExpContext ctx) {
        List<ResolveParser.MathExpContext> mathExps = ctx.mathExp();
        Set<MathExp> mathExpsSet = new HashSet<>();
        for (ResolveParser.MathExpContext context : mathExps) {
            mathExpsSet.add((MathExp) myNodes.removeFrom(context));
        }

        myNodes.put(ctx, new SetCollectionExp(createLocation(ctx), mathExpsSet));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math type receptacles expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math recep expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathRecpExp(ResolveParser.MathRecpExpContext ctx) {
        Exp innerExp;
        if (ctx.mathDotExp() != null) {
            innerExp = (DotExp) myNodes.removeFrom(ctx.mathDotExp());
        } else {
            innerExp = (VarExp) myNodes.removeFrom(ctx.mathVarNameExp());
        }

        myNodes.put(ctx, new RecpExp(createLocation(ctx), innerExp));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math type receptacles expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math type receptacles expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathTypeReceptaclesExp(ResolveParser.MathTypeReceptaclesExpContext ctx) {
        myNodes.put(ctx,
                new TypeReceptaclesExp(createLocation(ctx), (VarExp) myNodes.removeFrom(ctx.mathVarNameExp())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math tuple expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math tuple expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathTupleExp(ResolveParser.MathTupleExpContext ctx) {
        // Add the two expressions inside the tuple to a list
        List<Exp> tupleExps = new ArrayList<>();
        tupleExps.add((Exp) myNodes.removeFrom(ctx.mathExp(0)));
        tupleExps.add((Exp) myNodes.removeFrom(ctx.mathExp(1)));

        myNodes.put(ctx, new TupleExp(createLocation(ctx), tupleExps));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math lambda expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math lambda expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathLambdaExp(ResolveParser.MathLambdaExpContext ctx) {
        // Construct the various variables inside the lambda expression
        List<ResolveParser.MathVariableDeclGroupContext> variableDeclGroups = ctx.mathVariableDeclGroup();
        List<MathVarDec> varDecls = new ArrayList<>();
        for (ResolveParser.MathVariableDeclGroupContext context : variableDeclGroups) {
            // Get each math variable declaration
            List<TerminalNode> idents = context.IDENTIFIER();
            for (TerminalNode ident : idents) {
                varDecls.add((MathVarDec) myNodes.removeFrom(ident));
            }
        }

        // body expression
        Exp bodyExp = (Exp) myNodes.removeFrom(ctx.mathExp());

        myNodes.put(ctx, new LambdaExp(createLocation(ctx), varDecls, bodyExp));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math Cartesian product expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math Cartesian product expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathTaggedCartProdTypeExp(ResolveParser.MathTaggedCartProdTypeExpContext ctx) {
        // Construct the various variables inside the cartesian product
        List<ResolveParser.MathVariableDeclGroupContext> variableDeclGroups = ctx.mathVariableDeclGroup();
        Map<PosSymbol, ArbitraryExpTy> tagsToFieldsMap = new LinkedHashMap<>();
        for (ResolveParser.MathVariableDeclGroupContext context : variableDeclGroups) {
            // Get each math variable declaration
            List<TerminalNode> idents = context.IDENTIFIER();
            for (TerminalNode ident : idents) {
                MathVarDec varDec = (MathVarDec) myNodes.removeFrom(ident);
                tagsToFieldsMap.put(varDec.getName(), (ArbitraryExpTy) varDec.getTy());
            }
        }

        myNodes.put(ctx, new CrossTypeExp(createLocation(ctx), tagsToFieldsMap));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the nested math expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Nested math expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathNestedExp(ResolveParser.MathNestedExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.mathExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the math expression representing the where clause generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Math where expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathWhereExp(ResolveParser.MathWhereExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.mathExp()));
    }

    // -----------------------------------------------------------
    // Programming expressions
    // -----------------------------------------------------------

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a program application expression. This is really a syntactic sugar for the different function
     * calls, so all we are returning are program function expressions.
     * </p>
     *
     * @param ctx
     *            Program application expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgApplicationExp(ResolveParser.ProgApplicationExpContext ctx) {
        Location functionNameLoc = createLocation(ctx.op);
        PosSymbol functionName;
        switch (ctx.op.getType()) {
            case ResolveLexer.AND:
                functionName = new PosSymbol(functionNameLoc, "And");
                break;
            case ResolveLexer.OR:
                functionName = new PosSymbol(functionNameLoc, "Or");
                break;
            case ResolveLexer.EQL:
                functionName = new PosSymbol(functionNameLoc, "Are_Equal");
                break;
            case ResolveLexer.NOT_EQL:
                functionName = new PosSymbol(functionNameLoc, "Are_Not_Equal");
                break;
            case ResolveLexer.LT:
                functionName = new PosSymbol(functionNameLoc, "Less");
                break;
            case ResolveLexer.LT_EQL:
                functionName = new PosSymbol(functionNameLoc, "Less_Or_Equal");
                break;
            case ResolveLexer.GT:
                functionName = new PosSymbol(functionNameLoc, "Greater");
                break;
            case ResolveLexer.GT_EQL:
                functionName = new PosSymbol(functionNameLoc, "Greater_Or_Equal");
                break;
            case ResolveLexer.PLUS:
                functionName = new PosSymbol(functionNameLoc, "Sum");
                break;
            case ResolveLexer.MINUS:
                functionName = new PosSymbol(functionNameLoc, "Difference");
                break;
            case ResolveLexer.MULTIPLY:
                functionName = new PosSymbol(functionNameLoc, "Product");
                break;
            case ResolveLexer.DIVIDE:
                functionName = new PosSymbol(functionNameLoc, "Divide");
                break;
            case ResolveLexer.MOD:
                functionName = new PosSymbol(functionNameLoc, "Mod");
                break;
            case ResolveLexer.REM:
                functionName = new PosSymbol(functionNameLoc, "Rem");
                break;
            default:
                functionName = new PosSymbol(functionNameLoc, "Div");
                break;
        }

        List<ProgramExp> args = new ArrayList<>();
        args.add((ProgramExp) myNodes.removeFrom(ctx.progExp(0)));
        args.add((ProgramExp) myNodes.removeFrom(ctx.progExp(1)));

        myNodes.put(ctx, new ProgramFunctionExp(createLocation(ctx), null, functionName, args));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a program exponent expressions representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Program exponential expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgExponentialExp(ResolveParser.ProgExponentialExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progExponential()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a program exponential expressions. This is really a syntactic sugar for the different function
     * calls, so all we are returning are program function expressions.
     * </p>
     *
     * @param ctx
     *            Program exponential node in ANTLR4 AST.
     */
    @Override
    public void exitProgExponential(ResolveParser.ProgExponentialContext ctx) {
        ResolveConceptualElement newElement;

        // Create new function expression if needed
        if (ctx.EXP() != null) {
            PosSymbol functionName = new PosSymbol(createLocation(ctx.EXP().getSymbol()), "Power");
            List<ProgramExp> args = new ArrayList<>();
            args.add((ProgramExp) myNodes.removeFrom(ctx.progUnary()));
            args.add((ProgramExp) myNodes.removeFrom(ctx.progExponential()));

            newElement = new ProgramFunctionExp(createLocation(ctx), null, functionName, args);
        } else {
            newElement = myNodes.removeFrom(ctx.progUnary());
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a program unary expressions. This is really a syntactic sugar for the different function
     * calls, so all we are returning are program function expressions.
     * </p>
     *
     * @param ctx
     *            Program unary expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgUnaryExp(ResolveParser.ProgUnaryExpContext ctx) {
        Location functionNameLoc = createLocation(ctx.op);
        PosSymbol functionName;
        List<ProgramExp> args = new ArrayList<>();
        switch (ctx.op.getType()) {
            case ResolveLexer.NOT:
                functionName = new PosSymbol(functionNameLoc, "Not");
                args.add((ProgramExp) myNodes.removeFrom(ctx.progExp()));
                break;
            default:
                functionName = new PosSymbol(functionNameLoc, "Negate");
                args.add((ProgramExp) myNodes.removeFrom(ctx.progExp()));
                break;
        }

        myNodes.put(ctx, new ProgramFunctionExp(createLocation(ctx), null, functionName, args));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a program primary expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Program primary expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgPrimaryExp(ResolveParser.ProgPrimaryExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progPrimary()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a program literal expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Program literal expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgLiteralExp(ResolveParser.ProgLiteralExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progLiteral()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a program function expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Program function expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgFunctionExp(ResolveParser.ProgFunctionExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progParamExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a program variable expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Program var expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVarExp(ResolveParser.ProgVarExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progVariableExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a program nested expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Program nested expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgNestedExp(ResolveParser.ProgNestedExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progExp()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the program integer literal.
     * </p>
     *
     * @param ctx
     *            Program integer literal node in ANTLR4 AST.
     */
    @Override
    public void exitProgIntegerExp(ResolveParser.ProgIntegerExpContext ctx) {
        myNodes.put(ctx, new ProgramIntegerExp(createLocation(ctx.INTEGER_LITERAL().getSymbol()),
                Integer.valueOf(ctx.INTEGER_LITERAL().getText())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the program character literal.
     * </p>
     *
     * @param ctx
     *            Program character literal node in ANTLR4 AST.
     */
    @Override
    public void exitProgCharacterExp(ResolveParser.ProgCharacterExpContext ctx) {
        myNodes.put(ctx, new ProgramCharExp(createLocation(ctx.CHARACTER_LITERAL().getSymbol()),
                ctx.CHARACTER_LITERAL().getText().charAt(1)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the program string literal.
     * </p>
     *
     * @param ctx
     *            Program string literal node in ANTLR4 AST.
     */
    @Override
    public void exitProgStringExp(ResolveParser.ProgStringExpContext ctx) {
        myNodes.put(ctx,
                new ProgramStringExp(createLocation(ctx.STRING_LITERAL().getSymbol()), ctx.STRING_LITERAL().getText()));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the program function expression representation.
     * </p>
     *
     * @param ctx
     *            Program function expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgParamExp(ResolveParser.ProgParamExpContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        // function arguments
        List<ResolveParser.ProgExpContext> programExps = ctx.progExp();
        List<ProgramExp> functionArgs = new ArrayList<>();
        for (ResolveParser.ProgExpContext context : programExps) {
            functionArgs.add((ProgramExp) myNodes.removeFrom(context));
        }

        myNodes.put(ctx,
                new ProgramFunctionExp(createLocation(ctx), qualifier, createPosSymbol(ctx.name), functionArgs));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a program variable expression representation generated by its child rules.
     * </p>
     *
     * @param ctx
     *            Program variable expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVariableExp(ResolveParser.ProgVariableExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method generates a new program variable dotted expression.
     * </p>
     *
     * @param ctx
     *            Program variable dot expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVarDotExp(ResolveParser.ProgVarDotExpContext ctx) {
        // Create a dot expression
        List<ResolveParser.ProgVarNameExpContext> progNamedExp = ctx.progVarNameExp();
        List<ProgramVariableExp> dotExps = new ArrayList<>();
        for (ResolveParser.ProgVarNameExpContext context : progNamedExp) {
            dotExps.add((ProgramVariableExp) myNodes.removeFrom(context));
        }

        myNodes.put(ctx, new ProgramVariableDotExp(createLocation(ctx), dotExps));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the generated temporary object to represent a program variable array expression as the last
     * element of this dotted expression. The rule that contains this expression should convert it to the appropriate
     * function call.
     * </p>
     *
     * @param ctx
     *            Program variable dot array expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVarDotArrayExp(ResolveParser.ProgVarDotArrayExpContext ctx) {
        // Create a dot expression
        List<ResolveParser.ProgVarNameExpContext> progNamedExp = ctx.progVarNameExp();
        List<ProgramVariableExp> dotExps = new ArrayList<>();
        for (ResolveParser.ProgVarNameExpContext context : progNamedExp) {
            dotExps.add((ProgramVariableExp) myNodes.removeFrom(context));
        }

        // Add the array expression
        dotExps.add((ProgramVariableExp) myNodes.removeFrom(ctx.progVarArrayExp()));

        myNodes.put(ctx, new ProgramVariableDotExp(createLocation(ctx), dotExps));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * Checks to see if this expression is part of a module argument. If yes, then this is an error, because we can't
     * convert it to the appropriate function call.
     * </p>
     *
     * @param ctx
     *            Program variable array expression node in ANTLR4 AST.
     */
    @Override
    public void enterProgVarArrayExp(ResolveParser.ProgVarArrayExpContext ctx) {
        if (myIsProcessingModuleArgument) {
            Fault f = new Fault(FaultType.PARSE_EXCEPTION, createLocation(ctx),
                    "Variable array expressions cannot be passed as module arguments.", true);
            myStatusHandler.registerAndStreamFault(f);
        }
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores a temporary object to represent a program variable array expression. The rule that contains
     * this expression should convert it to the appropriate function call.
     * </p>
     *
     * @param ctx
     *            Program variable array expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVarArrayExp(ResolveParser.ProgVarArrayExpContext ctx) {
        myNodes.put(ctx,
                new ProgramVariableArrayExp(createLocation(ctx),
                        (ProgramVariableExp) myNodes.removeFrom(ctx.progVarNameExp()),
                        (ProgramExp) myNodes.removeFrom(ctx.progExp())));
    }

    /**
     * {@inheritDoc} <br>
     * <p>
     * This method stores the program variable name expression representation.
     * </p>
     *
     * @param ctx
     *            Program variable name expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVarNameExp(ResolveParser.ProgVarNameExpContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        myNodes.put(ctx, new ProgramVariableNameExp(createLocation(ctx), qualifier, createPosSymbol(ctx.name)));
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Return the complete module representation build by this class.
     * </p>
     *
     * @return A {link ModuleDec} (intermediate representation) object.
     */
    public ModuleDec getModule() {
        return myFinalModule;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method that adds a new module dependency if it doesn't exist already.
     * </p>
     *
     * @param filename
     *            Name of the module.
     * @param parentDirectoryName
     *            Parent directory name.
     * @param isExternallyRealiz
     *            Boolean that indicates whether or not this is a Non-RESOLVE file.
     */
    private void addNewModuleDependency(String filename, String parentDirectoryName, boolean isExternallyRealiz) {
        ResolveFileBasicInfo fileBasicInfo = new ResolveFileBasicInfo(filename, parentDirectoryName);
        if (!myModuleDependencies.containsKey(fileBasicInfo)) {
            myModuleDependencies.put(fileBasicInfo, isExternallyRealiz);
        }
    }

    /**
     * <p>
     * An helper method that adds elements from {@code newItems} if it doesn't exist already.
     * </p>
     *
     * @param usesList
     *            The original uses list.
     * @param newItems
     *            The new elements to be added.
     *
     * @return The modified uses list.
     */
    private List<UsesItem> addToUsesList(List<UsesItem> usesList, List<UsesItem> newItems) {
        // Get the modules we import as a string
        List<String> importAsStrings = new ArrayList<>(usesList.size());
        for (UsesItem item : usesList) {
            importAsStrings.add(item.getName().getName());
        }

        // Add the new items if they aren't declared already
        for (UsesItem newItem : newItems) {
            if (!importAsStrings.contains(newItem.getName().getName())) {
                usesList.add(newItem);
            }
        }

        return usesList;
    }

    /**
     * <p>
     * Create a {@link FacilityDec} for the current parser rule we are visiting.
     * </p>
     *
     * @param l
     *            Location for all the new elements.
     * @param newTy
     *            The new name type.
     * @param arrayElementTy
     *            The type for the elements in the array.
     * @param lowerBound
     *            The lower bound for the array.
     * @param upperBound
     *            The upper bound for the array.
     *
     * @return A {@link FacilityDec} for the rule.
     */
    private FacilityDec createArrayFacilityDec(Location l, NameTy newTy, NameTy arrayElementTy, ProgramExp lowerBound,
            ProgramExp upperBound) {
        // Create a list of arguments for the new FacilityDec and
        // add the type, Low and High for Arrays
        List<ModuleArgumentItem> moduleArgumentItems = new ArrayList<>();
        moduleArgumentItems.add(new ModuleArgumentItem(
                new ProgramVariableNameExp(l.clone(), arrayElementTy.getQualifier(), arrayElementTy.getName())));
        moduleArgumentItems.add(new ModuleArgumentItem(lowerBound));
        moduleArgumentItems.add(new ModuleArgumentItem(upperBound));

        // Add Static_Array_Template as module dependency
        addNewModuleDependency("Static_Array_Template", "Static_Array_Template", false);
        addNewModuleDependency("Std_Array_Realiz", "Static_Array_Template", true);

        return new FacilityDec(new PosSymbol(l.clone(), newTy.getQualifier().getName()),
                new PosSymbol(l.clone(), "Static_Array_Template"), moduleArgumentItems,
                new ArrayList<EnhancementSpecItem>(), new PosSymbol(l.clone(), "Std_Array_Realiz"),
                new ArrayList<ModuleArgumentItem>(), new ArrayList<EnhancementSpecRealizItem>(), null, true);
    }

    /**
     * <p>
     * Create an {@link AssertionClause} for the current parser rule we are visiting.
     * </p>
     *
     * @param l
     *            Location for the clause.
     * @param clauseType
     *            The type of clause.
     * @param mathExps
     *            List of mathematical expressions in the clause.
     *
     * @return An {@link AssertionClause} for the rule.
     */
    private AssertionClause createAssertionClause(Location l, AssertionClause.ClauseType clauseType,
            List<ResolveParser.MathExpContext> mathExps) {
        return createAssertionClause(l, clauseType, mathExps, new ArrayList<ResolveParser.MathVarNameExpContext>());
    }

    /**
     * <p>
     * Create an {@link AssertionClause} for the current parser rule we are visiting.
     * </p>
     *
     * @param l
     *            Location for the clause.
     * @param clauseType
     *            The type of clause.
     * @param mathExps
     *            List of mathematical expressions in the clause.
     * @param involvesMathVarExps
     *            List of mathematical variable expressions involved (or affecting) this clause.
     *
     * @return An {@link AssertionClause} for the rule.
     */
    private AssertionClause createAssertionClause(Location l, AssertionClause.ClauseType clauseType,
            List<ResolveParser.MathExpContext> mathExps,
            List<ResolveParser.MathVarNameExpContext> involvesMathVarExps) {
        Exp whichEntailsExp = null;
        if (mathExps.size() > 1) {
            whichEntailsExp = (Exp) myNodes.removeFrom(mathExps.get(1));
        }
        Exp assertionExp = (Exp) myNodes.removeFrom(mathExps.get(0));

        // Obtain the list of involved shared variable expressions
        List<Exp> involvedSharedVars = new ArrayList<>();
        for (ResolveParser.MathVarNameExpContext context : involvesMathVarExps) {
            involvedSharedVars.add((Exp) myNodes.removeFrom(context));
        }

        return new AssertionClause(l, clauseType, assertionExp, whichEntailsExp, involvedSharedVars);
    }

    /**
     * <p>
     * Create an {@link FacilityInitFinalItem} for the current parser rule we are visiting.
     * </p>
     *
     * @param l
     *            Location for the item.
     * @param itemType
     *            The item type.
     * @param affects
     *            The {@link AffectsClause} for this item.
     * @param requires
     *            The requires {@link AssertionClause} for this item.
     * @param ensures
     *            The ensures {@link AssertionClause} for this item.
     * @param facilityDecs
     *            List of {@link FacilityDec}s for this item.
     * @param varDecs
     *            List of {@link VarDec}s for this item.
     * @param statements
     *            List of {@link Statement}s for this item.
     *
     * @return A {@link FacilityInitFinalItem} for the rule.
     */
    private FacilityInitFinalItem createFacilityTypeInitFinalItem(Location l, FacilityInitFinalItem.ItemType itemType,
            AffectsClause affects, AssertionClause requires, AssertionClause ensures, List<FacilityDec> facilityDecs,
            List<VarDec> varDecs, List<Statement> statements) {
        // Create the finalization item that we are going to perform
        // the syntactic sugar conversions on.
        FacilityInitFinalItem beforeConversionFinalItem = new FacilityInitFinalItem(l.clone(), itemType, affects,
                requires, ensures, facilityDecs, varDecs, statements);

        // Attempt to resolve all the syntactic sugar conversions
        SyntacticSugarConverter converter = new SyntacticSugarConverter(myArrayNameTyToInnerTyMap, myCopyTRList,
                myCopySSRList, myNewElementCounter);
        TreeWalker.visit(converter, beforeConversionFinalItem);

        // Obtain the new TypeInitFinalItem generated by the converter
        FacilityInitFinalItem afterConversionFinalItem = (FacilityInitFinalItem) converter.getProcessedElement();
        myNewElementCounter = converter.getNewElementCounter();

        return afterConversionFinalItem;
    }

    /**
     * <p>
     * Create a location for the current parser rule we are visiting.
     * </p>
     *
     * @param ctx
     *            The visiting ANTLR4 parser rule.
     *
     * @return A {@link Location} for the rule.
     */
    private Location createLocation(ParserRuleContext ctx) {
        return createLocation(ctx.getStart());
    }

    /**
     * <p>
     * Create a location for the current parser token we are visiting.
     * </p>
     *
     * @param t
     *            The visiting ANTLR4 parser token.
     *
     * @return A {@link Location} for the rule.
     */
    private Location createLocation(Token t) {
        return new Location(myFile, t.getLine(), t.getCharPositionInLine());
    }

    /**
     * <p>
     * Create a {@link NameTy} from the a program array type for the current parser rule we are visiting.
     * </p>
     *
     * @param l
     *            Location for the new elements.
     * @param firstIdentAsString
     *            The first variable identifier as a string.
     * @param arrayTypeContext
     *            The program array context.
     *
     * @return A {@link NameTy} for the rule.
     */
    private NameTy createNameTyFromArrayType(Location l, String firstIdentAsString,
            ResolveParser.ProgramArrayTypeContext arrayTypeContext) {
        // Type for the elements in the array
        NameTy arrayElementsTy = (NameTy) myNodes.removeFrom(arrayTypeContext.programNamedType());

        // Lower and Upper Bound
        ProgramExp low = (ProgramExp) myNodes.removeFrom(arrayTypeContext.progExp(0));
        ProgramExp high = (ProgramExp) myNodes.removeFrom(arrayTypeContext.progExp(1));

        // Create name in the format of "_(Name of Variable)_Array_Fac_(myCounter)"
        String newArrayName = "";
        newArrayName += ("_" + firstIdentAsString + "_Array_Fac_" + (++myNewElementCounter));

        // Create the new raw type
        NameTy rawNameTy = new NameTy(l.clone(), new PosSymbol(l.clone(), newArrayName),
                new PosSymbol(l.clone(), "Static_Array"));

        // Create the new array facility and add it to the appropriate container
        ArrayFacilityDecContainer innerMostContainer = myArrayFacilityDecContainerStack.pop();
        innerMostContainer.newFacilityDecs.add(createArrayFacilityDec(l, rawNameTy, arrayElementsTy, low, high));
        myArrayFacilityDecContainerStack.push(innerMostContainer);

        // Store the raw types in the map
        myArrayNameTyToInnerTyMap.put((NameTy) rawNameTy.clone(), (NameTy) arrayElementsTy.clone());

        return rawNameTy;
    }

    /**
     * <p>
     * Create a symbol representation for the current parser token we are visiting.
     * </p>
     *
     * @param t
     *            The visiting ANTLR4 parser token.
     *
     * @return A {@link PosSymbol} for the rule.
     */
    private PosSymbol createPosSymbol(Token t) {
        return new PosSymbol(createLocation(t), t.getText());
    }

    /**
     * <p>
     * Create an {@link AssertionClause} with {@code true} as the assertion expression for the current parser rule we
     * are visiting.
     * </p>
     *
     * @param l
     *            Location for the clause.
     * @param clauseType
     *            The type of clause.
     *
     * @return An {@link AssertionClause} for the rule.
     */
    private AssertionClause createTrueAssertionClause(Location l, AssertionClause.ClauseType clauseType) {
        return new AssertionClause(l, clauseType, VarExp.getTrueVarExp(l, myTypeGraph));
    }

    /**
     * <p>
     * Create an {@link RealizInitFinalItem} for the current parser rule we are visiting.
     * </p>
     *
     * @param l
     *            Location for the item.
     * @param itemType
     *            The item type.
     * @param affects
     *            The {@link AffectsClause} for this item.
     * @param facilityDecs
     *            List of {@link FacilityDec}s for this item.
     * @param varDecs
     *            List of {@link VarDec}s for this item.
     * @param statements
     *            List of {@link Statement}s for this item.
     *
     * @return A {@link RealizInitFinalItem} for the rule.
     */
    private RealizInitFinalItem createRealizInitFinalItem(Location l, RealizInitFinalItem.ItemType itemType,
            AffectsClause affects, List<FacilityDec> facilityDecs, List<VarDec> varDecs, List<Statement> statements) {
        // Create the finalization item that we are going to perform
        // the syntactic sugar conversions on.
        RealizInitFinalItem beforeConversionFinalItem = new RealizInitFinalItem(l.clone(), itemType, affects,
                facilityDecs, varDecs, statements);

        // Attempt to resolve all the syntactic sugar conversions
        SyntacticSugarConverter converter = new SyntacticSugarConverter(myArrayNameTyToInnerTyMap, myCopyTRList,
                myCopySSRList, myNewElementCounter);
        TreeWalker.visit(converter, beforeConversionFinalItem);

        // Obtain the new TypeInitFinalItem generated by the converter
        RealizInitFinalItem afterConversionFinalItem = (RealizInitFinalItem) converter.getProcessedElement();
        myNewElementCounter = converter.getNewElementCounter();

        return afterConversionFinalItem;
    }

    /**
     * <p>
     * An helper method that creates new {@link UsesItem UseItem(s)} using the auto import list.
     * </p>
     *
     * @param loc
     *            An location object that will be used to create the new {@link UsesItem UseItem(s)}.
     *
     * @return A list containing the new {@link UsesItem UsesItem(s)}.
     */
    private List<UsesItem> generateAutoImportUsesItems(Location loc) {
        List<UsesItem> autoImportUsesItems = new ArrayList<>(ResolveCompiler.AUTO_IMPORT_FILES.size());
        for (String name : ResolveCompiler.AUTO_IMPORT_FILES) {
            PosSymbol nameAsPosSymbol = new PosSymbol(loc.clone(), name);
            autoImportUsesItems.add(new UsesItem(nameAsPosSymbol));

            // Add this as a module dependency
            addNewModuleDependency(name, "", false);
        }

        return autoImportUsesItems;
    }

    /**
     * <p>
     * An helper method to retrieve the facility declarations (including any newly declared array facilities).
     * </p>
     *
     * @param facilityDeclContexts
     *            The ANTLR4 parser rule for list of facilty declarations.
     *
     * @return List of {@link FacilityDec}.
     */
    private List<FacilityDec> getFacilityDecls(List<ResolveParser.FacilityDeclContext> facilityDeclContexts) {
        // Pop innermost the array facility container
        ArrayFacilityDecContainer container = myArrayFacilityDecContainerStack.pop();

        // Add any local array facilities
        List<FacilityDec> facilityDecs = Utilities.collect(FacilityDec.class, facilityDeclContexts, myNodes);
        facilityDecs.addAll(container.newFacilityDecs);

        return facilityDecs;
    }

    /**
     * <p>
     * Obtain the correct parameter mode based on the given context.
     * </p>
     *
     * @param ctx
     *            The ANTLR4 parser rule for parameter modes.
     *
     * @return The corresponding {@link ProgramParameterEntry.ParameterMode}.
     */
    private ProgramParameterEntry.ParameterMode getMode(ResolveParser.ParameterModeContext ctx) {
        ProgramParameterEntry.ParameterMode mode;
        switch (ctx.getStart().getType()) {
            case ResolveLexer.ALTERS:
                mode = ProgramParameterEntry.ParameterMode.ALTERS;
                break;
            case ResolveLexer.UPDATES:
                mode = ProgramParameterEntry.ParameterMode.UPDATES;
                break;
            case ResolveLexer.CLEARS:
                mode = ProgramParameterEntry.ParameterMode.CLEARS;
                break;
            case ResolveLexer.RESTORES:
                mode = ProgramParameterEntry.ParameterMode.RESTORES;
                break;
            case ResolveLexer.PRESERVES:
                mode = ProgramParameterEntry.ParameterMode.PRESERVES;
                break;
            case ResolveLexer.REPLACES:
                mode = ProgramParameterEntry.ParameterMode.REPLACES;
                break;
            default:
                mode = ProgramParameterEntry.ParameterMode.EVALUATES;
                break;
        }

        return mode;
    }

    /**
     * <p>
     * An helper method to retrieve the module arguments (if any).
     * </p>
     *
     * @param moduleParameterListContext
     *            The ANTLR4 parser rule for list of module parameters.
     *
     * @return List of {@link ModuleParameterDec}.
     */
    private List<ModuleParameterDec> getModuleArguments(
            ResolveParser.ModuleParameterListContext moduleParameterListContext) {
        List<ModuleParameterDec> parameterDecls = new ArrayList<>();
        if (moduleParameterListContext != null) {
            List<ResolveParser.ModuleParameterDeclContext> parameterDeclContexts = moduleParameterListContext
                    .moduleParameterDecl();
            for (ResolveParser.ModuleParameterDeclContext context : parameterDeclContexts) {
                if (context.constantParameterDecl() != null) {
                    List<TerminalNode> varNames = context.constantParameterDecl().variableDeclGroup().IDENTIFIER();
                    for (TerminalNode ident : varNames) {
                        parameterDecls.add((ModuleParameterDec) myNodes.removeFrom(ident));
                    }
                } else {
                    parameterDecls.add((ModuleParameterDec) myNodes.removeFrom(context));
                }
            }
        }

        return parameterDecls;
    }

    /**
     * <p>
     * An helper method to retrieve the parameter variable declarations (if any).
     * </p>
     *
     * @param parameterDeclContexts
     *            A list containing the ANTLR4 parser rule for parameter variable declarations.
     *
     * @return List of {@link ParameterVarDec}.
     */
    private List<ParameterVarDec> getParameterDecls(List<ResolveParser.ParameterDeclContext> parameterDeclContexts) {
        List<ParameterVarDec> varDecs = new ArrayList<>();
        for (ResolveParser.ParameterDeclContext context : parameterDeclContexts) {
            List<TerminalNode> varNames = context.variableDeclGroup().IDENTIFIER();
            for (TerminalNode ident : varNames) {
                varDecs.add((ParameterVarDec) myNodes.removeFrom(ident));
            }
        }

        return varDecs;
    }

    /**
     * <p>
     * An helper method to retrieve the variable declarations (if any).
     * </p>
     *
     * @param variableDeclContexts
     *            A list containing the ANTLR4 parser rule for variable declarations.
     *
     * @return List of {@link VarDec}.
     */
    private List<VarDec> getVarDecls(List<ResolveParser.VariableDeclContext> variableDeclContexts) {
        List<VarDec> varDecs = new ArrayList<>();
        for (ResolveParser.VariableDeclContext context : variableDeclContexts) {
            for (TerminalNode node : context.variableDeclGroup().IDENTIFIER()) {
                varDecs.add((VarDec) myNodes.removeFrom(node));
            }
        }

        return varDecs;
    }

    /**
     * <p>
     * An helper method that checks to see if we have a sharing construct.
     * </p>
     *
     * @param conceptDecls
     *            List of all declarations in a concept.
     *
     * @return {@code true} if there is a shared variables block and/or a type family with a definition variable,
     *         {@code false} otherwise.
     */
    private boolean hasSharingConstructs(List<Dec> conceptDecls) {
        boolean retval = false;

        Iterator<Dec> decIterator = conceptDecls.iterator();
        while (decIterator.hasNext() && !retval) {
            Dec dec = decIterator.next();
            if (dec instanceof SharedStateDec) {
                retval = true;
            } else if (dec instanceof TypeFamilyDec) {
                if (((TypeFamilyDec) dec).getDefinitionVarList().size() > 0) {
                    retval = true;
                }
            }
        }

        return retval;
    }

    /**
     * <p>
     * An helper method that checks to see if the current module is part of the no auto import list.
     * </p>
     *
     * @param name
     *            Current module name.
     *
     * @return {@code true} if the no auto import list contains this module, {@code false} otherwise.
     */
    private boolean inNoAutoImportExceptionList(PosSymbol name) {
        boolean retval = false;
        Iterator<String> it = ResolveCompiler.NO_AUTO_IMPORT_EXCEPTION_LIST.iterator();
        while (it.hasNext() && !retval) {
            String nextModuleName = it.next();
            if (nextModuleName.equals(name.getName())) {
                retval = true;
            }
        }

        return retval;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * This holds items that are needed to build a {@link MathDefinitionDec}
     * </p>
     */
    private class DefinitionMembers {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * Definition name
         * </p>
         */
        PosSymbol name;

        /**
         * <p>
         * Definition parameters
         * </p>
         */
        List<MathVarDec> params;

        /**
         * <p>
         * Definition return type
         * </p>
         */
        Ty rawType;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This constructs a temporary structure to store all the relevant items to build a {@link MathDefinitionDec}.
         * </p>
         *
         * @param name
         *            Definition name.
         * @param params
         *            Definition parameters.
         * @param rawType
         *            Definition return type.
         */
        DefinitionMembers(PosSymbol name, List<MathVarDec> params, Ty rawType) {
            this.name = name;
            this.params = params;
            this.rawType = rawType;
        }

    }

    /**
     * <p>
     * This holds new items related to syntactic sugar conversions for raw array types.
     * </p>
     */
    private class ArrayFacilityDecContainer {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * The context that instantiated this object
         * </p>
         */
        final ParserRuleContext instantiatingContext;

        /**
         * <p>
         * List of new facility declaration objects.
         * </p>
         * <p>
         * <strong>Note:</strong> The only facilities generated at the moment are new {@code Static_Array_Template}
         * facilities.
         * </p>
         */
        final List<FacilityDec> newFacilityDecs;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This constructs a temporary structure to store all the new array facility declarations that resulted from
         * syntactic sugar conversions for raw array types.
         * </p>
         *
         * @param instantiatingContext
         *            The context that instantiated this object.
         */
        ArrayFacilityDecContainer(ParserRuleContext instantiatingContext) {
            this.instantiatingContext = instantiatingContext;
            newFacilityDecs = new ArrayList<>();
        }
    }

    /**
     * <p>
     * When building a {@link ModuleDec}, we would like the new array facility declarations to appear immediately before
     * the different type/shared state representations that created it.
     * </p>
     * <p>
     * This class allow us to keep track to this, so that when we add the different declarations to the
     * {@link ModuleDec}, we add these array facilities in the right spot.
     * </p>
     */
    private class NewModuleDecMembers {

        // ===========================================================
        // Member Fields
        // ===========================================================

        final Map<ParserRuleContext, List<FacilityDec>> newFacilityDecsMap;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This constructs a temporary structure to a map that contains all the parser rules contexts that generated an
         * array facility declaration.
         * </p>
         */
        NewModuleDecMembers() {
            this.newFacilityDecsMap = new HashMap<>();
        }
    }

}
