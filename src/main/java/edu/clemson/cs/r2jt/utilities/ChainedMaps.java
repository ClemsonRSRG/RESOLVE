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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChainedMaps<K, V> implements Map<K, V> {

    private final Map<K, V> myFirst;
    private final Map<K, V> mySecond;
    private final Map<K, V> myPersonalMap = new HashMap<K, V>();

    public ChainedMaps(Map<K, V> first, Map<K, V> second) {
        myFirst = first;
        mySecond = second;
    }

    @Override
    public void clear() {
        myFirst.clear();
        mySecond.clear();
        myPersonalMap.clear();
    }

    @Override
    public boolean containsKey(Object arg0) {
        return myFirst.containsKey(arg0) || mySecond.containsKey(arg0)
                || myPersonalMap.containsKey(arg0);
    }

    @Override
    public boolean containsValue(Object arg0) {
        return myFirst.containsValue(arg0) || mySecond.containsValue(arg0)
                || myPersonalMap.containsValue(arg0);
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return new UnionedSets<Map.Entry<K, V>>(myPersonalMap.entrySet(),
                new UnionedSets<Map.Entry<K, V>>(myFirst.entrySet(), mySecond
                        .entrySet()));
    }

    @Override
    public V get(Object arg0) {
        V result;

        if (myPersonalMap.containsKey(arg0)) {
            result = myPersonalMap.get(arg0);
        }
        else if (myFirst.containsKey(arg0)) {
            result = myFirst.get(arg0);
        }
        else {
            result = mySecond.get(arg0);
        }

        return result;
    }

    @Override
    public boolean isEmpty() {
        return myPersonalMap.isEmpty() && myFirst.isEmpty()
                && mySecond.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return new UnionedSets<K>(myPersonalMap.keySet(), new UnionedSets<K>(
                myFirst.keySet(), mySecond.keySet()));
    }

    @Override
    public V put(K arg0, V arg1) {
        V result = get(arg0);

        myPersonalMap.put(arg0, arg1);

        return result;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> arg0) {
        for (Map.Entry<? extends K, ? extends V> entry : arg0.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object arg0) {
        V result = get(arg0);

        myPersonalMap.remove(arg0);
        myFirst.remove(arg0);
        mySecond.remove(arg0);

        return result;
    }

    @Override
    public int size() {
        return myPersonalMap.size() + myFirst.size() + mySecond.size();
    }

    @Override
    public Collection<V> values() {
        return new ChainedCollections<V>(myPersonalMap.values(),
                new ChainedCollections<V>(myFirst.values(), mySecond.values()));
    }

}
