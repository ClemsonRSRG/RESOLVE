/**
 * TreeBuildingVisitor.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.ImportCollectionAST.ImportCollectionBuilder;
import edu.clemson.cs.r2jt.absynnew.ImportCollectionAST.ImportType;
import edu.clemson.cs.r2jt.absynnew.InitFinalAST.Type;
import edu.clemson.cs.r2jt.absynnew.BlockAST.BlockBuilder;
import edu.clemson.cs.r2jt.absynnew.decl.*;
import edu.clemson.cs.r2jt.absynnew.decl.MathDefinitionAST.DefinitionBuilder;
import edu.clemson.cs.r2jt.absynnew.decl.OperationImplAST.OperationImplBuilder;
import edu.clemson.cs.r2jt.absynnew.decl.TypeModelAST.TypeDeclBuilder;
import edu.clemson.cs.r2jt.absynnew.decl.TypeRepresentationAST.RepresentationBuilder;
import edu.clemson.cs.r2jt.absynnew.expr.*;
import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST.MathSymbolExprBuilder;
import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST.DisplayStyle;
import edu.clemson.cs.r2jt.absynnew.stmt.*;
import edu.clemson.cs.r2jt.misc.SrcErrorException;
import edu.clemson.cs.r2jt.parsing.ResolveBaseListener;
import edu.clemson.cs.r2jt.parsing.ResolveParser;
import edu.clemson.cs.r2jt.misc.Utils.Builder;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Constructs an ast representation of RESOLVE sourcecode from the
 * concrete syntax tree produced by <tt>Antlr v4.x</tt>.</p>
 *
 * <p>The ast is built over the course of a pre-post traversal of the concrete
 * syntax tree. Automatically generated <tt>Antlr v4.x</tt> nodes are annotated
 * with their custom abstract-syntax counterparts via an instance of
 * {@link TreeDecorator}, resulting in a tree with a similar, but sparser
 * structure.</p>
 *
 * <p>Note that this class is parameterized by <code>T</code> to indicate that
 * it can handle building of specific subtrees when used in combination with
 * {@link ResolveParserFactory} and the {@link TreeUtil#createASTNodeFrom}
 * method.</p>
 *
 * <p>References to the completed, ast can be acquired through
 * calls to {@link #build()}.</p>
 */
public class TreeBuildingVisitor<T extends ResolveAST>
        extends
            ResolveBaseListener implements Builder<T> {

    private final TreeDecorator myDecorator = new TreeDecorator();

    /**
     * <p>Collects all imports. This builder must be global as it is added to by
     * various contexts encountered throughout the parsetree.</p>
     */
    private ImportCollectionAST.ImportCollectionBuilder myImportBuilder =
            new ImportCollectionBuilder();

    /**
     * <p>All the various signature styles a definition can take on requires
     * us to break from our usual post-oriented tree traversal decoration
     * pattern by declaring this particular builder global. Anyways, this should
     * be initialized in the appropriate top level definition rule, and reset to
     * <code>null</code> after being built and put into an annotation.</p>
     */
    private DefinitionBuilder myDefinitionBuilder = null;

    /**
     * <p>These flags enable a simple error check to ensure that the user has
     * provided at most a single module level initialization and finalization
     * section, respectively.</p>
     */
    private boolean mySeenModuleInitFlag, mySeenModuleFinalFlag = false;

    private final ParseTree myRootTree;

    public TreeBuildingVisitor(ParseTree tree) {
        myRootTree = tree;
    }

    @Override
    public T build() {
        ResolveAST result = get(ResolveAST.class, myRootTree);
        if (result == null) {
            throw new IllegalStateException("ast builder result-tree is null");
        }
        return (T) result;
    }

    @Override
    public void exitUsesList(@NotNull ResolveParser.UsesListContext ctx) {
        myImportBuilder =
                new ImportCollectionBuilder(ctx.getStart(), ctx.getStop())
                        .imports(ImportType.EXPLICIT, ctx.IDENTIFIER());
        put(ctx, myImportBuilder.build());
    }

    @Override
    public void exitModule(@NotNull ResolveParser.ModuleContext ctx) {
        put(ctx, get(ModuleAST.class, ctx.getChild(0)));
    }

    @Override
    public void enterConceptModule(
            @NotNull ResolveParser.ConceptModuleContext ctx) {
        sanityCheckBlockEnds(ctx.name, ctx.closename);
    }

    /*@Override
    public void exitConceptModule(
            @NotNull ResolveParser.ConceptModuleContext ctx) {

        SpecModuleBuilder builder =
                new SpecModuleBuilder(ctx.getStart(), ctx.getStop(), ctx.name)//
                        .requires(get(ExprAST.class, ctx.requiresClause()))//
                        .block(get(BlockAST.class, ctx.conceptItems()))//
                        .imports(myImportBuilder.build());

        if (ctx.moduleParameterList() != null) {
            builder.parameters(getAll(ModuleParameterAST.class, ctx
                    .moduleParameterList().moduleParameterDecl()));
        }
        put(ctx, builder.build());
    }*/

    @Override
    public void exitConceptItems(@NotNull ResolveParser.ConceptItemsContext ctx) {
        BlockBuilder blockBuilder =
                new BlockBuilder(ctx.getStart(), ctx.getStop())
                        .generalElements(getAll(ResolveAST.class, ctx
                                .conceptItem()));
        put(ctx, blockBuilder.build());
    }

    @Override
    public void exitConceptItem(@NotNull ResolveParser.ConceptItemContext ctx) {
        put(ctx, get(ResolveAST.class, ctx.getChild(0)));
    }

    @Override
    public void enterEnhancementModule(
            @NotNull ResolveParser.EnhancementModuleContext ctx) {
        sanityCheckBlockEnds(ctx.name, ctx.closename);
    }

    /*@Override
    public void exitEnhancementModule(
            @NotNull ResolveParser.EnhancementModuleContext ctx) {
        myImportBuilder.imports(ImportType.IMPLICIT, ctx.concept);
        SpecModuleBuilder builder =
                new SpecModuleBuilder(ctx.getStart(), ctx.getStop(), ctx.name)//
                        .requires(get(ExprAST.class, ctx.requiresClause()))//
                        .block(get(BlockAST.class, ctx.enhancementItems()))//
                        .concept(ctx.concept)//
                        .imports(myImportBuilder.build());

        if (ctx.moduleParameterList() != null) {
            builder.parameters(getAll(ModuleParameterAST.class, ctx
                    .moduleParameterList().moduleParameterDecl()));
        }
        put(ctx, builder.build());
    }*/

    @Override
    public void exitEnhancementItems(
            @NotNull ResolveParser.EnhancementItemsContext ctx) {
        BlockBuilder blockBuilder =
                new BlockBuilder(ctx.getStart(), ctx.getStop())
                        .generalElements(getAll(ResolveAST.class, ctx
                                .enhancementItem()));
        put(ctx, blockBuilder.build());
    }

    @Override
    public void exitEnhancementItem(
            @NotNull ResolveParser.EnhancementItemContext ctx) {
        put(ctx, get(ResolveAST.class, ctx.getChild(0)));
    }

    @Override
    public void enterConceptImplModule(
            @NotNull ResolveParser.ConceptImplModuleContext ctx) {
        sanityCheckBlockEnds(ctx.name, ctx.closename);
    }

    @Override
    public void exitConceptImplModule(
            @NotNull ResolveParser.ConceptImplModuleContext ctx) {
        myImportBuilder.imports(ImportType.IMPLICIT, ctx.concept);
        /*       ImplModuleBuilder builder =
                       new ImplModuleBuilder(ctx.getStart(), ctx.getStop(), ctx.name)
                               .block(get(BlockAST.class, ctx.implItems())).imports(
                                       myImportBuilder.build()).concept(ctx.concept);
               put(ctx, builder.build());*/
    }

    @Override
    public void exitEnhancementImplModule(
            @NotNull ResolveParser.EnhancementImplModuleContext ctx) {
        myImportBuilder.imports(ImportType.IMPLICIT, ctx.concept).imports(
                ImportType.IMPLICIT, ctx.enhancement);
        /*&        ImplModuleBuilder builder =
         new ImplModuleBuilder(ctx.getStart(), ctx.getStop(), ctx.name)
         .block(get(BlockAST.class, ctx.implItems())).imports(
         myImportBuilder.build()).concept(ctx.concept);
         put(ctx, builder.build());*/
    }

    @Override
    public void exitImplItems(@NotNull ResolveParser.ImplItemsContext ctx) {
        BlockBuilder blockBuilder =
                new BlockBuilder(ctx.getStart(), ctx.getStop())
                        .generalElements(getAll(ResolveAST.class, ctx
                                .implItem()));
        put(ctx, blockBuilder.build());
    }

    @Override
    public void exitImplItem(@NotNull ResolveParser.ImplItemContext ctx) {
        put(ctx, get(ResolveAST.class, ctx.getChild(0)));
    }

    @Override
    public void enterPrecisModule(@NotNull ResolveParser.PrecisModuleContext ctx) {
        sanityCheckBlockEnds(ctx.name, ctx.closename);
    }

    @Override
    public void exitPrecisModule(@NotNull ResolveParser.PrecisModuleContext ctx) {
    /*       PrecisBuilder builder =
                   new PrecisBuilder(ctx.getStart(), ctx.getStop(), ctx.name)//
                           .block(get(BlockAST.class, ctx.precisItems()))//
                           .imports(myImportBuilder.build());*/

    //put(ctx, builder.build());
    }

    @Override
    public void exitPrecisItems(@NotNull ResolveParser.PrecisItemsContext ctx) {
        BlockBuilder blockBuilder =
                new BlockBuilder(ctx.getStart(), ctx.getStop())
                        .generalElements(getAll(ResolveAST.class, ctx
                                .precisItem()));
        put(ctx, blockBuilder.build());
    }

    @Override
    public void exitPrecisItem(@NotNull ResolveParser.PrecisItemContext ctx) {
        put(ctx, get(ResolveAST.class, ctx.getChild(0)));
    }

    @Override
    public void enterFacilityModule(
            @NotNull ResolveParser.FacilityModuleContext ctx) {
        sanityCheckBlockEnds(ctx.name, ctx.closename);
    }

    @Override
    public void exitFacilityModule(
            @NotNull ResolveParser.FacilityModuleContext ctx) {
    /*        ImplModuleBuilder builder =
     new ImplModuleBuilder(ctx.getStart(), ctx.getStop(), ctx.name)//
     .block(get(BlockAST.class, ctx.facilityItems()))//
     .imports(myImportBuilder.build());
     put(ctx, builder.build());*/
    }

    @Override
    public void exitFacilityItems(
            @NotNull ResolveParser.FacilityItemsContext ctx) {
        BlockBuilder blockBuilder =
                new BlockBuilder(ctx.getStart(), ctx.getStop())
                        .generalElements(getAll(ResolveAST.class, ctx
                                .facilityItem()));
        put(ctx, blockBuilder.build());
    }

    @Override
    public void exitFacilityItem(@NotNull ResolveParser.FacilityItemContext ctx) {
        put(ctx, get(ResolveAST.class, ctx.getChild(0)));
    }

    @Override
    public void exitOperationDecl(
            @NotNull ResolveParser.OperationDeclContext ctx) {

        OperationSigAST.OperationDeclBuilder builder =
                new OperationSigAST.OperationDeclBuilder(ctx) //
                        .type(get(NamedTypeAST.class, ctx.type())) //
                        .requires(get(ExprAST.class, ctx.requiresClause())) //
                        .ensures(get(ExprAST.class, ctx.ensuresClause())) //
                        .params(
                                getAll(ParameterAST.class, ctx
                                        .operationParameterList()
                                        .parameterDecl()));
        put(ctx, builder.build());
    }

    @Override
    public void enterOperationProcedureDecl(
            @NotNull ResolveParser.OperationProcedureDeclContext ctx) {
        sanityCheckBlockEnds(ctx.name, ctx.closename);
    }

    @Override
    public void exitOperationProcedureDecl(
            @NotNull ResolveParser.OperationProcedureDeclContext ctx) {

        OperationImplBuilder builder =
                new OperationImplBuilder(ctx.getStart(), ctx.getStop(),
                        ctx.name) //
                        .recursive(ctx.recursive != null) //
                        .parameters(
                                getAll(ParameterAST.class, ctx
                                        .operationParameterList()
                                        .parameterDecl()));

        for (ResolveParser.VariableDeclGroupContext grp : ctx
                .variableDeclGroup()) {
            builder.localVariables(getAll(VariableAST.class, grp.IDENTIFIER()));
        }
        put(ctx, builder.build());
    }

    @Override
    public void enterProcedureDecl(
            @NotNull ResolveParser.ProcedureDeclContext ctx) {
        sanityCheckBlockEnds(ctx.name, ctx.closename);
    }

    @Override
    public void exitProcedureDecl(
            @NotNull ResolveParser.ProcedureDeclContext ctx) {
        OperationImplBuilder builder =
                new OperationImplBuilder(ctx.getStart(), ctx.getStop(),
                        ctx.name) //
                        .returnType(get(NamedTypeAST.class, ctx.type())) //
                        .recursive(ctx.recursive != null) //
                        .implementsContract(true) //
                        .statements(getAll(StmtAST.class, ctx.stmt())) //
                        .parameters(
                                getAll(ParameterAST.class, ctx
                                        .operationParameterList()
                                        .parameterDecl()));

        //Variable lists are a pain in the ass. It'd be easier if we just kept
        //them list-ifed.
        for (ResolveParser.VariableDeclGroupContext grp : ctx
                .variableDeclGroup()) {
            builder.localVariables(getAll(VariableAST.class, grp.IDENTIFIER()));
        }
        put(ctx, builder.build());
    }

    //Todo: Make the *declGroup methods return a BlockAST then filter by ctx
    @Override
    public void exitVariableDeclGroup(
            @NotNull ResolveParser.VariableDeclGroupContext ctx) {
        NamedTypeAST groupType = get(NamedTypeAST.class, ctx.type());

        for (TerminalNode t : ctx.IDENTIFIER()) {
            put(t, new VariableAST(ctx.getStart(), ctx.getStop(),
                    t.getSymbol(), groupType));
        }
    }

    @Override
    public void exitFacilityDecl(@NotNull ResolveParser.FacilityDeclContext ctx) {

        List<ModuleArgumentAST> specArgs =
                ctx.specArgs == null ? new ArrayList<ModuleArgumentAST>()
                        : getAll(ModuleArgumentAST.class, ctx.specArgs
                                .moduleArgument());

        List<ModuleArgumentAST> bodyArgs =
                ctx.implArgs == null ? new ArrayList<ModuleArgumentAST>()
                        : getAll(ModuleArgumentAST.class, ctx.implArgs
                                .moduleArgument());

        List<EnhancementPairAST> enhancements =
                getAll(EnhancementPairAST.class, ctx.enhancementPairDecl());
        myImportBuilder.imports(ctx);

        put(ctx, new FacilityAST(ctx.getStart(), ctx.getStop(), ctx.name,
                ctx.concept, specArgs, ctx.impl, bodyArgs, enhancements));
    }

    @Override
    public void exitEnhancementPairDecl(
            @NotNull ResolveParser.EnhancementPairDeclContext ctx) {
        List<ModuleArgumentAST> specArgs =
                ctx.specArgs == null ? new ArrayList<ModuleArgumentAST>()
                        : getAll(ModuleArgumentAST.class, ctx.specArgs
                                .moduleArgument());

        List<ModuleArgumentAST> implArgs =
                ctx.implArgs == null ? new ArrayList<ModuleArgumentAST>()
                        : getAll(ModuleArgumentAST.class, ctx.implArgs
                                .moduleArgument());

        put(ctx, new EnhancementPairAST(ctx.getStart(), ctx.getStop(),
                ctx.spec, specArgs, ctx.impl, implArgs));
    }

    @Override
    public void exitModuleArgumentList(
            @NotNull ResolveParser.ModuleArgumentListContext ctx) {
        for (ResolveParser.ModuleArgumentContext arg : ctx.moduleArgument()) {
            put(arg, get(ModuleArgumentAST.class, arg));
        }
    }

    @Override
    public void exitModuleArgument(
            @NotNull ResolveParser.ModuleArgumentContext ctx) {
        put(ctx, new ModuleArgumentAST(get(ProgExprAST.class, ctx.progExp())));
    }

    @Override
    public void exitTypeModelDecl(
            @NotNull ResolveParser.TypeModelDeclContext ctx) {
        TypeDeclBuilder builder =
                new TypeDeclBuilder(ctx.getStart(), ctx.getStop(), ctx.name,
                        ctx.exemplar)//
                        .model(get(MathTypeAST.class, ctx.mathTypeExp()))//
                        .init(get(InitFinalAST.class, ctx.typeModelInit()))//
                        .constraint(get(ExprAST.class, ctx.constraintClause()))//
                        .finalize(get(InitFinalAST.class, ctx.typeModelFinal()));

        put(ctx, builder.build());
    }

    @Override
    public void exitTypeRepresentationDecl(
            @NotNull ResolveParser.TypeRepresentationDeclContext ctx) {
        InitFinalAST initial =
                get(InitFinalAST.class, ctx.typeRepresentationInit());
        InitFinalAST finalize =
                get(InitFinalAST.class, ctx.typeRepresentationFinal());
        ExprAST correspondence = get(ExprAST.class, ctx.correspondenceClause());
        ParserRuleContext typeCtx =
                ctx.type() != null ? ctx.type() : ctx.record();

        RepresentationBuilder builder =
                new RepresentationBuilder(ctx.getStart(), ctx.getStop(),
                        ctx.name)//
                        .representation(get(TypeAST.class, typeCtx))//
                        .convention(get(ExprAST.class, ctx.conventionClause()))//
                        .initialization(initial)//
                        .finalization(finalize)//
                        .correspondence(correspondence);

        put(ctx, builder.build());
    }

    @Override
    public void exitRecord(@NotNull ResolveParser.RecordContext ctx) {
        List<VariableAST> fields = new ArrayList<VariableAST>();

        for (ResolveParser.RecordVariableDeclGroupContext grp : ctx
                .recordVariableDeclGroup()) {
            NamedTypeAST grpType = get(NamedTypeAST.class, grp.type());

            for (TerminalNode t : grp.IDENTIFIER()) {
                fields.add(new VariableAST(grp.getStart(), grp.getStop(), t
                        .getSymbol(), grpType));
            }
        }
        put(ctx, new RecordTypeAST(ctx.getStart(), ctx.getStop(), fields));
    }

    @Override
    public void exitMathTypeTheoremDecl(
            @NotNull ResolveParser.MathTypeTheoremDeclContext ctx) {
        List<MathVariableAST> universals = new ArrayList<MathVariableAST>();

        for (ResolveParser.MathVariableDeclGroupContext grp : ctx
                .mathVariableDeclGroup()) {
            universals.addAll(getAll(MathVariableAST.class, grp.IDENTIFIER()));
        }
        MathTypeTheoremAST theorem =
                new MathTypeTheoremAST(ctx.getStart(), ctx.getStop(), ctx.name,
                        universals, get(ExprAST.class, ctx.mathExp()));
        put(ctx, theorem);
    }

    @Override
    public void exitMathVariableDecl(
            @NotNull ResolveParser.MathVariableDeclContext ctx) {
        put(ctx, new MathVariableAST(ctx.getStart(), ctx.getStop(), ctx
                .IDENTIFIER().getSymbol(), get(MathTypeAST.class, ctx
                .mathTypeExp())));
    }

    @Override
    public void exitMathVariableDeclGroup(
            @NotNull ResolveParser.MathVariableDeclGroupContext ctx) {
        MathTypeAST groupType = get(MathTypeAST.class, ctx.mathTypeExp());

        for (TerminalNode t : ctx.IDENTIFIER()) {
            put(t, new MathVariableAST(ctx.getStart(), ctx.getStop(), t
                    .getSymbol(), groupType));
        }
    }

    @Override
    public void exitModuleParameterDecl(
            @NotNull ResolveParser.ModuleParameterDeclContext ctx) {
        put(ctx, new ModuleParameterAST(get(DeclAST.class, ctx.getChild(0))));
    }

    @Override
    public void exitTypeParameterDecl(
            @NotNull ResolveParser.TypeParameterDeclContext ctx) {
        put(ctx, new TypeParameterAST(ctx.getStart(), ctx.getStop(), ctx.name));
    }

    @Override
    public void exitParameterDecl(
            @NotNull ResolveParser.ParameterDeclContext ctx) {
        NamedTypeAST groupType = get(NamedTypeAST.class, ctx.type());
        ProgramParameterEntry.ParameterMode mode =
                ProgramParameterEntry.getModeMapping().get(
                        ctx.parameterMode().getText());
        put(ctx, new ParameterAST(ctx.getStart(), ctx.getStop(), ctx.name,
                groupType, mode));
    }

    //Todo: Figure out if we want to throw this SrcErrorException in the
    //constructor of ModuleAST or not (filter the blockAST by type). hmmm.
    @Override
    public void exitModuleSpecInit(
            @NotNull ResolveParser.ModuleSpecInitContext ctx) {
        sanityCheckInitFinal(ctx);
        InitFinalAST moduleInit =
                new InitFinalAST(ctx.getStart(), ctx.getStop(), get(
                        ExprAST.class, ctx.requiresClause()), get(
                        ExprAST.class, ctx.ensuresClause()), Type.MODULE_INIT);

        mySeenModuleInitFlag = true;
        put(ctx, moduleInit);
    }

    @Override
    public void exitModuleSpecFinal(
            @NotNull ResolveParser.ModuleSpecFinalContext ctx) {
        sanityCheckInitFinal(ctx);
        InitFinalAST moduleFinal =
                new InitFinalAST(ctx.getStart(), ctx.getStop(), get(
                        ExprAST.class, ctx.requiresClause()), get(
                        ExprAST.class, ctx.ensuresClause()), Type.MODULE_FINAL);

        mySeenModuleFinalFlag = true;
        put(ctx, moduleFinal);
    }

    @Override
    public void exitTypeModelInit(
            @NotNull ResolveParser.TypeModelInitContext ctx) {
        InitFinalAST initialization =
                new InitFinalAST(ctx.getStart(), ctx.getStop(), get(
                        ExprAST.class, ctx.requiresClause()), get(
                        ExprAST.class, ctx.ensuresClause()), Type.TYPE_INIT);

        put(ctx, initialization);
    }

    @Override
    public void exitTypeModelFinal(
            @NotNull ResolveParser.TypeModelFinalContext ctx) {
        InitFinalAST finalization =
                new InitFinalAST(ctx.getStart(), ctx.getStop(), get(
                        ExprAST.class, ctx.requiresClause()), get(
                        ExprAST.class, ctx.ensuresClause()), Type.TYPE_FINAL);

        put(ctx, finalization);
    }

    @Override
    public void exitModuleFacilityInit(
            @NotNull ResolveParser.ModuleFacilityInitContext ctx) {
        put(ctx, buildImplModuleInitFinal(ctx, ctx.variableDeclGroup(), ctx
                .requiresClause(), ctx.ensuresClause(), Type.MODULE_INIT));
        mySeenModuleInitFlag = true;
    }

    @Override
    public void exitModuleFacilityFinal(
            @NotNull ResolveParser.ModuleFacilityFinalContext ctx) {
        put(ctx, buildImplModuleInitFinal(ctx, ctx.variableDeclGroup(), ctx
                .requiresClause(), ctx.ensuresClause(), Type.MODULE_FINAL));
        mySeenModuleInitFlag = true;
    }

    @Override
    public void exitModuleImplInit(
            @NotNull ResolveParser.ModuleImplInitContext ctx) {
        put(ctx, buildImplModuleInitFinal(ctx, ctx.variableDeclGroup(),
                Type.MODULE_INIT));
        mySeenModuleInitFlag = true;
    }

    @Override
    public void exitModuleImplFinal(
            @NotNull ResolveParser.ModuleImplFinalContext ctx) {
        put(ctx, buildImplModuleInitFinal(ctx, ctx.variableDeclGroup(),
                Type.MODULE_FINAL));
        mySeenModuleFinalFlag = true;
    }

    private InitFinalAST buildImplModuleInitFinal(ParserRuleContext ctx,
            List<ResolveParser.VariableDeclGroupContext> v, Type t) {
        return buildImplModuleInitFinal(ctx, v, null, null, t);
    }

    private InitFinalAST buildImplModuleInitFinal(ParserRuleContext ctx,
            List<ResolveParser.VariableDeclGroupContext> v, ParseTree requires,
            ParseTree ensures, Type t) {
        sanityCheckInitFinal(ctx);
        List<VariableAST> variables = new ArrayList<VariableAST>();

        for (ResolveParser.VariableDeclGroupContext grp : v) {
            variables.addAll(getAll(VariableAST.class, grp.IDENTIFIER()));
        }
        return new InitFinalAST(ctx.getStart(), ctx.getStop(), get(
                ExprAST.class, requires), get(ExprAST.class, ensures),
                variables, new ArrayList<StmtAST>(), t);
    }

    /**
     * <p>Raises hell if the user provided more than a single module level
     * initialization or finalization section.</p>
     */
    private void sanityCheckInitFinal(ParserRuleContext ctx) {
        if (mySeenModuleInitFlag) {
            throw new SrcErrorException("only one module level initialization"
                    + " section per module is permitted", ctx.getStart());
        }
        if (mySeenModuleFinalFlag) {
            throw new SrcErrorException("only one module level finalization"
                    + " section per module is permitted", ctx.getStart());
        }
    }

    @Override
    public void exitStmt(@NotNull ResolveParser.StmtContext ctx) {
        put(ctx, get(StmtAST.class, ctx.getChild(0)));
    }

    @Override
    public void exitAssignStmt(@NotNull ResolveParser.AssignStmtContext ctx) {
        AssignAST assign =
                new AssignAST(ctx.getStart(), ctx.getStop(), get(
                        ProgExprAST.class, ctx.left), get(ProgExprAST.class,
                        ctx.right));
        put(ctx, assign);
    }

    @Override
    public void exitSwapStmt(@NotNull ResolveParser.SwapStmtContext ctx) {
        SwapAST swap =
                new SwapAST(ctx.getStart(), ctx.getStop(), get(
                        ProgExprAST.class, ctx.left), get(ProgExprAST.class,
                        ctx.right));
        put(ctx, swap);
    }

    @Override
    public void exitCallStmt(@NotNull ResolveParser.CallStmtContext ctx) {
        ProgOperationRefAST opRef =
                get(ProgOperationRefAST.class, ctx.progParamExp());
        put(ctx, new CallAST(opRef));
    }

    @Override
    public void exitIfStmt(@NotNull ResolveParser.IfStmtContext ctx) {
        ProgExprAST condition = get(ProgExprAST.class, ctx.progExp());
        List<StmtAST> ifBlock = getAll(StmtAST.class, ctx.stmt());
        List<StmtAST> elseBlock = new ArrayList<StmtAST>();
        if (ctx.elsePart() != null) {
            elseBlock = getAll(StmtAST.class, ctx.elsePart().stmt());
        }
        put(ctx, new IfAST(ctx.getStart(), ctx.getStop(), condition, ifBlock,
                elseBlock));
    }

    @Override
    public void exitWhileStmt(@NotNull ResolveParser.WhileStmtContext ctx) {
        ProgExprAST condition = get(ProgExprAST.class, ctx.progExp());

        ExprAST maintaining = get(ExprAST.class, ctx.maintainingClause());
        ExprAST decreasing = get(ExprAST.class, ctx.decreasingClause());
        List<ProgExprAST> changingVars = new ArrayList<ProgExprAST>();
        List<StmtAST> stmts = getAll(StmtAST.class, ctx.stmt());

        if (ctx.changingClause() != null) {
            changingVars =
                    getAll(ProgExprAST.class, ctx.changingClause()
                            .progVariableExp());
        }
        put(ctx, new WhileAST(ctx.getStart(), ctx.getStop(), condition,
                changingVars, maintaining, decreasing, stmts));
    }

    @Override
    public void exitMathTheoremDecl(
            @NotNull ResolveParser.MathTheoremDeclContext ctx) {
        put(ctx, new MathTheoremAST(ctx.getStart(), ctx.getStop(), ctx.name,
                get(ExprAST.class, ctx.mathAssertionExp())));
    }

    @Override
    public void enterMathDefinitionDecl(
            @NotNull ResolveParser.MathDefinitionDeclContext ctx) {
        myDefinitionBuilder =
                new MathDefinitionAST.DefinitionBuilder(ctx.getStart(), ctx
                        .getStop());
    }

    @Override
    public void exitMathDefinitionDecl(
            @NotNull ResolveParser.MathDefinitionDeclContext ctx) {
        put(ctx, get(MathDefinitionAST.class, ctx.getChild(0)));
        myDefinitionBuilder = null;
    }

    @Override
    public void exitMathStandardDefinitionDecl(
            @NotNull ResolveParser.MathStandardDefinitionDeclContext ctx) {
        myDefinitionBuilder//
                .standardBody(get(ExprAST.class, ctx.mathAssertionExp())).type(
                        MathDefinitionAST.DefinitionType.STANDARD);

        //Even though we're dealing with a global builder, we still decorate
        //this with what we've built so far in case someone requests it.
        //Also since there is no direct RESOLVE ast equivalent of a definition's
        //signature, for completeness, just stick the finished definition into
        //the signature rule slots too.
        MathDefinitionAST finished = myDefinitionBuilder.build();
        put(ctx, finished);
        put(ctx.definitionSignature(), finished); //set top level sig rule
        put(ctx.definitionSignature().getChild(0), finished); //set particular
    }

    @Override
    public void exitMathInductiveDefinitionDecl(
            @NotNull ResolveParser.MathInductiveDefinitionDeclContext ctx) {
        myDefinitionBuilder//
                .inductiveBaseCase(get(ExprAST.class, ctx.mathAssertionExp(0)))//
                .inductiveHypo(get(ExprAST.class, ctx.mathAssertionExp(1)))//
                .type(MathDefinitionAST.DefinitionType.INDUCTIVE);

        MathDefinitionAST finished = myDefinitionBuilder.build();
        put(ctx, finished);
        put(ctx.inductiveDefinitionSignature(), finished);
        put(ctx.inductiveDefinitionSignature().getChild(0), finished);
    }

    @Override
    public void exitInductivePrefixSignature(
            @NotNull ResolveParser.InductivePrefixSignatureContext ctx) {
        myDefinitionBuilder//
                .name(ctx.prefixOp().getStart())//
                .returnType(get(MathTypeAST.class, ctx.mathTypeExp()))//
                .parameters(
                        buildInductiveParameter(ctx.IDENTIFIER().getSymbol(),
                                ctx.mathVariableDecl().mathTypeExp()));
    }

    @Override
    public void exitInductiveInfixSignature(
            @NotNull ResolveParser.InductiveInfixSignatureContext ctx) {
        myDefinitionBuilder//
                .name(ctx.infixOp().getStart())//
                .returnType(get(MathTypeAST.class, ctx.mathTypeExp()))//
                .parameters(
                        buildInductiveParameter(ctx.IDENTIFIER().getSymbol(),
                                ctx.mathVariableDecl(0).mathTypeExp()),
                        get(MathVariableAST.class, ctx.mathVariableDecl(1)));
    }

    private MathVariableAST buildInductiveParameter(Token name,
            ResolveParser.MathTypeExpContext t) {
        MathTypeAST type = get(MathTypeAST.class, t);
        return new MathVariableAST(null, null, name, type);
    }

    @Override
    public void exitStandardPrefixSignature(
            @NotNull ResolveParser.StandardPrefixSignatureContext ctx) {
        myDefinitionBuilder//
                .name(ctx.prefixOp().getStart())//
                .returnType(get(MathTypeAST.class, ctx.mathTypeExp()));
        //We've already set the annotation via the top level rule.
        //see exitMathStandardDefinitionDecl.
    }

    @Override
    public void exitStandardOutfixSignature(
            @NotNull ResolveParser.StandardOutfixSignatureContext ctx) {
        myDefinitionBuilder//
                .name(new ResolveToken(ctx.lOp + "..." + ctx.rOp))//
                .returnType(get(MathTypeAST.class, ctx.mathTypeExp()))//
                .parameters(get(MathVariableAST.class, ctx.mathVariableDecl()));
    }

    @Override
    public void exitStandardInfixSignature(
            @NotNull ResolveParser.StandardInfixSignatureContext ctx) {
        myDefinitionBuilder//
                .name(ctx.infixOp().getStart())//
                .returnType(get(MathTypeAST.class, ctx.mathTypeExp()))//
                .parameters(
                        getAll(MathVariableAST.class, ctx.mathVariableDecl()));
        //We've already set the annotation via the top level rule.
        //see exitMathStandardDefinitionDecl.
    }

    @Override
    public void exitInductiveParameterList(
            @NotNull ResolveParser.InductiveParameterListContext ctx) {
        for (ResolveParser.MathVariableDeclGroupContext grp : ctx
                .mathVariableDeclGroup()) {
            myDefinitionBuilder.parameters(getAll(MathVariableAST.class, grp
                    .IDENTIFIER()));
        }
    }

    @Override
    public void exitDefinitionParameterList(
            @NotNull ResolveParser.DefinitionParameterListContext ctx) {
        for (ResolveParser.MathVariableDeclGroupContext grp : ctx
                .mathVariableDeclGroup()) {
            myDefinitionBuilder.parameters(getAll(MathVariableAST.class, grp
                    .IDENTIFIER()));
        }
    }

    @Override
    public void exitType(@NotNull ResolveParser.TypeContext ctx) {
        put(ctx, new NamedTypeAST(ctx));
    }

    @Override
    public void exitProgApplicationExp(
            @NotNull ResolveParser.ProgApplicationExpContext ctx) {
    /*      Token name = TreeUtil.getTemplateOperationNameFor(ctx.op);
          //Unary minus unfornately needs special casing (or else we'll call it
          //negate).
          if (ctx.progExp().size() == 1 && ctx.op.getText().equals("-")) {
              name = new ResolveToken("Negate");
          }
          ProgOperationRefAST call =
                  new ProgOperationRefAST(ctx.getStart(), ctx.getStop(), null,
                          TreeUtil.getTemplateOperationNameFor(ctx.op), getAll(
                                  ProgExprAST.class, ctx.progExp()));
          put(ctx, call);*/
    }

    @Override
    public void exitMathTypeAssertExp(
            @NotNull ResolveParser.MathTypeAssertExpContext ctx) {
        ExprAST lhs = get(ExprAST.class, ctx.mathExp(0));
        ExprAST rhs = get(ExprAST.class, ctx.mathExp(1));

        MathTypeAssertionAST typeAssertion =
                new MathTypeAssertionAST(ctx.getStart(), ctx.getStop(), lhs,
                        new MathTypeAST(rhs));
        put(ctx, typeAssertion);
    }

    @Override
    public void exitProgNestedExp(
            @NotNull ResolveParser.ProgNestedExpContext ctx) {
        put(ctx, get(ProgExprAST.class, ctx.progExp()));
    }

    @Override
    public void exitProgPrimaryExp(
            @NotNull ResolveParser.ProgPrimaryExpContext ctx) {
        put(ctx, get(ProgExprAST.class, ctx.getChild(0)));
    }

    @Override
    public void exitProgPrimary(@NotNull ResolveParser.ProgPrimaryContext ctx) {
        put(ctx, get(ProgExprAST.class, ctx.getChild(0)));
    }

    @Override
    public void exitProgVariableExp(
            @NotNull ResolveParser.ProgVariableExpContext ctx) {
        put(ctx, get(ProgExprAST.class, ctx.getChild(0)));
    }

    @Override
    public void exitProgParamExp(@NotNull ResolveParser.ProgParamExpContext ctx) {
        ProgOperationRefAST param =
                new ProgOperationRefAST(ctx.getStart(), ctx.getStop(),
                        ctx.qualifier, ctx.name, getAll(ProgExprAST.class, ctx
                                .progExp()));
        put(ctx, param);
    }

    @Override
    public void exitProgDotExp(@NotNull ResolveParser.ProgDotExpContext ctx) {
        put(ctx, new ProgNamedSegmentsAST(ctx.getStart(), ctx.getStop(),
                getAll(ProgNameRefAST.class, ctx.progNamedExp())));
    }

    @Override
    public void exitProgNamedExp(@NotNull ResolveParser.ProgNamedExpContext ctx) {
        put(ctx, new ProgNameRefAST(ctx.getStart(), ctx.getStop(),
                ctx.qualifier, ctx.name));
    }

    @Override
    public void exitProgIntegerExp(
            @NotNull ResolveParser.ProgIntegerExpContext ctx) {
        put(ctx, new ProgLiteralRefAST.ProgIntegerRefAST(ctx.getStart(), ctx
                .getStop(), Integer.valueOf(ctx.INTEGER_LITERAL().getText())));
    }

    @Override
    public void exitProgStringExp(
            @NotNull ResolveParser.ProgStringExpContext ctx) {
        put(ctx, new ProgLiteralRefAST.ProgStringRefAST(ctx.getStart(), ctx
                .getStop(), String.valueOf(ctx.STRING_LITERAL().getText())));
    }

    @Override
    public void exitMathTypeExp(@NotNull ResolveParser.MathTypeExpContext ctx) {
        put(ctx, new MathTypeAST(get(ExprAST.class, ctx.mathExp())));
    }

    @Override
    public void exitRequiresClause(
            @NotNull ResolveParser.RequiresClauseContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathAssertionExp()));
    }

    @Override
    public void exitEnsuresClause(
            @NotNull ResolveParser.EnsuresClauseContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathAssertionExp()));
    }

    @Override
    public void exitConstraintClause(
            @NotNull ResolveParser.ConstraintClauseContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathAssertionExp()));
    }

    @Override
    public void exitConventionClause(
            @NotNull ResolveParser.ConventionClauseContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathAssertionExp()));
    }

    @Override
    public void exitCorrespondenceClause(
            @NotNull ResolveParser.CorrespondenceClauseContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathAssertionExp()));
    }

    @Override
    public void exitMaintainingClause(
            @NotNull ResolveParser.MaintainingClauseContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathAssertionExp()));
    }

    @Override
    public void exitDecreasingClause(
            @NotNull ResolveParser.DecreasingClauseContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathAssertionExp()));
    }

    @Override
    public void exitMathAssertionExp(
            @NotNull ResolveParser.MathAssertionExpContext ctx) {
        put(ctx, get(ExprAST.class, ctx.getChild(0)));
    }

    @Override
    public void exitMathPrimeExp(@NotNull ResolveParser.MathPrimeExpContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathPrimaryExp()));
    }

    @Override
    public void exitMathNestedExp(
            @NotNull ResolveParser.MathNestedExpContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathAssertionExp()));
    }

    @Override
    public void exitMathPrimaryExp(
            @NotNull ResolveParser.MathPrimaryExpContext ctx) {
        put(ctx, get(ExprAST.class, ctx.getChild(0)));
    }

    @Override
    public void exitMathDotExp(@NotNull ResolveParser.MathDotExpContext ctx) {
        MathSegmentsAST dots =
                new MathSegmentsAST(ctx.getStart(), ctx.getStop(), getAll(
                        MathSymbolAST.class, ctx.mathFunctionApplicationExp()));
        put(ctx, dots);
    }

    @Override
    public void exitMathBooleanExp(
            @NotNull ResolveParser.MathBooleanExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.BOOLEAN_LITERAL(), ctx).literal(
                true).build());
    }

    @Override
    public void exitMathIntegerExp(
            @NotNull ResolveParser.MathIntegerExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.INTEGER_LITERAL(), ctx).literal(
                true).build());
    }

    @Override
    public void exitMathQuantifiedExp(
            @NotNull ResolveParser.MathQuantifiedExpContext ctx) {
        ExprAST where = get(ExprAST.class, ctx.whereClause());
        ExprAST assertion = get(ExprAST.class, ctx.mathAssertionExp());

        List<MathVariableAST> quantifiedVariables =
                getAll(MathVariableAST.class, ctx.mathVariableDeclGroup()
                        .IDENTIFIER());

        MathQuantifiedAST quantExpr =
                new MathQuantifiedAST(ctx.getStart(), ctx.getStop(),
                        SymbolTableEntry.Quantification.UNIVERSAL,
                        quantifiedVariables, where, assertion);

        put(ctx, quantExpr);
    }

    @Override
    public void exitMathFunctionApplicationExp(
            @NotNull ResolveParser.MathFunctionApplicationExpContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathCleanFunctionExp()));
    }

    @Override
    public void exitMathFunctionExp(
            @NotNull ResolveParser.MathFunctionExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.name, ctx, ctx.mathExp())
                .incoming(ctx.getParent().getStart().getText().equals("#"))
                .build());
    }

    @Override
    public void exitMathSetCollectionExp(
            @NotNull ResolveParser.MathSetCollectionExpContext ctx) {
        put(ctx, new MathSetAST(ctx.getStart(), ctx.getStop(), getAll(
                ExprAST.class, ctx.mathExp())));
    }

    @Override
    public void exitMathSetBuilderExp(
            @NotNull ResolveParser.MathSetBuilderExpContext ctx) {
        throw new UnsupportedOperationException("set builder notation not yet "
                + "supported by the compiler.");
    }

    @Override
    public void exitMathLambdaExp(
            @NotNull ResolveParser.MathLambdaExpContext ctx) {
        List<MathVariableAST> parameters = new ArrayList<MathVariableAST>();

        for (ResolveParser.MathVariableDeclGroupContext grp : ctx
                .mathVariableDeclGroup()) {
            parameters.addAll(getAll(MathVariableAST.class, grp.IDENTIFIER()));
        }
        put(ctx, new MathLambdaAST(ctx.getStart(), ctx.getStop(), parameters,
                get(ExprAST.class, ctx.mathAssertionExp())));
    }

    @Override
    public void exitMathVariableExp(
            @NotNull ResolveParser.MathVariableExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.name, ctx).incoming(
                ctx.getParent().getStart().getText().equals("#")).build());
    }

    @Override
    public void exitMathInfixExp(@NotNull ResolveParser.MathInfixExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.op, ctx, ctx.mathExp(0),
                ctx.mathExp(1)).style(DisplayStyle.INFIX).build());
    }

    @Override
    public void exitMathUnaryExp(@NotNull ResolveParser.MathUnaryExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.op, ctx, ctx.mathExp()).build());
    }

    @Override
    public void exitMathOutfixExp(
            @NotNull ResolveParser.MathOutfixExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.lop, ctx.rop, ctx, ctx.mathExp())
                .style(DisplayStyle.OUTFIX).build());
    }

    private MathSymbolExprBuilder buildFunctionApplication(Token lname,
            Token rname, ParserRuleContext t,
            ResolveParser.MathExpContext... args) {
        return buildFunctionApplication(lname, rname, t, Arrays.asList(args));
    }

    private MathSymbolExprBuilder buildFunctionApplication(Token name,
            ParserRuleContext t, List<ResolveParser.MathExpContext> args) {
        return buildFunctionApplication(name, null, t, args);
    }

    private MathSymbolExprBuilder buildFunctionApplication(Token name,
            ParserRuleContext t, ResolveParser.MathExpContext... args) {
        return buildFunctionApplication(name, t, Arrays.asList(args));
    }

    private MathSymbolExprBuilder buildFunctionApplication(TerminalNode term,
            ParserRuleContext t) {
        return buildFunctionApplication(term.getSymbol(), t,
                new ArrayList<ResolveParser.MathExpContext>());
    }

    private MathSymbolExprBuilder buildFunctionApplication(Token lname,
            Token rname, ParserRuleContext t,
            List<ResolveParser.MathExpContext> args) {
        MathSymbolExprBuilder result =
                new MathSymbolExprBuilder(t, lname, rname).arguments(getAll(
                        ExprAST.class, args));
        return result;
    }

    @Override
    public void exitMathTupleExp(@NotNull ResolveParser.MathTupleExpContext ctx) {
        put(ctx, new MathTupleAST(ctx.getStart(), ctx.getStop(), getAll(
                ExprAST.class, ctx.mathExp())));
    }

    private void put(ParseTree parseTree, ResolveAST ast) {
        myDecorator.putProp(parseTree, ast);
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> the string text within
     * <code>topName</code> equals <code>endName</code></p>.
     *
     * @param topName The name at the top that introduces a block.
     *
     * @param endName The {@link ResolveToken} following the <tt>end</tt>
     *                portion of a named block.
     *
     * @throws SrcErrorException If the provided top and bottom names don't
     *      match.
     */
    private void sanityCheckBlockEnds(Token topName, Token endName) {
        if (!topName.equals(endName)) {
            throw new SrcErrorException("block end name " + endName + " != "
                    + topName, endName);
        }
    }

    /**
     * <p>Shortcut methods to ease interaction with <code>TreeDecorator</code>;
     * for example it's somewhat shorter to say <pre>get(x.class, t)</pre>
     * than <pre>myDecorator.getProp(x.class, t)</pre>.</p>
     *
     * @param type A class within the {@link ResolveAST} hierarchy indicating
     *             expected-type.
     * @param t    A {@link ParseTree} indicating which subtree to draw the
     *             annotation from.
     *
     * @param <E>  An ast type.
     * @return
     */
    protected <E extends ResolveAST> E get(Class<E> type, ParseTree t) {
        return myDecorator.getProp(type, t);
    }

    protected <E extends ResolveAST> List<E> getAll(Class<E> type,
            List<? extends ParseTree> t) {
        return myDecorator.collect(type, t);
    }

    protected static class TreeDecorator {

        private final ParseTreeProperty<ResolveAST> visitedCtxs =
                new ParseTreeProperty<ResolveAST>();

        public <T extends ResolveAST> List<T> collect(Class<T> type,
                List<? extends ParseTree> parseTrees) {
            List<T> result = new ArrayList<T>();
            for (ParseTree tree : parseTrees) {
                result.add(type.cast(visitedCtxs.get(tree)));
            }
            return result;
        }

        public void putProp(ParseTree parseTree, ResolveAST e) {
            visitedCtxs.put(parseTree, e);
        }

        public <T extends ResolveAST> T getProp(Class<T> type,
                ParseTree parseTree) {
            return type.cast(visitedCtxs.get(parseTree));
        }
    }
}
