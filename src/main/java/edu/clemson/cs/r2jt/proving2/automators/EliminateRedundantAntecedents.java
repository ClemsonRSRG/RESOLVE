/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.RemoveAntecedent;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class EliminateRedundantAntecedents implements Automator {

    public static final EliminateRedundantAntecedents INSTANCE = 
            new EliminateRedundantAntecedents();
    
    private EliminateRedundantAntecedents() {
        
    }
    
    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        Set<PExp> seen = new HashSet<PExp>();
        
        LocalTheorem curTheorem;
        LocalTheorem toRemove = null;
        Iterator<LocalTheorem> localTheorems = 
                model.getLocalTheoremList().iterator();
        while (toRemove == null && localTheorems.hasNext()) {
            curTheorem = localTheorems.next();
            
            if (seen.contains(curTheorem.getAssertion())) {
                toRemove = curTheorem;
            }
            
            seen.add(curTheorem.getAssertion());
        }
        
        if (toRemove == null) {
            stack.pop();
        }
        else {
            new RemoveAntecedent(model, toRemove).getApplications(model).next().apply(model);
        }
    }
}
