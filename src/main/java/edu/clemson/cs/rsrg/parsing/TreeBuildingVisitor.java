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
package edu.clemson.cs.rsrg.parsing;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.errorhandling.ErrorHandler;
import edu.clemson.cs.rsrg.init.file.Utilities.Builder;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

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
 * it can handle building of specific subtrees when used with the
 * {@link TreeUtil#createASTNodeFrom} method.</p>
 *
 * <p>References to the completed, ast can be acquired through
 * calls to {@link #build()}.</p>
 */
public class TreeBuildingVisitor<T extends ResolveConceptualElement>
        extends
        ResolveParserBaseListener implements Builder<T> {

    private final TreeDecorator myDecorator = new TreeDecorator();
    private final ParseTree myRootTree;
    private final ErrorHandler myErrorHandler;

    public TreeBuildingVisitor(ErrorHandler errorHandler, ParseTree tree) {
        myRootTree = tree;
        myErrorHandler = errorHandler;
    }

    @Override
    public T build() {
        ResolveConceptualElement result = get(ResolveConceptualElement.class, myRootTree);
        return result == null ? null : (T) result;
    }

    @Override
    public void exitModule(@NotNull ResolveParser.ModuleContext ctx) {
        put(ctx, get(ResolveConceptualElement.class, ctx.getChild(0)));
    }

    private void put(ParseTree parseTree, ResolveConceptualElement ast) {
        myDecorator.putProp(parseTree, ast);
    }

    /**
     * <p>Checks to make sure the string text within <code>topName</code> equals
     * <code>endName</code>; issuing a warning if not.</p>
     *
     * @param topName The name at the top that introduces a block.
     *
     * @param endName The {@link PosSymbol} following the <tt>end</tt>
     *                portion of a named block.
     */
    private void sanityCheckBlockEnds(Token topName, Token endName) {
        if (topName == null || endName == null) {
            return;
        }
        if (!topName.equals(endName)) {
            myErrorHandler.error(null, "Expecting an end block with the name: " + topName.getText() + ", but found an end block with the name: " + endName.getText());
        }
    }

    /**
     * <p>Shortcut methods to ease interaction with <code>TreeDecorator</code>;
     * for example it's somewhat shorter to say <pre>get(x.class, t)</pre>
     * than <pre>myDecorator.getProp(x.class, t)</pre>.</p>
     *
     * @param type A class within the {@link ResolveConceptualElement} hierarchy indicating
     *             expected-type.
     * @param t    A {@link ParseTree} indicating which subtree to draw the
     *             annotation from.
     *
     * @param <E>  An ast type.
     */
    protected <E extends ResolveConceptualElement> E get(Class<E> type, ParseTree t) {
        return myDecorator.getProp(type, t);
    }

    protected <E extends ResolveConceptualElement> List<E> getAll(Class<E> type,
                                                    List<? extends ParseTree> t) {
        return myDecorator.collect(type, t);
    }

    protected static class TreeDecorator {

        private final ParseTreeProperty<ResolveConceptualElement> visitedCtxs =
                new ParseTreeProperty<>();

        public <T extends ResolveConceptualElement> List<T> collect(Class<T> type,
                                                      List<? extends ParseTree> parseTrees) {
            List<T> result = new ArrayList<T>();
            for (ParseTree tree : parseTrees) {
                result.add(type.cast(visitedCtxs.get(tree)));
            }
            return result;
        }

        public void putProp(ParseTree parseTree, ResolveConceptualElement e) {
            visitedCtxs.put(parseTree, e);
        }

        public <T extends ResolveConceptualElement> T getProp(Class<T> type,
                                                ParseTree parseTree) {
            return type.cast(visitedCtxs.get(parseTree));
        }
    }
}