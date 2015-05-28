/**
 * SymbolTable.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
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