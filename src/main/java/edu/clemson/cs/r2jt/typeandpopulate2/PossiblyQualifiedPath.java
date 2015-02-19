/**
 * PossiblyQualifiedPath.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.TableSearcher;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class PossiblyQualifiedPath implements ScopeSearchPath {

    private final ScopeSearchPath myActualSearchPath;

    public PossiblyQualifiedPath(Token qualifier,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        myActualSearchPath =
                getAppropriatePath(qualifier, importStrategy, facilityStrategy,
                        localPriority);
    }

    public PossiblyQualifiedPath(Token qualifier) {
        this(qualifier, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }

    @Override
    public <E extends SymbolTableEntry> List<E> searchFromContext(
            TableSearcher<E> searcher, Scope source, ScopeRepository repo)
            throws DuplicateSymbolException {
        return myActualSearchPath.searchFromContext(searcher, source, repo);
    }

    private static ScopeSearchPath getAppropriatePath(Token qualifier,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        ScopeSearchPath result;

        if (qualifier == null) {
            result =
                    new UnqualifiedPath(importStrategy, facilityStrategy,
                            localPriority);
        }
        else {
            result = new QualifiedPath(qualifier, facilityStrategy);
        }
        return result;
    }
}
