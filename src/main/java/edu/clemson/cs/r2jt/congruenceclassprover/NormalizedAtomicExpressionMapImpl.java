/**
 * NormalizedAtomicExpressionMapImpl.java
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
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// todo: create array impl and compare.

/**
 * Created by mike on 4/4/2014.
 */
public class NormalizedAtomicExpressionMapImpl
        implements
            Comparable<NormalizedAtomicExpressionMapImpl> {

    private static final int m_maxPositions = 6;
    private Map<Integer, Integer> m_expression;

    public NormalizedAtomicExpressionMapImpl() {
        m_expression = new TreeMap<Integer, Integer>();
    }

    /**
     *
     * @param operator
     * @return bit pattern of positions. 0 indicates the operator is not used
     */
    public int readOperator(int operator) {
        if (m_expression.containsKey(operator))
            return m_expression.get(operator);
        return 0;
    }

    protected Set<Integer> getKeys(){
        return m_expression.keySet();
    }
    /**
     * 
     * @param position
     * @return integer representation of operator at position or -1 if none
     */
    public int readPosition(int position) {
        if(position >= m_maxPositions) return -1; // needed for construction of str arrays
        position = 1 << position;
        Set<Map.Entry<Integer, Integer>> entries = m_expression.entrySet();
        for (Map.Entry<Integer, Integer> e : entries) {
            if ((e.getValue() & position) != 0)
                return e.getKey();
            /* todo: possible use for map: multiple operators in a single position.
             for use in binding quantified variables.
             */
        }
        return -1;
    }

    /**
     * @param operator integer value of operator
     * @param position 0 denotes first position.
     * @return post operation value of position;
     */
    public void writeOnto(int operator, int position) {
        int porig = position;
        position = 1 << position;
        Integer curValue = m_expression.get(operator);
        if (curValue != null) {
            position = position | curValue;
        }
        m_expression.put(operator, position);
        return;
    }

    public boolean replaceOperator(int orig, int repl) {
        int origPositions = readOperator(orig);
        if (origPositions > 0) {
            m_expression.remove(orig);
            m_expression.put(repl, origPositions);
            return true;
        }
        return false;
    }

    /**
     *
     * @param root
     */
    protected void writeToRoot(int root) {
        int position = 1 << m_maxPositions;
        m_expression.put(root, position);
    }

    protected int readRoot() {
        int position = 1 << m_maxPositions;
        Set<Map.Entry<Integer, Integer>> entries = m_expression.entrySet();
        for (Map.Entry<Integer, Integer> e : entries) {
            if ((e.getValue() & position) != 0)
                return e.getKey();
            /* todo: possible use for map: multiple operators in a single position.
             for use in binding quantified variables.
             */
        }
        return -1;
    }

    // compare left sides of 2 expressions.  If this returns 0, you must compare right hand sides afterwards.
    @Override
    public int compareTo(NormalizedAtomicExpressionMapImpl o) {
        for (int i = 0; i < m_maxPositions; ++i) {
            int cmp = readPosition(i) - o.readPosition(i);
            if (cmp != 0)
                return cmp;
        }
        return 0;
    }

    public NormalizedAtomicExpressionMapImpl clear(){
        m_expression.clear();
        return this;
    }
    
    public int numOperators(){
        return m_expression.keySet().size();
    }
    public NormalizedAtomicExpressionMapImpl translateFromRegParam1ToRegParam2(Registry source,
            Registry destination, HashMap<String,String> mapping){
        // delete after working:
        String sourceStr = toHumanReadableString(source);
        
        NormalizedAtomicExpressionMapImpl translated = new NormalizedAtomicExpressionMapImpl();
        Set<Integer> keys = m_expression.keySet();
        for(Integer k : keys){
            String sourceName = source.getSymbolForIndex(k);
            String destName = "";
            switch (source.getUsage(sourceName)){
                case LITERAL:
                    if(destination.isSymbolInTable(sourceName))
                        destName = sourceName;
                    else return translated.clear();
                    break;
                case FORALL:
                    if(mapping.containsKey(sourceName))
                        destName = mapping.get(sourceName);
                    break;
            }
            if(!destName.equals("")){
                int trKey = destination.getIndexForSymbol(destName);
                int positions = m_expression.get(k);
                translated.m_expression.put(trKey, positions);
            }
           
        }
        String dest = translated.toHumanReadableString(destination);
        return translated;
    }
    

    // todo: consider using SearchBox.NAEtoStr function for this
    public boolean noConflicts(NormalizedAtomicExpressionMapImpl expr){
        // return false if an op is defined here and in expr, and expr uses it in a position not used here.
        Set<Integer> keys = m_expression.keySet();
        for(Integer k : keys){
            int theirPos = expr.readOperator(k); // expr has key if pos >= 0
            int myPos = readOperator(k); 
            assert myPos >= 0 : "Shouldn't be an unused key in NAEMI";
            if(theirPos < 0) continue;
            if((myPos & theirPos) == 0) return false;
        }
        return true;
    }
    public NormalizedAtomicExpressionMapImpl incrementLastKnown(){
        NormalizedAtomicExpressionMapImpl incremented = new NormalizedAtomicExpressionMapImpl();
        int pos = 0;
        int op = readPosition(pos);
        if(op < 0){ // function operator unknown, upper bound is end of list
            incremented.writeOnto(Integer.MAX_VALUE,0);
            incremented.writeToRoot(Integer.MAX_VALUE);
            return incremented;
        }
        int opPrev = op;
        while(op >= 0){
            incremented.writeOnto(opPrev, pos);
            pos++;
            opPrev = op;
            op = readPosition(pos);
        }
        incremented.writeOnto(++opPrev, pos);
        return incremented;
    }
    public String toHumanReadableString(Registry registry) {
        if(m_expression.isEmpty()) return "empty expression";
        String r = "";
        int cur;
        int i = 0;
        while (((cur = readPosition(i)) >= 0) && i < m_maxPositions) {
            if (i == 1)
                r += "(";
            r += registry.getSymbolForIndex(cur);
            if (i != 0)
                r += ",";
            i++;
        }
        // if there is an arg list
        if(r.length() > 1){
            r = r.substring(0, r.length() - 1);
            r += ")" ;
        }
        // if there is a root
        int root = readRoot();
        if(root >= 0)
            r += "=" + registry.getSymbolForIndex(root);

        return r;
    }

    public String toString() {
        return m_expression.toString();
    }
}
