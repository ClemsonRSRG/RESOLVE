/*
 * AbstractReductionRuleApplication.java
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
package edu.clemson.rsrg.vcgeneration.sequents.reductionrules;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.vcgeneration.sequents.Sequent;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This is the abstract base class for all the {@code Sequent Reduction Rules}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public abstract class AbstractReductionRuleApplication implements ReductionRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An impacting reduction means that either {@code myOriginalExp} is a negation {@code exp} or {@code myOriginalExp}
     * created a new associated {@link Sequent}.
     * </p>
     */
    protected boolean myIsImpactingReductionFlag;

    /**
     * <p>
     * The original expression to be reduced
     * </p>
     */
    protected final Exp myOriginalExp;

    /**
     * <p>
     * The original sequent that contains {@code myOriginalExp}.
     * </p>
     */
    protected final Sequent myOriginalSequent;

    /**
     * <p>
     * The resulting {@code sequents}.
     * </p>
     */
    protected final List<Sequent> myResultingSequents;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * An helper constructor that stores the {@code originalSequent} and the {@code originalExp} we are trying to reduce
     * as well as creating the list to store the resulting {@link Sequent Sequents}.
     * </p>
     *
     * @param originalSequent
     *            The original {@link Sequent} that contains the expression to be reduced.
     * @param originalExp
     *            The {@link Exp} to be reduced.
     */
    protected AbstractReductionRuleApplication(Sequent originalSequent, Exp originalExp) {
        myIsImpactingReductionFlag = false;
        myOriginalExp = originalExp;
        myOriginalSequent = originalSequent;
        myResultingSequents = new ArrayList<>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method indicates whether or not this {@link ReductionRuleApplication} encountered a {@code not(<exp>)} or if
     * it created a new associated {@link Sequent}.
     * </p>
     *
     * @return {@code true} if we have applied some kind of impacting logical reduction to the original {@link Sequent},
     *         {@code false} otherwise.
     */
    @Override
    public final boolean isIsImpactingReductionFlag() {
        return myIsImpactingReductionFlag;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * An helper method that deep copies an {@link Exp}.
     * </p>
     *
     * @param originalExp
     *            The expression to copy.
     * @param parentLocationDetail
     *            Location detail for the parent expression.
     *
     * @return A deep copy of {@code originalExp}.
     */
    protected final Exp copyExp(Exp originalExp, LocationDetailModel parentLocationDetail) {
        Exp expCopy = originalExp.clone();

        // Check to see if we have a location detail model
        // If we have one, the clone method must have made a
        // a copy already.
        if (expCopy.getLocationDetailModel() == null) {
            // Attempt to copy our parent's model.
            // YS: At this point, someone should have
            // some sort of location detail model.
            // If not, the VC generator didn't generate
            // the expression properly.
            if (parentLocationDetail != null) {
                expCopy.setLocationDetailModel(parentLocationDetail.clone());
            }
        }

        return expCopy;
    }

    /**
     * <p>
     * An helper method that deep copies a list of {@link Exp Exps}.
     * </p>
     *
     * @param originalExpList
     *            A list of {@link Exp Exps}.
     * @param parentLocationDetail
     *            Location detail for the parent expression.
     *
     * @return A deep copy of {@code originalExpList}.
     */
    protected final List<Exp> copyExpList(List<Exp> originalExpList, LocationDetailModel parentLocationDetail) {
        List<Exp> copyExpList = new ArrayList<>(originalExpList.size());

        for (Exp exp : originalExpList) {
            Exp expCopy = exp.clone();

            // Check to see if we have a location detail model
            // If we have one, the clone method must have made a
            // a copy already.
            if (expCopy.getLocationDetailModel() == null) {
                // Attempt to copy our parent's model.
                // YS: At this point, someone should have
                // some sort of location detail model.
                // If not, the VC generator didn't generate
                // the expression properly.
                if (parentLocationDetail != null) {
                    expCopy.setLocationDetailModel(parentLocationDetail.clone());
                }
            }

            copyExpList.add(expCopy);
        }

        return copyExpList;
    }

    /**
     * <p>
     * An helper method that throws an unexpected expression error message.
     * </p>
     */
    protected final void unexpectedExp() {
        throw new SourceErrorException("[VCGenerator] Found: " + myOriginalExp + " of type: "
                + myOriginalExp.getClass().getSimpleName() + " while applying " + getRuleDescription(),
                myOriginalExp.getLocation());
    }

}
