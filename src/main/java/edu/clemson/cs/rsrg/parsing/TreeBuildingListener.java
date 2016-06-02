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

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.PrecisModuleDec;
import edu.clemson.cs.rsrg.absyn.rawtypes.ArbitraryExpTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
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
        /*List<Dec> decls =
                Utilities.collect(Dec.class,
                        ctx.precisItems() != null ? ctx.precisItems().precisItem() : new ArrayList<ParseTree>(),
                        myNodes);*/
        List<ModuleParameterDec> parameterDecls = new ArrayList<>();
        List<UsesItem> uses = Utilities.collect(UsesItem.class,
                ctx.usesList() != null ? ctx.usesList().usesItem() : new ArrayList<ParseTree>(),
                myNodes);
        PrecisModuleDec precis = new PrecisModuleDec(createLocation(ctx),
                createPosSymbol(ctx.name), parameterDecls, uses, new ArrayList<Dec>());

        myNodes.put(ctx, precis);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitPrecisItem(ResolveParser.PrecisItemContext ctx) {
        super.exitPrecisItem(ctx);
    }

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

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterShortFacilityModule(
            ResolveParser.ShortFacilityModuleContext ctx) {
        super.enterShortFacilityModule(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitShortFacilityModule(
            ResolveParser.ShortFacilityModuleContext ctx) {
        super.exitShortFacilityModule(ctx);
    }

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

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterType(ResolveParser.TypeContext ctx) {
        super.enterType(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitType(ResolveParser.TypeContext ctx) {
        super.exitType(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterRecord(ResolveParser.RecordContext ctx) {
        super.enterRecord(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitRecord(ResolveParser.RecordContext ctx) {
        super.exitRecord(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterRecordVariableDeclGroup(
            ResolveParser.RecordVariableDeclGroupContext ctx) {
        super.enterRecordVariableDeclGroup(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitRecordVariableDeclGroup(
            ResolveParser.RecordVariableDeclGroupContext ctx) {
        super.exitRecordVariableDeclGroup(ctx);
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

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterFacilityDecl(ResolveParser.FacilityDeclContext ctx) {
        super.enterFacilityDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitFacilityDecl(ResolveParser.FacilityDeclContext ctx) {
        super.exitFacilityDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterConceptEnhancementDecl(
            ResolveParser.ConceptEnhancementDeclContext ctx) {
        super.enterConceptEnhancementDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitConceptEnhancementDecl(
            ResolveParser.ConceptEnhancementDeclContext ctx) {
        super.exitConceptEnhancementDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterEnhancementPairDecl(
            ResolveParser.EnhancementPairDeclContext ctx) {
        super.enterEnhancementPairDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitEnhancementPairDecl(
            ResolveParser.EnhancementPairDeclContext ctx) {
        super.exitEnhancementPairDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterModuleArgumentList(
            ResolveParser.ModuleArgumentListContext ctx) {
        super.enterModuleArgumentList(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitModuleArgumentList(
            ResolveParser.ModuleArgumentListContext ctx) {
        super.exitModuleArgumentList(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterModuleArgument(ResolveParser.ModuleArgumentContext ctx) {
        super.enterModuleArgument(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitModuleArgument(ResolveParser.ModuleArgumentContext ctx) {
        super.exitModuleArgument(ctx);
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
            myNodes.put(ctx, new MathVarDec(
                    createPosSymbol(varName.getSymbol()), rawType));
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
    public void enterStateVariableDecl(
            ResolveParser.StateVariableDeclContext ctx) {
        super.enterStateVariableDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitStateVariableDecl(ResolveParser.StateVariableDeclContext ctx) {
        super.exitStateVariableDecl(ctx);
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
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterMathTypeTheoremDecl(
            ResolveParser.MathTypeTheoremDeclContext ctx) {
        super.enterMathTypeTheoremDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathTypeTheoremDecl(
            ResolveParser.MathTypeTheoremDeclContext ctx) {
        super.exitMathTypeTheoremDecl(ctx);
    }

    // -----------------------------------------------------------
    // Mathematical theorems, corollaries, etc
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterMathAssertionDecl(
            ResolveParser.MathAssertionDeclContext ctx) {
        super.enterMathAssertionDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathAssertionDecl(ResolveParser.MathAssertionDeclContext ctx) {
        super.exitMathAssertionDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterMathTheoremIdent(ResolveParser.MathTheoremIdentContext ctx) {
        super.enterMathTheoremIdent(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathTheoremIdent(ResolveParser.MathTheoremIdentContext ctx) {
        super.exitMathTheoremIdent(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterMathDefinesDecl(ResolveParser.MathDefinesDeclContext ctx) {
        super.enterMathDefinesDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathDefinesDecl(ResolveParser.MathDefinesDeclContext ctx) {
        super.exitMathDefinesDecl(ctx);
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
    //myNodes.put(ctx, myNodes.get(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathCategoricalDecl(
            ResolveParser.MathCategoricalDeclContext ctx) {
        super.exitMathCategoricalDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathImplicitDefinitionDecl(
            ResolveParser.MathImplicitDefinitionDeclContext ctx) {
        super.exitMathImplicitDefinitionDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathInductiveDefinitionDecl(
            ResolveParser.MathInductiveDefinitionDeclContext ctx) {
        super.exitMathInductiveDefinitionDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathStandardDefinitionDecl(
            ResolveParser.MathStandardDefinitionDeclContext ctx) {
        super.exitMathStandardDefinitionDecl(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterCategoricalDefinitionSignature(
            ResolveParser.CategoricalDefinitionSignatureContext ctx) {
        super.enterCategoricalDefinitionSignature(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitCategoricalDefinitionSignature(
            ResolveParser.CategoricalDefinitionSignatureContext ctx) {
        super.exitCategoricalDefinitionSignature(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterDefinitionSignature(
            ResolveParser.DefinitionSignatureContext ctx) {
        super.enterDefinitionSignature(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitDefinitionSignature(
            ResolveParser.DefinitionSignatureContext ctx) {
        super.exitDefinitionSignature(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterStandardInfixSignature(
            ResolveParser.StandardInfixSignatureContext ctx) {
        super.enterStandardInfixSignature(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitStandardInfixSignature(
            ResolveParser.StandardInfixSignatureContext ctx) {
        super.exitStandardInfixSignature(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterStandardOutfixSignature(
            ResolveParser.StandardOutfixSignatureContext ctx) {
        super.enterStandardOutfixSignature(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitStandardOutfixSignature(
            ResolveParser.StandardOutfixSignatureContext ctx) {
        super.exitStandardOutfixSignature(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterStandardPrefixSignature(
            ResolveParser.StandardPrefixSignatureContext ctx) {
        super.enterStandardPrefixSignature(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitStandardPrefixSignature(
            ResolveParser.StandardPrefixSignatureContext ctx) {
        super.exitStandardPrefixSignature(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterPrefixOp(ResolveParser.PrefixOpContext ctx) {
        super.enterPrefixOp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitPrefixOp(ResolveParser.PrefixOpContext ctx) {
        super.exitPrefixOp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterInfixOp(ResolveParser.InfixOpContext ctx) {
        super.enterInfixOp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitInfixOp(ResolveParser.InfixOpContext ctx) {
        super.exitInfixOp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterDefinitionParameterList(
            ResolveParser.DefinitionParameterListContext ctx) {
        super.enterDefinitionParameterList(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitDefinitionParameterList(
            ResolveParser.DefinitionParameterListContext ctx) {
        super.exitDefinitionParameterList(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterAffectsClause(ResolveParser.AffectsClauseContext ctx) {
        super.enterAffectsClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitAffectsClause(ResolveParser.AffectsClauseContext ctx) {
        super.exitAffectsClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterRequiresClause(ResolveParser.RequiresClauseContext ctx) {
        super.enterRequiresClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitRequiresClause(ResolveParser.RequiresClauseContext ctx) {
        super.exitRequiresClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterEnsuresClause(ResolveParser.EnsuresClauseContext ctx) {
        super.enterEnsuresClause(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitEnsuresClause(ResolveParser.EnsuresClauseContext ctx) {
        super.exitEnsuresClause(ctx);
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
    // Mathematical expressions
    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math type expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Math type expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathTypeExp(ResolveParser.MathTypeExpContext ctx) {
    //myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

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
    //myNodes.put(ctx, myNodes.removeFrom(ctx.getChild(0)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Math iterated expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathIteratedExp(ResolveParser.MathIteratedExpContext ctx) {
    /*IterativeExp.Operator operator;
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
            varDecl, whereExp, bodyExp));*/
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Math quantified expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathQuantifiedExp(ResolveParser.MathQuantifiedExpContext ctx) {
    /*ResolveConceptualElement newElement;
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

    myNodes.put(ctx, newElement);*/
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Math implies expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathImpliesExp(ResolveParser.MathImpliesExpContext ctx) {
    /*ResolveConceptualElement newElement;

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
    else {
        Exp leftExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(0));
        Exp rightExp = (Exp) myNodes.removeFrom(ctx.mathLogicalExp(1));

        newElement =
                new InfixExp(createLocation(ctx), leftExp, null,
                        createPosSymbol(ctx.op), rightExp);
    }

    myNodes.put(ctx, newElement);*/
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>This method stores the math expression representation
     * generated by its child rules.</p>
     *
     * @param ctx Math logical expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathLogicalExp(ResolveParser.MathLogicalExpContext ctx) {
    /*ResolveConceptualElement newElement;
    List<ResolveParser.MathRelationalExpContext> relationalExpContexts = ctx.mathRelationalExp();

    // relational expressions
    if (relationalExpContexts.size() == 1) {
        newElement = myNodes.removeFrom(ctx.mathRelationalExp(0));
    }
    // build logical expressions
    else {
        List<Exp> exps = new ArrayList<>();
        for (ResolveParser.MathRelationalExpContext context : relationalExpContexts) {
            exps.add((Exp) myNodes.removeFrom(context));
        }

        while (exps.size() != 1) {
            int lastIndex = exps.size() - 1;
            Exp leftExp = exps.remove(lastIndex - 1);
            Exp rightExp = exps.remove(lastIndex);
            exps.add(new InfixExp(leftExp.getLocation(), leftExp, null, createPosSymbol(ctx.op), rightExp));
        }

        newElement = exps.get(0);
    }

    myNodes.put(ctx, newElement);*/
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathRelationalExp(ResolveParser.MathRelationalExpContext ctx) {
        super.exitMathRelationalExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathInfixExp(ResolveParser.MathInfixExpContext ctx) {
        super.exitMathInfixExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathTypeAssertionExp(
            ResolveParser.MathTypeAssertionExpContext ctx) {
        super.exitMathTypeAssertionExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitMathFunctionTypeExp(
            ResolveParser.MathFunctionTypeExpContext ctx) {
        super.exitMathFunctionTypeExp(ctx);
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
                            .removeFrom(mathExps.remove(0)), qualifier,
                            createPosSymbol(ctx.op), (Exp) myNodes
                                    .removeFrom(mathExps.remove(1)));
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
        if (mathExps.size() > 1) {
            PosSymbol qualifier = null;
            if (ctx.qualifier != null) {
                qualifier = createPosSymbol(ctx.qualifier);
            }

            newElement =
                    new InfixExp(createLocation(ctx), (Exp) myNodes
                            .removeFrom(mathExps.remove(0)), qualifier,
                            createPosSymbol(ctx.op), (Exp) myNodes
                                    .removeFrom(mathExps.remove(1)));
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
        if (ctx.DOT() != null) {
            // dotted expressions
            List<ResolveParser.MathCleanFunctionExpContext> mathExps = ctx.mathCleanFunctionExp();
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
     * <p>This method stores the math variable expression representation.</p>
     *
     * @param ctx Math variable expression node in ANTLR4 AST.
     */
    @Override
    public void exitMathVarExp(ResolveParser.MathVarExpContext ctx) {
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
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgPrimaryExp(ResolveParser.ProgPrimaryExpContext ctx) {
        super.enterProgPrimaryExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgPrimaryExp(ResolveParser.ProgPrimaryExpContext ctx) {
        super.exitProgPrimaryExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgApplicationExp(
            ResolveParser.ProgApplicationExpContext ctx) {
        super.enterProgApplicationExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgApplicationExp(
            ResolveParser.ProgApplicationExpContext ctx) {
        super.exitProgApplicationExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgNestedExp(ResolveParser.ProgNestedExpContext ctx) {
        super.enterProgNestedExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgNestedExp(ResolveParser.ProgNestedExpContext ctx) {
        super.exitProgNestedExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgPrimary(ResolveParser.ProgPrimaryContext ctx) {
        super.enterProgPrimary(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgPrimary(ResolveParser.ProgPrimaryContext ctx) {
        super.exitProgPrimary(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgVariableExp(ResolveParser.ProgVariableExpContext ctx) {
        super.enterProgVariableExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgVariableExp(ResolveParser.ProgVariableExpContext ctx) {
        super.exitProgVariableExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgDotExp(ResolveParser.ProgDotExpContext ctx) {
        super.enterProgDotExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgDotExp(ResolveParser.ProgDotExpContext ctx) {
        super.exitProgDotExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgParamExp(ResolveParser.ProgParamExpContext ctx) {
        super.enterProgParamExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgParamExp(ResolveParser.ProgParamExpContext ctx) {
        super.exitProgParamExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgNamedExp(ResolveParser.ProgNamedExpContext ctx) {
        super.enterProgNamedExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgNamedExp(ResolveParser.ProgNamedExpContext ctx) {
        super.exitProgNamedExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgIntegerExp(ResolveParser.ProgIntegerExpContext ctx) {
        super.enterProgIntegerExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgIntegerExp(ResolveParser.ProgIntegerExpContext ctx) {
        super.exitProgIntegerExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgCharacterExp(ResolveParser.ProgCharacterExpContext ctx) {
        super.enterProgCharacterExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgCharacterExp(ResolveParser.ProgCharacterExpContext ctx) {
        super.exitProgCharacterExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void enterProgStringExp(ResolveParser.ProgStringExpContext ctx) {
        super.enterProgStringExp(ctx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>The default implementation does nothing.</p>
     *
     * @param ctx
     */
    @Override
    public void exitProgStringExp(ResolveParser.ProgStringExpContext ctx) {
        super.exitProgStringExp(ctx);
    }

    //    @Override
    //    public void exitPrecisItem(ResolveParser.PrecisItemContext ctx) {
    //        //this node at any given time has at most one child. if you're unsure,
    //        //go back to the grammar and check: is it  a rule that only refers to
    //        //other rules? The absence of these types of middle rules in the parse
    //        //tree would effectively result in a more traditional ast -- and is
    //        //consequently the sort of thing you're doing here by hand, but w/ your own objects
    //
    //        //In other words, you're going to be the thing below quite a bit, so understand it:
    //        myNodes.put(ctx, myNodes.get(ctx.getChild(0)));
    //    }
    //
    //
    //    // -----------------------------------------------------------
    //    // Mathematical theorems, corollaries, etc
    //    // -----------------------------------------------------------
    //
    //    @Override
    //    public void exitMathAssertionDecl(
    //            ResolveParser.MathAssertionDeclContext ctx) {
    //        /*MathAssertionDec theorem = new MathAssertionDec(
    //                createLocation(ctx.getStart()), createPosSymbol(
    //                ctx.name.getStart())); //Todo: Add the actual assertion once exprs are represented.
    //        myNodes.put(ctx, theorem);*/
    //    }

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

}