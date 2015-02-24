/**
 * ModuleBuilderExtension.java
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

import edu.clemson.cs.r2jt.absynnew.decl.ModuleParameterAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>I don't really know yet how much I like the smell of this abstract module
 * builder class. Though for now if it works I'll use it since it lets me avoid
 * re-writing some common builder methods the exact same way five times
 * over for each module subclass.</p>
 * 
 * @param <E> The concrete module builder (weird, I know).
 *
 * @see <a href="http://en.wikipedia.org/wiki/Curiously_recurring_template_pattern">
 *     http://en.wikipedia.org/wiki/Curiously_recurring_template_pattern</a>.
 */
public abstract class ModuleBuilderExtension<E extends ModuleBuilderExtension<E>>
        extends
            AbstractNodeBuilder<ModuleAST> {

    public final Token name;
    public BlockAST block = BlockAST.EMPTY_BLOCK;
    public ExprAST requires = null;

    public final List<ModuleParameterAST> moduleParameters =
            new ArrayList<ModuleParameterAST>();

    public ImportCollectionAST imports =
            new ImportCollectionAST.ImportCollectionBuilder().build();

    public ModuleBuilderExtension(Token start, Token stop, Token name) {
        super(start, stop);
        this.name = name;
    }

    public Token getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public E parameters(List<ModuleParameterAST> e) {
        sanityCheckAdditions(e);
        moduleParameters.addAll(e);
        return (E) this;
    }

    @SuppressWarnings("unchecked")
    public E imports(ImportCollectionAST e) {
        sanityCheckAddition(e);
        imports = e;
        return (E) this;
    }

    @SuppressWarnings("unchecked")
    public E requires(ExprAST e) {
        requires = e;
        return (E) this;
    }

    @SuppressWarnings("unchecked")
    public E block(BlockAST e) {
        block = e == null ? BlockAST.EMPTY_BLOCK : e;
        return (E) this;
    }
}