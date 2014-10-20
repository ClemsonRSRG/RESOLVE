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
import edu.clemson.cs.r2jt.proving.absyn.PLambda;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate.MTNamed;
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

    // TODO: exclude statements with dummy variables not in matching component, or do another search/match with result
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
        // temporary: exclude inserting lambdas (causes a hang -- probably in prioritization system)
        if (allValidBindings == null || allValidBindings.size() == 0) {
            m_unneeded = true;
            return null;
        }

        HashMap<PExp, PExp> quantToLit = new HashMap<PExp, PExp>();
        outerBindingLoop: while (!allValidBindings.empty()
                && System.currentTimeMillis() < endTime) {
            HashMap<String, String> curBinding = allValidBindings.pop();
            for (String thKey : curBinding.keySet()) {

                MTType quanType =
                        m_theoremRegistry.getTypeByIndex(m_theoremRegistry
                                .getIndexForSymbol(thKey));

                quantToLit.put(new PSymbol(quanType, null, thKey), new PSymbol(
                        quanType, null, curBinding.get(thKey)));
            }
            // todo: replace lambda param types if they are type variables
            PExp modifiedInsert = m_insertExpr.substitute(quantToLit);

            rList.add(new InsertExpWithJustification(modifiedInsert,
                    m_theoremString));
            quantToLit.clear();

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
        /*if(m_theoremString.contains("cn")
                 && vc.m_name.equals("1_10")){
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
                    if (allValidBindings.isEmpty()
                            || !curBox.m_bindings.equals(allValidBindings
                                    .peek())) {
                        // use allBound to disable type checks, otherwise just use typeCheck
                        if (allBound(curBox)) {
                            allValidBindings.push(curBox.m_bindings);
                            if (extraOutput)
                                System.out
                                        .println("saved " + curBox.m_bindings);
                        }
                        else {
                            if (extraOutput) {
                                System.out.println("failed type check");
                            }
                        }
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

    boolean allBound(SearchBox box) {
        for (String oSymbol : box.m_bindings.keySet()) {
            String dSymbol = box.m_bindings.get(oSymbol);
            if (!box.m_destRegistry.isSymbolInTable(dSymbol)) {
                if (!oSymbol.contains("¢")) {
                    System.err.println("Unbound: " + oSymbol + ": " + dSymbol
                            + " in " + m_theoremString);
                    box.m_failedBindings = box.m_bindings;
                    return false;
                }
                continue;
            }
        }
        return true;
    }

    boolean typeCheck(SearchBox box) {
        // type check here
        for (String oSymbol : box.m_bindings.keySet()) {
            String dSymbol = box.m_bindings.get(oSymbol);
            if (!box.m_destRegistry.isSymbolInTable(dSymbol)) {
                if (!oSymbol.contains("¢")) {
                    System.err.println("Unbound: " + oSymbol + ":'" + dSymbol
                            + "' in " + m_theoremString);
                    box.m_failedBindings = box.m_bindings;
                    return false;
                }
                continue;
            }
            MTType oType, dType;
            oType =
                    box.m_origRegistry.getTypeByIndex(box.m_origRegistry
                            .getIndexForSymbol(oSymbol));
            dType =
                    box.m_destRegistry.getTypeByIndex(box.m_destRegistry
                            .getIndexForSymbol(dSymbol));
            if (oType.alphaEquivalentTo(dType))
                continue;
            if (dType.isSubtypeOf(oType))
                continue;
            if (oType.getClass().getSimpleName().contains("MTFunction")
                    && dType.getClass().getSimpleName().contains("MTFunction")) {
                dType = (MTFunction) dType;
                MTType oRange = ((MTFunction) oType).getRange();
                MTType oDomain = ((MTFunction) oType).getDomain();
                MTType dRange = ((MTFunction) dType).getRange();
                MTType dDomain = ((MTFunction) dType).getDomain();
                if (oRange == null || oDomain == null || dRange == null
                        || dDomain == null) {
                    System.err.println("null type error");
                    return false;
                }
                // Check if these are type variables

                String bDomain, bRange;
                if (box.m_bindings.containsKey(oRange.toString().replace("'",
                        ""))) {
                    bRange =
                            box.m_bindings.get(oRange.toString().replace("'",
                                    ""));
                    oRange = box.m_destRegistry.m_typeDictionary.get(bRange);
                    if (oRange == null) {
                        System.err.println("null type for: " + bRange);
                        return false;
                    }
                }
                if (box.m_bindings.containsKey(oDomain.toString().replace("'",
                        ""))) {
                    bDomain =
                            box.m_bindings.get(oDomain.toString().replace("'",
                                    ""));
                    oDomain = box.m_destRegistry.m_typeDictionary.get(bDomain);
                    if (oDomain == null) {
                        System.err.println("null type for:" + bDomain);
                        return false;
                    }
                }

                if ((oDomain.alphaEquivalentTo(dDomain) || (dDomain
                        .isSubtypeOf(oDomain)))
                        && (oRange.alphaEquivalentTo(dRange) || (dRange
                                .isSubtypeOf(oRange)))) {
                    System.out.println("Type match: " + oSymbol + ":" + oType
                            + " maps to " + dSymbol + ":" + dType);
                    continue;
                }
                System.err.println("Type Mismatch: orig: " + oDomain + "->"
                        + oRange + " dest: " + dType);
            }
            System.err.println("Failed type check: " + oSymbol + ": " + oType
                    + " " + dSymbol + ": " + dType);

            box.m_failedBindings = box.m_bindings;
            return false;

        }
        //System.out.println(box.m_bindings);

        return true;
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
