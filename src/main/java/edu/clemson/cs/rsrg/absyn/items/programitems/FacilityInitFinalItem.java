/*
 * FacilityInitFinalItem.java
 * ---------------------------------
 * Copyright (c) 2020
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
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.List;

/**
 * <p>
 * This is the class for all the facility type initialization/finalization block
 * objects that the
 * compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 1.0
 */
public class FacilityInitFinalItem extends AbstractInitFinalItem {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The requires expression
     * </p>
     */
    private final AssertionClause myRequires;

    /**
     * <p>
     * The ensures expression
     * </p>
     */
    private final AssertionClause myEnsures;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a facility type initialization/finalization block that
     * happens when a variable
     * of this type is initialized/finalized.
     * </p>
     *
     * @param l A {@link Location} representation object.
     * @param type Indicates if it is an initialization or finalization block.
     * @param affects A {@link AffectsClause} representing the
     *        initialization's/finalization's affects
     *        clause.
     * @param requires A {@link AssertionClause} representing the operation's
     *        requires clause.
     * @param ensures A {@link AssertionClause} representing the operation's
     *        ensures clause.
     * @param facilities List of facility declarations in this block.
     * @param variables List of variables in this block.
     * @param statements List of statements in this block.
     */
    public FacilityInitFinalItem(Location l, ItemType type,
            AffectsClause affects, AssertionClause requires,
            AssertionClause ensures, List<FacilityDec> facilities,
            List<VarDec> variables, List<Statement> statements) {
        super(l, type, affects, facilities, variables, statements);
        myRequires = requires;
        myEnsures = ensures;
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
        printSpace(indentSize, sb);
        sb.append(myItemType.toString());
        sb.append("\n");

        // affects clause
        if (myAffects != null) {
            sb.append(myAffects.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        // requires clause
        sb.append(myRequires.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append("\n");

        // ensures clause
        sb.append(myEnsures.asString(indentSize + innerIndentInc,
                innerIndentInc));

        for (FacilityDec myFacilityDec : myFacilityDecs) {
            sb.append(myFacilityDec.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        for (VarDec myVariableDec : myVariableDecs) {
            sb.append(myVariableDec.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        for (Statement myStatement : myStatements) {
            sb.append(myStatement.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        printSpace(indentSize, sb);
        sb.append("end;\n");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final FacilityInitFinalItem clone() {
        AffectsClause newAffects = null;
        if (myAffects != null) {
            newAffects = myAffects.clone();
        }

        return new FacilityInitFinalItem(cloneLocation(), myItemType,
                newAffects, myRequires.clone(), myEnsures.clone(),
                copyFacDecs(), copyVars(), copyStatements());
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
        if (!super.equals(o))
            return false;

        FacilityInitFinalItem that = (FacilityInitFinalItem) o;

        if (!myRequires.equals(that.myRequires))
            return false;
        return myEnsures.equals(that.myEnsures);

    }

    /**
     * <p>
     * This method returns the ensures clause for this item.
     * </p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getEnsures() {
        return myEnsures;
    }

    /**
     * <p>
     * This method returns the requires clause for this item.
     * </p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getRequires() {
        return myRequires;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myRequires.hashCode();
        result = 31 * result + myEnsures.hashCode();
        return result;
    }

}
