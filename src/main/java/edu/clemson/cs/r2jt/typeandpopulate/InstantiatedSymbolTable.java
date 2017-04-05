/*
 * InstantiatedSymbolTable.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.rewriteprover.iterators.LazyMappingIterator;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.misc.Utils.Mapping;
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
