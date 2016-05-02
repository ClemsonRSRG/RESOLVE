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

import edu.clemson.cs.rsrg.absyn.Dec;
import edu.clemson.cs.rsrg.absyn.ModuleDec;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.declarations.MathAssertionDec;
import edu.clemson.cs.rsrg.absyn.modules.parameters.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.items.UsesItem;
import edu.clemson.cs.rsrg.absyn.modules.PrecisModuleDec;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.misc.Utilities;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

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
    private final ParseTreeProperty<ResolveConceptualElement> myNodes =
            new ParseTreeProperty<>();

    /** <p>The complete module representation.</p> */
    private ModuleDec myFinalModule = null;

    /** <p>The current file we are compiling.</p> */
    private final ResolveFile myFile;

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
    public TreeBuildingListener(ResolveFile file) {
        myFile = file;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Module Declaration
    // -----------------------------------------------------------

    @Override
    public void exitModule(ResolveParser.ModuleContext ctx) {
        myNodes.put(ctx, myNodes.get(ctx.getChild(0)));
        myFinalModule = (ModuleDec) myNodes.get(ctx.getChild(0));
    }

    // -----------------------------------------------------------
    // Precis Module
    // -----------------------------------------------------------

    @Override
    public void exitPrecisModule(
            ResolveParser.PrecisModuleContext ctx) {
        List<Dec> decls =
                Utilities.collect(Dec.class, ctx.precisItems() != null ?
                        ctx.precisItems().precisItem() :
                        new ArrayList<ParseTree>(), myNodes);
        List<ModuleParameterDec> parameterDecls = new ArrayList<>();
        List<UsesItem> uses =
                Utilities.collect(UsesItem.class, ctx.usesList() != null ?
                        ctx.usesList().usesItem() :
                        new ArrayList<ParseTree>(), myNodes);
        PrecisModuleDec precis =
                new PrecisModuleDec(createLocation(ctx), createPosSymbol(ctx.name),
                        parameterDecls, uses, decls);
        myNodes.put(ctx, precis);
    }

    @Override
    public void exitPrecisItem(ResolveParser.PrecisItemContext ctx) {
        //this node at any given time has at most one child. if you're unsure,
        //go back to the grammar and check: is it  a rule that only refers to
        //other rules? The absence of these types of middle rules in the parse
        //tree would effectively result in a more traditional ast -- and is
        //consequently the sort of thing you're doing here by hand, but w/ your own objects

        //In other words, you're going to be the thing below quite a bit, so understand it:
        myNodes.put(ctx, myNodes.get(ctx.getChild(0)));
    }

    // -----------------------------------------------------------
    // Uses Items (Imports)
    // -----------------------------------------------------------

    @Override
    public void exitUsesItem(ResolveParser.UsesItemContext ctx) {
        myNodes.put(ctx, new UsesItem(createPosSymbol(ctx.getStart())));
    }

    // -----------------------------------------------------------
    // Mathematical theorems, corollaries, etc
    // -----------------------------------------------------------

    @Override
    public void exitMathAssertionDecl(
            ResolveParser.MathAssertionDeclContext ctx) {
        MathAssertionDec theorem = new MathAssertionDec(
                createLocation(ctx.getStart()), createPosSymbol(
                ctx.name.getStart())); //Todo: Add the actual assertion once exprs are represented.
        myNodes.put(ctx, theorem);
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
     * <p>Create a location for the current parser rule
     * we are visiting.</p>
     *
     * @param ctx The visiting ANTLR4 parser rule.
     *
     * @return A {link Location} for the rule.
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
     * @return A {link Location} for the rule.
     */
    private Location createLocation(Token t) {
        return new Location(new Location(myFile, t.getLine(),
                t.getCharPositionInLine(), ""));
    }

    /**
     * <p>Create a symbol representation for the current
     * parser token we are visiting.</p>
     *
     * @param t The visiting ANTLR4 parser token.
     *
     * @return A {link PosSymbol} for the rule.
     */
    private PosSymbol createPosSymbol(Token t) {
        return new PosSymbol(createLocation(t), t.getText());
    }

}