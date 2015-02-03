/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
