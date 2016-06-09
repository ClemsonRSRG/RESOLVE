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

import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathAssertionDec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathCategoricalDefinitionDec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathDefinitionDec;
import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathTypeTheoremDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ShortFacilityModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.absyn.items.mathitems.DefinitionBodyItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.EnhancementSpecItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.ModuleArgumentItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.PrecisModuleDec;
import edu.clemson.cs.rsrg.absyn.rawtypes.ArbitraryExpTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.errorhandling.ErrorHandler;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.misc.Utilities;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
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

    /** <p>Stack of current syntactic sugar conversions</p> */
    //private Stack<ProgramExpAdapter> myCurrentProgExpAdapterStack;

    /** <p>The complete module representation.</p> */
    private ModuleDec myFinalModule;

    /** <p>The current file we are compiling.</p> */
    private final ResolveFile myFile;

    /** <p>The error listener.</p> */
    private final ErrorHandler myErrorHandler;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Create a listener to walk the entire compiler generated
     * ANTLR4 parser tree and generate the intermediate representation
     * objects used by the subsequent modules.</p>
     *
     * @param file The current file we are compiling.
     */
    public TreeBuildingListener(ResolveFile file, ErrorHandler errorHandler) {
        myErrorHandler = errorHandler;
        myFile = file;
        myFinalModule = null;
        myNodes = new ParseTreeProperty<>();
        myDefinitionMemberList = null;
        myIsProcessingModuleArgument = false;
        //myCurrentProgExpAdapterStack = new Stack<>();
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
            myErrorHandler.error(createLocation(ctx.name),
                    "Module name does not match filename.");
        }

        if (!myFile.getName().equals(ctx.closename.getText())) {
            myErrorHandler.error(createLocation(ctx.closename),
                    "End module name does not match the filename.");
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
        List<Dec> decls =
                Utilities.collect(Dec.class,
                        ctx.precisItems() != null ? ctx.precisItems().precisItem() : new ArrayList<ParseTree>(),
                        myNodes);
        List<ModuleParameterDec> parameterDecls = new ArrayList<>();
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(),
                myNodes);
        PrecisModuleDec precis = new PrecisModuleDec(createLocation(ctx), createPosSymbol(ctx.name), parameterDecls, uses, decls);

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
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterFacilityModule(ResolveParser.FacilityModuleContext ctx) {
        super.enterFacilityModule(ctx);
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
    public void enterFacilityItems(ResolveParser.FacilityItemsContext ctx) {
        super.enterFacilityItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitFacilityItems(ResolveParser.FacilityItemsContext ctx) {
        super.exitFacilityItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterFacilityItem(ResolveParser.FacilityItemContext ctx) {
        super.enterFacilityItem(ctx);
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
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConceptModule(ResolveParser.ConceptModuleContext ctx) {
        super.enterConceptModule(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConceptModule(ResolveParser.ConceptModuleContext ctx) {
        super.exitConceptModule(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConceptItems(ResolveParser.ConceptItemsContext ctx) {
        super.enterConceptItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConceptItems(ResolveParser.ConceptItemsContext ctx) {
        super.exitConceptItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConceptItem(ResolveParser.ConceptItemContext ctx) {
        super.enterConceptItem(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConceptItem(ResolveParser.ConceptItemContext ctx) {
        super.exitConceptItem(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConceptImplModule(
            ResolveParser.ConceptImplModuleContext ctx) {
        super.enterConceptImplModule(ctx);
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

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterEnhancementModule(
            ResolveParser.EnhancementModuleContext ctx) {
        super.enterEnhancementModule(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitEnhancementModule(ResolveParser.EnhancementModuleContext ctx) {
        super.exitEnhancementModule(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterEnhancementItems(ResolveParser.EnhancementItemsContext ctx) {
        super.enterEnhancementItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitEnhancementItems(ResolveParser.EnhancementItemsContext ctx) {
        super.exitEnhancementItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterEnhancementItem(ResolveParser.EnhancementItemContext ctx) {
        super.enterEnhancementItem(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitEnhancementItem(ResolveParser.EnhancementItemContext ctx) {
        super.exitEnhancementItem(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterEnhancementImplModule(
            ResolveParser.EnhancementImplModuleContext ctx) {
        super.enterEnhancementImplModule(ctx);
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
    public void enterImplItems(ResolveParser.ImplItemsContext ctx) {
        super.enterImplItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitImplItems(ResolveParser.ImplItemsContext ctx) {
        super.exitImplItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterImplItem(ResolveParser.ImplItemContext ctx) {
        super.enterImplItem(ctx);
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

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConceptPerformanceModule(
            ResolveParser.ConceptPerformanceModuleContext ctx) {
        super.enterConceptPerformanceModule(ctx);
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
    public void enterConceptPerformanceItems(
            ResolveParser.ConceptPerformanceItemsContext ctx) {
        super.enterConceptPerformanceItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConceptPerformanceItems(
            ResolveParser.ConceptPerformanceItemsContext ctx) {
        super.exitConceptPerformanceItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConceptPerformanceItem(
            ResolveParser.ConceptPerformanceItemContext ctx) {
        super.enterConceptPerformanceItem(ctx);
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

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterEnhancementPerformanceModule(
            ResolveParser.EnhancementPerformanceModuleContext ctx) {
        super.enterEnhancementPerformanceModule(ctx);
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
    public void enterEnhancementPerformanceItems(
            ResolveParser.EnhancementPerformanceItemsContext ctx) {
        super.enterEnhancementPerformanceItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitEnhancementPerformanceItems(
            ResolveParser.EnhancementPerformanceItemsContext ctx) {
        super.exitEnhancementPerformanceItems(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterEnhancementPerformanceItem(
            ResolveParser.EnhancementPerformanceItemContext ctx) {
        super.enterEnhancementPerformanceItem(ctx);
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

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterOperationParameterList(
            ResolveParser.OperationParameterListContext ctx) {
        super.enterOperationParameterList(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitOperationParameterList(
            ResolveParser.OperationParameterListContext ctx) {
        super.exitOperationParameterList(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterModuleParameterList(
            ResolveParser.ModuleParameterListContext ctx) {
        super.enterModuleParameterList(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitModuleParameterList(
            ResolveParser.ModuleParameterListContext ctx) {
        super.exitModuleParameterList(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterModuleParameterDecl(
            ResolveParser.ModuleParameterDeclContext ctx) {
        super.enterModuleParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitModuleParameterDecl(
            ResolveParser.ModuleParameterDeclContext ctx) {
        super.exitModuleParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterDefinitionParameterDecl(
            ResolveParser.DefinitionParameterDeclContext ctx) {
        super.enterDefinitionParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitDefinitionParameterDecl(
            ResolveParser.DefinitionParameterDeclContext ctx) {
        super.exitDefinitionParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterTypeParameterDecl(
            ResolveParser.TypeParameterDeclContext ctx) {
        super.enterTypeParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitTypeParameterDecl(ResolveParser.TypeParameterDeclContext ctx) {
        super.exitTypeParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConstantParameterDecl(
            ResolveParser.ConstantParameterDeclContext ctx) {
        super.enterConstantParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConstantParameterDecl(
            ResolveParser.ConstantParameterDeclContext ctx) {
        super.exitConstantParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterOperationParameterDecl(
            ResolveParser.OperationParameterDeclContext ctx) {
        super.enterOperationParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitOperationParameterDecl(
            ResolveParser.OperationParameterDeclContext ctx) {
        super.exitOperationParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConceptImplParameterDecl(
            ResolveParser.ConceptImplParameterDeclContext ctx) {
        super.enterConceptImplParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConceptImplParameterDecl(
            ResolveParser.ConceptImplParameterDeclContext ctx) {
        super.exitConceptImplParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterParameterDecl(ResolveParser.ParameterDeclContext ctx) {
        super.enterParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitParameterDecl(ResolveParser.ParameterDeclContext ctx) {
        super.exitParameterDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterParameterMode(ResolveParser.ParameterModeContext ctx) {
        super.enterParameterMode(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitParameterMode(ResolveParser.ParameterModeContext ctx) {
        super.exitParameterMode(ctx);
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
    public void enterProgramArrayType(ResolveParser.ProgramArrayTypeContext ctx) {
        super.enterProgramArrayType(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgramArrayType(ResolveParser.ProgramArrayTypeContext ctx) {
        super.exitProgramArrayType(ctx);
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

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterTypeModelDecl(ResolveParser.TypeModelDeclContext ctx) {
        super.enterTypeModelDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitTypeModelDecl(ResolveParser.TypeModelDeclContext ctx) {
        super.exitTypeModelDecl(ctx);
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

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterSharedStateDecl(ResolveParser.SharedStateDeclContext ctx) {
        super.enterSharedStateDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitSharedStateDecl(ResolveParser.SharedStateDeclContext ctx) {
        super.exitSharedStateDecl(ctx);
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

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterSpecModelInit(ResolveParser.SpecModelInitContext ctx) {
        super.enterSpecModelInit(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitSpecModelInit(ResolveParser.SpecModelInitContext ctx) {
        super.exitSpecModelInit(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterSpecModelFinal(ResolveParser.SpecModelFinalContext ctx) {
        super.enterSpecModelFinal(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitSpecModelFinal(ResolveParser.SpecModelFinalContext ctx) {
        super.exitSpecModelFinal(ctx);
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
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProcedureDecl(ResolveParser.ProcedureDeclContext ctx) {
        super.enterProcedureDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProcedureDecl(ResolveParser.ProcedureDeclContext ctx) {
        super.exitProcedureDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterRecursiveProcedureDecl(
            ResolveParser.RecursiveProcedureDeclContext ctx) {
        super.enterRecursiveProcedureDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitRecursiveProcedureDecl(
            ResolveParser.RecursiveProcedureDeclContext ctx) {
        super.exitRecursiveProcedureDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterOperationProcedureDecl(
            ResolveParser.OperationProcedureDeclContext ctx) {
        super.enterOperationProcedureDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitOperationProcedureDecl(
            ResolveParser.OperationProcedureDeclContext ctx) {
        super.exitOperationProcedureDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterRecursiveOperationProcedureDecl(
            ResolveParser.RecursiveOperationProcedureDeclContext ctx) {
        super.enterRecursiveOperationProcedureDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitRecursiveOperationProcedureDecl(
            ResolveParser.RecursiveOperationProcedureDeclContext ctx) {
        super.exitRecursiveOperationProcedureDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterOperationDecl(ResolveParser.OperationDeclContext ctx) {
        super.enterOperationDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitOperationDecl(ResolveParser.OperationDeclContext ctx) {
        super.exitOperationDecl(ctx);
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
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterVariableDeclGroup(
            ResolveParser.VariableDeclGroupContext ctx) {
        super.enterVariableDeclGroup(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitVariableDeclGroup(ResolveParser.VariableDeclGroupContext ctx) {
        super.exitVariableDeclGroup(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterVariableDecl(ResolveParser.VariableDeclContext ctx) {
        super.enterVariableDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitVariableDecl(ResolveParser.VariableDeclContext ctx) {
        super.exitVariableDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterAuxVariableDeclGroup(
            ResolveParser.AuxVariableDeclGroupContext ctx) {
        super.enterAuxVariableDeclGroup(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitAuxVariableDeclGroup(
            ResolveParser.AuxVariableDeclGroupContext ctx) {
        super.exitAuxVariableDeclGroup(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterAuxVariableDecl(ResolveParser.AuxVariableDeclContext ctx) {
        super.enterAuxVariableDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitAuxVariableDecl(ResolveParser.AuxVariableDeclContext ctx) {
        super.exitAuxVariableDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterModuleStateVariableDecl(
            ResolveParser.ModuleStateVariableDeclContext ctx) {
        super.enterModuleStateVariableDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitModuleStateVariableDecl(
            ResolveParser.ModuleStateVariableDeclContext ctx) {
        super.exitModuleStateVariableDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterStmt(ResolveParser.StmtContext ctx) {
        super.enterStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitStmt(ResolveParser.StmtContext ctx) {
        super.exitStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterAssignStmt(ResolveParser.AssignStmtContext ctx) {
        super.enterAssignStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitAssignStmt(ResolveParser.AssignStmtContext ctx) {
        super.exitAssignStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterSwapStmt(ResolveParser.SwapStmtContext ctx) {
        super.enterSwapStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitSwapStmt(ResolveParser.SwapStmtContext ctx) {
        super.exitSwapStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterCallStmt(ResolveParser.CallStmtContext ctx) {
        super.enterCallStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitCallStmt(ResolveParser.CallStmtContext ctx) {
        super.exitCallStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterPresumeStmt(ResolveParser.PresumeStmtContext ctx) {
        super.enterPresumeStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitPresumeStmt(ResolveParser.PresumeStmtContext ctx) {
        super.exitPresumeStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConfirmStmt(ResolveParser.ConfirmStmtContext ctx) {
        super.enterConfirmStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConfirmStmt(ResolveParser.ConfirmStmtContext ctx) {
        super.exitConfirmStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterIfStmt(ResolveParser.IfStmtContext ctx) {
        super.enterIfStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitIfStmt(ResolveParser.IfStmtContext ctx) {
        super.exitIfStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterElsePart(ResolveParser.ElsePartContext ctx) {
        super.enterElsePart(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitElsePart(ResolveParser.ElsePartContext ctx) {
        super.exitElsePart(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterWhileStmt(ResolveParser.WhileStmtContext ctx) {
        super.enterWhileStmt(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitWhileStmt(ResolveParser.WhileStmtContext ctx) {
        super.exitWhileStmt(ctx);
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
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConstraintClause(ResolveParser.ConstraintClauseContext ctx) {
        super.enterConstraintClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConstraintClause(ResolveParser.ConstraintClauseContext ctx) {
        super.exitConstraintClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterChangingClause(ResolveParser.ChangingClauseContext ctx) {
        super.enterChangingClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitChangingClause(ResolveParser.ChangingClauseContext ctx) {
        super.exitChangingClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterMaintainingClause(
            ResolveParser.MaintainingClauseContext ctx) {
        super.enterMaintainingClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMaintainingClause(ResolveParser.MaintainingClauseContext ctx) {
        super.exitMaintainingClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterDecreasingClause(ResolveParser.DecreasingClauseContext ctx) {
        super.enterDecreasingClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitDecreasingClause(ResolveParser.DecreasingClauseContext ctx) {
        super.exitDecreasingClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterWhereClause(ResolveParser.WhereClauseContext ctx) {
        super.enterWhereClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitWhereClause(ResolveParser.WhereClauseContext ctx) {
        super.exitWhereClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterCorrespondenceClause(
            ResolveParser.CorrespondenceClauseContext ctx) {
        super.enterCorrespondenceClause(ctx);
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
    public void enterConventionClause(ResolveParser.ConventionClauseContext ctx) {
        super.enterConventionClause(ctx);
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
    public void enterDurationClause(ResolveParser.DurationClauseContext ctx) {
        super.enterDurationClause(ctx);
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
    public void enterManipulationDispClause(
            ResolveParser.ManipulationDispClauseContext ctx) {
        super.enterManipulationDispClause(ctx);
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
        Exp whereExp = (Exp) myNodes.removeFrom(ctx.whereClause());
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
            Exp whereExp = (Exp) myNodes.removeFrom(ctx.whereClause());
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

            // Obtain the 2 expressions
            Exp exp1 = (Exp) myNodes.removeFrom(mathExps.get(0));
            Exp exp2 = (Exp) myNodes.removeFrom(mathExps.get(1));

            newElement =
                    new InfixExp(createLocation(ctx), exp1, qualifier,
                            createPosSymbol(ctx.op), exp2);
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

            // Obtain the 2 expressions
            Exp exp1 = (Exp) myNodes.removeFrom(mathExps.get(0));
            Exp exp2 = (Exp) myNodes.removeFrom(mathExps.get(1));

            newElement =
                    new InfixExp(createLocation(ctx), exp1, qualifier,
                            createPosSymbol(ctx.op), exp2);
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
        Exp whereExp = (Exp) myNodes.removeFrom(ctx.whereClause());
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

        myNodes.put(ctx, new ProgramFunctionExp(createLocation(ctx), null, functionName, args));
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

            newElement = new ProgramFunctionExp(createLocation(ctx), null, functionName, args);
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

        myNodes.put(ctx, new ProgramFunctionExp(createLocation(ctx), null, functionName, args));
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
     * <p>This method stores a program named variable expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Program named variable expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgNamedVariableExp(
            ResolveParser.ProgNamedVariableExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.progVariableExp()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This is really a syntactic sugar for the different function calls
     * in {@code Static_Array_Template}, so we are returning a function call
     * generated by its child rules.</p>
     *
     * @param ctx Program array expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgArrayExp(ResolveParser.ProgArrayExpContext ctx) {
        myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
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

        myNodes.put(ctx, new ProgramFunctionExp(createLocation(ctx), qualifier, createPosSymbol(ctx.name), functionArgs));
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
    public void enterProgVariableArrayExp(
            ResolveParser.ProgVariableArrayExpContext ctx) {
        if (myIsProcessingModuleArgument) {
            myErrorHandler
                    .error(createLocation(ctx),
                            "Variable array expressions cannot be passed as module arguments.");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This is really a syntactic sugar for the different function calls
     * in {@code Static_Array_Template}, so we are generating the new
     * function calls and storing it.</p>
     *
     * @param ctx Program variable array expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgVariableArrayExp(
            ResolveParser.ProgVariableArrayExpContext ctx) {
        // TODO: Migrate VariableArrayExp conversions logic from PreProcessor.
        super.enterProgVariableArrayExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method either stores the program variable expressions
     * representation generated by its child rules.</p>
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
     * <p>This method either stores the program expressions representation
     * generated by its child rules or generates a new program variable
     * dotted expression.</p>
     *
     * @param ctx Program dot expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgDotExp(ResolveParser.ProgDotExpContext ctx) {
        ResolveConceptualElement newElement;

        // Create a dot expression if needed
        List<ResolveParser.ProgNamedExpContext> progNamedExp = ctx.progNamedExp();
        if (progNamedExp.size() > 0) {
            // dotted expressions
            List<ProgramVariableExp> dotExps = new ArrayList<>();

            for (ResolveParser.ProgNamedExpContext context : progNamedExp) {
                dotExps.add((ProgramVariableExp) myNodes.removeFrom(context));
            }

            newElement = new ProgramVariableDotExp(createLocation(ctx), dotExps);
        }
        else {
            newElement = myNodes.removeFrom(ctx.progNamedExp(0));
        }

        myNodes.put(ctx, newElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the program variable expression representation.</p>
     *
     * @param ctx Program variable expression node in ANTLR4 AST.
     */
    @Override
    public void exitProgNamedExp(ResolveParser.ProgNamedExpContext ctx) {
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
     * <p>Create an {@link AssertionClause} for the current parser rule
     * we are visiting.</p>
     *
     * @param l Location for the clause.
     * @param clauseType The type of clause.
     * @param mathExps List of mathematical expressions in the clause.
     *
     * @return A {@link AssertionClause} for the rule.
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
     * <p>This holds items related to syntactic sugar conversions for
     * {@link ProgramExp}s.</p>
     *
     * <p><strong>Note:</strong> The only conversions we do right now are program
     * array conversions.</p>
     */
    private class ProgramExpAdapter {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>The context that instantiated this object</p> */
        ParserRuleContext instantiatingContext;

        /**
         * <p>List of new variable declaration objects.</p>
         *
         * <p><strong>Note:</strong> The only variables generated at the moment are
         * new integer variables to store the indexes resulting from program array
         * conversions.</p>
         */
        List<VarDec> newVarDecs;

        /**
         * <p>List of new statements that needs to be inserted before the code
         * that contains a program array expression.</p>
         *
         * <p><strong>Note:</strong> The only statements generated at the moment are
         * either new function assignment statements that stores the indexes in
         * program array expressions or call statements to swap elements in the array(s).</p>
         */
        List<Statement> newPreStmts;

        /**
         * <p>List of new statements that needs to be inserted after the code
         * that contains a program array expression.</p>
         *
         * <p><strong>Note:</strong> The only statements generated at the moment are
         * call statements to swap elements in the array(s).</p>
         */
        List<Statement> newPostStmts;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs a temporary structure to store all the newly declared
         * items that resulted from syntactic sugar conversions for {@link ProgramExp}s.</p>
         *
         * @param instantiatingContext The context that instantiated this object.
         */
        ProgramExpAdapter(ParserRuleContext instantiatingContext) {
            this.instantiatingContext = instantiatingContext;
            newVarDecs = new ArrayList<>();
            newPreStmts = new ArrayList<>();
            newPostStmts = new ArrayList<>();
        }
    }

    /**
     * <p>This holds items related to syntactic sugar conversions for
     * {@link Ty}s.</p>
     *
     * <p><strong>Note:</strong> The only conversions we do right now are
     * raw array types.</p>
     */
    private class RawTypeAdapter {

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

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs a temporary structure to store all the newly facility
         * declarations that resulted from syntactic sugar conversions for {@link Ty}s.</p>
         *
         * @param instantiatingContext The context that instantiated this object.
         */
        RawTypeAdapter(ParserRuleContext instantiatingContext) {
            this.instantiatingContext = instantiatingContext;
            newFacilityDecs = new ArrayList<>();
        }
    }

}