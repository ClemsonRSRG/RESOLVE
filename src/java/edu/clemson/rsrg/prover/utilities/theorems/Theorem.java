/*
 * Theorem.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.utilities.theorems;

import edu.clemson.rsrg.prover.absyn.PExp;
import edu.clemson.rsrg.prover.absyn.expressions.PSymbol;
import edu.clemson.rsrg.prover.utilities.ImmutableVC;
import edu.clemson.rsrg.prover.utilities.PExpWithScore;
import edu.clemson.rsrg.prover.utilities.Registry;
import edu.clemson.rsrg.prover.utilities.expressions.ConjunctionOfNormalizedAtomicExpressions;
import edu.clemson.rsrg.prover.utilities.expressions.NormalizedAtomicExpression;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.*;

/**
 * <p>
 * This class represents an immutable <em>theorem</em>.
 * </p>
 *
 * @author Mike Kabbani
 * 
 * @version 2.0
 */
public class Theorem {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Theorem's name
     * </p>
     */
    private final String myName;

    /**
     * <p>
     * A flag that indicates if we are checking for equality.
     * </p>
     */
    private final boolean myIsEqualityFlag;

    /**
     * <p>
     * A registry for theorem expressions.
     * </p>
     */
    private final Registry myTheoremRegistry;

    /**
     * <p>
     * A conjunction of matched expressions.
     * </p>
     */
    private final ConjunctionOfNormalizedAtomicExpressions myMatchedConjExps;

    /**
     * <p>
     * A list of expressions that needs to be matched.
     * </p>
     */
    private final List<NormalizedAtomicExpression> myMatchRequiredExps;

    /**
     * <p>
     * A list of expressions that do not match.
     * </p>
     */
    private final List<NormalizedAtomicExpression> myNoMatchRequiredExps;

    /**
     * <p>
     * Theorem represented as a string.
     * </p>
     */
    private final String myTheoremAsString;

    /**
     * <p>
     * Expression to be inserted into our registry.
     * </p>
     */
    private final PExp myInsertExpr;

    /**
     * <p>
     * Theorem expressed as a {@link PExp}.
     * </p>
     */
    private final PExp myTheoremExp;

    /**
     * <p>
     * A set of literal symbols from this theorem.
     * </p>
     */
    private Set<String> myAllLiteralsStrs;

    /**
     * <p>
     * A flag that indicates if we allow new symbols to be applied.
     * </p>
     */
    private boolean myAllowedNewSymbols;

    /**
     * <p>
     * A flag that indicates whether or not this theorem has quantifiers.
     * </p>
     */
    private final boolean myHasNoQuantifiersFlag;

    /**
     * <p>
     * The last processed {@code VC}.
     * </p>
     */
    private ImmutableVC myLastProcessedVC;

    /**
     * <p>
     * A set of quantified variables to be inserted.
     * </p>
     */
    private final Set<String> myInsertQuantifiedVars;

    // clear these when starting new VC
    /**
     * <p>
     * A collection of bounded expressions.
     * </p>
     */
    private final List<Map<String, String>> myBindings;

    /**
     * <p>
     * A collection of selected bounded expressions.
     * </p>
     */
    private final Set<Map<String, String>> mySelectedBindings;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an immutable <em>theorem</em> for the prover.
     * </p>
     *
     * @param g
     *            The mathematical type graph.
     * @param entireTheorem
     *            Complete theorem as a {@link PExp}.
     * @param mustMatch
     *            An expression that must be matched.
     * @param restOfExp
     *            An expression that don't necessary have to match.
     * @param toInsert
     *            An expression to be inserted.
     * @param enterToMatchAndBindAsEquivalentToTrue
     *            A flag that indicates that if found a match, we bind this to be {@code true}.
     * @param allowNewSymbols
     *            A flag that indicates if we allow new symbols to be evaluated.
     * @param name
     *            This theorem's name.
     */
    public Theorem(TypeGraph g, PExp entireTheorem, PExp mustMatch, PExp restOfExp, PExp toInsert,
            boolean enterToMatchAndBindAsEquivalentToTrue, boolean allowNewSymbols, String name) {
        myName = name;
        myAllowedNewSymbols = allowNewSymbols;
        myTheoremExp = entireTheorem;
        myTheoremAsString = entireTheorem.toString();
        myIsEqualityFlag = true;
        myTheoremRegistry = new Registry(g);
        myBindings = new ArrayList<>(128);
        mySelectedBindings = new HashSet<>(128);
        myMatchedConjExps = new ConjunctionOfNormalizedAtomicExpressions(null, myTheoremRegistry);
        if (mustMatch.getSubExpressions().size() > 0) {
            if (enterToMatchAndBindAsEquivalentToTrue) {
                myMatchedConjExps.addExpression(mustMatch);
            } else {
                myMatchedConjExps.addFormula(mustMatch);
            }
        }

        myMatchRequiredExps = new ArrayList<>(myMatchedConjExps.getNormalizedAtomicExpressionKeys());
        Collections.sort(myMatchRequiredExps, new NormalizedAtomicExpression.numQuantsComparator());

        myInsertExpr = toInsert;
        myInsertQuantifiedVars = new HashSet<>();
        for (PSymbol p : myInsertExpr.getQuantifiedVariables()) {
            myInsertQuantifiedVars.add(p.toString());
        }

        if (!mustMatch.equals(restOfExp) && restOfExp.getSubExpressions().size() > 1
                && (!mustMatch.getQuantifiedVariables().containsAll(restOfExp.getQuantifiedVariables())
                        || mustMatch.getSubExpressions().size() == 0)) {
            myMatchedConjExps.addFormula(restOfExp);
        }

        myNoMatchRequiredExps = new ArrayList<>();
        Set<String> insert_quants = new HashSet<>();
        for (PSymbol p : toInsert.getQuantifiedVariables()) {
            insert_quants.add(p.toString());
        }

        for (NormalizedAtomicExpression n : myMatchedConjExps.getNormalizedAtomicExpressionKeys()) {
            Map<String, Integer> ops = n.getOperatorsAsStrings(false);
            Set<String> intersection = new HashSet<>(insert_quants);
            intersection.retainAll(ops.keySet());
            if (!myMatchRequiredExps.contains(n) && !ops.containsKey("_g") && !intersection.isEmpty()) {
                myNoMatchRequiredExps.add(n);
            }
        }

        Collections.sort(myNoMatchRequiredExps, new NormalizedAtomicExpression.numQuantsComparator());

        myHasNoQuantifiersFlag = myTheoremExp.getQuantifiedVariables().isEmpty();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method applies this <em>theorem</em> to a {@code VC}.
     * </p>
     *
     * @param vc
     *            A {@code VC}.
     * @param endTime
     *            An end time.
     *
     * @return An integer indicating how applicable is this <em>theorem</em>.
     */
    public final int applyTo(ImmutableVC vc, long endTime) {
        Set<Map<String, String>> sResults;
        myBindings.clear();
        if (myLastProcessedVC == null || !myLastProcessedVC.equals(vc)) {
            mySelectedBindings.clear();
            myLastProcessedVC = vc;
        }

        if (myHasNoQuantifiersFlag) {
            return 1;
        }

        if (myMatchRequiredExps.size() == 0
                || ((myAllowedNewSymbols && myTheoremExp.getQuantifiedVariables().size() == 1) && myIsEqualityFlag)) {
            sResults = findValidBindingsByType(vc);
        } else {
            sResults = findValidBindings(vc);
        }

        if (sResults == null || sResults.isEmpty()) {
            return 0;
        }

        nextMap: for (Map<String, String> s : sResults) {
            if (!mySelectedBindings.contains(s)) {
                for (String k : s.keySet()) {
                    String v = s.get(k);
                    if (!myInsertQuantifiedVars.contains(v)) {
                        continue;
                    }
                    if (s.get(k).equals(""))
                        continue nextMap;
                }
                myBindings.add(s);
            }
        }

        Collections.sort(myBindings, new Comparator<Map<String, String>>() {

            /**
             * <p>
             * Compares <code>o1</code> and <code>o2</code>.
             * </p>
             *
             * @param o1
             *            A map of symbols.
             * @param o2
             *            Another map of symbols.
             *
             * @return Comparison results expressed as an integer.
             */
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return calculateScore(o1) - calculateScore(o2);
            }
        });

        return myBindings.size();
    }

    /**
     * <p>
     * This method returns this <em>theorem</em>'s name.
     * </p>
     *
     * @return A string.
     */
    public final String getName() {
        return myName;
    }

    /**
     * <p>
     * This method returns the "next" expression with a score.
     * </p>
     *
     * @return A prover expression with a score.
     */
    public final PExpWithScore getNext() {
        if (myHasNoQuantifiersFlag && mySelectedBindings.isEmpty()) {
            mySelectedBindings.add(new HashMap<String, String>());

            return new PExpWithScore(myInsertExpr, new HashMap<String, String>(), myTheoremExp.toString());
        }

        if (myBindings.isEmpty()) {
            return null;
        }

        Map<PExp, PExp> quantToLit = new HashMap<>();
        Map<String, String> curBinding = myBindings.remove(0);
        mySelectedBindings.add(curBinding);
        for (PSymbol p : myInsertExpr.getQuantifiedVariables()) {
            String thKey = p.getTopLevelOperation();
            String thVal = "";
            if (curBinding.containsKey(thKey)) {
                thVal = curBinding.get(thKey);
            } else if (curBinding.containsKey(myTheoremRegistry.getRootSymbolForSymbol(thKey))) {
                thVal = curBinding.get(myTheoremRegistry.getRootSymbolForSymbol(thKey));
            }

            if (thVal.equals("")) {
                return getNext();
            }

            MTType quanType = myTheoremRegistry.getTypeByIndex(myTheoremRegistry.getIndexForSymbol(thKey));
            quantToLit.put(new PSymbol(quanType, null, thKey, PSymbol.Quantification.FOR_ALL),
                    new PSymbol(quanType, null, thVal, PSymbol.Quantification.NONE));
        }

        PExp modifiedInsert = myInsertExpr.substitute(quantToLit);
        modifiedInsert = myLastProcessedVC.getConjunct().find(modifiedInsert);
        // Discard s = s
        if ((modifiedInsert.getTopLevelOperation().equals("=") && modifiedInsert.getSubExpressions().get(0).toString()
                .equals(modifiedInsert.getSubExpressions().get(1).toString()))) {
            return getNext();
        }

        return new PExpWithScore(modifiedInsert, curBinding, myTheoremExp.toString());
    }

    /**
     * <p>
     * This method returns all non-quantified symbols.
     * </p>
     *
     * @return A set of symbols.
     */
    public final Set<String> getNonQuantifiedSymbols() {
        if (myAllLiteralsStrs == null) {
            myAllLiteralsStrs = ((PSymbol) myTheoremExp).getNonQuantifiedSymbols();

            myAllLiteralsStrs.remove("=B");
            myAllLiteralsStrs.remove("andB");
            myAllLiteralsStrs.remove("impliesB");
            myAllLiteralsStrs.remove("true");
            myAllLiteralsStrs.remove("false");
            myAllLiteralsStrs.remove("/=B");
            myAllLiteralsStrs.remove("Empty_String");
            myAllLiteralsStrs.remove("0");
            myAllLiteralsStrs.remove("1");
            myAllLiteralsStrs.remove("2");
            myAllLiteralsStrs.remove("3");
            myAllLiteralsStrs.remove("4");
            myAllLiteralsStrs.remove("5");
            myAllLiteralsStrs.remove("6");
            myAllLiteralsStrs.remove("7");
            myAllLiteralsStrs.remove("8");
            myAllLiteralsStrs.remove("9");
            myAllLiteralsStrs.remove("orB");
            myAllLiteralsStrs.remove("+Z");
            myAllLiteralsStrs.remove("+N");
        }

        return myAllLiteralsStrs;
    }

    /**
     * <p>
     * This method checks to see if this <em>theorem</em> contains quantifiers or not.
     * </p>
     *
     * @return {@code true} if it does, {@code false} otherwise.
     */
    public final boolean hasNoQuantifiers() {
        return myHasNoQuantifiersFlag;
    }

    /**
     * <p>
     * This method returns this <em>theorem</em> in string format.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String toString() {
        return "\n--------------------------------------\n" + myTheoremAsString + "\nif found\n" + myMatchedConjExps
                + "\ninsert\n" + myInsertExpr + "\n--------------------------------------\n";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method that computes a score for this <em>theorem</em>.
     * </p>
     *
     * @param bmap
     *            A map of symbols to be matched.
     *
     * @return A score.
     */
    private int calculateScore(Map<String, String> bmap) {
        Set<String> seen = new HashSet<>(bmap.keySet().size());
        float max = myLastProcessedVC.getRegistry().myIndexToSymbol.size();
        float age = 0f;
        float sSz = bmap.keySet().size();
        for (String k : bmap.keySet()) {
            String rS = myLastProcessedVC.getRegistry().getRootSymbolForSymbol(bmap.get(k));
            seen.add(rS);
            if (myLastProcessedVC.getRegistry().mySymbolToIndex.containsKey(rS)) {
                int indexVal = myLastProcessedVC.getRegistry().getIndexForSymbol(rS);
                // Age
                age += indexVal;
            }
        }

        float diff = 1.0f - seen.size() / sSz;
        float avgAge = age / sSz;
        // these range from [0,1], lower is better
        float scaledAvgAge = avgAge / max;

        scaledAvgAge += .01;
        diff += .01;

        return (int) ((80f * scaledAvgAge) + (20f * diff));
    }

    /**
     * <p>
     * An helper method that finds the collection of valid bindings that can be used to prove this {@code VC}.
     * </p>
     *
     * @param vc
     *            A {@code VC}.
     *
     * @return A collection of bounded expressions.
     */
    private Set<Map<String, String>> findValidBindings(ImmutableVC vc) {
        Set<Map<String, String>> results = new HashSet<>();
        Map<String, String> initBindings = getInitBindings();
        if (myTheoremRegistry.mySymbolToIndex.containsKey("_g")) {
            // each goal gets a new map with _g bound to the goal
            for (String g : vc.VCGoalStrings) {
                Map<String, String> gBinds = new HashMap<>(initBindings);
                gBinds.put("_g", g);
                results.add(gBinds);
            }
        } else {
            results.add(initBindings);
        }

        for (NormalizedAtomicExpression e_t : myMatchRequiredExps) {
            results = vc.getConjunct().getMatchesForOverrideSet(e_t, results);
        }

        Set<Map<String, String>> t_results;
        for (NormalizedAtomicExpression e_t : myNoMatchRequiredExps) {
            t_results = vc.getConjunct().getMatchesForOverrideSet(e_t, results);
            if (!t_results.isEmpty()) {
                results.addAll(t_results);
            }
        }

        return results;
    }

    /**
     * <p>
     * An helper method that finds the set of all bounded variables that matches a mathematical type used by one of the
     * {@code VC}'s symbols.
     * </p>
     *
     * @param vc
     *            A {@code VC}.
     *
     * @return A collection of bounded expressions by type.
     */
    private Set<Map<String, String>> findValidBindingsByType(ImmutableVC vc) {
        // Case where no match conj. is produced.
        // Example: S = Empty_String. Relevant info is only in registry.
        Set<Map<String, String>> allValidBindings = new HashSet<>();
        // x = constant?
        boolean partMatchedisConstantEquation = false;
        if (partMatchedisConstantEquation) {
            Map<String, String> wildToActual = new HashMap<>();
            for (String wild : myTheoremRegistry.getForAlls()) {
                String actual = myTheoremRegistry.getRootSymbolForSymbol(wild);
                // wildcard is not parent
                if (!actual.equals(wild)) {
                    wildToActual.put(wild, actual);
                }
                // wildcard is parent, bind to child
                else {
                    Set<String> ch = myTheoremRegistry.getChildren(wild);
                    // choose first non quantified symbol (they are all equal)
                    if (ch.isEmpty())
                        return null;
                    for (String c : ch) {
                        if (!myTheoremRegistry.getUsage(c).equals(Registry.Usage.FORALL)
                                || !myTheoremRegistry.getUsage(c).equals(Registry.Usage.CREATED)) {
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
        Set<String> foralls = myTheoremRegistry.getForAlls();
        if (foralls.size() != 1) {
            return null;
        }

        String wild = foralls.iterator().next();
        MTType t = myTheoremRegistry.getTypeByIndex(myTheoremRegistry.getIndexForSymbol(wild));

        for (String actual : vc.getRegistry().getParentsByType(t)) {
            Map<String, String> wildToActual = new HashMap<>();
            wildToActual.put(wild, actual);
            if (!wild.equals(actual)) { // can be = with constants in theorems
                allValidBindings.add(wildToActual);
            }
        }

        return allValidBindings;
    }

    /**
     * <p>
     * An helper method that obtains the initial bindings which includes any quantified variables and the created
     * variables in the matched conjunction.
     * </p>
     *
     * @return A mapping of bounded expressions.
     */
    private Map<String, String> getInitBindings() {
        Map<String, String> initBindings = new HashMap<>();
        // Created vars. that are parents of quantified vars can be a problem later
        for (int i = 0; i < myTheoremRegistry.myIndexToSymbol.size(); ++i) {
            String curSym = myTheoremRegistry.getSymbolForIndex(i);
            Registry.Usage us = myTheoremRegistry.getUsage(curSym);
            if (us == Registry.Usage.CREATED || us == Registry.Usage.FORALL || us == Registry.Usage.HASARGS_FORALL) {
                initBindings.put(curSym, "");
            }
        }

        return initBindings;
    }
}
