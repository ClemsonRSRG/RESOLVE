/*
 * TheoremPrioritizer.java
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
package edu.clemson.cs.rsrg.prover.utilities.theorems;

import edu.clemson.cs.rsrg.prover.utilities.ImmutableVC;
import edu.clemson.cs.rsrg.prover.utilities.Registry;
import edu.clemson.cs.rsrg.prover.utilities.expressions.NormalizedAtomicExpression;
import java.util.*;

/**
 * <p>This class allows us to prioritize which <em>Theorems</em> to use in
 * proving a {@code VC}.</p>
 *
 * @author Mike Kabbani
 * @version 2.0
 */
public class TheoremPrioritizer {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final Map<String, Integer> myNonQuantifiedSymbolMap;
    private final ImmutableVC myVC;
    private final PriorityQueue<TheoremWithScore> myTheoremPriorityQueue;
    private final Registry myVCRegistry;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TheoremPrioritizer(List<Theorem> theoremList, Map<String, Integer> theoremAppliedCountMap,
            ImmutableVC vc, Set<String> nonQuantifiedTheoremSymbols, Set<Theorem> smallEndEquations) {
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
                score =
                        calculateScoreMinimum(t.getNonQuantifiedSymbols(),
                                myVCRegistry.mySymbolToIndex.keySet().size());
                if (theoremAppliedCountMap.containsKey(t.m_name)) {
                    score += theoremAppliedCountMap.get(t.m_name);
                }
                if (smallEndEquations.contains(t)) {
                    score += 1;
                }

                // Update the theorem score.
                tws.updateTheoremScore(score);

                myTheoremPriorityQueue.add(tws);
            }
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Theorem poll() {
        return Objects.requireNonNull(myTheoremPriorityQueue.poll())
                .getTheorem();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    //  minimum of symbol scores in both vc and theorem
    private int calculateScoreMinimum(Set<String> theorem_symbols,
            int not_contained_penalty) {
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
            }
            else {
                number_not_contained++;
            }
        }
        return (score + 1) * number_not_contained;
    }

    private int goalArg(String s) {
        int si = myVCRegistry.getIndexForSymbol(s);
        String sc = myVCRegistry.getRootSymbolForSymbol(s);
        for (String g : myVC.m_goal) {
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