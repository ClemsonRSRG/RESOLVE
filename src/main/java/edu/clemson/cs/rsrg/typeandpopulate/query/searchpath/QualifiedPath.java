/*
 * QualifiedPath.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.query.searchpath;

import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher.SearchContext;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeRepository;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleParameterization;
import java.util.List;

/**
 * <p>Defines the search path when a symbol is fully qualified. Namely:</p>
 *
 * <ul>
 * 		<li>If the qualifier matches a facility defined in the same module as
 *          the source scope, open the corresponding module and search for the
 *          symbol there.</li>
 *      <li>Otherwise, look for a module with that name and search there.</li>
 * </ul>
 *
 * <p>Instances of this class can be parameterized to determine how generics are
 * handled if the qualifier refers to a facility.</p>
 *
 * @version 2.0
 */
public class QualifiedPath implements ScopeSearchPath {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The facility strategy to use.</p> */
    private final FacilityStrategy myFacilityStrategy;

    /** <p>A qualifier symbol that indicates the instantiating facility or module.</p> */
    private final PosSymbol myQualifier;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>A search path for searching entries that are qualified.</p>
     *
     * <p><em>Note:</em> The {@code FACILITY_IGNORE} strategy is not permitted.</p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *                  facility or module.
     * @param facilityStrategy The facility strategy to use.
     */
    public QualifiedPath(PosSymbol qualifier, FacilityStrategy facilityStrategy) {
        if (facilityStrategy == FacilityStrategy.FACILITY_IGNORE) {
            throw new IllegalArgumentException("Can't use FACILITY_IGNORE");
        }

        if (qualifier == null) {
            throw new IllegalArgumentException("Qualifier can't be null!");
        }

        myQualifier = qualifier;
        myFacilityStrategy = facilityStrategy;
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
        List<E> result;

        try {
            //Note that this will throw the appropriate SourceErrorException if
            //the returned symbol identifies anything other than a facility
            FacilityEntry facility =
                    source.queryForOne(
                            new UnqualifiedNameQuery(myQualifier.getName()))
                            .toFacilityEntry(myQualifier.getLocation());

            Scope facilityScope =
                    facility
                            .getFacility()
                            .getSpecification()
                            .getScope(
                                    myFacilityStrategy == FacilityStrategy.FACILITY_INSTANTIATE);

            result = facilityScope.getMatches(searcher, SearchContext.FACILITY);

            // YS Edits
            // Search any enhancements in this facility declaration
            if (result.size() == 0) {
                List<ModuleParameterization> enhancementList =
                        facility.getEnhancements();

                List<E> tempResult;
                for (ModuleParameterization facEnh : enhancementList) {
                    // Obtain the scope for the enhancement
                    facilityScope =
                            facEnh
                                    .getScope(myFacilityStrategy
                                            .equals(FacilityStrategy.FACILITY_INSTANTIATE));

                    // Search for matches
                    tempResult =
                            facilityScope.getMatches(searcher,
                                    SearchContext.FACILITY);

                    // Check to see if we have results or not
                    if (tempResult.size() != 0) {
                        if (result.size() == 0) {
                            result = tempResult;
                        }
                        else {
                            // Found more than one
                            throw new DuplicateSymbolException(
                                    "Found two matching entries!", result
                                            .get(1));
                        }
                    }
                }
            }
        }
        catch (NoSuchSymbolException nsse) {
            //There's nothing by that name in local scope, so it must be the
            //name of a module
            try {
                ModuleScope moduleScope =
                        repo.getModuleScope(new ModuleIdentifier(myQualifier
                                .getName()));

                result = moduleScope.getMatches(searcher, SearchContext.IMPORT);
            }
            catch (NoSuchSymbolException nsse2) {
                throw new SourceErrorException("No such facility or a module.",
                        myQualifier.getLocation());
            }
        }
        catch (DuplicateSymbolException dse) {
            //Not possible--UnqualifiedNameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

}