/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.absyn.MathAssertionDec;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
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

        myAssertionAsPExp = PExp.buildPExp(definingElement.getAssertion());

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

}
