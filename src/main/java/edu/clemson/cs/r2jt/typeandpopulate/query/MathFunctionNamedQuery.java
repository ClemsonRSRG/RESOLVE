/*
 * MathFunctionNamedQuery.java
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
import edu.clemson.cs.r2jt.misc.SourceErrorException;

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
