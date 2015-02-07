/**
 * AbstractNodeBuilder.java
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

import edu.clemson.cs.r2jt.utilities.Builder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.Collection;

/**
 * <p>Factors out some common logic for <code>Builder</code>s that construct
 * <code>ResolveAST</code> node classes.</p>
 *
 * <p><strong>Note:</strong> this class is <em>not</em> intended to hold any
 * adder methods, no matter how common they might be -- as this breaks the
 * pattern being used here.</p>
 *
 * @param <T> The type of the {@link ResolveAST} node being built.
 */
public abstract class AbstractNodeBuilder<T extends ResolveAST>
        implements
            Builder<T> {

    private final Token start, stop;

    public AbstractNodeBuilder(Token start, Token stop) {
        this.start = start;
        this.stop = stop;
    }

    //Todo: this should probably go.
    public AbstractNodeBuilder(ParserRuleContext ctx) {
        this(ctx.getStart(), ctx.getStop());
    }

    @Override
    public abstract T build();

    public Token getStart() {
        return start;
    }

    public Token getStop() {
        return stop;
    }

    protected static <E extends ResolveAST> void sanityCheckAdditions(
            Collection<? extends E> additions) {
        for (E addition : additions) {
            sanityCheckAddition(addition);
        }
    }

    /**
     * <p>A sanity check that sounds the alarm should anyone attempt to add
     * a <code>null</code> {@link ResolveAST} to a <strong>Collection</strong>
     * within some <code>AbstractNodeBuilder</code>.</p>
     *
     * @param e The element to be added.
     * @param <E> The type of entry.
     */
    protected static <E extends ResolveAST> void sanityCheckAddition(E e) {
        if (e == null) {
            throw new IllegalArgumentException("attempting to add a null "
                    + "element to a buildable ast node");
        }
    }
}