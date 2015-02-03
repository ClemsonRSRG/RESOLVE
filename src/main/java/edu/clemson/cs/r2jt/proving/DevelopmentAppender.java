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
package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>DevelopmentAppender</code> adapts an
 * <code>AntecedentDeveloper</code> so that, rather than returning 
 * <code>Antecedent</code>s consisting of just those conjuncts that have been
 * added, it returns <code>Antecedent</code>s consisting of the original 
 * conjuncts with the new conjuncts appended at the end.</p>
 */
public class DevelopmentAppender implements AntecedentTransformer {

    private final AntecedentDeveloper myDeveloper;

    public DevelopmentAppender(AntecedentDeveloper d) {
        myDeveloper = d;
    }

    @Override
    public Iterator<Antecedent> transform(Antecedent source) {
        return new Extender(source, myDeveloper.transform(source));
    }

    private class Extender implements Iterator<Antecedent> {

        private final Antecedent myOriginal;
        private final Iterator<Antecedent> myExtensions;

        public Extender(Antecedent original, Iterator<Antecedent> extensions) {
            myOriginal = original;
            myExtensions = extensions;
        }

        @Override
        public boolean hasNext() {
            return myExtensions.hasNext();
        }

        @Override
        public Antecedent next() {
            return myOriginal.appended(myExtensions.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
