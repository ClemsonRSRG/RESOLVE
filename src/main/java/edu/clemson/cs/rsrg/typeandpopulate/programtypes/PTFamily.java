/**
 * PTFamily.java
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
package edu.clemson.cs.rsrg.typeandpopulate.programtypes;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSolutionException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTNamed;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.Map;

/**
 * <p>Represents a <em>type family</em> as would be introduced inside a concept.
 * This is an abstract program type without a realization and without parameters
 * instantiated.</p>
 *
 * @version 2.0
 */
public class PTFamily extends PTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The type's mathematical model.</p> */
    private final MTType myModel;

    /** <p>Name associated with this type.</p> */
    private final String myName;

    /** <p>Exemplar associated with this type.</p> */
    private final String myExemplarName;

    /** <p>Constraint associated with this type.</p> */
    private final AssertionClause myConstraint;

    /** <p>Initialization ensures associated with this type.</p> */
    private final AssertionClause myInitializationEnsures;

    /** <p>Finalization ensures associated with this type.</p> */
    private final AssertionClause myFinalizationEnsures;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a program type using a type family.</p>
     *
     * @param model The type family's mathematical model.
     * @param familyName The type family's name.
     * @param exemplarName The type family's exemplar name.
     * @param constraint The type family's type constraint.
     * @param initializationEnsures The type family's initialization ensures clause.
     * @param finalizationEnsures The type family's finalization ensures clause.
     */
    public PTFamily(MTType model, String familyName, String exemplarName,
            AssertionClause constraint, AssertionClause initializationEnsures,
            AssertionClause finalizationEnsures) {
        super(model.getTypeGraph());

        myName = familyName;
        myModel = model;
        myExemplarName = exemplarName;
        myConstraint = constraint;
        myInitializationEnsures = initializationEnsures;
        myFinalizationEnsures = finalizationEnsures;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default equals method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        boolean result = (o instanceof PTFamily);

        if (result) {
            PTFamily oAsPTFamily = (PTFamily) o;

            result =
                    (myModel.equals(oAsPTFamily.myModel))
                            && (myName.equals(oAsPTFamily.myName))
                            && (myExemplarName
                                    .equals(oAsPTFamily.myExemplarName))
                            && (myConstraint.equals(oAsPTFamily.myConstraint))
                            && (myInitializationEnsures
                                    .equals(oAsPTFamily.myInitializationEnsures))
                            && (myFinalizationEnsures
                                    .equals(oAsPTFamily.myFinalizationEnsures));
        }

        return result;
    }

    /**
     * <p>Since this is used by multiple objects, we really don't want to be returning a reference,
     * therefore this method returns a deep copy of the constraint clause.</p>
     *
     * @return A {@link AssertionClause} representation object.
     */
    public final AssertionClause getConstraint() {
        return myConstraint.clone();
    }

    /**
     * <p>This method returns the exemplar name associated with this type.</p>
     *
     * @return A string.
     */
    public final String getExemplarName() {
        return myExemplarName;
    }

    /**
     * <p>Since this is used by multiple objects, we really don't want to be returning a reference,
     * therefore this method returns a deep copy of the finalization ensures clause.</p>
     *
     * @return A {@link AssertionClause} representation object.
     */
    public final AssertionClause getFinalizationEnsures() {
        return myFinalizationEnsures.clone();
    }

    /**
     * <p>Since this is used by multiple objects, we really don't want to be returning a reference,
     * therefore this method returns a deep copy of the initialization ensures clause.</p>
     *
     * @return A {@link AssertionClause} representation object.
     */
    public final AssertionClause getInitializationEnsures() {
        return myInitializationEnsures.clone();
    }

    /**
     * <p>This method returns the name associated with this type.</p>
     *
     * @return A string.
     */
    public final String getName() {
        return myName;
    }

    /**
     * <p>This method converts a generic {@link PTType} to a program type
     * that has all the generic types and variables replaced with actual
     * values.</p>
     *
     * @param genericInstantiations Map containing all the instantiations.
     * @param instantiatingFacility Facility that instantiated this type.
     *
     * @return A {@link PTType} that has been instantiated.
     */
    @Override
    public final PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        Map<String, MTType> stringToMathType =
                SymbolTableEntry.buildMathTypeGenerics(genericInstantiations);

        Map<MTNamed, MTType> mathTypeToMathType =
                MTNamed.toMTNamedMap(getTypeGraph(), stringToMathType);

        MTType newModel =
                myModel.getCopyWithVariablesSubstituted(stringToMathType);

        // Obtain the new assertion clauses with the new types.
        AssertionClause newConstraint =
                withTypesSubstituted(myConstraint, mathTypeToMathType);
        AssertionClause newInitializationEnsures =
                withTypesSubstituted(myInitializationEnsures,
                        mathTypeToMathType);
        AssertionClause newFinalizationEnsures =
                withTypesSubstituted(myFinalizationEnsures, mathTypeToMathType);

        return new PTFamily(newModel, myName, myExemplarName, newConstraint,
                newInitializationEnsures, newFinalizationEnsures);
    }

    /**
     * <p>This method returns the mathematical type associated with this program type.</p>
     *
     * @return A {@link MTType} representation object.
     */
    @Override
    public final MTType toMath() {
        return myModel;
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return myName;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method to that creates a new {@link AssertionClause} with the
     * provided instantiated types.</p>
     *
     * @param original Original assertion clause.
     * @param mathTypeToMathType A map of generic to instantiated types.
     *
     * @return A new {@link AssertionClause} with the new types.
     */
    private AssertionClause withTypesSubstituted(AssertionClause original,
            Map<MTNamed, MTType> mathTypeToMathType) {
        Exp originalExp = original.getAssertionExp();
        Exp newExp = originalExp.clone();
        MTNamed oldType = (MTNamed) originalExp.getMathType();
        newExp.setMathType(mathTypeToMathType.get(oldType));

        // TODO: Figure out what to do when the MathTypeValue is not null
        if (originalExp.getMathTypeValue() != null) {
            throw new NoSolutionException("Don't know what to do here!",
                    new IllegalStateException());
        }

        // Clone the which_entails clause if there is one
        Exp newWhichEntails = null;
        if (original.getWhichEntailsExp() != null) {
            newWhichEntails = original.getWhichEntailsExp().clone();
        }

        return new AssertionClause(original.getLocation().clone(), original
                .getClauseType(), newExp, newWhichEntails);
    }

}