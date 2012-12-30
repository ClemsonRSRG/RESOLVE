/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.proving2.justifications.Justification;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentByImplication;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class Theorem {

    public static final Mapping<Theorem, PExp> UNWRAPPER = 
            new TheoremUnwrapper();
    
    /**
     * <p>Guaranteed not to have a top-level and (otherwise this would be two
     * theorems.)</p>
     */
    private final PExp myAssertion;
    private final Justification myJustification;

    public Theorem(PExp assertion, Justification justification) {
        myAssertion = assertion;
        myJustification = justification;
    }

    public PExp getAssertion() {
        return myAssertion;
    }

    public List<Transformation> getTransformations(Iterable<PExp> globalTheorems) {
        List<Transformation> result = new LinkedList<Transformation>();
        
        if (myAssertion instanceof PSymbol) {
            PSymbol assertionAsPS = (PSymbol) myAssertion;
            if (assertionAsPS.name.equals("implies")) {
                PExp left = assertionAsPS.arguments.get(0);
                PExp right = assertionAsPS.arguments.get(1);
                
                result.add(new ExpandAntecedentByImplication(
                        left.splitIntoConjuncts(), right));
            }
        }
        
        return result;
    }
    
    @Override
    public String toString() {
        return "" + myAssertion;
    }
    
    private static class TheoremUnwrapper implements Mapping<Theorem, PExp> {

        @Override
        public PExp map(Theorem input) {
            return input.myAssertion;
        }
    }
}
