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
import edu.clemson.cs.r2jt.typeandpopulate.InstantiatedScope;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.vcgeneration.vcs.VerificationCondition;

import java.util.*;

/**
 * Created by mike on 4/3/2014.
 */
public class TheoremCongruenceClosureImpl {

    private final boolean isEquality;
    private final Registry m_theoremRegistry;
    private final ConjunctionOfNormalizedAtomicExpressions m_matchConj;
    private final Set<NormalizedAtomicExpression> m_matchRequired;
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

    public TheoremCongruenceClosureImpl(TypeGraph g, PExp entireTheorem, PExp mustMatch, PExp restOfExp,
                                        PExp toInsert, boolean enterToMatchAndBindAsEquivalentToTrue,
                                        boolean allowNewSymbols, String name) {
        m_name = name;
        m_allowNewSymbols = allowNewSymbols;
        m_typeGraph = g;
        m_theorem = entireTheorem;
        m_theoremString = toInsert.toString();
        isEquality = true;
        m_theoremRegistry = new Registry(g);
        m_matchConj =
                new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry,
                        null);
        if(mustMatch.getSubExpressions().size()>0) {
            if (enterToMatchAndBindAsEquivalentToTrue)
                m_matchConj.addExpression(mustMatch);
            else
                m_matchConj.addFormula(mustMatch);
        }
        m_matchRequired = new HashSet<NormalizedAtomicExpression>(m_matchConj.m_expSet);
        m_insertExpr = toInsert;
        if (!m_matchConj.equals(restOfExp) && restOfExp.getSubExpressions().size() > 1 &&
                !mustMatch.getQuantifiedVariablesNoCache().containsAll(restOfExp.getQuantifiedVariablesNoCache())) {
            m_matchConj.addFormula(restOfExp);
        }
        m_insertCnt =
                m_insertExpr.getSymbolNames().size()
                        + m_insertExpr.getQuantifiedVariables().size();
    }

    public Set<String> getFunctionNames() {
        if (m_function_names == null) {
            Set<String> rSet = m_theoremRegistry.getFunctionNames();
            rSet.remove("=");
            rSet.remove("implies");
            rSet.remove("and");
            rSet.remove("/=");
            //rSet.remove("not"); // remove when not p ==> p = false
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

    public Set<InsertExpWithJustification> applyTo(
            VerificationConditionCongruenceClosureImpl vc, long endTime) {
        HashSet<InsertExpWithJustification> rList =
                new HashSet<InsertExpWithJustification>();

        if (m_insertExpr.getQuantifiedVariables().isEmpty()) {
            m_noQuants = true;
            rList.add(new InsertExpWithJustification(m_insertExpr, m_name
                    + "\n\t" + m_theoremString, m_insertCnt));
            return rList;
        }

        Set<java.util.Map<String, String>> allValidBindings;
        if (m_matchConj.size() == 0
                || ((m_allowNewSymbols && m_theorem.getQuantifiedVariables()
                .size() == 1) && isEquality)) {
            allValidBindings = findValidBindingsByType(vc, endTime);
        } else
            allValidBindings = findValidBindings(vc, endTime);

        if (allValidBindings == null || allValidBindings.size() == 0) {
            //m_unneeded = true;
            return null;
        }
        HashMap<PExp, PExp> quantToLit = new HashMap<PExp, PExp>();
        //allValidBindings = discardBindingIfAllValuesNotUnique(allValidBindings);
        nextMap:
        for (java.util.Map<String, String> curBinding : allValidBindings) {
            for (PSymbol p : m_insertExpr.getQuantifiedVariables()) {

                String thKey = p.getTopLevelOperation();
                String thVal = "";
                if (curBinding.containsKey(thKey)) {
                    thVal = curBinding.get(thKey);
                } else if (curBinding.containsKey(m_theoremRegistry.getRootSymbolForSymbol(thKey))) {
                    thVal = curBinding.get(m_theoremRegistry.getRootSymbolForSymbol(thKey));
                }
                if (thVal.equals(""))
                    continue nextMap;
                MTType quanType =
                        m_theoremRegistry.getTypeByIndex(m_theoremRegistry
                                .getIndexForSymbol(thKey));
                quantToLit.put(new PSymbol(quanType, null, thKey, PSymbol.Quantification.FOR_ALL), new PSymbol(
                        quanType, null, thVal, PSymbol.Quantification.NONE));
            }

            PExp modifiedInsert = m_insertExpr.substitute(quantToLit);
            modifiedInsert = vc.getConjunct().find(modifiedInsert);
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
        if (m_theoremRegistry.m_symbolToIndex.containsKey("_g")) {
            // each goal gets a new map with _g bound to the goal
            for (String g : vc.m_goal) {
                java.util.Map<String, String> gBinds = new HashMap<String, String>(initBindings);
                gBinds.put("_g", g);
                results.add(gBinds);
            }
        } else {
            results.add(initBindings);
        }
        // todo: order by proportion of literal to quants
        Set<NormalizedAtomicExpression> postSet = new HashSet<NormalizedAtomicExpression>();
        for (NormalizedAtomicExpression e_t : m_matchConj.m_expSet) {
            if (!m_matchRequired.contains(e_t)) {
                for (PSymbol pq : m_insertExpr.getQuantifiedVariables()) {
                    if(pq.toString().equals("_g")) continue;
                    if (e_t.getOperatorsAsStrings(false).containsKey(pq.toString())) {
                        postSet.add(e_t);
                        break;
                    }
                }
            } else {
                results =
                        vc.getConjunct().getMatchesForOverideSet(e_t, results);
            }
        }
        Set<java.util.Map<String, String>> t_results;
        for (NormalizedAtomicExpression p_t : postSet) {
            t_results = vc.getConjunct().getMatchesForOverideSet(p_t, results);
            if (t_results.isEmpty()) continue;
            else results = t_results;
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
