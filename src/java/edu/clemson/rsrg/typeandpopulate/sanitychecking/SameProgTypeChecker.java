/*
 * SameProgTypeChecker.java
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
package edu.clemson.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.rsrg.typeandpopulate.exception.TypeMismatchException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTNamed;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTRepresentation;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;

/**
 * <p>
 * This is a sanity checker for making sure our the two programming types are the same. This is a requirement before we
 * can safely execute a swap or function assignment.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class SameProgTypeChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Programming expression on the left
     * </p>
     */
    private final ProgramExp myLeftExp;

    /**
     * <p>
     * Programming expression on the right
     * </p>
     */
    private final ProgramExp myRightExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates a sanity checker for checking if the left expression's programming type matches the right expression's
     * programming type.
     * </p>
     *
     * @param leftExp
     *            The programming expression on the left.
     * @param rightExp
     *            The programming expression on the right.
     */
    public SameProgTypeChecker(ProgramExp leftExp, ProgramExp rightExp) {
        myLeftExp = leftExp;
        myRightExp = rightExp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method indicates whether the programming types match.
     * </p>
     */
    public final void hasSameProgrammingType() {
        if (!extractProgramType(myLeftExp.getProgramType()).equals(extractProgramType(myRightExp.getProgramType()))) {
            throw new TypeMismatchException("Non-matching programming types. [" + myLeftExp.getLocation() + "]"
                    + "\nLeft: " + myLeftExp.getProgramType() + "\nRight: " + myRightExp.getProgramType());
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for extracting the proper program type to be compared.
     * </p>
     *
     * @param type
     *            A potentially instantiated {@link PTType}.
     *
     * @return The actual {@link PTType}.
     */
    private PTType extractProgramType(PTType type) {
        PTType retType;

        if (type instanceof PTNamed) {
            retType = ((PTNamed) type).getInstantiatedFamilyType();
        } else if (type instanceof PTRepresentation) {
            retType = ((PTRepresentation) type).getFamily().getProgramType();
        } else {
            retType = type;
        }

        return retType;
    }
}
