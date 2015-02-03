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

import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.ConcurrentModificationException;

/**
 * <p>A <code>LazyMappingIterator</code> wraps an <code>Iterator</code> that
 * iterates over objects of type <code>I</code> and presents an interface for
 * mapping over objects of type <code>O</code>.  A <code>Mapping</code> from
 * <code>I</code> to <code>O</code> is used to transform each object as it is
 * requested.</p>

 * @param <I> The type of the objects in the source iterator.
 * @param <O> The type of the final objects.
 */
public final class LazyMappingIterator<I, O> implements Iterator<O> {

    private final Iterator<I> mySource;
    private final Mapping<I, O> myMapper;

    public LazyMappingIterator(Iterator<I> source, Mapping<I, O> mapper) {
        mySource = source;
        myMapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return mySource.hasNext();
    }

    @Override
    public O next() {
        try {
            return myMapper.map(mySource.next());
        }
        catch (ConcurrentModificationException cme) {
            int i = 5;
            throw new RuntimeException(cme);
        }
    }

    @Override
    public void remove() {
        mySource.remove();
    }
}
