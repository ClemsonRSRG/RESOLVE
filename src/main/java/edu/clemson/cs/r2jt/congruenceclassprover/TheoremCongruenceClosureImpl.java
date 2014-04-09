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

    public VerificationConditionCongruenceClosureImpl applyTo(VerificationConditionCongruenceClosureImpl vc){
        // Get stack of bindings
        // find match(s) in vc
        Stack<HashMap<String, String>> allValidBindings = findValidBindings(vc);
        // convert string map to PExp map
        // get new Pexp(s) through substitution function
        // add those to vc
        return vc;
    }
    
    private Stack<HashMap<String, String>> findValidBindings(VerificationConditionCongruenceClosureImpl vc){
        if(m_matchConj.size()==0){
            // todo: handle definition bindings
            return null;
        }
        Stack<HashMap<String,String>> allValidBindings = new Stack<HashMap<String, String>>();
        Stack<HashMap<String,String>> curBindingStack = new Stack<HashMap<String, String>>();
        SearchBox[] positions = new SearchBox[m_matchConj.size()];
        HashMap<String,String> curBindings = new HashMap<String,String>();
        boolean positiveMatch = false;
        boolean allVariablesBound = false;
        int indexInMatchConj = 0;
        while(indexInMatchConj >= 0){
            
            // Get candidate
            positions[indexInMatchConj] = 
                    vc.findNAE(m_matchConj.getExprAtPosition(indexInMatchConj),
                            m_theoremRegistry, curBindings, positions[indexInMatchConj]);
            int candidate = positions[indexInMatchConj].currentIndex;
            
            // binding works
            if(positiveMatch){
                HashMap<String,String> copyBindings = new HashMap<String, String>();
                copyBindings.putAll(curBindings);
                curBindingStack.push(copyBindings);
                // if binding works for all expressions, save the bindings
                if(indexInMatchConj == m_matchConj.size()-1 && allVariablesBound){
                    allValidBindings.push(curBindingStack.pop());
                    // other bindings may be possible
                    positions[indexInMatchConj].currentIndex++;
                }
                    
                indexInMatchConj++;
                 
            }
            // binding doesn't work
            else{             
                // try binding with next vc expression in range until upper bound reached
                positions[indexInMatchConj].currentIndex++;
                if(!positions[indexInMatchConj].inBounds()){
                    // search exhausted with current bindings
                    indexInMatchConj--;
                    if(!curBindingStack.isEmpty())
                        curBindingStack.pop();
                }
                
            }
            
        }
        return allValidBindings;
    }
    private HashMap<String, String> findMatch(NormalizedAtomicExpressionMapImpl match,
            HashMap<String, String> binding){
        
        
        return binding;
    }
    
    
    // this is mostly temporary
    public static boolean canProcess(PExp p){
        return true;
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
