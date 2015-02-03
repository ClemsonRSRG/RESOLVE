/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.proving2.model;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.typeandpopulate.NoSolutionException;
import java.util.Collections;
import java.util.Iterator;

/**
 * <p>A <code>Site</code> identifies a particular <code>PExp</code> accessible
 * from a {@link PerVCProverModel PerVCProverModel}, which may be a top level
 * local or global theorem, or consequent, or may be a sub-expression embedded
 * in a theorem or consequent.</p>
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
            //This looks weird but suppresses a "leaked this" warning
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
            result =
                    (conjunct == oAsSite.conjunct) && path.equals(oAsSite.path);
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
