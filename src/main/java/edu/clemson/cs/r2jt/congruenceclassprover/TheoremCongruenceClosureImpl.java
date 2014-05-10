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
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
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
    private final TypeGraph m_typeGraph;
    protected boolean m_unneeded = false;

    public TheoremCongruenceClosureImpl(TypeGraph g, PExp p) {
        m_typeGraph = g;
        m_theorem = p;
        m_theoremString = p.toString();
        isEquality = p.getTopLevelOperation().equals("=");
        m_theoremRegistry = new Registry(g);
        m_matchConj =
                new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry);

        if (isEquality) {
            m_matchConj.addFormula(p.getSubExpressions().get(0));
            m_insertExpr = p;
        }
        else if (p.getTopLevelOperation().equals("implies")) {
            m_matchConj.addExpression(p.getSubExpressions().get(0));
            m_insertExpr = p.getSubExpressions().get(1);
        }
        else {
            /* experimental
            
            Is_Permutation((S o T), (T o S)) for example,
            should go into matchConj as itself, but equal to a boolean variable.
            .
             */
            m_matchConj.addFormula(p);
            m_insertExpr = p; // this will add "= true"
        }

        if (m_matchConj.size() == 0) {
            m_unneeded = true;
        }

    }

    public TheoremCongruenceClosureImpl(TypeGraph g, PExp toMatchAndBind,
            PExp toInsert, boolean enterToMatchAndBindAsEquivalentToTrue) {
        m_typeGraph = g;
        m_theorem = toInsert;
        m_theoremString = toInsert.toString();
        isEquality = true;
        m_theoremRegistry = new Registry(g);
        m_matchConj =
                new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry);
        if (enterToMatchAndBindAsEquivalentToTrue)
            m_matchConj.addExpression(toMatchAndBind);
        else
            m_matchConj.addFormula(toMatchAndBind);
        m_insertExpr = toInsert;
        if (m_matchConj.size() == 0) {
            m_unneeded = true;
        }
    }

    public Set<String> getFunctionNames() {
        Registry tReg = new Registry(m_typeGraph);
        ConjunctionOfNormalizedAtomicExpressions temp =
                new ConjunctionOfNormalizedAtomicExpressions(tReg);
        ImmutableList<PExp> pit = m_theorem.getSubExpressions();
        temp.addExpression(m_theorem);

        Set<String> rSet = tReg.getFunctionNames();
        rSet.remove("=");
        rSet.remove("implies");
        rSet.remove("and");
        rSet.remove("or");
        return rSet;
    }

    public ArrayList<InsertExpWithJustification> applyTo(
            VerificationConditionCongruenceClosureImpl vc, long endTime) {
        ArrayList<InsertExpWithJustification> rList =
                new ArrayList<InsertExpWithJustification>();

        if (m_insertExpr.getQuantifiedVariables().isEmpty()) {
            String r = "\tinserting: " + m_insertExpr + "\n";
            rList.add(new InsertExpWithJustification(m_insertExpr,
                    m_theoremString));
            return rList;
        }

        Stack<HashMap<String, String>> allValidBindings =
                findValidBindings(vc, endTime);
        if (allValidBindings == null || allValidBindings.size() == 0) {
            m_unneeded = true;
            return null;
        }

        HashMap<PExp, PExp> quantToLit = new HashMap<PExp, PExp>();
        while (!allValidBindings.empty()
                && System.currentTimeMillis() < endTime) {
            HashMap<String, String> curBinding = allValidBindings.pop();
            for (String thKey : curBinding.keySet()) {
                MTType quanType =
                        m_theoremRegistry.getTypeByIndex(m_theoremRegistry
                                .getIndexForSymbol(thKey));
                quantToLit.put(new PSymbol(quanType, quanType, thKey),
                        new PSymbol(quanType, quanType, curBinding.get(thKey)));
            }
            PExp modifiedInsert = m_insertExpr.substitute(quantToLit);
            quantToLit.clear();
            rList.add(new InsertExpWithJustification(modifiedInsert,
                    m_theoremString));
        }
        return rList;
    }

    private HashMap<String, String> getInitBindings() {
        HashMap<String, String> initBindings = new HashMap<String, String>();
        for (int i = 0; i < m_theoremRegistry.m_indexToSymbol.size(); ++i) {
            String curSym = m_theoremRegistry.m_indexToSymbol.get(i);
            Registry.Usage us = m_theoremRegistry.getUsage(curSym);
            if (us == Registry.Usage.CREATED || us == Registry.Usage.FORALL
                    || us == Registry.Usage.HASARGS_FORALL) {
                initBindings.put(curSym, "");

            }
        }
        return initBindings;
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
            VerificationConditionCongruenceClosureImpl vc, long endTime) {
        if (m_matchConj.size() == 0) {
            return null;
        }
        boolean extraOutput = false;
        /*if(m_theoremString.equals("((S o Empty_String) = S)")
                && vc.m_name.equals("1_1")){
            extraOutput = true;
            System.out.println("looking for: \n" + m_matchConj + "in " + vc);
        }*/
        Stack<HashMap<String, String>> allValidBindings =
                new Stack<HashMap<String, String>>();
        Stack<SearchBox> boxStack = new Stack<SearchBox>();
        boxStack.push(new SearchBox(m_matchConj.getExprAtPosition(0),
                m_theoremRegistry, vc.getConjunct(), vc.getRegistry(),
                getInitBindings(), 0));

        while (!boxStack.isEmpty() && System.currentTimeMillis() < endTime) {
            SearchBox curBox = boxStack.peek();
            // rollBack
            curBox.m_bindings =
                    new HashMap<String, String>(curBox.m_bindingsInitial);
            curBox.getNextMatch();
            if (curBox.impossibleToMatch) {
                if (extraOutput)
                    System.out.println("rejecting " + curBox);
                boxStack.pop();
                if (!boxStack.isEmpty()) {
                    boxStack.peek().currentIndex =
                            boxStack.peek().m_lastGoodMatchIndex;
                    boxStack.peek().currentIndex++;
                }
            }
            else {
                if (extraOutput) {
                    System.out.println("matched " + curBox.toString());
                }
                // save bindings if for last index, then try and find more
                if (curBox.m_indexInList + 1 == m_matchConj.size()) {
                    if (allValidBindings.isEmpty()) {
                        allValidBindings.push(curBox.m_bindings);
                        if (extraOutput)
                            System.out.println("saved " + curBox.m_bindings);
                    }
                    else if (!curBox.m_bindings.equals(allValidBindings.peek())) {
                        allValidBindings.push(curBox.m_bindings);
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

    @Override
    public String toString() {
        String r = "\n--------------------------------------\n";
        r += m_theoremString;
        r += "\nif found\n" + m_matchConj + "\ninsert\n" + m_insertExpr;
        r += "\n--------------------------------------\n";
        return r;
    }
}
