/*
 * AbstractScope.java
 * ---------------------------------
 * Copyright (c) 2019
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
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher.SearchContext;
import java.util.*;

abstract class AbstractScope implements Scope {

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>A simple variation on {@link #addMatches} that creates
     * and returns a new list rather than requiring an accumulator and starts
     * with an empty set of searched scopes and instantiations.</p>
     *
     * @param searcher The searcher to be used to match symbol table entries.
     *
     * @return A list of all symbols that match.
     */
    @Override
    public final <E extends SymbolTableEntry> List<E> getMatches(
            TableSearcher<E> searcher, SearchContext l)
            throws DuplicateSymbolException {
        List<E> result = new LinkedList<>();
        Set<Scope> searchedScopes = new HashSet<>();
        Map<String, PTType> genericInstantiations =
                new HashMap<>();

        addMatches(searcher, result, searchedScopes, genericInstantiations, null, l);

        return result;
    }
}