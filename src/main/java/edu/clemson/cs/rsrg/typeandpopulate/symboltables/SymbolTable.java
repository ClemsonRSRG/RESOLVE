/**
 * SymbolTable.java
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

import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO: Refactor this class
 */
public interface SymbolTable extends Iterable<SymbolTableEntry> {

    void put(String name, SymbolTableEntry entry);

    void putAll(Map<String, SymbolTableEntry> source);

    SymbolTableEntry get(String name);

    boolean containsKey(String name);

    <T extends SymbolTableEntry> Iterator<T> iterateByType(Class<T> type);

    <T extends SymbolTableEntry> Iterator<T> iterateByType(
            Collection<Class<T>> types);

}