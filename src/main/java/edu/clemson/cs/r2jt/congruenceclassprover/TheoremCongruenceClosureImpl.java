/**
 * TheoremCongruenceClosureImpl.java
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
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

/**
 * Created by mike on 4/3/2014.
 */
public class TheoremCongruenceClosureImpl {

    private final boolean isEquality;
    private final Registry m_theoremRegistry;
    private final ConjunctionOfNormalizedAtomicExpressions m_matchConj;
    public final String m_theoremString;
    private final PExp m_insertExpr;
    private final PExp m_theorem;

    public TheoremCongruenceClosureImpl(TypeGraph g, PExp p) {
        m_theorem = p;
        m_theoremString = p.toString();
        isEquality = p.getTopLevelOperation().equals("=");
        m_theoremRegistry = new Registry(g);
        m_matchConj =
                new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry);

        if (isEquality) {
            m_matchConj.addExpression(p.getSubExpressions().get(0));
            m_insertExpr = p;
        }
        else if (p.getTopLevelOperation().equals("implies")) {
            m_matchConj.addExpression(p.getSubExpressions().get(0));
            m_insertExpr = p.getSubExpressions().get(1);
        }
        else {
            m_matchConj.addExpression(p);
            m_insertExpr = p;
        }
    }

    public TheoremCongruenceClosureImpl(TypeGraph g, PExp toMatchAndBind,
            PExp toInsert) {
        m_theorem = toInsert;
        m_theoremString = toInsert.toString();
        isEquality = true;
        m_theoremRegistry = new Registry(g);
        m_matchConj =
                new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry);
        m_matchConj.addExpression(toMatchAndBind);
        m_insertExpr = toInsert;
    }

    public String applyTo(VerificationConditionCongruenceClosureImpl vc) {
        if (m_insertExpr.getQuantifiedVariables().isEmpty()) {
            String r = "\tinserting: " + m_insertExpr + "\n";
            vc.getConjunct().addExpression(m_insertExpr);
            return r;
        }
        String r = "";

        Stack<HashMap<String, String>> allValidBindings = findValidBindings(vc);

        if (allValidBindings == null || allValidBindings.size() == 0) {
            return "";
        }

        HashMap<PExp, PExp> quantToLit = new HashMap<PExp, PExp>();
        while (!allValidBindings.empty()) {
            HashMap<String, String> curBinding = allValidBindings.pop();
            for (String thKey : curBinding.keySet()) {
                MTType quanType =
                        m_theoremRegistry.getTypeByIndex(m_theoremRegistry
                                .getIndexForSymbol(thKey));
                quantToLit.put(new PSymbol(quanType, quanType, thKey),
                        new PSymbol(quanType, quanType, curBinding.get(thKey)));
            }
            PExp modifiedInsert = m_insertExpr.substitute(quantToLit);
            assert modifiedInsert != m_insertExpr : this.toString()
                    + m_matchConj;
            quantToLit.clear();
            r += ("\tinserting: " + modifiedInsert + "\n");
            vc.getConjunct().addExpression(modifiedInsert);

        }
        return r;
    }

    private HashMap<String, String> getInitBindings() {
        HashMap<String, String> initBindings = new HashMap<String, String>();
        for (int i = 0; i < m_theoremRegistry.m_indexToSymbol.size(); ++i) {
            String curSym = m_theoremRegistry.m_indexToSymbol.get(i);
            Registry.Usage us = m_theoremRegistry.getUsage(curSym);
            if (us == Registry.Usage.CREATED || us == Registry.Usage.FORALL) {
                initBindings.put(curSym, "");

            }
        }
        return initBindings;
    }

    // for those PExp that did not tranlate into a Conj list( their information is only in the registry)
    private Stack<HashMap<String, String>> findPExp(PExp p,
            VerificationConditionCongruenceClosureImpl vc) {
        assert m_matchConj.size() == 0;

        if (p.isEquality() && p.equals(m_theorem)) {
            m_matchConj.addExpression(p);
            if (m_matchConj.size() != 0) {
                return findValidBindings(vc);
            }
        }
        if (p.getTopLevelOperation().equals("implies")) {
            return findPExp(p.getSubExpressions().get(0), vc);
        }
        Stack<HashMap<String, String>> allValidBindings =
                new Stack<HashMap<String, String>>();
        HashMap<String, String> curBindings = getInitBindings();
        MTType t;
        if (p.isEquality()) {
            PExp lhs = p.getSubExpressions().get(0);
            PExp rhs = p.getSubExpressions().get(1);
            assert lhs.isVariable() || rhs.isVariable() : "neither " + lhs
                    + "or" + rhs + "is variable";
            // if s = t, then you only need to find one or the other (one actual symbol for each known equality class)
            // and it wont matter which you use, since they will have the same type.
            t = rhs.getType();

        }
        else {
            t = p.getType();
        }
        Set<String> actuals = vc.getRegistry().getSetMatchingType(t);
        ArrayList<String> toRemove = new ArrayList<String>();
        for (String a : actuals) {
            if (vc.getRegistry().getUsage(a).equals(Registry.Usage.HASARGS)) {
                toRemove.add(a);
            }
        }
        for (String tor : toRemove) {
            actuals.remove(tor);
        }

        String[] virtualsArr =
                curBindings.keySet().toArray(
                        new String[curBindings.keySet().size()]);
        String[] actualsArr = actuals.toArray(new String[actuals.size()]);

        for (int i = 0; i < actualsArr.length; ++i) {
            for (String k : curBindings.keySet()) {
                curBindings.put(k, actualsArr[i]);
            }
            allValidBindings.push(curBindings);
            curBindings = getInitBindings();
        }
        // inequalities are functions in this system and would have produced a conj list
        return allValidBindings;
    }

    private boolean pushNewSearchBox(Stack<SearchBox> boxStack) {
        SearchBox top = boxStack.peek();
        int index = top.m_indexInList + 1;
        // if top of stack contains last expression return false, cant push another
        if (index == m_matchConj.size()) {
            return false;
        }
        /*
         SearchBox(NormalizedAtomicExpressionMapImpl query, Registry queryReg,
         ConjunctionOfNormalizedAtomicExpressions dataSet, Registry dataReg,
         HashMap<String,String> bindings, int indexInList)
         */

        boxStack.push(new SearchBox(m_matchConj.getExprAtPosition(index),
                m_theoremRegistry, top.m_dataSet, top.m_destRegistry,
                new HashMap<String, String>(top.m_bindings), index));
        return true;
    }

    private Stack<HashMap<String, String>> findValidBindings(
            VerificationConditionCongruenceClosureImpl vc) {

        if (m_matchConj.size() == 0) {

            return findPExp(m_theorem, vc);
        }
        Stack<HashMap<String, String>> allValidBindings =
                new Stack<HashMap<String, String>>();
        Stack<SearchBox> boxStack = new Stack<SearchBox>();
        boxStack.push(new SearchBox(m_matchConj.getExprAtPosition(0),
                m_theoremRegistry, vc.getConjunct(), vc.getRegistry(),
                getInitBindings(), 0));

        while (!boxStack.isEmpty()) {
            SearchBox curBox = boxStack.peek();
            // rollBack
            curBox.m_bindings =
                    new HashMap<String, String>(curBox.m_bindingsInitial);
            curBox.getNextMatch();
            if (curBox.impossibleToMatch) {
                boxStack.pop();
                if (!boxStack.isEmpty()) {
                    boxStack.peek().currentIndex =
                            boxStack.peek().m_lastGoodMatchIndex;
                    boxStack.peek().currentIndex++;
                }
            }
            else {
                // save bindings if for last index, then pop
                if (curBox.m_indexInList + 1 == m_matchConj.size()) {
                    if (allValidBindings.isEmpty()) {
                        allValidBindings.push(curBox.m_bindings);
                    }
                    else if (!curBox.m_bindings.equals(allValidBindings.peek())) {
                        allValidBindings.push(curBox.m_bindings);
                    }
                    // If there is only one left, do not pop it when good match
                    // is found.
                    if (boxStack.size() > 1) {
                        boxStack.pop();
                    }

                    boxStack.peek().currentIndex++;

                }
                else {
                    pushNewSearchBox(boxStack);
                }
            }
        }
        return allValidBindings;
    }

    public String toString() {
        String r = "\n--------------------------------------\n";
        r += m_theoremString;
        r += "\nif found\n" + m_matchConj + "\ninsert\n" + m_insertExpr;
        r += "\n--------------------------------------\n";
        return r;
    }
}
