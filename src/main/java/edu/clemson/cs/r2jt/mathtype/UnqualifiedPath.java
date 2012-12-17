package edu.clemson.cs.r2jt.mathtype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.ImportStrategy;

/**
 * <p>Defines the search path used when a symbol is referenced in an 
 * unqualified way, along with some parameters for tweaking how the search is
 * accomplished.  In general, the path is as follows:</p>
 * 
 * <ol>
 * 		<li>Search the local scope.</li>
 * 		<li>Search any facilities declared in the local scope.</li>
 * 		<li>Search any imports in a depth-first manner, skipping any 
 * 		    already-searched scopes.</li>
 * 		<ul>
 * 			<li>For each searched import, search any facilities declared
 * 			    inside.</li>
 * 		</ul>
 * </ol>
 *
 * <p>Instance of this class can be parameterized to search only direct imports
 * or to exclude all imports, as well as to exclude searching facilities, or 
 * change how generics are handled when searching facilities.</p>
 * 
 * <p>Additionally, by setting the <code>localPriority</code> flag, the search
 * can be made to stop without considering imports (regardless of the import
 * strategy) if at least one local match is found.  Note that any local 
 * facilities will still be searched if the facility strategy requires it.</p>
 */
public class UnqualifiedPath implements ScopeSearchPath {

    private final ImportStrategy myImportStrategy;
    private final FacilityStrategy myFacilityStrategy;
    private final boolean myLocalPriorityFlag;

    public UnqualifiedPath(ImportStrategy imports, FacilityStrategy facilities,
            boolean localPriority) {

        myImportStrategy = imports;
        myFacilityStrategy = facilities;
        myLocalPriorityFlag = localPriority;
    }

    @Override
    public <E extends SymbolTableEntry> List<E> searchFromContext(
            TableSearcher<E> searcher, Scope source, ScopeRepository repo)
            throws DuplicateSymbolException {

        List<E> result = new LinkedList<E>();
        Set<Scope> searchedScopes = new HashSet<Scope>();
        Map<String, PTType> genericInstantiations =
                new HashMap<String, PTType>();

        searchModule(searcher, source, repo, result, searchedScopes,
                genericInstantiations, null, myImportStrategy, 0);

        return result;
    }

    private <E extends SymbolTableEntry> boolean searchModule(
            TableSearcher<E> searcher, Scope source, ScopeRepository repo,
            List<E> results, Set<Scope> searchedScopes,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility, ImportStrategy importStrategy,
            int depth) throws DuplicateSymbolException {

        //First we search locally
        boolean finished =
                source.addMatches(searcher, results, searchedScopes,
                        genericInstantiations, instantiatingFacility);

        //Next, if requested, we search any local facilities.
        if (!finished && myFacilityStrategy != FacilityStrategy.FACILITY_IGNORE) {

            finished =
                    searchFacilities(searcher, results, source,
                            genericInstantiations, searchedScopes, repo);
        }

        //Finally, if requested, we search imports
        if ((results.size() == 0 || !myLocalPriorityFlag)
                && source instanceof SyntacticScope
                && myImportStrategy != ImportStrategy.IMPORT_NONE) {

            SyntacticScope sourceAsSyntacticScope = (SyntacticScope) source;

            try {
                ModuleScope module =
                        repo.getModuleScope(sourceAsSyntacticScope
                                .getRootModule());
                List<ModuleIdentifier> imports = module.getImports();

                Iterator<ModuleIdentifier> importsIter = imports.iterator();
                Scope importScope;
                while (!finished && importsIter.hasNext()) {
                    importScope = repo.getModuleScope(importsIter.next());

                    finished =
                            searchModule(searcher, importScope, repo, results,
                                    searchedScopes, genericInstantiations,
                                    instantiatingFacility, importStrategy
                                            .cascadingStrategy(), depth + 1);
                }
            }
            catch (NoSuchSymbolException nsse) {
                //This shouldn't be possible--we'd've caught it by now
                throw new RuntimeException(nsse);
            }
        }

        return finished;
    }

    public <E extends SymbolTableEntry> boolean searchFacilities(
            TableSearcher<E> searcher, List<E> result, Scope source,
            Map<String, PTType> genericInstantiations,
            Set<Scope> searchedScopes, ScopeRepository repo)
            throws DuplicateSymbolException {

        List<FacilityEntry> facilities =
                source.getMatches(EntryTypeSearcher.FACILITY_SEARCHER);

        Map<String, PTType> inductiveInstantiations = genericInstantiations;
        FacilityEntry facility;

        boolean finished = false;
        Iterator<FacilityEntry> facilitiesIter = facilities.iterator();
        ModuleParameterization facilityConcept;
        Scope facilityScope;
        while (!finished && facilitiesIter.hasNext()) {
            facility = facilitiesIter.next();
            facilityConcept = facility.getFacility().getSpecification();

            facilityScope =
                    facilityConcept
                            .getScope(myFacilityStrategy == FacilityStrategy.FACILITY_INSTANTIATE);

            finished =
                    facilityScope.addMatches(searcher, result, searchedScopes,
                            new HashMap<String, PTType>(), null);
        }

        return finished;
    }
}
