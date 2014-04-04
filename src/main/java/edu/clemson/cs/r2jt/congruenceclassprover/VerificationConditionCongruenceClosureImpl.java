package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.Antecedent;
import edu.clemson.cs.r2jt.proving2.Consequent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public VerificationConditionCongruenceClosureImpl(VC vc){
        m_name = vc.getName();
        m_antecedent = vc.getAntecedent();
        m_consequent = vc.getConsequent();
        m_registry = new Registry();
        m_conjunction = new ConjunctionOfNormalizedAtomicExpressions(m_registry);
        m_goal = new ArrayList<List<String>>();

        addPExp(m_antecedent.iterator(), true);
        addPExp(m_consequent.iterator(), false);
    }

    private void addPExp(Iterator<PExp> pit, boolean inAntecedent){
        while(pit.hasNext()){
            PExp curr = pit.next();
            if(curr.isEquality()) { // f(x,y) = z and g(a,b) = c ; then z is replaced by c
                PExp lhs = curr.getSubExpressions().get(0);
                PExp rhs = curr.getSubExpressions().get(1);
                int lhsIndex = (m_conjunction.addExpression(lhs));
                int rhsIndex = (m_conjunction.addExpression(rhs));
                if(inAntecedent) m_conjunction.mergeOperators(lhsIndex, rhsIndex);
                else addGoal(m_registry.getSymbolForIndex(lhsIndex),m_registry.getSymbolForIndex(rhsIndex));
            } else { // P becomes P = true or P(x...) becomes P(x ...) = z and z is replaced by true
                int intRepForExp = m_conjunction.addExpression(curr);
                if(inAntecedent) m_conjunction.mergeOperators(m_registry.getIndexForSymbol("true"), intRepForExp);
                else addGoal(m_registry.getSymbolForIndex(intRepForExp),"true");
            }

        }

    }

    private void addGoal(String a, String b){
        List<String> newGoal = new ArrayList<String>(2);
        newGoal.add(a);
        newGoal.add(b);
        m_goal.add(newGoal);
    }

    public String toString(){
        String r = m_name + "\n" + m_conjunction;

        return r;
    }

}
