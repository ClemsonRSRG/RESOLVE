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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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

import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.Scope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

public class MathFunctionNamedQuery
        implements
            MultimatchSymbolQuery<MathSymbolEntry> {

    private final SymbolQuery<SymbolTableEntry> myNameQuery;

    public MathFunctionNamedQuery(PosSymbol qualifier, PosSymbol name) {
        myNameQuery =
                new NameQuery(qualifier, name, ImportStrategy.IMPORT_RECURSIVE,
                        FacilityStrategy.FACILITY_IGNORE, false);
    }

    @Override
    public List<MathSymbolEntry> searchFromContext(Scope source,
            ScopeRepository repo) {

        List<SymbolTableEntry> intermediateList;
        try {
            intermediateList = myNameQuery.searchFromContext(source, repo);
        }
        catch (DuplicateSymbolException dse) {
            //Shouldn't be possible
            throw new RuntimeException(dse);
        }

        List<MathSymbolEntry> finalList = new LinkedList<MathSymbolEntry>();
        for (SymbolTableEntry intermediateEntry : intermediateList) {
            try {
                finalList.add(intermediateEntry.toMathSymbolEntry(null));
            }
            catch (SourceErrorException see) {
                //This is ok, just don't add it to the final list
            }
        }

        return finalList;
    }
}
