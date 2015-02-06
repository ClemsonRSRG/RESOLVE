/**
 * ASTBuildingVisitor.java
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
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.ImportBlockAST.ImportCollectionBuilder;
import edu.clemson.cs.r2jt.absynnew.ImportBlockAST.ImportType;
import edu.clemson.cs.r2jt.absynnew.InitFinalAST.TypeFinalAST;
import edu.clemson.cs.r2jt.absynnew.InitFinalAST.TypeInitAST;
import edu.clemson.cs.r2jt.absynnew.ModuleAST.ConceptAST.ConceptBuilder;
import edu.clemson.cs.r2jt.absynnew.ModuleBlockAST.ModuleBlockBuilder;
import edu.clemson.cs.r2jt.absynnew.decl.*;
import edu.clemson.cs.r2jt.absynnew.decl.OperationImplAST.OperationImplBuilder;
import edu.clemson.cs.r2jt.absynnew.decl.TypeModelAST.TypeDeclBuilder;
import edu.clemson.cs.r2jt.absynnew.expr.*;
import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST.MathSymbolExprBuilder;
import edu.clemson.cs.r2jt.parsing.ResolveBaseListener;
import edu.clemson.cs.r2jt.parsing.ResolveParser;
import edu.clemson.cs.r2jt.utilities.Builder;
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
 * <p>Constructs an ast representation of <tt>RESOLVE</tt> sourcecode from the
 * concrete syntax tree produced by <tt>Antlr v4.x</tt>.</p>
 * 
 * <p>The ast is built over the course of a pre-post traversal of the concrete
 * syntax tree. Automatically generated <tt>Antlr v4.x</tt> nodes are annotated
 * with their custom abstract-syntax counterparts via an instance of
 * {@link TreeDecorator}, resulting in a tree with a similar, but sparser
 * structure.</p>
 *
 * <p>References to the completed, AST can be acquired through
 * calls to {@link #build()}.</p>
 */
public class ASTBuildingVisitor<T extends ResolveAST>
        extends
            ResolveBaseListener implements Builder<T> {

    private final TreeDecorator myDecorator = new TreeDecorator();

    /**
     * <p>Collects all imports. This builder must be global as it is added to by
     * various contexts encountered throughout the parsetree.</p>
     */
    private ImportBlockAST.ImportCollectionBuilder myImportBuilder =
            new ImportCollectionBuilder();

    private final ParseTree myRootTree;

    public ASTBuildingVisitor(ParseTree tree) {
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
    public void enterConceptModule(
            @NotNull ResolveParser.ConceptModuleContext ctx) {
        sanityCheckBlockEnds(ctx.name, ctx.closename);
    }

    @Override
    public void exitUsesList(@NotNull ResolveParser.UsesListContext ctx) {
        myImportBuilder =
                new ImportCollectionBuilder(ctx.getStart(), ctx.getStop())
                        .imports(ImportType.EXPLICIT, ctx.Identifier());
    }

    @Override
    public void exitModule(@NotNull ResolveParser.ModuleContext ctx) {
        put(ctx, get(ModuleAST.class, ctx.getChild(0)));
    }

    @Override
    public void exitConceptModule(
            @NotNull ResolveParser.ConceptModuleContext ctx) {

        ConceptBuilder builder =
                new ConceptBuilder(ctx.getStart(), ctx.getStop(), ctx.name)
                        .requires(get(ExprAST.class, ctx.requiresClause()))
                        .block(get(ModuleBlockAST.class, ctx.conceptItems()))
                        .imports(myImportBuilder.build());

        if (ctx.moduleParameterList() != null) {
            builder.parameters(getAll(ModuleParameterAST.class, ctx
                    .moduleParameterList().moduleParameterDecl()));
        }
        put(ctx, builder.build());
    }

    @Override
    public void exitConceptItems(@NotNull ResolveParser.ConceptItemsContext ctx) {
        ModuleBlockAST.ModuleBlockBuilder blockBuilder =
                new ModuleBlockBuilder(ctx.getStart(), ctx.getStop())
                        .generalElements(getAll(ResolveAST.class, ctx
                                .conceptItem()));
        put(ctx, blockBuilder.build());
    }

    @Override
    public void exitConceptItem(@NotNull ResolveParser.ConceptItemContext ctx) {
        put(ctx, get(ResolveAST.class, ctx.getChild(0)));
    }

    /*
     * @Override public void enterFacilityModule(
     * 
     * @NotNull ResolveParser.FacilityModuleContext ctx) {
     * sanityCheckBlockEnds(ctx.name, ctx.closename);
     * }
     * 
     * @Override public void exitFacilityModule(
     * 
     * @NotNull ResolveParser.FacilityModuleContext ctx) {
     * FacilityBuilder builder =
     * new FacilityBuilder(ctx.getStart(), ctx.getStop(), ctx.name)
     * .requires(get(ExprAST.class, ctx.requiresClause()))
     * .block(get(ModuleBlockAST.class, ctx.facilityItems()))
     * .imports(myImportBuilder.build());
     * 
     * myFinalBuilder = builder;
     * }
     * 
     * @Override public void exitFacilityItems(
     * 
     * @NotNull ResolveParser.FacilityItemsContext ctx) {
     * ModuleBlockBuilder blockBuilder =
     * new ModuleBlockBuilder(ctx.getStart(), ctx.getStop())
     * .generalElements(getAll(ResolveAST.class,
     * ctx.facilityItem()));
     * put(ctx, blockBuilder.build());
     * }
     * 
     * @Override public void exitFacilityItem(
     * 
     * @NotNull ResolveParser.FacilityItemContext ctx) {
     * put(ctx, get(ResolveAST.class, ctx.getChild(0)));
     * }
     */

    @Override
    public void exitOperationDecl(
            @NotNull ResolveParser.OperationDeclContext ctx) {

        OperationSigAST.OperationDeclBuilder builder =
                new OperationSigAST.OperationDeclBuilder(ctx).type(
                        get(NamedTypeAST.class, ctx.type())).requires(
                        get(ExprAST.class, ctx.requiresClause())).ensures(
                        get(ExprAST.class, ctx.ensuresClause())).params(
                        getAll(ParameterAST.class, ctx.operationParameterList()
                                .parameterDecl()));

        put(ctx, builder.build());
    }

    @Override
    public void enterFacilityOperationDecl(
            @NotNull ResolveParser.FacilityOperationDeclContext ctx) {
        sanityCheckBlockEnds(ctx.name, ctx.closename);
    }

    @Override
    public void exitFacilityOperationDecl(
            @NotNull ResolveParser.FacilityOperationDeclContext ctx) {

        OperationImplBuilder builder =
                new OperationImplBuilder(ctx.getStart(), ctx.getStop(),
                        ctx.name).recursive(ctx.recursive != null).parameters(
                        getAll(ParameterAST.class, ctx.operationParameterList()
                                .parameterDecl()));

        for (ResolveParser.VariableDeclGroupContext grp : ctx
                .variableDeclGroup()) {
            builder.localVariables(getAll(VariableAST.class, grp.Identifier()));
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
                        ctx.name).returnType(
                        get(NamedTypeAST.class, ctx.type())).recursive(
                        ctx.recursive != null).implementsContract(true)
                        .parameters(
                                getAll(ParameterAST.class, ctx
                                        .operationParameterList()
                                        .parameterDecl()));

        //Variable lists are a pain in the ass. It'd be easier if we just kept
        //them list-ifed.
        for (ResolveParser.VariableDeclGroupContext grp : ctx
                .variableDeclGroup()) {
            builder.localVariables(getAll(VariableAST.class, grp.Identifier()));
        }
        put(ctx, builder.build());
    }

    @Override
    public void exitVariableDeclGroup(
            @NotNull ResolveParser.VariableDeclGroupContext ctx) {
        NamedTypeAST groupType = get(NamedTypeAST.class, ctx.type());

        for (TerminalNode t : ctx.Identifier()) {
            put(t, new VariableAST(ctx.getStart(), ctx.getStop(),
                    t.getSymbol(), groupType));
        }
    }

    @Override
    public void exitTypeModelDecl(
            @NotNull ResolveParser.TypeModelDeclContext ctx) {

        TypeDeclBuilder builder =
                new TypeDeclBuilder(ctx).model(
                        get(MathTypeAST.class, ctx.mathTypeExp())).init(
                        get(TypeInitAST.class, ctx.specTypeInit())).finalize(
                        get(TypeFinalAST.class, ctx.specTypeFinal()))
                        .constraint(get(ExprAST.class, ctx.constraintClause()));

        put(ctx, builder.build());
    }

    /*
     * @Override public void exitFacilityDecl(
     * 
     * @NotNull ResolveParser.FacilityDeclContext ctx) {
     * 
     * myImportBuilder.imports(ImportType.IMPLICIT, ctx.concept).imports(
     * ctx.externally != null ? ImportType.EXTERNAL
     * : ImportType.IMPLICIT, ctx.impl);
     * 
     * FacilityDeclAST facility =
     * new FacilityDeclAST(ctx.getStart(), ctx.getStop(), ctx.name,
     * buildPairing(ctx), getAll(
     * FacilityDeclAST.PairedEnhancementAST.class,
     * ctx.pairedFacilityEnhancement()));
     * 
     * put(ctx, facility);
     * }
     * 
     * @Override public void exitPairedFacilityEnhancement(
     * 
     * @NotNull ResolveParser.PairedFacilityEnhancementContext ctx) {
     * 
     * myImportBuilder.imports(ImportType.IMPLICIT, ctx.name).imports(
     * ctx.externally != null ? ImportType.EXTERNAL
     * : ImportType.IMPLICIT, ctx.impl);
     * put(ctx, new FacilityDeclAST.PairedEnhancementAST(buildPairing(ctx)));
     * }
     * 
     * private final FacilityDeclAST.SpecBodyPairAST buildPairing(
     * ResolveParser.FacilityDeclContext ctx) {
     * ModuleParameterizationAST spec =
     * buildParameterization(ctx.concept, ctx,
     * ctx.moduleArgumentList(0).moduleArgument());
     * 
     * ModuleParameterizationAST body =
     * buildParameterization(ctx.impl, ctx, ctx.moduleArgumentList(1)
     * .moduleArgument());
     * return buildPairing(spec, body);
     * }
     * 
     * private final FacilityDeclAST.SpecBodyPairAST buildPairing(
     * ResolveParser.PairedFacilityEnhancementContext ctx) {
     * ModuleParameterizationAST spec =
     * buildParameterization(ctx.name, ctx, ctx.moduleArgumentList(0)
     * .moduleArgument());
     * 
     * ModuleParameterizationAST body =
     * buildParameterization(ctx.impl, ctx, ctx.moduleArgumentList(1)
     * .moduleArgument());
     * return buildPairing(spec, body);
     * }
     * 
     * private final FacilityDeclAST.SpecBodyPairAST buildPairing(
     * ModuleParameterizationAST spec, ModuleParameterizationAST body) {
     * return new FacilityDeclAST.SpecBodyPairAST(spec, body);
     * }
     * 
     * private final ModuleParameterizationAST buildParameterization(Token name,
     * ParserRuleContext ctx,
     * List<ResolveParser.ModuleArgumentContext> args) {
     * return new ModuleParameterizationAST(ctx.getStart(), ctx.getStop(),
     * name, getAll(FacilityDeclAST.ModuleArgAST.class, args));
     * }
     * 
     * @Override public void exitModuleArgument(
     * 
     * @NotNull ResolveParser.ModuleArgumentContext ctx) {
     * put(ctx, new ModuleArgAST(get(ProgExprAST.class, ctx.progExp())));
     * }
     */

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
        NamedTypeAST type = get(NamedTypeAST.class, ctx.type());
        ParameterAST param =
                new ParameterAST(ctx.getStart(), ctx.getStop(), ctx.name, type);
        put(ctx, param);
    }

    @Override
    public void exitSpecTypeInit(@NotNull ResolveParser.SpecTypeInitContext ctx) {
        TypeInitAST initialization =
                new TypeInitAST(ctx.getStart(), ctx.getStop(), get(
                        ExprAST.class, ctx.requiresClause()), get(
                        ExprAST.class, ctx.ensuresClause()));

        put(ctx, initialization);
    }

    @Override
    public void exitSpecTypeFinal(
            @NotNull ResolveParser.SpecTypeFinalContext ctx) {
        TypeFinalAST finalization =
                new TypeFinalAST(ctx.getStart(), ctx.getStop(), get(
                        ExprAST.class, ctx.requiresClause()), get(
                        ExprAST.class, ctx.ensuresClause()));

        put(ctx, finalization);
    }

    @Override
    public void exitType(@NotNull ResolveParser.TypeContext ctx) {
        put(ctx, new NamedTypeAST(ctx));
    }

    @Override
    public void exitProgApplicationExp(
            @NotNull ResolveParser.ProgApplicationExpContext ctx) {
        ProgOperationRefAST call =
                new ProgOperationRefAST(ctx.getStart(), ctx.getStop(), null,
                        ctx.op, getAll(ProgExprAST.class, ctx.progExp()));
        put(ctx, call);
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
    public void exitProgParamExp(@NotNull ResolveParser.ProgParamExpContext ctx) {
        ProgOperationRefAST param =
                new ProgOperationRefAST(ctx.getStart(), ctx.getStop(),
                        ctx.qualifier, ctx.name, getAll(ProgExprAST.class, ctx
                                .progExp()));
        put(ctx, param);
    }

    @Override
    public void exitProgRecordDotExp(
            @NotNull ResolveParser.ProgRecordDotExpContext ctx) {
        throw new UnsupportedOperationException("program record dot "
                + "expressions not yet supported by the compiler.");
    }

    @Override
    public void exitProgNamedExp(@NotNull ResolveParser.ProgNamedExpContext ctx) {
        put(ctx, get(ProgExprAST.class, ctx.progNamedVarExp()));
    }

    @Override
    public void exitProgNamedVarExp(
            @NotNull ResolveParser.ProgNamedVarExpContext ctx) {
        put(ctx, new ProgNameRefAST(ctx.getStart(), ctx.getStop(),
                ctx.qualifier, ctx.name));
    }

    @Override
    public void exitProgIntegerExp(
            @NotNull ResolveParser.ProgIntegerExpContext ctx) {
        put(ctx, new ProgLiteralRefAST.ProgIntegerRefAST(ctx.getStart(), ctx
                .getStop(), Integer.valueOf(ctx.IntegerLiteral().getText())));
    }

    @Override
    public void exitProgStringExp(
            @NotNull ResolveParser.ProgStringExpContext ctx) {
        put(ctx, new ProgLiteralRefAST.ProgStringRefAST(ctx.getStart(), ctx
                .getStop(), String.valueOf(ctx.StringLiteral().getText())));
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
    public void exitMathAssertionExp(
            @NotNull ResolveParser.MathAssertionExpContext ctx) {
        put(ctx, get(ExprAST.class, ctx.mathExp()));
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
    public void exitMathBooleanExp(
            @NotNull ResolveParser.MathBooleanExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.BooleanLiteral(), ctx).literal(
                true).build());
    }

    @Override
    public void exitMathIntegerExp(
            @NotNull ResolveParser.MathIntegerExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.IntegerLiteral(), ctx).literal(
                true).build());
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
    public void exitMathVariableExp(
            @NotNull ResolveParser.MathVariableExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.name, ctx).incoming(
                ctx.getParent().getStart().getText().equals("#")).build());
    }

    @Override
    public void exitMathInfixExp(@NotNull ResolveParser.MathInfixExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.op, ctx, ctx.mathExp(0),
                ctx.mathExp(1)).build());
    }

    @Override
    public void exitMathUnaryExp(@NotNull ResolveParser.MathUnaryExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.op, ctx, ctx.mathExp()).build());
    }

    @Override
    public void exitMathOutfixExp(
            @NotNull ResolveParser.MathOutfixExpContext ctx) {
        put(ctx, buildFunctionApplication(ctx.lop, ctx.rop, ctx, ctx.mathExp())
                .build());
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

    //Todo: SourceErrorException needs to change once this class becomes the
    //norm and the populator is made compliant with the new way of doing things.
    //simply comment out the source error exception below.
    private void sanityCheckBlockEnds(Token topName, Token bottomName) {
        if (!topName.equals(bottomName)) {
            throw new RuntimeException("block names do not match");
            //   throw new SourceErrorException("opening name '" + topName.getText()
            //           + "' doesn't match closing name.", bottomName);
        }
    }

    /**
     * <p>Shortcut methods to ease interaction with <code>TreeDecorator</code>;
     * for example it's somewhat shorter to say <pre>get(x.class, t)</pre>
     * than <pre>myDecorator.getProp(x.class, t)</pre>.</p>
     */
    private <T extends ResolveAST> T get(Class<T> type, ParseTree t) {
        return myDecorator.getProp(type, t);
    }

    private <T extends ResolveAST> List<T> getAll(Class<T> type,
            List<? extends ParseTree> t) {
        return myDecorator.collect(type, t);
    }

    public static class TreeDecorator {

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
