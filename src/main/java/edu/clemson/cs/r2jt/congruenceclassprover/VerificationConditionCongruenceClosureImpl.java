/**
 * VerificationConditionCongruenceClosureImpl.java
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

import edu.clemson.cs.r2jt.misc.Flag;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.Antecedent;
import edu.clemson.cs.r2jt.rewriteprover.Consequent;
import edu.clemson.cs.r2jt.rewriteprover.VC;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by mike on 4/3/2014.
 */
public class VerificationConditionCongruenceClosureImpl {

    private final Registry m_registry;
    private final TypeGraph m_typegraph;
    public final String m_name;
    private final Antecedent m_antecedent;
    private final Consequent m_consequent;
    private final ConjunctionOfNormalizedAtomicExpressions m_conjunction;
    protected final List<String> m_goal;
    private int m_fc_ctr = 0;

    public static enum STATUS {
        FALSE_ASSUMPTION, STILL_EVALUATING, PROVED, UNPROVABLE
    }

    public List<PExp> forAllQuantifiedPExps; // trap constraints, can create Theorems externally from this

    // currently support only unchained equalities, so each sublist is size 2.
    public VerificationConditionCongruenceClosureImpl(TypeGraph g, VC vc) {
        m_typegraph = g;
        m_name = vc.getName();
        m_antecedent = vc.getAntecedent();
        m_consequent = vc.getConsequent();
        m_registry = new Registry(g);
        m_conjunction =
                new ConjunctionOfNormalizedAtomicExpressions(m_registry, true);
        m_goal = new ArrayList<String>();
        forAllQuantifiedPExps = new ArrayList<PExp>();
        if (vc.m_liftedLambdaPredicates != null
                && vc.m_liftedLambdaPredicates.size() > 0) {
            forAllQuantifiedPExps.addAll(vc.m_liftedLambdaPredicates);
            //addPExp(forAllQuantifiedPExps.iterator(),true);
            makeSetAssertions(vc);
        }
        addPExp(m_antecedent.iterator(), true);
        addPExp(m_consequent.iterator(), false);
        // seed with not(false)
        ArrayList<PExp> args = new ArrayList<PExp>();
        args.add(new PSymbol(m_typegraph.BOOLEAN,null,"false"));
        PSymbol nF = new PSymbol(m_typegraph.BOOLEAN,null,"not",args);
        m_conjunction.addExpression(nF);
        m_conjunction.updateUseMap();
        //m_conjunction.mergeEquivalentFunctions();
    }

    protected void assertSet(PExp p, ModuleScope scope){
        if(p.getQuantifiedVariables().size()!=1) return;
        List<edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry> entries =
                scope.query(new NameQuery(null, "ZSetCons",
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE, false));
        if(entries.isEmpty()) return;
        MathSymbolEntry e = (MathSymbolEntry)entries.get(0);
        MTType consT = e.getType();
        entries =
                scope.query(new NameQuery(null, "ZSetConB",
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE, false));
        if(entries.isEmpty()) return;
        e = (MathSymbolEntry)entries.get(0);
        MTFunction conB = (MTFunction)e.getType();
        MTType zSetT = conB.getRange();
        PSymbol arg = p.getQuantifiedVariables().iterator().next();
        PSymbol fc = new PSymbol(arg.getType(),arg.getTypeValue(), "_sv" + m_fc_ctr++);
        HashMap<PExp,PExp> subMap = new HashMap<PExp, PExp>();
        subMap.put(arg,fc);
        PExp toAdd = p.substitute(subMap);
        m_conjunction.addExpression(toAdd);
        ArrayList<PExp> args = new ArrayList<PExp>();
        // change to ensure always adding f of f(x)
        PExp fx = toAdd.getSubExpressions().get(0);
        PSymbol lamName = new PSymbol(new MTFunction(m_typegraph,fx.getType(),fc.getType()),null,fx.getTopLevelOperation());
        args.add(lamName);
        PSymbol s1 = new PSymbol(conB,null,"ZSetConB",args);
        args.clear();
        args.add(fc);
        PSymbol s2 = new PSymbol(consT,null, "ZSetCons", args);
        args.clear();
        args.add(s1);
        args.add(s2);
        PSymbol setAsrt = new PSymbol(m_typegraph.BOOLEAN,null,"=",args);
        m_conjunction.addExpression(setAsrt);

    }
    private void makeSetAssertions(VC vc){
        ArrayList<PExp> splitConditions = new ArrayList<PExp>();
        boolean allOverZ = true;
        int c_count = 0;
        for (PExp px : vc.m_conditions) {
            // name it. add it to quantified expr list
            if(px.getQuantifiedVariables().size()!=1) continue;
            PSymbol qFun = new PSymbol(new MTFunction(m_typegraph,m_typegraph.BOOLEAN,
                    px.getQuantifiedVariables().iterator().next().getType()),null,"ConFunc" + (++c_count),
                    new ArrayList<PExp>(px.getQuantifiedVariables()));
            ArrayList<PExp> args = new ArrayList<PExp>();
            args.add(qFun);
            args.add(px);
            PSymbol qFunAssrt = new PSymbol(m_typegraph.BOOLEAN,null,"=",args);
            args.clear();
            forAllQuantifiedPExps.add(qFunAssrt);

            java.util.HashMap<PExp, PExp> substMap =
                    new HashMap<PExp, PExp>();

            // true branch
            PSymbol tBSym = null;
            for (PSymbol pq : px.getQuantifiedVariables()) {
                if (!pq.getType().toString().equals("Z"))
                    allOverZ = false;
                tBSym =
                        new PSymbol(pq.getType(), pq.getTypeValue(), pq
                                .getTopLevelOperation()
                                + ".T", PSymbol.Quantification.NONE);
                substMap.put(pq, tBSym);
            }
            PExp pxTrue = px.substitute(substMap);
            //splitConditions.add(pxTrue);
            args.add(pxTrue);
            args.add(new PSymbol(m_typegraph.BOOLEAN, null, "conVal" + (++c_count)));
            splitConditions.add(new PSymbol(m_typegraph.BOOLEAN, null, "=", args));
            // false branch
            PSymbol fBSym = null;
            for (PSymbol pq : px.getQuantifiedVariables()) {
                fBSym =
                        new PSymbol(pq.getType(), pq.getTypeValue(), pq
                                .getTopLevelOperation()
                                + ".F", PSymbol.Quantification.NONE);
                substMap.put(pq, fBSym);
            }
            PExp pxFalse = px.substitute(substMap);
            args.clear();
            args.add(pxFalse);
            PSymbol negatedCondition =
                    new PSymbol(m_typegraph.BOOLEAN, null, "not", args);
            //splitConditions.add(negatedCondition);
                args.clear();
                args.add(negatedCondition);
                args.add(new PSymbol(m_typegraph.BOOLEAN, null, "conVal" + (++c_count)));
                splitConditions.add(new PSymbol(m_typegraph.BOOLEAN, null, "=", args));


            args.clear();
            args.add(tBSym);
            args.add(fBSym);
            PSymbol assertion =
                    new PSymbol(m_typegraph.BOOLEAN, null,
                            "unionMakesZ", args);
            if (allOverZ)
                splitConditions.add(assertion);
        }
        addPExp(splitConditions.iterator(), true);

    }
    private void makeSetAssertionsOld(VC vc){
        ArrayList<PExp> splitConditions = new ArrayList<PExp>();
        boolean allOverZ = true;
        for (PExp px : vc.m_conditions) {
            java.util.HashMap<PExp, PExp> substMap =
                    new HashMap<PExp, PExp>();
            ArrayList<PExp> args = new ArrayList<PExp>();
            // true branch
            PSymbol tBSym = null;
            for (PSymbol pq : px.getQuantifiedVariables()) {
                if (!pq.getType().toString().equals("Z"))
                    allOverZ = false;
                tBSym =
                        new PSymbol(pq.getType(), pq.getTypeValue(), pq
                                .getTopLevelOperation()
                                + ".T", PSymbol.Quantification.NONE);
                substMap.put(pq, tBSym);
            }
            PExp pxTrue = px.substitute(substMap);
            splitConditions.add(pxTrue);
            //args.add(pxTrue);
            //args.add(new PSymbol(g.BOOLEAN, null, "true"));
            //splitConditions.add(new PSymbol(g.BOOLEAN, null, "=", args));
            // false branch
            PSymbol fBSym = null;
            for (PSymbol pq : px.getQuantifiedVariables()) {
                fBSym =
                        new PSymbol(pq.getType(), pq.getTypeValue(), pq
                                .getTopLevelOperation()
                                + ".F", PSymbol.Quantification.NONE);
                substMap.put(pq, fBSym);
            }
            PExp pxFalse = px.substitute(substMap);
            args.clear();
            args.add(pxFalse);
            PSymbol negatedCondition =
                    new PSymbol(m_typegraph.BOOLEAN, null, "not", args);
            splitConditions.add(negatedCondition);
                /*args.clear();
                args.add(negatedCondition);
                args.add(new PSymbol(g.BOOLEAN, null, "true"));
                splitConditions.add(new PSymbol(g.BOOLEAN, null, "=", args));
                // AddisBinaryPartition (p1: Entity, p2: Entity) : B;
                 */
            args.clear();
            args.add(tBSym);
            args.add(fBSym);
            PSymbol assertion =
                    new PSymbol(m_typegraph.BOOLEAN, null,
                            "unionMakesZ", args);
            if (allOverZ)
                splitConditions.add(assertion);
        }
        addPExp(splitConditions.iterator(), true);

    }
    protected ConjunctionOfNormalizedAtomicExpressions getConjunct() {
        return m_conjunction;
    }

    public Registry getRegistry() {
        return m_registry;
    }

    protected Set<String> getFunctionNames() {
        return m_registry.getFunctionNames();
    }

    protected Map<String, Integer> getGoalSymbols() {
        HashSet<String> goalSymbolSet = new HashSet<String>();
        for (String goal : m_goal) {
            // true is the root of many expressions
            if (goal.equals("true"))
                continue;
            goalSymbolSet.add(goal);

        }
        Map<String, Integer> rMap =
                m_conjunction.getSymbolProximity(goalSymbolSet);
        /*HashMap<String,Integer> rMap = new HashMap<String, Integer>();
        for(String s : tmpMap.keySet()){
            if(!m_registry.getUsage(s).equals(Registry.Usage.HASARGS_SINGULAR)){
                rMap.put(s,tmpMap.get(s));
            }
        }*/

        // remove function names
        return rMap;
    }

    // updated for multiple pairs of goals (any match -- goals or'd)
    public STATUS isProved() {
        if (m_conjunction.m_evaluates_to_false)
            return STATUS.FALSE_ASSUMPTION; // this doesn't mean P->Q = False, it just means P = false
        for (int i = 0; i < m_goal.size(); i += 2) {
            String goal1 = m_goal.get(i);
            String goal2 = m_goal.get(i + 1);
            int g1 = m_registry.getIndexForSymbol(goal1);
            int g2 = m_registry.getIndexForSymbol(goal2);
            // check each goal has same root
            if (g1 == g2) {
                return STATUS.PROVED;
            }
        }
        return STATUS.STILL_EVALUATING;
    }

    private void addPExp(Iterator<PExp> pit, boolean inAntecedent) {
        while (pit.hasNext()) {
            PExp curr = pit.next();
            if (curr.isEquality() && inAntecedent) { // f(x,y) = z and g(a,b) = c ; then z is replaced by c

                //if (inAntecedent) {
                    m_conjunction.addExpression(curr);
                //}
                /*else {
                    PExp lhs = curr.getSubExpressions().get(0);
                    PExp rhs = curr.getSubExpressions().get(1);
                    int lhsIndex = (m_conjunction.addFormula(lhs));
                    int rhsIndex = (m_conjunction.addFormula(rhs));
                    addGoal(m_registry.getSymbolForIndex(lhsIndex), m_registry
                            .getSymbolForIndex(rhsIndex));
                }*/
            }
            else { // P becomes P = true or P(x...) becomes P(x ...) = z and z is replaced by true

                if (inAntecedent) {
                    m_conjunction.addExpression(curr);
                    //m_conjunction.mergeOperators(m_registry
                    //        .getIndexForSymbol("true"), intRepForExp);
                }
                else {
                    int intRepForExp = m_conjunction.addFormula(curr);
                    addGoal(m_registry.getSymbolForIndex(intRepForExp), "true");
                }
            }

        }

    }

    private void addGoal(String a, String b) {
        m_goal.add(a);
        m_goal.add(b);
    }

    @Override
    public String toString() {
        String r = m_name + "\n" + m_conjunction;
        for (PExp pq : forAllQuantifiedPExps) {
            r += pq.toString() + "\n";
        }
        r += "----------------------------------\n";

        String ro0 =
                m_registry.getSymbolForIndex(m_registry
                        .getIndexForSymbol(m_goal.get(0)));
        String ro1 =
                m_registry.getSymbolForIndex(m_registry
                        .getIndexForSymbol(m_goal.get(1)));
        r += ro0 + "=" + ro1 + "\n";

        return r;
    }

}
