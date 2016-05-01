/**
 * VerificationConditionCongruenceClosureImpl.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.rewriteprover.*;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

import java.util.*;

/**
 * Created by mike on 4/3/2014.
 */
public class VerificationConditionCongruenceClosureImpl {

    private final Registry m_registry;
    private final TypeGraph m_typegraph;
    public final String m_name;
    public final String m_VC_string;
    private final Antecedent m_antecedent;
    private final Consequent m_consequent;
    private final ConjunctionOfNormalizedAtomicExpressions m_conjunction;
    private final MTType m_z;
    private final MTType m_n;
    protected final Set<String> m_goal;
    private int m_fc_ctr = 0;
    private final boolean DOCOMPLEMENTS = false;

    public static enum STATUS {
        FALSE_ASSUMPTION, STILL_EVALUATING, PROVED, UNPROVABLE
    }

    public List<PExp> forAllQuantifiedPExps; // trap constraints, can create Theorems externally from this

    // currently support only unchained equalities, so each sublist is size 2.
    public VerificationConditionCongruenceClosureImpl(TypeGraph g, VC vc,
            MTType z, MTType n) {
        m_typegraph = g;
        m_name = vc.getName();
        m_VC_string = vc.toString();
        m_antecedent = vc.getAntecedent();
        m_consequent = vc.getConsequent();
        m_registry = new Registry(g);
        m_z = z;
        m_n = n;
        m_conjunction =
                new ConjunctionOfNormalizedAtomicExpressions(m_registry, this);
        m_goal = new HashSet<String>();

        addPExp(m_consequent.iterator(), false);
        addPExp(m_antecedent.iterator(), true);

        forAllQuantifiedPExps = new ArrayList<PExp>();
        if (vc.m_liftedLambdaPredicates != null
                && vc.m_liftedLambdaPredicates.size() > 0) {
            forAllQuantifiedPExps.addAll(vc.m_liftedLambdaPredicates);
            //addPExp(forAllQuantifiedPExps.iterator(),true);
            makeSetAssertions(vc);
        }
        // seed with not(false)
        ArrayList<PExp> args = new ArrayList<PExp>();
        PSymbol fls = new PSymbol(m_typegraph.BOOLEAN, null, "false");
        args.add(fls);
        PSymbol nF = new PSymbol(m_typegraph.BOOLEAN, null, "not", args);
        m_conjunction.addExpression(nF);
        args.clear();
        // seed with true and true.  Need this for search: x and y, when x and y are both true
        PSymbol tr = new PSymbol(m_typegraph.BOOLEAN, null, "true");
        args.add(tr);
        args.add(tr);
        PSymbol tandt = new PSymbol(m_typegraph.BOOLEAN, null, "and", args);
        args.clear();
        args.add(tandt);
        args.add(tr);
        PSymbol tandteqt = new PSymbol(m_typegraph.BOOLEAN, null, "=", args);
        m_conjunction.addExpression(tandteqt);
        args.clear();
        // seed with true and false = false
        args.add(tr);
        args.add(fls);
        PSymbol tandf = new PSymbol(m_typegraph.BOOLEAN, null, "and", args);
        args.clear();
        args.add(tandf);
        args.add(fls);
        PSymbol tandfeqf = new PSymbol(m_typegraph.BOOLEAN, null, "=", args);
        m_conjunction.addExpression(tandfeqf);
        // seed with false and false = false
        args.clear();
        args.add(fls);
        args.add(fls);
        PSymbol fandf = new PSymbol(m_typegraph.BOOLEAN, null, "and", args);
        args.clear();
        args.add(fandf);
        args.add(fls);
        PSymbol fandfeqf = new PSymbol(m_typegraph.BOOLEAN, null, "=", args);
        m_conjunction.addExpression(fandfeqf);
        //m_goal.add("false");
    }

    /* Ex.: p is for all k:Z, lambda0(k) = (x <= k)
    Adds these assertions:
    lambda0(_sv0) = (x <= _sv0)
    ZSetConB(lambda0) = ZSetCons(_sv0)
    ------ Complements
    lambda0(_sv0_Comp) = not(x <=_sv0_Comp)
    ZSetComplement(ZSetCons(_sv0)) = ZSetCons(_sv0_Comp)
     */
    protected void assertSet(PExp p, ModuleScope scope) {
        if (p.getQuantifiedVariables().size() != 1)
            return;
        List<edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry> entries =
                scope.query(new NameQuery(null, "ZSetCons",
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE,
                        false));
        if (entries.isEmpty())
            return;
        MathSymbolEntry e = (MathSymbolEntry) entries.get(0);
        MTType consT = e.getType();
        entries =
                scope.query(new NameQuery(null, "ZSetConB",
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE,
                        false));
        if (entries.isEmpty())
            return;
        e = (MathSymbolEntry) entries.get(0);
        MTFunction conB = (MTFunction) e.getType();
        PSymbol arg = p.getQuantifiedVariables().iterator().next();
        PSymbol fc =
                new PSymbol(arg.getType(), arg.getTypeValue(), "_sv"
                        + ++m_fc_ctr);
        HashMap<PExp, PExp> subMap = new HashMap<PExp, PExp>();
        subMap.put(arg, fc);
        PExp toAdd = p.substitute(subMap);
        m_conjunction.addExpression(toAdd);
        ArrayList<PExp> args = new ArrayList<PExp>();
        // change to ensure always adding f of f(x)
        PExp fx = toAdd.getSubExpressions().get(0);
        PSymbol lamName =
                new PSymbol(new MTFunction(m_typegraph, fx.getType(), fc
                        .getType()), null, fx.getTopLevelOperation());
        args.add(lamName);
        PSymbol s1 = new PSymbol(conB, null, "ZSetConB", args);
        args.clear();
        args.add(fc);
        PSymbol s2 = new PSymbol(consT, null, "ZSetCons", args);
        args.clear();
        args.add(s1);
        args.add(s2);
        PSymbol setAsrt = new PSymbol(m_typegraph.BOOLEAN, null, "=", args);
        m_conjunction.addExpression(setAsrt);

        // What follows enters set complement assertions
        if (DOCOMPLEMENTS) {
            entries =
                    scope
                            .query(new NameQuery(
                                    null,
                                    "ZSetComplement",
                                    MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                                    MathSymbolTable.FacilityStrategy.FACILITY_INSTANTIATE,
                                    false));
            if (entries.isEmpty())
                return;
            MTType compT = ((MathSymbolEntry) entries.get(0)).getType();
            args.clear();
            args.add(p.getSubExpressions().get(1));
            PSymbol notRhs =
                    new PSymbol(m_typegraph.BOOLEAN, null, "not", args);
            args.clear();
            args.add(p.getSubExpressions().get(0));
            args.add(notRhs);
            PExp notP = new PSymbol(m_typegraph.BOOLEAN, null, "=", args);
            PSymbol fc_comp =
                    new PSymbol(arg.getType(), arg.getTypeValue(), "_sv"
                            + m_fc_ctr + "_Comp");
            subMap.clear();
            subMap.put(arg, fc_comp);
            notP = notP.substitute(subMap);
            m_conjunction.addExpression(notP);
            args.clear();
            args.add(s2);
            PSymbol compL = new PSymbol(compT, null, "ZSetComplement", args);
            args.clear();
            args.add(fc_comp);
            PSymbol compR = new PSymbol(consT, null, "ZSetCons", args);
            args.clear();
            args.add(compL);
            args.add(compR);
            PSymbol compEq = new PSymbol(m_typegraph.BOOLEAN, null, "=", args);
            m_conjunction.addExpression(compEq);
        }
    }

    private void makeSetAssertions(VC vc) {
        ArrayList<PExp> splitConditions = new ArrayList<PExp>();
        boolean allOverZ = true;
        int c_count = 0;
        for (PExp px : vc.m_conditions) {
            // name it. add it to quantified expr list
            if (px.getQuantifiedVariables().size() != 1)
                continue;
            PSymbol qFun =
                    new PSymbol(new MTFunction(m_typegraph,
                            m_typegraph.BOOLEAN, px.getQuantifiedVariables()
                                    .iterator().next().getType()), null,
                            "ConFunc" + (++c_count), new ArrayList<PExp>(px
                                    .getQuantifiedVariables()));
            ArrayList<PExp> args = new ArrayList<PExp>();
            args.add(qFun);
            args.add(px);
            PSymbol qFunAssrt =
                    new PSymbol(m_typegraph.BOOLEAN, null, "=", args);
            args.clear();
            forAllQuantifiedPExps.add(qFunAssrt);

            java.util.HashMap<PExp, PExp> substMap = new HashMap<PExp, PExp>();

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
            args.add(new PSymbol(m_typegraph.BOOLEAN, null, "conVal"
                    + (++c_count)));
            splitConditions.add(new PSymbol(m_typegraph.BOOLEAN, null, "=",
                    args));
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
            args.add(new PSymbol(m_typegraph.BOOLEAN, null, "conVal"
                    + (++c_count)));
            splitConditions.add(new PSymbol(m_typegraph.BOOLEAN, null, "=",
                    args));

            args.clear();
            args.add(tBSym);
            args.add(fBSym);
            PSymbol assertion =
                    new PSymbol(m_typegraph.BOOLEAN, null, "unionMakesZ", args);
            if (allOverZ)
                splitConditions.add(assertion);
        }
        addPExp(splitConditions.iterator(), true);

    }

    private void makeSetAssertionsOld(VC vc) {
        ArrayList<PExp> splitConditions = new ArrayList<PExp>();
        boolean allOverZ = true;
        for (PExp px : vc.m_conditions) {
            java.util.HashMap<PExp, PExp> substMap = new HashMap<PExp, PExp>();
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
                    new PSymbol(m_typegraph.BOOLEAN, null, "unionMakesZ", args);
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

    public STATUS isProved() {
        if (m_conjunction.m_evaluates_to_false) {
            return STATUS.FALSE_ASSUMPTION; // this doesn't mean P->Q = False, it just means P = false
        }
        else if (m_goal.contains("true")) {
            return STATUS.PROVED;
        }
        else {
            return STATUS.STILL_EVALUATING;
        }
    }

    private void addPExp(Iterator<PExp> pit, boolean inAntecedent) {
        while (pit.hasNext() && !m_conjunction.m_evaluates_to_false) {
            PExp curr =
                    Utilities.replacePExp(pit.next(), m_typegraph, m_z, m_n);
            if (inAntecedent) {
                m_conjunction.addExpression(curr);
            }
            else {
                int intRepForExp = m_conjunction.addFormula(curr);
                addGoal(m_registry.getSymbolForIndex(intRepForExp));
            }
        }
    }

    protected void addGoal(String a) {
        String r = m_registry.getRootSymbolForSymbol(a);
        if (m_goal.contains(r))
            return;
        m_goal.add(r);
    }

    @Override
    public String toString() {
        String r = "\n" + "\n" + m_name + "\n" + m_conjunction;
        for (PExp pq : forAllQuantifiedPExps) {
            r += pq.toString() + "\n";
        }
        r += "----------------------------------\n";

        // Goals
        if (m_goal.isEmpty())
            return r;
        for (String gS : m_goal) {
            r += m_registry.getRootSymbolForSymbol(gS) + " ";
        }
        r += "\n";
        return r;
    }

}
