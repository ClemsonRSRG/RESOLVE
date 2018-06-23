/*
 * InitItem.java
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
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;

public class InitItem extends ResolveConceptualElement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The stateVars member. */
    private List<AffectsItem> stateVars;

    /** The requires member. */
    private Exp requires;

    /** The ensures member. */
    private Exp ensures;

    /** The facilities member. */
    private List<FacilityDec> facilities;

    /** The variables member. */
    private List<VarDec> variables;

    /** The variables member. */
    private List<AuxVarDec> aux_variables;

    /** The statements member. */
    private List<Statement> statements;

    // ===========================================================
    // Constructors
    // ===========================================================

    public InitItem() {};

    public InitItem(Location location, List<AffectsItem> stateVars,
            Exp requires, Exp ensures, List<FacilityDec> facilities,
            List<VarDec> variables, List<AuxVarDec> aux_variables,
            List<Statement> statements) {
        this.location = location;
        this.stateVars = stateVars;
        this.requires = requires;
        this.ensures = ensures;
        this.facilities = facilities;
        this.variables = variables;
        this.variables = variables;
        this.statements = statements;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the stateVars variable. */
    public List<AffectsItem> getStateVars() {
        return stateVars;
    }

    /** Returns the value of the requires variable. */
    public Exp getRequires() {
        return requires;
    }

    /** Returns the value of the ensures variable. */
    public Exp getEnsures() {
        return ensures;
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

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the stateVars variable to the specified value. */
    public void setStateVars(List<AffectsItem> stateVars) {
        this.stateVars = stateVars;
    }

    /** Sets the requires variable to the specified value. */
    public void setRequires(Exp requires) {
        this.requires = requires;
    }

    /** Sets the ensures variable to the specified value. */
    public void setEnsures(Exp ensures) {
        this.ensures = ensures;
    }

    /** Sets the facilities variable to the specified value. */
    public void setFacilities(List<FacilityDec> facilities) {
        this.facilities = facilities;
    }

    /** Sets the variables variable to the specified value. */
    public void setVariables(List<VarDec> variables) {
        this.variables = variables;
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
        v.visitInitItem(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("InitItem\n");

        if (stateVars != null) {
            sb.append(stateVars.asString(indent + increment, increment));
        }

        if (requires != null) {
            sb.append(requires.asString(indent + increment, increment));
        }

        if (ensures != null) {
            sb.append(ensures.asString(indent + increment, increment));
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
