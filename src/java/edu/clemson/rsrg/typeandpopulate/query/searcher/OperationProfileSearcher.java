/*
 * OperationProfileSearcher.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.query.searcher;

import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.prover.immutableadts.ImmutableList;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.rsrg.typeandpopulate.entry.OperationProfileEntry;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.symboltables.SymbolTable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * An <code>OperationSearcher</code> returns entries in a {@link SymbolTable} that have a performance profile with the
 * specified operation name and the expected list of parameter types.
 * </p>
 *
 * @version 2.0
 */
public class OperationProfileSearcher implements TableSearcher<OperationProfileEntry> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An operation name to query for.
     * </p>
     */
    private final String myQueryName;

    /**
     * <p>
     * The location where we obtained the name to query.
     * </p>
     */
    private final Location myQueryLocation;

    /**
     * <p>
     * The list of program types for this operation.
     * </p>
     */
    private final List<PTType> myActualArgumentTypes;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a searcher that specifies a search string for a performance profile for an operation and a list
     * of program types for the operation.
     * </p>
     *
     * @param name
     *            An operation name to query for.
     * @param argumentTypes
     *            The list of program types for this operation.
     */
    public OperationProfileSearcher(PosSymbol name, List<PTType> argumentTypes) {
        myQueryName = name.getName();
        myQueryLocation = name.getLocation();
        myActualArgumentTypes = new LinkedList<>(argumentTypes);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Adds any symbol table entries from <code>entries</code> that match this search to <code>matches</code>. The order
     * that they are added is determined by the concrete base-class.
     * </p>
     *
     * <p>
     * If no matches exist, the method will simply leave <code>matches</code> unmodified.
     * </p>
     *
     * <p>
     * The semantics of the incoming accumulator are only that it is the appropriate place to add new matches, not that
     * it will necessarily contain all matches so far. This allows intermediate accumulators to be created and passed
     * without causing strange behavior. <em>No concrete subclass should depend on the incoming value of the
     * accumulator, save that it will be non-<code>null</code> and mutable.</em>
     * </p>
     *
     * @param entries
     *            The set of symbol table entries to consider.
     * @param matches
     *            A non-<code>null</code> accumulator of matches.
     * @param l
     *            The context from which <code>entries</code> was drawn.
     *
     * @return <code>true</code> if <code>matches</code> now represents a final list of search results; i.e., no further
     *         symbol table entries should be considered. <code>false</code> indicates that the search should continue,
     *         provided there are additional un-searched scopes.
     *
     * @throws DuplicateSymbolException
     *             If more than one match is found in <code>entries</code> where no more than one was expected.
     */
    @Override
    public final boolean addMatches(SymbolTable entries, List<OperationProfileEntry> matches, SearchContext l)
            throws DuplicateSymbolException {
        if (entries.containsKey(myQueryName)) {
            try {
                OperationProfileEntry operationProfile = entries.get(myQueryName)
                        .toOperationProfileEntry(myQueryLocation);

                if (argumentsMatch(operationProfile.getCorrespondingOperation().getParameters())) {
                    // We have a match at this point
                    if (!matches.isEmpty()) {
                        throw new DuplicateSymbolException("Found two matching operations!", operationProfile);
                    }

                    matches.add(operationProfile);
                }
            } catch (SourceErrorException see) {
                // No problem, just don't include it in the result
            }
        }

        return false;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This helper method attempts to match the formal parameter program types to the program types supplied by the
     * user.
     * </p>
     *
     * @param formalParameters
     *            Formal parameters found in an {@link OperationEntry}.
     *
     * @return {@code true} if the argument types match, {@code false} otherwise.
     */
    private boolean argumentsMatch(ImmutableList<ProgramParameterEntry> formalParameters) {
        boolean result = (formalParameters.size() == myActualArgumentTypes.size());

        if (result) {
            Iterator<ProgramParameterEntry> formalParametersIter = formalParameters.iterator();
            Iterator<PTType> actualArgumentTypeIter = myActualArgumentTypes.iterator();

            PTType actualArgumentType, formalParameterType;
            while (result && formalParametersIter.hasNext()) {
                actualArgumentType = actualArgumentTypeIter.next();
                formalParameterType = formalParametersIter.next().getDeclaredType();

                result = actualArgumentType.acceptableFor(formalParameterType);
            }
        }

        return result;
    }

}
