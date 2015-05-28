/**
 * TheoremEntry.java
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
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.absyn.MathAssertionDec;
import edu.clemson.cs.r2jt.congruenceclassprover.SMTProver;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Map;

/**
 *
 * @author hamptos
 */
public class TheoremEntry extends SymbolTableEntry {

    private PExp myAssertionAsPExp;
    private MathSymbolEntry myMathSymbolAlterEgo;

    public TheoremEntry(TypeGraph g, String name,
            MathAssertionDec definingElement, ModuleIdentifier sourceModule) {
        super(name, definingElement, sourceModule);
        if (g == null) {
            int bp = 0;
        }
        myAssertionAsPExp = PExp.buildPExp(g, definingElement.getAssertion());

        myMathSymbolAlterEgo =
                new MathSymbolEntry(g, name, Quantification.NONE,
                        definingElement, g.BOOLEAN, null, null, null,
                        sourceModule);
    }

    public PExp getAssertion() {
        return myAssertionAsPExp;
    }

    @Override
    public TheoremEntry toTheoremEntry(Location l) {
        return this;
    }

    @Override
    public MathSymbolEntry toMathSymbolEntry(Location l) {
        return myMathSymbolAlterEgo;
    }

    @Override
    public String getEntryTypeDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String toSMTLIB(Map<String, MTType> typeMap, boolean negate) {
        String forAllString = "";
        String typeRestrictionString = "";

        int varCount = 0;
        PExp asPExp = getAssertion();
        for (PSymbol ps : asPExp.getQuantifiedVariables()) {
            String nameSort = SMTProver.NameSort;
            MTType type = ps.getType();
            String typeString = ps.getType().toString();
            if (type.getClass().getSimpleName().equals("MTFunction")) {
                return "";
            }
            // TODO: add support for functions as types
            if (typeString.equals("B")) {
                nameSort = "B";
            }
            String name = ps.toSMTLIB(null);

            if (ps.quantification.equals(PSymbol.Quantification.FOR_ALL)) {
                forAllString += "(" + name + " " + nameSort + ")";
                if (nameSort != "B") {
                    typeRestrictionString +=
                            "(" + "EleOf " + name + " " + type + ")";
                    varCount++;
                }
            }
            else
                throw new UnsupportedOperationException(
                        "Only universal quantification is supported.");
        }
        if (forAllString.length() > 0) {
            forAllString = "forall(" + forAllString + ")";
        }
        else if (forAllString.length() == 0) {
            if (!negate)
                return "(assert " + myAssertionAsPExp.toSMTLIB(typeMap) + ")";
            else
                return "(assert(not " + myAssertionAsPExp.toSMTLIB(typeMap)
                        + "))";
        }
        if (varCount > 1) {
            typeRestrictionString = "(and " + typeRestrictionString + ")";
        }
        String assertion =
                "(" + forAllString + " (=> " + typeRestrictionString
                        + myAssertionAsPExp.toSMTLIB(typeMap) + "))";
        if (!negate)
            return "(assert " + assertion + ") ";
        else
            return "(assert(not " + assertion + "))";
    }
}
