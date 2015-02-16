package edu.clemson.cs.r2jt.typeandpopulate2.query;

import edu.clemson.cs.r2jt.typeandpopulate2.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate2.Scope;
import edu.clemson.cs.r2jt.typeandpopulate2.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;

import java.util.List;

/**
 * <p>Refines {@link SymbolQuery} by guaranteeing that
 * {@link #searchFromContext(Scope, ScopeRepository)} will not throw a
 * {@link DuplicateSymbolException}.</p>
 */
public interface MultimatchSymbolQuery<E extends SymbolTableEntry>
        extends
            SymbolQuery<E> {
    /**
     * <p>Behaves just as
     * {@link SymbolQuery#searchFromContext(Scope, ScopeRepository)
     * SymbolQuery.searchFromContext()}, except that it cannot throw a
     * {@link DuplicateSymbolException}.</p>
     */
    @Override
    public List<E> searchFromContext(Scope source, ScopeRepository repo);
}