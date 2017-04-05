/*
 * SameProgTypeChecker.java
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
package edu.clemson.cs.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.typeandpopulate.exception.TypeMismatchException;

/**
 * <p>This is a sanity checker for making sure our the two programming types are
 * the same. This is a requirement before we can safely execute a swap or function
 * assignment.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SameProgTypeChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Programming expression on the left</p> */
    private final ProgramExp myLeftExp;

    /** <p>Programming expression on the right</p> */
    private final ProgramExp myRightExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Creates a sanity checker for checking if the left expression's
     * programming type matches the right expression's programming type.</p>
     *
     * @param leftExp The programming expression on the left.
     * @param rightExp The programming expression on the right.
     */
    public SameProgTypeChecker(ProgramExp leftExp, ProgramExp rightExp) {
        myLeftExp = leftExp;
        myRightExp = rightExp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method indicates whether the programming types match.</p>
     */
    public final void hasSameProgrammingType() {
        if (!myLeftExp.getProgramType().equals(myRightExp.getProgramType())) {
            throw new TypeMismatchException("Non-matching programming types."
                    + "\nLeft: " + myLeftExp.getProgramType() + "\nRight: "
                    + myRightExp.getProgramType());
        }
    }
}