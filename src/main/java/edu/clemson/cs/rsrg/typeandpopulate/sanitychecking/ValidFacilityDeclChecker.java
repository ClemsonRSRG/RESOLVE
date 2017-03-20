/**
 * ValidFacilityDeclChecker.java
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
package edu.clemson.cs.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ConceptTypeParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ConstantParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramLiteralExp;
import edu.clemson.cs.rsrg.absyn.items.programitems.EnhancementSpecItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.ModuleArgumentItem;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is a sanity checker for making sure the declared {@link FacilityDec} has
 * valid arguments to each of the modules it is trying to instantiate.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ValidFacilityDeclChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The facility declaration we are checking.</p> */
    private final FacilityDec myFacilityDec;

    /** <p>The current scope.</p> */
    private final MathSymbolTableBuilder myCurrentScope;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Creates a sanity checker for checking whether or not
     * all the arguments passed to each of the {@link ModuleArgumentItem} are
     * the expected type.</p>
     *
     * @param facilityDec The encountered facility declaration.
     * @param currentScope The current scope.
     */
    public ValidFacilityDeclChecker(FacilityDec facilityDec,
            MathSymbolTableBuilder currentScope) {
        myFacilityDec = facilityDec;
        myCurrentScope = currentScope;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Checks to see if the module arguments being passed
     * can be used to instantiated those that have been specified in the
     * corresponding module declarations.</p>
     *
     * @throws SourceErrorException This is thrown when we encounter an item that cannot
     * be used to instantiate the module.
     */
    public final void hasValidModuleArgumentItems() {
        // Check concept arguments
        ModuleDec conceptModuleDec =
                getModuleDec(myFacilityDec.getConceptName());
        boolean isInvalidDeclaration =
                invalidDeclaration(conceptModuleDec.getParameterDecs(),
                        myFacilityDec.getConceptParams());
        if (isInvalidDeclaration) {
            throw new SourceErrorException("Invalid concept declaration.",
                    myFacilityDec.getConceptName().getLocation());
        }

        // Check concept realization arguments (if it is not externally realized)
        if (!myFacilityDec.getExternallyRealizedFlag()) {
            ModuleDec concepRealizModuleDec =
                    getModuleDec(myFacilityDec.getConceptRealizName());
            isInvalidDeclaration =
                    invalidDeclaration(
                            concepRealizModuleDec.getParameterDecs(),
                            myFacilityDec.getConceptRealizParams());
            if (isInvalidDeclaration) {
                throw new SourceErrorException(
                        "Invalid concept realization declaration.",
                        myFacilityDec.getConceptRealizName().getLocation());
            }
        }

        // Check all concept enhancements
        List<EnhancementSpecItem> enhancementSpecItems =
                myFacilityDec.getEnhancements();
        for (EnhancementSpecItem specItem : enhancementSpecItems) {
            ModuleDec enhancementModuleDec = getModuleDec(specItem.getName());
            isInvalidDeclaration =
                    invalidDeclaration(enhancementModuleDec.getParameterDecs(),
                            specItem.getParams());
            if (isInvalidDeclaration) {
                throw new SourceErrorException(
                        "Invalid enhancement declaration.", specItem.getName()
                                .getLocation());
            }
        }

        // Check all enhancement/enhancement realization pairs
        List<EnhancementSpecRealizItem> enhancementSpecRealizItems =
                myFacilityDec.getEnhancementRealizPairs();
        for (EnhancementSpecRealizItem specRealizItem : enhancementSpecRealizItems) {
            ModuleDec enhancementModuleDec =
                    getModuleDec(specRealizItem.getEnhancementName());
            isInvalidDeclaration =
                    invalidDeclaration(enhancementModuleDec.getParameterDecs(),
                            specRealizItem.getEnhancementParams());
            if (isInvalidDeclaration) {
                throw new SourceErrorException(
                        "Invalid enhancement declaration.", specRealizItem
                                .getEnhancementName().getLocation());
            }

            ModuleDec enhancementRealizModuleDec =
                    getModuleDec(specRealizItem.getEnhancementRealizName());
            isInvalidDeclaration =
                    invalidDeclaration(enhancementRealizModuleDec
                            .getParameterDecs(), specRealizItem
                            .getEnhancementRealizParams());
            if (isInvalidDeclaration) {
                throw new SourceErrorException(
                        "Invalid enhancement realization declaration.",
                        specRealizItem.getEnhancementRealizName().getLocation());
            }
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method for retrieving the module declaration associated
     * with the given name.</p>
     *
     * @param moduleName Name of the module
     *
     * @return A {@link ModuleDec} associated with the name.
     *
     * @throws SourceErrorException This is thrown when we are unable to find the module declaration
     * associated with the name.
     */
    private ModuleDec getModuleDec(PosSymbol moduleName) {
        ModuleDec moduleDec;
        try {
            moduleDec =
                    myCurrentScope.getModuleScope(
                            new ModuleIdentifier(moduleName.getName()))
                            .getDefiningElement();
        }
        catch (NoSuchSymbolException e) {
            throw new SourceErrorException(
                    "Module does not exist or is not in scope.", moduleName);
        }

        return moduleDec;
    }

    /**
     * <p>An helper method for comparing each of the module parameter declaration and module
     * argument pairs and see if it is a valid instantiation.</p>
     *
     * @param moduleParameterDecs List of module parameter declarations.
     * @param moduleArgumentItems List of corresponding module argument items.
     *
     * @return {@code true} if it is an invalid declaration, {@code false} otherwise.
     */
    private boolean invalidDeclaration(
            List<ModuleParameterDec> moduleParameterDecs,
            List<ModuleArgumentItem> moduleArgumentItems) {
        // Not sure why we haven't caught this yet, but it shouldn't happen.
        if (moduleParameterDecs.size() != moduleArgumentItems.size()) {
            return true;
        }

        // Make sure each pair is valid
        Iterator<ModuleParameterDec> moduleParameterDecIterator =
                moduleParameterDecs.iterator();
        Iterator<ModuleArgumentItem> moduleArgumentItemIterator =
                moduleArgumentItems.iterator();
        boolean foundDifferentTypes = false;
        while (moduleArgumentItemIterator.hasNext() && !foundDifferentTypes) {
            ModuleArgumentItem moduleArgumentItem =
                    moduleArgumentItemIterator.next();
            ModuleParameterDec moduleParameterDec =
                    moduleParameterDecIterator.next();

            // Wrapped declaration inside the moduleParameterDec
            // ProgramExp inside the moduleArgumentItem
            Dec wrappedDec = moduleParameterDec.getWrappedDec();
            ProgramExp exp = moduleArgumentItem.getArgumentExp();
            if (exp instanceof ProgramLiteralExp) {
                if (!(wrappedDec instanceof ConstantParamDec)) {
                    foundDifferentTypes = true;
                }
            }
            else if (exp instanceof ProgramFunctionExp) {
                if (!(wrappedDec instanceof OperationDec)) {
                    foundDifferentTypes = true;
                }
            }
            else {
                if (!(wrappedDec instanceof ConceptTypeParamDec)) {
                    foundDifferentTypes = true;
                }
            }
        }

        return foundDifferentTypes;
    }

}