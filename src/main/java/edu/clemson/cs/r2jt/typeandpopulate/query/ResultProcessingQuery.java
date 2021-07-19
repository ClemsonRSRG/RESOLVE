/*
 * ResultProcessingQuery.java
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
package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.Scope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.misc.Utils.Mapping;

/**
 * <p>
 * An implementation of {@link SymbolQuery SymbolQuery} that decorates an
 * existing
 * <code>SymbolQuery</code>, post processing its results and returning the
 * processed set of results.
 * </p>
 *
 * @param <T> The return type of the base <code>SymbolQuery</code>.
 * @param <R> The return type of the resultant, processed entries.
 */
public class ResultProcessingQuery<T extends SymbolTableEntry, R extends SymbolTableEntry>
        implements
            SymbolQuery<R> {

    private final SymbolQuery<T> myBaseQuery;
    private final Mapping<T, R> myMapping;

    public ResultProcessingQuery(SymbolQuery<T> baseQuery,
            Mapping<T, R> processing) {

        myBaseQuery = baseQuery;
        myMapping = processing;
    }

    @Override
    public List<R> searchFromContext(Scope source, ScopeRepository repo)
            throws DuplicateSymbolException {

        List<T> intermediateMatches =
                myBaseQuery.searchFromContext(source, repo);

        List<R> finalMatches = new LinkedList<R>();
        for (T intermediateMatch : intermediateMatches) {
            finalMatches.add(myMapping.map(intermediateMatch));
        }

        return finalMatches;
    }
}
