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

/**
 * <p>A <code>RepeatedApplicationTransformer</code> wraps an existing
 * <code>Transformer</code> from an <code>ImmutableConjuncts</code> subtype
 * <code>T</code> to an <code>Iterator</code> over new <code>T</code>s that 
 * returns singleton suggestions (that is, each call to <code>transform()</code>
 * returns an <code>Iterator</code> over <em>exactly one</em> element).  Given
 * <code>T</code>s are transformed by repeatedly applying the wrapped
 * transformation some finite number of times.</p>
 * 
 * TODO : Maybe construct some type safety so that ONLY singleton transforms can
 *        be used?
 */
public class RepeatedApplicationTransformer<T extends ImmutableConjuncts>
        implements
            Transformer<T, Iterator<T>> {

    private final Transformer<T, Iterator<T>> mySubTransformer;
    private final int myIterationCount;

    public RepeatedApplicationTransformer(Transformer<T, Iterator<T>> t,
            int iterations) {

        mySubTransformer = t;
        myIterationCount = iterations;
    }

    @Override
    public Iterator<T> transform(T original) {

        Iterator<T> singletonIterator;
        T soFar = original;
        for (int iteration = 0; iteration < myIterationCount; iteration++) {
            singletonIterator = mySubTransformer.transform(soFar);

            if (singletonIterator.hasNext()) {
                soFar = singletonIterator.next();
            }

            if (singletonIterator.hasNext()) {
                throw new RuntimeException("Non-singleton transform used in "
                        + this.getClass());
            }
        }

        return new SingletonIterator<T>(soFar);
    }

}
