/**
 * ConceptModuleDec.java
 * ---------------------------------
 * Copyright (c) 2015
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

public class ConceptModuleDec extends AbstractParameterizedModuleDec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The requirement member. */
    private Exp requirement;

    /** The facilityInit member. */
    private InitItem facilityInit;

    /** The facilityFinal member. */
    private FinalItem facilityFinal;

    /** The decs member. */
    private List<Dec> decs;

    /** The constraints member. 
     * Note that placing this down here means it is processed last by the 
     * treewalker, and so we will have access to any definitions in the
     * body of the concept. */
    private List<Exp> constraints;

    public ConceptModuleDec() {
    // Empty
    }

    public ConceptModuleDec(PosSymbol name,
            List<ModuleParameterDec> parameters, List<UsesItem> usesItems,
            Exp requirement, List<Exp> constraints, InitItem facilityInit,
            FinalItem facilityFinal, List<Dec> decs) {
        this.name = name;
        this.parameters = parameters;
        this.usesItems = usesItems;
        this.requirement = requirement;
        this.constraints = constraints;
        this.facilityInit = facilityInit;
        this.facilityFinal = facilityFinal;
        this.decs = decs;
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the requirement variable. */
    public Exp getRequirement() {
        return requirement;
    }

    /** Returns the value of the constraints variable. */
    public List<Exp> getConstraints() {
        return constraints;
    }

    /** Returns the value of the facilityInit variable. */
    public InitItem getFacilityInit() {
        return facilityInit;
    }

    /** Returns the value of the facilityFinal variable. */
    public FinalItem getFacilityFinal() {
        return facilityFinal;
    }

    /** Returns the value of the decs variable. */
    public List<Dec> getDecs() {
        return decs;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the requirement variable to the specified value. */
    public void setRequirement(Exp requirement) {
        this.requirement = requirement;
    }

    /** Sets the constraints variable to the specified value. */
    public void setConstraints(List<Exp> constraints) {
        this.constraints = constraints;
    }

    /** Sets the facilityInit variable to the specified value. */
    public void setFacilityInit(InitItem facilityInit) {
        this.facilityInit = facilityInit;
    }

    /** Sets the facilityFinal variable to the specified value. */
    public void setFacilityFinal(FinalItem facilityFinal) {
        this.facilityFinal = facilityFinal;
    }

    /** Sets the decs variable to the specified value. */
    public void setDecs(List<Dec> decs) {
        this.decs = decs;
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitConceptModuleDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ConceptModuleDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (parameters != null) {
            sb.append(parameters.asString(indent + increment, increment));
        }

        if (usesItems != null) {
            sb.append(usesItems.asString(indent + increment, increment));
        }

        if (requirement != null) {
            sb.append(requirement.asString(indent + increment, increment));
        }

        if (constraints != null) {
            sb.append(constraints.asString(indent + increment, increment));
        }

        if (facilityInit != null) {
            sb.append(facilityInit.asString(indent + increment, increment));
        }

        if (facilityFinal != null) {
            sb.append(facilityFinal.asString(indent + increment, increment));
        }

        if (decs != null) {
            sb.append(decs.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public String toString() {
        return name.toString();
    }
}
