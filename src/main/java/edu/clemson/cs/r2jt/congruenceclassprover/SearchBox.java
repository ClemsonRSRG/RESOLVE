/**
 * SearchBox.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author mike
 */
public class SearchBox {

    protected int lowerBound;
    protected int upperBound;
    protected int currentIndex;
    protected NormalizedAtomicExpressionMapImpl m_translated;
    protected final NormalizedAtomicExpressionMapImpl m_original;
    protected Registry m_origRegistry;
    protected Registry m_destRegistry;
    protected HashMap<String, String> m_bindings; // Wildcard to actual. meant to be only for foralls
    protected final HashMap<String, String> m_bindingsInitial; // bindings sent to constructor
    protected HashMap<String, String> m_failedBindings; // for troubleshooting
    // set m_bindings to this before nextMatch
    protected final ConjunctionOfNormalizedAtomicExpressions m_dataSet;
    public boolean directMatch = false;
    public boolean impossibleToMatch = false; //
    protected final List<String> m_origAsStrArray;
    public final int m_indexInList;
    public int m_lastGoodMatchIndex;

    public SearchBox(NormalizedAtomicExpressionMapImpl query,
                     Registry queryReg,
                     ConjunctionOfNormalizedAtomicExpressions dataSet, Registry dataReg,
                     HashMap<String, String> bindings, int indexInList) {
        m_original = query; // this is the search expr directly from the theorem
        m_origRegistry = queryReg;
        m_dataSet = dataSet;
        m_destRegistry = dataReg;
        m_origAsStrArray = NAEtoList(query, queryReg);
        m_lastGoodMatchIndex = currentIndex;
        m_bindings = bindings; // created from previous search and mathces
        m_bindingsInitial = bindings;
        m_indexInList = indexInList;

        // THIS IS NOT UPDATED, BUT IS ONLY USED AT CONSTRUCTION. Only for the find.
        m_translated =
                m_original.translateFromRegParam1ToRegParam2(m_origRegistry,
                        m_destRegistry, m_bindings);
        // do search with empty bindings; literals (func names, 0,1 etc. will be in query)
        dataSet.findNAE(this); // lb = currindex. does find on translated, sets bounds.
        // can make this function local
    }

    public void doSearch() {
        m_translated =
                m_original.translateFromRegParam1ToRegParam2(m_origRegistry,
                        m_destRegistry, m_bindings);
        m_dataSet.findNAE(this);
    }

    public static List<String> NAEtoList(
            NormalizedAtomicExpressionMapImpl atom, Registry atomReg) {
        ArrayList<String> atomAsStrArray = new ArrayList<String>();
        int op = atom.readPosition(0);
        int i = 1;
        while (op >= 0) {
            atomAsStrArray.add(atomReg.getSymbolForIndex(op));
            op = atom.readPosition(i++);
        }
        int root = atom.readRoot();
        if (root >= 0) {
            atomAsStrArray.add(atomReg.getSymbolForIndex(root));
        }
        return atomAsStrArray;
    }

    public boolean getNextMatch() {

        while (inBounds()) {
            if (compareAndBind()) {
                return true;
            }
            currentIndex++;
        }
        impossibleToMatch = true;
        return false;
    }

    // pre: bounds are set. index is in bounds.
    // post returns false or sets bindings 
    public boolean compareAndBind() {
        NormalizedAtomicExpressionMapImpl candidate =
                m_dataSet.getExprAtPosition(currentIndex);

        List<String> candStrArray = NAEtoList(candidate, m_destRegistry);
        if (candStrArray.size() != m_origAsStrArray.size()) {
            return false;
        }
        // this loop writes to m_bindings. Must revert on fail or do collision check first.
        // this method is not only called once, it is called until upperbound is exceeded.
        HashMap<String, String> tempMap =
                new HashMap<String, String>(m_bindings);
        for (int i = 0; i < m_origAsStrArray.size(); ++i) {
            String origOp = m_origAsStrArray.get(i);
            String boundOp = candStrArray.get(i);
            assert boundOp.length() != 0 : "error in SearchBox";
            String origValForComp;
            // if bound op is mapped, redine it to actual
            // an unmapped forall should have val "", and should not equal the actual

            /* false when: 
             search expr at a pos i contains forall
             and forall is mapped to an actual, and actual != symbol at i in vc expr
             search expr contains literal, and literal != symbol at i in vc expr

             true when:
             search expr at pos i contains forall, and forall is unmapped (now map it)
             mapped forall value agrees with symbol in vc expr.
             unmapped value (literal) agrees with symbol in vc expr.
             */
            if (tempMap.containsKey(origOp)) { // only unwritten wildcards
                if (tempMap.get(origOp).equals("")) {
                    tempMap.put(origOp, boundOp);
                    continue;

                } else {
                    origValForComp = tempMap.get(origOp);
                }
            } else {
                origValForComp = m_origAsStrArray.get(i);
            }
            if (!origValForComp.equals(boundOp)) { // not a wildcard, if not the same, ret false
                m_failedBindings = tempMap;
                return false; // problem, need to roll back m_bindings
            }
        }

        m_bindings = tempMap;
        m_lastGoodMatchIndex = currentIndex;
        return true;

    }

    public boolean inBounds() {
        if (currentIndex >= lowerBound && currentIndex <= upperBound) {
            return true;
        }
        return false;

    }

    @Override
    public String toString() {
        String rString = "";
        rString += m_origAsStrArray;
        rString += m_bindings + "\n";
        if (m_failedBindings != null) {
            rString += "\n\tfailedBindings: " + m_failedBindings;
        }
        return rString;
    }
}
