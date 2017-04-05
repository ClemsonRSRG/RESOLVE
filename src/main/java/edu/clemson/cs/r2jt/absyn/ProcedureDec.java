/*
 * ProcedureDec.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.collections.Iterator;

public class ProcedureDec extends Dec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The parameters member. */
    private List<ParameterVarDec> parameters;

    /** The returnTy member. */
    private Ty returnTy;

    /** The stateVars member. */
    private List<AffectsItem> stateVars;

    /** The decreasing member. */
    private Exp decreasing;

    /** The facilities member. */
    private List<FacilityDec> facilities;

    /** The variables member. */
    private List<VarDec> variables;

    /** The variables member. */
    private List<AuxVarDec> aux_variables;

    /** The statements member. */
    private List<Statement> statements;

    /** The recursive member. */
    private boolean recursive;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProcedureDec() {};

    public ProcedureDec(PosSymbol name, List<ParameterVarDec> parameters,
            Ty returnTy, List<AffectsItem> stateVars, Exp decreasing,
            List<FacilityDec> facilities, List<VarDec> variables,
            List<AuxVarDec> aux_variables, List<Statement> statements) {
        this.name = name;
        this.parameters = parameters;
        this.returnTy = returnTy;
        this.stateVars = stateVars;
        this.decreasing = decreasing;
        this.facilities = facilities;
        this.variables = variables;
        this.aux_variables = aux_variables;
        this.statements = statements;
        this.recursive = false;
    }

    public ProcedureDec(PosSymbol name, List<ParameterVarDec> parameters,
            Ty returnTy, List<AffectsItem> stateVars, Exp decreasing,
            List<FacilityDec> facilities, List<VarDec> variables,
            List<AuxVarDec> aux_variables, List<Statement> statements,
            boolean recursive) {
        this.name = name;
        this.parameters = parameters;
        this.returnTy = returnTy;
        this.stateVars = stateVars;
        this.decreasing = decreasing;
        this.facilities = facilities;
        this.variables = variables;
        this.aux_variables = aux_variables;
        this.statements = statements;
        this.recursive = recursive;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the recursive variable. */
    public boolean getRecursive() {
        return recursive;
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the parameters variable. */
    public List<ParameterVarDec> getParameters() {
        return parameters;
    }

    /** Returns the value of the returnTy variable. */
    public Ty getReturnTy() {
        return returnTy;
    }

    /** Returns the value of the stateVars variable. */
    public List<AffectsItem> getStateVars() {
        return stateVars;
    }

    /** Returns the value of the decreasing variable. */
    public Exp getDecreasing() {
        return decreasing;
    }

    /** Returns the value of the facilities variable. */
    public List<FacilityDec> getFacilities() {
        return facilities;
    }

    /** Returns the value of the variables variable. */
    public List<VarDec> getVariables() {
        return variables;
    }

    /** Returns the value of the variables variable. */
    public List<AuxVarDec> getAuxVariables() {
        return aux_variables;
    }

    /** Returns the value of the statements variable. */
    public List<Statement> getStatements() {
        return statements;
    }

    /** Returns the value of the variables variable. */
    public List<VarDec> getAllVariables() {
        List<VarDec> vars = new List<VarDec>();

        Iterator<VarDec> j;
        j = variables.iterator();
        while (j.hasNext()) {
            VarDec tmpAV = j.next();
            VarDec tmp = new VarDec(tmpAV.getName(), tmpAV.getTy());
            vars.add(tmp);
        }
        Iterator<AuxVarDec> i;
        i = aux_variables.iterator();
        while (i.hasNext()) {
            AuxVarDec tmpAV = i.next();
            VarDec tmp = new VarDec(tmpAV.getName(), tmpAV.getTy());
            vars.add(tmp);
        }
        return vars;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the parameters variable to the specified value. */
    public void setParameters(List<ParameterVarDec> parameters) {
        this.parameters = parameters;
    }

    /** Sets the returnTy variable to the specified value. */
    public void setReturnTy(Ty returnTy) {
        this.returnTy = returnTy;
    }

    /** Sets the stateVars variable to the specified value. */
    public void setStateVars(List<AffectsItem> stateVars) {
        this.stateVars = stateVars;
    }

    /** Sets the decreasing variable to the specified value. */
    public void setDecreasing(Exp decreasing) {
        this.decreasing = decreasing;
    }

    /** Sets the facilities variable to the specified value. */
    public void setFacilities(List<FacilityDec> facilities) {
        this.facilities = facilities;
    }

    /** Sets the variables variable to the specified value. */
    public void setVariables(List<VarDec> variables) {
        this.variables = variables;
    }

    /** Sets the variables variable to the specified value. */
    public void setAuxVariables(List<AuxVarDec> aux_variables) {
        this.aux_variables = aux_variables;
    }

    /** Sets the statements variable to the specified value. */
    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitProcedureDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ProcedureDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (parameters != null) {
            sb.append(parameters.asString(indent + increment, increment));
        }

        if (returnTy != null) {
            sb.append(returnTy.asString(indent + increment, increment));
        }

        if (stateVars != null) {
            sb.append(stateVars.asString(indent + increment, increment));
        }

        if (decreasing != null) {
            sb.append(decreasing.asString(indent + increment, increment));
        }

        if (facilities != null) {
            sb.append(facilities.asString(indent + increment, increment));
        }

        if (variables != null) {
            sb.append(variables.asString(indent + increment, increment));
        }

        if (statements != null) {
            sb.append(statements.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
