/*
 * SymbolQuery.java
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
package edu.clemson.rsrg.typeandpopulate.query;

import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.symboltables.ScopeRepository;
import edu.clemson.rsrg.typeandpopulate.symboltables.Scope;
import java.util.List;

/**
 * <p>
 * A <code>SymbolQuery</code> defines a strategy for returning a list of {@link SymbolTableEntry}s that meet a certain
 * set of criteria starting from some <em>source scope</em>.
 */
public interface SymbolQuery<E extends SymbolTableEntry> {

    /**
     * <p>
     * Given a source {@link Scope} and a {@link ScopeRepository} containing any imports, from which <code>source</code>
     * is drawn, searches them appropriately, returning a list of matching {@link SymbolTableEntry}s that are subtypes
     * of <code>E</code>.
     * </p>
     * <p>
     * If there are no matches, returns an empty list. If more than one match is found where no more than one was
     * expected, throws a {@link DuplicateSymbolException}.
     * </p>
     *
     * @param source
     *            The source scope from which the search was spawned.
     * @param repo
     *            A repository of any referenced modules.
     *
     * @return A list of matches.
     */
    List<E> searchFromContext(Scope source, ScopeRepository repo) throws DuplicateSymbolException;

}
