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

import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class InstantiatedSymbolTable implements SymbolTable {

    private final GenericInstantiatingMapping<SymbolTableEntry> INSTANTIATOR =
            new GenericInstantiatingMapping<SymbolTableEntry>();

    private final SymbolTable myBaseTable;
    private final Map<String, PTType> myGenericInstantiations;
    private final FacilityEntry myInstantiatingFacility;

    public InstantiatedSymbolTable(SymbolTable base,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        myBaseTable = base;
        myGenericInstantiations = genericInstantiations;
        myInstantiatingFacility = instantiatingFacility;
    }

    @Override
    public Iterator<SymbolTableEntry> iterator() {
        return new LazyMappingIterator<SymbolTableEntry, SymbolTableEntry>(
                myBaseTable.iterator(), INSTANTIATOR);
    }

    @Override
    public void put(String name, SymbolTableEntry entry) {
        myBaseTable.put(name, entry);
    }

    @Override
    public void putAll(Map<String, SymbolTableEntry> source) {
        myBaseTable.putAll(source);
    }

    @Override
    public SymbolTableEntry get(String name) {
        return INSTANTIATOR.map(myBaseTable.get(name));
    }

    @Override
    public boolean containsKey(String name) {
        return myBaseTable.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends SymbolTableEntry> Iterator<T> iterateByType(Class<T> type) {
        return new LazyMappingIterator<T, T>(myBaseTable.iterateByType(type),
                (Mapping<T, T>) INSTANTIATOR);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends SymbolTableEntry> Iterator<T> iterateByType(
            Collection<Class<T>> types) {
        return new LazyMappingIterator<T, T>(myBaseTable.iterateByType(types),
                (Mapping<T, T>) INSTANTIATOR);
    }

    private class GenericInstantiatingMapping<T extends SymbolTableEntry>
            implements
                Mapping<T, T> {

        @SuppressWarnings("unchecked")
        @Override
        public T map(T input) {
            return (T) input.instantiateGenerics(myGenericInstantiations,
                    myInstantiatingFacility);
        }
    }
}
