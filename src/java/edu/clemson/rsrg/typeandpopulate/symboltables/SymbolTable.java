/*
 * SymbolTable.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.symboltables;

import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * A <code>SymbolTable</code> stores all the {@link SymbolTableEntry} introduced by a RESOLVE module and created by the
 * compiler.
 * </p>
 *
 * @version 2.0
 */
public interface SymbolTable extends Iterable<SymbolTableEntry> {

    /**
     * <p>
     * This method checks to see if there is an entry with the given <code>name</code>.
     * </p>
     *
     * @param name
     *            A string name.
     *
     * @return {@code true} if there is an entry with this name, {@code false} otherwise.
     */
    boolean containsKey(String name);

    /**
     * <p>
     * This method returns the entry specified by the name.
     * </p>
     *
     * @param name
     *            Name of an entry.
     *
     * @return A {@link SymbolTableEntry} object.
     */
    SymbolTableEntry get(String name);

    /**
     * <p>
     * This method creates an iterator for the type <code>T</code>.
     * </p>
     *
     * @param type
     *            A type name.
     * @param <T>
     *            A {@link SymbolTableEntry} type.
     *
     * @return An {@link Iterator}.
     */
    <T extends SymbolTableEntry> Iterator<T> iterateByType(Class<T> type);

    /**
     * <p>
     * This method creates an iterator for to iterate over a collection of <code>T</code>.
     * </p>
     *
     * @param types
     *            A type name.
     * @param <T>
     *            A {@link SymbolTableEntry} type.
     *
     * @return An {@link Iterator}.
     */
    <T extends SymbolTableEntry> Iterator<T> iterateByType(Collection<Class<T>> types);

    /**
     * <p>
     * This method puts an entry into the symbol table.
     * </p>
     *
     * @param name
     *            Name of an entry.
     * @param entry
     *            The entry to be put into the table.
     */
    void put(String name, SymbolTableEntry entry);

    /**
     * <p>
     * This method puts all the entries from the source into the symbol table.
     * </p>
     *
     * @param source
     *            A source map of entries.
     */
    void putAll(Map<String, SymbolTableEntry> source);

}
