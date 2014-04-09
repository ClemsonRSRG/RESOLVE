/**
 * Theorem.java
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

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by mike on 4/3/2014.
 */
public class TheoremCongruenceClosureImpl {
    private final boolean isEquality;
    private final Registry m_theoremRegistry;
    private final ConjunctionOfNormalizedAtomicExpressions m_matchConj;
    private final String m_theoremString;
    private final PExp m_insertExpr;
      
    public TheoremCongruenceClosureImpl(PExp p){
        m_theoremString = p.toString();
        isEquality = p.getTopLevelOperation().equals("=");
        m_theoremRegistry = new Registry();
        m_matchConj = new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry);
        if(isEquality){
            m_matchConj.addExpression(p.getSubExpressions().get(0));
            m_insertExpr = p;
        }
        else if (p.getTopLevelOperation().equals("implies")){ // add consequent of implication
            m_matchConj.addExpression(p.getSubExpressions().get(0));
            m_insertExpr = p.getSubExpressions().get(1);
        }
        else {
            m_insertExpr = p;
        }
    }

    public boolean applyTo(VerificationConditionCongruenceClosureImpl vc){
        // Get stack of bindings
        // find match(s) in vc
        Stack<HashMap<String, String>> allValidBindings = findValidBindings(vc);
        // convert string map to PExp map
        // get new Pexp(s) through substitution function
        // add those to vc
        if(allValidBindings==null || allValidBindings.size()==0)
            return false;
        
        return true;
    }
    
    // todo: shoot inf loop problem
    private Stack<HashMap<String, String>> findValidBindings(VerificationConditionCongruenceClosureImpl vc){
        if(m_matchConj.size()==0){
            // todo: handle definition bindings
            return null;
        }
        Stack<HashMap<String,String>> allValidBindings = new Stack<HashMap<String, String>>();
        SearchBox[] boxes = new SearchBox[m_matchConj.size()];
        HashMap<String,String> curBindings = new HashMap<String,String>();
        int indexInMatchConj = 0;
        while(indexInMatchConj >= 0){
          // Get next match given current state  
            // create new searchbox for current expression if it hasnt been looked for
            if(boxes[indexInMatchConj]==null){
                boxes[indexInMatchConj] = new SearchBox(m_matchConj.getExprAtPosition(indexInMatchConj),
                m_theoremRegistry,vc.getConjunct(),vc.getRegistry(),curBindings);
                // getNextMatch in constructor.
            }
            else{
                boxes[indexInMatchConj].getNextMatch(); // searches using current bindings, 
                //then overwrites based on result( has the effect of only adding to).
            }
         // if no matches with current bindings,
            if(boxes[indexInMatchConj].impossibleToMatch){
                // go back one, this will reset bindings according to new search at top of loop
                indexInMatchConj--; // eventually all bindings will be impossible, causing the loop to exit.
                // why?
                
                if(indexInMatchConj<0){
                    SearchBox bc = boxes[0];
                    String tr = bc.m_translated.toHumanReadableString(vc.getRegistry());
                    String or = bc.m_original.toHumanReadableString(m_theoremRegistry);
                    System.out.println("Ending search: couldn't find " + or + " as " + tr);
                }
                
            }
            else{
              // these bindings work in this expression, try the in next in list
                indexInMatchConj++;
                if(indexInMatchConj >= m_matchConj.size()){
                    // you have just found bindings that work for all expressions.
                    HashMap<String,String> bCopy = new HashMap<String, String>();
                    bCopy.putAll(curBindings);
                    allValidBindings.push(bCopy);
                    // try this one again, can be many
                    indexInMatchConj--;
                }
            }
            
            
        }
        return allValidBindings;
    }
    
    
    // this is mostly temporary
    public static boolean canProcess(PExp p){
        if(p.getTopLevelOperation().equals("="))
        return true;
        if(p.getTopLevelOperation().equals("implies"))
        return true;
        return false;
    }
    /*
    These are apparently definitions. If blindly added they can greatly increase
    the memory usage of the VC.  If incorporated into the theorem, they will
    not be added to the VC unless a match is made in the larger context.
    Can't process (|(S o <E>)| > 0)
Can't process (|S| < |(<E> o S)|)
Can't process (|S| < |(S o <E>)|)
Can't process Is_Permutation(S, S)
Can't process Is_Permutation((S o T), (T o S))
Can't process Is_Universally_Related(Empty_String, S, f)
Can't process Is_Universally_Related(S, Empty_String, f)
Can't process (0 < 1)
Can't process (1 > 0)
Can't process ((i - 1) < i)
Can't process (i <= i)
    */
    public String toString(){
        String r = "\n--------------------------------------\n";
        r += m_theoremString;
        r += "\nif found\n" + m_matchConj + "\ninsert\n" + m_insertExpr;
        r += "\n--------------------------------------\n";
        return r;
    }
}
