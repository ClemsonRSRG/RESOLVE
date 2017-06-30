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
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.absyn.items.programitems.ModuleArgumentItem;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
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

    /**
     * <p>The module scope for the file we are generating
     * {@code VCs} for.</p>
     */
    private final ModuleScope myCurrentModuleScope;

    /** <p>The {@code facility} declaration we are applying the rule to.</p> */
    private final FacilityDec myFacilityDec;

    /** <p>A flag that indicates if this is a local facility declaration or not.</p> */
    private final boolean myIsLocalFacilityDec;

    /** <p>The symbol table we are currently building.</p> */
    private final MathSymbolTableBuilder mySymbolTable;

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
     * @param symbolTableBuilder The current symbol table.
     * @param moduleScope The current module scope we are visiting.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public FacilityDeclRule(FacilityDec facilityDec, boolean isLocalFacDec,
            MathSymbolTableBuilder symbolTableBuilder, ModuleScope moduleScope,
            AssertiveCodeBlock block, STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myCurrentModuleScope = moduleScope;
        myFacilityDec = facilityDec;
        myIsLocalFacilityDec = isLocalFacDec;
        mySymbolTable = symbolTableBuilder;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        try {
            // Obtain the concept module for the facility
            ConceptModuleDec facConceptDec =
                    (ConceptModuleDec) mySymbolTable.getModuleScope(
                            new ModuleIdentifier(myFacilityDec.getConceptName()
                                    .getName())).getDefiningElement();
        }
        catch (NoSuchSymbolException e) {
            Utilities
                    .noSuchModule(myFacilityDec.getConceptName().getLocation());
        }

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

    /**
     * <p>An helper method that creates a list of {@link Exp Exps}
     * representing each of the {@link ModuleArgumentItem ModuleArgumentItems}.
     * It is possible that the passed in programming expression
     * contains nested function calls, therefore we will need to deal
     * with it appropriately.</p>
     *
     * @param actualParams List of module instantiated arguments.
     *
     * @return A list containing the {@link Exp Exps} representing
     * each actual argument.
     */
    private List<Exp> createModuleArgExpList(List<ModuleArgumentItem> actualParams) {
        List<Exp> retExpList = new ArrayList<>();

        for (ModuleArgumentItem item : actualParams) {
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
}