/*
 * FormalActualLists.java
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
package edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual;

import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>An helper class that stores the formal parameters for a module and
 * the processed instantiation arguments from a {@link FacilityDec}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class FormalActualLists {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>A list that will be populated with the arguments used to
     * instantiate a module.</p>
     */
    private final List<Exp> myActualArgList;

    /**
     * <p>A list that will be populated with the instantiating
     * a module's formal parameters.</p>
     */
    private final List<VarExp> myFormalParamList;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that stores the formal parameters from a module
     * and the processed instantiating arguments from a {@link FacilityDec}.</p>
     *
     * @param formalParamList The formal parameters from a module.
     * @param actualArgList The processed arguments used to instantiate a module.
     */
    public FormalActualLists(List<VarExp> formalParamList, List<Exp> actualArgList) {
        myActualArgList = new ArrayList<>(actualArgList);
        myFormalParamList = new ArrayList<>(formalParamList);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default {@code equals} method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FormalActualLists that = (FormalActualLists) o;

        if (!myActualArgList.equals(that.myActualArgList))
            return false;
        return myFormalParamList.equals(that.myFormalParamList);
    }

    /**
     * <p>This method returns a deep copy of the processed arguments
     * from the instantiated {@code Facility}.</p>
     *
     * @return A {@link List} containing the actual arguments.
     */
    public final List<Exp> getActualArgList() {
        List<Exp> retList = new ArrayList<>(myActualArgList.size());
        for (Exp exp : myActualArgList) {
            retList.add(exp.clone());
        }

        return retList;
    }

    /**
     * <p>This method returns a deep copy of the formal parameters
     * from the module we are trying to instantiate.</p>
     *
     * @return A {@link List} containing the formal parameters.
     */
    public final List<VarExp> getFormalParamList() {
        List<VarExp> retList = new ArrayList<>(myFormalParamList.size());
        for (VarExp varExp : myFormalParamList) {
            retList.add((VarExp) varExp.clone());
        }

        return retList;
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myActualArgList.hashCode();
        result = 31 * result + myFormalParamList.hashCode();
        return result;
    }

}