/**
 * Implication.java
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
package edu.clemson.cs.r2jt.proving;

import edu.clemson.cs.r2jt.absyn.Exp;

public class Implication {

    private Exp myAntecedent;
    private Exp myConsequent;

    public Implication(Exp antecedent, Exp consequent) {
        myAntecedent = antecedent;
        myConsequent = consequent;
    }

    public Exp getAntecedent() {
        return myAntecedent;
    }

    public Exp getConsequent() {
        return myConsequent;
    }

    public String toString() {
        return myAntecedent + " --> " + myConsequent;
    }
}
