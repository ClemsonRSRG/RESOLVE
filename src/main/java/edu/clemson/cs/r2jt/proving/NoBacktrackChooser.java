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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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
package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>A <code>NoBacktrackChooser</code> wraps an existing 
 * <code>TransformationChooser</code> such that calls to 
 * {@link #suggestTransformations(VC, int, Metrics, Iterable) 
 * 		suggestTransformations()} return <code>Iterator</code>s over all the
 * <code>VCTransformer</code>s the wrapped <code>TransformationChooser</code>
 * would ordinarily suggest, themselves wrapped in 
 * {@link NoBacktrackTransformer NoBacktrackTransformer}s, up to and including
 * the first such <code>VCTransformer</code> that actually returns a transformed
 * <code>VC</code>, after which no further <code>VCTransformer</code>s are
 * returned.</p>
 */
public class NoBacktrackChooser implements TransformationChooser {

    private static final NoBacktrackWrappingMapping NO_BACKTRACK_MAP =
            new NoBacktrackWrappingMapping();

    private final TransformationChooser myBaseChooser;

    public NoBacktrackChooser(TransformationChooser base) {
        myBaseChooser = base;
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        myBaseChooser.preoptimizeForVC(vc);
    }

    @Override
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d) {

        return new ZeroOrOneIterator(
                new LazyMappingIterator<ProofPathSuggestion, ProofPathSuggestion>(
                        myBaseChooser.suggestTransformations(vc, curLength,
                                metrics, d), NO_BACKTRACK_MAP));
    }

    @Override
    public String toString() {
        return "" + myBaseChooser;
    }

    private static class ZeroOrOneIterator
            implements
                Iterator<ProofPathSuggestion> {

        private final Iterator<ProofPathSuggestion> myBaseIterator;
        private ProofPathSuggestion myLastSuggestion;

        public ZeroOrOneIterator(Iterator<ProofPathSuggestion> base) {
            myBaseIterator = base;
        }

        @Override
        public boolean hasNext() {
            boolean retval = true;

            if (myLastSuggestion != null) {
                retval =
                        !((NoBacktrackTransformer) myLastSuggestion.step)
                                .hasReturned();
            }

            retval &= myBaseIterator.hasNext();

            return retval;
        }

        @Override
        public ProofPathSuggestion next() {
            if (myLastSuggestion != null
                    && ((NoBacktrackTransformer) myLastSuggestion.step)
                            .hasReturned()) {
                throw new NoSuchElementException();
            }

            myLastSuggestion = myBaseIterator.next();

            return myLastSuggestion;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class NoBacktrackWrappingMapping
            implements
                Mapping<ProofPathSuggestion, ProofPathSuggestion> {

        @Override
        public ProofPathSuggestion map(ProofPathSuggestion i) {
            return new ProofPathSuggestion(new NoBacktrackTransformer(i.step),
                    i.data);
        }
    }

}
