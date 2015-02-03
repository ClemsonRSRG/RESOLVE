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
package edu.clemson.cs.r2jt.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>A set of utility functions for java collections.</p>
 */
public class RCollections {

    /**
     * <p>Returns a new list that is the result of processing each element of
     * the original list with the given mapping, in order, and adding it to
     * the final list.</p>
     * 
     * <p>This method will return an <code>ArrayList</code> if given one, and
     * a <code>LinkedList</code> if given one, but otherwise the specific 
     * subclass of list returned is undefined.</p>
     * 
     * @param original The original list.
     * @param mapping A mapping to be used to render the new list.
     * 
     * @return The new list.
     */
    public static <T, R> List<R> map(List<T> original, Mapping<T, R> mapping) {
        List<R> result;

        if (original instanceof ArrayList) {
            result = new ArrayList<R>(original.size());
        }
        else {
            result = new LinkedList<R>();
        }

        for (T t : original) {
            result.add(mapping.map(t));
        }

        return result;
    }

    public static <P1, P2, R> R foldr2(List<P1> list1, List<P2> list2,
            Mapping3<P1, P2, R, R> mapping, R initialValue) {

        if (list1.size() != list2.size()) {
            throw new IllegalArgumentException();
        }

        R result = initialValue;

        Iterator<P1> list1Iter = list1.iterator();
        Iterator<P2> list2Iter = list2.iterator();
        while (list1Iter.hasNext()) {
            result = mapping.map(list1Iter.next(), list2Iter.next(), result);
        }

        return result;
    }
}
