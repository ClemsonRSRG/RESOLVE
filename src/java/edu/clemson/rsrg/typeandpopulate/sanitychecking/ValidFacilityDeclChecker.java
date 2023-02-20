/*
 * ValidFacilityDeclChecker.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.declarations.mathdecl.MathDefinitionDec;
import edu.clemson.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ConceptTypeParamDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.rsrg.absyn.items.programitems.EnhancementSpecItem;
import edu.clemson.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.rsrg.absyn.items.programitems.ModuleArgumentItem;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.rsrg.typeandpopulate.query.NameQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This is a sanity checker for making sure the declared {@link FacilityDec} has valid arguments to each of the modules
 * it is trying to instantiate.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class ValidFacilityDeclChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The facility declaration we are checking.
     * </p>
     */
    private final FacilityDec myFacilityDec;

    /**
     * <p>
     * The current scope.
     * </p>
     */
    private final MathSymbolTableBuilder myCurrentScope;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates a sanity checker for checking whether or not all the arguments passed to each of the
     * {@link ModuleArgumentItem} are the expected type.
     * </p>
     *
     * @param facilityDec
     *            The encountered facility declaration.
     * @param currentScope
     *            The current scope.
     */
    public ValidFacilityDeclChecker(FacilityDec facilityDec, MathSymbolTableBuilder currentScope) {
        myFacilityDec = facilityDec;
        myCurrentScope = currentScope;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Checks to see if the module arguments being passed can be used to instantiated those that have been specified in
     * the corresponding module declarations.
     * </p>
     *
     * @throws SourceErrorException
     *             This is thrown when we encounter an item that cannot be used to instantiate the module.
     */
    public final void hasValidModuleArgumentItems() {
        // Check concept arguments
        ModuleDec conceptModuleDec = getModuleDec(myFacilityDec.getConceptName());
        moduleParameterSanityCheck(conceptModuleDec.getName().getLocation(),
                myFacilityDec.getConceptName().getLocation(), conceptModuleDec.getParameterDecs(),
                myFacilityDec.getConceptParams());

        // Check concept realization arguments (if it is not externally realized)
        if (!myFacilityDec.getExternallyRealizedFlag()) {
            ModuleDec conceptRealizModuleDec = getModuleDec(myFacilityDec.getConceptRealizName());
            moduleParameterSanityCheck(conceptRealizModuleDec.getName().getLocation(),
                    myFacilityDec.getConceptRealizName().getLocation(), conceptRealizModuleDec.getParameterDecs(),
                    myFacilityDec.getConceptRealizParams());
        }

        // Check all concept enhancements
        List<EnhancementSpecItem> enhancementSpecItems = myFacilityDec.getEnhancements();
        for (EnhancementSpecItem specItem : enhancementSpecItems) {
            ModuleDec enhancementModuleDec = getModuleDec(specItem.getName());
            moduleParameterSanityCheck(enhancementModuleDec.getName().getLocation(), specItem.getName().getLocation(),
                    enhancementModuleDec.getParameterDecs(), specItem.getParams());
        }

        // Check all enhancement/enhancement realization pairs
        List<EnhancementSpecRealizItem> enhancementSpecRealizItems = myFacilityDec.getEnhancementRealizPairs();
        for (EnhancementSpecRealizItem specRealizItem : enhancementSpecRealizItems) {
            ModuleDec enhancementModuleDec = getModuleDec(specRealizItem.getEnhancementName());
            moduleParameterSanityCheck(enhancementModuleDec.getName().getLocation(),
                    specRealizItem.getEnhancementName().getLocation(), enhancementModuleDec.getParameterDecs(),
                    specRealizItem.getEnhancementParams());

            ModuleDec enhancementRealizModuleDec = getModuleDec(specRealizItem.getEnhancementRealizName());
            moduleParameterSanityCheck(enhancementRealizModuleDec.getName().getLocation(),
                    specRealizItem.getEnhancementRealizName().getLocation(),
                    enhancementRealizModuleDec.getParameterDecs(), specRealizItem.getEnhancementRealizParams());
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for retrieving the module declaration associated with the given name.
     * </p>
     *
     * @param moduleName
     *            Name of the module
     *
     * @return A {@link ModuleDec} associated with the name.
     *
     * @throws SourceErrorException
     *             This is thrown when we are unable to find the module declaration associated with the name.
     */
    private ModuleDec getModuleDec(PosSymbol moduleName) {
        ModuleDec moduleDec;
        try {
            moduleDec = myCurrentScope.getModuleScope(new ModuleIdentifier(moduleName.getName())).getDefiningElement();
        } catch (NoSuchSymbolException e) {
            throw new SourceErrorException("Module does not exist or is not in scope.", moduleName);
        }

        return moduleDec;
    }

    /**
     * <p>
     * An helper method for sanity checking for any math definitions as parameters.
     * </p>
     *
     * @param actualMathDefNameExp
     *            Name of the mathematical definition being passed as module argument in the facility declaration.
     *
     * @throws SourceErrorException
     *             The actual mathematical definition cannot be passed as argument for the formal type parameter.
     */
    private void mathDefinitionAsParameterSanityCheck(ProgramVariableNameExp actualMathDefNameExp) {
        // Query and check the program type
        Location loc = actualMathDefNameExp.getLocation();
        try {
            SymbolTableEntry entry = myCurrentScope.getInnermostActiveScope()
                    .queryForOne(new NameQuery(actualMathDefNameExp.getQualifier(), actualMathDefNameExp.getName(),
                            MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                            MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE, true));

            // Make sure it is a math definition
            if (!(entry.getDefiningElement() instanceof MathDefinitionDec)) {
                throw new SourceErrorException(
                        "'" + actualMathDefNameExp.toString() + "' isn't a mathematical definition].",
                        actualMathDefNameExp.getLocation());
            }
        } catch (NoSuchSymbolException nsse) {
            String message;

            if (actualMathDefNameExp.getQualifier() == null) {
                message = "No such symbol: " + actualMathDefNameExp.getName().getName();
            } else {
                message = "No such symbol in module: " + actualMathDefNameExp.getQualifier().getName() + "::"
                        + actualMathDefNameExp.getName().getName();
            }
            throw new SourceErrorException(message, loc);
        } catch (DuplicateSymbolException dse) {
            // This should be caught earlier, when the duplicate operation is
            // created
            throw new RuntimeException(dse);
        }
    }

    /**
     * <p>
     * An helper method for comparing each of the module parameter declaration and module argument pairs and see if it
     * is a valid instantiation.
     * </p>
     *
     * @param moduleParametersLoc
     *            The location where for the module parameter declarations.
     * @param moduleArgumentItemsLoc
     *            The location where we encountered the list of module argument items.
     * @param moduleParameterDecs
     *            List of module parameter declarations.
     * @param moduleArgumentItems
     *            List of corresponding module argument items.
     */
    private void moduleParameterSanityCheck(Location moduleParametersLoc, Location moduleArgumentItemsLoc,
            List<ModuleParameterDec> moduleParameterDecs, List<ModuleArgumentItem> moduleArgumentItems) {
        // Not sure why we haven't caught this yet, but it shouldn't happen.
        if (moduleParameterDecs.size() != moduleArgumentItems.size()) {
            throw new SourceErrorException("Invalid facility declaration. "
                    + "Number of arguments does not match the number of module parameters." + "\n\nExpecting: "
                    + moduleParameterDecs.size() + " [" + moduleParametersLoc + "]\n" + "Found: "
                    + moduleArgumentItems.size(), moduleArgumentItemsLoc);
        }

        // Make sure each pair is valid
        Iterator<ModuleParameterDec> moduleParameterDecIterator = moduleParameterDecs.iterator();
        Iterator<ModuleArgumentItem> moduleArgumentItemIterator = moduleArgumentItems.iterator();
        while (moduleParameterDecIterator.hasNext() && moduleArgumentItemIterator.hasNext()) {
            ModuleArgumentItem moduleArgumentItem = moduleArgumentItemIterator.next();
            ModuleParameterDec moduleParameterDec = moduleParameterDecIterator.next();

            // Wrapped declaration inside the moduleParameterDec
            // ProgramExp inside the moduleArgumentItem
            Dec wrappedDec = moduleParameterDec.getWrappedDec();
            ProgramExp exp = moduleArgumentItem.getArgumentExp();

            // Case #1: Passing operations as arguments
            if (wrappedDec instanceof OperationDec) {
                if (exp instanceof ProgramVariableNameExp) {
                    opAsParameterSanityCheck((OperationDec) wrappedDec, (ProgramVariableNameExp) exp);
                } else {
                    throw new SourceErrorException(
                            "Invalid facility declaration. " + "\n\nExpecting: Operation Declaration" + " ["
                                    + wrappedDec.getLocation() + "]\n" + "Found: " + exp.getClass().getSimpleName(),
                            exp.getLocation());
                }
            }
            // Case #2: Instantiating a concept type
            else if (wrappedDec instanceof ConceptTypeParamDec) {
                if (exp instanceof ProgramVariableNameExp) {
                    typeAsParameterSanityCheck((ProgramVariableNameExp) exp);
                } else {
                    throw new SourceErrorException(
                            "Invalid facility declaration. " + "\n\nExpecting: A program type" + " ["
                                    + wrappedDec.getLocation() + "]\n" + "Found: " + exp.getClass().getSimpleName(),
                            exp.getLocation());
                }
            }
            // Case #3: Passing a mathematical definition
            else if (wrappedDec instanceof MathDefinitionDec) {
                if (exp instanceof ProgramVariableNameExp) {
                    mathDefinitionAsParameterSanityCheck((ProgramVariableNameExp) exp);
                } else {
                    throw new SourceErrorException(
                            "Invalid facility declaration. " + "\n\nExpecting: A mathematical definition name" + " ["
                                    + wrappedDec.getLocation() + "]\n" + "Found: " + exp.getClass().getSimpleName(),
                            exp.getLocation());
                }
            }
            // TODO: Sanity checks for the ConstantParamDec and RealizationParamDec if Needed
        }
    }

    /**
     * <p>
     * An helper method for sanity checking for any operations as parameters.
     * </p>
     *
     * @param wrappedDecAsOperationDec
     *            Formal operation declaration as module parameter.
     * @param actualOpNameExp
     *            Name of the operation being passed as module argument in the facility declaration.
     *
     * @throws SourceErrorException
     *             The actual operation cannot be passed as argument for the formal operation parameter.
     */
    private void opAsParameterSanityCheck(OperationDec wrappedDecAsOperationDec,
            ProgramVariableNameExp actualOpNameExp) {
        // Query and check the operation
        Location loc = actualOpNameExp.getLocation();
        try {
            OperationEntry op = myCurrentScope.getInnermostActiveScope()
                    .queryForOne(new NameQuery(actualOpNameExp.getQualifier(), actualOpNameExp.getName(),
                            MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                            MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE, true))
                    .toOperationEntry(loc);
            OperationDec actualOpDec = (OperationDec) op.getDefiningElement();

            // Check #1: Make sure the parameter sizes match
            List<ParameterVarDec> formalParams = wrappedDecAsOperationDec.getParameters();
            List<ParameterVarDec> actualParams = actualOpDec.getParameters();
            if (formalParams.size() != actualParams.size()) {
                throw new SourceErrorException(
                        "Number of parameters does not match." + "\n\nExpecting: " + formalParams.size() + " ["
                                + wrappedDecAsOperationDec.getLocation() + "]\n" + "Found: " + actualParams.size(),
                        loc);
            }

            // Check #2: Make sure the actual operation parameters all have
            // valid parameter modes to implement the formal parameters.
            Iterator<ParameterVarDec> formalIt = formalParams.iterator();
            Iterator<ParameterVarDec> actualIt = actualParams.iterator();
            ParameterVarDec currFormalParam, currActualParam;
            while (formalIt.hasNext()) {
                currFormalParam = formalIt.next();
                currActualParam = actualIt.next();

                if (!currFormalParam.getMode().canBeImplementedWith(currActualParam.getMode())) {
                    throw new SourceErrorException("Invalid operation parameter modes." + "\nExpecting: "
                            + currFormalParam.getMode().name() + " [" + currFormalParam.getLocation() + "]\n"
                            + "Found: " + currActualParam.getMode().name(), currActualParam.getLocation());
                }
            }
        } catch (NoSuchSymbolException nsse) {
            String message;

            if (actualOpNameExp.getQualifier() == null) {
                message = "No such symbol: " + actualOpNameExp.getName().getName();
            } else {
                message = "No such symbol in module: " + actualOpNameExp.getQualifier().getName() + "::"
                        + actualOpNameExp.getName().getName();
            }
            throw new SourceErrorException(message, loc);
        } catch (DuplicateSymbolException dse) {
            // This should be caught earlier, when the duplicate operation is
            // created
            throw new RuntimeException(dse);
        }
    }

    /**
     * <p>
     * An helper method for sanity checking for any program types as parameters.
     * </p>
     *
     * @param actualTypeNameExp
     *            Name of the program type being passed as module argument in the facility declaration.
     *
     * @throws SourceErrorException
     *             The actual program type cannot be passed as argument for the formal type parameter.
     */
    private void typeAsParameterSanityCheck(ProgramVariableNameExp actualTypeNameExp) {
        // Query and check the program type
        Location loc = actualTypeNameExp.getLocation();
        try {
            boolean knownToBeAProgramType = false;
            SymbolTableEntry entry = myCurrentScope.getInnermostActiveScope()
                    .queryForOne(new NameQuery(actualTypeNameExp.getQualifier(), actualTypeNameExp.getName(),
                            MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                            MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE, true));

            // Case #1: ProgramParameterEntry with a passing mode of TYPE
            if (entry instanceof ProgramParameterEntry && ((ProgramParameterEntry) entry).getParameterMode()
                    .equals(ProgramParameterEntry.ParameterMode.TYPE)) {
                knownToBeAProgramType = true;
            }
            // Case #2: ProgramTypeEntry
            else if (entry instanceof ProgramTypeEntry) {
                knownToBeAProgramType = true;
            }

            // Throw an error if the passed in argument is not known to be a program type
            if (!knownToBeAProgramType) {
                throw new SourceErrorException("'" + actualTypeNameExp.toString() + "' isn't a program type.",
                        actualTypeNameExp.getLocation());
            }
        } catch (NoSuchSymbolException nsse) {
            String message;

            if (actualTypeNameExp.getQualifier() == null) {
                message = "No such symbol: " + actualTypeNameExp.getName().getName();
            } else {
                message = "No such symbol in module: " + actualTypeNameExp.getQualifier().getName() + "::"
                        + actualTypeNameExp.getName().getName();
            }
            throw new SourceErrorException(message, loc);
        } catch (DuplicateSymbolException dse) {
            // This should be caught earlier, when the duplicate operation is
            // created
            throw new RuntimeException(dse);
        }
    }
}
