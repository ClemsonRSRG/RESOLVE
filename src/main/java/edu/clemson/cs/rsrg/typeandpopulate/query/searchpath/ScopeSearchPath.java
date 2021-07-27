/*
 * ScopeSearchPath.java
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
package edu.clemson.cs.rsrg.typeandpopulate.query.searchpath;

import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeRepository;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import java.util.List;

/**
 * <p>
 * A <code>ScopeSearchPath</code> defines which {@link Scope}s should be
 * searched for symbol table
 * matches and in what order.
 * </p>
 *
 * <p>
 * All symbol table searches take place in the context of a <em>source
 * scope</em>, which is the
 * scope from which the request is made. I.e., if a procedure called
 * <code>Foo</code> references a
 * symbol called <code>X</code>, triggering a look-up for what <code>X</code>
 * could be, then the
 * scope for <code>Foo</code> is the source scope.
 * </p>
 *
 * <p>
 * Given a {@link TableSearcher}, a source scope, and a {@link ScopeRepository}
 * containing any
 * imports, a <code>ScopeSearchPath</code> will apply the
 * <code>TableSearcher</code> appropriately
 * to any <code>Scope</code>s that should be searched.
 * </p>
 */
public interface ScopeSearchPath {

    /**
     * <p>
     * Applies the given {@link TableSearcher} to the appropriate
     * {@link Scope}s, given a source scope
     * and a {@link ScopeRepository} containing any imports, returning a list of
     * matching
     * {@link SymbolTableEntry}s.
     * </p>
     *
     * <p>
     * If there are no matches, returns an empty list. If more than one match is
     * found and
     * <code>searcher</code> expects no more than one match, throws a
     * {@link DuplicateSymbolException}.
     * </p>
     *
     * @param <E> A specific {@link SymbolTableEntry} we are searching for.
     * @param searcher A <code>TableSearcher</code> to apply to each scope along
     *        the search path.
     * @param source The current scope from which the search was spawned.
     * @param repo A collection of scopes.
     *
     * @return A list of matches.
     */
    <E extends SymbolTableEntry> List<E> searchFromContext(
            TableSearcher<E> searcher, Scope source, ScopeRepository repo)
            throws DuplicateSymbolException;

}
