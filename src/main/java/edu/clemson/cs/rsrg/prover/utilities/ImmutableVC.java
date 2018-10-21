/*
 * ImmutableVC.java
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
package edu.clemson.cs.rsrg.prover.utilities;

import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationCondition;
import java.util.*;

/**
 * <p>This class represents an immutable <em>verification condition</em>,
 * which takes the form of a mathematical implication.</p>
 *
 * @author Hampton Smith
 * @author Mike Kabbani
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class ImmutableVC {

    // ===========================================================
    // Current VC Status
    // ===========================================================

    public enum STATUS {
        FALSE_ASSUMPTION, STILL_EVALUATING, PROVED, UNPROVABLE
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Name is a human-readable name for the VC used for debugging purposes.</p */
    private final String myName;

    /** <p>Registry for symbols that we have encountered so far.</p> */
    private final Registry myRegistry;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    /** <p>A copy of the VC generated from {@link edu.clemson.cs.rsrg.vcgeneration.VCGenerator}.</p> */
    private final VerificationCondition myVCCopy;

    /** <p>A {@code VC} goal as set of strings.</p> */
    public final Set<String> VCGoalStrings;

    /*
    private HashMap<PLambda, String> m_liftedLamdas;
    // PLambda objects aren't hashing correctly.  would have to get into haschode/eq methods of PExp heirarchy
    private HashMap<String, PLambda> m_lamdaCodes;
    public java.util.List<PSymbol> m_liftedLambdaPredicates;
    public HashMap<String, PSymbol> rhsOfLamPredsToLamPreds;
    public java.util.Set<PExp> m_conditions;
    private int m_lambdaTag = 0;
    private int m_qVarTag = 0;
    private ImmutableVC liftedCopy; */

    // ===========================================================
    // Constructors
    // ===========================================================

    public ImmutableVC(VerificationCondition vc, TypeGraph g) {
        myName = vc.getName();
        myRegistry = new Registry(g);
        myTypeGraph = g;
        myVCCopy = vc.clone();
        VCGoalStrings = new HashSet<>();

        // Convert the antecedent/consequent from the sequent VC into the
        // format that the prover expects.
        //processSequentVC(vc.getSequent());

        /*
        m_liftedLamdas = new HashMap<>();
        m_liftedLambdaPredicates = new ArrayList<>();
        m_conditions = new HashSet<>();
        m_lamdaCodes = new HashMap<>();
        rhsOfLamPredsToLamPreds = new HashMap<>();*/
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method adds a new goal to prove.</p>
     *
     * @param a A goal represented using a string.
     */
    public final void addGoal(String a) {
        String r = myRegistry.getRootSymbolForSymbol(a);
        if (VCGoalStrings.contains(r)) {
            return;
        }

        VCGoalStrings.add(r);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /*private void processSequentVC(Sequent sequent) {
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
    }*/

    /*public String getName() {
        String retval = myName;

        if (myDerivedFlag) {
            retval += " (modified)";
        }

        return retval;
    }

    public void convertAllToPsymbols(TypeGraph g) {
        m_typegraph = g;
        liftLambdas();
        convertPAlternativesToCF();
        replaceLambdaSymbols();
        normalizeConditions();
        uniquelyNameQuantifiers();
    }

    // ensures conditions are unique
    public void normalizeConditions() {
        java.util.HashSet<PExp> replacement = new java.util.HashSet<PExp>();
        java.util.HashSet<String> inSet = new java.util.HashSet<String>();
        for (PExp p : m_conditions) {
            // replace the qVars dup with normalized names
            java.util.Set<PSymbol> qVars = p.getQuantifiedVariables();
            HashMap<PExp, PExp> substMap = new HashMap<PExp, PExp>();
            for (PSymbol pq : qVars) {
                PSymbol repP =
                        new PSymbol(pq.getType(), pq.getTypeValue(), pq
                                .getType().toString()
                                + m_qVarTag++, pq.quantification);
                substMap.put(pq, repP);
            }
            if (!substMap.isEmpty()) {
                p = (PSymbol) p.substitute(substMap);
            }
            if (!inSet.contains(p.toString())) {
                replacement.add(p);
                inSet.add(p.toString());
            }
            m_qVarTag = 0;
        }
        m_conditions = replacement;
    }

    public void uniquelyNameQuantifiers() {
        java.util.HashSet<PExp> replacement = new java.util.HashSet<PExp>();
        for (PExp p : m_conditions) {
            // replace the qVars dup with unique names
            java.util.Set<PSymbol> qVars = p.getQuantifiedVariables();
            HashMap<PExp, PExp> substMap = new HashMap<PExp, PExp>();
            for (PSymbol pq : qVars) {
                PSymbol repP =
                        new PSymbol(pq.getType(), pq.getTypeValue(), "¢vl"
                                + pq.getType().toString() + m_qVarTag++,
                                pq.quantification);
                substMap.put(pq, repP);
            }
            if (!substMap.isEmpty()) {
                p = (PSymbol) p.substitute(substMap);
            }
            replacement.add(p);
            //m_qVarTag = 0;
        }
        m_conditions = replacement;
    }

    public void replaceLambdaSymbols() {
        Map<PExp, PExp> substMap = new HashMap<PExp, PExp>();
        ArrayList<PExp> newConjuncts = new ArrayList<PExp>();
        java.util.List<PExp> a_p = myAntecedent.getMutableCopy();
        // build map
        for (PExp p : a_p) {
            if (p.isEquality()) {
                ImmutableList<PExp> args = p.getSubExpressions();
                PExp args0 = args.get(0);
                PExp args1 = args.get(1);
                if (args0.isVariable() && args1.isVariable()) {
                    if (args0.getTopLevelOperation().contains("lambda")) {
                        substMap.put(args0, args1);
                    }
                    else if (args1.getTopLevelOperation().contains("lambda")) {
                        substMap.put(args1, args0);
                    }
                }
            }
        }
        if (!substMap.isEmpty()) {
            myAntecedent = new Antecedent(myAntecedent.substitute(substMap));
            myConsequent = new Consequent(myConsequent.substitute(substMap));
            ArrayList<PSymbol> n_Preds = new ArrayList<PSymbol>();
            for (PSymbol p : m_liftedLambdaPredicates) {
                n_Preds.add((PSymbol) p.substitute(substMap));
            }
            m_liftedLambdaPredicates = n_Preds;
        }

    }

    public void convertPAlternativesToCF() {
        ArrayList<PSymbol> converted = new ArrayList<PSymbol>();
        ArrayList<PExp> noQuantConverted = new ArrayList<PExp>();
        for (PSymbol p : m_liftedLambdaPredicates) {
            if (p.arguments.size() == 2
                    && p.arguments.get(1) instanceof PAlternatives) {
                // lhs can't be a PALT
                PExp lhs = p.arguments.get(0);
                PExp rhs = p.arguments.get(1);
                ArrayList<PExp> args = new ArrayList<PExp>();
                PAlternatives asPa = (PAlternatives) rhs;
                if (asPa.myAlternatives.size() > 1)
                    throw new RuntimeException("Only 1 alternative supported");
                PAlternatives.Alternative alt = asPa.myAlternatives.get(0);
                PExp quantVar = lhs.getQuantifiedVariables().iterator().next();
                PExp cond = alt.condition;
                PExp conFunc = orderPlusOne(cond, quantVar, converted);
                PExp posChoice = alt.result;
                PExp posChoiceFun =
                        orderPlusOne(posChoice, quantVar, converted);
                PExp negChoice = asPa.myOtherwiseClauseResult;
                PExp negChoiceFun =
                        orderPlusOne(negChoice, quantVar, converted);
                args.add(conFunc);
                args.add(posChoiceFun);
                args.add(negChoiceFun);
                // remember to type this as Z->Entity
                PSymbol cf =
                        new PSymbol(posChoiceFun.getType(), null, "CF", args);
                args.clear();
                if (rhsOfLamPredsToLamPreds.containsKey(cf.toString())) {

                }
                else {
                    PSymbol lhsPsym =
                            new PSymbol(m_typegraph.BOOLEAN, null, lhs
                                    .getTopLevelOperation(),
                                    PSymbol.Quantification.NONE);
                    args.add(lhsPsym);
                    args.add(cf);
                    PSymbol cfPred =
                            new PSymbol(m_typegraph.BOOLEAN, null, "=", args);
                    if (cfPred.getQuantifiedVariables().size() > 0)
                        converted.add(cfPred);
                    else
                        noQuantConverted.add(cfPred);
                }

            }
            else
                converted.add(p);
        }
        m_liftedLambdaPredicates = converted;
        java.util.List<PExp> aList = myAntecedent.getMutableCopy();
        aList.addAll(noQuantConverted);
        myAntecedent = new Antecedent(aList);
    }

    private PExp orderPlusOne(PExp thingToHigherOrder, PExp quantVar,
            ArrayList<PSymbol> sideList) {
        java.util.Set<PSymbol> qVarSet =
                thingToHigherOrder.getQuantifiedVariables();
        if (qVarSet.size() > 1)
            throw new RuntimeException("Only 1 quantified var. supported");
        // no need to always make a new function
        String funStr = "";
        if (qVarSet.size() == 1 && qVarSet.contains(quantVar)
                && thingToHigherOrder.getSubExpressions().size() == 1) {
            MTFunction hoType =
                    new MTFunction(m_typegraph, thingToHigherOrder.getType(),
                            quantVar.getType());
            return new PSymbol(hoType, null, thingToHigherOrder
                    .getTopLevelOperation());
        }
        PSymbol funName;
        if (rhsOfLamPredsToLamPreds.containsKey(thingToHigherOrder.toString())) {
            String fStr =
                    rhsOfLamPredsToLamPreds.get(thingToHigherOrder.toString())
                            .getSubExpressions().get(0).getTopLevelOperation();
            return new PSymbol(new MTFunction(m_typegraph, thingToHigherOrder
                    .getType(), quantVar.getType()), null, fStr);
        }
        else {
            funName =
                    new PSymbol(new MTFunction(m_typegraph, thingToHigherOrder
                            .getType(), quantVar.getType()), null, "lambda"
                            + (m_lambdaTag++));
            ArrayList<PExp> args = new ArrayList<PExp>();
            args.add(quantVar);
            PSymbol funAppl =
                    new PSymbol(thingToHigherOrder.getType(), null, funName
                            .getTopLevelOperation(), args,
                            PSymbol.Quantification.NONE);
            args.clear();
            args.add(funAppl);
            args.add(thingToHigherOrder);
            PSymbol conQuant =
                    new PSymbol(m_typegraph.BOOLEAN, null, "=", args);
            sideList.add(conQuant);
            rhsOfLamPredsToLamPreds
                    .put(thingToHigherOrder.toString(), conQuant);
        }
        return funName;
    }

    // Assumes lambdas lifted first
    // Assumes all remaining PAlternatives are in m_liftedLambdaPredicates
    public void convertPAlternativesToImplications() {
        ArrayList<PSymbol> converted = new ArrayList<PSymbol>();
        for (PSymbol p : m_liftedLambdaPredicates) {
            if (p.arguments.size() == 2) {
                // lhs can't be a PALT
                PExp lhs = p.arguments.get(0);
                PExp rhs = p.arguments.get(1);
                if (rhs instanceof PAlternatives) {
                    PAlternatives asPa = (PAlternatives) rhs;
                    ArrayList<PExp> conditions = new ArrayList<PExp>();
                    for (PAlternatives.Alternative pa : asPa.myAlternatives) {
                        conditions.add(pa.condition);
                        ArrayList<PExp> args = new ArrayList<PExp>();
                        args.add(lhs);
                        args.add(pa.result);
                        PSymbol ant =
                                new PSymbol(m_typegraph.BOOLEAN, null, "=",
                                        args);

                        args.clear();
                        args.add(pa.condition);
                        args.add(ant);
                        PSymbol pc =
                                new PSymbol(m_typegraph.BOOLEAN, null,
                                        "implies", args);
                        converted.add(pc);
                        m_conditions.add(pa.condition);
                    }

                    // do otherwise clause
                    if (conditions.size() > 1) {
                        // make conjunction
                    }
                    else {
                        ArrayList<PExp> args = new ArrayList<PExp>();
                        args.add(conditions.get(0));
                        PExp neg =
                                new PSymbol(m_typegraph.BOOLEAN, null, "not",
                                        args);
                        args.clear();

                        args.add(lhs);
                        args.add(asPa.myOtherwiseClauseResult);
                        PExp eq =
                                new PSymbol(m_typegraph.BOOLEAN, null, "=",
                                        args);
                        args.clear();
                        args.add(neg);
                        args.add(eq);
                        converted.add(new PSymbol(m_typegraph.BOOLEAN, null,
                                "implies", args));
                        //m_conditions.add(neg);
                    }

                }
                // No PAlt
                else {
                    converted.add(p);
                }
            }

        }
        m_liftedLambdaPredicates = converted;
    }

    public void liftLambdas() {
        ArrayList<PExp> newConjuncts = new ArrayList<PExp>();
        java.util.List<PExp> a_p = myAntecedent.getMutableCopy();
        for (PExp p : a_p) {
            newConjuncts.add(recursiveLift(p));
        }
        myAntecedent = new Antecedent(newConjuncts);

        newConjuncts.clear();
        a_p = myConsequent.getMutableCopy();
        for (PExp p : a_p) {
            newConjuncts.add(recursiveLift(p));
        }
        myConsequent = new Consequent(newConjuncts);

        for (PLambda p : m_liftedLamdas.keySet()) {
            String name = m_liftedLamdas.get(p);
            PExp body = p.getBody();
            PSymbol lhs =
                    new PSymbol(p.getType(), p.getTypeValue(), name, p
                            .getParameters());
            ArrayList<PExp> args = new ArrayList<PExp>();
            args.add(lhs);
            args.add(body);
            m_liftedLambdaPredicates.add(new PSymbol(m_typegraph.BOOLEAN, null,
                    "=", args));
        }
    }

    private PExp recursiveLift(PExp p) {
        ArrayList<PExp> newArgList = new ArrayList<PExp>();
        for (PExp p_s : p.getSubExpressions()) {
            newArgList.add(recursiveLift(p_s));
        }
        if (p instanceof PLambda) {
            // replace lam(x).F(x) wth F
            PLambda pl = (PLambda) p;
            PExp body = pl.getBody();
            if ((pl.getParameters().size() == 1 && body
                    .getQuantifiedVariables().size() == 1)
                    && pl.getParameters().get(0).toString().equals(
                            body.getQuantifiedVariables().iterator().next()
                                    .toString())
                    && body.getSubExpressions().size() == 1
                    && body.getSubExpressions().get(0).isVariable()) {
                PSymbol funName =
                        new PSymbol(new MTFunction(m_typegraph, body.getType(),
                                pl.getParameters().get(0).getType()), null,
                                body.getTopLevelOperation());
                return funName;
            }
            String lname = "";
            // Normalize parameters here
            PLambda normP = ((PLambda) p).withNormalizedParameterNames();
            String lambdaCode = normP.toString();
            if (!m_lamdaCodes.containsKey(lambdaCode)) {
                lname = "lambda" + m_lambdaTag++;
                m_lamdaCodes.put(lambdaCode, (PLambda) normP);
                m_liftedLamdas.put((PLambda) normP, lname);
            }
            else {
                PLambda foundLamb = m_lamdaCodes.get(lambdaCode);
                lname = m_liftedLamdas.get(foundLamb);
            }
            return new PSymbol(normP.getType(), normP.getTypeValue(), lname);

        }
        return new PSymbol(p.getType(), p.getTypeValue(), p
                .getTopLevelOperation(), newArgList);
    }

    public String getSourceName() {
        return myName;
    }

    public Antecedent getAntecedent() {
        return myAntecedent;
    }

    public Consequent getConsequent() {
        return myConsequent;
    }

    @Override
    public String toString() {

        String retval =
                "========== " + getName() + " ==========\n" + myAntecedent
                        + "  -->\n" + myConsequent;

        if (!m_liftedLambdaPredicates.isEmpty()) {
            retval += "lifted lambda predicates:\n";
            for (PExp p : m_liftedLambdaPredicates) {
                retval += p.toString() + "\n";
            }
        }
        return retval;
    }

    public void processStringRepresentation(PExpVisitor visitor, Appendable a) {

        try {
            a.append("========== " + getName() + " ==========\n");
            myAntecedent.processStringRepresentation(visitor, a);
            a.append("  -->\n");
            myConsequent.processStringRepresentation(visitor, a);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/
}