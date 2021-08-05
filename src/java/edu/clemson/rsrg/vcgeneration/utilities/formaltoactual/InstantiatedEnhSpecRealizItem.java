/*
 * InstantiatedEnhSpecRealizItem.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.vcgeneration.utilities.formaltoactual;

import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import java.util.List;

/**
 * <p>
 * This class stores an {@link EnhancementSpecRealizItem EnhancementSpecRealizItem's} formal parameters in the
 * specifications/implementations and their actual arguments in the {@code Facility} instantiation.
 * </p>
 *
 * @author Yu-Shan Sun
 * 
 * @version 1.0
 */
public class InstantiatedEnhSpecRealizItem {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The {@code Enhancement} and {@code Enhancement Realization} instantiation from a {@link FacilityDec}.
     * </p>
     */
    private final EnhancementSpecRealizItem myEnhancementSpecRealizItem;

    /**
     * <p>
     * This contains all the {@code Enhancement}'s formal arguments and its instantiated actual arguments.
     * </p>
     */
    private final FormalActualLists myEnhancementParamArgs;

    /**
     * <p>
     * This contains all the {@code Enhancement Realization}'s formal arguments and its instantiated actual arguments.
     * </p>
     */
    private final FormalActualLists myEnhancementRealizParamArgs;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that stores the various pieces of information related to the instantiated
     * {@code Facility}'s {@code Enhancement} and {@code Enhancement Realization}.
     * </p>
     *
     * @param enhancementSpecRealizItem
     *            The {@link EnhancementSpecRealizItem} from the instantiated {@code Facility}.
     * @param eFormalParamList
     *            The formal parameters from the {@code Enhancement}.
     * @param eActualArgList
     *            The processed arguments used to instantiate the {@code Enhancement}.
     * @param erFormalParamList
     *            The formal parameters from the {@code Enhancement Realization}.
     * @param erActualArgList
     *            The processed arguments used to instantiate the {@code Enhancement Realization}.
     */
    public InstantiatedEnhSpecRealizItem(EnhancementSpecRealizItem enhancementSpecRealizItem,
            List<VarExp> eFormalParamList, List<Exp> eActualArgList, List<VarExp> erFormalParamList,
            List<Exp> erActualArgList) {
        myEnhancementSpecRealizItem = enhancementSpecRealizItem;
        myEnhancementParamArgs = new FormalActualLists(eFormalParamList, eActualArgList);
        myEnhancementRealizParamArgs = new FormalActualLists(erFormalParamList, erActualArgList);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method overrides the default {@code equals} method implementation.
     * </p>
     *
     * @param o
     *            Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        InstantiatedEnhSpecRealizItem that = (InstantiatedEnhSpecRealizItem) o;

        if (!myEnhancementSpecRealizItem.equals(that.myEnhancementSpecRealizItem))
            return false;
        if (!myEnhancementParamArgs.equals(that.myEnhancementParamArgs))
            return false;
        return myEnhancementRealizParamArgs.equals(that.myEnhancementRealizParamArgs);
    }

    /**
     * <p>
     * This method returns a {@link FormalActualLists} containing the {@code Enhancement}'s formal and actual arguments
     * for the instantiated {@code Facility}.
     * </p>
     *
     * @return A {@link FormalActualLists} containing the formal parameters and the instantiation arguments.
     */
    public final FormalActualLists getEnhancementParamArgLists() {
        return myEnhancementParamArgs;
    }

    /**
     * <p>
     * This method returns a {@link FormalActualLists} containing the {@code Enhancement Realization}'s formal and
     * actual arguments for the instantiated {@code Facility}.
     * </p>
     *
     * @return A {@link FormalActualLists} containing the formal parameters and the instantiation arguments.
     */
    public final FormalActualLists getEnhancementRealizParamArgLists() {
        return myEnhancementRealizParamArgs;
    }

    /**
     * <p>
     * This method returns the {@link EnhancementSpecRealizItem} from the instantiated {@code Facility} declaration.
     * </p>
     *
     * @return A {@link EnhancementSpecRealizItem}.
     */
    public final EnhancementSpecRealizItem getEnhancementSpecRealizItem() {
        return myEnhancementSpecRealizItem;
    }

    /**
     * <p>
     * This method overrides the default {@code hashCode} method implementation.
     * </p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myEnhancementSpecRealizItem.hashCode();
        result = 31 * result + myEnhancementParamArgs.hashCode();
        result = 31 * result + myEnhancementRealizParamArgs.hashCode();
        return result;
    }

}
