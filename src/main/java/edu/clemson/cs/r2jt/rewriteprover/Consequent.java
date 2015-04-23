/**
 * Consequent.java
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
package edu.clemson.cs.r2jt.rewriteprover;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

public class Consequent extends ImmutableConjuncts {

    public Consequent(Exp e) {
        super(e);
    }

    public Consequent(PExp e) {
        super(e);
    }

    public Consequent(Iterable<PExp> i) {
        super(i);
    }

    @Override
    public Consequent substitute(Map<PExp, PExp> mapping) {
        ImmutableConjuncts genericRetval = super.substitute(mapping);
        return new Consequent(genericRetval);
    }

    @Override
    public Consequent appended(Iterable<PExp> i) {
        ImmutableConjuncts genericRetval = super.appended(i);
        return new Consequent(genericRetval);
    }

    @Override
    public Consequent eliminateObviousConjuncts() {
        ImmutableConjuncts genericRetval = super.eliminateObviousConjuncts();
        return new Consequent(genericRetval);
    }

    @Override
    public Consequent removed(int index) {
        ImmutableConjuncts genericRetval = super.removed(index);
        return new Consequent(genericRetval);
    }

    @Override
    public Consequent eliminateRedundantConjuncts() {
        ImmutableConjuncts genericRetval = super.eliminateRedundantConjuncts();
        return new Consequent(genericRetval);
    }

    public Antecedent assumed() {
        return new Antecedent(this);
    }

    public String negateToSMT(Map<String, MTType> typeMap)
            throws RuntimeException {
        String rString = "";
        if (this.size() != 1) {
            throw new RuntimeException("Invalid Consequent");
        }
        rString = "(assert(not " + this.get(0).toSMTLIB(typeMap) + "))\n";
        return rString;
    }
}
