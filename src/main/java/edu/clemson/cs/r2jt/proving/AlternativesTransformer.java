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
import java.util.LinkedList;
import java.util.List;

public class AlternativesTransformer<T> implements Transformer<T, Iterator<T>> {

    private final Iterator<T> TYPE_SAFE_ITERATOR = null;

    private List<Transformer<T, Iterator<T>>> myAlternatives =
            new LinkedList<Transformer<T, Iterator<T>>>();

    public void addAlternative(Transformer<T, Iterator<T>> a) {
        myAlternatives.add(a);
    }

    @Override
    public Iterator<T> transform(T source) {

        Iterator<T> current;
        Iterator<T> soFar = DummyIterator.getInstance(TYPE_SAFE_ITERATOR);
        for (Transformer<T, Iterator<T>> alternative : myAlternatives) {

            current = alternative.transform(source);

            if (current.hasNext()) {
                soFar = new ChainingIterator<T>(soFar, current);
                //alternative.transform(source));
            }
        }

        return soFar;
    }
}
