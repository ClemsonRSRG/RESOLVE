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
package edu.clemson.cs.r2jt.utilities;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.clemson.cs.r2jt.proving.ChainingIterator;

public class UnionedSets<T> extends AbstractSet<T> {

    private final Set<T> mySubset1, mySubset2;
    private final Set<T> myPersonalSet = new HashSet<T>();

    public UnionedSets(Set<T> set1, Set<T> set2) {
        mySubset1 = set1;
        mySubset2 = set2;
    }

    @Override
    public boolean add(T arg0) {
        return myPersonalSet.add(arg0);
    }

    @Override
    public boolean addAll(Collection<? extends T> arg0) {
        return myPersonalSet.addAll(arg0);
    }

    @Override
    public void clear() {
        mySubset1.clear();
        mySubset2.clear();
        myPersonalSet.clear();
    }

    @Override
    public boolean contains(Object arg0) {
        return myPersonalSet.contains(arg0) || mySubset1.contains(arg0)
                || mySubset2.contains(arg0);
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        boolean result = true;

        for (Object o : arg0) {
            result = result && contains(o);
        }

        return result;
    }

    @Override
    public boolean isEmpty() {
        return myPersonalSet.isEmpty() && mySubset1.isEmpty()
                && mySubset2.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return new ChainingIterator<T>(new ChainingIterator<T>(myPersonalSet
                .iterator(), mySubset1.iterator()), mySubset2.iterator());
    }

    @Override
    public boolean remove(Object arg0) {
        boolean personal = myPersonalSet.remove(arg0);
        boolean subset1 = mySubset1.remove(arg0);
        boolean subset2 = mySubset2.remove(arg0);

        return personal || subset1 || subset2;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        boolean personal = myPersonalSet.retainAll(arg0);
        boolean subset1 = mySubset1.retainAll(arg0);
        boolean subset2 = mySubset2.retainAll(arg0);

        return personal || subset1 || subset2;
    }

    @Override
    public int size() {
        return myPersonalSet.size() + mySubset1.size() + mySubset2.size();
    }

    @Override
    public Object[] toArray() {
        Object[] personal = myPersonalSet.toArray();
        Object[] subset1 = mySubset1.toArray();
        Object[] subset2 = mySubset2.toArray();

        Object[] result =
                new Object[personal.length + subset1.length + subset2.length];

        System.arraycopy(personal, 0, result, 0, personal.length);
        System.arraycopy(subset1, 0, result, personal.length, subset1.length);
        System.arraycopy(subset2, 0, result, personal.length + subset1.length,
                subset2.length);

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R[] toArray(R[] arg0) {
        R[] exemplarArray = (R[]) new Object[0];

        R[] personal = myPersonalSet.toArray(exemplarArray);
        R[] subset1 = mySubset1.toArray(exemplarArray);
        R[] subset2 = mySubset2.toArray(exemplarArray);

        int requiredLength = personal.length + subset1.length + subset2.length;
        R[] result = arg0;
        if (result.length < requiredLength) {
            result = (R[]) new Object[requiredLength];
        }

        System.arraycopy(personal, 0, result, 0, personal.length);
        System.arraycopy(subset1, 0, result, personal.length, subset1.length);
        System.arraycopy(subset2, 0, result, personal.length + subset1.length,
                subset2.length);

        return result;
    }

}
