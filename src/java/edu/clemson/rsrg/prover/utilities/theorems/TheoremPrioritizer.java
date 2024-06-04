/*
 * TheoremPrioritizer.java
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
package edu.clemson.rsrg.prover.utilities.theorems;

import edu.clemson.rsrg.prover.utilities.ImmutableVC;
import edu.clemson.rsrg.prover.utilities.Registry;
import edu.clemson.rsrg.prover.utilities.expressions.NormalizedAtomicExpression;
import java.util.*;

/**
 * <p>
 * This class allows us to prioritize which <em>Theorems</em> to use in proving a {@code VC}.
 * </p>
 *
 * @author Mike Kabbani
 *
 * @version 2.0
 */
public class TheoremPrioritizer {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A map of non-quantified symbols
     * </p>
     */
    private final Map<String, Integer> myNonQuantifiedSymbolMap;

    /**
     * <p>
     * The current immutable VC.
     * </p>
     */
    private final ImmutableVC myVC;

    /**
     * <p>
     * A priority queue for selecting theorems.
     * </p>
     */
    private final PriorityQueue<TheoremWithScore> myTheoremPriorityQueue;

    /**
     * <p>
     * A symbol registry.
     * </p>
     */
    private final Registry myVCRegistry;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This class organizes the theorems into a particular priority.
     * </p>
     *
     * @param theoremList
     *            List of available theorems.
     * @param theoremAppliedCountMap
     *            A map of how many times theorems were selected.
     * @param vc
     *            The current vc we are processing.
     * @param nonQuantifiedTheoremSymbols
     *            A set of non-quantified theorem symbols.
     * @param smallEndEquations
     *            A set of small equation theorems.
     */
    public TheoremPrioritizer(List<Theorem> theoremList, Map<String, Integer> theoremAppliedCountMap, ImmutableVC vc,
            Set<String> nonQuantifiedTheoremSymbols, Set<Theorem> smallEndEquations) {
        myTheoremPriorityQueue = new PriorityQueue<>(theoremList.size());
        myVCRegistry = vc.getRegistry();
        myVC = vc;
        myNonQuantifiedSymbolMap = new HashMap<>();
        int count = 0;
        for (String s : myVCRegistry.myIndexToSymbol) {
            if (nonQuantifiedTheoremSymbols.contains(s)) {
                myNonQuantifiedSymbolMap.put(s, count++);
            }
        }

        for (Theorem t : theoremList) {
            // Create a theorem with a score if it does not
            // exclude the non quantified symbols.
            TheoremWithScore tws = new TheoremWithScore(t);
            int score;
            if (!shouldExclude(t.getNonQuantifiedSymbols())) {
                score = calculateScoreMinimum(t.getNonQuantifiedSymbols(),
                        myVCRegistry.mySymbolToIndex.keySet().size());
                if (theoremAppliedCountMap.containsKey(t.getName())) {
                    score += theoremAppliedCountMap.get(t.getName());
                }
                if (smallEndEquations.contains(t)) {
                    score += 1;
                }

                // Update the theorem score.
                tws.updateTheoremScore(score);

                // Update the priority queue with this new theorem
                myTheoremPriorityQueue.add(tws);
            }
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns a new theorem if there are more that can be considered.
     * </p>
     *
     * @return A {@link Theorem} object.
     */
    public final Theorem poll() {
        return Objects.requireNonNull(myTheoremPriorityQueue.poll()).getTheorem();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method that computes a minimum score based on the theorem symbols.
     * </p>
     *
     * @param theorem_symbols
     *            Set of symbols in the theorem.
     * @param not_contained_penalty
     *            A penalty score (if any).
     *
     * @return A score.
     */
    // minimum of symbol scores in both vc and theorem
    private int calculateScoreMinimum(Set<String> theorem_symbols, int not_contained_penalty) {
        if (theorem_symbols.isEmpty()) {
            return 0;
        }

        int score = not_contained_penalty;
        int number_not_contained = 1;
        for (String s : theorem_symbols) {
            if (myNonQuantifiedSymbolMap.containsKey(s)) {
                int c_score = goalArg(s);
                if (c_score < 0) {
                    c_score = myNonQuantifiedSymbolMap.get(s);
                }
                if (c_score < score) {
                    score = c_score;
                }
            } else {
                number_not_contained++;
            }
        }
        return (score + 1) * number_not_contained;
    }

    /**
     * <p>
     * An helper method that returns a value based on whether or not if we found the symbol to be {@code true}.
     * </p>
     *
     * @param s
     *            A string to be searched.
     *
     * @return 0 if it is an exact match, 1 if we found an equivalence relationship with another symbol, -1 otherwise.
     */
    private int goalArg(String s) {
        int si = myVCRegistry.getIndexForSymbol(s);
        String sc = myVCRegistry.getRootSymbolForSymbol(s);
        for (String g : myVC.VCGoalStrings) {
            if (g.equals("false")) {
                continue;
            }

            int gi = myVCRegistry.getIndexForSymbol(g);
            if (si == gi) {
                return 0;
            }

            for (NormalizedAtomicExpression ng : myVC.getConjunct().getUses(gi)) {
                if (ng.readRoot() != gi) {
                    continue;
                }

                if (ng.getOperatorsAsStrings(true).containsKey(sc)) {
                    return 1;
                }
            }
        }

        return -1;
    }

    /**
     * <p>
     * An helper method for determining if our registry contains any of the symbols required by the VC.
     * </p>
     *
     * @param vcMustContainThese
     *            A set of symbols.
     *
     * @return {@code true} if our registry contains one of the symbols, {@code false} otherwise.
     */
    private boolean shouldExclude(Set<String> vcMustContainThese) {
        for (String s : vcMustContainThese) {
            String c = myVCRegistry.getRootSymbolForSymbol(s);
            if (!myVCRegistry.mySymbolToIndex.containsKey(c)) {
                return true;
            }
        }

        return false;
    }

}
