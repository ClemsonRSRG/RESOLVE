package edu.clemson.cs.r2jt.mathtype;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.utilities.Mapping;

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
