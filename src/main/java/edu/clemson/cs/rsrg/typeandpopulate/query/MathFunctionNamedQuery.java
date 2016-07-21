/**
 * MathFunctionNamedQuery.java
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
package edu.clemson.cs.rsrg.typeandpopulate.query;

import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeRepository;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>A <code>MathFunctionNamedQuery</code> takes a (possibly-null) qualifier and a name
 * and searches for all {@link MathSymbolEntry} that match. If the qualifier is non-null, the
 * appropriate facility or module is searched. If it <em>is</em> null, a
 * search is performed using a recursive <code>ImportStrategy</code> and
 * ignores all <code>FacilityStrategy</code>.</p>
 *
 * @version 2.0
 */
public class MathFunctionNamedQuery
        implements
            MultimatchSymbolQuery<MathSymbolEntry> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The actual query that is going to perform the searching.</p> */
    private final MultimatchSymbolQuery<SymbolTableEntry> myNameQuery;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This query searches for all {@link MathSymbolEntry} that match the given name
     * represented as a {@link PosSymbol} using a recursive import strategy, no facility strategy
     * and no local priority.</p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *                  facility or module.
     * @param name A name symbol of the entry to be searched.
     */
    public MathFunctionNamedQuery(PosSymbol qualifier, PosSymbol name) {
        myNameQuery =
                new NameQuery(qualifier, name, ImportStrategy.IMPORT_RECURSIVE,
                        FacilityStrategy.FACILITY_IGNORE, false);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Behaves just as {@link SymbolQuery#searchFromContext(Scope, ScopeRepository)},
     * except that it cannot throw a {@link DuplicateSymbolException}.</p>
     *
     * @param source The source scope from which the search was spawned.
     * @param repo A repository of any referenced modules.
     *
     * @return A list of matches.
     */
    @Override
    public final List<MathSymbolEntry> searchFromContext(Scope source, ScopeRepository repo) {
        List<SymbolTableEntry> intermediateList = myNameQuery.searchFromContext(source, repo);

        List<MathSymbolEntry> finalList = new LinkedList<>();
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