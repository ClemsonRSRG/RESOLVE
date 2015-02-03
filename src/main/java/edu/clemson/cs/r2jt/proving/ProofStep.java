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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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
package edu.clemson.cs.r2jt.proving;

import java.util.List;

import edu.clemson.cs.r2jt.absyn.Exp;

public class ProofStep {

    private String myDescription;
    private List<Exp> myAlteredConfirms;

    private VerificationCondition myVC;

    public ProofStep(String description, List<Exp> confirms) {
        myDescription = description;
        myAlteredConfirms = confirms;
    }

    public ProofStep(String description, VerificationCondition vC) {
        myDescription = description;
        myVC = vC;
    }

    public String toString() {
        String retval = "\nApplied: " + myDescription + "\n\n";

        if (myVC == null) {
            retval += debugPrintConjuncts(myAlteredConfirms);
        }
        else {
            retval += myVC;
        }
        return "\nApplied: " + myDescription + "\n\n"
                + debugPrintConjuncts(myAlteredConfirms);
    }

    private static String debugPrintConjuncts(List<Exp> conjuncts) {
        String retval = "";
        boolean first = true;
        for (Exp e : conjuncts) {
            if (!first) {
                retval += " and ";
            }
            retval += e.toString(0);
            first = false;
        }
        retval += "\n";
        return retval;
    }
}
