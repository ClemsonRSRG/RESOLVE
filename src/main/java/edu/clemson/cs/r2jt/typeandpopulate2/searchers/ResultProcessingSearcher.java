/**
 * ResultProcessingSearcher.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2.searchers;

import edu.clemson.cs.r2jt.misc.Utils.Mapping;
import edu.clemson.cs.r2jt.typeandpopulate2.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate2.SymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;

import java.util.LinkedList;
import java.util.List;

public class ResultProcessingSearcher<T extends SymbolTableEntry, R extends SymbolTableEntry>
        implements
            TableSearcher<R> {

    private final TableSearcher<T> myBaseSearcher;
    private final Mapping<T, R> myMapping;

    public ResultProcessingSearcher(TableSearcher<T> baseSearcher,
            Mapping<T, R> processing) {
        myBaseSearcher = baseSearcher;
        myMapping = processing;
    }

    @Override
    public boolean addMatches(SymbolTable entries, List<R> matches,
            SearchContext l) throws DuplicateSymbolException {

        boolean result;

        List<T> intermediateList = new LinkedList<T>();

        result = myBaseSearcher.addMatches(entries, intermediateList, l);

        for (T match : intermediateList) {
            matches.add(myMapping.map(match));
        }

        return result;
    }

}
