/*
 * DummyIdentifierResolver.java
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

import edu.clemson.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.query.MultimatchSymbolQuery;
import edu.clemson.rsrg.typeandpopulate.query.SymbolQuery;
import edu.clemson.rsrg.typeandpopulate.query.searcher.TableSearcher;
import edu.clemson.rsrg.typeandpopulate.query.searcher.TableSearcher.SearchContext;
import java.util.*;

/**
 * <p>
 * A <code>DummyIdentifierResolver</code> is a scope for built-in things.
 * </p>
 *
 * @version 2.0
 */
class DummyIdentifierResolver extends AbstractScope {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a scope for all built-in things.
     * </p>
     */
    DummyIdentifierResolver() {
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Begins applying the given {@link TableSearcher} up the lexical hierarchy that ends in this scope, starting with
     * this scope, thus adding any matches to <code>matches</code>. For the purposes of the search and the returned
     * results, any generics will be instantiated appropriately according to <code>genericInstantiations</code>, but
     * this scope will not be permanently modified.
     * </p>
     *
     * <p>
     * The search will continue upward toward the top-level global scope until one of the following happens:
     * </p>
     *
     * <ul>
     * <li>The top-level scope is searched.</li>
     * <li>A scope is reached that is already in <code>searchedScopes</code>.</li>
     * <li><code>searcher</code>'s {@link TableSearcher#addMatches(SymbolTable, List, SearchContext)} method returns
     * <code>true</code>, indicating the list is complete.</li>
     * <li>The <code>addMatches()</code> method throws a {@link DuplicateSymbolException}.</li>
     * </ul>
     *
     * <p>
     * In the first three cases, the method returns normally, with any matches added to <code>matches</code>. If no
     * matches are found, <code>matches</code> will simply be left unchanged.
     * </p>
     *
     * <p>
     * In the last case, this method will throw a <code>DuplicateSymbolException</code>.
     * </p>
     *
     * <p>
     * Regardless of how this method terminates, any searched scopes will be added to <code>searchedScopes</code>.
     * </p>
     *
     * @param searcher
     *            The searcher to be used to match symbol table entries.
     * @param matches
     *            A non-<code>null</code> accumulator of matches.
     * @param searchedScopes
     *            A set of already-searched scopes.
     * @param genericInstantiations
     *            A mapping from generic names to instantiated types, which will be applied when searching and returning
     *            results.
     * @param instantiatingFacility
     *            A pointer to the symbol table entry corresponding to the facility that provided the instantiation of
     *            the generics given by <code>genericInstantiations</code>, or <code>null</code> if we are not searching
     *            an instantiated scope. If this parameter is <code>null</code> then
     *            <code>genericInstantiations.isEmpty() == true</code>, but the reverse is not necessarily the case.
     *
     * @return <code>true</code> if <code>matches</code> now represents a final list of search results; i.e., no further
     *         scopes should be considered. <code>false</code> indicates that the search should continue, provided there
     *         are additional un-searched scopes.
     */
    @Override
    public final <E extends SymbolTableEntry> boolean addMatches(TableSearcher<E> searcher, List<E> matches,
            Set<Scope> searchedScopes, Map<String, PTType> genericInstantiations, FacilityEntry instantiatingFacility,
            SearchContext l) throws DuplicateSymbolException {
        return false;
    }

    /**
     * <p>
     * Returns a list of {@link ProgramParameterEntry}s contained directly in this scope. These correspond to the formal
     * parameters defined by the syntactic element that introduced the scope.
     * </p>
     *
     * <p>
     * If there are no parameters, or the syntactic element is not of the sort that can define parameters, returns an
     * empty list.
     * </p>
     *
     * @return Entries for the parameters of the current scope.
     */
    @Override
    public final List<ProgramParameterEntry> getFormalParameterEntries() {
        return Collections.emptyList();
    }

    /**
     * <p>
     * Searches for symbols by the given query, using this <code>Scope</code> as the source scope of the search, i.e.
     * the scope that is the context from which the search was triggered.
     * </p>
     *
     * @param query
     *            The query to use.
     *
     * @return A list of all symbols matching the given query.
     */
    @Override
    public final <E extends SymbolTableEntry> List<E> query(MultimatchSymbolQuery<E> query) {
        return new LinkedList<>();
    }

    /**
     * <p>
     * Searches for a symbol using the given query, using this <code>Scope</code> as the source scope of the search,
     * i.e. the scope that is the context from which the search was triggered. This method works much like
     * {@link #query(MultimatchSymbolQuery)} except that it expects exactly one match, as determined by the given query.
     * If the given query matches more than one entry, or itself throws a {@link DuplicateSymbolException}, then this
     * method will throw a <code>DuplicateSymbolException</code>.
     * </p>
     *
     * @param query
     *            The query to use.
     *
     * @return The single symbol that is matched.
     *
     * @throws NoSuchSymbolException
     *             If the query matches no symbol table entries.
     * @throws DuplicateSymbolException
     *             If the query matches more than one symbol table entry.
     */
    @Override
    public final <E extends SymbolTableEntry> E queryForOne(SymbolQuery<E> query)
            throws NoSuchSymbolException, DuplicateSymbolException {
        throw new NoSuchSymbolException("This scope cannot contain user created entries.", new IllegalStateException());
    }

}
