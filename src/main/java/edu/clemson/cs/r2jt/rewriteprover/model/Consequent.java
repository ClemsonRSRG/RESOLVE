/*
 * Consequent.java
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
package edu.clemson.cs.r2jt.rewriteprover.model;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import java.util.Collections;

/**
 *
 * @author hamptos
 */
public class Consequent implements Conjunct {

    private PExp myExp;

    Consequent(PExp exp) {
        this.myExp = exp;
    }

    @Override
    public Site toSite(PerVCProverModel m) {
        return new Site(m, this, Collections.EMPTY_LIST, myExp);
    }

    @Override
    public PExp getExpression() {
        return myExp;
    }

    @Override
    public void setExpression(PExp newValue) {
        myExp = newValue;
    }

    @Override
    public String toString() {
        return "" + myExp;
    }

    @Override
    public boolean editable() {
        return true;
    }

    @Override
    public boolean libraryTheorem() {
        return false;
    }
}
