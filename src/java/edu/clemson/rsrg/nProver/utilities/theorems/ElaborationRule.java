/*
 * ElaborationRule.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.nProver.utilities.theorems;

import edu.clemson.rsrg.absyn.expressions.Exp;

import java.util.List;

public class ElaborationRule {
    private List<Exp> myPrecursorClauses;
    private Exp myResultantClause;

    public ElaborationRule(List<Exp> precursorClauses, Exp resultantClause) {
        myPrecursorClauses = precursorClauses;
        myResultantClause = resultantClause;
    }

    public List<Exp> getPrecursorClauses() {
        return myPrecursorClauses;
    }

    public Exp getResultantClause() {
        return myResultantClause;
    }

}
