/**
 * Theorem.java --------------------------------- Copyright (c) 2014 RESOLVE
 * Software Research Group School of Computing Clemson University All rights
 * reserved. --------------------------------- This file is subject to the terms
 * and conditions defined in file 'LICENSE.txt', which is part of this source
 * code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * Created by mike on 4/3/2014.
 */
public class TheoremCongruenceClosureImpl {

    private final boolean isEquality;
    private  Registry m_theoremRegistry;
    private ConjunctionOfNormalizedAtomicExpressions m_matchConj;
    public final String m_theoremString;
    private final PExp m_insertExpr;
    private final PExp m_theorem;

    public TheoremCongruenceClosureImpl(TypeGraph g, PExp p) {
        m_theorem = p;
        m_theoremString = p.toString();
        isEquality = p.getTopLevelOperation().equals("=");
        m_theoremRegistry = new Registry(g);
        m_matchConj = new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry);
        
        if (isEquality) {
            m_matchConj.addExpression(p.getSubExpressions().get(0));
            m_insertExpr = p;
        } else if (p.getTopLevelOperation().equals("implies")) { // add consequent of implication
            m_matchConj.addExpression(p.getSubExpressions().get(0));
            m_insertExpr = p.getSubExpressions().get(1);
        } else {
            m_matchConj.addExpression(p);
            m_insertExpr = p;
        }
    }
    
    public TheoremCongruenceClosureImpl(TypeGraph g, PExp toMatchAndBind, PExp toInsert){
        m_theorem = toInsert;
        m_theoremString = toInsert.toString();
        isEquality = true;
        m_theoremRegistry = new Registry(g);
        m_matchConj = new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry);
        

            m_matchConj.addExpression(toMatchAndBind);
            m_insertExpr = toInsert; 
    }
    
    // need a stack of currentBindings so you can roll back
    // need to set the current searchbox current bindings to this
    // keep bindings update only if undefined.
    public String applyTo(VerificationConditionCongruenceClosureImpl vc) {
        // Get stack of bindings
        // find match(s) in vc

        // convert string map to PExp map
        // get new Pexp(s) through substitution function
        // add those to vc
        if (m_insertExpr.getQuantifiedVariables().size() == 0) {
            String r = "inserting: " + m_insertExpr;
            vc.getConjunct().addExpression(m_insertExpr);
            return r;
        }
        String r = ""; 
        
        Stack<HashMap<String, String>> allValidBindings = findValidBindings(vc);

        if (allValidBindings == null || allValidBindings.size() == 0) {
            return "";
        }
        
        HashMap<PExp, PExp> quantToLit = new HashMap<PExp, PExp>();
        while (!allValidBindings.empty()) {
            HashMap<String, String> curBinding = allValidBindings.pop();
            if (!isEverythingBound(curBinding, vc)) {
               // continue; // can put this upstream
            }
            for (String thKey : curBinding.keySet()) {
                // todo: add types and stuff
                MTType quanType = m_theoremRegistry.getTypeByIndex(m_theoremRegistry.getIndexForSymbol(thKey));
                quantToLit.put(new PSymbol(quanType, quanType, thKey),
                        new PSymbol(quanType, quanType, curBinding.get(thKey)));
            }
            PExp modifiedInsert = m_insertExpr.substitute(quantToLit);
            assert modifiedInsert!= m_insertExpr;
            quantToLit.clear();
            //if(!allVarsInPExpDefinedInVC(modifiedInsert, vc)) continue;
            r += ("inserting: " + modifiedInsert);
            vc.getConjunct().addExpression(modifiedInsert);

        }
        return r;
    }

    boolean isEverythingBound(HashMap<String, String> binding, VerificationConditionCongruenceClosureImpl vc) {
        for (String k : binding.keySet()) {
            String v = binding.get(k);
            if (v.equals("") && m_theoremRegistry.getUsage(k) == Registry.Usage.FORALL) {
                // we only care about unbound guys if they are in the insert exp.
                return false;
            }
            if (!vc.getRegistry().isSymbolInTable(v)) {
                return false;
            }
        }
        // so the map checks outtrf43r5
        return true;
    }

    boolean allVarsInPExpDefinedInVC(PExp p, VerificationConditionCongruenceClosureImpl vc) {
        for (String s : p.getSymbolNames()) {
            if (s.equals("and") || s.equals("=")) {
                continue;
            }
            if (!vc.getRegistry().isSymbolInTable(s)) {
                if (m_theoremRegistry.getUsage(s) != Registry.Usage.LITERAL) {
                    return false;
                }
            }

        }
        return true;
    }

    private HashMap<String, String> getInitBindings() {
        HashMap<String, String> initBindings = new HashMap<String, String>();
        for (int i = 0; i < m_theoremRegistry.m_indexToSymbol.size(); ++i) {
            String curSym = m_theoremRegistry.m_indexToSymbol.get(i);
            Registry.Usage us = m_theoremRegistry.getUsage(curSym);
            if (us == Registry.Usage.CREATED || us == Registry.Usage.FORALL) {
                // if a merge happens, there can be unused created guys
                int idx = m_theoremRegistry.getIndexForSymbol(curSym);
               // if (us==Registry.Usage.FORALL && curSym.equals(m_theoremRegistry.getSymbolForIndex(i))) 
                    initBindings.put(curSym, "");
                

            }
        }
        return initBindings;
    }

   

    // for those PExp that did not tranlate into a Conj list( their information is only in the registry)
    private Stack<HashMap<String, String>> findPExp(PExp p, VerificationConditionCongruenceClosureImpl vc) {
        if (p.getTopLevelOperation().equals("implies")) {
            return findPExp(p.getSubExpressions().get(0), vc);
        }
        Stack<HashMap<String, String>> allValidBindings = new Stack<HashMap<String, String>>();
        HashMap<String, String> curBindings = getInitBindings();
        MTType t;
        if (p.isEquality()) {
            PExp lhs = p.getSubExpressions().get(0);
            PExp rhs = p.getSubExpressions().get(1);
            assert lhs.isVariable() || rhs.isVariable();
            // if s = t, then you only need to find one or the other (one actual symbol for each known equality class)
            // and it wont matter which you use, since they will have the same type.
            t = rhs.getType();

        } else {
            //assert p.isVariable() || p.isLiteral();
            t = p.getType();
        }
        Set<String> actuals = vc.getRegistry().getSetMatchingType(t);
        ArrayList<String> toRemove = new ArrayList<String>();
        for (String a : actuals) {
            if (vc.getRegistry().getUsage(a).equals(Registry.Usage.HASARGS)) {
                toRemove.add(a);
            }
        }
        for (String tor : toRemove) {
            actuals.remove(tor);
        }

        String[] virtualsArr = curBindings.keySet().toArray(new String[curBindings.keySet().size()]);
        String[] actualsArr = actuals.toArray(new String[actuals.size()]);
        /*for(int i = 0; i < actualsArr.length; ++i){
         for(int j = 0; j < actualsArr.length; ++j){
         curBindings.put(virtualsArr[0], actualsArr[i]);
         curBindings.put(virtualsArr[1], actualsArr[j]);
         allValidBindings.push(curBindings);
         curBindings = getInitBindings();
         }
         }
         */
        for (int i = 0; i < actualsArr.length; ++i) {
            for (String k : curBindings.keySet()) {
                curBindings.put(k, actualsArr[i]);
            }
            allValidBindings.push(curBindings);
            curBindings = getInitBindings();
        }
        // inequalities are functions in this system and would have produced a conj list
        return allValidBindings;
    }


    private boolean pushNewSearchBox(Stack<SearchBox> boxStack){
        SearchBox top = boxStack.peek();
        int index = top.m_indexInList + 1;
        // if top of stack contains last expression return false, cant push another
        if(index == m_matchConj.size()) return false;
        /*
        SearchBox(NormalizedAtomicExpressionMapImpl query, Registry queryReg,
            ConjunctionOfNormalizedAtomicExpressions dataSet, Registry dataReg,
            HashMap<String,String> bindings, int indexInList)
        */
        
        boxStack.push(new SearchBox(m_matchConj.getExprAtPosition(index),
        m_theoremRegistry, top.m_dataSet, top.m_destRegistry, 
                new HashMap<String,String>(top.m_bindings), index));
        return true;   
    }
    private Stack<HashMap<String, String>> findValidBindings(VerificationConditionCongruenceClosureImpl vc) {

        if (m_matchConj.size() == 0) {

            return findPExp(m_theorem, vc);
        }
        Stack<HashMap<String, String>> allValidBindings = new Stack<HashMap<String, String>>();
        Stack<SearchBox> boxStack = new Stack<SearchBox>();
        boxStack.push(new SearchBox(m_matchConj.getExprAtPosition(0),
        m_theoremRegistry, vc.getConjunct(), vc.getRegistry(), getInitBindings(), 0));
        
        while(!boxStack.isEmpty()){
            SearchBox curBox = boxStack.peek();
            // rollBack
            curBox.m_bindings = new HashMap<String,String>(curBox.m_bindingsInitial); // setup constructor so this works the same in both cases (new obj and old)
            curBox.getNextMatch(); // if new object this should be first match
            if(curBox.impossibleToMatch){
                boxStack.pop();
                if(!boxStack.isEmpty()){
                    boxStack.peek().currentIndex = boxStack.peek().m_lastGoodMatchIndex;
                    boxStack.peek().currentIndex++;
                }
                // this will get the next possible binding for the previous expression
            }
            else{
                // save bindings if for last index, then pop
                if(curBox.m_indexInList + 1 == m_matchConj.size()){
                    if(allValidBindings.isEmpty()){
                        allValidBindings.push(curBox.m_bindings);
                    }
                    else if(!curBox.m_bindings.equals(allValidBindings.peek()))
                        allValidBindings.push(curBox.m_bindings);
                    boxStack.pop();
                    if(!boxStack.isEmpty()){
                        boxStack.peek().currentIndex = boxStack.peek().m_lastGoodMatchIndex;
                        boxStack.peek().currentIndex++;
                    }
                }
                else{
                    curBox.m_lastGoodMatchIndex = curBox.currentIndex;
                    pushNewSearchBox(boxStack);
                }
            }
        }
        return allValidBindings;
    }

    // this is mostly temporary
    public static boolean canProcess(PExp p) {
        if (p.getTopLevelOperation().equals("=")) {
            return true;
        }
        if (p.getTopLevelOperation().equals("implies")) {
            return true;
        }
        if (p.getQuantifiedVariables().size() == 0) {
            return true;
        }
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

    public String toString() {
        String r = "\n--------------------------------------\n";
        r += m_theoremString;
        r += "\nif found\n" + m_matchConj + "\ninsert\n" + m_insertExpr;
        r += "\n--------------------------------------\n";
        return r;
    }
}
