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
    private final List<Exp> myPrecursorClauses;
    private final Exp myResultantClause;
    private final String mySourceTheoremName;
    private final String mySourceModuleName;
    private final String mySourceTheoremOperator;

    public ElaborationRule(List<Exp> precursorClauses, Exp resultantClause, String sourceTheoremName,
            String sourceModuleName, String sourceTheoremOperator) {
        myPrecursorClauses = precursorClauses;
        myResultantClause = resultantClause;
        this.mySourceTheoremName = sourceTheoremName;
        this.mySourceModuleName = sourceModuleName;
        this.mySourceTheoremOperator = sourceTheoremOperator;
    }

    public List<Exp> getPrecursorClauses() {
        return myPrecursorClauses;
    }

    public Exp getResultantClause() {
        return myResultantClause;
    }

    public String getSourceTheoremName() {
        return mySourceTheoremName;
    }

    public String getSourceModuleName() {
        return mySourceModuleName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mySourceModuleName != null) {
            sb.append("Source Theorem: ");
            sb.append("\u001B[33m");
            sb.append(mySourceTheoremName);
            sb.append("\u001B[0m");
            sb.append(" (from ");
            sb.append("\u001B[34m");
            sb.append(mySourceModuleName);
            sb.append("\u001B[0m");
            sb.append(")\n");
        } else {
            sb.append("No Source Theorem for this Rule \n");
        }
        sb.append("\n\n Precursor Clauses: ");

        sb.append(" [\n");
        for (int i = 0; i < myPrecursorClauses.size(); i++) {
            Exp exp = myPrecursorClauses.get(i);
            sb.append("  ");
            sb.append(exp);
            sb.append("; Matching: ").append(exp.getSide());
            if (i < myPrecursorClauses.size() - 1)
                sb.append(",\n");
        }
        sb.append("\n ]");
        sb.append("\n Source Operator: ");
        sb.append(mySourceTheoremOperator);
        sb.append("\n Resultant Clause: ");
        sb.append(myResultantClause.toString());
        sb.append(" Matching: ").append(myResultantClause.getSide());
        sb.append("\u001B[0m");
        sb.append("\n\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ElaborationRule other) {
            return myPrecursorClauses.equals(other.myPrecursorClauses)
                    && myResultantClause.equals(other.myResultantClause)
                    && mySourceModuleName.equals(other.mySourceModuleName)
                    && mySourceTheoremName.equals(other.mySourceTheoremName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return myPrecursorClauses.hashCode() + (myResultantClause.hashCode() * 10)
                + (mySourceModuleName.hashCode() * 100) + (mySourceTheoremName.hashCode() * 1000);
    }
}
