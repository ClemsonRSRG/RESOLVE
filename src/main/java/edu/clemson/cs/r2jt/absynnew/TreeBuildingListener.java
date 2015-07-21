/**
 * TreeBuildingListener.java
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

import edu.clemson.cs.r2jt.parsing.ResolveParserBaseListener;
import edu.clemson.cs.r2jt.parsing.ResolveParser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.List;

public class TreeBuildingListener extends ResolveParserBaseListener {

    private final ParseTreeProperty<ResolveAST> built =
            new ParseTreeProperty<ResolveAST>();

    public void exitModule(@NotNull ResolveParser.ModuleContext ctx) {
        built.put(ctx, built.get(ctx.getChild(0)));
    }

    public void exitPrecisModule(@NotNull ResolveParser.PrecisModuleContext ctx) {
    //built.put()
    //  ModuleAST.PrecisAST precis = ModuleAST.PrecisAST(
    //         ctx.getStart(), ctx.getStop(), )
    }

    public void exitUsesList(@NotNull ResolveParser.UsesListContext ctx) {
    //ImportCollectionAST
    }

    public static <E, T extends E> List<T> collect(Class<T> expectedType,
            List<? extends ParseTree> nodes,
            ParseTreeProperty<? extends E> annotations) {
        List<T> result = new ArrayList<T>();
        for (ParseTree node : nodes) {
            result.add(expectedType.cast(annotations.get(node)));
        }
        return result;
    }
}
