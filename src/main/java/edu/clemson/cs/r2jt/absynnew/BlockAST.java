/**
 * BlockAST.java
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
package edu.clemson.cs.r2jt.absynnew;

import org.antlr.v4.runtime.Token;

import java.util.*;

/**
 * A {@code BlockAST} is designed to contain some number {@link ResolveAST}s.
 * Due to the way in which this ast hierarchy is currently traversed, it is
 * extremely important that the elements list maintained by this class reflects
 * the exact order in which constructs appeared in the original source.
 */
public class BlockAST extends ResolveAST {

    public static final BlockAST EMPTY_BLOCK =
            new BlockBuilder(null, null).build();

    /**
     * This is the key field we need the walker to traverse; it contains all
     * elements within the scope of a module in the exact order they were
     * written into the source file.
     */
    protected final List<ResolveAST> myElements;

    private BlockAST(BlockBuilder builder) {
        super(builder.getStart(), builder.getStop());
        myElements = builder.elements;
    }

    public List<ResolveAST> getElements() {
        return myElements;
    }

    public <T extends ResolveAST> List<T> getElementsByType(
            Class<? extends T> astType) {
        if (myElements == null) {
            return Collections.emptyList();
        }
        List<T> result = null;
        for (ResolveAST o : myElements) {
            if (astType.isInstance(o)) {
                if (result == null) {
                    result = new ArrayList<T>();
                }
                result.add(astType.cast(o));
            }
        }
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * Constructs an {@link BlockAST}. The key to this particular
     * {@link edu.clemson.cs.r2jt.misc.Utils.Builder} is that it allows users to
     * add various elements over the course a traversal when needed -- for
     * example, over the course of a parsetree traversal.
     */
    public static class BlockBuilder extends AbstractNodeBuilder<BlockAST> {

        protected List<ResolveAST> elements = new ArrayList<ResolveAST>();

        public BlockBuilder(Token start, Token stop) {
            super(start, stop);
        }

        public BlockBuilder generalElements(ResolveAST... e) {
            generalElements(Arrays.asList(e));
            return this;
        }

        //Todo: perform module level init/final sanity check. Enforce at most
        //one of each; throw sourceErrorException otherwise.
        public BlockBuilder generalElements(Collection<? extends ResolveAST> e) {
            sanityCheckAdditions(e);
            elements.addAll(e);
            return this;
        }

        @Override
        public BlockAST build() {
            return new BlockAST(this);
        }
    }
}