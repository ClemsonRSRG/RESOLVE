/*
 * ImmutableVC.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.utilities;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.prover.absyn.PExp;
import edu.clemson.rsrg.prover.absyn.expressions.*;
import edu.clemson.rsrg.prover.immutableadts.ImmutableList;
import edu.clemson.rsrg.prover.utilities.expressions.ConjunctionOfNormalizedAtomicExpressions;
import edu.clemson.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
import java.util.*;

/**
 * <p>
 * This class represents an immutable <em>verification condition</em>, which takes the form of a mathematical
 * implication.
 * </p>
 *
 * @author Hampton Smith
 * @author Mike Kabbani
 * @author Yu-Shan Sun
 *
 * @version 2.0
 */
public class ImmutableVC {

    // ===========================================================
    // Current VC Status
    // ===========================================================

    /**
     * <p>
     * An enumeration for the current VC prove status.
     * </p>
     */
    public enum STATUS {
        FALSE_ASSUMPTION, STILL_EVALUATING, PROVED, UNPROVABLE
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A conjunction of post-processed expressions.
     * </p>
     */
    private final ConjunctionOfNormalizedAtomicExpressions myConjunction;

    /**
     * <p>
     * Name is a human-readable name for the VC used for debugging purposes.</p
     */
    private final String myName;

    /**
     * <p>
     * Registry for symbols that we have encountered so far.
     * </p>
     */
    private final Registry myRegistry;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>
     * A copy of the VC generated from {@link edu.clemson.rsrg.vcgeneration.VCGenerator}.
     * </p>
     */
    private final VerificationCondition myVCCopy;

    /**
     * <p>
     * A {@code VC} goal as set of strings.
     * </p>
     */
    public final Set<String> VCGoalStrings;

    // -----------------------------------------------------------
    // N and Z
    // -----------------------------------------------------------

    /**
     * <p>
     * A mathematical type representing {@code N}.
     * </p>
     */
    private final MTType N;

    /**
     * <p>
     * A mathematical type representing {@code Z}.
     * </p>
     */
    private final MTType Z;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an immutable <em>verification condition</em> for the prover.
     * </p>
     *
     * @param vc
     *            Sequent VC with mutable expressions.
     * @param g
     *            The mathematical type graph.
     * @param nType
     *            The mathematical type "N".
     * @param zType
     *            The mathematical type "Z".
     */
    public ImmutableVC(VerificationCondition vc, TypeGraph g, MTType nType, MTType zType) {
        myName = vc.getName();
        myTypeGraph = g;
        myVCCopy = vc.clone();
        VCGoalStrings = new HashSet<>();

        // N and Z
        N = nType;
        Z = zType;

        myRegistry = new Registry(g);
        myConjunction = new ConjunctionOfNormalizedAtomicExpressions(this, myRegistry);

        // Convert the antecedent/consequent from the sequent VC into the
        // format that the prover expects.
        processSequentVC(vc.getSequent());

        // Add default theorems for proving a VC
        seedDefaultTheorems();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method adds a new goal to prove.
     * </p>
     *
     * @param a
     *            A goal represented using a string.
     */
    public final void addGoal(String a) {
        String r = myRegistry.getRootSymbolForSymbol(a);
        if (VCGoalStrings.contains(r)) {
            return;
        }

        VCGoalStrings.add(r);
    }

    /**
     * <p>
     * This method returns the conjunction of post-processed antecedents.
     * </p>
     *
     * @return A {@link ConjunctionOfNormalizedAtomicExpressions}.
     */
    public final ConjunctionOfNormalizedAtomicExpressions getConjunct() {
        return myConjunction;
    }

    /**
     * <p>
     * This method returns the current registry of symbols.
     * </p>
     *
     * @return A {@link Registry}.
     */
    public final Registry getRegistry() {
        return myRegistry;
    }

    /**
     * <p>
     * This method returns the current proving status for this VC.
     * </p>
     *
     * @return A status enumeration.
     */
    public final STATUS isProved() {
        if (myConjunction.evaluatesToFalse()) {
            return STATUS.FALSE_ASSUMPTION; // this doesn't mean P->Q = False, it just means P = false
        } else if (VCGoalStrings.contains("true")) {
            return STATUS.PROVED;
        } else {
            return STATUS.STILL_EVALUATING;
        }
    }

    /**
     * <p>
     * This method returns this immutable VC in string format.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String toString() {
        StringBuilder r = new StringBuilder("\n" + "\n" + myName + "\n" + myConjunction);
        r.append("----------------------------------\n");

        // Goals
        if (VCGoalStrings.isEmpty()) {
            return r.toString();
        }

        for (String gS : VCGoalStrings) {
            r.append(myRegistry.getRootSymbolForSymbol(gS)).append(" ");
        }
        r.append("\n");

        return r.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for processing the sequent in a {@code VC}.
     * </p>
     *
     * @param sequent
     *            Sequent VC.
     */
    private void processSequentVC(Sequent sequent) {
        // Convert the antecedent/consequent from the sequent VC into the
        // auxiliary form for further conversion and processing.
        AuxiliaryVCRepresentation auxiliaryVCRepresentation = new AuxiliaryVCRepresentation(sequent);

        // Add the antecedents expressions
        Iterator<PExp> antecedentIt = auxiliaryVCRepresentation.myAntecedents.iterator();
        while (antecedentIt.hasNext() && !myConjunction.evaluatesToFalse()) {
            PExp curr = Utilities.replacePExp(antecedentIt.next(), myTypeGraph, Z, N);
            myConjunction.addExpression(curr);

        }

        // Add the consequent expressions
        Iterator<PExp> consequentIt = auxiliaryVCRepresentation.myConsequents.iterator();
        while (consequentIt.hasNext() && !myConjunction.evaluatesToFalse()) {
            PExp curr = Utilities.replacePExp(consequentIt.next(), myTypeGraph, Z, N);

            // Temp: replace with eliminate()
            if (curr.getTopLevelOperation().equals("orB")) {
                addGoal(myRegistry.getSymbolForIndex(myConjunction.addFormula(curr.getSubExpressions().get(0))));
                addGoal(myRegistry.getSymbolForIndex(myConjunction.addFormula(curr.getSubExpressions().get(1))));
            } else {
                int intRepForExp = myConjunction.addFormula(curr);
                addGoal(myRegistry.getSymbolForIndex(intRepForExp));
            }
        }
    }

    /**
     * <p>
     * An helper method for adding default theorems for proving a {@code VC}.
     * </p>
     */
    private void seedDefaultTheorems() {
        // seed with (true = false) = false
        List<PExp> args = new ArrayList<>();
        PSymbol fls = new PSymbol(myTypeGraph.BOOLEAN, null, "false");
        PSymbol tr = new PSymbol(myTypeGraph.BOOLEAN, null, "true");
        args.add(tr);
        args.add(fls);
        PSymbol trEqF = new PSymbol(myTypeGraph.BOOLEAN, null, "=B", args);
        args.clear();
        args.add(trEqF);
        args.add(fls);
        PSymbol trEqFEqF = new PSymbol(myTypeGraph.BOOLEAN, null, "=B", args);
        args.clear();
        myConjunction.addExpression(trEqFEqF);

        // seed with true and true. Need this for search: x and y, when x and y are both true
        args.add(tr);
        args.add(tr);
        PSymbol tandt = new PSymbol(myTypeGraph.BOOLEAN, null, "andB", args);
        args.clear();
        args.add(tandt);
        args.add(tr);
        PSymbol tandteqt = new PSymbol(myTypeGraph.BOOLEAN, null, "=B", args);
        myConjunction.addExpression(tandteqt);
        args.clear();

        // seed with true and false = false
        args.add(tr);
        args.add(fls);
        PSymbol tandf = new PSymbol(myTypeGraph.BOOLEAN, null, "andB", args);
        args.clear();
        args.add(tandf);
        args.add(fls);
        PSymbol tandfeqf = new PSymbol(myTypeGraph.BOOLEAN, null, "=B", args);
        myConjunction.addExpression(tandfeqf);

        // seed with false and false = false
        args.clear();
        args.add(fls);
        args.add(fls);
        PSymbol fandf = new PSymbol(myTypeGraph.BOOLEAN, null, "andB", args);
        args.clear();
        args.add(fandf);
        args.add(fls);
        PSymbol fandfeqf = new PSymbol(myTypeGraph.BOOLEAN, null, "=B", args);
        myConjunction.addExpression(fandfeqf);
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * When building an {@link ImmutableVC}, both Mike and Hampton did a bunch of conversions. This class allows us to
     * keep the same logic, but not have a separate Java class.
     * </p>
     */
    private class AuxiliaryVCRepresentation {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * A list of antecedents.
         * </p>
         */
        private List<PExp> myAntecedents;

        /**
         * <p>
         * A list of consequents.
         * </p>
         */
        private List<PExp> myConsequents;

        // -----------------------------------------------------------
        // Conversion-Related
        // -----------------------------------------------------------

        /**
         * <p>
         * A set of condition expressions.
         * </p>
         */
        private Set<PExp> myConditions;

        /**
         * <p>
         * A map used for reverse lookup when lifting lambda expressions.
         * </p>
         */
        private final Map<String, PLambda> myLambdaCodes;

        /**
         * <p>
         * A counter for keeping track of all lambda expressions.
         * </p>
         */
        private int myLambdaTag;

        /**
         * <p>
         * A map used for lifting lambda expressions.
         * </p>
         */
        private final Map<PLambda, String> myLiftedLambdas;

        /**
         * <p>
         * A list of lifted lambda predicates.
         * </p>
         */
        private List<PSymbol> myLiftedLambdaPredicates;

        /**
         * <p>
         * A counter for keeping track of all quantified variables.
         * </p>
         */
        private int myQVarTag;

        /**
         * <p>
         * A map used for the right hand side of lambda predicates.
         * </p>
         */
        private final Map<String, PSymbol> myRhsOfLamPredsToLamPreds;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This constructs an auxiliary VC representation.
         * </p>
         *
         * @param sequent
         *            Sequent VC.
         */
        AuxiliaryVCRepresentation(Sequent sequent) {
            myAntecedents = new LinkedList<>();
            myConsequents = new LinkedList<>();
            convertSequentVC(sequent);

            // A bunch of lists, sets and maps used for post-processing a PExp
            myConditions = new HashSet<>();
            myLambdaCodes = new HashMap<>();
            myLambdaTag = 0;
            myLiftedLambdas = new HashMap<>();
            myLiftedLambdaPredicates = new ArrayList<>();
            myQVarTag = 0;
            myRhsOfLamPredsToLamPreds = new HashMap<>();

            // A bunch of conversions defined by Mike
            // No clue what any of it does... YS
            liftLambdas();
            convertPAlternativesToCF();
            replaceLambdaSymbols();
            normalizeConditions();
            uniquelyNameQuantifiers();
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * This method returns this object in string format.
         * </p>
         *
         * @return A string.
         */
        @Override
        public final String toString() {
            StringBuilder retval = new StringBuilder("========== " + myName + " ==========\n"
                    + convertExpressionToString(myAntecedents) + "  -->\n" + convertExpressionToString(myConsequents));

            if (!myLiftedLambdaPredicates.isEmpty()) {
                retval.append("lifted lambda predicates:\n");
                for (PExp p : myLiftedLambdaPredicates) {
                    retval.append(p.toString()).append("\n");
                }
            }

            return retval.toString();
        }

        // ===========================================================
        // Private Methods
        // ===========================================================

        /**
         * <p>
         * An helper method for converting {@link PAlternatives}.
         * </p>
         */
        private void convertPAlternativesToCF() {
            List<PSymbol> converted = new ArrayList<>();
            List<PExp> noQuantConverted = new ArrayList<>();
            for (PSymbol p : myLiftedLambdaPredicates) {
                if (p.arguments.size() == 2 && p.arguments.get(1) instanceof PAlternatives) {
                    // lhs can't be a PALT
                    PExp lhs = p.arguments.get(0);
                    PExp rhs = p.arguments.get(1);
                    List<PExp> args = new ArrayList<>();
                    PAlternatives asPa = (PAlternatives) rhs;
                    if (asPa.myAlternatives.size() > 1) {
                        throw new MiscErrorException("[Prover] Only 1 alternative supported", new RuntimeException());
                    }

                    PAlternatives.Alternative alt = asPa.myAlternatives.get(0);
                    PExp quantVar = lhs.getQuantifiedVariables().iterator().next();
                    PExp cond = alt.condition;
                    PExp conFunc = orderPlusOne(cond, quantVar, converted);
                    PExp posChoice = alt.result;
                    PExp posChoiceFun = orderPlusOne(posChoice, quantVar, converted);
                    PExp negChoice = asPa.myOtherwiseClauseResult;
                    PExp negChoiceFun = orderPlusOne(negChoice, quantVar, converted);
                    args.add(conFunc);
                    args.add(posChoiceFun);
                    args.add(negChoiceFun);
                    // remember to type this as Z->Entity
                    PSymbol cf = new PSymbol(posChoiceFun.getMathType(), null, "CF", args);
                    args.clear();
                    if (!myRhsOfLamPredsToLamPreds.containsKey(cf.toString())) {
                        PSymbol lhsPsym = new PSymbol(myTypeGraph.BOOLEAN, null, lhs.getTopLevelOperation(),
                                PSymbol.Quantification.NONE);
                        args.add(lhsPsym);
                        args.add(cf);

                        PSymbol cfPred = new PSymbol(myTypeGraph.BOOLEAN, null, "=", args);
                        if (cfPred.getQuantifiedVariables().size() > 0) {
                            converted.add(cfPred);
                        } else {
                            noQuantConverted.add(cfPred);
                        }
                    }

                } else {
                    converted.add(p);
                }
            }

            myLiftedLambdaPredicates = converted;
            myAntecedents.addAll(noQuantConverted);
        }

        // Assumes lambdas lifted first
        // Assumes all remaining PAlternatives are in m_liftedLambdaPredicates
        /*
         * public void convertPAlternativesToImplications() { ArrayList<PSymbol> converted = new ArrayList<PSymbol>();
         * for (PSymbol p : m_liftedLambdaPredicates) { if (p.arguments.size() == 2) { // lhs can't be a PALT PExp lhs =
         * p.arguments.get(0); PExp rhs = p.arguments.get(1); if (rhs instanceof PAlternatives) { PAlternatives asPa =
         * (PAlternatives) rhs; ArrayList<PExp> conditions = new ArrayList<PExp>(); for (PAlternatives.Alternative pa :
         * asPa.myAlternatives) { conditions.add(pa.condition); ArrayList<PExp> args = new ArrayList<PExp>();
         * args.add(lhs); args.add(pa.result); PSymbol ant = new PSymbol(m_typegraph.BOOLEAN, null, "=", args);
         * args.clear(); args.add(pa.condition); args.add(ant); PSymbol pc = new PSymbol(m_typegraph.BOOLEAN, null,
         * "implies", args); converted.add(pc); m_conditions.add(pa.condition); } // do otherwise clause if
         * (conditions.size() > 1) { // make conjunction } else { ArrayList<PExp> args = new ArrayList<PExp>();
         * args.add(conditions.get(0)); PExp neg = new PSymbol(m_typegraph.BOOLEAN, null, "not", args); args.clear();
         * args.add(lhs); args.add(asPa.myOtherwiseClauseResult); PExp eq = new PSymbol(m_typegraph.BOOLEAN, null, "=",
         * args); args.clear(); args.add(neg); args.add(eq); converted.add(new PSymbol(m_typegraph.BOOLEAN, null,
         * "implies", args)); //m_conditions.add(neg); } } // No PAlt else { converted.add(p); } } }
         * m_liftedLambdaPredicates = converted; }
         */

        /**
         * <p>
         * An helper method for processing the sequent in a {@code VC}.
         * </p>
         *
         * @param sequent
         *            Sequent VC.
         */
        private void convertSequentVC(Sequent sequent) {
            for (Exp exp1 : sequent.getAntecedents()) {
                myAntecedents.add(Utilities.replacePExp(PExp.buildPExp(myTypeGraph, exp1), myTypeGraph, Z, N));

            }

            for (Exp exp : sequent.getConcequents()) {
                myConsequents.add(Utilities.replacePExp(PExp.buildPExp(myTypeGraph, exp), myTypeGraph, Z, N));
            }
        }

        /**
         * <p>
         * An helper method for lifting lambda expressions.
         * </p>
         */
        private void liftLambdas() {
            // Convert the antecedents expressions
            List<PExp> newAntecedents = new ArrayList<>();
            for (PExp p : myAntecedents) {
                newAntecedents.add(recursiveLift(p));
            }
            myAntecedents = newAntecedents;

            // Convert the consequent expressions
            List<PExp> newConsequents = new ArrayList<>();
            for (PExp p : myConsequents) {
                newConsequents.add(recursiveLift(p));
            }
            myConsequents = newConsequents;

            // Some more lambda lifting logic defined by Mike.
            // No clue what any of this does... YS
            for (PLambda p : myLiftedLambdas.keySet()) {
                String name = myLiftedLambdas.get(p);
                PExp body = p.getBody();
                PSymbol lhs = new PSymbol(p.getMathType(), p.getMathTypeValue(), name, p.getParameters());

                List<PExp> args = new ArrayList<>();
                args.add(lhs);
                args.add(body);
                myLiftedLambdaPredicates.add(new PSymbol(myTypeGraph.BOOLEAN, null, "=", args));
            }
        }

        /**
         * <p>
         * An helper method for normalizing condition and ensures that they are unique.
         * </p>
         */
        private void normalizeConditions() {
            Set<PExp> replacement = new HashSet<>();
            Set<String> inSet = new HashSet<>();
            for (PExp p : myConditions) {
                // replace the qVars dup with normalized names
                Set<PSymbol> qVars = p.getQuantifiedVariables();
                Map<PExp, PExp> substMap = new HashMap<>();
                for (PSymbol pq : qVars) {
                    PSymbol repP = new PSymbol(pq.getMathType(), pq.getMathTypeValue(),
                            pq.getMathType().toString() + myQVarTag++, pq.quantification);
                    substMap.put(pq, repP);
                }

                if (!substMap.isEmpty()) {
                    p = p.substitute(substMap);
                }

                if (!inSet.contains(p.toString())) {
                    replacement.add(p);
                    inSet.add(p.toString());
                }

                myQVarTag = 0;
            }

            myConditions = replacement;
        }

        /**
         * <p>
         * An helper method for dealing with quantified symbols.
         * </p >
         *
         * @param thingToHigherOrder
         *            An expression that contains the quantified variable.
         * @param quantVar
         *            A quantified variable
         * @param sideList
         *            A list of prover symbols
         *
         * @return A new name for the quantified variable.
         */
        private PExp orderPlusOne(PExp thingToHigherOrder, PExp quantVar, List<PSymbol> sideList) {
            Set<PSymbol> qVarSet = thingToHigherOrder.getQuantifiedVariables();
            if (qVarSet.size() > 1) {
                throw new MiscErrorException("[Prover] Only 1 quantified var. supported", new RuntimeException());
            }

            // no need to always make a new function
            if (qVarSet.size() == 1 && qVarSet.contains(quantVar)
                    && thingToHigherOrder.getSubExpressions().size() == 1) {
                MTFunction hoType = new MTFunction(myTypeGraph, thingToHigherOrder.getMathType(),
                        quantVar.getMathType());

                return new PSymbol(hoType, null, thingToHigherOrder.getTopLevelOperation());
            }

            PSymbol funName;
            if (myRhsOfLamPredsToLamPreds.containsKey(thingToHigherOrder.toString())) {
                String fStr = myRhsOfLamPredsToLamPreds.get(thingToHigherOrder.toString()).getSubExpressions().get(0)
                        .getTopLevelOperation();

                return new PSymbol(
                        new MTFunction(myTypeGraph, thingToHigherOrder.getMathType(), quantVar.getMathType()), null,
                        fStr);
            } else {
                funName = new PSymbol(
                        new MTFunction(myTypeGraph, thingToHigherOrder.getMathType(), quantVar.getMathType()), null,
                        "lambda" + (myLambdaTag++));
                List<PExp> args = new ArrayList<>();
                args.add(quantVar);
                PSymbol funAppl = new PSymbol(thingToHigherOrder.getMathType(), null, funName.getTopLevelOperation(),
                        args, PSymbol.Quantification.NONE);
                args.clear();
                args.add(funAppl);
                args.add(thingToHigherOrder);
                PSymbol conQuant = new PSymbol(myTypeGraph.BOOLEAN, null, "=", args);
                sideList.add(conQuant);
                myRhsOfLamPredsToLamPreds.put(thingToHigherOrder.toString(), conQuant);
            }

            return funName;
        }

        /**
         * <p>
         * An helper method for printing list of antecedents or consequent expressions.
         * </p>
         *
         * @param pExps
         *            A list of {@link PExp PExps}.
         *
         * @return A string.
         */
        private String convertExpressionToString(List<PExp> pExps) {
            StringBuilder retval = new StringBuilder();

            boolean first = true;
            for (PExp e : pExps) {
                if (!first) {
                    retval.append(" and \n");
                }
                retval.append(e.toString());
                first = false;
            }

            if (retval.toString() == "") {
                retval = new StringBuilder("True");
            }

            retval.append("\n");

            return retval.toString();
        }

        /**
         * <p>
         * An helper method for recursively lift any lambda expressions.
         * </p>
         *
         * @param p
         *            A {@link PExp}
         *
         * @return A potentially modified {@link PExp}.
         */
        private PExp recursiveLift(PExp p) {
            List<PExp> newArgList = new ArrayList<>();
            for (PExp p_s : p.getSubExpressions()) {
                newArgList.add(recursiveLift(p_s));
            }

            if (p instanceof PLambda) {
                // replace lam(x).F(x) wth F
                PLambda pl = (PLambda) p;
                PExp body = pl.getBody();
                if ((pl.getParameters().size() == 1 && body.getQuantifiedVariables().size() == 1)
                        && pl.getParameters().get(0).toString()
                                .equals(body.getQuantifiedVariables().iterator().next().toString())
                        && body.getSubExpressions().size() == 1 && body.getSubExpressions().get(0).isVariable()) {

                    return new PSymbol(
                            new MTFunction(myTypeGraph, body.getMathType(), pl.getParameters().get(0).getMathType()),
                            null, body.getTopLevelOperation());
                }

                // Normalize parameters here
                String lname;
                PLambda normP = ((PLambda) p).withNormalizedParameterNames();
                String lambdaCode = normP.toString();
                if (!myLambdaCodes.containsKey(lambdaCode)) {
                    lname = "lambda" + myLambdaTag++;
                    myLambdaCodes.put(lambdaCode, normP);
                    myLiftedLambdas.put(normP, lname);
                } else {
                    PLambda foundLamb = myLambdaCodes.get(lambdaCode);
                    lname = myLiftedLambdas.get(foundLamb);
                }

                return new PSymbol(normP.getMathType(), normP.getMathTypeValue(), lname);

            }

            return new PSymbol(p.getMathType(), p.getMathTypeValue(), p.getTopLevelOperation(), newArgList);
        }

        /**
         * <p>
         * An helper method for replacing lambda symbols.
         * </p>
         */
        private void replaceLambdaSymbols() {
            Map<PExp, PExp> substMap = new HashMap<>();
            List<PExp> newConjuncts = new ArrayList<>();
            // build map
            for (PExp p : myAntecedents) {
                if (p.isEquality()) {
                    ImmutableList<PExp> args = p.getSubExpressions();
                    PExp args0 = args.get(0);
                    PExp args1 = args.get(1);
                    if (args0.isVariable() && args1.isVariable()) {
                        if (args0.getTopLevelOperation().contains("lambda")) {
                            substMap.put(args0, args1);
                        } else if (args1.getTopLevelOperation().contains("lambda")) {
                            substMap.put(args1, args0);
                        }
                    }
                }
            }

            if (!substMap.isEmpty()) {
                // Replace antecedent expressions.
                List<PExp> newAntecedents = new ArrayList<>();
                for (PExp exp : myAntecedents) {
                    newAntecedents.add(exp.substitute(substMap));

                }
                myAntecedents = newAntecedents;

                // Replace consequent expressions.
                List<PExp> newConsequents = new ArrayList<>();
                for (PExp exp : myConsequents) {
                    newConsequents.add(exp.substitute(substMap));

                }
                myConsequents = newConsequents;

                // Replace the lifted lambda predicates map
                List<PSymbol> n_Preds = new ArrayList<>();
                for (PSymbol p : myLiftedLambdaPredicates) {
                    n_Preds.add((PSymbol) p.substitute(substMap));
                }
                myLiftedLambdaPredicates = n_Preds;
            }
        }

        /**
         * <p>
         * An helper method for creating unique named quantifiers.
         * </p>
         */
        private void uniquelyNameQuantifiers() {
            Set<PExp> replacement = new HashSet<>();
            for (PExp p : myConditions) {
                // replace the qVars dup with unique names
                Set<PSymbol> qVars = p.getQuantifiedVariables();
                Map<PExp, PExp> substMap = new HashMap<>();
                for (PSymbol pq : qVars) {
                    PSymbol repP = new PSymbol(pq.getMathType(), pq.getMathTypeValue(),
                            "¢vl" + pq.getMathType().toString() + myQVarTag++, pq.quantification);
                    substMap.put(pq, repP);
                }

                if (!substMap.isEmpty()) {
                    p = p.substitute(substMap);
                }

                replacement.add(p);
            }

            myConditions = replacement;
        }
    }
}
