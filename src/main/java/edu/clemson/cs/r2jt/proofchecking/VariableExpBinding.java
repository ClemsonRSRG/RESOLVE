/**
 * VariableExpBinding.java
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
package edu.clemson.cs.r2jt.proofchecking;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.VarExp;
import edu.clemson.cs.r2jt.type.Type;

public class VariableExpBinding {

    private VarExp var;

    private Exp exp;

    // ==========================================================
    // Constructors
    // ==========================================================

    public VariableExpBinding(VarExp var, Exp exp) {
        this.var = var;
        this.exp = exp;
    }

    public void setVarExp(VarExp var) {
        this.var = var;
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public String getVarName() {
        return var.getName().getName();
    }

    public Type getVarType() {
        return var.getType();
    }

    public VarExp getVarExp() {
        return var;
    }

    public Type getExpType() {
        return exp.getType();
    }

    public Exp getExp() {
        return exp;
    }

    public void prettyPrint() {
        var.prettyPrint();
        System.out.print(" maps to ");
        exp.prettyPrint();
        System.out.println("");
    }

}
