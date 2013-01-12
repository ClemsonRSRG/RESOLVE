package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.MultimatchTableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.Scope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeSearchPath;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.List;

/**
 * <p>Refines {@link BaseSymbolQuery BaseSymbolQuery} by guaranteeing that the 
 * associated searcher is a {@link MultimatchTableSearch MultimatchTableSearch},
 * and thus the search methods of this class are guaranteed not to throw a
 * {@link DuplicateSymbolException DuplicateSymbolException}.</p>
 */
public class BaseMultimatchSymbolQuery<E extends SymbolTableEntry>
        extends
            BaseSymbolQuery<E> {

    public BaseMultimatchSymbolQuery(ScopeSearchPath path,
            MultimatchTableSearcher<E> searcher) {
        super(path, searcher);
    }

    /**
     * <p>Refines {@link 
     * BaseSymbolQuery#searchFromContext(Scope, ScopeRepository)
     * BaseSymbolQuery.searchFromContext()} to guarantee that it will not
     * throw a {@link DuplicateSymbolException DuplicateSymbolException}.
     * Otherwise, behaves identically.</p>
     */
    public List<E> searchFromContext(Scope source, ScopeRepository repo) {

        List<E> result;

        try {
            result = super.searchFromContext(source, repo);
        }
        catch (DuplicateSymbolException dse) {
            //Not possible.  We know our searcher is, in fact, a 
            //MultimatchTableSearch
            throw new RuntimeException(dse);
        }

        return result;
    }
}
