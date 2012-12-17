package edu.clemson.cs.r2jt.mathtype;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public interface SymbolTable extends Iterable<SymbolTableEntry> {

    public void put(String name, SymbolTableEntry entry);

    public void putAll(Map<String, SymbolTableEntry> source);

    public SymbolTableEntry get(String name);

    public boolean containsKey(String name);

    public <T extends SymbolTableEntry> Iterator<T> iterateByType(Class<T> type);

    public <T extends SymbolTableEntry> Iterator<T> iterateByType(
            Collection<Class<T>> types);
}