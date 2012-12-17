package edu.clemson.cs.r2jt.mathtype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractScope implements Scope {

    @Override
    public final <E extends SymbolTableEntry> List<E> getMatches(
            TableSearcher<E> searcher) throws DuplicateSymbolException {
        List<E> result = new LinkedList<E>();
        Set<Scope> searchedScopes = new HashSet<Scope>();
        Map<String, PTType> genericInstantiations =
                new HashMap<String, PTType>();

        addMatches(searcher, result, searchedScopes, genericInstantiations,
                null);

        return result;
    }
}
