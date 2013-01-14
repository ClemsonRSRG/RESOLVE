package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.Scope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.List;

/**
 * <p>Refines {@link SymbolQuery SymbolQuery} by guaranteeing that 
 * {@link searchFromContext(Scope, ScopeRepository) searchFromContext()} will
 * not throw a {@link DuplicateSymbolException DuplicateSymbolException}.</p>
 */
public interface MultimatchSymbolQuery<E extends SymbolTableEntry>
        extends
            SymbolQuery<E> {

    /**
     * <p>Behaves just as 
     * {@link SymbolQuery#searchFromContext(Scope, ScopeRepository) 
     * SymbolQuery.searchFromContext()}, except that it cannot throw a
     * {@link DuplicateSymbolException DuplicateSymbolException}.</p>
     */
    @Override
    public List<E> searchFromContext(Scope source, ScopeRepository repo);
}
