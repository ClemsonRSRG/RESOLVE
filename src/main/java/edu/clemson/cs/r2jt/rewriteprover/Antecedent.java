/**
 * Antecedent.java
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
package edu.clemson.cs.r2jt.rewriteprover;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.LambdaExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;

import java.util.HashSet;

public class Antecedent extends ImmutableConjuncts {

    public static final Antecedent EMPTY = new Antecedent();

    public Antecedent(Exp e) {
        super(e);
    }

    public Antecedent(PExp e) {
        super(e);
    }

    public Antecedent(Iterable<PExp> i) {
        super(i);
    }

    private Antecedent() {
        super();
    }
}
