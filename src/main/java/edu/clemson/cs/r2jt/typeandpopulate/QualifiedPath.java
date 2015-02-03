/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher.SearchContext;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;
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
    private final PosSymbol myQualifier;

    /**
     * 
     * @param qualifier
     * @param facilityStrategy The FACILITY_IGNORE strategy is not permitted.
     */
    public QualifiedPath(PosSymbol qualifier, FacilityStrategy facilityStrategy) {

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
                            new UnqualifiedNameQuery(myQualifier.getName()))
                            .toFacilityEntry(myQualifier.getLocation());

            Scope facilityScope =
                    facility
                            .getFacility()
                            .getSpecification()
                            .getScope(
                                    myFacilityStrategy == FacilityStrategy.FACILITY_INSTANTIATE);

            result = facilityScope.getMatches(searcher, SearchContext.FACILITY);
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
