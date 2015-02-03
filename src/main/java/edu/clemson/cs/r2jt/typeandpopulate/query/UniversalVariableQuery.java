/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
