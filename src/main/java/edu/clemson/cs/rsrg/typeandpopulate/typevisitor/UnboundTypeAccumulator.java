/*
 * UnboundTypeAccumulator.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.typevisitor;

import edu.clemson.cs.rsrg.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTNamed;
import edu.clemson.cs.rsrg.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>This class visits the named types to see if any of the
 * given names is universally bound.</p>
 *
 * @version 2.0
 */
public class UnboundTypeAccumulator extends BoundVariableVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The set of unbound type names.</p> */
    private final Set<String> myUnboundTypeNames = new HashSet<>();

    /** <p>The searching scope.</p> */
    private final Scope myEnvironment;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a visitor used to visit all universally
     * bound type names in the provided scope.</p>
     *
     * @param environment The searching scope.
     */
    public UnboundTypeAccumulator(Scope environment) {
        myEnvironment = environment;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTNamed} by checking to see if <code>namedType</code>
     * is universally bound.</p>
     *
     * @param namedType A math type.
     */
    @Override
    public final void beginMTNamed(MTNamed namedType) {
        boolean universal;
        try {
            getInnermostBinding(namedType.getName());
            universal = true;
        }
        catch (NoSuchElementException e) {
            try {
                //We cast rather than call toMathSymbolEntry() because this
                //would represent an error in the compiler code rather than the
                //RESOLVE source: we're looking at math things here only
                MathSymbolEntry entry =
                        (MathSymbolEntry) myEnvironment
                                .queryForOne(new UnqualifiedNameQuery(
                                        namedType.getName()));
                universal =
                        entry.getQuantification().equals(
                                MathSymbolEntry.Quantification.UNIVERSAL);
            }
            catch (NoSuchSymbolException | DuplicateSymbolException nsse) {
                //Shouldn't be possible--we'd have dealt with it by now
                throw new RuntimeException(nsse);
            }
        }

        if (universal) {
            myUnboundTypeNames.add(namedType.getName());
        }
    }

    /**
     * <p>This method returns the final set of type names
     * that are universally bound.</p>
     *
     * @return A set of type names.
     */
    public final Set<String> getFinalUnboundNamedTypes() {
        return new HashSet<>(myUnboundTypeNames);
    }

}