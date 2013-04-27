/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.RemoveAntecedent;
import edu.clemson.cs.r2jt.proving2.transformations.SubstituteInPlaceInAntecedent;
import edu.clemson.cs.r2jt.proving2.transformations.SubstituteInPlaceInConsequent;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class VariablePropagator implements Automator {

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        Iterator<LocalTheorem> localTheorems =
                model.getLocalTheoremList().iterator();

        PExp variable = null;
        PExp expansion = null;
        int antecedentIndex = -1;
        PExp curTheoremAssertion;
        LocalTheorem curTheorem = null;
        while (variable == null && localTheorems.hasNext()) {
            curTheorem = localTheorems.next();
            curTheoremAssertion = curTheorem.getAssertion();
            antecedentIndex++;

            if (curTheoremAssertion instanceof PSymbol) {
                PSymbol curTheoremSymbol = (PSymbol) curTheoremAssertion;

                if (curTheoremSymbol.name.equals("=")) {
                    PExp left = curTheoremSymbol.arguments.get(0);
                    PExp right = curTheoremSymbol.arguments.get(1);

                    variable = getSingleVariable(left);
                    if (variable == null) {
                        variable = getSingleVariable(right);

                        if (variable != null) {
                            expansion = left;
                        }
                    }
                    else {
                        expansion = right;
                    }
                }
            }
        }

        if (variable == null) {
            stack.pop();
        }
        else {
            List<Automator> steps = new LinkedList<Automator>();
            
            if (!variable.equals(expansion)) {
                steps.add(new ApplyAll(new SubstituteInPlaceInAntecedent(
                        curTheorem, variable, expansion)));
                steps.add(new ApplyAll(new SubstituteInPlaceInConsequent(
                        curTheorem, variable, expansion)));
            }
            steps.add(new ApplyN(new RemoveAntecedent(model, curTheorem), 1));
            stack.push(new PushSequence(steps));
        }
    }

    /**
     * <p>Takes an expression and returns the same expression if it represents
     * a single, unquantified variable.  Otherwise it returns null.</p>
     * @param e
     * @return 
     */
    private PExp getSingleVariable(PExp e) {
        PExp result = null;

        if (e instanceof PSymbol) {
            PSymbol eAsPSymbol = (PSymbol) e;

            if (eAsPSymbol.arguments.size() == 0
                    && eAsPSymbol.quantification
                            .equals(PSymbol.Quantification.NONE)
                    && !eAsPSymbol.isLiteral()) {
                result = eAsPSymbol;
            }
        }

        return result;
    }
}
