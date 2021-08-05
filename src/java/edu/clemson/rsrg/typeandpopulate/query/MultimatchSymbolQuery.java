/*
 * MultimatchSymbolQuery.java
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
package edu.clemson.rsrg.typeandpopulate.query;

import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.symboltables.ScopeRepository;
import edu.clemson.rsrg.typeandpopulate.symboltables.Scope;
import java.util.List;

/**
 * <p>
 * Refines {@link SymbolQuery} by guaranteeing that {@link SymbolQuery#searchFromContext(Scope, ScopeRepository)} will
 * not throw a {@link DuplicateSymbolException}.
 * </p>
 *
 * @version 2.0
 */
public interface MultimatchSymbolQuery<E extends SymbolTableEntry> extends SymbolQuery<E> {

    /**
     * <p>
     * Behaves just as {@link SymbolQuery#searchFromContext(Scope, ScopeRepository)}, except that it cannot throw a
     * {@link DuplicateSymbolException}.
     * </p>
     *
     * @param source
     *            The source scope from which the search was spawned.
     * @param repo
     *            A repository of any referenced modules.
     *
     * @return A list of matches.
     */
    @Override
    List<E> searchFromContext(Scope source, ScopeRepository repo);

}
