/*
 * InstantiatedSymbolTable.java
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

import edu.clemson.rsrg.misc.Utilities.Mapping;
import edu.clemson.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.utilities.LazyMappingIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * An helper class that represents an instantiated symbol table.
 * </p>
 *
 * @version 2.0
 */
class InstantiatedSymbolTable implements SymbolTable {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A mapping object
     * </p>
     */
    private final GenericInstantiatingMapping<SymbolTableEntry> INSTANTIATOR = new GenericInstantiatingMapping<>();

    /**
     * <p>
     * A base symbol table to be instantiated.
     * </p>
     */
    private final SymbolTable myBaseTable;

    /**
     * <p>
     * A map of program type instantiations.
     * </p>
     */
    private final Map<String, PTType> myGenericInstantiations;

    /**
     * <p>
     * Facility entry that is instantiating this symbol table.
     * </p>
     */
    private final FacilityEntry myInstantiatingFacility;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an instantiated symbol table.
     * </p>
     *
     * @param base
     *            A base symbol table to be instantiated.
     * @param genericInstantiations
     *            A map of program type instantiations.
     * @param instantiatingFacility
     *            Facility entry that is instantiating this symbol table.
     */
    InstantiatedSymbolTable(SymbolTable base, Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        myBaseTable = base;
        myGenericInstantiations = genericInstantiations;
        myInstantiatingFacility = instantiatingFacility;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

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
    @Override
    public final boolean containsKey(String name) {
        return myBaseTable.containsKey(name);
    }

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
    @Override
    public final SymbolTableEntry get(String name) {
        return INSTANTIATOR.map(myBaseTable.get(name));
    }

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
    @SuppressWarnings("unchecked")
    @Override
    public final <T extends SymbolTableEntry> Iterator<T> iterateByType(Class<T> type) {
        return new LazyMappingIterator<>(myBaseTable.iterateByType(type), (Mapping<T, T>) INSTANTIATOR);
    }

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
    @SuppressWarnings("unchecked")
    @Override
    public final <T extends SymbolTableEntry> Iterator<T> iterateByType(Collection<Class<T>> types) {
        return new LazyMappingIterator<>(myBaseTable.iterateByType(types), (Mapping<T, T>) INSTANTIATOR);
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
        return new LazyMappingIterator<>(myBaseTable.iterator(), INSTANTIATOR);
    }

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
    @Override
    public final void put(String name, SymbolTableEntry entry) {
        myBaseTable.put(name, entry);
    }

    /**
     * <p>
     * This method puts all the entries from the source into the symbol table.
     * </p>
     *
     * @param source
     *            A source map of entries.
     */
    @Override
    public final void putAll(Map<String, SymbolTableEntry> source) {
        myBaseTable.putAll(source);
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * This is a instantiation mapping between generic and instantiated entries.
     * </p>
     *
     * @param <T>
     *            A {@link SymbolTableEntry} type.
     */
    private class GenericInstantiatingMapping<T extends SymbolTableEntry> implements Mapping<T, T> {

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * This method creates some sort of mapping between <code>input</code> and <code>T</code>.
         * </p>
         *
         * @param input
         *            An object of type <code>T</code>
         *
         * @return A relationship mapping <code>T</code>.
         */
        @SuppressWarnings("unchecked")
        @Override
        public final T map(T input) {
            return (T) input.instantiateGenerics(myGenericInstantiations, myInstantiatingFacility);
        }

    }

}
