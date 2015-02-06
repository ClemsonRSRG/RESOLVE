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

public class TreeUtil {

    /**
     * <p>This method is intended to allow parsing to start from effectively
     * <em>any</em> rule within <code>ResolveParser</code>.</p>
     *
     * <p>Unfortunately, it cannot be guaranteed to work on rules
     * without an explicit <tt>EOF</tt> until an issue with Antlr4 is fixed.
     * In the meantime, since all RESOLVE module rules include an explicit
     * <tt>EOF</tt>, this method should reliably work for the purpose of
     * generating modules.</p>
     *
     * @see <a href="https://github.com/antlr/antlr4/issues/118">
     *     https://github.com/antlr/antlr4/issues/118</a>.
     */
    public static <T extends ResolveAST> T createASTNodeFrom(
            ParserRuleContext startRule) {
        ASTBuildingVisitor<T> builder = new ASTBuildingVisitor(startRule);
        ParseTreeWalker.DEFAULT.walk(builder, startRule);

        return builder.build();
    }
}
