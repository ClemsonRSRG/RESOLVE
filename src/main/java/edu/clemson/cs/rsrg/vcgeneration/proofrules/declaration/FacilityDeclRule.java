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

import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
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

    /** <p>The {@code facility} declaration we are applying the rule to.</p> */
    private final FacilityDec myFacilityDec;

    /** <p>A flag that indicates if this is a local facility declaration or not.</p> */
    private final boolean myIsLocalFacilityDec;

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
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public FacilityDeclRule(FacilityDec facilityDec, boolean isLocalFacDec,
            AssertiveCodeBlock block, STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myFacilityDec = facilityDec;
        myIsLocalFacilityDec = isLocalFacDec;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
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

}