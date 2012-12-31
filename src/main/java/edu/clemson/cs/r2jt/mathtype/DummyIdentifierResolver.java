package edu.clemson.cs.r2jt.mathtype;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.FacilityStrategy;

public class DummyIdentifierResolver extends AbstractScope {

    @Override
    public List<ProgramParameterEntry> getFormalParameterEntries() {
        return Collections.emptyList();
    }

    @Override
    public <E extends SymbolTableEntry> List<E> query(
            MultimatchSymbolQuery<E> query) {

        return new LinkedList<E>();
    }

    @Override
    public <E extends SymbolTableEntry> E queryForOne(SymbolQuery<E> query)
            throws NoSuchSymbolException,
                DuplicateSymbolException {

        throw new NoSuchSymbolException();
    }

    @Override
    public <E extends SymbolTableEntry> boolean addMatches(
            TableSearcher<E> searcher, List<E> matches,
            Set<Scope> searchedScopes,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility)
            throws DuplicateSymbolException {

        return false;
    }
}
