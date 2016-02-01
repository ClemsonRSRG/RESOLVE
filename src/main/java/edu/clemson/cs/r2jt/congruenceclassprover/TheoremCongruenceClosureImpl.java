/**
 * TheoremCongruenceClosureImpl.java
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
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

import java.util.*;

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
    private Set<String> m_function_names;
    private Set<String> m_matching_literals;
    private Set<String> m_all_literals;
    private boolean partMatchedisConstantEquation = false;
    protected boolean m_allowNewSymbols;
    protected String m_name;
    protected int m_insertCnt;
    protected boolean m_noQuants = false;

    public TheoremCongruenceClosureImpl(TypeGraph g, PExp p,
            boolean allowNewSymbols, String name) {
        m_name = name;
        m_allowNewSymbols = allowNewSymbols;
        m_typeGraph = g;
        m_theorem = p;
        m_theoremString = p.toString();
        isEquality = p.getTopLevelOperation().equals("=");
        m_theoremRegistry = new Registry(g);
        m_matchConj =
                new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry,
                        null);
        if (p.getTopLevelOperation().equals("implies")) {
            PExp matchingpart = p.getSubExpressions().get(0);
            m_matchConj.addExpression(matchingpart);
            m_insertExpr = p.getSubExpressions().get(1);
            if (matchingpart.getTopLevelOperation().equals("=")) {
                if (matchingpart.getSubExpressions().get(0).getSubExpressions()
                        .size() == 0
                        && matchingpart.getSubExpressions().get(1)
                                .getSubExpressions().size() == 0) {
                    partMatchedisConstantEquation = true;
                }
            } //
        }
        else if (p.getQuantifiedVariables().size() == 1) {
            // empty matchConj will trigger find by type
            m_matchConj.addFormula(p); // this adds symbols to reg
            m_matchConj.clear(); // will match based on types
            m_insertExpr = p;
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

        m_insertCnt =
                m_insertExpr.getSymbolNames().size()
                        + m_insertExpr.getQuantifiedVariables().size();
    }

    // for theorems that are equations
    public TheoremCongruenceClosureImpl(TypeGraph g, PExp toMatchAndBind,
            PExp toInsert, boolean enterToMatchAndBindAsEquivalentToTrue,
            boolean allowNewSymbols, String name) {
        m_name = name;
        m_allowNewSymbols = allowNewSymbols;
        m_typeGraph = g;
        m_theorem = toInsert;
        m_theoremString = toInsert.toString();
        isEquality = true;
        m_theoremRegistry = new Registry(g);
        m_matchConj =
                new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry,
                        null);
        if (enterToMatchAndBindAsEquivalentToTrue)
            m_matchConj.addExpression(toMatchAndBind);
        else
            m_matchConj.addFormula(toMatchAndBind);
        m_insertExpr = toInsert;
        m_insertCnt =
                m_insertExpr.getSymbolNames().size()
                        + m_insertExpr.getQuantifiedVariables().size();
    }

    public Set<String> getFunctionNames() {
        if (m_function_names == null) {
            Registry tReg = new Registry(m_typeGraph);
            ConjunctionOfNormalizedAtomicExpressions temp =
                    new ConjunctionOfNormalizedAtomicExpressions(tReg, null);
            temp.addExpression(m_theorem);

            Set<String> rSet = tReg.getFunctionNames();
            rSet.remove("=");
            rSet.remove("implies");
            rSet.remove("and");
            rSet.remove("/=");
            rSet.remove("not"); // remove when not p ==> p = false
            rSet.remove("+"); // temporary
            //rSet.remove("-");
            rSet.remove("or"); // temporary this is really bad
            m_function_names = rSet;
        }
        return m_function_names;
    }

    public Set<String> getLiteralsInMatchingPart() {
        // registry should only contain symbols from matching section
        if (m_matching_literals == null) {
            m_matching_literals = new HashSet<String>();
            for (String s : getNonQuantifiedSymbols()) {
                if (m_theoremRegistry.m_symbolToIndex.containsKey(s)) {
                    m_matching_literals.add(s);
                }
            }
        }
        return m_matching_literals;
    }

    public Set<String> getNonQuantifiedSymbols() {
        if (m_all_literals == null) {
            m_all_literals = ((PSymbol) m_theorem).getNonQuantifiedSymbols();

            m_all_literals.remove("=");
            m_all_literals.remove("and");
            m_all_literals.remove("implies");
            m_all_literals.remove("true");
            m_all_literals.remove("false");
            m_all_literals.remove("/=");
            m_all_literals.remove("Empty_String");
            m_all_literals.remove("0");
            m_all_literals.remove("1");
            m_all_literals.remove("2");
            m_all_literals.remove("3");
            m_all_literals.remove("4");
            m_all_literals.remove("5");
            m_all_literals.remove("6");
            m_all_literals.remove("7");
            m_all_literals.remove("8");
            m_all_literals.remove("9");
            m_all_literals.remove("or");
            m_all_literals.remove("+");
        }

        return m_all_literals;

    }

    public ArrayList<InsertExpWithJustification> applyTo(
            VerificationConditionCongruenceClosureImpl vc, long endTime) {
        ArrayList<InsertExpWithJustification> rList =
                new ArrayList<InsertExpWithJustification>();

        if (m_insertExpr.getQuantifiedVariables().isEmpty()) {
            m_noQuants = true;
            rList.add(new InsertExpWithJustification(m_insertExpr, m_name
                    + "\n\t" + m_theoremString, m_insertCnt));
            return rList;
        }
        // Set flag if quantified variable was not in the matching part
        for (PSymbol ps : m_insertExpr.getQuantifiedVariables()) {
            if (!m_theoremRegistry.getForAlls().contains(ps.toString())) {
                /*if(m_insertExpr.getTopLevelOperation().equals("=")){
                    PExp lhs = m_insertExpr.getSubExpressions().get(0);
                    PExp rhs = m_insertExpr.getSubExpressions().get(1);
                   // potential replacement of subexp
                }*/
                m_unneeded = true;
                return null;
            }
        }
        Set<java.util.Map<String, String>> allValidBindings;
        if (m_matchConj.size() == 0
                || ((m_allowNewSymbols && m_theorem.getQuantifiedVariables()
                        .size() == 1) && isEquality)) {
            allValidBindings = findValidBindingsByType(vc, endTime);
        }
        else
            allValidBindings = findValidBindings(vc, endTime);

        if (allValidBindings == null || allValidBindings.size() == 0) {
            //m_unneeded = true;
            return null;
        }

        HashMap<PExp, PExp> quantToLit = new HashMap<PExp, PExp>();
        //allValidBindings = discardBindingIfAllValuesNotUnique(allValidBindings);
        for (java.util.Map<String, String> curBinding : allValidBindings) {
            for (String thKey : curBinding.keySet()) {

                if (m_theoremRegistry.m_indexToSymbol.contains(curBinding
                        .get(thKey))
                        && vc.getRegistry().getUsage(curBinding.get(thKey))
                                .equals(Registry.Usage.FORALL)) {
                    throw new RuntimeException("Quantified var unbound");
                }
                MTType quanType =
                        m_theoremRegistry.getTypeByIndex(m_theoremRegistry
                                .getIndexForSymbol(thKey));
                // thKey may be a parent of the symbol in the theorem
                // there will be no literal match in the substitute call
                // Check if any quantified var is a child.
                // Make extra map entry for each one
                for (PSymbol p : m_theorem.getQuantifiedVariables()) {
                    String pname = p.getTopLevelOperation();
                    if (m_theoremRegistry.getRootSymbolForSymbol(pname).equals(
                            thKey)) {
                        quantToLit.put(new PSymbol(quanType, null, pname),
                                new PSymbol(quanType, null, curBinding
                                        .get(thKey)));
                    }
                }
                quantToLit.put(new PSymbol(quanType, null, thKey), new PSymbol(
                        quanType, null, curBinding.get(thKey)));
            }

            PExp modifiedInsert = m_insertExpr.substitute(quantToLit);
            // Discard s = s
            if (!(modifiedInsert.getTopLevelOperation().equals("=") && modifiedInsert
                    .getSubExpressions().get(0).toString().equals(
                            modifiedInsert.getSubExpressions().get(1)
                                    .toString()))) {
                rList.add(new InsertExpWithJustification(modifiedInsert, m_name
                        + "\n\t" + m_theoremString, m_insertCnt));
            }
            quantToLit.clear();

        }
        return rList;
    }

    // variables to bind are the quantified vars the quantified statement
    // and the created variables in the match conjunction
    private java.util.Map<String, String> getInitBindings() {
        HashMap<String, String> initBindings = new HashMap<String, String>();
        // Created vars. that are parents of quantified vars can be a problem later
        for (int i = 0; i < m_theoremRegistry.m_indexToSymbol.size(); ++i) {

            String curSym = m_theoremRegistry.getSymbolForIndex(i);
            Registry.Usage us = m_theoremRegistry.getUsage(curSym);
            if (us == Registry.Usage.CREATED || us == Registry.Usage.FORALL
                    || us == Registry.Usage.HASARGS_FORALL) {
                initBindings.put(curSym, "");

            }
        }
        return initBindings;
    }

    private Set<java.util.Map<String, String>> findValidBindingsByType(
            VerificationConditionCongruenceClosureImpl vc, long endTime) {
        // Case where no match conj. is produced.
        // Example: S = Empty_String. Relevant info is only in registry.
        Set<java.util.Map<String, String>> allValidBindings =
                new HashSet<java.util.Map<String, String>>();
        // x = constant?
        if (partMatchedisConstantEquation) {
            HashMap<String, String> wildToActual =
                    new HashMap<String, String>();
            for (String wild : m_theoremRegistry.getForAlls()) {

                String actual = m_theoremRegistry.getRootSymbolForSymbol(wild);
                // wildcard is not parent
                if (!actual.equals(wild))
                    wildToActual.put(wild, actual);
                // wildcard is parent, bind to child
                else {
                    Set<String> ch = m_theoremRegistry.getChildren(wild);
                    // choose first non quantified symbol (they are all equal)
                    if (ch.isEmpty())
                        return null;
                    for (String c : ch) {
                        if (!m_theoremRegistry.getUsage(c).equals(
                                Registry.Usage.FORALL)
                                || !m_theoremRegistry.getUsage(c).equals(
                                        Registry.Usage.CREATED)) {
                            wildToActual.put(wild, c);
                            break;
                        }
                        return null;
                    }
                }
            }
            allValidBindings.add(wildToActual);
            return allValidBindings;

        }
        // only valid for preds other than equality
        Set<String> foralls = m_theoremRegistry.getForAlls();
        if (foralls.size() != 1)
            return null;
        String wild = foralls.iterator().next();
        MTType t =
                m_theoremRegistry.getTypeByIndex(m_theoremRegistry
                        .getIndexForSymbol(wild));

        for (String actual : vc.getRegistry().getParentsByType(t)) {
            HashMap<String, String> wildToActual =
                    new HashMap<String, String>();
            wildToActual.put(wild, actual);
            if (!wild.equals(actual)) // can be = with constants in theorems
                allValidBindings.add(wildToActual);
        }
        return allValidBindings;

    }

    private Set<java.util.Map<String, String>> findValidBindings(
            VerificationConditionCongruenceClosureImpl vc, long endTime) {

        Set<java.util.Map<String, String>> results =
                new HashSet<java.util.Map<String, String>>();
        java.util.Map<String, String> initBindings = getInitBindings();
        results.add(initBindings);
        // todo: order by proportion of literal to quants
        for (NormalizedAtomicExpression e_t : m_matchConj.m_expSet) {
            results =
                    vc.getConjunct().getMatchesForOverideSet(e_t,
                            m_theoremRegistry, results);
        }
        return results;
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
