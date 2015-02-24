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

import edu.clemson.cs.r2jt.parsing.ResolveParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;

import java.util.Collection;

/**
 * <p>A place for general-purpose abstract syntax related methods.</p>
 *
 * @author dtwelch
 */
public class TreeUtil {

    /**
     * <p>Returns the appropriate RESOLVE function name for the operator
     * appearing in {@link Token} <code>op</code>. Note that the name returned
     * is contingent on the naming of the operations in the 'standard' templates
     * (e.g. <tt>Integer_Template</tt>, <tt>Boolean_Template</tt>, etc).</p>
     *
     * <p>So it's important to realize that if one of these 'standard' operation
     * name's changes, that this method is updated to reflect the new name,
     * or {@link edu.clemson.cs.r2jt.typeandpopulate2.PopulatingVisitor}
     * will fail to find the operation (or worse, find a wrong one).</p>
     * @param op  A syntactic operator (<tt>+, -, *</tt>) as would appear
     *            in user sourcecode.
     * @return    The name of the
     *            {@link edu.clemson.cs.r2jt.absynnew.decl.OperationSigAST}
     *              representing the operator <code>op</code>.
     */
    public static Token getTemplateOperationNameFor(Token op) {
        String result;
        if (op == null) {
            throw new IllegalArgumentException("op passed is null");
        }
        switch (op.getType()) {
        case ResolveParser.Add:
            result = "Sum";
            break;
        case ResolveParser.Subtract:
            result = "Difference";
            break;
        case ResolveParser.Multiply:
            result = "Product";
            break;
        case ResolveParser.Divide:
            result = "Divide";
            break;
        case ResolveParser.GT:
            result = "Greater";
            break;
        case ResolveParser.LT:
            result = "Less";
            break;
        case ResolveParser.LTEquals:
            result = "Less_Or_Equal";
            break;
        case ResolveParser.GTEquals:
            result = "Greater_Or_Equal";
            break;
        case ResolveParser.And:
            result = "And";
            break;
        case ResolveParser.Or:
            result = "Or";
            break;
        case ResolveParser.Not:
            result = "Negate";
            break;
        default:
            result = op.getText();
            break;
        }
        return new ResolveToken(result);
    }

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
     * @param <T>       The expected raw type of the ast produced from
     *                  <code>startRule</code>.
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
    public static <T extends ResolveAST> T createASTNodeFrom(
            ParserRuleContext startRule) {
        TreeBuildingVisitor<T> builder = new TreeBuildingVisitor<T>(startRule);
        ParseTreeWalker.DEFAULT.walk(builder, startRule);

        return builder.build();
    }
}
