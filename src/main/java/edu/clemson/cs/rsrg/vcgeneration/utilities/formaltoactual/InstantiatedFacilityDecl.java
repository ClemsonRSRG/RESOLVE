/*
 * InstantiatedFacilityDecl.java
 * ---------------------------------
 * Copyright (c) 2018
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
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.List;

/**
 * <p>This class stores all information pertinent to an {@link FacilityDec}
 * that will be useful for the various different {@code Proof Rules}. This
 * includes {@link FacilityDec FacilityDec's} formal parameters
 * in the specifications/implementations and their actual arguments in the
 * instantiation. It also includes the shared state variables and types
 * declarations that will be instantiated.</p>
 *
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class InstantiatedFacilityDecl {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>This contains all the types declared by the {@code Concept}.</p> */
    private final List<TypeFamilyDec> myConceptDeclaredTypes;

    /**
     * <p>This contains all the {@code Concept}'s formal arguments
     * and its instantiated actual arguments.</p>
     */
    private final FormalActualLists myConceptParamArgs;

    /**
     * <p>This contains all the {@code Concept Realization}'s formal arguments
     * and its instantiated actual arguments.</p>
     */
    private final FormalActualLists myConceptRealizParamArgs;

    /** <p>This contains all the shared state declared by the {@code Concept}.</p> */
    private final List<SharedStateDec> myConceptSharedStates;

    /**
     * <p>A list that contains the {@code Enhancement} and {@code Enhancement Realization}'s
     * formal arguments to the instantiated actual arguments.</p>
     */
    private final List<InstantiatedEnhSpecRealizItem> myInstantiatedEnhSpecRealizItems;

    /** <p>The instantiated {@code Facility}.</p> */
    private final FacilityDec myInstantiatedFacilityDec;

    /** <p>A flag that indicates if this is a local facility declaration or not.</p> */
    private final boolean myIsLocalFacilityDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that stores the various pieces of
     * information related to the instantiated {@code Facility}.</p>
     *
     * @param dec The instantiated {@code Facility} declaration.
     * @param conceptSharedStates The shared states in the instantiating {@code Concept}.
     * @param conceptDeclaredTypes The types in the instantiating {@code Concept}.
     * @param cFormalParamList The formal parameters from the {@code Concept}.
     * @param cActualArgList The processed arguments used to instantiate the {@code Concept}.
     * @param crFormalParamList The formal parameters from the {@code Concept Realization}.
     * @param crActualArgList The processed arguments used to instantiate the {@code Concept Realization}.
     * @param enhSpecRealizItems A list of {@link InstantiatedEnhSpecRealizItem InstantiatedEnhSpecRealizItems}.
     * @param isLocalFacDec A flag that indicates if this is a local {@link FacilityDec}.
     */
    public InstantiatedFacilityDecl(FacilityDec dec,
            List<SharedStateDec> conceptSharedStates,
            List<TypeFamilyDec> conceptDeclaredTypes,
            List<VarExp> cFormalParamList, List<Exp> cActualArgList,
            List<VarExp> crFormalParamList, List<Exp> crActualArgList,
            List<InstantiatedEnhSpecRealizItem> enhSpecRealizItems,
            boolean isLocalFacDec) {
        myInstantiatedFacilityDec = dec;
        myConceptDeclaredTypes = conceptDeclaredTypes;
        myConceptParamArgs =
                new FormalActualLists(cFormalParamList, cActualArgList);
        myConceptRealizParamArgs =
                new FormalActualLists(crFormalParamList, crActualArgList);
        myConceptSharedStates = conceptSharedStates;
        myInstantiatedEnhSpecRealizItems = enhSpecRealizItems;
        myIsLocalFacilityDec = isLocalFacDec;
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

        InstantiatedFacilityDecl decl = (InstantiatedFacilityDecl) o;

        if (myIsLocalFacilityDec != decl.myIsLocalFacilityDec)
            return false;
        if (!myConceptDeclaredTypes.equals(decl.myConceptDeclaredTypes))
            return false;
        if (!myConceptParamArgs.equals(decl.myConceptParamArgs))
            return false;
        if (!myConceptRealizParamArgs.equals(decl.myConceptRealizParamArgs))
            return false;
        if (!myConceptSharedStates.equals(decl.myConceptSharedStates))
            return false;
        if (!myInstantiatedEnhSpecRealizItems
                .equals(decl.myInstantiatedEnhSpecRealizItems))
            return false;
        return myInstantiatedFacilityDec.equals(decl.myInstantiatedFacilityDec);
    }

    /**
     * <p>This method the list of {@link TypeFamilyDec TypeFamilyDecs}
     * instantiated by this {@code Facility}.</p>
     *
     * @return A list of {@link TypeFamilyDec}.
     */
    public final List<TypeFamilyDec> getConceptDeclaredTypes() {
        return myConceptDeclaredTypes;
    }

    /**
     * <p>This method returns a {@link FormalActualLists} containing the
     * {@code Concept}'s formal and actual arguments for the instantiated
     * {@code Facility}.</p>
     *
     * @return A {@link FormalActualLists} containing the formal parameters and
     * the instantiation arguments.
     */
    public final FormalActualLists getConceptParamArgLists() {
        return myConceptParamArgs;
    }

    /**
     * <p>This method returns a {@link FormalActualLists} containing the
     * {@code Concept Realization}'s formal and actual arguments for the
     * instantiated {@code Facility}.</p>
     *
     * @return A {@link FormalActualLists} containing the formal parameters and
     * the instantiation arguments.
     */
    public final FormalActualLists getConceptRealizParamArgLists() {
        return myConceptRealizParamArgs;
    }

    /**
     * <p>This method the list of {@link SharedStateDec SharedStateDecs}
     * instantiated by this {@code Facility}.</p>
     *
     * @return A list of {@link SharedStateDec}.
     */
    public final List<SharedStateDec> getConceptSharedStates() {
        return myConceptSharedStates;
    }

    /**
     * <p>This method returns a list of {@link InstantiatedEnhSpecRealizItem} containing
     * the {@code Enhancement's} and {@code Enhancement Realization's} formal and actual arguments
     * for the instantiated {@code Facility}.</p>
     *
     * @return A {@link List} containing {@link InstantiatedEnhSpecRealizItem}.
     */
    public final List<InstantiatedEnhSpecRealizItem> getInstantiatedEnhSpecRealizItems() {
        return myInstantiatedEnhSpecRealizItems;
    }

    /**
     * <p>This method returns the instantiated {@code Facility} declaration.</p>
     *
     * @return A {@link FacilityDec}.
     */
    public final FacilityDec getInstantiatedFacilityDec() {
        return myInstantiatedFacilityDec;
    }

    /**
     * <p>This method returns the instantiated {@code Facility}'s name.</p>
     *
     * @return {@code Facility} name as a {@link PosSymbol}.
     */
    public final PosSymbol getInstantiatedFacilityName() {
        return myInstantiatedFacilityDec.getName();
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myConceptDeclaredTypes.hashCode();
        result = 31 * result + myConceptParamArgs.hashCode();
        result = 31 * result + myConceptRealizParamArgs.hashCode();
        result = 31 * result + myConceptSharedStates.hashCode();
        result = 31 * result + myInstantiatedEnhSpecRealizItems.hashCode();
        result = 31 * result + myInstantiatedFacilityDec.hashCode();
        result = 31 * result + (myIsLocalFacilityDec ? 1 : 0);
        return result;
    }

    /**
     * <p>This method checks to see if this is a local {@code Facility} declaration.</p>
     *
     * @return {@code true} if it is, {@code false} otherwise.
     */
    public final boolean isLocalFacility() {
        return myIsLocalFacilityDec;
    }

}