/*
 * BaseSymbolTable.java
 * ---------------------------------
 * Copyright (c) 2021
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
import java.util.*;

/**
 * <p>
 * An helper class to factor out some logic repeated in {@link ScopeBuilder} and
 * {@link Scope} and
 * remove the temptation to muck about with the entry map directly.
 * </p>
 *
 * @version 2.0
 */
class BaseSymbolTable implements SymbolTable {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The collection of entries in this table.
     * </p>
     */
    private final Map<String, SymbolTableEntry> myEntries = new HashMap<>();

    /**
     * <p>
     * The collection of entries grouped by type.
     * </p>
     */
    private final Map<Class<?>, List<SymbolTableEntry>> myEntriesByType =
            new HashMap<>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an empty symbol table.
     * </p>
     */
    BaseSymbolTable() {}

    /**
     * <p>
     * This creates a symbol table from an existing source.
     * </p>
     *
     * @param source An existing source {@code BaseSymbolTable}.
     */
    BaseSymbolTable(BaseSymbolTable source) {
        putAll(source.myEntries);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method checks to see if there is an entry with the given
     * <code>name</code>.
     * </p>
     *
     * @param name A string name.
     *
     * @return {@code true} if there is an entry with this name, {@code false}
     *         otherwise.
     */
    @Override
    public final boolean containsKey(String name) {
        return myEntries.containsKey(name);
    }

    /**
     * <p>
     * This method returns the entry specified by the name.
     * </p>
     *
     * @param name Name of an entry.
     *
     * @return A {@link SymbolTableEntry} object.
     */
    @Override
    public final SymbolTableEntry get(String name) {
        return myEntries.get(name);
    }

    /**
     * <p>
     * This method creates an iterator for the type <code>T</code>.
     * </p>
     *
     * @param type A type name.
     * @param <T> A {@link SymbolTableEntry} type.
     *
     * @return An {@link Iterator}.
     */
    @Override
    public final <T extends SymbolTableEntry> Iterator<T>
            iterateByType(Class<T> type) {
        List<Class<T>> types = new LinkedList<>();
        types.add(type);

        return iterateByType(types);
    }

    /**
     * <p>
     * This method creates an iterator for to iterate over a collection of
     * <code>T</code>.
     * </p>
     *
     * @param types A type name.
     * @param <T> A {@link SymbolTableEntry} type.
     *
     * @return An {@link Iterator}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public final <T extends SymbolTableEntry> Iterator<T>
            iterateByType(Collection<Class<T>> types) {
        List<T> result = new LinkedList<>();

        List<T> typeList;
        for (Class<T> type : types) {
            typeList = (List<T>) myEntriesByType.get(type);

            if (typeList != null) {
                result.addAll(typeList);
            }
        }

        return Collections.unmodifiableList(result).iterator();
    }

    /**
     * <p>
     * This method returns an iterator over the elements of type {@code T}.
     * </p>
     *
     * @return An {@link Iterator}.
     */
    @Override
    public final Iterator<SymbolTableEntry> iterator() {
        return Collections.unmodifiableCollection(myEntries.values())
                .iterator();
    }

    /**
     * <p>
     * This method puts an entry into the symbol table.
     * </p>
     *
     * @param name Name of an entry.
     * @param entry The entry to be put into the table.
     */
    @Override
    public final void put(String name, SymbolTableEntry entry) {
        myEntries.put(name, entry);

        boolean foundTopLevel = false;
        Class<?> entryClass = entry.getClass();

        while (!foundTopLevel) {
            foundTopLevel = entryClass.equals(SymbolTableEntry.class);

            List<SymbolTableEntry> classList = myEntriesByType.get(entryClass);
            if (classList == null) {
                classList = new LinkedList<>();
                myEntriesByType.put(entryClass, classList);
            }

            classList.add(entry);

            entryClass = entryClass.getSuperclass();
        }
    }

    /**
     * <p>
     * This method puts all the entries from the source into the symbol table.
     * </p>
     *
     * @param source A source map of entries.
     */
    @Override
    public final void putAll(Map<String, SymbolTableEntry> source) {
        for (Map.Entry<String, SymbolTableEntry> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * <p>
     * This method returns the object in string format.
     * </p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
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
