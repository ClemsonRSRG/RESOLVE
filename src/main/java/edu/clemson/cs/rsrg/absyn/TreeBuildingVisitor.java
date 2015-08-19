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
package edu.clemson.cs.rsrg.absyn;

import edu.clemson.cs.r2jt.misc.Utils;
import edu.clemson.cs.rsrg.absyn.*;
import edu.clemson.cs.rsrg.errorhandling.ErrorHandler;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.init.file.Utilities.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.clemson.cs.rsrg.parsing.ResolveParser;
import edu.clemson.cs.rsrg.parsing.ResolveParserBaseListener;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class TreeBuildingVisitor extends ResolveParserBaseListener {

    private final ParseTreeProperty<ResolveConceptualElement> myNodes =
            new ParseTreeProperty<>();

    private ModuleDec myFinalModule = null;
    private final ResolveFile myFile;

    public TreeBuildingVisitor(ResolveFile file) {
        myFile = file;
    }

    public ModuleDec getModule() {
        return myFinalModule;
    }

    @Override public void exitModule(ResolveParser.ModuleContext ctx) {
        myNodes.put(ctx, myNodes.get(ctx.getChild(0)));
        myFinalModule = (ModuleDec) myNodes.get(ctx.getChild(0));
    }

    @Override public void exitPrecisModule(
            ResolveParser.PrecisModuleContext ctx) {
        List<Dec> decls =
                Utils.collect(Dec.class, ctx.precisItems() != null ?
                        ctx.precisItems().precisItem() :
                        new ArrayList<ParseTree>(), myNodes);
        //lines 56-59 same as:
        //List<ResolveParser.PrecisItemContext> trees = ctx.precisItems() != null ?
        //        ctx.precisItems().precisItem() : new ArrayList<ResolveParser.PrecisItemContext>();
        //List<Dec> decls = new ArrayList<>();
        //for (ResolveParser.PrecisItemContext t : precisItems) {
        //    decls.add(((Dec)myNodes.get(t));
        //}
        List<UsesItem> uses =
                Utils.collect(UsesItem.class, ctx.usesList() != null ?
                        ctx.usesList().usesItem() :
                        new ArrayList<ParseTree>(), myNodes);
        PrecisModuleDec precis =
                new PrecisModuleDec(createLocation(ctx), createPosSymbol(ctx.name),
                        uses, decls);
        myNodes.put(ctx, precis);
    }

    @Override public void exitUsesItem(ResolveParser.UsesItemContext ctx) {
        myNodes.put(ctx, new UsesItem(createPosSymbol(ctx.getStart())));
    }

    @Override public void exitPrecisItem(ResolveParser.PrecisItemContext ctx) {
        //this node at any given time has at most one child. if you're unsure,
        //go back to the grammar and check: is it  a rule that only refers to
        //other rules? The absence of these types of middle rules in the parse
        //tree would effectively result in a more traditional ast -- and is
        //consequently the sort of thing you're doing here by hand, but w/ your own objects

        //In other words, you're going to be the thing below quite a bit, so understand it:
        myNodes.put(ctx, myNodes.get(ctx.getChild(0)));
    }

    @Override public void exitMathAssertionDecl(
            ResolveParser.MathAssertionDeclContext ctx) {
        MathAssertionDec theorem = new MathAssertionDec(
                createLocation(ctx.getStart()), createPosSymbol(
                ctx.name.getStart())); //Todo: Add the actual assertion once exprs are represented.
        myNodes.put(ctx, theorem);
    }

    protected Location createLocation(ParserRuleContext ctx) {
        return createLocation(ctx.getStart());
    }

    protected Location createLocation(Token t) {
        return new Location(new Location(myFile, t.getLine(),
                t.getCharPositionInLine(), ""));
    }

    protected PosSymbol createPosSymbol(Token t) {
        return new PosSymbol(createLocation(t), t.getText());
    }
}