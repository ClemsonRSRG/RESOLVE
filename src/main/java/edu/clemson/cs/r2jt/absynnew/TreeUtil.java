/**
 * TreeUtil.java
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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * <p>A collection of general-purpose abstract syntax related methods.</p>
 *
 * @author dtwelch
 */
public class TreeUtil {

    /**
     * <p>Returns the abstract syntax representation of the concrete tree
     * rooted at <code>startRule</code>. As a result, this method requires
     * parsing to start from effectively any rule within the grammar.</p>
     *
     * <p>Unfortunately, it cannot be guaranteed to work on rules (subtrees)
     * lacking an explicit <tt>EOF</tt> until an issue with Antlr4 is fixed.
     * In the meantime, since all RESOLVE module rules <em>do</em> include
     * <tt>EOF</tt>, this method should reliably work for the purpose of
     * generating modules.</p>
     *
     * @param <T> The expected raw type of the ast produced from
     *           <code>startRule</code>.
     * @param startRule The rule context to begin parsing on. Note that the ast
     *                  type requested via <code>T</code> should play nice with
     *                  the passed rule. For instance, don't list ExprAST as the
     *                  expected type, then pass a variable declaration as
     *                  <code>startRule</code> (since the expected type of a
     *                  variable is DeclAST -- not ExprAST).
     * @return The <code>ResolveAST</code> representation of parse tree rooted
     *         at <code>startRule</code>.
     *
     * @see <a href="https://github.com/antlr/antlr4/issues/118">issue-118</a>.
     */
    public static <T extends ResolveAST> T createASTNodeFrom(
            ParserRuleContext startRule) {
        TreeBuildingVisitor<T> builder = new TreeBuildingVisitor(startRule);
        ParseTreeWalker.DEFAULT.walk(builder, startRule);

        return builder.build();
    }
}
