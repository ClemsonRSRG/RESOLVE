/*
 * Site.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.model;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.typeandpopulate.NoSolutionException;
import java.util.Collections;
import java.util.Iterator;

/**
 * <p>
 * A <code>Site</code> identifies a particular <code>PExp</code> accessible from
 * a
 * {@link PerVCProverModel PerVCProverModel}, which may be a top level local or
 * global theorem, or
 * consequent, or may be a sub-expression embedded in a theorem or consequent.
 * </p>
 */
public class Site {

    public enum Section {

        ANTECEDENTS {

            @Override
            public Theorem getRootTheorem(PerVCProverModel m, int index) {
                return m.getLocalTheorem(index);
            }
        },
        CONSEQUENTS {

            @Override
            public Theorem getRootTheorem(PerVCProverModel m, int index)
                    throws NoSolutionException {
                throw NoSolutionException.INSTANCE;
            }
        },
        THEOREM_LIBRARY {

            @Override
            public Theorem getRootTheorem(PerVCProverModel m, int index)
                    throws NoSolutionException {
                return m.getTheoremLibrary().get(index);
            }
        };

        public abstract Theorem getRootTheorem(PerVCProverModel m, int index)
                throws NoSolutionException;
    };

    public final Conjunct conjunct;
    public final ImmutableList path;
    public final PExp exp;
    public final Site root;

    private final int myHashCode;

    private final PerVCProverModel mySource;

    public Site(PerVCProverModel source, Conjunct c, Iterable<Integer> path,
            PExp exp) {
        this(source, c, path, exp, new Site(source, c, c.getExpression()));
    }

    public Site(PerVCProverModel source, Conjunct c, PExp exp) {
        this(source, c, Collections.EMPTY_LIST, exp, null);
    }

    private Site(PerVCProverModel source, Conjunct c, Iterable<Integer> path,
            PExp exp, Site root) {
        this.conjunct = c;
        this.path = new ArrayBackedImmutableList(path);
        this.exp = exp;

        if (exp == null) {
            throw new IllegalArgumentException("Null exp");
        }

        myHashCode = c.hashCode() + (57 * this.path.hashCode());

        if (root == null) {
            // This looks weird but suppresses a "leaked this" warning
            Site r = this;
            this.root = r;
        }
        else {
            this.root = root;
        }

        mySource = source;
    }

    public Theorem getRootTheorem() throws NoSolutionException {
        Theorem result;

        try {
            result = (Theorem) conjunct;
        }
        catch (ClassCastException cce) {
            throw new NoSolutionException("Site is not rooted in a theorem.");
        }

        return result;
    }

    @Override
    public int hashCode() {
        return myHashCode;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = (o instanceof Site);

        if (result) {
            Site oAsSite = (Site) o;
            result = (conjunct == oAsSite.conjunct)
                    && path.equals(oAsSite.path);
        }

        return result;
    }

    public PerVCProverModel getModel() {
        return mySource;
    }

    public Iterator<Integer> pathIterator() {
        return path.iterator();
    }

    public boolean inside(Site s) {
        if (getModel() != s.getModel()) {
            throw new RuntimeException("Incomparable sites--different models.");
        }

        boolean result = (conjunct == s.conjunct);

        if (result) {
            Iterator<Integer> myIter = pathIterator();
            Iterator<Integer> oIter = s.pathIterator();

            boolean goodSoFar = true;
            while (myIter.hasNext() && oIter.hasNext() && goodSoFar) {
                goodSoFar = (myIter.next().equals(oIter.next()));
            }

            result = (goodSoFar && !oIter.hasNext());
        }

        return result;
    }

    @Override
    public String toString() {
        return conjunct.getClass().getSimpleName() + ":" + root.exp + ":" + exp;
    }
}
