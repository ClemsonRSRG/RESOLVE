/**
 * TypeResolutionException.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.analysis;

import edu.clemson.cs.r2jt.absyn.Exp;

public class TypeResolutionException extends Exception {

    /**
     * <p>The expression we failed to determine the type of, or possibly
     * <code>null</code> if this information is unavailable.</p>
     */
    private final Exp myExp;

    // ==========================================================
    // Constructors
    // ==========================================================

    public TypeResolutionException() {
        myExp = null;
    }

    public TypeResolutionException(Exp e) {
        myExp = e;
    }

    /**
     * <p>Returns the expression whose type could not be determined, or possibly
     * <code>null</code> if this information is unavailable.</p>
     * 
     * @return  The expression whose type could not be determined, or 
     *          <code>null</code> if this information is unavailable.
     */
    public Exp getExp() {
        return myExp;
    }

    public TypeResolutionException(String msg) {
        super(msg);
        myExp = null;
    }

    public String toString() {
        String retval;

        if (myExp == null) {
            retval = "TypeResolutionException";
        }
        else {
            retval =
                    "TypeResolutionException: Could not return the type of:\n"
                            + myExp.toString(5) + "\nAt:\n     "
                            + myExp.getLocation();
        }

        return retval;
    }
}
