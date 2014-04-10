/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.clemson.cs.r2jt.congruenceclassprover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
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
    protected HashMap<String,String> m_bindings; // Wildcard to actual. meant to be only for foralls
    private final ConjunctionOfNormalizedAtomicExpressions m_dataSet;
    public boolean directMatch = false;
    public boolean impossibleToMatch = false; //
    protected final List<String> m_origAsStrArray;
    
    public SearchBox(NormalizedAtomicExpressionMapImpl query, Registry queryReg,
            ConjunctionOfNormalizedAtomicExpressions dataSet, Registry dataReg,
            HashMap<String,String> bindings){
        m_original = query;
        m_origRegistry = queryReg;
        m_dataSet = dataSet;
        m_destRegistry = dataReg;
        m_origAsStrArray = NAEtoList(query, queryReg);
        m_bindings =bindings;
        m_translated = m_original.translateFromRegParam1ToRegParam2(m_origRegistry, m_destRegistry, m_bindings);
        // do search with empty bindings; literals (func names, 0,1 etc. will be in query)
        dataSet.findNAE(this); // lb = currindex. does find on translated, sets bounds.
        // can make this function local
        // set up bindings
        if(inBounds()){
            // if in bounds, there must be a binding
            compareAndBind();
        }
        else{
            impossibleToMatch = true;
        }
        
    }
 
    
    public static List<String> NAEtoList(NormalizedAtomicExpressionMapImpl atom, Registry atomReg){
        ArrayList<String> atomAsStrArray = new ArrayList<String>();
        int op = atom.readPosition(0);
        int i = 1;
        while(op >= 0){
          atomAsStrArray.add(atomReg.getSymbolForIndex(op));
          op = atom.readPosition(i++);
        }
        int root = atom.readRoot();
        if(root >=0){
            atomAsStrArray.add(atomReg.getSymbolForIndex(root));
        }
        return atomAsStrArray;
    }

    public boolean getNextMatch(){
        currentIndex++;
        while(inBounds()){
            if(compareAndBind()){
                return true;
            }
            currentIndex++;
        }
        impossibleToMatch = true;
        return false;
    }
    
    // pre: bounds are set. index is in bounds.
    // post returns false or sets bindings 
    public boolean compareAndBind(){
        NormalizedAtomicExpressionMapImpl candidate = m_dataSet.getExprAtPosition(currentIndex);
        if(m_translated.noConflicts(candidate)){
            // 2 expressions, using same registry (one is translated)
            // there are no conflicts.
            // where there is an unfilled position
            List<String> candStrArray = NAEtoList(candidate, m_destRegistry);
            if(candStrArray.size() != m_origAsStrArray.size()) return false;
            m_bindings.clear();
            for(int i = 0; i < m_origAsStrArray.size(); ++i){
                String origOp = m_origAsStrArray.get(i);
                String boundOp = candStrArray.get(i);
                m_bindings.put(origOp, boundOp);
            }
            return true;
        }
        return false;
    }
    public boolean inBounds(){
        if(currentIndex >= lowerBound &&
                currentIndex <= upperBound)
            return true;
        return false;
        
    }
}
