/*
 * NoSharingConceptImportChecker.java
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
package edu.clemson.cs.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ConceptModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.FacilityModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ShortFacilityModuleDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.query.NameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeBuilder;
import java.util.Iterator;

/**
 * <p>This is a sanity checker for making sure our uses list does not contain a
 * {@code sharing concept} directly or is importing an instantiated {@code facility}
 * of a {@code sharing concept}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class NoSharingConceptImportChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Location object from the uses item.</p> */
    private final Location myLocation;

    /** <p>The module being imported by the uses item.</p> */
    private final ModuleDec myModuleDec;

    /** <p>The current scope.</p> */
    private final ScopeBuilder myCurrentScope;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Creates a sanity checker for checking whether or not
     * the we import a {@code sharing concept}.</p>
     *
     * @param location The location for the uses item.
     * @param moduleDec The module being imported.
     * @param currentScope The current scope.
     */
    public NoSharingConceptImportChecker(Location location,
            ModuleDec moduleDec, ScopeBuilder currentScope) {
        myLocation = location;
        myModuleDec = moduleDec;
        myCurrentScope = currentScope;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method indicates whether or not we are importing a
     * {@code sharing concept}.</p>
     *
     * @return {@code true} if this uses item imports a sharing concept,
     * {@code false} otherwise.
     */
    public final boolean importingSharingConcept() {
        boolean retval = false;

        if (myModuleDec instanceof ConceptModuleDec) {
            // Check to see if this concept is a sharing concept
            retval = ((ConceptModuleDec) myModuleDec).isSharingConcept();
        }
        else if (myModuleDec instanceof ShortFacilityModuleDec) {
            // Check the only facility declaration inside this short facility module dec
            FacilityEntry facilityEntry =
                    getFacilityEntry(((ShortFacilityModuleDec) myModuleDec)
                            .getDec().getName());
            retval = facilityEntry.isSharingConceptInstantion();
        }
        else if (myModuleDec instanceof FacilityModuleDec) {
            // Search all facility declarations and see if any of them instantiates a sharing concept.
            Iterator<Dec> facilityDecIt = myModuleDec.getDecList().iterator();
            while (facilityDecIt.hasNext() && !retval) {
                Dec dec = facilityDecIt.next();
                if (dec instanceof FacilityDec) {
                    FacilityEntry facilityEntry =
                            getFacilityEntry(dec.getName());
                    retval = facilityEntry.isSharingConceptInstantion();
                }
            }
        }

        return retval;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method for querying for a facility entry.</p>
     *
     * @param name A name for the facility declaration.
     *
     * @return A {@link FacilityEntry} if found. Otherwise a {@link SourceErrorException}
     * will be thrown.
     */
    private FacilityEntry getFacilityEntry(PosSymbol name) {
        FacilityEntry entry;

        try {
            entry =
                    myCurrentScope.queryForOne(
                            new NameQuery(null, name,
                                    ImportStrategy.IMPORT_RECURSIVE,
                                    FacilityStrategy.FACILITY_GENERIC, true))
                            .toFacilityEntry(myLocation);
        }
        catch (NoSuchSymbolException nsse) {
            throw new SourceErrorException(
                    "No module found with the given name: " + name, myLocation);
        }
        catch (DuplicateSymbolException dse) {
            throw new SourceErrorException("Duplicate symbol: " + name
                    + ". Consider qualifying.", myLocation);
        }

        return entry;
    }

}