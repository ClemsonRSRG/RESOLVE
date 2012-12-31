package edu.clemson.cs.r2jt.mathtype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.utilities.ChainedMaps;

/**
 * <p>An <code>InstantiatedScope</code> decorates an existing 
 * {@link Scope Scope} such that calls to {@link Scope#addMatches addMatches()},
 * the search method to which all others defer, are augmented with an additional
 * set of generic instantiations and an instantiating facility.</p>
 */
public class InstantiatedScope implements Scope {

    private final Scope myBaseScope;
    private final FacilityEntry myInstantiatingFacility;
    private final Map<String, PTType> myAdditionalGenericInstantiations =
            new HashMap<String, PTType>();

    public InstantiatedScope(Scope baseScope,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        myBaseScope = baseScope;
        myAdditionalGenericInstantiations.putAll(genericInstantiations);
        myInstantiatingFacility = instantiatingFacility;
    }

    @Override
    public <E extends SymbolTableEntry> List<E> query(
            MultimatchSymbolQuery<E> query) {
        return myBaseScope.query(query);
    }

    @Override
    public <E extends SymbolTableEntry> E queryForOne(SymbolQuery<E> query)
            throws NoSuchSymbolException,
                DuplicateSymbolException {
        return myBaseScope.queryForOne(query);
    }

    @Override
    public <E extends SymbolTableEntry> boolean addMatches(
            TableSearcher<E> searcher, List<E> matches,
            Set<Scope> searchedScopes,
            Map<String, PTType> genericInstantiations,
            FacilityEntry facilityInstantiation)
            throws DuplicateSymbolException {

        if (facilityInstantiation != null) {
            //It's unclear how this could happen or what it would mean, so we
            //fail fast.  If an example triggers this, we need to think 
            //carefully about what it would mean.
            throw new RuntimeException("Duplicate instantiation???");
        }

        return myBaseScope.addMatches(searcher, matches, searchedScopes,
                myAdditionalGenericInstantiations, myInstantiatingFacility);
    }

    @Override
    public <E extends SymbolTableEntry> List<E> getMatches(
            TableSearcher<E> searcher) throws DuplicateSymbolException {
        return myBaseScope.getMatches(searcher);
    }

    @Override
    public List<ProgramParameterEntry> getFormalParameterEntries() {
        return myBaseScope.getFormalParameterEntries();
    }

}
