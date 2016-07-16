/**
 * UnqualifiedPath.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.query.searchpath;

import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.EntryTypeSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher.SearchContext;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.*;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.*;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleParameterization;
import java.util.*;

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
 *
 * @version 2.0
 */
public class UnqualifiedPath implements ScopeSearchPath {

    /** <p>The import strategy to use.</p> */
    private final ImportStrategy myImportStrategy;

    /** <p>The facility strategy to use.</p> */
    private final FacilityStrategy myFacilityStrategy;

    /** <p>Boolean flag that indicates whether or not local items have priority.</p> */
    private final boolean myLocalPriorityFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>A search path for searching entries that are not qualified.</p>
     *
     * @param imports The import strategy to use.
     * @param facilities The facility strategy to use.
     * @param localPriority Boolean flag that indicates whether or not
     *                      local items have priority.
     */
    public UnqualifiedPath(ImportStrategy imports, FacilityStrategy facilities,
            boolean localPriority) {
        myImportStrategy = imports;
        myFacilityStrategy = facilities;
        myLocalPriorityFlag = localPriority;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Applies the given {@link TableSearcher} to the
     * appropriate {@link Scope}s, given a source scope and a
     * {@link ScopeRepository} containing any imports, returning
     * a list of matching {@link SymbolTableEntry}s.</p>
     *
     * <p>If there are no matches, returns an empty list. If more than one
     * match is found and <code>searcher</code> expects no more than one match,
     * throws a {@link DuplicateSymbolException}.</p>
     *
     * @param searcher A <code>TableSearcher</code> to apply to each scope along
     *                 the search path.
     * @param source The current scope from which the search was spawned.
     * @param repo A collection of scopes.
     *
     * @return A list of matches.
     */
    @Override
    public final <E extends SymbolTableEntry> List<E> searchFromContext(
            TableSearcher<E> searcher, Scope source, ScopeRepository repo)
            throws DuplicateSymbolException {

        List<E> result = new LinkedList<>();
        Set<Scope> searchedScopes = new HashSet<>();
        Map<String, PTType> genericInstantiations =
                new HashMap<>();

        searchModule(searcher, source, repo, result, searchedScopes,
                genericInstantiations, null, myImportStrategy, 0);

        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This method searches all the instantiated {@code Facilities} in scope and attempts to
     * find all entries that match and store those in the {@code result} list.</p>
     *
     * <p>If more than one match is found and <code>searcher</code> expects no
     * more than one match, throws a {@link DuplicateSymbolException}.</p>
     *
     * @param searcher A <code>TableSearcher</code> to apply to each scope along
     *                 the search path.
     * @param result List of matches.
     * @param source The current scope from which the search was spawned.
     * @param genericInstantiations Map containing all the instantiations.
     * @param searchedScopes Set of scopes searched.
     * @param <E> Entry type.
     *
     * @return {@code true} if we are done searching, {@code false} otherwise.
     */
    private <E extends SymbolTableEntry> boolean searchFacilities(
            TableSearcher<E> searcher, List<E> result, Scope source,
            Map<String, PTType> genericInstantiations, Set<Scope> searchedScopes)
            throws DuplicateSymbolException {

        List<FacilityEntry> facilities =
                source.getMatches(EntryTypeSearcher.FACILITY_SEARCHER,
                        SearchContext.SOURCE_MODULE);

        FacilityEntry facility;

        boolean finished = false;
        Iterator<FacilityEntry> facilitiesIter = facilities.iterator();
        ModuleParameterization facilityConcept;
        Scope facilityScope;
        while (!finished && facilitiesIter.hasNext()) {
            facility = facilitiesIter.next();
            facilityConcept = facility.getFacility().getSpecification();

            facilityScope =
                    facilityConcept.getScope(myFacilityStrategy
                            .equals(FacilityStrategy.FACILITY_INSTANTIATE));

            finished =
                    facilityScope
                            .addMatches(searcher, result, searchedScopes,
                                    genericInstantiations, null,
                                    SearchContext.FACILITY);

            // YS Edits
            // Search any enhancements in this facility declaration
            if (!finished) {
                List<ModuleParameterization> enhancementList =
                        facility.getEnhancements();
                for (ModuleParameterization facEnh : enhancementList) {
                    // Obtain the scope for the enhancement
                    facilityScope =
                            facEnh
                                    .getScope(myFacilityStrategy
                                            .equals(FacilityStrategy.FACILITY_INSTANTIATE));

                    // Search and add matches.
                    finished =
                            facilityScope.addMatches(searcher, result,
                                    searchedScopes, genericInstantiations,
                                    null, SearchContext.FACILITY);
                }
            }
        }

        return finished;
    }

    /**
     * <p>This method searches all the {@code Modules} in scope and attempts to
     * find all entries that match and store those in the {@code result} list.</p>
     *
     * <p>If more than one match is found and <code>searcher</code> expects no
     * more than one match, throws a {@link DuplicateSymbolException}.</p>
     *
     * @param searcher A <code>TableSearcher</code> to apply to each scope along
     *                 the search path.
     * @param source The current scope from which the search was spawned.
     * @param repo A collection of scopes.
     * @param results List of matches.
     * @param searchedScopes Set of scopes searched.
     * @param genericInstantiations Map containing all the instantiations.
     * @param instantiatingFacility Facility that instantiated this class.
     * @param importStrategy An import strategy.
     * @param depth Integer flag that indicates how deep we search.
     * @param <E> Entry type.
     *
     * @return {@code true} if we are done searching, {@code false} otherwise.
     */
    private <E extends SymbolTableEntry> boolean searchModule(
            TableSearcher<E> searcher, Scope source, ScopeRepository repo,
            List<E> results, Set<Scope> searchedScopes,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility, ImportStrategy importStrategy,
            int depth) throws DuplicateSymbolException {

        //First we search locally
        boolean finished =
                source.addMatches(searcher, results, searchedScopes,
                        genericInstantiations, instantiatingFacility,
                        SearchContext.SOURCE_MODULE);

        //Next, if requested, we search any local facilities.
        if (!finished && myFacilityStrategy != FacilityStrategy.FACILITY_IGNORE) {

            finished =
                    searchFacilities(searcher, results, source,
                            genericInstantiations, searchedScopes);
        }

        //Finally, if requested, we search imports
        if ((results.isEmpty() || !myLocalPriorityFlag)
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

}