/*
 * VerificationConditionCongruenceClosureImpl.java
 * ---------------------------------
 * Copyright (c) 2018
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

        // seed with (true = false) = false
        ArrayList<PExp> args = new ArrayList<PExp>();
        PSymbol fls = new PSymbol(m_typegraph.BOOLEAN, null, "false");
        PSymbol tr = new PSymbol(m_typegraph.BOOLEAN, null, "true");
        args.add(tr);
        args.add(fls);
        PSymbol trEqF = new PSymbol(m_typegraph.BOOLEAN, null, "=B", args);
        args.clear();
        args.add(trEqF);
        args.add(fls);
        PSymbol trEqFEqF = new PSymbol(m_typegraph.BOOLEAN, null, "=B", args);
        args.clear();
        m_conjunction.addExpression(trEqFEqF);

        // seed with true and true.  Need this for search: x and y, when x and y are both true
        args.add(tr);
        args.add(tr);
        PSymbol tandt = new PSymbol(m_typegraph.BOOLEAN, null, "andB", args);
        args.clear();
        args.add(tandt);
        args.add(tr);
        PSymbol tandteqt = new PSymbol(m_typegraph.BOOLEAN, null, "=B", args);
        m_conjunction.addExpression(tandteqt);
        args.clear();
        // seed with true and false = false
        args.add(tr);
        args.add(fls);
        PSymbol tandf = new PSymbol(m_typegraph.BOOLEAN, null, "andB", args);
        args.clear();
        args.add(tandf);
        args.add(fls);
        PSymbol tandfeqf = new PSymbol(m_typegraph.BOOLEAN, null, "=B", args);
        m_conjunction.addExpression(tandfeqf);
        // seed with false and false = false
        args.clear();
        args.add(fls);
        args.add(fls);
        PSymbol fandf = new PSymbol(m_typegraph.BOOLEAN, null, "andB", args);
        args.clear();
        args.add(fandf);
        args.add(fls);
        PSymbol fandfeqf = new PSymbol(m_typegraph.BOOLEAN, null, "=B", args);
        m_conjunction.addExpression(fandfeqf);
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
                // Temp: replace with eliminate()
                if (curr.getTopLevelOperation().equals("orB")) {
                    addGoal(m_registry.getSymbolForIndex(m_conjunction
                            .addFormula(curr.getSubExpressions().get(0))));
                    addGoal(m_registry.getSymbolForIndex(m_conjunction
                            .addFormula(curr.getSubExpressions().get(1))));
                }
                else {
                    int intRepForExp = m_conjunction.addFormula(curr);
                    addGoal(m_registry.getSymbolForIndex(intRepForExp));
                }
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
