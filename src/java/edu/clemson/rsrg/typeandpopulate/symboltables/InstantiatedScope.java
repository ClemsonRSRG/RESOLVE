/*
 * InstantiatedScope.java
 * ---------------------------------
 * Copyright (c) 2024
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * An <code>InstantiatedScope</code> decorates an existing {@link Scope} such that calls to {@code addMatches}, the
 * search method to which all others defer, are augmented with an additional set of generic instantiations and an
 * instantiating facility.
 * </p>
 *
 * @version 2.0
 */
public class InstantiatedScope extends AbstractScope {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The base scope that this class is instantiating.
     * </p>
     */
    private final Scope myBaseScope;

    /**
     * <p>
     * The facility that is instantiating this scope.
     * </p>
     */
    private final FacilityEntry myInstantiatingFacility;

    /**
     * <p>
     * Map containing all the instantiations.
     * </p>
     */
    private final Map<String, PTType> myAdditionalGenericInstantiations = new HashMap<>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an instantiated scope for the generic <code>baseScope</code>.
     * </p>
     *
     * @param baseScope
     *            The base scope that this class is instantiating.
     * @param genericInstantiations
     *            Map containing all the instantiations.
     * @param instantiatingFacility
     *            The facility that is instantiating this scope.
     */
    public InstantiatedScope(Scope baseScope, Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        myBaseScope = baseScope;
        myAdditionalGenericInstantiations.putAll(genericInstantiations);
        myInstantiatingFacility = instantiatingFacility;
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
     * <li><code>searcher</code>'s {@link TableSearcher#addMatches(SymbolTable, List, TableSearcher.SearchContext)}
     * method returns <code>true</code>, indicating the list is complete.</li>
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

        if (instantiatingFacility != null) {
            // It's unclear how this could happen or what it would mean, so we
            // fail fast. If an example triggers this, we need to think
            // carefully about what it would mean.
            throw new RuntimeException("Duplicate instantiation???");
        }

        return myBaseScope.addMatches(searcher, matches, searchedScopes, myAdditionalGenericInstantiations,
                myInstantiatingFacility, l);
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
        return myBaseScope.getFormalParameterEntries();
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
        return myBaseScope.query(query);
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
        return myBaseScope.queryForOne(query);
    }

}
