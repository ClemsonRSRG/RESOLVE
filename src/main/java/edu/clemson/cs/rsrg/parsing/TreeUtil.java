/**
 * TreeUtil.java
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

import edu.clemson.cs.rsrg.absyn.*;
import edu.clemson.cs.rsrg.errorhandling.ErrorHandler;
import java.util.Collection;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;

/**
 * <p>A place for general-purpose abstract syntax related methods.</p>
 *
 * @author Daniel Welch
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class TreeUtil {

    /**
     * <p>Joins (delimits) a list by separator <code>sep</code>.</p>
     *
     * @param l     The collection of elements to delmit.
     * @param sep   The desired separator.
     * @return      The delimited list.
     */
    public static String join(Collection<?> l, String sep) {
        ST elements =
                new ST("<elems; separator={" + sep + "}>").add("elems", l);
        return elements.render();
    }

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
     * @param errorHandler An instance of {@link ErrorHandler}.
     * @param startRule The rule context to begin parsing on. Note that the ast
     *                  type requested via <code>T</code> should play nice with
     *                  the passed rule. For instance, don't list ExprAST as the
     *                  expected type, then pass a variable declaration as
     *                  <code>startRule</code> (since the expected type of a
     *                  variable is DeclAST -- not ExprAST).
     * @return          The <code>ResolveAST</code> representation of parse tree
     *                  rooted at <code>startRule</code>.
     *
     * @see <a href="https://github.com/antlr/antlr4/issues/118">issue-118</a>.
     */
    public static <T extends ResolveConceptualElement> T createASTNodeFrom(
            ErrorHandler errorHandler,
            ParserRuleContext startRule) {
        TreeBuildingVisitor<T> builder =
                new TreeBuildingVisitor<>(errorHandler, startRule);
        ParseTreeWalker.DEFAULT.walk(builder, startRule);

        return builder.build();
    }
}