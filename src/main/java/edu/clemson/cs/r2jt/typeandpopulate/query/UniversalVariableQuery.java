/**
 * UniversalVariableQuery.java
 * ---------------------------------
 * Copyright (c) 2016
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
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.Scope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.SymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.UnqualifiedPath;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.MultimatchTableSearcher;
import java.util.Iterator;
import java.util.List;

public class UniversalVariableQuery
        implements
            MultimatchSymbolQuery<MathSymbolEntry> {

    public static final MultimatchSymbolQuery<MathSymbolEntry> INSTANCE =
            (MultimatchSymbolQuery<MathSymbolEntry>) new UniversalVariableQuery();

    private final BaseSymbolQuery<MathSymbolEntry> myBaseQuery;

    private UniversalVariableQuery() {
        myBaseQuery =
                new BaseSymbolQuery<MathSymbolEntry>(new UnqualifiedPath(
                        ImportStrategy.IMPORT_NONE,
                        FacilityStrategy.FACILITY_IGNORE, false),
                        new UniversalVariableSearcher());
    }

    @Override
    public List<MathSymbolEntry> searchFromContext(Scope source,
            ScopeRepository repo) {

        List<MathSymbolEntry> result;
        try {
            result = myBaseQuery.searchFromContext(source, repo);
        }
        catch (DuplicateSymbolException dse) {
            //Can't happen--our base query is a name matcher
            throw new RuntimeException(dse);
        }

        return result;
    }

    private static class UniversalVariableSearcher
            implements
                MultimatchTableSearcher<MathSymbolEntry> {

        @Override
        public boolean addMatches(SymbolTable entries,
                List<MathSymbolEntry> matches, SearchContext l) {

            Iterator<MathSymbolEntry> mathSymbols =
                    entries.iterateByType(MathSymbolEntry.class);

            MathSymbolEntry curSymbol;
            while (mathSymbols.hasNext()) {
                curSymbol = mathSymbols.next();

                if (curSymbol.getQuantification() == SymbolTableEntry.Quantification.UNIVERSAL) {
                    matches.add(curSymbol);
                }
            }

            return false;
        }
    }
}
