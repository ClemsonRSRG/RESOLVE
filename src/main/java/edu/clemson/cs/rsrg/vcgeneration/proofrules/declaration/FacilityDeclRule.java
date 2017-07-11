/*
 * FacilityDeclRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.declaration;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ConceptModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ConceptRealizModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.MathExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.absyn.items.programitems.ModuleArgumentItem;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.InstantiatedFacilityDecl;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.treewalkers.ConceptTypeExtractor;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for a {@code facility} declaration
 * rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class FacilityDeclRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    // -----------------------------------------------------------
    // General
    // -----------------------------------------------------------

    /**
     * <p>The module scope for the file we are generating
     * {@code VCs} for.</p>
     */
    private final ModuleScope myCurrentModuleScope;

    /** <p>The {@code facility} declaration we are applying the rule to.</p> */
    private final FacilityDec myFacilityDec;

    /** <p>A flag that indicates if this is a local facility declaration or not.</p> */
    private final boolean myIsLocalFacilityDec;

    /** <p>The list of processed {@link InstantiatedFacilityDecl}. </p> */
    private final List<InstantiatedFacilityDecl> myProcessedInstFacilityDecls;

    /** <p>The symbol table we are currently building.</p> */
    private final MathSymbolTableBuilder mySymbolTable;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    // -----------------------------------------------------------
    // InstantiatedFacilityDecl - Related
    // -----------------------------------------------------------

    /**
     * <p>This maps all {@code Concept} formal arguments to the instantiated
     * actual arguments.</p>
     */
    private final Map<Exp, Exp> myConceptArgMap;

    /** <p>This contains all the types declared by the {@code Concept}.</p> */
    private final List<TypeFamilyDec> myConceptDeclaredTypes;

    /**
     * <p>This maps all {@code Concept Realization} formal arguments to the instantiated
     * actual arguments.</p>
     */
    private final Map<Exp, Exp> myConceptRealizArgMap;

    /**
     * <p>This maps all {@code Enhancement} and {@code Enhancement Realization} formal arguments
     * to the instantiated actual arguments.</p>
     */
    private final Map<PosSymbol, Map<Exp, Exp>> myEnhancementArgMaps;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for a {@code facility}
     * declaration rule.</p>
     *
     * @param facilityDec The {@code facility} declaration we are applying the
     *                    rule to.
     * @param isLocalFacDec A flag that indicates if this is a local {@link FacilityDec}.
     * @param processedInstFacDecs The list of processed {@link InstantiatedFacilityDecl}.
     * @param symbolTableBuilder The current symbol table.
     * @param moduleScope The current module scope we are visiting.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public FacilityDeclRule(FacilityDec facilityDec, boolean isLocalFacDec,
            List<InstantiatedFacilityDecl> processedInstFacDecs,
            MathSymbolTableBuilder symbolTableBuilder, ModuleScope moduleScope,
            AssertiveCodeBlock block, STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myCurrentModuleScope = moduleScope;
        myFacilityDec = facilityDec;
        myIsLocalFacilityDec = isLocalFacDec;
        myProcessedInstFacilityDecls = processedInstFacDecs;
        mySymbolTable = symbolTableBuilder;
        myTypeGraph = symbolTableBuilder.getTypeGraph();

        // Objects needed to create a new InstantiatedFacilityDecl
        myConceptDeclaredTypes = new LinkedList<>();
        myConceptArgMap = new LinkedHashMap<>();
        myConceptRealizArgMap = new LinkedHashMap<>();
        myEnhancementArgMaps = new LinkedHashMap<>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        // Apply the part of the rule that deals with the facility
        // concept and its associated realization.
        Exp retExpPart1 = applyConceptRelatedPart();

        // This class is used by any importing facility declarations as well as
        // any local facility declarations. We really don't need to display
        // anything to our models if it isn't local. - YS
        if (myIsLocalFacilityDec) {
            // Add the different details to the various different output models
            ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
            stepModel.add("proofRuleName", getRuleDescription()).add(
                    "currentStateOfBlock", myCurrentAssertiveCodeBlock);
            myBlockModel.add("vcGenSteps", stepModel.render());
        }
    }

    /**
     * <p>This method returns an object that records all relevant information
     * for the instantiated {@code Facility}.</p>
     *
     * @return A {@link InstantiatedFacilityDecl} containing all the information.
     */
    public final InstantiatedFacilityDecl getInstantiatedFacilityDecl() {
        return new InstantiatedFacilityDecl(myFacilityDec,
                myConceptDeclaredTypes, myConceptArgMap, myConceptRealizArgMap,
                myEnhancementArgMaps);
    }

    /**
     * <p>This method returns a description associated with
     * the {@code Proof Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Facility Declaration Rule";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // -----------------------------------------------------------
    // General
    // -----------------------------------------------------------

    /**
     * <p>An helper method that creates a list of {@link Exp Exps}
     * representing each of the {@link ModuleArgumentItem ModuleArgumentItems}.
     * It is possible that the passed in programming expression
     * contains nested function calls, therefore we will need to deal
     * with it appropriately.</p>
     *
     * @param actualArgs List of module instantiated arguments.
     *
     * @return A list containing the {@link Exp Exps} representing
     * each actual argument.
     */
    private List<Exp> createModuleArgExpList(List<ModuleArgumentItem> actualArgs) {
        List<Exp> retExpList = new ArrayList<>();

        for (ModuleArgumentItem item : actualArgs) {
            // Convert the module argument items into the equivalent
            // mathematical expression.
            ProgramExp moduleArgumentExp = item.getArgumentExp();
            Exp moduleArgumentAsExp;
            if (moduleArgumentExp instanceof ProgramFunctionExp) {
                // TODO: Use the program function expression walker to generate moduleArgumentAsExp.
                throw new MiscErrorException("[VCGenerator] Insert program function expression walker here!",
                        new RuntimeException());
            }
            else {
                // Simply convert to the math equivalent expression
                moduleArgumentAsExp = Utilities.convertExp(moduleArgumentExp, myCurrentModuleScope);
            }

            // Add this to our return list
            retExpList.add(moduleArgumentAsExp);
        }

        return retExpList;
    }

    /**
     * <p>An helper method that creates a list of {@link VarExp VarExps}
     * representing each of the {@link ModuleParameterDec ModuleParameterDecs}.</p>
     *
     * @param formalParams List of module formal parameters.
     *
     * @return A list containing the {@link VarExp VarExps} representing
     * each formal parameter.
     */
    private List<VarExp> createModuleParamExpList(List<ModuleParameterDec> formalParams) {
        List<VarExp> retExpList = new ArrayList<>(formalParams.size());

        // Create a VarExp representing each of the module arguments
        for (ModuleParameterDec dec : formalParams) {
            // Use the wrapped declaration name and type to create a VarExp.
            Dec wrappedDec = dec.getWrappedDec();
            retExpList.add(Utilities.createVarExp(wrappedDec.getLocation(),
                    null, wrappedDec.getName(), wrappedDec.getMathType(), null));
        }

        return retExpList;
    }

    /**
     * <p>An helper method that replaces the module parameters with the
     * actual instantiated arguments. Note that both of these have been
     * converted to mathematical expressions.</p>
     *
     * @param exp The expression to be replaced.
     * @param formalParams List of module formal parameters.
     * @param actualArgs List of module instantiated arguments.
     *
     * @return The modified expression.
     */
    private Exp replaceFormalWithActual(Exp exp, List<VarExp> formalParams, List<Exp> actualArgs) {
        // YS: We need two replacement maps in case we happen to have the
        // same names in formal parameters expressions and in the argument list.
        Map<Exp, Exp> paramToTemp = new HashMap<>();
        Map<Exp, Exp> tempToActual = new HashMap<>();

        Exp retExp = exp.clone();
        if (formalParams.size() == actualArgs.size()) {
            // Loop through both lists
            for (int i = 0; i < formalParams.size(); i++) {
                VarExp formalParam = formalParams.get(i);
                Exp actualArg = actualArgs.get(i);

                // A temporary VarExp that avoids any formal with the same name as the actual.
                VarExp tempExp = Utilities.createVarExp(formalParam.getLocation(), null,
                        new PosSymbol(formalParam.getLocation(), "_" + formalParam.getName().getName()),
                        actualArg.getMathType(), actualArg.getMathTypeValue());

                // Add a substitution entry from formal parameter to tempExp.
                paramToTemp.put(formalParam, tempExp);

                // Add a substitution entry from tempExp to actual parameter.
                tempToActual.put(tempExp, actualArg);
            }

            // Replace from formal to temp and then from temp to actual
            retExp = retExp.substitute(paramToTemp);
            retExp = retExp.substitute(tempToActual);
        }
        else {
            // Something went wrong while obtaining the parameter and argument lists.
            throw new MiscErrorException(
                    "[VCGenerator] Formal parameter size is different than actual argument size.",
                    new RuntimeException());
        }

        return retExp;
    }

    // -----------------------------------------------------------
    // Proof Rule - Related
    // -----------------------------------------------------------

    /**
     * <p>An helper method that applies the part of the rule that deals with
     * {@code Concept} and {@code Concept Realizations}.</p>
     *
     * @return An {@link Exp} that contains the {@code Concept}'s and
     * {@code Concept Realization}'s modified requires clauses and any
     * passed-in operations requires clauses and ensures clause.
     */
    private Exp applyConceptRelatedPart() {
        Exp retExp = null;
        try {
            // Obtain the concept module for the facility
            ConceptModuleDec facConceptDec =
                    (ConceptModuleDec) mySymbolTable.getModuleScope(
                            new ModuleIdentifier(myFacilityDec.getConceptName()
                                    .getName())).getDefiningElement();

            // Extract the concept's type declarations
            ConceptTypeExtractor typeExtractor = new ConceptTypeExtractor();
            TreeWalker.visit(typeExtractor, facConceptDec);
            myConceptDeclaredTypes.addAll(typeExtractor.getTypeFamilyDecs());

            // Obtain the concept's requires clause
            Exp conceptReq =
                    facConceptDec.getRequires().getAssertionExp().clone();

            // Convert the concept's module parameters and the instantiated
            // concept's arguments into the appropriate mathematical expressions.
            // Note that any nested function calls will be dealt with appropriately.
            List<VarExp> conceptFormalParamList =
                    createModuleParamExpList(facConceptDec.getParameterDecs());
            List<Exp> conceptActualArgList =
                    createModuleArgExpList(myFacilityDec.getConceptParams());

            // Create a mapping from concept formal parameters
            // to actual arguments for future use.
            for (int i = 0; i < conceptFormalParamList.size(); i++) {
                myConceptArgMap.put(conceptFormalParamList.get(i),
                        conceptActualArgList.get(i));
            }

            // Step 1: Substitute concept realization's formal parameters with
            //         actual instantiation arguments for the concept realization's
            //         requires clause.
            //         ( RPC[ rn~>rn_exp, RR~>IRR ] )
            // Note: Only to this step if we don't have an external realization
            if (!myFacilityDec.getExternallyRealizedFlag()) {
                // Obtain the concept realization module for the facility
                ConceptRealizModuleDec facConceptRealizDec =
                        (ConceptRealizModuleDec) mySymbolTable.getModuleScope(
                                new ModuleIdentifier(myFacilityDec
                                        .getConceptRealizName().getName()))
                                .getDefiningElement();

                // Obtain the concept's requires clause
                Exp conceptRealizReq =
                        facConceptRealizDec.getRequires().getAssertionExp()
                                .clone();

                // Convert the concept realization's module parameters and the instantiated
                // concept's arguments into the appropriate mathematical expressions.
                // Note that any nested function calls will be dealt with appropriately.
                List<VarExp> conceptRealizFormalParamList =
                        createModuleParamExpList(facConceptRealizDec
                                .getParameterDecs());
                List<Exp> conceptRealizActualArgList =
                        createModuleArgExpList(myFacilityDec
                                .getConceptRealizParams());

                // Replace the formal with the actual (if conceptRealizReq /= true)
                if (!MathExp.isLiteralTrue(conceptRealizReq)) {
                    retExp =
                            replaceFormalWithActual(conceptRealizReq,
                                    conceptRealizFormalParamList,
                                    conceptRealizActualArgList);
                }

                // Create a mapping from concept realization formal parameters
                // to actual arguments for future use.
                for (int i = 0; i < conceptRealizFormalParamList.size(); i++) {
                    myConceptRealizArgMap.put(conceptRealizFormalParamList
                            .get(i), conceptRealizActualArgList.get(i));
                }
            }

            // Step 2: Form a conjunct with the substituted concept realization clause
            //         (if is not just "true") and the concept requires clause.
            //         Substitute concept's formal parameter with actual instantiation
            //         arguments for this new conjunct.
            //         ( ( RPC[ rn~>rn_exp, RR~>IRR ] ∧ CPC )[ n~>n_exp, R~>IR ] )
        }
        catch (NoSuchSymbolException e) {
            Utilities
                    .noSuchModule(myFacilityDec.getConceptName().getLocation());
        }

        return retExp;
    }

}