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
import edu.clemson.cs.r2jt.typeandpopulate.Scope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>An implementation of {@link SymbolQuery SymbolQuery} that decorates an
 * existing <code>SymbolQuery</code>, post processing its results and returning
 * the processed set of results.</p>
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
