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
package edu.clemson.cs.r2jt.proving.immutableadts;

import java.util.Iterator;

public interface SimpleImmutableList<E> extends Iterable<E> {

    /*
     * "Mutator" methods that return a new, changed version of this list
     */

    public SimpleImmutableList<E> appended(E e);

    public SimpleImmutableList<E> appended(SimpleImmutableList<E> l);

    public SimpleImmutableList<E> appended(Iterable<E> i);

    public SimpleImmutableList<E> removed(int index);

    public SimpleImmutableList<E> set(int index, E e);

    public SimpleImmutableList<E> insert(int index, E e);

    public SimpleImmutableList<E> insert(int index, SimpleImmutableList<E> e);

    /*
     * Methods that return a view of this list
     */

    public SimpleImmutableList<E> subList(int startIndex, int length);

    public SimpleImmutableList<E> tail(int startIndex);

    public SimpleImmutableList<E> head(int length);

    /*
     * Methods for getting out elements
     */

    public E first();

    public E get(int index);

    public Iterator<E> iterator();

    /*
     * Utility methods.
     */

    public int size();
}
