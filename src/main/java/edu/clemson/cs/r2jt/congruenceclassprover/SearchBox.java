/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.clemson.cs.r2jt.congruenceclassprover;

import java.util.HashMap;

/**
 *
 * @author mike
 */
public class SearchBox {
    protected int lowerBound;
    protected int upperBound;
    protected int currentIndex;
    protected NormalizedAtomicExpressionMapImpl m_expr;
    protected HashMap<String,String> m_bindings;
    public boolean directMatch = false;
    public boolean impossibleToMatch = false; //
    
    public SearchBox(int lower, int upper, NormalizedAtomicExpressionMapImpl expr){
        lowerBound = lower;
        upperBound = upper;
        currentIndex = lower;
        m_expr = expr; // expr in vc at current index, translated using current map
    }
    public SearchBox(int dirMatch, NormalizedAtomicExpressionMapImpl expr){
        directMatch = true;
        lowerBound = upperBound = currentIndex = dirMatch;
    }
    public SearchBox(){
        impossibleToMatch = true;
    }
    public boolean inBounds(){
        if(currentIndex >= lowerBound &&
                currentIndex <= upperBound)
            return true;
        return false;
        
    }
}
