/**
 * InstantiatedSymbolTable.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.symboltables;

import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO: Refactor this class
 */
public class InstantiatedSymbolTable implements SymbolTable {

    public InstantiatedSymbolTable(SymbolTable base,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {}

    @Override
    public void put(String name, SymbolTableEntry entry) {

    }

    @Override
    public void putAll(Map<String, SymbolTableEntry> source) {

    }

    @Override
    public SymbolTableEntry get(String name) {
        return null;
    }

    @Override
    public boolean containsKey(String name) {
        return false;
    }

    @Override
    public <T extends SymbolTableEntry> Iterator<T> iterateByType(Class<T> type) {
        return null;
    }

    @Override
    public <T extends SymbolTableEntry> Iterator<T> iterateByType(
            Collection<Class<T>> types) {
        return null;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<SymbolTableEntry> iterator() {
        return null;
    }
}
