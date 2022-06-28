/*
 * AbstractInitFinalItem.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn.items.programitems;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.rsrg.absyn.statements.Statement;
import edu.clemson.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This is the abstract base class for all the initialization/finalization block objects that the compiler builds using
 * the ANTLR4 AST nodes.
 * </p>
 *
 * @version 1.0
 */
public abstract class AbstractInitFinalItem extends ResolveConceptualElement {

    // ===========================================================
    // ItemType
    // ===========================================================

    /**
     * <p>
     * This defines the various different programming item types.
     * </p>
     *
     * @version 2.0
     */
    public enum ItemType {
        INITIALIZATION {

            @Override
            public String toString() {
                return "initialization";
            }

        },
        FINALIZATION {

            @Override
            public String toString() {
                return "finalization";
            }

        }
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The affects clause.
     * </p>
     */
    protected final AffectsClause myAffects;

    /**
     * <p>
     * List of facility declarations.
     * </p>
     */
    protected final List<FacilityDec> myFacilityDecs;

    /**
     * <p>
     * The type of clause
     * </p>
     */
    protected final ItemType myItemType;

    /**
     * <p>
     * List of statements.
     * </p>
     */
    protected final List<Statement> myStatements;

    /**
     * <p>
     * List of variable declarations.
     * </p>
     */
    protected final List<VarDec> myVariableDecs;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * An helper constructor that allow us to store the location, type, affects clause, facilities, variables and
     * statements of any objects created from a class that inherits from {@code AbstractTypeInitFinalItem}.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param type
     *            Indicates if it is an initialization or finalization block.
     * @param affects
     *            A {@link AffectsClause} representing the initialization's/finalization's affects clause.
     * @param facilities
     *            List of facility declarations in this block.
     * @param variables
     *            List of variables in this block.
     * @param statements
     *            List of statements in this block.
     */
    protected AbstractInitFinalItem(Location l, ItemType type, AffectsClause affects, List<FacilityDec> facilities,
            List<VarDec> variables, List<Statement> statements) {
        super(l);
        myAffects = affects;
        myFacilityDecs = facilities;
        myItemType = type;
        myStatements = statements;
        myVariableDecs = variables;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AbstractInitFinalItem that = (AbstractInitFinalItem) o;

        if (myAffects != null ? !myAffects.equals(that.myAffects) : that.myAffects != null)
            return false;
        if (!myFacilityDecs.equals(that.myFacilityDecs))
            return false;
        if (myItemType != that.myItemType)
            return false;
        if (!myStatements.equals(that.myStatements))
            return false;
        return myVariableDecs.equals(that.myVariableDecs);

    }

    /**
     * <p>
     * This method returns the affects clause in this type initialization/finalization block.
     * </p>
     *
     * @return The {@link AffectsClause} representation object.
     */
    public final AffectsClause getAffectedVars() {
        return myAffects;
    }

    /**
     * <p>
     * This method returns the list of facility declarations in this type initialization/finalization block.
     * </p>
     *
     * @return A list of {@link FacilityDec} representation objects.
     */
    public final List<FacilityDec> getFacilities() {
        return myFacilityDecs;
    }

    /**
     * <p>
     * This method returns the item type.
     * </p>
     *
     * @return The {@link ItemType} object.
     */
    public final ItemType getItemType() {
        return myItemType;
    }

    /**
     * <p>
     * This method returns the list of statements in this type initialization/finalization block.
     * </p>
     *
     * @return A list of {@link Statement} representation objects.
     */
    public final List<Statement> getStatements() {
        return myStatements;
    }

    /**
     * <p>
     * This method returns the list of variables in this type initialization/finalization block.
     * </p>
     *
     * @return A list of {@link VarDec} representation objects.
     */
    public final List<VarDec> getVariables() {
        return myVariableDecs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = myAffects != null ? myAffects.hashCode() : 0;
        result = 31 * result + myFacilityDecs.hashCode();
        result = 31 * result + myItemType.hashCode();
        result = 31 * result + myStatements.hashCode();
        result = 31 * result + myVariableDecs.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the facility declarations.
     * </p>
     *
     * @return A list containing {@link FacilityDec}s.
     */
    protected final List<FacilityDec> copyFacDecs() {
        List<FacilityDec> copyArgs = new ArrayList<>();
        for (FacilityDec f : myFacilityDecs) {
            copyArgs.add((FacilityDec) f.clone());
        }

        return copyArgs;
    }

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the statements.
     * </p>
     *
     * @return A list containing {@link Statement}s.
     */
    protected final List<Statement> copyStatements() {
        List<Statement> copyArgs = new ArrayList<>();
        for (Statement s : myStatements) {
            copyArgs.add(s.clone());
        }

        return copyArgs;
    }

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the variable declarations.
     * </p>
     *
     * @return A list containing {@link VarDec}s.
     */
    protected final List<VarDec> copyVars() {
        List<VarDec> copyArgs = new ArrayList<>();
        for (VarDec v : myVariableDecs) {
            copyArgs.add((VarDec) v.clone());
        }

        return copyArgs;
    }
}
