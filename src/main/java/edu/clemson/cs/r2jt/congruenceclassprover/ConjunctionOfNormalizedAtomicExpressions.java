/**
 * ConjunctionOfNormalizedAtomicExpressions.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.rewriteprover.absyn.*;
import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import sun.plugin.dom.exception.NoModificationAllowedException;

import java.util.*;

/**
 * Created by mike on 4/3/2014.
 */
public class ConjunctionOfNormalizedAtomicExpressions {

    private final Registry m_registry;
    protected final List<NormalizedAtomicExpressionMapImpl> m_exprList;
    protected long m_timeToEnd = -1;
    protected boolean m_evaluates_to_false = false;
    private int f_num = 0;
    private String m_current_justification = "";
    private final Map<Integer, Set<NormalizedAtomicExpressionMapImpl>> m_useMap;

    /**
     * @param registry the Registry symbols contained in the conjunction will
     * reference. This class will add entries to the registry if needed.
     */
    public ConjunctionOfNormalizedAtomicExpressions(Registry registry) {
        m_registry = registry;
        // Array list is much slower than LinkedList for this application
        m_exprList = new LinkedList<NormalizedAtomicExpressionMapImpl>();
        m_useMap = new HashMap<Integer, Set<NormalizedAtomicExpressionMapImpl>>();
    }

    protected int size() {
        return m_exprList.size();
    }

    protected void clear() {
        m_exprList.clear();
    }

    protected NormalizedAtomicExpressionMapImpl getExprAtPosition(int position) {
        return m_exprList.get(position);
    }

    protected String addExpression(PExp expression, long timeToEnd) {
        m_timeToEnd = timeToEnd;
        return addExpression(expression);
    }

    protected String addExpressionAndTrackChanges(PExp expression,
            long timeToEnd, String justification) {
        m_timeToEnd = timeToEnd;
        m_timeToEnd = Long.MAX_VALUE;
        m_current_justification = justification;
        String rString = addExpression(expression);
        m_current_justification = "";
        updateUseMap();
        return rString;
    }

    // Top level
    protected String addExpression(PExp expression) {
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return "";
        }
        String name = expression.getTopLevelOperation();

        if (expression.isEquality()) {
            int lhs = addFormula(expression.getSubExpressions().get(0));
            int rhs = addFormula(expression.getSubExpressions().get(1));
            return mergeOperators(lhs, rhs);
        }
        else if (name.equals("and")) {
            addExpression(expression.getSubExpressions().get(0));
            addExpression(expression.getSubExpressions().get(1));
        }
        else {
            MTType type = expression.getType();
            PSymbol asPsymbol = (PSymbol) expression;
            int intRepOfOp = addPsymbol(asPsymbol);
            int root = addFormula(expression);
            if (type.isBoolean()) {
                return mergeOperators(m_registry.getIndexForSymbol("true"),
                        root);
            }
        }
        return "";
    }

    // adds a particular symbol to the registry
    protected int addPsymbol(PSymbol ps) {
        String name = ps.getTopLevelOperation();
        MTType type = ps.getType();
        Registry.Usage usage = Registry.Usage.SINGULAR_VARIABLE;
        if (ps.isLiteral()) {
            usage = Registry.Usage.LITERAL;
        }

        else if (ps.isFunction()
                || ps.getType().getClass().getSimpleName().equals("MTFunction")) {
            if (ps.quantification.equals(PSymbol.Quantification.FOR_ALL)) {
                usage = Registry.Usage.HASARGS_FORALL;
            }
            else {
                usage = Registry.Usage.HASARGS_SINGULAR;
            }

        }
        else if (ps.quantification.equals(PSymbol.Quantification.FOR_ALL)) {
            usage = Registry.Usage.FORALL;
        }
        // The type stored with expressions is actually the range type
        // Ex: (S = T):B.
        // However, I need to store types for functions/relations.
        // Building these here.
        // It would be far better to handle this upstream.
        // Currently PExps from theorems have correct type set already
        if (ps.getSubExpressions().size() > 0
                && !type.getClass().getSimpleName().equals("MTFunction")) {
            List<MTType> paramList = new ArrayList<MTType>();
            for (PExp pParam : ps.getSubExpressions()) {
                paramList.add(pParam.getType());
            }
            type = new MTFunction(m_registry.m_typeGraph, type, paramList);
        }
        return m_registry.addSymbol(name, type, usage);
    }

    /* experimentally handling =
     i.e.: (|?S| = 0) = (?S = Empty_String))
     is broken down by addExpression so (|?S| = 0) is an argument
     should return int for true if known to be equal, otherwise return root representative. 
     */
    protected int addFormula(PExp formula) {
        if (formula.isEquality()) {
            int lhs = addFormula(formula.getSubExpressions().get(0));
            PExp r = formula.getSubExpressions().get(1);
            int rhs = addFormula(r);
            lhs = m_registry.findAndCompress(lhs);
            rhs = m_registry.findAndCompress(rhs);
            // This prevents matching of (i=i)=true, which is not built in
            /*if (lhs == rhs) {
                return m_registry.getIndexForSymbol("true");
            }
            else {*/
            // insert =(lhs,rhs) = someNewRoot
            int questEq = m_registry.getIndexForSymbol("=");
            NormalizedAtomicExpressionMapImpl pred =
                    new NormalizedAtomicExpressionMapImpl();
            pred.writeOnto(questEq, 0);
            pred.writeOnto(lhs, 1);
            pred.writeOnto(rhs, 2);
            return addAtomicFormula(pred);
            // }
        }
        PSymbol asPsymbol;
        if (!(formula instanceof PSymbol)) {
            System.err.println("unhandled PExp: " + formula.toString());
            throw new RuntimeException();

        }
        else
            asPsymbol = (PSymbol) formula;
        int intRepOfOp = addPsymbol(asPsymbol);
        // base case
        if (formula.isVariable()) {
            return intRepOfOp;
        }

        NormalizedAtomicExpressionMapImpl newExpr =
                new NormalizedAtomicExpressionMapImpl();
        newExpr.writeOnto(intRepOfOp, 0);
        int pos = 0;
        PExpSubexpressionIterator it = formula.getSubExpressionIterator();
        while (it.hasNext()) {
            PExp p = it.next();
            pos++;
            int root = addFormula(p);
            assert newExpr != null;
            newExpr.writeOnto(root, pos);
        }
        return addAtomicFormula(newExpr);
    }

    /**
     * @param atomicFormula one sided expression. (= new root) is appended and
     * expression is inserted if no match of the side is found. Otherwise
     * current root is returned.
     * @return current integer value of root symbol that represents the input.
     */
    private int addAtomicFormula(NormalizedAtomicExpressionMapImpl atomicFormula) {
        int posIfFound = Collections.binarySearch(m_exprList, atomicFormula);
        if (posIfFound >= 0) {
            return m_exprList.get(posIfFound).readRoot();
        }
        // no such formula exists
        int indexToInsert = -(posIfFound + 1);
        MTType typeOfFormula =
                m_registry.getTypeByIndex(atomicFormula.readPosition(0));
        // this is the full type and is necessarily a function type

        MTType rangeType = ((MTFunction) typeOfFormula).getRange();
        String symName =
                m_registry.getSymbolForIndex(atomicFormula.readPosition(0));
        assert rangeType != null : symName + " has null type";
        // if any of the symbols in atomicFormula are variables (FORALL) make created symbol a variable
        boolean isVar = false;
        for (Integer is : atomicFormula.getKeys()) {
            String s = m_registry.getSymbolForIndex(is);
            if (s.startsWith("¢v")) {
                isVar = true;
                break;
            }
            Registry.Usage us = m_registry.getUsage(s);
            if (us == Registry.Usage.FORALL
                    || us == Registry.Usage.HASARGS_FORALL) {
                isVar = true;
                break;
            }
        }
        int rhs = m_registry.makeSymbol(rangeType, isVar);
        atomicFormula.writeToRoot(rhs);
        m_exprList.add(indexToInsert, atomicFormula);
        return rhs;
    }

    protected String mergeOperators(int a, int b) {
        String rString = "";
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return rString;
        }
        a = m_registry.findAndCompress(a);
        b = m_registry.findAndCompress(b);
        if (a == b)
            return "";
        Stack<Integer> holdingTank = new Stack<Integer>();
        holdingTank.push(a);
        holdingTank.push(b);

        while (holdingTank != null && !holdingTank.empty()) {
            if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
                return rString;
            }
            int opA = m_registry.findAndCompress(holdingTank.pop());
            int opB = m_registry.findAndCompress(holdingTank.pop());
            // Rules that determine which symbol becomes root go here
            if (opA == opB)
                continue;
            String aString = m_registry.getSymbolForIndex(opA);
            String bString = m_registry.getSymbolForIndex(opB);

            if (aString.compareTo(bString) > 0) {
                int temp = opA;
                opA = opB;
                opB = temp;
            }

            // Favor retaining literals
            // THIS IS CURRENTLY ABSOLUTELY NECESSARY.  Finding could be redone to avoid this.
            // Otherwise, proofs can fail to prove if literals are redefined.
            aString = m_registry.getSymbolForIndex(opA);
            bString = m_registry.getSymbolForIndex(opB);
            Registry.Usage uB = m_registry.getUsage(bString);
            Registry.Usage uA = m_registry.getUsage((aString));
            // puts created in b
            if (uA.equals(Registry.Usage.CREATED)
                    && !uB.equals(Registry.Usage.CREATED)) {
                int temp = opA;
                opA = opB;
                opB = temp;
            }
            // Can't rely on literal property being set.  false is not set to be a literal.
            /*else if (uA.equals(Registry.Usage.LITERAL)
                    && uB.equals(Registry.Usage.LITERAL)) {
                System.err.println("Literal redefinition: " + aString + "."
                        + opA + " -> " + bString + "." + opB);
                //System.err.println(m_registry.m_symbolToIndex);
                //System.err.println(m_registry.m_indexToSymbol);
            }*/
            else if (uB.equals(Registry.Usage.LITERAL)) {
                int temp = opA;
                opA = opB;
                opB = temp;
            }
            aString = m_registry.getSymbolForIndex(opA);
            bString = m_registry.getSymbolForIndex(opB);
            uA = m_registry.getUsage((aString));
            uB = m_registry.getUsage(bString);
            // prevent constant replacement by variable
            if (uA != uB) {
                boolean aIsVar =
                        (uA == Registry.Usage.FORALL
                                || uA == Registry.Usage.HASARGS_FORALL || aString
                                .startsWith("¢v"));
                boolean bIsVar =
                        (uB == Registry.Usage.FORALL
                                || uB == Registry.Usage.HASARGS_FORALL || bString
                                .startsWith("¢v"));
                if ((aIsVar || bIsVar) && !(aIsVar == bIsVar) && (aIsVar)) {
                    int temp = opA;
                    opA = opB;
                    opB = temp;
                }
            }
            aString = m_registry.getSymbolForIndex(opA);
            bString = m_registry.getSymbolForIndex(opB);
            // prefer true or false
            if (bString.equals("true") || bString.equals("false")) {
                int temp = opA;
                opA = opB;
                opB = temp;
            }

            if (aString.equals("false") && bString.equals("true")
                    || (aString.equals("true") && bString.equals("false"))) {
                m_evaluates_to_false = true;
            }

            rString +=
                    m_registry.getSymbolForIndex(opA) + "/"
                            + m_registry.getSymbolForIndex(opB) + ",";
            Stack<Integer> mResult = mergeOnlyArgumentOperators(opA, opB);

            if (mResult != null)
                holdingTank.addAll(mResult);

        }
        return rString;
    }

    // This has been replaced by a theorem in my Boolean_Theory - mike
    // look for =(x,y)=true in list.  If found call merge(x,y).
    //  = will always be at top of list.
    // These expression will not be removed by this function,
    // but can be removed by merge().
    protected void mergeArgsOfEqualityPredicateIfRootIsTrue() {
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return;
        }
        // loop until end, function op is not =, or =(x,y)=true
        // when found do merge, start again.
        int eqQ = m_registry.getIndexForSymbol("=");
        for (int i = 0; i < m_exprList.size(); ++i) {
            NormalizedAtomicExpressionMapImpl cur = m_exprList.get(i);
            int f = cur.readPosition(0);
            if (f != eqQ) {
                return;
            }
            int t = m_registry.getIndexForSymbol("true");
            int root = cur.readRoot();
            int op1 = cur.readPosition(1);
            int op2 = cur.readPosition(2);
            if (root == t && op1 != op2) {
                mergeOperators(cur.readPosition(1), cur.readPosition(2));
                // mergeOperators will do any other merges that arise.
                i = 0;
            }
        }
    }

    // Return list of modified predicates by their position. Only these can cause new merges.
    // b is replaced by a
    protected Stack<Integer> mergeOnlyArgumentOperators(int a, int b) {
        if (m_timeToEnd > 0 && System.currentTimeMillis() > m_timeToEnd) {
            return null;
        }

        Iterator<NormalizedAtomicExpressionMapImpl> it = m_exprList.iterator();
        Stack<NormalizedAtomicExpressionMapImpl> modifiedEntries =
                new Stack<NormalizedAtomicExpressionMapImpl>();
        Stack<Integer> coincidentalMergeHoldingTank = new Stack<Integer>();
        while (it.hasNext()) {
            NormalizedAtomicExpressionMapImpl curr = it.next();
            if (curr.replaceOperator(b, a)) {
                modifiedEntries.push(curr);
                it.remove();
            }
        }
        while (!modifiedEntries.empty()) {
            int indexToInsert =
                    Collections
                            .binarySearch(m_exprList, modifiedEntries.peek());
            // If the modified one is already there, don't put it back
            if (indexToInsert < 0) {
                indexToInsert = -(indexToInsert + 1);
                // root of modified expression depends on the changed arg
                //String fordebug = modifiedEntries.peek().toHumanReadableString(m_registry);
                int rootOfChangedExpression = modifiedEntries.peek().readRoot();
                m_registry.addDependency(rootOfChangedExpression,
                        m_current_justification, false);
                m_exprList.add(indexToInsert, modifiedEntries.pop());
            }
            else {
                // the expr is in the list, but are the roots different?
                int rootA = modifiedEntries.pop().readRoot();
                int rootB = m_exprList.get(indexToInsert).readRoot();
                if (rootA != rootB) {
                    coincidentalMergeHoldingTank.push(rootA);
                    coincidentalMergeHoldingTank.push(rootB);
                }
            }
        }
        //System.err.println(m_registry.getSymbolForIndex(a) + "/" + m_registry.getSymbolForIndex(b));
        m_registry.addDependency(a, m_current_justification, true);
        m_registry.substitute(a, b);
        return coincidentalMergeHoldingTank;
    }

    protected void updateUseMap(){
        m_useMap.clear();
        assert m_useMap.size()==0;
        for(NormalizedAtomicExpressionMapImpl e : m_exprList){
            for(Integer k: e.getKeys()){
                assert m_registry.findAndCompress(k) == k : "child symbol in conj";
                if(m_useMap.containsKey(k))
                    m_useMap.get(k).add(e);
                else{
                    HashSet<NormalizedAtomicExpressionMapImpl> nm = new HashSet<NormalizedAtomicExpressionMapImpl>();
                    nm.add(e);
                    m_useMap.put(k,nm);
                }
            }
        }
        // for debug
        /*String s = "";
        for(Integer k : m_useMap.keySet()){
            s += m_registry.getSymbolForIndex(k) + "\n";
            for(NormalizedAtomicExpressionMapImpl nm : m_useMap.get(k)){
                s += nm.toHumanReadableString(m_registry) + "\n";
            }
            s += "\n";
        }
        System.err.println(s);*/
        for(Integer k : m_useMap.keySet()){
            assert !m_useMap.get(k).isEmpty();
            assert m_useMap.get(k).size()!=0;
        }
    }

    protected Set<java.util.Map<String,String>> getMatchesForOverideSet(NormalizedAtomicExpressionMapImpl expr,
                                                           Registry exprReg, Set<Map<String,String>> foreignSymbolOverideSet){
        Set<java.util.Map<String,String>> rSet = new HashSet<Map<String, String>>();
        for(Map<String,String> fs_m: foreignSymbolOverideSet){
            Set<java.util.Map<String,String>> results= getMatches(expr, exprReg, fs_m);
            if(results!=null && results.size()!=0)
                rSet.addAll(results);
        }
        return rSet;
    }
    // return map is expr Symbol -> this Symbol
    // returns null if no match found;
    // foreignSymbolOveride is expr Symbol -> this Symbol
    protected Set<java.util.Map<String,String>> getMatches(NormalizedAtomicExpressionMapImpl expr,
                                            Registry exprReg, Map<String,String> foreignSymbolOveride){
        Set<NormalizedAtomicExpressionMapImpl> candidates = new HashSet<NormalizedAtomicExpressionMapImpl>();
        boolean firstKey = true;
        for(Integer k: expr.getKeys()){
            String eSymb = exprReg.getSymbolForIndex(k);
            // String equals does not work for ""
            boolean isWild = foreignSymbolOveride.containsKey(eSymb) && foreignSymbolOveride.get(eSymb).length()==0;
            if(isWild) continue; // if it is wild, no point in looking for it here, go to next key
            if(foreignSymbolOveride.containsKey(eSymb))
                eSymb = foreignSymbolOveride.get(eSymb);
            // Early return for case where no possible match can occur
            if(!m_registry.m_symbolToIndex.containsKey(eSymb)) {
               return null;
            }

            // Either the symbol is a previously matched wildcard or it is a literal
            int symbolInConj = m_registry.getIndexForSymbol(eSymb);
            // Literal is in the registry, but it may not be in an expression. Some symbols added by default.
            if(!m_useMap.containsKey(symbolInConj)){
                return null;
            }
            // ALIAS alert!!!!!
            Set<NormalizedAtomicExpressionMapImpl> results = new HashSet<NormalizedAtomicExpressionMapImpl>(m_useMap.get(symbolInConj));
            // early return for no matches.
            if(results == null || results.isEmpty())
                return null;

            Set<NormalizedAtomicExpressionMapImpl> removalSet = new HashSet<NormalizedAtomicExpressionMapImpl>();
            // remove equations with non matching length
            // remove equations from the result set if they do not have the literal we just searched for where they occur
            // in the search expression.  They may occur elsewhere, in which case they will be dealt with at another
            // loop iteration (is another literal of the search expr in the same pos?) or in the binding phase.
            // 0101 expPos
            // 1111 r_n     Is OK
            // 1011 r_n     Is NOT ok
            int exprPositions = expr.readOperator(k); // this is the bit code, 1 if used, 0 if not
            for(NormalizedAtomicExpressionMapImpl r_n : results){
                int conjPos = r_n.readOperator(symbolInConj);
                if((r_n.numOperators()!= expr.numOperators()) ||
                        (conjPos & exprPositions)!= exprPositions){
                    removalSet.add(r_n);
                }
            }
            results.removeAll(removalSet);
            if(firstKey)
                candidates = results;
            else{
                // candidates = candidates intersect results
                candidates.retainAll(results);
                if(candidates.isEmpty())
                    return null;
            }
            firstKey = false;
        }
        // early return for case where no result due to non matching literal positions
        if(candidates.isEmpty()) return null;

        // At this point candidates is a set of all expressions that syntactically match,
        // also considering the wildcards already defined

        // Create collection of bindings to return
        Set<Map<String,String>> rSet = new HashSet<Map<String, String>>();
        for(NormalizedAtomicExpressionMapImpl c_n : candidates){
            // If a symbol in c_n is an undefined key in the overide map, define that key
            HashMap<String,String> binding = new HashMap<String,String>(foreignSymbolOveride);
            boolean validBinding = true;
            // hopefully I don't need to deep copy Strings
            for(String exprKey : binding.keySet()){
                if(binding.get(exprKey).length()==0){
                    // We have found a wildcard
                    int posInSearchExpr = expr.readOperator(exprReg.getIndexForSymbol(exprKey));
                    if(posInSearchExpr==0) continue; // meaning the wildcard is not a part of the equation
                    int localSymbolIndex = c_n.readPositionBitcode(posInSearchExpr);
                    if(localSymbolIndex == -1){
                        // Occurs when wildcard used in multiple positions, but different symbols in matched expr.
                        // Throw it out.
                        validBinding = false;
                        break;

                    }
                    String localSymbol = m_registry.getSymbolForIndex(localSymbolIndex);
                    // Type check here.  Incompatible types should invalidate the whole binding
                    MTType theoremSymbolType = exprReg.getTypeByIndex(exprReg.getIndexForSymbol(exprKey));
                    MTType localSymbolType = m_registry.getTypeByIndex(localSymbolIndex);
                    if(!localSymbolType.isSubtypeOf(theoremSymbolType)) {
                        validBinding = false;
                        break;
                    }
                    binding.put(exprKey,localSymbol);
                }
            }
            if(validBinding) {
                // At this point we have bound all the wildcards for a particular candidate

                rSet.add(binding);
            }

        }
        return rSet;
    }

    protected Map<String, Integer> getSymbolProximity(Set<String> symbols) {
        boolean done = false;
        Map<Integer, Integer> relatedKeys = new HashMap<Integer, Integer>();
        for (String s : symbols) {
            relatedKeys.put(m_registry.getIndexForSymbol(s), 0);
        }
        int closeness = 0;
        Set<Integer> relatedSet = new HashSet<Integer>();

        while (!done) {
            closeness++;
            int startSize = relatedKeys.size();
            HashMap<Integer, Integer> relatedKeys2 =
                    new HashMap<Integer, Integer>();
            for (int i = 0; i < m_exprList.size(); ++i) {
                if (relatedSet.contains(i))
                    continue;
                Set<Integer> intersection =
                        new HashSet<Integer>(m_exprList.get(i).getKeys());
                intersection.retainAll(relatedKeys.keySet());
                if (!intersection.isEmpty()) {
                    relatedSet.add(i);
                    for (Integer k : m_exprList.get(i).getKeys()) {
                        if (!relatedKeys.containsKey(k)) {
                            relatedKeys2.put(k, closeness);
                        }
                    }
                }
            }
            relatedKeys.putAll(relatedKeys2);
            if (startSize == relatedKeys.size()) {
                done = true;
            }

        }

        Map<String, Integer> rMap =
                new HashMap<String, Integer>(relatedKeys.size());
        for (Integer i : relatedKeys.keySet()) {
            rMap.put(m_registry.getSymbolForIndex(i), relatedKeys.get(i));
        }

        return rMap;
    }

    @Override
    public String toString() {
        String r = "";
        if (m_evaluates_to_false)
            r += "Conjunction evaluates to false" + "\n";
        for (MTType key : m_registry.m_typeToSetOfOperators.keySet()) {
            r += key.toString() + ":\n";
            r += m_registry.m_typeToSetOfOperators.get(key) + "\n\n";
        }
        for (NormalizedAtomicExpressionMapImpl cur : m_exprList) {
            r += cur.toHumanReadableString(m_registry) + "\n";
        }
        return r;
    }

}
