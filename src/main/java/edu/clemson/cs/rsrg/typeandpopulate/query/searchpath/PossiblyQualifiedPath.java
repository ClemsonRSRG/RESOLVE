/*
 * PossiblyQualifiedPath.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.query.searchpath;

import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeRepository;
import java.util.List;

/**
 * <p>
 * This class attempts to figure out either to use {@link QualifiedPath} when a
 * symbol is fully
 * qualified or use {@link UnqualifiedPath} when it is not.
 * </p>
 *
 * @version 2.0
 */
public class PossiblyQualifiedPath implements ScopeSearchPath {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This is the actual search path we are going to use.
     * </p>
     */
    private final ScopeSearchPath myActualSearchPath;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * A search path for searching entries that could potentially be qualified.
     * </p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *        facility or module.
     * @param importStrategy The import strategy to use.
     * @param facilityStrategy The facility strategy to use.
     * @param localPriority Boolean flag that indicates whether or not local
     *        items have priority.
     */
    public PossiblyQualifiedPath(PosSymbol qualifier,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        myActualSearchPath = getAppropriatePath(qualifier, importStrategy,
                facilityStrategy, localPriority);
    }

    /**
     * <p>
     * A search path for searching entries that could potentially be qualified.
     * </p>
     *
     * <p>
     * <em>Note:</em> This search path uses {@code IMPORT_NONE} and
     * {@code FACILITY_IGNORE} strategies
     * and local entries do not have priority.
     * </p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *        facility or module.
     */
    public PossiblyQualifiedPath(PosSymbol qualifier) {
        this(qualifier, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Applies the given {@link TableSearcher} to the appropriate
     * {@link Scope}s, given a source scope
     * and a {@link ScopeRepository} containing any imports, returning a list of
     * matching
     * {@link SymbolTableEntry}s.
     * </p>
     *
     * <p>
     * If there are no matches, returns an empty list. If more than one match is
     * found and
     * <code>searcher</code> expects no more than one match, throws a
     * {@link DuplicateSymbolException}.
     * </p>
     *
     * @param searcher A <code>TableSearcher</code> to apply to each scope along
     *        the search path.
     * @param source The current scope from which the search was spawned.
     * @param repo A collection of scopes.
     *
     * @return A list of matches.
     */
    @Override
    public final <E extends SymbolTableEntry> List<E> searchFromContext(
            TableSearcher<E> searcher, Scope source, ScopeRepository repo)
            throws DuplicateSymbolException {

        return myActualSearchPath.searchFromContext(searcher, source, repo);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * A static helper method that helps this class decide either to use a
     * {@link QualifiedPath} or a
     * {@link UnqualifiedPath} as its search path.
     * </p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *        facility or module.
     * @param importStrategy The import strategy to use.
     * @param facilityStrategy The facility strategy to use.
     * @param localPriority Boolean flag that indicates whether or not local
     *        items have priority.
     *
     * @return The actual search path to use.
     */
    private static ScopeSearchPath getAppropriatePath(PosSymbol qualifier,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        ScopeSearchPath result;

        if (qualifier == null) {
            result = new UnqualifiedPath(importStrategy, facilityStrategy,
                    localPriority);
        }
        else {
            result = new QualifiedPath(qualifier, facilityStrategy);
        }

        return result;
    }

}
