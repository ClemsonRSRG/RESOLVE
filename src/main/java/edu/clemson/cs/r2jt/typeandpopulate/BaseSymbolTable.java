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
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>A helper class to factor out some logic repeated in 
 * <code>ScopeBuilder</code> and <code>Scope</code> and remove the temptation
 * to muck about with the entry map directly.</p>
 */
class BaseSymbolTable implements SymbolTable {

    private Map<String, SymbolTableEntry> myEntries =
            new HashMap<String, SymbolTableEntry>();
    private Map<Class<?>, List<SymbolTableEntry>> myEntriesByType =
            new HashMap<Class<?>, List<SymbolTableEntry>>();

    public BaseSymbolTable() {}

    public BaseSymbolTable(BaseSymbolTable source) {
        putAll(source.myEntries);
    }

    @Override
    public void put(String name, SymbolTableEntry entry) {
        myEntries.put(name, entry);

        boolean foundTopLevel = false;
        Class<?> entryClass = entry.getClass();

        while (!foundTopLevel) {
            foundTopLevel = entryClass.equals(SymbolTableEntry.class);

            List<SymbolTableEntry> classList = myEntriesByType.get(entryClass);
            if (classList == null) {
                classList = new LinkedList<SymbolTableEntry>();
                myEntriesByType.put(entryClass, classList);
            }

            classList.add(entry);

            entryClass = entryClass.getSuperclass();
        }
    }

    @Override
    public void putAll(Map<String, SymbolTableEntry> source) {
        for (Map.Entry<String, SymbolTableEntry> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public SymbolTableEntry get(String name) {
        return myEntries.get(name);
    }

    @Override
    public boolean containsKey(String name) {
        return myEntries.containsKey(name);
    }

    @Override
    public Iterator<SymbolTableEntry> iterator() {
        return Collections.unmodifiableCollection(myEntries.values())
                .iterator();
    }

    @Override
    public <T extends SymbolTableEntry> Iterator<T> iterateByType(Class<T> type) {

        List<Class<T>> types = new LinkedList<Class<T>>();
        types.add(type);

        return iterateByType(types);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends SymbolTableEntry> Iterator<T> iterateByType(
            Collection<Class<T>> types) {
        List<T> result = new LinkedList<T>();

        List<T> typeList;
        for (Class<T> type : types) {
            typeList = (List<T>) myEntriesByType.get(type);

            if (typeList != null) {
                result.addAll(typeList);
            }
        }

        return Collections.unmodifiableList(result).iterator();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        boolean first = true;
        for (Map.Entry<String, SymbolTableEntry> entry : myEntries.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                result.append(", ");
            }

            result.append(entry.getKey());
        }

        return result.toString();
    }
}
