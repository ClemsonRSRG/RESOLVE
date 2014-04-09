/**
 * VerificationConditionCongruenceClosureImpl.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.Antecedent;
import edu.clemson.cs.r2jt.proving2.Consequent;
import edu.clemson.cs.r2jt.proving2.VC;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mike on 4/3/2014.
 */
public class VerificationConditionCongruenceClosureImpl {

    private Registry m_registry;
    private String m_name;
    private Antecedent m_antecedent;
    private Consequent m_consequent;
    private ConjunctionOfNormalizedAtomicExpressions m_conjunction;
    private List<List<String>> m_goal; // every item in each sublist is equivalent iff proved.  Disjunctions in consequent are split into seperate vc's before we see them here.

    // currently support only unchained equalities, so each sublist is size 2.

    public VerificationConditionCongruenceClosureImpl(VC vc) {
        m_name = vc.getName();
        m_antecedent = vc.getAntecedent();
        m_consequent = vc.getConsequent();
        m_registry = new Registry();
        m_conjunction =
                new ConjunctionOfNormalizedAtomicExpressions(m_registry);
        m_goal = new ArrayList<List<String>>();

        addPExp(m_antecedent.iterator(), true);
        addPExp(m_consequent.iterator(), false);
    }

    public SearchBox findNAE(NormalizedAtomicExpressionMapImpl query, 
            Registry queryRegistry,
            HashMap<String,String> bindings,
            SearchBox searchbox){
        
        return m_conjunction.findNAE(query,queryRegistry,bindings,searchbox);
    }
    
    public boolean isProved(){
        for(List<String> g :m_goal){
            // check each goal has same root
            if(!g.get(0).equals(g.get(1))) // diff symbols, same root?
                if(m_registry.getIndexForSymbol(g.get(0))!=m_registry.getIndexForSymbol(g.get(1)))
                    // can avoid this check by updating goal on merges
                    return false; // not proved yet
        }
        return true;
    }
    private void addPExp(Iterator<PExp> pit, boolean inAntecedent) {
        while (pit.hasNext()) {
            PExp curr = pit.next();
            if (curr.isEquality()) { // f(x,y) = z and g(a,b) = c ; then z is replaced by c
                PExp lhs = curr.getSubExpressions().get(0);
                PExp rhs = curr.getSubExpressions().get(1);
                int lhsIndex = (m_conjunction.addFormula(lhs));
                int rhsIndex = (m_conjunction.addFormula(rhs));
                if (inAntecedent)
                    m_conjunction.mergeOperators(lhsIndex, rhsIndex);
                else
                    addGoal(m_registry.getSymbolForIndex(lhsIndex), m_registry
                            .getSymbolForIndex(rhsIndex));
            }
            else { // P becomes P = true or P(x...) becomes P(x ...) = z and z is replaced by true
                int intRepForExp = m_conjunction.addFormula(curr);
                if (inAntecedent)
                    m_conjunction.mergeOperators(m_registry
                            .getIndexForSymbol("true"), intRepForExp);
                else
                    addGoal(m_registry.getSymbolForIndex(intRepForExp), "true");
            }

        }

    }

    private void addGoal(String a, String b) {
        List<String> newGoal = new ArrayList<String>(2);
        newGoal.add(a);
        newGoal.add(b);
        m_goal.add(newGoal);
    }

    public String toString() {
        String r = m_name + "\n" + m_conjunction;
        r += "----------------------------------\n";
        for (List<String> gl : m_goal) {
            r += gl.get(0) + "=" + gl.get(1) + "\n";
        }

        return r;
    }

}
