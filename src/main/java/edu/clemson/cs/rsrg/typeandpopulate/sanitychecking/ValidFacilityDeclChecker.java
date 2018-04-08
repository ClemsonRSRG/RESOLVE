/*
 * ValidFacilityDeclChecker.java
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
package edu.clemson.cs.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ConstantParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.absyn.items.programitems.EnhancementSpecItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.ModuleArgumentItem;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.query.NameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable;
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
            ModuleDec conceptRealizModuleDec =
                    getModuleDec(myFacilityDec.getConceptRealizName());
            isInvalidDeclaration =
                    invalidDeclaration(conceptRealizModuleDec
                            .getParameterDecs(), myFacilityDec
                            .getConceptRealizParams());
            if (isInvalidDeclaration) {
                throw new SourceErrorException(
                        "Invalid concept realization declaration.",
                        myFacilityDec.getConceptRealizName().getLocation());
            }

            // Sanity check any operations as parameters
            opAsParameterSanityCheck(conceptRealizModuleDec.getParameterDecs(),
                    myFacilityDec.getConceptRealizParams());
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

            // Sanity check any operations as parameters
            opAsParameterSanityCheck(enhancementRealizModuleDec
                    .getParameterDecs(), specRealizItem
                    .getEnhancementRealizParams());
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
        }

        return foundDifferentTypes;
    }

    /**
     * <p>An helper method for sanity checking for any operations as parameters.</p>
     *
     * @param moduleParameterDecs List of module parameter declarations.
     * @param moduleArgumentItems List of corresponding module argument items.
     *
     * @throws SourceErrorException The actual operation cannot be passed as argument
     * for the formal operation parameter.
     */
    private void opAsParameterSanityCheck(
            List<ModuleParameterDec> moduleParameterDecs,
            List<ModuleArgumentItem> moduleArgumentItems) {
        Iterator<ModuleParameterDec> moduleParameterDecIterator =
                moduleParameterDecs.iterator();
        Iterator<ModuleArgumentItem> moduleArgumentItemIterator =
                moduleArgumentItems.iterator();
        while (moduleArgumentItemIterator.hasNext()
                && moduleParameterDecIterator.hasNext()) {
            ModuleArgumentItem moduleArgumentItem =
                    moduleArgumentItemIterator.next();
            ModuleParameterDec moduleParameterDec =
                    moduleParameterDecIterator.next();
            Location loc = moduleArgumentItem.getLocation();

            // Wrapped declaration inside the moduleParameterDec
            Dec wrappedDec = moduleParameterDec.getWrappedDec();
            if (wrappedDec instanceof OperationDec) {
                // Query and check the operation
                OperationDec wrappedDecAsOperationDec =
                        (OperationDec) wrappedDec;
                ProgramVariableNameExp actualOpNameExp =
                        (ProgramVariableNameExp) moduleArgumentItem
                                .getArgumentExp();
                try {
                    OperationEntry op =
                            myCurrentScope
                                    .getInnermostActiveScope()
                                    .queryForOne(
                                            new NameQuery(
                                                    actualOpNameExp
                                                            .getQualifier(),
                                                    actualOpNameExp.getName(),
                                                    MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                                                    MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE,
                                                    true))
                                    .toOperationEntry(loc);
                    OperationDec actualOpDec =
                            (OperationDec) op.getDefiningElement();

                    // Check #1: Make sure the parameter sizes match
                    List<ParameterVarDec> formalParams =
                            wrappedDecAsOperationDec.getParameters();
                    List<ParameterVarDec> actualParams =
                            actualOpDec.getParameters();
                    if (formalParams.size() != actualParams.size()) {
                        throw new SourceErrorException(
                                "Actual operation parameter count "
                                        + "does not correspond to the formal operation parameter count."
                                        + "\n\tExpected count: "
                                        + formalParams.size()
                                        + "\n\tFound count: "
                                        + actualParams.size(), loc);
                    }

                    // Check #2: Make sure the actual operation parameters all have
                    // valid parameter modes to implement the formal parameters.
                    Iterator<ParameterVarDec> formalIt =
                            formalParams.iterator();
                    Iterator<ParameterVarDec> actualIt =
                            actualParams.iterator();
                    ParameterVarDec currFormalParam, currActualParam;
                    while (formalIt.hasNext()) {
                        currFormalParam = formalIt.next();
                        currActualParam = actualIt.next();

                        if (!currFormalParam.getMode().canBeImplementedWith(
                                currActualParam.getMode())) {
                            throw new SourceErrorException(
                                    "Operation parameter modes are not the same."
                                            + "\n\tExpecting: "
                                            + currFormalParam.getMode().name()
                                            + " " + currFormalParam.getName()
                                            + "\n\tFound: "
                                            + currActualParam.getMode().name()
                                            + " " + currActualParam.getName(),
                                    loc);
                        }
                    }
                }
                catch (NoSuchSymbolException nsse) {
                    String message;

                    if (actualOpNameExp.getQualifier() == null) {
                        message =
                                "No such symbol: "
                                        + actualOpNameExp.getName().getName();
                    }
                    else {
                        message =
                                "No such symbol in module: "
                                        + actualOpNameExp.getQualifier()
                                                .getName() + "::"
                                        + actualOpNameExp.getName().getName();
                    }
                    throw new SourceErrorException(message, loc);
                }
                catch (DuplicateSymbolException dse) {
                    //This should be caught earlier, when the duplicate operation is
                    //created
                    throw new RuntimeException(dse);
                }
            }
        }
    }

}