/**
 * TypeInitFinalItem.java
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
package edu.clemson.cs.rsrg.absyn.items.programitems;

import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the type initialization/finalization block objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class TypeInitFinalItem extends ResolveConceptualElement {

    // ===========================================================
    // ItemType
    // ===========================================================

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

    /** <p>The affects clause.</p> */
    private final AffectsClause myAffects;

    /** <p>List of facility declarations.</p> */
    private final List<FacilityDec> myFacilityDecs;

    /** <p>The type of clause</p> */
    private final ItemType myItemType;

    /** <p>List of statements.</p> */
    private final List<Statement> myStatements;

    /** <p>List of variable declarations.</p> */
    private final List<VarDec> myVariableDecs;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type initialization/finalization block that happens
     * when a variable of this type is initialized/finalized.</p>
     *
     * @param l A {@link Location} representation object.
     * @param type Indicates if it is an initialization or finalization block.
     * @param affects A {@link AffectsClause} representing the initialization's/finalization's
     *                affects clause.
     * @param facilities List of facility declarations in this block.
     * @param variables List of variables in this block.
     * @param statements List of statements in this block.
     */
    public TypeInitFinalItem(Location l, ItemType type, AffectsClause affects,
            List<FacilityDec> facilities, List<VarDec> variables,
            List<Statement> statements) {
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
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        sb.append(myItemType.toString());
        sb.append("\n");

        // affects clause
        if (myAffects != null) {
            sb.append(myAffects.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        Iterator<FacilityDec> it1 = myFacilityDecs.iterator();
        while (it1.hasNext()) {
            sb.append(it1.next().asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        Iterator<VarDec> it2 = myVariableDecs.iterator();
        while (it2.hasNext()) {
            sb.append(it2.next().asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        Iterator<Statement> it4 = myStatements.iterator();
        while (it4.hasNext()) {
            sb.append(it4.next().asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        sb.append("end\n");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final TypeInitFinalItem clone() {
        AffectsClause newAffects = null;
        if (myAffects != null) {
            newAffects = myAffects.clone();
        }

        return new TypeInitFinalItem(new Location(myLoc), myItemType,
                newAffects, copyFacDecs(), copyVars(), copyStatements());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TypeInitFinalItem that = (TypeInitFinalItem) o;

        if (myAffects != null ? !myAffects.equals(that.myAffects)
                : that.myAffects != null)
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
     * <p>This method returns the affects clause
     * in this type initialization/finalization block.</p>
     *
     * @return The {@link AffectsClause} representation object.
     */
    public final AffectsClause getAffectedVars() {
        return myAffects;
    }

    /**
     * <p>This method returns the list of facility declarations
     * in this type initialization/finalization block.</p>
     *
     * @return A list of {@link FacilityDec} representation objects.
     */
    public final List<FacilityDec> getFacilities() {
        return myFacilityDecs;
    }

    /**
     * <p>This method returns the item type.</p>
     *
     * @return The {@link ItemType} object.
     */
    public final ItemType getItemType() {
        return myItemType;
    }

    /**
     * <p>This method returns the list of statements
     * in this type initialization/finalization block.</p>
     *
     * @return A list of {@link Statement} representation objects.
     */
    public final List<Statement> getStatements() {
        return myStatements;
    }

    /**
     * <p>This method returns the list of variables
     * in this type initialization/finalization block.</p>
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
    public final int hashCode() {
        int result = myAffects != null ? myAffects.hashCode() : 0;
        result = 31 * result + myFacilityDecs.hashCode();
        result = 31 * result + myItemType.hashCode();
        result = 31 * result + myStatements.hashCode();
        result = 31 * result + myVariableDecs.hashCode();
        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the facility declarations.</p>
     *
     * @return A list containing {@link FacilityDec}s.
     */
    private List<FacilityDec> copyFacDecs() {
        List<FacilityDec> copyArgs = new ArrayList<>();
        for (FacilityDec f : myFacilityDecs) {
            copyArgs.add((FacilityDec) f.clone());
        }

        return copyArgs;
    }

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the statements.</p>
     *
     * @return A list containing {@link Statement}s.
     */
    private List<Statement> copyStatements() {
        List<Statement> copyArgs = new ArrayList<>();
        for (Statement s : myStatements) {
            copyArgs.add(s.clone());
        }

        return copyArgs;
    }

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the variable declarations.</p>
     *
     * @return A list containing {@link VarDec}s.
     */
    private List<VarDec> copyVars() {
        List<VarDec> copyArgs = new ArrayList<>();
        for (VarDec v : myVariableDecs) {
            copyArgs.add((VarDec) v.clone());
        }

        return copyArgs;
    }
}