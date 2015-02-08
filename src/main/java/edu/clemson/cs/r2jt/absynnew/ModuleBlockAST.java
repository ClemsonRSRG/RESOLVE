/**
 * ModuleBlockAST.java
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

import org.antlr.v4.runtime.Token;

import java.util.*;

/**
 * <p>A <code>ModuleBlockAST</code> is designed to contain the various constructs
 * that compose the body of an {@link ModuleAST}. Due to the way in which this
 * ast hierarchy is currently traversed, it is extremely important that the
 * elements list maintained by this class reflects the exact order in which
 * constructs appeared in the original source.</p>
 */
public class ModuleBlockAST extends ResolveAST {

    public static final ModuleBlockAST EMPTY_BLOCK =
            new ModuleBlockBuilder(null, null).build();

    /**
     * <p>This is the key field we need the walker to traverse; it contains all
     * elements within the scope of a module in the exact order they were
     * written into the source file.</p>
     */
    protected final List<ResolveAST> myElements;

    private ModuleBlockAST(ModuleBlockBuilder builder) {
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
     * <p>Constructs an {@link ModuleBlockAST}. The key to this particular
     * {@link edu.clemson.cs.r2jt.utilities.Builder} is that it allows users to
     * add various elements over the course a traversal when needed -- for
     * example, over the course of a parsetree traversal.</p>
     */
    public static class ModuleBlockBuilder
            extends
                AbstractNodeBuilder<ModuleBlockAST> {

        protected List<ResolveAST> elements = new ArrayList<ResolveAST>();

        /**
         * <p>These flags enable us to perform a simple error check as to
         * whether or not</p>
         */
        private boolean seenModuleLevelInitialization = false;
        private boolean seenModuleLevelFinalization = false;

        public ModuleBlockBuilder(Token start, Token stop) {
            super(start, stop);
        }

        public ModuleBlockBuilder generalElements(ResolveAST... e) {
            generalElements(Arrays.asList(e));
            return this;
        }

        public ModuleBlockBuilder generalElements(
                Collection<? extends ResolveAST> e) {
            sanityCheckAdditions(e);
            elements.addAll(e);
            return this;
        }

        @Override
        public ModuleBlockAST build() {
            return new ModuleBlockAST(this);
        }
    }
}