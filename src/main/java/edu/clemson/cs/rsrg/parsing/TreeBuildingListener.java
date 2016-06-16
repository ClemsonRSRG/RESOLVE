/**
 * TreeBuildingListener.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.parsing;

import edu.clemson.cs.r2jt.typeandpopulate2.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathAssertionDec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathCategoricalDefinitionDec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathDefinitionDec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathTypeTheoremDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ConceptTypeParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ConstantParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.RealizationParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.absyn.items.mathitems.DefinitionBodyItem;
import edu.clemson.cs.rsrg.absyn.items.mathitems.LoopVerificationItem;
import edu.clemson.cs.rsrg.absyn.items.mathitems.SpecInitFinalItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.*;
import edu.clemson.cs.rsrg.absyn.rawtypes.ArbitraryExpTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.absyn.statements.*;
import edu.clemson.cs.rsrg.errorhandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.misc.Utilities;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * <p>This replaces the old RESOLVE ANTLR3 builder and builds the
 * intermediate representation objects used during the compilation
 * process.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 1.0
 */
public class TreeBuildingListener extends ResolveParserBaseListener {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Stores all the parser nodes we have encountered.</p> */
    private final ParseTreeProperty<ResolveConceptualElement> myNodes;

    /**
     * <p>Stores the information gathered from the children nodes of
     * {@code ResolveParser.DefinitionSignatureContext}</p>
     */
    private List<DefinitionMembers> myDefinitionMemberList;

    /** <p>Boolean that indicates that we are processing a module argument.</p> */
    private boolean myIsProcessingModuleArgument;

    /**
     * <p>This is a stack that contains containers for potential new array
     * facilities.</p>
     */
    private Stack<ArrayFacilityDecContainer> myArrayFacilityDecContainerStack;

    /**
     * <p>This map provides a mapping between the newly declared array name types
     * to the types of elements in the array.</p>
     */
    private Map<NameTy, NameTy> myArrayNameTyToInnerTyMap;

    /**
     * <p>Since we don't have symbol table, we really don't know if
     * we are generating a new object with the same name. In order to avoid
     * problems, all of our objects will have a name that starts with "_" and
     * end the current new element counter. This number increases by 1 each
     * time we create a new element.</p>
     */
    private int newElementCounter;

    /** <p>The complete module representation.</p> */
    private ModuleDec myFinalModule;

    /** <p>The current file we are compiling.</p> */
    private final ResolveFile myFile;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Create a listener to walk the entire compiler generated
     * ANTLR4 parser tree and generate the intermediate representation
     * objects used by the subsequent modules.</p>
     *
     * @param file The current file we are compiling.
     * @param typeGraph Type graph that indicates relationship between different mathematical types.
     */
    public TreeBuildingListener(ResolveFile file, TypeGraph typeGraph) {
        myTypeGraph = typeGraph;
        myFile = file;
        myFinalModule = null;
        myNodes = new ParseTreeProperty<>();
        myDefinitionMemberList = null;
        myIsProcessingModuleArgument = false;
        myArrayFacilityDecContainerStack = new Stack<>();
        myArrayNameTyToInnerTyMap = new HashMap<>();
        newElementCounter = 0;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Module Declaration
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates and saves the complete
     * module declaration.</p>
     *
     * @param ctx Module node in ANTLR4 AST.
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
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if the {@link ResolveFile} name matches the
     * open and close names given in the file.</p>
     *
     * @param ctx Precis module node in ANTLR4 AST.
     */
    @Override
    public void enterPrecisModule(ResolveParser.PrecisModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            throw new SourceErrorException(
                    "Precis name does not match filename.",
                    createPosSymbol(ctx.name), new IllegalArgumentException());
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            throw new SourceErrorException(
                    "End name does not match the filename.",
                    createPosSymbol(ctx.closename),
                    new IllegalArgumentException());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a representation of a {@code Precis}
     * module declaration.</p>
     *
     * @param ctx Precis module node in ANTLR4 AST.
     */
    @Override
    public void exitPrecisModule(ResolveParser.PrecisModuleContext ctx) {
        List<ModuleParameterDec> parameterDecls = new ArrayList<>();
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(), myNodes);
        List<Dec> decls = Utilities.collect(Dec.class,
                ctx.precisItems() != null ? ctx.precisItems().precisItem() : new ArrayList<ParseTree>(), myNodes);

        PrecisModuleDec precis = new PrecisModuleDec(createLocation(ctx),
                createPosSymbol(ctx.name), parameterDecls, uses, decls);

        myNodes.put(ctx, precis);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the generated precis item.</p>
     *
     * @param ctx Precis item node in ANTLR4 AST.
     */
    @Override
    public void exitPrecisItem(ResolveParser.PrecisItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Facility Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if the {@link ResolveFile} name matches the
     * open and close names given in the file.</p>
     *
     * <p>If everything checks out, we create a new object to store
     * all the elements that can be created by the syntatic sugar
     * conversions.</p>
     *
     * @param ctx Facility module node in ANTLR4 AST.
     */
    @Override
    public void enterFacilityModule(ResolveParser.FacilityModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            throw new SourceErrorException(
                    "Facility name does not match filename.",
                    createPosSymbol(ctx.name), new IllegalArgumentException());
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            throw new SourceErrorException(
                    "End name does not match the filename.",
                    createPosSymbol(ctx.closename),
                    new IllegalArgumentException());
        }

        // Create a new container
        myArrayFacilityDecContainerStack
                .push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitFacilityModule(ResolveParser.FacilityModuleContext ctx) {
        super.exitFacilityModule(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitFacilityItem(ResolveParser.FacilityItemContext ctx) {
        super.exitFacilityItem(ctx);
    }

    // -----------------------------------------------------------
    // Short Facility Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a representation of a short facility
     * module declaration.</p>
     *
     * @param ctx Short facility module node in ANTLR4 AST.
     */
    @Override
    public void exitShortFacilityModule(
            ResolveParser.ShortFacilityModuleContext ctx) {
        FacilityDec facilityDec =
                (FacilityDec) myNodes.removeFrom(ctx.facilityDecl());
        ShortFacilityModuleDec shortFacility =
                new ShortFacilityModuleDec(createLocation(ctx), facilityDec
                        .getName(), facilityDec);

        myNodes.put(ctx, shortFacility);
    }

    // -----------------------------------------------------------
    // Concept Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if the {@link ResolveFile} name matches the
     * open and close names given in the file.</p>
     *
     * @param ctx Concept module node in ANTLR4 AST.
     */
    @Override
    public void enterConceptModule(ResolveParser.ConceptModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            throw new SourceErrorException(
                    "Concept name does not match filename.",
                    createPosSymbol(ctx.name), new IllegalArgumentException());
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            throw new SourceErrorException(
                    "End name does not match the filename.",
                    createPosSymbol(ctx.closename),
                    new IllegalArgumentException());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a representation of a {@code Concept}
     * module declaration.</p>
     *
     * @param ctx Concept module node in ANTLR4 AST.
     */
    @Override
    public void exitConceptModule(ResolveParser.ConceptModuleContext ctx) {
        // Module parameters (if any)
        List<ModuleParameterDec> parameterDecls =
                getModuleArguments(ctx.moduleParameterList());

        // Uses items (if any)
        List<UsesItem> uses =
                Utilities.collect(UsesItem.class, ctx.usesList() != null ? ctx
                        .usesList().usesItem() : new ArrayList<ParseTree>(),
                        myNodes);

        // Module requires (if any)
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires =
                    (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        }
        else {
            requires =
                    createTrueAssertionClause(createLocation(ctx),
                            AssertionClause.ClauseType.REQUIRES);
        }

        // Add any Constraints or Decs (if any)
        List<AssertionClause> constraints = new ArrayList<>();
        List<Dec> decls = new ArrayList<>();
        if (ctx.conceptItems() != null) {
            List<ResolveParser.ConceptItemContext> itemContexts = ctx.conceptItems().conceptItem();
            for (ResolveParser.ConceptItemContext item : itemContexts) {
                if (item.constraintClause() != null) {
                    constraints.add((AssertionClause) myNodes.removeFrom(item));
                }
                else {
                    decls.add((Dec) myNodes.removeFrom(item));
                }
            }
        }

        ConceptModuleDec concept =
                new ConceptModuleDec(createLocation(ctx),
                        createPosSymbol(ctx.name), parameterDecls, uses,
                        requires, constraints, decls);
        myNodes.put(ctx, concept);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the generated concept item.</p>
     *
     * @param ctx Concept item node in ANTLR4 AST.
     */
    @Override
    public void exitConceptItem(ResolveParser.ConceptItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Concept Realization Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if the {@link ResolveFile} name matches the
     * open and close names given in the file.</p>
     *
     * <p>If everything checks out, we create a new object to store
     * all the new array facilities that can be created by the
     * syntactic sugar conversions.</p>
     *
     * @param ctx Concept impl module node in ANTLR4 AST.
     */
    @Override
    public void enterConceptImplModule(
            ResolveParser.ConceptImplModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            throw new SourceErrorException(
                    "Concept realization name does not match filename.",
                    createPosSymbol(ctx.name), new IllegalArgumentException());
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            throw new SourceErrorException(
                    "End name does not match the filename.",
                    createPosSymbol(ctx.closename),
                    new IllegalArgumentException());
        }

        // Create a new container
        myArrayFacilityDecContainerStack
                .push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConceptImplModule(ResolveParser.ConceptImplModuleContext ctx) {
        super.exitConceptImplModule(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConceptImplItem(ResolveParser.ConceptImplItemContext ctx) {
        super.exitConceptImplItem(ctx);
    }

    // -----------------------------------------------------------
    // Enhancement Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if the {@link ResolveFile} name matches the
     * open and close names given in the file.</p>
     *
     * @param ctx Enhancement module node in ANTLR4 AST.
     */
    @Override
    public void enterEnhancementModule(
            ResolveParser.EnhancementModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            throw new SourceErrorException(
                    "Enhancement name does not match filename.",
                    createPosSymbol(ctx.name), new IllegalArgumentException());
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            throw new SourceErrorException(
                    "End name does not match the filename.",
                    createPosSymbol(ctx.closename),
                    new IllegalArgumentException());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a representation of a {@code Enhancement}
     * module declaration.</p>
     *
     * @param ctx Enhancement module node in ANTLR4 AST.
     */
    @Override
    public void exitEnhancementModule(ResolveParser.EnhancementModuleContext ctx) {
        // Module parameters (if any)
        List<ModuleParameterDec> parameterDecls =
                getModuleArguments(ctx.moduleParameterList());

        // Uses items (if any)
        List<UsesItem> uses =
                Utilities.collect(UsesItem.class, ctx.usesList() != null ? ctx
                        .usesList().usesItem() : new ArrayList<ParseTree>(),
                        myNodes);

        // Module requires (if any)
        AssertionClause requires;
        if (ctx.requiresClause() != null) {
            requires =
                    (AssertionClause) myNodes.removeFrom(ctx.requiresClause());
        }
        else {
            requires =
                    createTrueAssertionClause(createLocation(ctx),
                            AssertionClause.ClauseType.REQUIRES);
        }

        // Decs (if any)
        List<Dec> decls =
                Utilities
                        .collect(Dec.class,
                                ctx.enhancementItems() != null ? ctx
                                        .enhancementItems().enhancementItem()
                                        : new ArrayList<ParseTree>(), myNodes);

        EnhancementModuleDec enhancement =
                new EnhancementModuleDec(createLocation(ctx),
                        createPosSymbol(ctx.name), parameterDecls,
                        createPosSymbol(ctx.concept), uses, requires, decls);
        myNodes.put(ctx, enhancement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the generated enhancement item.</p>
     *
     * @param ctx Enhancement item node in ANTLR4 AST.
     */
    @Override
    public void exitEnhancementItem(ResolveParser.EnhancementItemContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Enhancement Realization Module
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if the {@link ResolveFile} name matches the
     * open and close names given in the file.</p>
     *
     * <p>If everything checks out, we create a new object to store
     * all the new array facilities that can be created by the
     * syntactic sugar conversions.</p>
     *
     * @param ctx Enhancement impl module node in ANTLR4 AST.
     */
    @Override
    public void enterEnhancementImplModule(
            ResolveParser.EnhancementImplModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            throw new SourceErrorException(
                    "Enhancement realization name does not match filename.",
                    createPosSymbol(ctx.name), new IllegalArgumentException());
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            throw new SourceErrorException(
                    "End name does not match the filename.",
                    createPosSymbol(ctx.closename),
                    new IllegalArgumentException());
        }

        // Create a new container
        myArrayFacilityDecContainerStack
                .push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitEnhancementImplModule(
            ResolveParser.EnhancementImplModuleContext ctx) {
        super.exitEnhancementImplModule(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitImplItem(ResolveParser.ImplItemContext ctx) {
        super.exitImplItem(ctx);
    }

    // -----------------------------------------------------------
    // Performance Profile Module for Concepts
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if the {@link ResolveFile} name matches the
     * open and close names given in the file.</p>
     *
     * @param ctx Concept performance module node in ANTLR4 AST.
     */
    @Override
    public void enterConceptPerformanceModule(
            ResolveParser.ConceptPerformanceModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            throw new SourceErrorException(
                    "Concept profile name does not match filename.",
                    createPosSymbol(ctx.name), new IllegalArgumentException());
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            throw new SourceErrorException(
                    "End name does not match the filename.",
                    createPosSymbol(ctx.closename),
                    new IllegalArgumentException());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConceptPerformanceModule(
            ResolveParser.ConceptPerformanceModuleContext ctx) {
        super.exitConceptPerformanceModule(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConceptPerformanceItem(
            ResolveParser.ConceptPerformanceItemContext ctx) {
        super.exitConceptPerformanceItem(ctx);
    }

    // -----------------------------------------------------------
    // Performance Profile Module for Enhancements
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if the {@link ResolveFile} name matches the
     * open and close names given in the file.</p>
     *
     * @param ctx Enhancement performance module node in ANTLR4 AST.
     */
    @Override
    public void enterEnhancementPerformanceModule(
            ResolveParser.EnhancementPerformanceModuleContext ctx) {
        if (!myFile.getName().equals(ctx.name.getText())) {
            throw new SourceErrorException(
                    "Concept profile name does not match filename.",
                    createPosSymbol(ctx.name), new IllegalArgumentException());
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            throw new SourceErrorException(
                    "End name does not match the filename.",
                    createPosSymbol(ctx.closename),
                    new IllegalArgumentException());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitEnhancementPerformanceModule(
            ResolveParser.EnhancementPerformanceModuleContext ctx) {
        super.exitEnhancementPerformanceModule(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitEnhancementPerformanceItem(
            ResolveParser.EnhancementPerformanceItemContext ctx) {
        super.exitEnhancementPerformanceItem(ctx);
    }

    // -----------------------------------------------------------
    // Uses Items (Imports)
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a representation for an import
     * module name.</p>
     *
     * @param ctx Uses item node in ANTLR4 AST.
     */
    @Override
    public void exitUsesItem(ResolveParser.UsesItemContext ctx) {
        myNodes.put(ctx, new UsesItem(createPosSymbol(ctx.getStart())));
    }

    // -----------------------------------------------------------
    // Module parameter declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a module parameter declaration.</p>
     *
     * @param ctx Module parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitModuleParameterDecl(
            ResolveParser.ModuleParameterDeclContext ctx) {
        if (ctx.definitionParameterDecl() != null) {
            myNodes.put(ctx, new ModuleParameterDec<>((MathDefinitionDec) myNodes.removeFrom(ctx.getChild(0))));
        }
        else if (ctx.typeParameterDecl() != null) {
            myNodes.put(ctx, new ModuleParameterDec<>((ConceptTypeParamDec) myNodes.removeFrom(ctx.getChild(0))));
        }
        else if (ctx.constantParameterDecl() != null) {
            // Could have multiple variables declared as a group
            List<TerminalNode> varNames = ctx.constantParameterDecl().variableDeclGroup().IDENTIFIER();
            for (TerminalNode ident : varNames) {
                myNodes.put(ident, new ModuleParameterDec<>((ConstantParamDec) myNodes.removeFrom(ident)));
            }
        }
        else if (ctx.operationParameterDecl() != null) {
            myNodes.put(ctx, new ModuleParameterDec<>((OperationDec) myNodes.removeFrom(ctx.getChild(0))));
        }
        else {
            myNodes.put(ctx, new ModuleParameterDec<>((RealizationParamDec) myNodes.removeFrom(ctx.getChild(0))));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method creates a temporary list to store all the
     * temporary definition members</p>
     *
     * @param ctx Definition parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void enterDefinitionParameterDecl(
            ResolveParser.DefinitionParameterDeclContext ctx) {
        myDefinitionMemberList = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a definition parameter declaration.</p>
     *
     * @param ctx Definition parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitDefinitionParameterDecl(
            ResolveParser.DefinitionParameterDeclContext ctx) {
        DefinitionMembers members = myDefinitionMemberList.remove(0);
        MathDefinitionDec definitionDec =
                new MathDefinitionDec(members.name, members.params,
                        members.rawType, null, false);
        myDefinitionMemberList = null;

        myNodes.put(ctx, definitionDec);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a concept type parameter declaration.</p>
     *
     * @param ctx Type parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitTypeParameterDecl(ResolveParser.TypeParameterDeclContext ctx) {
        myNodes.put(ctx, new ConceptTypeParamDec(createPosSymbol(ctx.name)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if this parameter declaration has a programming array type.
     * If yes, then this is an error, because there is no way the caller can pass
     * a variable of the same type to the calling statement.</p>
     *
     * @param ctx Constant parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void enterConstantParameterDecl(
            ResolveParser.ConstantParameterDeclContext ctx) {
        if (ctx.variableDeclGroup().programArrayType() != null) {
            throw new SourceErrorException(
                    "Array types cannot be used as a type for the parameter variables",
                    createPosSymbol(ctx.variableDeclGroup().programArrayType().start),
                    new IllegalArgumentException());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a constant parameter declaration
     * for each of the variables in the variable group.</p>
     *
     * @param ctx Constant parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitConstantParameterDecl(
            ResolveParser.ConstantParameterDeclContext ctx) {
        // Since we have ruled out array types, this should be a NameTy
        ResolveParser.VariableDeclGroupContext variableDeclGroupContext =
                ctx.variableDeclGroup();
        NameTy rawType =
                (NameTy) myNodes.removeFrom(variableDeclGroupContext
                        .programNamedType());

        // Generate a new parameter declaration for each of the
        // variables in the variable group.
        List<TerminalNode> variableIndents =
                variableDeclGroupContext.IDENTIFIER();
        for (TerminalNode node : variableIndents) {
            myNodes.put(node, new ConstantParamDec(createPosSymbol(node
                    .getSymbol()), rawType.clone()));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates an operation parameter declaration.</p>
     *
     * @param ctx Operation parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitOperationParameterDecl(
            ResolveParser.OperationParameterDeclContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.operationDecl()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a realization parameter declaration.</p>
     *
     * @param ctx Concept implementation parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitConceptImplParameterDecl(
            ResolveParser.ConceptImplParameterDeclContext ctx) {
        myNodes.put(ctx, new RealizationParamDec(createPosSymbol(ctx.name),
                createPosSymbol(ctx.concept)));
    }

    // -----------------------------------------------------------
    // Operation parameter declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if this parameter declaration has a programming array type.
     * If yes, then this is an error, because there is no way the caller can pass
     * a variable of the same type to the calling statement.</p>
     *
     * @param ctx Parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void enterParameterDecl(ResolveParser.ParameterDeclContext ctx) {
        if (ctx.variableDeclGroup().programArrayType() != null) {
            throw new SourceErrorException(
                    "Array types cannot be used as a type for the parameter variables",
                    createPosSymbol(ctx.variableDeclGroup().programArrayType().start),
                    new IllegalArgumentException());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the parameter declaration(s).</p>
     *
     * @param ctx Parameter declaration node in ANTLR4 AST.
     */
    @Override
    public void exitParameterDecl(ResolveParser.ParameterDeclContext ctx) {
        // Since we have ruled out array types, this should be a NameTy
        ResolveParser.VariableDeclGroupContext variableDeclGroupContext =
                ctx.variableDeclGroup();
        NameTy rawType =
                (NameTy) myNodes.removeFrom(variableDeclGroupContext
                        .programNamedType());

        // Generate a new parameter declaration for each of the
        // variables in the variable group.
        List<TerminalNode> variableIndents =
                variableDeclGroupContext.IDENTIFIER();
        for (TerminalNode node : variableIndents) {
            myNodes.put(node, new ParameterVarDec(getMode(ctx.parameterMode()),
                    createPosSymbol(node.getSymbol()), rawType.clone()));
        }
    }

    // -----------------------------------------------------------
    // Programming raw types
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new program named type.</p>
     *
     * @param ctx Program named type node in ANTLR4 AST.
     */
    @Override
    public void exitProgramNamedType(ResolveParser.ProgramNamedTypeContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        myNodes.put(ctx, new NameTy(createLocation(ctx), qualifier,
                createPosSymbol(ctx.name)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgramRecordType(
            ResolveParser.ProgramRecordTypeContext ctx) {
        super.enterProgramRecordType(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgramRecordType(ResolveParser.ProgramRecordTypeContext ctx) {
        super.exitProgramRecordType(ctx);
    }

    // -----------------------------------------------------------
    // Type Spec/Realization Declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a representation of a type model
     * declaration.</p>
     *
     * @param ctx Type model declaration node in ANTLR4 AST.
     */
    @Override
    public void exitTypeModelDecl(ResolveParser.TypeModelDeclContext ctx) {
        Ty mathTy = (Ty) myNodes.removeFrom(ctx.mathTypeExp());

        AssertionClause constraint;
        if (ctx.constraintClause() != null) {
            constraint =
                    (AssertionClause) myNodes
                            .removeFrom(ctx.constraintClause());
        }
        else {
            constraint =
                    createTrueAssertionClause(createLocation(ctx),
                            AssertionClause.ClauseType.CONSTRAINT);
        }

        SpecInitFinalItem initItem;
        if (ctx.specModelInit() != null) {
            initItem =
                    (SpecInitFinalItem) myNodes.removeFrom(ctx.specModelInit());
        }
        else {
            initItem =
                    new SpecInitFinalItem(createLocation(ctx),
                            SpecInitFinalItem.ItemType.INITIALIZATION, null,
                            createTrueAssertionClause(createLocation(ctx),
                                    AssertionClause.ClauseType.ENSURES));
        }

        SpecInitFinalItem finalItem;
        if (ctx.specModelFinal() != null) {
            finalItem =
                    (SpecInitFinalItem) myNodes
                            .removeFrom(ctx.specModelFinal());
        }
        else {
            finalItem =
                    new SpecInitFinalItem(createLocation(ctx),
                            SpecInitFinalItem.ItemType.FINALIZATION, null,
                            createTrueAssertionClause(createLocation(ctx),
                                    AssertionClause.ClauseType.ENSURES));
        }

        myNodes
                .put(ctx, new TypeFamilyDec(createPosSymbol(ctx.name), mathTy,
                        createPosSymbol(ctx.exemplar), constraint, initItem,
                        finalItem));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterTypeRepresentationDecl(
            ResolveParser.TypeRepresentationDeclContext ctx) {
        super.enterTypeRepresentationDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitTypeRepresentationDecl(
            ResolveParser.TypeRepresentationDeclContext ctx) {
        super.exitTypeRepresentationDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterFacilityTypeRepresentationDecl(
            ResolveParser.FacilityTypeRepresentationDeclContext ctx) {
        super.enterFacilityTypeRepresentationDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitFacilityTypeRepresentationDecl(
            ResolveParser.FacilityTypeRepresentationDeclContext ctx) {
        super.exitFacilityTypeRepresentationDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterPerformanceTypeModelDecl(
            ResolveParser.PerformanceTypeModelDeclContext ctx) {
        super.enterPerformanceTypeModelDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitPerformanceTypeModelDecl(
            ResolveParser.PerformanceTypeModelDeclContext ctx) {
        super.exitPerformanceTypeModelDecl(ctx);
    }

    // -----------------------------------------------------------
    // Shared State Spec/Realization Declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a representation of a shared state
     * declaration.</p>
     *
     * @param ctx Shared state declaration node in ANTLR4 AST.
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
            constraint =
                    (AssertionClause) myNodes
                            .removeFrom(ctx.constraintClause());
        }
        else {
            constraint =
                    createTrueAssertionClause(createLocation(ctx),
                            AssertionClause.ClauseType.CONSTRAINT);
        }

        SpecInitFinalItem initItem;
        if (ctx.specModelInit() != null) {
            initItem =
                    (SpecInitFinalItem) myNodes.removeFrom(ctx.specModelInit());
        }
        else {
            initItem =
                    new SpecInitFinalItem(createLocation(ctx),
                            SpecInitFinalItem.ItemType.INITIALIZATION, null,
                            createTrueAssertionClause(createLocation(ctx),
                                    AssertionClause.ClauseType.ENSURES));
        }

        SpecInitFinalItem finalItem;
        if (ctx.specModelFinal() != null) {
            finalItem =
                    (SpecInitFinalItem) myNodes
                            .removeFrom(ctx.specModelFinal());
        }
        else {
            finalItem =
                    new SpecInitFinalItem(createLocation(ctx),
                            SpecInitFinalItem.ItemType.FINALIZATION, null,
                            createTrueAssertionClause(createLocation(ctx),
                                    AssertionClause.ClauseType.ENSURES));
        }

        myNodes.put(ctx, new SharedStateDec(createPosSymbol(ctx.name), abstractStateVars, constraint, initItem, finalItem));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterSharedStateRepresentationDecl(
            ResolveParser.SharedStateRepresentationDeclContext ctx) {
        super.enterSharedStateRepresentationDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitSharedStateRepresentationDecl(
            ResolveParser.SharedStateRepresentationDeclContext ctx) {
        super.exitSharedStateRepresentationDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterFacilitySharedStateRepresentationDecl(
            ResolveParser.FacilitySharedStateRepresentationDeclContext ctx) {
        super.enterFacilitySharedStateRepresentationDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitFacilitySharedStateRepresentationDecl(
            ResolveParser.FacilitySharedStateRepresentationDeclContext ctx) {
        super.exitFacilitySharedStateRepresentationDecl(ctx);
    }

    // -----------------------------------------------------------
    // Initialization/Finalization Items
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new representation for a type model
     * initialization item.</p>
     *
     * @param ctx Spec model init item node in ANTLR4 AST.
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
        }
        else {
            ensures =
                    createTrueAssertionClause(createLocation(ctx),
                            AssertionClause.ClauseType.ENSURES);
        }

        myNodes.put(ctx, new SpecInitFinalItem(createLocation(ctx),
                SpecInitFinalItem.ItemType.INITIALIZATION, affects, ensures));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new representation for a type model
     * finalization item.</p>
     *
     * @param ctx Spec model final item node in ANTLR4 AST.
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
        }
        else {
            ensures =
                    createTrueAssertionClause(createLocation(ctx),
                            AssertionClause.ClauseType.ENSURES);
        }

        myNodes.put(ctx, new SpecInitFinalItem(createLocation(ctx),
                SpecInitFinalItem.ItemType.FINALIZATION, affects, ensures));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterRepresentationInit(
            ResolveParser.RepresentationInitContext ctx) {
        super.enterRepresentationInit(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitRepresentationInit(
            ResolveParser.RepresentationInitContext ctx) {
        super.exitRepresentationInit(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterRepresentationFinal(
            ResolveParser.RepresentationFinalContext ctx) {
        super.enterRepresentationFinal(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitRepresentationFinal(
            ResolveParser.RepresentationFinalContext ctx) {
        super.exitRepresentationFinal(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterFacilityRepresentationInit(
            ResolveParser.FacilityRepresentationInitContext ctx) {
        super.enterFacilityRepresentationInit(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitFacilityRepresentationInit(
            ResolveParser.FacilityRepresentationInitContext ctx) {
        super.exitFacilityRepresentationInit(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterFacilityRepresentationFinal(
            ResolveParser.FacilityRepresentationFinalContext ctx) {
        super.enterFacilityRepresentationFinal(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitFacilityRepresentationFinal(
            ResolveParser.FacilityRepresentationFinalContext ctx) {
        super.exitFacilityRepresentationFinal(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterPerformanceSpecModelInit(
            ResolveParser.PerformanceSpecModelInitContext ctx) {
        super.enterPerformanceSpecModelInit(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitPerformanceSpecModelInit(
            ResolveParser.PerformanceSpecModelInitContext ctx) {
        super.exitPerformanceSpecModelInit(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterPerformanceSpecModelFinal(
            ResolveParser.PerformanceSpecModelFinalContext ctx) {
        super.enterPerformanceSpecModelFinal(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitPerformanceSpecModelFinal(
            ResolveParser.PerformanceSpecModelFinalContext ctx) {
        super.exitPerformanceSpecModelFinal(ctx);
    }

    // -----------------------------------------------------------
    // Operation-related declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>We create a new object to store all the new array facilities
     * that can be created by the syntactic sugar conversions.</p>
     *
     * @param ctx Procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void enterProcedureDecl(ResolveParser.ProcedureDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack
                .push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new representation for a procedure
     * declaration. Any syntactic sugar will be taken care of before we
     * are done processing this node.</p>
     *
     * @param ctx Procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void exitProcedureDecl(ResolveParser.ProcedureDeclContext ctx) {
        // Pop innermost the array facility container
        ArrayFacilityDecContainer container =
                myArrayFacilityDecContainerStack.pop();

        // TODO:
        // If either side contains an array expression, its index could contain another array expression.
        // In this case, we will generate extra variable declarations and statements that will need to be
        // inserted appropriately.
        /*SwapStmtGenerator generator =
                new SwapStmtGenerator(createLocation(ctx),
                        (ProgramVariableExp) myNodes.removeFrom(ctx.left),
                        (ProgramVariableExp) myNodes.removeFrom(ctx.right));*/
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>We create a new object to store all the new array facilities
     * that can be created by the syntactic sugar conversions.</p>
     *
     * @param ctx Recursive procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void enterRecursiveProcedureDecl(
            ResolveParser.RecursiveProcedureDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack
                .push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new representation for a recursive procedure
     * declaration. Any syntactic sugar will be taken care of before we
     * are done processing this node.</p>
     *
     * @param ctx Recursive procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void exitRecursiveProcedureDecl(
            ResolveParser.RecursiveProcedureDeclContext ctx) {
        // Pop innermost the array facility container
        ArrayFacilityDecContainer container =
                myArrayFacilityDecContainerStack.pop();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>We create a new object to store all the new array facilities
     * that can be created by the syntactic sugar conversions.</p>
     *
     * @param ctx Operation procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void enterOperationProcedureDecl(
            ResolveParser.OperationProcedureDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack
                .push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new representation for an operation procedure
     * declaration. Any syntactic sugar will be taken care of before we
     * are done processing this node.</p>
     *
     * @param ctx Operation procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void exitOperationProcedureDecl(
            ResolveParser.OperationProcedureDeclContext ctx) {
        // Pop innermost the array facility container
        ArrayFacilityDecContainer container =
                myArrayFacilityDecContainerStack.pop();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>We create a new object to store all the new array facilities
     * that can be created by the syntactic sugar conversions.</p>
     *
     * @param ctx Recursive operation procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void enterRecursiveOperationProcedureDecl(
            ResolveParser.RecursiveOperationProcedureDeclContext ctx) {
        // Create a new container
        myArrayFacilityDecContainerStack
                .push(new ArrayFacilityDecContainer(ctx));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new representation for a recursive operation procedure
     * declaration. Any syntactic sugar will be taken care of before we
     * are done processing this node.</p>
     *
     * @param ctx Recursive operation procedure declaration node in ANTLR4 AST.
     */
    @Override
    public void exitRecursiveOperationProcedureDecl(
            ResolveParser.RecursiveOperationProcedureDeclContext ctx) {
        // Pop innermost the array facility container
        ArrayFacilityDecContainer container =
                myArrayFacilityDecContainerStack.pop();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new representation for an operation
     * declaration.</p>
     *
     * @param ctx Operation declaration node in ANTLR4 AST.
     */
    @Override
    public void exitOperationDecl(ResolveParser.OperationDeclContext ctx) {
        // Parameters
        List<ResolveParser.ParameterDeclContext> parameterDeclContexts =
                ctx.operationParameterList().parameterDecl();
        List<ParameterVarDec> varDecs = new ArrayList<>();
        for (ResolveParser.ParameterDeclContext context : parameterDeclContexts) {
            List<TerminalNode> varNames = context.variableDeclGroup().IDENTIFIER();
            for (TerminalNode ident : varNames) {
                varDecs.add((ParameterVarDec) myNodes.removeFrom(ident));
            }
        }

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
        }
        else {
            requires = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.REQUIRES);
        }

        AssertionClause ensures;
        if (ctx.ensuresClause() != null) {
            ensures = (AssertionClause) myNodes.removeFrom(ctx.ensuresClause());
        }
        else {
            ensures = createTrueAssertionClause(createLocation(ctx), AssertionClause.ClauseType.ENSURES);
        }

        myNodes.put(ctx, new OperationDec(createPosSymbol(ctx.name),
                varDecs, returnTy, affectsClause, requires, ensures));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterPerformanceOperationDecl(
            ResolveParser.PerformanceOperationDeclContext ctx) {
        super.enterPerformanceOperationDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitPerformanceOperationDecl(
            ResolveParser.PerformanceOperationDeclContext ctx) {
        super.exitPerformanceOperationDecl(ctx);
    }

    // -----------------------------------------------------------
    // Facility declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new representation for a facility
     * declaration.</p>
     *
     * @param ctx Facility declaration node in ANTLR4 AST.
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
        List<EnhancementSpecItem> enhancements =
                Utilities.collect(EnhancementSpecItem.class, ctx.conceptEnhancementDecl(), myNodes);

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
        List<EnhancementSpecRealizItem> enhancementBodies =
                Utilities.collect(EnhancementSpecRealizItem.class, ctx.enhancementPairDecl(), myNodes);

        // Profile name (if any)
        PosSymbol profileName = null;
        if (ctx.profile != null) {
            profileName = createPosSymbol(ctx.profile);
        }

        myNodes.put(ctx, new FacilityDec(createPosSymbol(ctx.name),
                createPosSymbol(ctx.concept), conceptArgs,
                enhancements,
                createPosSymbol(ctx.impl), conceptRealizArgs,
                enhancementBodies,
                profileName,
                externallyRealized));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new representation for a concept
     * enhancement declaration.</p>
     *
     * @param ctx Concept enhancement declaration node in ANTLR4 AST.
     */
    @Override
    public void exitConceptEnhancementDecl(
            ResolveParser.ConceptEnhancementDeclContext ctx) {
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
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new representation for an
     * enhancement/enhancement realization pair declaration.</p>
     *
     * @param ctx Enhancement pair declaration node in ANTLR4 AST.
     */
    @Override
    public void exitEnhancementPairDecl(
            ResolveParser.EnhancementPairDeclContext ctx) {
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
                createPosSymbol(ctx.impl), enhancementRealizArgs,
                profileName));
    }

    // -----------------------------------------------------------
    // Module arguments
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>Since programming array expressions are simply syntactic sugar
     * that gets converted to call statements, they are not allowed to be
     * passed as module argument. This method stores a boolean that indicates
     * we are in a module argument.</p>
     *
     * @param ctx Module argument node in ANTLR4 AST.
     */
    @Override
    public void enterModuleArgument(ResolveParser.ModuleArgumentContext ctx) {
        myIsProcessingModuleArgument = true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a representation of a module
     * argument.</p>
     *
     * @param ctx Module argument node in ANTLR4 AST.
     */
    @Override
    public void exitModuleArgument(ResolveParser.ModuleArgumentContext ctx) {
        myNodes.put(ctx, new ModuleArgumentItem((ProgramExp) myNodes
                .removeFrom(ctx.progExp())));
        myIsProcessingModuleArgument = false;
    }

    // -----------------------------------------------------------
    // Variable declarations
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores all math variable declarations.</p>
     *
     * @param ctx Math variable declaration groups node in ANTLR4 AST.
     */
    @Override
    public void exitMathVariableDeclGroup(
            ResolveParser.MathVariableDeclGroupContext ctx) {
        Ty rawType = (Ty) myNodes.removeFrom(ctx.mathTypeExp());
        List<TerminalNode> varNames = ctx.IDENTIFIER();
        for (TerminalNode varName : varNames) {
            myNodes.put(varName, new MathVarDec(createPosSymbol(varName
                    .getSymbol()), rawType.clone()));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a math variable declaration.</p>
     *
     * @param ctx Math variable declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathVariableDecl(ResolveParser.MathVariableDeclContext ctx) {
        Ty rawType = (Ty) myNodes.removeFrom(ctx.mathTypeExp());
        myNodes.put(ctx, new MathVarDec(createPosSymbol(ctx.IDENTIFIER()
                .getSymbol()), rawType));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a programming variable declaration.</p>
     *
     * @param ctx Variable declaration node in ANTLR4 AST.
     */
    @Override
    public void exitVariableDecl(ResolveParser.VariableDeclContext ctx) {
        ResolveParser.VariableDeclGroupContext variableDeclGroupContext =
                ctx.variableDeclGroup();

        // Obtain the raw programing type. If we encounter an array type,
        // we add a new array facility and create a new raw name type that
        // refers to this array facility.
        NameTy rawNameTy;
        if (variableDeclGroupContext.programArrayType() != null) {
            // Location
            Location loc =
                    createLocation(variableDeclGroupContext.programArrayType());

            // Create name in the format of "_(Name of Variable)_Array_Fac_(myCounter)"
            String newArrayName = "";
            newArrayName +=
                    ("_" + variableDeclGroupContext.IDENTIFIER(0).getText()
                            + "_Array_Fac_" + newElementCounter++);

            // Create the new raw type
            rawNameTy =
                    new NameTy(new Location(loc), new PosSymbol(new Location(
                            loc), newArrayName), new PosSymbol(
                            new Location(loc), "Static_Array"));

            // Type for the elements in the array
            NameTy arrayElementsTy =
                    (NameTy) myNodes.removeFrom(variableDeclGroupContext
                            .programArrayType().programNamedType());

            // Lower and Upper Bound
            ProgramExp low =
                    (ProgramExp) myNodes.removeFrom(variableDeclGroupContext
                            .programArrayType().progExp(0));
            ProgramExp high =
                    (ProgramExp) myNodes.removeFrom(variableDeclGroupContext
                            .programArrayType().progExp(1));

            // Create the new array facility and add it to the appropriate container
            ArrayFacilityDecContainer innerMostContainer =
                    myArrayFacilityDecContainerStack.pop();
            innerMostContainer.newFacilityDecs.add(createArrayFacilityDec(loc,
                    rawNameTy, arrayElementsTy, low, high));
            myArrayFacilityDecContainerStack.push(innerMostContainer);

            // Store the raw types in the map
            myArrayNameTyToInnerTyMap.put((NameTy) rawNameTy.clone(),
                    (NameTy) arrayElementsTy.clone());
        }
        else {
            rawNameTy =
                    (NameTy) myNodes.removeFrom(variableDeclGroupContext
                            .programNamedType());
        }

        // For each identifier, create a new variable declaration
        for (TerminalNode ident : variableDeclGroupContext.IDENTIFIER()) {
            myNodes.put(ident, new VarDec(createPosSymbol(ident.getSymbol()),
                    rawNameTy.clone()));
        }
    }

    // -----------------------------------------------------------
    // Statements
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the statement representation
     * generated by its child rules.</p>
     *
     * @param ctx Statement node in ANTLR4 AST.
     */
    @Override
    public void exitStmt(ResolveParser.StmtContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a function assignment statement.</p>
     *
     * @param ctx Assign statement node in ANTLR4 AST.
     */
    @Override
    public void exitAssignStmt(ResolveParser.AssignStmtContext ctx) {
        myNodes.put(ctx, new FuncAssignStmt(createLocation(ctx),
                (ProgramVariableExp) myNodes.removeFrom(ctx.left),
                (ProgramExp) myNodes.removeFrom(ctx.right)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a swap statement.</p>
     *
     * @param ctx Swap statement node in ANTLR4 AST.
     */
    @Override
    public void exitSwapStmt(ResolveParser.SwapStmtContext ctx) {
        myNodes.put(ctx, new SwapStmt(createLocation(ctx),
                (ProgramVariableExp) myNodes.removeFrom(ctx.left),
                (ProgramVariableExp) myNodes.removeFrom(ctx.right)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a call statement.</p>
     *
     * @param ctx Call statement node in ANTLR4 AST.
     */
    @Override
    public void exitCallStmt(ResolveParser.CallStmtContext ctx) {
        myNodes.put(ctx, new CallStmt(createLocation(ctx),
                (ProgramFunctionExp) myNodes.removeFrom(ctx.progParamExp())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a presume statement.</p>
     *
     * @param ctx Presume statement node in ANTLR4 AST.
     */
    @Override
    public void exitPresumeStmt(ResolveParser.PresumeStmtContext ctx) {
        myNodes.put(ctx, new PresumeStmt(createLocation(ctx), (Exp) myNodes
                .removeFrom(ctx.mathExp())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a confirm statement with {@code false}
     * as its simplify flag.</p>
     *
     * @param ctx Confirm statement node in ANTLR4 AST.
     */
    @Override
    public void exitConfirmStmt(ResolveParser.ConfirmStmtContext ctx) {
        myNodes.put(ctx, new ConfirmStmt(createLocation(ctx), (Exp) myNodes
                .removeFrom(ctx.mathExp()), false));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates either a {@code Remember} or a {@code Forget}
     * statement.</p>
     *
     * @param ctx Memory statement node in ANTLR4 AST.
     */
    @Override
    public void exitMemoryStmt(ResolveParser.MemoryStmtContext ctx) {
        ResolveConceptualElement element;
        if (ctx.FORGET() != null) {
            element =
                    new MemoryStmt(createLocation(ctx),
                            MemoryStmt.StatementType.FORGET);
        }
        else {
            element =
                    new MemoryStmt(createLocation(ctx),
                            MemoryStmt.StatementType.REMEMBER);
        }

        myNodes.put(ctx, element);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates an if statement.</p>
     *
     * @param ctx If statement node in ANTLR4 AST.
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

        myNodes.put(ctx, new IfStmt(createLocation(ctx),
                new IfConditionItem(createLocation(ctx), conditionExp, ifStmts),
                new ArrayList<IfConditionItem>(), elseStmts));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a while statement.</p>
     *
     * @param ctx While statement node in ANTLR4 AST.
     */
    @Override
    public void exitWhileStmt(ResolveParser.WhileStmtContext ctx) {
        // Condition
        ProgramExp conditionExp = (ProgramExp) myNodes.removeFrom(ctx.progExp());

        // Changing clause
        List<ProgramVariableExp> changingVars = new ArrayList<>();
        if (ctx.changingClause() != null) {
            List<ResolveParser.ProgVarNameExpContext> changingVarContexts =
                    ctx.changingClause().progVarNameExp();
            for (ResolveParser.ProgVarNameExpContext context : changingVarContexts) {
                changingVars.add((ProgramVariableExp) myNodes.removeFrom(context));
            }
        }

        // Maintaining clause
        AssertionClause maintainingClause;
        if (ctx.maintainingClause() != null) {
            maintainingClause = (AssertionClause) myNodes.removeFrom(ctx.maintainingClause());
        }
        else {
            maintainingClause = createTrueAssertionClause(createLocation(ctx),
                    AssertionClause.ClauseType.MAINTAINING);
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
                new LoopVerificationItem(createLocation(ctx), changingVars,
                        maintainingClause, decreasingClause, null), whileStmts));
    }

    // -----------------------------------------------------------
    // Mathematical type theorems
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a representation of a math
     * type theorem declaration.</p>
     *
     * @param ctx Type theorem declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathTypeTheoremDecl(
            ResolveParser.MathTypeTheoremDeclContext ctx) {
        List<MathVarDec> varDecls =
                Utilities.collect(MathVarDec.class,
                        ctx.mathVariableDeclGroup(), myNodes);
        Exp assertionExp = (Exp) myNodes.removeFrom(ctx.mathImpliesExp());

        myNodes.put(ctx, new MathTypeTheoremDec(createPosSymbol(ctx.name),
                varDecls, assertionExp));
    }

    // -----------------------------------------------------------
    // Mathematical theorems, corollaries, etc
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a representation of a math assertion
     * declaration.</p>
     *
     * @param ctx Math assertion declaration node in ANTLR4 AST.
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
            }
            else if (ctx.assertionType.getType() == ResolveLexer.THEOREM_COMMUTATIVE) {
                theoremSubtype = MathAssertionDec.TheoremSubtype.COMMUTATIVITY;
            }
            else {
                theoremSubtype = MathAssertionDec.TheoremSubtype.NONE;
            }

            newElement =
                    new MathAssertionDec(createPosSymbol(ctx.name.getStart()),
                            theoremSubtype, mathExp);
            break;
        case ResolveLexer.AXIOM:
            newElement =
                    new MathAssertionDec(createPosSymbol(ctx.name.getStart()),
                            MathAssertionDec.AssertionType.AXIOM, mathExp);
            break;
        case ResolveLexer.COROLLARY:
            newElement =
                    new MathAssertionDec(createPosSymbol(ctx.name.getStart()),
                            MathAssertionDec.AssertionType.COROLLARY, mathExp);
            break;
        case ResolveLexer.LEMMA:
            newElement =
                    new MathAssertionDec(createPosSymbol(ctx.name.getStart()),
                            MathAssertionDec.AssertionType.LEMMA, mathExp);
            break;
        default:
            newElement =
                    new MathAssertionDec(createPosSymbol(ctx.name.getStart()),
                            MathAssertionDec.AssertionType.PROPERTY, mathExp);
            break;
        }

        myNodes.put(ctx, newElement);
    }

    // -----------------------------------------------------------
    // Mathematical definitions
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method creates a temporary list to store all the
     * temporary definition members</p>
     *
     * @param ctx Defines declaration node in ANTLR4 AST.
     */
    @Override
    public void enterMathDefinesDecl(ResolveParser.MathDefinesDeclContext ctx) {
        myDefinitionMemberList = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a defines declaration.</p>
     *
     * @param ctx Defines declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathDefinesDecl(ResolveParser.MathDefinesDeclContext ctx) {
        DefinitionMembers members = myDefinitionMemberList.remove(0);
        MathDefinitionDec definitionDec =
                new MathDefinitionDec(members.name, members.params,
                        members.rawType, null, false);
        myDefinitionMemberList = null;

        myNodes.put(ctx, definitionDec);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method creates a temporary list to store all the
     * temporary definition members</p>
     *
     * @param ctx Definition declaration node in ANTLR4 AST.
     */
    @Override
    public void enterMathDefinitionDecl(ResolveParser.MathDefinitionDeclContext ctx) {
        myDefinitionMemberList = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the definition representation
     * generated by its child rules.</p>
     *
     * @param ctx Definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathDefinitionDecl(
            ResolveParser.MathDefinitionDeclContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a categorical definition declaration.</p>
     *
     * @param ctx Categorical definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathCategoricalDecl(
            ResolveParser.MathCategoricalDeclContext ctx) {
        // Create all the definition declarations inside
        // the categorical definition
        List<MathDefinitionDec> definitionDecls = new ArrayList<>();
        for (DefinitionMembers members : myDefinitionMemberList) {
            definitionDecls.add(new MathDefinitionDec(members.name, members.params, members.rawType, null, false));
        }
        myDefinitionMemberList = null;

        myNodes.put(ctx, new MathCategoricalDefinitionDec(createPosSymbol(ctx.name), definitionDecls, (Exp) myNodes.removeFrom(ctx.mathExp())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates an implicit definition declaration.</p>
     *
     * @param ctx Implicit definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathImplicitDefinitionDecl(
            ResolveParser.MathImplicitDefinitionDeclContext ctx) {
        DefinitionMembers members = myDefinitionMemberList.remove(0);
        MathDefinitionDec definitionDec =
                new MathDefinitionDec(members.name, members.params,
                        members.rawType, new DefinitionBodyItem((Exp) myNodes
                                .removeFrom(ctx.mathExp())), true);
        myDefinitionMemberList = null;

        myNodes.put(ctx, definitionDec);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates an inductive definition declaration.</p>
     *
     * @param ctx Inductive definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathInductiveDefinitionDecl(
            ResolveParser.MathInductiveDefinitionDeclContext ctx) {
        DefinitionMembers members = myDefinitionMemberList.remove(0);
        MathDefinitionDec definitionDec =
                new MathDefinitionDec(members.name, members.params,
                        members.rawType, new DefinitionBodyItem((Exp) myNodes
                                .removeFrom(ctx.mathExp(0)), (Exp) myNodes
                                .removeFrom(ctx.mathExp(1))), false);
        myDefinitionMemberList = null;

        myNodes.put(ctx, definitionDec);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a standard definition declaration.</p>
     *
     * @param ctx Standard definition declaration node in ANTLR4 AST.
     */
    @Override
    public void exitMathStandardDefinitionDecl(
            ResolveParser.MathStandardDefinitionDeclContext ctx) {
        DefinitionBodyItem bodyItem = null;
        if (ctx.mathExp() != null) {
            bodyItem =
                    new DefinitionBodyItem((Exp) myNodes.removeFrom(ctx
                            .mathExp()));
        }

        DefinitionMembers members = myDefinitionMemberList.remove(0);
        MathDefinitionDec definitionDec =
                new MathDefinitionDec(members.name, members.params,
                        members.rawType, bodyItem, false);
        myDefinitionMemberList = null;

        myNodes.put(ctx, definitionDec);
    }

    // -----------------------------------------------------------
    // Standard definition signatures
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a temporary definition member object that stores
     * all the relevant information needed by the parent rule.</p>
     *
     * @param ctx Infix definition signature node in ANTLR4 AST.
     */
    @Override
    public void exitStandardInfixSignature(
            ResolveParser.StandardInfixSignatureContext ctx) {
        PosSymbol name;
        if (ctx.IDENTIFIER() != null) {
            name = createPosSymbol(ctx.IDENTIFIER().getSymbol());
        }
        else {
            name = createPosSymbol(ctx.infixOp().op);
        }

        List<MathVarDec> varDecls = new ArrayList<>();
        varDecls.add((MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl(0)));
        varDecls.add((MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl(1)));

        myDefinitionMemberList.add(new DefinitionMembers(name, varDecls, (Ty) myNodes.removeFrom(ctx.mathTypeExp())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a temporary definition member object that stores
     * all the relevant information needed by the parent rule.</p>
     *
     * @param ctx Outfix definition signature node in ANTLR4 AST.
     */
    @Override
    public void exitStandardOutfixSignature(
            ResolveParser.StandardOutfixSignatureContext ctx) {
        PosSymbol name = new PosSymbol(createLocation(ctx.lOp), ctx.lOp.getText() + "_" + ctx.rOp.getText());

        List<MathVarDec> varDecls = new ArrayList<>();
        varDecls.add((MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl()));

        myDefinitionMemberList.add(new DefinitionMembers(name, varDecls, (Ty) myNodes.removeFrom(ctx.mathTypeExp())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a temporary definition member object that stores
     * all the relevant information needed by the parent rule.</p>
     *
     * @param ctx Prefix definition signature node in ANTLR4 AST.
     */
    @Override
    public void exitStandardPrefixSignature(
            ResolveParser.StandardPrefixSignatureContext ctx) {
        Token nameToken;
        if (ctx.getStart() == ctx.prefixOp()) {
            nameToken = ctx.prefixOp().getStart();
        }
        else {
            nameToken = ctx.getStart();
        }
        PosSymbol name = createPosSymbol(nameToken);

        List<MathVarDec> varDecls =
                Utilities.collect(MathVarDec.class, ctx
                        .definitionParameterList() != null ? ctx
                        .definitionParameterList().mathVariableDeclGroup()
                        : new ArrayList<ParseTree>(), myNodes);

        myDefinitionMemberList.add(new DefinitionMembers(name, varDecls,
                (Ty) myNodes.removeFrom(ctx.mathTypeExp())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Definition parameter list node in ANTLR4 AST.
     */
    @Override
    public void exitDefinitionParameterList(
            ResolveParser.DefinitionParameterListContext ctx) {
        List<ResolveParser.MathVariableDeclGroupContext> variableDeclGroups =
                ctx.mathVariableDeclGroup();
        for (ResolveParser.MathVariableDeclGroupContext context : variableDeclGroups) {
            List<TerminalNode> identifiers = context.IDENTIFIER();
            for (TerminalNode id : identifiers) {
                myNodes.put(context, myNodes.removeFrom(id));
            }
        }
    }

    // -----------------------------------------------------------
    // Different Types of Clauses
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new affects clause.</p>
     *
     * @param ctx Affects clause node in ANTLR4 AST.
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
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new requires clause.</p>
     *
     * @param ctx Requires clause node in ANTLR4 AST.
     */
    @Override
    public void exitRequiresClause(ResolveParser.RequiresClauseContext ctx) {
        myNodes.put(ctx, createAssertionClause(createLocation(ctx),
                AssertionClause.ClauseType.REQUIRES, ctx.mathExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new ensures clause.</p>
     *
     * @param ctx Ensures clause node in ANTLR4 AST.
     */
    @Override
    public void exitEnsuresClause(ResolveParser.EnsuresClauseContext ctx) {
        myNodes.put(ctx, createAssertionClause(createLocation(ctx),
                AssertionClause.ClauseType.ENSURES, ctx.mathExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new ensures clause.</p>
     *
     * @param ctx Constraint clause node in ANTLR4 AST.
     */
    @Override
    public void exitConstraintClause(ResolveParser.ConstraintClauseContext ctx) {
        myNodes.put(ctx, createAssertionClause(createLocation(ctx),
                AssertionClause.ClauseType.CONSTRAINT, ctx.mathExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new maintaining clause.</p>
     *
     * @param ctx Maintaining clause node in ANTLR4 AST.
     */
    @Override
    public void exitMaintainingClause(ResolveParser.MaintainingClauseContext ctx) {
        myNodes.put(ctx, createAssertionClause(createLocation(ctx),
                AssertionClause.ClauseType.MAINTAINING, ctx.mathExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new decreasing clause.</p>
     *
     * @param ctx Decreasing clause node in ANTLR4 AST.
     */
    @Override
    public void exitDecreasingClause(ResolveParser.DecreasingClauseContext ctx) {
        Exp whichEntailsExp = null;
        if (ctx.mathExp() != null) {
            whichEntailsExp = (Exp) myNodes.removeFrom(ctx.mathExp());
        }
        Exp decreasingExp = (Exp) myNodes.removeFrom(ctx.mathAddingExp());

        myNodes.put(ctx, new AssertionClause(createLocation(ctx),
                AssertionClause.ClauseType.DECREASING, decreasingExp,
                whichEntailsExp));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitCorrespondenceClause(
            ResolveParser.CorrespondenceClauseContext ctx) {
        super.exitCorrespondenceClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConventionClause(ResolveParser.ConventionClauseContext ctx) {
        super.exitConventionClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitDurationClause(ResolveParser.DurationClauseContext ctx) {
        super.exitDurationClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitManipulationDispClause(
            ResolveParser.ManipulationDispClauseContext ctx) {
        super.exitManipulationDispClause(ctx);
    }

    // -----------------------------------------------------------
    // Arbitrary raw type built from a math expression
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new arbitrary type with
     * the math type expression generated by its child rules.</p>
     *
     * @param ctx Math type expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathTypeExp(ResolveParser.MathTypeExpContext ctx) {
        myNodes.put(ctx, new ArbitraryExpTy((Exp) myNodes.removeFrom(ctx
                .getChild(0))));
    }

    // -----------------------------------------------------------
    // Mathematical expressions
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Math expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathExp(ResolveParser.MathExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates an iterated math expression.</p>
     *
     * @param ctx Math iterated expression node in ANTLR4 AST.
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

        MathVarDec varDecl =
                (MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl());
        Exp whereExp = (Exp) myNodes.removeFrom(ctx.mathWhereExp());
        Exp bodyExp = (Exp) myNodes.removeFrom(ctx.mathExp());

        myNodes.put(ctx, new IterativeExp(createLocation(ctx), operator,
                varDecl, whereExp, bodyExp));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates a quantified math expression.</p>
     *
     * @param ctx Math quantified expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathQuantifiedExp(ResolveParser.MathQuantifiedExpContext ctx) {
        ResolveConceptualElement newElement;
        ParseTree child = ctx.getChild(0);

        // Only need to construct a new ResolveConceptualElement if
        // it is a quantified expression.
        if (child instanceof ResolveParser.MathImpliesExpContext) {
            newElement = myNodes.removeFrom(child);
        }
        else {
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

            List<MathVarDec> mathVarDecls =
                    Utilities.collect(MathVarDec.class, ctx
                            .mathVariableDeclGroup() != null ? ctx
                            .mathVariableDeclGroup().IDENTIFIER()
                            : new ArrayList<ParseTree>(), myNodes);
            Exp whereExp = (Exp) myNodes.removeFrom(ctx.mathWhereExp());
            Exp bodyExp = (Exp) myNodes.removeFrom(ctx.mathQuantifiedExp());

            newElement =
                    new QuantExp(createLocation(ctx), quantification,
                            mathVarDecls, whereExp, bodyExp);
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates a new math implies expression.</p>
     *
     * @param ctx Math implies expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathImpliesExp(ResolveParser.MathImpliesExpContext ctx) {
        ResolveConceptualElement newElement;

        // if-then-else expressions
        if (ctx.getStart().getType() == ResolveLexer.IF) {
            Exp testExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(0));
            Exp thenExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(1));
            Exp elseExp = null;
            if (ctx.mathLogicalExp().size() > 2) {
                elseExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(2));
            }

            newElement =
                    new IfExp(createLocation(ctx), testExp, thenExp, elseExp);
        }
        // iff and implies expressions
        else if (ctx.op != null) {
            Exp leftExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(0));
            Exp rightExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(1));

            newElement =
                    new InfixExp(createLocation(ctx), leftExp, null,
                            createPosSymbol(ctx.op), rightExp);
        }
        else {
            newElement = myNodes.removeFrom(ctx.mathLogicalExp(0));
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates a new math infix expression
     * that contains all the logical expressions.</p>
     *
     * @param ctx Math logical expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathLogicalExp(ResolveParser.MathLogicalExpContext ctx) {
        ResolveConceptualElement newElement;
        List<ResolveParser.MathRelationalExpContext> relationalExpContexts =
                ctx.mathRelationalExp();

        // relational expressions
        if (relationalExpContexts.size() == 1) {
            newElement = myNodes.removeFrom(ctx.mathRelationalExp(0));
        }
        // build logical expressions
        else {
            // Obtain the 2 expressions
            Exp leftExp = (Exp) myNodes.removeFrom(ctx.mathRelationalExp(0));
            Exp rightExp = (Exp) myNodes.removeFrom(ctx.mathRelationalExp(1));

            newElement =
                    new InfixExp(leftExp.getLocation(), leftExp, null,
                            createPosSymbol(ctx.op), rightExp);
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules, generates a new math between expression
     * or generates a new math infix expression with the specified
     * operators.</p>
     *
     * @param ctx Math relational expression node in ANTLR4 AST.
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
            joiningExps.add(new InfixExp(new Location(exp1.getLocation()), exp1.clone(), null, createPosSymbol(ctx.op1), exp2.clone()));
            joiningExps.add(new InfixExp(new Location(exp1.getLocation()), exp2.clone(), null, createPosSymbol(ctx.op2), exp3.clone()));

            newElement = new BetweenExp(createLocation(ctx), joiningExps);
        }
        else {
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
                        }
                        else {
                            op = EqualsExp.Operator.NOT_EQUAL;
                        }

                        newElement = new EqualsExp(createLocation(ctx), exp1, null, op, exp2);
                        break;
                    default:
                        newElement = new InfixExp(createLocation(ctx), exp1, null, createPosSymbol(ctx.op), exp2);
                        break;
                }
            }
            else {
                newElement = myNodes.removeFrom(ctx.mathInfixExp(0));
            }
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates a new math infix expression
     * with a range operator.</p>
     *
     * @param ctx Math infix expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathInfixExp(ResolveParser.MathInfixExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create a math infix expression with a range operator if needed
        if (ctx.mathTypeAssertionExp().size() > 1) {
            newElement =
                    new InfixExp(createLocation(ctx), (Exp) myNodes
                            .removeFrom(ctx.mathTypeAssertionExp(0)), null,
                            createPosSymbol(ctx.RANGE().getSymbol()),
                            (Exp) myNodes.removeFrom(ctx
                                    .mathTypeAssertionExp(1)));
        }
        else {
            newElement = myNodes.removeFrom(ctx.mathTypeAssertionExp(0));
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates a new math type assertion expression.</p>
     *
     * @param ctx Math type assertion expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathTypeAssertionExp(
            ResolveParser.MathTypeAssertionExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create a math type assertion expression if needed
        if (ctx.mathTypeExp() != null) {
            newElement =
                    new TypeAssertionExp(createLocation(ctx), (Exp) myNodes
                            .removeFrom(ctx.mathFunctionTypeExp()),
                            (ArbitraryExpTy) myNodes.removeFrom(ctx
                                    .mathTypeExp()));
        }
        else {
            newElement = myNodes.removeFrom(ctx.mathFunctionTypeExp());
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates a new math function type expression.</p>
     *
     * @param ctx Math function type expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathFunctionTypeExp(
            ResolveParser.MathFunctionTypeExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create an function type expression if needed
        if (ctx.mathAddingExp().size() > 1) {
            newElement =
                    new InfixExp(createLocation(ctx), (Exp) myNodes
                            .removeFrom(ctx.mathAddingExp(0)), null,
                            createPosSymbol(ctx.FUNCARROW().getSymbol()),
                            (Exp) myNodes.removeFrom(ctx.mathAddingExp(1)));
        }
        else {
            newElement = myNodes.removeFrom(ctx.mathAddingExp(0));
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates a new math adding expression.</p>
     *
     * @param ctx Math adding expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathAddingExp(ResolveParser.MathAddingExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create an addition expression if needed
        List<ResolveParser.MathMultiplyingExpContext> mathExps =
                ctx.mathMultiplyingExp();
        if (mathExps.size() > 1) {
            PosSymbol qualifier = null;
            if (ctx.qualifier != null) {
                qualifier = createPosSymbol(ctx.qualifier);
            }

            newElement =
                    new InfixExp(createLocation(ctx), (Exp) myNodes
                            .removeFrom(mathExps.get(0)), qualifier,
                            createPosSymbol(ctx.op), (Exp) myNodes
                                    .removeFrom(mathExps.get(1)));
        }
        else {
            newElement = myNodes.removeFrom(mathExps.remove(0));
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates a new math multiplication expression.</p>
     *
     * @param ctx Math multiplication expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathMultiplyingExp(
            ResolveParser.MathMultiplyingExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create a multiplication expression if needed
        List<ResolveParser.MathExponentialExpContext> mathExps =
                ctx.mathExponentialExp();
        if (mathExps.size() != 1) {
            PosSymbol qualifier = null;
            if (ctx.qualifier != null) {
                qualifier = createPosSymbol(ctx.qualifier);
            }

            newElement =
                    new InfixExp(createLocation(ctx), (Exp) myNodes
                            .removeFrom(mathExps.get(0)), qualifier,
                            createPosSymbol(ctx.op), (Exp) myNodes
                                    .removeFrom(mathExps.get(1)));
        }
        else {
            newElement = myNodes.removeFrom(mathExps.remove(0));
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates a new math exponential expression.</p>
     *
     * @param ctx Math exponential expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathExponentialExp(
            ResolveParser.MathExponentialExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create a exponential expression if needed
        if (ctx.mathExponentialExp() != null) {
            newElement =
                    new InfixExp(createLocation(ctx), (Exp) myNodes
                            .removeFrom(ctx.mathPrefixExp()), null,
                            createPosSymbol(ctx.EXP().getSymbol()),
                            (Exp) myNodes.removeFrom(ctx.mathExponentialExp()));
        }
        else {
            newElement = myNodes.removeFrom(ctx.mathPrefixExp());
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expression representation
     * generated by its child rules or generates a new math prefix expression.</p>
     *
     * @param ctx Math prefix expression node in ANTLR4 AST.
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

            newElement =
                    new PrefixExp(createLocation(ctx), qualifier,
                            createPosSymbol(ctx.prefixOp().op), (Exp) myNodes
                                    .removeFrom(ctx.mathPrimaryExp()));
        }
        else {
            newElement = myNodes.removeFrom(ctx.mathPrimaryExp());
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a math expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Math primary expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathPrimaryExp(ResolveParser.MathPrimaryExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the mathematical alternative expression.</p>
     *
     * @param ctx Math alternative expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathAlternativeExp(
            ResolveParser.MathAlternativeExpContext ctx) {
        List<ResolveParser.MathAlternativeExpItemContext> mathExps = ctx.mathAlternativeExpItem();
        List<AltItemExp> alternatives = new ArrayList<>();
        for (ResolveParser.MathAlternativeExpItemContext context : mathExps) {
            alternatives.add((AltItemExp) myNodes.removeFrom(context));
        }

        myNodes.put(ctx, new AlternativeExp(createLocation(ctx), alternatives));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the different alternatives for the
     * mathematical alternative expression.</p>
     *
     * @param ctx Math alternative expression item node in ANTLR4 AST.
     */
    @Override
    public void exitMathAlternativeExpItem(
            ResolveParser.MathAlternativeExpItemContext ctx) {
        Exp testExp = null;
        if (ctx.mathRelationalExp() != null) {
            testExp = (Exp) myNodes.removeFrom(ctx.mathRelationalExp());
        }

        myNodes.put(ctx, new AltItemExp(createLocation(ctx), testExp,
                (Exp) myNodes.removeFrom(ctx.mathAddingExp())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math boolean literal.</p>
     *
     * @param ctx Math boolean literal node in ANTLR4 AST.
     */
    @Override
    public void exitMathBooleanExp(ResolveParser.MathBooleanExpContext ctx) {
        myNodes.put(ctx, new VarExp(createLocation(ctx.BOOLEAN_LITERAL()
                .getSymbol()), null, createPosSymbol(ctx.BOOLEAN_LITERAL()
                .getSymbol())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math integer literal.</p>
     *
     * @param ctx Math integer literal node in ANTLR4 AST.
     */
    @Override
    public void exitMathIntegerExp(ResolveParser.MathIntegerExpContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        myNodes.put(ctx, new IntegerExp(createLocation(ctx), qualifier, Integer
                .valueOf(ctx.INTEGER_LITERAL().getText())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math real literal.</p>
     *
     * @param ctx Math real literal node in ANTLR4 AST.
     */
    @Override
    public void exitMathRealExp(ResolveParser.MathRealExpContext ctx) {
        myNodes.put(ctx, new DoubleExp(createLocation(ctx.REAL_LITERAL()
                .getSymbol()), Double.valueOf(ctx.REAL_LITERAL().getText())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math character literal.</p>
     *
     * @param ctx Math character literal node in ANTLR4 AST.
     */
    @Override
    public void exitMathCharacterExp(ResolveParser.MathCharacterExpContext ctx) {
        myNodes.put(ctx, new CharExp(createLocation(ctx.CHARACTER_LITERAL()
                .getSymbol()), ctx.CHARACTER_LITERAL().getText().charAt(1)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math string literal.</p>
     *
     * @param ctx Math string literal node in ANTLR4 AST.
     */
    @Override
    public void exitMathStringExp(ResolveParser.MathStringExpContext ctx) {
        myNodes.put(ctx, new StringExp(createLocation(ctx.STRING_LITERAL()
                .getSymbol()), ctx.STRING_LITERAL().getText()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the math expressions representation
     * generated by its child rules or generates a new math dotted expression.</p>
     *
     * @param ctx Math dot expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathDotExp(ResolveParser.MathDotExpContext ctx) {
        ResolveConceptualElement newElement;

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
        }
        else {
            newElement = myNodes.removeFrom(ctx.mathFunctionApplicationExp());
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a math function expression or variable expression
     * representation generated by its child rules.</p>
     *
     * @param ctx Math function or variable expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathFunctOrVarExp(ResolveParser.MathFunctOrVarExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.mathCleanFunctionExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math old expression representation.</p>
     *
     * @param ctx Math old expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathOldExp(ResolveParser.MathOldExpContext ctx) {
        myNodes.put(ctx, new OldExp(createLocation(ctx), (Exp) myNodes
                .removeFrom(ctx.mathCleanFunctionExp())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math function expression representation.</p>
     *
     * @param ctx Math function expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathFunctionExp(ResolveParser.MathFunctionExpContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        // function name
        VarExp functionNameExp = new VarExp(createLocation(ctx.name), null, createPosSymbol(ctx.name));

        // exponent-like part to the name
        Exp caratExp = (Exp) myNodes.removeFrom(ctx.mathNestedExp());

        // function arguments
        List<ResolveParser.MathExpContext> mathExps = ctx.mathExp();
        List<Exp> functionArgs = new ArrayList<>();
        for (ResolveParser.MathExpContext context : mathExps) {
            functionArgs.add((Exp) myNodes.removeFrom(context));
        }

        myNodes.put(ctx, new FunctionExp(createLocation(ctx), qualifier, functionNameExp, caratExp, functionArgs));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math variable expression
     * representation generated by its child rules.</p>
     *
     * @param ctx Math variable expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathVarExp(ResolveParser.MathVarExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.mathVarNameExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math variable name expression
     * representation.</p>
     *
     * @param ctx Math variable name expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathVarNameExp(ResolveParser.MathVarNameExpContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        myNodes.put(ctx, new VarExp(createLocation(ctx), qualifier,
                createPosSymbol(ctx.name)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math operator name expression representation.</p>
     *
     * @param ctx Math operator name expression node in ANTLR4 AST.
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
        }
        else {
            opToken = ctx.op;
        }

        myNodes.put(ctx, new VarExp(createLocation(ctx), qualifier,
                createPosSymbol(opToken)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math outfix expression
     * representation generated by its child rules.</p>
     *
     * @param ctx Math outfix expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathOutfixExp(ResolveParser.MathOutfixExpContext ctx) {
        OutfixExp.Operator operator;
        if (ctx.lop.getType() == ResolveLexer.LT) {
            operator = OutfixExp.Operator.ANGLE;
        }
        else if (ctx.lop.getType() == ResolveLexer.LL) {
            operator = OutfixExp.Operator.DBL_ANGLE;
        }
        else if (ctx.lop.getType() == ResolveLexer.BAR) {
            operator = OutfixExp.Operator.BAR;
        }
        else {
            operator = OutfixExp.Operator.DBL_BAR;
        }

        // math expression
        Exp mathExp = (Exp) myNodes.removeFrom(ctx.mathExp());

        myNodes.put(ctx, new OutfixExp(createLocation(ctx), operator, mathExp));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math set builder expression
     * representation generated by its child rules.</p>
     *
     * @param ctx Math set builder expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathSetBuilderExp(ResolveParser.MathSetBuilderExpContext ctx) {
        MathVarDec varDec =
                (MathVarDec) myNodes.removeFrom(ctx.mathVariableDecl());
        Exp whereExp = (Exp) myNodes.removeFrom(ctx.mathWhereExp());
        Exp bodyExp = (Exp) myNodes.removeFrom(ctx.mathExp());

        myNodes.put(ctx, new SetExp(createLocation(ctx), varDec, whereExp,
                bodyExp));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math set collection expression
     * representation generated by its child rules.</p>
     *
     * @param ctx Math set collection expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathSetCollectionExp(
            ResolveParser.MathSetCollectionExpContext ctx) {
        List<ResolveParser.MathExpContext> mathExps = ctx.mathExp();
        Set<MathExp> mathExpsSet = new HashSet<>();
        for (ResolveParser.MathExpContext context : mathExps) {
            mathExpsSet.add((MathExp) myNodes.removeFrom(context));
        }

        myNodes.put(ctx, new SetCollectionExp(createLocation(ctx), mathExpsSet));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math tuple expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Math tuple expression node in ANTLR4 AST.
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
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math lambda expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Math lambda expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathLambdaExp(ResolveParser.MathLambdaExpContext ctx) {
        // Construct the various variables inside the lambda expression
        List<ResolveParser.MathVariableDeclGroupContext> variableDeclGroups = ctx.mathVariableDeclGroup();
        List<MathVarDec> varDecls = new ArrayList<>();
        for (ResolveParser.MathVariableDeclGroupContext context : variableDeclGroups) {
            // Get each math variable declaration
            varDecls.add((MathVarDec) myNodes.removeFrom(context));
        }

        // body expression
        Exp bodyExp = (Exp) myNodes.removeFrom(ctx.mathExp());

        myNodes.put(ctx, new LambdaExp(createLocation(ctx), varDecls, bodyExp));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math Cartesian product expression
     * representation generated by its child rules.</p>
     *
     * @param ctx Math Cartesian product expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathTaggedCartProdTypeExp(
            ResolveParser.MathTaggedCartProdTypeExpContext ctx) {
        // Construct the various variables inside the cartesian product
        List<ResolveParser.MathVariableDeclGroupContext> variableDeclGroups = ctx.mathVariableDeclGroup();
        Map<PosSymbol, ArbitraryExpTy> tagsToFieldsMap = new HashMap<>();
        for (ResolveParser.MathVariableDeclGroupContext context : variableDeclGroups) {
            // Get each math variable declaration
            MathVarDec varDec = (MathVarDec) myNodes.removeFrom(context);
            tagsToFieldsMap.put(varDec.getName(), (ArbitraryExpTy) varDec.getTy());
        }

        myNodes.put(ctx, new CrossTypeExp(createLocation(ctx), tagsToFieldsMap));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the nested math expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Nested math expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathNestedExp(ResolveParser.MathNestedExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.mathExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math expression representing the where clause
     * generated by its child rules.</p>
     *
     * @param ctx Math where expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathWhereExp(ResolveParser.MathWhereExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.mathExp()));
    }

    // -----------------------------------------------------------
    // Programming expressions
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a program application expression.
     * This is really a syntactic sugar for the different function
     * calls, so all we are returning are program function expressions.</p>
     *
     * @param ctx Program application expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgApplicationExp(
            ResolveParser.ProgApplicationExpContext ctx) {
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

        myNodes.put(ctx, new ProgramFunctionExp(createLocation(ctx),
                null, functionName, args));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a program exponent expressions representation
     * generated by its child rules.</p>
     *
     * @param ctx Program exponential expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgExponentialExp(
            ResolveParser.ProgExponentialExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progExponential()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a program exponential expressions.
     * This is really a syntactic sugar for the different function
     * calls, so all we are returning are program function expressions.</p>
     *
     * @param ctx Program exponential node in ANTLR4 AST.
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

            newElement = new ProgramFunctionExp(createLocation(ctx),
                    null, functionName, args);
        }
        else {
            newElement = myNodes.removeFrom(ctx.progUnary());
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a program unary expressions.
     * This is really a syntactic sugar for the different function
     * calls, so all we are returning are program function expressions.</p>
     *
     * @param ctx Program unary expression node in ANTLR4 AST.
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

        myNodes.put(ctx, new ProgramFunctionExp(createLocation(ctx),
                null, functionName, args));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a program primary expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Program primary expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgPrimaryExp(ResolveParser.ProgPrimaryExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progPrimary()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a program literal expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Program literal expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgLiteralExp(ResolveParser.ProgLiteralExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progLiteral()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a program function expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Program function expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgFunctionExp(ResolveParser.ProgFunctionExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progParamExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a program variable expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Program var expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVarExp(ResolveParser.ProgVarExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progVariableExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a program nested expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Program nested expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgNestedExp(ResolveParser.ProgNestedExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the program integer literal.</p>
     *
     * @param ctx Program integer literal node in ANTLR4 AST.
     */
    @Override
    public void exitProgIntegerExp(ResolveParser.ProgIntegerExpContext ctx) {
        myNodes.put(ctx, new ProgramIntegerExp(createLocation(ctx
                .INTEGER_LITERAL().getSymbol()), Integer.valueOf(ctx
                .INTEGER_LITERAL().getText())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the program character literal.</p>
     *
     * @param ctx Program character literal node in ANTLR4 AST.
     */
    @Override
    public void exitProgCharacterExp(ResolveParser.ProgCharacterExpContext ctx) {
        myNodes.put(ctx, new ProgramCharExp(createLocation(ctx
                .CHARACTER_LITERAL().getSymbol()), ctx.CHARACTER_LITERAL()
                .getText().charAt(1)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the program string literal.</p>
     *
     * @param ctx Program string literal node in ANTLR4 AST.
     */
    @Override
    public void exitProgStringExp(ResolveParser.ProgStringExpContext ctx) {
        myNodes
                .put(ctx, new ProgramStringExp(createLocation(ctx
                        .STRING_LITERAL().getSymbol()), ctx.STRING_LITERAL()
                        .getText()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the program function expression representation.</p>
     *
     * @param ctx Program function expression node in ANTLR4 AST.
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

        myNodes.put(ctx, new ProgramFunctionExp(createLocation(ctx),
                qualifier, createPosSymbol(ctx.name), functionArgs));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a program variable expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Program variable expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVariableExp(ResolveParser.ProgVariableExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method generates a new program variable dotted
     * expression.</p>
     *
     * @param ctx Program variable dot expression node in ANTLR4 AST.
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
     * {@inheritDoc}
     * <p>
     * <p>This method stores the generated temporary object to represent a program variable array expression
     * as the last element of this dotted expression. The rule that contains this expression should convert
     * it to the appropriate function call.</p>
     *
     * @param ctx Program variable dot array expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVarDotArrayExp(
            ResolveParser.ProgVarDotArrayExpContext ctx) {
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
     * {@inheritDoc}
     * <p>
     * <p>Checks to see if this expression is part of a module argument.
     * If yes, then this is an error, because we can't convert it to the
     * appropriate function call.</p>
     *
     * @param ctx Program variable array expression node in ANTLR4 AST.
     */
    @Override
    public void enterProgVarArrayExp(ResolveParser.ProgVarArrayExpContext ctx) {
        if (myIsProcessingModuleArgument) {
            throw new SourceErrorException(
                    "Variable array expressions cannot be passed as module arguments.",
                    createPosSymbol(ctx.start), new IllegalArgumentException());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores a temporary object to represent a program variable array expression.
     * The rule that contains this expression should convert it to the appropriate function
     * call.</p>
     *
     * @param ctx Program variable array expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVarArrayExp(ResolveParser.ProgVarArrayExpContext ctx) {
        myNodes.put(ctx, new ProgramVariableArrayExp(createLocation(ctx),
                (ProgramVariableExp) myNodes.removeFrom(ctx.progVarNameExp()),
                (ProgramExp) myNodes.removeFrom(ctx.progExp())));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the program variable name expression representation.</p>
     *
     * @param ctx Program variable name expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVarNameExp(ResolveParser.ProgVarNameExpContext ctx) {
        PosSymbol qualifier = null;
        if (ctx.qualifier != null) {
            qualifier = createPosSymbol(ctx.qualifier);
        }

        myNodes.put(ctx, new ProgramVariableNameExp(createLocation(ctx),
                qualifier, createPosSymbol(ctx.name)));
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Return the complete module representation build by
     * this class.</p>
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
     * <p>Create a {@link FacilityDec} for the current parser rule
     * we are visiting.</p>
     *
     * @param l Location for all the new elements.
     * @param newTy The new name type.
     * @param arrayElementTy The type for the elements in the array.
     * @param lowerBound The lower bound for the array.
     * @param upperBound The upper bound for the array.
     *
     * @return A {@link FacilityDec} for the rule.
     */
    private FacilityDec createArrayFacilityDec(Location l, NameTy newTy, NameTy arrayElementTy, ProgramExp lowerBound, ProgramExp upperBound) {
        // Create a list of arguments for the new FacilityDec and
        // add the type, Low and High for Arrays
        List<ModuleArgumentItem> moduleArgumentItems = new ArrayList<>();
        moduleArgumentItems.add(new ModuleArgumentItem(new ProgramVariableNameExp(new Location(l), arrayElementTy.getQualifier(), arrayElementTy.getName())));
        moduleArgumentItems.add(new ModuleArgumentItem(lowerBound));
        moduleArgumentItems.add(new ModuleArgumentItem(upperBound));

        return new FacilityDec(new PosSymbol(new Location(l), newTy.getName().getName()),
                new PosSymbol(new Location(l), "Static_Array_Template"),
                moduleArgumentItems, new ArrayList<EnhancementSpecItem>(),
                new PosSymbol(new Location(l), "Std_Array_Realiz"), new ArrayList<ModuleArgumentItem>(),
                new ArrayList<EnhancementSpecRealizItem>(), null, true);
    }

    /**
     * <p>Create an {@link AssertionClause} for the current parser rule
     * we are visiting.</p>
     *
     * @param l Location for the clause.
     * @param clauseType The type of clause.
     * @param mathExps List of mathematical expressions in the clause.
     *
     * @return An {@link AssertionClause} for the rule.
     */
    private AssertionClause createAssertionClause(Location l,
            AssertionClause.ClauseType clauseType,
            List<ResolveParser.MathExpContext> mathExps) {
        Exp whichEntailsExp = null;
        if (mathExps.size() > 1) {
            whichEntailsExp = (Exp) myNodes.removeFrom(mathExps.get(1));
        }
        Exp assertionExp = (Exp) myNodes.removeFrom(mathExps.get(0));

        return new AssertionClause(l, clauseType, assertionExp, whichEntailsExp);
    }

    /**
     * <p>Create a location for the current parser rule
     * we are visiting.</p>
     *
     * @param ctx The visiting ANTLR4 parser rule.
     *
     * @return A {@link Location} for the rule.
     */
    private Location createLocation(ParserRuleContext ctx) {
        return createLocation(ctx.getStart());
    }

    /**
     * <p>Create a location for the current parser token
     * we are visiting.</p>
     *
     * @param t The visiting ANTLR4 parser token.
     *
     * @return A {@link Location} for the rule.
     */
    private Location createLocation(Token t) {
        return new Location(new Location(myFile, t.getLine(), t
                .getCharPositionInLine(), ""));
    }

    /**
     * <p>Create a symbol representation for the current
     * parser token we are visiting.</p>
     *
     * @param t The visiting ANTLR4 parser token.
     *
     * @return A {@link PosSymbol} for the rule.
     */
    private PosSymbol createPosSymbol(Token t) {
        return new PosSymbol(createLocation(t), t.getText());
    }

    /**
     * <p>Create an {@link AssertionClause} with {@code true} as the assertion expression
     * for the current parser rule we are visiting.</p>
     *
     * @param l Location for the clause.
     * @param clauseType The type of clause.
     *
     * @return An {@link AssertionClause} for the rule.
     */
    private AssertionClause createTrueAssertionClause(Location l,
            AssertionClause.ClauseType clauseType) {
        return new AssertionClause(l, clauseType, VarExp.getTrueVarExp(
                new Location(l), myTypeGraph));
    }

    /**
     * <p>Obtain the correct parameter mode based on the given
     * context.</p>
     *
     * @param ctx The ANTLR4 parser rule for parameter modes.
     *
     * @return The corresponding {@link ProgramParameterEntry.ParameterMode}.
     */
    private ProgramParameterEntry.ParameterMode getMode(
            ResolveParser.ParameterModeContext ctx) {
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
     * <p>An helper method to retrieve the module arguments (if any).</p>
     *
     * @param moduleParameterListContext The ANTLR4 parser rule for list of module parameters.
     *
     * @return List of {@link ModuleParameterDec}.
     */
    private List<ModuleParameterDec> getModuleArguments(
            ResolveParser.ModuleParameterListContext moduleParameterListContext) {
        List<ModuleParameterDec> parameterDecls = new ArrayList<>();
        if (moduleParameterListContext != null) {
            List<ResolveParser.ModuleParameterDeclContext> parameterDeclContexts =
                    moduleParameterListContext.moduleParameterDecl();
            for (ResolveParser.ModuleParameterDeclContext context : parameterDeclContexts) {
                if (context.constantParameterDecl() != null) {
                    List<TerminalNode> varNames = context.constantParameterDecl().variableDeclGroup().IDENTIFIER();
                    for (TerminalNode ident : varNames) {
                        parameterDecls.add((ModuleParameterDec) myNodes.removeFrom(ident));
                    }
                }
                else {
                    parameterDecls.add((ModuleParameterDec) myNodes.removeFrom(context));
                }
            }
        }

        return parameterDecls;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>This holds items that are needed to build a
     * {@link MathDefinitionDec}</p>
     */
    private class DefinitionMembers {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>Definition name</p> */
        PosSymbol name;

        /** <p>Definition parameters</p> */
        List<MathVarDec> params;

        /** <p>Definition return type</p> */
        Ty rawType;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs a temporary structure to store all the relevant
         * items to build a {@link MathDefinitionDec}.</p>
         *
         * @param name Definition name.
         * @param params Definition parameters.
         * @param rawType Definition return type.
         */
        DefinitionMembers(PosSymbol name, List<MathVarDec> params, Ty rawType) {
            this.name = name;
            this.params = params;
            this.rawType = rawType;
        }

    }

    /**
     * <p>This holds new items related to syntactic sugar conversions for
     * raw array types.</p>
     */
    private class ArrayFacilityDecContainer {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>The context that instantiated this object</p> */
        ParserRuleContext instantiatingContext;

        /**
         * <p>List of new facility declaration objects.</p>
         *
         * <p><strong>Note:</strong> The only facilities generated at the moment are
         * new {@code Static_Array_Template} facilities.</p>
         */
        List<FacilityDec> newFacilityDecs;

        // TODO: Move the following to the conversion walker.
        /**
         * <p>List of new variable declaration objects.</p>
         *
         * <p><strong>Note:</strong> The only variables generated at the moment are
         * new integer variables to store the indexes resulting from program array
         * conversions.</p>
         */
        //List<VarDec> newVarDecs;

        /**
         * <p>List of new statements that needs to be inserted before the code
         * that contains a program array expression.</p>
         *
         * <p><strong>Note:</strong> The only statements generated at the moment are
         * either new function assignment statements from indexes in
         * program array expressions or call statements to swap elements in the array(s).</p>
         */
        //List<Statement> newPreStmts;

        /**
         * <p>List of new statements that needs to be inserted after the code
         * that contains a program array expression.</p>
         *
         * <p><strong>Note:</strong> The only statements generated at the moment are
         * call statements to swap elements in the array(s).</p>
         */
        //List<Statement> newPostStmts;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs a temporary structure to store all the newly array facility
         * declarations that resulted from syntactic sugar conversions for raw array types.</p>
         *
         * @param instantiatingContext The context that instantiated this object.
         */
        ArrayFacilityDecContainer(ParserRuleContext instantiatingContext) {
            this.instantiatingContext = instantiatingContext;
            newFacilityDecs = new ArrayList<>();
        }
    }

}