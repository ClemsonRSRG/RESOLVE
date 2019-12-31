/*
 * RealizInitFinalItem.java
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
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.List;

/**
 * <p>
 * This is the class for all the initialization/finalization block objects that
 * the compiler builds
 * using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class RealizInitFinalItem extends AbstractInitFinalItem {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a initialization/finalization block that happens when a
     * variable of this type
     * is initialized/finalized.
     * </p>
     *
     * @param l A {@link Location} representation object.
     * @param type Indicates if it is an initialization or finalization block.
     * @param affects A {@link AffectsClause} representing the
     *        initialization's/finalization's affects
     *        clause.
     * @param facilities List of facility declarations in this block.
     * @param variables List of variables in this block.
     * @param statements List of statements in this block.
     */
    public RealizInitFinalItem(Location l, ItemType type, AffectsClause affects,
            List<FacilityDec> facilities, List<VarDec> variables,
            List<Statement> statements) {
        super(l, type, affects, facilities, variables, statements);
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
            sb.append("\n");
        }

        for (FacilityDec myFacilityDec : myFacilityDecs) {
            sb.append(myFacilityDec.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n");
        }

        for (VarDec myVariableDec : myVariableDecs) {
            sb.append(myVariableDec.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n");
        }

        for (Statement myStatement : myStatements) {
            sb.append(myStatement.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n");
        }

        printSpace(indentSize, sb);
        sb.append("end;\n");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final RealizInitFinalItem clone() {
        AffectsClause newAffects = null;
        if (myAffects != null) {
            newAffects = myAffects.clone();
        }

        return new RealizInitFinalItem(cloneLocation(), myItemType, newAffects,
                copyFacDecs(), copyVars(), copyStatements());
    }

}
