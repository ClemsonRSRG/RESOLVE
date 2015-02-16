/**
 * QualifiedPath.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.misc.SrcErrorException;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.TableSearcher;
import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * <p>Defines the search path when a symbol is fully qualified.  Namely:</p>
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
 */
public class QualifiedPath implements ScopeSearchPath {

    private final FacilityStrategy myFacilityStrategy;
    private final Token myQualifier;

    /**
     *
     * @param qualifier
     * @param facilityStrategy The FACILITY_IGNORE strategy is not permitted.
     */
    public QualifiedPath(Token qualifier, FacilityStrategy facilityStrategy) {

        if (facilityStrategy == FacilityStrategy.FACILITY_IGNORE) {
            throw new IllegalArgumentException("Can't use FACILITY_IGNORE");
        }

        myQualifier = qualifier;
        myFacilityStrategy = facilityStrategy;
    }

    @Override
    public <E extends SymbolTableEntry> List<E> searchFromContext(
            TableSearcher<E> searcher, Scope source, ScopeRepository repo)
            throws DuplicateSymbolException {

        List<E> result;

        try {
            //Note that this will throw the appropriate SourceErrorException if
            //the returned symbol identifies anything other than a facility
            FacilityEntry facility =
                    source.queryForOne(
                            new UnqualifiedNameQuery(myQualifier.getText()))
                            .toFacilityEntry(myQualifier);

            Scope facilityScope =
                    facility
                            .getFacility()
                            .getSpecification()
                            .getScope(
                                    myFacilityStrategy == FacilityStrategy.FACILITY_INSTANTIATE);

            result = facilityScope.getMatches(searcher, TableSearcher.SearchContext.FACILITY);
        }
        catch (NoSuchSymbolException nsse) {
            //There's nothing by that name in local scope, so it must be the
            //name of a module
            try {
                ModuleScope moduleScope =
                        repo.getModuleScope(new ModuleIdentifier(myQualifier
                                .getText()));

                result = moduleScope.getMatches(searcher,
                        TableSearcher.SearchContext.IMPORT);
            }
            catch (NoSuchSymbolException nsse2) {
                throw new SrcErrorException("no such facility or a module",
                        myQualifier);
            }
        }
        catch (DuplicateSymbolException dse) {
            //Not possible--UnqualifiedNameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

}
