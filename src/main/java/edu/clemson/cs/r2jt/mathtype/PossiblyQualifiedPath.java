package edu.clemson.cs.r2jt.mathtype;

import java.util.List;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.ImportStrategy;

public class PossiblyQualifiedPath implements ScopeSearchPath {

    private final ScopeSearchPath myActualSearchPath;

    public PossiblyQualifiedPath(PosSymbol qualifier,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {

        myActualSearchPath =
                getAppropriatePath(qualifier, importStrategy, facilityStrategy,
                        localPriority);
    }

    public PossiblyQualifiedPath(PosSymbol qualifier) {
        this(qualifier, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }

    @Override
    public <E extends SymbolTableEntry> List<E> searchFromContext(
            TableSearcher<E> searcher, Scope source, ScopeRepository repo)
            throws DuplicateSymbolException {

        return myActualSearchPath.searchFromContext(searcher, source, repo);
    }

    private static ScopeSearchPath getAppropriatePath(PosSymbol qualifier,
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
