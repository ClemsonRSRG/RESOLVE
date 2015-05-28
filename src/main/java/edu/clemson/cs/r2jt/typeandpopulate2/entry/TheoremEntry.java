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
package edu.clemson.cs.r2jt.typeandpopulate2.entry;

import edu.clemson.cs.r2jt.absynnew.decl.MathTheoremAST;
import edu.clemson.cs.r2jt.rewriteprover.absyn2.PExpr;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import org.antlr.v4.runtime.Token;

import java.util.Map;

/**
 *
 * @author hamptos
 */
public class TheoremEntry extends SymbolTableEntry {

    private PExpr myAssertionAsPExp;
    private MathSymbolEntry myMathSymbolAlterEgo;

    public TheoremEntry(TypeGraph g, String name,
            MathTheoremAST definingElement, ModuleIdentifier sourceModule) {
        super(name, definingElement, sourceModule);

        myAssertionAsPExp = PExpr.buildPExp(definingElement.getAssertion());

        myMathSymbolAlterEgo =
                new MathSymbolEntry(g, name, Quantification.NONE,
                        definingElement, g.BOOLEAN, null, null, null,
                        sourceModule);
    }

    public PExpr getAssertion() {
        return myAssertionAsPExp;
    }

    @Override
    public TheoremEntry toTheoremEntry(Token l) {
        return this;
    }

    @Override
    public MathSymbolEntry toMathSymbolEntry(Token l) {
        return myMathSymbolAlterEgo;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a math theorem";
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
